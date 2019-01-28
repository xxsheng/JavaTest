var UserGameWaterBill = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var UserGameWaterBillTable = function() {
		var tableList = $('#table-user-game-water-bill-list');
		var tablePagelist = tableList.find('.page-list');

		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var sTime = tableList.find('[data-field="time"] > input[name="sTime"]').val();
			var eTime = tableList.find('[data-field="time"] > input[name="eTime"]').val();
			var minUserAmount = tableList.find('input[name="minUserAmount"]').val();
			var maxUserAmount = tableList.find('input[name="maxUserAmount"]').val();
			var status = tableList.find('select[name="status"]').val();
			var type = tableList.find('select[name="type"]').val();
			return {username: username, sTime: sTime, eTime: eTime, minUserAmount: minUserAmount, maxUserAmount: maxUserAmount, status: status, type: type};
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './user-game-water-bill/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var user = '<a href="./lottery-user-profile?username='+val.username+'">' + val.username + '</a>';
					var fromUser = '<a href="./lottery-user-profile?username='+val.fromUsername+'">' + val.fromUsername + '</a>';
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + val.bean.id + '</td>'+
						'<td>' + user +'</td>'+
						'<td>' + fromUser +'</td>'+
						'<td>' + ((val.bean.scale * 100).toFixed(2)) + '%</td>'+
						'<td>' + val.bean.billingOrder.toFixed(4) + '</td>'+
						'<td>' + val.bean.userAmount.toFixed(4) + '</td>'+
						'<td>' + val.bean.indicateDate + '</td>'+
						'<td>' + DataFormat.formatUserGameWaterBillType(val.bean.type) + '</td>'+
						'<td>' + val.bean.settleTime + '</td>'+
						'<td>' + DataFormat.formatUserGameWaterBillStatus(val.bean.status) + '</td>'+
						'<td>' + val.bean.remark + '</td>'+
					'</tr>';
					// innerHtml +=
					// '<tr class="align-center">'+
					// 	'<td>1</td>'+
					// 	'<td>2</td>'+
					// 	'<td>3</td>'+
					// 	'<td>4</td>'+
					// 	'<td>5</td>'+
					// 	'<td>6</td>'+
					// 	'<td>7</td>'+
					// 	'<td>8</td>'+
					// 	'<td>9</td>'+
					// 	'<td>10</td>'+
					// '</tr>';

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
			tableList.find('input[name="minUserAmount"]').val('');
			tableList.find('input[name="maxUserAmount"]').val('');
			tableList.find('select[name="type"] > option').eq(0).attr('selected', true);
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
			var yesterday = moment().add(-1, 'days').format('YYYY-MM-DD');
			var today = moment().format('YYYY-MM-DD');
			var tableList = $("#table-user-game-water-bill-list");
			tableList.find('[data-field="time"] > input[name="sTime"]').val(yesterday);
			tableList.find('[data-field="time"] > input[name="eTime"]').val(today);
			UserGameWaterBillTable.init();
			handelDatePicker();
		}
	}
}();