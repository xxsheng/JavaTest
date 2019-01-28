<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
<meta charset="utf-8" />
<title>业务查询 - 历史投注查询</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="renderer" content="webkit">
<meta http-equiv="Content-type" content="text/html; charset=utf-8">
<meta content="" name="description" />
<meta content="" name="author" />
<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link
	href="${cdnDomain}theme/assets/global/plugins/font-awesome/css/font-awesome.min.css"
	rel="stylesheet" type="text/css">
<link
	href="${cdnDomain}theme/assets/global/plugins/simple-line-icons/simple-line-icons.min.css"
	rel="stylesheet" type="text/css">
<link
	href="${cdnDomain}theme/assets/global/plugins/bootstrap/css/bootstrap.min.css"
	rel="stylesheet" type="text/css">
<link
	href="${cdnDomain}theme/assets/global/plugins/uniform/css/uniform.default.css"
	rel="stylesheet" type="text/css">
<link
	href="${cdnDomain}theme/assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css"
	rel="stylesheet" type="text/css" />
<!-- END GLOBAL MANDATORY STYLES -->
<link
	href="${cdnDomain}theme/assets/global/plugins/bootstrap-select/bootstrap-select.min.css"
	rel="stylesheet" type="text/css" />
<link
	href="${cdnDomain}theme/assets/global/plugins/bootstrap-toastr/toastr.min.css"
	rel="stylesheet" type="text/css" />
<link
	href="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/css/datepicker3.css"
	rel="stylesheet" type="text/css" />
<link
	href="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css"
	rel="stylesheet" type="text/css" />

<link
	href="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.css"
	rel="stylesheet" type="text/css" />
<!-- BEGIN THEME STYLES -->
<link
	href="${cdnDomain}theme/assets/global/css/components.css?v=${cdnVersion}"
	rel="stylesheet" type="text/css" />
<link href="${cdnDomain}theme/assets/global/css/plugins.css"
	rel="stylesheet" type="text/css" />
<link href="${cdnDomain}theme/assets/admin/layout/css/layout.css"
	rel="stylesheet" type="text/css" />
<link
	href="${cdnDomain}theme/assets/admin/layout/css/themes/default.css"
	rel="stylesheet" type="text/css" />
<link
	href="${cdnDomain}theme/assets/admin/layout/css/custom.css?v=${cdnVersion}"
	rel="stylesheet" type="text/css" />
