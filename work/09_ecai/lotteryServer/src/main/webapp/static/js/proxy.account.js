$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentTab = $("#contentTab", $content);
    var $contentBox = $("#contentBox", $content);
    var userData;
    var maxCode;
    var minCode = 1800;
    var step = 2;
    var $sliderBar;
    var $code;
    var $username;
    var $deviceType;
    var $days;
    var clipboard;
    var $listTable;
    var $listPager;

    function init() {
        // 初始化内容
        initContentBox();
    }

    function initContentBox() {
        layer.closeAll('tips');

        var type = $contentTab.find('span.cur').attr('data-type');

        if (type == 'manual') {
            $contentBox.html(tpl('#proxy_account_manual_content_tpl', {}));
            initManualContent();
        }
        else if (type == 'webLink') {
            $contentBox.html(tpl('#proxy_account_webLink_content_tpl', {}));
            initWebLinkContent();
        }
        else if (type == 'webLinkManager') {
            $contentBox.html(tpl('#proxy_account_webLinkManager_content_tpl', {}));
            initWebLinkManagerContent();
        }
        else if (type == 'mobileLinkManager') {
            $contentBox.html(tpl('#proxy_account_mobileLinkManager_content_tpl', {}));
            initMobileLinkManagerContent();
        }
    }

    function initManualContent() {
        userData = null;
        $sliderBar = null;
        $code = null;
        $username = null;
        $deviceType = null;
        $days = null;
        clipboard = null;
        maxCode = 0;

        $.YF.showLoadingMask();
        $.ajax({
            url: '/GetUserBase',
            success: function(res) {
                if(res.error === 0) {
                    userData = res.data;

                    maxCode = res.data.allowEqualCode == -1 ? (res.data.code - 2) : res.data.code;
                   // TODO bojin 定制
                    if(maxCode > res.coco){
                    	maxCode = res.coco;
                    }
                    if (maxCode <= minCode) {
                        maxCode = minCode;
                    }
                    
                    initManualContentEvents(maxCode);
                }
            },
            complete: function(){
                $.YF.hideLoadingMask();
            }
        })
    }

    function initWebLinkContent() {
        userData = null;
        $sliderBar = null;
        $code = null;
        $username = null;
        $deviceType = null;
        $days = null;
        clipboard = null;
        maxCode = 0;

        $.YF.showLoadingMask();
        $.ajax({
            url: '/GetUserBase',
            success: function(res) {
                if(res.error === 0) {
                    userData = res.data;

                    maxCode = res.data.allowEqualCode == -1 ? (res.data.code - 2) : res.data.code;
                    //TODO bojin 定制
                    if(maxCode > res.coco){
                    	maxCode = res.coco;
                    }
                    if (maxCode <= minCode) {
                        maxCode = minCode;
                    }

                    initWebLinkContentEvents(maxCode);
                }
            },
            complete: function(){
                $.YF.hideLoadingMask();
            }
        })
    }

    function initWebLinkManagerContent() {
        userData = null;
        $sliderBar = null;
        $code = null;
        $username = null;
        $deviceType = null;
        $days = null;
        clipboard = null;
        maxCode = 0;

        $listTable = $contentBox.find('table tbody');
        $listPager = $contentBox.find('.paging');

        searchWebLinks(0);

        // 分页
        $listPager.off('click', '.pager');
        $listPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            searchWebLinks($target.attr('data-num'))
        })
    }

    function initMobileLinkManagerContent() {
        userData = null;
        $sliderBar = null;
        $code = null;
        $username = null;
        $deviceType = null;
        $days = null;
        clipboard = null;
        maxCode = 0;

        $listTable = $contentBox.find('table tbody');
        $listPager = $contentBox.find('.paging');

        searchMobileLinks(0);

        // 分页
        $listPager.off('click', '.pager');
        $listPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            searchMobileLinks($target.attr('data-num'))
        })
    }

    function initManualContentEvents(maxCode) {
        // 初始化拉条
        var $slider = $('#codeSliderBox .slider-bar', $contentBox);
        $code = $('input[name=code]', $contentBox);
        $username = $('input[name=username]', $contentBox);
        initSlider($slider, $code, maxCode);
    }

    function initWebLinkContentEvents(maxCode) {
        // 初始化拉条
        var $slider = $('#codeSliderBox .slider-bar', $contentBox);
        $code = $('input[name=code]', $contentBox);
        $deviceType = $('select[name=deviceType]', $contentBox);
        $days = $('select[name=days]', $contentBox);
        initSlider($slider, $code, maxCode);
    }

    function initSlider($slider, $code, maxCode) {
        $slider.empty();
        $slider.append('<div class="slider"></div>');
        $sliderBar = $slider.find('.slider');
        $sliderBar.noUiSlider({ connect: 'lower', behaviour: 'snap', step: step, start: maxCode, range: { 'max': maxCode, 'min': minCode } });
        if (maxCode <= minCode) {
            $sliderBar.attr('disabled', 'disabled');
            $code.attr('disabled', 'disabled');
        }
        var update = function(code) {
            $code.val(code);
        }
        var onSet = function() {
            var code = Number($sliderBar.val());
            update(code);
        }
        var onSlide = function() {
            var code = Number($sliderBar.val());
            update(code);
        }
        if (maxCode > minCode) {
            $sliderBar.on({ set: onSet, slide: onSlide });
        }
        update(maxCode);
    }

    function onCodeValidate() {
        var code = $code.val();
        if (code === '')
            return
        if (/^[0-9]*$/.test(code)) {
            code = Number(code);
            if (code >= minCode && code <= maxCode) {
                $sliderBar.val(code);
            }
        }
        else {
            if(code.length == 1){
                $code.val(maxCode);
                $sliderBar.val(maxCode);
            }else{
                code = Number(code.replace(/\D/g, ''));
                $code.val(code);
            }
        }
    }

    function onCodeChange() {
        var code = $code.val();
        if (code === '') {
            $code.val(maxCode);
            $sliderBar.val(maxCode);
        }
        else {
            if (/^[0-9]*$/.test(code)) {
                code = Number(code);
                if (code < minCode || code > maxCode) {
                    $code.val(maxCode);
                    $sliderBar.val(maxCode);
                }
                else {
                    if (code % step != 0) {
                        code -= 1;
                        $code.val(code);
                        $sliderBar.val(code);
                    }
                }
            } else {
                $code.val(maxCode);
                $sliderBar.val(maxCode);
            }
        }
    }

    $contentBox.on("keyup", 'input[name=code]', function(){
        onCodeValidate();
    });

    $contentBox.on("blur", 'input[name=code]', function(){
        onCodeChange();
    });

    $contentBox.on("change", 'input[name=code]', function(){
        onCodeChange();
    });

    // 加减
    $contentBox.on('click', '[data-command=plus], [data-command=minus]', function(e) {
        var code = parseInt($code.val());
        var $target = $(e.target)
        if ($target.hasClass('jj_1')) {
            if (code <= minCode) {
                return;
            }

            if (code > minCode) {
                code -= step;
                $code.val(code)
            }
        } else {
            if (code >= maxCode) {
                return;
            }
            code += step;
            $code.val(code)
        }
        $sliderBar.val(code);
    })
    
    // 代理玩家选择
    $contentBox.on('click', '[data-command=changeType]', function(e) {
        var $this = $(this);
        if ($this.hasClass('cur')) {
            return;
        }

        $this.addClass('cur').siblings().removeClass('cur');
    })

    // 手动开户提交
    $contentBox.on('click', '[data-command=submitManual]', function(e) {
        if (userData == null) {
            $.YF.alert_warning('数据加载中，请稍安勿躁！');
            return;
        }

        var username = $username.val();
        if ($.YF.isEmpty(username)) {
            $username.focus();
            $.YF.alert_tooltip('请输入用户名', $username);
            return;
        }
        if (!$.YF.isUserName(username)) {
            $username.focus();
            $.YF.alert_tooltip('用户名只能输入字母开头的6~12位字符', $username);
            return;
        }

        var code = $code.val();
        if ($.YF.isEmpty(code)) {
            $code.focus();
            $.YF.alert_tooltip('请输入账户返点', $code);
            return;
        }

        if (!$.YF.isDigitsZZS(code)) {
            $.YF.alert_warning('账号返点请输入'+minCode+'~'+maxCode+'之间的数值！');
            $code.focus();
            return;
        }

        code = parseInt(code);
        if (code < minCode || code > maxCode) {
            $.YF.alert_warning('账号返点请输入'+minCode+'~'+maxCode+'之间的数值！');
            $code.focus();
            return;
        }
        if (code % step != 0) {
            $.YF.alert_warning('账号返点数值错误！');
            $code.focus();
            return;
        }

        var type = $contentBox.find('.player i.cur').attr('data-type');
        if ($.YF.isEmpty(type)) {
            $.YF.alert_warning('请选择账户类型！');
            return;
        }

        var data = {type: type,username: username, code: code};

        layer.closeAll('tips');
        
        $.YF.showLoadingMask();
        $.ajax({
            url: "/AddProxyUser",
            data: data,
            success: function(res){
                if (res.error === 0) {
                    $username.val('');

                    var html = tpl('#add_user_success_tpl', data);

                    swal({
                        title: '用户创建成功',
                        customClass:'popup',
                        html: html,
                        width: 460,
                        showCloseButton: true,
                        showCancelButton: true,
                        showConfirmButton: false,
                        cancelButtonText: '关闭',
                        buttonsStyling: false,
                        confirmButtonClass: 'popup-btn-confirm',
                        cancelButtonClass: 'popup-btn-cancel',
                        allowEnterKey: false,
                        onBeforeOpen: function () {
                            if (clipboard != null) clipboard.destroy();
                            clipboard = new Clipboard('#copyManualUsername');

                            clipboard.off('success').on('success', function(e) {
                                $.YF.alert_tooltip('复制成功！', '#copyManualUsername')
                                e.clearSelection();
                            });
                        }
                    }).then(function() {
                        clipboard = null;
                        layer.closeAll('tips');
                    }, function() {
                        clipboard = null;
                        layer.closeAll('tips');
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
    })

    // 链接开户提交
    $contentBox.on('click', '[data-command=submitWebLink]', function(e) {
        if (userData == null) {
            $.YF.alert_warning('数据加载中，请稍安勿躁！');
            return;
        }

        var deviceType = $deviceType.val();
        if ($.YF.isEmpty(deviceType)) {
            $.YF.alert_tooltip('请选择设备类型', $deviceType);
            return;
        }

        var days = $days.val();
        if ($.YF.isEmpty(days)) {
            $.YF.alert_tooltip('请选择链接有效期', $days);
            return;
        }

        var code = $code.val();
        if ($.YF.isEmpty(code)) {
            $code.focus();
            $.YF.alert_tooltip('请输入账户返点', $code);
            return;
        }

        if (!$.YF.isDigitsZZS(code)) {
            $.YF.alert_warning('账号返点请输入'+minCode+'~'+maxCode+'之间的数值！');
            $code.focus();
            return;
        }

        code = parseInt(code);
        if (code < minCode || code > maxCode) {
            $.YF.alert_warning('账号返点请输入'+minCode+'~'+maxCode+'之间的数值！');
            $code.focus();
            return;
        }
        if (code % step != 0) {
            $.YF.alert_warning('账号返点数值错误！');
            $code.focus();
            return;
        }

        var type = $contentBox.find('.player i.cur').attr('data-type');
        if ($.YF.isEmpty(type)) {
            $.YF.alert_warning('请选择账户类型！');
            return;
        }

        var data = {type: type, code: code, days: days, deviceType: deviceType, amount: 9999};

        $.YF.showLoadingMask();
        $.ajax({
            url: "/AddProxyLink",
            data: data,
            success: function(res){
                if (res.error === 0) {

                    if (deviceType == 1) {
                        var link = window.location.protocol + '//' + window.location.hostname + '/register?code=' + res.linkCode;
                        var html = tpl('#add_weblink_success_tpl', {link: link, code: code});

                        swal({
                            title: '注册链接创建成功',
                            customClass:'popup',
                            html: html,
                            width: 460,
                            showCloseButton: true,
                            showCancelButton: true,
                            showConfirmButton: false,
                            cancelButtonText: '关闭',
                            buttonsStyling: false,
                            confirmButtonClass: 'popup-btn-confirm',
                            cancelButtonClass: 'popup-btn-cancel',
                            allowEnterKey: false,
                            onBeforeOpen: function () {
                                if (clipboard != null) clipboard.destroy();
                                clipboard = new Clipboard('#copyWebLink');

                                clipboard.on('success', function(e) {
                                    $.YF.alert_tooltip('复制成功！', '#copyWebLink')
                                    e.clearSelection();
                                });
                            }
                        }).then(function() {
                            clipboard = null;
                        }, function() {
                            clipboard = null;
                        })
                    }
                    else if (deviceType == 2) {
                        showQRCode(res.qrCode);
                    }
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
                $.YF.hideLoadingMask();
            }
        })
    })

    // 删除网页链接
    $contentBox.on('click', '[data-command=delLink]', function(e) {
        var id = $(e.target).attr('data-id');
        $.YF.alert_question('确认要删除该注册链接吗？', function(resolve, reject){
            $.ajax({
                url: '/DelProxyLink',
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
            $.YF.alert_success('该注册链接已成功删除！', function(){
                setTimeout(function(){
                    searchWebLinks(0);
                }, 100);
            });
        })
    })

    // 删除手机链接
    $contentBox.on('click', '[data-command=delMobileLink]', function(e) {
        var id = $(e.target).attr('data-id');
        $.YF.alert_question('确认要删除该注册链接吗？', function(resolve, reject){
            $.ajax({
                url: '/DelProxyLink',
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
            $.YF.alert_success('该注册链接已成功删除！', function(){
                setTimeout(function(){
                    searchMobileLinks(0);
                }, 100);
            });
        })
    })

    // 显示二维码
    $contentBox.on('click', '[data-command=showQRCode]', function(e) {
        var qrCode = $(this).find('.qr-code-big-list').attr('src');
        showQRCode(qrCode);
    })

    // 切换tab
    $contentTab.on('click', '[data-command=changeTab]', function(){
        var $this = $(this);
        if ($this.hasClass('cur')) {
            return;
        }

        $this.addClass('cur').siblings().removeClass('cur');
        initContentBox();
    });

    // web链接搜索
    function searchWebLinks(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/ProxyLinkList',
            data: {
                limit: 10,
                start: page * 10,
                deviceType: 1
            },
            success : function(res) {
                if (res.error === 0) {
                    var link = window.location.protocol + "//" + window.location.host + "/register?code=";
                    $listTable.html(tpl('#proxy_account_webLinkManager_list_tpl', {
                        rows: res.data,
                        link: link
                    }))

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

    // 手机链接搜索
    function searchMobileLinks(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/ProxyLinkList',
            data: {
                limit: 10,
                start: page * 10,
                deviceType: 2
            },
            success : function(res) {
                if (res.error === 0) {
                    $listTable.html(tpl('#proxy_account_mobileLinkManager_list_tpl', {
                        rows: res.data
                    }))

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

    function showQRCode(qrCode) {
        var html = tpl('#image_popup_tpl', {url: qrCode, desc: '使用手机扫描上方二维码即可进行注册'})
        swal({
            title: '手机注册二维码',
            text: '使用手机扫描上方二维码即可进行注册',
            customClass:'popup',
            html: html,
            width: 300,
            imageWidth: 200,
            imageHeight: 200,
            showCloseButton: true,
            showCancelButton: true,
            showConfirmButton: false,
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
        }, function() {
        })
    }

    // 初始化
    init();
})