<!DOCTYPE html>
<html lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <meta name="description" content="" />
  <meta name="author" content="" />

  <title>Login</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/client/css/bootstrap.min.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/client/css/style.css" />
  <script type="text/javascript" src="${pageContext.request.contextPath}/client/js/lib/jquery.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/client/js/lib/bootstrap.min.js"></script>
</head>

<body>

<div class="head">
  <img src="${pageContext.request.contextPath}/client/img/surf-oauth.png"/>
</div>

<div class="main">
  <div class="full">
    <div class="page-header">
      <h1>Login</h1>
    </div>

    <form class="form-horizontal" id="registerHere" method="post"
      action="${actionUri}">
      <fieldset>
        <div class="control-group">
          <label class="control-label">Identifier</label>
          <div class="controls">
            <input type="text" class="input-xlarge" id="username"
              name="j_username" rel="popover"
              data-content="Enter your identifier."
              data-original-title="Identifier" />
            <p class="help-block">Hint: can be anything</p>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label">Password</label>
          <div class="controls">
            <input type="password" class="input-xlarge" id="password"
              name="j_password" rel="popover"
              data-content="What's your password?"
              data-original-title="Password" />
            <p class="help-block">Hint: can be anything</p>
          </div>
        </div>
        <input type="hidden" name="AUTH_STATE" value="${AUTH_STATE}" />
      </fieldset>

      <div class="form-actions">
        <button type="submit" class="btn btn-primary">Login</button>
      </div>
    </form>
  </div>
</div>

<div class="foot">
  <p>Powered by <a href="http://www.surfnet.nl/">SURFnet</a>. Fork me on <a href="https://github.com/OpenConextApps/oa-aas/">Github</a>. Licensed under the <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache License 2.0</a>.</p>
</div>

</body>
</html>