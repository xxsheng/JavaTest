<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>${Lottery.showName}</title>
    <%@include file="file/common.jsp"%>
    <%@include file="file/lottery.files.jsp"%>
    <link changeable="true" href="<%=cdnDomain%>static/css/center-<%=currentTheme%>.css?v=${cdnVersion}" rel="stylesheet">
</head>
<body>

<div class="container">
    <div class="tiger-wrapper content-lottery" id="content">
        <div class="content-left">
            <div class="betting">
            	<div class="betting-box betting-head clearfix">
			        <div class="pull-left box1">
			            <img src="<%=cdnDomain%>static/images/fc3d.png">
			            <p>${Lottery.showName}</p>
			        </div>
			        <div class="pull-left box2" id="current_sell">    
			        	<dl class="clearfix">        
			        		<dt>        
					        	<p>第 <span class="c-red" data-property="currentExpect">Loading...</span> 期</p>
					        	 <p>离截止时间还有</p>       
					        </dt>        
					        <dd>            
					        		<div id="countdown">
					        			<div id="tiles" data-property="currentTime">
					        				<span><label class="hour_show">00</label></span><span><label class="minute_show">00</label></span><span><label class="second_show">00</label></span>
					        			</div>            
					        		</div>       
					        </dd>    
					       </dl>
					</div>
			          <div class="prize" id="openCodeSummary">
                    		<div class="prize_box" id="slideBox">
                    </div>
                </div>
<%-- 			         <div class="history_chars"><a href="lottery-trend?l=${Lottery.shortName}" target="_blank">走势图</a></div> --%>
			    </div>
               
                <div class="pick_box" id="layoutBox">
                    <div class="betting_left" id="betting_left">
                        <div class="pick_tab pick_tab_ssc" id="bettingTab"></div>
                        <div class="pick_cen pick_cen_ssc" id="bettingContent">
                            <div class="elec">
                                <div class="elec_l" id="bettingCondition"></div>
                                <div class="elec_r" id="bettingPrize">
                                    <span class="prize">单注奖金: <i data-property="prize">0</i>&nbsp;&nbsp;元</span>
                                    <span class="banner-btn" id="playRulesTipBtn">
                                        玩法规则
                                        <div class="tips" id="playRulesTipBox">
                                            <i class="tip-arrow"></i>
                                            <div data-property="tips"></div>
                                            <div data-property="example"></div>
                                            <div data-property="help"></div>
                                        </div>
                                    </span>
                                    <div class="lottery">
		                            <span class="bt-text">近期开奖结果</span>
			                            <div class="lottery_xl" style="display:none;">
			                                <div class="lottery-jg">
			                                    <div class="jt-top"></div>
			                                    <div class="history-codes" id="openHistory">
			                                        <ul></ul>
			                                    </div>
			                                </div>
			                            </div>
			                        </div>
			                        <div class="s_his">
			                        	<span class="bt-text"><a href="lottery-trend?l=${Lottery.shortName}" target="_blank">走势图>></a></span>
			                        </div>
                                </div>
                            </div>
                            <div class="betting-area" id="betting-area">
                            </div>
                        </div>
                    </div>
                    <div class="betting_right">
                        <div class="betting-theme">
                            <span class="pull-left"><i class="icon iconfont">&#xe605;</i>近期开奖结果</span>
                            <span class="pull-right"><a href="lottery-trend?l=${Lottery.shortName}" target="_blank">走势图>></a></span>
                        </div>
                        <div class="betting-prize" id="rightOpenHistory"><div class="history-list"></div></div>
                    </div>
                    <div class="tations" id="play-options"></div>
                </div>
                <div class="asides-wrapper order-action" id="rightBanner"></div>
                <div class="bet_list" id="recordBox">
                    <div class="bet_tab" id="recordBoxHeader"><span class="cur" data-val="unOpen">未结算订单</span><span data-val="opened">往期订单</span></div>
                    <div class="tab-list" data-val="unOpen"></div>
                    <div class="tab-list" data-val="opened" style="display: none;"></div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
<script src="<%=cdnDomain%>static/js/lottery.3d.js?v=${cdnVersion}"></script>
</body>
</html>