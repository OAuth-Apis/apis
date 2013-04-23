<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%--
  Copyright 2012 SURFnet bv, The Netherlands

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  --%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Example Client App</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="<c:url value="/assets/bootstrap-2.0.2/css/bootstrap.min.css"/>"></link>
<link rel="stylesheet"
	href="<c:url value="/assets/awesome-1.0.0/css/font-awesome.css"/>"></link>
<link rel="stylesheet" href="<c:url value="/assets/css/style.css"/>"></link>
<link rel="stylesheet" href="<c:url value="/assets/css/style-additional.css"/>"></link>
<link rel="stylesheet" href="<c:url value="/assets/css/client.css"/>"></link>
<script type="text/javascript"
	src="<c:url value="/assets/js/jquery-1.7.2.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/assets/bootstrap-2.0.2/js/bootstrap.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/assets/js/client.js"/>"></script>
	
<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
	
</head>
<body>
    <div class="head">
      <img src="<c:url value="/assets/img/surf-oauth.png"/>" />
    </div>
    <div class="subcontent">
	<div class="row">
		<div class="span11 columns">
			<p>The client app simulates a (native) application that first obtaines an Access Token from the Authorization Server and then uses the example Resource Server to access protected resources.</p>
		</div>
	</div>

	<div class="row">
		<div class="span7 columns">
			<form:form action="/test" commandName="settings"
				method="post" class="form-horizontal">
				<div class="accordion" id="mainOptions">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse"
								data-parent="#mainOptions" href="#step1"> <span
								class="badge badge-info">1</span> OAuth Settings</a>
						</div>
						<div id="step1" class="accordion-body collapse">
							<div class="accordion-inner">
								<fieldset>
									<div class="control-group">
										<div class="control-group">
											<label class="control-label" for="oauthKey">OAuth key</label>
											<div class="controls">
												<form:input path="oauthKey" id="oauthKey" name="oauthKey"
													class="input-xxlarge" readonly="true"/>
												<p class="help-block">The client must have obtained the key out-of-band during the registration process</p>	
											</div>
										</div>
										<div class="control-group" id="secretInput">
											<label class="control-label" for="oauthSecret">OAuth
												secret</label>
											<div class="controls">
												<form:input path="oauthSecret" id="oauthSecret"
													name="oauthSecret" class="input-xxlarge" readonly="true"/>
												<p class="help-block">The secret (normally an UUID) is also out-of-band obtained by the client during the registration process</p>	
											</div>
										</div>
									</div>
										<div id="oauth20Input">
											<div class="control-group">
												<label class="control-label" for="accessTokenEndPoint">AccessToken
													URL</label>
												<div class="controls">
													<form:input path="accessTokenEndPoint"
														id="accessTokenEndPoint" name="accessTokenEndPoint"
														class="input-xxlarge" readonly="true"/>
												<p class="help-block">The access token endpoint of the Authorization server</p>	
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label" for="authorizationURL">Authorization
												URL</label>
											<div class="controls">
												<form:input path="authorizationURL" id="authorizationURL"
													name="authorizationURL" class="input-xxlarge" readonly="true"/>
												<p class="help-block">The authorization token endpoint of the Authorization server</p>	
											</div>
										</div>
									<div class="form-actions">
										<button name="step1" class="btn btn-primary">Next</button>
										<button name="reset" class="btn">Reset</button>
									</div>

								</fieldset>
							</div>
						</div>
					</div>
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse"
								data-parent="#mainOptions" href="#step2"> <span
								class="badge badge-info">2</span> OAuth Authorization</a>
						</div>
						<div id="step2" class="accordion-body collapse">
							<div class="accordion-inner">
								<fieldset>
									<div class="control-group">
										<label class="control-label" for="authorizationURLComplete">Authorization URL</label>
										<div class="controls break-word">
											<form:textarea path="authorizationURLComplete" id="authorizationURLComplete" name="authorizationURLComplete"
												rows="5" class="input-xxlarge" readonly="true"/>
											<p class="help-block">This is the URL to redirect to for user authentication. It is based on the authorization url of the Authorization Server and the registered client 
													in /apis-authorization-server/src/main/resources/db/migration/hsqldb/V1__auth-server-admin.sql</p>	
										</div>
									</div>
									<div class="form-actions">
										<button name="step2" class="btn btn-primary">Next</button>
										<button name="reset" class="btn">Reset</button>
									</div>
								</fieldset>
							</div>
						</div>
					</div>
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse"
								data-parent="#mainOptions" href="#step3"> <span
								class="badge badge-info">3</span> OAuth Requests </a>
						</div>
						<div id="step3" class="accordion-body collapse">
							<div class="accordion-inner">
								<fieldset>
									<div class="control-group">
										<label class="control-label" for="accessToken">Access token</label>
										<div class="controls">
											<form:input path="accessToken" id="accessToken"
												name="accessToken" class="input-xxlarge" readonly="true"/>
											<p class="help-block">Note: this is the accessToken for
												all subsequent OAuth queries</p>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label" for="requestURL">API
											Request </label>
										<div class="controls">
											<form:input path="requestURL" id="requestURL"
												name="requestURL" class="input-xxlarge" />
											<p class="help-block">An endpoint on the example Resource server</p>
											<p class="help-block">Hint:
												http://localhost:8180/v1/api/course</p>
											<p class="help-block">Hint:
												http://localhost:8180/v1/api/course/cs3</p>
											<p class="help-block">Hint:
												http://localhost:8180/v1/api/student</p>
											<p class="help-block">Hint:
												http://localhost:8180/v1/api/student/foo5</p>
										</div>
									</div>
									<div class="form-actions">
										<button id="step3" name="step3" class="btn btn-primary">Fetch</button>
										<button name="reset" class="btn">Reset</button>
									</div>
								</fieldset>
							</div>
						</div>
					</div>
				</div>
				<input id="step" type="hidden"
					value="<c:out value="${settings.step}"/>" name="step" />
			</form:form>
		</div>
		<div class="span5 columns">
			<div id="request">
				<div class="alert alert-info alert-http">HTTP Request</div>
				<pre id="requestInfo" class="prettyprint pre-scrollable pre-json"><c:out value="${requestInfo}" /></pre>
			</div>
			<div id="response">
				<div class="alert alert-info alert-http">HTTP Response Headers</div>
				<pre id="responseInfo" class="prettyprint pre-scrollable pre-json"><c:out value="${responseInfo}" /></pre>
			</div>
			<div id="raw-response">
				<div class="alert alert-info alert-http">HTTP Response Body </div>
				<pre id="rawResponseInfo" class="prettyprint pre-scrollable pre-json"><c:out value="${rawResponseInfo}" /></pre>
			</div>
		</div>
	</div>
	</div>
<div class="foot">
  <p>Powered by <a href="http://www.surfnet.nl/">SURFnet</a>. Fork me on <a href="https://github.com/oharsta/apis/">Github</a>. Licensed under the <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache License 2.0</a>.</p>
</div>

</body>
</html>
