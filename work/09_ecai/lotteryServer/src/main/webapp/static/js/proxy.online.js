$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $listTable;
    var $listPager;

    function init() {
        // 初始化内容
        initContentBox();
    }

    function initContentBox() {
        $contentBox.html(tpl('#proxy_online_content_tpl', {}));

        $listTable = $contentBox.find('table tbody');
        $listPager = $contentBox.find('.paging');

        // 分页
        $listPager.off('click', '.pager');
        $listPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            search($target.attr('data-num'))
        })

        search(0);
    }

    // 初始化列表
    function search(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/ProxyOnlineList',
            data: {
                limit: 10,
                start: page * 10
            },
            success : function(res) {
                if (res.error === 0) {
                    $listTable.html(tpl('#proxy_online_list_tpl', {rows: res.data}))

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