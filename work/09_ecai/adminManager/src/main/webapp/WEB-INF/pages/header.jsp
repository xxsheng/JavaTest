<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!-- BEGIN HEADER -->
<div class="page-header navbar navbar-fixed-top">
	<!-- BEGIN HEADER INNER -->
	<div class="page-header-inner">
		<div class="page-logo">
			<img data-field="logo">
		</div>
		<!-- BEGIN RESPONSIVE MENU TOGGLER -->
		<a href="javascript:;" class="menu-toggler responsive-toggler" data-toggle="collapse" data-target=".navbar-collapse"></a>
		<!-- END RESPONSIVE MENU TOGGLER -->
		<!-- BEGIN PAGE TOP -->
		<div class="page-top">
			<div class="top-toolbar">
				<div class="button-group" style="display: none;">
					<a data-command="back" href="javascript:;" class="btn-history"><i class="fa fa-arrow-circle-left"></i> 后退</a>
					<a data-command="forward" href="javascript:;" class="btn-history"><i class="fa fa-arrow-circle-right"></i> 前进</a>
				</div>
				<div class="text">当前在线人数：<span data-field="uOnlineCount">0</span>人</div>
			</div>
			<div class="top-menu pull-left">
				<ul class="nav navbar-nav">
									
					<li class="dropdown dropdown-extended" data-type="audios">
						<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
							<i class="fa fa-bell" aria-hidden="true" data-type="audios-icon"></i>
						</a>
						<ul class="dropdown-menu">
							<li class="external">
								<a href="javascript:;" data-command="play-withdraw-audio"><span>播放线下充值、提款提示音</span> <i class="icon-control-play"></i></a>
							</li>
							
							
							<li class="external" data-type="highPrize">
								<a href="javascript:;" data-command="high-prize-audio"><span>播放大额中奖提示音</span> <i class="icon-control-play"></i></a>
							</li>
						</ul>
					</li>
					
					<li class="dropdown dropdown-extended" data-type="lock-withdraw">
						<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
							<i class="fa fa-lock" aria-hidden="true" data-type="lock-icon"></i>
						</a>
						<ul class="dropdown-menu">
							<li class="external">
								<a href="javascript:;" data-command="unlock-withdraw" data-group="unlock" style="display: none;"><span>解锁资金密码</span> <i class="fa fa-unlock"></i></a>
								<a href="javascript:;" data-group="unlock" style="display: none;"><span>解锁后可进行资金密码类操作</span></a>
								<a href="javascript:;" data-command="lock-withdraw" data-group="lock" style="display: none;"><span>锁定资金密码</span> <i class="fa fa-lock"></i></a>
								<a href="javascript:;" data-group="lock" style="display: none;"><span>您的资金密码已解锁，可进行资金密码类操作</span></a>
							</li>
						</ul>
					</li>
				</ul>
			</div>
			<div class="top-menu">
				<ul class="nav navbar-nav pull-right">
					<li class="dropdown dropdown-extended">
						<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
							<i class="icon-credit-card"></i>
							<span data-field="uWithdrawCount" class="badge badge-danger">0</span>
						</a>
						<ul class="dropdown-menu">
							<li><p>您有 <span data-field="uWithdrawCount">0</span> 条提款请求需要处理</p>
							</li>
							<li>
								<ul class="dropdown-menu-list"></ul>
							</li>
							<li class="external">
								<a href="javascript:;" data-href="lottery-user-withdraw">查看所有提现请求 <i class="icon-arrow-right"></i></a>
							</li>
						</ul>
					</li>
					<li class="dropdown dropdown-extended">
						<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
							<i class="icon-credit-card"></i>
							<span data-field="bRegchargeCount" class="badge badge-danger">0</span>
						</a>
						<ul class="dropdown-menu">
							<li><p>您有 <span data-field="bRegchargeCount">0</span> 条线下转帐请求需要处理</p>
							</li>
							<li>
								<ul class="dropdown-menu-list"></ul>
							</li>
							<li class="external">
								<a href="javascript:;" data-href="lottery-user-recharge">查看所有充值请求 <i class="icon-arrow-right"></i></a>
							</li>
						</ul>
					</li>
					
					

					<li class="dropdown dropdown-extended" data-type="highPrize">
						<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
							<i class="fa fa-money" aria-hidden="true"></i>
							<span data-field="highPrizeUnProcessCount" class="badge badge-danger">0</span>
						</a>
						<ul class="dropdown-menu">
							<li><p>您有 <span data-field="highPrizeUnProcessCount">0</span> 条大额中奖需要检查</p>
							</li>
							<li>
								<ul class="dropdown-menu-list"></ul>
							</li>
							<li class="external">
								<a href="javascript:;" data-href="user-high-prize">查看所有大额中奖 <i class="icon-arrow-right"></i></a>
							</li>
						</ul>
					</li>

					<%--<li class="dropdown dropdown-extended">--%>
					<%--<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">--%>
					<%--<i class="icon-hourglass"></i>--%>
					<%--<span data-field="pOverloadCount" class="badge badge-danger">0</span>--%>
					<%--</a>--%>
					<%--<ul class="dropdown-menu extended tasks">--%>
					<%--<li><p>您有 <span data-field="pOverloadCount">0</span> 个收款账号已达警示额度</p></li>--%>
					<%--<li class="external">--%>
					<%--<a href="javascript:;" data-href="lottery-payment-card">转账账号警告 <span data-field="pCardOverload">0</span> 个 <i class="icon-arrow-right"></i></a>--%>
					<%--</li>--%>
					<%--<li class="external">--%>
					<%--<a href="javascript:;" data-href="lottery-payment-thrid">第三方账号警告 <span data-field="pThridOverload">0</span> 个 <i class="icon-arrow-right"></i></a>--%>
					<%--</li>--%>
					<%--</ul>--%>
					<%--</li>--%>
					<%--<li class="dropdown dropdown-extended">--%>
					<%--<a href="javascritp:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">--%>
					<%--<i class="icon-present"></i>--%>
					<%--<span data-field="aTotalCount" class="badge badge-success">0</span>--%>
					<%--</a>--%>
					<%--<ul class="dropdown-menu extended tasks">--%>
					<%--<li><p>您有 <span data-field="aTotalCount">0</span> 条未处理活动</p></li>--%>
					<%--<li class="external">--%>
					<%--<a href="javascript:;" data-href="activity-rebate-bind">绑定资料活动 <span data-field="aBindCount">0</span> 条 <i class="icon-arrow-right"></i></a>--%>
					<%--</li>--%>
					<%--<li class="external">--%>
					<%--<a href="javascript:;" data-href="activity-rebate-recharge">开业大酬宾活动 <span data-field="aRechargeCount">0</span> 条 <i class="icon-arrow-right"></i></a>--%>
					<%--</li>--%>
					<%--</ul>--%>
					<%--</li>--%>
					<li class="dropdown dropdown-user">
						<a href="javascritp:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
							<img alt="" class="img-circle" src="${cdnDomain}theme/assets/admin/layout/img/avatar.png"/>
							<span data-field="username" class="username username-hide-on-mobile"></span>
							<i class="fa fa-angle-down"></i>
						</a>
						<ul class="dropdown-menu">
							<li><a data-command="mod-login-pwd" href="javascript:;"><i class="fa fa-key"></i> 修改登录密码 </a></li>
							<li><a data-command="mod-withdraw-pwd" href="javascript:;"><i class="fa fa-key"></i> 修改资金密码 </a></li>
							<li><a href="./logout"><i class="fa fa-circle-o-notch"></i> 退出登录 </a></li>
						</ul>
					</li>
				</ul>
			</div>
		</div>
		<!-- END PAGE TOP -->
	</div>
	<!-- END HEADER INNER -->

