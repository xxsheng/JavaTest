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
<title>系统配置</title>
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
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css"/>
<link href="${cdnDomain}theme/assets/global/plugins/bootstrap-toastr/toastr.min.css" rel="stylesheet" type="text/css"/>

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
			<h3 class="page-title">系统配置</h3>
			<div class="page-bar">
				<ul class="page-breadcrumb">
					<li>当前位置：站点维护<i class="fa fa-angle-right"></i></li><li>系统配置</li>
				</ul>
			</div>
			<!-- END PAGE HEADER-->
			<!-- BEGIN PAGE CONTENT-->
			<div id="table-lottery-sys-config" class="row">
				<div class="col-md-12">
					<!-- BEGIN PORTLET-->
					<div class="portlet light">
						<div class="portlet-title">
							<div class="caption">
								<i class="fa fa-cogs font-green-sharp"></i>
								<span class="caption-subject font-green-sharp bold uppercase">系统配置</span>
							</div>
						</div>
						<div class="portlet-body form">
							<!-- BEGIN FORM-->
							<form action="javascript:;" class="form-horizontal">
								<div class="form-body">
									<section data-group="SERVICE">
										<h4 class="form-section">客服配置</h4>
											<div class="form-group">
											<label class="control-label col-md-3">客服地址</label>
											<div class="col-md-4">
												<input name="URL" class="form-control form-control-inline" type="text" />
											</div>
											<div class="col-md-2">
												<button data-command="update" type="submit" class="btn green"><i>保存更新</i></button>
											</div>
										</div>
									</section>
									<section data-group="WITHDRAW">
										<h4 class="form-section">取款配置</h4>
										<div class="form-group">
											<label class="control-label col-md-3">每天免手续费次数</label>
											<div class="col-md-4">
												<input name="FREE_TIMES" class="form-control form-control-inline" type="text" />
											</div>
											<div class="col-md-2">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">每天最多取款次数</label>
											<div class="col-md-4">
												<input name="MAX_TIMES" class="form-control form-control-inline" type="text" />
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">每次最低取款金额</label>
											<div class="col-md-4">
												<input name="MIN_AMOUNT" class="form-control form-control-inline" type="text" />
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">每次最高取款金额</label>
											<div class="col-md-4">
												<input name="MAX_AMOUNT" class="form-control form-control-inline" type="text" />
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">取现手续费</label>
											<div class="col-md-4">
												<input name="FEE" class="form-control form-control-inline" type="text" />
												<span class="help-block">超过免费取现次数后的手续费，例如：输入0.01为1%手续费。</span>
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">取现最高手续费</label>
											<div class="col-md-4">
												<input name="MAX_FEE" class="form-control form-control-inline" type="text" />
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">系统充值消费比例</label>
											<div class="col-md-4">
												<input name="SYSTEM_CONSUMPTION_PERCENT" class="form-control form-control-inline" type="text" />
												<span class="help-block">说明：没有达到这个比例，将无法进行转出操作。例如：输入0.5表示百分之五十。</span>
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">上下级转账消费比例</label>
											<div class="col-md-4">
												<input name="TRANSFER_CONSUMPTION_PERCENT" class="form-control form-control-inline" type="text" />
												<span class="help-block">说明：没有达到这个比例，将无法进行转出操作。例如：输入0.5表示百分之五十。</span>
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>

										<div class="form-group">
											<label class="control-label col-md-3">是否允许取现</label>
											<div class="col-md-4">
												<input name="STATUS" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">取现说明</label>
											<div class="col-md-4">
												<input name="SERVICE_MSG" class="form-control form-control-inline" type="text" />
												<span class="help-block">如果关闭了取现，请在这里说明原因。</span>
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">取现时间限制</label>
											<div class="col-md-4">
												<input name="SERVICE_TIME" class="form-control form-control-inline" type="text" />
												<span class="help-block">请用24小时格式，例如：14:00~1:00（下午2点至凌晨1点）。</span>
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
									</section>
									<section data-group="SELF_LOTTERY">
										<h4 class="form-section">私彩配置</h4>
										<div class="form-group">
											<label class="control-label col-md-3">是否开启自主彩杀单</label>
											<div class="col-md-4">
												<input name="CONTROL" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">自主彩杀率</label>
											<div class="col-md-4">
												<input name="PROBABILITY" class="form-control form-control-inline" type="text" />
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
									</section>
									<section data-group="RECHARGE">
										<h4 class="form-section">充值配置</h4>
										<div class="form-group">
											<label class="control-label col-md-3">是否开启充值</label>
											<div class="col-md-4">
												<input name="STATUS" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">
											</div>
										</div>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">充值时间限制</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="SERVICE_TIME" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">请用24小时格式，例如：14:00~1:00（下午2点至凌晨1点）。</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
									</section>
									<%--<section data-group="AUTO_REMIT">--%>
										<%--<h4 class="form-section">自动打款配置</h4>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">是否开启自动打款</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="STATUS" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">每次最低取款金额</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="MIN_AMOUNT" class="form-control form-control-inline" type="text" />--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">每次最高取款金额</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="MAX_AMOUNT" class="form-control form-control-inline" type="text" />--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">商户号(code)</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="MER_CODE" class="form-control form-control-inline" type="text" />--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">商户秘钥(key)</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="MER_KEY" class="form-control form-control-inline" type="text" />--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
									<%--</section>--%>
									<%--<section data-group="LOGIN">--%>
										<%--<h4 class="form-section">登录配置</h4>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">是否允许登录</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="LOGIN_STATUS" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">--%>
											<%--</div>--%>
										<%--</div>--%>
									<%--</section>--%>
									<section data-group="REGIST">
										<h4 class="form-section">注册配置</h4>
										<div class="form-group">
											<label class="control-label col-md-3">是否允许注册</label>
											<div class="col-md-4">
												<input name="REGIST_STATUS" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">
											</div>
										</div>
									</section>
									<section data-group="LOTTERY">
										<%--<h4 class="form-section">投注配置</h4>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">每注投注金额</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="BET_UNIT_MONEY" class="form-control form-control-inline" type="text" />--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">分模式降点</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="FEN_MODEL_DOWN_CODE" class="form-control form-control-inline" type="text" />--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">厘模式降点</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="LI_MODEL_DOWN_CODE" class="form-control form-control-inline" type="text" />--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>

										<h4 class="form-section">中奖排行榜配置</h4>
										<div class="form-group">
											<label class="control-label col-md-3">是否自动更新中奖排行榜</label>
											<div class="col-md-4">
												<input name="AUTO_HIT_RANKING" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">
											</div>
										</div>
										<div class="form-group">
											<label class="control-label col-md-3">中奖排行榜条数</label>
											<div class="col-md-4">
												<input name="HIT_RANKING_SIZE" class="form-control form-control-inline" type="text" />
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
									</section>
									<section data-group="DIVIDEND">
										<h4 class="form-section">契约分红配置</h4>
										<div class="form-group">
											<label class="control-label col-md-3">是否开启契约分红</label>
											<div class="col-md-4">
												<input name="STATUS" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">
											</div>
										</div>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">直属固定比例</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="ZHISHU_SCALE" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">直属号固定分红比例</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">总代浮动比例</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="ZONGDAI_SCALE" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">总代浮动比例</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">总代以下允许设置范围比例</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="ZONGDAI_BELOW_SCALE" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">总代以下范围比例，不应超过总代</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">总代最低活跃人数</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="CONFIG_MIN_VALID_USER" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">只针对总代</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
									</section>
									<section data-group="DAILY_SETTLE">
										<h4 class="form-section">契约日结配置</h4>
										<div class="form-group">
											<label class="control-label col-md-3">是否开启契约日结</label>
											<div class="col-md-4">
												<input name="STATUS" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">
											</div>
										</div>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">主管日结比例(百分比)</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="MAX_SCALE" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">mick开出的账号</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">最低活跃人数</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="MIN_VALID_USER" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">只针对总代及以下</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>

									</section>
									<section data-group="SETTLE">
										<h4 class="form-section">有效会员配置</h4>
										<div class="form-group">
											<label class="control-label col-md-3">有效会员最低消费</label>
											<div class="col-md-4">
												<input name="MIN_BILLING_ORDER" class="form-control form-control-inline" type="text" />
												<span class="help-block">消费达到多少算有效会员</span>
											</div>
											<div class="col-md-3">
												<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>
											</div>
										</div>
									</section>
									<section data-group="GAME_DIVIDEND">
										<h4 class="form-section">老虎机真人体育分红配置</h4>
										<div class="form-group">
											<label class="control-label col-md-3">是否开启分红</label>
											<div class="col-md-4">
												<input name="STATUS" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">
											</div>
										</div>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">游戏分红比例</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="ZONGDAI_SCALE" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">游戏亏损分红比例</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
									</section>
									<%--<section data-group="VIP">--%>
										<%--<h4 class="form-section">VIP配置</h4>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">是否开启积分计算</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="CAL_JIFEN" type="checkbox" class="make-switch" data-on-text="是" data-off-text="否">--%>
											<%--</div>--%>
										<%--</div>--%>
										
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">VIP生日礼金</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="BIRTHDAY_GIFTS" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">从级别低到高排列，中间用英文逗号分隔。</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">VIP每月免费筹码</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="FREE_CHIPS" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">从级别低到高排列，中间用英文逗号分隔。</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">VIP晋级礼金</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="UPGRADE_GIFTS" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">从级别低到高排列，中间用英文逗号分隔。</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">VIP每日提款上限</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="WITHDRAW" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">普通,青铜,紫晶,白银,黄金,钻石,至尊;中间用英文逗号分隔。</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">积分兑现金汇率</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="EXCHANGE_RATE" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">积分兑现金汇率。</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">单次最高倍数</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="MAX_EXCHANGE_MULTIPLE" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">单次最高兑换倍数，1000倍=1000元。</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">每日最多兑换次数</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="MAX_EXCHANGE_TIMES" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">每日最多兑换次数。</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
									<%--</section>--%>
									<%--<section data-group="DOMAIN">--%>
										<%--<h4 class="form-section">域名配置</h4>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">域名列表</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="URLS" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">多个用英文逗号分隔。</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
									<%--</section>--%>
									<%--<section data-group="CDN">--%>
										<%--<h4 class="form-section">CDN配置</h4>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">多CDN域名</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="DOMAIN" class="form-control form-control-inline" type="text"/>--%>
												<%--<span class="help-block">如http://static.rzkxy.com&nbsp;&nbsp;配置/表示不加速，多个英文逗号分割，非技术请不要使用</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
										<%--<div class="form-group">--%>
											<%--<label class="control-label col-md-3">CDN版本号</label>--%>
											<%--<div class="col-md-4">--%>
												<%--<input name="VERSION" class="form-control form-control-inline" type="text" />--%>
												<%--<span class="help-block">每次修改静态文件都需要更新版本号，如20160924001</span>--%>
											<%--</div>--%>
											<%--<div class="col-md-3">--%>
												<%--<button data-command="update" type="submit" class="btn green"><i class="fa fa-save"></i> 保存修改</button>--%>
											<%--</div>--%>
										<%--</div>--%>
									<%--</section>--%>
								</div>
							</form>
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
<script src="${cdnDomain}theme/assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/bootbox/bootbox.min.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/plugins/jquery.easyweb/jquery.easyweb.js" type="text/javascript"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<script src="${cdnDomain}theme/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/global/scripts/md5.js" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/admin/layout/scripts/layout.js" type="text/javascript"></script>

<script src="${cdnDomain}theme/assets/custom/scripts/global.js?v=${cdnVersion}" type="text/javascript"></script>
<script src="${cdnDomain}theme/assets/custom/scripts/lottery-sys-config.js?v=${cdnVersion}" type="text/javascript"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
jQuery(document).ready(function() {
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	// init data
	LotterySysConfig.init();
});
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>