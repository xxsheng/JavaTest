<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>财务中心</title>
    <%@include file="file/common.jsp"%>
    <link changeable="true" rel="stylesheet" href="${cdnDomain}static/css/center-<%=currentTheme%>.css?v=${cdnVersion}">

    <% String pageName = request.getParameter("key"); %>
    <% if ("recharge".equals(pageName)) { %>
    <script src="${cdnDomain}static/plugin/clipboard/dist/clipboard.min.js"></script>
    <script src="${cdnDomain}static/js/manager.funds.recharge.js?v=${cdnVersion}"></script>
    <% } else if ("withdraw".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.funds.withdraw.js?v=${cdnVersion}"></script>
    <% } else if ("transfer".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.funds.transfer.js?v=${cdnVersion}"></script>
    <% } else if ("rechargerecord".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.funds.rechargerecord.js?v=${cdnVersion}"></script>
    <% } else if ("withdrawrecord".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.funds.withdrawrecord.js?v=${cdnVersion}"></script>
    <% } else if ("transferrecord".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.funds.transferrecord.js?v=${cdnVersion}"></script>
    <% } %>
</head>
<body>
<div class="main">
    <!--自定义-->
    <div class="form">
        <div class="tab clearfix">
            <div class="tab-title pull-left"><span>财务中心</span></div>
            <ul class="clearfix pull-right">
                <li <% if ("recharge".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-funds?key=recharge'">充值</li>
                <li <% if ("withdraw".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-funds?key=withdraw'">提款</li>
                <li <% if ("transfer".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-funds?key=transfer'">转账</li>
                <li <% if ("rechargerecord".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-funds?key=rechargerecord'">充值记录</li>
                <li <% if ("withdrawrecord".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-funds?key=withdrawrecord'">提款记录</li>
                <li <% if ("transferrecord".equals(pageName)){%>class="current"<% } %> onclick="self.window.location='/manager-funds?key=transferrecord'">转账记录</li>
            </ul>
        </div>
        <div class="list">
            <% if ("recharge".equals(pageName)) { %>
            <div id="recharge" style="display: none;">
                <div class="cc_tab clearfix" id="rechargeTab"></div>
                <div class="Matters" id="rechargeTip"><span>注意事项：</span>充值限时：在线充值请在15分钟内完成充值，网银转账请在2小时内完成，否则不会自动到账。</div>

                <div class="new_y" id="rechargeStep1">
                    <div class="ncw_text" id ="qrCodeAmount"> </div>

                    <div class="ncw_text" id="rechargeBank"></div>

                    <div class="ncw_btn">
                        <input type="button" class="btn_yj button-1" id="gonext" value="下一步">
                    </div>
                </div>

                <div class="new_y" id="rechargeStep2" style="display:none;"></div>
            <% } else if ("withdraw".equals(pageName)) { %>
            <div id="withdraw">
                <div class="Matters">
                    <p>提款时间：早上10：00 至 凌晨 02：00</p>
                    <p>注意事项：每天前五次免收手续费，提款超出五次，每次按提款金额金额1%收取手续费，最高25元。单笔金额为<span id="withdrawMinAmount">0</span>~<span id="withdrawMaxAmount"></span>元。</p>
                    <p>资金归集是指将您账户下所有资金一次性转移至主账户(含彩票账户、AG、PT等)</p>
                </div>
                <div class="new_x">
                    <div class="new_text">
                        <span class="cur">可提现余额：</span><i class="cur" id="withdrawBalance">￥0.00</i>
                        <span class="cur">主账户：</span><i class="cur" id="withdrawTotalMoney">￥0.00</i>
                        <span class="cur">彩票账户：</span><i class="cur" id="withdrawLotteryMoney">￥0.00</i>
                        <span class="cur">AG：</span><i class="cur" id="agBalance"><a href="javascript:;" class="link-general fs18" data-command="checkAGBalance">查看余额</a></i>
                        <span class="cur">PT：</span><i class="cur" id="ptBalance"><a href="javascript:;" class="link-general fs18" data-command="checkPTBalance">查看余额</a></i>
                        <input type="button" class="btn_yj button-1" value="资金归集" id="withdrawTransferAll">
                    </div>
                    <div class="new_text"><span class="cur">提款金额：</span><i><input type="text" class="yh force-digit" maxlength="5" id="withdrawAmount" autocomplete="off"></i></div>
                    <div class="new_text"><span class="cur">选择银行：</span><i><select id="withdrawCard"></select></i></div>
                    <div class="new_text"><span class="cur">资金密码：</span><i><input type="password" maxlength="30" class="yh" id="withdrawPwd" autocomplete="off"></i></div>
                    <div class="new_btn"><input type="button" class="btn_yj button-1" value="提交" id="withdrawSubmit"></div>
                </div>
            </div>
            <% } else if ("transfer".equals(pageName)) { %>
            <div id="transfer">
                <div class="Matters">
                    <p>同账户之间不允许相互转换</p>
                    <p>资金归集是指将您账户下所有资金一次性转移至主账户(含彩票账户、AG、PT等)</p>
                </div>

                <div class="new_x">
                    <div class="new_text">
                        <span class="cur">主账户：</span><i class="cur" id="transferTotalMoney">￥0.00</i>
                        <span class="cur">彩票账户：</span><i class="cur" id="transferLotteryMoney">￥0.00</i>
                        <span class="cur">AG：</span><i class="cur" id="agBalance"><a href="javascript:;" class="link-general fs18" data-command="checkAGBalance">查看余额</a></i>
                        <span class="cur">PT：</span><i class="cur" id="ptBalance"><a href="javascript:;" class="link-general fs18" data-command="checkPTBalance">查看余额</a></i>
                        <input type="button" class="btn_yj button-1" value="资金归集" id="transferTransferAll">
                    </div>

                    <div class="new_text"><span class="cur">转出：</span><i><select id="transferOut"></select></i></div>
                    <div class="new_text"><span class="cur">转入：</span><i><select id="transferIn"></select></i></div>
                    <div class="new_text"><span class="cur">金额：</span><i><input type="text" class="yh force-digit" maxlength="7" id="transferAmount" autocomplete="off"></i></div>
                    <div class="new_btn"><input type="button" class="btn_yj button-1" value="提交" id="transferSubmit"></div>
                </div>
            </div>
            <% } else if ("rechargerecord".equals(pageName)) { %>
            <div id="rechargeRecord">
                <div class="Team_data">
                    <div class="Team_data_top" id="rechargeRecordSearch">
                        <div class="Team_data_top_1">
                            <i>充值方式：
                                <select id="rechargeRecordType">
                                    <option value="">全部</option>
                                    <option value="2">在线充值</option>
                                    <option value="4">微信&支付宝充值</option>
                                    <option value="3">系统充值</option>
                                    <option value="1">转账汇款</option>
                                </select>
                            </i>
                        </div>
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="rechargeRecordSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="rechargeRecordETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3"><input type="button" class="btn_sc" value="搜索" data-command="search"></div>
                    </div>
                </div>
                <table class="tab-list" id="rechargeRecordTable">
                    <thead>
                        <tr>
                            <th width="20%">订单号</th>
                            <th width="15%">交易时间</th>
                            <th width="15%">充值方式</th>
                            <th width="15%">充值金额</th>
                            <th width="15%">账户余额</th>
                            <th width="20%">备注</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="rechargeRecordPage"></div>
            </div>
            <% } else if ("withdrawrecord".equals(pageName)) { %>
            <div id="withdrawRecord">
                <div class="Team_data" id="withdrawRecordSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>状态：
                                <select id="withdrawStatus">
                                    <option value="">全部</option>
                                    <option value="0">待处理</option>
                                    <option value="1">已完成</option>
                                    <option value="2">处理中</option>
                                    <option value="3">银行处理中</option>
                                    <option value="4">提现失败</option>
                                    <option value="-1">拒绝支付</option>
                                </select>
                            </i>
                        </div>

                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="withdrawRecordSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="withdrawRecordETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3"><input type="button" class="btn_sc" value="搜索" data-command="search"></div>
                    </div>
                </div>
                <table class="tab-list" id="withdrawRecordTable">
                    <thead>
                        <tr>
                            <th width="20%">订单号</th>
                            <th width="10%">申请金额</th>
                            <th width="10%">到账金额</th>
                            <th width="10%">账户余额</th>
                            <th width="15%">取款时间</th>
                            <th width="10%">状态</th>
                            <th width="25%">备注</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="withdrawRecordPage"></div>
            </div>
            <% } else if ("transferrecord".equals(pageName)) { %>
            <div id="transferRecord">
                <div class="Team_data" id="transferRecordSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_2">
                            时间：<span>
                            <input type="text" class="time" id="transferRecordSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                            <input type="text" class="time" id="transferRecordETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                        </div>
                        <div class="Team_data_top_3"><input type="button" class="btn_sc" value="搜索" data-command="search"></div>
                    </div>
                </div>
                <table class="tab-list" id="transferRecordTable">
                    <thead>
                    <tr>
                        <th width="20%">订单号</th>
                        <th width="10%">类型</th>
                        <th width="10%">转出账户</th>
                        <th width="10%">转入账户</th>
                        <th width="10%">操作金额</th>
                        <th width="10%">账户余额</th>
                        <th width="15%">操作时间</th>
                        <th width="15%">备注</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="transferRecordPage"></div>
            </div>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>