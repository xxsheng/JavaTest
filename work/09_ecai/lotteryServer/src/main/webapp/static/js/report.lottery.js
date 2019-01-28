$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $userLevels = $("#userLevels", $content);
    var $sTime;
    var $eTime;
    var $username;
    var $listTable;
    var $listPager;

    function init() {
        // 初始化内容
        initContentBox();
    }

    function initContentBox() {
        $contentBox.html(tpl('#report_lottery_content_tpl', {}));

        $listTable = $contentBox.find('table tbody');
        $listPager = $contentBox.find('.paging');

        $username = $contentBox.find('input[name=username]');
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

        $.YF.initDateUI('#reportLotterySTime');
        $.YF.initDateUI('#reportLotteryETime');

        $sTime.val(sTime);
        $eTime.val(eTime);

        // 初始化列表
        search(0);
    }

    // 初始化列表
    function search(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/UserLotteryReport',
            data: {
                limit: 10,
                start: page * 10,
                sTime: $sTime.val(),
                eTime: $.YF.getNetDate($eTime.val()),
                username: $username.val()
            },
            success : function(res) {
                if (res.error === 0) {
                    $listTable.html(tpl('#report_lottery_list_tpl', {rows: res.data}))

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

    function setUserLevels(userLevels) {
        $userLevels.html('层级关系：');
        $.each(userLevels, function(index, val){
            $userLevels.append('<em data-command="showLowers" data-user="'+val+'"> '+val+'</em>');
            if (index < userLevels.length - 1) {
                $userLevels.append(' >');
            }
        });
    }

    $contentBox.on('click', '[data-command=search]', function(){
        search(0);
    });

    $content.on('click', '[data-command=showLowers]', function(e){
        var $target = $(e.target);
        var username = $target.attr('data-user');
        $username.val(username);
        search(0);
    });

    // 初始化
    init();
})