<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@include file="file/cdn.jsp"%>
    <title>欧尚娱乐</title>
    <meta charset="UTF-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link href="<%=cdnDomain%>static/plugins/scroll/jquery.mCustomScrollbar.css" rel="stylesheet">
    <link changeable="true" href="<%=cdnDomain%>static/css/common-<%=currentTheme%>.css?v=${cdnVersion}" rel="stylesheet">
    <link changeable="true" href="<%=cdnDomain%>static/css/center-<%=currentTheme%>.css?v=${cdnVersion}" rel="stylesheet">
</head>
<body class="bg-gray">

<div class="container no-atuo">
    <div class="tiger-wrapper" id="content">
        <div class="content-left">
            <!--内容-->
            <div class="index_box">
                <!-- <div class="top_box">
                    <div class="newest_notice text-elip" id="newestNotice"></div>
                </div>  -->
                <div class="center_r_1 center_r_11">
	                <div class="banner" >
	                    <div class="slideBox" id="slideBox">
	                        <div class="bd">
	                            <ul>
	                                <li>
	                                <img src="<%=cdnDomain%>static/images/index/banner1.jpg">
	                                <!-- <div class="box">
	                                    <div class="pull-left"><h2>欢迎加入</h2><p>WELCOME TO ECAIZAIXIAN</p></div>
	                                    <div class="pull-left"><h1>WINonline</h1></div>
	                                    <div class="pull-left"><span>在<br>线</span></div>
	                                    <div class="clearfix"></div>
	                                    <h4>投入梦想 精彩一生</h4>
	                                    <button class="btn-red" onclick="self.window.location='lottery-cqssc'">开始游戏</button>
	                                </div> -->
	                                </li>
	                                 <li><img src="<%=cdnDomain%>static/images/index/banner2.jpg"></li>
	                                 <li><img src="<%=cdnDomain%>static/images/index/banner3.jpg"></li>
	                            </ul>
	                        </div>
	                        <span class="prev pr_left user-select-none"><i class="icon iconfont">&#xe501;</i> </span>
	                        <span class="next pr_right user-select-none"><i class="icon iconfont">&#xe503;</i></span>
	                    </div>
	                </div>
	                <!-- <div class="index_list" id="indexList"></div> -->
	                
						<!--20180926  原中奖信息位置删除 center.jsp -->
	            </div>
	            
	            <div class="center_r_1 center_m11">
		            <!-- <div class="notice" id="noticeBox">
		            	<div class="notice_title_row">
			                <div class="notice_title"><i class="icon iconfont">&#xe60e;</i>公告栏</div>
				             <div class="notice_title2"  id="showAllNotice">更多</div>
			             </div>
		                <div class="noticesol">
		                	<ul id="noticeList"></ul>
		                </div>
	                </div> -->
	                <div class="hot_box">
	                    <div class="hot_cen">
	                        <div class="hot_top">
                                <div class="title">
                                	<img class="cont" src="<%=cdnDomain%>static/images/index/hot.png">
                                	<span class="cont" >热门游戏</span>
                                </div>
                            </div>
	                        <div class="hot" id="hotBox"></div>
	                    </div>
	                    <!-- <div class="treatment">
	                        <div class="treatment_top">优惠活动</div>
	                        <div class="treatment_box" id="promotionBox"></div>
	                    </div> -->
	                </div>
	                
	                <div class="notice" id="noticeBox">
			            	<div class="notice_title_row">
				                <div class="notice_title"><i class="icon iconfont">&#xe60e;</i>公告栏</div>
<!-- 					             <div class="notice_title2"  id="showAllNotice">更多</div> -->
				             </div>
			                <div class="noticesol">
			                	<ul class="noticeList">
			                	</ul>
			                </div>
		                </div>
		                
		                <!--20180926 修正开始 幸运大奖部分 center.jsp -->
		                <div class="center_c_1" >
		                <div class="luck" id="luckBox">
			                <div class="luck_title"><i class="icon iconfont">&#xf02e6;</i>幸运大奖玩家</div>
			                <div class="rankingWrap">
			                	<ul id="hitRanking" style="top:0px"></ul>
			                </div>
		                </div>
		                
		            </div>
		                <!--20180926 修正结束 幸运大奖部分 center.jsp  -->
		                
		                <!--20180926 修正开始 异地登录 隐藏 center.jsp  -->
	                	<div class="recent" id="recentBox" style="display: none;">
			                <div class="recent_top">
			                    <div class="re_1"><i class="icon iconfont">&#xe606;</i>最近登录地址</div>
			                    <div class="re_2">异地登录验证<i class="c_1" id="loginValidateBtn"></i></div>
			                </div>
			                <div class="recent_box">
			                    <div class="recent_title"><span>日期</span><span>地址</span></div>
			                    <ul id="recentLogins"></ul>
			                </div>
			            </div>
			            <!--20180926 修正结束 异地登录 隐藏 center.jsp  -->
			            
	                </div>
	                <!-- 20180926修正 开始 页脚图片部分 center.jsp -->
	            <div class="image" id="image" ></div>
	            	<!-- 20180926修正 结束 页脚图片部分 center.jsp -->
                <div class="footer" id="footer"></div>
            </div>
            <!--内容-->
            
        </div>
    </div>
    <%--<div class="asides-wrapper" id="rightBanner">
        <div class="dside"></div>
    </div>--%>
</div>
<div id="center_template_tpl" style="display: none;"></div>
<script src="<%=cdnDomain%>static/plugins/jquery/jquery.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/jquery/jquery.SuperSlide.2.1.2.js"></script>
<script src="<%=cdnDomain%>static/plugins/scroll/jquery.mCustomScrollbar.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/sweet/core.js"></script>
<script src="<%=cdnDomain%>static/plugins/sweet/sweet.all.min.js"></script>
<script src="<%=cdnDomain%>static/plugins/layer/layer.js"></script>
<script src="<%=cdnDomain%>static/plugins/core.js"></script>
<script src="<%=cdnDomain%>static/js/common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/center.js?v=${cdnVersion}"></script>
</body>
</html>