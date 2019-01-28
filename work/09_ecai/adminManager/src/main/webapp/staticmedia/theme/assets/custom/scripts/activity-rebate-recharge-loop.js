var ActivityRebateRechargeLoop = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var ActivityRebateRechargeLoopConfig = function() {
		
		var loadData = function() {
			var url = './activity-rebate-recharge-loop/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						buildData(data.data);
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
		
		var buildData = function(data) {
			var thisPanel = $('#activity-rebate-recharge-loop-config');
			thisPanel.attr('data-id', data.id);
			// 绑定内容
			var rules = eval('(' + data.rules + ')');
			// step1
			var step1 = '<li>环节1<br>单笔充值大于<br><span>' + rules.s1Recharge + '元</span><br>消费充值流水<br><span>' + rules.s1Cost + '%</span><br>获赠充值金额<br><span>' + rules.s1Reward + '%</span></li>';
			// step2
			var step2 = '<li>环节2<br>每消费流水<br><span>' + rules.s2Cost + '元</span><br>奖励金额<br><span>' + rules.s2Reward + '元</span><br>最多领取<br><span>' + rules.s2Times + '次</span></li>';
			// step3
			var step3 = '<li>环节3<br>环节2完成次数<br><span>' + rules.s3Limit + '次</span><br>获赠金额<br><span>' + rules.s3Reward + '元</span></li>';
			thisPanel.find('.rlist > ul').empty().append(step1).append(step2).append(step3);
			// 绑定事件
			thisPanel.find('[data-command="edit"]').unbind('click').click(function() {
				var id = thisPanel.attr('data-id');
				var url = './activity-rebate-recharge-loop/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						EditActivityRebateRechargeLoopModal.show(data);
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
			var url = './activity-rebate-recharge-loop/update-status';
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
							toastr['success']('已启用该活动！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('已暂停该活动！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var EditActivityRebateRechargeLoopModal = function() {
		var modal = $('#modal-activity-rebate-recharge-loop-edit');
		var form = modal.find('form');
		var tableRules = form.find('table[data-table="rules"]');
		var initForm = function() {
			form.validate({
				rules: {
					s1Recharge: {
						required: true,
						number: true
					},
					s1Cost: {
						required: true,
						number: true
					},
					s1Reward: {
						required: true,
						number: true
					},
					s2Cost: {
						required: true,
						number: true
					},
					s2Reward: {
						required: true,
						number: true
					},
					s2Times: {
						required: true,
						number: true
					},
					s3Reward: {
						required: true,
						number: true
					},
					s3Limit: {
						required: true,
						number: true
					}
				},
				messages: {
					s1Recharge: {
						required: '不能为空！',
	                	number: '请填写数字！',
	                },
					s1Cost: {
						required: '不能为空！',
	                	number: '请填写数字！',
					},
					s1Reward: {
						required: '不能为空！',
	                	number: '请填写数字！',
					},
					s2Cost: {
						required: '不能为空！',
	                	number: '请填写数字！',
					},
					s2Reward: {
						required: '不能为空！',
	                	number: '请填写数字！',
					},
					s2Times: {
						required: '不能为空！',
	                	number: '请填写数字！',
					},
					s3Reward: {
						required: '不能为空！',
	                	number: '请填写数字！',
					},
					s3Limit: {
						required: '不能为空！',
	                	number: '请填写数字！',
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
		
		var getRules = function() {
			var s1Recharge = form.find('input[name="s1Recharge"]').val();
			var s1Cost = form.find('input[name="s1Cost"]').val();
			var s1Reward = form.find('input[name="s1Reward"]').val();
			var s2Cost = form.find('input[name="s2Cost"]').val();
			var s2Reward = form.find('input[name="s2Reward"]').val();
			var s2Times = form.find('input[name="s2Times"]').val();
			var s3Reward = form.find('input[name="s3Reward"]').val();
			var s3Limit = form.find('input[name="s3Limit"]').val();
			var rules = {s1Recharge: s1Recharge, s1Cost: s1Cost, s1Reward: s1Reward, s2Cost: s2Cost, s2Reward: s2Reward, s2Times: s2Times, s3Reward: s3Reward, s3Limit: s3Limit};
			return $.toJSON(rules);
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var id = modal.attr('data-id');
			var rules = getRules();
			var params = {id: id, rules: rules};
			var url = './activity-rebate-recharge-loop/edit';
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
						ActivityRebateRechargeLoopConfig.init();
						toastr['success']('活动修改成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('活动修改失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var show = function(data) {
			form[0].reset();
			if(data) {
				modal.attr('data-id', data.id);
				var rules = eval('(' + data.rules + ')');
				form.find('input[name="s1Recharge"]').val(rules.s1Recharge);
				form.find('input[name="s1Cost"]').val(rules.s1Cost);
				form.find('input[name="s1Reward"]').val(rules.s1Reward);
				form.find('input[name="s2Cost"]').val(rules.s2Cost);
				form.find('input[name="s2Reward"]').val(rules.s2Reward);
				form.find('input[name="s2Times"]').val(rules.s2Times);
				form.find('input[name="s3Reward"]').val(rules.s3Reward);
				form.find('input[name="s3Limit"]').val(rules.s3Limit);
			}
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
	
	var ActivityRebateRechargeLoopBill = function() {
		var tableList = $('#activity-rebate-recharge-loop-bill');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var date = tableList.find('input[name="date"]').val();
			var step = tableList.find('select[name="step"]').val();
			return {username: username, date: date, step: step};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './activity-rebate-recharge-loop-bill/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + val.bean.id + '</td>'+
						'<td>' + val.username + '</td>'+
						'<td>' + val.bean.ip + '</td>'+
						'<td>环节' + val.bean.step + '</td>'+
						'<td>' + val.bean.totalMoney + '</td>'+
						'<td>' + val.bean.totalCost + '</td>'+
						'<td>' + val.bean.money + '</td>'+
						'<td>' + val.bean.time + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
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
	
	return {
		init: function() {
			ActivityRebateRechargeLoopConfig.init();
			EditActivityRebateRechargeLoopModal.init();
			ActivityRebateRechargeLoopBill.init();
			handelDatePicker();
		}
	}
}();