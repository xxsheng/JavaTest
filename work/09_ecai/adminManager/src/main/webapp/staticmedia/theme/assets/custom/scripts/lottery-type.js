var LotteryType = function() {
	
	var LotteryTypeTable = function() {
		var tableList = $('#table-lottery-type-list');
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './lottery-type/list';
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
				var statusAction = '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 停用 </a>';
				if(val.status == -1) {
					statusAction = '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
				}
				innerHtml +=
				'<tr class="align-center" data-id="' + val.id + '">'+
					'<td>' + val.id + '</td>'+
					'<td>' + val.name + '</td>'+
					'<td>' + val.sort + '</td>'+
					'<td>' + DataFormat.formatLotteryStatus(val.status) + '</td>'+
					'<td>' + statusAction + '</td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定禁用该类型？(禁用后用户不能进行该彩票种类投注操作，已投注的不影响。)';
				if(status == -1) {
					msg = '确定启用该类型？';
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
			var url = './lottery-type/update-status';
			isSending = true;
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
							toastr['success']('该类型已恢复正常状态！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该类型已禁用！', '操作提示');
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
			LotteryTypeTable.init();
		}
	}
}();