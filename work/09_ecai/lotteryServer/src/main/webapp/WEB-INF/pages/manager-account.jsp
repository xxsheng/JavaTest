<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>我的账户</title>
    <%@include file="file/common.jsp"%>
    <link changeable="true" rel="stylesheet" href="${cdnDomain}static/css/center-<%=currentTheme%>.css?v=${cdnVersion}">
    <script src="${cdnDomain}static/js/manager.account.common.js?v=${cdnVersion}"></script>
    <script src="${cdnDomain}static/js/lottery.order.details.js?v=${cdnVersion}"></script>

    <% String pageName = request.getParameter("key"); %>
    <% if ("account".equals(pageName)) { %>
        <script src="${cdnDomain}static/js/manager.account.js?v=${cdnVersion}"></script>
    <% } else if ("card".equals(pageName)) { %>
        <script src="${cdnDomain}static/js/manager.account.card.js?v=${cdnVersion}"></script>
    <% } else if ("report".equals(pageName)) { %>
        <script src="${cdnDomain}static/js/manager.account.report.js?v=${cdnVersion}"></script>
    <% } else if ("order".equals(pageName)) { %>
        <script src="${cdnDomain}static/js/manager.account.order.js?v=${cdnVersion}"></script>
    <% } else if ("chase".equals(pageName)) { %>
        <script src="${cdnDomain}static/js/manager.account.chase.js?v=${cdnVersion}"></script>
    <% } else if ("gameOrder".equals(pageName)) { %>
        <script src="${cdnDomain}static/js/manager.account.gameOrder.js?v=${cdnVersion}"></script>
    <% } else if ("bill".equals(pageName)) { %>
        <script src="${cdnDomain}static/js/manager.account.bill.js?v=${cdnVersion}"></script>
    <% } %>
