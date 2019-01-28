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
<title>手机扫码转账</title>
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
<!-- END GLOBAL MANDATORY STYLES -->
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-select/bootstrap-select.min.css" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-toastr/toastr.min.css" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/css/datepicker3.css" rel="stylesheet" type="text/css"/>

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
			<h3 class="page-title">手机扫码转账</h3>
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>当前位置：站点维护<i class="fa fa-angle-right"></i></li><li>手机扫码转账</li>
				</ul>
			</div>
			<!-- END PAGE HEADER-->
			<div id="modal-lottery-payment-channel-add" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">新增账号</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">类别</label>
										<div class="col-md-9">
											<select name="type" class="form-control input-medium">
												<option data-type="2" data-subType="2" value="scanCodeWeChat">微信扫码转账</option>
												<option data-type="2" data-subType="4" value="scanCodeAlipay">支付宝扫码转账</option>
												<option data-type="2" data-subType="6" value="scanCodeQQ">QQ钱包扫码转账</option>
											</select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">后台名称</label>
										<div class="col-md-9">
											<input name="name" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">前台名称</label>
										<div class="col-md-9">
											<input name="frontName" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">手机端名称</label>
										<div class="col-md-9">
											<input name="mobileName" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">总额度</label>
										<div class="col-md-9">
											<input name="totalCredits" class="form-control input-inline input-medium" type="text" value="50000">
											<span class="help-inline"></span>
										</div>
									</div>

									<div class="form-group">
										<label class="col-md-3 control-label">累计充值可见</label>
										<div class="col-md-9">
											<input name="minTotalRecharge" class="form-control input-inline input-small" type="text" value="0">
											~
											<input name="maxTotalRecharge" class="form-control input-inline input-small" type="text" value="10000000">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">单次充值范围</label>
										<div class="col-md-9">
											<input name="minUnitRecharge" class="form-control input-inline input-small" type="text" value="100">
											~
											<input name="maxUnitRecharge" class="form-control input-inline input-small" type="text" value="5000">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">最大注册时间</label>
										<div class="col-md-9">
											<input name="maxRegisterTime" class="form-control input-inline input-medium date-picker" type="text" value="2099-12-31">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">消费比例</label>
										<div class="col-md-9">
											<input name="consumptionPercent" class="form-control input-inline input-medium" type="text" min="0.1" max="1" value="0.5">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">白名单用户</label>
										<div class="col-md-9">
											<input name="whiteUsernames" class="form-control input-inline input-medium" type="text">
											<span class="help-inline">英文逗号分割</span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">开放时间</label>
										<div class="col-md-9">
											<input name="startTime" class="form-control input-inline input-small" type="text" value="10:00">
											~
											<input name="endTime" class="form-control input-inline input-small" type="text" value="2:00">
										</div>
									</div>

									<div class="form-group">
										<label class="col-md-3 control-label">是否固定金额</label>
										<div class="col-md-9">
											<div class="radio-list">
												<label class="radio-inline"><input type="radio" name="fixedQRAmount" value="0" checked="checked">不固定金额</label>
												<label class="radio-inline"><input type="radio" name="fixedQRAmount" value="1">固定金额</label>
											</div>
										</div>
									</div>
									<div class="form-group" id="qrCodeContent" style="display: none;">
										<label class="col-md-3 control-label">二维码内容<i class="fa fa-question-circle cursor-pointer ml2" title="请将二维码中的内容解码后填入"></i></label>
										<div class="col-md-9">
											<textarea rows="5" name="qrCodeContent" cols="120"  class="form-control input-inline" style="width: 340px;resize: none;"></textarea>
										</div>
									</div>
									<div class="form-group" id="ImgQrUrlCode" style="display: none;">
										<label class="col-md-3 control-label">二维码</label>
										<img name="ImgQrUrlCode" src="">
										<button type="button" data-command="updateQrUrlCode" class="btn green-meadow">修改二维码</button>
									</div>
									
									<div class="form-group" id="fixedAmountButton" style="display: none;">
										<label class="col-md-3 control-label"></label>
										<div class="col-md-9">
											<button type="button" data-command="fixedAmountQr" class="btn green-meadow">添加固定金额二维码</button>
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
			
			<div id="modal-lottery-payment-channel-add-qr" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="col-md-12">
					<!-- BEGIN PORTLET-->
					<div class="portlet light" style="margin-top: 50px;margin-left: 250px;margin-right: 250px;">
						<div class="portlet-body"  style="padding: 30px 20px 15px 20px;">
						<form class="form-horizontal">
							<div class="table-toolbar">
								<div class="form-inline">
									<div class="row">
										<div class="col-md-12">
