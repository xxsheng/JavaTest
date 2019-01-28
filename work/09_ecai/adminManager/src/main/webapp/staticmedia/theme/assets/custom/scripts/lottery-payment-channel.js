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
			var url = './lottery-payment-channel/list';
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

				var statusAction = '<a data-command="moveUp" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-arrow-up"></i></a>';
				statusAction += '<a data-command="moveDown" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-arrow-down"></i></a>';
				if (val.addMoneyType == 1) {
					// 网银可以在这里编辑
					if(val.status == -1) {
						statusAction += '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
					}
					else {
						statusAction += '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用 </a>';
					}

					statusAction += '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a>';
					statusAction += '<a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-remove"></i> 删除 </a>';
				}
				else {
					if(val.status == -1) {
						statusAction += '<a disabled="true"  href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
					}
					else {
						statusAction += '<a disabled="true"  href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用 </a>';
					}

					statusAction += '<a disabled="true" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a>';
					statusAction += '<a disabled="true" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-remove"></i> 删除 </a>';
				}
				innerHtml +=
				'<tr class="align-center" data-id="' + val.id + '">'+
					'<td>' + val.name + '</td>'+
					'<td>' + val.frontName + '</td>'+
					'<td>' + val.mobileName + '</td>'+
					'<td>' + val.merCode + '</td>'+
					'<td>' + val.minTotalRecharge + ' ~ ' + val.maxTotalRecharge + '</td>'+
					'<td>' + val.minUnitRecharge + ' ~ ' + val.maxUnitRecharge + '</td>'+
					'<td>' + DataFormat.formatLotteryPaymentChannelStatus(val.status) + '</td>'+
					'<td>' + statusAction + '</td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var url = './lottery-payment-channel/get';
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
				var msg = '确定禁用该通道？';
				if(status == -1) {
					msg = '确定启用该通道？';
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
				var msg = '确定清零该通道已用额度？';
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
				var msg = '确定删除该通道？';
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

			table.find('[data-command="moveUp"]').unbind('click').click(function(e) {
				var $tr = $(e.target).parents("tr");
				var id = $(this).parents('tr').attr('data-id');
				moveUp($tr, id);
			});

			table.find('[data-command="moveDown"]').unbind('click').click(function(e) {
				var $tr = $(e.target).parents("tr");
				var id = $(this).parents('tr').attr('data-id');
				moveDown($tr, id);
			});
		}

		var moveSending = false;
		var moveUp = function($tr, id) {
			if (moveSending) {
				toastr['error']('正在操作，请稍候！', '操作提示');
				return;
			}

			if ($tr.index() != 0) {
				var $prevTr = $tr.prev();
				if ($prevTr.length>0) {
					$tr.hide();

					moveSending = true;
					var params = {id: id};
					var url = './lottery-payment-channel/move-up';
					$.ajax({
						type : 'post',
						url : url,
						data : params,
						dataType : 'json',
						success : function(data) {
							if(data.error == 0) {
								// toastr['success']('操作成功！', '操作提示');
								$tr.insertBefore($prevTr);
								$tr.fadeIn(600);
							}
							else if(data.error == 1 || data.error == 2) {
								toastr['error']('操作失败！' + data.message, '操作提示');
							}
						},
						complete: function(){
							moveSending = false;
						}
					});
				}
			}
		}

		var moveDown = function($tr, id) {
			if (moveSending) {
				toastr['error']('正在操作，请稍候！', '操作提示');
				return;
			}
			
			var trLength = $tr.siblings().length;
			if ($tr.index() != trLength) {
				var $nextTr = $tr.next();
				if ($nextTr.length>0) {
					$tr.hide();

					moveSending = true;
					var params = {id: id};
					var url = './lottery-payment-channel/move-down';
					$.ajax({
						type : 'post',
						url : url,
						data : params,
						dataType : 'json',
						success : function(data) {
							if(data.error == 0) {
								// toastr['success']('操作成功！', '操作提示');
								$tr.insertAfter($nextTr);
								$tr.fadeIn(600);
							}
							else if(data.error == 1 || data.error == 2) {
								toastr['error']('操作失败！' + data.message, '操作提示');
							}
						},
						complete: function(){
							moveSending = false;
						}
					});
				}
			}
		}

		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './lottery-payment-channel/update-status';
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
							toastr['success']('该通道已启用！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该通道已禁用！', '操作提示');
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
						toastr['success']('已用额度成功清零！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var deleteCard = function(id) {
			var params = {id: id};
			var url = './lottery-payment-channel/delete';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						loadData();
						toastr['success']('通道删除成功！', '操作提示');
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
					},
					totalCredits: {
						required: true,
						digits: true
					},
					consumptionPercent: {
						required: true,
						number: true,
						min: 0.1,
						max: 10
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
	                },
					totalCredits: {
						required: '总额度不能为空！',
						digits: '总额度必须为整数！'
					},
					consumptionPercent: {
						required: '消费比例不能为空！',
						number: '请输入数值',
						min: '最大小0.1',
						max: '最大10'
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

			var params = {
				name: name, mobileName: mobileName, 
				frontName: frontName, totalCredits: totalCredits,
				minTotalRecharge: minTotalRecharge, maxTotalRecharge: maxTotalRecharge, 
				minUnitRecharge: minUnitRecharge, maxUnitRecharge: maxUnitRecharge, 
				maxRegisterTime: maxRegisterTime, consumptionPercent: consumptionPercent,
				whiteUsernames: whiteUsernames, startTime: startTime,
				endTime: endTime
			};
			var url = './lottery-payment-channel/add';
			if(action == 'edit') {
				var id = modal.attr('data-id');
				params.id = id;
				url = './lottery-payment-channel/edit';
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
							toastr['success']('通道添加成功！', '操作提示');
						}
						if(action == 'edit') {
							toastr['success']('通道修改成功！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						if(action == 'add') {
							toastr['error']('通道添加失败！' + data.message, '操作提示');
						}
						if(action == 'edit') {
							toastr['error']('通道修改失败！' + data.message, '操作提示');
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
			if(data) {
				$('#qrUrlCode').hide();
				$('#ImgQrUrlCode').hide();
				modal.attr('data-action', 'edit');
				modal.attr('data-id', data.data.id);
				modal.find('.modal-title').html('编辑充值通道');
				form.find('input[name="name"]').val(data.data.name);
				form.find('input[name="mobileName"]').val(data.data.mobileName);
				form.find('input[name="frontName"]').val(data.data.frontName);
				form.find('input[name="merCode"]').val(data.data.merCode);
				form.find('input[name="link"]').val(data.data.link);
				form.find('input[name="totalCredits"]').val(data.data.totalCredits);
				form.find('input[name="minTotalRecharge"]').val(data.data.minTotalRecharge);
				form.find('input[name="maxTotalRecharge"]').val(data.data.maxTotalRecharge);
				form.find('input[name="minUnitRecharge"]').val(data.data.minUnitRecharge);
				form.find('input[name="maxUnitRecharge"]').val(data.data.maxUnitRecharge);
				form.find('input[name="consumptionPercent"]').val(data.data.consumptionPercent);
				form.find('input[name="whiteUsernames"]').val(data.data.whiteUsernames);
				form.find('input[name="startTime"]').val(data.data.startTime);
				form.find('input[name="endTime"]').val(data.data.endTime);
				if (!$.isEmptyObject(data.data.maxRegisterTime)) {
					form.find('input[name="maxRegisterTime"]').val(moment(data.data.maxRegisterTime).format('YYYY-MM-DD'));
				}
				else {
					form.find('input[name="maxRegisterTime"]').val('');
				}
				if(data.data.type == 2 && (data.data.subType == 2 || data.data.subType == 4 || data.data.subType == 6)){
					if(data.data.qrUrlCode == ""){
						$('#qrUrlCodeContent').show();
						form.find('textarea[name="qrUrlCodeContent"]').val(data.data.qrUrlCode);
					}else{
						$('#ImgQrUrlCode').show();
						form.find('img[name="ImgQrUrlCode"]').attr("src",data.data.qrUrlCode);
					}
				}
				Metronic.initAjax();
			} else {
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				modal.find('.modal-title').html('新增充值通道');
				Metronic.initAjax();
			}
			modal.find('[data-command="updateQrUrlCode"]').unbind('click').click(function() {
				$('#qrUrlCodeContent').show();
				$('#ImgQrUrlCode').hide();
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
	
	return {
		init: function() {
			LotteryPaymentChannelTable.init();
			AddLotteryPaymentChannelModal.init();
			handelDatePicker();
		}
	}
}();