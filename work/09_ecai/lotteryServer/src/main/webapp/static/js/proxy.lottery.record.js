$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $userLevels = $("#userLevels", $content);
    var $lotteryId;
    var $status;
    var $username;
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
                $contentBox.html(tpl('#proxy_lottery_record_content_tpl', {lotteries: res.data}));

                $listTable = $contentBox.find('table tbody');
                $listPager = $contentBox.find('.paging');

                $lotteryId = $contentBox.find('select[name=lotteryId]');
                $status = $contentBox.find('select[name=status]');
                $username = $contentBox.find('input[name=username]');
                $sTime = $contentBox.find('input[name=sTime]');
                $eTime = $contentBox.find('input[name=eTime]');
                
                var username = $.YF.getUrlParam(window.location.search, 'username');
                if (username) {
                    $username.val(username);
                }

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

    // 初始化日期组件
    function initDate() {
        var sTime = $.YF.addDayForDate(new Date(), 0);
        var eTime = $.YF.addDayForDate(new Date(), 0);

        $.YF.initDateUI('#proxyLotteryRecordSTime');
        $.YF.initDateUI('#proxyLotteryRecordETime');

        $sTime.val(sTime);
        $eTime.val(eTime);

        // 初始化列表
        search(0);
    }

    // 初始化列表
    function search(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/ProxyOrderSearch',
            data: {
                limit: 10,
                start: page * 10,
                sTime: $sTime.val(),
                eTime: $.YF.getNetDate($eTime.val()),
                status: $status.val(),
                lotteryId: $lotteryId.val(),
                username: $username.val()
            },
            success : function(res) {
                if (res.error === 0) {
                    $listTable.html(tpl('#proxy_lottery_record_list_tpl', {rows: res.data}))

                    setUserLevels(res.userLevels);

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

    // 设置层级
    function setUserLevels(userLevels) {
        $userLevels.html('层级关系：');
        $.each(userLevels, function(index, val){
            $userLevels.append('<em data-command="showUserOrder" data-user="'+val+'"> '+val+'</em>');
            if (index < userLevels.length - 1) {
                $userLevels.append(' >');
            }
        });
    }

    // 搜索按钮
    $contentBox.on('click', '[data-command=search]', function(){
        search(0);
    });

    // 详情
    $contentBox.on('click', '[data-command="details"]', function(e){
        var id = $(e.target).closest('tr').attr('data-id');
        new LotteryOrderDetail(id, function(){
            search($('span.cur', $listPager).attr('data-num'))
        });
    })

    // 显示下级数据
    $content.on('click', '[data-command=showUserOrder]', function(e){
        var $target = $(e.target);
        var username = $target.attr('data-user');
        $username.val(username);
        search(0);
    });

    // 初始化
    init();
})