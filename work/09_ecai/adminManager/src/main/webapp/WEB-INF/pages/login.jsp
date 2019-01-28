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
<title>管理平台</title>
<script>
	var cdnDomain = '${cdnDomain}';
	if (cdnDomain == '/') {
		cdnDomain = "<%=request.getScheme() + "://" + request.getHeader("Host") + "/"%>" ;
	}
	var cdnVersion = '${cdnVersion}';
</script>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<meta http-equiv="Content-type" content="text/html; charset=utf-8">
<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link href="${cdnDomain}theme/assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/global/plugins/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css">
<!-- END GLOBAL MANDATORY STYLES -->
<!-- BEGIN PAGE LEVEL STYLES -->
<link href="${cdnDomain}theme/assets/admin/pages/css/login.css?v=${cdnVersion}" rel="stylesheet" type="text/css"/>
<!-- END PAGE LEVEL SCRIPTS -->
<!-- BEGIN THEME STYLES -->
<link href="${cdnDomain}theme/assets/global/css/components.css?v=${cdnVersion}" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/global/css/plugins.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/admin/layout/css/layout.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/admin/layout/css/themes/default.css?v=${cdnVersion}" rel="stylesheet" type="text/css" id="style_color">
<link href="${cdnDomain}theme/assets/admin/layout/css/custom.css?v=${cdnVersion}" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="${cdnDomain}theme/assets/global/plugins/animate/animate.min.css" type="text/css">
<!-- END THEME STYLES -->
<link rel="shortcut icon" href="favicon.ico"/>
</head>
<!-- BEGIN BODY -->
<body>
<!-- BEGIN LOGIN -->
<div class="login" id="loginContent" style="display: none;">
	<form class="login-form" id="loginForm" action="javascript:;">
		<h3 class="form-title">管理平台</h3>
		<div class="alert alert-danger display-hide" style="padding: 8px 15px; margin-bottom: 15px;">
			<button class="close" data-close="alert"></button>
			<span></span>
		</div>
		<div class="form-group">
			<!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
			<label class="control-label visible-ie8 visible-ie9">Username</label>
			<div class="input-icon">
				<i class="fa fa-user"></i>
				<input name="username" class="form-control placeholder-no-fix" type="text" autocomplete="off" placeholder="Username" autofocus/>
			</div>
		</div>
		<div class="form-group">
			<label class="control-label visible-ie8 visible-ie9">Password</label>
			<div class="input-icon">
				<i class="fa fa-lock"></i>
				<input name="password" class="form-control placeholder-no-fix" type="password" autocomplete="off" placeholder="Password" />
			</div>
		</div>
		<div class="form-group" id="googleAuth" style="display: none;">
			<label class="control-label visible-ie8 visible-ie9">Google Auth</label>
			<div class="input-icon">
				<i class="fa fa-lock"></i>
				<input name="googlecode" class="form-control placeholder-no-fix" type="password" required minlength="6" maxlength="6" digits="true" autocomplete="off" placeholder="Google口令" />
			</div>
		</div>
		<div class="form-actions">
			<button data-command="submit" type="button" class="btn btn-primary pull-right">
				登录 <i class="fa fa-sign-in"></i>
			</button>
		</div>
	</form>
</div>
<!-- END LOGIN -->
<%--<div id="particles">--%>
<%--</div>--%>

<canvas id="background-canvas"></canvas>

<!-- Google动态密码 -->
<div id="modal-admin-mod-google-pwd" class="modal fade" data-backdrop="static" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"></button>
				<h4 class="modal-title">绑定Google动态密码</h4>
			</div>
			<div class="modal-body" style="padding: 30px 20px 15px 20px;">
				<form class="form-horizontal" id="googleBindForm">
					<div class="form-body">
						<div class="form-group">
							<div style="display: inline;padding: 5px;float: left;"><img alt="" src="" id="googleQR" ></div>
							<div style="display: inline;padding: 5px;float: left;margin-top: 10px;"><h4>Google Auth 使用说明</h4> <span>iOS和Android用户到App Store搜索 Google Authenticator </span></div>
							<div style="display: inline;padding: 5px;float: left;"><h4>第一步</h4> <span>到App Store下载客户端</span></div><br>
							<div style="display: inline;padding: 5px;float: left;"><h4>第二步</h4> <span>输入客户端的口令绑定动态密码</span></div>
							<div style="display: inline;padding: 5px;float: left;"><h4>无法下载Google Authenticator？</h4> <span>也可以使用其它软件,如Authy等</span></div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">登录密码</label>
							<div class="col-md-9">
								<input name="loginPassword" class="form-control input-inline input-medium" autocomplete="off" type="password">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">Google口令</label>
							<div class="col-md-9">
								<input name="password" class="form-control input-inline input-medium" autocomplete="off" type="password">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">秘钥</label>
							<div class="col-md-9">
								<input id="googleSecret" class="form-control input-inline input-medium" type="text" readonly disabled>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label"></label>
							<div class="col-md-7">*每个二维码只能用一次，重新打开需要重新扫描!</div>
							<label class="col-md-3 control-label"></label>
							<div class="col-md-7">*绑定成功后，请勿删除手机中的密码器!</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" data-command="submit" class="btn green-meadow"><i class="fa fa-check"></i> 确认</button>
				<button type="button" data-dismiss="modal" class="btn default"><i class="fa fa-undo"></i> 取消</button>
			</div>
		</div>
	</div>
</div>
<!-- END COPYRIGHT -->
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<!-- BEGIN CORE PLUGINS -->
<!--[if lt IE 9]>
<script src="${cdnDomain}theme/assets/global/plugins/respond.min.js"></script>
<script src="${cdnDomain}theme/assets/global/plugins/excanvas.min.js"></script>
<![endif]-->
<script src="${cdnDomain}theme/assets/global/plugins/jquery.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-migrate.min.js" type="text/javascript"></script>
<!-- IMPORTANT! Load jquery-ui-1.10.3.custom.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
<script src="${cdnDomain}theme/assets/global/plugins/jquery-ui/jquery-ui-1.10.3.custom.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery.cokie.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script>
<%--<script src="${cdnDomain}theme/assets/global/plugins/particles/particles.min.js" type="text/javascript"></script>--%>
<!-- END CORE PLUGINS -->
<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/login.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
jQuery(document).ready(function() {     
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	Login.init();
});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>