var LotteryOpenTime = function() {
	
	var LotteryOpenTimeTable = function() {
		var tableList = $('#table-lottery-open-time-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var lottery = tableList.find('select[name="lottery"]').val();
			var expect = tableList.find('input[name="expect"]').val();
			return {lottery: lottery, expect: expect};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 200,
			ajaxType: 'post',
			ajaxUrl: './lottery-open-time/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list, data) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';

				if (data.lottery && data.lottery.expectTrans == 1) {
					tableList.find('[data-command="ref-expect-modify"]').show();
				}
				else {
					tableList.find('[data-command="ref-expect-modify"]').hide();
				}

				$.each(list, function(idx, val) {
					innerHtml +=
					'<tr class="align-center" data-id="' + val.bean.id + '">'+
						'<td>' + val.lottery + '</td>'+
						'<td>' + val.bean.expect + '</td>'+
						'<td>' + val.bean.startTime + '</td>'+
						'<td>' + val.bean.stopTime + '</td>'+
						'<td>' + val.bean.openTime + '</td>'+
						'<td><a data-idx="' + idx + '" data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 修改 </a></td>'+
					'</tr>';
				});
				table.html(innerHtml);
				table.find('[data-command="edit"]').unbind('click').click(function() {
					var idx = $(this).attr('data-idx');
					ModifyLotteryOpenTimeModal.show(list[idx]);
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
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		
		tableList.find('[data-command="batch-modify"]').unbind('click').click(function() {
			var lottery = tableList.find('select[name="lottery"]').val();
			BatchModifyLotteryOpenTimeModal.show(lottery);
		});

		tableList.find('[data-command="ref-expect-modify"]').unbind('click').click(function() {
			var lottery = tableList.find('select[name="lottery"]').val();
			ModifyLotteryRefExpectModal.show(lottery);
		});

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
				lottery.append('<option value="' + val.bean.shortName + '">' + val.bean.showName + '</option>');
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
			pagination.init();
			handleSelect();
		}
		
		return {
			init: init
		}
	}();
	
	var ModifyLotteryOpenTimeModal = function() {
		var modal = $('#modal-lottery-open-time-modify');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					startTime: {
						required: true
					},
					stopTime: {
						required: true
					}
				},
				messages: {
					startTime: {
	                    required: '请输入开始时间！'
	                },
	                stopTime: {
	                	required: '请输入结束时间！'
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
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var id = modal.attr('data-id');
			var startTime = modal.find('input[name="startTime"]').val();
			var stopTime = modal.find('input[name="stopTime"]').val();
			var params = {id: id, startTime: startTime, stopTime: stopTime};
			var url = './lottery-open-time/modify';
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
						LotteryOpenTimeTable.init();
						toastr['success']('开奖时间修改成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('开奖时间修改失败！' + data.message, '操作提示');
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
		
		var show = function(data) {
			if(!data) return;
			form[0].reset();
			// data set up
			modal.attr('data-id', data.bean.id);
			modal.find('input[name="startTime"]').val(data.bean.startTime);
			modal.find('input[name="stopTime"]').val(data.bean.stopTime);
			
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
	
	var BatchModifyLotteryOpenTimeModal = function() {
		var modal = $('#modal-lottery-open-time-batch-modify');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					seconds: {
						required: true,
						number: true
					}
				},
				messages: {
					seconds: {
	                    required: '请输入调整秒数！',
	                    number: '请输入整数！'
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
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var lottery = modal.attr('data-lottery');
			var seconds = modal.find('input[name="seconds"]').val();
			var params = {lottery: lottery, seconds: seconds};
			var url = './lottery-open-time/batch-modify';
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
						LotteryOpenTimeTable.init();
						toastr['success']('开奖时间修改成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('开奖时间修改失败！' + data.message, '操作提示');
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
		
		var show = function(data) {
			if(!data) return;
			form[0].reset();
			// data set up
			modal.attr('data-lottery', data);
			
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

	var ModifyLotteryRefExpectModal = function() {
		var modal = $('#modal-ref-expect-modify');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					times: {
						required: true,
						number: true
					}
				},
				messages: {
					times: {
	                    required: '请输入增减期数！',
	                    number: '请输入整数！'
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
		}

		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var lottery = modal.attr('data-lottery');
			var times = modal.find('input[name="times"]').val();
			var params = {lottery: lottery, times: times};
			var url = './lottery-open-time/modify-ref-expect';
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
						LotteryOpenTimeTable.init();
						toastr['success']('开奖期号增减成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('开奖期号增减失败！' + data.message, '操作提示');
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

		var show = function(data) {
			if(!data) return;
			form[0].reset();
			// data set up
			modal.attr('data-lottery', data);

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

	var handleSelect = function() {
		$('.bs-select').selectpicker({
			iconBase: 'fa',
			tickIcon: 'fa-check'
		});
		$('.bs-select').selectpicker('refresh');
	}
	
	return {
		init: function() {
			ModifyLotteryOpenTimeModal.init();
			BatchModifyLotteryOpenTimeModal.init();
			ModifyLotteryRefExpectModal.init();
			handleSelect();
		}
	}
}();