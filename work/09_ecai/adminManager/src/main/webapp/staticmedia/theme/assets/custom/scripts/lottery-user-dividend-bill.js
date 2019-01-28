var LotteryDividendBill = function() {
	var isLoading = false;
	var dividendDesc = " <abbr title='计算公式：分红比例 × 本次共亏损(需小于0) - 下级共需发放'>?</abbr>";

	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true
		});
	}

	var UserDividendBillTable = function() {
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
			ajaxUrl: './lottery-user-dividend-bill/list',
			ajaxData: getSearchParams,
			beforeSend: function() {

			},
			complete: function() {

			},
			success: function(list, rsp) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var formatId = '<a href="javascript:;" data-val="' + val.username + '" data-command="showLower" title="点击查看直属下级分红情况">' + val.username + '</a>';
					var formatOperate = '';
					if (val.bean.status == 2) { // 待审核
						formatOperate += '<a data-command="approve" data-id="' + val.bean.id + '" href="javascript:;" class="btn default btn-xs black">审核</a>';
					}
					else {
						formatOperate += '<a href="javascript:;" class="btn default btn-xs black" disabled="disabled">审核</a>';
					}

					if (val.bean.status == 6) { // 余额不足清零欠账
						formatOperate += '<a data-command="reset" data-id="' + val.bean.id + '" href="javascript:;" class="btn default btn-xs black">清零</a>';
					}
					else {
						formatOperate += '<a href="javascript:;" class="btn default btn-xs black" disabled="disabled">清零</a>';
					}

					// formatOperate += '<a data-command="del" data-id="' + val.bean.id + '" href="javascript:;" class="btn default btn-xs black">删除</a>';

					var trClass = '';
					var trTitle = '';
					if (val.bean.lowerTotalAmount > 0
							&& val.bean.lowerPaidAmount < val.bean.lowerTotalAmount
						    && (val.bean.status == 2 || val.bean.status == 6)) { // 2：待审核；6：余额不足
						trClass = 'danger';
						trTitle = '需向下级补发';
					}
					else if (val.bean.issueType == 2
							&& (val.bean.status == 2 || val.bean.status == 3 || val.bean.status == 7)) { // 2：待审核；3：待领取；7：部分领取
						trClass = 'info';
						trTitle = '由上级发放';
					}

					var scale = '<a data-command="details" data-id="' + val.bean.id + '" href="javascript:;" title="点击查看详情">'+Number(val.bean.scale)+'%</a>';

					innerHtml +=
						'<tr class="align-center '+trClass+'" data-id="' + val.bean.id + '" title="'+trTitle+'">'+
						'<td>' + formatId +'</td>'+
						'<td>' + scale + '</td>'+
						'<td>' + val.bean.validUser + '/' + val.bean.minValidUser + '</td>'+
						'<td>' + val.bean.thisLoss.toFixed(1) + '/' + val.bean.billingOrder.toFixed(1) + '</td>'+
						'<td>' + val.bean.dailyBillingOrder.toFixed(1) + '</td>'+
						'<td>' + val.bean.userAmount.toFixed(1) + '/' + val.bean.calAmount.toFixed(1) + '</td>'+
						'<td>' + val.bean.lowerPaidAmount.toFixed(1) + '/' + val.bean.lowerTotalAmount.toFixed(1) + '</td>'+
						'<td>' + val.bean.indicateStartDate + '~' + val.bean.indicateEndDate + '</td>'+
						'<td>' + DataFormat.formatDividendBillIssueType(val.bean.issueType) + '</td>'+
						'<td>' + DataFormat.formatDividendBillStatus(val.bean.status) + '</td>'+
						'<td>' + formatOperate + '</td>'+
						'</tr>';
				});
				table.html(innerHtml);
				$("#platformTotalLoss").html(rsp.platformTotalLoss.toFixed(1));
				$("#platformTotalUserAmount").html(rsp.platformTotalUserAmount.toFixed(1));
				$("#upperTotalUserAmount").html(rsp.upperTotalUserAmount.toFixed(1));
				table.find('[data-command="showLower"]').unbind('click').click(function() {
					var upUsername = $(this).attr('data-val');
					resetParamsForUpUser(upUsername);
					UserDividendBillTable.init();
				});
				table.find('[data-command="approve"]').unbind('click').click(function() {
					var id = $(this).attr('data-id');
					doLoadDividendBillData(id, 'approve');
				});
				table.find('[data-command="del"]').unbind('click').click(function() {
					var id = $(this).attr('data-id');
					doLoadDividendBillData(id, 'del');
				});
				table.find('[data-command="details"]').unbind('click').click(function() {
					var id = $(this).attr('data-id');
					doLoadDividendBillDetails(id);
				});
				table.find('[data-command="reset"]').unbind('click').click(function() {
					var id = $(this).attr('data-id');
					doResetDividendBill(id);
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
				$("#platformTotalLoss").html("0");
				$("#platformTotalUserAmount").html("0");
				$("#upperTotalUserAmount").html("0");
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});

		var doLoadDividendBillData = function(id, operation) {
			if(isLoading) return;
			var params = {id: id};
			var url = './lottery-user-dividend-bill/get';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						EditDividendModal.show(data.data, operation);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var doLoadDividendBillDetails = function(id) {
			if(isLoading) return;
			var params = {id: id};
			var url = './lottery-user-dividend-bill/get';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						doShowDividendBillDetails(data.data);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var doResetDividendBill = function(id) {
			if(isLoading) return;
			var params = {id: id};
			var url = './lottery-user-dividend-bill/get';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						ResetDividendModal.show(data.data);
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

		tableList.find('[data-command="showLossDetails"]').unbind('click').click(function() {
			var searchParams = getSearchParams();
			TotalLossDetailsTable.show(searchParams.username, searchParams.sTime, searchParams.eTime, searchParams.minUserAmount, searchParams.maxUserAmount);
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

	var EditDividendModal = function() {
		var modal = $('#modal-approve-dividend');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					withdrawPwd: {
						required: true
					}
				},
				messages: {
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
					var msg = '<strong>确认要同意发放该笔分红吗？（编辑后的数据用户可见）</strong></br></br><strong style="color:red;">注：如需要向下级发放分红，审核后系统将立即扣除该用户账户余额给下级(有多少扣多少，不含PT和AG)，如仍然余额不足，将锁定用户取款、转账操作，同时期间产生的所有收益将自动扣发给下级，直至扣发完成。</strong>';
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
					var msg = '<strong>确认要彻底删除该团队本次分红数据吗？（该操作不可恢复）</strong>';
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
			form.find('[data-field="scale"]').html((data.bean.scale) + "%");
			form.find('[data-field="validUser"]').html(data.bean.validUser + '/' + data.bean.minValidUser);
			form.find('[data-field="dailyBillingOrder"]').html(data.bean.dailyBillingOrder.toFixed(1));
			form.find('[data-field="billingOrder"]').html(data.bean.billingOrder.toFixed(1));
			form.find('[data-field="thisLoss"]').html(data.bean.thisLoss.toFixed(1));
			form.find('[data-field="lastLoss"]').html(data.bean.lastLoss.toFixed(1));
			form.find('[data-field="totalLoss"]').html(data.bean.totalLoss.toFixed(1));
			form.find('[data-field="period"]').html(data.bean.indicateStartDate + '~' + data.bean.indicateEndDate);
			form.find('[data-field="status"]').html(DataFormat.formatDividendBillStatus(data.bean.status));
			form.find('[data-field="lowerTotalAmount"]').html(data.bean.lowerTotalAmount.toFixed(1));
			form.find('[data-field="lowerPaidAmount"]').html(data.bean.lowerPaidAmount.toFixed(1));
			form.find('[data-field="calAmount"]').html(data.bean.calAmount.toFixed(1));
			form.find('[data-field="userAmount"]').html(data.bean.userAmount.toFixed(1));
			form.find('[data-field="availableAmount"]').html(data.bean.availableAmount.toFixed(1));
			form.find('[data-field="totalReceived"]').html(data.bean.totalReceived.toFixed(1));
			form.find('[data-field="issueType"]').html(DataFormat.formatDividendBillIssueType(data.bean.issueType));
			form.find('[data-field="settleTime"]').html(data.bean.settleTime);
			form.find('input[name="remarks"]').val(data.bean.remarks);
			form.find('input[name="withdrawPwd"]').val('');
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');

			if ("approve" == operation) {
				modal.find('[data-command="agree"]').show();
				modal.find('[data-command="deny"]').show();
				modal.find('[data-command="del"]').hide();
			}
			else {
				modal.find('[data-command="agree"]').hide();
				modal.find('[data-command="deny"]').hide();
				modal.find('[data-command="del"]').show();
			}

			modal.modal('show');
		}

		var isSending = false;
		var doAgree = function() {
			if(isSending) return;
			var id = modal.find('input[name="id"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();

			var token = $.getDisposableToken();
			withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, token);

			var params = {id: id, remarks: remarks, withdrawPwd: withdrawPwd};
			var url = './lottery-user-dividend-bill/agree';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						modal.modal('hide');
						UserDividendBillTable.reload();
						toastr['success']('同意发放分红成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('同意发放分红失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
					modal.modal('hide');
				},
				error: function() {
					isSending = false;
					modal.modal('hide');
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
				}
			});
		}

		var doDeny = function() {
			if(isSending) return;
			var id = modal.find('input[name="id"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();

			var token = $.getDisposableToken();
			withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, token);

			var params = {id: id, remarks: remarks, withdrawPwd: withdrawPwd};
			var url = './lottery-user-dividend-bill/deny';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						modal.modal('hide');
						UserDividendBillTable.reload();
						toastr['success']('拒绝发放分红成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('拒绝发放分红失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
					modal.modal('hide');
				},
				error: function() {
					isSending = false;
					modal.modal('hide');
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
				}
			});
		}

		var doDel = function(id) {
			if(isSending) return;
			var id = modal.find('input[name="id"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();

			var token = $.getDisposableToken();
			withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, token);

			var params = {id: id, remarks: remarks, withdrawPwd: withdrawPwd};
			var url = './lottery-user-dividend-bill/del';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						modal.modal('hide');
						UserDividendBillTable.reload();
						toastr['success']('删除分红数据成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('删除分红数据失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
					modal.modal('hide');
				},
				error: function() {
					isSending = false;
					modal.modal('hide');
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
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

	var ResetDividendModal = function() {
		var modal = $('#modal-reset-dividend');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					withdrawPwd: {
						required: true
					},
					remarks: {
						required: true
					}
				},
				messages: {
					withdrawPwd: {
						required: '资金密码不能为空！'
					},
					remarks: {
						required: '备注不能为空！'
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

			modal.find('[data-command="reset"]').unbind('click').click(function() {
				if(form.validate().form()) {
					var msg = '<strong>确认要将该用户分红欠账清零吗？</strong></br></br><strong style="color:red;">注：清零后将解锁用户及契约分红下级取款、转账等操作，收益也将不再自动扣发给下级，同时系统会修改本周期其所有下级分红账单为已发放。该操作不会给任何用户加减钱，清零后需由用户手动补发给下级，原下级待领取的金额，也将不再可以领取</strong>';
					bootbox.dialog({
						message: msg,
						title: '确认消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									doReset();
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

		var show = function(data) {
			form.find('[data-field="username"]').html(data.username);
			form.find('input[name="id"]').val(data.bean.id);
			form.find('[data-field="lowerTotalAmount"]').html(data.bean.lowerTotalAmount.toFixed(4));
			form.find('[data-field="lowerPaidAmount"]').html(data.bean.lowerPaidAmount.toFixed(4));
			form.find('[data-field="availableAmount"]').html(data.bean.availableAmount.toFixed(4));
			form.find('[data-field="totalReceived"]').html(data.bean.totalReceived.toFixed(4));
			form.find('[data-field="issueType"]').html(DataFormat.formatDividendBillIssueType(data.bean.issueType));
			form.find('input[name="remarks"]').val('');
			form.find('input[name="withdrawPwd"]').val('');
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}

		var isSending = false;
		var doReset = function() {
			if(isSending) return;
			var id = modal.find('input[name="id"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();

			var token = $.getDisposableToken();
			withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, token);

			var params = {id: id, remarks: remarks, withdrawPwd: withdrawPwd};
			var url = './lottery-user-dividend-bill/reset';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						modal.modal('hide');
						UserDividendBillTable.init();
						toastr['success']('清零契约分红成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('清零契约分红失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
					modal.modal('hide');
				},
				error: function() {
					isSending = false;
					modal.modal('hide');
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
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

	var TotalLossDetailsTable = function() {
		var tableList = $('#modal-total-loss-details');
		var tablePagelist = tableList.find('.page-list');

		var _username;
		var _sTime;
		var _eTime;
		var _minUserAmount;
		var _maxUserAmount;

		var getSearchParams = function() {
			return {username: _username, sTime: _sTime, eTime: _eTime, minUserAmount: _minUserAmount, maxUserAmount: _maxUserAmount};
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-dividend-bill/platform-loss-list',
			ajaxData: getSearchParams,
			beforeSend: function() {
			},
			complete: function() {
			},
			success: function(list, rsp) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					innerHtml +=
						'<tr>'+
						'<td>' + val.username + '</td>'+
						'<td>' + DataFormat.formatLevelUsers(val.username, val.userLevels) +'</td>'+
						'<td>' + val.bean.lastLoss.toFixed(1) + '</td>'+
						'<td>' + val.bean.thisLoss.toFixed(1) + '</td>'+
						'<td>' + val.bean.totalLoss.toFixed(1) + '</td>'+
						'<td>' + val.bean.billingOrder.toFixed(1) + '</td>'+
						'<td>' + parseInt((val.bean.scale)) + '%</td>'+
						'<td>' + val.bean.calAmount.toFixed(1) + '</td>'+
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

		var init = function() {
			pagination.init();
		}

		var reload = function() {
			pagination.reload();
		}

		var show = function(username, sTime, eTime, minUserAmount, maxUserAmount){
			_username = username;
			_sTime = sTime;
			_eTime = eTime;
			_minUserAmount = minUserAmount;
			_maxUserAmount = maxUserAmount;
			init();
			tableList.modal('show');
		}

		return {
			init: init,
			reload: reload,
			show: show
		}
	}();

	var detailsModal = $('#modal-details-dividend');
	var doShowDividendBillDetails = function(data) {
		detailsModal.find('[data-field="username"]').html(data.username);
		detailsModal.find('[data-field="scale"]').html((data.bean.scale) + "%");
		detailsModal.find('[data-field="validUser"]').html(data.bean.validUser + '/' + data.bean.minValidUser);
		detailsModal.find('[data-field="dailyBillingOrder"]').html(data.bean.dailyBillingOrder.toFixed(1));
		detailsModal.find('[data-field="billingOrder"]').html(data.bean.billingOrder.toFixed(1));
		detailsModal.find('[data-field="thisLoss"]').html(data.bean.thisLoss.toFixed(1));
		detailsModal.find('[data-field="lastLoss"]').html(data.bean.lastLoss.toFixed(1));
		detailsModal.find('[data-field="totalLoss"]').html(data.bean.totalLoss.toFixed(1));
		detailsModal.find('[data-field="period"]').html(data.bean.indicateStartDate + '~' + data.bean.indicateEndDate);
		detailsModal.find('[data-field="status"]').html(DataFormat.formatDividendBillStatus(data.bean.status));
		detailsModal.find('[data-field="lowerTotalAmount"]').html(data.bean.lowerTotalAmount.toFixed(1));
		detailsModal.find('[data-field="lowerPaidAmount"]').html(data.bean.lowerPaidAmount.toFixed(1));
		detailsModal.find('[data-field="calAmount"]').html(data.bean.calAmount.toFixed(1));
		detailsModal.find('[data-field="userAmount"]').html(data.bean.userAmount.toFixed(1));
		detailsModal.find('[data-field="availableAmount"]').html(data.bean.availableAmount.toFixed(1));
		detailsModal.find('[data-field="totalReceived"]').html(data.bean.totalReceived.toFixed(1));
		detailsModal.find('[data-field="issueType"]').html(DataFormat.formatDividendBillIssueType(data.bean.issueType));
		detailsModal.find('[data-field="settleTime"]').html(data.bean.settleTime);
		detailsModal.find('[data-field="remarks"]').html(data.bean.remarks);

		detailsModal.modal('show');
	};
	return {
		init: function() {
			var startDate = moment().add(-31, 'days').format('YYYY-MM-DD');
			var endDate = moment().add(1, 'days').format('YYYY-MM-DD');
			var tableList = $('#table-dividend-bill-list');
			tableList.find('[data-field="time"] > input[name="sTime"]').val(startDate);
			tableList.find('[data-field="time"] > input[name="eTime"]').val(endDate);
			
			UserDividendBillTable.init();
			EditDividendModal.init();
			ResetDividendModal.init();
			handelDatePicker();
		}
	}
}();