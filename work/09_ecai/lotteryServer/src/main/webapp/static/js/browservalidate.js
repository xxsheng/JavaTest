function validBrowser() {
    var u_agent = navigator.userAgent;
    var browser_name = "unKnow browser";
    if (u_agent.indexOf("Firefox") > -1) {
        browser_name = "Firefox"
    } else {
        if (u_agent.indexOf("Chrome") > -1) {
            browser_name = "Chrome"
        } else {
            if (u_agent.indexOf("Trident") > -1) {
                browser_name = "IE11"
            } else {
                if (u_agent.indexOf("Mobile") > -1) {
                    browser_name = "Mobile"
                }
                else {
                    if (u_agent.indexOf("Safari") > -1) {
                        browser_name = "Safari"
                    }
                    else {
                        if (u_agent.indexOf("MSIE") > -1) {
                            browser_name = "IE(8-10)"
                        } else {
                            if (u_agent.indexOf("MSIE") > -1) {
                                browser_name = "IE(6-7)"
                            } else {
                                if (u_agent.indexOf("Opera") > -1) {
                                    browser_name = "Opera"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (browser_name != "IE11" && browser_name != "Chrome" && browser_name != "Firefox" && browser_name != "Mobile" && browser_name != "Safari") {
        swal({
            title: '浏览器版本过低',
            text: '您当前的浏览器版本过低，为了您能更好的体验我们的平台，强烈推荐谷歌浏览器版本为：59.0或IE11浏览器。<br/><br/>是否需要自动给您下载谷歌浏览器？单击【确认】按钮则自动下载！',
            width: 460,
            type: 'warning',
            confirmButtonText:'确认',
            showCancelButton:true,
            cancelButtonText:'关闭',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel'
        }).then(function(isConfirm) {
            if (isConfirm === true) {
                window.open("http://sw.bos.baidu.com/sw-search-sp/software/14aca1f5063ab/ChromeStandalone_59.0.3071.115_Setup.exe", "_bank")
    
            } else if (isConfirm === false) {
            } else {
            }
        });
    }
}
setTimeout(function(){
    validBrowser();
}, 2000);