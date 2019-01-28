<%@ page language="java" pageEncoding="utf-8"%>
<div class="tx_box" id="manger-top">
    <div class="tx"><img src="<%=cdnDomain%>static/images/cw/portrait.jpg"></div>
    <div class="level">
        <div class="level_1"><span data-property="greeting" class="greeting"></span><span data-property="nickName" class="nickname">Loading...</span></div>
        <div class="level_2">账号返点： <span data-property="code">Loading...</span></div>
        <div class="level_3"><i data-property="securityLevelIcon"></i></div>
        <div class="level_4">账号安全等级：<span data-property="securityLevel">Loading...</span></div>
    </div>
    <div class="rental">
        <span>账户总额</span><b data-property="totalBalance" data-command="refreshBalance" title="点击刷新余额">Loading...</b>
    </div>
    <div class="tx_list">
        <a class="btn" href="fund-recharge"><i class="icon iconfont">&#xe65f;</i>存款</a>
        <a class="btn" href="fund-withdraw"><i class="icon iconfont">&#xe65e;</i>提款</a>
        <a class="btn" href="fund-transfer"><i class="icon iconfont">&#xe613;</i>资金转换</a>
        <a class="btn" href="report-bill-record"><i class="icon iconfont">&#xe65b;</i>资金明细</a>
    </div>
</div>