var AdminUserAction = function() {
	
	var AdminUserActionTable = function() {
		var tableList = $('#table-admin-user-action-list');
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './admin-user-action/list';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					buildData(data);
				}
			});
		}
		
		var buildData = function(data) {
			var table = tableList.find('table > tbody').empty();
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var statusAction = '<a data-command="status" data-status="' + val.bean.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用 </a>';
				if(val.bean.status == -1) {
					statusAction = '<a data-command="status" data-status="' + val.bean.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
				}
				innerHtml +=
				'<tr class="align-center" data-id="' + val.bean.id + '">'+
					'<td class="align-left">' + val.bean.name + '</td>'+
					'<td class="align-left">' + val.bean.key + '</td>'+
					//'<td>' + val.bean.description + '</td>'+
					'<td>' + val.bean.sort + '</td>'+
					'<td>' + DataFormat.formatAdminUserActionStatus(val.bean.status) + '</td>'+
					'<td>' + statusAction + '</td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定禁用该权限？';
				if(status == -1) {
					msg = '确定启用该权限？';
				}
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								if(status == 0) {
									updateStatus(id, -1);
								}
								if(status == -1) {
									updateStatus(id, 0);
								}
							}
						},
						danger: {
							label: '<i class="fa fa-undo"></i> 取消',
							className: 'btn-danger',
							callback: function() {}
						}
					}
				});
			});
		}
		
		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './admin-user-action/update-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						loadData();
						if(status == 0) {
							toastr['success']('该权限已启用！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该权限已禁用！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			AdminUserActionTable.init();
		}
	}
}();