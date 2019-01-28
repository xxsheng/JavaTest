var VipUpgradeGifts = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var ThisTableList = function() {
		var tableList = $('#vip-upgrade-gifts-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var date = tableList.find('input[name="date"]').val();
			var status = tableList.find('select[name="status"]').val();
			return {username: username, date: date, status: status};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './vip-upgrade-gifts/list',
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
					'<tr class="align-center" data-id="' + val.bean.id + '">'+
						'<td>' + val.bean.id + '</td>'+
						'<td>' + val.username + '</td>'+
						'<td>' + DataFormat.formatUserVipLevel(val.bean.beforeLevel) + '</td>'+
						'<td>' + DataFormat.formatUserVipLevel(val.bean.afterLevel) + '</td>'+
						'<td>' + val.bean.money + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + DataFormat.formatVipUpgradeGiftsStatus(val.bean.status) + '</td>'+
						'<td>' + DataFormat.formatVipUpgradeGiftsReceivedStatus(val.bean.isReceived) + '</td>'+
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
			ThisTableList.init();
			handelDatePicker();
		}
	}
}();