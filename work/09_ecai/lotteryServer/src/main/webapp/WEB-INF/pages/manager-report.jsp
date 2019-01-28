<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>团队报表</title>
    <%@include file="file/common.jsp"%>
    <link changeable="true" rel="stylesheet" href="${cdnDomain}static/css/center-<%=currentTheme%>.css?v=${cdnVersion}">

    <% String pageName = request.getParameter("key"); %>
    <% if ("teamReport".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.report.teamReport.js?v=${cdnVersion}"></script>
    <% } else if ("mainReport".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.report.mainReport.js?v=${cdnVersion}"></script>
    <% } else if ("lotteryReport".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.report.lotteryReport.js?v=${cdnVersion}"></script>
    <% } else if ("gameReport".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.report.gameReport.js?v=${cdnVersion}"></script>
    <% } else if ("order".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/lottery.order.details.js?v=${cdnVersion}"></script>
    <script src="${cdnDomain}static/js/manager.report.order.js?v=${cdnVersion}"></script>
    <% } else if ("gameOrder".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.report.gameOrder.js?v=${cdnVersion}"></script>
    <% } else if ("bill".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/lottery.order.details.js?v=${cdnVersion}"></script>
    <script src="${cdnDomain}static/js/manager.report.bill.js?v=${cdnVersion}"></script>
    <% } %>
</head>
<body>

