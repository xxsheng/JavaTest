<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>跳转中</title>
    <script>if (typeof module === 'object') {window.module = module; module = undefined;}</script>
    <style type="text/css">
        html,body {
            width: 100%;
            height: 100%;
            margin: 0;
            padding: 0;
        }
        .content {
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
<body onload="login()">
    <script type="text/javascript">
        function login() {
            window.location.href = '${gameUrl}';
        }
    </script>
    <div class="content">正在进入游戏,请耐心等待</div>
</body>
</html>