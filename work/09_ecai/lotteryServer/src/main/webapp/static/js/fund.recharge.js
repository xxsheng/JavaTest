$(function(){
    $.fn.serializeObject = function(){
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    }
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $channelTab = $("#channelTab", $content);
    var $channelContent = $("#channelContent", $content);
    var $redirectForm = $("#redirectForm", $content);
    var selectedChannel;
    var paymentChannels;

    function init() {
        // 初始化充值通道
        initPaymentChannels();
        // $.each(paymentChannels, function(index, channel){
        //     if (channel.id == channelId) {
        //         showChannel(channel)
        //         return false;
        //     }
        // });
    }

    // 初始化充值通道
    function initPaymentChannels() {
        $.ajax({
            url : '/ListPayment',
            success : function(res) {
                if (res.error === 0) {
                	if(res.code =='2-13'){
                	    $.YF.alert_warning(res.message, function(){
                        });
                		   return;
                	}
                    if (!res.hasWithdrawPwd) {
                        $.YF.alert_warning('请先设置资金密码再进行充值操作！', function(){
                            parent.window.location = '/manager?link=account-manager';
                        });
                        return;
                    }

                    $channelTab.html(tpl('#channel_tab_tpl', {channels: res.channels, cdnDomain: cdnDomain}))
                  
                    paymentChannels = res.channels;
                    // 显示第一个充值通道
                    showChannel(res.channels[0]);
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            }
        })
    }

    // 渲染充值通道
    function showChannel(channel) {
        selectedChannel = channel;
    	 $channelContent.html(tpl('#channel_content_tpl', {channel: selectedChannel, cdnDomain: cdnDomain}))
     
        
        // 存款金额只能输入数字
        if (channel.fixedQRAmount == 1 && $.YF.isManualScanChannel(channel.type, channel.subType)) {
        }
        else {
            var $amount = $channelContent.find('input[name=inputAmount]');
            $.YF.initInputForceDigit($amount);
        }
    }

    // 充值通道点击
    $channelTab.off('click', '.tab');
    $channelTab.on('click', '.tab', function(){
        var $this = $(this);
        if ($this.hasClass('cur')) {
            return;
        }

        $this.addClass('cur').siblings('.cur').removeClass('cur');

        var channelId = $this.attr('data-channel-id');

        $.each(paymentChannels, function(index, channel){
            if (channel.id == channelId) {
                showChannel(channel)
                return false;
            }
        });
    });

    // 快速选择金额
    $channelContent.off('click', '[data-command=fastAmount]');
    $channelContent.on('click', '[data-command=fastAmount]', function(){
        var $this = $(this);
        var amount = $this.attr('data-val');
        $channelContent.find('input[name=inputAmount]').val(amount);
        layer.closeAll('tips');
    });

    // 固定金额选择事件
    $channelContent.off('click', '.fixed-amount');
    $channelContent.on('click', '.fixed-amount', function(){
        var $this = $(this);
        if ($this.hasClass('cur')) {
            return;
        }

        $this.addClass('cur').siblings('.cur').removeClass('cur');
    });

    // 支付方式点击
    $channelContent.off('click', '.bank i');
    $channelContent.on('click', '.bank i', function(){
        var $this = $(this);
        if ($this.hasClass('cur')) {
            return;
        }

        $this.addClass('cur').siblings('.cur').removeClass('cur');
    });
    
    // 支付方式点击
    $channelContent.off('click', '[name =addBankTransfer]');
    $channelContent.on('click', '[name =addBankTransfer]', function(){
    var data = $("form[name = addBankTransfersFrom]").serializeObject(); //自动将form表单封装成json      
     data.pid = selectedChannel.id;
     var name = data.name;
     if ($.YF.isEmpty(name)) {
    	 $.YF.alert_warning('请输入账户姓名！');
         return;
     }
     if (!$.YF.isChinese(name)) {
    	 $.YF.alert_warning('姓名只能输入中文！');
         return;
     }
     var postscript = data.postscript;
     if ($.YF.isEmpty(postscript)) {
    	 $.YF.alert_warning('请输入附言！');
         return;
     }
     if ($.YF.isPassword(postscript)) {
    	 $.YF.alert_warning(' 不能输入特殊字符！');
         return;
     }
     
    	$.YF.showLoadingMask();
        $.ajax({
            url: '/BankTransfers',
            data: data,
            success: function(res) {
            	console.log(res);
                if (res.error === 0) {
             	 $channelContent.html('<div style = "text-align:center ;font-size:16px"><div class="swal2-icon swal2-info" >√</div><p>您的充值申请已提交，系统正在处理中....</p></div>');
                }else{
                	 $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
                $.YF.hideLoadingMask();
            }
        })
    });
    
    

    // 下一步
    
    $channelContent.off('click', '[data-command=next]');
    $channelContent.on('click', '[data-command=next]', function(){
        if (!selectedChannel) {
            $.YF.alert_warning('请选择充值方式！');
            return;
        }
        	   var $selectedBank = $channelContent.find('.bank i.cur');
               if ($selectedBank.length <= 0) {
                   $.YF.alert_warning('请选择银行');
                   return;
               }


        var pid = selectedChannel.id;
        var channelType = selectedChannel.type;
        var channelSubType = selectedChannel.subType;
        var channelCode = selectedChannel.channelCode;
        var fixedQRAmount = selectedChannel.fixedQRAmount;
        var bankId = $selectedBank.attr('data-bankId');
        var bankco = $selectedBank.attr('data-bankco');

        var amount;
        var qrCodeId;
        if (fixedQRAmount == 1 && $.YF.isManualScanChannel(channelType, channelSubType)) {
            var selectedMoney = $channelContent.find('.fixed-amount.cur');
            if (selectedMoney.length <= 0) {
                $.YF.alert_warning('请选择金额');
                return;
            }

            qrCodeId = selectedMoney.attr('data-qrCodeId');
            amount = selectedMoney.attr('data-amount');
            if (!$.YF.isDigits(amount)) {
                $.YF.alert_warning('金额请选择整数');
                return;
            }
        }
        else {
            var $amount = $channelContent.find('input[name=inputAmount]');
            amount = $amount.val();
            if ($.YF.isEmpty(amount)) {
                $amount.focus();
                $.YF.alert_tooltip('请输入金额', $amount);
                return;
            }
            amount = parseInt(amount);

            var minUnitRecharge = selectedChannel.minUnitRecharge;
            var maxUnitRecharge = selectedChannel.maxUnitRecharge;

            // 检查输入值是否在范围值内
            if (amount < minUnitRecharge || amount > maxUnitRecharge) {
                $.YF.alert_tooltip('充值范围：' + minUnitRecharge + '-' + maxUnitRecharge, $amount);
                return;
            }
        }

        if (!amount) {
            $.YF.alert_warning('请输入或选择金额');
            return;
        }
        if (!$.YF.isDigits(amount)) {
            $.YF.alert_warning('金额请输入整数');
            return;
        }

        $.YF.showLoadingMask();

        var rechargeData = {pid: pid, bankId: bankId, amount: amount, bankco: bankco, qrCodeId: qrCodeId,os:"browser"};
        if (channelCode == 'bankTransfer') {
           $.ajax({
               url: '/LoadBankTransfers',
               data: rechargeData,
               success: function(res) {
               	console.log(res);
                   if (res.error === 0) {
                       var html = tpl('#bankTransfer_form_tpl', {data:res.data,amount:amount})
                	 $channelContent.html(html);
                   }
               },
               complete: function() {
                   $.YF.hideLoadingMask();
               }
           })
        }
        else {
            layer.closeAll('tips');

            $.ajax({
                url: '/RechargeAdd',
                data: rechargeData,
                success: function(res) {
                    if (res.error === 0) {

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
                        showChannel(selectedChannel);
                    }
                    else {
                        $.YF.alert_warning(res.message);
                    }
                },
                complete: function() {
                    $.YF.hideLoadingMask();
                }
            })
        }
    });

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

    // 初始化
    init();
})