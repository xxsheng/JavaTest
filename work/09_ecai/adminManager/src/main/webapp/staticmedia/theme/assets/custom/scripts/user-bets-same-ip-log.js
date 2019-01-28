var UserBetsSameIpLog = function() {
	
	var UserBetsSameIpLogTable = function() {
		var tableList = $('#table-user-bets-same-ip-log-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var ip = tableList.find('input[name="ip"]').val();
			var username = tableList.find('input[name="username"]').val();
			var sortColumn = tableList.find('select[name="sortColumn"]').val();
			var sortType = tableList.find('select[name="sortType"]').val();
			return {ip: ip, username: username, sortColumn: sortColumn, sortType: sortType};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './user-bets-same-ip-log/list',
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
						'<td>' + val.users + '</td>'+
						'<td>' + val.usersCount + '</td>'+
						'<td>' + val.lastTime + '</td>'+
						'<td><a href="./lottery-user-profile?username=' + val.lastUser + '">' + val.lastUser + '</a></td>'+
						'<td>' + val.times + '</td>'+
						'<td>' + val.amount.toFixed(3) + '</td>'+
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

		tableList.find('select[name="sortColumn"]').change(function() {
			isSort($(this));
		});

		var isSort = function(sortColumn) {
			if(!sortColumn) {
				sortColumn = tableList.find('select[name="sortColumn"]');
			}
			var sortType = tableList.find('select[name="sortType"]');
			if(sortColumn.val() == '') {
				sortType.hide();
			} else {
				sortType.show();
			}
		}

		isSort();
		
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
			UserBetsSameIpLogTable.init();
		}
	}
}();