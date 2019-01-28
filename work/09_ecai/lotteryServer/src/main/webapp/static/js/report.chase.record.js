$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $lotteryId;
    var $status;
    var $expect;
    var $sTime;
    var $eTime;
    var $listTable;
    var $listPager;

    function init() {
        // 初始化内容
        initContentBox();
    }

    function initContentBox() {
        $.ajax({
            url: '/GetLottery',
            success: function(res) {
                $contentBox.html(tpl('#report_chase_record_content_tpl', {lotteries: res.data}));

                $listTable = $contentBox.find('table tbody');
                $listPager = $contentBox.find('.paging');

                $lotteryId = $contentBox.find('select[name=lotteryId]');
                $status = $contentBox.find('select[name=status]');
                $expect = $contentBox.find('input[name=expect]');
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
        })
    }

    function initDate() {
        var sTime = $.YF.addDayForDate(new Date(), 0);
        var eTime = $.YF.addDayForDate(new Date(), 0);

        $.YF.initDateUI('#reportChaseRecordSTime');
        $.YF.initDateUI('#reportChaseRecordETime');

        $sTime.val(sTime);
        $eTime.val(eTime);

        // 初始化列表
        search(0);
    }

    // 初始化列表
    function search(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/UserBetsChaseSearch',
            data: {
                limit: 10,
                start: page * 10,
                sTime: $sTime.val(),
                eTime: $.YF.getNetDate($eTime.val()),
                expect: $expect.val(),
                status: $status.val(),
                lotteryId: $lotteryId.val()
            },
            success : function(res) {
                if (res.error === 0) {
                    $listTable.html(tpl('#report_chase_record_list_tpl', {rows: res.data}))

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
    $contentBox.on('click', '[data-command="details"]', function(e){
        var id = $(e.target).closest('tr').attr('data-id');
        new LotteryOrderDetail(id, function(){
            search($('span.cur', $listPager).attr('data-num'))
        });
    });

    // 复投
    $contentBox.on('click', '[data-command="rebet"]', function(e){
        var id = $(e.target).closest('tr').attr('data-id');
        $.YF.alert_question('确认要重复投注该订单吗？', function(resolve, reject){
            $.ajax({
                url: '/UserBetsReBet',
                data: {id : id},
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
            $.YF.alert_success('重复投注成功！', function(){
                search($('span.cur', $listPager).attr('data-num'))
            });
        })
    });

    // 初始化
    init();
})