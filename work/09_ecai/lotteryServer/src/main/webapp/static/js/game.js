$.holdReady(true); // 锁住所有ready方法，加载模板文件
var url = cdnDomain + '/static/template/game_template.html?v=' + cdnVersion;
$.ajax({
    type: "GET",
    url: url,
    cache: true,
    complete: function(res) {
        $("#game_template_tpl").html(res.responseText);
        $.holdReady(false);
    }
});

$(function(){
    var $doc = $(document);
    var $lotteryMenu = $("#game-menu", $doc);
    var $gameiframe = $("#iframe", $doc);

    function init() {
        // 初始化游戏导航
        initGames();

        $gameiframe.height($(window).height());
        $(window).resize(function(){$gameiframe.height($(window).height())});
    }

    // 初始化彩票导航
    function initGames() {
        // // 获取所有游戏平台和
        // $.ajax({
        //     url: '/GetLottery',
        //     success: function(res) {
        //         var sscs = new Array();
        //         var x5s = new Array();
        //         var k3s = new Array();
        //         var pk10s = new Array();
        //         var sds = new Array();
        //         var kl8s = new Array();
        //
        //         $.each(res.data, function(index, lottery){
        //             if (lottery.type == 1 || lottery.type == 7) {
        //                 sscs.push(lottery);
        //             }
        //             else if (lottery.type == 2) {
        //                 x5s.push(lottery);
        //             }
        //             else if (lottery.type == 3) {
        //                 k3s.push(lottery);
        //             }
        //             else if (lottery.type == 6) {
        //                 pk10s.push(lottery);
        //             }
        //             else if (lottery.type == 4) {
        //                 sds.push(lottery);
        //             }
        //             else if (lottery.type == 5) {
        //                 kl8s.push(lottery);
        //             }
        //         })
        //
        //         $lotteryMenu.html(tpl('#lottery_menu_tpl', {sscs: sscs, x5s: x5s, k3s: k3s, pk10s: pk10s, sds: sds, kl8s: kl8s, cdnDomain: cdnDomain, cdnVersion: cdnVersion}));
        //
        //         // 类型点击
        //         $lotteryMenu.on('click', '.menu dt', function(){
        //             if($(this).hasClass("op")){
        //                 $(".menu dd").slideUp();
        //                 $(".menu dt em i").html("&#xe515;");
        //                 $(".menu dt").removeClass("op");
        //                 $(this).removeClass("op");
        //                 $(this).next("dd").slideUp();
        //                 $(this).find("em i").html("&#xe515;");
        //             }else{
        //                 $(".menu dd").slideUp();
        //                 $(".menu dt em i").html("&#xe515;");
        //                 $(".menu dt").removeClass("op");
        //                 $(this).addClass("op");
        //                 $(this).next("dd").slideDown();
        //                 $(this).find("em i").html("&#xe503;");
        //             }
        //         }).eq(1).click();
        //
        //         // 彩种点击
        //         $lotteryMenu.on('click', '.menu dd a', function(){
        //             $lotteryMenu.find('.menu dd a').removeClass('current');
        //             $(this).addClass("current");
        //         })
        //        
        //         // 滚动条
        //         $lotteryMenu.find('.menu-list').mCustomScrollbar({
        //             autoHideScrollbar: true,
        //             scrollButtons:{
        //                 enable: true
        //             },
        //             theme:"dark-thin",
        //             advanced: {
        //                 updateOnContentResize: true
        //             }
        //         });
        //     }
        // });
    }

    // 初始化
    init();
})