<!-- END THEME STYLES -->
<link rel="shortcut icon" href="favicon.ico" />
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
				<h3 class="page-title">历史投注查询</h3>
				<div class="page-bar">
					<ul class="page-breadcrumb">
						<li>当前位置：业务查询<i class="fa fa-angle-right"></i></li>
						<li>历史投注查询</li>
					</ul>
				</div>
				<!-- END PAGE HEADER-->
				<div id="modal-lottery-user-bets-details" class="modal fade"
					tabindex="-1">
					<div class="modal-dialog" style="width: 1024px;">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"></button>
								<h4 class="modal-title">投注信息</h4>
							</div>
							<div class="modal-body">
								<form class="form-horizontal">
									<div class="form-body">
										<div class="row">
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">用户名：</label>
													<div class="col-md-7">
														<p data-field="username" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">订单编号：</label>
													<div class="col-md-7">
														<p data-field="billno" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">状态：</label>
													<div class="col-md-7">
														<p data-field="status" class="form-control-static"></p>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">彩种：</label>
													<div class="col-md-7">
														<p data-field="lottery" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">期号：</label>
													<div class="col-md-7">
														<p data-field="expect" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">玩法：</label>
													<div class="col-md-7">
														<p data-field="mname" class="form-control-static"></p>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">注数：</label>
													<div class="col-md-7">
														<p data-field="nums" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">资金模式：</label>
													<div class="col-md-7">
														<p data-field="model" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">倍数：</label>
													<div class="col-md-7">
														<p data-field="multiple" class="form-control-static"></p>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">奖金模式：</label>
													<div class="col-md-7">
														<p data-field="prizeInfo" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">中奖停止追号：</label>
													<div class="col-md-7">
														<p data-field="chaseStop" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">下单时间：</label>
													<div class="col-md-7">
														<p data-field="time" class="form-control-static"></p>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">投注金额：</label>
													<div class="col-md-7">
														<p data-field="money" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">中奖金额：</label>
													<div class="col-md-7">
														<p data-field="prizeMoney" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">截止时间：</label>
													<div class="col-md-7">
														<p data-field="stopTime" class="form-control-static"></p>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-8">
												<div class="form-group">
													<label class="control-label col-md-2">开奖号码：</label>
													<div class="col-md-10">
														<p data-field="openCode" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label col-md-5">开奖时间：</label>
													<div class="col-md-7">
														<p data-field="prizeTime" class="form-control-static"></p>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-10">
												<div class="form-group">
													<label class="control-label col-md-2">投注内容：</label>
													<div class="col-md-10">
														<textarea name="codes" readonly="readonly"
															class="noresize"
															style="height: 150px; width: 700px; padding: 10px; display: none;"
															aria-autocomplete="none" autocomplete="off"></textarea>
													</div>
												</div>
											</div>
										</div>
									</div>
								</form>
							</div>
							<div class="modal-footer">
								<button type="button" data-dismiss="modal"
									class="btn btn-danger">
									<i class="fa fa-undo"></i> 返回列表
								</button>
							</div>
						</div>
					</div>
				</div>
				<!-- BEGIN PAGE CONTENT-->
				<div id="table-user-bets-list" class="row">
					<div class="col-md-12">
						<!-- BEGIN PORTLET-->
						<div class="portlet light" style="margin-bottom: 10px;">
							<div class="portlet-body">
								<div class="table-toolbar">
									<div class="form-inline">
										<div class="row">
											<div class="col-md-12">
												<div class="form-group">
													<div class="input-group input-medium">
														<span class="input-group-addon no-bg fixed">关键字</span> <input
															name="keyword" class="form-control" placeholder="id、订单号"
															type="text" autocomplete="off">
													</div>
												</div>
												<div class="form-group ">
													<div class="input-group input-medium">
														<span class="input-group-addon no-bg fixed">用户名</span> <input
															name="username" class="form-control" type="text"
															autocomplete="off">
													</div>
												</div>
												
												<div class="form-group">
													<div class="input-group input-medium">
														<span class="input-group-addon no-bg fixed">订单类型</span> <select
															name="type" class="bs-select form-control" data-size="8">
															<option value="">全部类型</option>
															<option value="0">普通订单</option>
															<option value="1">追号订单</option>
															<option value="2">计划订单</option>
														</select>
													</div>
												</div>
												<div class="form-group">
													<div class="input-group input-medium">
														<span class="input-group-addon no-bg fixed">订单状态</span> <select
															name="status" class="bs-select form-control"
															data-size="8">
															<option value="">全部订单</option>
															<option value="0">未开奖</option>
															<option value="1">未中奖</option>
															<option value="2">已中奖</option>
															<option value="-1">已撤单</option>
														</select>
													</div>
												</div>
												<div class="form-group">
													<a data-command="search" href="javascript:;"
														class="btn green-meadow"><i class="fa fa-search"></i>
														搜索信息</a> <label><input name="advanced" type="checkbox">
														高级搜索</label>
												</div>
											</div>
										</div>
										<div class="row" style="padding-top: 3px;">
											<div class="col-md-12">
												<div class="form-group">
													<div class="input-group input-medium">
														<span class="input-group-addon no-bg fixed">彩票</span> <select
															name="lottery" class="bs-select form-control show-tick"
															data-live-search="true" data-size="8">
															<option value="">全部类型</option>
														</select>
													</div>
												</div>
												<div class="form-group">
													<div class="input-group input-medium">
														<span class="input-group-addon no-bg fixed">玩法</span> <select
															name="rule" class="bs-select form-control show-tick"
															data-live-search="true" data-size="8" title="全部玩法">
															<option value="">全部玩法</option>
														</select>
													</div>
												</div>
												<div class="form-group">
													<div class="input-group input-range input-medium">
														<span class="input-group-addon no-bg">投注期号</span> <input
															name="expect" class="form-control" type="text"
															autocomplete="off">
													</div>
												</div>
												<div class="form-group">
													<div data-field="time"
														class="input-group input-large date-picker input-daterange">
														<span class="input-group-addon no-bg">下单日期</span> <input
															class="form-control" name="from" type="text"> <span
															class="input-group-addon">至</span> <input
															class="form-control" name="to" type="text">
													</div>
												</div>
											</div>
										</div>
										<div class="row" data-hide="advanced"
											style="display: none; padding-top: 3px;">
											<div class="col-md-12">
												<div class="form-group">
													<div class="input-group input-range input-medium">
														<span class="input-group-addon no-bg">投注金额</span> <input
															class="form-control from" name="minBetsMoney" type="text">
														<span class="input-group-addon symbol">~</span> <input
															class="form-control to" name="maxBetsMoney" type="text">
													</div>
												</div>
												<div class="form-group">
													<div class="input-group input-range input-medium">
														<span class="input-group-addon no-bg fixed">倍数</span> <input
															class="form-control from" name="minMultiple" type="text">
														<span class="input-group-addon symbol">~</span> <input
															class="form-control to" name="maxMultiple" type="text">
													</div>
												</div>
												<div class="form-group">
													<div class="input-group input-range input-medium">
														<span class="input-group-addon no-bg">中奖金额</span> <input
															class="form-control from" name="minPrizeMoney"
															type="text"> <span
															class="input-group-addon symbol">~</span> <input
															class="form-control to" name="maxPrizeMoney" type="text">
													</div>
												</div>
												<div class="form-group">
													<div data-field="prizeTime"
														class="input-group input-large date-picker input-daterange">
														<span class="input-group-addon no-bg">开奖日期</span> <input
															class="form-control" name="from" type="text"> <span
															class="input-group-addon">至</span> <input
															class="form-control" name="to" type="text">
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="table-toolbar" style="margin: 0px;">
									<p class="input-group-addon no-bg fixed">总投注金额：</p>
									<p class="input-group-addon no-bg fixed" id="totalMoney">0</p>
									<p class="input-group-addon no-bg fixed">总奖金：</p>
									<p class="input-group-addon no-bg fixed" id="totalPrizeMoney">0</p>
								</div>
								<div class="table-scrollable table-scrollable-borderless">
									<table class="table table-hover table-light">
										<thead>
											<tr class="align-center">
												<th width="9%">用户名</th>
												<th width="8%">彩种</th>
												<th width="8%">期号</th>
												<th width="12%">玩法</th>
												<th width="4%">模式</th>
												<th width="4%">倍数</th>
												<th width="7%">投注金额</th>
												<th width="7%">奖金</th>
												<th width="13%">下单时间</th>
												<th width="13%">开奖时间</th>
												<th width="5%">状态</th>
												<th class="two" width="9%">操作</th>
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
	<script src="${cdnDomain}theme/assets/global/plugins/jquery.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/jquery-migrate.min.js"
		type="text/javascript"></script>
	<!-- IMPORTANT! Load jquery-ui-1.10.3.custom.min.js before bootstrap.min.js to fix bootstrap tooltip conflict with jquery ui tooltip -->
	<script
		src="${cdnDomain}theme/assets/global/plugins/jquery-ui/jquery-ui-1.10.3.custom.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/bootstrap/js/bootstrap.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/jquery.blockui.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/jquery.cokie.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/uniform/jquery.uniform.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js"
		type="text/javascript"></script>
	<!-- END CORE PLUGINS -->
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<script
		src="${cdnDomain}theme/assets/global/plugins/bootstrap-select/bootstrap-select.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/bootstrap-toastr/toastr.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/locales/bootstrap-datepicker.zh-CN.js"
		type="text/javascript"></script>

	<script
		src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/moment.min.js"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js"
		type="text/javascript"></script>

	<script
		src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js"
		type="text/javascript"></script>
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<script src="${cdnDomain}theme/assets/global/scripts/metronic.js"
		type="text/javascript"></script>
	<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js"
		type="text/javascript"></script>
	<script src="${cdnDomain}theme/assets/global/plugins/lzma/lzma-min.js"
		type="text/javascript"></script>

	<script
		src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}"
		type="text/javascript"></script>
	<script
		src="${cdnDomain}theme/assets/custom/scripts/history-lottery-user-bets.js?v=${cdnVersion}"
		type="text/javascript"></script>
	<!-- END PAGE LEVEL SCRIPTS -->
	<script type="text/javascript">
		jQuery(document).ready(function() {
			Metronic.init(); // init metronic core components
			Layout.init(); // init current layout
			// init data
			LotteryUserBets.init();
		});
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>