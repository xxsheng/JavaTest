$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentTab = $("#contentTab", $content);
    var $contentBox = $("#contentBox", $content);
    var $userLevels = $("#userLevels", $contentTab);

    /**********契约分红账单**************/
    var $proxyDividendBillScope;
    var $proxyDividendBillStatus;
    var $proxyDividendBillUsername;
    var $proxyDividendBillSTime;
    var $proxyDividendBillETime;
    var $proxyDividendBillListTable;
    var $proxyDividendBillListPager;
    /**********契约分红账单**************/

    /**********契约分红管理**************/
    var $proxyDividendManagerScope;
    var $proxyDividendManagerUsername;
    var $proxyDividendManagerListTable;
    var $proxyDividendManagerListPager;
    /**********契约分红管理**************/

    /**********发起契约分红**************/
    var $proxyDividendRequestUsername;
    var $proxyDividendRequestScale;
    var $proxyDividendRequestSlide;
//    var $proxyDividendRequestSliderBar;
    var $proxyDividendRequestMinValidUser;
    var $proxyDividendRequestMinValidUserDesc;
    var proxyDividendRequestDataMinScale = 0;
    var proxyDividendRequestDataMaxScale = 0;
    var proxyDividendRequestDataMinSales = 0;
    var proxyDividendRequestDataMaxSales = 0;
    var proxyDividendRequestDataMinLoss = 0;
    var proxyDividendRequestDataMaxLoss = 0;
    var proxyDividendRequestDataMinValidUser = 0;
    var proxyDividendRequestDataMaxValidUser = 0;
    var maxSignLevel = 1;
    var currentSignLevel = 1;
    /**********发起契约分红**************/

    // 设置层级
    function setUserLevels(userLevels, command) {
        $userLevels.html('层级关系：');
        $.each(userLevels, function(index, val){
            $userLevels.append('<em data-command="'+command+'" data-user="'+val+'"> '+val+'</em>');
            if (index < userLevels.length - 1) {
                $userLevels.append(' >');
            }
        });

        $userLevels.show();
    }

    function init() {
        // 初始化内容
        initContentBox();
    }

    function initContentBox() {
        layer.closeAll('tips');

        var type = $contentTab.find('span.cur').attr('data-type');

        $userLevels.hide();

        if (type == 'bill') {
            initProxyDividendBillContent();
        }
        else if (type == 'manager') {
            initProxyDividendManagerContent();
        }
        else if (type == 'request') {
            initProxyDividendRequestContent();
        }
    }

    /**********契约分红账单**************/
    function initProxyDividendBillContent() {
        $contentBox.html(tpl('#proxy_dividend_bill_content_tpl', {}));

        $proxyDividendBillScope = $contentBox.find('select[name=proxyDividendBillScope]');
        $proxyDividendBillStatus = $contentBox.find('select[name=proxyDividendBillStatus]');
        $proxyDividendBillUsername = $contentBox.find('input[name=proxyDividendBillUsername]');
        $proxyDividendBillSTime = $contentBox.find('input[name=proxyDividendBillSTime]');
        $proxyDividendBillETime = $contentBox.find('input[name=proxyDividendBillETime]');

        $proxyDividendBillListTable = $contentBox.find('table tbody');
        $proxyDividendBillListPager = $contentBox.find('.paging');

        // 分页
        $proxyDividendBillListPager.off('click', '.pager');
        $proxyDividendBillListPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            searchProxyDividendBill($target.attr('data-num'))
        })

        // 搜索按钮
        $contentBox.off('click', '[data-command=searchProxyDividendBill]');
        $contentBox.on('click', '[data-command=searchProxyDividendBill]', function(e){
            searchProxyDividendBill(0);
        });

        // 详情
        $contentBox.off('click', '[data-command=proxyDividendBillDetails]');
        $contentBox.on('click', '[data-command="proxyDividendBillDetails"]', function(e){
            var id = $(e.target).closest('tr').attr('data-id');
            showProxyDividendBillDetails(id);
        })

        // 领取
        $contentBox.off('click', '[data-command=proxyDividendBillCollect]');
        $contentBox.on('click', '[data-command="proxyDividendBillCollect"]', function(e){
            var $tr = $(e.target).closest('tr');
            var id = $tr.attr("data-id");
            var availableAmount = $tr.attr("data-availableAmount");
            var userAmount = $tr.attr("data-userAmount");
            var totalReceived = $tr.attr("data-totalReceived");
            showProxyDividendBillCollect(id, availableAmount, userAmount, totalReceived);
        })

        // 显示下级数据
        $content.off('click', '[data-command=showProxyDividendBillLowers]');
        $content.on('click', '[data-command=showProxyDividendBillLowers]', function(e){
            var $target = $(e.target);
            var username = $target.attr('data-user');
            $proxyDividendBillUsername.val(username);
            searchProxyDividendBill(0);
        });

        // 初始化日期组件
        initProxyDividendBillDate();
    }

    // 初始化日期组件
    function initProxyDividendBillDate() {
        var sTime = $.YF.addDayForDate(new Date(), -31);
        var eTime = $.YF.addDayForDate(new Date(), 0);

        $.YF.initDateUI('#proxyDividendBillSTime');
        $.YF.initDateUI('#proxyDividendBillETime');

        $proxyDividendBillSTime.val(sTime);
        $proxyDividendBillETime.val(eTime);

        // 初始化列表
        searchProxyDividendBill(0);
    }

    // 初始化列表
    function searchProxyDividendBill(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/DividendBillSearch',
            data: {
                start: page * 10,
                limit: 10,
                username: $proxyDividendBillUsername.val(),
                sTime: $proxyDividendBillSTime.val(),
                eTime: $.YF.getNetDate( $proxyDividendBillETime.val()),
                status: $proxyDividendBillStatus.val(),
                scope: $proxyDividendBillScope.val()
            },
            success : function(res) {
                if (res.error === 0) {
                    $proxyDividendBillListTable.html(tpl('#proxy_dividend_bill_list_tpl', {rows: res.data, username: res.username}))

                    setUserLevels(res.userLevels, 'showProxyDividendBillLowers');

                    $proxyDividendBillListPager.html(tpl('#pager_tpl', {
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

    function showProxyDividendBillCollect(id, availableAmount, userAmount, totalReceived) {
        var data = {availableAmount: parseFloat(availableAmount), userAmount: parseFloat(userAmount), totalReceived: parseFloat(totalReceived)};
        var html = tpl('#proxy_dividend_bill_collect_tpl', {data: data});

        var $dom;
        swal({
            title: '领取契约分红',
            customClass:'popup',
            html: html,
            width: 460,
            showCloseButton: true,
            showCancelButton: true,
            showConfirmButton: true,
            confirmButtonText: '领取',
            cancelButtonText: '关闭',
            buttonsStyling: false,
            confirmButtonClass: 'popup-btn-confirm',
            cancelButtonClass: 'popup-btn-cancel',
            allowEnterKey: false,
            onBeforeOpen: function (dom) {
                $dom = $(dom);
            },
            onOpen: function(){
            },
            showLoaderOnConfirm: true,
            preConfirm: function(){
                return new Promise(function (resolve, reject) {
                    submitProxyDividendBillCollect(id, function(){
                        resolve();
                    }, function(msg){
                        reject(msg);
                    });
                })
            }

        }).then(function() {
            $.YF.alert_success('契约分红领取成功！', function(){
                setTimeout(function(){
                    var page = $proxyDividendBillListPager.find('.cur').attr('data-num');
                    searchProxyDividendBill(page);
                }, 100);
            });
            $dom = null;
        }, function() {
            $dom = null;
        })
    }

    function submitProxyDividendBillCollect(id, resolve, reject) {
        $.ajax({
            url : '/DividendCollect',
            data: {
                id: id
            },
            success : function(res) {
                if (res.error === 0) {
                    resolve();
                }
                else {
                    reject(res.message);
                }
            },
            complete: function(){
            }
        })
    }

    function showProxyDividendBillDetails(id) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/DividendGet',
            data: {
                id: id
            },
            success : function(res) {
                if (res.error === 0) {
                    var html = tpl('#proxy_dividend_bill_details_tpl', {data: res.data});

                    swal({
                        title: '契约分红详情',
                        customClass:'popup',
                        html: html,
                        width: 560,
                        showCloseButton: true,
                        showCancelButton: true,
                        showConfirmButton: false,
                        confirmButtonText: '确认',
                        cancelButtonText: '关闭',
                        buttonsStyling: false,
                        confirmButtonClass: 'popup-btn-confirm',
                        cancelButtonClass: 'popup-btn-cancel',
                        allowEnterKey: false
                    }).then(function() {
                    }, function() {
                    })
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function(){
                $.YF.hideLoadingMask();
            }
        })
    }
    /**********契约分红账单**************/






    /**********契约分红管理**************/
    function initProxyDividendManagerContent() {
        $contentBox.html(tpl('#proxy_dividend_manager_content_tpl', {}));

        $proxyDividendManagerScope = $contentBox.find('select[name=proxyDividendManagerScope]');
        $proxyDividendManagerUsername = $contentBox.find('input[name=proxyDividendManagerUsername]');

        $proxyDividendManagerListTable = $contentBox.find('table tbody');
        $proxyDividendManagerListPager = $contentBox.find('.paging');

        // 分页
        $proxyDividendManagerListPager.off('click', '.pager');
        $proxyDividendManagerListPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            searchProxyDividendManager($target.attr('data-num'))
        })

        // 搜索按钮
        $contentBox.off('click', '[data-command=searchProxyDividendManager]');
        $contentBox.on('click', '[data-command=searchProxyDividendManager]', function(){
            searchProxyDividendManager(0);
        });

        // 显示下级数据
        $content.off('click', '[data-command=showProxyDividendManagerLowers]');
        $content.on('click', '[data-command=showProxyDividendManagerLowers]', function(e){
            var $target = $(e.target);
            var username = $target.attr('data-user');
            $proxyDividendManagerUsername.val(username);
            searchProxyDividendManager(0);
        });

        // 同意
        $contentBox.off('click', '[data-command=agree]');
        $contentBox.on('click', '[data-command=agree]', function(e){
            var id = $(e.target).closest('tr').attr('data-id');
            confirmAgree(id);
        });

        // 拒绝
        $contentBox.off('click', '[data-command=deny]');
        $contentBox.on('click', '[data-command=deny]', function(e){
            var id = $(e.target).closest('tr').attr('data-id');
            confirmDeny(id);
        });

        // 初始化列表
        searchProxyDividendManager(0);
    }

    // 初始化列表
    function searchProxyDividendManager(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/DividendSearch',
            data: {
                start: page * 10,
                limit: 10,
                username: $proxyDividendManagerUsername.val(),
                scope: $proxyDividendManagerScope.val()
            },
            success : function(res) {
                if (res.error === 0) {
                    $proxyDividendManagerListTable.html(tpl('#proxy_dividend_manager_list_tpl', {rows: res.data, username: res.username}))

                    setUserLevels(res.userLevels, 'showProxyDividendManagerLowers');

                    $proxyDividendManagerListPager.html(tpl('#pager_tpl', {
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

    function confirmAgree(id) {
        $.YF.alert_question('确认同意吗？同意后将于本周期开始结算。', function(resolve, reject){
            $.ajax({
                url: '/DividendAgree',
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
            $.YF.alert_success('契约分红同意成功，将于本周期开始结算！', function(){
                setTimeout(function(){
                    var page = $proxyDividendManagerListPager.find('.cur').attr('data-num');
                    searchProxyDividendManager(page);
                }, 100);
            });
        })
    }
    
    function confirmDeny(id) {
        $.YF.alert_question('确认拒绝吗？拒绝后可以联系您的上级再次发起！', function(resolve, reject){
            $.ajax({
                url: '/DividendDeny',
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
            $.YF.alert_success('契约分红拒绝成功！', function(){
                setTimeout(function(){
                    var page = $proxyDividendManagerListPager.find('.cur').attr('data-num');
                    searchProxyDividendManager(page);
                }, 100);
            });
        })
    }
    /**********契约分红管理**************/

    /**********发起契约分红**************/
    function initProxyDividendRequestContent() {
        $contentBox.html(tpl('#proxy_dividend_request_content_tpl', {}));

        // 组件
        $proxyDividendRequestUsername = $contentBox.find('input[name=proxyDividendRequestUsername]');
        $proxyDividendRequestMinValidUser = $contentBox.find('input[name=proxyDividendRequestMinValidUser]');
        $proxyDividendRequestMinValidUserDesc = $contentBox.find('[data-property=proxyDividendRequestMinValidUserDesc]');
//        initProxyDividendRequestMinValidUser();

        // 选择用户事件
        $contentBox.off('click', '[data-command=chooseUser]');
        $contentBox.on('click', '[data-command=chooseUser]', function(){
            new SingleChooseMember(function(data) {
                $proxyDividendRequestUsername.val(data);
                searchProxyDividendRequest(data);
            }, "/DividendListLower");
        });

        // 清空选择事件
        $contentBox.off('click', '[data-command=clearUser]');
        $contentBox.on('click', '[data-command=clearUser]', function(){
            resetProxyDividendData();
        });

        // 提交按钮
        $contentBox.off('click', '[data-command=submitRequest]');
        $contentBox.on('click', '[data-command=submitRequest]', function(){
            requestProxyDividend();
        });
    }

    function requestProxyDividend() {
        var username = $proxyDividendRequestUsername.val();
        if ($.YF.isEmpty(username)) {
            $.YF.alert_tooltip('请选择用户', $proxyDividendRequestUsername);
            return;
        }

        /*var minValidUser = $proxyDividendRequestMinValidUser.val();
        if (!$.YF.isDigitsZZS(minValidUser)) {
            $.YF.alert_tooltip('请输入正确数值', $proxyDividendRequestMinValidUser);
            return;
        }

        minValidUser = Number(minValidUser);
        if (minValidUser < proxyDividendRequestDataMinValidUser || minValidUser > proxyDividendRequestDataMaxValidUser || minValidUser < 0) {
            $.YF.alert_tooltip('有效人数可分配范围'+proxyDividendRequestDataMinValidUser+'~'+proxyDividendRequestDataMaxValidUser+'之间的数值', $proxyDividendRequestMinValidUser);
            return;
        }*/
        
        if(!checkLevel()){
        	$.YF.alert_warning('分红条款错误，请参考各项条款最大最小值填写');
        	return;
        }
        
        var form =  $("#addView");
        var saleLevel = '';
        var $eles = form.find('input[name="proxyDividendRequestSales"]');
        $eles.each(function(i,o){
        	if(i != $eles.length -1 ){
        		saleLevel += Number(o.value).toFixed(2) + ',';
        	}else{
        		saleLevel += Number(o.value).toFixed(2);
        	}
        });
        
        var lossLevel = '';
        $eles = form.find('input[name="proxyDividendRequestLoss"]');
        $eles.each(function(i,o){
        	if(i != $eles.length - 1){
        		lossLevel += Number(o.value).toFixed(2) + ',';
        	}else{
        		lossLevel += Number(o.value).toFixed(2);
        	}
        });
        	
        var scaleLevel = '';
        $eles = form.find('input[name="proxyDividendRequestScale"]');
        $eles.each(function(i,o){
        	if(i != $eles.length - 1){
        		scaleLevel += Number(o.value).toFixed(2) + ',';
        	}else{
        		scaleLevel += Number(o.value).toFixed(2);
        	}
        });
        
        var userLevel = '';
        $eles = form.find('input[name="proxyDividendRequestUser"]');
        $eles.each(function(i,o){
        	if(i != $eles.length - 1){
        		userLevel += Number(o.value) + ',';
        	}else{
        		userLevel += Number(o.value);
        	}
        });

        $.YF.showLoadingMask();
        var data = {username: username, salesLevel: saleLevel,lossLevel:lossLevel,scaleLevel:scaleLevel, userLevel: userLevel};
        $.ajax({
            url: "/DividendRequest",
            data: data,
            success: function(res){
                if (res.error === 0) {
                    $.YF.alert_success('契约分红发起成功，对方同意后即可生效！', function(){
                        resetProxyDividendData();
                    })
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
                $.YF.hideLoadingMask();
            }
        })
    }

    function resetProxyDividendData() {
        $proxyDividendRequestUsername.val('');
        proxyDividendRequestDataMinScale = 0;
        proxyDividendRequestDataMaxScale = 0;
        proxyDividendRequestDataMinSales = 0;
        proxyDividendRequestDataMaxSales = 0;
        proxyDividendRequestDataMinLoss = 0;
        proxyDividendRequestDataMaxLoss = 0;
        proxyDividendRequestDataMinValidUser = 0;
        proxyDividendRequestDataMaxValidUser = 0;
        maxSignLevel = 1;
        currentSignLevel = 1;
        $("#addView .level-group").remove();
        var self =  $("#addView");
        self.append(View.htmlView({disabled:true,isPlus: false,isDel: false},self));
//        initProxyDividendRequestMinValidUser();
    }

    function searchProxyDividendRequest() {
        $.YF.showLoadingMask();

        $.ajax({
            url: '/DividendRequestData',
            data: {
                username: $proxyDividendRequestUsername.val()
            },
            success: function(res) {
                if(res.error === 0) {
                    proxyDividendRequestDataMinScale = res.minScale;
                    proxyDividendRequestDataMaxScale = res.maxScale;
                    
                    proxyDividendRequestDataMinLoss = res.minLoss;
                    proxyDividendRequestDataMaxLoss = res.maxLoss;
                    
                    proxyDividendRequestDataMinSales = res.minSales;
                    proxyDividendRequestDataMaxSales = res.maxSales;
                    
                    proxyDividendRequestDataMinValidUser = res.minValidUser;
                    proxyDividendRequestDataMaxValidUser = res.maxValidUser;

                    maxSignLevel = res.maxSignLevel;
                    currentSignLevel = 1;
                    
                    //initSlider();
                    initProxyDividendRequestMinValidUser();
                    
                    var param = {
		                isPlus: true,
		                isDel: false,
		                minSale: proxyDividendRequestDataMinSales,
		                valSale:proxyDividendRequestDataMinSales,
		                maxSale: proxyDividendRequestDataMaxSales,
		                minLoss: proxyDividendRequestDataMinLoss,
		                valLoss:proxyDividendRequestDataMinLoss,
		                maxLoss: proxyDividendRequestDataMaxLoss,
		                minScale: proxyDividendRequestDataMinScale,
		                valScale:proxyDividendRequestDataMinScale,
		                maxScale: proxyDividendRequestDataMaxScale,
		                maxSignLevel: maxSignLevel,
		                minUser:proxyDividendRequestDataMinValidUser,
		                maxUser:proxyDividendRequestDataMaxValidUser,
		                valUser:proxyDividendRequestDataMinValidUser
		            }
                    var self =  $("#addView");
					 $("#addView .level-group").remove();
		            self.append(View.htmlView(param,self));
                }
                else {
                    $.YF.alert_warning(res.message);
                }
            },
            complete: function() {
                $.YF.hideLoadingMask();
            }
        });
    }

    function initProxyDividendRequestMinValidUser() {
        if (proxyDividendRequestDataMaxValidUser <= proxyDividendRequestDataMinValidUser) {
            $proxyDividendRequestMinValidUser.val(proxyDividendRequestDataMinValidUser).attr('disabled', 'disabled');
        }
        else {
            $proxyDividendRequestMinValidUser.val(proxyDividendRequestDataMinValidUser)
                .attr('data-min', proxyDividendRequestDataMinValidUser)
                .attr('data-max', proxyDividendRequestDataMaxValidUser)
                .attr('data-default', proxyDividendRequestDataMinValidUser)
                .removeAttr('disabled');
            $.YF.initInputForceDigit($proxyDividendRequestMinValidUser);
        }

        $proxyDividendRequestMinValidUserDesc.html('（可分配范围 '+proxyDividendRequestDataMinValidUser+' ~ '+proxyDividendRequestDataMaxValidUser+'）')
    }
    
    var View = {
    		htmlView: function(param,self) {
            var deaultParam = {
                isPlus: true,
                isDel: true,
                minSale: 0,
                valSale:0,
                maxSale: 0,
                minLoss: 0,
                valLoss:0,
                maxLoss: 0,
                minScale: 0,
                valScale:0,
                maxScale: 0,
                minUser:0,
                maxUser:0,
                valUser:0,
                disabled:false
            }
            $.extend(true, deaultParam, param);

            if(currentSignLevel == maxSignLevel){
        		deaultParam.isPlus = false;
        	}
            var disabledStr = ''; 
            if(deaultParam.disabled){
            	disabledStr = 'disabled="disabled"';
            }
            var add = deaultParam.isPlus == true ? '<i class="jj_2" name="add" data-command="plus">+</i>': '';
            var del = deaultParam.isDel == true ? '<i class="jj_1" name ="del" data-command="minus">-</i>': '';

            var item = $('<div class="level-group" style="clear:left"><span class="label" style="width:100px;">分红条款 '+currentSignLevel+'：</span>'+
                	'<span class="label" style="width:100px;">销量（W）：</span>'+
                    '<input name="proxyDividendRequestSales" type="text" style="width:110px;" class="text" maxlength="6" min="'+deaultParam.minSale+'" max="'+deaultParam.maxSale+'"  value="'+deaultParam.valSale+'" '+disabledStr+'>'+
    				'<span class="label" style="width:100px;">亏损（W）：</span>'+
                    '<input name="proxyDividendRequestLoss" type="text" style="width:110px;" class="text" maxlength="6" min="'+deaultParam.minLoss+'" max="'+deaultParam.maxLoss+'" value="'+deaultParam.valLoss+'" '+disabledStr+'>'+
                    '<span class="label" style="width:100px;">人数：</span>'+
                    '<input name="proxyDividendRequestUser" type="text" style="width:110px;" class="text" maxlength="6" min="'+deaultParam.minUser+'" max="'+deaultParam.maxUser+'" value="'+deaultParam.valUser+'" '+disabledStr+'>'+
    				'<span class="label" style="width:100px;">比例（%）：</span>'+
                    '<input name="proxyDividendRequestScale" type="text" style="width:110px;" class="text" maxlength="4" min="'+deaultParam.minScale+'" max="'+deaultParam.maxScale+'" value="'+deaultParam.valScale+'" '+disabledStr+'>'+
    				'<span class="jj" style="height: auto;width: auto; margin-left: 30px;border:none;">'+
    				(add + del) +
    				'</span>'+
    				'<div class="num" data-property="proxyDividendRequestLevelDesc">（范围：销量(W) '+deaultParam.minSale+' ~ '+deaultParam.maxSale+'；亏损(W) '+deaultParam.minLoss+' ~ '+deaultParam.maxLoss+'；人数：' + deaultParam.minUser+' ~ '+deaultParam.maxUser +'；比例(%) ：'+deaultParam.minScale+' ~ '+deaultParam.maxScale+'）</div></div>');
            
            item.find("input[name=proxyDividendRequestSales]").unbind('blur').blur(function() {
            		var o = $(this);
        	        var code = o.val();
        	        if (code === '' || parseFloat(code).toString() == 'NaN') {
        	            o.val(proxyDividendRequestDataMinSales);
        	        }
        	        else {
    	                code = Number(code);
    	                if (code < proxyDividendRequestDataMinSales) {
    	                    o.val(proxyDividendRequestDataMinSales);
    	                }else if(code > proxyDividendRequestDataMaxSales){
    	                	o.val(proxyDividendRequestDataMaxSales);
    	                }else{
    	                	o.val(code.toFixed(2));
    	                }
        	        }
            });
            
            item.find("input[name=proxyDividendRequestScale]").unbind('blur').blur(function() {
            		var o = $(this);
        	        var code = o.val();
        	        if (code === '' || parseFloat(code).toString() == 'NaN') {
        	            o.val(proxyDividendRequestDataMinScale);
        	        }
        	        else {
    	                code = Number(code);
    	                if (code < proxyDividendRequestDataMinScale) {
    	                    o.val(proxyDividendRequestDataMinScale);
    	                }else if(code > proxyDividendRequestDataMaxScale){
    	                	o.val(proxyDividendRequestDataMaxScale);
    	                }else{
    	                	o.val(code.toFixed(2));
    	                }
        	        }
            });
            
            item.find("input[name=proxyDividendRequestLoss]").unbind('blur').blur(function() {
            	 var o = $(this);
            	 var code = o.val();
                 if (code === '' || parseFloat(code).toString() == 'NaN') {
                     o.val(proxyDividendRequestDataMinLoss);
                 }
                 else {
                     code = Number(code);
                     if (code < proxyDividendRequestDataMinLoss) {
                         o.val(proxyDividendRequestDataMinLoss);
                     }else if(code > proxyDividendRequestDataMaxLoss){
                     	o.val(proxyDividendRequestDataMaxLoss);
                     }else{
 	                	o.val(code.toFixed(2));
 	                }
                 }
            });
            
            item.find("input[name=proxyDividendRequestUser]").unbind('blur').blur(function() {
           	 var o = $(this);
           	 var code = o.val();
                if (code === '' || parseInt(code).toString() == 'NaN') {
                    o.val(proxyDividendRequestDataMinLoss);
                }
                else {
                    code = Number(code);
                    if (code < proxyDividendRequestDataMinValidUser) {
                        o.val(proxyDividendRequestDataMinValidUser);
                    }else if(code > proxyDividendRequestDataMaxValidUser){
                    	o.val(proxyDividendRequestDataMaxValidUser);
                    }else{
	                	o.val(code);
	                }
                }
           });
            
            item.find("i").unbind('click').click(function() {
                switch($(this).attr("name")){
                    case 'add':
                        if (currentSignLevel >= maxSignLevel) {
                            $.YF.alert_warning('只能添加' + (maxSignLevel) + '级');
                            return;
                        }
                        var tmsale = new Number($(this).closest(".level-group").find("input[name=proxyDividendRequestSales]").val());
                        var tmloss = new Number($(this).closest(".level-group").find("input[name=proxyDividendRequestLoss]").val());
                        var tmscale = new Number($(this).closest(".level-group").find("input[name=proxyDividendRequestScale]").val());
                        var tmuser = new Number($(this).closest(".level-group").find("input[name=proxyDividendRequestUser]").val());
                        
                        if(tmsale >= deaultParam.maxSale){
                        	$.YF.alert_warning('无法添加条款，销量已达最大：' + (deaultParam.maxSale) + 'W');
                            return;
                        }
                        if(tmloss >= deaultParam.maxLoss){
                        	$.YF.alert_warning('无法添加条款，亏损已达最大：' + (deaultParam.maxLoss) + 'W');
                            return;
                        }
                        if(tmscale >= deaultParam.maxScale){
                        	$.YF.alert_warning('无法添加条款，比例已达最大：' + (deaultParam.maxScale) + '%');
                            return;
                        }
                        if(tmuser >= deaultParam.maxUser){
                        	$.YF.alert_warning('无法添加条款，人数已达最大：' + (deaultParam.maxUser));
                            return;
                        }
                        
                        if(!checkLevel()){
                        	$.YF.alert_warning('条款内容错误');
                            return;
                        }
                        
                        currentSignLevel++;
//                        deaultParam.minSale = tmsale + 1;
//                        deaultParam.minLoss =  tmloss + 1;
//                        deaultParam.minScale= tmscale + 0.1;
//                        deaultParam.minScale = deaultParam.minScale.toFixed(2);
                        
                        deaultParam.valSale = tmsale + 0.1;
                        deaultParam.valLoss =  tmloss + 0.1;
                        deaultParam.valScale= tmscale + 0.1;
                        deaultParam.valUser= tmuser + 1;
                        deaultParam.valSale = deaultParam.valSale.toFixed(2);
                        deaultParam.valLoss = deaultParam.valLoss.toFixed(2);
                        deaultParam.valScale = deaultParam.valScale.toFixed(2);
                        
                    	$(this).hide();
                    	$("i[name=del").hide();
                    	deaultParam.isDel = true;
                    	var html = View.htmlView(deaultParam,self);
                    	self.append(html);
                           break;
                    case 'del':
                    	currentSignLevel--;
                    	var group = $(this).closest(".level-group");
                    	group.prev().find("i[name=add]").show();
                    	group.prev().find("i[name=del]").show();
                    	group.remove();
                           break;
                    default:
                    	return;
                }
            });
            return item;
        }
    }
    
    var checkLevel = function(){
    	var form =  $("#addView");
    	 var saleLevel = '';
    	 var result = true;
         var $eles = form.find('input[name="proxyDividendRequestSales"]');
         $eles.each(function(i,o){
         	if(i != $eles.length -1 ){
         		saleLevel += Number(o.value).toFixed(2) + ',';
         	}else{
         		saleLevel += Number(o.value).toFixed(2);
         	}
         });
         
         var lossLevel = '';
         $eles = form.find('input[name="proxyDividendRequestLoss"]');
         $eles.each(function(i,o){
         	if(i != $eles.length - 1){
         		lossLevel += Number(o.value).toFixed(2) + ',';
         	}else{
         		lossLevel += Number(o.value).toFixed(2);
         	}
         });
         	
         var scaleLevel = '';
         $eles = form.find('input[name="proxyDividendRequestScale"]');
         $eles.each(function(i,o){
         	if(i != $eles.length - 1){
         		scaleLevel += Number(o.value).toFixed(2) + ',';
         	}else{
         		scaleLevel += Number(o.value).toFixed(2);
         	}
         });
         
         var userLevel = '';
         $eles = form.find('input[name="proxyDividendRequestUser"]');
         $eles.each(function(i,o){
         	if(i != $eles.length - 1){
         		userLevel += Number(o.value) + ',';
         	}else{
         		userLevel += Number(o.value);
         	}
         });
    	
    	var arrSales = saleLevel.split(',');
    	var arrLoss = lossLevel.split(',');
    	var arrScale = scaleLevel.split(',');
    	var arrUser = userLevel.split(',');
    	
    	if(Number(arrSales[0]) < Number(proxyDividendRequestDataMinSales) || Number(arrSales[arrSales.length-1]) > Number(proxyDividendRequestDataMaxSales)){
    		result = false;
    		return false;
		}
    	for(var i = 0; i < arrSales.length; i++){
    		if (arrSales[i] === '' || parseFloat(arrSales[i]).toString() == 'NaN') {
    			result = false;
	       		 return false;
	       	 }
    		// 必须是递增条款
//			if (i>0 && Number(arrSales[i-1]) >= Number(arrSales[i])) {
//				result = false;
//				return false;
//			}
    	}
    	
    	if(Number(arrUser[0]) < Number(proxyDividendRequestDataMinValidUser) || Number(arrUser[arrUser.length-1]) > Number(proxyDividendRequestDataMaxValidUser)){
    		result = false;
    		return false;
		}
    	for(var i = 0; i < arrUser.length; i++){
    		if (arrUser[i] === '' || parseInt(arrUser[i]).toString() == 'NaN') {
    			result = false;
	       		 return false;
	       	 }
    		// 必须是递增条款
//			if (i>0 && Number(arrSales[i-1]) >= Number(arrSales[i])) {
//				result = false;
//				return false;
//			}
    	}
    	
    	if(Number(arrLoss[0]) < Number(proxyDividendRequestDataMinLoss) || Number(arrLoss[arrLoss.length-1]) > Number(proxyDividendRequestDataMaxLoss)){
    		result = false;
			return false;
		}
    	for(var i = 0; i < arrLoss.length; i++){
    		if (arrLoss[i] === '' || parseFloat(arrLoss[i]).toString() == 'NaN') {
    			result = false;
	       		 return false;
	       	 }
    		// 必须是递增条款
//			if (i>0 && Number(arrLoss[i-1]) >= Number(arrLoss[i])) {
//				result = false;
//				return false;
//			}
    	}
    	
    	if(Number(arrScale[0]) < Number(proxyDividendRequestDataMinScale) || Number(arrScale[arrScale.length-1]) > Number(proxyDividendRequestDataMaxScale)){
    		result = false;
			return false;
		}
    	for(var i = 0; i < arrScale.length; i++){
    		// 必须是递增条款
			if (i>0 && Number(arrScale[i-1]) >= Number(arrScale[i])) {
				result = false;
				return false;
			}
    	}
    	
    	return result;
    	
    }
    
    /**********发起契约分红**************/

    // 切换tab
    $contentTab.on('click', '[data-command=changeTab]', function(){
        var $this = $(this);
        if ($this.hasClass('cur')) {
            return;
        }

        $this.addClass('cur').siblings().removeClass('cur');
        initContentBox();
    });

    // 初始化
    init();
})