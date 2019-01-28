$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $sTime;
    var $eTime;
    var $listTable;
    var $listPager;

    function init() {
        // 初始化内容
        initContentBox();
    }

    function initContentBox() {
        $contentBox.html(tpl('#funds_transfer_record_content_tpl', {}));

        $listTable = $contentBox.find('table tbody');
        $listPager = $contentBox.find('.paging');

        $sTime = $contentBox.find('input[name=sTime]');
        $eTime = $contentBox.find('input[name=eTime]');

        // 分页
        $listPager.off('click', '.pager');
        $listPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            search($target.attr('data-num'))
        })

        // 初始化日期组件
        initDate();
    }

    function initDate() {
        var sTime = $.YF.addDayForDate(new Date(), 0);
        var eTime = $.YF.addDayForDate(new Date(), 0);

        $.YF.initDateUI('#transferRecordSTime');
        $.YF.initDateUI('#transferRecordETime');

        $sTime.val(sTime);
        $eTime.val(eTime);

        // 初始化列表
        search(0);
    }

    // 初始化列表
    function search(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/UserTransfersSearch',
            data: {
                limit: 10,
                start: page * 10,
                sTime: $sTime.val(),
                eTime: $.YF.getNetDate($eTime.val()),
            },
            success : function(res) {
                if (res.error === 0) {
                    $listTable.html(tpl('#funds_transfer_record_list_tpl', {rows: res.data}))

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

    $contentBox.off('click', '[data-command=search]');
    $contentBox.on('click', '[data-command=search]', function(){
        search(0);
    });

    // 初始化
    init();
})