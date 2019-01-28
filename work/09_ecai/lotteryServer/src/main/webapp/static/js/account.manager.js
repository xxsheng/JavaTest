$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);

    function init() {
        // 初始化安全列表
        initSecurityList();
    }

    // 初始化安全等级
    function initSecurityList() {
        $.ajax({
            url : '/GetUserBind',
            success : function(res) {
                if (res.error === 0) {
                    $contentBox.html(tpl('#security_list_tpl', res.data))
                }
            }
        })
    }

    // 开启异地登录验证
    function openLoginValidate() {
        $.ajax({
            url : '/ModLoginValidate',
            success : function(res) {
                if(res.error === 0){
                    initSecurityList();
                    refreshSecurityLevel();
                }
                else {
                    $.YF.alert_warning(res.message);
                }
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
                            initSecurityList();
                            refreshSecurityLevel();
                        }, function() {
                            layer.closeAll('tips');
                        })
                    }
                }
                else {
                    $.YF.alert_warning(res.message);
                }
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
                    $("#cardName", $dom).focus();
                    layer.closeAll('tips');
                    reject(res.message);
                }
            }
        })
    }

    // 绑定账户姓名
    function bindWithdrawName() {
        var html = tpl('#bind_withdraw_name_tpl', {});

        var $dom;
        swal({
            title: '绑定账户姓名',
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
                    submitWithdrawName($dom, function(){
                        resolve();
                    }, function(msg){
                        reject(msg);
                    });
                })
            }
        }).then(function() {
            $.YF.alert_success('账户姓名绑定成功！');
            initSecurityList();
            refreshSecurityLevel();
        }, function() {
        })
    }

    function submitWithdrawName($dom, resolve, reject) {
        var $withdrawName = $("#withdrawName", $dom);
        var withdrawName = $withdrawName.val();
        if ($.YF.isEmpty(withdrawName)) {
            $withdrawName.focus();
            reject('请输入账户姓名！');
            return;
        }
        if (!$.YF.isChinese(withdrawName)) {
            $withdrawName.focus();
            reject('账户姓名只能输入中文！');
            return;
        }

        withdrawName = withdrawName.trim();

        var data = {withdrawName : withdrawName};

        $.ajax({
            url: '/ModUserBind',
            data: data,
            success: function(res) {
                if (res.error == 0) {
                    resolve();
                }
                else {
                    $withdrawName.focus();
                    reject(res.message);
                }
            }
        })
    }

    // 修改登录密码
    function modifyPassword() {
        var html = tpl('#modify_password_tpl', {});

        var $dom;
        swal({
            title: '修改登录密码',
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
                    submitModifyPassword($dom, function(){
                        resolve();
                    }, function(msg){
                        reject(msg);
                    });
                })
            }
        }).then(function() {
            $.YF.alert_success('登录密码修改成功，请重新登录！', function () {
                $.removeCookie('USER_NAME');
                top.window.location = '/login';
            });
        }, function() {
        })
    }

    function submitModifyPassword($dom, resolve, reject) {
        var $oldPassword = $("#oldPassword", $dom);
        var $newPassword = $("#newPassword", $dom);
        var $rePassword = $("#rePassword", $dom);

        var oldPassword = $oldPassword.val();
        var newPassword = $newPassword.val();
        var rePassword = $rePassword.val();
        if ($.YF.isEmpty(oldPassword)) {
            $oldPassword.focus();
            reject('请输入当前登录密码！');
            return;
        }
        if ($.YF.isEmpty(newPassword)) {
            $newPassword.focus();
            reject('请输入新的登录密码！');
            return;
        }
        if (!$.YF.isPassword(newPassword)) {
            $newPassword.focus();
            reject('必须为英文、数字、字符任意两种组合，长度6-20位！');
            return;
        }
        if (newPassword == oldPassword) {
            $newPassword.focus();
            reject('新登录密码不能与原登录密码一致！');
            return;
        }
        if ($.YF.isEmpty(rePassword)) {
            $rePassword.focus();
            reject('请输入确认密码！');
            return;
        }
        if (newPassword != rePassword) {
            $rePassword.focus();
            reject('两次密码输入不一致，请检查！');
            return;
        }

        var token = $.YF.getDisposableToken();
        oldPassword = $.YF.encryptPasswordWithToken(oldPassword, token);
        newPassword = $.YF.generatePassword(newPassword);
        rePassword = $.YF.generatePassword(rePassword);

        var data = {oldPassword:oldPassword, newPassword: newPassword, rePassword: rePassword};

        $.ajax({
            url: '/ModUserLoginPwd',
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

    // 设置资金密码
    function bindWithdrawPwd() {
        $.ajax({
            url : '/GetUserSecurity',
            success : function(res) {
                if(res.data === null){
                    $.YF.alert_warning('请先绑定安全密保！');
                    return;
                }

                var html = tpl('#bind_withdraw_password_tpl', res.data);

                var $dom;
                swal({
                    title: '设置资金密码',
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
                            submitBindWithdrawPwd($dom, function(){
                                resolve();
                            }, function(msg){
                                reject(msg);
                            });
                        })
                    }
                }).then(function() {
                    initSecurityList();
                    refreshSecurityLevel();
                    $.YF.alert_success('资金密码设置成功！');
                }, function() {
                })
            },
            complete: function() {
            }
        })
    }
    function submitBindWithdrawPwd($dom, resolve, reject) {
        var $answer = $("#answer", $dom);
        var $password = $("#password", $dom);
        var $rePassword = $("#rePassword", $dom);

        var answer = $answer.val();
        var password = $password.val();
        var rePassword = $rePassword.val();
        if ($.YF.isEmpty(answer)) {
            $answer.focus();
            reject('请输入密保答案！');
            return;
        }
        if ($.YF.isEmpty(password)) {
            $password.focus();
            reject('请输入新的资金密码！');
            return;
        }
        if (!$.YF.isPassword(password)) {
            $password.focus();
            reject('必须为英文、数字、字符任意两种组合，长度6-20位！');
            return;
        }
        if ($.YF.isEmpty(rePassword)) {
            $rePassword.focus();
            reject('请输入确认密码！');
            return;
        }
        if (password != rePassword) {
            $rePassword.focus();
            reject('两次密码输入不一致，请检查！');
            return;
        }

        var sid = $("input[name=sid]", $dom).val();

        var token = $.YF.getDisposableToken();
        answer = $.YF.encryptPasswordWithToken(answer, token);
        password = $.YF.generatePassword(password);
        rePassword = $.YF.generatePassword(rePassword);

        var data = {sid: sid, answer: answer, password: password};

        $.ajax({
            url: '/BindUserWithdrawPwd',
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
    
    // 修改资金密码
    function modifyWithdrawPwd() {
        var html = tpl('#modify_withdraw_password_tpl', {});

        var $dom;
        swal({
            title: '修改资金密码',
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
                    submitModifyWithdrawPwd($dom, function(){
                        resolve();
                    }, function(msg){
                        reject(msg);
                    });
                })
            }
        }).then(function() {
            initSecurityList();
            refreshSecurityLevel();
            $.YF.alert_success('资金密码修改成功，请重新登录！', function () {
                $.removeCookie('USER_NAME');
                top.window.location = '/login';
            });
        }, function() {
        })
    }

    function submitModifyWithdrawPwd($dom, resolve, reject) {
        var $oldPassword = $("#oldPassword", $dom);
        var $password = $("#password", $dom);
        var $rePassword = $("#rePassword", $dom);

        var oldPassword = $oldPassword.val();
        var password = $password.val();
        var rePassword = $rePassword.val();
        if ($.YF.isEmpty(oldPassword)) {
            $oldPassword.focus();
            reject('请输入当前资金密码！');
            return;
        }
        if ($.YF.isEmpty(password)) {
            $password.focus();
            reject('请输入新的资金密码！');
            return;
        }
        if (!$.YF.isPassword(password)) {
            $password.focus();
            reject('必须为英文、数字、字符任意两种组合，长度6-20位！');
            return;
        }
        if ($.YF.isEmpty(rePassword)) {
            $rePassword.focus();
            reject('请输入确认密码！');
            return;
        }
        if (password != rePassword) {
            $rePassword.focus();
            reject('两次密码输入不一致，请检查！');
            return;
        }

        var sid = $("input[name=sid]", $dom).val();

        var token = $.YF.getDisposableToken();
        oldPassword = $.YF.encryptPasswordWithToken(oldPassword, token);
        password = $.YF.generatePassword(password);

        var data = {oldPassword: oldPassword, newPassword: password};

        $.ajax({
            url: '/ModUserWithdrawPwd',
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

    // 绑定安全密保
    function bindSecurity() {
        var html = tpl('#bind_security_tpl', {});

        var $dom;
        swal({
            title: '绑定安全密保',
            customClass:'popup',
            html: html,
            width: 540,
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
                    submitBindSecurity($dom, function(){
                        resolve();
                    }, function(msg){
                        reject(msg);
                    });
                })
            }
        }).then(function() {
            initSecurityList();
            refreshSecurityLevel();
            $.YF.alert_success('安全密保绑定成功！');
        }, function() {
        })
    }

    function submitBindSecurity($dom, resolve, reject) {
        var $question1 = $("#question1", $dom);
        var $question2 = $("#question2", $dom);
        var $question3 = $("#question3", $dom);
        var $answer1 = $("#answer1", $dom);
        var $answer2 = $("#answer2", $dom);
        var $answer3 = $("#answer3", $dom);

        var question1 = $question1.val();
        var question2 = $question2.val();
        var question3 = $question3.val();
        var answer1 = $answer1.val();
        var answer2 = $answer2.val();
        var answer3 = $answer3.val();
        if ($.YF.isEmpty(question1)) {
            reject('请选择密保问题1！');
            return;
        }
        if ($.YF.isEmpty(question2)) {
            reject('请选择密保问题2！');
            return;
        }
        if ($.YF.isEmpty(question3)) {
            reject('请选择密保问题3！');
            return;
        }
        if ($.YF.isEmpty(answer1)) {
            $answer1.focus();
            reject('请输入密保答案1！');
            return;
        }
        if (answer1.length < 2 || answer1.length > 30) {
            $answer1.focus();
            reject('密保答案1长度为2~30，请重新输入！');
            return;
        }
        if ($.YF.isEmpty(answer2)) {
            $answer2.focus();
            reject('请输入密保答案2！');
            return;
        }
        if (answer2.length < 2 || answer2.length > 30) {
            $answer2.focus();
            reject('密保答案2长度为2~30，请重新输入！');
            return;
        }
        if (answer2 == answer1) {
            $answer2.focus();
            reject('密保答案2不能与密保答案1相同！');
            return;
        }
        if ($.YF.isEmpty(answer3)) {
            $answer3.focus();
            reject('请输入密保答案3！');
            return;
        }
        if (answer3.length < 2 || answer3.length > 30) {
            $answer3.focus();
            reject('密保答案3长度为2~30，请重新输入！');
            return;
        }
        if (answer3 == answer1) {
            $answer3.focus();
            reject('密保答案3不能与密保答案1相同！');
            return;
        }
        if (answer3 == answer2) {
            $answer3.focus();
            reject('密保答案3不能与密保答案2相同！');
            return;
        }

        answer1 = $.YF.generatePassword(answer1);
        answer2 = $.YF.generatePassword(answer2);
        answer3 = $.YF.generatePassword(answer3);

        var data = {question1 : question1, answer1: answer1, question2: question2, answer2 : answer2, question3: question3, answer3 : answer3};

        $.ajax({
            url: '/BindUserSecurity',
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

    // 绑定谷歌
    function bindGoogle() {
        $.ajax({
            url : '/BindGoogleGet',
            success : function(res) {
                if(res.data === null){
                    $.YF.alert_warning('请先绑定安全密保！');
                    return;
                }

                var html = tpl('#bind_google_tpl', res.data);

                var $dom;
                swal({
                    title: '绑定谷歌身份验证器',
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

                        var $vCode = $("input[name=vCode]", $dom);
                        $.YF.initInputForceDigit($vCode);
                    },
                    onOpen: function(){
                    },
                    showLoaderOnConfirm: true,
                    preConfirm: function(){
                        return new Promise(function (resolve, reject) {
                            submitBindGoogle($dom, function(){
                                resolve();
                            }, function(msg){
                                reject(msg);
                            });
                        })
                    }
                }).then(function() {
                    initSecurityList();
                    refreshSecurityLevel();
                    $.YF.alert_success('谷歌身份验证器绑定成功！');
                }, function() {
                })
            },
            complete: function() {
            }
        })
    }

    function submitBindGoogle($dom, resolve, reject) {
        var $answer = $("input[name=answer]", $dom);
        var $vCode = $("input[name=vCode]", $dom);

        var answer = $answer.val();
        var vCode = $vCode.val();
        if ($.YF.isEmpty(answer)) {
            $answer.focus();
            reject('请输入密保答案！');
            return;
        }
        if ($.YF.isEmpty(vCode)) {
            $vCode.focus();
            reject('请输入谷歌口令！');
            return;
        }
        if (!$.YF.isDigits(vCode)) {
            $vCode.focus();
            reject('谷歌口令请输入数字！');
            return;
        }

        var sid = $("input[name=sid]", $dom).val();
        var token = $.YF.getDisposableToken();
        answer = $.YF.encryptPasswordWithToken(answer, token);

        var data = {sid : sid, answer: answer, vCode: vCode};

        $.ajax({
            url: '/BindGoogle',
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

    // 关闭异地登录验证
    $contentBox.on('click', '[data-command=closeLoginValidate]', closeLoginValidate);

    // 开启异地登录验证
    $contentBox.on('click', '[data-command=openLoginValidate]', openLoginValidate);

    // 绑定账户姓名
    $contentBox.on('click', '[data-command=bindWithdrawName]', bindWithdrawName);

    // 修改登录密码
    $contentBox.on('click', '[data-command=modifyPassword]', modifyPassword);

    // 绑定资金密码
    $contentBox.on('click', '[data-command=bindWithdrawPwd]', bindWithdrawPwd);

    // 修改资金密码
    $contentBox.on('click', '[data-command=modifyWithdrawPwd]', modifyWithdrawPwd);

    // 绑定安全密保
    $contentBox.on('click', '[data-command=bindSecurity]', bindSecurity);

    // 绑定谷歌
    $contentBox.on('click', '[data-command=bindGoogle]', bindGoogle);

    // 初始化
    init();
})