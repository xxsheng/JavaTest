var LotteryUserWithdraw = function() {
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

	var handleAutoRefreshCheckbox = function() {
		var withdrawAutoRefresh = $.cookie('WITHDRAW_AUTO_REFRESH');
		if (withdrawAutoRefresh == true || withdrawAutoRefresh == 'true') {
			$("input[name='autoRefresh']", "#table-user-withdraw-list").trigger("click");
			autoRefresh();
		}
	}

	var autoRefreshId;
	var autoRefresh = function() {
		clearAutoRefresh();

		autoRefreshId = setInterval(function(){
			UserWithdrawTable.reload();
		}, 10000);
	}

	var clearAutoRefresh = function(){
		if (autoRefreshId) {
			clearInterval(autoRefreshId);
		}
	}
	var UserWithdrawLogs = function() {
		var tableList = $('#user_withdraw_logs');
		var tablePagelist = tableList.find('.page-list');
		var _billno;
		var _username;

		var getParams = function() {
			return {username: _username, billno: _billno};
		}
		var pagination = $.pagination({
			render: tablePagelist,
			pageLength: 3,
			pageSize: 5,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-withdraw-log/list',
			ajaxData: getParams,
			beforeSend: function() {},
			complete: function() {},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					innerHtml +=
					'<tr class="align-center">'+
//						'<td>' + val.username + '</td>'+
						'<td>' + val.action + '</td>'+
						'<td>' + val.time + '</td>'+
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
		
		var show = function(billno, username){
			_username = username;
			_billno = billno;
			init();
		}
		
		return {
			init: init,
			show: show
		}
		
	}();
	var UserWithdrawTable = function() {
		var tableList = $('#table-user-withdraw-list');
		var tablePagelist = tableList.find('.page-list');
		var $loader = $("<div class='loader'></div>");
		var getSearchParams = function() {
			var type =  tableList.find('select[name="type"]').val();
			var billno = tableList.find('input[name="billno"]').val();
			var username = tableList.find('input[name="username"]').val();
			var minTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var maxTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			maxTime = getNetDate(maxTime);
			var minOperatorTime = tableList.find('[data-field="operatorTime"] > input[name="from"]').val();
			var maxOperatorTime = tableList.find('[data-field="operatorTime"] > input[name="to"]').val();
			maxOperatorTime = getNetDate(maxOperatorTime);
			var minMoney = tableList.find('input[name="minMoney"]').val();
			var maxMoney = tableList.find('input[name="maxMoney"]').val();
			var keyword = tableList.find('input[name="keyword"]').val();
			var status = tableList.find('select[name="status"]').val();
			var checkStatus = tableList.find('select[name="checkStatus"]').val();
			var remitStatus = tableList.find('select[name="remitStatus"]').val();
			var paymentChannelId = tableList.find('select[name="paymentChannelId"]').val();
			return {type:type,billno: billno, username: username, minTime: minTime, maxTime: maxTime, minOperatorTime: minOperatorTime, maxOperatorTime: maxOperatorTime, minMoney: minMoney, maxMoney: maxMoney, keyword: keyword, status: status, checkStatus: checkStatus, remitStatus: remitStatus, paymentChannelId: paymentChannelId};
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-withdraw/list',
			ajaxData: getSearchParams,
			beforeSend: function() {

			},
			complete: function() {

			},
			success: function(list, rsp) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var statusAction = '';
					var lockStatus = '未锁定';
					if(val.bean.status == 0) {
						if(val.bean.lockStatus == 0) {
							if(val.bean.checkStatus != 0) {
								if(val.bean.operatorUser == null || val.bean.operatorUser == '') {
									statusAction += '<a data-command="lock" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-lock"></i>锁定</a>';
								}
							}
						} 
						else if(val.bean.lockStatus == 1) {
							lockStatus = '<i class="fa fa-lock"></i>' + val.bean.operatorUser;
							if(LoginUser == val.bean.operatorUser) {
								// // 0：未处理；1：银行处理中；2：打款完成；3：第三方待处理；-1：请求失败；-2：打款失败；-3查询状态中；-4：未知状态；-5：第三方处理失败；-6：银行处理失败；-7：第三方拒绝支付
								if(val.bean.remitStatus == 0) {
									statusAction += '<a data-command="unlock" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-unlock"></i> 解锁</a>';
									statusAction += '<a data-command="pay" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-sign-out"></i> 支付</a>';
								}
								else {
									statusAction += '<a data-command="unlock" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-unlock"></i> 解锁</a>';
									statusAction += '<a data-command="completeRemit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 到账</a>';
								}
							}
						}
					}
					var formatMoney = '<a href="javascript:;" data-command="details" class="tippy" title="点击查看详情">' + BigDecimal.setMaxScale(val.bean.money, 0) + '</a>';
					innerHtml +=
						'<tr class="align-center" data-name="' + val.username + '" data-id="' + val.bean.id + '" data-billno="' + val.bean.billno + '">'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
						'<td>' + formatMoney + '</td>'+
						'<td>' + val.bean.afterMoney.toFixed(1) + '</td>'+
						'<td>' + val.bean.cardId + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + DataFormat.formatUserWithdrawStatus(val.bean.status) + '</td>'+
						'<td>' + lockStatus + '</td>'+
						'<td>' + DataFormat.formatUserWithdrawCheckStatus(val.bean.checkStatus) + '</td>'+
						'<td>' + DataFormat.formatUserWithdrawRemitStatus(val.bean.remitStatus) + '</td>'+
						'<td>' + statusAction + '<a data-command="check" href="/lottery-user-withdraw-check?id=' + val.bean.id + '" class="btn default btn-xs black"><i class="fa fa-question"></i>可疑</a></td>'+
						'</tr>';
				});
				table.html(innerHtml);
				$("#sumRecMoney").html(rsp.totalRecMoney.toFixed(2));
				$("#sumFeeMoney").html(rsp.totalFeeMoney.toFixed(2));
				table.find('[data-command="details"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var username = $(this).parents('tr').attr('data-name');
					var billno = $(this).parents('tr').attr('data-billno');
					UserWithdrawLogs.show(billno, username);
					doLoadDetails(id, doShowDetails);
				});

				table.find('[data-command="pay"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					doLoadPayDetails(id, doShowPayInfo);
				});

				table.find('[data-command="lock"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					lock(id);
				});

				table.find('[data-command="unlock"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					unlock(id);
				});

				table.find('[data-command="completeRemit"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					completeRemit(id);
				});
			},
			pageError: function(response) {
				$("#sumRecMoney").html('0.00');
				$("#sumFeeMoney").html('0.00');

				if (response.error != 0 && response.code == '2-6') {
					return;
				}
				
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
				$("#sumRecMoney").html('0.00');
				$("#sumFeeMoney").html('0.00');
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});

		var isLoading = false;
		var doLoadDetails = function(id, callback) {
			if(isLoading) return;
			var params = {id: id};
			var url = './lottery-user-withdraw/get';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						if($.isFunction(callback)) callback(data.data);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var isPayLoading = false;
		var doLoadPayDetails = function(id, callback) {
			if(isPayLoading) return;
			var params = {id: id};
			var url = './lottery-user-withdraw/pay-get';
			isPayLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isPayLoading = false;
					if(data.error == 0) {
						if($.isFunction(callback)) callback(data.data);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var detailsModal = $('#modal-user-withdraw-details');
		var doShowDetails = function(data) {
			detailsModal.find('[data-field="id"]').html(data.bean.id);
			detailsModal.find('[data-field="billno"]').html(data.bean.billno);
			detailsModal.find('[data-field="username"]').html(data.username);
			detailsModal.find('[data-field="money"]').html(data.bean.money.toFixed(2));
			detailsModal.find('[data-field="recMoney"]').html(data.bean.recMoney.toFixed(2));
			detailsModal.find('[data-field="feeMoney"]').html(data.bean.feeMoney.toFixed(2));
			detailsModal.find('[data-field="time"]').html(data.bean.time);
			detailsModal.find('[data-field="status"]').html(DataFormat.formatUserWithdrawStatus(data.bean.status));
			if(data.bean.status == 0) {
				detailsModal.find('section[data-hidden="pay"]').hide();
			}
			if(data.bean.status == 1) {
				detailsModal.find('section[data-hidden="pay"]').show();
			}
			detailsModal.find('[data-field="bankInfos"]').html(data.bean.bankName + ' ' + data.bean.bankBranch);
			detailsModal.find('[data-field="cardInfos"]').html(data.bean.cardName + ' ' + data.bean.cardId);
			detailsModal.find('[data-field="payBillno"]').html(data.bean.payBillno);
			detailsModal.find('[data-field="operatorInfos"]').html(data.bean.operatorUser + ' / ' + data.bean.operatorTime);
			detailsModal.find('[data-field="remarks"]').html(data.bean.remarks);
			detailsModal.modal('show');
		}

		var copyUsernameClipboard;
		var copyCardNameClipboard;
		var copyBankNameClipboard;
		var copyCardIdClipboard;
		var copyRecMoneyClipboard;
		var copyBillnoClipboard;
		var doShowPayInfo = function(data) {
			var thisModal = $('#modal-user-withdraw-pay');
			var thisTable = thisModal.find('table');
			$loader.remove();
			thisModal.find(".modal-footer").find('button[type="button"]').removeAttr("disabled");

			thisTable.find('[data-field]').html('');
			thisTable.find('input[type="text"]').val('');
			thisTable.find('select > option').eq(0).attr('selected', true);

			thisTable.find('[data-command="cancel"]').unbind('click');
			thisTable.find('[data-command="lock"]').unbind('click');
			thisTable.find('[data-command="confirm"]').unbind('click');
			thisTable.find('[data-command="refuse"]').unbind('click');
			thisTable.find('[data-command="gsyPay"]').unbind('click');

			thisTable.find('[data-field="username"]').html(data.username);
			thisTable.find('[data-field="username"]').append('<a data-clipboard-text="' + data.username + '" id="copyUsername" href="javascript:;" class="btn blue pull-right">复制</a>');
			thisTable.find('[data-field="cardName"]').html(data.bean.cardName);
			thisTable.find('[data-field="cardName"]').append('<a data-clipboard-text="' + data.bean.cardName + '" id="copyCardName" href="javascript:;" class="btn blue pull-right">复制</a>');
			thisTable.find('[data-field="bankName"]').html(data.bean.bankName);
			thisTable.find('[data-field="bankName"]').append('<a data-clipboard-text="' + data.bean.bankName + '" id="copyBankName" href="javascript:;" class="btn blue pull-right">复制</a>');
			thisTable.find('[data-field="cardId"]').html(data.bean.cardId);
			thisTable.find('[data-field="cardId"]').append('<a data-clipboard-text="' + data.bean.cardId + '" id="copyCardId" href="javascript:;" class="btn blue pull-right">复制</a>');
			thisTable.find('[data-field="recMoney"]').html(data.bean.recMoney.toFixed(2));
			thisTable.find('[data-field="recMoney"]').append('<a data-clipboard-text="' + data.bean.recMoney.toFixed(2) + '" id="copyRecMoney" href="javascript:;" class="btn blue pull-right">复制</a>');

			thisTable.find('[data-field="billno"]').html(data.bean.billno);
			thisTable.find('[data-field="billno"]').append('<a data-clipboard-text="' + data.bean.billno + '" href="javascript:;" id="copyBillno" class="btn blue pull-right">复制</a>');

			thisTable.find('[data-field="money"]').html(data.bean.money.toFixed(2));
			thisTable.find('[data-field="feeMoney"]').html(data.bean.feeMoney.toFixed(2));
			thisTable.find('[data-field="time"]').html(data.bean.time);
			thisTable.find('[data-field="status"]').html(DataFormat.formatUserWithdrawStatus(data.bean.status));
			thisTable.find('[data-field="lockStatus"]').html(DataFormat.formatUserWithdrawLockStatus(data.bean.lockStatus));
			thisTable.find('[data-field="checkStatus"]').html(DataFormat.formatUserWithdrawCheckStatus(data.bean.checkStatus));
			if(data.bean.lockStatus == 1) {
				thisTable.find('[data-field="lockStatus"]').append('（锁定人：' + data.bean.operatorUser + '）');
			}

			var intCopy = function(clipboard, els) {
				if (clipboard) clipboard.destroy();

				clipboard = new Clipboard(els, {
					container: document.getElementById('modal-user-withdraw-pay')
				});

				clipboard.on('success', function(e) {
					clipboard.destroy();
					var $target = $(els);
					$target.removeClass("blue").addClass("green disabled").text('已复制');
					toastr['success']('已成功复制到剪切板！', '操作提示');
				});
			}

			intCopy(copyUsernameClipboard, '#copyUsername');
			intCopy(copyCardNameClipboard, '#copyCardName');
			intCopy(copyBankNameClipboard, '#copyBankName');
			intCopy(copyCardIdClipboard, '#copyCardId');
			intCopy(copyRecMoneyClipboard, '#copyRecMoney');
			intCopy(copyBillnoClipboard, '#copyBillno');

			thisModal.find('[data-command="confirm"]').unbind('click').click(function() {
				var payBillno = thisTable.find('input[name="payBillno"]').val();
				var remarks = thisTable.find('input[name="remarks"]').val();
				var params = {id: data.bean.id, action: 'confirm', payBillno: payBillno, remarks: remarks};
				var payBillno = thisTable.find('input[name="payBillno"]').val();
				var remarks = thisTable.find('input[name="remarks"]').val();
				var params = {id: data.bean.id, action: 'confirm', payBillno: payBillno, remarks: remarks};
				var msg = '确定您已手动完成打款操作？';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								doManualPay(params, function() {
									thisModal.modal('hide');
									reload();
								});
							}
						},
						danger: {
							label: '<i class="fa fa-undo"></i> 取消',
							className: 'btn-danger',
							callback: function() {
							}
						}
					}
				});
			});

			thisModal.find('[data-command="refuse"]').unbind('click').click(function() {
				var reason = thisTable.find('select[name="reason"]').val();
				var remarks = thisTable.find('input[name="remarks"]').val();
				var params = {id: data.bean.id, action: 'refuse', reason: reason, remarks: remarks};
				if(reason == '' && remarks == '') {
					return toastr['error']('请选择理由或填写操作备注！', '操作提示');
				}

				var msg = '确认拒绝本次用户体现并将金额返还至用户账户？';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								doRefuse(params, function() {
									reload();
									thisModal.modal('hide');
								});
							}
						},
						danger: {
							label: '<i class="fa fa-undo"></i> 取消',
							className: 'btn-danger',
							callback: function() {
							}
						}
					}
				});
			});

			thisModal.find('[data-command="htPay"]').unbind('click').click(function() {
				var params = {id: data.bean.id};
				var msg = '确认使用<strong>汇通</strong>代付？请确认余额可供本次支付(PS:后台会自动填写支付流水号,出现异常后请前往第三方后台查询订单状态)';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						channel1: {
							label: '汇通QQ钱包代付',
							className: 'green-meadow',
							callback: function() {
								thisModal.append($loader);
								thisModal.find(".modal-footer").find('button[type="button"]').attr("disabled", true);
								params.channelCode = 'htQQ';
								// params.channelId = -1; // 如果有多个商户号，指定ID即可
								doAPIPay(params, function() {
									$loader.remove();
									thisModal.find(".modal-footer").find('button[type="button"]').removeAttr("disabled");
									thisModal.modal('hide');
									reload();
								});
							}
						},
						channel2: {
							label: '汇通微信代付',
							className: 'green-meadow',
							callback: function() {
								thisModal.append($loader);
								thisModal.find(".modal-footer").find('button[type="button"]').attr("disabled", true);
								params.channelCode = 'htWeChat';
								// params.channelId = -1; // 如果有多个商户号，指定ID即可
								doAPIPay(params, function() {
									$loader.remove();
									thisModal.find(".modal-footer").find('button[type="button"]').removeAttr("disabled");
									thisModal.modal('hide');
									reload();
								});
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

			thisModal.find('[data-command="yrPay"]').unbind('click').click(function() {
				var params = {id: data.bean.id};
				var msg = '确认使用<strong>易云</strong>代付？请确认余额可供本次支付(PS:后台会自动填写支付流水号,出现异常后请前往第三方后台查询订单状态)';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						channel1: {
							label: '代付',
							className: 'green-meadow',
							callback: function() {
								thisModal.append($loader);
								thisModal.find(".modal-footer").find('button[type="button"]').attr("disabled", true);
								params.channelCode = 'yr';
								// params.channelId = -1; // 如果有多个商户号，指定ID即可
								doAPIPay(params, function() {
									$loader.remove();
									thisModal.find(".modal-footer").find('button[type="button"]').removeAttr("disabled");
									thisModal.modal('hide');
									reload();
								});
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
			
			thisModal.find('[data-command="afPay"]').unbind('click').click(function() {
				var params = {id: data.bean.id};
				var msg = '确认使用<strong>艾付</strong>代付？请确认余额可供本次支付(PS:后台会自动填写支付流水号,出现异常后请前往第三方后台查询订单状态)';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						channel1: {
							label: '代付',
							className: 'green-meadow',
							callback: function() {
								thisModal.append($loader);
								thisModal.find(".modal-footer").find('button[type="button"]').attr("disabled", true);
								params.channelCode = 'af';
								// params.channelId = -1; // 如果有多个商户号，指定ID即可
								doAPIPay(params, function() {
									$loader.remove();
									thisModal.find(".modal-footer").find('button[type="button"]').removeAttr("disabled");
									thisModal.modal('hide');
									reload();
								});
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
			
			
			thisModal.find('[data-command="tgfPay"]').unbind('click').click(function() {
				var params = {id: data.bean.id};
				var msg = '确认使用<strong>天机付</strong>代付？请确认余额可供本次支付(PS:后台会自动填写支付流水号,出现异常后请前往第三方后台查询订单状态)';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						channel1: {
							label: '代付',
							className: 'green-meadow',
							callback: function() {
								thisModal.append($loader);
								thisModal.find(".modal-footer").find('button[type="button"]').attr("disabled", true);
								params.channelCode = 'tgf';
								// params.channelId = -1; // 如果有多个商户号，指定ID即可
								doAPIPay(params, function() {
									$loader.remove();
									thisModal.find(".modal-footer").find('button[type="button"]').removeAttr("disabled");
									thisModal.modal('hide');
									reload();
								});
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
			
			
			thisModal.find('[data-command="zsPay"]').unbind('click').click(function() {
				var params = {id: data.bean.id};
				var msg = '确认使用<strong>泽圣</strong>代付？请确认余额可供本次支付(PS:后台会自动填写支付流水号,出现异常后请前往第三方后台查询订单状态)';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						channel1: {
							label: '代付',
							className: 'green-meadow',
							callback: function() {
								thisModal.append($loader);
								thisModal.find(".modal-footer").find('button[type="button"]').attr("disabled", true);
								params.channelCode = 'zs';
								// params.channelId = -1; // 如果有多个商户号，指定ID即可
								doAPIPay(params, function() {
									$loader.remove();
									thisModal.find(".modal-footer").find('button[type="button"]').removeAttr("disabled");
									thisModal.modal('hide');
									reload();
								});
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

			thisModal.find('[data-command="fktPay"]').unbind('click').click(function() {
				var params = {id: data.bean.id};
				var msg = '确认使用<strong>福卡通</strong>代付？请确认余额可供本次支付(PS:后台会自动填写支付流水号,出现异常后请前往第三方后台查询订单状态)';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						channel1: {
							label: '福卡通网银代付',
							className: 'green-meadow',
							callback: function() {
								thisModal.append($loader);
								thisModal.find(".modal-footer").find('button[type="button"]').attr("disabled", true);
								params.channelCode = 'fkt';
								// params.channelId = -1; // 如果有多个商户号，指定ID即可
								doAPIPay(params, function() {
									$loader.remove();
									thisModal.find(".modal-footer").find('button[type="button"]').removeAttr("disabled");
									thisModal.modal('hide');
									reload();
								});
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

			thisModal.find('[data-command="htfPay"]').unbind('click').click(function() {
				var params = {id: data.bean.id};
				var msg = '确认使用<strong>汇天付</strong>代付？请确认余额可供本次支付(PS:后台会自动填写支付流水号,出现异常后请前往第三方后台查询订单状态)';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						channel1: {
							label: '汇天付网银代付',
							className: 'green-meadow',
							callback: function() {
								thisModal.append($loader);
								thisModal.find(".modal-footer").find('button[type="button"]').attr("disabled", true);
								params.channelCode = 'htf';
								// params.channelId = -1; // 如果有多个商户号，指定ID即可
								doAPIPay(params, function() {
									$loader.remove();
									thisModal.find(".modal-footer").find('button[type="button"]').removeAttr("disabled");
									thisModal.modal('hide');
									reload();
								});
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

			thisModal.modal('show');
		}
		var isSendingManualPay = false;
		var doManualPay = function(data, callback) {
			if(isSendingManualPay) return;
			isSendingManualPay = true;
			var url = './lottery-user-withdraw/manual-pay';
			$.ajax({
				type : 'post',
				url : url,
				data : data,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
					isSendingManualPay = false;
					if($.isFunction(callback)) callback(data.data);
				},
				error: function() {
					isSendingManualPay = false;
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
					if($.isFunction(callback)) callback();
				}
			});
		}

		var isSendingRefuse = false;
		var doRefuse = function(data, callback) {
			if(isSendingRefuse) return;
			isSendingRefuse = true;
			var url = './lottery-user-withdraw/refuse';
			$.ajax({
				type : 'post',
				url : url,
				data : data,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
					if($.isFunction(callback)) callback();
					isSendingRefuse = false;
				},
				error: function() {
					isSendingManualPay = false;
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
					if($.isFunction(callback)) callback();
				}
			});
		}

		var lock = function(id) {
			var params = {id: id};
			var url = './lottery-user-withdraw/lock';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('已成功锁定，请尽快处理！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var unlock = function(id) {
			var params = {id: id};
			var url = './lottery-user-withdraw/unlock';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('已成功解锁！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var completeRemit = function(id) {
			var params = {id: id, action: 'complete-remit'};
			var msg = '确认该笔打款已经处理成功或是失败？';
			var thisModal =$("#modal-user-account-pay");
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确认已成功',
						className: 'green-meadow',
						callback: function() {
							doCompleteRemit(params, function() {
								reload();
							});
						}
					},
					warning: {
						label: '确认已失败',
						className: 'btn-danger',
						callback: function() {
							//thisModal.modal('show');
//							$("#modal-bodySuccess").find("span").hide();
							//ModifyEmailModal();//
							$("#id").val(id);
							ModifyEmailModal.show();
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

		var doCompleteRemit = function(data, callback) {
			var url = './lottery-user-withdraw/complete-remit';
			$.ajax({
				type : 'post',
				url : url,
				data : data,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						toastr['success']('操作成功！', '操作提示');
						
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
					if($.isFunction(callback)) callback();
				},
				error: function() {
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
					if($.isFunction(callback)) callback();
				}
			});
		}

		var isSendingAPIPay = false;
		var doAPIPay = function(params, callback) {
			if(isSendingAPIPay) return;
			isSendingAPIPay = true;
			var url = './lottery-user-withdraw/api-pay';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error'](data.message, '操作提示');
					}
					if($.isFunction(callback)) callback(data.data);
				},
				complete: function() {
					isSendingAPIPay = false;
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
			var operatorDateTime = tableList.find('[data-field="operatorTime"]');
			operatorDateTime.find('input[name="from"]').val('');
			operatorDateTime.find('input[name="to"]').val('');
			tableList.find('select[name="checkStatus"]').val('');
			tableList.find('select[name="remitStatus"]').val('');
			tableList.find('select[name="paymentChannelId"]').val('');
		}

		isAdvanced();

		var loadAPIPayChannels = function() {
			var url = './lottery-payment-channel/simple-list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					buildAPIPayChannels(data);
				}
			});
		}

		var buildAPIPayChannels = function(data) {
			var $select = tableList.find('select[name="paymentChannelId"]');
			$select.empty();
			$select.append('<option value="">全部</option>');
			$select.append('<option value="-1">手动出款</option>');
			$.each(data.data, function(idx, val) {
				if (val.apiPayStatus == 0) {
					$select.append('<option value="' + val.id + '">使用' + val.name + '代付</option>');
				}
			});
			handleSelect();
		}
		loadAPIPayChannels();

		var init = function() {
			pagination.init();

			tableList.find('input[name="autoRefresh"]').unbind('change').change(function() {
				var checked = $(this).is(':checked');
				if (checked == true) {
					autoRefresh();
					var expires = new Date(moment().startOf('year').add(1, 'years'));
					$.cookie('WITHDRAW_AUTO_REFRESH', true, { expires: expires, path: '/' });
				}
				else {
					clearAutoRefresh();
					var expires = new Date(moment().startOf('year').add(1, 'years'));
					$.cookie('WITHDRAW_AUTO_REFRESH', false, { expires: expires, path: '/' });
				}
			});
		}
		var reload = function() {
			pagination.reload();
		}

		return {
			init: init,
			reload: reload
		}
	}();
	var ModifyEmailModal = function() {
		var modal = $('#modal-user-account-pay');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					message: {
						required: true,
					}
				},
				messages: {
					message: {
						required: '操作备注不能为空！',
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
//                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var message = modal.find('input[name="message"]').val();
			var id=$("#id").val();
			var action="withdrawFailure";
			var params = {id:id,action:action,remarks: message};
			isSending = true;
			var url = './lottery-user-withdraw/withdraw-failure';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						modal.modal('hide');
						UserWithdrawTable.reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
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
	return {
		init: function() {
			var yesterday = moment().add(-1, 'days').format('YYYY-MM-DD');
			var tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');
			var tableList = $('#table-user-withdraw-list');
			tableList.find('[data-field="time"] > input[name="from"]').val(yesterday);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);
			ModifyEmailModal.init();
			UserWithdrawTable.init();
			handleSelect();
			handelDatePicker();
			handleAutoRefreshCheckbox();
		}
	}
}();