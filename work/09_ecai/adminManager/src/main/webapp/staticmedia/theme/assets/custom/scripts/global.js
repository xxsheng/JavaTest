function getNetDate (strTime) {
	   if(strTime == ""){
		   return "";
	   }
	    var date = new Date(strTime);
	    date.setDate(date.getDate()+1);  
	    return date.getFullYear()+"-"+getFormatDate((date.getMonth()+1))+"-"+getFormatDate(date.getDate());
	}

function getFormatDate(arg) {
    if (arg == undefined || arg == '') {
        return '';
    }

    var re = arg + '';
    if (re.length < 2) {
        re = '0' + re;
    }

    return re;
}

var DataFormat = function() {
	var formatUserType = function(type) {
		if(type == 1) {
			return '代理';
		}
		if(type == 2) {
			return '玩家';
		}
		if(type == 3) {
			return '关联账号';
		}
		if(type == 4) {
			return '虚拟用户';
		}
	}

	var formatUserVipLevel = function(level) {
		if(level == 0) {
			return '普通会员';
		}
		if(level == 1) {
			return '青铜 VIP';
		}
		if(level == 2) {
			return '紫晶 VIP';
		}
		if(level == 3) {
			return '白金 VIP';
		}
		if(level == 4) {
			return '黄金 VIP';
		}
		if(level == 5) {
			return '钻石 VIP';
		}
		if(level == 6) {
			return '至尊 VIP';
		}
	}

	var formatLevelUsers = function(thisUser, list) {
		var users = "<a href='./lottery-user-profile?username=" + thisUser + "'>" + thisUser + "</a>";
		$.each(list, function(i, val) {
			users += ' &gt; ' + "<a href='./lottery-user-profile?username=" + val + "'>" + val + "</a>";;
		});
		return users;
	}

	var formatLevelUsers2 = function(list) {
		var users = "";
		$.each(list, function(i, val) {
			if (i >= 1) {
				users += ' &gt; ';
			}
			users += "<a href='./lottery-user-profile?username=" + val + "'>" + val + "</a>";
		});
		return users;
	}

	var formatUserAStatus = function(status) {
		if(status == 0) {
			return '正常';
		}
		if(status == -1) {
			return '冻结';
		}
		if(status == -2) {
			return '永久冻结';
		}
		if(status == -3) {
			return '禁用';
		}
	}

	var formatUserBStatus = function(status) {
		if(status == 0) {
			return '正常';
		}
		if(status == -1) {
			return '禁止投注';
		}
		if(status == -2) {
			return '自动掉线';
		}
		if(status == -3) {
			return '投注超时';
		}
	}

	var formatOnlineStatus = function(status) {
		if(status == 0) {
			return '离线';
		}
		if(status == 1) {
			return '在线';
		}
	}

	var formatUserPlanLevel = function(level) {
		if(level == 0) {
			return '菜鸟';
		}
		if(level == 1) {
			return '学徒';
		}
		if(level == 2) {
			return '出师';
		}
		if(level == 3) {
			return '操盘';
		}
		if(level == 4) {
			return '大师';
		}
		if(level == 5) {
			return '宗师';
		}
		if(level == 6) {
			return '大神';
		}
	}

	var formatUserCardStatus = function(status) {
		if(status == 0) {
			return '正常';
		}
		if(status == -1) {
			return '资料无效';
		}
		if(status == -2) {
			return '锁定';
		}
	}

	var formatPaymentChannelType = function(type, subtype) {
		// 1：网银充值
		if(type == 1) {
			if (subtype == 1) return '网银在线';
			if (subtype == 2) return '网银转账';
			if (subtype == 3) return '快捷支付';
			if (subtype == 4) return '银联扫码';
			if (subtype == 5) return '网银扫码转账';
			return '网银充值';
		}
		// 2：手机充值
		if(type == 2) {
			if (subtype == 1) return '微信在线';
			if (subtype == 2) return '微信扫码转账';
			if (subtype == 3) return '支付宝在线';
			if (subtype == 4) return '支付宝扫码转账';
			if (subtype == 5) return 'QQ在线';
			if (subtype == 6) return 'QQ扫码转账';
			if (subtype == 7) return '京东钱包';
			return '手机充值';
		}
		// 3：系统充值
		if(type == 3) {
			if (subtype == 1) return '充值未到账';
			if (subtype == 2) return '活动补贴';
			if (subtype == 3) return '管理员增';
			if (subtype == 4) return '管理员减';
			return '系统充值';
		}
		// 4：上下级转账
		if(type == 4) {
			if (subtype == 1) return '上下级转账(转入)';
			if (subtype == 2) return '上下级转账(转出)';
			return '上下级转账';
		}
		return '未知';
	}

	var formatPaymentChannelCode = function(channelCode) {
		if(channelCode == 'ips') {
			return '环讯支付';
		}
		if(channelCode == 'baofoo') {
			return '宝付支付';
		}
		if(channelCode == 'newpay') {
			return '新生支付';
		}
		if(channelCode == 'ecpss') {
			return '汇潮支付';
		}
		if(channelCode == 'yeepay') {
			return '易宝支付';
		}
		if(channelCode == 'mobao') {
			return '摩宝支付';
		}
		if(channelCode == 'gopay') {
			return '国付宝支付';
		}
		if(channelCode == 'pay41') {
			return '通汇支付';
		}
		if(channelCode == "dinpay"){
			return '智付支付';
		}
		if(channelCode == 'dinpayWeChat'){
			return '智付微信'
		}
		if(channelCode == 'pay41WeChat'){
			return '通汇微信'
		}
		if(channelCode == 'mobaoWeChat'){
			return '摩宝微信'
		}
		if(channelCode == 'mobaoQQpay'){
			return '摩宝QQ钱包'
		}
		if(channelCode == 'mobaoAlipay'){
			return '摩宝支付宝'
		}
		if(channelCode == 'huanstWeChat'){
			return '环商通微信'
		}
		if(channelCode == 'ifbaoWeChat'){
			return '雅付微信'
		}
		if(channelCode == 'bcWeChat'){
			return 'BC微信'
		}
		if(channelCode == 'lepayAlipay'){
			return '乐付支付宝'
		}
		if(channelCode == 'lepayWeChat'){
			return '乐付微信'
		}
		if(channelCode == 'lepay'){
			return '乐付网银'
		}
		if(channelCode == 'gst'){
			return '国盛通网银'
		}
		if(channelCode == 'gstWeChat'){
			return '国盛通微信'
		}
		if(channelCode == 'gstAlipay'){
			return '国盛通支付宝'
		}
		if(channelCode == "bankTransfer"){
			return '网银转账';
		}
		if(channelCode == "quickPay"){
			return '闪付网银';
		}
		if(channelCode == "quickAlipay"){
			return '闪付支付宝';
		}
		if(channelCode == "ystWeChat"){
			return '银商通微信';
		}
		if(channelCode == "ystAlipay"){
			return '银商通支付宝';
		}
		if(channelCode == "lepaySpeed"){
			return '乐付快捷';
		}
		if(channelCode == "mkt"){
			return '秒卡通网银';
		}
		if(channelCode == "xunbao"){
			return '讯宝网银';
		}
		if(channelCode == "xunbaoAlipay"){
			return '讯宝支付宝';
		}
		if(channelCode == "xunbaoQQ"){
			return '讯宝QQ钱包';
		}
		if(channelCode == "zhfWeChat"){
			return '智汇付微信';
		}
		if(channelCode == "zhfAlipay"){
			return '智汇付支付宝';
		}
		if(channelCode == "zhf"){
			return '智汇付网银';
		}
		if(channelCode == "zhfQQ"){
			return '智汇付QQ钱包';
		}
		if(channelCode == "xinbei"){
			return '新贝网银';
		}
		if(channelCode == "xinbeiWeChat"){
			return '新贝微信';
		}
		if(channelCode == "xinbeiAlipay"){
			return '新贝支付宝';
		}
		if(channelCode == "zhf2"){
			return '智汇付网银2';
		}
		if(channelCode == "zhfWeChat2"){
			return '智汇付微信2';
		}
		if(channelCode == "zhfAlipay2"){
			return '智汇付支付宝2';
		}
		if(channelCode == "jiushuiWeChat"){
			return '玖水微信';
		}
		if(channelCode == "jiushuiAlipay"){
			return '玖水支付宝';
		}
		if(channelCode == "jiushui"){
			return '玖水网银';
		}
		if(channelCode == "zs"){
			return '泽圣网银';
		}
		if(channelCode == "zsWeChat"){
			return '泽圣微信';
		}
		if(channelCode == "zsAlipay"){
			return '泽圣支付宝';
		}
		if(channelCode == "zsQQ"){
			return '泽圣QQ钱包';
		}
		if(channelCode == "zf"){
			return '智付网银';
		}
		if(channelCode == "zfWeChat"){
			return '智付微信';
		}
		if(channelCode == "zfQQ"){
			return '智付QQ钱包';
		}
		if(channelCode == "zfAlipay"){
			return '智付支付宝';
		}
		if(channelCode == "xinbeiQQpay"){
			return '新贝QQ钱包';
		}
		if(channelCode == "ht"){
			return '汇通网银';
		}
		if(channelCode == "htQQ"){
			return '汇通QQ钱包';
		}
		if(channelCode == "hh"){
			return '汇合网银';
		}
		if(channelCode == "hhQQ"){
			return '汇合QQ钱包';
		}
		if(channelCode == "hhWeChat"){
			return '汇合微信';
		}
		if(channelCode == "hhAlipay"){
			return '汇合支付宝';
		}
		if(channelCode == "zft"){
			return '支付通网银';
		}
		if(channelCode == "zftQQ"){
			return '支付通QQ钱包';
		}
		if(channelCode == "zftWeChat"){
			return '支付通微信';
		}
		if(channelCode == "zftAlipay"){
			return '支付通支付宝';
		}
		if(channelCode == "rx"){
			return '荣讯网银';
		}
		if(channelCode == "rxQQ"){
			return '荣讯QQ钱包';
		}
		if(channelCode == "rxWeChat"){
			return '荣讯微信';
		}
		if(channelCode == "rxAlipay"){
			return '荣讯支付宝';
		}
		if(channelCode == "rxJDPay"){
			return '荣讯京东钱包';
		}
		if(channelCode == "scanCodeWeChat"){
			return '微信扫码';
		}
		if(channelCode == "scanCodeAlipay"){
			return '支付宝扫码';
		}
		if(channelCode == "scanCodeQQ"){
			return 'QQ钱包扫码';
		}
		if(channelCode == "cf"){
			return '创富网银';
		}
	}

	var formatUserRechargeStatus = function(status) {
		if(status == 0) {
			return '未支付';
		}
		if(status == 1) {
			return '已完成';
		}
		if(status == -1) {
			return '已撤销';
		}
		if(status == -2) {
			return '已完成';
		}
	}

	var formatUserWithdrawStatus = function(status) {
		if(status == 0) {
			return '待处理';
		}
		if(status == 1) {
			return '<font color="#35AA47">已完成</font>';
		}
		if(status == -1) {
			return '<font color="#D84A38">拒绝支付</font>';
		}
	}

	var formatUserWithdrawLockStatus = function(status) {
		if(status == 0) {
			return '未锁定';
		}
		if(status == 1) {
			return '已锁定';
		}
	}

	var formatUserWithdrawCheckStatus = function(status) {
		if(status == 0) {
			return '待审核';
		}
		if(status == 1) {
			return '<font color="#35AA47">已通过</font>';
		}
		if(status == -1) {
			return '<font color="#D84A38">未通过</font>';
		}
	}

	var formatUserWithdrawLimitCheckType = function(type) {
		if(type == 1) {
			return '网银充值';
		}
		if(type == 2) {
			return '微信&支付宝';
		}
		if(type == 3) {
			return '充值未到账';
		}
		if(type == 4) {
			return '上下级转账（转入）';
		}
		if(type == 5) {
			return '上下级转账（转出）';
		}
		if(type == 6) {
			return '活动补贴';
		}
	}

	var formatUserWithdrawRemitStatus = function(status) {
		if(status == 0) {
			return '未处理';
		}
		if(status == 1) {
			return '银行处理中';
		}
		if(status == 2) {
			return '<span style="color: #35AA47;">打款完成</span>';
		}
		if(status == 3) {
			return '<span>第三方待处理 <i class="fa fa-question-circle cursor-pointer" title="系统自动同步第三方状态,请稍候刷新再看" data-class="tippy"></i></span>';
		}
		if(status == -1) {
			return '<span style="color: #D84A38;">请求失败</span>';
		}
		if(status == -2) {
			return '<span style="color: #D84A38;">打款失败</span>';
		}
		if(status == -3) {
			return '<span>查询状态中 <i class="fa fa-question-circle cursor-pointer" title="指API代付过程中发生未知异常，系统尝试主动与第三方进行核对，超过10分钟后系统不再处理，此时请前往第三方后台核对数据后再手动处理" data-class="tippy"></i></span>';
		}
		if(status == -4) {
			return '<span style="color: #D84A38;">未知状态 <i class="fa fa-question-circle cursor-pointer" title="指API代付过程中发生未知异常或第三方处理超时，系统无法确定该次代付是否成功，请前往第三方后台核对数据后再手动处理" data-class="tippy"></i></span>';
		}
		if(status == -5) {
			return '<span>第三方处理失败 <i class="fa fa-question-circle cursor-pointer" title="第三方状态，请前往第三方后台核对数据后再手动处理" data-class="tippy"></i></span>';
		}
		if(status == -6) {
			return '<span>银行处理失败 <i class="fa fa-question-circle cursor-pointer" title="第三方状态，请前往第三方后台核对数据后再手动处理" data-class="tippy"></i></span>';
		}
		if(status == -7) {
			return '<span>第三方拒绝支付 <i class="fa fa-question-circle cursor-pointer" title="第三方状态，请前往第三方后台核对数据后再手动处理" data-class="tippy"></i></span>';
		}
	}

	var formatUserBetsModel = function(status) {
		if(status == 'yuan') {
			return '2元';
		}
		if(status == 'jiao') {
			return '2角';
		}
		if(status == 'fen') {
			return '2分';
		}
		if(status == 'li') {
			return '2厘';
		}
		if(status == '1yuan') {
			return '1元';
		}
		if(status == '1jiao') {
			return '1角';
		}
		if(status == '1fen') {
			return '1分';
		}
		if(status == '1li') {
			return '1厘';
		}
	}

	var formatUserBetsStatus = function(status) {
		if(status == 0) {
			return '未开奖';
		}
		if(status == 1) {
			return '未中奖';
		}
		if(status == 2) {
			return '已中奖';
		}
		if(status == -1) {
			return '已撤单';
		}
	}

	var formatUserBetsPlanStatus = function(status) {
		if(status == 0) {
			return '未清算';
		}
		if(status == 1) {
			return '已清算';
		}
	}

	var formatUserBillType = function(type) {
		if(type == 1) {
			return '存款';
		}
		if(type == 2) {
			return '取款';
		}
		if(type == 3) {
			return '转入';
		}
		if(type == 4) {
			return '转出';
		}
		if(type == 5) {
			return '优惠活动';
		}
		if(type == 6) {
			return '投注';
		}
		if(type == 7) {
			return '派奖';
		}
		if(type == 8) {
			return '投注返点';
		}
		if(type == 9) {
			return '代理返点';
		}
		if(type == 10) {
			return '撤销订单';
		}
		if(type == 11) {
			return '会员返水';
		}
		if(type == 12) {
			return '代理分红';
		}
		if(type == 13) {
			return '管理员增';
		}
		if(type == 14) {
			return '管理员减';
		}
		if(type == 15) {
			return '上下级转账';
		}
		if(type == 16) {
			return '取款退回';
		}
		if(type == 17) {
			return '积分兑换';
		}
		if(type == 18) {
			return '支付佣金';
		}
		if(type == 19) {
			return '获得佣金';
		}
		if(type == 20) {
			return '退还佣金';
		}
		if(type == 21) {
			return '红包';
		}
		if(type == 22) {
			return '日结';
		}
	}

	var formatUserBillStatus = function(status) {
		if(status == 1) {
			return '正常';
		}
		if(status == -1) {
			return '无效';
		}
	}

	var formatUserMessageStatus = function(status) {
		if(status == 0) {
			return '未读';
		}
		if(status == 1) {
			return '已读';
		}
		if(status == -1) {
			return '已删除';
		}
	}

	var formatLotteryStatus = function(status) {
		if(status == 0) {
			return '<span class="color-green">启用</span>';
		}
		if(status == -1) {
			return '<span class="color-red">禁用</span>';
		}
	}

	var formatLotteryPlayRulesFixed = function(fixed) {
		if(fixed == 0) {
			return '否';
		}
		if(fixed == 1) {
			return '是';
		}
	}

	var formatLotteryPlayRulesStatus = function(status) {
		if(status == 0) {
			return '<span class="color-green">启用</span>';
		}
		if(status == -1) {
			return '<span class="color-red">禁用</span>';
		}
	}

    var formatLotteryPlayRulesIsLocate = function(isLocate) {
        if(isLocate == 0) {
            return '否';
        }
        if(isLocate == 1) {
            return '是';
        }
    }

	var formatLotteryPaymentCardStatus = function(status) {
		if(status == 0) {
			return '<span class="color-green">启用</span>';
		}
		if(status == -1) {
			return '<span class="color-red">禁用</span>';
		}
	}

	var formatLotteryPaymentCreditsType = function(type) {
		if(type == 0) {
			return '小';
		}
		if(type == 1) {
			return '中';
		}
		if(type == 2) {
			return '大';
		}
	}

	var formatLotteryPaymentChannelStatus = function(status) {
		if(status == 0) {
			return '<span class="color-green">启用</span>';
		}
		if(status == -1) {
			return '<span class="color-red">禁用</span>';
		}
	}

	var formatLotteryPaymentChannelBankStatus = function(status) {
		if(status == 0) {
			return '<span class="color-green">启用</span>';
		}
		if(status == -1) {
			return '<span class="color-red">禁用</span>';
		}
	}

	var formatLotterySysNoticeType = function(type) {
		if(type == 1) {
			return '公告';
		}
		if(type == 2) {
			return '活动';
		}
	}

	var formatLotterySysNoticeStatus = function(status) {
		if(status == 0) {
			return '显示中';
		}
		if(status == -1) {
			return '已隐藏';
		}
	}

	var formatLotterySysNoticeSort = function(sort) {
		if(sort == 0) {
			return '否';
		}
		if(sort == 1) {
			return '是';
		}
	}

	var formatLotterySysNoticeStatus = function(status) {
		if(status == 0) {
			return '显示中';
		}
		if(status == -1) {
			return '已隐藏';
		}
	}

	var formatAdminUserStatus = function(status) {
		if(status == 0) {
			return '启用';
		}
		if(status == -1) {
			return '禁用';
		}
	}

	var formatAdminUserRoleStatus = function(status) {
		if(status == 0) {
			return '启用';
		}
		if(status == -1) {
			return '禁用';
		}
	}

	var formatAdminUserGroupStatus = function(status) {
		if(status == 0) {
			return '启用';
		}
		if(status == -1) {
			return '禁用';
		}
	}

	var formatAdminUserActionStatus = function(status) {
		if(status == 0) {
			return '启用';
		}
		if(status == -1) {
			return '禁用';
		}
	}

	var formatAdminUserMenuStatus = function(status) {
		if(status == 0) {
			return '启用';
		}
		if(status == -1) {
			return '禁用';
		}
	}

	var formatLotteryUserDividendStatus = function(status) {
		if(status == 0) {
			return '可用';
		}
		if(status == -1) {
			return '禁用';
		}
	}

	var formatLotteryUserDividendBillStatus = function(status) {
		if(status == 0) {
			return '待处理';
		}
		if(status == 1) {
			return '已发放';
		}
		if(status == -1) {
			return '已拒绝';
		}
	}

	var formatLotteryUserDividendBillReceivedStatus = function(isReceived){
		if(isReceived == 0){
			return '未领取';
		}
		if(isReceived == 1){
			return '已领取';
		}
	}

	/*var formatActivityStatusWithTime = function(status, startTime, endTime) {
	 var value = '';
	 if(status == 0) {
	 value += '正常';
	 }
	 if(status == -1) {
	 value += '暂停';
	 }
	 if(moment().isBefore(startTime)) {
	 value += '（未开始）';
	 } else if(moment().isAfter(endTime)) {
	 value += '（已结束）';
	 } else {
	 value += '（进行中）';
	 }
	 return value;
	 }*/

	var formatActivityRewardBillType = function(type) {
		if(type == 1) {
			return '消费佣金';
		}
		if(type == 2) {
			return '盈亏佣金';
		}
	}

	var formatActivityRewardBillStatus = function(status) {
		if(status == 0) {
			return '待处理';
		}
		if(status == 1) {
			return '已发放';
		}
		if(status == -1) {
			return '已拒绝';
		}
	}

	var formatActivitySalaryBillType = function(type) {
		if(type == 1) {
			return '直属日工资';
		}
		if(type == 2) {
			return '总代日工资';
		}
	}

	var formatActivityBindBillStatus = function(status) {
		if(status == 0) {
			return '待处理';
		}
		if(status == 1) {
			return '已发放';
		}
		if(status == -1) {
			return '已拒绝';
		}
	}

	var formatActivityRechargeBillStatus = function(status) {
		if(status == 0) {
			return '待处理';
		}
		if(status == 1) {
			return '已发放';
		}
		if(status == -1) {
			return '已拒绝';
		}
	}

	var formatVipBirthdayGiftsStatus = function(status) {
		if(status == 0) {
			return '待处理';
		}
		if(status == 1) {
			return '已发放';
		}
		if(status == -1) {
			return '已拒绝';
		}
	}

	var formatVipBirthdayGiftsReceivedStatus = function(isReceived) {
		if(isReceived == 0) {
			return '未领取';
		}
		if(isReceived == 1) {
			return '已领取';
		}
	}

	var formatVipUpgradeGiftsStatus = function(status) {
		if(status == 0) {
			return '待处理';
		}
		if(status == 1) {
			return '已发放';
		}
		if(status == -1) {
			return '已拒绝';
		}
	}

	var formatDailySettleBillStatus = function(status) {
		// 1：已发放；2：部分发放；3：余额不足；4：未达标；5：已拒绝；
		if(status == 1) {
			return '<span style="color: #35AA47; ">已发放</span>';
		}
		if(status == 2) {
			return '部分发放';
		}
		if(status == 3) {
			return '余额不足';
		}
		if(status == 4) {
			return '未达标';
		}
		if(status == 5) {
			return '已拒绝';
		}
		return '';
	}

	var formatDailySettleStatus = function(status) {
		if(status == 1) {
			return '<span style="color: #35AA47; ">已生效</span>';
		}
		if(status == 2) {
			return '<span style="color: #FFB90F; ">待同意</span>';
		}
		if(status == 3) {
			return '<span style="color: #FF0000; ">已过期</span>';
		}
		if(status == 4) {
			return '<span style="color: #FF0000; ">无效</span>';
		}
		if(status == 5) {
			return '<span style="color: #FF0000; ">已拒绝</span>';
		}
		return '';
	}

	var formatDividendBillStatus = function(status) {
		if(status == 1) {
			return '<span style="color: #35AA47; ">已发放</span>';
		}
		if(status == 2) {
			return '<strong style="color: #FFB90F; ">待审核</strong>';
		}
		if(status == 3) {
			return '待领取';
		}
		if(status == 4) {
			return '<span style="color: #FF0000; ">已拒绝</span>';
		}
		if(status == 5) {
			return '未达标';
		}
		if(status == 6) {
			return '<span style="color: #0000FF; ">余额不足</span>';
		}
		if(status == 7) {
			return '<span style="color: #35AA47; ">部分发放</span>';
		}
		if(status == 8) {
			return '<span style="color: #FF0000; ">已过期</span>';
		}

		return '';
	}

	var formatGameDividendBillStatus = function(status) {
		if(status == 1) {
			return '<span style="color: #35AA47; ">已发放</span>';
		}
		if(status == 2) {
			return '<strong style="color: #FFB90F; ">待审核</strong>';
		}
		if(status == 3) {
			return '待领取';
		}
		if(status == 4) {
			return '<span style="color: #FF0000; ">已拒绝</span>';
		}
		if(status == 5) {
			return '未达标';
		}
		return '';
	}

	var formatDividendStatus = function(status) {
		if(status == 1) {
			return '<span style="color: #35AA47; ">已生效</span>';
		}
		if(status == 2) {
			return '<span style="color: #FFB90F; ">待同意</span>';
		}
		if(status == 3) {
			return '<span style="color: #FF0000; ">已过期</span>';
		}
		if(status == 4) {
			return '<span style="color: #FF0000; ">无效</span>';
		}
		if(status == 5) {
			return '<span style="color: #FF0000; ">已拒绝</span>';
		}
		return '';
	}

	var formatDividendBillIssueType = function(issueType) {
		if(issueType == 1) {
			return '平台发放';
		}
		if(issueType == 2) {
			return '上级发放';
		}
		return '';
	}

	var formatDailySettleBillIssueType = function(issueType) {
		if(issueType == 1) {
			return '平台发放';
		}
		if(issueType == 2) {
			return '上级发放';
		}
		return '';
	}

	var formatVipUpgradeGiftsReceivedStatus = function(isReceived) {
		if(isReceived == 0) {
			return '未领取';
		}
		if(isReceived == 1) {
			return '已领取';
		}
	}

	var formatGameDisplay = function(display) {
		if(display == 0) {
			return '隐藏中';
		}
		if(display == 1) {
			return '显示中';
		}
		return '';
	}

	var formatGameFlashSupport = function(flashSupport) {
		if(flashSupport == 0) {
			return '不支持';
		}
		if(flashSupport == 1) {
			return '支持';
		}
		return '';
	}

	var formatGameH5Support = function(h5Support) {
		if(h5Support == 0) {
			return '不支持';
		}
		if(h5Support == 1) {
			return '支持';
		}
		return '';
	}

	var formatGamePlatformStatus = function(status) {
		if(status == 0) {
			return '启用';
		}
		else {
			return '禁用';
		}
	}

	var formatUserGameWaterBillStatus = function(status) {
		if(status == 1) {
			return '<span style="color: #35AA47; ">已发放</span>';
		}
		if(status == 2) {
			return '<span style="color: #FF0000; ">已拒绝</span>';
		}
		return '';
	}

	var formatUserGameWaterBillType = function(type) {
		if(type == 1) {
			return '消费返水';
		}
		if(type == 2) {
			return '代理返水';
		}
		return '';
	}

	var formatUserHighPrizeStatus = function(status) {
		// 状态;0:待确认;1:已锁定;2:已确认
		if(status == 0) {
			return '待确认';
		}
		if(status == 1) {
			return '<strong style="color: #FFB90F; ">已锁定</strong>';
		}
		if(status == 2) {
			return '<strong style="color: #35AA47; ">已确认</strong>';
		}
		return '';
	}

	var formatUserHighPrizeTimes = function(times) {
		if(times <= 1) {
			return times;
		}
		if(times <= 3) {
			return '<strong style="color: #0000FF;">'+times+'</strong>';
		}
		if(times <= 5) {
			return '<strong style="color: #FF0000;">'+times+'</strong>';
		}
		if(times <= 10) {
			return '<strong style="color: #FFD700;font-weight: bold;font-size: 14px;">'+times+'</strong>';
		}
		return '<strong style="color: #800080;font-weight: bold;font-size: 16px;">'+times+'</strong>';
	}

	var formatGameStatus = function(status) {
		if(status == -1) {
			return '未知';
		}
		if(status == 1) {
			return '完成';
		}
		if(status == 2) {
			return '等待中';
		}
		if(status == 3) {
			return '进行中';
		}
		if(status == 4) {
			return '赢';
		}
		if(status == 5) {
			return '输';
		}
		if(status == 6) {
			return '平局';
		}
		if(status == 7) {
			return '拒绝';
		}
		if(status == 8) {
			return '退钱';
		}
		if(status == 9) {
			return '取消';
		}
		if(status == 10) {
			return '上半场赢';
		}
		if(status == 11) {
			return '上半场输';
		}
		if(status == 12) {
			return '和';
		}
		return '';
	}

	return {
		formatUserType: formatUserType,
		formatUserVipLevel: formatUserVipLevel,
		formatLevelUsers: formatLevelUsers,
		formatLevelUsers2: formatLevelUsers2,
		formatOnlineStatus: formatOnlineStatus,
		formatUserPlanLevel: formatUserPlanLevel,
		formatLotteryStatus: formatLotteryStatus,
		formatLotteryPlayRulesFixed: formatLotteryPlayRulesFixed,
		formatLotteryPlayRulesStatus: formatLotteryPlayRulesStatus,
        formatLotteryPlayRulesIsLocate: formatLotteryPlayRulesIsLocate,
		formatUserAStatus: formatUserAStatus,
		formatUserBStatus: formatUserBStatus,
		formatUserCardStatus: formatUserCardStatus,
		formatPaymentChannelType: formatPaymentChannelType,
		formatPaymentChannelCode: formatPaymentChannelCode,
		formatUserRechargeStatus: formatUserRechargeStatus,
		formatUserWithdrawStatus: formatUserWithdrawStatus,
		formatUserWithdrawLockStatus: formatUserWithdrawLockStatus,
		formatUserWithdrawLimitCheckType:formatUserWithdrawLimitCheckType,
		formatUserWithdrawCheckStatus: formatUserWithdrawCheckStatus,
		formatUserWithdrawRemitStatus: formatUserWithdrawRemitStatus,
		formatUserBetsModel: formatUserBetsModel,
		formatUserBetsStatus: formatUserBetsStatus,
		formatUserBetsPlanStatus: formatUserBetsPlanStatus,
		formatUserBillType: formatUserBillType,
		formatUserBillStatus: formatUserBillStatus,
		formatUserMessageStatus: formatUserMessageStatus,
		formatLotteryPaymentCardStatus: formatLotteryPaymentCardStatus,
		formatLotteryPaymentCreditsType: formatLotteryPaymentCreditsType,
		formatLotteryPaymentChannelStatus: formatLotteryPaymentChannelStatus,
		formatLotteryPaymentChannelBankStatus: formatLotteryPaymentChannelBankStatus,
		formatLotterySysNoticeType: formatLotterySysNoticeType,
		formatLotterySysNoticeStatus: formatLotterySysNoticeStatus,
		formatLotterySysNoticeSort: formatLotterySysNoticeSort,
		formatAdminUserStatus: formatAdminUserStatus,
		formatAdminUserRoleStatus: formatAdminUserRoleStatus,
		formatAdminUserGroupStatus: formatAdminUserGroupStatus,
		formatAdminUserActionStatus: formatAdminUserActionStatus,
		formatAdminUserMenuStatus: formatAdminUserMenuStatus,
		formatLotteryUserDividendStatus: formatLotteryUserDividendStatus,
		formatLotteryUserDividendBillStatus: formatLotteryUserDividendBillStatus,
		formatLotteryUserDividendBillReceivedStatus: formatLotteryUserDividendBillReceivedStatus,
		formatActivityRewardBillType: formatActivityRewardBillType,
		formatActivityRewardBillStatus: formatActivityRewardBillStatus,
		formatActivitySalaryBillType: formatActivitySalaryBillType,
		formatActivityBindBillStatus: formatActivityBindBillStatus,
		formatActivityRechargeBillStatus: formatActivityRechargeBillStatus,
		formatVipBirthdayGiftsStatus: formatVipBirthdayGiftsStatus,
		formatVipBirthdayGiftsReceivedStatus: formatVipBirthdayGiftsReceivedStatus,
		formatVipUpgradeGiftsStatus: formatVipUpgradeGiftsStatus,
		formatVipUpgradeGiftsReceivedStatus: formatVipUpgradeGiftsReceivedStatus,
		formatDailySettleBillStatus: formatDailySettleBillStatus,
		formatDailySettleStatus: formatDailySettleStatus,
		formatDividendBillStatus: formatDividendBillStatus,
		formatDividendStatus: formatDividendStatus,
		formatDividendBillIssueType: formatDividendBillIssueType,
		formatGameDisplay: formatGameDisplay,
		formatGameFlashSupport: formatGameFlashSupport,
		formatGameH5Support: formatGameH5Support,
		formatGamePlatformStatus: formatGamePlatformStatus,
		formatGameDividendBillStatus: formatGameDividendBillStatus,
		formatUserGameWaterBillStatus: formatUserGameWaterBillStatus,
		formatUserGameWaterBillType: formatUserGameWaterBillType,
		formatUserHighPrizeStatus: formatUserHighPrizeStatus,
		formatUserHighPrizeTimes: formatUserHighPrizeTimes,
		formatGameStatus: formatGameStatus,
		formatDailySettleBillIssueType: formatDailySettleBillIssueType
	}
}();

