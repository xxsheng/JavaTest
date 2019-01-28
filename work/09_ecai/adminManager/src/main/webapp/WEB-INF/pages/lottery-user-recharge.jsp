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
<title>财务管理 - 充值查询</title>
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
			<h3 class="page-title">充值查询</h3>
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>当前位置：财务管理<i class="fa fa-angle-right"></i></li><li>充值查询</li>
				</ul>
			</div>
			<!-- END PAGE HEADER-->
			<div id="modal-user-recharge-details" class="modal fade bs-modal-lg" tabindex="-1">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<div class="modal-body" style="padding: 0 20px 0 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<h3 class="form-section">充值信息</h3>
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">id：</label>
												<div class="col-md-8">
													<p data-field="id" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">流水号：</label>
												<div class="col-md-8">
													<p data-field="billno" class="form-control-static"></p>
												</div>
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">用户名：</label>
												<div class="col-md-8">
													<p data-field="username" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">充值金额：</label>
												<div class="col-md-8">
													<p data-field="money" class="form-control-static"></p>
												</div>
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">下单时间：</label>
												<div class="col-md-8">
													<p data-field="time" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">订单状态：</label>
												<div class="col-md-8">
													<p data-field="status" class="form-control-static"></p>
												</div>
											</div>
										</div>
									</div>
									<section data-hidden="pay">
										<h3 class="form-section" style="margin-top: 0;">支付信息</h3>
										<div class="row">
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4">支付方式：</label>
													<div class="col-md-8">
														<p data-field="channelName" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4">支付单号：</label>
													<div class="col-md-8">
														<p data-field="payBillno" class="form-control-static"></p>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4">到账时间：</label>
													<div class="col-md-8">
														<p data-field="payTime" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4">支付说明：</label>
													<div class="col-md-8">
														<p data-field="infos" class="form-control-static"></p>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4">转款人：</label>
													<div class="col-md-8">
														<p data-field="realName" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4">收款卡：</label>
													<div class="col-md-8">
														<p data-field="cardId" class="form-control-static"></p>
													</div>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4">附言：</label>
													<div class="col-md-8">
														<p data-field="postscript" class="form-control-static"></p>
													</div>
												</div>
											</div>
										</div>
									</section>
									<div class="row">
										<div class="col-md-12">
											<div class="form-group">
												<label class="control-label col-md-2">订单说明：</label>
												<div class="col-md-10">
													<p data-field="remarks" class="form-control-static"></p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
						<div class="modal-footer">
							<button type="button" data-dismiss="modal" class="btn btn-danger"><i class="fa fa-undo"></i> 返回列表</button>
						</div>
					</div>
				</div>
			</div>
			<div id="modal-patch-order" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">补单</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" type="text" disabled readonly>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">下单时间</label>
										<div class="col-md-9">
											<input name="time" class="form-control input-inline input-medium" type="text" disabled readonly>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">订单号</label>
										<div class="col-md-9">
											<input name="billno" class="form-control input-inline input-medium" type="text" disabled="disabled" >
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">支付单号</label>
										<div class="col-md-9">
											<input name="paybillno" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">备注说明</label>
										<div class="col-md-9">
											<input name="remarks" class="form-control input-inline input-medium" type="text">
											<span class="help-inline">请输入备注说明。</span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">资金密码</label>
										<div class="col-md-9">
											<input name="withdrawPwd" class="form-control input-inline input-medium" type="password">
											<span class="help-inline">请输入资金密码。</span>
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
			<div id="table-user-recharge-list" class="row">
				<div class="col-md-12">
					<!-- BEGIN PORTLET-->
					<div class="portlet light" style="margin-bottom: 10px;">
						<div class="portlet-body">
							<div class="table-toolbar">
								<div class="form-inline">
									<div class="row">
										<div class="col-md-12">
											<div class="form-group">
												<div class="input-group input-large">
													<span class="input-group-addon no-bg fixed">订单单号</span>
													<input name="billno" class="form-control" type="text">
												</div>
											</div>
											<div class="form-group ">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg fixed">用户名</span>
													<input name="username" class="form-control" type="text">
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg fixed">账号类型</span>
													<select name="type" class="form-control">
														<option value="">全部</option>
														<option value="1">代理</option>
														<option value="2">玩家</option>
														<option value="4">虚拟用户</option>
													</select>
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg fixed">订单状态</span>
													<select name="status" class="form-control" data-size="8">
														<option value="">全部状态</option>
														<option value="0">未支付</option>
														<option value="1" selected="selected">已完成</option>
														<option value="-1">已撤销</option>
													</select>
												</div>
											</div>
											<div class="form-group">
												<a data-command="search" href="javascript:;" class="btn green-meadow"><i class="fa fa-search"></i> 搜索信息</a>
												<label><input name="unpay" type="checkbox"> 未支付 </label>
											</div>
										</div>
									</div>
									<div class="row" style="padding-top: 3px;">
										<div class="col-md-12">
											<div class="form-group">
												<div data-field="time" class="input-group input-large date-picker input-daterange">
													<span class="input-group-addon no-bg fixed">下单日期</span>
													<input type="text" class="form-control" name="from" />
													<span class="input-group-addon">至</span>
													<input type="text" class="form-control" name="to" />
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-range input-medium">
													<span class="input-group-addon no-bg fixed">充值金额</span>
													<input class="form-control from" name="minMoney" type="text">
													<span class="input-group-addon symbol">~</span>
													<input class="form-control to" name="maxMoney" type="text">
												</div>
											</div>
											<%--<div class="form-group">--%>
												<%--<div class="input-group input-medium">--%>
													<%--<span class="input-group-addon no-bg fixed">收款信息</span>--%>
													<%--<input class="form-control" placeholder="收款单号、卡号、账号" type="text">--%>
												<%--</div>--%>
											<%--</div>--%>
										</div>
									</div>
									<div class="row" style="padding-top: 3px;">
										<div class="col-md-12">
											<div class="form-group">
												<div data-field="payTime" class="input-group input-large date-picker input-daterange">
													<span class="input-group-addon no-bg fixed">到账日期</span>
													<input type="text" class="form-control" name="from" />
													<span class="input-group-addon">至</span>
													<input type="text" class="form-control" name="to" />
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg fixed">支付类型</span>
													<select name="channelId" class="form-control">
													</select>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="table-toolbar" style="margin:0px;">
								<p class="input-group-addon no-bg fixed">总充值金额：</p>
								<p class="input-group-addon no-bg fixed" id="totalRecharge">0</p>
							</div>
							<div class="table-scrollable table-scrollable-borderless" style="margin:0px 0 !important">
								<table class="table table-hover table-light" style="width: 100%">
									<thead>
										<tr class="align-center">
											<th width="5%">ID</th>
											<th width="10%">用户名</th>
											<th width="7%">金额</th>
											<th width="10%">支付类型</th>
											<th width="10%">下单时间</th>
											<th width="10%">到账时间</th>
											<th width="5%">状态</th>
											<th width="10%">订单号</th>
											<th width="10%">订单说明</th>
											<th width="5%">转款人</th>
											<th width="7%">附言</th>
											<th width="8%" class="three">操作</th>
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
<script src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-select/bootstrap-select.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/locales/bootstrap-datepicker.zh-CN.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/moment.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-user-recharge.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
jQuery(document).ready(function() {
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	// init data
	LotteryUserRecharge.init();
});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>