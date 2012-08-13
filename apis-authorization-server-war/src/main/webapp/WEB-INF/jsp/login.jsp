<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Login University Foo</title>
	<link rel="stylesheet" href="/assets/bootstrap-2.0.2/css/bootstrap.css" />
	<link rel="stylesheet" href="/assets/awesome-1.0.0/css/font-awesome.css" />
	<script type="text/javascript" src="/assets/js/jquery-1.7.2.js"></script>
	<script type="text/javascript"
		src="/assets/bootstrap-2.0.2/js/bootstrap.js"></script>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="span8">
				<form class="form-horizontal" id="registerHere" method="post"
					action="${actionUri}">
					<fieldset>

						<legend>Login with your identifier and password</legend>
						
						<div class="control-group">
							<label class="control-label">Identifier</label>
							<div class="controls">
								<input type="text" class="input-xlarge" id="username"
									name="username" rel="popover"
									data-content="Enter your identifier."
									data-original-title="Identifier">
								<p class="help-block">Hint: can be anything</p>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">Password</label>
							<div class="controls">
								<input type="password" class="input-xlarge" id="password"
									name="password" rel="popover"
									data-content="What's your password?"
									data-original-title="Password">
								<p class="help-block">Hint: can be anything</p>
							</div>
						</div>
						<input type="hidden" name="AUTH_STATE"
							value="${AUTH_STATE}" /> 
					</fieldset>
					<div class="control-group">
						<label class="control-label"></label>
						<div class="controls">
							<button type="submit" class="btn btn-success">Login</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>

</body>
</html>