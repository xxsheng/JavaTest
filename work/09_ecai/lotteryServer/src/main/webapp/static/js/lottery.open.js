$(function(){
    var randomCodes;
    var emptyCode = [];
    if (Lottery.type === 1) {
        randomCodes = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"];
        emptyCode = ['-', '-', '-', '-', '-']
    }
    else if (Lottery.type === 2) {
        randomCodes = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"];
        emptyCode = ['-', '-', '-', '-', '-']
    }
    else if (Lottery.type === 3) {
        randomCodes = ["1", "2", "3", "4", "5", "6"];
        emptyCode = ['-', '-', '-']
    }
    else if (Lottery.type === 4) {
        randomCodes = ["0", "1", "2", "3", "4", "5", "6"];
        emptyCode = ['-', '-', '-']
    }
    else if (Lottery.type === 5) {
        randomCodes = [
            "01", "02", "03", "04", "05", "06", "07", "08", "09",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
            "60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
            "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
            "80"];
        emptyCode = [
            '-', '-', '-', '-', '-',
            '-', '-', '-', '-', '-',
            '-', '-', '-', '-', '-',
            '-', '-', '-', '-', '-']
    }
    else if (Lottery.type === 6) {
        randomCodes = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10"];
        emptyCode = [
            '-', '-', '-', '-', '-',
            '-', '-', '-', '-', '-',]
    }
    else {
        randomCodes = ["-", "-", "-", "-", "-"];
    }
    var countdowntimer = null;
    var $doc = $(document);
    var $content = $('#content', $doc);
    var $rightBanner = $('#rightBanner', $doc);
    var $recordBox = $('#recordBox', $content);
    var $recordBoxHeader = $('#recordBoxHeader', $content);
    var $recordList = $('#recordList', $content);

    var $bettingTitle = $('#bettingTitle', $content);
    var $bettingCondition = $('#bettingCondition', $content);
    var $bettingLeft = $('#betting_left', $content);
    var $bettingContent = $('#bettingContent', $content);
    var $currentExpect = $('[data-property=currentExpect]', $('#current_sell'));
    var $currentTime = $('[data-property=currentTime]', $('#countdown'));
    var $openCodeSummary = $('#openCodeSummary', $content);
    var $slideBox = $('#slideBox', $openCodeSummary);
    var $openHistory = $('#openHistory', $doc);
    var $rightOpenHistory = $('#rightOpenHistory', $content);
    var historyCodes = [];

    var animationId = null;
    var stopIndexes = new Array();
    var lastCode; // 上一次号码
    var lastExpect;
    var playAnimationInternal = 50; // 间隔多少毫秒一次动画
    var playAnimationCurrent = 0; // 已经播放了多久的动画
    var playAnimationTimeout = 60000; // 播放动画1分钟后没有响应就停止，并展示上一次的号码
    var playAnimationAlreadyTimeout = false;
    var channels = [];
    var withdrawData = null;
    var transferData = null;

    var tpls = {
        unOpen: '#lottery_user_bets_unopen_tpl',
        opened: '#lottery_user_bets_opened_tpl'
    }
    var urls = {
        unOpen: '/RecentUserBetsUnOpenSearch',
        opened: '/RecentUserBetsOpenedSearch'
    }

    $recordBoxHeader.on("click", "span", function(){
        var $this = $(this);
        if ($this.hasClass('cur')) {
            return;
        }

        // var key = $this.attr('data-val');
        $this.addClass("cur").siblings().removeClass("cur");

        refreshRecord();
    });

    function init() {
        getOpenTime();

        loadLastExpect();

        loadOpenCode();

        registerWebSocket();
        
        initRightBanner();

        $content.find('.content-left').mCustomScrollbar({
            scrollInertia: 70,
            autoHideScrollbar: false,
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });
    }

    // 获取开奖号码列表
    function loadOpenCode() {
        if (!isPlayingAnimation()) {
            $.ajax({
                url: '/LotteryOpenCode',
                data: {
                    lotteryId: Lottery.id,
                    count: 15
                },
                success: function(res) {
                    buildOpenCode(res);
                }
            })
        }
    }

    // 获取上次期号
    function loadLastExpect() {
        $.ajax({
            url: '/LotteryLastExpect',
            data: {
                lotteryId: Lottery.id
            },
            success: function(res) {
                lastExpect = res.expect;
            }
        })
    }

    function buildOpenCode(res) {
        if (isPlayingAnimation()) {
            return;
        }
        var code;
        var histories = new Array();

        if (res && res.length > 0) {
            for (var i = 0; i < res.length; i++) {
                code = res[i].code.split(',');
                histories.push({
                    expect: res[i].expect,
                    code: code
                });
            }
        }
        else {
            histories.push({
                expect: '00000000-000',
                code: emptyCode
            });
        }

        stopAnimation();

        historyCodes = histories;
        lastCode = histories[0];

        // slider历史记录
        var sliderHTML = tpl('#open_code_slider_tpl', { rows: histories, showExpect: Lottery.shortName != 'jsmmc', lottery: Lottery});
        $slideBox.empty().html(sliderHTML);
        $slideBox.slide({mainCell:"#openCodeHistory",effect:"leftLoop", scroll: 1, vis: "auto"});

        // banner历史记录
        var bannerHTML = tpl('#open_code_banner_tpl', { rows: histories, showExpect: Lottery.shortName != 'jsmmc', lottery: Lottery});
        $openHistory.find('ul').html(bannerHTML);

        $openHistory.mCustomScrollbar({
            scrollInertia: 70,
            autoHideScrollbar: true,
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });
        
        // 右边历史记录
        var historyHTML = tpl('#open_code_history_tpl', { rows: histories, showExpect: Lottery.shortName != 'jsmmc', lottery: Lottery});
        $rightOpenHistory.find('.history-list').html(historyHTML);

        $rightOpenHistory.mCustomScrollbar({
            scrollInertia: 70,
            autoHideScrollbar: true,
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });
    }

    function stopAll() {
        stopAnimation();
    }

    // 获取开奖时间
    function getOpenTime() {
        if ($.YF.isForceLogout()) {
            stopAll();
            return;
        }

        $.ajax({
            url: '/LotteryOpenTime',
            data: {
                lotteryId: Lottery.id
            },
            success: function(data) {
                // 如果存在要清空一次
                $.YF.clearTimer(countdowntimer)
                if ($.YF.isForceLogout()) {
                    stopAll();
                    return;
                }
                if (data == undefined || data.opentime == undefined || data.opentime == null || data.opentime == '') {
                    countdowntimer = setInterval(function() {
                        getOpenTime();
                    }, 5000);
                    return;
                }
                var open = data.opentime; // 本期时间
                var sTime = data.sTime; // 服务器时间

                var remain = moment(open.stopTime).valueOf() - moment(sTime).valueOf();
                remain = Math.round(remain / 1000); // 下一期剩余开奖时间，精确到秒

                var surplus = remain; // countdown以后剩余的开奖时间
                $currentExpect.html(open.expect);
                $('#global-expect').html(open.expect); // 全局的倒计时时间
                var setTime = function() {
                    if ($.YF.isForceLogout()) {
                        return;
                    }

                    if (surplus <= open.play && open.play > 0) {
                        if (playAnimationAlreadyTimeout == false && lastExpect && lastCode && lastCode.expect != lastExpect && !isPlayingAnimation()) {
                            setTimeout(function () {
                                prependOnlyCodeToHistoryCode(lastExpect);
                                playAnimationWithRandom();
                            }, 50)
                        }
                    }

                    var fTime = $.YF.formatSecondsToHMS(surplus);
                    
                    $currentTime.html('<span><label class="hour_show">'+fTime[0]+'</label></span><span><label class="minute_show">'+fTime[1]+'</label></span><span><label class="second_show">'+fTime[2]+'</label></span>');
                    $('[data-property="countdown"]', '#lottery-chase').html(fTime[0] + ':' + fTime[1] + ':' + fTime[2]); // 全局的倒计时时间
                    // TODO 倒计时横幅
                    // $(".prize_tt span", $openCodeSummary).css("width",(3600-surplus)*(100/3600)+"%"); // 倒计时横幅
                    if (surplus <= 0) {
                        $.YF.alert_info('第 '+open.expect+' 期投注时间已截止，请注意投注期号！', null, 3000);
                        $.YF.clearTimer(countdowntimer);
                        lastExpect = open.expect;
                        getOpenTime();
                        stopAll();
                        loadOpenCode();
                    }
                    else {
                        surplus--;
                    }
                }
                setTime();
                countdowntimer = setInterval(setTime, 1000);
            }
        })
    }

    function registerWebSocket() {
        var params = "type=1&lotteryId=" + Lottery.id + "&_" + new Date().getTime();
        WS.connect(params, function(data){
            var socketData = $.parseJSON(data);
            if (socketData.type === 1) {
                // 抓取号码
            	//alert(socketData.code);
                socketData.code = socketData.code.split(",");
                showPreItemWithAnimation(socketData);
            }
        });
    }

    function prependOnlyCodeToHistoryCode(expect) {
        var code = {expect: expect, code: emptyCode};

        var tmpHistoryCodes = historyCodes.slice(0);
        tmpHistoryCodes.unshift(code);

        var html = tpl('#open_code_slider_tpl', { rows: tmpHistoryCodes, showExpect: Lottery.shortName != 'jsmmc', lottery: Lottery});
        $slideBox.empty().html(html);
        $slideBox.slide({mainCell:"#openCodeHistory",effect:"leftLoop", scroll: 1, vis: "auto"});
    }

    function appendToHistory(preItem) {
        var code = {expect: preItem.expect, code: preItem.code};
        historyCodes.unshift(code);
        if (historyCodes.length > 15) {
            historyCodes.pop();
        }

        appendToSlider();

        appendToBannerHistory(code);

        appendToRightHistory(code);
    }

    function appendToSlider() {
        var sliderHTML = tpl('#open_code_slider_tpl', { rows: historyCodes, showExpect: Lottery.shortName != 'jsmmc', lottery: Lottery});
        $slideBox.empty().html(sliderHTML);
        $slideBox.slide({mainCell:"#openCodeHistory", effect:"leftLoop", scroll: 1, vis: "auto"});
    }

    function appendToBannerHistory(code) {
        // banner历史记录
        var $bannerHistories = $openHistory.find('li');
        var historySize = $bannerHistories.size();
        if (historySize >= 15) {
            $bannerHistories.last().remove();
        }

        var $newEntry = $bannerHistories.first().clone(true);
        $newEntry.find(".expect").html(code.expect);
        var $code = $newEntry.find(".code");
        for(var i=0; i<code.code.length; i++) {
            $code.find("span:eq("+i+")").html(code.code[i]);
        }

        $openHistory.find('ul').prepend($newEntry);
    }

    function appendToRightHistory(code) {
        // 右边历史记录
        var $histories = $rightOpenHistory.find('table tr');
        var historySize = $histories.size();
        if (historySize >= 15) {
            $histories.last().remove();
        }

        var $newEntry = $histories.first().clone(true);
        $newEntry.find(".expect").html(code.expect);
        var $code = $newEntry.find(".code");
        for(var i=0; i<code.code.length; i++) {
            $code.find("span:eq("+i+")").html(code.code[i]);
        }

        $rightOpenHistory.find('table').prepend($newEntry);
    }

    function showPreItemWithAnimation(preItem) {
        stopAnimation();
        lastCode = {expect: preItem.expect, code: preItem.code};
        prependOnlyCodeToHistoryCode(preItem.expect);

        // 开始播放动画，随机值
        playAnimationWithCode(preItem);
    }

    function playAnimationWithCode(preItem) {
        stopAnimation();

        var length = emptyCode.length;
        var $balls = $slideBox.find("#openCodeHistory li").eq(1).find('.prize_2 [data-property=open-code]');

        animationId = setInterval(function(){
            fillRandomCode(length, $balls);
        }, playAnimationInternal);

        var i = 0;
        var stopThreadId = setInterval(function(){
            stopIndexes.push(i);

            $balls.eq(i).html(preItem.code[i]);

            if (i >= length-1) {
                clearInterval(stopThreadId);
                appendToHistory(preItem);
            }
            else {
                i++;
            }
        }, 1000);
    }

    function playAnimationWithRandom() {
        var isPlaying = isPlayingAnimation();
        if (isPlaying) {
            return;
        }

        stopAnimation();

        var length = emptyCode.length;
        var $balls = $slideBox.find("#openCodeHistory li").eq(1).find('.prize_2 [data-property=open-code]');

        if (length > 0) {
            animationId = setInterval(function(){
                if ($.YF.isForceLogout()) {
                    $.YF.clearTimer(animationId);
                    return;
                }

                fillRandomCode(length, $balls);
            }, playAnimationInternal);
        }
    }

    function stopAnimation() {
        if (animationId) {
            clearInterval(animationId);
            animationId = null;
            playAnimationCurrent = 0;
            stopIndexes = new Array();
        }
    }

    function fillRandomCode(length, $balls) {
        if (playAnimationCurrent >= playAnimationTimeout) {
            stopAnimation();
            playAnimationAlreadyTimeout = true;
            loadOpenCode();
            return;
        }

        if (stopIndexes.length >= length) {
            stopAnimation();
        }
        else {
            for(var i=0; i<length; i++){
                if ($.inArray(i, stopIndexes) <= -1) {
                    if (playAnimationCurrent < playAnimationTimeout) {
                        var num = Math.floor(Math.random() * randomCodes.length-1);
                        if (num > 0) {
                            var code = randomCodes[num];
                            $balls.eq(i).html(code);
                        }
                    }
                }
            }
            playAnimationCurrent += playAnimationInternal;
        }
    }

    function isPlayingAnimation() {
        return animationId != null;
    }

    function initRightBanner() {
        // 初始化右边内容
        $rightBanner.html(tpl('#lottery_right_banner_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));

        // banner滚动条
//        $rightBanner.mCustomScrollbar({
//            scrollInertia: 70,
//            autoHideScrollbar: true,
//            theme:"dark-thin",
//            advanced: {
//                updateOnContentResize: true
//            }
//        });

        // 设计购物篮高度
        var $shoppingCarUL =  $rightBanner.find('#shoppingCar ul');
        // $shoppingCarUL.css("height", $(window).height() - 520);
        // $(window).resize(function(){
        //     $shoppingCarUL.css("height", $(window).height() - 520);
        // })

        // 购物车滚动条
        $shoppingCarUL.mCustomScrollbar({
            scrollInertia: 70,
            autoHideScrollbar: true,
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });

        // 加载今天报表信息
        loadTodayReport(false);

        // 点击刷新
        $rightBanner.off('click', "[data-command=refreshBalance]");
        $rightBanner.on('click', "[data-command=refreshBalance]", function(){
            loadTodayReport(true);
        });
        
        // 关闭事件
        $rightBanner.off('click', "[data-command=close]");
        $rightBanner.on('click', "[data-command=close]", function(){
            resetFunds();
        });

        var $fundsBox = $rightBanner.find('#fundsBox');
        var $fundsTab = $rightBanner.find('#fundsTab');

        // 初始化充值
        initRecharge($fundsBox, $fundsTab);

        // 提款
        initWithdraw($fundsBox, $fundsTab);

        // 资金转换
        initTransfer($fundsBox, $fundsTab)
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

        var $summary = $rightBanner.find('#summary');
        $summary.find('[data-property=lotteryBalance]').html('Loading...');
        $summary.find('[data-property=unSettleMoney]').html('Loading...');
        $summary.find('[data-property=lotteryBet]').html('Loading...');

        $.ajax({
            url: '/UserTodayReport',
            data: {},
            success: function(res) {
                if (res.error == 0) {
                    if ($.YF.isProxy(res.data.user.type)) {
                        $rightBanner.find('[data-type=proxy]').show();
                    }

                    if (delayLoading) {
                        setTimeout(function(){
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
            complete: function(){
                setTimeout(function(){
                    isLoadingTodayReport = false;
                }, 5000);
            }
        })
    }

    // 初始化充值
    function initRecharge($fundsBox, $fundsTab) {
        var $channels = $fundsBox.find('#channels');
        var $rechargeBanks = $fundsBox.find('#rechargeBanks');
        var $rechargeInputMoney = $fundsBox.find('#rechargeInputMoney');
        var $rechargeSelectMoney = $fundsBox.find('#rechargeSelectMoney');
        var $redirectForm = $fundsBox.find('#redirectForm');

        $fundsTab.off('click', "[data-command=recharge]");
        $fundsTab.on('click', "[data-command=recharge]", function(){
            if ($(this).closest('li').hasClass('cur')) {
                resetFunds();
                return;
            }

            $.YF.hideLoadingMask();
            $fundsTab.find('.refresh-spin').hide();

            if (channels && channels.length > 0) {
                showBox($fundsTab, $fundsBox, 'recharge');
                $rechargeInputMoney.val('').focus();
            }
            else {
                $fundsTab.find('li[data-group=recharge]').find(".refresh-spin").show();
                loadRecharge($fundsBox, $fundsTab);
            }
        });

        // 确认事件
        $fundsBox.off('click', "[data-command=submitRecharge]");
        $fundsBox.on('click', "[data-command=submitRecharge]", function(){
            var selectedChannel = $channels.find('option:selected');
            if (!selectedChannel) {
                $.YF.alert_tooltip('请选择充值方式', $channels);
                return;
            }
            var selectedBank = $rechargeBanks.find('option:selected');
            if (!selectedBank) {
                $.YF.alert_tooltip('请选择选择', $rechargeBanks);
                return;
            }

            var pid = selectedChannel.val();
            var channelType = selectedChannel.attr('data-type');
            var channelSubType = selectedChannel.attr('data-subType');
            var channelCode = selectedChannel.attr('data-channel-code');
            var fixedQRAmount = selectedChannel.attr('data-fixedQRAmount');
            var bankId = selectedBank.val();
            var bankco = selectedBank.attr('data-bankco');

            var amount;
            var qrCodeId;
            if (fixedQRAmount == 1 && $.YF.isManualScanChannel(channelType, channelSubType)) {
                var selectedMoney = $rechargeSelectMoney.find('option:selected');
                if (!selectedMoney) {
                    $.YF.alert_tooltip('请选择金额', $rechargeSelectMoney);
                    return;
                }

                qrCodeId = selectedMoney.val();
                amount = selectedMoney.attr('data-amount');
                if (!$.YF.isDigits(amount)) {
                    $.YF.alert_tooltip('金额请选择整数', $rechargeSelectMoney);
                    return;
                }
            }
            else {
                amount = $rechargeInputMoney.val();
                if ($.YF.isEmpty(amount)) {
                    $rechargeInputMoney.focus();
                    $.YF.alert_tooltip('请输入金额', $rechargeInputMoney);
                    return;
                }
                amount = parseInt(amount);
            }

            if (!amount) {
                $.YF.alert_warning('请输入或选择金额');
                return;
            }
            if (!$.YF.isDigits(amount)) {
                $.YF.alert_warning('金额请输入整数');
                return;
            }


            var minUnitRecharge = parseInt(selectedChannel.attr('data-min-unit-recharge'));
            var maxUnitRecharge = parseInt(selectedChannel.attr('data-max-unit-recharge'));

            // 检查输入值是否在范围值内
            if (amount < minUnitRecharge || amount > maxUnitRecharge) {
                $rechargeInputMoney.focus();
                $.YF.alert_tooltip('充值范围：' + minUnitRecharge + '-' + maxUnitRecharge, $rechargeInputMoney);
                return;
            }

            var rechargeData = {pid: pid, bankId: bankId, amount: amount, bankco: bankco, qrCodeId: qrCodeId};

            $.YF.showLoadingMask($fundsBox.find('[data-group=recharge]'));

            if (channelCode == 'bankTransfer') {
                // '/BankTransfer'

            	
            }
            else {
                $.ajax({
                    url: '/RechargeAdd',
                    data: rechargeData,
                    success: function(res) {
                        if (res.error === 0) {
                            layer.closeAll('tips');
                            if ($.YF.isManualScanChannel(channelType, channelSubType)) {
                                var desc;
                                if (channelSubType == 2) {
                                    desc = '请使用微信扫描上方二维码完成充值';
                                }
                                else if (channelSubType == 4) {
                                    desc = '请使用支付宝扫描上方二维码完成充值';
                                }
                                else if (channelSubType == 6) {
                                    desc = '请使用QQ扫描上方二维码完成充值';
                                }
                                else {
                                    desc = '请使用手机扫描上方二维码完成充值';
                                }
                                popupQRCode(res.data.qrUrl, desc);
                            }
                            else {
                                var html = tpl('#redirect_form_tpl', {data: res.data});

                                var $dom;
                                swal({
                                    title: '确认充值',
                                    customClass:'popup',
                                    html: html,
                                    width: 460,
                                    showCloseButton: true,
                                    showCancelButton: false,
                                    showConfirmButton: false,
                                    allowEnterKey: false,
                                    onBeforeOpen: function (dom) {
                                        $dom = $(dom);

                                        $dom.off('click', '[data-command=start]');
                                        $dom.on('click', '[data-command=start]', function(e) {
                                            swal.close();
                                        })

                                        $dom.off('click', '[data-command=close]');
                                        $dom.on('click', '[data-command=close]', function(e) {
                                            swal.close();
                                        })
                                    },
                                    onOpen: function(){
                                    },
                                    showLoaderOnConfirm: true,
                                    preConfirm: function(){
                                        return new Promise(function (resolve, reject) {
                                            resolve();
                                        })
                                    }
                                }).then(function() {
                                }, function() {
                                    layer.closeAll('tips');
                                })
                            }
                            $rechargeInputMoney.val('');
                            resetFunds();
                        }
                        else {
                            if (res.code == '2-1136') {
                                $.YF.alert_warning(res.message, function(){
                                    $.YF.showLoadingMask($fundsBox.find('[data-group=recharge]'));
                                    loadRecharge($fundsBox, $fundsTab);
                                });
                            }
                            else {
                                $.YF.alert_warning(res.message);
                            }
                        }
                    },
                    complete: function() {
                        $.YF.hideLoadingMask();
                    }
                })
            }
        });
    }

    function loadRecharge($fundsBox, $fundsTab) {
        $.ajax({
            url: '/ListPayment',
            success: function(res) {
                if (res.error === 0) {
                	if(res.code =='2-13'){
                	    $.YF.alert_warning(res.message, function(){
                        });
                		   return;
                	}
                    if (!res.hasWithdrawPwd) {
                        resetFunds();
                        $.YF.alert_warning('请先设置资金密码再进行充值操作！', function(){
                            parent.window.location = '/manager?link=account-manager';
                        });
                        return;
                    }

                    channels = res.channels;
                    buildRecharge();
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
                $.YF.hideLoadingMask();
                $fundsTab.find('.refresh-spin').hide();
            }
        })
    }

    function resetFunds() {
        layer.closeAll('tips');
        var $fundsTab = $rightBanner.find('#fundsTab');
        $fundsTab.find('li').removeClass('cur');
        var $fundsBox = $rightBanner.find('#fundsBox');
        $fundsTab.find('.refresh-spin').hide();
        $fundsBox.find('.b').hide();
    }

    function buildRecharge() {
        var $channels = $rightBanner.find('#channels');
        var $rechargeBanks = $rightBanner.find('#rechargeBanks');
        var $rechargeInputMoney = $rightBanner.find('#rechargeInputMoney');
        var $rechargeSelectMoney = $rightBanner.find('#rechargeSelectMoney');
        var $fundsTab = $rightBanner.find('#fundsTab');
        var $fundsBox = $rightBanner.find('#fundsBox');

        // 渲染充值方式
        $channels.empty();
        $.each(channels, function(index, channel){
            var channelOption = $('<option value="'+channel.id+'" data-min-unit-recharge="'+channel.minUnitRecharge+'" data-max-unit-recharge="'+channel.maxUnitRecharge+'" data-channel-code="'+channel.channelCode+'" data-type="'+channel.type+'" data-subType="'+channel.subType+'" data-fixedQRAmount="'+channel.fixedQRAmount+'">'+channel.frontName+'</option>');
            $channels.append(channelOption);

            if (index == 0) {
                // 渲染第一个银行列表、设置输入框等
                buildRechargeOptions(channel, $rechargeBanks, $rechargeInputMoney, $rechargeSelectMoney);
            }
        });

        showBox($fundsTab, $fundsBox, 'recharge');

        $channels.unbind('change').bind('change', function(){
            var $option = $channels.find('option:selected');
            var selectedChannelId = $option.val();

            $.each(channels, function(index, channel){
                if (selectedChannelId == channel.id) {
                    buildRechargeOptions(channel, $rechargeBanks, $rechargeInputMoney, $rechargeSelectMoney);
                }
            });
        })
    }

    function buildRechargeOptions(channel, $rechargeBanks, $rechargeInputMoney, $rechargeSelectMoney) {
        // 渲染银行列表
        $rechargeBanks.empty();
        var banks = $.YF.formatRechargeBanks(channel);
        $.each(banks, function(index, bank){
            var bankco = bank.bankco;
            if (!bankco) {
                bankco = 'NONE';
            }
            var bankOption = $('<option value="'+bank.bankId+'" data-bankco="'+bankco+'">'+bank.name+'</option>');
            $rechargeBanks.append(bankOption);
        });

        if (channel.fixedQRAmount == 1 && $.YF.isManualScanChannel(channel.type, channel.subType)) {
            // 设置金额选择下拉
            $rechargeSelectMoney.empty();
            $.each(channel.qrCodes, function(index, qrCode){
                var moneyOption = $('<option value="'+qrCode.id+'" data-amount="'+qrCode.money+'">'+qrCode.money+'</option>');
                $rechargeSelectMoney.append(moneyOption);
            });

            $rechargeInputMoney.closest('#inputMoney').hide();
            $rechargeSelectMoney.closest('#selectMoney').show();
        }
        else {
            // 设置输入框
            $rechargeInputMoney.attr('placeholder', '金额范围' + channel.minUnitRecharge + '~' + channel.maxUnitRecharge);
            $.YF.initInputForceDigit($rechargeInputMoney);

            $rechargeSelectMoney.closest('#selectMoney').hide();
            $rechargeInputMoney.closest('#inputMoney').show();
            $rechargeInputMoney.focus().val('');
        }

    }

    function popupQRCode(qr, desc) {
        var html = tpl('#image_popup_tpl', {url: qr, desc: desc})
        swal({
            title: '扫码充值',
            text: '请使用手机扫描上方二维码完成充值',
            customClass:'popup',
            html: html,
            width: 300,
            imageWidth: 200,
            imageHeight: 200,
            showCloseButton: true,
            showCancelButton: true,
            showConfirmButton: true,
            confirmButtonText: '完成充值',
            cancelButtonText: '关闭',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel',
            allowEnterKey: false,
            onBeforeOpen: function (dom) {
            },
            onOpen: function(){
            },
            showLoaderOnConfirm: true,
            preConfirm: function(){
                return new Promise(function (resolve, reject) {
                    resolve();
                })
            }
        }).then(function() {
            parent.window.location='/manager?link=manager-funds';
        }, function() {
        })
    }

    // 初始化提款
    function initWithdraw($fundsBox, $fundsTab) {
        var $withdrawMoney = $fundsBox.find('#withdrawMoney');
        var $withdrawBanks = $fundsBox.find('#withdrawBanks');
        var $withdrawPwd = $fundsBox.find('#withdrawPwd');

        $fundsTab.off('click', "[data-command=withdraw]");
        $fundsTab.on('click', "[data-command=withdraw]", function(){
            if ($(this).closest('li').hasClass('cur')) {
                resetFunds();
                return;
            }
            $.YF.hideLoadingMask();

            $fundsTab.find('.refresh-spin').hide();

            if (withdrawData) {
                showBox($fundsTab, $fundsBox, 'withdraw');
                $withdrawPwd.val('');
                $withdrawMoney.val('').focus();
            }
            else {
                $fundsTab.find('li[data-group=withdraw]').find(".refresh-spin").show();
                loadWithdraw($fundsBox, $fundsTab, $withdrawMoney, $withdrawBanks, $withdrawPwd);
            }
        });

        // 确认事件
        $fundsBox.off('click', "[data-command=submitWithdraw]");
        $fundsBox.on('click', "[data-command=submitWithdraw]", function(){
            if (!withdrawData) {
                $.YF.alert_warning('未找到银行卡，请刷新页面再试');
                return;
            }

            var withdrawBank = $withdrawBanks.find('option:selected');
            if (!withdrawBank) {
                $.YF.alert_tooltip('请选择您要提款的银行', $withdrawBanks);
                return;
            }
            var withdrawMoney = $withdrawMoney.val();
            if ($.YF.isEmpty(withdrawMoney)) {
                $withdrawMoney.focus();
                $.YF.alert_tooltip('请输入提款金额', $withdrawMoney);
                return;
            }
            if (!$.YF.isDigits(withdrawMoney)) {
                $.YF.alert_tooltip('提款金额请输入整数', $withdrawMoney);
                return;
            }
            withdrawMoney = parseInt(withdrawMoney);

            if (withdrawMoney < withdrawData.minWithdrawals || withdrawMoney > withdrawData.maxWithdrawals) {
                $withdrawMoney.focus();
                $.YF.alert_tooltip('单次提款范围为：' + withdrawData.minWithdrawals + '-' + withdrawData.maxWithdrawals, $withdrawMoney);
                return;
            }

            var withdrawPwd = $withdrawPwd.val();
            if ($.YF.isEmpty(withdrawPwd)) {
                $withdrawPwd.focus();
                $.YF.alert_tooltip('请输入资金密码', $withdrawPwd);
                return;
            }

            $.YF.showLoadingMask($fundsBox.find('[data-group=withdraw]'));

            var token = $.YF.getDisposableToken();
            withdrawPwd = $.YF.encryptPasswordWithToken(withdrawPwd, token);

            var data = {amount : withdrawMoney, cid: withdrawBank.val(), withdrawPwd: withdrawPwd};

            $.ajax({
                url: '/ApplyUserWithdrawals',
                data: data,
                success: function(res) {
                    if (res.error === 0) {
                        $.YF.alert_success('您的提现请求已提交，请等待工作人员处理！', function(){
                            loadTodayReport(true);
                        });
                        layer.closeAll('tips');
                        $withdrawMoney.val('');
                        $withdrawPwd.val('');
                    }
                    else {
                        $.YF.alert_warning(res.message)
                    }
                },
                complete: function() {
                    $.YF.hideLoadingMask();
                },
                error: function(){
                }
            })
        });
    }

    function loadWithdraw($fundsBox, $fundsTab, $withdrawMoney, $withdrawBanks, $withdrawPwd) {
        $.ajax({
            url: '/LoadUserWithdrawals',
            success: function(res) {
                if (res.error === 0) {
                   	if(res.code =='2-13'){
                	    $.YF.alert_warning(res.message, function(){
                        });
                		   return;
                	}
                    if (!res.data.hasWithdrawPwd) {
                        resetFunds();
                        $.YF.alert_warning('请先设置资金密码再进行提款操作！', function(){
                            parent.window.location = '/manager?link=account-manager';
                        });
                        return;
                    }

                    if (res.data.cList.length < 1) {
                        resetFunds();
                        $.YF.alert_warning('请先绑定银行卡再进行提款操作！', function(){
                            parent.window.location = '/manager?link=account-card';
                        });
                        return;
                    }

                    withdrawData = res.data;

                    var min = parseInt(res.data.minWithdrawals);
                    var max = parseInt(res.data.maxWithdrawals);
                    $withdrawMoney.attr('placeholder', '金额范围' + min + '~' + max);
                    $.YF.initInputForceDigit($withdrawMoney);
                    $withdrawBanks.html(tpl('#withdraw_banks_tpl', { rows: res.data.cList }));
                    showBox($fundsTab, $fundsBox, 'withdraw');
                    $withdrawMoney.focus();
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
                $fundsTab.find('.refresh-spin').hide();
            }
        })
    }

    function showBox($fundsTab, $fundsBox, type) {
        $fundsTab.find('li[data-group=' + type + ']').addClass("cur").siblings().removeClass("cur");
        $fundsBox.find('div[data-group=' + type + ']').show().siblings().hide();
    }

    // 初始化资金转换
    function initTransfer($fundsBox, $fundsTab) {
        var $transferOutAccount = $fundsBox.find('#transferOutAccount');
        var $transferInAccount = $fundsBox.find('#transferInAccount');
        var $transferMoney = $fundsBox.find('#transferMoney');

        $fundsTab.off('click', "[data-command=transfer]");
        $fundsTab.on('click', "[data-command=transfer]", function(){
            if ($(this).closest('li').hasClass('cur')) {
                resetFunds();
                return;
            }
            $.YF.hideLoadingMask();

            $fundsTab.find('.refresh-spin').hide();

            if (transferData) {
                showBox($fundsTab, $fundsBox, 'transfer');
                $transferMoney.val('').focus();
            }
            else {
                $fundsTab.find('li[data-group=transfer]').find(".refresh-spin").show();
                loadTransfer($fundsBox, $fundsTab, $transferOutAccount, $transferInAccount, $transferMoney);
            }
        });

        // 确认事件
        $fundsBox.off('click', "[data-command=submitTransfer]");
        $fundsBox.on('click', "[data-command=submitTransfer]", function(){
            var transferMoney = $transferMoney.val();
            if ($.YF.isEmpty(transferMoney)) {
                $transferMoney.focus();
                $.YF.alert_tooltip('请输入金额', $transferMoney);
                return;
            }
            if (!$.YF.isDigits(transferMoney)) {
                $.YF.alert_tooltip('金额请输入整数', $transferMoney);
                return;
            }
            transferMoney = parseInt(transferMoney);

            var fromAccount = $transferOutAccount.val();
            var toAccount = $transferInAccount.val();

            if (fromAccount == toAccount) {
                $.YF.alert_tooltip('转出与转入账户不能相同', $transferOutAccount);
                return;
            }

            $.YF.showLoadingMask($fundsBox.find('[data-group=transfer]'));

            var data = {fromAccount : fromAccount, toAccount: toAccount, amount: transferMoney};

            $.ajax({
                url: '/SelfUserTransfers',
                data: data,
                success: function(res) {
                    if (res.error === 0) {
                        $.YF.alert_success('您的资金已经转到指定账户', function(){
                            loadTodayReport(true);
                        });
                        layer.closeAll('tips');
                        $transferMoney.val('');
                    }
                    else {
                        $.YF.alert_warning(res.message)
                    }
                },
                complete: function() {
                    $.YF.hideLoadingMask();
                },
                error: function(){
                }
            })
        });
    }

    function loadTransfer($fundsBox, $fundsTab, $transferOutAccount, $transferInAccount, $transferMoney) {
        $.ajax({
            url: '/LoadUserTransfers',
            success: function(res) {
                if (res.error === 0) {
                    transferData = res.data;

                    var list = res.data.listPlatform;
                    $transferOutAccount.html(tpl('#accounts_tpl', {
                        selected: 0,
                        rows: list
                    }));
                    $transferInAccount.html(tpl('#accounts_tpl', {
                        selected: 1,
                        rows: list
                    }));


                    $.YF.initInputForceDigit($transferMoney);
                    showBox($fundsTab, $fundsBox, 'transfer');
                    $transferMoney.focus();
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
                $fundsTab.find('.refresh-spin').hide();
            }
        })
    }

    var isRefreshingRecord = false;
    function refreshRecord() {
        if ($.YF.isForceLogout()) {
            return;
        }
        if (isRefreshingRecord) {
            return;
        }

        isRefreshingRecord = true;

        var key = $recordBoxHeader.find("span.cur").attr("data-val");
        var $list = $('div.tab-list[data-val='+key+']', $recordBox);
        $list.show().siblings('.tab-list').hide();

        $.ajax({
            url: urls[key],
            data: {},
            success: function(res) {
                if(res.error === 0) {
                    $list.html(tpl(tpls[key], {
                        rows: res.data
                    }));
                }
            },
            complete: function() {
                isRefreshingRecord = false;
            }
        })
    }

    function refresh() {
        loadTodayReport(true);
        refreshRecord(true);
    }

    // 订单详情
    $recordBox.on('click', '[data-command="details"]', function(e){
        var id = $(e.target).closest('tr').attr('data-id');
        new LotteryOrderDetail(id, function(){
            refreshRecord();
        });
    });

    // 复投
    $recordBox.on('click', '[data-command="rebet"]', function(e){
        var id = $(e.target).closest('tr').attr('data-id');
        $.YF.alert_question('确认要重复投注该订单吗？', function(resolve, reject){
            $.ajax({
                url: '/UserBetsReBet',
                data: {id : id},
                success: function(res) {
                    if(res.error === 0) {
                        resolve();
                    }
                    else {
                        reject();
                        $.YF.alert_warning(res.message);
                    }
                }
            })
        }, function(){
            $.YF.alert_success('重复投注成功！', function(){
                setTimeout(function(){
                    refreshRecord();
                }, 2000);
            });
        })
    });

    // 撤销订单
    $recordBox.on('click', '[data-command="cancel"]', function(e){
        var id = $(e.target).closest('tr').attr('data-id');
        $.YF.alert_question('确认要撤销该订单吗？', function(resolve, reject){
            $.ajax({
                url: '/UserBetsCancel',
                data: {id : id, type : 'general'},
                success: function(res) {
                    if(res.error === 0) {
                        resolve();
                    }
                    else {
                        reject();
                        $.YF.alert_warning(res.message);
                    }
                }
            })
        }, function(){
            $.YF.alert_success('订单已撤销成功！', function(){
                refreshRecord();
            });
        })
    });

    // 右边历史记录高度
    $(document).on('rule.changed', function() {
        resizeRightHistories();
    })

    $(window).resize(function(){
        resizeRightHistories();
    })

    function resizeRightHistories() {
        var height = $bettingContent.height();
        $rightOpenHistory.css('height', height + "px");
    }
    
    setInterval(function(){
        if ($.YF.isForceLogout()) {
            stopAll();
            return;
        }

        getOpenTime();
    }, 30000);

    setInterval(function(){
        if ($.YF.isForceLogout()) {
            stopAll();
            return;
        }
        loadOpenCode();
    }, 35000);

    setInterval(function(){
        if ($.YF.isForceLogout()) {
            stopAll();
            return;
        }

        loadTodayReport(true);
    }, 160000);

    setInterval(function(){
        if ($.YF.isForceLogout()) {
            stopAll();
            return;
        }

        refreshRecord();
    }, 40000);
    
    window.loadTodayReport = loadTodayReport;

    window.refreshRecord = refreshRecord

    window.refresh = refresh

    init();
    refreshRecord();
})
