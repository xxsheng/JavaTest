<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
<meta charset="utf-8"/>
<title>Page error</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<meta http-equiv="Content-type" content="text/html; charset=utf-8">
<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link href="${cdnDomain}theme/assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<!-- END GLOBAL MANDATORY STYLES -->
<!-- BEGIN PAGE LEVEL STYLES -->
<link href="${cdnDomain}theme/assets/admin/pages/css/error.css?v=${cdnVersion}" rel="stylesheet" type="text/css"/>
<!-- END PAGE LEVEL STYLES -->
<!-- BEGIN THEME STYLES -->
<link href="${cdnDomain}theme/assets/global/css/components.css?v=${cdnVersion}" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/global/css/plugins.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/admin/layout/css/layout.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/admin/layout/css/themes/default.css?v=${cdnVersion}" rel="stylesheet" type="text/css" id="style_color">
<link href="${cdnDomain}theme/assets/admin/layout/css/custom.css?v=${cdnVersion}" rel="stylesheet" type="text/css">
<!-- END THEME STYLES -->
<link rel="shortcut icon" href="favicon.ico"/>
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="page-404-full-page error-body">
<div class="row">
	<div class="col-md-12 page-404">
		<div class="error-container">
			<div class="error-main">
				<div class="error-number error-500">500</div>
				<div class="error-description">
					I'm very sorry, the page seems error occurred.
				</div>
				<div class="error-description-mini">
					You can try again or visit later.
				</div>
				<br>
				<form action="javascript:;">
					<div class="input-group">
						<input class="form-control" placeholder="What are you looking for?" type="text">
						<span class="input-group-btn">
							<button type="submit" class="btn blue"><i class="fa fa-search"></i> Search</button>
						</span>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
</body>
<!-- END BODY -->
</html>