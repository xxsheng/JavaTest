$.holdReady(true); // 锁住所有ready方法，加载模板文件
var url = cdnDomain + '/static/template/index_template.html?v=' + cdnVersion;
$.ajax({
    type: "GET",
    url: url,
    cache: true,
    complete: function (res) {
        $("#index_template_tpl").html(res.responseText);
        $.holdReady(false);
    }
});

$(function () {
    var $doc = $(document);
    var $lotteryMenu = $("#lottery-menu", $doc);
    var $indexiframe = $("#iframe", $doc);

    function init() {
        // 初始化彩票导航
        initLotteries();

        $indexiframe.height($(window).height());
        $(window).resize(function () {
            $indexiframe.height($(window).height())
        });
    }

    // 初始化彩票导航
    function initLotteries() {
        // 根据链接默认展开菜单栏
        var link = $.YF.getUrlParam(window.location.search, 'link');
        var defaultOpenType = '';
        var defaultOpenLottery = '';
        var defaultLotteryShortName = '';
        if (link && link.indexOf('lottery') > -1) {
            defaultOpenLottery = link;
            defaultLotteryShortName = link.split('-')[1];
        }

        $.ajax({
            url: '/GetLottery',
            success: function (res) {
                var hots = new Array();
                var sscs = new Array();
                var x5s = new Array();
                var k3s = new Array();
                var pk10s = new Array();
                var sds = new Array();
                var kl8s = new Array();

                $.each(res.data, function (index, lottery) {
                    if (defaultLotteryShortName == lottery.shortName) {
                        defaultOpenType = lottery.type;
                    }

                    if (lottery.status === 0) {

                        if (lottery.shortName == 'cqssc' || lottery.shortName == 'txffc' || lottery.shortName == 'xjssc' || lottery.shortName == 'gd11x5' || lottery.shortName == 'bjpk10') {
                            hots.push(lottery);
                        }


                        if (lottery.type == 1 || lottery.type == 7) {
                            sscs.push(lottery);
                        }
                        else if (lottery.type == 2) {
                            x5s.push(lottery);
                        }
                        else if (lottery.type == 3) {
                            k3s.push(lottery);
                        }
                        else if (lottery.type == 6) {
                            pk10s.push(lottery);
                        }
                        else if (lottery.type == 4) {
                            sds.push(lottery);
                        }
                        else if (lottery.type == 5) {
                            kl8s.push(lottery);
                        }
                    }
                })

                $lotteryMenu.html(tpl('#lottery_menu_tpl', {
                    hots: hots,
                    sscs: sscs,
                    x5s: x5s,
                    k3s: k3s,
                    pk10s: pk10s,
                    sds: sds,
                    kl8s: kl8s,
                    cdnDomain: cdnDomain,
                    cdnVersion: cdnVersion
                }));

                // 类型点击
                $lotteryMenu.off('click', '.lottery dt');
                $lotteryMenu.on('click', '.lottery dt', function () {
                    if ($(this).hasClass("op")) {
                        $(".lottery dd").slideUp();
                        $(".lottery dt em i").html("&#xe515;");
                        $(".lottery dt").removeClass("op");
                        $(this).removeClass("op");
                        $(this).next("dd").slideUp();
                        $(this).find("em i").html("&#xe515;");
                    } else {
                        $(".lottery dd").slideUp();
                        $(".lottery dt em i").html("&#xe515;");
                        $(".lottery dt").removeClass("op");
                        $(this).addClass("op");
                        $(this).next("dd").slideDown();
                        $(this).find("em i").html("&#xe503;");
                    }
                });

                // 彩种点击
                $lotteryMenu.off('click', '.lottery dd a');
                $lotteryMenu.on('click', '.lottery dd a', function () {
                    $lotteryMenu.find('.lottery dd a').removeClass('current');
                    $(this).addClass("current");
                })

                // 滚动条
                $lotteryMenu.find('.menu-list').mCustomScrollbar({
                    scrollInertia: 70,
                    autoHideScrollbar: true,
                    theme: "dark-thin",
                    advanced: {
                        updateOnContentResize: true
                    }
                });

                // 关于我们
                $lotteryMenu.off('click', '.menu dt span[data-command=showAboutUS]');
                $lotteryMenu.on('click', '.menu dt span[data-command=showAboutUS]', function () {
                    if (top.window) {
                        var $this = $(this);
                        var id = $this.attr('data-id');
                        top.window.showAboutUs(id);
                    }
                });

                // 常见问题
                $lotteryMenu.off('click', '.menu dt span[data-command=showQuestion]');
                $lotteryMenu.on('click', '.menu dt span[data-command=showQuestion]', function () {
                    if (top.window) {
                        var $this = $(this);
                        var id = $this.attr('data-id');
                        top.window.showQuestion(id);
                    }
                });

                // $lotteryMenu.on('click', '.menu dt span[data-command=transfer]', function () {
                //     window.location.href="transfer-account";
                // });

                /*if (defaultOpenType  && defaultOpenLottery) {
                    $('.lottery[data-type='+defaultOpenType+'] dt', $lotteryMenu).addClass("op");
                    $('.lottery[data-type='+defaultOpenType+'] dd', $lotteryMenu).show();
                    $('.lottery[data-type='+defaultOpenType+'] em i', $lotteryMenu).html("&#xe503;");

                    $('.lottery[data-type='+defaultOpenType+'] dd a[href='+defaultOpenLottery+']').addClass("current");
                }
                else {
                    $('.lottery[data-type=hot] dt', $lotteryMenu).addClass("op");
                    $('.lottery[data-type=hot] dd', $lotteryMenu).show();
                    $('.lottery[data-type=hot] em i', $lotteryMenu).html("&#xe503;");
                }*/


                var $userInfo = $("#menu-userInfo", $doc);
                var $bjoper = $("#bjoper", $doc);

                var $appupload = $("#appupload", $doc);

                $appupload.off('click');
                $appupload.on('click', function () {
//                	var showhtml = '<div class="showappimg"><img href="../imgages/ewm.png"></img></div>';
                    var showhtml = '<div id="" class="layui-layer-content" style="height: 400px;">' +
                        '<div class="xz_box">' +
                        '<div class="xz_cent">' +
                        '<div class="xz_1">WINonline手机APP</div>' +
                        '<div class="xz_2">当别人还沉寂在敲打键盘进行游戏时丶我们已经将时下最火的彩票、真人视讯、捕鱼、电子游艺等装进了口袋，让您真正体验裤兜里的快乐。</div>' +
                        '</div>' +
                        '<div class="erweima">' +
                        '<img src="../static/images/ewm.png?v=1.0.2">' +
                        '</div>' +
                        '<div class="neirong">' +
                        '<p class="tishi">扫描二维码立即下载</p>' +
                        '</div>' +
                        '</div>' +
                        '</div>';
                    swal({
                        customClass: 'popup',
                        title: '手机APP下载',
                        html: showhtml,
                        width: 680,
                        height: 400,
                        showCloseButton: true,
                        showCancelButton: false,
                        showConfirmButton: false,
                        buttonsStyling: false,
                        confirmButtonClass: 'popup-btn-confirm',
                        cancelButtonClass: 'popup-btn-cancel',
                        allowEnterKey: false,
                        showLoaderOnConfirm: true
                    }).then(function () {
                    }, function () {
                    })
                });

                // 点击刷新
                $userInfo.off('click', "[data-command=refreshBalance]");
                $userInfo.on('click', "[data-command=refreshBalance]", function () {
                    loadTodayReport(true);
                });

                $bjoper.off('click', "[data-command=btnExit]");
                $bjoper.on('click', "[data-command=btnExit]", confirmLogout);

                function confirmLogout() {
                    $.YF.alert_question('确认退出吗？', logout, function () {
                        setTimeout(function () {
                            $.removeCookie('USER_NAME');
                            top.window.location = '/logout';
                        }, 100);
                    })
                }

                function logout(resolve, reject) {
                    resolve();
                }


                // 加载报表
                var isLoadingTodayReport = false;

                function loadTodayReport(delayLoading) {
                    if ($.YF.isForceLogout()) {
                        return;
                    }
                    if (isLoadingTodayReport) {
                        return;
                    }

                    isLoadingTodayReport = true;

                    var $summary = $userInfo.find('#summary');
                    $summary.find('[data-property=lotteryBalance]').html('Loading...');
                    $summary.find('[data-property=unSettleMoney]').html('Loading...');
                    $summary.find('[data-property=lotteryBet]').html('Loading...');
                    $userInfo.find('[data-property=balanceCash]').html('Loading...');
                    $userInfo.find('[data-field="nickname"]').html('欢迎您：Loading...');

                    $.ajax({
                        url: '/UserTodayReport',
                        data: {},
                        success: function (res) {
                            if (res.error == 0) {
                                if ($.YF.isProxy(res.data.user.type)) {
                                    $(window.parent.document).find("div[name=proxy]").show();
                                    // $rightBanner.find('[data-type=proxy]').show();
                                }
                                if (delayLoading) {
                                    setTimeout(function () {
                                        var lotteryMoney = $.YF.formatMoney(res.data.user.lotteryMoney);
                                        var unSettleMoney = $.YF.formatMoney(res.data.unSettleMoney);
                                        var lotteryBet = $.YF.formatMoney(res.data.lotteryReport.billingOrder);

                                        $summary.find('[data-property=lotteryBalance]').html(lotteryMoney).attr('title', lotteryMoney);
                                        $summary.find('[data-property=unSettleMoney]').html(unSettleMoney).attr('title', unSettleMoney);
                                        $summary.find('[data-property=lotteryBet]').html(lotteryBet).attr('title', lotteryBet);
                                    }, 1000);
                                }
                                else {
                                    var lotteryMoney = $.YF.formatMoney(res.data.user.lotteryMoney);
                                    var unSettleMoney = $.YF.formatMoney(res.data.unSettleMoney);
                                    var lotteryBet = $.YF.formatMoney(res.data.lotteryReport.billingOrder);

                                    $summary.find('[data-property=lotteryBalance]').html(lotteryMoney).attr('title', lotteryMoney);
                                    $summary.find('[data-property=unSettleMoney]').html(unSettleMoney).attr('title', unSettleMoney);
                                    $summary.find('[data-property=lotteryBet]').html(lotteryBet).attr('title', lotteryBet);
                                }
                            }
                            else {
                                $.YF.alert_warning(res.message);
                            }
                        },
                        complete: function () {
                            setTimeout(function () {
                                isLoadingTodayReport = false;
                            }, 5000);
                        }
                    });
                    $.ajax({
                        url: '/GetGlobal',
                        data: {},
                        success: function (res) {
                            if (res.error == 0) {
                                setTimeout(function () {
                                    var totalBalance = $.YF.formatMoney(res.data.uBean.totalMoney + res.data.uBean.lotteryMoney);
                                    $userInfo.find('[data-property=balanceCash]').html(totalBalance).attr('title', totalBalance);
                                    $userInfo.find('[data-field="nickname"]').html('欢迎您：' + res.data.uBean.username);
                                }, 1000);
                            }
                        },
                        complete: function () {
                        }
                    })
                }

                // 加载今天报表信息
                loadTodayReport(false);
                setInterval(function () {
                    loadTodayReport(true);
                }, 20000);
            }
        });
    }

    // 初始化
    init();
})