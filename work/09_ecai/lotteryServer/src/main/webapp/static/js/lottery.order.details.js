// 查看会员定单详情
var LotteryOrderDetail = function (id, callback) {
    this.orderId = id
    this.popup = this.open()
    this.callback = callback || $.noop
}
LotteryOrderDetail.prototype = {
    ajax: function (success) {
        var me = this
        $.YF.showLoadingMask();
        me.xhr = $.ajax({
            url: '/ProxyOrderDetails',
            data: {
                id: me.orderId
            },
            success: success,
            complete: function () {
                $.YF.hideLoadingMask();
            }
        })
    },
    open: function () {
        swal.close();
        
        var me = this

        var popup;
        me.ajax(function(res) {
            var html = tpl('#lottery_order_details_tpl', res.data);

            var $dom;
            swal({
                title: '订单详情',
                customClass:'popup',
                html: html,
                width: 680,
                showCloseButton: true,
                showCancelButton: false,
                showConfirmButton: false,
                buttonsStyling: false,
                confirmButtonClass: 'popup-btn-confirm',
                cancelButtonClass: 'popup-btn-cancel',
                allowEnterKey: false,
                onBeforeOpen: function (dom) {
                    $dom = $(dom);

                    $dom.off('click', '[data-command=cancel-general]');
                    $dom.on('click', '[data-command=cancel-general]', function(e) {
                        me.submitHandler('确认要撤销该订单吗？', {id : me.orderId, type : 'general'})
                    })
                    // 撤销追号
                    $dom.off('click', '[data-command=cancel-chase]');
                    $dom.on('click', '[data-command=cancel-chase]', function(e) {
                        me.submitHandler('确认要撤销该追号订单？', {chaseBillno : $(e.target).attr('data-no'), type : 'chase'})
                    })

                    $dom.off('click', '[data-command=close]');
                    $dom.on('click', '[data-command=close]', function(e) {
                        swal.close();
                    })
                    $(".codes", $dom).mCustomScrollbar({
                        scrollInertia: 70,
                        autoHideScrollbar: true,
                        theme:"dark-thin",
                        advanced: {
                            updateOnContentResize: true
                        }
                    });
                }
            }).then(function() {
            }, function() {
            })
        });
        return popup;
    },
    submitHandler: function(msg, data) {
        var me = this;

        $.YF.alert_question(msg, function(resolve, reject){
            $.ajax({
                url: '/UserBetsCancel',
                data: data,
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
                me.callback()
            });
        })
    }
}



