$.holdReady(true); // 锁住所有ready方法，加载模板文件
var url = cdnDomain + '/static/template/main_template.html?v=' + cdnVersion;
$.ajax({
    type: "GET",
    url: url,
    cache: true,
    complete: function(res) {
        $("#main_template_tpl").html(res.responseText);
        $.holdReady(false);
    }
});
$(function(){
    var $doc = $(document);
    var $mainiframe = $("mainiframe", $doc);
    var $header = $(".header", $doc);
    var $nav = $("#nav", $doc);
    var $headerRight = $("#header-right", $doc);
    var $promotion = $("#promotion", $headerRight);
    var $promotionBox = $("#promotion-box", $doc);
    var $aboutusBox = $("#aboutus-box", $doc);
    var $questionBox = $("#question-box", $doc);
    var $appdownload = $("#appdownload", $nav);
    var $phoneTc = $(".phone_tc", $nav);
    var $screenMask = $("#screenMask", $doc);
    var $noticeBanner = $("#notice_banner", $headerRight);
    var $topBalance = $("#topBalance", $headerRight);
    var $msgCount = $("#msgCount", $headerRight);
    
    var $noticeBox = $("#noticeBox", $doc);
    
    var noticesData = [];
    var beating = false;
    var refreshingBalance = false;
    var kefuUrl;
    var isShowNotices = true;

    function init() {
        $mainiframe.height($(window).height()-$header.height());
        $(window).resize(function(){$mainiframe.height($(window).height()-$header.height())});

        // 初始化APP下载
        initAppDownload();

        // 初始化优惠活动
        initPromotion();

        // 初始化关于我们
        initAbout();
        
        // 初始化常见问题
        initQuestion();

        // 初始化Banner公告
        initBannerNotices();
        
        // 初始化余额、消息等
        initGlobal();

        // 初始化客服地址
        initKefu();

        $("[data-command=disabled]", $nav).click(function(){
            $.YF.alert_message('温馨提示', '即将开放，敬请期待！')
        });

        $promotionBox.mCustomScrollbar({
            scrollInertia: 70,
            autoHideScrollbar: false,
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });

        $aboutusBox.mCustomScrollbar({
            scrollInertia: 70,
            autoHideScrollbar: false,
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });

        $questionBox.mCustomScrollbar({
            scrollInertia: 70,
            autoHideScrollbar: false,
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });
    }

    // 初始化APP下载
    function initAppDownload() {
        $phoneTc.html(tpl('#app_download_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));

        $appdownload.click(function(){
            if ($phoneTc.is(':visible')) {
                $phoneTc.fadeOut('fast');
            }
            else {
                $phoneTc.fadeIn('fast');
            }
        })
        $("[data-command=close]", $phoneTc).click(function(){
            $phoneTc.fadeOut('fast');
        })
    }

    // 初始化优惠活动
    function initPromotion() {
    	
        $promotionBox.html(tpl('#promotion_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));

        $promotion.click(function(){
            showPromotion();
        })

        $("[data-command=close]", $promotionBox).unbind('click').click(function(){
            $screenMask.hide();
            $promotionBox.animate({"right":"-950px"}, 500);
        })

        $screenMask.click(function(){
            $screenMask.hide();
            $promotionBox.animate({"right":"-950px"}, 500);
        })
        
        // 幸运大转盘活动
        $("[data-command=drawWheel]", $promotionBox).unbind('click').click(function(){
            var $this = $(this);
            if ($this.hasClass('disabled')) {
                return;
            }

            $promotionBox.find('[data-group="draw"]').show();
        })
        // 幸运大转盘活动
        $("[data-command=closeWheel]", $promotionBox).unbind('click').click(function(){
            var $this = $(this);
            if ($this.hasClass('disabled')) {
                return;
            }
            
            $promotionBox.find('[data-group="draw"]').hide();
        })
        // 幸运大转盘活动
        $("[data-command=startDraw]", $promotionBox).unbind('click').click(function(){
            var $this = $(this);
            if ($this.hasClass('disabled')) {
                return;
            }
            
            startDrawWheel();
        })
    }

    var rotateTimeOut = function (){
        $promotionBox.find('.zp2').rotate({
            angle:0,
            animateTo:2160,
            duration:12000,
            callback:function (){
                alert('网络超时，请检查您的网络设置！');
            }
        });
    };
    var bRotate = false;
    var rotateFn = function (awards, angles, money){
        bRotate = !bRotate;
        $promotionBox.find('.zp2').stopRotate();
        $promotionBox.find('.zp2').rotate({
            angle:0,
            animateTo:angles+1800,
            duration:12000,
            callback:function (){
                bRotate = !bRotate;

                $.YF.alert_success("恭喜您中奖" + money + "元！", function(){
                    $("[data-command=startDraw]", $promotionBox).removeClass('disabled');
                    $promotionBox.find('[data-group="draw"]').hide();
                    $screenMask.hide();
                    $promotionBox.animate({"right":"-950px"}, 500);
                })
            }
        })
    };

    function startDrawWheel() {
        if(bRotate)return;
        
        $("[data-command=startDraw]", $promotionBox).addClass('disabled');
        
        $.ajax({
            url: '/ActivityWheelDraw',
            success: function(res) {
                if (res.error != 0) {
                    $.YF.alert_warning(res.message);
                    return;
                }
        
                var money = parseInt(res.amount);
                if (money <= 0) {
                    $.YF.alert_warning("抽奖失败，请联系客服！");
                    return;
                }
        
                var rotateTo = -1;
                switch (money) {
                    case 18: rotateTo = 611; break;
                    case 28: rotateTo = 576; break;
                    case 88: rotateTo = 392; break;
                    case 128: rotateTo = 320; break;
                    case 168: rotateTo = 467; break;
                    case 288: rotateTo = 644; break;
                    case 518: rotateTo = 542; break;
                    case 888: rotateTo = 505; break;
                    case 1688: rotateTo = 429; break;
                    case 2888: rotateTo = 355; break;
                }
                if (rotateTo <= -1) {
                    $.YF.alert_success("恭喜您中奖" + money + "元！", function(){
                        $("[data-command=startDraw]", $promotionBox).removeClass('disabled');
                        $promotionBox.find('[data-group="draw"]').hide();
                        $screenMask.hide();
                        $promotionBox.animate({"right":"-950px"}, 500);
                    })
                }
                else {
                    rotateFn(0, rotateTo, money);
                }
            }
        });
    }

    function showPromotion(type) {
        // 幸运大转盘活动
        loadWheelData(function(){
            $promotionBox.find('[data-group="draw"]').hide();
            $screenMask.show();
            $promotionBox.animate({"right":"0px"}, 500);
        });
    }
    
    function loadWheelData(callback) {
        $.ajax({
            url: '/LoadActivityWheel',
            success: function(res) {
                if (res.error != 0) {
                    $.YF.alert_warning(res.message);
                    return;
                }

                if (res.data.enabled == true) {
                    $promotionBox.find('[data-property="status"]').removeClass().addClass('enable').html('进行中');
                    
                    if (res.data.todayDrew == true) {
                        $promotionBox.find('[data-command="drawWheel"]').removeClass().addClass('btn-gray-big disabled').html('今日已参与');
                    }
                    else if (res.data.todayDrew == false) {
                        if (res.data.remainCost <= 0) {
                            $promotionBox.find('[data-command="drawWheel"]').removeClass().addClass('btn-yellow-big').html('立即抽奖');
                        }
                        else {
                            $promotionBox.find('[data-command="drawWheel"]').removeClass().addClass('btn-gray-big disabled').html('消费未达标');
                        }
                    }
                }
                else {
                    $promotionBox.find('[data-property="status"]').removeClass().addClass('pause').html('暂停中');
                    $promotionBox.find('[data-command="drawWheel"]').removeClass().addClass('btn-gray-big disabled').html('活动未开启');
                }

                $promotionBox.find('[data-property="todayCost"]').html($.YF.formatMoney(res.data.todayCost));
                $promotionBox.find('[data-property="remainCost"]').html($.YF.formatMoney(res.data.remainCost));
                $promotionBox.find('[data-property="todayPrize"]').html($.YF.formatMoney(res.data.todayPrize));
                
                if (callback && $.isFunction(callback)) {
                    callback(res);
                }
            }
        });
    }

    // 初始化关于我们
    function initAbout() {
        $aboutusBox.html(tpl('#about_us_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));

        $("[data-command=close]", $aboutusBox).click(function(){
            $screenMask.hide();
            $aboutusBox.animate({"right":"-950px"}, 500);
        })

        $("[data-command=changeTab]", $aboutusBox).click(function(){
            var id = $(this).attr('data-id');
            showAboutUs(id);
        })

        $screenMask.click(function(){
            $screenMask.hide();
            $aboutusBox.animate({"right":"-950px"}, 500);
        })
    }

    function showAboutUs(id) {
        $screenMask.show();
        $aboutusBox.animate({"right":"0px"}, 500);

        $aboutusBox.find('.tcc_left ul li .screen-left-btn').removeClass('cur');
        $aboutusBox.find('.tcc_right').hide();

        if (!id) {
            $aboutusBox.find('.tcc_left ul li').eq(0).find('.screen-left-btn').addClass('cur');
            $aboutusBox.find('.tcc_right').eq(0).show();
        }
        else {
            $aboutusBox.find('.tcc_left ul li[data-id='+id+']').find('.screen-left-btn').addClass('cur');
            $aboutusBox.find('.tcc_right[data-id='+id+']').show();
        }
    }

    // 初始化常见问题
    function initQuestion() {
        $questionBox.html(tpl('#question_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));

        $("[data-command=close]", $questionBox).click(function(){
            $screenMask.hide();
            $questionBox.animate({"right":"-950px"}, 500);
        })

        $("[data-command=changeTab]", $questionBox).click(function(){
            var id = $(this).attr('data-id');
            showQuestion(id);
        })

        $screenMask.click(function(){
            $screenMask.hide();
            $questionBox.animate({"right":"-950px"}, 500);
        })
    }

    function showQuestion(id) {
        $screenMask.show();
        $questionBox.animate({"right":"0px"}, 500);

        $questionBox.find('.tcc_left ul li .screen-left-btn').removeClass('cur');
        $questionBox.find('.tcc_right').hide();

        if (!id) {
            $questionBox.find('.tcc_left ul li').eq(0).find('.screen-left-btn').addClass('cur');
            $questionBox.find('.tcc_right').eq(0).show();
        }
        else {
            $questionBox.find('.tcc_left ul li[data-id='+id+']').find('.screen-left-btn').addClass('cur');
            $questionBox.find('.tcc_right[data-id='+id+']').show();
        }
    }

    // 初始化Banner公告
    function initBannerNotices() {
        if (window.forcelogout && window.forcelogout === true) {
            return;
        }

        $.ajax({
            url: '/SysNoticeList',
            success: function(res) {
                if (res.error == 0 && res.data && res.data.length > 0) {
                    noticesData = res.data;

                    checkUnReadForNotices();

                    var unReadCount = 0;
                    $.each(noticesData, function(index, notice){
                        if (notice.read == false) {
                            unReadCount++;
                        }
                    });

                    var html = tpl('#notice_banner_tpl', {notices: noticesData, unReadCount: unReadCount});
                    $noticeBanner.html(html);

                    // 滚动条
                    $(".notice_box ul", $noticeBanner).mCustomScrollbar({
                        scrollInertia: 70,
                        autoHideScrollbar: true,
                        theme:"dark-thin",
                        advanced: {
                            updateOnContentResize: true
                        }
                    });

                    // 全部设为已读
                    $noticeBanner.off("click", "[data-command=setAllRead]");
                    $noticeBanner.on("click", "[data-command=setAllRead]", function(){
                        setAllReadForNotices();
                    });

                    // 查看全部公告
                    $noticeBanner.off("click", "[data-command=more]");
                    $noticeBanner.on("click", "[data-command=more]", function(){
                        popupNotices(noticesData.length <= 0 ? null : noticesData[0].id);
                    });

                    // 查看单条公告
                    $noticeBanner.off("click", "[data-command=details]");
                    $noticeBanner.on("click", "[data-command=details]", function(e){
                        var $target = $(e.target);
                        var id = $target.attr('data-id');
                        popupNotices(id);
                    });

                    // 弹出第一条未读公告
                    setTimeout(popupFirstUnRead, 500);
                }
                else {
                    noticesData = [];
                    setAllReadForNotices();
                }
                
                if(isShowNotices){
                	$.ajax({
                        url: '/SysNoticeLastSimple',
                        success: function(res) {
                            if (res.error == 0 && res.data) {
                            	 isShowNotices = false;
                                 popupNotices(res.data.id);
                            }
                        }
                    });
                }
            }
        });
    }
    
    function checkUnReadForNotices() {
        if (noticesData.length <= 0){
            return;
        }

        var setUnreadTag = false;
        $.each(noticesData, function(index, notice){
            var newsMD5 = hex_md5(notice.id + notice.title + notice.date);
            var news = window.localStorage.getItem('NOTICE_' + newsMD5);

            if (!news) {
                notice.read = false;
                if (setUnreadTag == false) {
                    $headerRight.find(".news .title .unread").show();
                    setUnreadTag = true;
                }
            }
            else {
                notice.read = true;
            }
        });
    }

    function setAllReadForNotices() {
        if (noticesData.length <= 0){
            return;
        }


        $.each(noticesData, function(index, notice){
            var newsMD5 = hex_md5(notice.id + notice.title + notice.date);
            var news = window.localStorage.getItem('NOTICE_' + newsMD5);

            if (!news) {
                notice.read = true;
                window.localStorage.setItem('NOTICE_' + newsMD5, "true");
            }
        });

        $noticeBanner.find('.notice_top .nb-color').html(' 0 ');
        $noticeBanner.find('.notice_box .unread').hide();
        $headerRight.find(".news .title .unread").hide();
    }

    function setBannerNoticeRead(id) {
        var bannerLi = $noticeBanner.find('li[data-id='+id+']');
        if (!bannerLi) {
            return;
        }

        // 去掉banner的未读标识
        var $unRead = bannerLi.find('.unread');
        if (!$unRead) {
            return;
        }

        $unRead.remove();


        var readCount = 0;
        $.each(noticesData, function(index, notice){
            if (notice.id == id && notice.read == false) {
                var newsMD5 = hex_md5(notice.id + notice.title + notice.date);
                notice.read = true;
                window.localStorage.setItem('NOTICE_' + newsMD5, "true");

                var beforeUnReadCount = $noticeBanner.find('.nb-color').html();
                if (beforeUnReadCount > 0) {
                    // 设置未读条数
                    $noticeBanner.find('.nb-color').html(' '+(parseInt(beforeUnReadCount-1))+' ');
                }
            }

            if (notice.read == true) {
                readCount++;
            }
        });

        if (readCount >= noticesData.length) {
            $headerRight.find(".news .title .unread").hide();
        }
    }

    function popupFirstUnRead() {
        if (noticesData.length <= 0){
            return;
        }

        var notice = noticesData[0];

        if (notice && notice.read == false) {
            popupNotices(notice.id);
        }
    }

    var noticesDom = false;
    function popupNotices(chkId) {
        if (noticesDom) {
            var $li = noticesDom.find('li[data-id='+chkId+']');

            if ($li.hasClass('current')) {
                return;
            }

            // 收起所有
            noticesDom.find('li').removeClass('current').find('.notice_popup_content').stop(true, true).hide();

            var $scroll = $(".notice_popup ul", noticesDom);
            $scroll.mCustomScrollbar('stop');
            $scroll.mCustomScrollbar('scrollTo', $li, {
                scrollInertia: 800
            });
            setTimeout(function(){
                $li.find('.notice_popup_content').slideDown(function(){
                    $li.addClass('current read');
                    setBannerNoticeRead(chkId);
                });
            }, 800);

            return;
        }

        setBannerNoticeRead(chkId);

        var html = tpl('#notice_popup_tpl', {notices: noticesData, chkId: chkId});
        swal({
            title: '<i class="icon iconfont pr5">&#xe60e;</i>最新公告',
            customClass: 'popup notice',
            html: html,
            width: 740,
            showCloseButton: true,
            showCancelButton: false,
            showConfirmButton:false,
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel',
            onOpen: function(dom){
                var $dom =  $(dom);
                noticesDom = $dom;
                $(".notice_popup ul", $dom).mCustomScrollbar({
                    scrollInertia: 70,
                    autoHideScrollbar: true,
                    theme:"dark-thin",
                    advanced: {
                        updateOnContentResize: true
                    }
                });

                var $li = $dom.find('li[data-id='+chkId+']');
                $(".notice_popup ul", $dom).mCustomScrollbar('scrollTo', $li, function(){
                    scrollInertia: 800
                });

                // 点击事件)
                $dom.off('click', "[data-command=expand]");
                $dom.on('click', "[data-command=expand]", function(e){
                    var $target = $(e.target);
                    var $li = $target.closest('li');
                    $li.stop(true, true);
                    var id = $li.attr('data-id');

                    if ($li.hasClass('current')) {
                        $li.find('.notice_popup_content').slideUp(function(){
                            $li.removeClass('current').find('.unread').remove();
                            setBannerNoticeRead(id);
                        });

                        var $siblings = $li.siblings();
                        $siblings.stop(true, true).find('.notice_popup_content').slideUp(function(){
                            $siblings.removeClass('current')
                        });
                    }
                    else {
                        $li.find('.notice_popup_content').slideDown(function(){
                            $li.addClass('current').find('.unread').remove();
                            setBannerNoticeRead(id);
                        });

                        var $siblings = $li.siblings();
                        $siblings.stop(true, true).find('.notice_popup_content').slideUp(function(){
                            $siblings.removeClass('current')
                        });
                    }

                    // $li.find('.notice_popup_content').slideToggle(function(){
                    //     $li.toggleClass('current');
                    //     $li.find('.unread').remove();
                    //     setBannerNoticeRead(id);
                    // });
                    //
                    // var $siblings = $li.siblings();
                    // $siblings.find('.notice_popup_content').slideUp(function(){
                    //     $siblings.removeClass('current')
                    // });
                });
            }
        }).then(function() {
            noticesDom = null;
        }, function() {
            noticesDom = null
        })
    }

    // 初始化余额、消息等
    function initGlobal() {
        $topBalance.html(tpl('#top_balance_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));

        refreshGlobal(null, true);
    }

    // 刷新余额、消息等
    function refreshGlobal(callback, spin) {
        if (window.forcelogout && window.forcelogout === true) {
            return;
        }
        
        if (beating) return
        beating = true;
        
        if (spin) {
            $topBalance.find('.balance_top .refresh-balance-btn').css('display', 'none');
            $topBalance.find('.balance_top .refresh-balance-spin').css('display', 'inline-block');
        }

        $topBalance.find('[data-property=totalBalance]').html('Loading...');
        $topBalance.find('[data-property=mainBalance]').html('Loading...');
        $topBalance.find('[data-property=lotteryBalance]').html('Loading...');

        $.ajax({
            url: '/GetGlobal',
            data: {},
            success: function(res) {
                if (res.error == 0) {
                    var msgCount = res.data.uMsgCount + res.data.uSysMsgCount;
                    if (msgCount <= 0) {
                        $msgCount.html("0").hide();
                    }
                    else {
                        if (msgCount > 99) {
                            $msgCount.html("..").show();
                        }
                        else {
                            $msgCount.html(msgCount).show();
                        }
                    }

                    $headerRight.find('[data-property=nickname]').html(res.data.uBean.nickname);

                    setTimeout(function(){
                        beating = false

                        var totalBalance = $.YF.formatMoney(res.data.uBean.totalMoney + res.data.uBean.lotteryMoney);
                        var mainBalance = $.YF.formatMoney(res.data.uBean.totalMoney);
                        var lotteryBalance = $.YF.formatMoney(res.data.uBean.lotteryMoney);

                        $topBalance.find('[data-property=totalBalance]').html(totalBalance);
                        $topBalance.find('[data-property=mainBalance]').html(mainBalance);
                        $topBalance.find('[data-property=lotteryBalance]').html(lotteryBalance);

                        if (spin){
                            $topBalance.find('.balance_top .refresh-balance-btn').css('display', 'inline-block');
                            $topBalance.find('.balance_top .refresh-balance-spin').css('display', 'none');
                        };

                        if (callback && $.isFunction(callback)) callback(res);

                        // $(document).trigger('globe.refreshed', res);
                    }, 1000);
                }
                else {
                    switch (res.code) {
                        case "2-1":
                        case "2-2":
                        case "2-3":
                        case "2-1006":
                            window.forcelogout = true;
                            $.YF.alert_warning(res.message, function(){
                                top.window.location = '/login'
                            });
                            break
                    }
                }
            },
            complete: function() {
            }
        })
    }

    // 资金归集
    function confirmTransferAll() {
        $.YF.alert_question('确认要将所有资金全部转移至主账户吗？', transferAll, function(){
            $.YF.alert_success('您的资金已全部成功转移至主账户！', function(){
                refreshGlobal(null, true);
            });
        })
    }
    function transferAll(resolve, reject) {

        $.ajax({
            url: '/SelfUserTransfersAll',
            data: {},
            success: function(res) {
                if (res.error === 0) {
                    resolve();
                }
                else {
                    reject();
                    $.YF.alert_warning(res.message);
                }
            }
        })
    }

    // 初始化客服地址
    function initKefu() {
        $.ajax({
            url: '/ServiceKefu',
            dataType: 'text',
            success: function(onlineService) {
                kefuUrl = onlineService;
                $headerRight.off('click', '[data-command=kefu]');
                $headerRight.on('click', '[data-command=kefu]', function() {
                    openKefu();
                });
            }
        })
    }
    
    function openKefu() {
        if (kefuUrl) {
            window.open(kefuUrl, 'service', 'height=500,width=600,directories=no,location=no,menubar=no,resizable=no,screenX=' + (window.screen.width - 600) / 2 + ',creenY=' + (window.screen.height - 500) / 2 + ',scrollbars=no,titlebar=no,toolbar=no')
        }
    }

    // 退出
    function confirmLogout() {
        $.YF.alert_question('确认退出吗？', logout, function(){
            setTimeout(function(){
                $.removeCookie('USER_NAME');
                top.window.location = '/logout';
            }, 100);
        })
    }
    function logout(resolve, reject) {
        resolve();
    }

    // 点击查看余额
    function refreshPlatformBalance() {
        if (refreshingBalance == true) {
            return;
        }
        refreshingBalance = true;
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
                        refreshingBalance = false;
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

    // 手动刷新余额(直接触发心跳)
    $topBalance.on('click', '[data-command=refreshGlobal]', function() {
        refreshGlobal(null, true);
    })

    // 资金归集
    $topBalance.on('click', '[data-command=transferAll]', confirmTransferAll);

    // 退出
    $headerRight.on('click', '[data-command=logout]', confirmLogout);

    // 点击查看余额
    $topBalance.on('click', '[data-command=refreshPlatformBalance]', refreshPlatformBalance);

    // 切换模式
    $("[data-command='changeTheme']").click(function(){
        var theme = $(this).attr('data-val');
        if (!theme) {
            return;
        }

        changeTheme(theme);
        $.cookie("YF_THEME", currentTheme, {  path: '/' });
    });

    function changeTheme(theme) {
        if (theme != 'black' && theme != 'blue' && theme != 'white') {
            return;
        }

        currentTheme = theme;

        changeThemeByChangeable();
    }

    function changeThemeByChangeable() {
        $.YF.showFullDarkLoadingMask();

        // main main>iframe main>iframe>iframe，共3层

        var changeCount = 0;
        var doneCount = 0;

        var $mainLinks = $("link[changeable='true']");
        changeCount += $mainLinks.size();
        $mainLinks.each(function(index){
            var src = $(this).attr('href');
            var pre = src.substr(0, src.lastIndexOf('-'));
            var suffix = src.substr(src.lastIndexOf('.'));
            var src = pre + '-' + currentTheme + suffix;
            $(this).attr('href', src);

            cssLoaded(src, function(){
                doneCount++;
            });
        });

        var $mainImgs = $("img[changeable='true']");
        changeCount += $mainImgs.size();
        $mainImgs.each(function(index){
            var src = $(this).attr('src');
            var pre = src.substr(0, src.lastIndexOf('-'));
            var suffix = src.substr(src.lastIndexOf('.'));
            var src = pre + '-' + currentTheme + suffix;
            $(this).attr('src', src);
            $(this).on('load', function(){
                doneCount++;
            });
        });

        var $mainIframeLinks = $(window.frames["mainiframe"].document).find("link[changeable='true']");
        changeCount += $mainIframeLinks.size();
        $mainIframeLinks.each(function(index){
            var src = $(this).attr('href');
            var pre = src.substr(0, src.lastIndexOf('-'));
            var suffix = src.substr(src.lastIndexOf('.'));
            var src = pre + '-' + currentTheme + suffix;
            $(this).attr('href', src);

            cssLoaded(src, function(){
                doneCount++;
            });
        });

        if (window.frames["mainiframe"].window.frames["iframe"]) {
            var $mainIframeSubIframeLinks = $(window.frames["mainiframe"].window.frames["iframe"].document).find("link[changeable='true']");
            changeCount += $mainIframeSubIframeLinks.size();
            $mainIframeSubIframeLinks.each(function(index){
                var src = $(this).attr('href');
                var pre = src.substr(0, src.lastIndexOf('-'));
                var suffix = src.substr(src.lastIndexOf('.'));
                var src = pre + '-' + currentTheme + suffix;
                $(this).attr('href', src);

                cssLoaded(src, function(){
                    doneCount++;
                });
            });
        }

        setTimeout(function(){
            var checkDoneInterval = setInterval(function(){
                if (doneCount >= changeCount) {
                    $.YF.hideFullDarkLoadingMask();
                    clearInterval(checkDoneInterval);
                }
            }, 100);
        }, 1000);
    }

    function cssLoaded (link, callback) {
        var img = document.createElement('img');
        img.onerror = function(){
            callback();
        }
        img.src = link;
    };

    // 初始化
    init();

    // 每120秒获取次公告列表
    setInterval(initBannerNotices, 120000);

    // 定时刷新余额等
    setInterval(refreshGlobal, 32000);

    // 将刷新余额定义为全局变量，便于其它地方可以直接发起
    window.refreshGlobal = refreshGlobal

    // 将弹出公告定义为全局对象
    window.popupNotices = popupNotices
    
    // 关于我们
    window.showAboutUs = showAboutUs
    
    // 常见问题
    window.showQuestion = showQuestion
    
    // 常见问题
    window.openKefu = openKefu
    
    window.showPromotion = showPromotion;
});