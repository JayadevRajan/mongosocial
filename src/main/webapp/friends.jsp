<?xml version="1.0" encoding="ISO-8859-1" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html" version="2.0">
	<jsp:directive.page language="java"
		contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" />
	<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<link type="text/css" rel="stylesheet" href="/css/mongostyles.css" />
<title>Mongobook</title>
</head>
<body class="document-body">
	<f:view>
		<h:form>
			<div class="header">
				<div class="header-container">
					<h:panelGrid id="panel" columns="4" border="0"
						cellpadding="10" cellspacing="1" rendered="true" style="width:100%;">
						<h:outputText value="Mongobook" styleClass="header-logo"/>
						<h:outputText styleClass="header-link" value = "#{userBean.userName}"/>
						<h:panelGrid id="panel2" columns="5" border="0"
						cellpadding="8" cellspacing="2" rendered="true" styleClass="header-links">
							<h:commandLink action="search" styleClass="header-link">
								<h:outputText value="Search"/>
							</h:commandLink>
							<h:commandLink action="friends" styleClass="header-link">
								<h:outputText value="My Friends"/>
							</h:commandLink>
							<h:commandLink action="home" styleClass="header-link">
								<h:outputText value="My Home"/>
							</h:commandLink>
							<h:commandLink action="profile" styleClass="header-link">
								<h:outputText value="My Profile"/>
							</h:commandLink>
							<h:commandLink action="signout" styleClass="header-link" actionListener="#{userBean.signOut}">
								<h:outputText value="Sign Out"/>
							</h:commandLink>
						</h:panelGrid>
					</h:panelGrid>
				</div>
			</div>
			<div class="content">
				<div class="content-container">
					<h:panelGrid id="pnl3" columns="2" border="0" cellpadding="8"
						cellspacing="2" rendered="true">
						<h:panelGrid id="pnl4" columns="1" styleClass="left-col">
							<h:graphicImage styleClass="profile-pic"
								value="/image?user=#{userBean.userName}"></h:graphicImage>
							<h:outputText value="#{userBean.location}"></h:outputText>
						</h:panelGrid>
						<h:panelGrid id="pnl5" columns="1" styleClass="content-col">
							<h:panelGrid id="pnl6" columns="1" styleClass="stretch-width">
								<h:dataTable value="#{userBean.friends}" var="friend" styleClass="profile-view-box">
									<h:column >
										<h:panelGrid columns="2" styleClass="search-result" columnClasses="profile-view-profile-pic-column,profile-view-details-column">
											<h:graphicImage styleClass="profile-pic"
												value="/image?user=#{friend.friendName}"></h:graphicImage>
											<h:panelGrid columns="1">
												<h:outputText value="#{friend.friendName}"
													styleClass="body-message"></h:outputText>
												<h:outputText value="#{friend.location}"></h:outputText>
											</h:panelGrid>
										</h:panelGrid>
									</h:column>
								</h:dataTable>
							</h:panelGrid>
						</h:panelGrid>
					</h:panelGrid>
				</div>
			</div>
		</h:form>
	</f:view>
</body>
	</html>
</jsp:root>