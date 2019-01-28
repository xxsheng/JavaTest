$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);

    function init() {
        // 初始化银行卡列表
        refreshBankCards();
    }

    // 初始化银行卡列表
    function refreshBankCards() {
        $.ajax({
            url : '/ListUserCard',
            success : function(res) {
                if (res.error === 0) {
                    $contentBox.html(tpl('#account_card_bank_card_tpl', {rows: res.data, cdnDomain: cdnDomain}))
                }
            }
        })
    }

    // 添加银行卡
    function addCard() {
        console.log('asdfasdfasdf');
        $.ajax({
            url : '/GetBindUserCardNeed',
            success : function(res) {
                if (res.error != 0) {
                    $.YF.alert_warning(res.message);
                }
                if (!res.data.bindCardName || res.data.bindCardName == null || res.data.bindCardName == '') {
                    $.YF.alert_warning('请先绑定账户姓名！');
                    return;
                }

                if (res.data.count >= 5) {
                    $.YF.alert_warning('每人最多绑定5张银行卡！');
                    return;
                }

                var html = tpl('#add_bank_card_tpl', {rows: res.data.bankList, bindCardName: res.data.bindCardName});

                var $dom;
                swal({
                    title: '添加银行卡',
                    customClass:'popup',
                    html: html,
                    width: 560,
                    showCloseButton: true,
                    showCancelButton: true,
                    showConfirmButton: true,
                    confirmButtonText: '确认',
                    cancelButtonText: '关闭',
                    buttonsStyling: false,
                    confirmButtonClass: 'popup-btn-confirm',
                    cancelButtonClass: 'popup-btn-cancel',
                    allowEnterKey: false,
                    onBeforeOpen: function (dom) {
                        $dom = $(dom);

                        var $cardId = $("input[name=cardId]", $dom);
                        var $reCardId = $("input[name=reCardId]", $dom);
                        $.YF.initInputForceDigit($cardId);
                        $.YF.initInputForceDigit($reCardId);
                    },
                    onOpen: function(){
                    },
                    showLoaderOnConfirm: true,
                    preConfirm: function(){
                        return new Promise(function (resolve, reject) {
                            submitAddCard($dom, function(){
                                resolve();
                            }, function(msg){
                                reject(msg);
                            });
                        })
                    }
                }).then(function() {
                    refreshBankCards();
                    $.YF.alert_success('银行卡添加成功！');
                }, function() {
                })
            },
            complete: function() {
            }
        })
    }

    function submitAddCard($dom, resolve, reject) {
        var $bankId = $("select[name=bankId]", $dom);
        var $bankBranch = $("input[name=bankBranch]", $dom);
        var $cardId = $("input[name=cardId]", $dom);
        var $reCardId = $("input[name=reCardId]", $dom);
        var $withdrawPwd = $("input[name=withdrawPwd]", $dom);

        var bankId = $bankId.val();
        var bankBranch = $bankBranch.val();
        var cardId = $cardId.val();
        var reCardId = $reCardId.val();
        var withdrawPwd = $withdrawPwd.val();

        if ($.YF.isEmpty(bankId)) {
            reject('请选择开户银行！');
            return;
        }
        if ($.YF.isEmpty(bankBranch)) {
            $bankBranch.focus();
            reject('请输入支行名称！');
            return;
        }
        if (bankBranch.length < 4) {
            $bankBranch.focus();
            reject('支行名称最少输入4位！');
            return;
        }
        if ($.YF.isEmpty(cardId)) {
            $cardId.focus();
            reject('请输入银行卡号！');
            return;
        }
        if (!$.YF.isDigits(cardId)) {
            $cardId.focus();
            reject('请输入正确的银行卡号！');
            return;
        }
        if (cardId.length < 10 || cardId.length > 28) {
            $cardId.focus();
            reject('银行卡号长度为10-28位！');
            return;
        }
        if ($.YF.isEmpty(reCardId)) {
            $reCardId.focus();
            reject('请输入确认卡号！');
            return;
        }
        if (cardId != reCardId) {
            $reCardId.focus();
            reject('两次输入银行卡号不一致，请检查！');
            return;
        }
        if ($.YF.isEmpty(withdrawPwd)) {
            $withdrawPwd.focus();
            reject('请输入资金密码！');
            return;
        }

        var token = $.YF.getDisposableToken();
        withdrawPwd = $.YF.encryptPasswordWithToken(withdrawPwd, token);
        var data = {bankId : bankId, bankBranch: bankBranch, cardId: cardId, withdrawPwd : withdrawPwd};

        $.ajax({
            url: '/BindUserCard',
            data: data,
            success: function(res) {
                if (res.error == 0) {
                    resolve();
                }
                else {
                    if (res.code == '2-1009') {
                        $withdrawPwd.focus();
                    }
                    reject(res.message);
                }
            }
        })
    }

    // 解绑银行卡
    function unbindCard(e) {
        var id = $(e.target).closest('div.jt_bot').attr('data-id');

        swal({
            title: '温馨提示',
            text: '确认要解绑此银行卡吗？',
            width: 460,
            type: 'question',
            showCloseButton: false,
            showCancelButton: true,
            showConfirmButton: true,
            confirmButtonText: '确认',
            cancelButtonText: '关闭',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel',
            showLoaderOnConfirm: true,
            preConfirm: function(){
                return new Promise(function (resolve, reject) {
                    submitUnbindCard(id, function(){
                        resolve();
                    }, function(msg){
                        reject(msg);
                    });
                })
            }
        }).then(function() {
            refreshBankCards();
            $.YF.alert_success('解绑银行卡成功！');
        }, function() {
        })
    }

    function submitUnbindCard(id, resolve, reject) {
        $.ajax({
            url: '/UserUnBindCard',
            data: {id: id},
            success: function(res) {
                if (res.error == 0) {
                    resolve();
                }
                else {
                    reject(res.message);
                }
            }
        })
    }

    // 添加银行卡
    $contentBox.on('click', '[data-command=addCard]', addCard);

    // 解绑银行卡
    $contentBox.on('click', '[data-command=unbindCard]', unbindCard);

    // 初始化
    init();
})