<!-- 											<div class="btn-group"> -->
<!-- 												<button data-command="add" class="btn green"> -->
													 最多添加10个固定金额二维码
<!-- 												</button> -->
<!-- 											</div> -->
										</div>
									</div>
								</div>
							</div>
							<div class="table-scrollable table-scrollable-borderless">
								<table class="table table-hover table-light">
									<thead>
										<tr class="align-center">
											<th width="45%">二维码</th>
											<th width="10%">固定金额</th>
											<th width="10%">操作</th>
										</tr>
									</thead>
									<tbody>
										<tr class="align-center">
											<td>
												<textarea rows="1" name="qrCodeContent" cols="180"  class="form-control input-inline" style="width: 500px"></textarea>
												<span class="help-inline"></span>
											</td>
											<td>
												<input name="amount" class="form-control input-inline input-medium" type="text" value="0"/>
												<span class="help-inline"></span>
											</td>
											<td>
												<a data-command="add" data-status="0" href="javascript:;" class="btn default btn-xs black">
													<i class="fa fa-ban"></i> 添加
												</a>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							</form>
							<div class="modal-footer">
								<button type="button" data-command="submit" class="btn green-meadow"><i class="fa fa-check"></i> 确认</button>
								<button type="button" data-dismiss="modal" class="btn default"><i class="fa fa-undo"></i> 取消</button>
							</div>
						</div>
					</div>
					<!-- END PORTLET-->
				</div>
			</div>
			
			<div id="modal-lottery-payment-channel-edit-qr" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="col-md-12">
					<!-- BEGIN PORTLET-->
					<div class="portlet light" style="margin-top: 50px;margin-left: 250px;margin-right: 250px;">
						<div class="portlet-body"  style="padding: 30px 20px 15px 20px;">
						<form class="form-horizontal">
							<div class="table-toolbar">
								<div class="form-inline">
									<div class="row">
										<div class="col-md-12">
<!-- 											<div class="btn-group"> -->
<!-- 												<button data-command="add" class="btn green"> -->
													 最多添加10个固定金额二维码
<!-- 												</button> -->
<!-- 											</div> -->
										</div>
									</div>
								</div>
							</div>
							<div class="table-scrollable table-scrollable-borderless">
								<table class="table table-hover table-light">
									<thead>
										<tr class="align-center">
											<th width="45%">二维码</th>
											<th width="10%">固定金额</th>
											<th width="10%">操作</th>
										</tr>
									</thead>
									<tbody>
										
									</tbody>
								</table>
							</div>
							</form>
							<div class="modal-footer">
								<button type="button" data-command="submit" class="btn green-meadow"><i class="fa fa-check"></i> 确认</button>
								<button type="button" data-dismiss="modal" class="btn default"><i class="fa fa-undo"></i> 取消</button>
							</div>
						</div>
					</div>
					<!-- END PORTLET-->
				</div>
			</div>
			
			<!-- BEGIN PAGE CONTENT-->
			<div id="table-lottery-payment-channel-list" class="row">
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
													<i class="fa fa-plus"></i> 新增账号
												</button>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="table-scrollable table-scrollable-borderless">
								<table class="table table-hover table-light">
									<thead>
										<tr class="align-center">
											<%--<th width="5%">ID</th>--%>
											<th width="12%">后台名称</th>
											<th width="12%">前台名称</th>
											<th width="12%">手机端名称</th>
											<th width="15%">累计充值可见 <i class="fa fa-question-circle cursor-pointer tippy" title="只有当用户累计充值在该范围内时，卡号才可见"></i></th>
											<th width="10%">单次充值范围</th>
											<th width="10%">额度 <i class="fa fa-question-circle cursor-pointer tippy" data-html="#creditDesc"></i></th>
											<th width="5%">状态</th>
											<th class="four" width="24%">操作</th>
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
			<!-- END PAGE CONTENT-->

			<div id="creditDesc" style="display: none;">
				<div style="text-align: left;">
					<span class="color-red">红色</span>表示额度已经使用完且不可继续使用，可选择清零额度。
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
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/locales/bootstrap-datepicker.zh-CN.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/moment.min.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/plugins/tippy/tippy.min.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-payment-channel-mobilescan.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
jQuery(document).ready(function() {
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	// init data
	LotteryPaymentChannel.init();
});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>