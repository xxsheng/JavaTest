var VipUpgradeList = function() {
	
	var AddLotteryUserModal = function() {
		var modal = $('#modal-vip-user-add');
		var form = modal.find('form');
		modal.find('[data-command="submit"]').unbind('click').click(function() {
			doSubmit();
		});
	 
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var username = modal.find('input[name="username"]').val();
			var vipLevel = modal.find('input[name="vipLevel"]:checked').val();
			var params = {username: username, vipLevel: vipLevel};
			var url = './vip-upgrade-list/teyao';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						modal.modal('hide');
						toastr['success']('用户添加成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('用户添加失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var show = function() {
			form[0].reset();
			form.find('.help-inline').each(function() {
				if($(this).attr('data-default')) {
					$(this).html($(this).attr('data-default'));
				} else {
					$(this).empty();
				}
			});
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}
		
		var init = function() {
			initForm();
		}
		
		return {
			init: init,
			show: show
		}
		
	}();
	
	var ThisTableList = function() {
		var tableList = $('#vip-upgrade-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var month = tableList.find('select[name="month"]').val();
			return {username: username, month: month};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './vip-upgrade-list/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var formatType = val.bean.beforeLevel < val.bean.afterLevel ? '晋级' : '降级';
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + val.bean.id + '</td>'+
						'<td>' + val.username + '</td>'+
						'<td>' + DataFormat.formatUserVipLevel(val.bean.beforeLevel) + '</td>'+
						'<td>' + formatType + '</td>'+
						'<td>' + DataFormat.formatUserVipLevel(val.bean.afterLevel) + '</td>'+
						'<td>' + val.bean.recharge + '</td>'+
						'<td>' + val.bean.cost + '</td>'+
						'<td>' + val.bean.month + '</td>'+
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
		
		var doCalculate = function() {
			var url = './vip-upgrade-list/calculate';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					isSending = false;
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
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		tableList.find('[data-command="addVip"]').unbind('click').click(function() {
			AddLotteryUserModal.show();
		});
		
		tableList.find('[data-command="calculate"]').unbind('click').click(function() {
			var msg = '确定手动检测？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doCalculate();
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
		
		var initMonth = function() {
			var month = tableList.find('select[name="month"]');
			month.empty();
			for (var i = 0; i < 12; i++) {
				var thisMonth = moment().subtract(i, 'months');
				month.append('<option value="' + thisMonth.format('YYYY-MM') + '">' + thisMonth.format('YYYY年MM月') + '</option>');
			}
		}
		initMonth();
		
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
		}
	}
}();