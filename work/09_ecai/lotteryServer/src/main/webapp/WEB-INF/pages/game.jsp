<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@include file="file/cdn.jsp"%>
    <title>老虎机</title>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link href="<%=cdnDomain%>static/plugins/scroll/jquery.mCustomScrollbar.css" rel="stylesheet">
    <link changeable="true" href="<%=cdnDomain%>static/css/common-<%=currentTheme%>.css?v=${cdnVersion}" rel="stylesheet">
</head>
<body>
<!--*******************************内容******************************-->
<div class="aside-wrapper" id="game-menu">
    <div class="menu-list">
        <div class="aside-menu"><i class="icon iconfont">&#xe68d;</i><span>游戏导航</span></div>
        <dl class="menu">
            <dt>
                <img  src="<%=cdnDomain%>static/images/lh1.png">
                <span>Playtech</span>
                <em><i class="icon iconfont">&#xe515;</i></em>
            </dt>
            <dd>
                <a  class="current" href="#">全部游戏</a>
                <a href="#">热门推荐  </a>
                <a href="lhj_nr.html" target="iframe">老虎机  </a>
                <a href="#">奖池游戏  </a>
                <a href="#">牌桌游戏  </a>
                <a href="#">其他</a>
            </dd>
        </dl>
    </div>
</div>
<div class="section-wrapper">
    <%
        String link = request.getParameter("link");
        String indexSrc;
        if (link == null || "".equals(link)) {
            indexSrc = "gameCenter?platform=11&gameType=1";
        }
        else {
            indexSrc = link;
        }
    %>
    <iframe changeable="true" src="<%=indexSrc%>" frameborder="0" id="iframe" name="iframe" scrolling="0" width="100%" height="100%" class="bg-gray"></iframe>
</div>
<!--*******************************内容******************************-->
<div id="game_template_tpl" style="display: none;"></div>

<script src="<%=cdnDomain%>static/plugins/jquery/jquery.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/scroll/jquery.mCustomScrollbar.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/core.js"></script>
<script src="<%=cdnDomain%>static/js/common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/game.js?v=${cdnVersion}"></script>
</body>
</html>