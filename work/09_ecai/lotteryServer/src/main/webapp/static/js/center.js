var Activity = {
		IconType :{"2":"static/images/index/xydzp.jpg","6":"static/images/index/xydzp.jpg","8":"static/images/index/xydzp.jpg","16":"static/images/index/xydzp.jpg"},
}
$.holdReady(true); // 锁住所有ready方法，加载模板文件
var url = cdnDomain + '/static/template/center_template.html?v=' + cdnVersion;
$.ajax({
    type: "GET",
    url: url,
    cache: true,
    complete: function(res) {
        $("#center_template_tpl").html(res.responseText);
        $.holdReady(false);
    }
});

$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $newestNotice = $("#newestNotice", $content);
    var $slideBox = $("#slideBox", $content);
    var $hotBox = $("#hotBox", $content);
    var $indexList = $("#indexList", $content);
    var $promotionBox = $("#promotionBox", $content);
    //20180926 页脚图片新规    
    var $image = $("#image", $content);
    var $footer = $("#footer", $content);
    var $rightBanner = $("#rightBanner", $doc);
    
    var $luckBox = $("#luckBox", $doc);
    var $recentBox = $("#recentBox", $doc);
    
    var $noticeBanner = $('#noticeBox',$doc);
    
    var channels = [];
    var withdrawData = null;
    var transferData = null;

    function init() {
        // 初始化最新公告
        initNewestNotice();

        // 初始化中间内容
        initContent();

        // 初始化热门游戏
        initHotBox();

        // 初始化右边Banner
        initRightBanner();
        
        BetsHitRankingScroll();
    }

    // 初始化最新公告
    function initNewestNotice() {
        $.ajax({
            url: '/SysNoticeList',
            success: function(res) {
                if (res.error == 0 && res.data && res.data.length > 0) {
                    noticesData = res.data;

                    var html = '';
                    $.each(noticesData, function(index, notice){
                    	html += '<li class="notrow" data-id="'+notice.id+'" data-command=details> <div class="notice_row_title" data-command="expand"><span class="notice_title_txt">'+(index+1)+'、'+notice.title+'</span><i class="title_time">'+notice.date+'</i></div></li>';
                    });

                    $noticeBanner.find('.noticeList').html(html);

                    // 滚动条
                    $noticeBanner.mCustomScrollbar({
                        scrollInertia: 70,
                        autoHideScrollbar: false,
                        theme:"dark-thin",
                        advanced: {
                            updateOnContentResize: true
                        }
                    });

                    // 查看单条公告
                    $noticeBanner.off("click", "[data-command=details]");
                    $noticeBanner.on("click", "[data-command=details]", function(e){
                        var $target = $(e.target);
                        var id = $target.attr('data-id');
                        top.window.popupNotices(id);
                    });
                }
            }
        });
    }
    
  //加载所有进行中的活动
    function initActivityList(){
        $.ajax({
            url: '/LoadActivityList',
            success: function(data) {
            	var top = '<ul>';
            	var bottom = '</ul>';
            	var content='';
            	if(data.rebateList.length >0){
	                $.each(data.rebateList, function(index, item){
	                	var type = item.type;
	                	var icon = cdnDomain+Activity.IconType[type];
	                	var html = '<li><div class="game-item">'+
	                					'<div class="pic"><img src="'+icon+'"></div>'+
	    								'<div class="pac"><span class="btn" lang =  "'+type+'" data-command="showPromotion">查看活动</span></div>'+
	    								'</div>'+
	    								'</li>';
	                	content+=html;
	    				
	                });	
                }else{
                	var html = '<li><div class="game-item">'+
					'<div class="pic"><img src="/static/images/index/cms.jpg"></div>'+
					'<div class="pac"><span class="btn"  data-command="showPromotion">查看活动</span></div>'+
					'</div>'+
					'</li>';
                	content+=html;
                }
            	  $promotionBox.html(top+content+bottom);
            }
        })
    } 
    function initContent() {
        // $slideBox.html(tpl('#banner_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));
    	 
    	
    	$slideBox.slide({mainCell:".bd ul",effect:"fold", scroll: 1, vis: "auto", autoPlay:true});

        // $slideBox.slide({mainCell:".bd ul", effect:"fold", autoPlay:true});
        // $(window).resize(function(){
        //     $slideBox.slide({mainCell:".bd ul", effect:"fold", autoPlay:true});
        // })

        //$indexList.html(tpl('#index_list_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));
        
        //initActivityList();

        //20180926 页脚图片新规    
        $image.html(tpl('#footer_tpl_image', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));
        $footer.html(tpl('#footer_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));
        
        // 关于我们
        $footer.off('click', '[data-command=showAboutUS]');
        $footer.on('click', '[data-command=showAboutUS]', function(){
            if (top.window) {
                var $this = $(this);
                var id = $this.attr('data-id');
                top.window.showAboutUs(id);
            }
        });

        // 常见问题
        $footer.off('click', '[data-command=showQuestion]');
        $footer.on('click', '[data-command=showQuestion]', function(){
            if (top.window) {
                var $this = $(this);
                var id = $this.attr('data-id');
                top.window.showQuestion(id);
            }
        });

        // 在线客服
        $footer.off('click', '[data-command=openKefu]');
        $footer.on('click', '[data-command=openKefu]', function(){
            if (top.window) {
                top.window.openKefu();
            }
        });

        // 优惠活动
        $promotionBox.off('click', '[data-command=showPromotion]');
        $promotionBox.on('click', '[data-command=showPromotion]', function(){
        	var type = $(this).attr("lang");
        	if(type == null){
        		return;
        	}
        	if (top.window) {
                top.window.showPromotion(type);
            }
        });
        
        $content.find('.content-left').mCustomScrollbar({
            scrollInertia: 70,
            autoHideScrollbar: false,
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });
    }

    // 初始化热门游戏
    function initHotBox() {
        $hotBox.html(tpl('#hot_box_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));

        /*$hotBox.mCustomScrollbar({
            scrollInertia: 70,
            alwaysShowScrollbar: true,
            autoHideScrollbar: false,
            axis: 'x',
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });*/

        // 加载彩种剩余时间
        $hotBox.find('li').each(function(index, item){
            var $item = $(item);
            var id = $item.attr('data-id');
            getOpenTime(id);
        });
    }

    function clearTimer(timer) {
        if (timer) {
            clearTimeout(timer)
            clearInterval(timer)
            timer = null
        }
    }

    // 获取开奖时间
    function getOpenTime(id) {
        var timerId = 'lottery_' + id;
        if ($.YF.isForceLogout()) {
            clearTimer(timerId);
            return;
        }

        var $dom = $hotBox.find('li[data-id='+id+']');

        $.ajax({
            url: '/LotteryOpenTimeCode',
            data: {
                lotteryId: id
            },
            success: function(data) {
                clearTimer(timerId);
                if (data == undefined || data.opentime == undefined || data.opentime == null || data.opentime == '') {
                    return
                }

                var open = data.opentime; // 本期时间
                var sTime = data.sTime; // 服务器时间

                var remain = moment(open.stopTime).valueOf() - moment(sTime).valueOf();
                remain = Math.round(remain / 1000); // 下一期剩余开奖时间，精确到秒
                var surplus = remain; // countdown以后剩余的开奖时间

                $dom.find('[data-property="expect"]').html('No.'+open.expect);
                var $time = $dom.find('[data-property="time"]');
                var setTime = function() {
                    if ($.YF.isForceLogout()) {
                        clearTimer(timerId);
                        return;
                    }

                    var fTime = $.YF.formatTime(surplus);
                    $time.html(fTime[0] + ":" + fTime[1] + ":" + fTime[2]);
                    if (surplus <= 0) {
                        clearTimer(timerId);
                        getOpenTime(id);
                    }
                    else {
                        surplus--;
                    }
                }
                setTime();
                timerId = setInterval(setTime, 1000);

                if (data.openCode) {
                    var openCodes = data.openCode.code.split(',');
                    $dom.find('[data-property="openCode"]').find('i').each(function(index, item){
                        var $item = $(item);
                        var openCode = openCodes[index];
                        if (openCode) {
                            $item.html(openCode);
                        }
                        else {
                            $item.html('-');
                        }
                    });
                }
            }
        })
    }

    // 初始化右边Banner
    function initRightBanner() {
        // 初始化内容
//        $rightBanner.html(tpl('#right_banner_tpl', {cdnDomain: cdnDomain, cdnVersion: cdnVersion}));

        // 滚动条
//        $rightBanner.mCustomScrollbar({
//            scrollInertia: 70,
//            autoHideScrollbar: false,
//            theme:"dark-thin",
//            advanced: {
//                updateOnContentResize: true
//            }
//        });

        // 加载今天报表信息
        //loadTodayReport(false);

        // 加载最近3条登录地址
        loadRecentLogin();

        // 加载中奖排行榜
        loadHitRanking();

        // 异地登录按钮
        $recentBox.off('click', "#loginValidateBtn");
        $recentBox.on('click', "#loginValidateBtn", function(){
            if ($(this).hasClass("active")) {
                openLoginValidate($(this));
            }
            else {
            	closeLoginValidate();
            }
        });

        // 关闭事件
        $rightBanner.off('click', "[data-command=close]");
        $rightBanner.on('click', "[data-command=close]", function(){
            resetFunds();
        });

        //var $fundsBox = $rightBanner.find('#fundsBox');
        //var $fundsTab = $rightBanner.find('#fundsTab');

        // 初始化充值
        //initRecharge($fundsBox, $fundsTab);

        // 提款
        //initWithdraw($fundsBox, $fundsTab);

        // 资金转换
        //initTransfer($fundsBox, $fundsTab)
    }


    // 加载最近3条登录地址
    function loadRecentLogin() {
        if ($.YF.isForceLogout()) {
            return;
        }

        $.ajax({
            url: '/GetUserLoginLog',
            data: {count: 4},
            success: function(res) {
                if (res.error == 0) {
                    var html = tpl('#recent_login_tpl', {rows: res.data});
                    $recentBox.find('#recentLogins').html(html);
                }else {
                    $.YF.alert_warning(res.message);
                }
            }
        })
        
        $.ajax({
            url: '/GetUserBase',
            success: function(res) {
                if (res.error == 0 && res.data.loginValidate == 0) {
                	$recentBox.find('#loginValidateBtn').addClass("active");
                }
            }
        })
        
    }
    
    function BetsHitRankingScroll() {
        var i = 1;    //element是ul
        
        setInterval(function() {
        	var length = $("#hitRanking li").length;
            if(i == length) {
                i = 0;//初始位置
                $("#hitRanking")[0].style.top  = "0px";
            }
            var topscorll = -(i * 76);

            feeltoTop(topscorll)
            i++;
        }, 3000)
        //向上滚动
        function feeltoTop(topscorll){  //console.log(topscorll):topscorll是top值
            var buchang = -10;
            var feelTimer = setInterval(function(){
            	$("#hitRanking")[0].style.top = parseInt($("#hitRanking")[0].style.top) + buchang + "px";
                if(parseInt($("#hitRanking")[0].style.top) <= topscorll){
                	$("#hitRanking")[0].style.top = topscorll + "px";
                    window.clearInterval(feelTimer);
                }
            },100);
        }
    }
    
    // 加载中奖排行榜
    function loadHitRanking() {
        if ($.YF.isForceLogout()) {
            return;
        }

        $.ajax({
            url: '/UserBetsHitRanking',
            data: {},
            success: function(data) {
                if (!data || data.length <= 0) {
                    return;
                }
                $.each(data, function(index, val){
                    if(val.type == 1) val.image = "ssc"
                    else if(val.type == 2) val.image = "11x5"
                    else if(val.type == 3) val.image = "k3"
                    else if(val.type == 4) val.image = "fc3d"
                    else if(val.type == 5) val.image = "kl8"
                    else if(val.type == 6) val.image = "pk10"
                    else val.image = "ssc"

                    val.prizeMoney = $.YF.formatMoney(val.prizeMoney);
                });
                var html = tpl('#hit_ranking_tpl', {rows: data});
                $luckBox.find('#hitRanking').html(html);
                
                // 滚动条
                /*$luckBox.mCustomScrollbar({
                  scrollInertia: 70,
                  autoHideScrollbar: false,
                  theme:"dark-thin",
                  advanced: {
                      updateOnContentResize: true
                  }
              });*/
            }
        })
    }

    // 开启异地登录验证
    function openLoginValidate() {
        // $.YF.showLoadingMask();

        $.ajax({
            url : '/ModLoginValidate',
            success : function(res) {
                if(res.error === 0){
                    // $.YF.alert_success('开启异地登录验证成功！');
                	$recentBox.find('#loginValidateBtn').removeClass("active");
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
                // $.YF.hideLoadingMask();
            }
        })
    }

    // 关闭异地登录验证
    function closeLoginValidate() {
        $.ajax({
            url : '/GetRandomCard',
            success : function(res) {
                if (res.error === 0) {
                    if (res.data == null) {
                        $.YF.alert_warning('请先绑定银行卡');
                        return;
                    }

                    if(res.data != null){
                        var html = tpl('#close_login_validate_tpl', res.data);

                        var $dom;
                        swal({
                            title: '关闭异地登录验证',
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
                            },
                            onOpen: function(){
                            },
                            showLoaderOnConfirm: true,
                            preConfirm: function(){
                                return new Promise(function (resolve, reject) {
                                    submitCloseLoginValidate($dom, function(){
                                        resolve();
                                    }, function(msg){
                                        reject(msg);
                                    });
                                })
                            }
                        }).then(function() {
                            // $.YF.alert_success('关闭异地登录验证成功！')
                        	$recentBox.find('#loginValidateBtn').addClass("active");
                        }, function() {
                            layer.closeAll('tips');
                        })
                    }
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
            }
        })
    }

    function submitCloseLoginValidate($dom, resolve, reject) {
        layer.closeAll('tips');

        var cardName = $("#cardName", $dom);
        if (/^\s+$/.test(cardName.val()) || cardName.val().length < 1) {
            $("#cardName", $dom).focus();
            $.YF.alert_tooltip('请输入持卡人姓名', '#cardName');
            reject();
            return;
        }

        var cid = $("input[name=cid]", $dom).val();
        var data = {cid : cid, cardName: cardName.val()};

        $.ajax({
            url: '/ModLoginValidate',
            data: data,
            success: function(res) {
                if (res.error == 0) {
                    resolve();
                }
                else {
                    console.log('验证错误错误');
                    $("#cardName", $dom).focus();
                    layer.closeAll('tips');
                    reject(res.message);
                }
            },
            complete: function() {
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
                $.YF.alert_tooltip('请选择充值方式！', $channels);
                return;
            }
            var selectedBank = $rechargeBanks.find('option:selected');
            if (!selectedBank) {
                $.YF.alert_tooltip('请选择银行', $rechargeBanks);
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

            var rechargeData = {pid: pid, bankId: bankId, amount: amount, bankco: bankco};

            $.YF.showLoadingMask($fundsBox.find('[data-group=recharge]'));

            if (channelCode == 'bankTransfer'){
            	 parent.window.location = '/manager?link=fund-recharge&amount='+amount;
            	return;
            }
            else {
                layer.closeAll('tips');
                
                $.ajax({
                    url: '/RechargeAdd',
                    data: rechargeData,
                    success: function(res) {
                        if (res.error === 0) {
                          if(channelType == '5'){
                        	  alert(channelType);
                          }
                        else if ($.YF.isManualScanChannel(channelType, channelSubType)) {
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
            parent.window.location='/manager?link=fund-recharge-record';
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

            if (withdrawMoney < Number(withdrawData.minWithdrawals) || withdrawMoney > Number(withdrawData.maxWithdrawals)) {
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
    
    init();
   
})