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
<title>彩票管理 - 玩法列表</title>
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
<link href="${cdnDomain}theme/assets/custom/plugins/tippy/tippy.css" rel="stylesheet" type="text/css"/>
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
			<h3 class="page-title">玩法列表</h3>
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>当前位置：彩票管理<i class="fa fa-angle-right"></i></li><li>玩法列表</li>
				</ul>
			</div>
			<!-- END PAGE HEADER-->
			<div id="modal-lottery-play-rules-edit" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">编辑玩法</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<div class="alert alert-warning">
										<ul style="padding-left: 15px;">
											<li><strong>最小注/码：</strong> 该玩法最小投注数或单行最小码数，0则不限制，多个使用英文逗号分割。</li>
											<li><strong>最大注/码：</strong> 该玩法最大投注数或单行最小码数，0则不限制，多个使用英文逗号分割。</li>
										</ul>
									</div>
									<div class="alert alert-danger">
										<p><strong>请按照数据显示时的格式进行编辑，切勿随意修改数据格式，否则设置无效。</strong></p>
									</div>

									<input type="hidden" name="ruleId"/>
									<input type="hidden" name="lotteryId"/>
									<div class="form-group">
										<label class="col-md-3 control-label">名称</label>
										<div class="col-md-9">
											<p class="form-control-static" data-field="rulesName"></p>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">最小注/码</label>
										<div class="col-md-9">
											<input name="minNum" class="form-control input-inline input-medium" type="text" autocomplete="off">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">最大注/码</label>
										<div class="col-md-9">
											<input name="maxNum" class="form-control input-inline input-medium" type="text" autocomplete="off">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">总注数</label>
										<div class="col-md-9">
											<input name="totalNum" class="form-control input-inline input-medium" type="text" disabled>
										</div>
									</div>
								</div>
							</form>
						</div>
						<div class="modal-footer">
							<button type="button" data-command="submitAllLotteries" class="btn green-meadow"><i class="fa fa-check"></i> 修改所有彩种（...）</button>
							<button type="button" data-command="submitSingleLottery" class="btn green-meadow"><i class="fa fa-check"></i> 修改当前彩种（...）</button>
							<button type="button" data-dismiss="modal" class="btn default"><i class="fa fa-undo"></i> 取消</button>
						</div>
					</div>
				</div>
			</div>
			<!-- BEGIN PAGE CONTENT-->
			<div id="table-lottery-play-rules-list" class="row">
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
													<span class="input-group-addon no-bg fixed">彩票类型</span>
													<select name="lotteryType" class="bs-select form-control show-tick" data-size="8"></select>
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg fixed">彩票</span>
													<select name="lottery" class="bs-select form-control show-tick" data-live-search="true" data-size="8"></select>
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg fixed">玩法组</span>
													<select name="group" class="bs-select form-control show-tick" data-live-search="true" data-size="8"></select>
												</div>
											</div>
											<%--<div class="form-group pull-right">--%>
												<%--<button data-command="testPrize" class="btn green">--%>
													<%--<i class="fa fa-money"></i> 测试奖金--%>
												<%--</button>--%>
											<%--</div>--%>
										</div>
									</div>
								</div>
							</div>
							<div class="table-scrollable table-scrollable-borderless">
								<table class="table table-hover table-light">
									<thead>
										<tr class="align-center">
											<th>彩票名称</th>
											<th>玩法</th>
											<th style="width: 80px;">最小注/码 <i class="fa fa-question-circle cursor-pointer tippy" title="单注最小投注数或单行最小码数"></i></th>
											<th style="width: 80px;">最大注/码 <i class="fa fa-question-circle cursor-pointer tippy" title="单注最大投注数或单行最大码数"></i></th>
											<th style="width: 80px;">总注/码 <i class="fa fa-question-circle cursor-pointer tippy" title="玩法总注数或单行总码数"></i></th>
											<th style="width: 70px;">固定奖金</th>
											<th width="200px;">奖金配置 <i class="fa fa-question-circle cursor-pointer tippy" data-html="#prizeDesc"></i></th>
											<th style="width: 80px;">单挑注数 <i class="fa fa-question-circle cursor-pointer tippy" title="低于等于单挑注数，按照彩种单挑奖金限制进行派奖，同期同号多注单共享单挑奖金"></i></th>
											<th style="width: 70px;">定位 <i class="fa fa-question-circle cursor-pointer tippy" title="定位与不定位玩法代理返点按照不同基数进行计算"></i></th>
											<th style="width: 70px;">状态</th>
											<th class="two">操作</th>
										</tr>
									</thead>
									<tbody></tbody>
								</table>
							</div>
						</div>
					</div>
					<!-- END PORTLET-->
				</div>
			</div>

			<div id="prizeDesc" style="display: none;">
				<div style="text-align: left;">
					<h4>奖金计算说明：</h4>
					<p>账号等级 / 奖金配置 = 奖金</p>
					<p>如：1956 / 0.01 = 195600</p>
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
<script src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/additional-methods.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/plugins/tippy/tippy.min.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-play-rules.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
jQuery(document).ready(function() {
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	LotteryPlayRules.init();
});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>