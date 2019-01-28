$(function(){
    var $doc = $(document);
    var $loginBox = $("#loginBox", $doc);
    var $username = $("#username", $doc);
    var $password = $("#password", $doc);
    var $imgCode = $("#imgCode", $doc);
    var $checkCode = $("#checkCode", $doc);
    var $loginBtn = $("#loginBtn", $doc);
    var $demouser = $("#demouser", $doc);
    var $showAppDownload = $("#showAppDownload", $doc);
    // 回车登录
    $(window.document).on('keydown', function(e){
        var key = e.keyCode;
        if(key==13){
            login();
        }
    });

    // 如果当前页面不是登录页面，则跳到登录页面
    if (top.window && top.window.location.pathname !== '/login') {
        top.window.location = '/login';
    }

    // 客服按钮点击
    $doc.on('click', '[data-command=kefu]', function(e) {
    	var url = 'https://chat56.live800.com/live800/chatClient/chatbox.jsp?companyID=971030&configID=161674&jid=2828750180&s=1';
        window.open(url, 'service', 'height=600,width=800,directories=no,location=no,menubar=no,resizable=no,screenX=' + (window.screen.width - 600) / 2 + ',creenY=' + (window.screen.height - 500) / 2 + ',scrollbars=no,titlebar=no,toolbar=no')
    });
    
    //试玩点击
    $doc.on('click', '[data-command=shiwan]', function(e) {
    	$.ajax({
            url: '/DemoLogin',
            type: 'POST',
            success: function(res) {
            	successLogin(res.uBean.username);
            }
        })
    });

    // 登录按钮点击
    $loginBtn.on('click', function(e) {
        login();
    });
    
    $showAppDownload.off('click');
    $showAppDownload.on('click',function(){
    	var showhtml ='<div id="" class="layui-layer-content" style="height: 400px;">'+
			        '<div class="xz_box">'+
			            '<div class="xz_cent">'+
			                '<div class="xz_1">WINonline手机APP</div>'+
			                '<div class="xz_2">当别人还沉寂在敲打键盘进行游戏时丶我们已经将时下最火的彩票、真人视讯、捕鱼、电子游艺等装进了口袋，让您真正体验裤兜里的快乐。</div>'+
			            '</div>'+
			            '<div class="erweima">'+
			                '<img src="../static/images/ewm.png?v=1.0.2">'+
			            '</div>'+
			            '<div class="neirong">'+
			                '<p class="tishi">扫描二维码立即下载</p>'+
			            '</div>'+
			        '</div>'+
			    '</div>';
			swal({
			customClass:'popup',
			title:'手机APP下载',
			html: showhtml,
			width: 680,
			height:400,
			showCloseButton: true,
			showCancelButton: false,
			showConfirmButton: false,
			buttonsStyling: false,
			confirmButtonClass: 'popup-btn-confirm',
			cancelButtonClass: 'popup-btn-cancel',
			allowEnterKey: false,
			showLoaderOnConfirm: true
			}).then(function() {
			}, function() {
			});
    });
    
    // 登录按钮点击
    $demouser.on('click', function(e) {
        $.ajax({
            url: '/DemoLogin',
            type: 'POST',
            success: function(res) {
            	successLogin(res.uBean.username);
            }
        })
    });
    

    // 验证码点击
    $imgCode.on('click', function(e) {
        refreshImgCode();
    });

    // 刷新验证码
    function refreshImgCode() {
        $imgCode.attr('src', '/LoginCode?t=' + (+new Date()))
    }
    function logout(resolve, reject) {
        resolve();
    }
    function login(){
        if (swal.isVisible()) {
            return;
        }
        if ($loginBtn.is(":disabled")) {
            return;
        }

        layer.closeAll('tips');

        var username = $username.val();
        if ($.YF.isEmpty(username) || username.length < 2) {
            $username.focus();
            $.YF.alert_tooltip('请输入用户名', $username);
            return;
        }
        var password = $password.val();
        if ($.YF.isEmpty(password)) {
            $password.focus();
            $.YF.alert_tooltip('请输入密码', $password);
            return
        }
        var code = $checkCode.val();
        if ($.YF.isEmpty(code)) {
            $checkCode.focus();
            $.YF.alert_tooltip('请输入验证码', $checkCode);
            return
        }
        if (!(/^[0-9]{4}$/.test(code))) {
            $checkCode.focus();
            $.YF.alert_tooltip('请输入4位数字验证码', $checkCode);
            return
        }

        layer.closeAll('tips');

        startLogin();

        var token = $.YF.getDisposableToken();
        password = $.YF.encryptPasswordWithToken(password, token);
        $.ajax({
            url: '/AppLogin',
            type: 'POST',
            data: {
                username: username,
                password: password,
                checkcode: code
            },
            success: function(res) {
                if (res.error === 0) {
                    // $.YF.alert_login("本系統為好彩系統獨家開發,JAVA語言開發，集合市面上主流功能（日分紅,契約，预设开奖）等一系列功能，此版本為運營版本," +
                    //     "本站唯一聯係"+"<br>"+"QQ：356570723   1787064872"+
                    //     "<br>"+"本站仅供学习和参考，不做商业用途", logout,function(){
                    //     successLogin(username);
                    // }, function(){
                    //     setTimeout(function(){
                    //         $.removeCookie('USER_NAME');
                    //         top.window.location = '/logout';
                    //     }, 100);
                    // })

                    //successLogin(username);
					
					$.YF.alert_success("登录成功", function(){
                        successLogin(username);
                    })

                }else{
                    // 验证码输入有误
                    if (res.code === '2-1005') {
                        $.YF.alert_tooltip(res.message, $checkCode);
                        $checkCode.select().focus();
                        refreshImgCode();
                        endLogin();
                    }
                    // 用户名或密码错误
                    else if (res.code === '2-1074') {
                        $.YF.alert_tooltip(res.message, $password);
                        $password.select().focus();
                        refreshImgCode();
                        endLogin();
                    }
                    // 异地登录验证
                    else if (res.code === '2-4026') {
                        showValidateLogin(res.data);
                        endLogin();
                    }
                    else {
                        $.YF.alert_warning(res.message);
                        refreshImgCode();
                        endLogin();
                    }
                }
            }
        })
    }

    function showValidateLogin(data) {
        var html = tpl('#validate_card_tpl', data);

        var $dom;
        swal({
            title: '异地登录验证',
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
                    validate($dom, function(){
                        resolve();
                    }, function(msg){
                        reject(msg);
                    });
                })
            }
        }).then(function() {
            successLogin();
        }, function() {
            layer.closeAll('tips');
        })
    }

    function validate($dom, resolve, reject) {
        // swal.resetValidationError();
        layer.closeAll('tips');

        var cardName = $("#cardName", $dom);
        if (/^\s+$/.test(cardName.val()) || cardName.val().length < 1) {
            $("#cardName", $dom).focus();
            $.YF.alert_tooltip('请输入持卡人姓名', '#cardName');
            reject();
            return;
        }

        // swal.showLoading();
        // showLoadingMask();

        $.ajax({
            url: '/ValidateLogin',
            type: 'POST',
            data: {
                cardName: cardName.val()
            },
            success: function(res) {
                if (res.error === 0) {
                    resolve();
                }
                else{
                    // swal.showValidationError(res.message);
                    // alert_warning('提示', res.message);
                    $("#cardName", $dom).focus();
                    layer.closeAll('tips');
                    reject(res.message);
                    endLogin();
                }
            },
            complete: function(xhr, status) {
                // swal.hideLoading();
            }
        })
    }

    function startLogin() {
//        $loginBtn.text("登陆中...").attr("disabled");
    }

    function endLogin() {
//        $loginBtn.text("登陆").removeAttr("disabled");
    }

    function successLogin() {
//        $loginBtn.text("登陆成功，正在跳转");
        $.cookie('USER_NAME', $username.val());
        top.window.location = '/main';
    }
})