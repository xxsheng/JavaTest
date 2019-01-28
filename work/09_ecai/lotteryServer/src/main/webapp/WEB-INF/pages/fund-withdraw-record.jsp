<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>提款记录</title>
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
            <div class="tab-title pull-left"><span>财务中心</span></div>
            <ul class="clearfix pull-right">
                <li  onclick="self.window.location='/fund-recharge'" >存款</li>
                <li onclick="self.window.location='/fund-withdraw'">提款</li>
                <li onclick="self.window.location='/fund-transfer'">资金转换</li>
                 <li onclick="self.window.location='/fund-recharge-record'">存款记录</li>
                <li class="current"  >提款记录</li>
                <li onclick="self.window.location='/fund-transfer-record'">转账记录</li>
            </ul>
        </div>
	</div>
        <div class="finance">
            <%@include file="file/manager-common.jsp"%>
            <div class="finance_box">
                <div class="finance_tab"><span class="cur">提款记录</span></div>
                <div class="list_box" id="contentBox"></div>
            </div>
        </div>
    </div>
</div>
</body>
<script src="<%=cdnDomain%>static/plugins/laydate/laydate.js"></script>
<script src="<%=cdnDomain%>static/js/manager.common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/fund.withdraw.record.js?v=${cdnVersion}"></script>
</html>