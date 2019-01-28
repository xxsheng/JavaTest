<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@include file="file/cdn.jsp"%>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>用户注册</title>
    <link rel="shortcut icon" href="<%=cdnDomain%>static/favicon.ico" type="image/x-icon">
    <link href="<%=cdnDomain%>static/css/common-white.css?v=${cdnVersion}" rel="stylesheet">
    <link href="<%=cdnDomain%>static/css/login.css?v=${cdnVersion}" rel="stylesheet">
</head>
<body class="login-bg">
<div class="login_top">
        <div class="login_logo"><img src="${cdnDomain}static/images/logo-white.png?v=${cdnVersion}"></div>
        <a href="javascript:;" data-command="kefu">联系客服</a>
	    <a href="javascript:;"  data-command="showAppDownload" id="showAppDownload">手机客户端</a>
    </div>
<div class="login register">
    <div class="login-box" id="loginBox">
        <h1>用户注册</h1>
        <div class="login-form">
            <div class="login-form-inline">
                <div class="form-control">
                    <input type="text" class="txt" id="username" autofocus autocomplete="off" maxlength="12" placeholder="用户名" autofocus>
                </div>
            </div>
            <div class="login-form-inline">
                <div class="form-control">
                    <input type="password" class="txt" id="password" autocomplete="off" maxlength="20" placeholder="设置登录密码">
                </div>
            </div>
            <div class="login-form-inline">
                <div class="form-control">
                    <input type="password" class="txt" id="rePassword" autocomplete="off" maxlength="20" placeholder="请输入确认登录密码">
                </div>
            </div>
            <div class="login-form-inline code-inline">
                <div class="form-control"><input type="text" class="txt force-digit" id="checkCode" autocomplete="off" maxlength="4" data-default="" placeholder="验证码"></div>
                <img src="/RegistCode" class="imgCode" id="imgCode">
            </div>
        </div>
        <div class="form-info">
            <span class="ck cur" id="agreeTerms"></span>
            我已满合法BC年龄﹐且同意各项 <span class="nor">开户条约</span> 。</div>
        <div class="form-action"><button class="btn-login" id="registerBtn">注册</button></div>
    </div>
<%--     <div class="login-info">
        <div class="logo"><img src="<%=cdnDomain%>static/images/logo-black.png?v=${cdnVersion}"></div>
        <h6>威霆娱乐手机客户端</h6>
        <P>支持安卓、苹果移动设备，走到哪玩到哪。</P>
        <P><img src="<%=cdnDomain%>static/images/ewm.png?v=${cdnVersion}" class="ewm"></P>
        <p><i class="icon iconfont">&#xf01ff;</i>下载PC客户端</p>
    </div> --%>
</div>

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