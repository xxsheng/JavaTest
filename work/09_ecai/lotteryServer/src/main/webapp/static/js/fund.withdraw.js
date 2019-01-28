$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $amount;
    var $withdrawPwd;
    var $withdrawBank;
    var withdrawData;

    function init() {
        // 初始化
        initContent();
    }

    // 初始化充值通道
    function initContent() {
        $.YF.showLoadingMask();
        
        $.ajax({
            url : '/LoadUserWithdrawals',
            success : function(res) {
                if (res.error === 0) {
                   	if(res.code =='2-13'){
                	    $.YF.alert_warning(res.message, function(){
                        });
                		   return;
                	}
                    if (!res.data.hasWithdrawPwd) {
                        $.YF.alert_warning('请先设置资金密码再进行提款操作！', function(){
                            self.window.location = '/account-manager';
                        })
                        return;
                    }
                    if (res.data.cList.length < 1) {
                        $.YF.alert_warning('请先绑定银行卡再进行提款操作！', function(){
                            self.window.location = '/account-card';
                        })
                        return;
                    }
                    
                    withdrawData = res.data;
                    $contentBox.html(tpl('#withdraw_content_tpl', res.data));
                    $amount = $contentBox.find('input[name=amount]');
                    $withdrawPwd = $contentBox.find('input[name=withdrawPwd]');
                    $withdrawBank = $contentBox.find('select[name=withdrawBank]');
                    $.YF.initInputForceDigit($amount);
                }
            },
            complete: function(){
                $.YF.hideLoadingMask();
            }
        })
    }

    // 提交
    $contentBox.off('click', '[data-command=submit]');
    $contentBox.on('click', '[data-command=submit]', function(){
        if (!withdrawData) {
            $.YF.alert_warning('数据加载中，请稍安勿躁！')
            return;
        }

        layer.closeAll('tips');

        var amount = $amount.val();
        if ($.YF.isEmpty(amount)) {
            $amount.focus();
            $.YF.alert_tooltip('请输入提款金额', $amount);
            return;
        }
        if (!$.YF.isDigits(amount)) {
            $.YF.alert_tooltip('提款金额请输入整数', $amount);
            return;
        }
        amount = parseInt(amount);
        if (amount > Number(withdrawData.availableMoney)) {
            $amount.focus();
            $.YF.alert_tooltip('您的可提款余额不足', $amount);
            return;
        }
        if (amount < Number(withdrawData.minWithdrawals) || amount > withdrawData.maxWithdrawals) {
            $amount.focus();
            $.YF.alert_tooltip('单次提款范围为：' + withdrawData.minWithdrawals + '-' + withdrawData.maxWithdrawals, $amount);
            return;
        }

        var withdrawBank = $withdrawBank.find('option:selected');
        if (!withdrawBank) {
            $.YF.alert_tooltip('请选择您要提款的银行卡', $withdrawBank);
            return;
        }

        var withdrawPwd = $withdrawPwd.val();
        if ($.YF.isEmpty(withdrawPwd)) {
            $withdrawPwd.focus();
            $.YF.alert_tooltip('请输入资金密码', $withdrawPwd);
            return;
        }

        $.YF.showLoadingMask();

        var token = $.YF.getDisposableToken();
        withdrawPwd = $.YF.encryptPasswordWithToken(withdrawPwd, token);

        var data = {amount : amount, cid: withdrawBank.val(), withdrawPwd: withdrawPwd};

        $.ajax({
            url: '/ApplyUserWithdrawals',
            data: data,
            success: function(res) {
                if (res.error === 0) {
                    $.YF.alert_success('您的提现请求已提交，请等待工作人员处理！', function(){
                        self.window.location.reload();
                    });
                    layer.closeAll('tips');
                }
                else {
                    $.YF.alert_warning(res.message)
                }
            },
            complete: function() {
                $.YF.hideLoadingMask();
            }
        })
    });

    // 初始化
    init();
})