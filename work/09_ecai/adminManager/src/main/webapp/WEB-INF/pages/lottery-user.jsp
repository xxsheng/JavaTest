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
<title>会员管理 - 会员列表</title>
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
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/css/datepicker3.css" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css"/>

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
	<!-- BEGIN CONTENT -->
	<div class="page-content-wrapper">
		<div class="page-content">
			<!-- BEGIN PAGE HEADER-->
			<h3 class="page-title">会员列表</h3>
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>当前位置：会员管理<i class="fa fa-angle-right"></i></li><li>会员列表</li>
				</ul>
			</div>
			<!-- END PAGE HEADER-->
			<div id="modal-lottery-user-change" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">线路转移</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<div class="alert alert-warning">
										<strong>只转移下级用户：</strong> 仅将“待转移线路”会员本身转移到“目标线路”，不包含下级。
										<br/>
										<strong>转移整条线路：</strong> “待转移线路”下所有会员和本身将转移到“目标线路”，一般选这个。
									</div>
									<div class="alert alert-danger">
										<strong>暂不允许从上级转移到下级。系统会自动修正“待转移线路”会员及其所有下级的契约关系,“目标线路”不受影响,请谨慎操作。</strong>
										<br/>
										<br/>
										<strong>如果两条线路会员中存在关联关系，请确认是否需要修正并手动处理。</strong>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label"></label>
										<div class="col-md-9">
											<div class="radio-list">
												<label class="radio-inline"><input type="radio" name="type" value="0" checked="checked"> 只转移下级用户</label>
												<label class="radio-inline"><input type="radio" name="type" value="1"> 转移整条线路</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">待转移线路</label>
										<div class="col-md-9">
											<input name="aUser" class="form-control input-inline input-medium" type="text">
											<span class="help-inline" data-default="请填写用户名。"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">目标线路</label>
										<div class="col-md-9">
											<input name="bUser" class="form-control input-inline input-medium" type="text">
											<span class="help-inline" data-default="请填写用户名。"></span>
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
			<div id="modal-user-transfer" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">用户转账</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<div class="alert alert-warning">
										<h4>说明：</h4>
										<ul style="padding-left: 15px;">
											<li>系统从待转账会员余额中转账给目标会员。</li>
											<li>用户之间无需存在上下级关系。</li>
										</ul>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">待转会员</label>
										<div class="col-md-9">
											<input name="aUser" class="form-control input-inline input-medium" type="text">
											<span class="help-inline" data-default="请填写用户名。"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">目标会员</label>
										<div class="col-md-9">
											<input name="bUser" class="form-control input-inline input-medium" type="text">
											<span class="help-inline" data-default="请填写用户名。"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">转账金额</label>
										<div class="col-md-9">
											<input name="money" class="form-control input-inline input-medium" type="text">
											<span class="help-inline" data-default="请填写金额。"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">备注</label>
										<div class="col-md-9">
											<input name="remarks" class="form-control input-inline input-medium" type="text">
											<span class="help-inline" data-default="请填写备注。"></span>
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
										<label class="col-md-3 control-label">冻结说明</label>
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
			<div id="modal-lottery-user-add" class="modal fade" data-backdrop="static" tabindex="-1">
				<div class="modal-dialog" style="width: 680px;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"></button>
							<h4 class="modal-title">添加会员</h4>
						</div>
						<div class="modal-body" style="padding: 30px 20px 15px 20px;">
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<div class="alert alert-warning">
										<h4>账户类型说明：</h4>
										<ul style="padding-left: 15px;">
											<li><strong>代理：</strong> 可在前台自主开号，拥有代理相关功能。系统将根据等级自动生成契约配置。</li>
											<li><strong>玩家：</strong> 普通玩家，仅拥有游戏、充值、提款等除代理外的功能。</li>
											<li><strong>关联账号：</strong> 前台数据查看账号，可关联至多个账号，创建后也可在详情中修改。</li>
										</ul>
									</div>

									<div class="form-group">
										<label class="col-md-3 control-label">账户类型</label>
										<div class="col-md-9">
											<div class="radio-list">
												<label class="radio-inline"><input type="radio" name="type" value="1"> 代理</label>
												<label class="radio-inline"><input type="radio" name="type" value="2"> 玩家</label>
												<label class="radio-inline"><input type="radio" name="type" value="3"> 关联账号</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">用户名</label>
										<div class="col-md-9">
											<input name="username" class="form-control input-inline input-medium" autocomplete="off" type="text">
											<span class="help-inline" data-default="请输入用户名。"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">昵称</label>
										<div class="col-md-9">
											<input name="nickname" class="form-control input-inline input-medium" autocomplete="off" type="text">
											<span class="help-inline" data-default="请填写账户昵称。"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">用户密码</label>
										<div class="col-md-9">
											<input name="password1" class="form-control input-inline input-medium" autocomplete="off" type="password">
											<span class="help-inline" data-default="请输入账户密码。"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">重复密码</label>
										<div class="col-md-9">
											<input name="password2" class="form-control input-inline input-medium" autocomplete="off" type="password">
											<span class="help-inline" data-default="请重复上面输入的密码。"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">定位返点</label>
										<div class="col-md-9">
											<input name="locatePoint" class="form-control input-inline input-medium" autocomplete="off" type="text">
											<span class="help-inline" data-default="必须低于上级代理账号定位返点。"></span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">上级代理</label>
										<div class="col-md-9">
											<input name="upperUser" class="form-control input-inline input-medium" autocomplete="off" type="text">
											<span class="help-inline" data-default="请填写上级代理。"></span>
										</div>
									</div>
									<div class="form-group" data-hidden="relatedUsers" style="display: none;">
										<label class="col-md-3 control-label">关联会员</label>
										<div class="col-md-9">
											<input name="relatedUsers" class="form-control input-inline input-medium" autocomplete="off" type="text" required>
											<span class="help-inline" data-default="英文逗号分割。"></span>
										</div>
									</div>
								</div>
							</form>
						</div>
						<div class="modal-footer">
							<button type="button" data-dismiss="modal" class="btn default"><i class="fa fa-undo"></i> 返回列表</button>
							<button type="button" data-command="submit" class="btn green-meadow"><i class="fa fa-check"></i> 确认添加</button>
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
											<input name="username" class="form-control input-inline input-medium" type="text" disabled="disabled" autocomplete="off">
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
											<input name="amount" class="form-control input-inline input-medium" type="text" autocomplete="off">
											<span class="help-inline">请输入金额。</span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-md-3 control-label">资金密码</label>
										<div class="col-md-9">
											<input name="withdrawPwd" class="form-control input-inline input-medium" type="password" autocomplete="off">
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
											<input name="remarks" class="form-control input-inline input-medium" type="text" autocomplete="off">
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
			<!-- BEGIN PAGE CONTENT-->
			<div id="table-user-list" class="row">
				<div class="col-md-12">
					<!-- BEGIN PORTLET-->
					<div class="portlet light" style="margin-bottom: 10px;">
						<div class="portlet-body">
							<div class="table-toolbar">
								<div class="form-inline">
									<div class="row">
										<div class="col-md-12">
											<div class="form-group pull-right">
												<button data-command="change-line" class="btn blue">
													<i class="fa fa-plus"></i> 线路转移
												</button>
												<button data-command="add" class="btn green tippy">
													<i class="fa fa-plus"></i> 添加用户
												</button>
												<button data-command="transfer" class="btn yellow">
													<i class="fa fa-recycle"></i> 用户转账
												</button>
											</div>
										</div>
									</div>
									<div class="row" style="padding-top: 3px;">
										<div class="col-md-12">
											<div class="form-group">
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
														<option value="3">关联账号</option>
														<option value="4">虚拟用户</option>
													</select>
												</div>
											</div>
											<div class="form-group">
												<div class="input-group">
													<span class="input-group-addon no-bg fixed">查找方式</span>
													<select name="matchType" class="form-control">
														<option value="USER">精确查找</option>
														<option value="LOWER">直属下级</option>
														<option value="UPPER">直属上级</option>
														<option value="TEAM">团队成员</option>
													</select>
												</div>
											</div>
											<div class="form-group">
												<div class="input-group">
													<span class="input-group-addon no-bg fixed">排序方式</span>
													<select name="sortColoum" class="form-control">
														<option value="">默认排序</option>
														<option value="totalMoney">主账户</option>
														<option value="lotteryMoney">彩票账户</option>
														<option value="code">代理级别</option>
														<option value="registTime">注册时间</option>
														<option value="freezeMoney">冻结资金</option>
													</select>
												</div>
											</div>
											<div class="form-group">
												<div class="input-group">
													<select name="sortType" class="form-control">
														<option value="DESC">大到小</option>
														<option value="ASC">小到大</option>
													</select>
												</div>
											</div>
											<div class="form-group">
												<a data-command="search" href="javascript:;" class="btn green-meadow"><i class="fa fa-search"></i> 搜索用户</a>
												<label><input name="advanced" type="checkbox"> 高级搜索</label>
												<label><input name="online" type="checkbox"> 在线用户</label>
												<!-- <label><input name="nickname" type="checkbox" > 虚拟用户</label> -->
											</div>
										</div>
									</div>
									<div class="row" data-hide="advanced" style="display: none; padding-top: 3px;">
										<div class="col-md-12">
								
											<div class="form-group">
												<div class="input-group">
													<span class="input-group-addon no-bg fixed">账号状态</span>
													<select name="aStatus" class="form-control">
														<option value="">全部</option>
														<option value="0">正常</option>
														<option value="-1">冻结</option>
														<option value="-2">永久冻结</option>
														<%--<option value="-3">禁用</option>--%>
													</select>
												</div>
											</div>
											<div class="form-group">
												<div class="input-group">
													<span class="input-group-addon no-bg fixed">投注权限</span>
													<select name="bStatus" class="form-control">
														<option value="">全部</option>
														<option value="0">正常</option>
														<option value="-1">禁止投注</option>
														<option value="-2">自动掉线</option>
														<option value="-3">投注超时</option>
													</select>
												</div>
											</div>
										</div>

									</div>
									<div class="row" data-hide="advanced" style="display: none; padding-top: 3px;">
										<div class="col-md-12">
											<div class="form-group">
												<div class="input-group input-range input-medium">
													<span class="input-group-addon no-bg">主账余额</span>
													<input class="form-control from" name="minTotalMoney" type="number">
													<span class="input-group-addon symbol">~</span>
													<input class="form-control to" name="maxTotalMoney" type="number">
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-range input-medium">
													<span class="input-group-addon no-bg">彩票余额</span>
													<input class="form-control from" name="minLotteryMoney" type="number">
													<span class="input-group-addon symbol">~</span>
													<input class="form-control to" name="maxLotteryMoney" type="number">
												</div>
											</div>
											<div class="form-group">
												<div class="input-group input-range input-medium">
													<span class="input-group-addon no-bg">代理级别</span>
													<input class="form-control from" name="minProxyCode" type="number">
													<span class="input-group-addon symbol">~</span>
													<input class="form-control to" name="maxProxyCode" type="number">
												</div>
											</div>
										</div>
									</div>
									<div class="row" data-hide="advanced" style="display: none; padding-top: 3px;">
										<div class="col-md-12">
											<div class="form-group">
												<div class="input-group input-medium">
													<span class="input-group-addon no-bg">注册时间</span>
													<input name="registTime" class="form-control date-picker" type="text">
												</div>
											</div>
										</div>
									</div>

								</div>
							</div>
							<div class="table-scrollable table-scrollable-borderless">
								<div class="user-direction">
									<ul class="page-breadcrumb">
									</ul>
								</div>

								<table class="table table-hover table-light">
									<thead>
										<tr class="align-center">
											<th>用户名</th>
											<th>团队在线</th>
											<th>账号类型</th>
											<th>主账户</th>
											<th>彩票账户</th>
											<th>级别/返点</th>
											<th style="width: 140px;">最后登录</th>
											<th>账号状态</th>
											<th>投注权限</th>
											<th>在线</th>
											<th class="three" style="width: 130px;">操作</th>
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

<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-datepicker/js/locales/bootstrap-datepicker.zh-CN.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/moment.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/plugins/tippy/tippy.min.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-user.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
jQuery(document).ready(function() {
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	// init data
	LotteryUser.init();
});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>