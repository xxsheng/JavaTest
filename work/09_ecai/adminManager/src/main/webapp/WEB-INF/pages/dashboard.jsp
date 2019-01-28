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
<title>控制面板</title>
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
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css"/>
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
	<!-- BEGIN CONTENT -->
	<div class="page-content-wrapper">
		<div class="page-content">
			<!-- BEGIN PAGE HEADER-->
			<h3 class="page-title">控制面板</h3>
			<div class="page-bar">
				<div class="page-toolbar pull-left">
					<div id="dashboard-report-range" class="pull-right tooltips btn btn-fit-height grey-salt" data-placement="top" data-original-title="Change dashboard date range">
						<i class="icon-calendar"></i> 统计日期：<span class="thin uppercase visible-lg-inline-block">&nbsp;</span>&nbsp;<i class="fa fa-angle-down"></i> 
					</div>
				</div>
			</div>
			<!-- END PAGE HEADER-->
			<!-- BEGIN DASHBOARD STATS -->
			<div id="lottery-total-info" class="row">
				<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
					<div class="dashboard-stat dashboard-stat-light blue-soft">
						<div class="visual">
							<i class="fa fa-comments"></i>
						</div>
						<div class="details">
							<div data-field="totalUserRegist" class="number">
								0
							</div>
							<div class="desc">
								 注册用户
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
					<div class="dashboard-stat dashboard-stat-light red-soft">
						<div class="visual">
							<i class="fa fa-trophy"></i>
						</div>
						<div class="details">
							<div data-field="totalBetsMoney" class="number">
								¥ 0.000
							</div>
							<div class="desc">
								 投注总额
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
					<div class="dashboard-stat dashboard-stat-light green-soft">
						<div class="visual">
							<i class="fa fa-shopping-cart"></i>
						</div>
						<div class="details">
							<div data-field="totalOrderCount" class="number">
								0
							</div>
							<div class="desc">
								 订单数量
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
					<div class="dashboard-stat dashboard-stat-light purple-soft">
						<div class="visual">
							<i class="fa fa-globe"></i>
						</div>
						<div class="details">
							<div data-field="totalProfitMoney" class="number">
								¥ 0.000
							</div>
							<div class="desc">
								 盈亏金额
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- END DASHBOARD STATS -->
			<!-- BEGIN PAGE CONTENT-->
			<div class="row">
				<div class="col-md-6 col-sm-6">
					<!-- BEGIN PORTLET-->
					<div id="chart-user-regist" class="portlet light ">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-bar-chart-o font-green-sharp"></i>
								<span class="caption-subject font-green-sharp bold uppercase">用户注册</span>
								<span class="caption-helper">按天显示</span>
							</div>
						</div>
						<div class="portlet-body">
							<div class="chart"></div>
						</div>
					</div>
					<!-- END PORTLET-->
				</div>
				<div class="col-md-6 col-sm-6">
					<!-- BEGIN PORTLET-->
					<div id="chart-user-login" class="portlet light ">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-bar-chart-o font-red-sunglo"></i>
								<span class="caption-subject font-red-sunglo bold uppercase">登录用户</span>
								<span class="caption-helper">按天显示</span>
							</div>
						</div>
						<div class="portlet-body">
							<div class="chart"></div>
						</div>
					</div>
					<!-- END PORTLET-->
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<!-- BEGIN PORTLET-->
					<div id="chart-user-complex" class="portlet light ">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-bar-chart-o font-green-sharp"></i>
								<span class="caption-subject font-green-sharp bold uppercase">用户综合</span>
								<span class="caption-helper">按天显示</span>
							</div>
							<div class="actions">
								<div class="btn-group">
									<select name="lotteryType" class="form-control input-inline input-sm">
										<option value="">全部类型</option>
									</select>
									<select name="lottery" class="form-control input-inline input-sm">
										<option value="">全部彩票</option>
									</select>
								</div>
							</div>
						</div>
						<div class="portlet-body">
							<div class="chart"></div>
							<div>*消费为有效投注订单金额。（即不包含撤单金额）</div>
						</div>
					</div>
					<!-- END PORTLET-->
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 col-sm-6">
					<!-- BEGIN PORTLET-->
					<div id="chart-lottery-hot" class="portlet light ">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-bar-chart-o font-green-sharp"></i>
								<span class="caption-subject font-green-sharp bold uppercase">热门彩票类型</span>
							</div>
							<div class="actions">
								<div class="btn-group">
									<select name="lotteryType" class="form-control input-sm">
										<option value="">全部类型</option>
									</select>
								</div>
							</div>
						</div>
						<div class="portlet-body">
							<div class="chart"></div>
							<div>*热门彩票类型根据所选时间段内彩票的订单数量统计而成。</div>
						</div>
					</div>
					<!-- END PORTLET-->
				</div>
				<div class="col-md-6 col-sm-6">
					<!-- BEGIN PORTLET-->
					<div id="chart-user-bets" class="portlet light ">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-bar-chart-o font-red-sunglo"></i>
								<span class="caption-subject font-red-sunglo bold uppercase">订单量</span>
								<span class="caption-helper">按天显示</span>
							</div>
							<div class="actions">
								<div class="btn-group">
									<select name="lotteryType" class="form-control input-inline input-sm">
										<option value="">全部类型</option>
									</select>
									<select name="lottery" class="form-control input-inline input-sm">
										<option value="">全部彩票</option>
									</select>
								</div>
							</div>
						</div>
						<div class="portlet-body">
							<div class="chart"></div>
							<div>*只统计有效订单数量。（即不包含撤单的订单数）</div>
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
<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/moment.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/plugins/echarts/build/dist/echarts.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- END CORE PLUGINS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/dashboard.js?v=${cdnVersion}" type="text/javascript"></script>
<script type="text/javascript">
jQuery(document).ready(function() {
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	
	Dashboard.init();
});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>