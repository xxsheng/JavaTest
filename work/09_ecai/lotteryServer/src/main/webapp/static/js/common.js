// 通用JS文件
if ($("#common_template_tpl").length>0) {
    // 锁住所有ready方法，加载模板文件
    $.holdReady(true);
    var url = cdnDomain + '/static/template/common_template.html?v=' + cdnVersion;
    $.ajax({
        type: "GET",
        url: url,
        cache: true,
        complete: function(res) {
            $("#common_template_tpl").html(res.responseText);
            $.holdReady(false);
        }
    });
}

// 本文件仅提供通用组件
(function ($) {
    var $doc = $(document);
    var $body = $(window.document.body);
    
    function _getNetDate (strTime) {
  	   if(strTime == ""){
  		   return "";
  	   }
  	    var date = new Date(strTime);
  	    date.setDate(date.getDate()+1);  
  	    return date.getFullYear()+"-"+getFormatDate((date.getMonth()+1))+"-"+getFormatDate(date.getDate());
  	}

  function getFormatDate(arg) {
      if (arg == undefined || arg == '') {
          return '';
      }

      var re = arg + '';
      if (re.length < 2) {
          re = '0' + re;
      }

      return re;
  }
  

    // *************ajax 全局配置**************** //
    $.ajaxSetup({
        type: 'POST',
        cache: false,
        dataType: 'json',
        timeout: 30000,
        statusCode: {
            404: function(data, status, xhr) {

            },
            405: function(data, status, xhr) {

            },
            500: function(data, status, xhr) {

            }
        },
        sendBefore: function(xhr, status, msg) {
        },
        error: function(xhr, status, msg) {
        }
    })
    // $doc.ajaxSuccess(function(e, xhr, set) {
    //     if (set.ignoreGlobal) {
    //         return;
    //     }
    //
    //     var res
    //     switch (set.dataType) {
    //         case 'json':
    //             res = JSON.parse(xhr.responseText)
    //             if (res.error && res.error > 0) {
    //                 switch (res.code) {
    //                     // 2-1：您的账号已被系统冻结，请联系客服处理!
    //                     // 2-2：密码已更改，请重新登录！；
    //                     // 2-3：您账号在其他地方登录，请您重新登录！
    //                     case "2-1":
    //                     case "2-2":
    //                     case "2-3":
    //                         window.forcelogout = true;
    //                         _alert_warning(res.message, function(){
    //                             top.window.location = '/login'
    //                         });
    //                         break
    //                     // 2-1006: 您还未登录！
    //                     case "2-1006":
    //                         window.forcelogout = true;
    //                         if (set.url !== '/GetGlobal') {
    //                             _alert_warning(res.message, function(index){
    //                                 top.window.location = '/login'
    //                             });
    //                         }
    //                         break
    //                     default:
    //                         _alert_warning(res.message);
    //                 }
    //             }
    //             break
    //     }
    // })
    // *************ajax 全局配置**************** //

    // *************强制输入框使用整数值,且是正整数**************** //
    function _initForceDigit() {
        $(".force-digit").off('keyup').on('keyup', function () {
            _forceDigitsValidate($(this));
        });
        $(".force-digit").off('keydown').on('keydown', function () {
            _forceDigitsValidate($(this));
        });
        $(".force-digit").off('blur').on('blur', function () {
            _forceDigitsValidate($(this));
        });
        $(".force-digit").off('change').on('change', function () {
            _forceDigitsCheck($(this));
        });
    }

    function _initInputForceDigit($input) {
        $input.off('keyup').on('keyup', function () {
            _forceDigitsValidate($input);
        });
        $input.off('keydown').on('keydown', function () {
            _forceDigitsValidate($input);
        });
        $input.off('blur').on('blur', function () {
            _forceDigitsValidate($input);
        });
        $input.off('change').on('change', function () {
            _forceDigitsCheck($input);
        });
    }

    function _forceDigitsValidate($input) {
        var val = $input.val();
        if (val === '') {
            return
        }

        if (/^[0-9]*$/.test(val)) {
        }
        else {
            if(val.length == 1){
                $input.val('');
            }else{
                val = Number(val.replace(/\D/g, ''));
                $input.val(val);
            }
        }
    }

    function _forceDigitsCheck($input) {
        var val = $input.val();
        if (val === '') {
            if ( typeof($input.attr('data-default')) != 'undefined' ) {
                $input.val($input.attr('data-default'));
            }
        }
        else {
            if (/^[0-9]*$/.test(val)) {
                val = Number(val);
                var minVal = $input.attr('data-min');
                var maxVal = $input.attr('data-max');
                var defaultVal = $input.attr('data-default');
                if (minVal && val < Number(minVal)) {
                    $input.val(defaultVal);
                }
                else if (maxVal && val > Number(maxVal)) {
                    $input.val(defaultVal);
                }
            } else {
                if(val.length == 1){
                    if ( typeof($input.attr('data-default')) != 'undefined' ) {
                        $input.val($input.attr('data-default'));
                    }
                    else {
                        $input.val('');
                    }
                }else{
                    val = Number(val.replace(/\D/g, ''));
                    $input.val(val);
                }
            }
        }
    }
    // *************强制输入框使用整数值,且是正整数**************** //

    // *************加载动画**************** //
    // var _loadingContainer = $('<div class="_load-mask"></div><div class="_loader ball-clip-rotate-multiple"><div></div><div></div></div>');
    var _loadingContainer = $('<div class="_load-mask"></div><div class="_loader ball-logo"><img src="'+cdnDomain+'static/images/nav10.png" class="spin-infinite"/><p>Loading...</p></div>');
    function _showLoadingMask(container) {
        _hideLoadingMask();

        var _container;
        if (container) {
            _container = container;
        }
        else {
            _container = $body;
        }

        var borderRadius = _container.css('border-radius');
        _container.append(_loadingContainer);

        if (borderRadius) {
            _container.find('._load-mask').css('border-radius', borderRadius);
        }
    }
    function _hideLoadingMask() {
        if (_loadingContainer) {
            _loadingContainer.remove();
        }
    }
    // *************加载动画**************** //

    // *************全屏全黑加载动画**************** //
    var _fullDarkLoadingContainer = $('<div class="_full-dark-load-mask"></div><div class="_loader ball-logo"><img src="'+cdnDomain+'static/images/nav10.png" class="spin-infinite"/><p>Loading...</p></div>');
    function _showFullDarkLoadingMask() {
        _hideFullDarkLoadingMask();

        var _container = $body;

        _container.append(_fullDarkLoadingContainer);
    }
    function _hideFullDarkLoadingMask() {
        if (_fullDarkLoadingContainer) {
            _fullDarkLoadingContainer.remove();
        }
    }
    // *************全屏全黑加载动画**************** //

    // *************弹窗**************** //
    // 询问
    function _alert_question(text, promise, confirmCallback, cancelCallback) {
        swal({
            title: '温馨提示',
            html: text,
            width: 320,
            type: 'question',
            customClass: 'alert',
            showCloseButton: false,
            showCancelButton: true,
            showConfirmButton: true,
            confirmButtonText: '确认',
            cancelButtonText: '关闭',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel',
            showLoaderOnConfirm: true,
            preConfirm: function(){
                return new Promise(function (resolve, reject) {
                    promise(function(){
                        resolve();
                    }, function(msg){
                        reject(msg);
                    });
                })
            }
        }).then(function() {
            if (confirmCallback && $.isFunction(confirmCallback)) {
                confirmCallback();
            }
        }, function() {
            if (cancelCallback && $.isFunction(cancelCallback)) {
                cancelCallback();
            }
        })
    }
    function _alert_login(text, promise, confirmCallback, cancelCallback) {
        swal({
            title: '温馨提示',
            html: text,
            width: 500,
            type: 'question',
            customClass: 'alert',
            showCloseButton: false,
            showCancelButton: true,
            showConfirmButton: true,
            confirmButtonText: '确认',
            cancelButtonText: '关闭',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel',
            showLoaderOnConfirm: true,
            preConfirm: function(){
                return new Promise(function (resolve, reject) {
                    promise(function(){
                        resolve();
                    }, function(msg){
                        reject(msg);
                    });
                })
            }
        }).then(function() {
            if (confirmCallback && $.isFunction(confirmCallback)) {
                confirmCallback();
            }
        }, function() {
            if (cancelCallback && $.isFunction(cancelCallback)) {
                cancelCallback();
            }
        })
    }
    // 信息
    function _alert_info(text, confirmCallback, time) {
        swal({
            title: '温馨提示',
            html: text,
            width: 320,
            type: 'info',
            timer: time,
            showCloseButton: false,
            showCancelButton: false,
            showConfirmButton: true,
            confirmButtonText: '确认',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel'
        }).then(function() {
            if (confirmCallback) {
                confirmCallback();
            }
        }, function() {
        })
    }
    // 消息
    function _alert_message(title, text, confirmCallback) {
        swal({
            title: title,
            html: text,
            width: 320,
            showCloseButton: false,
            showCancelButton: false,
            showConfirmButton: true,
            confirmButtonText: '确认',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel'
        }).then(function() {
            if (confirmCallback) {
                confirmCallback();
            }
        }, function() {
        })
    }
    // 错误
    function _alert_error(text, confirmCallback) {
        swal({
            title: '温馨提示',
            html: text,
            width: 320,
            type: 'error',
            showCloseButton: false,
            showCancelButton: false,
            showConfirmButton: true,
            confirmButtonText: '确认',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel'
        }).then(function() {
            if (confirmCallback) {
                confirmCallback();
            }
        }, function() {
        })
    }
    // 警告
    function _alert_warning(text, confirmCallback) {
        swal({
            title: '温馨提示',
            html: text,
            width: 320,
            type: 'warning',
            showCloseButton: false,
            showCancelButton: false,
            showConfirmButton: true,
            confirmButtonText: '确认',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel'
        }).then(function() {
            if (confirmCallback) {
                confirmCallback();
            }
        }, function() {
        })
    }
    // 成功
    function _alert_success(text, confirmCallback) {
        swal({
            title: '温馨提示',
            html: text,
            width: 320,
            type: 'success',
            timer: 10000,
            showCloseButton: false,
            showCancelButton: false,
            showConfirmButton: true,
            confirmButtonText: '确认',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel'
        }).then(function() {
            if (confirmCallback) {
                confirmCallback();
            }
        }, function() {
        })
    }
    // 显示加载中
    function _alert_showLoading() {
        swal.showLoading();
    }
    // 隐藏加载中
    function _alert_hideLoading() {
        swal.hideLoading();
    }
    
    // tooltip
    function _alert_tooltip(text, dom) {
        layer.tips(text, dom, {tips: [1, '#777d83']});
    }
    // *************弹窗**************** //

    // *************时间**************** //
    function _initDateUI($dom) {
        if (window.laydate) {
            laydate.render({
                elem: $dom,
                btns: ['now', 'confirm'],
                theme: '#ea5050'
            });
        }
    }
    // *************时间**************** //

    // *************密码**************** //
    function _getDisposableToken (){
        var token = null;
        $.ajax({
            url : '/DisposableToken',
            type: 'POST',
            async: false,
            success : function(tokenRes) {
                if (tokenRes.error === 0) {
                    token = tokenRes.token;
                }
            },
            complete: function() {
            },
            error: function(){
            }
        });
        return token;
    }

    function _encryptPasswordWithToken (plainStr, token) {
        var password = hex_md5(plainStr).toUpperCase();
        password = hex_md5(password).toUpperCase();
        password = hex_md5(password).toUpperCase();
        password = hex_md5(password + token).toUpperCase();
        return password;
    }
    

    function _generatePassword (plainStr) {
        var password = hex_md5(plainStr).toUpperCase();
        password = hex_md5(password).toUpperCase();
        return password;
    }
    // *************密码**************** //
    
    // *************工具**************** //
    function _getUrlParam (location, name) {
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = location.substr(1).match(reg);
        if(r!=null)return  unescape(r[2]); return null;
    }
    // *************工具**************** //

    // 格式化时间
    function _formatTime(seconds) {
        var s = 1, m = 60 * s, h = m * 60
        var ss = 0, mm = 0, hh = 0
        if (s > 0) {
            hh = Math.floor(seconds / h)
            mm = Math.floor(seconds % h / m)
            ss = Math.floor(seconds % h % m / s)
        }
        var p = function(t) {
            return t < 10 ? '0' + t : t
        }
        return [ p(hh), p(mm), p(ss) ]
    }

    // 是否已经被服务器强制登出
    function _isForceLogout() {
        return (window.forcelogout && window.forcelogout === true) || (top.window.forcelogout && top.window.forcelogout === true);
    }

    // 数字转货币数值
    function _formatMoney(number, places, thousand, decimal) {
        number = number || 0;
        places = !isNaN(places = Math.abs(places)) ? places : 2;
        thousand = thousand || ",";
        decimal = decimal || ".";
        var negative = number < 0 ? "-" : "",
            i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
            j = (j = i.length) > 3 ? j % 3 : 0;
        return negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
    }

    // 使用截取设置最大精度，不使用四舍五入
    var _setMaxScale = function(value, maxScale) {
        if (value == 0) {
            return 0;
        }
        var valStr = value.toString();
        var nums = valStr.split(".");
        if(nums.length == 1){
            return value;
        }
        if (nums.length>1) {
            if(nums[1].length > maxScale){
                var end = nums[0].length + 1 + maxScale;
                return parseFloat(valStr.substring(0, end));
            }
            return value;
        }
        return value;
    }

    // 根据银行ID格式化银行名称
    function _formatBankName(bankId) {
        if (bankId == 1) return '中国工商银行';
        if (bankId == 2) return '中国建设银行';
        if (bankId == 3) return '中国农业银行';
        if (bankId == 4) return '招商银行';
        if (bankId == 5) return '中国银行';
        if (bankId == 6) return '交通银行';
        if (bankId == 7) return '浦发银行';
        if (bankId == 8) return '兴业银行';
        if (bankId == 9) return '中信银行';
        if (bankId == 10) return '宁波银行';
        if (bankId == 11) return '上海银行';
        if (bankId == 12) return '杭州银行';
        if (bankId == 13) return '渤海银行';
        if (bankId == 14) return '浙商银行';
        if (bankId == 15) return '广发银行';
        if (bankId == 16) return '中国邮政储蓄';
        if (bankId == 17) return '深圳发展银行';
        if (bankId == 18) return '中国民生银行';
        if (bankId == 19) return '中国光大银行';
        if (bankId == 20) return '华夏银行';
        if (bankId == 21) return '北京银行';
        if (bankId == 22) return '南京银行';
        if (bankId == 23) return '平安银行';
        if (bankId == 24) return '北京农村商';

        return '未知';
    }

    // 格式化充值通道银行列表
    function _formatRechargeBanks(channel) {
        var banks = new Array();
        // 1：网银充值
        if(channel.type == 1) {
            // 1：网银在线；2：网银转账；3：快捷支付；4：银联扫码；5：网银扫码转账
            if (channel.subType == 3) {
                var bank = {bankId: -1, name: '快捷支持', bankco: ''};
                banks.push(bank);
            }
            else if (channel.subType == 4) {
                var bank = {bankId: -1, name: '银联扫码', bankco: ''};
                banks.push(bank);
            }
            else if (channel.subType == 5) {
                var bank = {bankId: -1, name: '网银扫码转账', bankco: ''};
                banks.push(bank);
            }
            else if (channel.subType == 1 || channel.subType == 2) {
                $.each(channel.banklist, function(index, bankItem){
                    var bank = {bankId: bankItem.bankId, bankco: bankItem.code, name: _formatBankName(bankItem.bankId)};
                    banks.push(bank);
                });
            }
        }
        else if(channel.type == 2) {
            // 1：微信在线；2：微信扫码转账；3：支付宝在线；4：支付宝扫码转账；5：QQ在线、6：QQ扫码转账；7：京东钱包
            if (channel.subType == 1 || channel.subType == 2) {
                var bank = {bankId: -1, name: '微信', bankco: ''};
                banks.push(bank);
            }
            else if (channel.subType == 3 || channel.subType == 4) {
                var bank = {bankId: -1, name: '支付宝', bankco: ''};
                banks.push(bank);
            }
            else if (channel.subType == 5 || channel.subType == 6) {
                var bank = {bankId: -1, name: 'QQ钱包', bankco: ''};
                banks.push(bank);
            }
            else if (channel.subType == 7) {
                var bank = {bankId: -1, name: '京东钱包', bankco: ''};
                banks.push(bank);
            }
        }

        return banks;
    }

    // 格式化充值通道名称
    function _formatPaymentChannelType(type) {
        // 1：网银充值
        if(type == 1) {
            return '网银充值';
        }
        // 2：手机充值
        if(type == 2) {
            return '手机充值';
        }
        // 3：系统充值
        if(type == 3) {
            return '系统充值';
        }
        // 4：上下级转账
        if(type == 4) {
            return '上下级转账';
        }
        return '未知';
    }
    
    // 判断是否是空，排除空格
    function _isEmpty(val) {
        if (/^\s+$/.test(val) || val.length < 1) {
            return true;
        }
        
        return false;
    }
    
    // 判断是否是扫码充值
    function _isManualScanChannel(type, subType) {
        if (type == 2) {
            if (subType == 2 || subType == 4 || subType == 6) {
                return true;
            }
        }
        return false;
    }

    // 判断是否是微信扫码充值
    function _isManualWeChatScanChannel(type, subType) {
        if (type == 2 && subType == 2) {
            return true;
        }
        return false;
    }

    // 判断是否是支付宝扫码充值
    function _isManualAliPayScanChannel(type, subType) {
        if (type == 2 && subType == 4) {
            return true;
        }
        return false;
    }

    // 判断是否是QQ扫码充值
    function _isManualQQScanChannel(type, subType) {
        if (type == 2 && subType == 6) {
            return true;
        }
        return false;
    }

    // 判断是否是微信扫码充值
    function _isWeChatChannel(type, subType) {
        if (type == 2 && (subType == 1 || subType == 2)) {
            return true;
        }
        return false;
    }

    // 判断是否是支付宝扫码充值
    function _isAliPayChannel(type, subType) {
        if (type == 2 && (subType == 3 || subType == 4)) {
            return true;
        }
        return false;
    }

    // 判断是否是QQ扫码充值
    function _isQQChannel(type, subType) {
        if (type == 2 && (subType == 5 || subType == 6)) {
            return true;
        }
        return false;
    }

    // 判断是否快捷网银
    function _isSpeedChannel(type, subType) {
        if (type == 1 && subType == 3) {
            return true;
        }
        return false;
    }

    // 判断是否京东钱包
    function _isJDChannel(type, subType) {
        if (type == 2 && subType == 7) {
            return true;
        }
        return false;
    }

    // 判断是否是数字
    function _isDigits (value) {
        var reg = /^[0-9]+$/ ;
        return reg.test(value);
    }
    
    // 判断是否是正整数
    function _isDigitsZZS (value) {
        var reg = /^[0-9]+$/ ;
        var valid = reg.test(value);
        if (valid) {
            if (Number(valid) < 0) {
                return false;
            }
        }
        return valid;
    }

    // 判断是否是中文
    function _isChinese (value) {
        return /^[\u0391-\uFFE5]{2,12}(?:·[\u0391-\uFFE5]{2,12})*$/.test(value);
    }

    // 判断是否是密码
    function _isPassword (value) {
        return /^(?![\d]+$)(?![a-zA-Z]+$)(?![~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]+$)[\da-zA-Z~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]{6,20}$/.test(value);
    }
    
    // 判断是否是用户名
    function _isUserName (value) {
        return /[a-zA-Z][a-zA-Z0-9]{5,11}/.test(value);
    }

    // 清理setTimeout or setInterval
    function _clearTimer(timer) {
        if (timer) {
            clearTimeout(timer)
            clearInterval(timer)
            timer = null
        }
    }

    // 将秒转换成小时/分钟/秒
    function _formatSecondsToHMS (seconds) {
        var s = 1, m = 60 * s, h = m * 60
        var ss = 0, mm = 0, hh = 0
        if (s > 0) {
            hh = Math.floor(seconds / h)
            mm = Math.floor(seconds % h / m)
            ss = Math.floor(seconds % h % m / s)
        }
        var p = function(t) {
            return t < 10 ? '0' + t : t
        }
        return [ p(hh), p(mm), p(ss) ]
    }

    // 给指定日期(年月日)加上或减去天数，返回年月日
    function _addDayForDate (date, days){
        var d=new Date(date);
        d.setDate(d.getDate()+days);
        var month=d.getMonth()+1;
        var day = d.getDate();
        if(month<10){
            month = "0"+month;
        }
        if(day<10){
            day = "0"+day;
        }
        var val = d.getFullYear()+"-"+month+"-"+day;
        return val;
    }

    // 是否是正数小数
    function _isPositiveFloatOrDigits(value) {
        if (_isDigits(value)) {
            return true;
        }
        if (!$.isNumeric(value)) {
            return false;
        }
        var num = Number(value);
        if (num < 0) {
            return false;
        }
        return true;
    }

    function _greeting() {
        var d = new Date();
        var hour = d.getHours();
        if(hour >= 6 && hour < 11) {
            return '早上好';
        }
        if(hour >= 11 && hour < 13) {
            return '中午好';
        }
        if(hour >= 13 && hour < 19) {
            return '下午好';
        }
        if(hour >= 19 || hour < 6) {
            return '晚上好';
        }
    }

    function _numberAdd(num1, num2) {
        return (num1 * 10 + num2 * 10) / 10;
    }

    function _numberSub(num1, num2) {
        return (num1 * 10 - num2 * 10) / 10;
    }
    
    function _isProxy(type) {
        return type == 1 || type == 3;
    }

    window.GlobalFun = {
        formatBankName: _formatBankName,
        formatMoney: _formatMoney,
        formatUserType: function(type) {
            if(type == 1 || type == 3) {
                return '代理';
            }
            if(type == 2) {
                return '玩家';
            }
        },
        formatUserOnlineStatus: function(status) {
            if(status == 0) {
                return '离线';
            }
            if(status == 1) {
                return '<span class="co_1">在线</span>';
            }
        },
        formatUserCardStatus: function(status) {
            if(status == 0) {
                return '正常';
            }
            if(status == -1) {
                return '资料无效';
            }
            if(status == -2) {
                return '已锁定';
            }
        },
        formatUserRechargeType: function(type) {
            if(type == 1) {
                return '网银充值';
            }
            if(type == 2) {
                return '转账汇款';
            }
            if(type == 3) {
                return '系统充值';
            }
            if(type == 4) {
                return '手机充值';
            }
        },
        formatUserWithdrawalsStatus: function(status) {
            if(status == 0) {
                return '待处理';
            }
            if(status == 1) {
                return '已完成';
            }
            if(status == 2) {
                return '处理中';
            }
            if(status == 3) {
                return '银行处理中';
            }
            if(status == 4) {
                return '提现失败';
            }
            if(status == -1) {
                return '拒绝支付';
            }
        },
        formatUserTransfersType: function(type) {
            if(type == 0) {
                return '平台转账';
            }
            if(type == 1) {
                return '上下级转账（存）';
            }
            if(type == 2) {
                return '上下级转账（取）';
            }
        },
        formatUserBetsModel: function(model) {
            if(model == 'yuan') {
                return '2元';
            }
            if(model == 'jiao') {
                return '2角';
            }
            if(model == 'fen') {
                return '2分';
            }
            if(model == 'li') {
                return '2厘';
            }
            if(model == '1yuan') {
                return '1元';
            }
            if(model == '1jiao') {
                return '1角';
            }
            if(model == '1fen') {
                return '1分';
            }
            if(model == '1li') {
                return '1厘';
            }
        },
        formatUserBetsStatus: function(status) {
            if(status == 0) {
                return '<span class="user_bet_status_normal">未开奖</span>';
            }
            if(status == 1) {
                return '<span class="user_bet_status_loss">未中奖</span>';
            }
            if(status == 2) {
                return '<span class="user_bet_status_win">已中奖</span>';
            }
            if(status == -1) {
                return '<span class="user_bet_status_normal">已撤单</span>';
            }
        },
        formatMoneyStatus: function(money) {
            if (money > 0)
                return '<span class="money_status_win">' + money.toFixed(2) + '</span>'
            if (money < 0)
                return '<span class="money_status_loss">' + money.toFixed(2) + '</span>'
            return '<span class="money_status_normal">'+money.toFixed(2)+'</span>';
        },
        formatMoneyStatus2: function(money) {
            // if (money > 0)
            //     return '<span class="fc-c00">' + money.toFixed(2) + '</span>'
            // if (money < 0)
            //     return '<span class="fc-390">' + money.toFixed(2) + '</span>'
            return money.toFixed(2)
        },
        formatUserBillType: function(type) {
            if(type == 1) {
                return '存款';
            }
            if(type == 2) {
                return '取款';
            }
            if(type == 3) {
                return '转入';
            }
            if(type == 4) {
                return '转出';
            }
            if(type == 5) {
                return '优惠活动';
            }
            if(type == 6) {
                return '投注';
            }
            if(type == 7) {
                return '派奖';
            }
            if(type == 8) {
                return '投注返点';
            }
            if(type == 9) {
                return '代理返点';
            }
            if(type == 10) {
                return '撤销订单';
            }
            if(type == 11) {
                return '会员返水';
            }
            if(type == 12) {
                return '代理分红';
            }
            if(type == 13) {
                return '管理员增';
            }
            if(type == 14) {
                return '管理员减';
            }
            if(type == 15) {
                return '上下级转账';
            }
            if(type == 16) {
                return '取款退回';
            }
            if(type == 17) {
                return '积分兑换';
            }
            if(type == 18) {
                return '支付佣金';
            }
            if(type == 19) {
                return '获得佣金';
            }
            if(type == 20) {
                return '退还佣金';
            }
            if(type == 21) {
                return '红包';
            }
            if(type == 22) {
                return '日结';
            }
            if(type == 23) {
                return '转账';
            }
        },
        formatUserBillStatus: function(status) {
            if(status == 1) {
                return '正常';
            }
            if(status == -1) {
                return '无效';
            }
        },
        formatUserMessageType: function(type) {
            if(type == 0) {
                return '建议反馈';
            }
            if(type == 1) {
                return '已收消息';
            }
            if(type == 2) {
                return '已发消息';
            }
        },
        formatUserMessageStatus: function(status, type) {
            if(status == 0) {
                return '未读';
            }
            if(status == 1) {
                return '已读';
            }
            if(status == -1) {
                return '已删除';
            }
        },
        formatUserSysMessageType: function(type) {
            if(type == 0) {
                return '系统通知';
            }
            if(type == 1) {
                return '到账通知';
            }
            if(type == 2) {
                return '提现通知';
            }
        },
        formatLotteryPaymentThridType: function(type) {
            switch (type) {
                case 'ips':
                    return '环讯支付'
                case 'baofoo':
                    return '宝付支付'
                case 'newpay':
                    return '新生支付'
                case 'ecpss':
                    return '汇潮支付'
                case 'yeepay':
                    return '易宝支付'
                case 'gopay':
                    return '国付宝支付'
                case 'pay41':
                    return '通汇支付'
                case 'dinpay':
                    return '智付支付'
                case 'mobile':
                    return '微信 & 支付宝'
                case 'dinpayWeChat':
                case 'pay41WeChat':
                case 'mobaoWeChat':
                case 'huanstWeChat':
                case 'ifbaoWeChat':
                case 'bcWeChat':
                case 'lepayWeChat':
                case 'gstWeChat':
                case 'ystWeChat':
                case 'zhfWeChat':
                case 'xinbeiWeChat':
                case 'zhfWeChat2':
                case 'jiushuiWeChat':
                case 'zsWeChat':
                case 'zfWeChat':
                case 'hhWeChat':
                case 'zftWeChat':
                case 'rxWeChat':
                    return '微信支付'
                case 'lepayAlipay':
                case 'gstAlipay':
                case 'quickAlipay':
                case 'ystAlipay':
                case 'xunbaoAlipay':
                case 'zhfAlipay':
                case 'zhfAlipay2':
                case 'xinbeiAlipay':
                case 'jiushuiAlipay':
                case 'zsAlipay':
                case 'zfAlipay':
                case 'mobaoAlipay':
                case 'hhAlipay':
                case 'zftAlipay':
                case 'rxAlipay':
                    return '支付宝'
                case 'xunbaoQQ':
                case 'zsQQ':
                case 'zhfQQ':
                case 'zfQQ':
                case 'xinbeiQQpay':
                case 'mobaoQQpay':
                case 'hhQQ':
                case 'zftQQ':
                case 'rxQQ':
                    return 'QQ钱包'
                case 'lepay':
                    return '乐付支付'
                case 'lepaySpeed':
                    return '快捷支付'
                case 'gst':
                    return '国盛通支付'
                case 'bankTransfer':
                    return '网银转账'
                case 'quickPay':
                    return '闪付支付'
                case 'mkt':
                    return '秒卡通支付'
                case 'xunbao':
                    return '讯宝支付'
                case 'zhf':
                case 'zhf2':
                    return '智汇付'
                case 'xinbei':
                    return '新贝网银'
                case 'jiushui':
                    return '玖水支付'
                case 'aos':
                    return 'AOS支付'
                case 'zs':
                    return '泽圣网银'
                case 'zf':
                    return '智付网银'
                case 'ht':
                    return '汇通网银'
                case 'htQQ':
                    return 'QQ钱包'
                case 'hh' :
                    return '汇合网银'
                case 'zft' :
                    return '支付通网银'
                case 'rx' :
                    return '荣讯网银'
                case 'scanCodeWeChat' :
                    return '微信扫码'
                case 'scanCodeAlipay' :
                    return '支付宝扫码'
                case 'scanCodeQQ' :
                    return 'QQ钱包扫码'
                case 'rxJDPay' :
                    return '京东钱包'
            }
        },
        formatDailySettleStatus: function(status) {
            if(status == 1) {
                return '已生效';
            }
            if(status == 2) {
                return '待同意';
            }
            if(status == 3) {
                return '已过期';
            }
            if(status == 4) {
                return '无效';
            }
            if(status == 5) {
                return '已拒绝';
            }
            return '';
        },
        formatDailySettleBillStatus: function(status) {
            // 1：已发放；2：部分发放；3：余额不足；4：未达标；5：已拒绝；
            if(status == 1) {
                return '<span class="c-green">已发放</span>';
            }
            if(status == 2) {
                return '<span class="c-green">部分发放</span>';
            }
            if(status == 3) {
                return '<span class="c-red">余额不足</span>';
            }
            if(status == 4) {
                return '未达标';
            }
            if(status == 5) {
                return '<span class="c-red">已拒绝</span>';
            }
            return '';
        },
        formatDividendStatus: function(status) {
            if(status == 1) {
                return '已生效';
            }
            if(status == 2) {
                return '待同意';
            }
            if(status == 3) {
                return '已过期';
            }
            if(status == 4) {
                return '无效';
            }
            if(status == 5) {
                return '已拒绝';
            }
            return '';
        },
        formatDividendBillStatus: function(status) {
            if(status == 1) {
                return '<span class="c-green">已发放</span>';
            }
            if(status == 2) {
                return '核对中';
            }
            if(status == 3) {
                return '待领取';
            }
            if(status == 4) {
                return '<span class="c-red">已拒绝</span>';
            }
            if(status == 5) {
                return '未达标';
            }
            if(status == 6) {
                return '<span class="c-red">余额不足</span>';
            }
            if(status == 7) {
                return '<span class="c-green">部分发放</span>';
            }
            if(status == 8) {
                return '<span class="c-red">已过期</span>';
            }
            return '';
        },
        formatGameDividendBillStatus: function(status) {
            if(status == 1) {
                return '<span class="fc-390">已发放</span>';
            }
            if(status == 2) {
                return '<span>核对中</span>';
            }
            if(status == 3) {
                return '<span>待领取</span>';
            }
            if(status == 4) {
                return '<span>已拒绝</span>';
            }
            if(status == 5) {
                return '未达标';
            }
            return '';
        },
        formatGameWaterBillStatus: function(status) {
            if(status == 1) {
                return '<span class="fc-390">已发放</span>';
            }
            if(status == 2) {
                return '<span>已拒绝</span>';
            }
            return '';
        },
        formatGameStatus: function(status) {
            if(status == -1) {
                return '未知';
            }
            if(status == 1) {
                return '完成';
            }
            if(status == 2) {
                return '等待中';
            }
            if(status == 3) {
                return '进行中';
            }
            if(status == 4) {
                return '赢';
            }
            if(status == 5) {
                return '输';
            }
            if(status == 6) {
                return '平局';
            }
            if(status == 7) {
                return '拒绝';
            }
            if(status == 8) {
                return '退钱';
            }
            if(status == 9) {
                return '取消';
            }
            if(status == 10) {
                return '上半场赢';
            }
            if(status == 11) {
                return '上半场输';
            }
            if(status == 12) {
                return '和';
            }
            return '';
        },
        isManualScanChannel: _isManualScanChannel,
        isManualWeChatScanChannel: _isManualWeChatScanChannel,
        isManualAliPayScanChannel: _isManualAliPayScanChannel,
        isManualQQScanChannel: _isManualQQScanChannel,
        isWeChatChannel: _isWeChatChannel,
        isAliPayChannel: _isAliPayChannel,
        isQQChannel: _isQQChannel,
        isSpeedChannel: _isSpeedChannel,
        isJDChannel: _isJDChannel,
        formatPaymentChannelType: _formatPaymentChannelType,
        inArray: $.inArray
    }

    _initForceDigit();

    $.YF = function () { };
    $.extend($.YF,
        {
            initInputForceDigit: _initInputForceDigit,
            initForceDigit: _initForceDigit,
            getNetDate:_getNetDate,
            showLoadingMask: _showLoadingMask,
            hideLoadingMask: _hideLoadingMask,
            showFullDarkLoadingMask: _showFullDarkLoadingMask,
            hideFullDarkLoadingMask: _hideFullDarkLoadingMask,
            alert_question: _alert_question,
            alert_login: _alert_login,
            alert_info: _alert_info,
            alert_message: _alert_message,
            alert_error: _alert_error,
            alert_warning: _alert_warning,
            alert_success: _alert_success,
            alert_showLoading: _alert_showLoading,
            alert_hideLoading: _alert_hideLoading,
            alert_tooltip: _alert_tooltip,
            initDateUI: _initDateUI,
            getDisposableToken: _getDisposableToken,
            encryptPasswordWithToken: _encryptPasswordWithToken,
            generatePassword: _generatePassword,
            getUrlParam: _getUrlParam,
            formatTime: _formatTime,
            isForceLogout: _isForceLogout,
            formatMoney: _formatMoney,
            setMaxScale: _setMaxScale,
            formatRechargeBanks: _formatRechargeBanks,
            formatPaymentChannelType: _formatPaymentChannelType,
            formatBankName: _formatBankName,
            isEmpty: _isEmpty,
            isManualScanChannel: _isManualScanChannel,
            isManualWeChatScanChannel: _isManualWeChatScanChannel,
            isManualAliPayScanChannel: _isManualAliPayScanChannel,
            isManualQQScanChannel: _isManualQQScanChannel,
            isWeChatChannel: _isWeChatChannel,
            isAliPayChannel: _isAliPayChannel,
            isQQChannel: _isQQChannel,
            isSpeedChannel: _isSpeedChannel,
            isJDChannel: _isJDChannel,
            isDigits: _isDigits,
            isDigitsZZS: _isDigitsZZS,
            isChinese: _isChinese,
            isPassword: _isPassword,
            isUserName: _isUserName,
            clearTimer: _clearTimer,
            formatSecondsToHMS: _formatSecondsToHMS,
            addDayForDate: _addDayForDate,
            isPositiveFloatOrDigits: _isPositiveFloatOrDigits,
            greeting: _greeting,
            numberAdd: _numberAdd,
            numberSub: _numberSub,
            isProxy: _isProxy
        }
    );
})(jQuery);