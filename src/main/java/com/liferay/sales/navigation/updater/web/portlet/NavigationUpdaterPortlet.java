package com.liferay.sales.navigation.updater.web.portlet;

import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sales.navigation.updater.impl.NavigationUpdaterUtil;
import com.liferay.sales.navigation.updater.web.constants.NavigationUpdaterPortletKeys;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionParameters;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Olaf Kock
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-controlpanel",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.display-name=NavigationUpdaterWeb",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + NavigationUpdaterPortletKeys.NAVIGATIONUPDATERWEB,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=administrator",
		"javax.portlet.supports.mime-type=text/html",
		"javax.portlet.version=3.0"
	},
	service = Portlet.class
)
public class NavigationUpdaterPortlet extends MVCPortlet {
	
	private NavigationUpdaterUtil util = null;
	
	private NavigationUpdaterUtil getUtil() {
		if(util == null) {
			util = new NavigationUpdaterUtil(snmils, snmls, vhls);
		}
		return util;
	}
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		NavigationUpdaterUtil util = getUtil();
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();
		PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();
		if(permissionChecker.isCompanyAdmin()) {
			String mainHostName = util.getMainHostName(themeDisplay.getCompanyId());
			
			List<SiteNavigationMenuItem> urlItems = util.getUrlSiteNavigationItems(groupId, mainHostName);
			if(urlItems.size()>0) {
				renderRequest.setAttribute("urls", util.urlItemsToUnnumberedListHtml(urlItems));
				renderRequest.setAttribute("host", mainHostName);
			}
			super.doView(renderRequest, renderResponse);
		}
	}

	public void patchNavigationUrls(ActionRequest actionRequest, ActionResponse actionResponse) {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();
		PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();
		if(permissionChecker.isCompanyAdmin()) {
			ActionParameters actionParameters = actionRequest.getActionParameters();
			String host = actionParameters.getValue("host");

			util.patchGroupNavigation(themeDisplay.getCompanyId(), groupId, host);
		}
	}
	
	@Override
	public void destroy() {
		PortletContext portletContext = getPortletContext();

		ServletContextPool.remove(portletContext.getPortletContextName());

		super.destroy();
	}

	@Override
	public void init(PortletConfig portletConfig) throws PortletException {
		super.init(portletConfig);

		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)portletConfig;

		com.liferay.portal.kernel.model.Portlet portlet =
			liferayPortletConfig.getPortlet();

		PortletApp portletApp = portlet.getPortletApp();

		ServletContextPool.put(
			portletApp.getServletContextName(), portletApp.getServletContext());
	}
		
	@Reference 
	SiteNavigationMenuItemLocalService snmils;
	@Reference
	SiteNavigationMenuLocalService snmls;
	@Reference
	VirtualHostLocalService vhls;
}