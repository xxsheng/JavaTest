var LotteryUserWhitelist = function() {
	
	var UserWhitelistTable = function() {
		var tableList = $('#table-user-whitelist-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var keyword = tableList.find('input[name="keyword"]').val();
			return {keyword: keyword};
		}
		
		var formatValue = function(value) {
			return value == '' ? '-' : value;
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-whitelist/list',
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
					'<tr class="align-center" data-id="' + val.id + '">'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + formatValue(val.username) + '</a></td>'+
						'<td>' + formatValue(val.cardName) + '</td>'+
						'<td>' + formatValue(val.cardId) + '</td>'+
						'<td>' + formatValue(val.bankName) + '</td>'+
						'<td>' + formatValue(val.ip) + '</td>'+
						'<td>' + val.operatorUser + '</td>'+
						'<td>' + val.operatorTime + '</td>'+
						'<td>' + formatValue(val.remarks) + '</td>'+
						'<td><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 删除 </a></td>'+
					'</tr>';
				});
				table.html(innerHtml);
				
				table.find('[data-command="delete"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					confirmDelete(id);
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
		
		var confirmDelete = function(id) {
			var msg = '确定要删除该白名单记录？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							deleteWhitelist(id);
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
		
		var deleteWhitelist = function(id) {
			var params = {id: id};
			var url = './lottery-user-whitelist/delete';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						reload();
						toastr['success']('成功删除该条白名单记录！', '操作提示');
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
			AddLotteryUserWhitelistModal.show();
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
	
	var AddLotteryUserWhitelistModal = function() {
		var modal = $('#modal-lottery-user-whitelist-add');
		var form = modal.find('form');
		var initForm = function() {
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				doSubmit();
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var username = modal.find('input[name="username"]').val();
			var cardName = modal.find('input[name="cardName"]').val();
			var bankName = modal.find('select[name="bankName"]').val();
			var cardId = modal.find('input[name="cardId"]').val();
			var ip = modal.find('input[name="ip"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			if(username == '' && cardName == '' && cardId == '' && ip == '') return;
			var params = {username: username, cardName: cardName, bankName: bankName, cardId: cardId, ip: ip, remarks: remarks};
			var url = './lottery-user-whitelist/add';
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
						UserWhitelistTable.reload();
						toastr['success']('白名单添加成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('白名单添加失败！' + data.message, '操作提示');
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
			var bank = form.find('select[name="bankName"]');
			bank.empty();
			$.each(data, function(idx, val) {
				bank.append('<option value="' + val.name + '">' + val.name + '</option>');
			});
		}
		
		var show = function() {
			form[0].reset();
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
			UserWhitelistTable.init();
			AddLotteryUserWhitelistModal.init();
		}
	}
}();