var PageHeader = function() {

	var header = $('.page-header-inner');

	/**
	 * 修改登录密码
	 */
	var AdminModLoginPwdModal = function() {
		var modal = $('#modal-admin-mod-login-pwd');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					oldPassword: {
						required: true
					},
					password1: {
						required: true,
						minlength: 6
					},
					password2: {
						required: true,
						minlength: 6,
						equalTo: $(form).find('input[name="password1"]')
					},
					googleCode: {
						required: true,
						digits: true,
						minlength: 6,
						maxlength: 6
					}
				},
				messages: {
					oldPassword: {
						required: '旧密码不能为空！'
					},
					password1: {
						required: '新密码不能为空！',
						minlength: '至少输入{0}个字符'
					},
					password2: {
						required: '请重复输入新密码！',
						equalTo: '两次密码不一致！',
						minlength: '至少输入{0}个字符'
					},
					googleCode: {
						required: '口令不能为空！',
						digits: '请输入纯数字',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					}
				},
				invalidHandler: function (event, validator) {},
				errorPlacement: function (error, element) {
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
				},
				highlight: function (element) {
					$(element).closest('.form-group').removeClass('has-success').addClass('has-error');
				},
				unhighlight: function (element) {
					$(element).closest('.form-group').removeClass('has-error').addClass('has-success');
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
				}
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
				}
			});
		}

		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var oldPassword = modal.find('input[name="oldPassword"]').val();
			var password = modal.find('input[name="password1"]').val();
			var googleCode = modal.find('input[name="googleCode"]').val();
			password = $.generatePassword(password);
			isSending = true;

			$.ajax({
				type : 'post',
				url : './DisposableToken',
				data : {},
				dataType : 'json',
				success : function(tokenData) {
					if(tokenData.error == 0) {
						oldPassword = $.encryptPasswordWithToken(oldPassword, tokenData.token);
						var params = {oldPassword: oldPassword, password: password, googleCode: googleCode};
						var url = './admin-user/mod-login-pwd';
						$.ajax({
							type : 'post',
							url : url,
							data : params,
							dataType : 'json',
							success : function(data) {
								isSending = false;
								if(data.error == 0) {
									modal.modal('hide');
									alert('密码修改成功！请重新登录！');
									window.location.href = '/login';
								}
								if(data.error == 1 || data.error == 2) {
									toastr['error']('密码修改失败！' + data.message, '操作提示');
								}
							}
						});
					}
					else {
						isSending = false;
						toastr['error']('请求超时，请重试！', '操作提示');
					}
				},
				error: function(){
					isSending = false;
					toastr['error']('请求失败，请重试！', '操作提示');
				}
			});
		}

		var show = function() {
			form[0].reset();
			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}

		var init = function() {
			initForm();
		}

		return {
			init: init,
			show: show
		}

	}();

	/**
	 * 修改资金密码
	 */
	var AdminModWithdrawPwdModal = function() {
		var modal = $('#modal-admin-mod-withdraw-pwd');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					oldPassword: {
						required: true
					},
					password1: {
						required: true,
						minlength: 6
					},
					password2: {
						required: true,
						minlength: 6,
						equalTo: $(form).find('input[name="password1"]')
					},
					googleCode: {
						required: true,
						digits: true,
						minlength: 6,
						maxlength: 6
					}
				},
				messages: {
					oldPassword: {
						required: '旧密码不能为空！'
					},
					password1: {
						required: '新密码不能为空！',
						minlength: '至少输入{0}个字符'
					},
					password2: {
						required: '请重复输入新密码！',
						equalTo: '两次密码不一致！',
						minlength: '至少输入{0}个字符'
					},
					googleCode: {
						required: '口令不能为空！',
						digits: '请输入纯数字',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					}
				},
				invalidHandler: function (event, validator) {},
				errorPlacement: function (error, element) {
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
				},
				highlight: function (element) {
					$(element).closest('.form-group').removeClass('has-success').addClass('has-error');
				},
				unhighlight: function (element) {
					$(element).closest('.form-group').removeClass('has-error').addClass('has-success');
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
				}
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
				}
			});
		}

		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var oldPassword = modal.find('input[name="oldPassword"]').val();
			var password = modal.find('input[name="password1"]').val();
			var googleCode = modal.find('input[name="googleCode"]').val();
			password = $.generatePassword(password);

			isSending = true;
			$.ajax({
				type : 'post',
				url : './DisposableToken',
				data : {},
				dataType : 'json',
				success : function(tokenData) {
					if(tokenData.error == 0) {
						oldPassword = $.encryptPasswordWithToken(oldPassword, tokenData.token);
						var params = {oldPassword: oldPassword, password: password, googleCode: googleCode};
						var url = './admin-user/mod-withdraw-pwd';

						$.ajax({
							type : 'post',
							url : url,
							data : params,
							dataType : 'json',
							success : function(data) {
								isSending = false;
								if(data.error == 0) {
									modal.modal('hide');
									toastr['success']('密码修改成功！', '操作提示');
								}
								if(data.error == 1 || data.error == 2) {
									toastr['error']('密码修改失败！' + data.message, '操作提示');
								}
							}
						});

					}
					else {
						isSending = false;
						toastr['error']('请求超时，请重试！', '操作提示');
					}
				},
				error: function(){
					isSending = false;
					toastr['error']('请求失败，请重试！', '操作提示');
				}
			});



		}

		var show = function() {
			form[0].reset();
			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}

		var initBrowserTools = function() {
			var tools = header.find('.top-toolbar .button-group');
			tools.find('a[data-command="back"]').unbind('click').click(function() {
				window.frames['main-frame'].history.go(-1);
			});
			tools.find('a[data-command="forward"]').unbind('click').click(function() {
				window.frames['main-frame'].history.go(1);
			});
		}

		var init = function() {
			initForm();
		}

		return {
			init: init,
			show: show
		}

	}();

	/**
	 * 设置Google动态密码
	 */
	var AdminModGooglePwdModal = function() {
		var modal = $('#modal-admin-mod-google-pwd');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					password: {
						required: true,
						number:true,
						minlength: 6
					},
					loginPassword: {
						required: true,
						minlength: 6
					},
				},
				messages: {
					password: {
						required: '密码不能为空！',
						number:'请输入有效的数字',
						minlength: '至少输入{0}个数字'
					},
					loginPassword: {
						required: '新密码不能为空！',
						minlength: '至少输入{0}个字符'
					}
				},
				invalidHandler: function (event, validator) {},
				errorPlacement: function (error, element) {
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
				},
				highlight: function (element) {
					$(element).closest('.form-group').removeClass('has-success').addClass('has-error');
				},
				unhighlight: function (element) {
					$(element).closest('.form-group').removeClass('has-error').addClass('has-success');
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
				}
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
				}
			});
		}

		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var password = modal.find('input[name="password"]').val();
			var loginPwd = modal.find('input[name="loginPassword"]').val();
			isSending = true;
			$.ajax({
				type : 'post',
				url : './DisposableToken',
				data : {},
				dataType : 'json',
				success : function(tokenData) {
					if(tokenData.error == 0) {

						loginPwd = $.encryptPasswordWithToken(loginPwd, tokenData.token);
						var params = { vCode: password, loginPwd:loginPwd};
						var url = './google-auth/authorize';

						$.ajax({
							type : 'post',
							url : url,
							data : params,
							dataType : 'json',
							success : function(data) {
								isSending = false;

								if(data.error == 0) {
									modal.modal('hide');
									toastr['success']('Google动态密码绑定成功！', '操作提示');
								} else {
									toastr['error'](data.message, '操作提示');
								}
							}
						});

					} else {
						isSending = false;
						toastr['error']('请求超时，请重试！', '操作提示');
					}
				},
				error: function(){
					isSending = false;
					toastr['error']('请求失败，请重试！', '操作提示');
				}
			});
		}

		var show = function() {
			var url = './google-auth/bind';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						form[0].reset();
						form.find('.help-inline').empty();
						form.find('.has-error').removeClass('has-error');
						form.find('.has-success').removeClass('has-success');
						modal.modal('show');

						var url = data.qr;
						var secret = data.secret;
						$('#googleQR').attr('src', url);
						$('#googleSecret').val(secret);
					}
					if(data.error == 1 || data.error == 2) {
						bootbox.dialog({
							message: data.message,
							title: '提示消息',
							buttons: {
								success: {
									label: '<i class="fa fa-check"></i> 确定',
									className: 'btn-success',
									callback: function() {}
								}
							}
						});
					}
				}
			});

		}

		var initBrowserTools = function() {
			var tools = header.find('.top-toolbar .button-group');
			tools.find('a[data-command="back"]').unbind('click').click(function() {
				window.frames['main-frame'].history.go(-1);
			});
			tools.find('a[data-command="forward"]').unbind('click').click(function() {
				window.frames['main-frame'].history.go(1);
			});
		}

		var init = function() {
			initForm();
		}

		return {
			init: init,
			show: show
		}

	}();


	/**
	 * 解锁资金密码
	 */
	var UnLockWithdrawPwdModal = function() {
		var modal = $('#modal-admin-unlock-withdraw-pwd');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					withdrawPwd: {
						required: true,
						minlength: 6
					},
					googleCode: {
						required: true,
						digits: true,
						minlength: 6,
						maxlength: 6
					}
				},
				messages: {
					withdrawPwd: {
						required: '密码不能为空！',
						minlength: '至少输入{0}个字符'
					},
					googleCode: {
						required: '口令不能为空！',
						digits: '请输入纯数字',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					}
				},
				invalidHandler: function (event, validator) {},
				errorPlacement: function (error, element) {
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
				},
				highlight: function (element) {
					$(element).closest('.form-group').removeClass('has-success').addClass('has-error');
				},
				unhighlight: function (element) {
					$(element).closest('.form-group').removeClass('has-error').addClass('has-success');
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
				}
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
				}
			});
		}

		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();
			var googleCode = modal.find('input[name="googleCode"]').val();

			isSending = true;
			$.ajax({
				type : 'post',
				url : './DisposableToken',
				data : {},
				dataType : 'json',
				success : function(tokenData) {
					if(tokenData.error == 0) {
						withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, tokenData.token);
						var params = {withdrawPwd: withdrawPwd, googleCode: googleCode};
						var url = './admin-user/unlock-withdraw-pwd';

						$.ajax({
							type : 'post',
							url : url,
							data : params,
							dataType : 'json',
							success : function(data) {
								isSending = false;
								if(data.error == 0) {
									modal.modal('hide');
									loadGlobal();
									toastr['success']('资金密码已解锁！', '操作提示');
								}
								if(data.error == 1 || data.error == 2) {
									toastr['error']('资金密码解锁失败！' + data.message, '操作提示');
								}
							}
						});

					}
					else {
						isSending = false;
						toastr['error']('请求超时，请重试！', '操作提示');
					}
				},
				error: function(){
					isSending = false;
					toastr['error']('请求失败，请重试！', '操作提示');
				}
			});

		}

		var show = function() {
			form[0].reset();
			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}

		var init = function() {
			initForm();
		}

		return {
			init: init,
			show: show
		}

	}();

	header.find('.dropdown .dropdown-menu .external > a').unbind('click').click(function() {
		var href = $(this).attr('data-href');
		if(href) {
			MainFrame.load(href);
		}
	});

	header.find('[data-command="mod-login-pwd"]').unbind('click').click(function() {
		AdminModLoginPwdModal.show();
	});

	header.find('[data-command="mod-withdraw-pwd"]').unbind('click').click(function() {
		AdminModWithdrawPwdModal.show();
	});

	header.find('[data-command="mod-google-pwd"]').unbind('click').click(function() {
		AdminModGooglePwdModal.show();
	});
	
	header.find('[data-command="play-withdraw-audio"]').unbind('click').click(function() {
		checkPlayWithdrawAudio();
	});
	
	
	
	header.find('[data-command="high-prize-audio"]').unbind('click').click(function() {
		checkHighPrizeAudio();
	});
	header.find('[data-command="unlock-withdraw"]').unbind('click').click(function() {
		UnLockWithdrawPwdModal.show();
	});
	header.find('[data-command="lock-withdraw"]').unbind('click').click(function() {
		var url = './admin-user/lock-withdraw-pwd';

		$.ajax({
			type : 'post',
			url : url,
			data : {},
			dataType : 'json',
			success : function(data) {
				isSending = false;
				if(data.error == 0) {
					loadGlobal();
					toastr['success']('资金密码已锁定！', '操作提示');
				}
				if(data.error == 1 || data.error == 2) {
					toastr['error']('资金密码锁定失败！' + data.message, '操作提示');
				}
			}
		});
	});

	var initUserInfo = function() {
		var url = './admin-user/info';
		$.ajax({
			type : 'post',
			url : url,
			data : {},
			dataType : 'json',
			success : function(response) {
				if(response.error == 0) {
					buildUserInfo(response.data);
				}
			}
		});
	}

	var buildUserInfo = function(data) {
		header.find('[data-field="username"]').html(data.bean.username + '（' + data.role + '）');
	}

	var audio = function() {
		$('audio#message').remove();
		var audio = $('<audio id="message" autoplay="autoplay">');
		audio.attr('src', 'theme/assets/custom/audio/message.mp3').hide();
		$('body').append(audio);
	}
	
	var audioReg = function() {
		$('audio#regmessage').remove();
		var audio = $('<audio id="regmessage" autoplay="autoplay">');
		audio.attr('src', 'theme/assets/custom/audio/audioReg.mp3').hide();
		$('body').append(audio);
	}
	
	var audioForHighPrize = function() {
		$('audio#highPrize').remove();
		var audio = $('<audio id="highPrize" autoplay="autoplay">');
		audio.attr('src', 'theme/assets/custom/audio/new-packet.mp3').hide();
		$('body').append(audio);
	}

	var checkPlayWithdrawAudio = function () {
		var a = header.find('[data-command="play-withdraw-audio"]');
		var icon = a.find("i");
		var text = a.find("span");
		var playing = !icon.hasClass("icon-control-play");
		if (playing) {
			text.html("播放线下充值、提款提示音");
			icon.removeAttr("class");
			icon.addClass("icon-control-play");
		}
		else {
			text.html("取消线下充值、提款提示音");
			icon.removeAttr("class");
			icon.addClass("icon-control-pause");
		}
		window.localStorage.setItem('PLAY_WITHDARW_AUDIO', !playing);
		checkForAllAudio();
	}

	var checkHighPrizeAudio = function () {
		var a = header.find('[data-command="high-prize-audio"]');
		var icon = a.find("i");
		var text = a.find("span");
		var playing = !icon.hasClass("icon-control-play");
		if (playing) {
			text.html("播放大额中奖提示音");
			icon.removeAttr("class");
			icon.addClass("icon-control-play");
		}
		else {
			text.html("取消大额中奖提示音");
			icon.removeAttr("class");
			icon.addClass("icon-control-pause");
		}
		window.localStorage.setItem('PLAY_HIGH_PRIZE_AUDIO', !playing);
		checkForAllAudio();
	}

	var initPlayWithdrawAudio = function () {
		// var playWithdrawAudio = $.cookie('PLAY_WITHDARW_AUDIO');
		var playWithdrawAudio = window.localStorage.getItem('PLAY_WITHDARW_AUDIO');
		var a = header.find('[data-command="play-withdraw-audio"]');
		var icon = a.find("i");
		var text = a.find("span");
		if (playWithdrawAudio == 'true') {
			text.html("取消线下充值、提款提示音");
			icon.removeAttr("class");
			icon.addClass("icon-control-pause");
		}
		else {
			text.html("播放线下充值、提款提示音");
			icon.removeAttr("class");
			icon.addClass("icon-control-play");
		}
		checkForAllAudio();
	}

	var initHighPrizeAudio = function () {
		var playHighPrizeAudio = window.localStorage.getItem('PLAY_HIGH_PRIZE_AUDIO');
		var a = header.find('[data-command="high-prize-audio"]');
		var icon = a.find("i");
		var text = a.find("span");
		if (playHighPrizeAudio == 'true') {
			text.html("取消大额中奖提示音");
			icon.removeAttr("class");
			icon.addClass("icon-control-pause");
		}
		else {
			text.html("播放大额中奖提示音");
			icon.removeAttr("class");
			icon.addClass("icon-control-play");
		}

		checkForAllAudio();
	}

	var checkForAllAudio = function() {
		var audios = header.find('[data-type="audios"]');

		var playWithDrawAudio = window.localStorage.getItem('PLAY_WITHDARW_AUDIO');
		var playHighPrizeAudio = window.localStorage.getItem('PLAY_HIGH_PRIZE_AUDIO');

		if (playWithDrawAudio == 'false' && (playHighPrizeAudio == '' || playHighPrizeAudio == undefined || playHighPrizeAudio == 'false')) {
			audios.find("[data-type='audios-icon']").removeClass().addClass("fa fa-bell-slash");
			return;
		}

		if (playHighPrizeAudio == 'true' && (playWithDrawAudio == '' || playWithDrawAudio == undefined || playWithDrawAudio == 'true')) {
			audios.find("[data-type='audios-icon']").removeClass().addClass("fa fa-bell");
			return;
		}

		audios.find("[data-type='audios-icon']").removeClass().addClass("fa fa-bell");
	}

	var setISUnlockedWithdrawPwd = function(unlocked) {
		var $lockWithdraw = header.find('[data-type="lock-withdraw"]');

		if (unlocked == true) {
			$lockWithdraw.find('[data-type="lock-icon"]').removeClass().addClass("fa fa-unlock");
			$lockWithdraw.find('[data-group="unlock"]').hide();
			$lockWithdraw.find('[data-group="lock"]').show();
		}
		else {
			$lockWithdraw.find('[data-type="lock-icon"]').removeClass().addClass("fa fa-lock");
			$lockWithdraw.find('[data-group="lock"]').hide();
			$lockWithdraw.find('[data-group="unlock"]').show();
		}
	}

	var lastOverload= 0;
	var lastWithdrawCount= 0;
	var lastRechargeCount = 0;
	var sessionTimeoutAlert = false;
	var loadGlobal = function() {
		var url = './global';
		$.ajax({
			type : 'post',
			url : url,
			data : {},
			dataType : 'json',
			success : function(response) {
				if (!response) {
					return;
				}

				if (response.error != 0) {
					if (response.code == '2-6') {
						if (sessionTimeoutAlert) {
							return;
						}

						sessionTimeoutAlert = true;

						bootbox.dialog({
							message: '由于您长时间未操作，请重新登录！',
							title: '提示消息',
							closeButton: false,
							buttons: {
								success: {
									label: '<i class="fa fa-check"></i> 确定',
									className: 'btn-success',
									callback: function() {
										window.location.href = '/login';
									}
								}
							}
						});
					}
					return;
				}
				header.find('[data-field="uOnlineCount"]').html(response.uOnlineCount);
				//header.find('[data-field="uOnlineCount"]').html(response.ubetsCount);

				header.find('[data-field="uWithdrawCount"]').html(response.uWithdrawCount);
				header.find('[data-field="bRegchargeCount"]').html(response.bRegchargeCount);
				header.find('[data-field="aBindCount"]').html(response.aBindCount);
				header.find('[data-field="aRechargeCount"]').html(response.aRechargeCount);

				header.find('[data-field="aTotalCount"]').html(response.aBindCount + response.aRechargeCount);

				header.find('[data-field="pCardOverload"]').html(response.pCardOverload);
				header.find('[data-field="pThridOverload"]').html(response.pThridOverload);
				var pOverloadCount = response.pCardOverload + response.pThridOverload;
				header.find('[data-field="pOverloadCount"]').html(pOverloadCount);
				if(response.uWithdrawCount > lastWithdrawCount) {
					// var playWithdrawAudio = $.cookie('PLAY_WITHDARW_AUDIO');
					var playWithdrawAudio = window.localStorage.getItem('PLAY_WITHDARW_AUDIO');
					if (playWithdrawAudio == 'true') {
						audio();
					}
				}
				
				if(response.bRegchargeCount > lastRechargeCount) {
					var playWithdrawAudio = window.localStorage.getItem('PLAY_WITHDARW_AUDIO');
					if (playWithdrawAudio == 'true') {
						audioReg();
					}
				}
				lastRechargeCount = response.bRegchargeCount;
				lastOverload = pOverloadCount;
				lastWithdrawCount = response.uWithdrawCount;

				setISUnlockedWithdrawPwd(response.isUnlockedWithdrawPwd);
			}
		});
	}

	var loadGlobalConfig = function() {
		var url = './admin-global-config';
		$.ajax({
			type : 'post',
			url : url,
			data : {},
			dataType : 'json',
			success : function(response) {
				header.find('img[data-field="logo"]').attr("src", cdnDomain + response.adminGlobalConfig.logo + "?v=" + cdnVersion);
			}
		});
	}

	var lastHighPrizeUnProcessCount = 0;
	var loadHighPrizeUnProcessCount = function() {
		var url = './high-prize-unprocess-count';
		$.ajax({
			type : 'post',
			url : url,
			data : {},
			dataType : 'json',
			success : function(response) {
				header.find('[data-field="highPrizeUnProcessCount"]').html(response.unProcessCount);
				if(response.unProcessCount > lastHighPrizeUnProcessCount) {
					var playHighPrizeAudio = window.localStorage.getItem('PLAY_HIGH_PRIZE_AUDIO');
					if (playHighPrizeAudio == 'true') {
						audioForHighPrize();
					}
				}
				lastHighPrizeUnProcessCount = response.unProcessCount;
			}
		});
	}

	var registerWebSocket = function() {
		var params = "type=1";
		WS.connect(params, function(data){
			var socketData = $.parseJSON(data);
			if (socketData.type === 1) {
				var msg;
				msg = '用户'+socketData.username+'在' + socketData.name + socketData.subName + '投注￥'
					+ socketData.money.toFixed(4) +'元中奖￥' + socketData.prizeMoney.toFixed(4) + ',奖金倍数:'
					+ DataFormat.formatUserHighPrizeTimes(socketData.times.toFixed(2))
					+ ',请在<大额中奖查询>中检查';

				// 大额中奖消息
				toastr['info'](msg);
			}
		});
	}

	return {
		init: function() {
			AdminModLoginPwdModal.init();
			AdminModWithdrawPwdModal.init();
			AdminModGooglePwdModal.init();
			UnLockWithdrawPwdModal.init();

			loadGlobalConfig();

			initUserInfo();
			// 20秒自动同步任务
			loadGlobal();
			setInterval(loadGlobal, 10 * 1000);
			initPlayWithdrawAudio();

			header.find('[data-type="highPrize"]').show();
			loadHighPrizeUnProcessCount();
			setInterval(loadHighPrizeUnProcessCount, 22 * 1000);
			initHighPrizeAudio();

			// 目前只有这一种websocket,所以暂时先放这里,以后加了其它的再改
			// registerWebSocket();
		}
	}
}();

