var LotteryUserUbindCard = function() {
	
	var UserUbindCardTable = function() {
		var tableList = $('#table-user-unbind-card-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var cardId = tableList.find('input[name="cardId"]').val();
			var unbindTime = tableList.find('input[name="date"]').val();
			return {username: username, cardId: cardId, unbindTime: unbindTime};
		}
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-card/unbid-list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var action = '<a data-command="del" data-cardId="' + val.cardId + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-remove"></i>删除</a>';
					
					var unbindTime = val.unbindTime != '' ? val.unbindTime : '-';
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + val.id + '</td>'+
						'<td>' + val.username + '</td>'+
						'<td>' + val.cardId + '</td>'+
						'<td>' + val.unbindNum + '</td>'+
						'<td>' + unbindTime + '</td>'+
						'<td>' + action + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);

				table.find('[data-command="del"]').unbind('click').click(function() {
					var cardId = $(this).attr('data-cardId');
					confirmDelUnbindCard(cardId);
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

		var confirmDelUnbindCard = function(cardId) {
			bootbox.prompt({
				size: "small",
				inputType: 'text',
				title: "请输入备注",
				callback: function(remark){
					if (!remark) {
						return;
					}
					
					var msg = '<strong>确认要删除该记录吗？</strong>';
					bootbox.dialog({
						message: msg,
						title: '确认消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									doDelUnbindCard(cardId, remark);
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

			
		}

		var doDelUnbindCard = function(cardId, remark) {
			var params = {cardId: cardId, remark: remark};
			var url = './lottery-user-card/unbid-del';
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
			UserUbindCardTable.init();
			//EditLotteryUserUbindCardModal.init();
		}
	}
}();