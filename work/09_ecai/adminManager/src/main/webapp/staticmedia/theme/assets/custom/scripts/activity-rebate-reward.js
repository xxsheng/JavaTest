var ActivityRebateReward = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var updateDatePicker = function() {
		$('.date-picker').datepicker('update');
	}
	
	var ActivityRebateRewardTable = function() {
		
		var loadData = function(type) {
			var url = './activity-rebate-reward/list';
			var params = {type: type};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						buildData(type, data.data);
					}
					if(data.error == 1 || data.error == 2) {
						bootbox.dialog({
							message: data.message,
							title: '提示消息',
							buttons: {
								success: {
									label: '<i class="fa fa-check"></i> 确定',
									className: 'btn-success',
									callback: function() {}
								}
							}
						});
					}
				}
			});
		}
		
		var buildData = function(type, data) {
			var thisPanel = $('#activity-rebate-reward-' + type + '-config');
			thisPanel.attr('data-id', data.id);
			// 绑定内容
			thisPanel.find('table > tbody').empty();
			$.each(eval(data.rules), function(idx, val) {
				thisPanel.find('table > tbody').append('<tr class="align-center"><td>' + val.money + '</td><td>' + val.rewardUp1 + '</td><td>' + val.rewardUp2 + '</td></tr>');
			});
			// 绑定事件
			thisPanel.find('[data-command="edit"]').unbind('click').click(function() {
				var id = thisPanel.attr('data-id');
				var url = './activity-rebate-reward/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						EditActivityRebateRewardModal.show(data);
					}
				});
			});
			if(data.status == 0) {
				thisPanel.find('[data-command="status"]').html('<i class="fa fa-ban"></i> 暂停</a>');
				thisPanel.find('[data-command="status"]').attr('data-status', data.status);
			}
			if(data.status == -1) {
				thisPanel.find('[data-command="status"]').html('<i class="fa fa-check"></i> 启用</a>');
				thisPanel.find('[data-command="status"]').attr('data-status', data.status);
			}
			thisPanel.find('[data-command="status"]').unbind('click').click(function() {
				var id = thisPanel.attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定暂停该活动？';
				if(status == -1) {
					msg = '确定启用该活动？';
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
		}
		
		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './activity-rebate-reward/update-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						init();
						if(status == 0) {
							toastr['success']('已启用发放佣金！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('已暂停发放佣金！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var init = function() {
			loadData('xf');
			loadData('yk');
		}
		
		return {
			init: init
		}
	}();
	
	var EditActivityRebateRewardModal = function() {
		var modal = $('#modal-activity-rebate-reward-edit');
		var form = modal.find('form');
		var tableRules = form.find('table[data-table="rules"]');
		var initForm = function() {
			tableRules.find('[data-command="add"]').unbind('click').click(function() {
				var innerHtml = 
				'<tr>'+
					'<td><input type="text" class="form-control input-sm"></td>'+
					'<td><input type="text" class="form-control input-sm"></td>'+
					'<td><input type="text" class="form-control input-sm"></td>'+
					'<td><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i> 删除</a></td>'+
				'</tr>';
				tableRules.find('tbody').append(innerHtml);
				initTableRules();
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				doSubmit();
			});
		}
		
		var getRules = function() {
			var rules = [];
			tableRules.find('tbody > tr').each(function() {
				var money = $(this).find('input').eq(0).val();
				var rewardUp1 = $(this).find('input').eq(1).val();
				var rewardUp2 = $(this).find('input').eq(2).val();
				if(money != '') {
					money = parseFloat(money);
					rewardUp1 = parseFloat(rewardUp1);
					rewardUp2 = parseFloat(rewardUp2);
					var reward = {money: money, rewardUp1: rewardUp1, rewardUp2: rewardUp2};
					rules.push(reward);
				}
			});
			return $.toJSON(rules);
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var id = modal.attr('data-id');
			var rules = getRules();
			var params = {id: id, rules: rules};
			var url = './activity-rebate-reward/edit';
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
						ActivityRebateRewardTable.init();
						toastr['success']('佣金活动修改成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('佣金活动修改失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var show = function(data) {
			form[0].reset();
			if(data) {
				modal.attr('data-id', data.id);
				var rules = eval(data.rules);
				resetTableData(rules);
				if(data.type == 1) {
					modal.find('[data-field="reward-type"]').html('会员消费');
				}
				if(data.type == 2) {
					modal.find('[data-field="reward-type"]').html('会员盈亏');
				}
			}
			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}
		
		var initTableRules = function() {
			tableRules.find('[data-command="delete"]').unbind('click').click(function() {
				if(tableRules.find('tbody > tr').length == 1) return;
				$(this).parents('tr').remove();
			});
		}
		
		var resetTableData = function(rules) {
			tableRules.find('tbody').empty();
			if(rules) {
				$.each(rules, function(idx, val) {
					var innerHtml = 
					'<tr>'+
						'<td><input type="text" class="form-control input-sm" value="' + val.money + '"></td>'+
						'<td><input type="text" class="form-control input-sm" value="' + val.rewardUp1 + '"></td>'+
						'<td><input type="text" class="form-control input-sm" value="' + val.rewardUp2 + '"></td>'+
						'<td><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i> 删除</a></td>'+
					'</tr>';
					tableRules.find('tbody').append(innerHtml);
				});
			} else {
				for (var i = 0; i < 6; i++) {
					var innerHtml = 
					'<tr>'+
						'<td><input type="text" class="form-control input-sm"></td>'+
						'<td><input type="text" class="form-control input-sm"></td>'+
						'<td><input type="text" class="form-control input-sm"></td>'+
						'<td><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i> 删除</a></td>'+
					'</tr>';
					tableRules.find('tbody').append(innerHtml);
				}
			}
			initTableRules();
		}
		
		var init = function() {
			initForm();
			initTableRules();
		}
		
		return {
			init: init,
			show: show
		}
		
	}();
	
	var ActivityRebateRewardBill = function() {
		var tableList = $('#activity-rebate-reward-bill');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var date = tableList.find('input[name="date"]').val();
			var type = tableList.find('select[name="type"]').val();
			var status = tableList.find('select[name="status"]').val();
			return {username: username, date: date, type: type, status: status};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './activity-rebate-reward-bill/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var checkbox = '<input type="checkbox">';
					if(val.bean.status == 1) {
						checkbox = '<input type="checkbox" disabled="disabled" >';
					}
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + val.bean.id + '</td>'+
						'<td>' + val.toUser + '</td>'+
						'<td>' + DataFormat.formatActivityRewardBillType(val.bean.type) + '</td>'+
						'<td>' + val.fromUser + '</td>'+
						'<td>' + val.bean.totalMoney.toFixed(3) + '</td>'+
						'<td>' + val.bean.money.toFixed(3) + '</td>'+
						'<td>' + val.bean.date + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + DataFormat.formatActivityRewardBillStatus(val.bean.status) + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
				Metronic.initAjax();
				table.find('input[type="checkbox"]').change(function() {
					var tChecked = $(this).is(':checked');
					if(tChecked) {
						$(this).parents('tr').addClass('checked');
					} else {
						$(this).parents('tr').removeClass('checked');
					}
				});
			},
			pageError: function(response) {
				bootbox.dialog({
					message: response.message,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {}
						}
					}
				});
			},
			emptyData: function() {
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});
		
		tableList.find('table > thead').find('input[type="checkbox"]').change(function() {
			var sChecked = $(this).is(':checked');
			tableList.find('table > tbody').find('input[type="checkbox"]').each(function() {
				var tChecked = $(this).is(':checked');
				if(sChecked && tChecked) return;
				if(!sChecked && !tChecked) return;
				$(this).trigger('click');
			});
		});
		
		var confirm = function(ids, action) {
			var params = {ids: ids, action: action};
			var url = './activity-rebate-reward-bill/confirm';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		tableList.find('[data-command="calculate"]').unbind('click').click(function() {
			CalculateModal.show();
		});
		
		tableList.find('[data-command="agree-all"]').unbind('click').click(function() {
			AgreeAllModal.show();
		});
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		
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
		
	}();
	
	var CalculateModal = function() {
		var modal = $('#modal-calculate');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					date: {
						required: true
					}
				},
				messages: {
					date: {
						required: '日期不能为空！'
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
			var date = modal.find('input[name="date"]').val();
			var params = {date: date};
			var url = './activity-rebate-reward-bill/calculate';
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
						ActivityRebateRewardBill.reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var show = function() {
			form[0].reset();
			updateDatePicker();
			
			var today = moment().subtract(1, 'days').format('YYYY-MM-DD');
			modal.find('input[name="date"]').val(today);
			
			form.find('.help-inline').each(function() {
				if($(this).attr('data-default')) {
					$(this).html($(this).attr('data-default'));
				} else {
					$(this).empty();
				}
			});
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
	
	var AgreeAllModal = function() {
		var modal = $('#modal-agree-all');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					date: {
						required: true
					}
				},
				messages: {
					date: {
	                    required: '日期不能为空！'
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
			var date = modal.find('input[name="date"]').val();
			var params = {date: date};
			var url = './activity-rebate-reward-bill/confirm';
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
						ActivityRebateRewardBill.init();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var show = function() {
			form[0].reset();
			updateDatePicker();
			
			var today = moment().subtract(1, 'days').format('YYYY-MM-DD');
			modal.find('input[name="date"]').val(today);
			
			form.find('.help-inline').each(function() {
				if($(this).attr('data-default')) {
					$(this).html($(this).attr('data-default'));
				} else {
					$(this).empty();
				}
			});
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
			ActivityRebateRewardTable.init();
			EditActivityRebateRewardModal.init();
			ActivityRebateRewardBill.init();
			CalculateModal.init();
			AgreeAllModal.init();
			handelDatePicker();
		}
	}
}();