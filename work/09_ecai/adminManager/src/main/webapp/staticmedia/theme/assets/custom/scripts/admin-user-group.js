var AdminUserGroup = function() {
	
	var AdminUserGroupTable = function() {
		var tableList = $('#table-admin-user-group-list');
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './admin-user-group/list';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : {},
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
				var statusAction = '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用 </a>';
				if(val.status == -1) {
					statusAction = '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
				}
				innerHtml +=
				'<tr class="align-center" data-id="' + val.id + '">'+
					'<td>' + val.name + '</td>'+
					'<td>' + val.description + '</td>'+
					'<td>' + val.sort + '</td>'+
					'<td>' + DataFormat.formatAdminUserGroupStatus(val.status) + '</td>'+
					'<td>' + statusAction + '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a></td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var url = './admin-user-group/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						AddUserGroupModal.show(data);
					}
				});
			});
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定禁用该分组？';
				if(status == -1) {
					msg = '确定启用该分组？';
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
			var url = './admin-user-group/update-status';
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
							toastr['success']('该分组已启用！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该分组已禁用！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		tableList.find('[data-command="add"]').unbind('click').click(function() {
			AddUserGroupModal.show();
		});
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var AddUserGroupModal = function() {
		var modal = $('#modal-admin-user-group-add');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					name: {
						required: true,
						remote: {
							url: './admin-user-group/check-exist',
							type: 'post',
							data: {
								id: function() {
									return modal.attr('data-action') == 'edit' ? modal.attr('data-id') : '';
								}
							}
						}
					},
					sort: {
						number: true
					}
				},
				messages: {
					name: {
	                    required: '组名不能为空！',
	                    remote: '分组已存在！'
	                },
	                sort: {
	                	number: '请输入正确的数字！'
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
			var action = modal.attr('data-action');
			var name = modal.find('input[name="name"]').val();
			var description = modal.find('textarea[name="description"]').val();
			var sort = modal.find('input[name="sort"]').val();
			var params = {name: name, description: description, sort: sort};
			var url = './admin-user-group/add';
			if(action == 'edit') {
				var id = modal.attr('data-id');
				params.id = id;
				url = './admin-user-group/edit';
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
						AdminUserGroupTable.init();
						if(action == 'add') {
							toastr['success']('分组添加成功！', '操作提示');
						}
						if(action == 'edit') {
							toastr['success']('分组修改成功！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						if(action == 'add') {
							toastr['error']('分组添加失败！' + data.message, '操作提示');
						}
						if(action == 'edit') {
							toastr['error']('分组修改失败！' + data.message, '操作提示');
						}
					}
				}
			});
		}
		
		var show = function(data) {
			form[0].reset();
			if(data) {
				modal.attr('data-action', 'edit');
				modal.attr('data-id', data.id);
				modal.find('.modal-title').html('编辑分组');
				form.find('input[name="name"]').val(data.name);
				form.find('textarea[name="description"]').val(data.description);
				form.find('input[name="sort"]').val(data.sort);
			} else {
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				modal.find('.modal-title').html('新增分组');
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
	
	return {
		init: function() {
			AdminUserGroupTable.init();
			AddUserGroupModal.init();
		}
	}
}();