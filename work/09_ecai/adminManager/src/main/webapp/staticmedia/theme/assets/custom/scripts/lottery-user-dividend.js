var LotteryDividend = function() {
	// 最小销量值(单位W元)
	var minSale = 0;
	// 最大销量值(单位W元)
	var maxSale = 0;
	// 最小亏损值(单位W元)
	var minLoss = 0;
	// 最大亏损值(单位W元)
	var maxLoss = 0;
	// 最小分红比例（%）
	var minScale = 0;
	// 最大分红比例（%）
	var maxScale = 0;
	var minUser = 1;
	var maxUser = 1000;
	var maxSignLevel = 1;
	var currentSignLevel = 1;
	
    var isLoading = false;
    var handelDatePicker = function() {
        $('.date-picker').datepicker({
            language: 'zh-CN',
            format: 'yyyy-mm-dd',
            autoclose: true
        });
    }

    var UserDividendTable = function() {
        var tableList = $('#table-dividend-list');
        var tablePagelist = tableList.find('.page-list');

        var getSearchParams = function() {
            var username = tableList.find('input[name="username"]').val();
            var sTime = tableList.find('[data-field="time"] > input[name="sTime"]').val();
            var eTime = tableList.find('[data-field="time"] > input[name="eTime"]').val();
            var minScale = tableList.find('input[name="minScale"]').val();
            var maxScale = tableList.find('input[name="maxScale"]').val();
            var minValidUser = tableList.find('input[name="minValidUser"]').val();
            var maxValidUser = tableList.find('input[name="maxValidUser"]').val();
            var status = tableList.find('select[name="status"]').val();
            var fixed = tableList.find('input[name="fixed"]').is(':checked') ? 0 : null;
            return {
                username: username,
                sTime: sTime,
                eTime: eTime,
                minScale: minScale,
                maxScale: maxScale,
                minValidUser: minValidUser,
                maxValidUser: maxValidUser,
                status: status,
                fixed: fixed
            };
        }

        var resetParamsForUpUser = function(upUsername) {
            tableList.find('input[name="username"]').val(upUsername);
            tableList.find('input[name="minScale"]').val('');
            tableList.find('input[name="maxScale"]').val('');
            tableList.find('input[name="minValidUser"]').val('');
            tableList.find('input[name="maxValidUser"]').val('');
            tableList.find('input[name="fixed"]').removeAttr('checked');
        }

        var pagination = $.pagination({
            render: tablePagelist,
            pageSize: 20,
            ajaxType: 'post',
            ajaxUrl: './lottery-user-dividend/list',
            ajaxData: getSearchParams,
            beforeSend: function() {

},
            complete: function() {

},
            success: function(list) {
                var table = tableList.find('table > tbody').empty();
                var innerHtml = '';
                $.each(list,
                function(idx, val) {
                    var formatId = '<a href="javascript:;" data-val="' + val.username + '" data-command="details">' + val.username + '</a>';
                    var minValidUser = val.minValidUser <= 0 ? '无限制': val.minValidUser;
                    var fixedtype = val.fixed === 0 ? '浮动比例' : '固定比例';
                    var sales = val.salesLevel.split(',');//销量
                    var loss = val.lossLevel.split(',');//亏损
                    var scale = val.scaleLevel.split(',');//比例
                    var users = val.userLevel.split(',');//人数
                    var showHtml = '';
                    for(var i = 0; i < sales.length; i++){
                		showHtml +=  '条款(' + (i + 1) + ')：销量 >= ' + Number(sales[i]).toFixed(2) + 'W ；亏损 >= ' + Number(loss[i]).toFixed(2) + 'W ；人数 >= ' + users[i] + ' ；分红比例：' + Number(scale[i]).toFixed(2) + '%</br>';
                    }

                    var formatOperate = '<a data-command="del" data-username="' + val.username + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-remove"></i> 删除</a><a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑</a>';

                    if(val.status == 1){
						var formatOperate = '<a data-command="del" data-username="' + val.username + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-remove"></i> 删除</a>';	
                    }
                    
                    innerHtml += '<tr class="align-center" data-id="' + val.id + '">' +
                    '<td>' + val.id + '</td>' +
                    '<td>' + formatId + '</td>' +
                    '<td>' + fixedtype + '</td>' +
                    '<td>' + showHtml + '</td>' + 
//                    '<td>' + minValidUser + '</td>' + 
                    '<td>' + val.totalAmount.toFixed(4) + '</td>' + 
                    '<td>' + val.createTime + '</td>' + 
                    '<td>' + val.agreeTime + '</td>' + 
                    '<td>' + DataFormat.formatDividendStatus(val.status) + '</td>' + 
                    '<td>' + formatOperate + '</td>' + '</tr>';

                });
                table.html(innerHtml);
                table.find('[data-command="details"]').unbind('click').click(function() {
                    var upUsername = $(this).attr('data-val');
                    resetParamsForUpUser(upUsername);
                    UserDividendTable.init();
                });
                table.find('[data-command="del"]').unbind('click').click(function() {
                    var username = $(this).attr('data-username');
                    doDel(username);
                });
                table.find('[data-command="edit"]').unbind('click').click(function() {
                    var id = $(this).parents('tr').attr('data-id');
                    loadEditDetails(id,
                    function(data) {
                        if (data) {
                            ModifyUserDividendModal.show(data);
                        } else {
                            bootbox.alert({
                                title: "提示",
                                message: '无法编辑该用户分红配置'
                            });
                        }
                    });
                });
            },
            pageError: function(response) {
                bootbox.dialog({
                    message: response.message,
                    title: '提示消息',
                    buttons: {
                        success: {
                            label: '<i class="fa fa-check"></i> 确定',
                            className: 'btn-success',
                            callback: function() {}
                        }
                    }
                });
            },
            emptyData: function() {
                var tds = tableList.find('thead tr th').size();
                tableList.find('table > tbody').html('<tr><td colspan="' + tds + '">没有相关数据</td></tr>');
            }
        });

        tableList.find('[data-command="search"]').unbind('click').click(function() {
            init();
        });

        tableList.find('input[name="advanced"]').unbind('change').change(function() {
            isAdvanced($(this));
        });

        tableList.find('[data-command="add"]').unbind('click').click(function() {
            AddUserDividendModal.show();
        });

        var doDel = function(username) {
            var msg = '确认要删除会员<span class="fw600 fs14">【' + username + '】</span>的团队契约分红配置吗？（其所有下级也会被删除，该操作不可恢复！）';
            bootbox.dialog({
                message: msg,
                title: '提示消息',
                buttons: {
                    success: {
                        label: '<i class="fa fa-check"></i> 确定',
                        className: 'green-meadow',
                        callback: function() {
                            if (isLoading) return;
                            var params = {
                                username: username
                            };
                            var url = './lottery-user-dividend/del';
                            isLoading = true;
                            $.ajax({
                                type: 'post',
                                url: url,
                                data: params,
                                dataType: 'json',
                                success: function(data) {
                                    isLoading = false;
                                    if (data.error == 0) {
                                        UserDividendTable.init();
                                        toastr['success']('删除成功！', '操作提示');
                                    }
                                    if (data.error == 1 || data.error == 2) {
                                        toastr['error']('操作失败！' + data.message, '操作提示');
                                    }
                                }
                            });
                        }
                    },
                    danger: {
                        label: '<i class="fa fa-undo"></i> 取消',
                        className: 'btn-danger',
                        callback: function() {}
                    }
                }
            });
        }

        var loadEditDetails = function(id, callback) {
            var params = {
                id: id
            };
            var url = './lottery-user-dividend/edit-get';
            $.ajax({
                type: 'post',
                url: url,
                data: params,
                dataType: 'json',
                success: function(data) {
                    if (data.error == 0) {
                        callback(data.data);
                    } else {
                        toastr['error']('操作失败！' + data.message, '操作提示');
                    }
                }
            });
        }

        var isAdvanced = function(advanced) {
            if (!advanced) {
                advanced = tableList.find('input[name="advanced"]');
            }
            if (advanced.is(':checked')) {
                tableList.find('[data-hide="advanced"]').show();
            } else {
                clearAdvanced();
                tableList.find('[data-hide="advanced"]').hide();
            }
        }

        var clearAdvanced = function() {
            var dateTime = tableList.find('[data-field="time"]');
            dateTime.find('input[name="sTime"]').val('').change();
            dateTime.find('input[name="eTime"]').val('').change();
            tableList.find('input[name="minScale"]').val('');
            tableList.find('input[name="maxScale"]').val('');
            tableList.find('input[name="minValidUser"]').val('');
            tableList.find('input[name="maxValidUser"]').val('');
            tableList.find('select[name="status"] > option').eq(0).attr('selected', true);
            tableList.find('input[name="fixed"]').removeAttr('checked');
            $("#addView .inputLevel").remove();
        }

        isAdvanced();

        var init = function() {
            pagination.init();
        }

        var reload = function() {
            pagination.reload();
        }

        return {
            init: init,
            reload: reload
        }
    } ();

    var ModifyUserDividendModal = function() {
        var modal = $('#modal-modify');
        var form = modal.find('form');
        var initForm = function() {
            form.validate({
            	rules: {
    				scale: {
    					required: true,
    					number: true
    				},
    				loss: {
    					required: true,
    					number: true
    				},
    				sale: {
    					required: true,
    					number: true
    				},
    				validUser:{
    					required: true,
    					digits: true,
    					min:1,
    					max:1000
    				}
    			},
    			messages: {
    				sale : {
    					required : '不能为空！',
    					number : '请填写数值！',
    					min : '最小{0}',
    					max : '最大{0}',
    					maxlength:'最长输入{0}字符'
    				},
    				loss : {
    					required : '不能为空！',
    					number : '请填写数值！',
    					min : '最小{0}',
    					max : '最大{0}',
    					maxlength:'最长输入{0}字符'
    				},
    				scale : {
    					required : '不能为空！',
    					number : '请填写数值！',
    					min : '最小{0}',
    					max : '最大{0}',
    					maxlength:'最长输入{0}字符'
    				},
    				validUser : {
    					required : '不能为空！',
    					digits : '必须为整数！',
    					min : '最小{0}',
    					max : '最大{0}'
    				}

    			},
                invalidHandler: function(event, validator) {},
                errorPlacement: function (error, element) {
    				$(element).closest('div').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
    			},
    			highlight: function (element) {
    				$(element).closest('div').removeClass('has-success').addClass('has-error');
    			},
    			unhighlight: function (element) {
    				$(element).closest('div').removeClass('has-error').addClass('has-success');
    				$(element).closest('div').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
    			}
            });
            modal.find('[data-command="submit"]').unbind('click').click(function() {
                if (form.validate().form()) {
                    doSubmit();
                }
            });
        }

        var isSending = false;
        var doSubmit = function() {
            if (isSending) return;
            var id = form.find('input[name="id"]').val();
            var minValidUser = form.find('input[name="minValidUser"]').val();
            
            if(!checkLevel(form)){
            	toastr['error']('分红条款错误，请参考各项条款最大最小值填写');
            	return;
            }
            
            var saleLevel = '';
            var $eles = form.find('input[name="sale"]');
            $eles.each(function(i,o){
            	if(i != $eles.length -1 ){
            		saleLevel += Number(o.value).toFixed(1) + ',';
            	}else{
            		saleLevel += Number(o.value).toFixed(1);
            	}
            });
            
            var lossLevel = '';
            $eles = form.find('input[name="loss"]');
            $eles.each(function(i,o){
            	if(i != $eles.length - 1){
            		lossLevel += Number(o.value).toFixed(1) + ',';
            	}else{
            		lossLevel += Number(o.value).toFixed(1);
            	}
            });
            	
            var scaleLevel = '';
            $eles = form.find('input[name="scale"]');
            $eles.each(function(i,o){
            	if(i != $eles.length - 1){
            		scaleLevel += Number(o.value).toFixed(1) + ',';
            	}else{
            		scaleLevel += Number(o.value).toFixed(1);
            	}
            });
            
            var losss = lossLevel.split(',');
            var sales = saleLevel.split(',');
            for (var int = 0; int < losss.length; int++) {
				if(Number(losss[int])  > Number(sales[int])){
					toastr['error']('分红条款错误！条款中销量必须大于亏损', '操作提示');
					return;
				}
			}
            
            var userLevel = '';
            $eles = form.find('input[name="validUser"]');
            $eles.each(function(i,o){
            	if(i != $eles.length - 1){
            		userLevel += Number(o.value) + ',';
            	}else{
            		userLevel += Number(o.value);
            	}
            });
            
            var params = {
                id: id,
                salesLevel: saleLevel,
                lossLevel: lossLevel,
                scaleLevel: scaleLevel,
                userLevel: userLevel
            };
            var url = './lottery-user-dividend/edit';
            isSending = true;
            $.ajax({
                type: 'post',
                url: url,
                data: params,
                dataType: 'json',
                success: function(data) {
                    isSending = false;
                    if (data.error == 0) {
                        modal.modal('hide');
                        UserDividendTable.reload();
                        toastr['success']('操作成功！', '操作提示');
                    }
                    if (data.error == 1 || data.error == 2) {
                        toastr['error']('操作失败！' + data.message, '操作提示');
                    }
                }
            });
        }

        var show = function(data) {
            if (data) {
            	$("#editView .inputLevel").remove();
                form[0].reset();
                form.find('input[name="id"]').val(data.bean.id);
                var userLevels = DataFormat.formatLevelUsers2(data.userLevels);
                form.find('[data-field="userLevels"]').html(userLevels);
//                form.find('input[name="minValidUser"]').val(data.bean.minValidUser);

                if(data.upBean){
					//TODO 阶梯显示
					var sales = data.upBean.salesLevel.split(',');//销量
					var loss = data.upBean.lossLevel.split(',');//亏损
					var scale = data.upBean.scaleLevel.split(',');//比例
					var validUser = data.upBean.userLevel.split(',');//人数
					
					var showHtml = '';
                    for(var i = 0; i < sales.length; i++){
                    	showHtml +=  '条款(' + (i + 1) + ')：销量 >= ' + Number(sales[i]).toFixed(2) + 'W；亏损 >= ' + Number(loss[i]).toFixed(2) + 'W；人数 >= '+ validUser[i] +';分红比例：' + Number(scale[i]).toFixed(2) + '%</br>';
                    }
                    form.find('[data-field="upScale"]').html(showHtml);
				}else{
					form.find('[data-field="upScale"]').html('<span class="static-placeholder-error"><i class="fa fa-warning"></i>上级招商未签署契约分红。</span>');
				}
               
                var sales = data.salesLevel.split(',');//销量
				var loss = data.lossLevel.split(',');//亏损
				var scale = data.scaleLevel.split(',');//比例
				var validUser = data.userLevel.split(',');//人数
				
				// 最小销量值(单位W元)
				minSale = data.minSales;
				// 最大销量值(单位W元)
				maxSale = data.maxSales;
				// 最小亏损值(单位W元)
				minLoss = data.minLoss;
				// 最大亏损值(单位W元)
				maxLoss = data.maxLoss;
				// 最小分红比例（%）
				minScale = data.minScale;
				// 最大分红比例（%）
				maxScale = data.maxScale;
				minUser = data.minUser;
				maxUser = data.maxUser;
				maxSignLevel = data.maxSignLevel;
				
				currentSignLevel = 1;
				//初始添加条款
 				
				var param = {
 	                isPlus: false,
 	                isDel: false,
 	                minSale: minSale,
 	                maxSale: maxSale,
 	                minLoss: minLoss,
 	                maxLoss: maxLoss,
 	                minScale: minScale,
 	                maxScale: maxScale,
 	                minUser:minUser,
	                maxUser:maxUser,
 	                maxSignLevel: maxSignLevel
 	            }
 				$("#editView .inputLevel").remove();
				 for(var i = 0; i < sales.length; i++){
					if(i > 0){
						param.isDel = true;
						currentSignLevel++;
					 }
                 	if(i == sales.length - 1){
                 		param.isPlus = true;
                 		param.isDel = true;
                 	}
                 	param.valSale = sales[i];
                 	param.valLoss = loss[i];
                 	param.valScale = scale[i];
                 	param.valUser = validUser[i];
                 	var self =  $("#editView");
    	            self.append(View.htmlView(param,self));
                 }
                
                form.find('.help-inline').empty();
                form.find('.has-error').removeClass('has-error');
                form.find('.has-success').removeClass('has-success');
                modal.modal('show');
            }
        }

        var init = function() {
            initForm();
        }

        return {
            init: init,
            show: show
        }

    } ();

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
                valUser:0
            }
            $.extend(true, deaultParam, param);

            if(currentSignLevel == maxSignLevel){
        		deaultParam.isPlus = false;
        	}
            
            var add = deaultParam.isPlus == true ? '<i class="fa fa-plus  fa-1x"   name ="add"   style = "color: #1bbc9b;"></i>': '';
            var del = deaultParam.isDel == true ? '<i class=" fa fa-times fa-1x"   name ="del"   style = "color: #FF0000;margin-left:10px"></i>': '';
            var item = $('<div class="form-group inputLevel"> ' +
					'<div class="col-md-11">' +
					'<label class="control-label col-md-1">销量:</label>' + 
					'<div class="col-md-2">' +
					'<input name="sale" class="form-control " maxLength="6" type="number" autocomplete="off" step="1" min="'+deaultParam.minSale+'" max="'+deaultParam.maxSale+'"  value="'+deaultParam.valSale+'">' + 
					'<span class="help-inline"></span>' + '</div>' +
					'<label class="control-label col-md-1">亏损:</label>' +
					'<div class="col-md-2">' + 
					'<input name="loss" class="form-control" maxLength="6" min="'+deaultParam.minLoss+'" max="'+deaultParam.maxLoss+'" type="number" autocomplete="off" step="1"  value="'+deaultParam.valLoss+'" >' +
					'<span class="help-inline"></span>' + '</div>' + 
					'<label class="control-label col-md-1">人数:</label>' +
					'<div class="col-md-2">' + 
					'<input name="validUser" class="form-control" maxLength="4" min="'+deaultParam.minUser+'" max="'+deaultParam.maxUser+'" type="number" autocomplete="off" step="1"  value="'+deaultParam.valUser+'" >' +
					'<span class="help-inline"></span>' + '</div>' + 
					'<label class="control-label col-md-1">比例:</label>' + 
					'<div class="col-md-2">' +
					'<input name="scale" class="form-control" maxLength="4" type="number" step="1" min="'+deaultParam.minScale+'" max="'+deaultParam.maxScale+'" value="'+deaultParam.valScale+'">' + 
					'<span class="help-inline"></span>' + 
					'</div></div>' +'<label class="control-label col-md-1" style="text-align: left;">' + (add + del) + '</label>' + +'</div>');

		            item.find("input[name=sale]").unbind('blur').blur(function() {
		        		var o = $(this);
		    	        var code = o.val();
		    	        if (code === '' || parseFloat(code).toString() == 'NaN') {
		    	            o.val(minSale);
		    	        }
		    	        else {
	    	                code = Number(code);
	    	                if (code < minSale) {
	    	                    o.val(minSale);
	    	                }else if(code > maxSale){
	    	                	o.val(maxSale);
	    	                }else{
	    	                	o.val(code.toFixed(1));
	    	                }
		    	        }
		        });
		        
		        item.find("input[name=scale]").unbind('blur').blur(function() {
		        		var o = $(this);
		    	        var code = o.val();
		    	        if (code === '' || parseFloat(code).toString() == 'NaN') {
		    	            o.val(minScale);
		    	        }
		    	        else {
	    	                code = Number(code);
	    	                if (code < minScale) {
	    	                    o.val(minScale);
	    	                }else if(code > maxScale){
	    	                	o.val(maxScale);
	    	                }else{
	    	                	o.val(code.toFixed(1));
	    	                }
		    	        }
		        });
		        
		        item.find("input[name=loss]").unbind('blur').blur(function() {
		        	 var o = $(this);
		        	 var code = o.val();
		             if (code === '' || parseFloat(code).toString() == 'NaN') {
		                 o.val(minLoss);
		             }
		             else {
	                     code = Number(code);
	                     if (code < minLoss) {
	                         o.val(minLoss);
	                     }else if(code > maxLoss){
	                     	o.val(maxLoss);
	                     }else{
    	                	o.val(code.toFixed(1));
    	                }
		             }
		        });
		        
		        item.find("input[name=validUser]").unbind('blur').blur(function() {
		        	 var o = $(this);
		        	 var code = o.val();
		             if (code === '' || parseInt(code).toString() == 'NaN') {
		                 o.val(minUser);
		             }
		             else {
	                     code = Number(code);
	                     if (code < minUser) {
	                         o.val(minUser);
	                     }else if(code > maxUser){
	                     	o.val(maxUser);
	                     }else{
   	                	o.val(code);
   	                }
		             }
		        });
		        
            item.find("i").click(function() {
                switch($(this).attr("name")){
                    case 'add':
                        if (currentSignLevel >= maxSignLevel) {
                            toastr['error']('操作失败！' + '只能添加' + (maxSignLevel) + '级', '操作提示');
                            return;
                        }
                        var tmsale = new Number($(this).closest(".form-group").find("input[name=sale]").val());
                        var tmloss = new Number($(this).closest(".form-group").find("input[name=loss]").val());
                        var tmscale = new Number($(this).closest(".form-group").find("input[name=scale]").val());
                        var tmuser = new Number($(this).closest(".form-group").find("input[name=validUser]").val());
                        
                        if(tmsale >= deaultParam.maxSale){
                        	toastr['error']('操作失败！' + '销量已达最大：' + (deaultParam.maxSale) + 'W', '操作提示');
                            return;
                        }
                        if(tmloss >= deaultParam.maxLoss){
                        	toastr['error']('操作失败！' + '亏损已达最大：' + (deaultParam.maxLoss) + 'W', '操作提示');
                            return;
                        }
                        if(tmscale >= deaultParam.maxScale){
                        	toastr['error']('操作失败！' + '比例已达最大：' + (deaultParam.maxScale) + '%', '操作提示');
                            return;
                        }
                        if(tmuser >= deaultParam.maxUser){
                        	toastr['error']('操作失败！' + '人数已达最大：' + (deaultParam.maxScale) + '%', '操作提示');
                            return;
                        }
                        
                        currentSignLevel++;
//                        deaultParam.minSale = tmsale + 1;
//                        deaultParam.minLoss =  tmloss + 1;
//                        deaultParam.minScale= tmscale + 0.1;
//                        deaultParam.minScale = deaultParam.minScale.toFixed(1);
                        
                        deaultParam.valSale = tmsale + 0.1;
                        deaultParam.valLoss =  tmloss + 0.1;
                        deaultParam.valScale= tmscale + 0.1;
                        deaultParam.valUser= tmuser + 1;
                        deaultParam.valSale = deaultParam.valSale.toFixed(1);
                        deaultParam.valLoss = deaultParam.valLoss.toFixed(1);
                        deaultParam.valScale = deaultParam.valScale.toFixed(1);
                        
                    	$(this).hide();
                    	$("i[name=del").hide();
                    	deaultParam.isDel = true;
                    	
                    	var html = View.htmlView(deaultParam,self);
                    	self.append(html);
                           break;
                    case 'del':
                    	currentSignLevel--;
                    	var group = $(this).closest(".form-group");
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
    
    var checkLevel = function(form){
    	 var saleLevel = '';
    	 var result = true;
         var $eles = form.find('input[name="sale"]');
         $eles.each(function(i,o){
         	if(i != $eles.length -1 ){
         		saleLevel += Number(o.value).toFixed(1) + ',';
         	}else{
         		saleLevel += Number(o.value).toFixed(1);
         	}
         });
         
         var lossLevel = '';
         $eles = form.find('input[name="loss"]');
         $eles.each(function(i,o){
         	if(i != $eles.length - 1){
         		lossLevel += Number(o.value).toFixed(1) + ',';
         	}else{
         		lossLevel += Number(o.value).toFixed(1);
         	}
         });
         	
         var scaleLevel = '';
         $eles = form.find('input[name="scale"]');
         $eles.each(function(i,o){
         	if(i != $eles.length - 1){
         		scaleLevel += Number(o.value).toFixed(1) + ',';
         	}else{
         		scaleLevel += Number(o.value).toFixed(1);
         	}
         });
         
         var userLevel = '';
         $eles = form.find('input[name="validUser"]');
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
    	
    	if(Number(arrSales[0]) < Number(minSale) || Number(arrSales[arrSales.length-1]) > Number(maxSale)){
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
    	
    	if(Number(arrLoss[0]) < Number(minLoss) || Number(arrLoss[arrLoss.length-1]) > Number(maxLoss)){
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
    	
    	if(Number(arrUser[0]) < Number(minUser) || Number(arrUser[arrUser.length-1]) > Number(maxUser)){
    		result = false;
			return false;
		}
    	for(var i = 0; i < arrUser.length; i++){
    		if (arrUser[i] === '' || parseInt(arrUser[i]).toString() == 'NaN') {
    			 result = false;
	       		 return false;
	       	 }
    		// 必须是递增条款
//			if (i>0 && Number(arrLoss[i-1]) >= Number(arrLoss[i])) {
//				result = false;
//				return false;
//			}
    	}
    	
    	if(Number(arrScale[0]) < Number(minScale) || Number(arrScale[arrScale.length-1]) > Number(maxScale)){
    		result = false;
			return false;
		}
    	for(var i = 0; i < arrScale.length; i++){
    		if (arrScale[i] === '' || parseFloat(arrScale[i]).toString() == 'NaN') {
    			result = false;
	       		 return false;
	       	 }
    		// 必须是递增条款
			if (i>0 && Number(arrScale[i-1]) >= Number(arrScale[i])) {
				result = false;
				return false;
			}
    	}
    	
    	return result;
    	
    }

    var AddUserDividendModal = function() {
        var modal = $('#modal-add');
        var form = modal.find('form');
        var $loader = $("<div class='loader'></div>");
        
		form.validate({
			rules: {
				scale: {
					required: true,
					number: true
				},
				loss: {
					required: true,
					number: true
					
				},
				sale: {
					required: true,
					number: true
				},
				validUser:{
					required: true,
					digits: true,
					min:3,
					max:1000
				}
			},
			messages: {
				sale : {
					required : '不能为空！',
					number : '请输入数值！',
					min : '最小{0}',
					max : '最大{0}',
					maxlength:'最长输入{0}字符'
				},
				loss : {
					required : '不能为空！',
					number : '请输入数值！',
					min : '最小{0}',
					max : '最大{0}'
				},
				scale : {
					required : '不能为空！',
					number : '请输入数值！',
					min : '最小{0}',
					max : '最大{0}',
					maxlength:'最长输入{0}字符'
				},
				validUser : {
					required : '不能为空！',
					digits : '必须为整数！',
					min : '最小{0}',
					max : '最大{0}',
					maxlength:'最长输入{0}字符'
				}

			},
			invalidHandler: function (event, validator) {},
			errorPlacement: function (error, element) {
				$(element).closest('div').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
			},
			highlight: function (element) {
				$(element).closest('div').removeClass('has-success').addClass('has-error');
			},
			unhighlight: function (element) {
				$(element).closest('div').removeClass('has-error').addClass('has-success');
				$(element).closest('div').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
			}
		});
		
        var initForm = function() {

			var isSendingUpCheck = false;

			var isSendingUpCheck = false;
			
			modal.find('input[name="username"]').unbind('blur').blur(function() {
				if (isSendingUpCheck == true) {
					return;
				}
				var val = $(this).val();
				if(val && val.length >= 6) {
					isSendingUpCheck = true;
					modal.find('.modal-content').append($loader);
					$.ajax({
						type : 'post',
						url : './lottery-user-dividend/add-get',
						data : {username: val},
						dataType : 'json',
						success : function(data) {
							isSendingUpCheck = false;
							if(data.error === 0) {
								var userLevels = DataFormat.formatLevelUsers2(data.data.userLevels);
								modal.find('[data-field="userLevels"]').html(userLevels);

								// 最小销量值(单位W元)
								minSale = data.data.minSales;
								// 最大销量值(单位W元)
								maxSale = data.data.maxSales;
								// 最小亏损值(单位W元)
								minLoss = data.data.minLoss;
								// 最大亏损值(单位W元)
								maxLoss = data.data.maxLoss;
								// 最小分红比例（%）
								minScale = data.data.minScale;
								// 最大分红比例（%）
								maxScale = data.data.maxScale;
								maxSignLevel = data.data.maxSignLevel;
								currentSignLevel = 1;
								minUser = data.data.minUser;
								maxUser = data.data.maxUser;
								
								//初始添加条款
								 var param = {
					                isPlus: true,
					                isDel: false,
					                minSale: minSale,
					                valSale: minSale,
					                maxSale: maxSale,
					                minLoss: minLoss,
					                valLoss: minLoss,
					                maxLoss: maxLoss,
					                minScale: minScale,
					                valScale: minScale,
					                maxScale: maxScale,
					                minUser:minUser,
					                valUser:minUser,
					                maxUser:maxUser,
					                maxSignLevel: maxSignLevel
					            }
					            var self =  $("#addView");
								 $("#addView .inputLevel").remove();
					            self.append(View.htmlView(param,self));
					            
								if(data.data.upBean){
									//TODO 阶梯显示
									var sales = data.data.upBean.salesLevel.split(',');//销量
									var loss = data.data.upBean.lossLevel.split(',');//亏损
									var scale = data.data.upBean.scaleLevel.split(',');//比例
									var validUser = data.data.upBean.userLevel.split(',');//人数
									
									var showHtml = '';
				                    for(var i = 0; i < sales.length; i++){
				                    	showHtml +=  '条款(' + (i + 1) + ')：销量 >= ' + Number(sales[i]).toFixed(2) + 'W ；亏损 >= ' + Number(loss[i]).toFixed(2) + 'W；人数 >= ' + Number(validUser[i]) + ';分红比例：' + Number(scale[i]).toFixed(2) + '%</br>';
				                    }
				                    modal.find('[data-field="upScale"]').html(showHtml);
								}else{
									modal.find('[data-field="upScale"]').html('<span class="static-placeholder-error"><i class="fa fa-warning"></i>上级招商未签署契约分红。</span>');
								}
							}
							if(data.error == 1 || data.error == 2) {
								modal.find('[data-field="userLevels"]').html('<span class="static-placeholder-error"><i class="fa fa-warning"></i>'+data.message+'</span>');
								modal.find('[data-field="upScale"]').html('<span class="static-placeholder-error"><i class="fa fa-warning"></i>'+data.message+'</span>');
								modal.find('[data-field="minScale"]').html('0');
								modal.find('[data-field="maxScale"]').html('0');
								$("#addView .inputLevel").remove();
							}
						},
						complete: function(){
							isSendingUpCheck = false;
							$loader.remove();
						}
					});

				}
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
				}
			});
        }

        var isSending = false;
        var doSubmit = function() {
            if (isSending) return;
            var username = form.find('input[name="username"]').val();
            var saleLevel = '';
            
            if(!checkLevel(form)){
            	toastr['error']('分红条款错误，请参考各项条款最大最小值填写');
            	return;
            }
            
            var $eles = form.find('input[name="sale"]');
            $eles.each(function(i,o){
            	if(i != $eles.length -1 ){
            		saleLevel += Number(o.value).toFixed(1) + ',';
            	}else{
            		saleLevel += Number(o.value).toFixed(1);
            	}
            });
            
            var lossLevel = '';
            $eles = form.find('input[name="loss"]');
            $eles.each(function(i,o){
            	if(i != $eles.length - 1){
            		lossLevel += Number(o.value).toFixed(1) + ',';
            	}else{
            		lossLevel += Number(o.value).toFixed(1);
            	}
            });
            	
            var scaleLevel = '';
            $eles = form.find('input[name="scale"]');
            $eles.each(function(i,o){
            	if(i != $eles.length - 1){
            		scaleLevel += Number(o.value).toFixed(1) + ',';
            	}else{
            		scaleLevel += Number(o.value).toFixed(1);
            	}
            });
            var losss = lossLevel.split(',');
            var sales = saleLevel.split(',');
            for (var int = 0; int < losss.length; int++) {
				if(Number(losss[int])  > Number(sales[int])){
					toastr['error']('分红条款错误！条款中销量必须大于亏损', '操作提示');
					return;
				}
			}
            
            var userLevel = '';
            $eles = form.find('input[name="validUser"]');
            $eles.each(function(i,o){
            	if(i != $eles.length - 1){
            		userLevel += Number(o.value) + ',';
            	}else{
            		userLevel += Number(o.value);
            	}
            });
            
//            var minValidUser = form.find('input[name="minValidUser"]').val();
            var status = form.find('select[name="status"]').val();
            var params = {
                username: username,
                salesLevel: saleLevel,
                lossLevel: lossLevel,
                scaleLevel: scaleLevel,
                userLevel: userLevel,
                status: status
            };
            var url = './lottery-user-dividend/add';
            isSending = true;
            $.ajax({
                type: 'post',
                url: url,
                data: params,
                dataType: 'json',
                success: function(data) {
                    isSending = false;
                    if (data.error == 0) {
                        modal.modal('hide');
                        UserDividendTable.reload();
                        toastr['success']('新增契约日结成功，将于本次结算周期生效！', '操作提示');
                    }
                    if (data.error == 1 || data.error == 2) {
                        toastr['error']('操作失败！' + data.message, '操作提示');
                    }
                }
            });
        }

        var show = function(data) {
			$("#addView .inputLevel").remove();
            form[0].reset();
            form.find('.help-inline').each(function() {
                if ($(this).attr('data-default')) {
                    $(this).html($(this).attr('data-default'));
                } else {
                    $(this).empty();
                }
            });

            modal.find('[data-field="userLevels"]').html('<span class="static-placeholder">输入用户名后显示</span>');
            modal.find('[data-field="upScale"]').html('<span class="static-placeholder">输入用户名后显示</span>');
            modal.find('[data-field="minScale"]').html('0');
            modal.find('[data-field="maxScale"]').html('0');
            form.find('.has-error').removeClass('has-error');
            form.find('.has-success').removeClass('has-success');
            modal.modal('show');
        }

        var init = function() {
        	jQuery.validator.addMethod("beforeSale", function(value, element) {
        	    var length = value.length;
        	   
        	    return this.optional(element) || (length == 11 && mobile.test(value));
        	}, "请正确填写您的手机号码");

        	
            initForm();
        }

        return {
            init: init,
            show: show
        }

    } ();

    return {
        init: function() {
            UserDividendTable.init();
            ModifyUserDividendModal.init();
            AddUserDividendModal.init();
            handelDatePicker();
        }
    }
} ();

