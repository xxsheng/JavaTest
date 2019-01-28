<%@ page language="java" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>资金转换</title>
    <%@include file="file/common.jsp" %>
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
            <%@include file="file/manager-common.jsp" %>
            <div class="finance_box">

                <div class="finance_tab"><span class="cur">转账中心</span></div>
                <div class="finance_text">
                    <div class="text_cc">
                        <span class="label">转入账户：</span>
                        <input name="userName" type="text" class="text" placeholder="请输入转账的用户名">
                        <input id="userId" type="hidden">
                    </div>
                    <div class="text_cc"><span class="label">转入金额：</span>
                        <input id="amount" type="text" class="text" utofocus autocomplete="off" data-min="1" placeholder="请输入转入金额">
                    </div>
                    <div class="text_cc"><span class="label">资金密码：</span>
                        <input id="withdrawPwd" type="password" class="text" utofocus autocomplete="off" data-min="1" placeholder="请输入资金密码">
                    </div>
                    <div class="fina_btn"><input type="button" onclick="submitTrans()" class="btn-gray-big" value="提交" data-command="submit">
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
</body>
<script src="<%=cdnDomain%>static/js/manager.common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/fund.transfer.js?v=${cdnVersion}"></script>
</html>