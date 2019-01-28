var AdminUserLog = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var AdminUserLogTable = function() {
		var tableList = $('#table-admin-user-log-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var actionId = tableList.find('select[name="action"]').val();
			var ip = tableList.find('input[name="ip"]').val();
			var keyword = tableList.find('input[name="keyword"]').val();
			var sDate = tableList.find('[data-field="time"] > input[name="from"]').val();
			var eDate = tableList.find('[data-field="time"] > input[name="to"]').val();
			eDate = getNetDate(eDate);
			return {username: username,actionId:actionId, ip: ip, keyword: keyword, sDate: sDate, eDate: eDate};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './admin-user-critical-log/list',
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
						'<td><a href="./lottery-user-profile?username=' + val.adminUsername + '">' + val.adminUsername + '</a></td>'+
						'<td>' + val.bean.ip + '</td>'+
						'<td>' + val.bean.address + '</td>'+
						'<td>' + val.bean.action + '</td>'+
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
//		var loadAction = function() {
//			var url = './admin-user-critical-log-action/list';
//			$.ajax({
//				type : 'post',
//				url : url,
//				data : {},
//				dataType : 'json',
//				success : function(data) {
//					buildAction(data);
//				}
//			});
//		}
		
//		var buildAction = function(data) {
//			var action = tableList.find('select[name="action"]');
//			action.empty();
//			action.append('<option value="">全部操作</option>');
//			$.each(data, function(idx, val) {
//				action.append('<option value="' + val.bean.id + '">' + val.bean.name + '</option>');
//			});

			//handleSelect();
//		}
		
//		loadAction();
		
		tableList.find('input[name="advanced"]').change(function() {
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
			tableList.find('input[name="ip"]').val('');
			tableList.find('input[name="keyword"]').val('');
		}

		isAdvanced();
		
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
			var today = moment().format('YYYY-MM-DD');
			var tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');
			var tableList = $('#table-admin-user-log-list');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);

			AdminUserLogTable.init();
			handelDatePicker();
		}
	}
}();