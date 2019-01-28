$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $account;
    var $type;
    var $sTime;
    var $eTime;
    var $listTable;
    var $listPager;

    function init() {
        // 初始化内容
        initContentBox();
    }

    function initContentBox() {
        $contentBox.html(tpl('#report_bill_record_content_tpl', {}));

        $listTable = $contentBox.find('table tbody');
        $listPager = $contentBox.find('.paging');

        $account = $contentBox.find('select[name=account]');
        $type = $contentBox.find('select[name=type]');
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

        $.YF.initDateUI('#reportBillRecordSTime');
        $.YF.initDateUI('#reportBillRecordETime');

        $sTime.val(sTime);
        $eTime.val(eTime);

        // 初始化列表
        search(0);
    }

    // 初始化列表
    function search(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/UserBillSearch',
            data: {
                limit: 10,
                start: page * 10,
                sTime: $sTime.val(),
                eTime: $.YF.getNetDate($eTime.val()),
                account: $account.val(),
                type: $type.val()
            },
            success : function(res) {
                if (res.error === 0) {
                    $listTable.html(tpl('#report_bill_record_list_tpl', {rows: res.data}))

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

    $contentBox.on('click', '[data-command=search]', function(){
        search(0);
    });

    // 订单详情
    $contentBox.on('click', '[data-command="remarks"]', function(e){
        var remarks = $(e.target).closest('tr').attr('data-remarks');
        $.YF.alert_message('', remarks);
    });

    // 初始化
    init();
})