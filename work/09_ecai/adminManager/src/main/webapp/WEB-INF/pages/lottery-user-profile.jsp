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
	<title>会员详细信息</title>
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

	<link href="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.css" rel="stylesheet" type="text/css"/>
	<link href="${cdnDomain}theme/assets/custom/plugins/tippy/tippy.css" rel="stylesheet" type="text/css"/>
	<!-- BEGIN THEME STYLES -->
	<link href="${cdnDomain}theme/assets/global/css/components.css?v=${cdnVersion}" rel="stylesheet" type="text/css"/>
	<link href="${cdnDomain}theme/assets/global/css/plugins.css" rel="stylesheet" type="text/css"/>
	<link href="${cdnDomain}theme/assets/admin/layout/css/layout.css" rel="stylesheet" type="text/css"/>
	<link href="${cdnDomain}theme/assets/admin/layout/css/themes/default.css?v=${cdnVersion}" rel="stylesheet" type="text/css"/>
	<link href="${cdnDomain}theme/assets/admin/layout/css/custom.css?v=${cdnVersion}" rel="stylesheet" type="text/css"/>
	<!-- END THEME STYLES -->
	<style type="text/css">
		.button-list {
			clear: both;
			margin: 15px 5px;
		}

		.button-list > .btn {
			width: 160px;
		}
	</style>
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
			<h3 class="page-title">会员详细信息</h3>
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>当前位置：会员管理<i class="fa fa-angle-right"></i></li><li>会员详细信息</li>
				</ul>
			</div>
			<div id="modal-email-modify" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">修改用户邮箱</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">邮箱</label>
										<div class="col-md-9">
											<input name="email" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
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
			<div id="modal-withdraw-name-modify" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">修改取款人</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">取款人</label>
										<div class="col-md-9">
											<input name="withdrawName" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
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
			<div id="modal-extra-point-edit" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">修改私返点数</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">私返点数</label>
										<div class="col-md-9">
											<input name="point" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
											<span class="help-block">请合理分配点数。（系统给代理用户额外的返点）</span>
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
			<div id="modal-user-bets-status-set" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">用户投注权限</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" type="text" disabled="disabled">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">投注权限</label>
										<div class="col-md-9">
											<div class="radio-list">
												<label class="radio-inline"><input type="radio" name="status" value="0" checked="checked"> 正常</label>
												<label class="radio-inline"><input type="radio" name="status" value="-1"> 禁止投注</label>
												<label class="radio-inline"><input type="radio" name="status" value="-2"> 自动掉线</label>
												<label class="radio-inline"><input type="radio" name="status" value="-3"> 投注超时</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">说明</label>
										<div class="col-md-9">
											<input name="message" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
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
			<div id="modal-lottery-user-lock" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">冻结用户</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<div class="alert alert-warning">
										<h4>账户状态说明：</h4>
										<ul style="padding-left: 15px;">
											<li><strong>冻结：</strong> 临时冻结用户，不影响分红、日结及返点等，冻结后可以恢复。</li>
											<li><strong>永久冻结：</strong> 永久冻结用户，分红、日结、返点等都将不再派发，冻结后不可恢复。</li>
										</ul>
									</div>

									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" type="text" disabled="disabled">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">账户状态</label>
										<div class="col-md-9">
											<div class="radio-list">
												<%--<label class="radio-inline"><input type="radio" name="status" value="-3"> 禁用</label>--%>
												<label class="radio-inline"><input type="radio" name="status" value="-1" checked="checked"> 冻结</label>
												<label class="radio-inline"><input type="radio" name="status" value="-2"> 永久冻结</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">说明</label>
										<div class="col-md-9">
											<input name="message" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
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
			<div id="modal-lottery-user-card-edit" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">编辑银行卡信息</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" type="text" disabled="disabled">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">开户行</label>
										<div class="col-md-9">
											<select name="bank" class="form-control input-medium">
											</select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">支行</label>
										<div class="col-md-9">
											<input name="bankBranch" class="form-control input-inline input-medium" type="text">
										</div>
									</div>
									<!-- <div class="form-group">
										<label class="col-md-3 control-label">姓名</label>
										<div class="col-md-9">
											<input name="cardName" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
										</div>
									</div> -->
									<div class="form-group">
										<label class="col-md-3 control-label">卡号</label>
										<div class="col-md-9">
											<input name="cardId" class="form-control input-inline input-medium" type="text">
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
			<div id="modal-password-modify" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">修改密码</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" type="text" disabled="disabled">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">输入密码</label>
										<div class="col-md-9">
											<input name="password1" class="form-control input-inline input-medium" type="password">
											<span class="help-inline"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">重复密码</label>
										<div class="col-md-9">
											<input name="password2" class="form-control input-inline input-medium" type="password">
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
			<div id="modal-point-modify" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">调整返点</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" type="text" disabled="disabled">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">定位返点</label>
										<div class="col-md-9">
											<input name="locatePoint" class="form-control input-inline input-medium" type="text">
											<span class="help-inline"></span>
											<span class="help-block">可调整区间：<span data-field="minPoint">0</span> ~ <span data-field="maxPoint">0</span></span>
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
			<div id="modal-recharge" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">用户充值</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form class="form-horizontal">
								<div class="form-body">
									<div class="alert alert-warning">
										<h4>充值类型说明：</h4>
										<ul style="padding-left: 15px;">
											<li><strong>充值未到账：</strong> 一般用作线下充值，该方式将增加用户报表手续费字段，消费要求与手机类充值一致。</li>
											<li><strong>活动补贴：</strong> 一般用作优惠派发，该方式将增加用户报表优惠字段。</li>
											<li><strong>修改资金（增加）：</strong> 直接增加用户资金。</li>
											<li><strong>修改资金（减少）：</strong> 直接减少用户资金，该方式可将用户资金扣减为负数。</li>
										</ul>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" type="text" disabled="disabled">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">充值类型</label>
										<div class="col-md-9">
											<select name="type" class="form-control input-inline input-medium">
												<option value="">请选择类型</option>
												<option value="1">充值未到账</option>
												<option value="2">活动补贴</option>
												<option value="3">修改资金（增加）</option>
												<option value="4">修改资金（减少）</option>
											</select>
											<span class="help-inline">请选择类型。</span>
										</div>
									</div>
									<div data-hidden="account" class="form-group" style="display: none;">
										<label class="col-md-3 control-label">账户类型</label>
										<div class="col-md-9">
											<select name="account" class="form-control input-inline input-medium">
												<option value="1">主账户</option>
												<option value="2">彩票账户</option>
											</select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">金额</label>
										<div class="col-md-9">
											<input name="amount" class="form-control input-inline input-medium" type="text">
											<span class="help-inline">请输入金额。</span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">资金密码</label>
										<div class="col-md-9">
											<input name="withdrawPwd" class="form-control input-inline input-medium" type="password">
											<span class="help-inline">请输入资金密码。</span>
										</div>
									</div>
									<div data-hidden="limit" class="form-group" style="display: none;">
										<label class="col-md-3 control-label"></label>
										<div class="col-md-9">
											<label><input name="limit" type="checkbox"> 需要消费取款</label>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">说明</label>
										<div class="col-md-9">
											<input name="remarks" class="form-control input-inline input-medium" type="text">
											<span class="help-inline">操作说明。</span>
										</div>
									</div>
								</div>
							</form>
						</div>
						<div class="modal-footer">
							<button type="button" data-dismiss="modal" class="btn default"><i class="fa fa-undo"></i> 取消充值</button>
							<button type="button" data-command="submit" class="btn green-meadow"><i class="fa fa-check"></i> 确认充值</button>
						</div>
					</div>
				</div>
			</div>
			<!-- 关联上级 -->
			<div id="modal-related-upper-modify" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">修改关联上级</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<div class="alert alert-warning">
										<strong>将当前用户关联至其他上级代理，上级代理将享受如下待遇</strong>
										<br>
										<ol>
											<li>上级代理可在前台查看该会员的团队数据(上级代理需在前台手动输入用户名)</li>
											<li>上级代理以百分比形式享受该用户的代理返点</li>
										</ol>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" type="text" disabled="disabled">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">关联上级</label>
										<div class="col-md-9">
											<input name="relatedUpUser" class="form-control input-inline input-medium" autocomplete="off" type="text" required>
											<span class="help-inline">关联至其他用户</span>
											<span class="help-block">请输入要关联至哪个上级的用户名</span>
										</div>
									</div>

									<div class="form-group">
										<label class="col-md-3 control-label">关联返点</label>
										<div class="col-md-9">
											<input name="relatedPoint" class="form-control input-inline input-medium" autocomplete="off" type="number" min="0" step="0.01" max="1">
											<span class="help-inline">关联返点百分比</span>
											<span class="help-block">请输入0至1之间的数值，如0.05，即5%。</span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">说明</label>
										<div class="col-md-9">
											<input name="remarks" class="form-control input-inline input-medium" type="text">
											<span class="help-inline">请输入操作说明</span>
										</div>
									</div>
								</div>
							</form>
						</div>
						<div class="modal-footer">
							<button type="button" data-dismiss="modal" class="btn default"><i class="fa fa-undo"></i> 取消</button>
							<button type="button" data-command="relive" class="btn btn-danger"><i class="fa fa-recycle"></i> 解除关联关系</button>
							<button type="button" data-command="relate" class="btn green-meadow"><i class="fa fa-check"></i> 关联</button>
						</div>
					</div>
				</div>
			</div>
			<!-- 关联会员 -->
			<div id="modal-related-users-modify" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">关联会员</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" type="text" disabled="disabled">
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">关联会员</label>
										<div class="col-md-9">
											<input name="relatedUsers" class="form-control input-inline input-medium" autocomplete="off" type="text">
											<span class="help-inline"></span>
											<span class="help-block">请输入会员名，多个会员之间，请使用英文逗号分割</span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">说明</label>
										<div class="col-md-9">
											<input name="remarks" class="form-control input-inline input-medium" type="text">
											<span class="help-inline">操作说明。</span>
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

			<ul class="nav nav-tabs">
				<li class="active"><a href="javascript:;" data-target="#tab1" data-toggle="tab">基础信息</a></li>
				<li><a href="javascript:;" data-target="#tab2" data-toggle="tab">银行卡信息</a></li>
				<li><a href="javascript:;" data-target="#tab3" data-toggle="tab">登录日志</a></li>
				<li id="critical-log-tab" style="display:none;"><a href="javascript:;" data-target="#tab4" data-toggle="tab">关键日志</a></li>
				<li id="withdraw-limit-log-tab" style="display:none;"><a href="javascript:;" data-target="#tab5" data-toggle="tab">提款消费明细</a></li>
			</ul>

			<div class="tab-content">
				<div class="tab-pane active" id="tab1">
					<div class="row">
						<div class="col-md-12">
							<!-- BEGIN PORTLET-->
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
											<td class="text">冻结金额：</td>
											<td data-field="freezeMoney" colspan="3"></td>
										</tr>
										<tr>
											<td class="text">PT余额：</td>
											<td data-field="ptMoney"><a href="javascript:;" data-method="checkPTBalance">查看余额</a></td>
											<td class="text">AG余额：</td>
											<td data-field="agMoney"><a href="javascript:;" data-method="checkAGBalance">查看余额</a></td>
											<td class="text">PT用户名：</td>
											<td data-field="ptUsername"></td>
											<td class="text">AG用户名：</td>
											<td data-field="agUsername"></td>
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
											<td class="text">绑定取款人：</td>
											<td data-field="withdrawName"></td>
											<td class="text">资金密码：</td>
											<td data-field="withdrawPassword"></td>
											<td class="text">绑定银行卡：</td>
											<td data-field="bindCard"></td>
											<td class="text">密保问题：</td>
											<td data-field="bindSecurity"></td>
										</tr>
										<tr>
											<td class="text">绑定邮箱：</td>
											<td data-field="bindEmail"></td>
											<td class="text">密保问题1：</td>
											<td data-field="securityQ1"></td>
											<td class="text">密保问题2：</td>
											<td data-field="securityQ2"></td>
											<td class="text">密保问题3：</td>
											<td data-field="securityQ3"></td>
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
											<td class="text">VIP等级：</td>
											<td data-field="vipLevel" colspan="1"></td>
											<td class="text">会员积分：</td>
											<td data-field="integral" colspan="1"></td>
											<td class="text">其他：</td>
											<td data-field="message" colspan="7"></td>
										</tr>
										<tr>
											<td class="text">谷歌身份验证器：</td>
											<td data-field="isBindGoogle"></td>
											<td class="text">状态备注：</td>
											<td data-field="message" colspan="5"></td>
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
										<tr>
											<td class="text">取款权限：</td>
											<td data-field="allowWithdraw"  colspan="3"></td>
											<td class="text">转账权限：</td>
											<td data-field="allowPlatformTransfers" colspan="3"></td>
										</tr>
										<tr>
											<td class="text">关联上级：</td>
											<td data-field="relatedUpper" colspan="7"></td>
										</tr>
										<tr>
											<td class="text">关联会员：</td>
											<td data-field="relatedUsers" colspan="7"></td>
										</tr>
									</table>
									<div class="button-list">
										<a data-command="modify-point" href="javascript:;" class="btn green"><i class="fa fa-arrow-up"></i> 修改返点</a>
										<a data-command="down-point" href="javascript:;" class="btn green"><i class="fa fa-arrow-down"></i> 统一降点</a>
										<%--<a data-command="extra-point" href="javascript:;" class="btn blue"><i class="fa fa-edit"></i> 修改私返点数</a>--%>
										<a data-command="recharge" href="javascript:;" class="btn green"><i class="fa fa-sign-in"></i> 充值</a>
										<a data-command="login-password" href="javascript:;" class="btn green"><i class="fa fa-edit"></i> 修改登录密码</a>
										<a data-command="withdraw-password" href="javascript:;" class="btn green"><i class="fa fa-edit"></i> 修改资金密码</a>
										<a data-command="withdraw-name" href="javascript:;" class="btn green"><i class="fa fa-edit"></i> 修改取款人</a>
										<a data-command="reset-security" href="javascript:;" class="btn green"><i class="fa fa-undo"></i> 重置密保问题</a>
									</div>
									<div class="button-list">
										<a data-command="modify-xiaofei" href="javascript:;" class="btn red"><i class="fa fa-edit"></i> 清空提款消费量</a>
										<a data-command="reset-lock-time" href="javascript:;" class="btn red disabled tippy" title="清空账户时间锁后如仍不能取款，请在银行卡解锁记录中删除相关卡号解锁记录即可"><i class="fa fa-undo"></i> 清空账户时间锁</a>
										<a data-command="modify-related-upper" href="javascript:;" class="btn red"><i class="fa fa-edit"></i> 修改关联上级</a>
										<a data-command="modify-related-users" href="javascript:;" class="btn red disabled" disabled="disabled"><i class="fa fa-edit"></i> 修改关联会员</a>
										<a data-command="change-proxy" href="javascript:;" class="btn red disabled"><i class="fa fa-refresh"></i> 转为代理</a>
										<a data-command="BStatus" href="javascript:;" class="btn red"><i class="fa fa-gamepad"></i> 投注权限</a>
										<a data-command="AStatus" href="javascript:;" class="btn red"><i class="fa fa-ban"></i> 冻结账号</a>
									</div>
									<div class="button-list">
										<a data-command="allow-withdraw" href="javascript:;" class="btn blue"><i class="fa fa-check"></i> 开启取款</a>
										<a data-command="allow-team-withdraw" href="javascript:;" class="btn blue"><i class="fa fa-check"></i> 开启团队取款</a>
										<a data-command="prohibit-team-withdraw" href="javascript:;" class="btn blue"><i class="fa fa-ban"></i> 关闭团队取款</a>
										<a data-command="allow-equal-code" href="javascript:;" class="btn blue"><i class="fa fa-check"></i> 开启同级开号</a>
										<a data-command="allow-transfers" href="javascript:;" class="btn blue"><i class="fa fa-check"></i> 开启上下级转账</a>
										<a data-command="allow-team-transfers" href="javascript:;" class="btn blue"><i class="fa fa-check"></i> 开启团队上下级转账</a>
										<a data-command="prohibit-team-transfers" href="javascript:;" class="btn blue"><i class="fa fa-ban"></i> 关闭团队上下级转账</a>
										<%--<a data-command="reset-email" href="javascript:;" class="btn blue"><i class="fa fa-undo"></i> 重置邮箱</a>--%>
										<%--<a data-command="modify-email" href="javascript:;" class="btn blue"><i class="fa fa-edit"></i> 修改邮箱</a>--%>
									</div>
									<div class="button-list">
										<a data-command="allow-platform-transfers" href="javascript:;" class="btn green"><i class="fa fa-check"></i> 开启转账</a>
										<a data-command="allow-team-platform-transfers" href="javascript:;" class="btn green"><i class="fa fa-check"></i> 开启团队转账</a>
										<a data-command="prohibit-team-platform-transfers" href="javascript:;" class="btn green"><i class="fa fa-ban"></i> 关闭团队转账</a>
										<a data-command="unbind-google" href="javascript:;" class="btn green disabled"><i class="fa fa-edit"></i> 解绑谷歌验证器</a>
										<a data-command="change-zhaoshang" href="javascript:;" class="btn green disabled"><i class="fa fa-edit"></i> 转为超级招商</a>
									</div>
									<div class="button-list">
										<a data-command="lock-team" href="javascript:;" class="btn grey-cascade" disabled="disabled"><i class="fa fa-ban"></i> 冻结团队</a>
										<a data-command="un-lock-team" href="javascript:;" class="btn grey-cascade" disabled="disabled"><i class="fa fa-check"></i> 解冻团队</a>
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
							<!-- BEGIN PORTLET-->
							<div id="card_information" class="portlet light">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">银行卡信息</span>
									</div>
									<div class="actions">
										<div class="btn-group">
											<a data-command="add" href="javascript:;" class="btn green"><i class="fa fa-plus"></i> 添加银行卡</a>
										</div>
									</div>
								</div>
								<div class="portlet-body">
									<div class="table-scrollable table-scrollable-borderless">
										<table class="table table-hover table-light">
											<thead>
											<tr class="align-center">
												<th width="8%">序号</th>
												<th width="10%">用户名</th>
												<th width="10%">开户行</th>
												<th>支行</th>
												<th width="10%">姓名</th>
												<th width="14%">卡号</th>
												<th width="8%">卡片状态</th>
												<th width="12%">锁定时间</th>
												<th class="three">操作</th>
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
				</div>
				<div class="tab-pane" id="tab3">
					<div class="row">
						<%--<div class="col-md-6 col-sm-6">--%>
						<%--<!-- BEGIN PORTLET-->--%>
						<%--<div id="user_quota" class="portlet light">--%>
						<%--<div class="portlet-title">--%>
						<%--<div class="caption">--%>
						<%--<span class="caption-subject font-green-sharp bold uppercase">配额信息</span>--%>
						<%--</div>--%>
						<%--<div class="actions">--%>
						<%--<div class="btn-group">--%>
						<%--<a data-command="save" href="javascript:;" class="btn blue"><i class="fa fa-save"></i> 保存配额</a>--%>
						<%--</div>--%>
						<%--</div>--%>
						<%--</div>--%>
						<%--<div class="portlet-body">--%>
						<%--<div class="table-scrollable table-scrollable-borderless">--%>
						<%--<table class="table table-hover table-light">--%>
						<%--<thead>--%>
						<%--<tr class="align-center">--%>
						<%--<td width="25%">区段</td>--%>
						<%--<td width="25%">总额</td>--%>
						<%--<td width="25%">使用</td>--%>
						<%--<td width="25%">剩余</td>--%>
						<%--</tr>--%>
						<%--</thead>--%>
						<%--<tbody>--%>
						<%----%>
						<%--</tbody>--%>
						<%--</table>--%>
						<%--</div>--%>
						<%--</div>--%>
						<%--</div>--%>
						<%--<!-- END PORTLET-->--%>
						<%--</div>--%>
						<div class="col-md-12">
							<!-- BEGIN PORTLET-->
							<div id="login_logs" class="portlet light">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">登录日志</span>
									</div>
								</div>
								<div class="portlet-body">
									<div class="table-scrollable table-scrollable-borderless">
										<table class="table table-hover table-light">
											<thead>
											<tr class="align-center">
												<th width="30%">登录时间</th>
												<th width="30%">IP</th>
												<th width="40%">参考地址</th>
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
				</div>
				<div class="tab-pane" id="tab4">
					<div class="row" id="critical-log-container">
						<div class="col-md-12">
							<!-- BEGIN PORTLET-->
							<div id="user_update_logs" class="portlet light">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">关键日志</span>
									</div>
								</div>
								<div class="portlet-body">
									<div class="table-scrollable table-scrollable-borderless">
										<table class="table table-hover table-light">
											<thead>
											<tr class="align-center">
												<th width="60%">操作内容</th>
												<th width="10%">操作用户</th>
												<th width="10%">操作IP</th>
												<th width="10%">参考地址</th>
												<th width="10%">操作时间</th>
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
				</div>
				<div class="tab-pane" id="tab5">
					<div class="row" id="withdraw-limit-log-container">
						<div class="col-md-12">
							<!-- BEGIN PORTLET-->
							<div id="withdraw-limit-table" class="portlet light">
								<div class="portlet-title">
									<div class="caption">
										<span class="caption-subject font-green-sharp bold uppercase">提款消费明细 <i class="fa fa-question-circle cursor-pointer tippy" data-html="#limitDesc"></i></span>
									</div>
								</div>

								<div class="table-toolbar" style="margin:0px;" id="withdraw-limit">
								</div>

								<div class="portlet-body">
									<div class="table-scrollable table-scrollable-borderless">
										<table class="table table-hover table-light">
											<thead>
											<tr class="align-center">
												<th width="16%">充值时间</th>
												<th width="10%">充值金额</th>
												<th width="10%">消费比例</th>
												<th>已消费/消费要求</th>
												<th width="10%">剩余 <i class="fa fa-question-circle cursor-pointer tippy" data-html="#remainConsumptionTip"></i></th>
												<th width="10%">支付类型</th>
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

			<div id="limitDesc" style="display: none;">
				<div style="text-align: left;">
					<h4>提款消费说明：</h4>
					<ul>
						<li><h5>用户每一次充值都需要完成相应金额提款消费要求。</h5></li>
					</ul>
				</div>

				<div style="text-align: left;">
					<h4>计算流水说明：</h4>
					<ul>
						<li><h5>每一次充值时间到下次充值时间之间的间隔为计算当次消费要求的时间依据。（只统计已完成数据，包含彩票和第三方游戏）</h5></li>
						<li><h5>所有已消费记录都会往较早时间的充值进行消费填补，但不会往后填补。</h5></li>
						<li><h5>上下级转账的消费要求将会转移至相应下级。</h5></li>
					</ul>
				</div>

				<div style="text-align: left;">
					<h4>消费比例说明：</h4>
					<ul>
						<li><h5>网银类充值：30%。</h5></li>
						<li><h5>手机类充值：50%。（微信、支付宝、QQ都归属到这类）</h5></li>
						<li><h5>系统充值：50%。（充值未到账、活动补贴、管理员增）</h5></li>
						<li><h5>上下级转账：50%。</h5></li>
						<li><h5>充值补单：根据不同类型要求同上。</h5></li>
					</ul>
				</div>

				<h5 class="color-red text-left">后台用户转账（会员列表->用户转账）不会产生消费明细相关数据。</h5>
			</div>

			<div id="remainConsumptionTip" style="display: none;">
				<div style="text-align: left;">
					<ul>
						<li><span class="color-green">绿色：已完成；</span></li>
						<li><span class="color-red">红色：未完成；</span></li>
						<li><span class="color-blue">蓝色：需扣除；（如上下级转账时的提款消费要求转移）</span></li>
					</ul>
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
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/moment.min.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/plugins/tippy/tippy.min.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-user-profile.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
	jQuery(document).ready(function() {
		Metronic.init(); // init metronic core components
		Layout.init(); // init current layout
		// init data
		LotteryUserProfile.init();
	});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>