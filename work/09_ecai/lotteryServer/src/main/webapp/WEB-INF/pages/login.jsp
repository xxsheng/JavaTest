<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@include file="file/cdn.jsp"%>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>用户登录</title>
    <link rel="shortcut icon" href="<%=cdnDomain%>static/favicon.ico" type="image/x-icon">
    <link href="<%=cdnDomain%>static/css/common-white.css?v=${cdnVersion}" rel="stylesheet">
    <link href="<%=cdnDomain%>static/css/login.css?v=${cdnVersion}" rel="stylesheet">
</head>
<body class="login-bg">
	 <div class="login_top">
        <div class="login_logo"><img src="${cdnDomain}static/images/logo-white.png?v=${cdnVersion}"></div>
        <!-- 20180926 页面修改 -->
        <!-- <a href="javascript:;" data-command="kefu">联系客服</a> -->
	    <a href="javascript:;"  data-command="showAppDownload" id="showAppDownload">手机客户端</a>
    </div>
<div class="login">
    <div class="login-box" id="loginBox" > 
        <h1>用户登录</h1>
        <div class="login-form">
            <div class="login-form-inline">
                <div class="form-control">
                    <input type="text" class="txt" id="username" autofocus autocomplete="off" maxlength="12" placeholder="用户名" autofocus>
                </div>
            </div>
            <div class="login-form-inline">
                <div class="form-control">
                    <input type="password" class="txt" id="password" autocomplete="off" maxlength="50" placeholder="密码">
                </div>
            </div>
            <div class="login-form-inline code-inline">
                <div class="form-control">
                    <input type="text" class="txt force-digit" id="checkCode" autocomplete="off" maxlength="4" placeholder="验证码">
                </div>
                <img src="/LoginCode" class="imgCode" id="imgCode">
            </div>
        </div>
        <!-- 20180926 页面修改 -->
        <div class="form-info"><!-- <span id="demouser" class="demouser">立即试玩</span> --> 忘记密码？点击这里联系  <!-- <span data-command="kefu" class="kefu">在线客服</span> --> </div>
        <div class="form-action"><button class="btn-login" id="loginBtn">登陆</button></div>
    </div>
   <%--  <div class="login-info">
        <div class="logo"><img src="<%=cdnDomain%>static/images/logo-black.png?v=${cdnVersion}"></div>
        <h6>威霆娱乐手机客户端</h6>
        <P>支持安卓、苹果移动设备，走到哪玩到哪。</P>
        <P><img src="<%=cdnDomain%>static/images/ewm.png?v=${cdnVersion}" class="ewm"></P>
        <p><i class="icon iconfont">&#xf01ff;</i>下载PC客户端 <i style = "margin-left: 20px"class="icon iconfont">&#xf01ff;</i><span id="demouser" >立即试玩</span></p>
    </div> --%>
   
</div>
<div id="login-bot">
    <div class="botom_login">
        <p>为了更好的操作体验首选Google Chrome、Firefox  浏览器，点击可立即下载。</p>
        <div class="llp">
					<a class="b-b-chrome"  href="http://rj.baidu.com/soft/detail/14744.html?ald" title="Chrome快速、简单且安全的浏览器" target="_blank"><img src="<%=cdnDomain%>static/images/Chrome.png?v=${cdnVersion}" ></a>
					<a class="b-b-firefox" href="http://rj.baidu.com/soft/detail/11843.html?ald" title="火狐屡获大奖的开源浏览器" target="_blank"><img src="<%=cdnDomain%>static/images/Firefox.png?v=${cdnVersion}" ></a>
					<a class="b-b-safari" href="http://rj.baidu.com/soft/detail/12966.html?ald" title="Safari提供极致愉悦的网络体验方式" target="_blank"><img src="<%=cdnDomain%>static/images/Safari.png?v=${cdnVersion}" > </a>
					<a class="b-b-ie" href="http://rj.baidu.com/soft/detail/14917.html?ald" title="IE全面升级的IE浏览器" target="_blank"><img src="<%=cdnDomain%>static/images/IE.png?v=${cdnVersion}" ></a>
			</ul>
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
<script src="<%=cdnDomain%>static/plugins/jquery/jquery.cookie.js"></script>
<script src="<%=cdnDomain%>static/plugins/sweet/core.js"></script>
<script src="<%=cdnDomain%>static/plugins/sweet/sweet.all.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/layer/layer.js"></script>
<script src="<%=cdnDomain%>static/plugins/core.js"></script>
<%--<script src="<%=cdnDomain%>static/js/browservalidate.js?v=${cdnVersion}"></script>--%>
<script src="<%=cdnDomain%>static/js/common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/login.js?v=${cdnVersion}"></script>
</body>
</html>