</div>
<!-- END HEADER -->

<!-- 修改登录密码 -->
<div id="modal-admin-mod-login-pwd" class="modal fade" data-backdrop="static" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"></button>
				<h4 class="modal-title">修改登录密码</h4>
			</div>
			<div class="modal-body" style="padding: 30px 20px 15px 20px;">
				<form class="form-horizontal">
					<div class="form-body">
						<div class="form-group">
							<label class="col-md-3 control-label">旧密码</label>
							<div class="col-md-9">
								<input name="oldPassword" class="form-control input-inline input-medium" type="password">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">新密码</label>
							<div class="col-md-9">
								<input name="password1" class="form-control input-inline input-medium" type="password">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">重复新密码</label>
							<div class="col-md-9">
								<input name="password2" class="form-control input-inline input-medium" type="password">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">谷歌口令</label>
							<div class="col-md-9">
								<input name="googleCode" class="form-control input-inline input-medium" type="text" autocomplete="off">
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
<!-- 修改资金密码 -->
<div id="modal-admin-mod-withdraw-pwd" class="modal fade" data-backdrop="static" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"></button>
				<h4 class="modal-title">修改资金密码</h4>
			</div>
			<div class="modal-body" style="padding: 30px 20px 15px 20px;">
				<form class="form-horizontal">
					<div class="form-body">
						<div class="form-group">
							<label class="col-md-3 control-label">旧密码</label>
							<div class="col-md-9">
								<input name="oldPassword" class="form-control input-inline input-medium" type="password">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">新密码</label>
							<div class="col-md-9">
								<input name="password1" class="form-control input-inline input-medium" type="password">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">重复新密码</label>
							<div class="col-md-9">
								<input name="password2" class="form-control input-inline input-medium" type="password">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">谷歌口令</label>
							<div class="col-md-9">
								<input name="googleCode" class="form-control input-inline input-medium" type="text" autocomplete="off">
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

