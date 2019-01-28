<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-CN" >
<head>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>发生错误</title>
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
<body>
	<div class="content">错误代码：${code}，错误信息：${message}</div>
</body>
</html>