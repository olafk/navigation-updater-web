package com.liferay.sales.navigation.updater.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Olaf Kock
 */
public class NavigationUpdaterUtil {
	
	public NavigationUpdaterUtil(SiteNavigationMenuItemLocalService snmils, SiteNavigationMenuLocalService snmls,
			VirtualHostLocalService vhls) {
				this.snmils = snmils;
				this.snmls = snmls;
				this.vhls = vhls;
	}

	public String patch(String typeSettings, String host) {
		UnicodeProperties props = new UnicodeProperties(true);
		props.fastLoad(typeSettings);
		String url = props.getProperty("url");
		props.setProperty("url", patchHost(url, host));
		return props.toString();
	}
		
	private String patchHost(String original, String host) {	
		String result = original;
		if(original.startsWith("http://") || original.startsWith("https://")) {
			int startHost = original.indexOf("://") + 3;
			int endHost = original.indexOf("/", startHost+3);
			if(endHost < 0) endHost = original.length();
			result = original.substring(0, startHost) + host + original.substring(endHost);
		}
		System.out.println("Patched " + original + " with " + host + " to " + result);
		return result;
	}

	public String getMainHostName(long companyId) {
		String hostName = "localhost";
		try {
			List<VirtualHost> virtualHosts = vhls.getVirtualHosts(companyId, 0);
			if(virtualHosts.size()==1) {
				hostName = virtualHosts.get(0).getHostname();
			}
		} catch (PortalException e) {
		}
		return hostName;
	}
	
	/**
	 * Retrieve all URL navigation items of the given group that DO NOT link to given host name
	 * @param groupId
	 * @param host
	 * @return
	 */
	public List<SiteNavigationMenuItem> getUrlSiteNavigationItems(long groupId, String host) {
		List<SiteNavigationMenuItem> urlItems = new LinkedList<SiteNavigationMenuItem>();

		List<SiteNavigationMenu> menus = snmls.getSiteNavigationMenus(groupId);
		for (SiteNavigationMenu menu : menus) {
			List<SiteNavigationMenuItem> menuItems = snmils.getSiteNavigationMenuItems(menu.getSiteNavigationMenuId());
			for (SiteNavigationMenuItem item : menuItems) {
				if(item.getType().equals("url")) {
					String url = getUrl(item);
					if(url.indexOf("http://" + host) < 0 &&
					   url.indexOf("https://" + host) < 0) {
						urlItems.add(item);
					}
				}
			}
		}
		return urlItems;
	}

	private String getUrl(SiteNavigationMenuItem item) {
		UnicodeProperties props = new UnicodeProperties(true);
		props.fastLoad(item.getTypeSettings());
		String url = props.getProperty("url");
		return url;
	}
	
	public String urlItemsToUnnumberedListHtml(List<SiteNavigationMenuItem> items) {
		StringBundler urls = new StringBundler("<ul>");
		
		for (SiteNavigationMenuItem item : items) {
			String url = getUrl(item);
			urls.append("<li>");
			urls.append(url);
			urls.append("</li>");
		}
		urls.append("</ul>");
		return urls.toString();
	}
	
	public List<String> siteNavToStringList(List<SiteNavigationMenuItem> items) {
		ArrayList<String> result = new ArrayList<String>(items.size());
		for (SiteNavigationMenuItem item : items) {
			result.add(getUrl(item));
		}
		return result;
	}
	
	public void patchGroupNavigation(long companyId, long groupId, String host) {
		List<SiteNavigationMenuItem> urlItems = this.getUrlSiteNavigationItems(groupId, this.getMainHostName(companyId));
		for (SiteNavigationMenuItem item : urlItems) {
			item.setTypeSettings(this.patch(item.getTypeSettings(), host));
			snmils.updateSiteNavigationMenuItem(item);
		}
	}
	
	SiteNavigationMenuItemLocalService snmils;
	SiteNavigationMenuLocalService snmls;
	VirtualHostLocalService vhls;
}