<!-- Google动态密码 -->
<div id="modal-admin-mod-google-pwd" class="modal fade" data-backdrop="static" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"></button>
				<h4 class="modal-title">绑定Google动态密码</h4>
			</div>
			<div class="modal-body" style="padding: 30px 20px 15px 20px;">
				<form class="form-horizontal">
					<div class="form-body">
						<div class="form-group">
							<div style="display: inline;padding: 5px;float: left;"><img alt="" src="" id="googleQR" ></div>
							<div style="display: inline;padding: 5px;float: left;margin-top: 10px;"><h4>Google Auth 使用说明</h4> <span>IOS和Android用户到App Store搜索 Google Authenticator </span></div>
							<div style="display: inline;padding: 5px;float: left;"><h4>第一步</h4> <span>到App Store下载客户端</span></div><br>
							<div style="display: inline;padding: 5px;float: left;"><h4>第二步</h4> <span>输入客户端的口令绑定动态密码</span></div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">登录密码</label>
							<div class="col-md-9">
								<input name="loginPassword" class="form-control input-inline input-medium" type="password">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">Google口令</label>
							<div class="col-md-9">
								<input name="password" class="form-control input-inline input-medium" type="text">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">秘钥</label>
							<div class="col-md-9">
								<input id="googleSecret" class="form-control input-inline input-medium" type="text" readonly disabled>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label"></label>
							<div class="col-md-7">*每个二维码只能用一次，重新打开需要重新扫描!</div>
							<label class="col-md-3 control-label"></label>
							<div class="col-md-7">*绑定成功后，请勿删除手机中的密码器!</div>
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

<!-- 解锁资金密码 -->
<div id="modal-admin-unlock-withdraw-pwd" class="modal fade" data-backdrop="static" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"></button>
				<h4 class="modal-title">解锁资金密码</h4>
			</div>
			<div class="modal-body" style="padding: 30px 20px 15px 20px;">
				<form class="form-horizontal">
					<div class="form-body">
						<div class="form-group">
							<label class="col-md-3 control-label">资金密码</label>
							<div class="col-md-9">
								<input name="withdrawPwd" class="form-control input-inline input-medium" type="password" autocomplete="off">
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-3 control-label">谷歌口令</label>
							<div class="col-md-9">
								<input name="googleCode" class="form-control input-inline input-medium" type="text" autocomplete="off">
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