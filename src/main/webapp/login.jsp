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
		<h:form id="loginForm">
			<div class="header">
				<div class="header-container">
					<h:panelGrid id="panel" columns="3" border="0"
						cellpadding="10" cellspacing="1" rendered="true" style="width:100%;">
						<h:outputText value="Mongobook" styleClass="header-logo"/>
					</h:panelGrid>
				</div>
			</div>
			<div styleClass="content"> 
				<div class="content-container">
					<h:panelGrid id="pnl3" columns="2" border="0"
						cellpadding="0" cellspacing="2" rendered="true">
						<h:panelGrid id="pnl5" columns="2" styleClass="stretch-width">
							<h:outputText value="Welcome to Mongobook. 
							Sign in to learn the awesome features of mongo social" styleClass="body-message"/>
							<h:panelGrid id="pnl6" columns="2" border="0"
							   cellpadding="10" cellspacing="1">
							      <f:facet name="header">
							      	<h:panelGrid columns="1">
							         	<h:outputText value="Sign Up/Sign In" styleClass="body-message"/>
							         	<h:message id="message" for="username" style="color:red; font-weight:bold;"></h:message>
							         </h:panelGrid>
							      </f:facet>
							      <h:outputLabel value="Username"/>
							      <h:inputText id="username" value="#{userBean.userName}" />
							      <h:outputLabel value="Password" />
							      <h:inputSecret id="password" value="#{userBean.password}"/>
							      <f:facet name="footer">
							         <h:panelGrid columns="2" style="display:block; text-align:center; width;100%">
							            <h:commandButton id="signin" value="Sign In" styleClass="button" 
							            			action="#{userBean.action}"	 actionListener="#{userBean.signIn}"/>
										<h:commandButton id="signup" value="Sign Up" styleClass="button"
													action="#{userBean.action}" actionListener="#{userBean.signUp}"/>
							         </h:panelGrid>
							      </f:facet>
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