var LotteryUserLoginSameIpLog = function() {
	
	var UserLoginSameIpLogTable = function() {
		var tableList = $('#table-user-login-sameip-log-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var ip = tableList.find('input[name="ip"]').val();
			var username = tableList.find('input[name="username"]').val();
			return {ip: ip, username: username};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-login-sameip-log/list',
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
						'<td>' + val.ip + '</td>'+
						'<td>' + val.address + '</td>'+
						'<td title="'+val.users+'" style="overflow: hidden;text-overflow: ellipsis;">' + val.users + '</td>'+
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
			init: init
		}
	}();
	
	return {
		init: function() {
			UserLoginSameIpLogTable.init();
		}
	}
}();