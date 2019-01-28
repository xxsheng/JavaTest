// 选择下级会员
var SingleChooseMember = function (callback, url) {
    this.url = url
    this.callback = callback
    this.$dom = null
    this.checked = []
    this.open()
    this.getData = callback || $.noop
}
SingleChooseMember.prototype = {
    ajax: function (success) {
        $.ajax({
            url: this.url,
            success: success,
            complete: function () {
            }
        })
    },
    open: function () {
        var me = this

        me.ajax(function(res) {
            if (res.error !== 0) {
                $.YF.alert_warning(res.message);
                return
            }
            var html = tpl('#single_choose_member_content_tpl', res);

            swal({
                title: '会员选择',
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
                    me.$dom = $(dom);
                    
                    var $input = me.$dom.find('input[name=username]');

                    var str = JSON.stringify(res.list)
                    me.$dom.off('click', '.chek');
                    me.$dom.on('click', '.chek', function(e){
                        me.checkedSingle(me.$dom, $(e.target));
                    })
                    me.$dom.off('click', '[data-command=search]');
                    me.$dom.on('click', '[data-command=search]', function(e) {
                        me.searchMember(me.$dom, $input, str)
                    })

                    // 滚动条
                    me.$dom.find('#scroll_container').mCustomScrollbar({
                        scrollInertia: 70,
                        autoHideScrollbar: true,
                        theme:"dark-thin",
                        advanced: {
                            updateOnContentResize: true
                        }
                    });
                },
                onOpen: function(){
                },
                showLoaderOnConfirm: true,
                preConfirm: function(){
                    return new Promise(function (resolve, reject) {
                        me.submit(me.$dom, function(){
                            resolve();
                        }, function(msg){
                            reject(msg);
                        });
                    })
                }
            }).then(function() {
                $('.chek.cheked', me.$dom).each(function() {
                    me.checked.push($(this).next().html())
                })
                me.getData(me.checked.join())
            }, function() {
            })
        })
    },
    checkedSingle: function($dom, $target) {
        if ($target.hasClass('cheked')) {
            $dom.find('.cheked').removeClass('cheked');
        }
        else {
            $dom.find('.cheked').removeClass('cheked');
            $target.addClass('cheked');
        }
    },
    searchMember: function($dom, $input, str) {
        var data = []
        str.replace(eval('/(\{.*?username\"\:\"' + $input.val() + '.*?\})/ig'), function($0, $1, $2) {
            data.push(eval('(' + $0 + ')'))
        })
        $('#scroll_container', $dom).html(tpl('#choose_member_list_tpl', {list: data}))
    },
    submit: function($dom, resolve, reject) {
        var checked = $dom.find('.cheked');
        if (checked.length <= 0) {
            reject('请选择会员');
        }
        else if(checked.length > 1){
            reject('只能选择一个会员');
        }
        else {
            resolve();
        }
    }
}



