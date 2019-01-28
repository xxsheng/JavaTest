var userId = "";
$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $amount;
    var $transferOut;
    var $transferIn;

    function init() {
        // 初始化
        initContent();
    }

    // 初始化充值通道
    function initContent() {
        $.YF.showLoadingMask();
        
        $.ajax({
            url : '/LoadUserTransfers',
            success : function(res) {
                if (res.error === 0) {
                    $contentBox.html(tpl('#transfer_content_tpl', {rows: res.data.listPlatform}));
                    $amount = $contentBox.find('input[name=amount]');
                    $transferOut = $contentBox.find('select[name=transferOut]');
                    $transferIn = $contentBox.find('select[name=transferIn]');
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
        layer.closeAll('tips');

        var amount = $amount.val();
        if ($.YF.isEmpty(amount)) {
            $amount.focus();
            $.YF.alert_tooltip('请输入转换金额', $amount);
            return;
        }
        if (!$.YF.isDigits(amount)) {
            $amount.focus();
            $.YF.alert_tooltip('转换金额请输入整数', $amount);
            return;
        }
        amount = parseInt(amount);

        if (amount < 1) {
            $amount.focus();
            $.YF.alert_tooltip('请输入正确的数值', $amount);
            return;
        }

        var fromAccount = $transferOut.val();
        var toAccount = $transferIn.val();

        if (fromAccount == toAccount) {
            $.YF.alert_tooltip('转出与转入账户不能相同', $transferOut);
            return;
        }

        $.YF.showLoadingMask();

        var data = {fromAccount : fromAccount, toAccount: toAccount, amount: amount};

        $.ajax({
            url: '/SelfUserTransfers',
            data: data,
            success: function(res) {
                if (res.error === 0) {
                    $.YF.alert_success('您的资金已经转到指定账户！', function(){
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


    $("input[name='userName']").blur(function () {
        var name = $(this).val().trim();
        if(name==''||name.length==0){
            userId = "";
            return ;
        }
        $.ajax({
            url: '/GetUserOther',
            type: 'POST',
            data: {
                username: name
            },
            success: function(res) {
                if (res.status) {
                    userId = res.userId;
                }else{
                    userId = "";
                    $.YF.alert_tooltip('该用户名不存在', $("input[name='userName']"));
                }
            }
        })
    });

})

function submitTrans(){
    if(userId==''||userId.length==0){
        $.YF.alert_tooltip('用户名为空或不存在！', $("input[name='userName']"));
        return ;
    }


    var token = $.YF.getDisposableToken();
    var withdrawPwd = $('#withdrawPwd').val();
    var amount = $('#amount').val();
    withdrawPwd = $.YF.encryptPasswordWithToken(withdrawPwd, token);
    if (!$.YF.isDigitsZZS(amount)) {
        $.YF.alert_tooltip('请输入正确的数值！', $('#amount'));
        $('#amount').focus();
        return;
    }
    $.ajax({
        url: '/transferSave',
        type: 'POST',
        data: {
            userId: userId,
            withdrawPwd: withdrawPwd,
            amount: amount
        },
        success: function(res) {
            if(res.code=='2-1009'){
                $.YF.alert_tooltip(res.message, $('#withdrawPwd'));
            }else{
                $.YF.alert_success(res.message,function () {
                    window.location.reload();
                });
            }
        }
    })
}