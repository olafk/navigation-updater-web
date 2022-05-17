package com.liferay.sales.navigation.updater.web.portlet;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.sales.navigation.updater.web.constants.NavigationUpdaterPortletKeys;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		property = {
				"panel.category.key=" + PanelCategoryKeys.SITE_ADMINISTRATION_BUILD,
				"javax.portlet.resource-bundle=content.Language",
				"panel.app.order:Integer=100",
		},
		service=PanelApp.class
		)
public class NavigationUpdaterPanelApp extends BasePanelApp {

	public NavigationUpdaterPanelApp() {
	}
	
	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group) throws PortalException {
		return true;
	}
	
	@Override
	public String getPortletId() {
		return NavigationUpdaterPortletKeys.NAVIGATIONUPDATERWEB;
	}

	@Override
	@Reference(
		target = "(javax.portlet.name=" + NavigationUpdaterPortletKeys.NAVIGATIONUPDATERWEB + ")",
		unbind = "-"
	)
	public void setPortlet(Portlet portlet) {
		super.setPortlet(portlet);
	}
	
	/**
	 * This override is necessary due to https://issues.liferay.com/browse/LPS-146186 (and until that issue is fixed)
	 */
	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass());
		return LanguageUtil.get(resourceBundle, "javax.portlet.title." + NavigationUpdaterPortletKeys.NAVIGATIONUPDATERWEB);
	}
}
