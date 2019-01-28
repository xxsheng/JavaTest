<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@include file="file/cdn.jsp"%>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>WINonline</title>
    <link rel="shortcut icon" href="<%=cdnDomain%>static/favicon.ico" type="image/x-icon">
    <link href="<%=cdnDomain%>static/plugins/scroll/jquery.mCustomScrollbar.css" rel="stylesheet">
    <link changeable="true" href="<%=cdnDomain%>static/css/common-<%=currentTheme%>.css?v=${cdnVersion}" rel="stylesheet">
    <link changeable="true" href="<%=cdnDomain%>static/css/main-<%=currentTheme%>.css?v=${cdnVersion}" rel="stylesheet">
</head>
<body>
<!--*******************************头部******************************-->
<section>
    <div class="header clear">
        <div class="header-left">
            <div class="logo"><a href="index" target="mainiframe"><img changeable="true" src="<%=cdnDomain%>static/images/logo-<%=currentTheme%>.png" width="160" height="auto"></a></div>
        </div>
        <div class="nav" id="nav">
            <ul class="clear">
                <div class="color_xl" >
                    <div class="xl_1 titletxt">
                        WINonline，彩票投注首选品牌
                    </div>
                    <div class="jt-top"></div>
                    <div class="xl_1">
                        <div class="xl_black"></div>
                        <div class="cl_1" data-command="changeTheme" data-val="black">深灰</div>
                    </div>
                    <div class="xl_1">
                        <div class="xl_white"></div>
                        <div class="cl_1" data-command="changeTheme" data-val="white">浅灰</div>
                    </div>
                </div>
                <!--                 <li><a href="index?link=lottery-cqssc" target="mainiframe">彩票投注</a></li>
                                <li><a href="real" target="mainiframe">真人视讯</a></li>
                                <li><a href="javascript:;" target="mainiframe" data-command="disabled">幸运骰子</a></li>
                                <li><a href="javascript:;" target="mainiframe" data-command="disabled">老虎机</a></li>
                                <li><a href="javascript:;" target="mainiframe" data-command="disabled">体育</a></li>
                                <li><a href="javascript:;" id="promotion">优惠活动</a></li>
                                <li><a href="javascript:;" id="appdownload">手机投注</a></li>-->
            </ul>
            <!--手机投注-->
            <div class="phone_tc"></div>
        </div>
        <div class="header-right" id="header-right">
            <div class="news">
                <a href="index"  target="mainiframe" class="title">
                    <i class="icon iconfont">&#xe632;</i>
                    <p>首页</p>
                </a>
            </div>
            <div class="news">
                <a href="javascript:;" id="promotion" class="title">
                    <i class="icon iconfont">&#xe610;</i>
                    <p>优惠活动</p>
                </a>
            </div>
            <div class="news">
                <a href="account-manager" target="iframe" class="title">
                    <i class="icon iconfont">&#xe626;</i>
                    <p>会员中心</p>
                </a>
            </div>

            <div class="news">
                <a href="fund-recharge" target="iframe" class="title">
                    <i class="icon iconfont">&#xe613;</i>
                    <p>财务中心</p>
                </a>
            </div>
            <div class="news">
                <a href="report-lottery"  target="iframe" class="title">
                    <i class="icon iconfont">&#xe6c5;</i>
                    <p>订单报表</p>
                </a>
            </div>
            <div class="news" name = "proxy" style="border-right:0;display: none;">
                <a href="proxy-index"  target="iframe"  class="title">
                    <i class="icon iconfont">&#xe6b1;</i>
                    <p>代理管理</p>
                </a>
            </div>
            

            <div class="news">
                <a href="message-inbox"  target="iframe"  class="title">
                    <i class="icon iconfont"></i>
                    <p>我的消息</p>
                </a>
            </div>

            <div class="news">
                <a href="javascript:;" class="title">
                    <em class="unread" style="display: none;"></em>
                    <i class="icon iconfont">&#xe60e;</i>
                    <p>公告</p>
                </a>
                <!--公告-->
                <div class="notice_xl" id="notice_banner"></div>
                <!--公告-->
            </div>
            <div class="news" data-command="kefu">
                <a href="#" class="title">
                    <i class="icon iconfont">&#xe612;</i>
                    <p >在线客服</p>
                </a>
            </div>
            <div class="info">
                <div class="pic"><a href="message-inbox" target="iframe"><img src="<%=cdnDomain%>static/images/nav10.png" ></a><a class="msg-count" id="msgCount" href="message-inbox" target="iframe">..</a></div>
                <div class="box">
                    <div class="mb"><span class="nickname text-elip" data-property="nickname">Loading...</span></div>
                    <div class="detail">余额详情<i class="icon iconfont">&#xe61b;</i>
                        <!--余额详情-->
                        <div class="balance_xl" id="topBalance"></div>
                        <!--余额详情-->
                    </div>
                    <span class="link-white logout" data-command="logout">[退出]</span>
                </div>
            </div>

        </div>
    </div>
</section>
<!--*******************************头部******************************-->
<!--*******************************内容******************************-->
<div class="content-wrapper">
    <iframe changeable="true" src="index" frameborder="0" id="mainiframe" name="mainiframe" scrolling="0" width="100%" height="100%"></iframe>
</div>
<!--*******************************内容******************************-->
<!--优惠活动-->
<div class="index-model" id="promotion-box"></div>
<div class="index-model" id="aboutus-box"></div>
<div class="index-model" id="question-box"></div>
<div class="index-dialog" id="screenMask"></div>
<div id="main_template_tpl" style="display: none;"></div>

<script src="<%=cdnDomain%>static/plugins/jquery/jquery.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/jquery/jquery.cookie.js"></script>
<script src="<%=cdnDomain%>static/plugins/scroll/jquery.mCustomScrollbar.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/sweet/core.js"></script>
<script src="<%=cdnDomain%>static/plugins/sweet/sweet.all.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/core.js"></script>
<script src="<%=cdnDomain%>static/plugins/award/awardRotate.js"></script>
<script src="<%=cdnDomain%>static/js/common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/main.js?v=${cdnVersion}"></script>
</body>
</html>