var GameDividendBill = function() {
	var isLoading = false;
	var dividendDesc = " <abbr title='计算公式：分红比例 × 本次共亏损(需小于0)'>?</abbr>";

	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true
		});
	}

	var UserGameDividendBillTable = function() {
		var tableList = $('#table-dividend-bill-list');
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
			ajaxUrl: './user-game-dividend-bill/list',
			ajaxData: getSearchParams,
			beforeSend: function() {

			},
			complete: function() {

			},
			success: function(list, rsp) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var formatId = '<a href="javascript:;" data-val="' + val.username + '" data-command="showLower" title="点击查看下级分红情况">' + val.username + '</a>';
					var formatOperate = '';
					if (val.bean.status == 2) { // 待审核
						formatOperate += '<a data-command="approve" data-id="' + val.bean.id + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 审核</a>';
					}
					else {
						formatOperate += '<a href="javascript:;" class="btn default btn-xs black" disabled="disabled"><i class="fa fa-check"></i> 审核</a>';
					}

					formatOperate += '<a data-command="del" data-id="' + val.bean.id + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-remove"></i> 删除</a>';
					formatOperate += '<a data-command="details" data-id="' + val.bean.id + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详情</a>';
					innerHtml +=
						'<tr class="align-center" data-id="' + val.bean.id + '">'+
						'<td>' + val.bean.id + '</td>'+
						'<td>' + formatId +'</td>'+
						'<td>' + parseInt((val.bean.scale * 100)) + '%</td>'+
						'<td>' + val.bean.billingOrder.toFixed(4) + '</td>'+
						'<td>' + val.bean.totalLoss.toFixed(4) + '</td>'+
						'<td>' + val.bean.userAmount.toFixed(4) + '</td>'+
						'<td>' + val.bean.indicateStartDate + '~' + val.bean.indicateEndDate + '</td>'+
						'<td>' + val.bean.settleTime + '</td>'+
						'<td>' + DataFormat.formatGameDividendBillStatus(val.bean.status) + '</td>'+
						'<td>' + formatOperate + '</td>'+
						'</tr>';
				});
				table.html(innerHtml);
				$("#totalUserAmount").html(rsp.totalUserAmount.toFixed(4));
				table.find('[data-command="showLower"]').unbind('click').click(function() {
					var upUsername = $(this).attr('data-val');
					resetParamsForUpUser(upUsername);
					UserGameDividendBillTable.init();
				});
				table.find('[data-command="approve"]').unbind('click').click(function() {
					var id = $(this).attr('data-id');
					doLoadGameDividendBillData(id, 'approve');
				});
				table.find('[data-command="del"]').unbind('click').click(function() {
					var id = $(this).attr('data-id');
					doLoadGameDividendBillData(id, 'del');
				});
				table.find('[data-command="details"]').unbind('click').click(function() {
					var id = $(this).attr('data-id');
					doLoadGameDividendBillDetails(id);
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
				$("#totalUserAmount").html("0.0000");
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});

		var doLoadGameDividendBillData = function(id, operation) {
			if(isLoading) return;
			var params = {id: id};
			var url = './user-game-dividend-bill/get';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						EditGameDividendModal.show(data.data, operation);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var doLoadGameDividendBillDetails = function(id) {
			if(isLoading) return;
			var params = {id: id};
			var url = './user-game-dividend-bill/get';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						doShowGameDividendBillDetails(data.data);
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

		var reload = function() {
			pagination.reload();
		}

		return {
			init: init,
			reload: reload
		}
	}();

	var EditGameDividendModal = function() {
		var modal = $('#modal-approve-dividend');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					userAmount: {
						required: true,
						number: true
					},
					withdrawPwd: {
						required: true
					}
				},
				messages: {
					userAmount: {
						required: '用户金额不能为空！',
						number: '请填写正确金额！',
						min: '最低操作金额0.00元。'
					},
					withdrawPwd: {
						required: '资金密码不能为空！'
					}
				},
				invalidHandler: function (event, validator) {},
				errorPlacement: function (error, element) {
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
				},
				highlight: function (element) {
					$(element).closest('.form-group').removeClass('has-success').addClass('has-error');
				},
				unhighlight: function (element) {
					$(element).closest('.form-group').removeClass('has-error').addClass('has-success');
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
				}
			});
			modal.find('[data-command="agree"]').unbind('click').click(function() {
				if(form.validate().form()) {
					var userAmount = form.find('input[name="userAmount"]').val();
					var msg = '<strong>确认要同意发放该笔分红吗？（编辑后的数据用户可见）</strong></br></br><strong style="color:red;">注：确认发放后用户即可领取。</strong>';
					bootbox.dialog({
						message: msg,
						title: '确认消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									doAgree();
								}
							},
							danger: {
								label: '<i class="fa fa-undo"></i> 取消',
								className: 'btn-danger',
								callback: function() {}
							}
						}
					});
				}
			});
			modal.find('[data-command="deny"]').unbind('click').click(function() {
				if(form.validate().form()) {
					var msg = '<strong>确认要拒绝发放该笔分红吗？（编辑后的数据用户可见）</strong>';
					bootbox.dialog({
						message: msg,
						title: '确认消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									doDeny();
								}
							},
							danger: {
								label: '<i class="fa fa-undo"></i> 取消',
								className: 'btn-danger',
								callback: function() {}
							}
						}
					});
				}
			});
			modal.find('[data-command="del"]').unbind('click').click(function() {
				if(form.validate().form()) {
					var msg = '<strong>确认要彻底删除该笔分红数据吗？（该操作不可恢复）</strong>';
					bootbox.dialog({
						message: msg,
						title: '确认消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									doDel();
								}
							},
							danger: {
								label: '<i class="fa fa-undo"></i> 取消',
								className: 'btn-danger',
								callback: function() {}
							}
						}
					});
				}
			});
		}

		var show = function(data, operation) {
			form.find('[data-field="username"]').html(data.username);
			form.find('input[name="id"]').val(data.bean.id);
			form.find('[data-field="scale"]').html((data.bean.scale * 100) + "%");
			form.find('[data-field="billingOrder"]').html(data.bean.billingOrder.toFixed(4));
			form.find('[data-field="thisLoss"]').html(data.bean.thisLoss.toFixed(4));
			form.find('[data-field="lastLoss"]').html(data.bean.lastLoss.toFixed(4));
			form.find('[data-field="totalLoss"]').html(data.bean.totalLoss.toFixed(4));
			form.find('[data-field="period"]').html(data.bean.indicateStartDate + '~' + data.bean.indicateEndDate);
			form.find('[data-field="status"]').html(DataFormat.formatGameDividendBillStatus(data.bean.status));
			form.find('input[name="userAmount"]').val(data.bean.userAmount.toFixed(4));
			form.find('input[name="remarks"]').val(data.bean.remarks);
			form.find('input[name="withdrawPwd"]').val('');
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');

			if ("approve" == operation) {
				form.find('input[name="userAmount"]').removeAttr("disabled");
				modal.find('[data-command="agree"]').show();
				modal.find('[data-command="deny"]').show();
			}
			else {
				form.find('input[name="userAmount"]').attr("disabled", "disabled");
				modal.find('[data-command="agree"]').hide();
				modal.find('[data-command="deny"]').hide();
			}

			modal.modal('show');
		}

		var isSending = false;
		var doAgree = function() {
			if(isSending) return;
			var id = modal.find('input[name="id"]').val();
			var userAmount = modal.find('input[name="userAmount"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();
			var params = {id: id, userAmount: userAmount, remarks: remarks, withdrawPwd: withdrawPwd};
			var url = './user-game-dividend-bill/agree';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						modal.modal('hide');
						UserGameDividendBillTable.reload();
						toastr['success']('同意发放分红成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('同意发放分红失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
				}
			});
		}

		var doDeny = function() {
			if(isSending) return;
			var id = modal.find('input[name="id"]').val();
			var userAmount = modal.find('input[name="userAmount"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();
			var params = {id: id, userAmount: userAmount, remarks: remarks, withdrawPwd: withdrawPwd};
			var url = './user-game-dividend-bill/deny';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						modal.modal('hide');
						UserGameDividendBillTable.reload();
						toastr['success']('拒绝发放分红成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('拒绝发放分红失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
				}
			});
		}

		var doDel = function(id) {
			if(isSending) return;
			var id = modal.find('input[name="id"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();
			var params = {id: id, remarks: remarks, withdrawPwd: withdrawPwd};
			var url = './user-game-dividend-bill/del';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						modal.modal('hide');
						UserGameDividendBillTable.reload();
						toastr['success']('删除分红数据成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('删除分红数据失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
				}
			});
		}

		var init = function() {
			initForm();
		}

		return {
			init: init,
			show: show
		}
	}();

	var detailsModal = $('#modal-details-dividend');
	var doShowGameDividendBillDetails = function(data) {
		detailsModal.find('[data-field="username"]').html(data.username);
		detailsModal.find('[data-field="scale"]').html((data.bean.scale * 100) + "%");
		detailsModal.find('[data-field="billingOrder"]').html(data.bean.billingOrder.toFixed(4));
		detailsModal.find('[data-field="thisLoss"]').html(data.bean.thisLoss.toFixed(4));
		detailsModal.find('[data-field="lastLoss"]').html(data.bean.lastLoss.toFixed(4));
		detailsModal.find('[data-field="totalLoss"]').html(data.bean.totalLoss.toFixed(4));
		detailsModal.find('[data-field="period"]').html(data.bean.indicateStartDate + '~' + data.bean.indicateEndDate);
		detailsModal.find('[data-field="status"]').html(DataFormat.formatDividendBillStatus(data.bean.status));
		detailsModal.find('[data-field="userAmount"]').html(data.bean.userAmount.toFixed(4) + dividendDesc);
		detailsModal.find('[data-field="remarks"]').html(data.bean.remarks);

		detailsModal.modal('show');
	};
	return {
		init: function() {
			var startDate = moment().add(-30, 'days').format('YYYY-MM-DD');
			var endDate = moment().format('YYYY-MM-DD');
			var tableList = $('#table-dividend-bill-list');
			tableList.find('[data-field="time"] > input[name="sTime"]').val(startDate);
			tableList.find('[data-field="time"] > input[name="eTime"]').val(endDate);
			
			UserGameDividendBillTable.init();
			EditGameDividendModal.init();
			handelDatePicker();
		}
	}
}();