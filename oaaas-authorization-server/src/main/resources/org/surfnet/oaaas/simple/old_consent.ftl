<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="robots" content="noindex, nofollow" />
		<meta name="viewport" content="width=device-width" />

		<title>Consent</title>

		<link href="https://static.surfconext.nl/css/responsive/screen.css"
			rel="stylesheet" type="text/css" media="screen" />
		<link rel="stylesheet" href="/assets/bootstrap-2.0.2/css/bootstrap.css" />
		<link rel="stylesheet" href="/assets/awesome-1.0.0/css/font-awesome.css" />
		<link href="/assets/main.css" rel="stylesheet" type="text/css" media="screen" />

		<script type="text/javascript" src="/assets/js/jquery-1.7.2.js"></script>
		<script type="text/javascript"
			src="/assets/bootstrap-2.0.2/js/bootstrap.js"></script>
</head>
<body>
	<div id="wrapper">
  		<div id="main">
    <h1>${context.client.name} (${context.client.description}) is asking consent for accessing:"/></h1>

    <div class="logos">
        <img class="logo"
             alt="${context.client.name}"
             title="${context.client.name}"
             src="${context.client.thumbNailUrl}"/>
      </div>

  <div id="approve">
        <form id="accept" method="post" action="${context.actionUri}">
          <p>
            <input name="user_oauth_approval" value="true" type="hidden"/>
		      <ul class="scopes">
		      	<#list scopes as scope>
			        <li><input id="granted_scopes" type="checkbox" name="granted_scopes" checked="yes" value="${scope}"/></li>
				</#list>
		      </ul>

            <input id="accept_terms_button"
                   class="submit bigbutton"
                   type="submit"
                   value="Yes, grant access"
                   style="font-weight: bold;" />
          </p>
        </form>
	</div>
    <div id="deny">
        <form id="reject" method="post" action="${context.actionUri}">
          <p>
            <input name="user_oauth_approval" value="false" type="hidden"/>

            <input id="decline_terms_button" class="submit bigbutton"
                   type="submit" value="No, deny access" />
          </p>
        </form>
      </div>
	<div id="consent">	
      <p>
      
      </p>
    </div>


</body>
</html>