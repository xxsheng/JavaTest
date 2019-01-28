<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@include file="file/cdn.jsp"%>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>WINonline</title>
    <link rel="shortcut icon" href="<%=cdnDomain%>static/favicon.ico" type="image/x-icon">
    <link href="<%=cdnDomain%>static/css/common-white.css?v=${cdnVersion}" rel="stylesheet">
    <link href="<%=cdnDomain%>static/css/login.css?v=${cdnVersion}" rel="stylesheet">
    <style type="text/css">
    form#login_form dl dt {
	    margin-bottom: 15px;
	}
    </style>
</head>
<body class="login-bg">
	<div class="container" style="background-color:transparent;min-width: auto;">
	    <div class="logo">
	        <img src="${cdnDomain}static/images/logo-white.png?v=${cdnVersion}" width="250" alt="WINonline">
	    </div>
	    <!--end logo-->
	    <div class="info-wrapper">
	        <div class="tip-wrapper">
	            <!--轮播-->
	            <div class="banner">
	                <div class="banner-btn" style="display: none;">
	                    <a href="javascript:;" class="prevBtn">
	                    </a>
	                    <a href="javascript:;" class="nextBtn">
	                    </a>
	                </div>
	                <ul class="banner-img" style="width: 603px; left: 0px;">
	                    <li>
	                        <a href="javascript:;" xhr="#sale" class="My_models_Default">
	                            <img src="<%=cdnDomain%>static/images/62_TT.jpg"
	                            alt="感恩大回赠 全民乐翻天">
	                        </a>
	                    </li>
	                </ul>
	                <ul class="banner-circle" style="margin-left: -20px;">
	                    <li class="selected" href="#">
	                        <a>
	                        </a>
	                    </li>
	                </ul>
	            </div>
	        </div>
	        <!--end tips-wrapper-->
	        <form class="login_form" id="login_form" style="margin-top: 0px;">
	            <dl>
	                <dt class="w100 positionre">
	                    <span class="i-user">
	                    </span>
	                    <input id="username" name="login" class="required w70" type="text" autocomplete="off"
	                    autocapitalize="off" placeholder="用户名">
	                </dt>
	                <dt class="w100 positionre">
	                    <span class="i-lock">
	                    </span>
	                    <input name="pass" id="password" class="required w70" type="password" autocomplete="off"
	                    placeholder="设置登录密码">
	                </dt>
	                <dt class="w100 positionre">
	                    <span class="i-lock">
	                    </span>
	                    <input name="pass" id="rePassword" class="required w70" type="password" autocomplete="off"
	                    placeholder="请输入确认登录密码">
	                </dt>
	                <dt class="w100 positionre" style="margin-bottom: 0px;">
	                    <span class="i-lock">
	                    </span>
	                    <input name="authnum" type="text" id="checkCode" class="required number w40" size="10" maxlength="5" placeholder="验证码">
	                    <span class="w30 c-code">
	                        &nbsp;
	                        <img id="imgCode" src="/RegistCode" width="55">
	                    </span>
	                </dt>
	            </dl>
	            <div class="form-info">
            <span class="ck cur" id="agreeTerms"></span>
            我已满合法BC年龄﹐且同意各项 <span class="nor">开户条约</span> 。</div>
	            <div class="btn-wrapper login-btn-wrapper text-center">
	                <button type="button" id="registerBtn" class="submit-btn">注 册</button>
	            </div>
	        </form>
	        <p id="f-test" class="bbn">
	            <a data-command="shiwan" href="javascript:;" style="color: #da251d;">用户注册</a>
	        </p>
	        <p id="f-service" class="webonlineservice bbn">
	            <a data-command="kefu" href="javascript:;">
	                在线客服
	            </a>
	        </p>
	        <span class="customer_bg">
	            <a data-command="showAppDownload" id="showAppDownload" href="javascript:;" class="client">
	                <img src="<%=cdnDomain%>static/images/phone.png"
	                height="66" width="66" alt="">
	            </a>
	        </span>
	    </div>
	    <!--end info-wrapper-->
	    <div class="footer">
	        <div class="browsers">
	            <a target="_blank" href="http://rj.baidu.com/soft/detail/14744.html?ald">
	            </a>
	            <a target="_blank" class="x2" href="http://rj.baidu.com/soft/detail/11843.html?ald">
	            </a>
	            <a target="_blank" class="x3" href="http://rj.baidu.com/soft/detail/12966.html?ald">
	            </a>
	            <a target="_blank" class="x4" href="http://rj.baidu.com/soft/detail/14917.html?ald">
	            </a>
	        </div>
	        <p>
	            为了获得更好的操作体验，建议使用Google Chrome、Firefox 或 IE10 浏览器，点击图标立即下载。
	        </p>
	    </div>
	</div>
<script type="text/template" id="validate_card_tpl">
    <form novalidate onsubmit="return false;">
        <div class="popup-window">
            <div class="popup-title">系统检测到您本次登录地点与上次不一致，请完成以下验证</div>
            <div class="popup-group-static"><label class="label">上次登录：</label><span class="static-text text-elip" title="<#=lastAddress#>"><#=lastAddress#></span></div>
            <div class="popup-group-static"><label class="label">本次登录：</label><span class="static-text text-elip" title="<#=thisAddress#>"><#=thisAddress#></span></div>
            <div class="popup-group-static"><label class="label">银行卡号：</label><span class="static-text text-elip" title="<#=cardId#>"><#=cardId#></span></div>
            <div class="popup-group"><label class="label">持卡姓名：</label><input type="text" class="input-text" maxlength="30" name="cardName" id="cardName" autofocus autocomplete="off" placeholder="请输入持卡人姓名"></div>
        </div>
    </form>
</script>


<script src="<%=cdnDomain%>static/plugins/jquery/jquery.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/sweet/core.js"></script>
<script src="<%=cdnDomain%>static/plugins/sweet/sweet.all.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/layer/layer.js"></script>
<script src="<%=cdnDomain%>static/plugins/core.js"></script>
<%--<script src="<%=cdnDomain%>static/js/browservalidate.js?v=${cdnVersion}"></script>--%>
<script src="<%=cdnDomain%>static/js/common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/register.js?v=${cdnVersion}"></script>
</body>
</html>