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
	<title>业务查询 - 契约分红列表</title>
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
	<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-toastr/toastr.min.css" rel="stylesheet" type="text/css"/>
	<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-select/bootstrap-select.min.css" rel="stylesheet" type="text/css"/>
	<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/css/datepicker3.css" rel="stylesheet" type="text/css"/>
	<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css"/>

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
			<h3 class="page-title">契约分红列表</h3>
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>当前位置：业务查询<i class="fa fa-angle-right"></i></li><li>契约分红列表</li>
				</ul>
			</div>
			<!-- BEGIN PAGE CONTENT-->

			<div id="modal-modify" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog" style="width: 900px;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">编辑契约分红</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body" id = "editView">
									<div class="alert alert-warning">
										<h5>
										1.销量单位：万；亏损单位：万；分红比例：%（10=10%）；</br>
										2.允许同级签订。</br>
										3.分红比例必须逐级递增，销量不能低于亏损值</h5>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">用户</label>
										<div class="col-md-9">
											<input name="id" type="hidden">
											<p class="form-control-static" data-field="userLevels"></p>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">上级条款</label>
										<div class="col-md-9">
											<p class="form-control-static" data-field="upScale"></p>
										</div>
									</div>
									<!-- <div class="form-group">
										<label class="col-md-3 control-label">有效人数</label>
										<div class="col-md-9">
											<input name="minValidUser" class="form-control input-inline input-medium" type="number" autocomplete="off" step="1" min="0" max="1000">
											<span class="help-inline"></span>
										</div>
									</div> -->
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

			<div id="modal-add" class="modal fade" data-backdrop="static" tabindex="-1" >
				<div class="modal-dialog"  style="width: 900px;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">新增契约分红</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body" id = "addView">
								<div class="alert alert-warning">
										<h5>
										1.销量单位：万；亏损单位：万；分红比例：%（10=10%）。</br>
										2.上下级允许相同条款。</br>
										3.签署规则：销量、亏损大于等于上级某条款，并且分红比例小于等于上级某条款。</br>
										</h5>
									</div>
									<div class="form-group">
										<div class="col-md-1"></div>
										<label class="col-md-2 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" type="text" autocomplete="off">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<div class="col-md-1"></div>
										<label class="col-md-2 control-label">用户层级</label>
										<div class="col-md-9">
											<p class="form-control-static" data-field="userLevels"><span class="static-placeholder">输入用户名后显示</span></p>
										</div>
									</div>
									<div class="form-group">
										<div class="col-md-1"></div>
										<label class="col-md-2 control-label">上级条款</label>
										<div class="col-md-9">
											<p class="form-control-static" data-field="upScale"><span class="static-placeholder">输入用户名后显示</span></p>
										</div>
									</div>
								<div class="form-group">
										<!-- <label class="col-md-3 control-label">周期最小人数</label>
										<div class="col-md-3">
											<input name="minValidUser" class="form-control " maxLength="2" type="number" autocomplete="off" step="1" min="0" max="99"  value="1">
											<span class="help-inline"></span>
										</div> -->
										<div class="col-md-1"></div>
										<label class="col-md-2 control-label">状态</label>
										<div class="col-md-9">
											<select name="status" class="form-control input-inline input-medium" data-size="2">
												<option value="1">生效</option>
												<option value="2">待同意</option>
											</select>
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

			<div id="table-dividend-list" class="row">
				<div class="col-md-12">
					<!-- BEGIN PORTLET-->
					<div class="portlet light" style="margin-bottom: 10px;">
						<div class="portlet-body">
							<div class="table-toolbar">
								<div class="form-inline">
									<div class="row">
										<div class="col-md-12">
											<div class="form-group pull-right">
												<button data-command="add" class="btn green">
													<i class="fa fa-plus"></i> 新增契约分红
												</button>
												<%--<button data-command="add" class="btn green">--%>
												<%--<i class="fa fa-gears"></i> 修复契约分红--%>
												<%--</button>--%>
											</div>
										</div>
									</div>

									<div class="row">
										<div class="col-md-12">
											<div class="form-group ">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg fixed">用户名</span>
													<input name="username" class="form-control" type="text">
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg fixed">状态</span>
													<select name="status" class="form-control" data-size="6">
														<option value="">全部</option>
														<option value="1">已生效</option>
														<option value="2">待同意</option>
														<option value="3">已过期</option>
														<option value="4">无效</option>
														<option value="5">已拒绝</option>
													</select>
												</div>
											</div>
											<div class="form-group">
												<div data-field="time" class="input-group input-large date-picker input-daterange">
													<span class="input-group-addon no-bg fixed">生效日期筛选</span>
													<input type="text" class="form-control" name="sTime" />
													<span class="input-group-addon">至</span>
													<input type="text" class="form-control" name="eTime" />
												</div>
											</div>
											<div class="form-group">
												<a data-command="search" href="javascript:;" class="btn green-meadow"><i class="fa fa-search"></i> 搜索信息</a>
												<label><input name="advanced" type="checkbox"> 高级搜索</label>
											</div>
										</div>
									</div>
									<div class="row" data-hide="advanced" style="padding-top: 3px;">
										<div class="col-md-12">
											<div class="form-group">
												<div class="input-group input-range input-medium">
													<span class="input-group-addon no-bg">比例筛选</span>
													<input class="form-control from" name="minScale" type="text">
													<span class="input-group-addon symbol">~</span>
													<input class="form-control to" name="maxScale" type="text">
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-range input-medium">
													<span class="input-group-addon no-bg">最低有效会员筛选</span>
													<input class="form-control from" name="minValidUser" type="text">
													<span class="input-group-addon symbol">~</span>
													<input class="form-control to" name="maxValidUser" type="text">
												</div>
											</div>
											<div class="form-group">
												<label><input name="fixed" type="checkbox"> 浮动比例</label>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="table-scrollable table-scrollable-borderless">
								<table class="table table-hover table-light">
									<thead>
									<tr class="align-center">
										<th width="5%">ID</th>
										<th width="8%">用户名</th>
										<th width="6%">结算类型</th>
										<th width="34%">契约分红条款</th>
<!-- 										<th width="6%">最低有效会员</th> -->
										<th width="10%">累计分红金额</th>
										<th width="10%">创建时间</th>
										<th width="10%">生效时间</th>
										<th width="5%">状态</th>
										<th width="8%">操作</th>
									</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
							<div class="page-list"></div>
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
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-toastr/toastr.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-select/bootstrap-select.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/locales/bootstrap-datepicker.zh-CN.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/moment.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-user-dividend.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
	jQuery(document).ready(function() {
		Metronic.init(); // init metronic core components
		Layout.init(); // init current layout
		// init data
		LotteryDividend.init();
	});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>