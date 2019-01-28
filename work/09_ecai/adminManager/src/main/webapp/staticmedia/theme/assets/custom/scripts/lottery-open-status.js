var LotteryOpenStatus = function() {
	
	var LotteryOpenStatusTable = function() {
		var tableList = $('#table-lottery-open-status-list');
		
		var getSearchParams = function() {
			var lotteryId = tableList.find('select[name="lottery"]').val();
			var $date = tableList.find('input[name="date"]');
			var date = $date.val();
			if(date == '') {
				date = moment().format('YYYY-MM-DD');
				$date.val(date);
			}
			return {lotteryId: lotteryId, date: date};
		}
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './lottery-open-status/list';
			var params = getSearchParams();
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						buildData(data.data, data.thisOpentime);
					}
				}
			});
		}
		
		var buildData = function(data, thisOpentime) {
			var table = tableList.find('table > tbody').empty();
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var isCurrExpect = val.openTime.expect == thisOpentime.expect;
				var time = val.openCode ? val.openCode.openTime : '-';
				var code = val.openCode ? val.openCode.code : '-';
				innerHtml +=
				'<tr class="align-center' + (isCurrExpect ? ' highlight' : '') + '" data-lottery="' + val.lottery.shortName + '" data-expect="' + val.openTime.expect + '">'+
					'<td>' + val.lottery.showName + '</td>'+
					'<td>' + val.openTime.expect + (isCurrExpect ? '（当前期）' : '') + '</td>'+
					'<td>' + val.openTime.openTime + '</td>'+
					'<td>' + code + '</td>'+
					'<td>' + time + '</td>'+
					'<td><a data-command="correct" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 修正号码</a><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 删除号码</a></td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="manual"]').unbind('click').click(function() {
				var lottery = $(this).parents('tr').attr('data-lottery');
				var expect = $(this).parents('tr').attr('data-expect');
				var msg = '确定进行手动结算？';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								checkOpenCode(lottery, expect);
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
			
			table.find('[data-command="correct"]').unbind('click').click(function() {
				var lottery = $(this).parents('tr').attr('data-lottery');
				var expect = $(this).parents('tr').attr('data-expect');
				var lotteryName = tableList.find('select[name="lottery"]').find('option:selected').text();
				CorrectLotteryOpenCodeModal.show({
					lottery: lottery, 
					lotteryName: lotteryName,
					expect: expect
				});
			});
			
			table.find('[data-command="delete"]').unbind('click').click(function() {
				var lottery = $(this).parents('tr').attr('data-lottery');
				var expect = $(this).parents('tr').attr('data-expect');
				var msg = '确定删除改号码？';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								deleteOpenCode(lottery, expect);
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
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		
		var deleteOpenCode = function(lottery, expect) {
			var params = {lottery: lottery, expect: expect};
			var url = './lottery-open-code/delete';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						toastr['success']('操作成功！' + data.message, '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var checkOpenCode = function(lottery, expect) {
			var params = {lottery: lottery, expect: expect};
			var url = './lottery-open-code/get';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						if(data.data) {
							// 有开奖号码，可以直接通知
							doManualControl(lottery, expect, null);
						} else {
							var lotteryName = tableList.find('select[name="lottery"]').find('option:selected').text();
							AddLotteryOpenCodeModal.show({
								lottery: lottery, 
								lotteryName: lotteryName,
								expect: expect
							});
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var doManualControl = function(lottery, expect) {
			var params = {lottery: lottery, expect: expect};
			var url = './lottery-open-status/manual-control';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						toastr['success']('通知开奖器结算成功，请稍等片刻等待结果！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var isLoading = false;
		var loadLottery = function() {
			var type = tableList.find('select[name="lotteryType"]').val();
			if(isLoading) return;
			var url = './lottery/list';
			var params = {type: type};
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					buildLottery(data);
				}
			});
		}
		
		var buildLottery = function(data) {
			var lottery = tableList.find('select[name="lottery"]');
			lottery.empty();
			$.each(data, function(idx, val) {
				lottery.append('<option value="' + val.bean.id + '">' + val.bean.showName + '</option>');
			});
			lottery.unbind('change').change(function() {
				init();
			});
			init();
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
				loadLottery();
			});
			loadLottery();
		}
		
		loadLotteryType();
		
		var init = function() {
			loadData();
			handleSelect();
		}
		
		return {
			init: init
		}
	}();
	
	var AddLotteryOpenCodeModal = function() {
		var modal = $('#modal-lottery-opencode-add');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					opencode: {
						required: true
					}
				},
				messages: {
					opencode: {
	                    required: '开奖号码不能为空！'
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
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
			modal.find('[data-command="self-lottery"]').unbind('click').click(function() {
				 doSelfLottery();
			});
		}
		
		
		var isSendingSelfLottery = false;
		
		var doSelfLottery = function(){
			if(isSendingSelfLottery) return;
			var lottery = modal.attr('data-lottery');
			var expect = modal.find('input[name="expect"]').val();
			var params = {lottery: lottery, expect: expect, group:"SELF_LOTTERY"};
			var url = './lottery-open-code/sleflottery/open';
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
						LotteryOpenStatusTable.init();
						toastr['success']('正在开奖，请勿重复补开！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('补开失败，请重试' + data.message, '操作提示');
					}
				}
			});
		};
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var lottery = modal.attr('data-lottery');
			var expect = modal.find('input[name="expect"]').val();
			var code = modal.find('input[name="code"]').val();
			var params = {lottery: lottery, expect: expect, code: code};
			var url = './lottery-open-code/add';
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
						LotteryOpenStatusTable.init();
						toastr['success']('开奖号码添加成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('开奖号码添加失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
					modal.modal('hide');
				}
			});
		}
		
		var show = function(data) {
			if(!data) return;
			form[0].reset();
			if(data) {
				modal.attr('data-lottery', data.lottery);
				form.find('input[name="lotteryName"]').val(data.lotteryName);
				form.find('input[name="expect"]').val(data.expect);
			}
			form.find('.help-inline').empty();
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
	
	/**
	 * 修正开奖号码 
	 */
	var CorrectLotteryOpenCodeModal = function() {
		var modal = $('#modal-lottery-opencode-correct');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					opencode: {
						required: true
					}
				},
				messages: {
					opencode: {
	                    required: '开奖号码不能为空！'
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
			
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doCorrectOpenCodeSubmit();
		    	}
			});
		}
		
		var isSendingCorrectOpenCode = false;
		
		var doCorrectOpenCodeSubmit = function() {
			if(isSendingCorrectOpenCode) return;
			var lottery = modal.attr('data-lottery');
			var expect = modal.find('input[name="expect"]').val();
			var code = modal.find('input[name="code"]').val();
			var moneyPwd = modal.find('input[name="moneypwd"]').val();
			isSendingCorrectOpenCode = true;

			$.ajax({
				type : 'post',
				url : './DisposableToken',
				data : {},
				dataType : 'json',
				success : function(tokenData) {
					if(tokenData.error == 0) {
						moneyPwd = $.encryptPasswordWithToken(moneyPwd, tokenData.token);
						var params = {lottery: lottery, expect: expect, code: code, moneyPwd:moneyPwd};
						var url = './lottery-open-code/correct';
						$.ajax({
							type : 'post',
							url : url,
							data : params,
							dataType : 'json',
							success : function(data) {
								isSendingCorrectOpenCode = false;
								if(data.error == 0) {
									modal.modal('hide');
									LotteryOpenStatusTable.init();
									toastr['success']('开奖号码修正成功！', '操作提示');
								}
								if(data.error == 1 || data.error == 2) {
									toastr['error']('开奖号码修正失败！' + data.message, '操作提示');
								}
							},
							error: function(){
								isSendingCorrectOpenCode = false;
								toastr['error']('请求失败，请稍候再试！', '操作提示');
							}
						});

					}
					else {
						isSendingCorrectOpenCode = false;
						toastr['error']('请求超时，请重试！', '操作提示');
					}
				},
				error: function(){
					isSendingCorrectOpenCode = false;
					toastr['error']('请求失败，请重试！', '操作提示');
				},
				complete: function() {
					modal.modal('hide');
				},
				error: function() {
					isSendingCorrectOpenCode = false;
					modal.modal('hide');
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
				}
			});
		}
		
		var show = function(data) {
			if(!data) return;
			form[0].reset();
			if(data) {
				modal.attr('data-lottery', data.lottery);
				form.find('input[name="lotteryName"]').val(data.lotteryName);
				form.find('input[name="expect"]').val(data.expect);
			}
			form.find('.help-inline').empty();
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
	
	var handelDatePicker = function() {
		$('input.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	handelDatePicker();
	
	var handleSelect = function() {
		$('.bs-select').selectpicker({
			iconBase: 'fa',
			tickIcon: 'fa-check'
		});
		$('.bs-select').selectpicker('refresh');
	}
	
	return {
		init: function() {
			AddLotteryOpenCodeModal.init();
			CorrectLotteryOpenCodeModal.init();
		}
	}
}();