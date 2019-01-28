$(function(){
    var $doc = $(document);
    var $managerMenu = $("#manager-menu", $doc);
    var $proxyMenu = $("#proxy-menu", $managerMenu);
    var $manageriframe = $("#iframe", $doc);

    function init() {
        // 初始化导航
        initNavigation();

        $manageriframe.height($(window).height());
        $(window).resize(function(){$manageriframe.height($(window).height())});
    }

    // 初始化导航
    function initNavigation() {
        var link = $.YF.getUrlParam(window.location.search, 'link');
        var defaultOpenType = '';
        var defaultOpenLink = '';
        if (link) {
            defaultOpenLink = link.split('?')[0];
            defaultOpenType = defaultOpenLink.split('-')[0];
        }
        if (defaultOpenLink && defaultOpenType) {
            $('.menu[data-type='+defaultOpenType+'] dt', $managerMenu).addClass("op");
            $('.menu[data-type='+defaultOpenType+'] dd', $managerMenu).show();
            $('.menu[data-type='+defaultOpenType+'] em i', $managerMenu).html("&#xe503;");

            $('.menu[data-type='+defaultOpenType+'] dd a[href='+defaultOpenLink+']').addClass("current");
        }

        // 类型点击
        $managerMenu.off('click', '.menu dt');
        $managerMenu.on('click', '.menu dt', function(){
            if($(this).hasClass("op")){
                $(".menu dd").slideUp();
                $(".menu dt em i").html("&#xe515;");
                $(".menu dt").removeClass("op");
                $(this).removeClass("op");
                $(this).next("dd").slideUp();
                $(this).find("em i").html("&#xe515;");
            }else{
                $(".menu dd").slideUp();
                $(".menu dt em i").html("&#xe515;");
                $(".menu dt").removeClass("op");
                $(this).addClass("op");
                $(this).next("dd").slideDown();
                $(this).find("em i").html("&#xe503;");
            }
        });

        // 点击
        $managerMenu.off('click', '.menu dd a');
        $managerMenu.on('click', '.menu dd a', function(){
            $managerMenu.find('.menu dd a').removeClass('current');
            $(this).addClass("current");
        })

        // 滚动条
        $managerMenu.find('.menu-list').mCustomScrollbar({
            scrollInertia: 70,
            autoHideScrollbar: true,
            theme:"dark-thin",
            advanced: {
                updateOnContentResize: true
            }
        });
        
        $.ajax({
            url: '/GetUserBase',
            success: function(res) {
                if (res.error == 0) {
                    if ($.YF.isProxy(res.data.type)) {
                        $proxyMenu.show();
                    }
                    else {
                        $proxyMenu.remove();
                    }
                }
            }
        });
    }

    // 初始化
    init();
})