var LotteryUserCard = function() {
	
	var UserCardTable = function() {
		var tableList = $('#table-user-card-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var keyword = tableList.find('input[name="keyword"]').val();
			var status = tableList.find('select[name="status"]').val();
			return {username: username, keyword: keyword, status: status};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-card/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var statusAction = '';
					if(val.bean.status == 0) {
						statusAction = '<a data-command="status" data-status="-1" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-times"></i> 无效</a><a data-command="status" data-status="-2" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-lock"></i> 锁定</a>';
					}
					if(val.bean.status == -1) {
						statusAction = '<a data-command="status" data-status="0" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 资料通过</a>';
					}
					if(val.bean.status == -2) {
						statusAction = '<a data-command="status" data-status="0" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-unlock"></i> 解锁</a>';
					}
					var bankBranch = val.bean.bankBranch != '' ? val.bean.bankBranch : '-';
					var lockTime = val.bean.lockTime != '' ? val.bean.lockTime : '-';
					innerHtml +=
					'<tr class="align-center" data-id="' + val.bean.id + '">'+
						'<td>' + val.bean.id + '</td>'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
						'<td>' + val.bankName + '</td>'+
						'<td>' + bankBranch + '</td>'+
						'<td>' + val.bean.cardName + '</td>'+
						'<td>' + val.bean.cardId + '</td>'+
						'<td>' + DataFormat.formatUserCardStatus(val.bean.status) + '</td>'+
						'<td>' + lockTime + '</td>'+
						'<td>' + statusAction + '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a></td>'+
					'</tr>';
				});
				table.html(innerHtml);
				table.find('[data-command="edit"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					doLoadCard(id, 'edit');
				});
				
				table.find('[data-command="status"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var status = $(this).attr('data-status');
					var msg = '确定要修改卡片状态？';
					bootbox.dialog({
						message: msg,
						title: '提示消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									updateStatus(id, status);
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
							className: 'btn-success',
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
		
		var doLoadCard = function(id, action) {
			var url = './lottery-user-card/get';
			var params = {id: id};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						EditLotteryUserCardModal.show(action, data.data);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './lottery-user-card/lock-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('操作成功，卡片状态已改变！', '操作提示');
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
		
		tableList.find('[data-command="add"]').unbind('click').click(function() {
			EditLotteryUserCardModal.show('add');
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
	
	var EditLotteryUserCardModal = function() {
		var modal = $('#modal-lottery-user-card-edit');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					username: {
						required: true
					},
					cardName: {
						required: true
					},
					cardId: {
						required: true
					}
				},
				messages: {
					username: {
						required: '用户名不能为空！'
					},
					cardName: {
	                    required: '姓名不能为空！'
	                },
	                cardId: {
	                    required: '卡号不能为空！'
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
			var params = {};
			var url = '';
			if(action == 'edit') {
				url = './lottery-user-card/edit';
				var id = modal.attr('data-id');
				var bankId = form.find('select[name="bank"]').val();
				var bankBranch = form.find('input[name="bankBranch"]').val();
				var cardName = form.find('input[name="cardName"]').val();
				var cardId = form.find('input[name="cardId"]').val();
				params = {id: id, bankId: bankId, bankBranch: bankBranch, cardName: cardName, cardId: cardId};
			}
			if(action == 'add') {
				url = './lottery-user-card/add';
				var username = form.find('input[name="username"]').val();
				var bankId = form.find('select[name="bank"]').val();
				var bankBranch = form.find('input[name="bankBranch"]').val();
				var cardName = form.find('input[name="cardName"]').val();
				var cardId = form.find('input[name="cardId"]').val();
				var status = form.find('input[name="status"]:checked').val();
				params = {username: username, bankId: bankId, bankBranch: bankBranch, cardName: cardName, cardId: cardId, status: status};
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
						UserCardTable.init();
						if(action == 'edit') {
							toastr['success']('银行卡修改成功！', '操作提示');
						}
						if(action == 'add') {
							toastr['success']('银行卡添加完成！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						if(action == 'edit') {
							toastr['error']('银行卡修改失败！' + data.message, '操作提示');
						}
						if(action == 'add') {
							toastr['error']('银行卡添加失败！' + data.message, '操作提示');
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
		
		var show = function(action, data) {
			form[0].reset();
			if('edit' == action) {
				modal.attr('data-action', 'edit');
				modal.attr('data-id', data.bean.id);
				modal.find('.modal-title').html('编辑银行卡');
				form.find('input[name="username"]').attr('disabled', true).val(data.username);
				form.find('select[name="bank"]').removeAttr('disabled').find('option[value="' + data.bean.bankId + '"]').attr('selected', true);
				form.find('input[name="bankBranch"]').val(data.bean.bankBranch).removeAttr('disabled');
				form.find('input[name="cardName"]').val(data.bean.cardName).removeAttr('disabled');
				form.find('input[name="cardId"]').val(data.bean.cardId).removeAttr('disabled');
				form.find('input[name="status"][value="' + data.bean.status + '"]').attr('checked', true);
				Metronic.initAjax();
			}
			if('add' == action) {
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				modal.find('.modal-title').html('添加银行卡');
				if(data) {
					form.find('input[name="username"]').attr('disabled', true).val(data);
				} else {
					form.find('input[name="username"]').removeAttr('disabled');
				}
				form.find('select[name="bank"]').removeAttr('disabled').find('option').eq(0).attr('selected', true);
				form.find('input[name="bankBranch"]').removeAttr('disabled');
				form.find('input[name="cardName"]').removeAttr('disabled');
				form.find('input[name="cardId"]').removeAttr('disabled');
				form.find('input[name="status"]').eq(0).attr('checked', true);
				Metronic.initAjax();
			}
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
			UserCardTable.init();
			EditLotteryUserCardModal.init();
		}
	}
}();