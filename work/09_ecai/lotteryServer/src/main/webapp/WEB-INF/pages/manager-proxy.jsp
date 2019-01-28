<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <title>代理管理</title>
    <%@include file="file/common.jsp"%>
    <link changeable="true" rel="stylesheet" href="${cdnDomain}static/css/center-<%=currentTheme%>.css?v=${cdnVersion}">
    <link rel="stylesheet" href="${cdnDomain}static/plugin/noUiSlider/noUiSlider.css">

    <script src="${cdnDomain}static/plugin/noUiSlider/noUiSlider.js"></script>
     <script src="${cdnDomain}static/js/manager.proxy.js?v=${cdnVersion}"></script>

    <% String pageName = request.getParameter("key"); %>
    <% if ("team".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.proxy.team.js?v=${cdnVersion}"></script>
    <% } else if ("add".equals(pageName)) { %>
    <script src="${cdnDomain}static/plugin/clipboard/dist/clipboard.min.js"></script>
    <script src="${cdnDomain}static/js/manager.proxy.add.js?v=${cdnVersion}"></script>
    <% } else if ("link".equals(pageName)) { %>
    <script src="${cdnDomain}static/plugin/clipboard/dist/clipboard.min.js"></script>
    <script src="${cdnDomain}static/js/manager.proxy.link.js?v=${cdnVersion}"></script>
    <% } else if ("online".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/manager.proxy.online.js?v=${cdnVersion}"></script>
    <% } else if ("salary".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/single.choose.member.js?v=${cdnVersion}"></script>
    <script src="${cdnDomain}static/js/manager.proxy.salary.js?v=${cdnVersion}"></script>
    <% } else if ("dividend".equals(pageName)) { %>
    <script src="${cdnDomain}static/js/single.choose.member.js?v=${cdnVersion}"></script>
    <script src="${cdnDomain}static/js/manager.proxy.dividend.js?v=${cdnVersion}"></script>
    <% } %>
</head>
<body>

