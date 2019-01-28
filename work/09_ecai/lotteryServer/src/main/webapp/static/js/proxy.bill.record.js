$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $userLevels = $("#userLevels", $content);
    var $account;
    var $type;
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
        $contentBox.html(tpl('#proxy_bill_record_content_tpl', {}));

        $listTable = $contentBox.find('table tbody');
        $listPager = $contentBox.find('.paging');

        $account = $contentBox.find('select[name=account]');
        $type = $contentBox.find('select[name=type]');
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

    // 初始化日期组件
    function initDate() {
        var sTime = $.YF.addDayForDate(new Date(), 0);
        var eTime = $.YF.addDayForDate(new Date(), 0);

        $.YF.initDateUI('#proxyBillRecordSTime');
        $.YF.initDateUI('#proxyBillRecordETime');

        $sTime.val(sTime);
        $eTime.val(eTime);

        // 初始化列表
        search(0);
    }

    // 初始化列表
    function search(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/ProxyBillSearch',
            data: {
                limit: 10,
                start: page * 10,
                sTime: $sTime.val(),
                eTime: $.YF.getNetDate($eTime.val()),
                account: $account.val(),
                type: $type.val(),
                username: $username.val()
            },
            success : function(res) {
                if (res.error === 0) {
                    $listTable.html(tpl('#proxy_bill_record_list_tpl', {rows: res.data}))

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
            $userLevels.append('<em data-command="showUserData" data-user="'+val+'"> '+val+'</em>');
            if (index < userLevels.length - 1) {
                $userLevels.append(' >');
            }
        });
    }

    // 搜索按钮
    $contentBox.on('click', '[data-command=search]', function(){
        search(0);
    });

    // 显示用户数据
    $content.on('click', '[data-command=showUserData]', function(e){
        var $target = $(e.target);
        var username = $target.attr('data-user');
        $username.val(username);
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