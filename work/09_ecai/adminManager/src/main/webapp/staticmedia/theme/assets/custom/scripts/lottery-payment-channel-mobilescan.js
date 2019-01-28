var LotteryPaymentChannel = function() {

	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			clearBtn: true
		});
	}
	
	var LotteryPaymentChannelTable = function() {
		var tableList = $('#table-lottery-payment-channel-list');
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './lottery-payment-channel-mobilescan/list';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					buildData(data.data);
				}
			});
		}
		
		var buildData = function(data) {
			var table = tableList.find('table > tbody').empty();
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var statusAction = '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用 </a>';
				if(val.status == -1) {
					statusAction = '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
				}
				statusAction += '<a data-command="reset" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-refresh"></i> 清零 </a>';
				var usedCredits = Number(val.usedCredits);
				var totalCredits = Number(val.totalCredits);
				var credits = usedCredits >= totalCredits ? ('<span class="color-red">'+val.usedCredits + '/' + val.totalCredits+'</span>') : (val.usedCredits + '/' + val.totalCredits);
				innerHtml +=
				'<tr class="align-center" data-id="' + val.id + '">'+
					'<td>' + val.name + '</td>'+
					'<td>' + val.frontName + '</td>'+
					'<td>' + val.mobileName + '</td>'+
					'<td>' + val.minTotalRecharge + ' ~ ' + val.maxTotalRecharge + '</td>'+
					'<td>' + val.minUnitRecharge + ' ~ ' + val.maxUnitRecharge + '</td>'+
					'<td>' + credits + '</td>'+
					'<td>' + DataFormat.formatLotteryPaymentChannelStatus(val.status) + '</td>'+
					'<td>' + statusAction + '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-remove"></i> 删除 </a></td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var url = './lottery-payment-channel-mobilescan/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						AddLotteryPaymentChannelModal.show(data);
					}
				});
			});
			
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定禁用该账号？';
				if(status == -1) {
					msg = '确定启用该账号？';
				}
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								if(status == 0) {
									updateStatus(id, -1);
								}
								if(status == -1) {
									updateStatus(id, 0);
								}
							}
						},
						danger: {
							label: '<i class="fa fa-undo"></i> 取消',
							className: 'btn-danger',
							callback: function() {}
						}
					}
				});
			});
			
			table.find('[data-command="reset"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var msg = '确定清零该账号已用额度？';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								resetCredits(id);
							}
						},
						danger: {
							label: '<i class="fa fa-undo"></i> 取消',
							className: 'btn-danger',
							callback: function() {}
						}
					}
				});
			});
			
			table.find('[data-command="delete"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var msg = '确定删除该账号？';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								deleteCard(id);
							}
						},
						danger: {
							label: '<i class="fa fa-undo"></i> 取消',
							className: 'btn-danger',
							callback: function() {}
						}
					}
				});
			});
		}

		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './lottery-payment-channel-mobilescan/update-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						loadData();
						if(status == 0) {
							toastr['success']('该账号已启用！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该账号已禁用！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var resetCredits = function(id) {
			var params = {id: id};
			var url = './lottery-payment-channel/reset-credits';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						loadData();
						toastr['success']('已用额度已成功清零！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var deleteCard = function(id) {
			var params = {id: id};
			var url = './lottery-payment-channel-mobilescan/delete';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						loadData();
						toastr['success']('账号删除成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		tableList.find('[data-command="add"]').unbind('click').click(function() {
			AddLotteryPaymentChannelModal.show();
		});
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var AddLotteryPaymentChannelModal = function() {
		var modal = $('#modal-lottery-payment-channel-add');
		var form = modal.find('form');
		var modalQr = $('#modal-lottery-payment-channel-add-qr');
		var formQr = modalQr.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					name: {
						required: true
					},
					mobileName: {
						required: true
					},
					frontName: {
						required: true
					}
				},
				messages: {
					name: {
	                    required: '后台名称不能为空！'
	                },
	                mobileName: {
	                    required: '手机端名称不能为空！'
	                },
	                frontName: {
	                    required: '前端名称不能为空！'
	                }
	            },
	            invalidHandler: function (event, validator) {},
	            errorPlacement: function (error, element) {
	            	$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
                },
                highlight: function (element) {
                    $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
                },
                unhighlight: function (element) {
                    $(element).closest('.form-group').removeClass('has-error').addClass('has-success');
                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
			
			modal.find('input:radio[name="fixedQRAmount"]').unbind('change').click(function() {
				 var fixedAmountType= $(this).val();
				 if(fixedAmountType == 0){
					 $("#qrCodeContent").show();
					 $("#fixedAmountButton").hide();
				 }else{
					 $("#qrCodeContent").hide();
					 $("#ImgQrUrlCode").hide();
					 $("#fixedAmountButton").show();
				 }
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var action = modal.attr('data-action');
			var name = modal.find('input[name="name"]').val();
			var mobileName = modal.find('input[name="mobileName"]').val();
			var frontName = modal.find('input[name="frontName"]').val();
			var totalCredits = modal.find('input[name="totalCredits"]').val();
			var minTotalRecharge = modal.find('input[name="minTotalRecharge"]').val();
			var maxTotalRecharge = modal.find('input[name="maxTotalRecharge"]').val();
			var minUnitRecharge = modal.find('input[name="minUnitRecharge"]').val();
			var maxUnitRecharge = modal.find('input[name="maxUnitRecharge"]').val();
			var maxRegisterTime = modal.find('input[name="maxRegisterTime"]').val();
			var consumptionPercent = modal.find('input[name="consumptionPercent"]').val();
			var whiteUsernames = modal.find('input[name="whiteUsernames"]').val();
			var startTime = modal.find('input[name="startTime"]').val();
			var endTime = modal.find('input[name="endTime"]').val();
			var qrCodeContent = modal.find('textarea[name="qrCodeContent"]').val();
			var fixedQRAmount = modal.find('input[name="fixedQRAmount"]:checked').val();
			var type = modal.find('select[name="type"] option:selected').attr("data-type");
			var subType = modal.find('select[name="type"] option:selected').attr("data-subType");
			var channelCode = modal.find('select[name="type"] option:selected').val();
			//var fixedAmountQr="";
			//组装固定金额二维码
        	var fixedAmountQrs=[];
        	var channelId = "";
			var url = './lottery-payment-channel-mobilescan/add';
			if(action == 'edit') {
				channelId = modal.attr('data-id');
				url = './lottery-payment-channel-mobilescan/edit';
				
				if(fixedQRAmount == 1){//固定金额二维码
					var isSuccess=true;
					var modalQr = $('#modal-lottery-payment-channel-edit-qr');
					var fixedAmountQr = modalQr.find("tbody tr");
					  $(fixedAmountQr).each(function(){
						  	var id = $(this).attr("data-id");
						    var qrCodeContent = $(this).find('textarea[name="qrCodeContent"]').val();
						    var amount = $(this).find('input[name="amount"]').val();
						    if(id =="" && (qrCodeContent == "" || amount =="")){
						    	isSuccess=false;
						    	return false;
						    }
						    var arr ={
						    		"id" : id,
						    		"qrCodeContent":qrCodeContent,
						    		"amount":amount
						    }
						    fixedAmountQrs.push(arr);
					  });
					  if(!isSuccess){
						  toastr['error']('您有固定金额二维码没填写完整，请填写完整后确认！', '操作提示');
						  return;
					  }
				}
				
			}else{
				if(fixedQRAmount == 1){//固定金额二维码
					var isSuccess=true;
					var modalQr = $('#modal-lottery-payment-channel-add-qr');
					var fixedAmountQr = modalQr.find("tbody tr");
					  $(fixedAmountQr).each(function(){
						    var qrCodeContent = $(this).find('textarea[name="qrCodeContent"]').val();
						    var amount = $(this).find('input[name="amount"]').val();
						    if(qrCodeContent == "" || amount ==""){
						    	isSuccess=false;
						    	return false;
						    }
						    var arr ={
						    		"qrCodeContent":qrCodeContent,
						    		"amount":amount
						    }
						    fixedAmountQrs.push(arr);
					  });
					  if(!isSuccess){
						  toastr['error']('您有固定金额二维码没填写完整，请填写完整后确认！', '操作提示');
						  return;
					  }
				}
			}
			
			var params = {
				name: name, mobileName:mobileName, 
				frontName:frontName, channelCode: channelCode,
				channelCode: channelCode, totalCredits: totalCredits,
				minTotalRecharge: minTotalRecharge, maxTotalRecharge: maxTotalRecharge,
				minUnitRecharge: minUnitRecharge, maxUnitRecharge: maxUnitRecharge,
				maxRegisterTime: maxRegisterTime, qrCodeContent: qrCodeContent,
				fixedQRAmount: fixedQRAmount, type: type,
				subType: subType, consumptionPercent: consumptionPercent,
				whiteUsernames: whiteUsernames, startTime: startTime,
				endTime: endTime, fixedAmountQrs: JSON.stringify(fixedAmountQrs)};
			if(channelId){
				params.id = channelId;
			}
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						modal.modal('hide');
						LotteryPaymentChannelTable.init();
						if(action == 'add') {
							toastr['success']('账号添加成功！', '操作提示');
						}
						if(action == 'edit') {
							toastr['success']('账号修改成功！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						if(action == 'add') {
							toastr['error']('账号添加失败！' + data.message, '操作提示');
						}
						if(action == 'edit') {
							toastr['error']('账号修改失败！' + data.message, '操作提示');
						}
					}
				},
				complete: function() {
					isSending = false;
					modal.modal('hide');
				},
				error: function() {
					isSending = false;
					modal.modal('hide');
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
				}
			});
		}
		
		var show = function(data) {
			form[0].reset();
			formQr[0].reset();
			if(data) {
				$('#qrCodeContent').hide();
				$('#ImgQrUrlCode').hide();
				$("#fixedAmountButton").hide();
				modal.attr('data-action', 'edit');
				modal.attr('data-id', data.data.id);
				modal.find('.modal-title').html('编辑账号');
				form.find('select[name="type"]').find('option[value="' + data.data.channelCode + '"]').attr('selected', true);
				form.find('input[name="name"]').val(data.data.name);
				form.find('input[name="mobileName"]').val(data.data.mobileName);
				form.find('input[name="frontName"]').val(data.data.frontName);
				form.find('input[name="totalCredits"]').val(data.data.totalCredits);
				form.find('input[name="minTotalRecharge"]').val(data.data.minTotalRecharge);
				form.find('input[name="maxTotalRecharge"]').val(data.data.maxTotalRecharge);
				form.find('input[name="minUnitRecharge"]').val(data.data.minUnitRecharge);
				form.find('input[name="maxUnitRecharge"]').val(data.data.maxUnitRecharge);
				form.find('input[name="maxRegisterTime"]').val(data.data.maxRegisterTime);
				form.find('input[name="consumptionPercent"]').val(data.data.consumptionPercent);
				form.find('input[name="whiteUsernames"]').val(data.data.whiteUsernames);
				form.find('input[name="startTime"]').val(data.data.startTime);
				form.find('input[name="endTime"]').val(data.data.endTime);
				
				form.find('[data-command="fixedAmountQr"]').html("编辑固定金额二维码");
				if (!$.isEmptyObject(data.data.maxRegisterTime)) {
					form.find('input[name="maxRegisterTime"]').val(moment(data.data.maxRegisterTime).format('YYYY-MM-DD'));
				}
				else {
					form.find('input[name="maxRegisterTime"]').val('');
				}
				if(data.data.fixedQRAmount == 0){
					if(data.qrUrlCode == ""){
						$('#qrCodeContent').show();
						form.find('textarea[name="qrCodeContent"]').val(data.data.qrUrlCode);
					}else{
						$('#ImgQrUrlCode').show();
						form.find('img[name="ImgQrUrlCode"]').attr("src",data.data.qrUrlCode);
					}
				}
				if(data.data.fixedQRAmount == 1){
					$("#fixedAmountButton").show();
				}
				form.find('input[name="fixedQRAmount"][value="' + data.data.fixedQRAmount + '"]').attr('checked','true');
				editLotteryPaymentChannelModalQr.initdata(data);
				Metronic.initAjax();
			} else {
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				modal.find('.modal-title').html('新增充值通道账号');
				form.find('select[name="type"]').find('option:eq(0)').attr('selected', true);
				$("#qrCodeContent").css('display','block');
				$("#ImgQrUrlCode").hide();
				$("#qrCodeContent").val("");
				modalQr.find("tbody tr").remove(".qrdel");
				form.find('[data-command="fixedAmountQr"]').html("添加固定金额二维码");
				$("#fixedAmountButton").hide();
				Metronic.initAjax();
			}
			modal.find('[data-command="updateQrUrlCode"]').unbind('click').click(function() {
				$('#qrCodeContent').show();
				$('#ImgQrUrlCode').hide();
			});
			modal.find('[data-command="fixedAmountQr"]').unbind('click').click(function() {
				var dataAction = modal.attr('data-action');
				if(dataAction == "add"){
					AddLotteryPaymentChannelModalQr.show();
				}else{
					editLotteryPaymentChannelModalQr.show();
				}
				
			});
			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}
		
		var init = function() {
			initForm();
		}
		
		return {
			init: init,
			show: show
		}
		
	}();
	
	var AddLotteryPaymentChannelModalQr = function() {
		var modal = $('#modal-lottery-payment-channel-add-qr');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					qrCodeContent: {
						required: true
					},
					amount: {
						required: true
					}
				},
				messages: {
					qrCodeContent: {
	                    required: '二维码不能为空！'
	                },
	                amount: {
	                    required: '固定金额不能为空！'
	                }
	            },
	            invalidHandler: function (event, validator) {},
	            errorPlacement: function (error, element) {
	            	$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
                },
                highlight: function (element) {
                    $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
                },
                unhighlight: function (element) {
                    $(element).closest('.form-group').removeClass('has-error').addClass('has-success');
                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					modal.modal('hide');
		    	}else{
		    		toastr['error']('您有值没填写完整，请填写完整后确认！', '操作提示');
		    	}
			});
			
			modal.find('[data-command="add"]').unbind('click').click(function() {
				
				var trLength=modal.find("tbody tr").length+1;
				if(trLength>10){
					return;
				}
				var html= '<tr class="align-center qrdel">'
					   		+  '<td>'
					   			+  '<textarea rows="1" name="qrCodeContent" cols="180"  class="form-control input-inline" style="width: 500px"></textarea>'
					   			+  '<span class="help-inline"></span>'
					   		+  '</td>'
					   		+  '<td>'
					   			+  '<input name="amount" class="form-control input-inline input-medium" type="text" value="0"/>'
					   			+  '<span class="help-inline"></span>'
					   		+  '</td>'
					   		+  '<td>'
					   			+  '<a data-command="delete" href="javascript:;" class="btn default btn-xs black">'
					   				+  '<i class="fa fa-remove"></i> 删除 '
					   			+  '</a>'
					   		+  '</td>'
					    +  '</tr>'
				$(this).parents('tr').after(html);
				modal.find('[data-command="delete"]').unbind('click').click(function() {
					$(this).parents('tr').remove();
				});
			});
			
		}
		var isSending = false;
		var doSubmit = function() {
			
		}
		
		var show = function() {
			modal.modal('show');
		}
		
		var init = function() {
			initForm();
		}
		
		return {
			init: init,
			show: show
		}
		
	}();
	
	//编辑固定金额二维码
	var editLotteryPaymentChannelModalQr = function() {
		var modal = $('#modal-lottery-payment-channel-edit-qr');
		var form = modal.find('form');
		var initForm = function() {
			
			
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					modal.modal('hide');
		    	}else{
		    		toastr['error']('您有值没填写完整，请填写完整后确认！', '操作提示');
		    	}
			});
		}
		
		var initData = function(data){
			modal.find("tbody").empty();
			if(data.data.fixedAmountType == 0 || data.qrList.length ==0){
				var trLength=modal.find("tbody tr").length+1;
				if(trLength>10){
					return;
				}
				var html= '<tr class="align-center qrdel" data-id="">'
					   		+  '<td>'
					   			+  '<textarea rows="1" name="qrCodeContent" cols="180"  class="form-control input-inline" style="width: 500px"></textarea>'
					   			+  '<span class="help-inline"></span>'
					   		+  '</td>'
					   		+  '<td>'
					   			+  '<input name="amount" class="form-control input-inline input-medium" type="text" value="0"/>'
					   			+  '<span class="help-inline"></span>'
					   		+  '</td>'
					   		+  '<td>'
					   			+  '<a data-command="add" href="javascript:;" class="btn default btn-xs black">'
					   				+  '<i class="fa fa-remove"></i> 添加 '
					   			+  '</a>'
					   		+  '</td>'
					    +  '</tr>'
				modal.find("tbody").append(html);
			} else {
				for (var int = 0; int < data.qrList.length; int++) {
					var dataCommandHtml = '<a data-command="add" data-status="0" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 添加</a>'
					if (int != 0) {
						dataCommandHtml = '<a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-remove"></i> 删除  </a>';
					}
					var html = '<tr class="align-center" data-id='+data.qrList[int].id+'>'
						  +'<td>'
							  +'<div class="form-group" style="display: block;">'
							  	+'<img name="ImgQrUrlCode" src="'+data.qrList[int].qrUrlCode+'">'
							  	+'<button type="button" data-command="updateQrUrlCode" class="btn green-meadow">修改二维码</button>'
							  +'</div>'
							  +'<div class="" style="display: none;">'
							  	+'<button type="button" data-command="QrUrlCodeShow" class="btn green-meadow">显示二维码</button>'
							  	+'<textarea rows="1" name="qrCodeContent" cols="180"  class="form-control input-inline" style="width: 583px;"></textarea>'
							  	+'<span class="help-inline"></span>'
							  +'</div>'
						  +'</td>'
						  +'<td>'
							  +'<input name="amount" class="form-control input-inline input-medium" type="text" value="'+data.qrList[int].money+'"/>'
							  +'<span class="help-inline"></span>'
						  +'</td>'
						  +'<td>'+dataCommandHtml+'</td>'
					  +'</tr>'
					  modal.find("tbody").append(html);
				}
			}
			
			modal.find('[data-command="add"]').unbind('click').click(function() {
				var trLength=modal.find("tbody tr").length+1;
				if(trLength>10){
					return;
				}
				var html= '<tr class="align-center qrdel" data-id="">'
				   		+  '<td>'
				   			+  '<textarea rows="1" name="qrCodeContent" cols="180"  class="form-control input-inline" style="width: 500px"></textarea>'
				   			+  '<span class="help-inline"></span>'
				   		+  '</td>'
				   		+  '<td>'
				   			+  '<input name="amount" class="form-control input-inline input-medium" type="text" value="0"/>'
				   			+  '<span class="help-inline"></span>'
				   		+  '</td>'
				   		+  '<td>'
				   			+  '<a data-command="delete" href="javascript:;" class="btn default btn-xs black">'
				   				+  '<i class="fa fa-remove"></i> 删除 '
				   			+  '</a>'
				   		+  '</td>'
				    +  '</tr>'
				    modal.find("tbody").append(html);
				modal.find('[data-command="delete"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr("data-id");
					var $this =$(this);
					if(id !=""){
						var id = $(this).parents('tr').attr('data-id');
						var msg = '确定删除该固定二维码吗？';
						bootbox.dialog({
							message: msg,
							title: '提示消息',
							buttons: {
								success: {
									label: '<i class="fa fa-check"></i> 确定',
									className: 'green-meadow',
									callback: function() {
										$this.parents('tr').remove();
										deleteQrCode(id);
									}
								},
								danger: {
									label: '<i class="fa fa-undo"></i> 取消',
									className: 'btn-danger',
									callback: function() {}
								}
							}
						});
					}else{
						$(this).parents('tr').remove();
					}
				});
			});
			
			modal.find('[data-command="updateQrUrlCode"]').unbind('click').click(function() {
				$(this).parent('div').next().show();
				$(this).parent('div').hide();
				
			});
			modal.find('[data-command="QrUrlCodeShow"]').unbind('click').click(function() {
				$(this).parent('div').prev().show();
				$(this).parent('div').hide();
			});	
			modal.find('[data-command="delete"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr("data-id");
				var $this=$(this);
				if(id !=""){
					var id = $(this).parents('tr').attr('data-id');
					var msg = '确定删除该固定二维码吗？';
					bootbox.dialog({
						message: msg,
						title: '提示消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									$this.parents('tr').remove();
									deleteQrCode(id);
								}
							},
							danger: {
								label: '<i class="fa fa-undo"></i> 取消',
								className: 'btn-danger',
								callback: function() {}
							}
						}
					});
				}else{
					$(this).parents('tr').remove();
				}
			});
		}
		
		//删除固定金额二维码
		var deleteQrCode = function(id) {
			var params = {id: id};
			var url = './lottery-payment-channel-mobilescan-qr-code/delete';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						toastr['success']('固定金额二维码删除成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		var isSending = false;
		var doSubmit = function() {
		}
		var show = function() {
//			modal.find("tbody").empty();
//			if(data.fixedAmountType == 0){
//				var trLength=modal.find("tbody tr").length+1;
//				if(trLength>10){
//					return;
//				}
//				var html= '<tr class="align-center qrdel">'
//					   		+  '<td>'
//					   			+  '<textarea rows="1" name="qrUrlCode" cols="180"  class="form-control input-inline" style="width: 500px"></textarea>'
//					   			+  '<span class="help-inline"></span>'
//					   		+  '</td>'
//					   		+  '<td>'
//					   			+  '<input name="amount" class="form-control input-inline input-medium" type="text" value="0"/>'
//					   			+  '<span class="help-inline"></span>'
//					   		+  '</td>'
//					   		+  '<td>'
//					   			+  '<a data-command="add" href="javascript:;" class="btn default btn-xs black">'
//					   				+  '<i class="fa fa-remove"></i> 添加 '
//					   			+  '</a>'
//					   		+  '</td>'
//					    +  '</tr>'
//				modal.find("tbody").append(html);
//			} else {
//				alert(data.qrList.length);
//				for (var int = 0; int < data.qrList.length; int++) {
//					var dataCommandHtml = '<a data-command="add" data-status="0" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 添加</a>'
//					if (int != 0) {
//						dataCommandHtml = '<a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-remove"></i> 删除  </a>';
//					}
//					var html = '<tr class="align-center">'
//						  +'<td>'
//							  +'<div class="form-group" style="display: block;">'
//							  	+'<img name="ImgQrUrlCode" src="'+data.qrList[int].qrUrlCode+'">'
//							  	+'<button type="button" data-command="updateQrUrlCode" class="btn green-meadow">修改二维码</button>'
//							  +'</div>'
//							  +'<div class="" style="display: none;">'
//							  	+'<button type="button" data-command="QrUrlCodeShow" class="btn green-meadow">显示二维码</button>'
//							  	+'<textarea rows="1" name="qrUrlCode" cols="180"  class="form-control input-inline" style="width: 583px;"></textarea>'
//							  	+'<span class="help-inline"></span>'
//							  +'</div>'
//						  +'</td>'
//						  +'<td>'
//							  +'<input name="amount" class="form-control input-inline input-medium" type="text" value="'+data.qrList[int].money+'"/>'
//							  +'<span class="help-inline"></span>'
//						  +'</td>'
//						  +'<td>'+dataCommandHtml+'</td>'
//					  +'</tr>'
//					  modal.find("tbody").append(html);
//				}
//			}
//			modal.find('[data-command="add"]').unbind('click').click(function() {
//				var trLength=modal.find("tbody tr").length+1;
//				if(trLength>10){
//					return;
//				}
//				var html= '<tr class="align-center qrdel">'
//				   		+  '<td>'
//				   			+  '<textarea rows="1" name="qrUrlCode" cols="180"  class="form-control input-inline" style="width: 500px"></textarea>'
//				   			+  '<span class="help-inline"></span>'
//				   		+  '</td>'
//				   		+  '<td>'
//				   			+  '<input name="amount" class="form-control input-inline input-medium" type="text" value="0"/>'
//				   			+  '<span class="help-inline"></span>'
//				   		+  '</td>'
//				   		+  '<td>'
//				   			+  '<a data-command="delete" href="javascript:;" class="btn default btn-xs black">'
//				   				+  '<i class="fa fa-remove"></i> 删除 '
//				   			+  '</a>'
//				   		+  '</td>'
//				    +  '</tr>'
//				    modal.find("tbody").append(html);
//				modal.find('[data-command="delete"]').unbind('click').click(function() {
//					$(this).parents('tr').remove();
//				});
//			});
//			
//			modal.find('[data-command="updateQrUrlCode"]').unbind('click').click(function() {
//				$(this).parent('div').next().show();
//				$(this).parent('div').hide();
//				
//			});
//			modal.find('[data-command="QrUrlCodeShow"]').unbind('click').click(function() {
//				$(this).parent('div').prev().show();
//				$(this).parent('div').hide();
//			});	
			
			modal.modal('show');
		}
		
		var init = function() {
			initForm();
		}
		
		return {
			init: init,
			show: show,
			initdata:initData
			
		}
		
	}();
	
	return {
		init: function() {
			LotteryPaymentChannelTable.init();
			AddLotteryPaymentChannelModal.init();
			AddLotteryPaymentChannelModalQr.init();
			editLotteryPaymentChannelModalQr.init();
			handelDatePicker();
		}
	}
}();