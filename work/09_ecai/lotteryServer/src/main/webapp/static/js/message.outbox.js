$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $listTable;
    var $listPager;
    var cacheData = [];

    function init() {
        // 初始化内容
        initContentBox();

        // 初始化列表
        search(0);
    }

    function initContentBox() {
        $contentBox.html(tpl('#message_outbox_content_tpl', {}));
        $listTable = $contentBox.find('table tbody');
        $listPager = $contentBox.find('.paging');

        // 分页
        $listPager.off('click', '.pager');
        $listPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            search($target.attr('data-num'))
        })
    }

    // 初始化列表
    function search(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/UserMessageOutbox',
            data: {
                limit: 10,
                start: page * 10
            },
            success : function(res) {
                if (res.error === 0) {
                    cacheData = res.data;
                    
                    $listTable.html(tpl('#message_list_tpl', {rows: res.data, type: 'outbox'}))

                    $listPager.html(tpl('#pager_tpl', {
                        index: Number(page),
                        pageSize: 10,
                        total: res.totalCount,
                        totalPage: Math.ceil(res.totalCount / 10)
                    }))

                    $contentBox.find('[data-command=deleteSelected]').addClass('disabled');
                    $('.chek', $contentBox).removeClass('cheked');
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function(){
                $.YF.hideLoadingMask();
            }
        })
    }

    // 选中全部
    $contentBox.on('click', '[data-command=selectAll]', function(e){
        var $target = $(e.target);
        if ($target.hasClass('cheked')) {
            $('.chek', $contentBox).removeClass('cheked');
            $contentBox.find('[data-command=deleteSelected]').addClass('disabled');
        }
        else {
            $('.chek', $contentBox).addClass('cheked');

            var selectedList = $listTable.find('.cheked');
            if (selectedList.length > 0) {
                $contentBox.find('[data-command=deleteSelected]').removeClass('disabled');
            }
        }
    });

    // 选中单个
    $contentBox.on('click', '[data-command=selectSingle]', function(e){
        var $target = $(e.target);
        if ($target.hasClass('cheked')) {
            $target.removeClass('cheked');
        }
        else {
            $target.addClass('cheked');
        }

        var $singles = $('.chek', $listTable);
        var $checkeds = $('.chek.cheked', $listTable);

        // 全部选中
        if ($checkeds.length === $singles.length) {
            $contentBox.find('[data-command=selectAll]').addClass('cheked');
        }
        else {
            $contentBox.find('[data-command=selectAll]').removeClass('cheked');
        }

        if ($checkeds.length > 0) {
            $contentBox.find('[data-command=deleteSelected]').removeClass('disabled');
        }
        else {
            $contentBox.find('[data-command=deleteSelected]').addClass('disabled');
        }
    });

    // 删除按钮
    $contentBox.on('click', '[data-command=deleteSelected]', function(e){
        var $target = $(e.target);
        if ($target.hasClass('disabled')) {
            return;
        }

        var selectedList = $listTable.find('.cheked');
        if (selectedList.length <= 0) {
            $('.chek', $contentBox).removeClass('cheked');
            $contentBox.find('[data-command=deleteAll]').addClass('disabled');
            $.YF.alert_warning('请选择要删除的数据！');
            return;
        }


        var ids = [];
        $(selectedList).each(function() {
            ids.push($(this).attr('data-id'))
        })

        deleteMSG(ids.join(), '确认要删除所选消息吗？')
    });

    // 单条记录的删除
    $contentBox.on('click', '[data-command="delete"]', function(e){
        deleteMSG($(e.target).closest('tr').attr('data-id'), '确认要删除该消息吗？')
    });

    // 详情
    $contentBox.on('click', '[data-command="details"]', reviewMessageDetails)

    // 弹出消息详情
    function reviewMessageDetails(e) {
        var $target = $(e.target)
        var index = $target.attr('data-index')
        var data = cacheData[index];
        var html = tpl('#message_detail_tpl', {
            author: data.fromUser,
            title: data.bean.subject,
            content: data.bean.content,
            date: data.bean.time
        });

        swal({
            title: '查看消息',
            customClass:'popup',
            html: html,
            width: 460,
            showCloseButton: true,
            showCancelButton: true,
            showConfirmButton: false,
            confirmButtonText: '确认',
            cancelButtonText: '关闭',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel',
            allowEnterKey: false,
            onBeforeOpen: function (dom) {
                var $dom = $(dom);

                $(".content", $dom).mCustomScrollbar({
                    scrollInertia: 70,
                    autoHideScrollbar: true,
                    theme:"dark-thin",
                    advanced: {
                        updateOnContentResize: true
                    }
                });
            },
        }).then(function() {
        }, function() {
        })
    }

    // 删除确认
    function deleteMSG(ids, msg) {
        $.YF.alert_question(msg, function(resolve, reject){
            $.ajax({
                url: '/DelUserMessage',
                data: {type: "outbox", ids: ids},
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
            $.YF.alert_success('消息删除成功！', function(){
                setTimeout(function(){
                    var page = $listPager.find('.cur').attr('data-num');
                    search(page);
                }, 100);
            });
        })
    }

    // 初始化
    init();
})