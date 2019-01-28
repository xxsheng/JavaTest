$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $userLevels = $("#userLevels", $content);
    var $scope;
    var $username;
    var $minMoney;
    var $maxMoney;
    var $listTable;
    var $listPager;

    function init() {
        // 初始化内容
        initContentBox();
    }

    function initContentBox() {
        $contentBox.html(tpl('#proxy_team_content_tpl', {}));

        $listTable = $contentBox.find('table tbody');
        $listPager = $contentBox.find('.paging');

        $scope = $contentBox.find('select[name=scope]');
        $username = $contentBox.find('input[name=username]');
        $minMoney = $contentBox.find('input[name=minMoney]');
        $maxMoney = $contentBox.find('input[name=maxMoney]');

        $.YF.initInputForceDigit($minMoney);
        $.YF.initInputForceDigit($maxMoney);

        // 分页
        $listPager.off('click', '.pager');
        $listPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            search($target.attr('data-num'))
        })

        search(0);
    }

    // 初始化列表
    function search(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/ProxyUserSearch',
            data: {
                limit: 10,
                start: page * 10,
                username: $username.val(),
                minMoney: $minMoney.val(),
                maxMoney: $maxMoney.val(),
                scope: $scope.val()
            },
            success : function(res) {
                if (res.error === 0) {
                    $listTable.html(tpl('#proxy_team_list_tpl', {rows: res.data}))

                    setUserLevels(res.userLevels);

                    $listPager.html(tpl('#pager_tpl', {
                        index: Number(page),
                        pageSize: 10,
                        total: res.totalCount,
                        totalPage: Math.ceil(res.totalCount / 10)
                    }))
                }
            },
            complete: function(){
                $.YF.hideLoadingMask();
            }
        })
    }

    function setUserLevels(userLevels) {
        $userLevels.html('层级关系：');
        $.each(userLevels, function(index, val){
            $userLevels.append('<em data-command="showLowers" data-user="'+val+'"> '+val+'</em>');
            if (index < userLevels.length - 1) {
                $userLevels.append(' >');
            }
        });
    }

    $contentBox.on('click', '[data-command=search]', function(){
        search(0);
    });

    $content.on('click', '[data-command=showLowers]', function(e){
        var $target = $(e.target);
        var username = $target.attr('data-user');
        $username.val(username);
        search(0);
    });

    // 升点
    $contentBox.on('click', '[data-command=quota_point]', function(e){
        var username = $(e.target).closest('tr').attr('data-username');

        $.YF.showLoadingMask();

        $.ajax({
            url: '/LoadProxyEditPoint',
            data: {
                username: username
            },
            success: function(res) {
                if(res.error === 0) {
                    if(res.data.lBean.locatePoint + 0.1 > res.data.uCode.maxLocatePoint) {
                        $.YF.alert_warning('无法修改该用户返点信息！');
                        return
                    }
                    var param = $.extend(true, res.data, {username: username});
                    var html = tpl('#edit_lower_point_tpl', param);

                    var $dom;
                    swal({
                        title: '升点',
                        customClass:'popup',
                        html: html,
                        width: 460,
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

                            // 初始化拉条
                            var $slider = $('.slider-bar', $dom);
                            var $codeText = $('[data-property=code]', $dom);
                            var $codeInput = $('input[name=code]', $dom);
                            initSlider($slider, $codeText, $codeInput, param.lBean.code, param.uCode.code, param.lBean.code, 2);
                        },
                        onOpen: function(){
                        },
                        showLoaderOnConfirm: true,
                        preConfirm: function(){
                            return new Promise(function (resolve, reject) {
                                submitQuotaPoint($dom, function(){
                                    resolve();
                                }, function(msg){
                                    reject(msg);
                                });
                            })
                        }
                    }).then(function() {
                        $.YF.alert_success('操作成功，下级返点已变更！', function(){
                            var page = $listPager.find('.cur').attr('data-num');
                            search(page);
                        })
                    }, function() {
                    })
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
                $.YF.hideLoadingMask();
            }
        })

    });

    function initSlider($slider, $codeText, $codeInput, defaultCode, maxCode, minCode, step) {
    	//TODO bojin 定制
    	if(maxCode > 1990){
    		maxCode = 1990;
    	}
        $slider.empty();
        $slider.append('<div class="slider"></div>');
        var $sliderBar = $slider.find('.slider');

        $sliderBar.noUiSlider({ connect: 'lower', behaviour: 'snap', step: step, start: defaultCode, range: { 'max': maxCode, 'min': minCode } });
        var update = function(code) {
            $codeText.html(code);
            $codeInput.val(code);
        }
        var onSlide = function() {
            var code = Number($sliderBar.val());
            update(code);
        }
        $sliderBar.on({ set: onSlide, slide: onSlide });
        update(defaultCode);
    }

    function submitQuotaPoint($dom, resolve, reject) {
        var code = $("input[name=code]", $dom).val();
        var username = $("input[name=username]", $dom).val();

        var data = {code : code, username: username};

        $.ajax({
            url: '/ProxyEditUserPoint',
            data: data,
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

    // 转账
    $contentBox.on('click', '[data-command=recharge]', function(e){
        var username = $(e.target).closest('tr').attr('data-username');

        $.YF.showLoadingMask();

        $.ajax({
            url: '/LoadProxyRecharge',
            data: {
                username: username
            },
            success: function(res) {
                if(res.error === 0) {
                    if(!res.data.hasSecurity) {
                        $.YF.alert_warning('您还没有绑定密保问题，无法转账！');
                        return;
                    }
                    if(!res.data.hasWithdrawPwd) {
                        $.YF.alert_warning('您还没有设置资金密码，无法转账！');
                        return;
                    }
                    if(!res.data.allowTransfers) {
                        $.YF.alert_warning('上下级转账已关闭，请联系客服开通！');
                        return;
                    }
                    
                /*    if(!res.data.hasBindGoogle) {
                        $.YF.alert_warning('请先绑定谷歌身份验证器！');
                        return;
                    }*/

                    var html = tpl('#recharge_to_member_tpl', $.extend(true, res.data, {username: username}));

                    var $dom;
                    swal({
                        title: '转账',
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
                            var $amount = $("input[name=amount]", $dom);
                            //var $vCode = $("input[name=vCode]", $dom);
                            $.YF.initInputForceDigit($amount);
                         //   $.YF.initInputForceDigit($vCode);
                        },
                        onOpen: function(){
                        },
                        showLoaderOnConfirm: true,
                        preConfirm: function(){
                            return new Promise(function (resolve, reject) {
                                submitRecharge($dom, function(){
                                    resolve();
                                }, function(msg){
                                    reject(msg);
                                });
                            })
                        }
                    }).then(function() {
                        $.YF.alert_success('转账成功，您的资金已转到指定账号！', function(){
                            var page = $listPager.find('.cur').attr('data-num');
                            search(page);
                        })
                    }, function() {
                    })
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
                $.YF.hideLoadingMask();
            }
        })
    });

    function submitRecharge($dom, resolve, reject) {
        var $answer = $("input[name=answer]", $dom);
        var answer = $answer.val();
        if ($.YF.isEmpty(answer)) {
            $.YF.alert_tooltip('请输入密保答案！', $answer);
            $answer.focus();
            reject();
            return;
        }

        var $transferType = $("select[name=transferType]", $dom);
        var transferType = $transferType.val();
        if ($.YF.isEmpty(transferType)) {
            $.YF.alert_tooltip('请选择转账类型！', $transferType);
            reject();
            return;
        }

        var $amount = $("input[name=amount]", $dom);
        var amount = $amount.val();
        if ($.YF.isEmpty(amount)) {
            $.YF.alert_tooltip('请输入充值金额！', $amount);
            $amount.focus();
            reject();
            return;
        }
        if (!$.YF.isDigitsZZS(amount)) {
            $.YF.alert_tooltip('请输入正确的数值！', $amount);
            $amount.focus();
            reject();
            return;
        }

        var $withdrawPwd = $("input[name=withdrawPwd]", $dom);
        var withdrawPwd = $withdrawPwd.val();
        if ($.YF.isEmpty(withdrawPwd)) {
            $.YF.alert_tooltip('请输入资金密码！', $withdrawPwd);
            $withdrawPwd.focus();
            reject();
            return;
        }

/*        var $vCode = $("input[name=vCode]", $dom);
        var vCode = $vCode.val();
        if ($.YF.isEmpty(vCode)) {
            $.YF.alert_tooltip('请输入谷歌口令！', $vCode);
            $vCode.focus();
            reject();
            return;
        }
        if (!$.YF.isDigitsZZS(vCode)) {
            $.YF.alert_tooltip('请输入正确的数值！', $vCode);
            $vCode.focus();
            reject();
            return;
        }*/

        layer.closeAll('tips');

        var username = $("input[name=username]", $dom).val();
        var sid = $("input[name=sid]", $dom).val();
        var token = $.YF.getDisposableToken();
        answer = $.YF.encryptPasswordWithToken(answer, token);
        withdrawPwd = $.YF.encryptPasswordWithToken(withdrawPwd, token);
        var data = {sid : sid, answer : answer, transferType: transferType, amount: amount, withdrawPwd : withdrawPwd, username : username};

        $.ajax({
            url: '/ProxyUserRecharge',
            data: data,
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

    // 初始化
    init();
})