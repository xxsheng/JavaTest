<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>发件箱</title>
    <%@include file="file/common.jsp"%>
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
						<li onclick="self.window.location='/message-inbox'">收信箱</li>
						<li  class="current">发信箱</li>
						<li onclick="self.window.location='/message-sys'">系统消息</li>
						<li onclick="self.window.location='/message-new'">新信息</li>
					</ul>
				</div>
			</div>
        <div class="finance">
            <div class="finance_box">
                <div class="finance_tab"><span class="cur">发件箱</span></div>
                <div class="list_box" id="contentBox"></div>
            </div>
        </div>
    </div>
</div>
</body>
<script src="<%=cdnDomain%>static/js/proxy.common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/message.outbox.js?v=${cdnVersion}"></script>
</html>