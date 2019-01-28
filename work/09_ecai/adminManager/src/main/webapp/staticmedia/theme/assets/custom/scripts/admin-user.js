var AdminUser = function() {

	var AdminUserTable = function() {
		var tableList = $('#table-admin-user-list');

		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './admin-user/list';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					buildData(data.data);
				}
			});
		}

		var buildData = function(data) {
			var table = tableList.find('table > tbody').empty();
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var loginTime = val.bean.loginTime == '' ? '从未登录过' : val.bean.loginTime;
				var statusAction = '<a data-command="status" data-status="' + val.bean.status + '" href="javascript:;" class="btn default btn-xs black">禁用</a>';
				if(val.bean.status == -1) {
					statusAction = '<a data-command="status" data-status="' + val.bean.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
				}
				statusAction += '<a data-command="edit" href="javascript:;" class="btn default btn-xs black">编辑</a>';
				var googleBind = '未绑定';
				if(val.bean.isValidate == 1 && val.bean.secretKey){
					var closeGoogleAuth = '<a data-command="closeGoogleAuth"  href="javascript:;" class="btn default btn-xs black">关闭谷歌</a>';
					statusAction += closeGoogleAuth;
					googleBind = '已绑定';
				}

				var setWithdrawPwd;
				if (val.bean.withdrawPwd == 'notset') {
					setWithdrawPwd = '已关闭';
					statusAction += '<a data-command="openWithdrawPwd"  href="javascript:;" class="btn default btn-xs black">开启资金密码</a>';
				}
				else {
					setWithdrawPwd = '已开启';
					statusAction += '<a data-command="closeWithdrawPwd"  href="javascript:;" class="btn default btn-xs black">关闭资金密码</a>';
				}

				var trClass = '';
				var trTitle = '';
				if(val.bean.pwd_error > 0) {
					statusAction += '<a data-command="resetPwdError" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-recycle" aria-hidden="true"></i> 重置密码错误次数 </a>';
					if(val.bean.pwd_error >= 3) {
						trClass = 'danger';
						trTitle = '连续登录密码错误3次,已禁止登录';
					}
				}

				innerHtml +=
					'<tr class="align-center '+trClass+'" title="'+trTitle+'" data-id="' + val.bean.id + '" data-username="' + val.bean.username + '">'+
					'<td>' + val.bean.username + '</td>'+
					'<td>' + val.role + '</td>'+
					'<td>' + val.bean.registTime + '</td>'+
					'<td>' + loginTime + '</td>'+
					'<td>' + val.bean.pwd_error + '</td>'+
					'<td>' + googleBind + '</td>'+
					'<td>' + DataFormat.formatAdminUserStatus(val.bean.status) + '</td>'+
					'<td>' + setWithdrawPwd + '</td>'+
					'<td>' + statusAction + '</td>'+
					'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="closeGoogleAuth"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				bootbox.dialog({
					message: '是否要关闭用户Google动态密码?',
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								closeGoogleAuth(id);
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

			table.find('[data-command="edit"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var url = './admin-user/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						AddUserModal.show(data);
					}
				});
			});
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定禁用该账号？';
				if(status == -1) {
					msg = '确定启用该账号？';
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

			table.find('[data-command="resetPwdError"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				bootbox.dialog({
					message: "确认清零该用户密码错误次数",
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								resetPwdError(id);
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

			table.find('[data-command="openWithdrawPwd"]').unbind('click').click(function() {
				var username = $(this).parents('tr').attr('data-username');
				OpenWithdrawPWDModal.show(username);
			});

			table.find('[data-command="closeWithdrawPwd"]').unbind('click').click(function() {
				var username = $(this).parents('tr').attr('data-username');
				CloseWithdrawPWDModal.show(username);
			});
		}

		var resetPwdError = function(id){
			var url = './admin-user/reset-pwd-error';
			var params = {id: id};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0){
						toastr['success']('重置用户密码错误次数成功！', '操作提示');
						loadData();
					}else if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var closeGoogleAuth = function(id){
			var url = './admin-user/close-google-auth';
			var params = {id: id};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0){
						toastr['success']('Google口令关闭成功！', '操作提示');
						loadData();
					}else if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './admin-user/update-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						loadData();
						if(status == 0) {
							toastr['success']('该账号已启用！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该账号已禁用！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		tableList.find('[data-command="add"]').unbind('click').click(function() {
			AddUserModal.show();
		});

		var init = function() {
			loadData();
		}

		return {
			init: init
		}
	}();

	var AddUserModal = function() {
		var modal = $('#modal-admin-user-add');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					username: {
						required: true,
						minlength: 5,
						remote: {
							url: './admin-user/check-exist',
							type: 'post'
						}
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
					withdrawPwd1: {
						required: true,
						minlength: 6,
						maxlength: 20
					},
					withdrawPwd2: {
						required: true,
						minlength: 6,
						maxlength: 20,
						equalTo: 'input[name="withdrawPwd1"]'
					},
					googleCode: {
						required: true,
						digits: true,
						minlength: 6,
						maxlength: 6
					}
				},
				messages: {
					username: {
						required: '用户名不能为空！',
						minlength: '至少输入{0}个字符',
						remote: '用户名已存在！'
					},
					password1: {
						required: '密码不能为空！',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					},
					password2: {
						required: '确认密码不能为空！',
						equalTo: '两次密码不一致！',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					},
					withdrawPwd1: {
						required: '密码不能为空！',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					},
					withdrawPwd2: {
						required: '确认密码不能为空！',
						equalTo: '两次密码不一致！',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					},
					googleCode: {
						required: '口令不能为空！',
						digits: '请输入纯数字',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					}
				},
				invalidHandler: function (event, validator) {
				},
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

			modal.find('[data-command="setWithdrawPwd"]').unbind('change').change(function() {
				var $this = $(this);
				if ($this.is(':checked')) {
					modal.find('[data-group=withdrawPwd]').show().removeClass('has-success').removeClass('has-error').find('.help-inline').html('');
					modal.find('input[name=withdrawPwd1]').val('');
					modal.find('input[name=withdrawPwd2]').val('');
				}
				else {
					modal.find('[data-group=withdrawPwd]').hide().removeClass('has-success').removeClass('has-error').find('.help-inline').html('');
					modal.find('input[name=withdrawPwd1]').val('');
					modal.find('input[name=withdrawPwd2]').val('');
				}
			});
		}

		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var action = modal.attr('data-action');
			var username = modal.find('input[name="username"]').val();
			var password = modal.find('input[name="password1"]').val();
			password = $.generatePassword(password);
			var roleId = modal.find('select[name="role"]').val();
			var setWithdrawPwd = modal.find('input[name="setWithdrawPwd"]').is(':checked') ? 1 : 0;
			var withdrawPwd = modal.find('input[name="withdrawPwd1"]').val();
			withdrawPwd = $.generatePassword(withdrawPwd);
			var googleCode = modal.find('input[name="googleCode"]').val();
			var params = {username: username, password: password, roleId: roleId, setWithdrawPwd: setWithdrawPwd, withdrawPwd: withdrawPwd, googleCode: googleCode};
			var url = './admin-user/add';
			if(action == 'edit') {
				url = './admin-user/edit';
			}
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
						AdminUserTable.init();
						if(action == 'add') {
							toastr['success']('用户添加成功！', '操作提示');
						}
						if(action == 'edit') {
							toastr['success']('用户修改成功！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						if(action == 'add') {
							toastr['error']('用户添加失败！' + data.message, '操作提示');
						}
						if(action == 'edit') {
							toastr['error']('用户修改失败！' + data.message, '操作提示');
						}
					}
				},
				complete: function() {
					isSending = false;
				},
				error: function() {
					isSending = false;
					toastr['error']('网络异常！请刷新后再试！', '操作提示');
				}
			});
		}

		var loadRole = function() {
			var url = './admin-user-role/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						buildRole(data.list);
					}
				}
			});
		}

		var buildRole = function(data) {
			var role = form.find('select[name="role"]');
			role.empty();
			$.each(data, function(idx, val) {
				if(val.upid != 0) {
					role.append('<option value="' + val.id + '">' + val.name + '</option>');
				}
			});
		}

		var show = function(data) {
			form[0].reset();
			if(data) {
				modal.attr('data-action', 'edit');
				modal.find('.modal-title').html('编辑用户');
				form.find('input[name="username"]').val(data.username).attr('disabled', true);
				form.find('select[name="role"]').find('option[value="' + data.roleId + '"]').attr('selected', true);
				modal.find('[data-group="add"]').hide();
				modal.find('[data-group="edit"]').show();
			} else {
				modal.attr('data-action', 'add');
				modal.find('.modal-title').html('新增用户');
				form.find('input[name="username"]').removeAttr('disabled');
				form.find('select[name="role"]').find('option:eq(0)').attr('selected', true);
				modal.find('[data-group="edit"]').hide();
				modal.find('[data-group="add"]').show();
			}

			modal.find('[data-command="setWithdrawPwd"]').find('input[name=setWithdrawPwd]').attr('checked', false);
			modal.find('input[name=password1]').val('');
			modal.find('input[name=password2]').val('');
			modal.find('input[name=withdrawPwd1]').val('');
			modal.find('input[name=withdrawPwd2]').val('');
			modal.find('input[name=googleCode]').val('');

			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}

		var init = function() {
			initForm();
			loadRole();
		}

		return {
			init: init,
			show: show
		}

	}();

	var OpenWithdrawPWDModal = function() {
		var modal = $('#modal-admin-user-open-withdraw-pwd');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					openWithdrawPwd1: {
						required: true,
						minlength: 6,
						maxlength: 20
					},
					openWithdrawPwd2: {
						required: true,
						minlength: 6,
						maxlength: 20,
						equalTo: 'input[name="openWithdrawPwd1"]'
					},
					googleCode: {
						required: true,
						digits: true,
						minlength: 6,
						maxlength: 6
					}
				},
				messages: {
					withdrawPwd1: {
						required: '密码不能为空！',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					},
					withdrawPwd2: {
						required: '确认密码不能为空！',
						equalTo: '两次密码不一致！',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					},
					googleCode: {
						required: '口令不能为空！',
						digits: '请输入纯数字',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					}
				},
				invalidHandler: function (event, validator) {
				},
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
			var action = modal.attr('data-action');
			var username = modal.find('input[name="username"]').val();
			var withdrawPwd = modal.find('input[name="openWithdrawPwd1"]').val();
			withdrawPwd = $.generatePassword(withdrawPwd);
			var googleCode = modal.find('input[name="googleCode"]').val();
			var params = {username: username, withdrawPwd: withdrawPwd, googleCode: googleCode};
			var url = './admin-user/open-withdraw-pwd';
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
						AdminUserTable.init();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
				},
				error: function() {
					isSending = false;
					toastr['error']('网络异常！请刷新后再试！', '操作提示');
				}
			});
		}

		var show = function(username) {
			form[0].reset();
			form.find('input[name="username"]').val(username).attr('disabled', true);
			form.find('input[name="openWithdrawPwd1"]').val('');
			form.find('input[name="openWithdrawPwd2"]').val('');
			form.find('input[name="googleCode"]').val('');

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

	var CloseWithdrawPWDModal = function() {
		var modal = $('#modal-admin-user-close-withdraw-pwd');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					withdrawPwd1: {
						required: true,
						minlength: 6,
						maxlength: 20
					},
					withdrawPwd2: {
						required: true,
						minlength: 6,
						maxlength: 20,
						equalTo: 'input[name="withdrawPwd1"]'
					},
					googleCode: {
						required: true,
						digits: true,
						minlength: 6,
						maxlength: 6
					}
				},
				messages: {
					withdrawPwd1: {
						required: '密码不能为空！',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					},
					withdrawPwd2: {
						required: '确认密码不能为空！',
						equalTo: '两次密码不一致！',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					},
					googleCode: {
						required: '口令不能为空！',
						digits: '请输入纯数字',
						minlength: '至少输入{0}个字符',
						maxlength: '最多输入{0}个字符'
					}
				},
				invalidHandler: function (event, validator) {
				},
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
			var action = modal.attr('data-action');
			var username = modal.find('input[name="username"]').val();
			var googleCode = modal.find('input[name="googleCode"]').val();
			var params = {username: username, googleCode: googleCode};
			var url = './admin-user/close-withdraw-pwd';
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
						AdminUserTable.init();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
				},
				error: function() {
					isSending = false;
					toastr['error']('网络异常！请刷新后再试！', '操作提示');
				}
			});
		}

		var show = function(username) {
			form[0].reset();
			form.find('input[name="username"]').val(username).attr('disabled', true);
			form.find('input[name="googleCode"]').val('');

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

	return {
		init: function() {
			AdminUserTable.init();
			AddUserModal.init();
			OpenWithdrawPWDModal.init();
			CloseWithdrawPWDModal.init();
		}
	}
}();