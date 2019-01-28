var Lottery = function() {
	
	var LotteryTable = function() {
		var tableList = $('#table-lottery-list');
		
		var isLoading = false;
		var loadData = function() {
			var lotteryType = tableList.find('select[name="lotteryType"]').val();
			if(isLoading) return;
			var url = './lottery/list';
			var params = {type: lotteryType};
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
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
					'<td>' + val.bean.id + '</td>'+
					'<td>' + val.bean.showName + '</td>'+
					'<td>' + val.bean.shortName + '</td>'+
					'<td>' + val.lotteryType + '</td>'+
					'<td>' + val.bean.times + '</td>'+
					'<td>' + val.bean.expectMaxPrize + '</td>'+
					'<td>' + val.bean.dantiaoMaxPrize + '</td>'+
					'<td>' + DataFormat.formatLotteryStatus(val.bean.status) + '</td>'+
					'<td>' + statusAction + '</td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定禁用该彩种？(禁用后用户不能进行投注操作，已投注的不影响。)';
				if(status == -1) {
					msg = '确定启用该彩种？';
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
			var url = './lottery/update-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						loadData();
						if(status == 0) {
							toastr['success']('该彩种已恢复正常状态！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该彩种已禁用！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var isLotteryTypeLoading = false;
		var loadLotteryType = function() {
			if(isLotteryTypeLoading) return;
			var url = './lottery-type/list';
			isLotteryTypeLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					isLotteryTypeLoading = false;
					buildLotteryType(data);
				}
			});
		}
		
		var buildLotteryType = function(data) {
			var lotteryType = tableList.find('select[name="lotteryType"]');
			$.each(data, function(idx, val) {
				lotteryType.append('<option value="' + val.id + '">' + val.name + '</option>');
			});
			lotteryType.change(function() {
				loadData();
			});
			handleSelect();
		}
		
		loadLotteryType();
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var handleSelect = function() {
		$('.bs-select').selectpicker({
			iconBase: 'fa',
			tickIcon: 'fa-check'
		});
		$('.bs-select').selectpicker('refresh');
	}
	
	return {
		init: function() {
			LotteryTable.init();
		}
	}
}();