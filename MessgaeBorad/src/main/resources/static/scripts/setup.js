//Setup flex slider
$(window).load(function () {
    $('.gf-slider').flexslider();

   // loadWorks();
});
//Setup Page
$(document).ready(function () {
    //Initialize PrettyPhoto here
    $("a[rel^='prettyPhoto']").prettyPhoto({ animation_speed: 'normal', theme: 'facebook', slideshow: 3000, autoplay_slideshow: false, social_tools: false 	});
	
	//Remove this line if you want to naviagate to url on each client box click
	$('#clients.grid a').click(function(){return false;});
    //Initialize jQuery knob here
    $(".knob").knob();
    //Initialie tipsy here
    $('#fb').tipsy({ gravity: 'n', fade: true });
    $('#WeChat').tipsy({ gravity: 'n', fade: true });
    $('#ld').tipsy({ gravity: 'n', fade: true });
    /* smooth-scroll */
    $("ul#navigation a").smoothScroll({
        afterScroll: function () {
            $('ul#navigation a.active').removeClass('active');
            $(this).addClass('active');
        }
    });

    //Scroll
    $('div.page').waypoint(function () {
        var id = $(this).attr('id');

        $('ul#navigation a.active').removeClass('active');
        $('ul#navigation a[href="#' + id + '"]').addClass('active');
    });

    /* fixes */
    $(window).scroll(function () {
        if ($(window).scrollTop() === 0) {
            $('ul#navigation a.active').removeClass('active');
            $('ul#navigation a[href="#home"]').addClass('active');
        } else if ($(window).height() + $(window).scrollTop() === $('#container').height()) {
            $('ul#navigation a.active').removeClass('active');
            $('ul#navigation a[href^="#"]:last').addClass('active');
        }
    });

    /* tab */
    // first selector
    $('.tab').each(function () {
        $(this).find('ul > li:first').addClass('active');
        $(this).find('div.tab_container > div:first').addClass('active');
    });

    /* toggles */
    $('.toggle h3').click(function () {
        $(this).parent().find('.toggle_data').slideToggle();
    });

    // click functions
    $('.tab > ul > li').click(function () {
        $(this).parent().find('li.active').removeClass('active');
        $(this).addClass('active');

        $(this).parent().parent().find('div.tab_container > div.active').removeClass('active').slideUp();
        $(this).parent().parent().find('div.tab_container > div#' + $(this).attr('id')).slideDown().addClass('active');

        return false;
    });

    var $container = $('div#works').isotope({
        itemSelector: 'img.work',
        layoutMode: 'fitRows'
    });

    // items filter
    $('#works_filter a').click(function () {
        var selector = $(this).attr('data-filter');
        $('div#works').isotope({
            filter: selector,
            itemSelector: 'img.work',
            layoutMode: 'fitRows'
        });

        $('#works_filter').find('a.selected').removeClass('selected');
        $(this).addClass('selected');

        return false;
    });

    //smooth scroll top
    $('.gotop').addClass('hidden');

    $("a.gotop").smoothScroll();

    $('#wrap').waypoint(function (event, direction) {
        $('.gotop').toggleClass('hidden', direction === "up");
    }, {
        offset: '-100%'
    });
    //bind send message here
    $('#submit').click(sendMessage);


});

/* Contact Form */
function checkEmail(email) {
    var check = /^[\w\.\+-]{1,}\@([\da-zA-Z-]{1,}\.){1,}[\da-zA-Z-]{2,6}$/;
    if (!check.test(email)) {
        return false;
    }
    return true;
}

function checkPhone(phonenumber) {
    var check = /^[1][2,3,4,5,6,7,8,9][0-9]{9}$/;
    if (!check.test(phonenumber)) {
        return false;
    }
    return true;
}

//===================================================以下可改=================================================
var prefix = "http://localhost:10001";