</head>
<body>
<div class="main">
    <div class="form">
        <div class="tab clearfix">
            <div class="tab-title pull-left"><span>会员信息</span></div>
        </div>
        <div class="account" id="account-review">
            <div class="acc">
                <div class="portrait"><img src="${cdnDomain}static/images/center/portrait.jpg"></div>
                <div class="amount">
                    <div class="name" id="nickname"></div>
                    <div class="oucc">
                        <div class="cc">
                            <div class="cc_1">账户总额</div>
                            <div class="cc_2">￥<span id="balance">0.00</span></div>
                        </div>
                        <div class="tt">
                            <div class="tt_1 text-elip" id="totalBalance">主账户：0.00</div>
                            <div class="tt_2 text-elip" id="agBalance">AG：<a href="javascript:;" class="link-general" data-command="checkAGBalance">查看余额</a></div>
                        </div>
                        <div class="tt">
                            <div class="tt_1 text-elip" id="lotteryBalance">彩票：0.00</div>
                            <div class="tt_2 text-elip" id="ptBalance">PT：<a href="javascript:;" class="link-general" data-command="checkPTBalance">查看余额</a></div>
                        </div>
                        <div class="tcc">
                            <div class="tcc_1"><span id="securityLevel">安全等级：<i class="fa fa-star star2" aria-hidden="true"></i><i class="fa fa-star star2" aria-hidden="true"></i><i class="fa fa-star star2" aria-hidden="true"></i><i class="fa fa-star star2" aria-hidden="true"></i><i class="fa fa-star star2" aria-hidden="true"></i></span><span id="loginTime">最近登录时间：加载中</span></div>
                            <div class="tcc_2"><span id="loginIp" class="text-elip">最近登录IP：加载中</span><span id="loginAddress" class="text-elip">最近登录地址：加载中</span></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="tab clearfix">
            <ul class="clearfix pull-left pull_cc">
                <li <% if ("account".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-account?key=account'">账号安全</li>
                <li <% if ("card".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-account?key=card'">银行卡管理</li>
                <li <% if ("report".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-account?key=report'">个人报表</li>
                <li <% if ("order".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-account?key=order'">彩票记录</li>
                <li <% if ("chase".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-account?key=chase'">追号记录</li>
                <li <% if ("gameOrder".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-account?key=gameOrder'">棋牌记录</li>
                <li <% if ("bill".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-account?key=bill'">盈亏明细</li>
            </ul>
        </div>
        <div class="list">
            <% if ("account".equals(pageName)) { %>
            <div id="userBind"></div>
            <% } else if ("card".equals(pageName)) { %>
            <div id="userCard"></div>
            <% } else if ("report".equals(pageName)) { %>
            <div id="userReport">
                <div class="Team_data" id="reportSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_2">时间：<span><input type="text" class="time" id="sTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span><input type="text" class="time" id="eTime" readonly required><i class="icon icon-日历">&#xe819;</i></span></div>
                        <div class="Team_data_top_3"><input type="button" class="btn_sc" value="搜索" data-command="search"></div>
                    </div>
                </div>
                <div class="report" id="reportData">
                </div>
            </div>
            <% } else if ("order".equals(pageName)) { %>
            <div id="userOrder">
                <div class="Team_data" id="userOrderSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>游戏类别：
                                <select id="lottery"></select>
                            </i>
                            <i>状态：
                                <select id="status">
                                    <option value="" selected>全部</option>
                                    <option value="0">未开奖</option>
                                    <option value="1">未中奖</option>
                                    <option value="2">已中奖</option>
                                    <option value="-1">已撤单</option>
                                </select>
                            </i>
                            <i>投注期号：
                                <input type="text" class="text" id="expect" maxlength="20" autocomplete="off">
                            </i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="sTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="eTime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3">
                            <input type="button" class="btn_sc" value="搜索" data-command="search">
                        </div>
                    </div>
                </div>
                <table class="tab-list" id="userOrderTable">
                    <thead>
                        <tr>
                            <th width="16%">订单</th>
                            <th width="11%">彩种</th>
                            <th width="14%">玩法</th>
                            <th width="13%">期号</th>
                            <th width="13%">时间</th>
                            <th width="9%">投注</th>
                            <th width="9%">奖金</th>
                            <th width="8%">状态</th>
                            <th width="6%">操作</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="userOrderPage"></div>
            </div>
            <% } else if ("chase".equals(pageName)) { %>
            <div id="userChase">
                <div class="Team_data" id="userChaseSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>游戏类别：
                                <select id="lottery"></select>
                            </i>
                            <i>状态：
                                <select id="status">
                                    <option value="" selected>全部</option>
                                    <option value="0">未开奖</option>
                                    <option value="1">未中奖</option>
                                    <option value="2">已中奖</option>
                                    <option value="-1">已撤单</option>
                                </select>
                            </i>
                            <i>投注期号：
                                <input type="text" class="text" id="expect" maxlength="20" autocomplete="off">
                            </i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="sTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="eTime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3"><input type="button" class="btn_sc" value="搜索" data-command="search"></div>
                    </div>
                </div>
                <table class="tab-list" id="userChaseTable">
                    <thead>
                        <tr>
                            <th width="16%">订单</th>
                            <th width="11%">彩种</th>
                            <th width="14%">玩法</th>
                            <th width="13%">期号</th>
                            <th width="13%">开奖时间</th>
                            <th width="9%">投注</th>
                            <th width="9%">奖金</th>
                            <th width="8%">状态</th>
                            <th width="6%">操作</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="userChasePage"></div>
            </div>
            <% } else if ("gameOrder".equals(pageName)) { %>
            <div id="userGameOrder">
                <div class="Team_data" id="userGameOrderSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>平台名称：
                                <select id="platform">
                                    <option value="4" selected>AG</option>
                                    <option value="11">PT</option>
                                </select>
                            </i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="sTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="eTime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3"><input type="button" class="btn_sc" value="搜索" data-command="search"></div>
                    </div>
                </div>
                <table class="tab-list" id="userGameOrderTable">
                    <thead>
                        <tr>
                            <th width="10%">平台名称</th>
                            <th width="17%">游戏类型</th>
                            <th width="17%">游戏名</th>
                            <th width="10%">投注</th>
                            <th width="10%">奖金</th>
                            <th width="15%">投注时间</th>
                            <th width="15%">派奖时间</th>
                            <th width="6%">状态</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="userGameOrderPage"></div>
            </div>
            <% } else if ("bill".equals(pageName)) { %>
            <div id="userBill">
                <div class="Team_data" id="userBillSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>账单类别：
                                <select id="type">
                                    <option value="" selected>全部</option>
                                    <option value="1">充值</option>
                                    <option value="2">取款</option>
                                    <option value="16">取款退回</option>
                                    <option value="3">转入</option>
                                    <option value="4">转出</option>
                                    <option value="15">上下级转账</option>
                                    <option value="5">优惠活动</option>
                                    <option value="6">投注</option>
                                    <option value="7">派奖</option>
                                    <option value="8">投注返点</option>
                                    <option value="9">代理返点</option>
                                    <option value="10">撤销订单</option>
                                    <option value="11">会员返水</option>
                                    <option value="13">管理员增</option>
                                    <option value="14">管理员减</option>
                                    <option value="22">日结</option>
                                </select>
                            </i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="sTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="eTime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3"><input type="button" class="btn_sc" value="搜索" data-command="search"></div>
                    </div>
                </div>
                <table class="tab-list" id="userBillTable">
                    <thead>
                        <tr>
                            <th width="15%">编号</th>
                            <th width="15%">账单类型</th>
                            <th width="15%">之前金额</th>
                            <th width="15%">操作金额</th>
                            <th width="15%">剩余金额</th>
                            <th width="15%">操作时间</th>
                            <th width="15%">备注</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="userBillPage"></div>
            </div>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>