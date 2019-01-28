var LotteryDailySettleBill = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var UserDailySettleBillTable = function() {
		var tableList = $('#table-daily-settle-bill-list');
		var tablePagelist = tableList.find('.page-list');

		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var sTime = tableList.find('[data-field="time"] > input[name="sTime"]').val();
			var eTime = tableList.find('[data-field="time"] > input[name="eTime"]').val();
			var minUserAmount = tableList.find('input[name="minUserAmount"]').val();
			var maxUserAmount = tableList.find('input[name="maxUserAmount"]').val();
			var status = tableList.find('select[name="status"]').val();
			return {username: username, sTime: sTime, eTime: eTime, minUserAmount: minUserAmount, maxUserAmount: maxUserAmount, status: status};
		}

		var resetParamsForUpUser = function(upUsername) {
			tableList.find('input[name="username"]').val(upUsername);
			tableList.find('input[name="minUserAmount"]').val('');
			tableList.find('input[name="maxUserAmount"]').val('');
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-daily-settle-bill/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var formatId = '<a href="javascript:;" data-val="' + val.username + '" data-command="details">' + val.username + '</a>';
					innerHtml +=
					'<tr class="align-center" data-id="' + val.id + '">'+
						'<td>' + val.id + '</td>'+
						'<td>' + formatId +'</td>'+
						'<td>' + val.scale.toFixed(2) + '%</td>'+
						'<td>' + val.validUser + '/' + val.minValidUser + '</td>'+
						'<td>' + val.billingOrder.toFixed(1) + '</td>'+
						'<td>' + val.thisLoss.toFixed(1) + '</td>'+
						'<td>' + val.calAmount.toFixed(1) + '</td>'+
						'<td>' + val.userAmount.toFixed(1) + '</td>'+
						'<td>' + val.totalReceived.toFixed(1) + '</td>'+
						'<td>' + val.lowerPaidAmount.toFixed(1) + '/' + val.lowerTotalAmount.toFixed(1) + '</td>'+
						'<td>' + val.settleTime + '</td>'+
						'<td>' + DataFormat.formatDailySettleBillIssueType(val.issueType) + '</td>'+
						'<td>' + DataFormat.formatDailySettleBillStatus(val.status) + '</td>'+
					'</tr>';

				});
				table.html(innerHtml);
				table.find('[data-command="details"]').unbind('click').click(function() {
					var upUsername = $(this).attr('data-val');
					resetParamsForUpUser(upUsername);
					UserDailySettleBillTable.init();
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
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		
		tableList.find('input[name="advanced"]').unbind('change').change(function() {
			isAdvanced($(this));
		});
		
		var isAdvanced = function(advanced) {
			if(!advanced) {
				advanced = tableList.find('input[name="advanced"]');
			}
			if(advanced.is(':checked')) {
				tableList.find('[data-hide="advanced"]').show();
			} else {
				clearAdvanced();
				tableList.find('[data-hide="advanced"]').hide();
			}
		}
		
		var clearAdvanced = function() {
			var dateTime = tableList.find('[data-field="time"]');
			dateTime.find('input[name="sTime"]').val('').change();
			dateTime.find('input[name="eTime"]').val('').change();
			tableList.find('input[name="minUserAmount"]').val('');
			tableList.find('input[name="maxUserAmount"]').val('');
			tableList.find('select[name="status"] > option').eq(0).attr('selected', true);
		}
		
		isAdvanced();
		
		var init = function() {
			pagination.init();
		}
		
		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			var today = moment().format('YYYY-MM-DD');
			var tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');
			var tableList = $('#table-daily-settle-bill-list');
			tableList.find('[data-field="time"] > input[name="sTime"]').val(today);
			tableList.find('[data-field="time"] > input[name="eTime"]').val(tomorrow);

			UserDailySettleBillTable.init();
			handelDatePicker();
		}
	}
}();