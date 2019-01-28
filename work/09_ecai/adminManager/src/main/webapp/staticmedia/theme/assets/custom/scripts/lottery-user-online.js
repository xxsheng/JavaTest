var LotteryUserOnline = function() {
	
	var UserOnlineTable = function() {
		var tableList = $('#table-user-online-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var sortColoum = tableList.find('select[name="sortColoum"]').val();
			var sortType = tableList.find('select[name="sortType"]').val();
			return {sortColoum: sortColoum, sortType: sortType};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user/list-online',
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
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
						'<td>' + val.totalMoney.toFixed(2) + '</td>'+
						'<td>' + val.lotteryMoney.toFixed(2) + '</td>'+
						'<td>' + val.baccaratMoney.toFixed(2) + '</td>'+
						'<td>' + DataFormat.formatLevelUsers(val.username, val.levelUsers) + '</td>'+
						'<td>' + val.loginTime + '</td>'+
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
		
		tableList.find('select[name="sortColoum"]').unbind('change').change(function() {
			init();
		});
		
		tableList.find('select[name="sortType"]').unbind('change').change(function() {
			init();
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
			UserOnlineTable.init();
		}
	}
}();