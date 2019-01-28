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
<title>用户单期最高奖金限额</title>
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
			<h3 class="page-title">单期最高奖金限额配置</h3>
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>当前位置：站点维护<i class="fa fa-angle-right"></i></li><li>单期奖金限额</i></li>
				</ul>
			</div>
			<!-- END PAGE HEADER-->
			<div id="modal-lottery-gobal-bets-limit-add" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">新增 彩种限额</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">彩种</label>
										<div class="col-md-9">
											<select name="lotteryId" class="form-control input-medium">
												<option value="101">重庆时时彩</option>
												<option value="102">江西时时彩</option>
												<option value="103">新疆时时彩</option>
												<option value="104">天津时时彩</option>
												<option value="110">分分彩</option>
												<option value="111">三分彩</option>
												<option value="112">五分彩</option>
												<option value="201">广东11选5</option>
												<option value="202">江西11选5</option>
												<option value="203">重庆11选5</option>
												<option value="204">安徽11选5</option>
												<option value="205">上海11选5</option>
												<option value="206">十一运夺金</option>
												<option value="301">江苏快3</option>
												<option value="206">安徽快3</option>
												<option value="303">湖北快3</option>
												<option value="304">吉林快3</option>
												<option value="401">福彩3D</option>
												<option value="402">排列三</option>
												<option value="501">北京快乐8</option>
												<option value="502">北京PK拾</option>
											</select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">单期奖金限额</label>
										<div class="col-md-9">
											<input name="maxPrize" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">用户单期奖金限额</label>
										<div class="col-md-9">
											<input name="maxBet" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
										</div>
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
			
			<div id="modal-lottery-user-bets-limit-add" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">新增 彩种限额</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">彩种</label>
										<div class="col-md-9">
											<select name="lotteryId" class="form-control input-medium">
												<option value="101">重庆时时彩</option>
												<option value="102">江西时时彩</option>
												<option value="103">新疆时时彩</option>
												<option value="104">天津时时彩</option>
												<option value="105">台湾5分彩</option>
												<option value="110">分分彩</option>
												<option value="111">三分彩</option>
												<option value="112">五分彩</option>
												<option value="201">广东11选5</option>
												<option value="202">江西11选5</option>
												<option value="203">重庆11选5</option>
												<option value="204">安徽11选5</option>
												<option value="205">上海11选5</option>
												<option value="206">十一运夺金</option>
												<option value="301">江苏快3</option>
												<option value="206">安徽快3</option>
												<option value="303">湖北快3</option>
												<option value="304">吉林快3</option>
												<option value="401">福彩3D</option>
												<option value="402">排列三</option>
												<option value="501">北京快乐8</option>
												<option value="502">北京PK拾</option>
											</select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="userName" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">单期最大限额</label>
										<div class="col-md-9">
											<input name="maxBet" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
										</div>
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
			
			<!-- BEGIN PAGE CONTENT-->
			<div id="table-lottery-gobal-bets-limit-list" class="row">
				<div class="col-md-12">
					<!-- BEGIN PORTLET-->
					<div class="portlet light" style="margin-bottom: 10px;">
						<div class="portlet-body">
							<div class="table-toolbar">
								<div class="form-inline">
									<div class="row">
										<div class="col-md-12">
											<div class="btn-group">
												<button data-command="add" class="btn green">
													<i class="fa fa-plus"></i> 全局单期奖金限额
												</button>
											</div>
										</div>
									</div>
								</div>
							</div>
							
							<div class="alert alert-info" role="alert">1.系统全局每个彩种每期最大投注限额，用户配置优先级高于全局配置 <br> 2.单期奖金限额:	每期用户最大中奖金额</div>
							
							<div class="table-scrollable table-scrollable-borderless">
								<table class="table table-hover table-light">
									<thead>
										<tr class="align-center">
											<th width="10%">彩种</th>
											<th width="10%">单期奖金限额(全部用户)</th>
											<th width="10%">单期奖金限额(单个用户)</th>
											<th width="10%" >操作</th>
										</tr>
									</thead>
									<tbody></tbody>
								</table>
							</div>
							<div class="page-list"></div>
						</div>
					</div>
					<!-- END PORTLET-->
				</div>
			</div>
			<!-- END PAGE CONTENT-->
			
			<div id="table-lottery-user-bets-limit-list" class="row">
				<div class="col-md-12">
					<!-- BEGIN PORTLET-->
					<div class="portlet light" style="margin-bottom: 10px;">
						<div class="portlet-body">
							<div class="table-toolbar">
								<div class="form-inline">
									<div class="row">
										<div class="col-md-12">
											<div class="btn-group">
												<button data-command="add" class="btn green">
													<i class="fa fa-plus"></i> 用户单期奖金限额
												</button>
											</div>
										</div>
									</div>
								</div>
							</div>
							
						
						<div class="portlet-body">
							<div class="table-toolbar">
								<form action="javascript:;" class="form-inline">
									<div class="row">
										<div class="col-md-12">
											<div class="form-group">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg fixed">用户名</span>
													<input id="queryUsername" name="username" class="form-control" type="text">
												</div>
											</div>
											<div class="form-group">
												<a data-command="search" href="javascript:;" class="btn green-meadow"><i class="fa fa-search"></i> 搜索记录</a>
											</div>
										</div>
									</div>
								</form>
							</div>
							
							<div class="table-scrollable table-scrollable-borderless">
								<table class="table table-hover table-light">
									<thead>
										<tr class="align-center">
											<th width="10%">用户名</th>
											<th width="10%">彩种</th>
											<th width="10%">单期奖金限额</th>
											<th width="10%" >操作</th>
										</tr>
									</thead>
									<tbody></tbody>
								</table>
							</div>
							<div class="page-list"></div>
						</div>
					</div>
				</div>
			</div>
			
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
<script src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-user-bets-limit.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
jQuery(document).ready(function() {
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	// init data
	LotteryUserBetsLimit.init();
});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>