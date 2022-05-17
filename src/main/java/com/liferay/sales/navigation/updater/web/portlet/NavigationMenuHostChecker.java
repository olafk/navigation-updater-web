package com.liferay.sales.navigation.updater.web.portlet;

import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.sales.checklist.api.BaseChecklistProviderImpl;
import com.liferay.sales.checklist.api.ChecklistItem;
import com.liferay.sales.checklist.api.ChecklistProvider;
import com.liferay.sales.navigation.updater.impl.NavigationUpdaterUtil;
import com.liferay.sales.navigation.updater.web.constants.NavigationUpdaterPortletKeys;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.util.List;

import org.jsoup.helper.StringUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		service = ChecklistProvider.class
		)
public class NavigationMenuHostChecker extends BaseChecklistProviderImpl {

	@Override
	public ChecklistItem check(ThemeDisplay themeDisplay) {
		NavigationUpdaterUtil util = getUtil();
		long groupId = themeDisplay.getScopeGroupId();
//		PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();
//		if(permissionChecker.isCompanyAdmin()) {
			String mainHostName = util.getMainHostName(themeDisplay.getCompanyId());
			
			List<SiteNavigationMenuItem> urlItems = util.getUrlSiteNavigationItems(groupId, mainHostName);
			if(urlItems.size()>0) {
				return create(false, themeDisplay.getLocale(), LINK_BASE+groupId, MSG, urlItems.size(),
						StringUtil.join(util.siteNavToStringList(urlItems), ",")); 
			}
//		}
		return create(true, themeDisplay.getLocale(), LINK_BASE+groupId, MSG); 
	}
	
	private NavigationUpdaterUtil getUtil() {
		if(util == null) {
			util = new NavigationUpdaterUtil(snmils, snmls, vhls);
		}
		return util;
	}
	private NavigationUpdaterUtil util = null;

	@Reference 
	SiteNavigationMenuItemLocalService snmils;
	@Reference
	SiteNavigationMenuLocalService snmls;
	@Reference
	VirtualHostLocalService vhls;

	private static final String MSG = "navigationupdater-configcheck";
	private static String LINK_BASE = "/group/control_panel/manage?p_p_id=" + NavigationUpdaterPortletKeys.NAVIGATIONUPDATERWEB + "&p_v_l_s_g_id=";
	
}
