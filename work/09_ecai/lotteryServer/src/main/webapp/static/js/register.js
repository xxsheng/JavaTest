$(function(){
    var $doc = $(document);
    var $loginBox = $("#loginBox", $doc);
    var $agreeTerms = $("#agreeTerms", $doc);
    var $username = $("#username", $doc);
    var $password = $("#password", $doc);
    var $rePassword = $("#rePassword", $doc);
    var $imgCode = $("#imgCode", $doc);
    var $checkCode = $("#checkCode", $doc);
    var $registerBtn = $("#registerBtn", $doc);
    var link = $.YF.getUrlParam(window.location.search, 'code');
    var $showAppDownload = $("#showAppDownload", $doc);

    // 回车提交
    $(window.document).on('keydown', function(e){
        var key = e.keyCode;
        if(key==13){
            register();
        }
    });

    // 如果当前页面不是登录页面，则跳到登录页面
    if (top.window && top.window.location.pathname !== '/register') {
        top.window.location = '/register';
    }

    // 登录按钮点击
    $registerBtn.on('click', function(e) {
        register();
    });

    // 验证码点击
    $imgCode.on('click', function(e) {
        refreshImgCode();
    });

    // 刷新验证码
    function refreshImgCode() {
        $imgCode.attr('src', '/RegistCode?t=' + (+new Date()))
    }

    function register(){
        if ($registerBtn.is(":disabled")) {
            return;
        }

        layer.closeAll('tips');

        if ($.YF.isEmpty(link)) {
            $.YF.alert_warning('无效的注册链接');
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

        var password = $password.val();
        if ($.YF.isEmpty(password)) {
            $password.focus();
            $.YF.alert_tooltip('请设置登录密码', $password);
            return;
        }

        if (!$.YF.isPassword(password)) {
            $password.focus();
            $.YF.alert_tooltip('必须为英文、数字、字符任意两种组合，长度6-20位', $password);
            return
        }

        var rePassword = $rePassword.val();
        if ($.YF.isEmpty(rePassword)) {
            $rePassword.focus();
            $.YF.alert_tooltip('请输入确认登录密码', $rePassword);
            return;
        }

        if (password != rePassword) {
            $rePassword.focus();
            $.YF.alert_tooltip('两次输入密码不一致，请检查', $rePassword);
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

        if (!$agreeTerms.hasClass('cur')) {
            $.YF.alert_tooltip('请同意开户条约', $agreeTerms);
            return
        }

        layer.closeAll('tips');

        startRegister();

        password = $.YF.generatePassword(password);
        $.ajax({
            url: '/UserRegist',
            type: 'POST',
            data: {
                username: username,
                nickname: username,
                password: password,
                code: code,
                link: link
            },
            success: function(res) {
                if (res.error === 0) {
                    successRegister();
                }
                else{
                    // 验证码输入有误
                    if (res.code === '2-1005') {
                        $.YF.alert_tooltip(res.message, $checkCode);
                        $checkCode.select().focus();
                        refreshImgCode();
                        endRegister();
                    }
                    else {
                        $.YF.alert_warning(res.message);
                        refreshImgCode();
                        endRegister();
                    }
                }
            }
        })
    }

    function startRegister() {
        $registerBtn.text("注册中...").attr("disabled");
    }

    function endRegister() {
        $registerBtn.text("注册").removeAttr("disabled");
    }

    function successRegister() {
        $.YF.alert_success('恭喜您，注册成功！', function(){
            $registerBtn.text("注册成功，正在跳转");
            top.window.location = '/login';
        });
    }

    $username.on('blur', function(){
        var username = $username.val();
        if ($.YF.isEmpty(username)) {
            return;
        }
        if (!/[a-zA-Z0-9]/.test(username)) {
            $.YF.alert_tooltip('用户名只能输入字母开头的6~12位字符', $username)
            return;
        }
        if (username.length < 6) {
            return;
        }

        if (!$.YF.isUserName(username)) {
            $.YF.alert_tooltip('用户名只能输入字母开头的6~12位字符', $username)
            return;
        }

        $.ajax({
            url: '/UserCheckExist',
            type: 'POST',
            data: {
                username: username,
            },
            success: function(res) {
                if (res == true || res == 'true') {
                    $.YF.alert_tooltip('该用户名已存在', $username)
                }
                else {
                    layer.closeAll('tips');
                }
            },
            complete: function(xhr, status) {
            }
        })

    });

    $agreeTerms.click(function(){
        $(this).toggleClass("cur");
    })
    
    // 客服按钮点击
    $doc.on('click', '[data-command=kefu]', function(e) {
    	var url = 'https://chat56.live800.com/live800/chatClient/chatbox.jsp?companyID=971030&configID=161674&jid=2828750180&s=1';
        window.open(url, 'service', 'height=600,width=800,directories=no,location=no,menubar=no,resizable=no,screenX=' + (window.screen.width - 600) / 2 + ',creenY=' + (window.screen.height - 500) / 2 + ',scrollbars=no,titlebar=no,toolbar=no')
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
})