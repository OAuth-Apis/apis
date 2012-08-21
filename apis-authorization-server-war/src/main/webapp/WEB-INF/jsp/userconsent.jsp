<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">
  <title>Consent</title>
  <!-- Le styles -->
  <link href="/client/css/style.css" rel="stylesheet">

  <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
  <!--[if lt IE 9]>
  <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
</head>
<body>


<div class="head">
  <img src="/client/img/surf-oauth.png"/>
</div>
<div class="main">
  <div class="full">


    <div class="page-header">
      <h1><strong>${client.name}</strong> wants to retrieve data
        from <strong>${client.resourceServer.name}</strong></h1>
    </div>

    <div class="consent">
      <img alt="${client.resourceServer.name}"
           title="${client.resourceServer.name}"
           src="${client.resourceServer.thumbNailUrl}">
      <img src="/client/img/arrow.png">
      <img alt="${client.name}"
           title="${client.name}" src="${client.thumbNailUrl}">
    </div>

    <form id="accept" method="post" action="${actionUri}">
      <input type="hidden" name="AUTH_STATE" value="${AUTH_STATE}"/>

      <h2>This data will be shared</h2>

      <fieldset>
        <c:forEach items="${client.scopes}" var="scope">
			<input type="checkbox" id="GRANTED_SCOPES" name="GRANTED_SCOPES" checked value="${scope}"/>
			<span class="consent-label">${scope}</span><br/>
        </c:forEach>
	  </fieldset>	
      <fieldset>
        <div class="form-actions">
          <button id="user_oauth_approval" name="user_oauth_approval" value="true" type="submit"
                  class="btn btn-success">Grant permission</button>
          &nbsp;&nbsp;&nbsp;<em>or</em>&nbsp;&nbsp;&nbsp;
          <button type="submit" name="user_oauth_approval" value="false"
                  class="btn btn-danger">Deny permission</button>
        </div>
      </fieldset>
    </form>
  </div>
</div>

<div class="foot">
  <p>Powered by <a href="http://www.surfnet.nl/">SURFnet</a>. Fork me on <a href="https://github.com/oharsta/oa-aas/">Github</a>. Licensed under the <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache License 2.0</a>.</p>
</div>
</body>
</html>