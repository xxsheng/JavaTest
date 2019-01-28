$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $newestNotice = $("#newestNotice", $content);
    var $mangerTop = $("#manger-top", $content);
    var $managerBalance = $("#manager-balance", $content);
    var $greeting = $("[data-property=greeting]", $mangerTop);
    var $nickName = $("[data-property=nickName]", $mangerTop);
    var $code = $("[data-property=code]", $mangerTop);
    var $securityLevel = $("[data-property=securityLevel]", $mangerTop);
    var $securityLevelIcon = $("[data-property=securityLevelIcon]", $mangerTop);
    var $totalBalance = $("[data-property=totalBalance]", $mangerTop);
    
    var $mainBalance = $("[data-property=mainBalance]", $managerBalance);
    var $lotteryBalance = $("[data-property=lotteryBalance]", $managerBalance);

    function init() {
        // 初始化最新公告
        initNewestNotice();

        // 初始化基本信息
        initBase();

        // 初始化安全等级
        initSecurity();

        // 滚动条
        $content.mCustomScrollbar({
            scrollInertia: 70,
            autoHideScrollbar: true,
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });
    }

    // 初始化最新公告
    function initNewestNotice() {
        $.ajax({
            url: '/SysNoticeLastSimple',
            success: function(res) {
                if (res.error == 0 && res.data) {
                    $newestNotice.html('最新公告：' + res.data.simpleContent);

                    $newestNotice.off('click').on('click', function(){
                        if (top.window) {
                            top.window.popupNotices(res.data.id);
                        }
                    });
                }
            }
        });
    }

    function initBase() {
        refreshBase(false);
    }

    // 刷新自己平台余额
    var refreshingBase = false;
    function refreshBase(delay) {
        if (refreshingBase == true) {
            return;
        }
        refreshingBase =  true;
        
        $totalBalance.html('Loading...');
        $mainBalance.html('Loading...');
        $lotteryBalance.html('Loading...');
        
        $.ajax({
            url: '/GetUserBase',
            success: function(res) {
                if (res.error == 0) {
                    // 昵称
                    var greeting = $.YF.greeting();
                    $greeting.html(greeting + '，');
                    $nickName.html(res.data.nickname);
                    // 账号等级
                    $code.html(res.data.code);

                    var totalBalance = $.YF.formatMoney(res.data.totalMoney + res.data.lotteryMoney);
                    var mainBalance = $.YF.formatMoney(res.data.totalMoney);
                    var lotteryBalance = $.YF.formatMoney(res.data.lotteryMoney);
                    
                    if (delay == true) {
                        setTimeout(function(){
                            $totalBalance.html(totalBalance);
                            $mainBalance.html(mainBalance);
                            $lotteryBalance.html(lotteryBalance);
                            refreshingBase = false;
                        }, 1000);
                    }
                    else {
                        $totalBalance.html(totalBalance);
                        $mainBalance.html(mainBalance);
                        $lotteryBalance.html(lotteryBalance);
                        refreshingBase = false;
                    }
                }
            }
        });
    }

    // 点击查看游戏平台余额
    var refreshingPlatformBalance = false;
    function refreshPlatformBalance() {
        if (refreshingPlatformBalance == true) {
            return;
        }
        refreshingPlatformBalance = true;
        var $this = $(this);
        var platform = $this.attr('data-platform');
        $this.html('Loading...');

        $.ajax({
            url: '/AccountBalance',
            data: {platformId: platform},
            success: function (res) {
                if (res.error === 0) {
                    setTimeout(function(){
                        var balance = $.YF.formatMoney(res.data.balance);
                        $this.html(balance);
                        refreshingPlatformBalance = false;
                    }, 1000);
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function () {
            },
            error: function () {
            }
        });
    }

    // 修改昵称
    function modifyNickName(){
        var oldNickName = $nickName.html();
        var updatedNickName;
        swal({
            title: '请输入您想修改的昵称',
            width: 320,
            showCloseButton: false,
            showCancelButton: true,
            showConfirmButton: true,
            confirmButtonText: '修改',
            cancelButtonText: '关闭',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel',
            showLoaderOnConfirm: true,
            input: 'text',
            inputPlaceholder: '请输入您想修改的昵称',
            inputAutoTrim: true,
            inputAttributes: {
                autocomplete: 'off'
            },
            inputValue: oldNickName,
            showLoaderOnConfirm: true,
            inputValidator: function (value) {
                return new Promise(function (resolve, reject) {
                    if (oldNickName == value) {
                        return reject('请输入一个新的昵称！');
                    }

                    if ($.YF.isEmpty(value)) {
                        return reject('请输入您想修改的昵称！');
                    }
                    if (value.length > 12 || value.length < 4) {
                        return reject('只能输入4~12位字符，汉字占2个字符！');
                    }
                    updatedNickName = value;
                    doModifyNickName(value, resolve, reject);
                })
            }
        }).then(function() {
            $nickName.html(updatedNickName);
            if (top.window && top.window.refreshGlobal) {
                top.window.refreshGlobal(null, false);
            }
        }, function() {
        })
    }

    function doModifyNickName(value, resolve, reject) {
        $.ajax({
            url: '/ModUserNickname',
            data: {nickname: value},
            success: function(res) {
                if(res.error == 0) {
                    resolve();
                }
                else {
                    reject(res.message);
                }
            }
        })
    }

    // 初始化安全等级
    function initSecurity() {
        $.ajax({
            url : '/GetUserBind',
            success : function(res) {
                if (res.error === 0) {
                    var score = calculateSecurityScore(res.data);
                    var level = calculateSecurityLevel(score);
                    var clazz = calculateSecurityLevelClass(score);

                    $securityLevelIcon.css('width', score + '%');
                    $securityLevel.removeClass().addClass(clazz).html(' ' + level);
                }
            }
        })
    }

    // 计算安全等级分数
    function calculateSecurityScore(data) {
        var score = 0;
        if (data.isLoginValidate && data.isLoginValidate === true) {
            score += 20;
        }
        if (data.hasWithdrawName && data.hasWithdrawName === true) {
            score += 20;
        }
        if (data.hasWithdrawPwd && data.hasWithdrawPwd === true) {
            score += 20;
        }
        if (data.hasSecurity && data.hasSecurity === true) {
            score += 20;
        }
        if (data.hasBindGoogle && data.hasBindGoogle === true) {
            score += 20;
        }
        return score;
    }

    // 计算安全等级
    function calculateSecurityLevel(score) {
        if (score < 60) {
            return '低';
        }
        if (score < 100) {
            return '中';
        }
        return '高';
    }

    // 计算安全等级class
    function calculateSecurityLevelClass(score) {
        if (score < 60) {
            return 'low';
        }
        if (score < 100) {
            return 'medium';
        }
        return 'high';
    }

    // 点击刷新余额
    $content.on('click', '[data-command=refreshBalance]', function(){
        refreshBase(true);
    });

    // 点击查看余额
    $managerBalance.on('click', '[data-command=refreshPlatformBalance]', refreshPlatformBalance);

    // 点击修改昵称
    $mangerTop.on('click', '[data-command=modifyNickName]', modifyNickName);

    // 全局刷新安全等级
    window.refreshSecurityLevel = initSecurity

    // 初始化
    init();
})