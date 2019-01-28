$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $listTable;
    var $listPager;

    function init() {
        // 初始化内容
        initContentBox();

        // 初始化最近登录列表
        refreshLoginList(0);
    }

    function initContentBox() {
        $contentBox.html(tpl('#account_login_content_tpl', {}));
        $listTable = $contentBox.find('table tbody');
        $listPager = $contentBox.find('.paging');

        // 分页
        $listPager.off('click', '.pager');
        $listPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            refreshLoginList($target.attr('data-num'))
        })
    }

    // 初始化最近登录列表
    function refreshLoginList(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/GetUserLoginLogList',
            data: {
                limit: 10,
                start: page * 10
            },
            success : function(res) {
                if (res.error === 0) {
                    $listTable.html(tpl('#account_login_list_tpl', {rows: res.data}))

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

    // 初始化
    init();
})