var LZMAUtil = function() {

	var toHex = function(byte_arr) {
		var hex_str = '', i, len, tmp_hex;
		len = byte_arr.length;
		for (i = 0; i < len; ++i) {
			if (byte_arr[i] < 0) {
				byte_arr[i] = byte_arr[i] + 256;
			}
			tmp_hex = byte_arr[i].toString(16);
			if (tmp_hex.length === 1) {
				tmp_hex = '0' + tmp_hex;
			}
			hex_str += tmp_hex;
		}
		return hex_str.trim();
	}

	var fromHex = function(hexStr) {
		var pos = 0;
		var len = hexStr.length;
		if(len %2 != 0) {
			return null;
		}
		len /= 2;
		var hexA = new Array();
		for(var i=0; i<len; i++){
			var s = hexStr.substr(pos, 2);
			var v = parseInt(s, 16);
			hexA.push(v);
			pos += 2;
		}
		return hexA;
	}

	return {
		toHex: toHex,
		fromHex: fromHex
	}

}();

var ArrayUtil = function() {

	var min = function(arr) {
		var min = Number(arr[0]);
		var len = arr.length;
		for (var i = 1; i < len; i++){
			var tmp = Number(arr[i]);
			if (tmp < min){
				min = tmp;
			}
		}
		return min;
	}

	var max = function(arr) {
		var max = Number(arr[0]);
		var len = arr.length;
		for (var i = 1; i < len; i++){
			var tmp = Number(arr[i]);
			if (tmp > max) {
				max = tmp;
			}
		}
		return max;
	}

	var allSame = function(arr) {
		var from = arr[0];
		var len = arr.length;
		for (var i = 1; i < len; i++){
			if (arr[i] != from) {
				return false;
			}
		}
		return true;
	}

	// 组合数
	var ComNum = function(n, m) {
		m = parseInt(m);
		n = parseInt(n);
		if (m < 0 || n < 0) {
			return false;
		}
		if (m == 0 || n == 0) {
			return 1;
		}
		if (m > n) {
			return 0;
		}
		if (m > n / 2.0) {
			m = n - m;
		}
		var result = 0.0;
		for (var i = n; i >= (n - m + 1); i--) {
			result += Math.log(i);
		}
		for (var i = m; i >= 1; i--) {
			result -= Math.log(i);
		}
		result = Math.exp(result);
		return Math.round(result);
	}

	// 组合值
	var ComVal = function(source, m, x) {
		var n = source.length;
		var list = [];
		var start = 0;
		while (m > 0) {
			if (m == 1) {
				list.push(source[start + x]);
				break;
			}
			for (var i = 0; i <= n - m; i++) {
				var cnm = ComNum(n - 1 - i, m - 1);
				if (x <= cnm - 1) {
					list.push(source[start + i]);
					start = start + (i + 1);
					n = n - (i + 1);
					m--;
					break;
				} else {
					x = x - cnm;
				}
			}
		}
		return list;
	}

	// 判断是否存在
	var inArray = function(e, data) {
		for (var i = 0; i < data.length; i++) {
			if (data[i] == e) return true;
		}
		return false;
	}

	// 数组去重复
	var uniquelize = function(data) {
		var array = new Array();
		for (var i = 0; i < data.length; i++) {
			if (!inArray(data[i], array)) {
				array.push(data[i]);
			}
		}
		return array;
	}

	//求两个集合的并集
	var union = function(a, b) {
		return uniquelize(a.concat(b));
	}

	//求两个集合的差集
	var minus = function(a, b) {
		var array = new Array();
		var ua = uniquelize(a);
		for (var i = 0; i < ua.length; i++) {
			if(!inArray(ua[i], b)) {
				array.push(ua[i]);
			}
		}
		return array;
	}

	//求两个集合的交集
	var intersect = function(a, b) {
		var array = new Array();
		var ua = uniquelize(a);
		for (var i = 0; i < ua.length; i++) {
			if(inArray(ua[i], b)) {
				array.push(ua[i]);
			}
		}
		return array;
	}

	//求两个集合的补集
	var complement = function(a, b) {
		return minus(union(a, b), intersect(a, b));
	}

	// 去除重复，最快速方法，会排序
	var unique = function(data) {
		data.sort();
		var re = [data[0]];
		for(var i = 1; i < data.length; i++) {
			if(data[i] !== re[re.length - 1]) {
				re.push(data[i]);
			}
		}
		return re;
	}

	// 根据下标删除
	var remove = function(data, idx) {
		if(data.length > idx) {
			data.splice(idx, 1);
		}
		return data;
	}

	return {
		ComNum: ComNum,
		ComVal: ComVal,
		unique: unique,
		uniquelize: uniquelize,
		intersect: intersect,
		complement: complement,
		remove: remove,
		min: min,
		max: max,
		allSame: allSame
	}

}();