function sendMessage() {
    // receive the provided data
    var name = $("input#name").val();
    var email = $("input#email").val();
    var subject = $("input#subject").val();
    var msg = $("textarea#msg").val();
    var phonenumber =$("input#phonenumber").val();
    // check if all the fields are filled
    if (name == '' || name == '姓名*' || email == '' || email == '邮箱*'|| phonenumber == ''|| phonenumber == '手机号码*' || subject == '' || subject == '摘要*' || msg == '' || msg == '正文（200字以内）*') {
        $("div#msgs").html('<p class="warning">请完整的填写您的信息</p>');

        return false;
    }

    // verify the email address
    if (!checkEmail(email)) {
        $("div#msgs").html('<p class="warning">请填写正确的邮箱格式</p>');
        return false;
    }

    if (!checkPhone(phonenumber)) {
        $("div#msgs").html('<p class="warning">请填写正确的手机号码</p>');
        return false;
    }

    // make the AJAX request
    var dataString = $('#cform').serialize();
    $.ajax({
        type: "POST",
        url: prefix+'/message/insert',
        data: dataString,
        dataType: 'jsonp',
        success: function (restUtil) {
            if (restUtil.status == 20001) {
                var errors = '<ul><li>';
                if (data.message.fullname != '')
                    errors += data.message.fullname + '</li>';
                if (data.message.emailaddress != '')
                    errors += '<li>' + data.message.emailaddress + '</li>';
                if (data.message.phonenumber != '')
                    errors += '<li>' + data.message.phonenumber + '</li>';
                if (data.message.subject != '')
                    errors += '<li>' + data.message.subject + '</li>';
                if (data.message.message != '')
                    errors += '<li>' + data.message.message + '</li>';
                $("div#msgs").html('<p class="error" >无法完成您的请求。请参阅下面的错误!</p>' + errors);
            }
            else if (restUtil.status  == 20000) {
                // layer.load();
                // //此处演示关闭
                // setTimeout(function(){
                //     layer.closeAll('loading');
                // }, 2000);

                $("div#msgs").html('<p class="error" >留言已收到，我们会尽快回复您。</p>');
                $('#cform').empty();
                var str = '';
                str += '<input type="text" id="name" name="fullname" value="Full Name*" onFocus="if(this.value == \'Full Name*\') this.value = \'\'" onblur="if(this.value == \'\') this.value = \'Full Name*\'" />';
                str += '<input type="text" id="email" name="emailaddress" value="Email Address*" onFocus="if(this.value == \'Email Address*\') this.value = \'\'" onblur="if(this.value == \'\') this.value = \'Email Address*\'" />';
                str += '<input type="text" id="phonenumber" name="phonenumber" value="Phone Number*" onFocus="if(this.value == \'Phone Number*\') this.value = \'\'" onblur="if(this.value == \'\') this.value = \'Phone Number*\'" />';
                str += '<input type="text" id="subject" name="subject" value="Subject*" onFocus="if(this.value == \'Subject*\') this.value = \'\'" onblur="if(this.value == \'\') this.value = \'Subject*\'" />';
                str += '<textarea id="msg" name="message" onFocus="if(this.value == \'200字以内\') this.value = \'\'" onblur="if(this.value == \'\') this.value = \'200字以内\'"  MaxLength="400">200字以内</textarea>';
                str += '<button id="submit" class="button" onclick="sendup();"> 留言</button>';
                $('#cform').append(str);
            }

        },
        error: function (XMLHttpResponse, textStatus, errorThrown) {
            $("div#msgs").html('<p class="error">未知错误，请联系中边！</p>');
            console.log("1 异步调用返回失败,XMLHttpResponse.readyState:"+XMLHttpResponse.readyState);
            console.log("2 异步调用返回失败,XMLHttpResponse.status:"+XMLHttpResponse.status);
            console.log("3 异步调用返回失败,textStatus:"+textStatus);
            console.log("4 异步调用返回失败,errorThrown:"+errorThrown);

        }
    });

    return false;
}

function WeChat() {
    layer.open({
        type: 1,
        title: false,
        closeBtn: 0,
        area: '258px',
        skin: 'layui-layer-nobg', //没有背景色
        shadeClose: true,
        content: '<img src="images/byk/WeChat.jpg" alt="" />'
    });
}

function loadWorks() {
    $.ajax({
        cache : true,
        type : "get",
        url : prefix+"/displayWork/list",
        async : false,
        //data:{"limit":1,"offset":100},
       // dataType:"jsonp",
        success : function(restUtil) {
            if (restUtil.status == 20000) {
               var displayWorks = restUtil.data;
               $("#works").empty();
               for(var i = 0;i<displayWorks.length;i++){
                  var pageName = displayWorks[i].pageName;
                   var pageAddress = displayWorks[i].pageAddress;
                   var pageLink = displayWorks[i].pageLink;
                   var pageClass = displayWorks[i].pageClass;
                   var targetLink = displayWorks[i].targetLink;
                   var str = '';
                    str += '<a href="'+pageLink+'" target="'+targetLink+'">';
                   str += ' <img class="'+pageClass+'" src="http://localhost:8081/file/'+pageName+'" alt=""/> </a>';
                   $("#works").append(str);
               }
            }
        }
    });
}