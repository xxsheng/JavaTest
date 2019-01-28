$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $proxySummary = $("#proxySummary", $content);
    var $chartDom;
    var $sTime;
    var $eTime;

    function init() {
        // 初始化内容
        initContentBox();
    }

    function initContentBox() {
        $contentBox.html(tpl('#proxy_index_content_tpl', {}));

        $chartDom = document.getElementById("chart");
        $sTime = $contentBox.find('input[name=sTime]');
        $eTime = $contentBox.find('input[name=eTime]');

        // 初始化日期组件
        initDate();
    }

    // 初始化日期组件
    function initDate() {
        $.YF.initDateUI('#proxyIndexSTime');
        $.YF.initDateUI('#proxyIndexETime');

        setDate(0); // 默认最近1天

        // 初始化图表数据
        searchTeamData();
    }

    var isLoadingData = false;
    function searchTeamData() {
        if (isLoadingData == true) {
            return;
        }
        isLoadingData = true;
        $.YF.showLoadingMask();

        $contentBox.find('[data-property=totalLotteryBillingOrder]').html('Loading...');
        $contentBox.find('[data-property=totalLotteryPrize]').html('Loading...');
        $contentBox.find('[data-property=totalLotteryProxyReturn]').html('Loading...');
        $contentBox.find('[data-property=totalRegister]').html('Loading...');
        $contentBox.find('[data-property=totalRecharge]').html('Loading...');
        $contentBox.find('[data-property=totalWithdraw]').html('Loading...');

        var dailyType;
        var type = $contentBox.find('.figure_top .cur').attr('data-type');;
        if ("totalLotteryBillingOrder" == type) {
            dailyType = 'lottery';
        }
        else if ("totalLotteryPrize" == type) {
            dailyType = 'lottery';
        }
        else if ("totalLotteryProxyReturn" == type) {
            dailyType = 'lottery';
        }
        else if ("totalRegister" == type) {
            dailyType = 'register';
        }
        else if ("totalRecharge" == type) {
            dailyType = 'main';
        }
        else if ("totalWithdraw" == type) {
            dailyType = 'main';
        }

        $.ajax({
            url : '/LoadProxyIndex',
            data: {
                sDate: $sTime.val(),
                eDate: $.YF.getNetDate($eTime.val()),
                dailyType: dailyType
            },
            success : function(res) {
                if (res.error === 0) {
                    // 顶部数据
                    $proxySummary.find('[data-property=totalUser]').html(res.data.totalUser);
                    $proxySummary.find('[data-property=onlineUser]').html(res.data.onlineUser);
                    var totalBalance = $.YF.formatMoney(res.data.totalBalance + res.data.lotteryBalance);
                    $proxySummary.find('[data-property=totalBalance]').html(totalBalance);
                    $proxySummary.find('[data-property=totalLotteryBalance]').html($.YF.formatMoney(res.data.lotteryBalance));

                    // 图表表头数据
                    $contentBox.find('[data-property=totalLotteryBillingOrder]').html($.YF.formatMoney(res.data.totalLotteryBillingOrder));
                    $contentBox.find('[data-property=totalLotteryPrize]').html($.YF.formatMoney(res.data.totalLotteryPrize));

                    var totalLotteryProxyReturn = $.YF.formatMoney(res.data.totalLotterySpendReturn + res.data.totalLotteryProxyReturn);
                    $contentBox.find('[data-property=totalLotteryProxyReturn]').html(totalLotteryProxyReturn);
                    $contentBox.find('[data-property=totalRegister]').html(res.data.totalRegister);
                    $contentBox.find('[data-property=totalRecharge]').html($.YF.formatMoney(res.data.totalRecharge));
                    $contentBox.find('[data-property=totalWithdraw]').html($.YF.formatMoney(res.data.totalWithdraw));

                    // 图表
                    initChart(res.data);
                }
            },
            complete: function(){
                isLoadingData = false;
                $.YF.hideLoadingMask();
            },
            error: function() {
                isLoadingData = false;
                $.YF.hideLoadingMask();
            }
        })
    }

    function initChart(data) {
        var formatter = ''; // 表头
        var dates = new Array(); // X数据
        var datas = new Array(); // Y数据
        var type = $contentBox.find('.figure_top .cur').attr('data-type');
        if ("totalLotteryBillingOrder" == type) {
            formatter = '投注';
            $.each(data.dailyData, function(index, item){
                dates.push(item.date);
                datas.push(Number(item.lotteryBillingOrder));
            });
        }
        else if ("totalLotteryPrize" == type) {
            formatter = '派奖';
            $.each(data.dailyData, function(index, item){
                dates.push(item.date);
                datas.push(Number(item.lotteryPrize));
            });
        }
        else if ("totalLotteryProxyReturn" == type) {
            formatter = '返点';
            $.each(data.dailyData, function(index, item){
                dates.push(item.date);
                var num = Number(item.lotterySpendReturn) + Number(item.lotteryProxyReturn);
                datas.push(num);
            });
        }
        else if ("totalRegister" == type) {
            formatter = '注册人数';
            $.each(data.dailyData, function(index, item){
                dates.push(item.date);
                datas.push(Number(item.register));
            });
        }
        else if ("totalRecharge" == type) {
            formatter = '充值';
            $.each(data.dailyData, function(index, item){
                dates.push(item.date);
                datas.push(Number(item.recharge));
            });
        }
        else if ("totalWithdraw" == type) {
            formatter = '取款';
            $.each(data.dailyData, function(index, item){
                dates.push(item.date);
                datas.push(Number(item.withdraw));
            });
        }

        var myChart = echarts.init($chartDom);

        var option = {
            color: ['#3396fb'],
            tooltip: {
                trigger: 'item',
                formatter: formatter + "：<br/>{b}<br/>{c}"
            },
            xAxis: {
                data: dates,
                axisTick: {
                    alignWithLabel: true
                },
                axisLine:{
                    lineStyle:{
                        color:'#999999',
                        width:2 //这里是为了突出显示加上的
                    }
                },
                axisLabel:{
                    textStyle: {
                        color:'#999999'
                    }
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            yAxis: {
                axisLine:{
                    lineStyle:{
                        color:'#999999',
                        width:2//这里是为了突出显示加上的
                    }
                },
                boundaryGap: false
            },
            series: [{
                name: formatter,
                type: 'line',
                data: datas
            }]
        };

        myChart.setOption(option, true);
    }

    // 搜索按钮
    $contentBox.on('click', '[data-command=search]', function(){
        searchTeamData();
    });

    // 快速选择日期
    $content.on('click', '[data-command=fastDate]', function(e){
        var val = $(this).attr('data-val');
        setDate(val);
    });

    // 图表选项点击事件
    $contentBox.on('click', '[data-command=show]', function(){
        var $this = $(this);
        if ($this.hasClass('cur')) {
            return;
        }

        $this.addClass('cur').siblings('i').removeClass('cur');
        searchTeamData();
    });
    
    function setDate(val) {
        var _day = parseInt(val);
        var sTime = $.YF.addDayForDate(new Date(), _day);
        var eTime = $.YF.addDayForDate(new Date(), 0);

        $sTime.val(sTime);
        $eTime.val(eTime);
    }

    // 初始化
    init();
})