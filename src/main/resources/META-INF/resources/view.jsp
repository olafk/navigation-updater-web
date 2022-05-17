<%@page import="com.liferay.site.navigation.model.SiteNavigationMenuItem"%>
<%@page import="java.util.List"%>
<%@ include file="init.jsp" %>
<div class="container-view container-xl">
<h2>
	<liferay-ui:message key="navigationupdaterweb.caption"/>
</h2>

<p>
	<liferay-ui:message key="navigationupdaterweb.explanation"/>
</p>

<% String items = (String)renderRequest.getAttribute("urls");
   if(items != null) { %>

		<p>
			<liferay-ui:message key="navigationupdaterweb.items-found"/>
		</p>

		<%=items %>
		
		<portlet:actionURL name="patchNavigationUrls" var="patchURLs"/>
		
		<aui:form action="<%= patchURLs %>" name="<portlet:namespace />fm">
		        <aui:fieldset>
		            <aui:input label="navigationupdaterweb.new-host-name" name="host" value="<%=renderRequest.getServerName() %>"/>
		        </aui:fieldset>
		
		        <aui:button-row>
		            <aui:button type="submit" value="navigationupdaterweb.patch-host-names"/>
		        </aui:button-row>
		</aui:form>

<% } else { %>

<p>
	<liferay-ui:message key="navigationupdaterweb.no-items-found"/>
</p>

<% } %>
</div>