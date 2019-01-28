<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
<title>彩票报表</title>
<%@include file="file/common.jsp"%>
<link href="<%=cdnDomain%>static/css/manager-<%=currentTheme%>.css?v=${cdnVersion}"
	rel="stylesheet">
</head>

<body>
	<div class="container" id="content">
		<div class="con_box">
<!-- 			<div class="top_box"> -->
<!-- 				<div class="newest_notice text-elip" id="newestNotice"></div> -->
<!-- 			</div> -->
			<div class="form">
				<div class="tab clearfix">
					<div class="tab-title pull-left">
						<span>订单报表</span>
					</div>
					<ul class="clearfix pull-right">
						<li onclick="self.window.location='/report-main'">主帐号报表</li>
						<li class="current">彩票报表</li>
						<li onclick="self.window.location='/report-game'">游戏报表</li>
						<li onclick="self.window.location='/report-lottery-record'">彩票记录</li>
						<li onclick="self.window.location='/report-game-record'">游戏记录</li>
						<li onclick="self.window.location='/report-chase-record'">追号记录</li>
						<li onclick="self.window.location='/report-bill-record'">帐变记录</li>
					</ul>
				</div>
			</div>
			<div class="finance">
				<%@include file="file/manager-common.jsp"%>
				<div class="finance_box">
					<div class="finance_tab">
						<span class="cur">彩票报表</span><i class="text-elip" id="userLevels"></i>
					</div>
					<div class="list_box" id="contentBox"></div>
				</div>
			</div>
		</div>
	</div>
</body>
<script src="<%=cdnDomain%>static/plugins/laydate/laydate.js"></script>
<script src="<%=cdnDomain%>static/js/manager.common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/report.lottery.js?v=${cdnVersion}"></script>
</html>