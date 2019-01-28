var GamePlatformList = function() {
	var GamePlatformTable = function() {
		var tableList = $('#table-game-platform-list');

		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './game/platform/list';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					buildData(data.data);
				},
				complete: function() {
					isLoading = false;
				}
			});
		}

		var buildData = function(data) {
			var table = tableList.find('table > tbody').empty();
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var operate = '';
				if(val.status != 0) {
					operate = '<a data-command="enable" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用</a>';
				}
				else {
					operate = '<a data-command="disable" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用</a>';
				}

				innerHtml +=
					'<tr class="align-center" data-id="' + val.id + '">'+
					'<td>' + val.id + '</td>'+
					'<td>' + val.name + '</td>'+
					'<td>' + DataFormat.formatGamePlatformStatus(val.status) + '</td>'+
					'<td>' + operate + '</td>'+
					'</tr>';
			});
			table.html(innerHtml);

			table.find('[data-command="enable"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				confirmUpdateStatus(id, 0);
			});
			table.find('[data-command="disable"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				confirmUpdateStatus(id, -1);
			});
		}

		var confirmUpdateStatus = function(id, status) {
			var msg;
			if (status == 1) {
				msg = "确定将该游戏平台启用吗？(客户端需联系游戏平台，本系统3分钟内生效)";
			}
			else {
				msg = "确定将该游戏平台禁用吗？(客户端需联系游戏平台，本系统3分钟内生效)";
			}
			
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doUpdateStatus(id, status);
						}
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
					}
				}
			});
		};

		var doUpdateStatus = function(id, status) {
			var params = {id : id, status : status};
			var url = './game/platform/mod-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('操作成功！', '操作提示');
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

		var reload = function() {
			loadData();
		}

		return {
			init: init,
			reload: reload
		}
	}();
	
	return {
		init: function() {
			GamePlatformTable.init();
		}
	}
}();