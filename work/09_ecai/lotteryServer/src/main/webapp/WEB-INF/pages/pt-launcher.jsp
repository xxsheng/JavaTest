<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>跳转中</title>
    <script>if (typeof module === 'object') {window.module = module; module = undefined;}</script>
    <script type="text/javascript" src="http://cache.download.banner.happypenguin88.com/integrationjs.php"></script>
    <style type="text/css">
        html,body {
            width: 100%;
            height: 100%;
            margin: 0;
            padding: 0;
        }
        .txt {
            width: 300px;
            height: 300px;
            margin: 0px auto 0;
            position: relative; /*脱离文档流*/
            top: 20%; /*偏移*/
            text-align: center;
            font-size: 22px;
            font-family: 微软雅黑;
            font-weight: 600;
        }
    </style>
</head>
<body onload="login()" style="margin:0;padding:0;border:0;">
<script type="text/javascript">
    function playproduction() {
        var web = "http://cache.download.banner.happypenguin88.com/casinoclient.html?language=ZH-CN&game=${gameCode}";
        window.location.href=web;
    }

    iapiSetCallout('Login', calloutLogin);

    function login() {
        startLoading();
        iapiLogin('${username}','${password}',1,'zh-cn');
    }

    function logout(allSessions,realMode) {
        iapiLogout(allSessions, 1);
    }

    function calloutLogin(response) {
        if (loadingInterval) {
            clearInterval(loadingInterval);
        }
        document.getElementById("loading").style.display = "none";

        if (response.errorCode) {
            document.getElementById("txt").innerHTML = "登录失败, 请关闭本页面重新进入(errorCode:"+response.errorCode+"，errorText："+response.errorText+"，playerMessage："+response.playerMessage+")";
        }else {
            document.getElementById("txt").style.display = "none";
            playproduction();
        }
    }
    var loadingInterval;
    function startLoading() {
        loadingInterval = setInterval(function () {
            var val = document.getElementById("loading").innerHTML;
            if (val.length >= 6) {
                document.getElementById("loading").innerHTML = ".";
            }
            else {
                document.getElementById("loading").innerHTML = val + ".";
            }
        }, 1000);
    }
</script>
<span id="txt">正在进入游戏，请耐心等待<span id="loading"></span></span>
</body>
</html>