<div class="main">
    <!--自定义-->
    <div class="form">
        <div class="tab clearfix">
            <div class="tab-title pull-left"><span>代理管理</span></div>
            <ul id="proxy-show" class="clearfix pull-right">
            </ul>
        </div>
        <div class="list">
            <% if ("team".equals(pageName)) { %>
            <div id="proxyTeam">
                <div class="Team_data" id="proxyTeamSearch">
                    <div class="Team_data_top">
                        <div class="Team_data_top_1">
                            <i>范围：
                                <select id="proxyTeamScope">
                                    <option value="1">会员</option>
                                    <option value="2">团队</option>
                                    <option value="3" selected>直属</option>
                                </select>
                            </i>
                            <i>用户名：
                                <input type="text" class="text" id="username" maxlength="12" autocomplete="off">
                            </i>
                            <i>余额：
                                <input type="text" class="text force-digit" id="minMoney" data-min="0" data-max="9999999" maxlength="12" autocomplete="off"> 至 <input type="text" class="text force-digit" id="maxMoney" data-min="0" data-max="9999999"  maxlength="12" autocomplete="off">
                            </i>
                        </div>
                        <div class="Team_data_top_3"><input type="button" class="btn_sc" value="搜索" data-command="search"></div>
                    </div>
                    <div class="hierarchy" id="hierarchy">层级关系：</div>
                </div>
                <table class="tab-list" id="proxyTeamTable">
                    <thead>
                        <tr>
                            <th width="12%">用户名</th>
                            <th width="6%">类别</th>
                            <th width="8%">账户余额</th>
                            <th width="8%">团队余额</th>
                            <th width="7%">团队人数</th>
                            <th width="7%">团队在线</th>
                            <th width="6%">返点</th>
                            <th width="14%">注册时间</th>
                            <th width="14%">最后登录</th>
                            <th width="6%">状态</th>
                            <th width="14%">操作</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="proxyTeamPage"></div>
            </div>
            <% } else if ("add".equals(pageName)) { %>
            <div id="form">
                <div class="Matters"><span>注意事项：</span>会员初始密码为“a123456”。为提高服务器效率，系统将自动清理注册一个月没有充值，或两个月未登录，并且金额低于10元的账户。</div>
                <div class="new_x">

                    <div class="new_text"><span>开户类型：</span>
                        <label class="check checked">
                            <span><input type="radio" name="type" value="1" class="radio" checked="true"></span>代理
                        </label>
                        <label class="check">
                            <span><input type="radio" name="type" value="2" class="radio"></span>玩家
                        </label>
                    </div>
                    <div class="new_text"><span class="cur">用户名：</span><i><input type="tetx" id="username" maxlength="12" class="yh" autocomplete="off"></i></div>
                    <div class="new_text"><span>账号返点：</span>
                        <div class="adjust-code">
                            <div class="slider"></div>
                        </div>
                    </div>
                    <div class="new_text">
                        <span class="cur">快速设置：</span>
                        <i class="fa fa-minus-circle fasticon cursor" aria-hidden="true"></i>
                        <i>
                            <input type="text" class="fast" value="1800" name="code" id="code" autocomplete="off">
                        </i>
                        <i class="fa fa-plus-circle fasticon cursor" aria-hidden="true"></i>
                    </div>
                    <div class="new_btn"><input type="button" data-command="submit" class="btn_yj button-1" value="立即开户"></div>
                    <div class="new_text mt20" id="quotas"></div>
                </div>
            </div>
            <% } else if ("link".equals(pageName)) { %>
            <div id="form">
                <div class="cc_tab clearfix" id="tabs">
                    <label class="check checked">
                        <span><input type="radio" name="linkType" class="radio" checked="checked"></span>链接开户
                    </label>
                    <label class="check">
                        <span><input type="radio" name="linkType" class="radio"></span>网页链接管理
                    </label>
                    <label class="check">
                        <span><input type="radio" name="linkType" class="radio"></span>手机链接管理
                    </label>
                </div>
                <div class="cub_box">
                    <div class="cub_1 cub" id="newLink">
                        <div class="Matters"><span>注意事项：</span>生成链接不会立即扣减配额，只有用户使用该链接注册成功的时候，才会扣减配额；请确保您的配额充足，配额不足将造成用户注册不成功！</div>
                        <div class="new_x">
                            <div class="new_text" id="userType"><span>开户类型：</span>
                                <label class="check checked">
                                    <span><input type="radio" name="type" value="1" class="radio" checked="checked"></span>代理
                                </label>
                                <label class="check">
                                    <span><input type="radio" name="type" value="2" class="radio"></span>玩家
                                </label>
                            </div>
                            <div class="new_text" id="deviceType"><span>设备类型：</span>
                                <label class="check checked">
                                    <span><input type="radio" name="deviceType" value="1" class="radio" checked="checked"></span>网页
                                </label>
                                <label class="check">
                                    <span><input type="radio" name="deviceType" value="2" class="radio"></span>手机
                                </label>
                            </div>
                            <div class="new_text">
                                <span class="cur">有效期：</span>
                                <i>
                                    <select id="days">
                                        <option value="0" selected>永久有效</option>
                                        <option value="1">1天</option>
                                        <option value="7">7天</option>
                                        <option value="30">30天</option>
                                    </select>
                                </i>
                            </div>
                            <div class="new_text">
                                <span class="cur">使用次数：</span>
                                <i>
                                    <input type="text" id="number" value="9999" class="yh force-digit" data-min="1" data-max="9999" data-default="9999" autocomplete="off">
                                </i>
                            </div>
                            <div class="new_text"><span>账号返点：</span>
                                <div class="adjust-code">
                                    <div class="slider"></div>
                                </div>
                            </div>
                            <div class="new_text">
                                <span class="cur">快速设置：</span>
                                <i class="fa fa-minus-circle fasticon cursor" aria-hidden="true"></i>
                                <i>
                                    <input type="text" class="fast" value="1800" name="code" id="code" autocomplete="off">
                                </i>
                                <i class="fa fa-plus-circle fasticon cursor" aria-hidden="true"></i>
                            </div>
                            <div class="new_btn"><input type="submit" data-command="submit" class="btn_yj button-1" value="生成链接"></div>
                            <div class="new_text mt20" id="quotas"></div>
                        </div>
                    </div>
                    <div class="cub_2 cub" style="display:none;" id="webLinks">
                        <table class="tab-list">
                            <thead>
                                <tr>
                                    <th width="55%">链接</th>
                                    <th width="8%">用户类别</th>
                                    <th width="8%">账号返点</th>
                                    <th width="12%">过期时间</th>
                                    <th width="8%">剩余个数</th>
                                    <th width="6%">操作</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div class="page-size" id="webLinkPage"></div>
                    </div>
                    <div class="cub_2 cub" style="display:none;" id="mobileLinks">
                        <table class="tab-list">
                            <thead>
                                <tr>
                                    <th width="55%">二维码</th>
                                    <th width="8%">用户类别</th>
                                    <th width="8%">账号返点</th>
                                    <th width="12%">过期时间</th>
                                    <th width="8%">剩余个数</th>
                                    <th width="6%">操作</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div class="page-size" id="mobileLinkPage"></div>
                    </div>
                </div>
            </div>
            <% } else if ("online".equals(pageName)) { %>
            <div id="onlineUser">
                <table class="tab-list" id="onlineUserTable">
                    <thead>
                        <tr>
                            <th width="12%">用户名</th>
                            <th width="10%">余额</th>
                            <th width="54%">上下级关系</th>
                            <th width="12%">最后登录</th>
                            <th width="12%">操作</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <div class="page-size" id="onlineUserPage"></div>
            </div>
            <% } else if ("salary".equals(pageName)) { %>
            <div id="salary">
                <div class="cc_tab ss clearfix" id="salaryTabs">
                    <label class="check checked">
                        <span><input type="radio" name="salaryType" class="radio" checked="checked"></span>契约日结账单
                    </label>
                    <label class="check">
                        <span><input type="radio" name="salaryType" class="radio"></span>契约日结管理
                    </label>
                    <label class="check">
                        <span><input type="radio" name="salaryType" class="radio"></span>发起契约日结
                    </label>
                    <label class="check">
                        <span><input type="radio" name="salaryType" class="radio"></span>老虎机/真人/体育返水
                    </label>
                </div>
                <div class="cub_box">
                    <div class="cub_1 cub" id="salaryBillTab">
                        <div class="Team_data" id="salaryBillSearch">
                            <div class="Team_data_top">
                                <div class="Team_data_top_1">
                                    <i>范围：
                                        <select id="salaryBillScope">
                                            <option value="1" selected>会员</option>
                                            <option value="2">团队</option>
                                        </select>
                                    </i>
                                    <i>用户名：
                                        <input type="text" class="text" id="salaryBillUsername" maxlength="12" autocomplete="off">
                                    </i>
                                </div>
                                <div class="Team_data_top_2">
                                    时间：<span>
                                    <input type="text" class="time" id="salaryBillSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                                    <input type="text" class="time" id="salaryBillETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                                </div>
                                <div class="Team_data_top_3"><input type="button" class="btn_sc" value="搜索" data-command="search"></div>
                            </div>
                            <div class="hierarchy" id="salaryBillHierarchy">层级关系：</div>
                        </div>
                        <table class="tab-list" id="salaryBillTable">
                            <thead>
                                <tr>
                                    <th width="15%">用户名</th>
                                    <th width="20%">投注</th>
                                    <th width="10%">比例</th>
                                    <th width="10%">有效会员</th>
                                    <th width="10%">金额</th>
                                    <th width="15%">日期</th>
                                    <th width="10%">状态</th>
                                    <th width="10%">操作</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div class="page-size" id="salaryBillPage"></div>
                    </div>
                    <div class="cub_2 cub" style="display:none;" id="salaryTab">
                        <div class="Team_data" id="salarySearch">
                            <div class="Team_data_top">
                                <div class="Team_data_top_1">
                                    <i>范围：
                                        <select id="salaryScope">
                                            <option value="1" selected>会员</option>
                                            <option value="2">团队</option>
                                        </select>
                                    </i>
                                    <i>用户名：
                                        <input type="text" class="text" id="salaryUsername" maxlength="12" autocomplete="off">
                                    </i>
                                </div>
                                <div class="Team_data_top_3">
                                    <input type="button" class="btn_sc" value="搜索" data-command="search">
                                </div>
                            </div>
                            <div class="hierarchy" id="salaryHierarchy">层级关系：</div>
                        </div>
                        <table class="tab-list" id="salaryTable">
                            <thead>
                                <tr>
                                    <th width="20%">用户名</th>
                                    <th width="15%">日结比例</th>
                                    <th width="15%">最低有效会员</th>
                                    <th width="20%">处理时间</th>
                                    <th width="15%">状态</th>
                                    <th width="15%">操作</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div class="page-size" id="salaryPage"></div>
                    </div>
                    <div class="cub_3 cub" style="display:none;" id="salaryRequestTab">
                        <div class="new_y">
                            <div class="ncw_text">
                                <span class="cur">用户名：</span>
                                <i>
                                    <input type="tetx" class="yh" readonly required id="salaryRequestUsername" autocomplete="off">
                                </i>
                                <em>
                                    <input type="button" class="btn_lc" value="选择用户" data-command="choose_member">
                                    <input type="button" class="btn_lcc" value="清空用户" data-command="clean_member">
                                </em>
                            </div>
                            <div class="ncw_text">
                                <span class="cur">比例(%)：</span>
                                <i>
                                    <input type="text" maxlength="3" class="ch" id="salaryRequestScale" autocomplete="off">
                                </i>
                                <em class="cur" data-field="scale">可分配范围0.0 ~ 0.0</em>
                            </div>
                            <div class="ncw_text">
                                <span class="cur">最低有效人数：</span>
                                <i>
                                    <input type="text" class="ch force-digit" data-min="0" data-max="1000" data-default="0" id="salaryRequestMinValidUser" autocomplete="off">
                                </i>
                                <em class="cur" data-field="minValidUser">可分配范围0 ~ 0</em>
                            </div>
                            <div class="ncw_btn">
                                <input type="button" class="btn_yj button-1" value="确认" data-command="submit">
                                <input type="button" class="btn_yy button-2" value="重置" data-command="reset">
                            </div>
                        </div>
                    </div>
                    <div class="cub_4 cub" style="display:none;" id="gameWaterTab">
                        <div class="Team_data" id="gameWaterSearch">
                            <div class="Team_data_top">
                                <div class="Team_data_top_1">
                                    <i>范围：
                                        <select id="gameWaterScope">
                                            <option value="1" selected>会员</option>
                                            <option value="2">团队</option>
                                        </select>
                                    </i>
                                    <i>用户名：
                                        <input type="text" class="text" id="gameWaterUsername" maxlength="12" autocomplete="off">
                                    </i>
                                </div>
                                <div class="Team_data_top_2">
                                    时间：<span>
                                    <input type="text" class="time" id="gameWaterSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                                    <input type="text" class="time" id="gameWaterETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                                </div>
                                <div class="Team_data_top_3">
                                    <input type="button" class="btn_sc" value="搜索" data-command="search">
                                </div>
                            </div>
                            <div class="hierarchy" id="gameWaterHierarchy">层级关系：</div>
                        </div>
                        <table class="tab-list" id="gameWaterTable">
                            <thead>
                                <tr>
                                    <th width="15%">用户名</th>
                                    <th width="15%">来自用户</th>
                                    <th width="15%">投注</th>
                                    <th width="15%">返水比列</th>
                                    <th width="15%">返水金额</th>
                                    <th width="15%">日期</th>
                                    <th width="10%">状态</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div class="page-size" id="gameWaterPage"></div>
                    </div>
                </div>
            </div>
            <% } else if ("dividend".equals(pageName)) { %>
            <div id="dividend">
                <div class="cc_tab ss clearfix" id="dividendTabs">
                    <label class="check checked">
                        <span><input type="radio" name="dividendType" class="radio" checked="checked"></span>契约分红账单
                    </label>
                    <label class="check">
                        <span><input type="radio" name="dividendType" class="radio"></span>契约分红管理
                    </label>
                    <label class="check">
                        <span><input type="radio" name="dividendType" class="radio"></span>发起契约分红
                    </label>
                    <%--<label class="check">--%>
                        <%--<span><input type="radio" name="dividendType" class="radio"></span>老虎机/真人/体育分红--%>
                    <%--</label>--%>
                </div>
                <div class="cub_box">
                    <div class="cub_1 cub" id="dividendBillTab">
                        <div class="Team_data" id="dividendBillSearch">
                            <div class="Team_data_top">
                                <div class="Team_data_top_1">
                                    <i>范围：
                                        <select id="dividendBillScope">
                                            <option value="1" selected>会员</option>
                                            <option value="2">团队</option>
                                        </select>
                                    </i>
                                    <i>状态：
                                        <select id="dividendBillStatus">
                                            <option value="" selected>全部</option>
                                            <option value="1">已发放</option>
                                            <option value="2">核对中</option>
                                            <option value="3">待领取</option>
                                            <option value="4">已拒绝</option>
                                            <option value="5">未达标</option>
                                            <option value="6">余额不足</option>
                                            <option value="7">部分领取</option>
                                            <option value="8">已过期</option>
                                        </select>
                                    </i>
                                    <i>用户名：
                                        <input type="text" class="text" id="dividendBillUsername" maxlength="12" autocomplete="off">
                                    </i>
                                </div>
                                <div class="Team_data_top_2">
                                    时间：<span>
                                    <input type="text" class="time" id="dividendBillSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                                    <input type="text" class="time" id="dividendBillETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                                </div>
                                <div class="Team_data_top_3"><input type="button" class="btn_sc" value="搜索" data-command="search"></div>
                            </div>
                            <div class="hierarchy" id="dividendBillHierarchy">层级关系：</div>
                        </div>
                        <table class="tab-list" id="dividendBillTable">
                            <thead>
                                <tr>
                                    <th width="10%">用户名</th>
                                    <th width="8%">投注</th>
                                    <th width="8%">亏损</th>
                                    <th width="8%">比例</th>
                                    <th width="8%">有效会员</th>
                                    <th width="8%">金额</th>
                                    <th width="10%">下级发放</th>
                                    <th width="10%">可领取</th>
                                    <th width="15%">周期</th>
                                    <th width="5%">状态</th>
                                    <th width="10%">操作</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div class="page-size" id="dividendBillPage"></div>
                    </div>
                    <div class="cub_2 cub" style="display:none;" id="dividendTab">
                        <div class="Team_data" id="dividendSearch">
                            <div class="Team_data_top">
                                <div class="Team_data_top_1">
                                    <i>范围：
                                        <select id="dividendScope">
                                            <option value="1" selected>会员</option>
                                            <option value="2">团队</option>
                                        </select>
                                    </i>
                                    <i>用户名：
                                        <input type="text" class="text" id="dividendUsername" maxlength="12" autocomplete="off">
                                    </i>
                                </div>
                                <div class="Team_data_top_3">
                                    <input type="button" class="btn_sc" value="搜索" data-command="search">
                                </div>
                            </div>
                            <div class="hierarchy" id="dividendHierarchy">层级关系：</div>
                        </div>
                        <table class="tab-list" id="dividendTable">
                            <thead>
                                <tr>
                                    <th width="15%">用户名</th>
                                    <th width="15%">分红比例</th>
                                    <th width="15%">最低有效会员</th>
                                    <th width="20%">处理时间</th>
                                    <th width="15%">状态</th>
                                    <th width="20%">操作</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div class="page-size" id="dividendPage"></div>
                    </div>
                    <div class="cub_3 cub" style="display:none;" id="dividendRequestTab">
                        <div class="new_y">
                            <div class="ncw_text">
                                <span class="cur">用户名：</span>
                                <i>
                                    <input type="tetx" class="yh" readonly required id="dividendRequestUsername" autocomplete="off">
                                </i>
                                <em>
                                    <input type="button" class="btn_lc" value="选择用户" data-command="choose_member">
                                    <input type="button" class="btn_lcc" value="清空用户" data-command="clean_member">
                                </em>
                            </div>
                            <div class="ncw_text">
                                <span class="cur">比例(%)：</span>
                                <i>
                                    <input type="text" class="ch force-digit" maxlength="3" id="dividendRequestScale" autocomplete="off">
                                </i>
                                <em class="cur" data-field="scale">可分配范围0.0 ~ 0.0</em>
                            </div>
                            <div class="ncw_text">
                                <span class="cur">最低有效人数：</span>
                                <i>
                                    <input type="text" class="ch force-digit" data-min="0" data-max="1000" data-default="0" id="dividendRequestMinValidUser" autocomplete="off">
                                </i>
                                <em class="cur" data-field="minValidUser">可分配范围0 ~ 0</em>
                            </div>
                            <div class="ncw_btn">
                                <input type="button" class="btn_yj button-1" value="确认" data-command="submit">
                                <input type="button" class="btn_yy button-2" value="重置" data-command="reset">
                            </div>
                        </div>
                    </div>
                    <div class="cub_4 cub" style="display:none;" id="gameDividendTab">
                        <div class="Team_data" id="gameDividendSearch">
                            <div class="Team_data_top">
                                <div class="Team_data_top_1">
                                    <i>用户名：
                                        <input type="text" class="text" id="gameDividendUsername" maxlength="12" autocomplete="off">
                                    </i>
                                </div>
                                <div class="Team_data_top_2">
                                    时间：<span>
                                    <input type="text" class="time" id="gameDividendSTime" readonly required><i class="icon icon-日历">&#xe819;</i></span> 至 <span>
                                    <input type="text" class="time" id="gameDividendETime" readonly required><i class="icon icon-日历">&#xe819;</i></span>
                                </div>
                                <div class="Team_data_top_3">
                                    <input type="button" class="btn_sc" value="搜索" data-command="search">
                                </div>
                            </div>
                        </div>
                        <table class="tab-list" id="gameDividendTable">
                            <thead>
                                <tr>
                                    <th width="10%">用户名</th>
                                    <th width="15%">投注</th>
                                    <th width="15%">亏损</th>
                                    <th width="10%">比例</th>
                                    <th width="10%">分红金额</th>
                                    <th width="20%">周期</th>
                                    <th width="10%">状态</th>
                                    <th width="10%">操作</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div class="page-size" id="gameDividendPage"></div>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>