var AdminUserActionLog = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}

	var handleSelect = function() {
		$('.bs-select').selectpicker({
			iconBase: 'fa',
			tickIcon: 'fa-check'
		});
		$('.bs-select').selectpicker('refresh');
	}
	
	var AdminUserActionLogTable = function() {
		var tableList = $('#table-admin-user-action-log-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var actionId = tableList.find('select[name="action"]').val();
			var error = tableList.find('select[name="error"]').val();
			var sTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var eTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			eTime = getNetDate(eTime);
			return {username: username, actionId: actionId, error: error, sTime: sTime, eTime: eTime};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './admin-user-action-log/list',
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
						'<td>' + val.actionName + '</td>'+
						'<td>' + val.bean.millisecond + 'ms</td>'+
						'<td>' + val.bean.error + '</td>'+
						'<td>' + val.bean.message + '</td>'+
						'<td>' + val.bean.time + '</td>'+
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
		
		var loadAction = function() {
			var url = './admin-user-action/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					buildAction(data);
				}
			});
		}
		
		var buildAction = function(data) {
			var action = tableList.find('select[name="action"]');
			action.empty();
			action.append('<option value="">全部操作</option>');
			$.each(data, function(idx, val) {
				action.append('<option value="' + val.bean.id + '">' + val.bean.name + '</option>');
			});

			handleSelect();
		}
		
		loadAction();

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
			var tableList = $('#table-admin-user-action-log-list');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);

			AdminUserActionLogTable.init();
			handelDatePicker();
		}
	}
}();