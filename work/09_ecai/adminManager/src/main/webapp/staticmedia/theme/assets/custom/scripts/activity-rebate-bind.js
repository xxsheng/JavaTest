var ActivityRebateBind = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var ActivityRebateBindConfig = function() {
		
		var loadData = function() {
			var url = './activity-rebate-bind/list';
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
			var thisPanel = $('#activity-rebate-bind-config');
			thisPanel.attr('data-id', data.id);
			// 绑定内容
			thisPanel.find('.rlist > ul').empty();
			thisPanel.find('.rlist > ul').append('<li>首次绑定资料<br><span>即可领取</span><br>账户体验金<br><span>' + data.rules + '元</span></li>');
			// 绑定事件
			thisPanel.find('[data-command="edit"]').unbind('click').click(function() {
				var id = thisPanel.attr('data-id');
				var url = './activity-rebate-bind/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						EditActivityRebateBindModal.show(data);
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
			var url = './activity-rebate-bind/update-status';
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
	
	var EditActivityRebateBindModal = function() {
		var modal = $('#modal-activity-rebate-bind-edit');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					money: {
						required: true,
						number: true,
						min: 0
					}
				},
				messages: {
					money: {
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
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var id = modal.attr('data-id');
			var rules = modal.find('input[name="money"]').val();
			var params = {id: id, rules: rules};
			var url = './activity-rebate-bind/edit';
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
						ActivityRebateBindConfig.init();
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
				modal.find('input[name="money"]').val(data.rules);
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
	
	var ActivityRebateBindBill = function() {
		var tableList = $('#activity-rebate-bind-bill');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var upperUser = tableList.find('input[name="upperUser"]').val();
			var date = tableList.find('input[name="date"]').val();
			var keyword = tableList.find('input[name="keyword"]').val();
			var status = tableList.find('select[name="status"]').val();
			return {username: username, upperUser: upperUser, date: date, keyword: keyword, status: status};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './activity-rebate-bind-bill/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var statusAction = '<a href="javascript:;" class="btn default btn-xs black disabled">无操作 </a>';
					if(val.bean.status == 0) {
						statusAction = '<a data-command="agree" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 同意 </a><a data-command="refuse" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 拒绝 </a>';
					}
					var checkbox = '<input type="checkbox">';
					if(val.bean.status == 1) {
						checkbox = '<input type="checkbox" disabled="disabled" >';
					}
					innerHtml +=
					'<tr class="align-center" data-id="' + val.bean.id + '">'+
						'<td>' + checkbox + '</td>' +
						'<td>' + val.bean.id + '</td>'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a> (' + val.locatePoint + ')' + '</td>'+
						'<td><a href="./lottery-report-complex?username=' + val.upperUser + '">' + val.upperUser + '</a></td>'+
						'<td>' + (val.recharge ? '是' : '否') + '</td>'+
						'<td>' + (val.cost ? '是' : '否') + '</td>'+
						'<td>' + val.bean.registTime + '</td>'+
						'<td>' + val.bean.ip + '</td>'+
						'<td>' + val.bean.bindName + ' (' + val.bindBank + ') <br/> ' + val.bean.bindCard + '</td>'+
						'<td>' + val.bean.money + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + DataFormat.formatActivityBindBillStatus(val.bean.status) + '</td>'+
						'<td>' + statusAction + '</td>'+
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
				table.find('[data-command="agree"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var msg = '确定同意发放活动金额？';
					bootbox.dialog({
						message: msg,
						title: '提示消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									doConfirm(id, 'N');
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
				table.find('[data-command="refuse"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var msg = '确定拒绝发放活动金额？';
					bootbox.dialog({
						message: msg,
						title: '提示消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									doRefuse([id].toString());
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
		
		var doConfirm = function(id, confirm) {
			var params = {id: id, confirm: confirm};
			var url = './activity-rebate-bind-bill/confirm';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						if(data.code == '2-2022') {
							bootbox.dialog({
								message: data.message + '是否继续发放？',
								title: '提示消息',
								buttons: {
									success: {
										label: '<i class="fa fa-check"></i> 确定',
										className: 'green-meadow',
										callback: function() {
											doConfirm(id, 'Y');
										}
									},
									danger: {
										label: '<i class="fa fa-undo"></i> 取消',
										className: 'btn-danger',
										callback: function() {}
									}
								}
							});
						} else {
							toastr['error']('操作失败！' + data.message, '操作提示');
						}
					}
				}
			});
		}
		
		var doRefuse = function(ids) {
			var params = {ids: ids};
			var url = './activity-rebate-bind-bill/refuse';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
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
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		
		tableList.find('[data-command="batch-refuse"]').unbind('click').click(function() {
			var checkes = tableList.find('table > tbody').find('input[type="checkbox"]:checked');
			if(checkes.length == 0) {
				toastr['info']('您没有选择任何数据！', '操作提示');
				return;
			}
			var ids = [];
			$.each(checkes, function() {
				var id = $(this).parents('tr').attr('data-id');
				ids.push(id);
			});
			var msg = '确定拒绝发放所选<font color="#B4020A">' + checkes.length + '</font>位用户活动金额？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doRefuse(ids.toString());
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
			ActivityRebateBindConfig.init();
			EditActivityRebateBindModal.init();
			ActivityRebateBindBill.init();
			handelDatePicker();
		}
	}
}();