$(function(){
	function showTooltip() {
		if (window.tippy) {
			tippy(".tippy", {
				// position: 'top', // top bottom left right
				animation: 'shift', // shift perspective fade scale
				arrow: true,
				interactive: true,
				// interactiveBorder: 2,
				animateFill: true,
				theme: 'dark', // dark light transparent yourname
				hideOnClick: false,
				// followCursor: true
				touchHold: true,
				size: 'big',
				delay: 100
				// dynamicTitle: true
				// onShow: function(){},
				// onShown: function(){},
				// onHide: function(){},
				// onHidden: function(){},
				// wait: function(){}
			});
		}
	}

	showTooltip();

	$.showTooltip = function () {
		showTooltip();
	}

	$.isDigits = function(value) {
		var reg = /^[0-9]+$/ ;
		return reg.test(value);
	}

	// 判断是否存在
	var inArray = function(e, data) {
		for (var i = 0; i < data.length; i++) {
			if (data[i] == e) return true;
		}
		return false;
	}

	// 数组去重复
	$.uniquelize = function(data) {
		var array = new Array();
		for (var i = 0; i < data.length; i++) {
			if (!inArray(data[i], array)) {
				array.push(data[i]);
			}
		}
		return array;
	}

	// 乘法
	$.floatMul = function(arg1,arg2){
		var m=0,s1=arg1.toString(),s2=arg2.toString();
		try{m+=s1.split(".")[1].length}catch(e){}
		try{m+=s2.split(".")[1].length}catch(e){}
		return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m)
	}
	//加法
	$.floatAdd = function(arg1,arg2){
		var r1,r2,m;
		try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
		try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}
		m=Math.pow(10,Math.max(r1,r2))
		return (arg1*m+arg2*m)/m
	}
	//减法
	$.floatSub = function(arg1,arg2){
		var r1,r2,m,n;
		try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
		try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}
		m=Math.pow(10,Math.max(r1,r2));
		n=(r1>=r2)?r1:r2;
		return ((arg1*m-arg2*m)/m).toFixed(n);
	}

	$.dateDiff = function(strFrom, strTo){
		if (strFrom == null || strTo == null) return 0;
		var dateFrom = new Date(strFrom);
		var dateTo = new Date(strTo);
		var diff = dateTo.valueOf() - dateFrom.valueOf();
		var diff_day = parseInt(diff/(1000*60*60*24));
		return diff_day;
	}

	$.encryptPasswordWithToken = function(plainStr, token) {
		var password = hex_md5(plainStr).toUpperCase();
		password = hex_md5(password).toUpperCase();
		password = hex_md5(password).toUpperCase();
		password = hex_md5(password + token).toUpperCase();
		return password;
	}

	$.generatePassword = function(plainStr) {
		var password = hex_md5(plainStr).toUpperCase();
		password = hex_md5(password).toUpperCase();
		return password;
	}

	$.getDisposableToken = function(){
		var token = null;
		$.ajax({
			type : 'post',
			url : './DisposableToken',
			data : {},
			dataType : 'json',
			async: false,
			success : function(tokenData) {
				if(tokenData.error == 0) {
					token = tokenData.token;
				}
				else {
					toastr['error']('请求超时，请重试！', '操作提示');
					token = null;;
				}
			},
			error: function(){
				toastr['error']('请求失败，请重试！', '操作提示');
				token = null;;
			}
		});
		return token;
	}
	
	$.isEmpty = function(val) {
		if (/^\s+$/.test(val) || val.length < 1) {
			return true;
		}

		return false;
	}
});

var BigDecimal = function(){
	// 使用截取设置最大精度，不使用四舍五入
	var setMaxScale = function(value, maxScale) {
		if (value == 0) {
			return 0;
		}
		var valStr = value.toString();
		var nums = valStr.split(".");
		if(nums.length == 1){
			return value;
		}
		if (nums.length>1) {
			if(nums[1].length > maxScale){
				var end = nums[0].length + 1 + maxScale;
				return parseFloat(valStr.substring(0, end));
			}
			return value;
		}
		return value;
	}

	return {
		setMaxScale: setMaxScale
	}
}();