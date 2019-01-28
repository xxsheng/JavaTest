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
<title>系统控制</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<meta http-equiv="Content-type" content="text/html; charset=utf-8">
<meta content="" name="description"/>
<meta content="" name="author"/>
<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link href="${cdnDomain}theme/assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/global/plugins/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css">
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css"/>
<!-- END GLOBAL MANDATORY STYLES -->
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-select/bootstrap-select.min.css" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-toastr/toastr.min.css" rel="stylesheet" type="text/css"/>

<link href="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.css" rel="stylesheet" type="text/css"/>
<!-- BEGIN THEME STYLES -->
<link href="${cdnDomain}theme/assets/global/css/components.css?v=${cdnVersion}" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/global/css/plugins.css" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/admin/layout/css/layout.css" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/admin/layout/css/themes/default.css?v=${cdnVersion}" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/admin/layout/css/custom.css?v=${cdnVersion}" rel="stylesheet" type="text/css"/>
<!-- END THEME STYLES -->
<link rel="shortcut icon" href="favicon.ico"/>
</head>
<body class="page-container-bg-solid">
<!-- BEGIN CONTAINER -->
<div class="page-container">
	<!-- BEGIN SIDEBAR -->
	<div class="page-sidebar-wrapper"></div>
	<!-- END SIDEBAR -->
	<!-- BEGIN CONTENT -->
	<div class="page-content-wrapper">
		<div class="page-content">
			<!-- BEGIN PAGE HEADER-->
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>服务器控制面板</li>
				</ul>
			</div>
			<!-- END PAGE HEADER-->
			<!-- BEGIN PAGE CONTENT-->
			<div id="table-lottery-sys-control" class="row">
				<div class="col-md-12">
					<!-- BEGIN PORTLET-->
					<div class="portlet light">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-cogs font-green-sharp"></i>
								<span class="caption-subject font-green-sharp bold uppercase">服务器控制面板</span>
							</div>
						</div>
						<div class="portlet-body form">
							<!-- BEGIN FORM-->
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
								
									<h4 class="form-section">开奖数据采集</h4>
									<div data-server="OPENCODE" class="form-group">
										<label class="control-label col-md-3">运行状态：</label>
										<label data-field="status" class="control-label col-md-3">没有采集到开奖号码时候</label>
										<div class="col-md-4">
											<button data-command="restart" type="submit" class="btn blue"><i class="fa fa-question"></i> 重启</button>
										</div>
									</div>
									
									<h4 class="form-section">开奖服务器</h4>
									<div data-server="LP" class="form-group">
										<label class="control-label col-md-3">运行状态：</label>
										<label data-field="status" class="control-label col-md-1">未知</label>
										<div class="col-md-8">
											<button data-command="status" type="submit" class="btn yellow"><i class="fa fa-question"></i> 获取状态</button>
											<button data-command="start" type="submit" class="btn green"><i class="fa fa-question"></i> 启动</button>
											<button data-command="stop" type="submit" class="btn red"><i class="fa fa-question"></i> 停止</button>
											<button data-command="restart" type="submit" class="btn blue"><i class="fa fa-question"></i> 重启</button>
										</div>
									</div>
									
									<h4 class="form-section">自主彩-分分彩</h4>
									<div data-server="LP-1" class="form-group">
										<label class="control-label col-md-3">运行状态：</label>
										<label data-field="status" class="control-label col-md-1">未知</label>
										<div class="col-md-8">
											<button data-command="status" type="submit" class="btn yellow"><i class="fa fa-question"></i> 获取状态</button>
											<button data-command="start" type="submit" class="btn green"><i class="fa fa-question"></i> 启动</button>
											<button data-command="stop" type="submit" class="btn red"><i class="fa fa-question"></i> 停止</button>
											<button data-command="restart" type="submit" class="btn blue"><i class="fa fa-question"></i> 重启</button>
										</div>
									</div>
									
									<h4 class="form-section">自主彩-三分彩</h4>
									<div data-server="LP-3" class="form-group">
										<label class="control-label col-md-3">运行状态：</label>
										<label data-field="status" class="control-label col-md-1">未知</label>
										<div class="col-md-8">
											<button data-command="status" type="submit" class="btn yellow"><i class="fa fa-question"></i> 获取状态</button>
											<button data-command="start" type="submit" class="btn green"><i class="fa fa-question"></i> 启动</button>
											<button data-command="stop" type="submit" class="btn red"><i class="fa fa-question"></i> 停止</button>
											<button data-command="restart" type="submit" class="btn blue"><i class="fa fa-question"></i> 重启</button>
										</div>
									</div>
									
									<h4 class="form-section">自主彩-五分彩</h4>
									<div data-server="LP-5" class="form-group">
										<label class="control-label col-md-3">运行状态：</label>
										<label data-field="status" class="control-label col-md-1">未知</label>
										<div class="col-md-8">
											<button data-command="status" type="submit" class="btn yellow"><i class="fa fa-question"></i> 获取状态</button>
											<button data-command="start" type="submit" class="btn green"><i class="fa fa-question"></i> 启动</button>
											<button data-command="stop" type="submit" class="btn red"><i class="fa fa-question"></i> 停止</button>
											<button data-command="restart" type="submit" class="btn blue"><i class="fa fa-question"></i> 重启</button>
										</div>
									</div>
									
									<h4 class="form-section">第三方支付</h4>
									<div data-server="Payment" class="form-group">
										<label class="control-label col-md-3">运行状态：</label>
										<label data-field="status" class="control-label col-md-1">未知</label>
										<div class="col-md-8">
											<button data-command="status" type="submit" class="btn yellow"><i class="fa fa-question"></i> 获取状态</button>
											<button data-command="start" type="submit" class="btn green"><i class="fa fa-question"></i> 启动</button>
											<button data-command="stop" type="submit" class="btn red"><i class="fa fa-question"></i> 停止</button>
											<button data-command="restart" type="submit" class="btn blue"><i class="fa fa-question"></i> 重启</button>
										</div>
									</div>
									
									<h4 class="form-section">后台服务器</h4>
									<div data-server="AdminManager" class="form-group">
										<label class="control-label col-md-3">运行状态：</label>
										<label data-field="status" class="control-label col-md-1">未知</label>
										<div class="col-md-8">
											<button data-command="status" type="submit" class="btn yellow"><i class="fa fa-question"></i> 获取状态</button>
											<button data-command="start" type="submit" class="btn green"><i class="fa fa-question"></i> 启动</button>
											<button data-command="stop" type="submit" class="btn red"><i class="fa fa-question"></i> 停止</button>
											<button data-command="restart" type="submit" class="btn blue"><i class="fa fa-question"></i> 重启</button>
										</div>
									</div>
									
										<h4 class="form-section">域名验证</h4>
									<div data-server="HostNameVerify" class="form-group">
										<label class="control-label col-md-3">运行状态：</label>
										<label data-field="status" class="control-label col-md-1">未知</label>
										<div class="col-md-8">
											<button data-command="status" type="submit" class="btn yellow"><i class="fa fa-question"></i> 获取状态</button>
											<button data-command="start" type="submit" class="btn green"><i class="fa fa-question"></i> 启动</button>
											<button data-command="stop" type="submit" class="btn red"><i class="fa fa-question"></i> 停止</button>
											<button data-command="restart" type="submit" class="btn blue"><i class="fa fa-question"></i> 重启</button>
										</div>
									</div>
								
									
								</div>
							</form>
						</div>
					</div>
					<!-- END PORTLET-->
				</div>
			</div>
			<!-- END PAGE CONTENT-->
		</div>
	</div>
	<!-- END CONTENT -->
</div>
<!-- END CONTAINER -->
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
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" type="text/javascript"></script>
<!-- END CORE PLUGINS -->
<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-select/bootstrap-select.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-toastr/toastr.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-sys-control.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
jQuery(document).ready(function() {
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	// init data
	LotterySysControl.init();
});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>