<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>契约日结</title>
    <%@include file="file/common.jsp"%>
    <link rel="stylesheet" href="<%=cdnDomain%>static/plugins/noUiSlider/noUiSlider.css">
    <link href="<%=cdnDomain%>static/css/manager-<%=currentTheme%>.css?v=${cdnVersion}" rel="stylesheet">
</head>

<body>
<div class="container" id="content">
    <div class="con_box">
<!--         <div class="top_box"> -->
<!--             <div class="newest_notice text-elip" id="newestNotice"></div> -->
<!--         </div> -->
         <div class="form">
			<div class="tab clearfix">
				<div class="tab-title pull-left">
					<span>代理管理</span>
				</div>
				<ul class="clearfix pull-right">
					<li onclick="self.window.location='/proxy-index'" >代理总览</li>
					<li onclick="self.window.location='/proxy-account'" >开户中心</li>
					<li onclick="self.window.location='/proxy-team'">团队管理</li>
					<li onclick="self.window.location='/proxy-online'">在线会员</li>
					<li onclick="self.window.location='/proxy-lottery-record'">团队彩票记录</li>
					<li onclick="self.window.location='/proxy-game-record'">团队游戏记录</li>
					<li onclick="self.window.location='/proxy-bill-record'">团队帐变记录</li>
					<li  class="current"  name = "proxy-salary"   style = " display: none;"  onclick="self.window.location='/proxy-salary'">契约日结</li>
					<li  name = "proxy-dividend"  style = " display: none;" onclick="self.window.location='/proxy-dividend'">契约分红</li>
				</ul>
			</div>
		</div>
        <div class="finance">
            <%@include file="file/proxy-common.jsp"%>
            <div class="finance_box agency">
                <div class="finance_tab" id="contentTab"><span class="cur" data-type="bill" data-command="changeTab">日结账单</span><span data-type="manager" data-command="changeTab">日结管理</span><span data-type="request" data-command="changeTab">发起契约日结</span><i class="text-elip" id="userLevels"></i></div>
                <div id="contentBox"></div>
            </div>
        </div>
    </div>
</div>
</body>
<script src="<%=cdnDomain%>static/plugins/noUiSlider/noUiSlider.js"></script>
<script src="<%=cdnDomain%>static/plugins/laydate/laydate.js"></script>
<script src="<%=cdnDomain%>static/js/single.choose.member.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/proxy.common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/proxy.salary.js?v=${cdnVersion}"></script>
</html>