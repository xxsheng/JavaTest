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
	<title>财务管理 - 提现处理</title>
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
			<h3 class="page-title">提现处理</h3>
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>当前位置：财务管理<i class="fa fa-angle-right"></i></li><li>提现处理</li>
				</ul>
			</div>
			<!-- END PAGE HEADER-->
			<div id="modal-user-withdraw-details" class="modal fade bs-modal-lg" tabindex="-1">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<div class="modal-body" style="padding: 0 20px 0 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<h3 class="form-section">取款信息</h3>
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">id:</label>
												<div class="col-md-8">
													<p data-field="id" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<!--/span-->
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">流水号:</label>
												<div class="col-md-8">
													<p data-field="billno" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<!--/span-->
									</div>
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">用户名:</label>
												<div class="col-md-8">
													<p data-field="username" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<!--/span-->
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">取款金额:</label>
												<div class="col-md-8">
													<p data-field="money" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<!--/span-->
									</div>
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">到账金额:</label>
												<div class="col-md-8">
													<p data-field="recMoney" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<!--/span-->
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">手续费:</label>
												<div class="col-md-8">
													<p data-field="feeMoney" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<!--/span-->
									</div>
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">申请时间:</label>
												<div class="col-md-8">
													<p data-field="time" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<!--/span-->
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">订单状态:</label>
												<div class="col-md-8">
													<p data-field="status" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<!--/span-->
									</div>
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">取现银行/支行:</label>
												<div class="col-md-8">
													<p data-field="bankInfos" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<!--/span-->
										<div class="col-md-6">
											<div class="form-group">
												<label class="control-label col-md-4">姓名/账号:</label>
												<div class="col-md-8">
													<p data-field="cardInfos" class="form-control-static"></p>
												</div>
											</div>
										</div>
										<!--/span-->
									</div>
									<section data-hidden="pay">
										<h3 class="form-section" style="margin-top: 0;">付款信息</h3>
										<div class="row">
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4">支付单号:</label>
													<div class="col-md-8">
														<p data-field="payBillno" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<!--/span-->
											<div class="col-md-6">
												<div class="form-group">
													<label class="control-label col-md-4">操作人/支付时间:</label>
													<div class="col-md-8">
														<p data-field="operatorInfos" class="form-control-static"></p>
													</div>
												</div>
											</div>
											<!--/span-->
										</div>
									</section>
									<div class="row">
										<div class="col-md-12">
											<div class="form-group">
												<label class="control-label col-md-2">订单备注:</label>
												<div class="col-md-10">
													<p data-field="remarks" class="form-control-static"></p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
				<div id="user_withdraw_logs" class="portlet light">
					<div class="portlet-title">
						<div class="caption">
							<span class="caption-subject font-green-sharp bold uppercase">日志</span>
						</div>
					</div>
					<div class="portlet-body">
						<div class="table-scrollable table-scrollable-borderless">
							<table class="table table-hover table-light">
								<thead>
									<tr class="align-center">
