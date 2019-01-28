var LotteryUser = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var UserTable = function() {
		var tableList = $('#table-user-list');
		var tablePagelist = tableList.find('.page-list');
		var $userLevels = $('.page-breadcrumb', tableList);
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var matchType = tableList.find('select[name="matchType"]').val();
			var registTime = tableList.find('input[name="registTime"]').val();
			var minTotalMoney = tableList.find('input[name="minTotalMoney"]').val();
			var maxTotalMoney = tableList.find('input[name="maxTotalMoney"]').val();
			var minLotteryMoney = tableList.find('input[name="minLotteryMoney"]').val();
			var maxLotteryMoney = tableList.find('input[name="maxLotteryMoney"]').val();
			var minCode = tableList.find('input[name="minProxyCode"]').val();
			var maxCode = tableList.find('input[name="maxProxyCode"]').val();
			var sortColoum = tableList.find('select[name="sortColoum"]').val();
			var sortType = tableList.find('select[name="sortType"]').val();
			var aStatus = tableList.find('select[name="aStatus"]').val();
			var bStatus = tableList.find('select[name="bStatus"]').val();
			var onlineStatus = tableList.find('input[name="online"]').is(':checked') ? 1 : '';
		/*	var nickname = tableList.find('input[name="nickname"]').is(':checked') ? '' : '';*/
			var type = tableList.find('select[name="type"]').val();
			return {username: username, matchType: matchType, registTime: registTime, minTotalMoney: minTotalMoney, maxTotalMoney: maxTotalMoney, minLotteryMoney : minLotteryMoney, maxLotteryMoney : maxLotteryMoney, minCode: minCode, maxCode: maxCode, sortColoum: sortColoum, sortType: sortType, aStatus: aStatus, bStatus: bStatus, onlineStatus: onlineStatus, type: type};
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list, data) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var statusAction = '<a data-command="status" data-status="' + val.AStatus + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 冻结</a>';
					if(val.AStatus < 0) {
						statusAction = '<a data-command="status" data-status="' + val.AStatus + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 解冻</a>';
					}
					var userType = DataFormat.formatUserType(val.type);
						userType = '<a href="./lottery-user-profile?username=' + val.username + '">'+userType+'</a>';
					
				
					
					innerHtml +=
					'<tr class="align-center" data-name="' + val.username + '">'+
						'<td><a data-command="listLower" href="javascript:;">' + val.username + '</a></td>'+
						'<td><span></span> <a data-command="online" href="javascript:;" class="tippy" title="点击统计团队在线人数">点击</a></td>'+
						'<td>' + userType + '</td>'+
						'<td>' + val.totalMoney.toFixed(1) + '</td>'+
						'<td>' + val.lotteryMoney.toFixed(1) + '</td>'+
						'<td>' + val.code + ' / ' + val.locatePoint.toFixed(2) + '</td>'+
						'<td>' + (val.loginTime ? val.loginTime : '从未登录过') + '</td>'+
						'<td>' + DataFormat.formatUserAStatus(val.AStatus) + '</td>'+
						'<td>' + DataFormat.formatUserBStatus(val.BStatus) + '</td>'+
						'<td>' + DataFormat.formatOnlineStatus(val.onlineStatus) + '</td>'+
						'<td><a data-command="recharge" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-sign-in"></i> 充值 </a>' + statusAction + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);

				showUserLevels(data.userLevels);
				
				table.find('[data-command="listLower"]').unbind('click').click(function() {
					var username = $(this).parents('tr').attr('data-name');
					tableList.find("input[name=username]").val(username);
					tableList.find("select[name=matchType]").val("LOWER");
					reload();
				});

				table.find('[data-command="recharge"]').unbind('click').click(function() {
					var username = $(this).parents('tr').attr('data-name');
					RechargeModal.show(username);
				});

				table.find('[data-command="status"]').unbind('click').click(function() {
					var username = $(this).parents('tr').attr('data-name');
					var status = $(this).attr('data-status');
					if(status < 0) {
						if(status == -2) {
							toastr['error']('该账户已经被永久冻结，无法恢复正常！', '操作提示');
						} else {
							confirmUnlock(username);
						}
					} else {
						LockLotteryUserModal.show(username);
					}
				});
				
				table.find('[data-command="online"]').unbind('click').click(function() {
					table.find('[data-command="online"]').each(function() {
						var username = $(this).parents('tr').attr('data-name');
						var thisEls = $(this);
						var thisTd = thisEls.parent('td');
						showOnlineCount(username, thisEls, thisTd);
					});
				});
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
			emptyData: function(response) {
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');

				showUserLevels(response.userLevels);
			}
		});
		
		var confirmUnlock = function(username) {
			var msg = '确定解冻该用户？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							unlockUser(username);
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
		
		var showOnlineCount = function(username, thisEls, thisTd) {
			thisTd.find('span').html('统计中...');
			thisEls.hide();
			var params = {username: username};
			var url = './lottery-user/lower-online';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					thisTd.find('span').html(data.data + '人');
				}
			});
		}
		
		var unlockUser = function(username) {
			var params = {username: username};
			var url = './lottery-user/unlock';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('该账号已经恢复正常！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var showUserLevels = function (userLevels) {
			$userLevels.empty();

			if (userLevels) {
				$.each(userLevels, function(index, username){
					$userLevels.append('<li data-username="'+username+'"><a href="javascript:;" data-command="showLowers" data-username="'+username+'">'+username+'</a></li>');

					if (index < userLevels.length - 1) {
						$userLevels.append('<i class=\"fa fa-angle-double-right prl5\"></i>');
					}
				});
			}
		}

		tableList.on('click', '[data-command=showLowers]', function(){
			var thisUsername = $(this).attr('data-username');
			tableList.find('input[name="username"]').val(thisUsername);
			reload();
		});
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		
		tableList.find('[data-command="change-line"]').unbind('click').click(function() {
			ChangeLotteryUserModal.show();
		});
		
		tableList.find('[data-command="add"]').unbind('click').click(function() {
			AddLotteryUserModal.show();
		});
		
		tableList.find('[data-command="transfer"]').unbind('click').click(function() {
			UserTransferModal.show();
		});
		
		tableList.find('input[name="advanced"]').change(function() {
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
			tableList.find('input[name="minTotalMoney"]').val('');
			tableList.find('input[name="maxTotalMoney"]').val('');
			tableList.find('input[name="minLotteryMoney"]').val('');
			tableList.find('input[name="maxLotteryMoney"]').val('');
			tableList.find('input[name="minProxyCode"]').val('');
			tableList.find('input[name="maxProxyCode"]').val('');
			tableList.find('input[name="registTime"]').val('');
			tableList.find('select[name="type"]').val('');
		}
		
		isAdvanced();
		
		tableList.find('select[name="sortColoum"]').change(function() {
			isSort($(this));
		});
		
		var isSort = function(sortColoum) {
			if(!sortColoum) {
				sortColoum = tableList.find('select[name="sortColoum"]');
			}
			var sortType = tableList.find('select[name="sortType"]');
			if(sortColoum.val() == '') {
				sortType.hide();
			} else {
				sortType.show();
			}
		}
		
		isSort();
		
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
	
	/**
	 * 充值弹出窗口
	 */
	var RechargeModal = function() {
		var modal = $('#modal-recharge');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					type: {
						required: true
					},
					amount: {
						required: true,
						number: true,
						min: 0.01
					},
					withdrawPwd: {
						required: true
					},
					remarks: {
						required: true
					}
				},
				messages: {
					type: {
	                	required: '请选择类型！'
	                },
	                amount: {
	                	required: '金额不能为空！',
	                	number: '请填写正确金额！',
	                	min: '最低操作金额0.01元。'
	                },
					withdrawPwd: {
						required: '资金密码不能为空！'
					},
					remarks: {
						required: '说明不能为空！'
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
			modal.find('select[name="type"]').change(function() {
				var thisVal = $(this).val();
				if(thisVal == 1 || !thisVal) {
					modal.find('[data-hidden="account"]').hide();
					modal.find('[data-hidden="limit"]').hide();
				}
				else if(thisVal == 2) {
					modal.find('[data-hidden="account"]').show();
					modal.find('[data-hidden="limit"]').show();
					modal.find('select[name="account"]').find('option[value="1"]').hide();
					modal.find('select[name="account"]').find('option[value="2"]').attr('selected', 'selected');
				}
				else if(thisVal == 3 || thisVal == 4) {
					modal.find('[data-hidden="account"]').show();
					modal.find('[data-hidden="limit"]').hide();
				}
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var show = function(username) {
			form[0].reset();
			form.find('.help-inline').each(function() {
				if($(this).attr('data-default')) {
					$(this).html($(this).attr('data-default'));
				} else {
					$(this).empty();
				}
			});
			form.find('input[name="username"]').val(username);
			form.find('input[name="amount"]').val('');
			form.find('input[name="withdrawPwd"]').val('');
			form.find('input[name="remarks"]').val('');
			modal.find('select[name="type"]').trigger('change');
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var username = modal.find('input[name="username"]').val();
			var type = modal.find('select[name="type"]').val();
			var account = modal.find('select[name="account"]').val();
			var amount = modal.find('input[name="amount"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var limit = modal.find('input[name="limit"]').is(':checked') ? 1 : 0;
			isSending = true;

			$.ajax({
				type : 'post',
				url : './DisposableToken',
				data : {},
				dataType : 'json',
				success : function(tokenData) {
					isSending = false;
					if(tokenData.error == 0) {
						withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, tokenData.token);
						var params = {username: username, type: type, account: account, amount: amount, withdrawPwd: withdrawPwd, limit: limit, remarks: remarks};
						var url = './lottery-user-recharge/add';
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
									toastr['success']('用户充值成功！', '操作提示');
								}
								if(data.error == 1 || data.error == 2) {
									toastr['error']('用户充值失败！' + data.message, '操作提示');
								}
							}
						});
					}
					else {
						isSending = false;
						toastr['error']('请求超时，请重试！', '操作提示');
					}
				},
				error: function(){
					isSending = false;
					modal.modal('hide');
					toastr['error']('请求失败，请重试！', '操作提示');
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
	
	var AddLotteryUserModal = function() {
		var modal = $('#modal-lottery-user-add');
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
	
	var LockLotteryUserModal = function() {
		var modal = $('#modal-lottery-user-lock');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					message: {
						required: true
					}
				},
				messages: {
					message: {
						required: '请填写冻结原因！！'
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
			var username = modal.find('input[name="username"]').val();
			var status = modal.find('input[name="status"]:checked').val();
			var message = modal.find('input[name="message"]').val();
			var params = {username: username, status: status, message: message};
			var url = './lottery-user/lock';
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
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
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
			form.find('.help-inline').each(function() {
				if($(this).attr('data-default')) {
					$(this).html($(this).attr('data-default'));
				} else {
					$(this).empty();
				}
			});
			form.find('input[name="username"]').val(data);
			form.find('input[name="status"]').eq(1).attr("checked",true).trigger('click');
			Metronic.initAjax();
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
	
	var ChangeLotteryUserModal = function() {
		var modal = $('#modal-lottery-user-change');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					aUser: {
						required: true
					},
					bUser: {
						required: true
					}
				},
				messages: {
					aUser: {
						required: '用户名不能为空！'
					},
					bUser: {
						required: '用户名不能为空！'
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
			var msg = '<strong>转移操作系统会自动删除“待转移线路”会员及其所有下级的契约关系,“目标线路”不受影响,确认操作？</strong>';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							if(isSending) return;
							var type = modal.find('input[name="type"]:checked').val();
							var aUser = modal.find('input[name="aUser"]').val();
							var bUser = modal.find('input[name="bUser"]').val();
							var params = {type: type, aUser: aUser, bUser: bUser};
							var url = './lottery-user/change-line';
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
										toastr['success']('操作成功！', '操作提示');
									}
									if(data.error == 1 || data.error == 2) {
										toastr['error']('操作失败！' + data.message, '操作提示');
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
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
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
			form.find('input[name="type"]').eq(1).attr("checked",true).trigger('click');;
			Metronic.initAjax();
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
	
	var UserTransferModal = function() {
		var modal = $('#modal-user-transfer');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					aUser: {
						required: true
					},
					bUser: {
						required: true
					},
					remarks :{
						required: true
					},
					money: {
						required: true,
						number: true,
						min: 0.1,
						max: 1000000
					}
				},
				messages: {
					aUser: {
						required: '用户名不能为空！'
					},
					bUser: {
						required: '用户名不能为空！'
					},
					remarks: {
						required: '备注不能为空！'
					},
					money: {
						required: '转账金额不能为空！',
						number: '请填写数字',
						min: '最小{0}',
						max: '最大{0}'
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
			var msg = '确认转账？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							if(isSending) return;
							var aUser = modal.find('input[name="aUser"]').val();
							var bUser = modal.find('input[name="bUser"]').val();
							var money = modal.find('input[name="money"]').val();
							var remarks = modal.find('input[name="remarks"]').val();
							var params = {aUser: aUser, bUser: bUser, money: money,remarks:remarks};
							var url = './lottery-user/user-transfer';
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
										toastr['success']('操作成功！', '操作提示');
									}
									if(data.error == 1 || data.error == 2) {
										toastr['error']('操作失败！' + data.message, '操作提示');
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
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
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
			// Metronic.initAjax();
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
			UserTable.init();
			RechargeModal.init();
			AddLotteryUserModal.init();
			LockLotteryUserModal.init();
			ChangeLotteryUserModal.init();
			UserTransferModal.init();
			handelDatePicker();
		}
	}
}();