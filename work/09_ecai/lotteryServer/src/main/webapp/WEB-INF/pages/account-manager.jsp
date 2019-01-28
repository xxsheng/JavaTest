<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>账户管理</title>
    <%@include file="file/common.jsp"%>
    <link href="<%=cdnDomain%>static/css/manager-<%=currentTheme%>.css?v=${cdnVersion}" rel="stylesheet">
</head>

<body>
<div class="container" id="content">
    <!--自定义-->
    <div class="con_box">
<!--         <div class="top_box"> -->
<!--             <div class="newest_notice text-elip" id="newestNotice"></div> -->
<!--         </div> -->
        <div class="finance">
            <%@include file="file/manager-common.jsp"%>
            <div class="finance_box">
                <div class="finance_tab">
                 <span class="cur">账户安全</span>
                <a href="account-card"  target="iframe"> <span >银行卡管理</span> </a>
                <a href="account-login" target="iframe" ><span >最近登录信息</span> </a>
                </div>
                <div class="security" id="contentBox">
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script src="<%=cdnDomain%>static/js/manager.common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/account.manager.js?v=${cdnVersion}"></script>
</html>