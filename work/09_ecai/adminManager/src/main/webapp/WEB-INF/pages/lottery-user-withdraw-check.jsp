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
<title>财务管理 - 提现可疑查询</title>
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
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/css/datepicker3.css" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css"/>
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
			<h3 class="page-title">提现可疑查询</h3>
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>当前位置：财务管理<i class="fa fa-angle-right"></i></li><li>提现可疑查询</li>
				</ul>
			</div>
			<!-- END PAGE HEADER-->
			<!-- BEGIN PAGE CONTENT-->

			<ul class="nav nav-tabs">
				<li class="active"><a href="javascript:;" data-target="#tab1" data-toggle="tab">提现详情</a></li>
				<li><a href="javascript:;" data-target="#tab2" data-toggle="tab">基础信息</a></li>
				<li><a href="javascript:;" data-target="#tab3" data-toggle="tab">活动记录</a></li>
				<li><a href="javascript:;" data-target="#tab4" data-toggle="tab">银行卡列表</a></li>
				<li><a href="javascript:;" data-target="#tab5" data-toggle="tab">最近提现记录</a></li>
				<li><a href="javascript:;" data-target="#tab6" data-toggle="tab">最近充值记录</a></li>
				<li><a href="javascript:;" data-target="#tab7" data-toggle="tab">大于5倍本金中奖</a></li>

				<li class="pull-right">
					<div class="button-list">
						<button type="button" data-command="cancel" class="btn default"><i class="fa fa-undo inline-block"></i> 返回列表</button>
						<button type="button" data-command="check" data-value="1" class="btn green hide inline-block"><i class="fa fa-check"></i> 审核通过</button>
						<button type="button" data-command="check" data-value="-1" class="btn red hide inline-block"><i class="fa fa-ban"></i> 审核不通过</button>
					</div>
				</li>
			</ul>


			<div class="tab-content">
				<div class="tab-pane active" id="tab1">
					<div id="table-withdraw-details" class="row">
						<div class="col-md-12">
							<!-- BEGIN PORTLET-->
							<div class="portlet light" style="margin-bottom: 10px;">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">提现详情</span>
									</div>
								</div>
								<div class="portlet-body">
									<table class="simple">
										<tbody>
										<tr>
											<td class="text">用户名：</td>
											<td data-field="username"></td>
											<td class="text"></td>
											<td></td>
										</tr>
										<tr>
											<td class="text">持卡人：</td>
											<td data-field="cardName"></td>
											<td class="text">到账金额：</td>
											<td data-field="recMoney"></td>
										</tr>
										<tr>
											<td class="text">开户行：</td>
											<td data-field="bankName"></td>
											<td class="text">提现金额：</td>
											<td data-field="money"></td>
										</tr>
										<tr>
											<td class="text">卡号：</td>
											<td data-field="cardId"></td>
											<td class="text">手续费：</td>
											<td data-field="feeMoney"></td>
										</tr>
										<tr>
											<td class="text">申请时间：</td>
											<td data-field="time"></td>
											<td class="text"></td>
											<td></td>
										</tr>
										<tr>
											<td class="text">处理状态：</td>
											<td data-field="status"></td>
											<td class="text"></td>
											<td></td>
										</tr>
										<tr>
											<td class="text">锁定状态：</td>
											<td data-field="lockStatus"></td>
											<td class="text"></td>
											<td></td>
										</tr>
										<tr>
											<td class="text">审核状态：</td>
											<td data-field="checkStatus"></td>
											<td class="text">支付流水号：</td>
											<td data-field="payBillno"></td>
										</tr>
										<tr>
											<td class="text">打款状态：</td>
											<td data-field="remitStatus"></td>
											<td class="text">操作备注：</td>
											<td data-field="remarks"></td>
										</tr>
										</tbody>
									</table>
									<div class="modal-footer" style="margin-top: 20px;">
										<div class="pull-left">
											<a data-command="bill" class="btn default"><i class="fa fa-file-text-o"></i> 查看账单</a>
											<a data-command="bets" class="btn default"><i class="fa fa-gamepad"></i> 查看投注</a>
											<a data-command="report" class="btn default"><i class="fa fa-list"></i> 查看报表</a>
											<a data-command="profile" class="btn default"><i class="fa fa-list"></i> 详细资料</a>
											<a data-command="recharge" class="btn default"><i class="fa fa-list"></i> 充值查询</a>
										</div>
										<%--<div class="pull-right">--%>
											<%--<button type="button" data-command="cancel" class="btn default"><i class="fa fa-undo"></i> 返回列表</button>--%>
											<%--<button type="button" data-command="check" data-value="1" class="btn green hide"><i class="fa fa-check"></i> 审核通过</button>--%>
											<%--<button type="button" data-command="check" data-value="-1" class="btn red hide"><i class="fa fa-ban"></i> 审核不通过</button>--%>
										<%--</div>--%>
									</div>
								</div>
							</div>
							<!-- END PORTLET-->
						</div>
					</div>
				</div>
				<div class="tab-pane" id="tab2">
					<div class="row">
						<div class="col-md-12">
							<div id="basic_information" class="portlet light">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">基础信息</span>
									</div>
								</div>
								<div class="portlet-body">
									<table class="simple">
										<tr>
											<td class="text">档案编号：</td>
											<td data-field="id"></td>
											<td class="text">用户名：</td>
											<td data-field="username"></td>
											<td class="text">昵称：</td>
											<td data-field="nickname"></td>
											<td class="text">账号类型：</td>
											<td data-field="type"></td>
										</tr>
										<tr>
											<td class="text">主账户：</td>
											<td data-field="totalMoney"></td>
											<td class="text">彩票账户：</td>
											<td data-field="lotteryMoney"></td>
											<td class="text">百家乐账户：</td>
											<td data-field="baccaratMoney"></td>
											<td class="text">冻结金额：</td>
											<td data-field="freezeMoney"></td>
										</tr>
										<tr>
											<td class="text">账户级别：</td>
											<td data-field="code"></td>
											<td class="text">定位返点：</td>
											<td data-field="locatePoint"></td>
											<td class="text">不定位返点：</td>
											<td data-field="notLocatePoint"></td>
											<td class="text">私返点数：</td>
											<td data-field="extraPoint"></td>
										</tr>
										<tr>
											<td class="text">下级会员：</td>
											<td data-field="lowerUsers"></td>
											<td class="text">上级代理：</td>
											<td data-field="levelUsers" colspan="5"></td>
										</tr>
										<tr>
											<td class="text">注册时间：</td>
											<td data-field="registTime"></td>
											<td class="text">登录时间：</td>
											<td data-field="loginTime"></td>
											<td class="text">账号状态：</td>
											<td data-field="AStatus"></td>
											<td class="text">投注权限：</td>
											<td data-field="BStatus"></td>
										</tr>
										<tr>
											<td class="text">状态备注：</td>
											<td data-field="message" colspan="7"></td>
										</tr>
										<tr>
											<td class="text">在线状态：</td>
											<td data-field="onlineStatus"></td>
											<td class="text">同级开号：</td>
											<td data-field="allowEqualCode"></td>
											<td class="text">上下级转账：</td>
											<td data-field="allowTransfers"></td>
											<td class="text">账户时间锁：</td>
											<td data-field="lockTime"></td>
										</tr>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="tab-pane" id="tab3">
					<div id="table-user-bill-list" class="row">
						<div class="col-md-12">
							<!-- BEGIN PORTLET-->
							<div class="portlet light" style="margin-bottom: 10px;">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">活动记录</span>
									</div>
								</div>
								<div class="portlet-body">
									<div class="table-scrollable table-scrollable-borderless">
										<table class="table table-hover table-light">
											<thead>
											<tr class="align-center">
												<th width="8%">ID</th>
												<th width="10%">用户名</th>
												<th width="10%">账户类型</th>
												<th width="10%">账单类型</th>
												<th width="10%">之前金额</th>
												<th width="10%">操作金额</th>
												<th width="10%">剩余金额</th>
												<th width="14%">生成时间</th>
												<th>说明</th>
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
				</div>
				<div class="tab-pane" id="tab4">
					<div id="table-user-card-list" class="row">
						<div class="col-md-12">
							<!-- BEGIN PORTLET-->
							<div class="portlet light" style="margin-bottom: 10px;">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">银行卡列表</span>
									</div>
								</div>
								<div class="portlet-body">
									<div class="table-scrollable table-scrollable-borderless">
										<table class="table table-hover table-light">
											<thead>
											<tr class="align-center">
												<th>用户名</th>
												<th>开户行</th>
												<th>姓名</th>
												<th>卡号</th>
												<th>卡片状态</th>
												<th>锁定时间</th>
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
				</div>
				<div class="tab-pane" id="tab5">
					<div id="table-user-withdraw-list" class="row">
						<div class="col-md-12">
							<!-- BEGIN PORTLET-->
							<div class="portlet light" style="margin-bottom: 10px;">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">最近提现记录</span>
									</div>
								</div>
								<div class="portlet-body">
									<div class="table-scrollable table-scrollable-borderless">
										<table class="table table-hover table-light">
											<thead>
											<tr class="align-center">
												<th width="16%">订单编号</th>
												<th width="10%">用户名</th>
												<th width="10%">之前金额</th>
												<th width="10%">提现金额</th>
												<th width="10%">之后金额</th>
												<th style="width: 200px;">银行卡资料</th>
												<th>申请时间</th>
												<th width="10%">订单状态</th>
												<th width="10%">审核状态</th>
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
				</div>
				<div class="tab-pane" id="tab6">
					<div id="table-user-recharge-list" class="row">
						<div class="col-md-12">
							<!-- BEGIN PORTLET-->
							<div class="portlet light" style="margin-bottom: 10px;">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">最近充值记录</span>
									</div>
								</div>
								<div class="portlet-body">
									<div class="table-scrollable table-scrollable-borderless">
										<table class="table table-hover table-light">
											<thead>
											<tr class="align-center">
												<th width="16%">订单编号</th>
												<th width="10%">用户名</th>
												<th width="10%">之前金额</th>
												<th width="10%">充值金额</th>
												<th width="10%">之后金额</th>
												<th>支付类型</th>
												<th>下单时间</th>
												<th width="10%">状态</th>
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
				</div>
				<div class="tab-pane" id="tab7">
					<div id="table-user-order-list" class="row">
						<div class="col-md-12">
							<!-- BEGIN PORTLET-->
							<div class="portlet light" style="margin-bottom: 10px;">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">大于5倍本金中奖</span>
									</div>
								</div>
								<div class="portlet-body">
									<div class="table-scrollable table-scrollable-borderless">
										<table class="table table-hover table-light">
											<thead>
											<tr class="align-center">
												<th>订单号</th>
												<th>用户名</th>
												<th>彩种</th>
												<th>期号</th>
												<th>玩法</th>
												<th>模式</th>
												<th>倍数</th>
												<th>投注金额</th>
												<th>奖金</th>
												<th>下单时间</th>
												<th>状态</th>
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
				</div>
			</div>
			<div id="modal-user-sh-Fail" class="modal fade" data-backdrop="static" tabindex="-1">
			<div class="modal-dialog" style="width: 500px;">
			<div class="modal-content">
				<div class="modal-header">
					<input type="hidden" id="id" value="">
					<button type="button" class="close" data-dismiss="modal"></button>
					<h4 class="modal-title">确认该笔提现审核不通过吗？</h4>相应金额将返回至用户彩票账户中！
				</div>
				<div class="modal-body" style="padding: 30px 20px 15px 20px;">
					<form action="javascript:;" class="form-horizontal" novalidate="novalidate">
						<div class="form-body">
							<div id="modal-bodySuccess" class="form-group has-success">
								<label class="col-md-3 control-label">操作备注：</label>
								<div class="col-md-9">
									<input name="message" class="form-control input-inline input-medium" type="text" aria-required="true" aria-invalid="false">
									<span class="help-inline"><i class="fa fa-check"></i> 填写正确。</span>
								</div>
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" data-dismiss="modal" class="btn default"><i class="fa fa-undo"></i> 取消</button>
					<button type="button" data-command="submit" class="btn green-meadow"><i class="fa fa-check"></i> 确认</button>
				</div>
				</div>
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
<script src="${cdnDomain}theme/assets/global/plugins/moment.min.js" type="text/javascript"></script>
<!-- END CORE PLUGINS -->
<!-- BEGIN PAGE LEVEL PLUGINS -->
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-select/bootstrap-select.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-toastr/toastr.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/locales/bootstrap-datepicker.zh-CN.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/plugins/zeroclipboard-master/dist/ZeroClipboard.js" type="text/javascript" ></script>

<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/plugins/tippy/tippy.min.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->

<script src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-user-withdraw-check.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
jQuery(document).ready(function() {
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	// init data
	LotteryUserWithdrawCheck.init();
});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>