<!-- 										<th width="20%">操作人</th> -->
										<th width="65%">操作内容</th>
										<th width="35%">操作时间</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						</div>
						<div class="page-list"></div>
					</div>
				</div>
						<div class="modal-footer">
							<button type="button" data-dismiss="modal" class="btn btn-danger"><i class="fa fa-undo"></i> 返回列表</button>
						</div>
					</div>
				</div>
		</div>
			<div id="modal-user-withdraw-pay" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog" style="width: 1024px;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">提现支付</h4>
						</div>
						<div class="modal-body" style="padding: 10px 20px 15px 20px;">
							<form class="form-horizontal">
								<table class="simple">
									<tbody>
									<tr>
										<td class="text">用户名：</td>
										<td data-field="username"></td>
										<td class="text">提现金额</td>
										<td data-field="money"></td>
									</tr>
									<tr>
										<td class="text">持卡人：</td>
										<td data-field="cardName"></td>
										<td class="text">手续费</td>
										<td data-field="feeMoney"></td>
									</tr>
									<tr>
										<td class="text">开户行：</td>
										<td data-field="bankName"></td>
										<td class="text">申请时间</td>
										<td data-field="time"></td>
									</tr>

									<tr>
										<td class="text">卡号：</td>
										<td data-field="cardId"></td>
										<td class="text"></td>
										<td></td>
									</tr>
									<tr>
										<td class="text">到账金额：</td>
										<td data-field="recMoney"></td>
										<td class="text"></td>
										<td></td>
									</tr>
									<tr>
										<td class="text">订单流水号：</td>
										<td data-field="billno"></td>
										<td class="text"></td>
										<td></td>
									</tr>
									<tr>
										<td class="text">处理状态：</td>
										<td data-field="status"></td>
										<td class="text">支付流水号：</td>
										<td>
											<input name="payBillno" class="form-control" placeholder="" type="text">
										</td>
									</tr>
									<tr>
										<td class="text">锁定状态：</td>
										<td data-field="lockStatus"></td>
										<td class="text">拒绝理由：</td>
										<td>
											<select name="reason" class="form-control">
												<option value="">请选择理由或在下面备注</option>
												<option>收款人账号与姓名不匹配</option>
												<option>取款资料填写不正确</option>
												<option>汇款方式已达限额</option>
												<option>提现银行正在维护</option>
											</select>
										</td>
									</tr>
									<tr>
										<td class="text">审核状态：</td>
										<td data-field="checkStatus"></td>
										<td class="text">操作备注：</td>
										<td>
											<input name="remarks" class="form-control" placeholder="" type="text">
										</td>
									</tr>
									</tbody>
								</table>
							</form>
						</div>
						<div class="modal-footer">
				<!-- 	   <button type="button" data-command="zsPay" class="btn green" style="width: 110px;float: left;"><i class="fa fa-check"></i>泽圣代付</button>
						<button type="button" data-command="afPay" class="btn green" style="width: 110px;float: left;"><i class="fa fa-check"></i>艾付代付</button> -->
						<!-- 	<button type="button" data-command="zsPay" class="btn green" style="width: 110px;float: left;"><i class="fa fa-check"></i>泽圣代付</button> -->
							<!-- <button type="button" data-command="cfPay" class="btn green" style="margin-left: 30px;width: 110px;float: left;"><i class="fa fa-check"></i>代付</button> -->
							<%--<button type="button" data-command="rxPay" class="btn green" style="margin-left: 30px;width: 130px;float: left;"><i class="fa fa-check"></i>荣讯代付</button>--%>
							<%--<br/><br/>--%>
							<!-- <button type="button" data-command="htPay" class="btn green" style="margin-left: 30px;width: 110px;float: left;"><i class="fa fa-check"></i>汇通代付</button>
							<button type="button" data-command="fktPay" class="btn green" style="margin-left: 30px;width: 110px;float: left;"><i class="fa fa-check"></i>福卡通代付</button>
							<button type="button" data-command="htfPay" class="btn green" style="margin-left: 30px;width: 110px;float: left;"><i class="fa fa-check"></i>汇天付代付</button> -->
							<button type="button" data-command="tgfPay" class="btn green" style="width: 110px;float: left;"><i class="fa fa-check"></i>天机付代付</button>
							<button type="button" data-command="confirm" class="btn blue" style="margin-right: 30px;width: 110px;"><i class="fa fa-check"></i>手动出款</button>
							<button type="button" data-command="refuse" class="btn red" style="margin-right: 5px;width: 110px;"><i class="fa fa-ban"></i>拒绝</button>
						</div>
					</div>
				</div>
			</div>
			
			
			<div id="modal-user-account-pay" class="modal fade" data-backdrop="static" tabindex="-1">
			<div class="modal-dialog" style="width: 500px;">
			<div class="modal-content">
				<div class="modal-header">
					<input type="hidden" id="id" value="">
					<button type="button" class="close" data-dismiss="modal"></button>
					<h4 class="modal-title">确认该笔打款已经处理失败？</h4>相应金额将返回至用户彩票账户中！
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
			<!-- BEGIN PAGE CONTENT-->
			<div id="table-user-withdraw-list" class="row">
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
												<div class="input-group">
													<span class="input-group-addon no-bg fixed">订单状态</span>
													<select name="status" class="form-control" data-size="8">
														<option value="">全部状态</option>
														<option selected="selected" value="0">待处理</option>
														<option value="1">已完成</option>
														<option value="-1">拒绝支付</option>
													</select>
												</div>
											</div>
											<div class="form-group">
												<a data-command="search" href="javascript:;" class="btn green-meadow"><i class="fa fa-search"></i> 搜索信息</a>
												<label><input name="advanced" type="checkbox"> 高级搜索 </label>
												<label><input name="autoRefresh" type="checkbox"> 自动刷新</label>
											</div>
										</div>
									</div>
									<div class="row" style="padding-top: 3px;">
										<div class="col-md-12">
											<div class="form-group">
												<div data-field="time" class="input-group input-large date-picker input-daterange">
													<span class="input-group-addon no-bg fixed">申请日期</span>
													<input type="text" class="form-control" name="from" />
													<span class="input-group-addon">至</span>
													<input type="text" class="form-control" name="to" />
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-range input-medium">
													<span class="input-group-addon no-bg fixed">提现金额</span>
													<input class="form-control from" name="minMoney" type="text">
													<span class="input-group-addon symbol">~</span>
													<input class="form-control to" name="maxMoney" type="text">
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg fixed">提现账号</span>
													<input name="keyword" class="form-control" placeholder="姓名或卡号" type="text">
												</div>
											</div>
										</div>
									</div>
									<div class="row" data-hide="advanced" style="display:none; padding-top: 3px;">
										<div class="col-md-12">
											<div class="form-group">
												<div data-field="operatorTime" class="input-group input-large date-picker input-daterange">
													<span class="input-group-addon no-bg fixed">支付日期</span>
													<input type="text" class="form-control" name="from" />
													<span class="input-group-addon">至</span>
													<input type="text" class="form-control" name="to" />
												</div>
											</div>
											<div class="form-group">
												<div class="input-group">
													<span class="input-group-addon no-bg fixed">支付通道</span>
													<select name="paymentChannelId" class="form-control">
													</select>
												</div>
											</div>
											<div class="form-group">
												<div class="input-group">
													<span class="input-group-addon no-bg fixed">审核状态</span>
													<select name="checkStatus" class="form-control">
														<option value="">全部状态</option>
														<option value="0">待审核</option>
														<option value="1">已通过</option>
														<option value="-1">未通过</option>
													</select>
												</div>
											</div>
											<div class="form-group">
												<div class="input-group">
													<span class="input-group-addon no-bg fixed">打款状态</span>
													<select name="remitStatus" class="form-control">
														<option value="">全部</option>
														<option value="0">未处理</option>
														<option value="1">银行处理中</option>
														<option value="2">打款完成</option>
														<option value="3">第三方待处理</option>
														<option value="-1">请求失败</option>
														<option value="-2">打款失败</option>
														<option value="-3">查询状态中</option>
														<option value="-4">未知状态</option>
														<option value="-5">第三方处理失败</option>
														<option value="-6">银行处理失败</option>
													</select>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="table-toolbar" style="margin:0px;">
								<p class="input-group-addon no-bg fixed">总提现金额：</p>
								<p class="input-group-addon no-bg fixed" id="sumRecMoney">0</p>
								<p class="input-group-addon no-bg fixed">总手续费：</p>
								<p class="input-group-addon no-bg fixed" id="sumFeeMoney">0</p>
							</div>
							<div class="table-scrollable table-scrollable-borderless">
								<table class="table table-hover table-light">
									<thead>
									<tr class="align-center">
										<th width="11%">用户名</th>
										<th width="5%">金额</th>
										<th width="7%">余额</th>
										<th width="15%">银行卡号</th>
										<th width="13%">申请时间</th>
										<th width="6%">订单状态</th>
										<th width="10%">锁定状态</th>
										<th width="5%">审核状态</th>
										<th width="8%">打款状态</th>
										<th class="four" width="20%">操作</th>
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
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-select/bootstrap-select.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-toastr/toastr.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/locales/bootstrap-datepicker.zh-CN.js" type="text/javascript"></script>
<%--<script src="${cdnDomain}theme/assets/custom/plugins/zeroclipboard-master/dist/ZeroClipboard.js" type="text/javascript" ></script>--%>
<script src="${cdnDomain}theme/assets/global/plugins/clipboard/dist/clipboard.min.js"></script>

<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/moment.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/plugins/tippy/tippy.min.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-user-withdraw.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
	var LoginUser = '${LoginUser}';
	jQuery(document).ready(function() {
		Metronic.init(); // init metronic core components
		Layout.init(); // init current layout
		// init data
		LotteryUserWithdraw.init();
	});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>