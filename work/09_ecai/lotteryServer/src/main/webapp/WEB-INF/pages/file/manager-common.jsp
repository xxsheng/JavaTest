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
<div class="funds_list" id="manager-balance">
    <ul>
        <li><span>主账户</span><b data-property="mainBalance" data-command="refreshBalance" title="点击刷新余额">Loading...</b></li>
        <li><span>彩票</span><b data-property="lotteryBalance" data-command="refreshBalance" title="点击刷新余额">Loading...</b></li>
        <li><span>AG</span><b data-property="agBalance" data-command="refreshPlatformBalance" data-platform="4" title="点击查看余额">点击查看</b></li>
        <li><span>BBIN</span><b data-property="bbinBalance" data-command="refreshPlatformBalance" data-platform="5" title="点击查看余额">点击查看</b></li>
        <li><span>PT</span><b data-property="ptBalance" data-command="refreshPlatformBalance" data-platform="11" title="点击查看余额">点击查看</b></li>
        <li><span>QT</span><b data-property="qtBalance" data-command="refreshPlatformBalance" data-platform="14" title="点击查看余额">点击查看</b></li>
        <li><span>体育</span><b data-property="ibcBalance" data-command="refreshPlatformBalance" data-platform="13" title="点击查看余额">点击查看</b></li>
    </ul>
</div>