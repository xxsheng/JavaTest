<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@include file="file/cdn.jsp"%>
    <title>WINonline</title>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link href="<%=cdnDomain%>static/plugins/scroll/jquery.mCustomScrollbar.css" rel="stylesheet">
    <link changeable="true" href="<%=cdnDomain%>static/css/common-<%=currentTheme%>.css?v=${cdnVersion}" rel="stylesheet">
</head>
<body>
<!--*******************************左侧导航******************************-->
<div class="aside-wrapper" id="manager-menu">
    <div class="menu-list">
        <div class="aside-menu"><i class="icon iconfont">&#xe68d;</i><span>菜单导航</span></div>
        <dl class="menu" data-type="account">
            <dt>
                <label><i class="icon iconfont">&#xe626;</i></label>
                <span>会员中心</span>
                <em><i class="icon iconfont nv-right">&#xe515;</i></em>
            </dt>
            <dd>
                <a href="account-manager" target="iframe">账户管理 </a>
                <a href="account-card" target="iframe">银行卡管理</a>
                <a href="account-login" target="iframe">最近登录信息</a>
            </dd>
        </dl>
        <dl class="menu" data-type="fund">
            <dt>
                <label><i class="icon iconfont">&#xe602;</i></label>
                <span>财务中心</span>
                <em><i class="icon iconfont nv-right">&#xe515;</i></em>
            </dt>
            <dd>
                <a href="fund-recharge" target="iframe">存款</a>
                <a href="fund-withdraw" target="iframe">提款</a>
                <a href="fund-transfer" target="iframe">资金转换</a>
                <a href="fund-recharge-record" target="iframe">存款记录</a>
                <a href="fund-withdraw-record"  target="iframe">提款记录</a>
                <a href="fund-transfer-record" target="iframe">转账记录</a>
            </dd>
        </dl>
        <dl class="menu" data-type="report">
            <dt>
                <label><i class="icon iconfont">&#xe65b;</i></label>
                <span>订单报表</span>
                <em><i class="icon iconfont nv-right">&#xe515;</i></em>
            </dt>
            <dd>
                <a href="report-main" target="iframe">主账户报表</a>
                <a href="report-lottery" target="iframe">彩票报表</a>
                <a href="report-game" target="iframe">老虎机/真人/体育报表</a>
                <a href="report-lottery-record" target="iframe">我的彩票记录</a>
                <a href="report-game-record" target="iframe">我的老虎机/真人/体育记录</a>
                <a href="report-chase-record" target="iframe">我的追号记录</a>
                <a href="report-bill-record" target="iframe">我的帐变记录</a>
            </dd>
        </dl>
        <dl class="menu" id="proxy-menu" style="display: none;" data-type="proxy">
            <dt>
                <label><i class="icon iconfont">&#xe6b1</i></label>
                <span>代理管理</span>
                <em><i class="icon iconfont nv-right">&#xe515;</i></em>
            </dt>
            <dd>
                <a href="proxy-index" target="iframe">代理总览</a>
                <a href="proxy-account" target="iframe">开户中心</a>
                <a href="proxy-team" target="iframe">团队管理</a>
                <a href="proxy-online" target="iframe">在线会员</a>
                <a href="proxy-lottery-record" target="iframe">团队彩票记录</a>
                <a href="proxy-game-record" target="iframe">团队老虎机/真人/体育记录</a>
                <a href="proxy-bill-record" target="iframe">团队帐变记录</a>
                <a href="proxy-salary" target="iframe">契约日结</a>
                <a href="proxy-dividend" target="iframe">契约分红</a>
            </dd>
        </dl>
        <dl class="menu" data-type="message">
            <dt>
                <label><i class="icon iconfont">&#xe60d;</i></label>
                <span>消息中心</span>
                <em><i class="icon iconfont nv-right">&#xe515;</i></em>
            </dt>
            <dd>
                <a href="message-inbox" target="iframe">收信箱</a>
                <a href="message-outbox" target="iframe">发信箱</a>
                <a href="message-sys" target="iframe">系统消息</a>
                <a href="message-new" target="iframe">新信息</a>
            </dd>
        </dl>

    </div>
</div>
<!--*******************************左侧导航******************************-->

<!--*******************************内容******************************-->
<div class="section-wrapper">
    <%
        String link = request.getParameter("link");
        String iframeSrc;
        if (link == null || "".equals(link)) {
            iframeSrc = "account-manager";
        }
        else {
            iframeSrc = link;
        }
    %>
    <iframe changeable="true" src="<%=iframeSrc%>" frameborder="0" id="iframe" name="iframe" scrolling="0" width="100%" height="100%" class="bg-gray"></iframe>
</div>
<!--*******************************内容******************************-->

<script src="<%=cdnDomain%>static/plugins/jquery/jquery.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/scroll/jquery.mCustomScrollbar.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/core.js"></script>
<script src="<%=cdnDomain%>static/js/common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/manager.js?v=${cdnVersion}"></script>
</body>
</html>