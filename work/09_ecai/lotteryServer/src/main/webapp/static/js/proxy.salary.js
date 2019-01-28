$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentTab = $("#contentTab", $content);
    var $contentBox = $("#contentBox", $content);
    var $userLevels = $("#userLevels", $contentTab);

    /**********契约日结账单**************/
    var $proxySalaryBillScope;
    var $proxySalaryBillUsername;
    var $proxySalaryBillSTime;
    var $proxySalaryBillETime;
    var $proxySalaryBillListTable;
    var $proxySalaryBillListPager;
    /**********契约日结账单**************/

    /**********契约日结管理**************/
    var $proxySalaryManagerScope;
    var $proxySalaryManagerUsername;
    var $proxySalaryManagerListTable;
    var $proxySalaryManagerListPager;
    /**********契约日结管理**************/

    /**********发起契约日结**************/
    var $proxySalaryRequestUsername;
    var $proxySalaryRequestScale;
    var $proxySalaryRequestSlide;
//    var $proxySalaryRequestSliderBar;
    var $proxySalaryRequestMinValidUser;
    var $proxySalaryRequestMinValidUserDesc;
    var proxySalaryRequestScaleRegex = /^([1-9]\d*|0)(\.\d{1})?$/;
    var proxySalaryRequestDataMinScale = 0;
    var proxySalaryRequestDataMaxScale = 0;
    var proxySalaryRequestDataMinSales = 0;
    var proxySalaryRequestDataMaxSales = 0;
    var proxySalaryRequestDataMinLoss = 0;
    var proxySalaryRequestDataMaxLoss = 0;
    var proxySalaryRequestDataMinValidUser = 0;
    var proxySalaryRequestDataMaxValidUser = 0;
    var maxSignLevel = 1;
    var currentSignLevel = 1;
    /**********发起契约日结**************/

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
            initProxySalaryBillContent();
        }
        else if (type == 'manager') {
            initProxySalaryManagerContent();
        }
        else if (type == 'request') {
            initProxySalaryRequestContent();
        }
    }

    /**********契约日结账单**************/
    function initProxySalaryBillContent() {
        $contentBox.html(tpl('#proxy_salary_bill_content_tpl', {}));

        $proxySalaryBillScope = $contentBox.find('select[name=proxySalaryBillScope]');
        $proxySalaryBillUsername = $contentBox.find('input[name=proxySalaryBillUsername]');
        $proxySalaryBillSTime = $contentBox.find('input[name=proxySalaryBillSTime]');
        $proxySalaryBillETime = $contentBox.find('input[name=proxySalaryBillETime]');

        $proxySalaryBillListTable = $contentBox.find('table tbody');
        $proxySalaryBillListPager = $contentBox.find('.paging');

        // 分页
        $proxySalaryBillListPager.off('click', '.pager');
        $proxySalaryBillListPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            searchProxySalaryBill($target.attr('data-num'))
        })

        // 搜索按钮
        $contentBox.off('click', '[data-command=searchProxySalaryBill]');
        $contentBox.on('click', '[data-command=searchProxySalaryBill]', function(e){
            searchProxySalaryBill(0);
        });

        // 详情
        $contentBox.off('click', '[data-command=proxySalaryBillDetails]');
        $contentBox.on('click', '[data-command="proxySalaryBillDetails"]', function(e){
            var id = $(e.target).closest('tr').attr('data-id');
            showProxySalaryBillDetails(id);
        })

        // 显示下级数据
        $content.off('click', '[data-command=showProxySalaryBillLowers]');
        $content.on('click', '[data-command=showProxySalaryBillLowers]', function(e){
            var $target = $(e.target);
            var username = $target.attr('data-user');
            $proxySalaryBillUsername.val(username);
            searchProxySalaryBill(0);
        });

        // 初始化日期组件
        initProxySalaryBillDate();
    }

    // 初始化日期组件
    function initProxySalaryBillDate() {
        var sTime = $.YF.addDayForDate(new Date(), 0);
        var eTime = $.YF.addDayForDate(new Date(), 1);

        $.YF.initDateUI('#proxySalaryBillSTime');
        $.YF.initDateUI('#proxySalaryBillETime');

        $proxySalaryBillSTime.val(sTime);
        $proxySalaryBillETime.val(eTime);

        // 初始化列表
        searchProxySalaryBill(0);
    }

    // 初始化列表
    function searchProxySalaryBill(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/DailySettleBillSearch',
            data: {
                start: page * 10,
                limit: 10,
                username: $proxySalaryBillUsername.val(),
                sTime: $proxySalaryBillSTime.val(),
                eTime: $proxySalaryBillETime.val(),
                scope: $proxySalaryBillScope.val()
            },
            success : function(res) {
                if (res.error === 0) {
                    $proxySalaryBillListTable.html(tpl('#proxy_salary_bill_list_tpl', {rows: res.data, username: res.username}))

                    setUserLevels(res.userLevels, 'showProxySalaryBillLowers');

                    $proxySalaryBillListPager.html(tpl('#pager_tpl', {
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

    function showProxySalaryBillDetails(id) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/DailySettleBillDetails',
            data: {
                id: id
            },
            success : function(res) {
                if (res.error === 0) {
                    var html = tpl('#proxy_salary_bill_details_tpl', {data: res.data});

                    swal({
                        title: '契约日结详情',
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
    /**********契约日结账单**************/






    /**********契约日结管理**************/
    function initProxySalaryManagerContent() {
        $contentBox.html(tpl('#proxy_salary_manager_content_tpl', {}));

        $proxySalaryManagerScope = $contentBox.find('select[name=proxySalaryManagerScope]');
        $proxySalaryManagerUsername = $contentBox.find('input[name=proxySalaryManagerUsername]');

        $proxySalaryManagerListTable = $contentBox.find('table tbody');
        $proxySalaryManagerListPager = $contentBox.find('.paging');

        // 分页
        $proxySalaryManagerListPager.off('click', '.pager');
        $proxySalaryManagerListPager.on('click', '.pager', function(e) {
            var $target = $(e.target)
            searchProxySalaryManager($target.attr('data-num'))
        })

        // 搜索按钮
        $contentBox.off('click', '[data-command=searchProxySalaryManager]');
        $contentBox.on('click', '[data-command=searchProxySalaryManager]', function(){
            searchProxySalaryManager(0);
        });

        // 显示下级数据
        $content.off('click', '[data-command=showProxySalaryManagerLowers]');
        $content.on('click', '[data-command=showProxySalaryManagerLowers]', function(e){
            var $target = $(e.target);
            var username = $target.attr('data-user');
            $proxySalaryManagerUsername.val(username);
            searchProxySalaryManager(0);
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
        searchProxySalaryManager(0);
    }

    // 初始化列表
    function searchProxySalaryManager(page) {
        $.YF.showLoadingMask();

        $.ajax({
            url : '/DailySettleSearch',
            data: {
                start: page * 10,
                limit: 10,
                username: $proxySalaryManagerUsername.val(),
                scope: $proxySalaryManagerScope.val()
            },
            success : function(res) {
                if (res.error === 0) {
                    $proxySalaryManagerListTable.html(tpl('#proxy_salary_manager_list_tpl', {rows: res.data, username: res.username}))

                    setUserLevels(res.userLevels, 'showProxySalaryManagerLowers');

                    $proxySalaryManagerListPager.html(tpl('#pager_tpl', {
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
        $.YF.alert_question('确认同意吗？同意后即刻生效！', function(resolve, reject){
            $.ajax({
                url: '/DailySettleAgree',
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
            $.YF.alert_success('契约日结同意成功！', function(){
                setTimeout(function(){
                    var page = $proxySalaryManagerListPager.find('.cur').attr('data-num');
                    searchProxySalaryManager(page);
                }, 100);
            });
        })
    }
    
    function confirmDeny(id) {
        $.YF.alert_question('确认拒绝吗？拒绝后可以联系您的上级再次发起！', function(resolve, reject){
            $.ajax({
                url: '/DailySettleDeny',
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
            $.YF.alert_success('契约日结拒绝成功！', function(){
                setTimeout(function(){
                    var page = $proxySalaryManagerListPager.find('.cur').attr('data-num');
                    searchProxySalaryManager(page);
                }, 100);
            });
        })
    }
    /**********契约日结管理**************/

    /**********发起契约日结**************/
    function initProxySalaryRequestContent() {
        $contentBox.html(tpl('#proxy_salary_request_content_tpl', {}));

        // 组件
        $proxySalaryRequestUsername = $contentBox.find('input[name=proxySalaryRequestUsername]');
        $proxySalaryRequestScale = $contentBox.find('input[name=proxySalaryRequestScale]');
        $proxySalaryRequestMinValidUser = $contentBox.find('input[name=proxySalaryRequestMinValidUser]');
        $proxySalaryRequestMinValidUserDesc = $contentBox.find('[data-property=proxySalaryRequestMinValidUserDesc]');
        initProxySalaryRequestMinValidUser();

        // slider
//        $proxySalaryRequestSlide = $contentBox.find('#proxySalaryRequestSliderBox .slider-bar');
//        initSlider();

        // 输入事件
       /* $contentBox.off("keyup", 'input[name=proxySalaryRequestScale]');
        $contentBox.on("keyup", 'input[name=proxySalaryRequestScale]', function(){
            onProxySalaryRequestScaleValidate();
        });
        $contentBox.off("blur", 'input[name=proxySalaryRequestScale]');
        $contentBox.on("blur", 'input[name=proxySalaryRequestScale]', function(){
            onProxySalaryRequestScaleChange();
        });
        $contentBox.off("change", 'input[name=proxySalaryRequestScale]');
        $contentBox.on("change", 'input[name=proxySalaryRequestScale]', function(){
            onProxySalaryRequestScaleChange();
        });*/

        // 加减
        $contentBox.off('click', '[data-command=plus], [data-command=minus]');
        $contentBox.on('click', '[data-command=plus], [data-command=minus]', function(e) {
            var code = parseFloat($proxySalaryRequestScale.val());
            var $target = $(e.target)
            if ($target.hasClass('jj_1')) {
                if (code <= proxySalaryRequestDataMinScale) {
                    return;
                }

                if (code > proxySalaryRequestDataMinScale) {
                    code = $.YF.numberSub(code, 0.1);
                    $proxySalaryRequestScale.val(code)
//                    $proxySalaryRequestSliderBar.val(code);
                }
            } else {
                if (code >= proxySalaryRequestDataMaxScale) {
                    return;
                }
                code = $.YF.numberAdd(code, 0.1);
                $proxySalaryRequestScale.val(code)
//                $proxySalaryRequestSliderBar.val(code);
            }
        })

        // 选择用户事件
        $contentBox.off('click', '[data-command=chooseUser]');
        $contentBox.on('click', '[data-command=chooseUser]', function(){
            new SingleChooseMember(function(data) {
                $proxySalaryRequestUsername.val(data);
                searchProxySalaryRequest(data);
            }, "/DailySettleListLower");
        });

        // 清空选择事件
        $contentBox.off('click', '[data-command=clearUser]');
        $contentBox.on('click', '[data-command=clearUser]', function(){
            resetProxySalaryData();
        });

        // 提交按钮
        $contentBox.off('click', '[data-command=submitRequest]');
        $contentBox.on('click', '[data-command=submitRequest]', function(){
            requestProxySalary();
        });
    }

    function requestProxySalary() {
        var username = $proxySalaryRequestUsername.val();
        if ($.YF.isEmpty(username)) {
            $.YF.alert_tooltip('请选择用户', $proxySalaryRequestUsername);
            return;
        }

        /*var minValidUser = $proxySalaryRequestMinValidUser.val();
        if (!$.YF.isDigitsZZS(minValidUser)) {
            $.YF.alert_tooltip('请输入正确数值', $proxySalaryRequestMinValidUser);
            return;
        }

        minValidUser = Number(minValidUser);
        if (minValidUser < proxySalaryRequestDataMinValidUser || minValidUser > proxySalaryRequestDataMaxValidUser || minValidUser < 0) {
            $.YF.alert_tooltip('有效人数可分配范围'+proxySalaryRequestDataMinValidUser+'~'+proxySalaryRequestDataMaxValidUser+'之间的数值', $proxySalaryRequestScale);
            return;
        }*/
        
        if(!checkLevel()){
        	$.YF.alert_warning('分红条款错误，请参考各项条款最大最小值填写');
        	return;
        }
        
        var form =  $("#addView");
        var saleLevel = '';
        var $eles = form.find('input[name="proxySalaryRequestSales"]');
        $eles.each(function(i,o){
        	if(i != $eles.length -1 ){
        		saleLevel += Number(o.value).toFixed(2) + ',';
        	}else{
        		saleLevel += Number(o.value).toFixed(2);
        	}
        });
        
        var lossLevel = '';
        $eles = form.find('input[name="proxySalaryRequestLoss"]');
        $eles.each(function(i,o){
        	if(i != $eles.length - 1){
        		lossLevel += Number(o.value).toFixed(2) + ',';
        	}else{
        		lossLevel += Number(o.value).toFixed(2);
        	}
        });
        	
        var scaleLevel = '';
        $eles = form.find('input[name="proxySalaryRequestScale"]');
        $eles.each(function(i,o){
        	if(i != $eles.length - 1){
        		scaleLevel += Number(o.value).toFixed(2) + ',';
        	}else{
        		scaleLevel += Number(o.value).toFixed(2);
        	}
        });
        
        var userLevel = '';
        $eles = form.find('input[name="proxySalaryRequestUser"]');
        $eles.each(function(i,o){
        	if(i != $eles.length - 1){
        		userLevel += Number(o.value) + ',';
        	}else{
        		userLevel += Number(o.value);
        	}
        });

        $.YF.showLoadingMask();
        var data = {username: username, salesLevel: saleLevel,lossLevel:lossLevel,scaleLevel:scaleLevel, userLevel: userLevel};

//        $.YF.showLoadingMask();
//        var data = {username: username, scale: scale, minValidUser: minValidUser};
        $.ajax({
            url: "/DailySettleRequest",
            data: data,
            success: function(res){
                if (res.error === 0) {
                    $.YF.alert_success('契约日结发起成功，对方同意后即可生效！', function(){
                        resetProxySalaryData();
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

    function resetProxySalaryData() {
        $proxySalaryRequestUsername.val('');
        proxySalaryRequestDataMinScale = 0;
        proxySalaryRequestDataMaxScale = 0;
        proxySalaryRequestDataMinSales = 0;
        proxySalaryRequestDataMaxSales = 0;
        proxySalaryRequestDataMinLoss = 0;
        proxySalaryRequestDataMaxLoss = 0;
        proxySalaryRequestDataMinValidUser = 0;
        proxySalaryRequestDataMaxValidUser = 0;
        maxSignLevel = 1;
        currentSignLevel = 1;
        $("#addView .level-group").remove();
        var self =  $("#addView");
        self.append(View.htmlView({disabled:true,isPlus: false,isDel: false},self));
        initProxySalaryRequestMinValidUser();
    }

    function searchProxySalaryRequest() {
        $.YF.showLoadingMask();

        $.ajax({
            url: '/DailySettleRequestData',
            data: {
                username: $proxySalaryRequestUsername.val()
            },
            success: function(res) {
                if(res.error === 0) {
                    proxySalaryRequestDataMinScale = res.minScale;
                    proxySalaryRequestDataMaxScale = res.maxScale;
                    
                    proxySalaryRequestDataMinLoss = res.minLoss;
                    proxySalaryRequestDataMaxLoss = res.maxLoss;
                    
                    proxySalaryRequestDataMinSales = res.minSales;
                    proxySalaryRequestDataMaxSales = res.maxSales;
                    
                    proxySalaryRequestDataMinValidUser = res.minValidUser;
                    proxySalaryRequestDataMaxValidUser = res.maxValidUser;

                    maxSignLevel = res.maxSignLevel;
                    currentSignLevel = 1;

                    initProxySalaryRequestMinValidUser();
                    
                    var param = {
    		                isPlus: true,
    		                isDel: false,
    		                minSale: proxySalaryRequestDataMinSales,
    		                valSale:proxySalaryRequestDataMinSales,
    		                maxSale: proxySalaryRequestDataMaxSales,
    		                minLoss: proxySalaryRequestDataMinLoss,
    		                valLoss:proxySalaryRequestDataMinLoss,
    		                maxLoss: proxySalaryRequestDataMaxLoss,
    		                minScale: proxySalaryRequestDataMinScale,
    		                valScale:proxySalaryRequestDataMinScale,
    		                maxScale: proxySalaryRequestDataMaxScale,
    		                maxSignLevel: maxSignLevel,
    		                minUser:proxySalaryRequestDataMinValidUser,
    		                maxUser:proxySalaryRequestDataMaxValidUser,
    		                valUser:proxySalaryRequestDataMinValidUser
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

    function initProxySalaryRequestMinValidUser() {
        if (proxySalaryRequestDataMaxValidUser <= proxySalaryRequestDataMinValidUser) {
            $proxySalaryRequestMinValidUser.val(proxySalaryRequestDataMinValidUser).attr('disabled', 'disabled');
        }
        else {
            $proxySalaryRequestMinValidUser.val(proxySalaryRequestDataMinValidUser)
                .attr('data-min', proxySalaryRequestDataMinValidUser)
                .attr('data-max', proxySalaryRequestDataMaxValidUser)
                .attr('data-default', proxySalaryRequestDataMinValidUser)
                .removeAttr('disabled');
            $.YF.initInputForceDigit($proxySalaryRequestMinValidUser);
        }

        $proxySalaryRequestMinValidUserDesc.html('（可分配范围 '+proxySalaryRequestDataMinValidUser+' ~ '+proxySalaryRequestDataMaxValidUser+'）')
    }

    function onProxySalaryRequestScaleValidate() {
        var code = $proxySalaryRequestScale.val();
        if (code === '')
            return
        if (proxySalaryRequestScaleRegex.test(code)) {
            code = Number(code);
            if (code >= proxySalaryRequestDataMinScale && code <= proxySalaryRequestDataMaxScale) {
                $proxySalaryRequestSliderBar.val(code);
            }
            else if (code > proxySalaryRequestDataMaxScale){
                $proxySalaryRequestScale.val(proxySalaryRequestDataMaxScale);
                $proxySalaryRequestSliderBar.val(proxySalaryRequestDataMaxScale);
            }
        }
        else {
            if(code.length == 1){
                $proxySalaryRequestScale.val(proxySalaryRequestDataMaxScale);
                $proxySalaryRequestSliderBar.val(proxySalaryRequestDataMaxScale);
            }else{
                if (code.endsWith('.')) {
                    if (code.endsWith('..')) {
                        $proxySalaryRequestScale.val(proxySalaryRequestDataMaxScale);
                        $proxySalaryRequestSliderBar.val(proxySalaryRequestDataMaxScale);
                    }
                    return;
                }
                else {
                    if (!/^([1-9]\d*|0)(\.\d{1})?$/.test(code)) {
                        code = Number(code.replace(/\D/g, ''));
                        $proxySalaryRequestScale.val(code);
                        $proxySalaryRequestSliderBar.val(code);
                    }
                }
            }
        }
    }

    function onProxySalaryRequestScaleChange() {
        var code = $proxySalaryRequestScale.val();
        if (code === '') {
            $proxySalaryRequestScale.val(proxySalaryRequestDataMaxScale);
            $proxySalaryRequestSliderBar.val(proxySalaryRequestDataMaxScale);
        }
        else {
            if (proxySalaryRequestScaleRegex.test(code)) {
                code = Number(code);
                if (code < proxySalaryRequestDataMinScale || code > proxySalaryRequestDataMaxScale) {
                    $proxySalaryRequestScale.val(proxySalaryRequestDataMaxScale);
                    $proxySalaryRequestSliderBar.val(proxySalaryRequestDataMaxScale);
                }
            } else {
                $proxySalaryRequestScale.val(proxySalaryRequestDataMaxScale);
                $proxySalaryRequestSliderBar.val(proxySalaryRequestDataMaxScale);
            }
        }
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
                    '<input name="proxySalaryRequestSales" type="text" style="width:110px;" class="text" maxlength="6" min="'+deaultParam.minSale+'" max="'+deaultParam.maxSale+'"  value="'+deaultParam.valSale+'" '+disabledStr+'>'+
    				'<span class="label" style="width:100px;">亏损（W）：</span>'+
                    '<input name="proxySalaryRequestLoss" type="text" style="width:110px;" class="text" maxlength="6" min="'+deaultParam.minLoss+'" max="'+deaultParam.maxLoss+'" value="'+deaultParam.valLoss+'" '+disabledStr+'>'+
                    '<span class="label" style="width:100px;">人数：</span>'+
                    '<input name="proxySalaryRequestUser" type="text" style="width:110px;" class="text" maxlength="4" min="'+deaultParam.minUser+'" max="'+deaultParam.maxUser +'" value="'+deaultParam.valUser+'" '+disabledStr+'>'+
    				'<span class="label" style="width:100px;">比例（%）：</span>'+
                    '<input name="proxySalaryRequestScale" type="text" style="width:110px;" class="text" maxlength="6" min="'+deaultParam.minScale+'" max="'+deaultParam.maxScale+'" value="'+deaultParam.valScale+'" '+disabledStr+'>'+
    				'<span class="jj" style="height: auto;width: auto; margin-left: 30px;border:none;">'+
    				(add + del) +
    				'</span>'+
    				'<div class="num" data-property="proxySalaryRequestLevelDesc">（范围：销量(W) '+deaultParam.minSale+' ~ '+deaultParam.maxSale+'；亏损(W) '+deaultParam.minLoss+' ~ '+deaultParam.maxLoss+'；人数：' + deaultParam.minUser+' ~ '+deaultParam.maxUser +'；比例(%) ：'+deaultParam.minScale+' ~ '+deaultParam.maxScale+'）</div></div>');
            
            item.find("input[name=proxySalaryRequestSales]").unbind('blur').blur(function() {
            		var o = $(this);
        	        var code = o.val();
        	        if (code === '' || parseFloat(code).toString() == 'NaN') {
        	            o.val(proxySalaryRequestDataMinSales);
        	        }
        	        else {
    	                code = Number(code);
    	                if (code < proxySalaryRequestDataMinSales) {
    	                    o.val(proxySalaryRequestDataMinSales);
    	                }else if(code > proxySalaryRequestDataMaxSales){
    	                	o.val(proxySalaryRequestDataMaxSales);
    	                }else{
    	                	o.val(code.toFixed(2));
    	                }
        	        }
            });
            
            item.find("input[name=proxySalaryRequestScale]").unbind('blur').blur(function() {
            		var o = $(this);
        	        var code = o.val();
        	        if (code === '' || parseFloat(code).toString() == 'NaN') {
        	            o.val(proxySalaryRequestDataMinScale);
        	        }
        	        else {
    	                code = Number(code);
    	                if (code < proxySalaryRequestDataMinScale) {
    	                    o.val(proxySalaryRequestDataMinScale);
    	                }else if(code > proxySalaryRequestDataMaxScale){
    	                	o.val(proxySalaryRequestDataMaxScale);
    	                }else{
    	                	o.val(code.toFixed(2));
    	                }
        	        }
            });
            
            item.find("input[name=proxySalaryRequestLoss]").unbind('blur').blur(function() {
            	 var o = $(this);
            	 var code = o.val();
                 if (code === '' || parseFloat(code).toString() == 'NaN') {
                     o.val(proxySalaryRequestDataMinLoss);
                 }
                 else {
                     code = Number(code);
                     if (code < proxySalaryRequestDataMinLoss) {
                         o.val(proxySalaryRequestDataMinLoss);
                     }else if(code > proxySalaryRequestDataMaxLoss){
                     	o.val(proxySalaryRequestDataMaxLoss);
                     }else{
 	                	o.val(code.toFixed(2));
 	                }
                 }
            });
            
            item.find("input[name=proxySalaryRequestUser]").unbind('blur').blur(function() {
           	 var o = $(this);
           	 var code = o.val();
                if (code === '' || parseInt(code).toString() == 'NaN') {
                    o.val(proxySalaryRequestDataMinValidUser);
                }
                else {
                    code = Number(code);
                    if (code < proxySalaryRequestDataMinValidUser) {
                        o.val(proxySalaryRequestDataMinValidUser);
                    }else if(code > proxySalaryRequestDataMaxValidUser){
                    	o.val(proxySalaryRequestDataMaxValidUser);
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
                        var tmsale = new Number($(this).closest(".level-group").find("input[name=proxySalaryRequestSales]").val());
                        var tmloss = new Number($(this).closest(".level-group").find("input[name=proxySalaryRequestLoss]").val());
                        var tmscale = new Number($(this).closest(".level-group").find("input[name=proxySalaryRequestScale]").val());
                        var tmuser = new Number($(this).closest(".level-group").find("input[name=proxySalaryRequestUser]").val());
                        
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
                        deaultParam.valUser = deaultParam.valUser;
                        
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
         var $eles = form.find('input[name="proxySalaryRequestSales"]');
         $eles.each(function(i,o){
         	if(i != $eles.length -1 ){
         		saleLevel += Number(o.value).toFixed(2) + ',';
         	}else{
         		saleLevel += Number(o.value).toFixed(2);
         	}
         });
         
         var lossLevel = '';
         $eles = form.find('input[name="proxySalaryRequestLoss"]');
         $eles.each(function(i,o){
         	if(i != $eles.length - 1){
         		lossLevel += Number(o.value).toFixed(2) + ',';
         	}else{
         		lossLevel += Number(o.value).toFixed(2);
         	}
         });
         	
         var scaleLevel = '';
         $eles = form.find('input[name="proxySalaryRequestScale"]');
         $eles.each(function(i,o){
         	if(i != $eles.length - 1){
         		scaleLevel += Number(o.value).toFixed(2) + ',';
         	}else{
         		scaleLevel += Number(o.value).toFixed(2);
         	}
         });
    	
         var userLevel = '';
         $eles = form.find('input[name="proxySalaryRequestUser"]');
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
    	
    	if(Number(arrSales[0]) < Number(proxySalaryRequestDataMinSales) || Number(arrSales[arrSales.length-1]) > Number(proxySalaryRequestDataMaxSales)){
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
    	
    	if(Number(arrUser[0]) < Number(proxySalaryRequestDataMinValidUser) || Number(arrUser[arrUser.length-1]) > Number(proxySalaryRequestDataMaxValidUser)){
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
    	
    	if(Number(arrLoss[0]) < Number(proxySalaryRequestDataMinLoss) || Number(arrLoss[arrLoss.length-1]) > Number(proxySalaryRequestDataMaxLoss)){
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
    	
    	if(Number(arrScale[0]) < Number(proxySalaryRequestDataMinScale) || Number(arrScale[arrScale.length-1]) > Number(proxySalaryRequestDataMaxScale)){
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
    
    /**********发起契约日结**************/

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