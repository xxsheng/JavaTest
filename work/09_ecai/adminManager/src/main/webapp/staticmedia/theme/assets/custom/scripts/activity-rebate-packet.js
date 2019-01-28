var ActivityRebatePacket = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	};
	
	var ActivityRebatePacketConfig = function() {
		
		var loadData = function() {
			var url = './activity-rebate-packet/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						buildData(data.data);
						initTotal(data.totalInfo);
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
		
		var initTotal = function(data){
			$.each(eval(data), function(i, o){
				if(o[0] == 0){
					$("#systemTotal").html(o[1]);
				}
				if(o[0] == 1){
					$("#userTotal").html(o[1]);
				}
			});
		}
		
		var buildData = function(data) {
			var thisPanel = $('#activity-rebate-packet-config');
			thisPanel.attr('data-id', data.id);
			// 绑定内容
			thisPanel.find('.rlist > ul').empty();
			$.each(eval(data.rules), function(idx, val) {
				thisPanel.find('.rlist > ul').append('<li>日最低消费:<span>' + val.cost + '元</span><br/></li>');
			});
			// 绑定事件
			thisPanel.find('[data-command="edit"]').unbind('click').click(function() {
				var id = thisPanel.attr('data-id');
				var url = './activity-rebate-packet/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						EditActivityRebatePacketModal.show(data);
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
			var url = './activity-rebate-packet/update-status';
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
	
	var SendActivityRebatePacketModal = function() {
		var modal = $('#table-activity-rebate-packet-send');
		var form = modal.find('form');
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var count =  $('#table-activity-rebate-packet-send').find('[name="count"]').val();
			var amount =  $('#table-activity-rebate-packet-send').find('[name="amount"]').val();
			var params = {count:count, amount:amount};
			var url = './activity-rebate-packet/send';
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
						ActivityRebatePacketConfig.init();
						toastr['success']('红包发送成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('红包失败！' + data.message, '操作提示');
					}
				}
			});
		};
		
		var show = function() {
			form[0].reset();
			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		};
		
		var init = function() {
			$('#activity-rebate-packet-info').find('[data-command="send"]').unbind('click').click(function() {
				show();
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				form.validate({
					rules: {
						count: {
							required: true,
							number: true,
							min: 1,
							max: 100
						},
						amount: {
							required: true,
							number: true,
							min: 0
						}
					},
					messages: {
		                count: {
							required: '发放数量不能为空！',
							number: '发放数量为数字！',
							min: '发放数量不能小于0！',
							max: '每次最多发100个！'
						},
						amount: {
							required: '金额不能为空！',
							number: '金额必须为数字！',
							min: '金额不能小于0！'
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
				
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		};
		
		return {
			init: init
		};
		
	}();
	
	var EditActivityRebatePacketModal = function() {
		var modal = $('#table-activity-rebate-packet-edit');
		var form = modal.find('form');
		var tableRules = form.find('table[data-table="rules"]');
		var initForm = function() {
			tableRules.find('[data-command="add"]').unbind('click').click(function() {
				var innerHtml = 
				'<tr>'+
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
				if(cost != '') {
					cost = parseFloat(cost);
					rules.push({cost: cost});
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
			var url = './activity-rebate-packet/edit';
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
						ActivityRebatePacketConfig.init();
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
	
	var ActivityRebatePacketBill = function() {
		var tableList = $('#activity-rebate-packet-bill');
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
			ajaxUrl: './activity-rebate-packet/bill',
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
						'<td>' + val.bean.packetId + '</td>'+
						'<td>' + val.bean.amount + '</td>'+
						'<td>' + val.bean.cost + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + val.bean.ip + '</td>'+
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
	
	var ActivityRebatePacketInfo = function() {
		var tableList = $('#activity-rebate-packet-info');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var date = tableList.find('input[name="date"]').val();
			var type = tableList.find('select[name="type"]').val();
			return {username: username, date: date, type: type};
		};
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './activity-rebate-packet/info',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var displayTxt = '未完成';
					var packetType = '用户红包';
					if(val.info.status == 1)displayTxt = '已抢完';
					if(val.info.type == 0)packetType = '系统红包';
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + val.info.id + '</td>'+
						'<td>' + val.username + '</td>'+
						'<td>' + val.info.amount + '</td>'+
						'<td>' + val.info.count + '</td>'+
						'<td>' + packetType + '</td>'+
						'<td>' + val.info.time + '</td>'+
						'<td>' + displayTxt + '</td>'+
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
			ActivityRebatePacketConfig.init();
			EditActivityRebatePacketModal.init();
			ActivityRebatePacketBill.init();
			ActivityRebatePacketInfo.init();
			SendActivityRebatePacketModal.init();
			handelDatePicker();
		}
	};
}();