<div class="main">
    <div class="form">
        <div class="tab clearfix">
            <div class="tab-title pull-left"><span>团队报表</span></div>
            <ul class="clearfix pull-right">
                <li <% if ("teamReport".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-report?key=teamReport'">团队数据</li>
                <li <% if ("mainReport".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-report?key=mainReport'">主账户报表</li>
                <li <% if ("lotteryReport".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-report?key=lotteryReport'">彩票报表</li>
                <li <% if ("gameReport".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-report?key=gameReport'">棋牌报表</li>
                <li <% if ("order".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-report?key=order'">彩票记录</li>
                <li <% if ("gameOrder".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-report?key=gameOrder'">棋牌记录</li>
                <li <% if ("bill".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-report?key=bill'">账变记录</li>
            </ul>
        </div>
        <div class="list">
            <% if ("teamReport".equals(pageName)) { %>
            <div id="teamReport">
                <div class="Team_data">
                    <div class="Team_data_top" id="teamReportSearch">
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="teamReportSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="teamReportETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_1">
                            <span data-days="-2" data-command="fast-search">最近三天</span>
                            <span data-days="-6" data-command="fast-search">最近七天</span>
                            <span data-days="-29" data-command="fast-search">最近一个月</span>
                        </div>
                        <div class="Team_data_top_3">
                            <input type="button" class="btn_sc" value="搜索" data-command="search">
                        </div>
                    </div>
                    <div class="Team_data_box" id="teamReportData"></div>
                </div>
            </div>
            <% } else if ("mainReport".equals(pageName)) { %>
            <div id="mainReport">
                <div class="Team_data" id="mainReportSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>用户名：<input type="text" class="text" maxlength="12" id="mainReportUsername" autocomplete="off"></i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="mainReportSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="mainReportETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3">
                            <input type="button" class="btn_sc" value="搜索" data-command="search">
                        </div>
                    </div>
                    <div class="hierarchy" id="mainReportHierarchy">层级关系：</div>
                </div>
                <table class="tab-list" id="mainReportTable">
                    <thead>
                    <tr>
                        <th width="15%">用户名</th>
                        <th width="12%">充值</th>
                        <th width="13%">提款</th>
                        <th width="10%">转入</th>
                        <th width="10%">转出</th>
                        <th width="20%">上下级转账（转入）</th>
                        <th width="20%">上下级转账（转出）</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="mainReportPage"></div>
            </div>
            <% } else if ("lotteryReport".equals(pageName)) { %>
            <div id="lotteryReport">
                <div class="Team_data" id="lotteryReportSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>用户名：<input type="text" class="text" maxlength="12" id="lotteryReportUsername" autocomplete="off"></i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="lotteryReportSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="lotteryReportETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3">
                            <input type="button" class="btn_sc" value="搜索" data-command="search">
                        </div>
                    </div>
                    <div class="hierarchy" id="lotteryReportHierarchy">层级关系：</div>
                </div>
                <table class="tab-list" id="lotteryReportTable">
                    <thead>
                        <tr>
                            <th width="14%">用户名</th>
                            <th width="12%">转入</th>
                            <th width="12%">转出</th>
                            <th width="13%">投注</th>
                            <th width="13%">派奖</th>
                            <th width="12%">返点</th>
                            <th width="12%">优惠</th>
                            <th width="12%">盈利</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="lotteryReportPage"></div>
            </div>
            <% } else if ("gameReport".equals(pageName)) { %>
            <div id="gameReport">
                <div class="Team_data" id="gameReportSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>用户名：<input type="text" class="text" maxlength="12" id="gameReportUsername" autocomplete="off"></i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="gameReportSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="gameReportETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3">
                            <input type="button" class="btn_sc" value="搜索" data-command="search">
                        </div>
                    </div>
                    <div class="hierarchy" id="gameReportHierarchy">层级关系：</div>
                </div>
                <table class="tab-list" id="gameReportTable">
                    <thead>
                    <tr>
                        <th width="14%">用户名</th>
                        <th width="12%">转入</th>
                        <th width="12%">转出</th>
                        <th width="13%">消费</th>
                        <th width="13%">派奖</th>
                        <th width="12%">返水/返点</th>
                        <th width="12%">优惠</th>
                        <th width="12%">盈利</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="gameReportPage"></div>
            </div>
            <% } else if ("order".equals(pageName)) { %>
            <div id="proxyOrder">
                <div class="Team_data">
                    <div class="Team_data_top" id="proxyOrderSearch">
                        <div class="Team_data_top_1">
                            <i>游戏类别：
                                <select id="proxyOrderLottery"></select>
                            </i>
                            <i>状态：
                                <select id="proxyOrderStatus">
                                    <option value="" selected>全部</option>
                                    <option value="0">未开奖</option>
                                    <option value="1">未中奖</option>
                                    <option value="2">已中奖</option>
                                    <option value="-1">已撤单</option>
                                </select>
                            </i>
                            <i>范围：
                                <select id="proxyOrderScope">
                                    <option value="1" selected>会员</option>
                                    <option value="2">团队</option>
                                </select>
                            </i>
                            <i>
                                用户名：
                                <input type="text" class="text" maxlength="12" id="proxyOrderUsername" autocomplete="off">
                            </i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="proxyOrderSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="proxyOrderETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3">
                            <input type="button" class="btn_sc" value="搜索" data-command="search">
                        </div>
                    </div>
                    <div class="hierarchy" id="proxyOrderHierarchy">层级关系：</div>
                </div>
                <table class="tab-list" id="proxyOrderTable">
                    <thead>
                        <tr>
                            <th width="17%">用户</th>
                            <th width="15%">订单</th>
                            <th width="10%">彩种</th>
                            <th width="12%">玩法</th>
                            <th width="12%">期号</th>
                            <th width="12%">时间</th>
                            <th width="8%">投注</th>
                            <th width="8%">奖金</th>
                            <th width="6%">状态</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="proxyOrderPage"></div>
            </div>
            <% } else if ("gameOrder".equals(pageName)) { %>
            <div id="gameOrder">
                <div class="Team_data" id="gameOrderSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>平台：
                                <select id="gameOrderPlatform">
                                    <option value="4" selected>AG</option>
                                    <option value="11">PT</option>
                                </select>
                            </i>
                            <i>范围：
                                <select id="gameOrderScope">
                                    <option value="1" selected>会员</option>
                                    <option value="2">团队</option>
                                </select>
                            </i>
                            <i>
                                用户名：
                                <input type="text" class="text" maxlength="12" id="gameOrderUsername" autocomplete="off">
                            </i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="gameOrderSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="gameOrderETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3">
                            <input type="button" class="btn_sc" value="搜索" data-command="search">
                        </div>
                    </div>
                    <div class="hierarchy" id="gameOrderHierarchy">层级关系：</div>
                </div>
                <table class="tab-list" id="gameOrderTable">
                    <thead>
                        <tr>
                            <th>用户</th>
                            <th>平台</th>
                            <th>类型</th>
                            <th>游戏名</th>
                            <th>投注</th>
                            <th>奖金</th>
                            <th>时间</th>
                            <th>派奖时间</th>
                            <th>状态</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="gameOrderPage"></div>
            </div>
            <% } else if ("bill".equals(pageName)) { %>
            <div id="proxyBill">
                <div class="Team_data" id="proxyBillSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>范围：
                                <select id="proxyBillScope">
                                    <option value="1" selected>会员</option>
                                    <option value="2">团队</option>
                                </select>
                            </i>
                            <i>账单类别：
                                <select id="proxyBillType">
                                    <option value="1">充值</option>
                                    <option value="2">取款</option>
                                    <option value="16">取款退回</option>
                                    <option value="3">转入</option>
                                    <option value="4">转出</option>
                                    <option value="15">上下级转账</option>
                                    <option value="5">优惠活动</option>
                                    <option value="6" selected>投注</option>
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
                            <i>用户名：
                                <input type="text" class="text" id="proxyBillUsername" autocomplete="off">
                            </i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="proxyBillSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="proxyBillETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3">
                            <input type="button" class="btn_sc" value="搜索" data-command="search">
                        </div>
                    </div>
                    <div class="hierarchy" id="proxyBillHierarchy">层级关系：</div>
                </div>
                <table class="tab-list" id="proxyBillTable">
                    <thead>
                        <tr>
                            <th width="17%">编号</th>
                            <th width="18%">用户名</th>
                            <th width="10%">账单类型</th>
                            <th width="10%">之前金额</th>
                            <th width="10%">操作金额</th>
                            <th width="10%">剩余金额</th>
                            <th width="15%">操作时间</th>
                            <th width="10%">备注</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="proxyBillPage"></div>
            </div>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>