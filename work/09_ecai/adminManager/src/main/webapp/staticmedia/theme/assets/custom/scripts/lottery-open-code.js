var LotteryOpenCode = function() {
	
	var LotteryOpenCodeTable = function() {
		var tableList = $('#table-lottery-open-code-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var lottery = tableList.find('select[name="lottery"]').val();
			var expect = tableList.find('input[name="expect"]').val();
			return {lottery: lottery, expect: expect};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-open-code/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var username = val.username ? val.username : "全部用户";
					innerHtml +=
						'<tr class="align-center">'+
						'<td>' + val.lotteryName + '</td>'+
						'<td>' + val.bean.expect + '</td>'+
						'<td>' + username + '</td>'+
						'<td>' + val.bean.code + '</td>'+
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
		
		tableList.find('[data-command="hand-code"]').unbind('click').click(function() {
			HandOpenModal.show();
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
	
	var HandOpenModal = function() {
		var modal = $('#modal-hand-open');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					username: {
						required: true,
						minlength: 6,
						maxlength: 12,
						remote: {
							url: './lottery-user/check-exist',
							type: 'post'
						}
					},
					nickname: {
						required: true,
						minlength: 6,
						maxlength: 12
					},
					password1: {
						required: true,
						minlength: 6,
						maxlength: 20
					},
					password2: {
						required: true,
						minlength: 6,
						maxlength: 20,
						equalTo: 'input[name="password1"]'
					},
					locatePoint: {
						required: true,
						number: true,
						min: 0
					},
					upperUser: {
						required: true,
						minlength: 2,
						maxlength: 20
					}
				},
				messages: {
					username: {
	                    required: '用户名不能为空！',
	                    minlength: '至少输入{0}个字符',
	                    maxlength: '最多输入{0}个字符',
	                    remote: '用户名已存在！'
	                },
	                nickname: {
						required: '用户昵称不能为空！',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					},
	                password1: {
	                    required: '用户密码不能为空！',
	                    minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
	                },
	                password2: {
	                    required: '确认密码不能为空！',
	                    equalTo: '两次密码不一致！',
	                    minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
	                },
	                locatePoint: {
						required: '返点不能为空！',
						number: '返点必须为数字！',
						min: '返点不能小于0！'
					},
					upperUser: {
						required: '上级代理不能为空！',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
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
			modal.find('input[name="type"]').unbind('change').change(function() {
				if($(this).val() == 3) {
					modal.find('input[name="locatePoint"]').val('2.0').attr('disabled', true);
					modal.find('input[name="upperUser"]').val('yf').attr('disabled', true);
					modal.find('[data-hidden="relatedUsers"]').show();
				}
				else {
					modal.find('input[name="locatePoint"]').val('').removeAttr('disabled');
					modal.find('input[name="upperUser"]').val('').removeAttr('disabled');
					modal.find('[data-hidden="relatedUsers"]').hide();
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
			var username = modal.find('input[name="username"]').val();
			var nickname = modal.find('input[name="nickname"]').val();
			var password = modal.find('input[name="password1"]').val();
			password = $.generatePassword(password);
			var type = modal.find('input[name="type"]:checked').val();
			var locatePoint = modal.find('input[name="locatePoint"]').val();
			var upperUser = modal.find('input[name="upperUser"]').val();
			var relatedUsers = modal.find('input[name="relatedUsers"]').val();
			if (type == 3 || type == '3') {
				if (relatedUsers == null || relatedUsers.trim() == '') {
					toastr['error']('请输入关联会员,多个会员使用英文逗号分割！', '操作提示');
					return;
				}
			}
			var params = {username: username, nickname: nickname, password: password, type: type, locatePoint: locatePoint, upperUser: upperUser, relatedUsers: relatedUsers};
			var url = './lottery-user/add';
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
						UserTable.reload();
						toastr['success']('用户添加成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('用户添加失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
				},
				error: function() {
					isSending = false;
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
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
			form.find('input[name="type"]').eq(0).attr("checked",true).trigger('click');
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			
			var loadSelfLottery = function(){
				$.ajax({
					type : 'post',
					url : './lottery/list',
					data : {type: selfLotteryType.val()},
					dataType : 'json',
					success : function(data) {
						var selfLottery = form.find('select[name="selfLottery"]');
						selfLottery.empty();
						$.each(data, function(idx, val) {
							selfLottery.append('<option value="' + val.bean.shortName + '">' + val.bean.showName + '</option>');
						});
					}
				});
			}
			$.ajax({
				type : 'post',
				url : './lottery-type/list',
				data : {},
				dataType : 'json',
				success : function(data) {
					var selfLotteryType = form.find('select[name="selfLotteryType"]');
					$.each(data, function(idx, val) {
						selfLotteryType.append('<option value="' + val.id + '">' + val.name + '</option>');
					});
					selfLotteryType.change(function() {
						loadSelfLottery();
					});
					
				}
			});
			
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
			LotteryOpenCodeTable.init();
		}
	}
}();