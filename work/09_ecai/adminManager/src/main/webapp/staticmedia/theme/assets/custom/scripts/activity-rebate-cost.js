var ActivityRebateSign = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	};
	
	var ActivityRebateCostConfig = function() {
		
		var loadData = function() {
			var url = './activity-rebate-cost/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						buildData(data.data);
					}else{
						thisPanel.attr('data-id', 15);
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
		};
		
		var buildData = function(data) {
			var thisPanel = $('#activity-rebate-cost-config');
			thisPanel.attr('data-id', data.id);
			// 绑定内容
			thisPanel.find('.rlist > ul').empty()
			if(data.rules != null && data.rules != ""){
				$.each(eval(data.rules), function(idx, val) {
					thisPanel.find('.rlist > ul').append('<li>日最低消费 <span>' + val.cost + '元</span><br/>奖励 <span>' + val.prizeMoney+ '元</span></li>');
				});
			}
			// 绑定事件
			thisPanel.find('[data-command="edit"]').unbind('click').click(function() {
				var id = thisPanel.attr('data-id');
				var url = './activity-rebate-cost/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						EditActivityRebateCostModal.show(data);
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
		};
		
		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './activity-rebate-cost/update-status';
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
		};
		
		var init = function() {
			loadData();
		};
		
		return {
			init: init
		};
	}();
	
	var EditActivityRebateCostModal = function() {
		var modal = $('#table-activity-rebate-cost-edit');
		var form = modal.find('form');
		var tableRules = form.find('table[data-table="rules"]');
		var initForm = function() {
			tableRules.find('[data-command="add"]').unbind('click').click(function() {
				var innerHtml = 
				'<tr>'+
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
		};
		
		var getRules = function() {
			var rules = [];
			tableRules.find('tbody > tr').each(function() {
				var cost = $(this).find('input').eq(0).val();
				var prizeMoney = $(this).find('input').eq(1).val();
				if(cost != '' && prizeMoney != '') {
					cost = parseInt(cost);
					prizeMoney = parseFloat(prizeMoney);
					rules.push({cost: cost, prizeMoney: prizeMoney});
				}
			});
			return $.toJSON(rules);
		};
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var id = modal.attr('data-id');
			var rules = getRules();
			var params = {id: id, rules: rules};
			var url = './activity-rebate-cost/edit';
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
						ActivityRebateCostConfig.init();
						toastr['success']('活动修改成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('活动修改失败！' + data.message, '操作提示');
					}
				}
			});
		};
		
		var show = function(data) {
			form[0].reset();
			if(data) {
				modal.attr('data-id', data.id);
				var rules = eval(data.rules);
				resetTableData(rules);
			}
			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		};
		
		var initTableRules = function() {
			tableRules.find('[data-command="delete"]').unbind('click').click(function() {
				if(tableRules.find('tbody > tr').length == 1) return;
				$(this).parents('tr').remove();
			});
		};
		
		var resetTableData = function(rules) {
			tableRules.find('tbody').empty();
			if(rules) {
				$.each(rules, function(idx, val) {
					var innerHtml = 
					'<tr>'+
						'<td><input type="text" class="form-control input-sm" value="' + val.cost + '"></td>'+
						'<td><input type="text" class="form-control input-sm" value="' + val.prizeMoney + '"></td>'+
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
						'<td><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i> 删除</a></td>'+
					'</tr>';
					tableRules.find('tbody').append(innerHtml);
				}
			}
			initTableRules();
		};
		
		var init = function() {
			initForm();
			initTableRules();
		};
		
		return {
			init: init,
			show: show
		};
		
	}();
	
	var ActivityRebateCostBill = function() {
		var tableList = $('#activity-rebate-cost-bill');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var date = tableList.find('input[name="date"]').val();
			return {username: username, date: date};
		};
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './activity-rebate-cost/bill',
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
						'<td>' + val.bean.totalCost + ' 元</td>'+
						'<td>' + val.bean.money + ' 元</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + val.bean.drawIp + '</td>'+
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
				tableList.find('table > tbody').empty();
				tablePagelist.html('没有相关数据');
			}
		});
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		
		var init = function() {
			pagination.init();
		};
		
		var reload = function() {
			pagination.reload();
		};
		
		return {
			init: init,
			reload: reload
		};
		
	}();
	
	
	return {
		init: function() {
			ActivityRebateCostConfig.init();
			EditActivityRebateCostModal.init();
			ActivityRebateCostBill.init();
			//ActivityRebateSignRecord.init();
			handelDatePicker();
		}
	};
}();