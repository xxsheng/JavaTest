/**
 * Created by guowx on 2017/10/28.
 */
$(function(){

    /*财务中心*/
    $(".fina_tab span").click(function(){
        $(this).addClass("cur").siblings().removeClass("cur");
        $(".text_cc input").val($(this).text()+".00");
    })
    $(".finance_tab span").click(function(){
        $(this).addClass("cur").siblings().removeClass("cur");
    })
    $(".finance_box .finance_text .text_cc .bank i").click(function(){
        $(this).addClass("cur").siblings().removeClass("cur");
    })
    /*代理中心*/

    $(".list_box .figure .figure_top i").click(function(){
        $(this).addClass("cur").siblings().removeClass("cur");
    })
    $(".finance_box .finance_text .text_cc .player i").click(function(){
        $(this).addClass("cur").siblings().removeClass("cur");
    })
    $(".finance_tab span").click(function(){
        $(this).addClass("cur").siblings().removeClass("cur");
        $(".finance_box [name=list]").eq($(this).index()).show().siblings("[name=list]").hide();
    })

    $(".chek").click(function(){
        $(this).toggleClass("cheked");
    })

    $(".security ul li .security_r span.t_1").click(function(){
        $(this).toggleClass("t_2");
    })
    /* 彩票投注*/
    $("#qk").click(function(){
        $(".pick_box .pick_cen .betting-unit dl dd .ks").toggle();
    })

    // $(".pick_box .pick_cen .betting-unit dl dd em").click(function(){
    //     $(this).addClass("cur").siblings().removeClass("cur");
    // })
    // $(".pick_box .pick_cen .betting-unit dl dd span").click(function(){
    //     $(this).addClass("active").siblings().removeClass("active");
    // })
    // $(".pick_box .pick_cen .elec .elec_l dl dd span").click(function(){
    //     $(this).addClass("cur").siblings().removeClass("cur");
    //     $(".pick_cen [name=list]").eq($(this).index()).show().siblings('[name=list]').hide();
    // })
    //
    
    // 加减按钮
    // $("#jia").click(function(){
    //     var v=parseInt($("#he").text());
    //     v++;
    //     $("#he").text(v);
    // })
    // $("#jian").click(function(){
    //     var v=parseInt($("#he").text());
    //     if(v>1){
    //         v--;
    //         $("#he").text(v);
    //     }
    // })
    
    // 元角分按钮
    // $(".pick_box .tations .tat_2 .yuan").click(function(){
    //     $(".pick_box .tations .tat_2 .yuan_cc").slideToggle();
    // })
    // $(".pick_box .tations .tat_2 .yuan_cc span").click(function(){
    //     $(this).parent().slideUp();
    //     $(".pick_box .tations .tat_2 .yuan span").text($(this).text());
    // })
    
    
    
    // $(".pick_box .tations .tat_3").click(function(){
    //     $(".pick_box .tations .tat_3 .tt").slideToggle();
    // })
    // $(".pick_box .pick_tab span").click(function(){
    //     $(this).addClass("cur").siblings().removeClass("cur");
    // })
    //
    // $(".bet_tab span").click(function(){
    //     $(this).addClass("cur").siblings().removeClass("cur");
    //     $(".bet_list .tab-list").eq($(this).index()).show().siblings(".tab-list").hide();
    // })


    $("#all").click(function(){
        if($(this).hasClass("o")){
            $(".tab-list table tr:gt(0) .chek").removeClass("cheked");
            $(this).removeClass("o");
        }else{
            $(".tab-list table tr:gt(0) .chek").addClass("cheked");
            $(this).addClass("o");
        }


    })


})