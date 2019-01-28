var LotteryPaymentCard = function() {
	
	var handelDatePicker = function() {
		$('.timepicker-24').timepicker({
            autoclose: true,
            minuteStep: 5,
            showSeconds: false,
            showMeridian: false
        });
	}
	
	var LotteryPaymentCardTable = function() {
		var tableList = $('#table-lottery-payment-card-list');
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './lottery-payment-card/list';
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
				var statusAction = '<a data-command="status" data-status="' + val.bean.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用 </a>';
				if(val.bean.status == -1) {
					statusAction = '<a data-command="status" data-status="' + val.bean.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
				}
				var usedCredits = Number(val.bean.usedCredits);
				var totalCredits = Number(val.bean.totalCredits);
				var credits = usedCredits >= totalCredits ? ('<span class="color-red">'+val.bean.usedCredits + '/' + val.bean.totalCredits+'</span>') : (val.bean.usedCredits + '/' + val.bean.totalCredits);
				innerHtml +=
				'<tr class="align-center" data-id="' + val.bean.id + '">'+
					'<td>' + val.bankName + '</td>'+
					'<td>' + val.bean.branchName + '</td>'+
					'<td>' + val.bean.cardName + '</td>'+
					'<td>' + val.bean.cardId + '</td>'+
					'<td>' + credits + '</td>'+
					'<td>' + val.bean.minTotalRecharge + ' ~ ' + val.bean.maxTotalRecharge + '</td>'+
					'<td>' + val.bean.minUnitRecharge + ' ~ ' + val.bean.maxUnitRecharge + '</td>'+
					'<td>' + DataFormat.formatLotteryPaymentCardStatus(val.bean.status) + '</td>'+
					'<td><a data-command="reset" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-undo"></i> 清零 </a>' + statusAction + '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 删除 </a></td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');

				bootbox.prompt({
					size: "small",
					inputType: 'password',
					title: "请输入资金密码",
					callback: function(withdrawPwd){
						if (!withdrawPwd) {
							return;
						}
						var token = $.getDisposableToken();
						withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, token);
						var url = './lottery-payment-card/get';
						var params = {id: id, withdrawPwd: withdrawPwd};
						$.ajax({
							type : 'post',
							url : url,
							data : params,
							dataType : 'json',
							success : function(data) {
								if(data.error == 0) {
									AddLotteryPaymentCardModal.show(data.data);
								}
								if(data.error == 1 || data.error == 2) {
									toastr['error']('操作失败！' + data.message, '操作提示');
								}
							}
						});
					}
				})
			});
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');

				bootbox.prompt({
					size: "small",
					inputType: 'password',
					title: "请输入资金密码",
					callback: function(withdrawPwd){
						if (!withdrawPwd) {
							return;
						}

						var msg = '确定禁用该银行卡？';
						if(status == -1) {
							msg = '确定启用该银行卡？';
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
											updateStatus(id, withdrawPwd, -1);
										}
										if(status == -1) {
											updateStatus(id, withdrawPwd, 0);
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
					}
				})
			});
			
			table.find('[data-command="reset"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');

				bootbox.prompt({
					size: "small",
					inputType: 'password',
					title: "请输入资金密码",
					callback: function(withdrawPwd){
						if (!withdrawPwd) {
							return;
						}

						var msg = '确定清零该转账银行卡已用额度？';
						bootbox.dialog({
							message: msg,
							title: '提示消息',
							buttons: {
								success: {
									label: '<i class="fa fa-check"></i> 确定',
									className: 'green-meadow',
									callback: function() {
										resetCredits(id, withdrawPwd);
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
				})
			});
			
			table.find('[data-command="delete"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');

				bootbox.prompt({
					size: "small",
					inputType: 'password',
					title: "请输入资金密码",
					callback: function(withdrawPwd){
						if (!withdrawPwd) {
							return;
						}

						var msg = '确定删除该转账银行卡？';
						bootbox.dialog({
							message: msg,
							title: '提示消息',
							buttons: {
								success: {
									label: '<i class="fa fa-check"></i> 确定',
									className: 'green-meadow',
									callback: function() {
										deleteCard(id, withdrawPwd);
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
				})
			});
		}
		
		var updateStatus = function(id, withdrawPwd, status) {
			var token = $.getDisposableToken();
			withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, token);
			var params = {id: id, withdrawPwd: withdrawPwd, status: status};
			var url = './lottery-payment-card/update-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						loadData();
						if(status == 0) {
							toastr['success']('该银行卡已启用！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该银行卡已禁用！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var resetCredits = function(id, withdrawPwd) {
			var token = $.getDisposableToken();
			withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, token);
			var params = {id: id, withdrawPwd: withdrawPwd};
			var url = './lottery-payment-card/reset-credits';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						loadData();
						toastr['success']('银行卡已成功清零！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var deleteCard = function(id, withdrawPwd) {
			var token = $.getDisposableToken();
			withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, token);
			var params = {id: id, withdrawPwd: withdrawPwd};
			var url = './lottery-payment-card/delete';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						loadData();
						toastr['success']('银行卡删除成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		tableList.find('[data-command="add"]').unbind('click').click(function() {
			AddLotteryPaymentCardModal.show();
		});
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var AddLotteryPaymentCardModal = function() {
		var modal = $('#modal-lottery-payment-card-add');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					cardName: {
						required: true
					},
					branchName: {
						required: true
					},
					cardId: {
						required: true
					},
					totalCredits: {
						required: true,
						number: true,
						min: 0
					},
					minTotalRecharge: {
						required: true,
						number: true,
						min: 0
					},
					maxTotalRecharge: {
						required: true,
						number: true,
						min: 0
					},
					withdrawPwd: {
						required: true
					}
				},
				messages: {
					cardName: {
	                    required: '姓名不能为空！'
	                },
					branchName: {
	                    required: '支行名称不能为空！'
	                },
	                cardId: {
	                    required: '银行卡不能为空！'
	                },
	                totalCredits: {
						required: '额度不能为空！',
						number: '额度必须为数字！',
						min: '额度不能小于0！'
					},
					withdrawPwd: {
						required: '请输入资金密码！'
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
			var bankId = modal.find('select[name="bank"]').val();
			var branchName = modal.find('input[name="branchName"]').val();
			var cardName = modal.find('input[name="cardName"]').val();
			var cardId = modal.find('input[name="cardId"]').val();
			var totalCredits = modal.find('input[name="totalCredits"]').val();
			var minTotalRecharge = modal.find('input[name="minTotalRecharge"]').val();
			var maxTotalRecharge = modal.find('input[name="maxTotalRecharge"]').val();
			var sTime = modal.find('input[name="sTime"]').val();
			var eTime = modal.find('input[name="eTime"]').val();
			var minUnitRecharge = modal.find('input[name="minUnitRecharge"]').val();
			var maxUnitRecharge = modal.find('input[name="maxUnitRecharge"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();
			isSending = true;

			var token = $.getDisposableToken();
			withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, token);

			var params = {bankId: bankId, branchName: branchName, cardName: cardName, cardId: cardId, totalCredits: totalCredits,
				minTotalRecharge: minTotalRecharge, maxTotalRecharge: maxTotalRecharge, sTime: sTime, eTime: eTime,
				minUnitRecharge: minUnitRecharge, maxUnitRecharge: maxUnitRecharge,
				withdrawPwd: withdrawPwd};
			var url = './lottery-payment-card/add';
			if(action == 'edit') {
				var id = modal.attr('data-id');
				params.id = id;
				url = './lottery-payment-card/edit';
			}

			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						modal.modal('hide');
						LotteryPaymentCardTable.init();
						if(action == 'add') {
							toastr['success']('银行卡添加成功！', '操作提示');
						}
						if(action == 'edit') {
							toastr['success']('银行卡修改成功！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						if(action == 'add') {
							toastr['error']('银行卡添加失败！' + data.message, '操作提示');
						}
						if(action == 'edit') {
							toastr['error']('银行卡修改失败！' + data.message, '操作提示');
						}
					}
				}
			});
		}
		
		var loadBank = function() {
			var url = './lottery-payment-bank/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					buildBank(data);
				}
			});
		}
		
		var buildBank = function(data) {
			var bank = form.find('select[name="bank"]');
			bank.empty();
			$.each(data, function(idx, val) {
				bank.append('<option value="' + val.id + '">' + val.name + '</option>');
			});
		}
		
		var show = function(data) {
			form[0].reset();
			if(data) {
				modal.attr('data-action', 'edit');
				modal.attr('data-id', data.id);
				modal.find('.modal-title').html('编辑转账银行卡');
				form.find('select[name="bank"]').find('option[value="' + data.bankId + '"]').attr('selected', true);
				form.find('input[name="branchName"]').val(data.branchName);
				form.find('input[name="cardName"]').val(data.cardName);
				form.find('input[name="cardId"]').val(data.cardId);
				form.find('input[name="totalCredits"]').val(data.totalCredits);
				form.find('input[name="minTotalRecharge"]').val(data.minTotalRecharge);
				form.find('input[name="maxTotalRecharge"]').val(data.maxTotalRecharge);
				form.find('input[name="sTime"]').val(data.startTime);
				form.find('input[name="eTime"]').val(data.endTime);
				form.find('input[name="minUnitRecharge"]').val(data.minUnitRecharge);
				form.find('input[name="maxUnitRecharge"]').val(data.maxUnitRecharge);
				Metronic.initAjax();
			} else {
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				modal.find('.modal-title').html('新增转账银行卡');
				form.find('select[name="bank"]').find('option:eq(0)').attr('selected', true);
				Metronic.initAjax();
			}
			form.find('input[name="withdrawPwd"]').val('');
			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}
		
		var init = function() {
			initForm();
			loadBank();
		}
		
		return {
			init: init,
			show: show
		}
		
	}();
	
	return {
		init: function() {
			LotteryPaymentCardTable.init();
			AddLotteryPaymentCardModal.init();
			handelDatePicker();
		}
	}
}();