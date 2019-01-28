var AdminUserRole = function() {
	
	var AdminUserRoleTable2 = function() {
		var tableList = $('#table-admin-user-role-list');
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './admin-user-role/tree-list';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						buildData(data.list);
					}
				}
			});
		}
		
		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './admin-user-role/update-status';
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
							toastr['success']('该角色已启用！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该角色已禁用！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var buildData = function(data) {
			var table = tableList.find('table > tbody').empty();
			var innerHtml = buildChild(data);
			table.html(innerHtml);
			
			/**
			 * 编辑
			 */
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var url = './admin-user-role/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						AddUserRoleModal.show(data);
					}
				});
			});
			
			/**
			 * 禁用/启用
			 */
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定禁用该角色？';
				if(status == -1) {
					msg = '确定启用该角色？';
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
			
			/**
			 * 权限
			 */
			table.find('[data-command="access"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var url = './admin-user-role/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						UserRoleAccessModal.show(data);
					}
				});
			});
			
			// tree效果
			table.find('tr').each(function() {
				var id = $(this).attr('data-id');
				var upid = $(this).attr('data-upid');
				var level = $(this).attr('data-level');
				$(this).find('td').eq(0).css('padding-left', level * 26 + 8);
				$(this).find('[data-command="child"]').unbind('click').click(function() {
					if($(this).hasClass('fa-caret-right')) {
						open(this, id);
					} else if($(this).hasClass('fa-caret-down')) {
						close(this, id);
					}
				});
			});
			
			var open = function(els, id) {
				table.find('tr[data-upid="' + id + '"]').show();
				$(els).removeClass('fa-caret-right').addClass('fa-caret-down');
			}
			
			var close = function(els, id) {
				table.find('tr[data-upid="' + id + '"]').each(function() {
					var command = $(this).find('[data-command="child"]');
					$(this).hide();
					if(command && command.hasClass('fa-caret-down')) {
						command.trigger('click');
					}
				});
				$(els).removeClass('fa-caret-down').addClass('fa-caret-right');
			}
		}
		
		var buildChild = function(data, level) {
			var innerHtml = '';
			level = level == undefined ? 0 : level + 1;
			$.each(data, function(idx, val) {
				var actions = '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用 </a>';
				if(val.status == -1) {
					actions = '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
				}
				if(val.upid != 0) {
					actions += '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a>';
					actions += '<a data-command="access" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-slack"></i> 权限 </a>';
				}
				if(val.id == 1) {
					actions += '<a data-command="access" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-slack"></i> 权限 </a>';
				}
				var formatCommand = '<i class="fa fa-user"></i>';
				if(val.items.length > 0) {
					formatCommand = '<i data-command="child" class="tree fa fa-caret-down"></i>';
				}
				innerHtml +=
				'<tr class="align-center" data-id="' + val.id + '" data-upid="' + val.upid + '" data-level="'+level+'">'+
					'<td class="align-left">' + formatCommand + val.name + '</td>'+
					'<td class="align-left">' + val.description + '</td>'+
					'<td>' + DataFormat.formatAdminUserMenuStatus(val.status) + '</td>'+
					'<td>' + actions + '</td>'+
				'</tr>';
				if(val.items.length > 0) {
					innerHtml += buildChild(val.items, level);
				}
			});
			return innerHtml;
		}
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var AdminUserRoleTable = function() {
		var tableList = $('#table-admin-user-role-list');
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './admin-user-role/list';
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
				var statusAction = '<a data-command="status" data-status="' + val.bean.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用 </a>';
				if(val.bean.status == -1) {
					statusAction = '<a data-command="status" data-status="' + val.bean.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
				}
				if(val.bean.upid != 0) {
					statusAction += '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a>';
				}
				innerHtml +=
				'<tr class="align-center" data-id="' + val.bean.id + '">'+
					'<td>' + val.bean.name + '</td>'+
					'<td>' + val.group + '</td>'+
					'<td>' + val.bean.description + '</td>'+
					'<td>' + val.bean.sort + '</td>'+
					'<td>' + DataFormat.formatAdminUserRoleStatus(val.bean.status) + '</td>'+
					'<td>' + statusAction + '<a data-command="access" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-slack"></i> 权限 </a></td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var url = './admin-user-role/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						AddUserRoleModal.show(data);
					}
				});
			});
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定禁用该角色？';
				if(status == -1) {
					msg = '确定启用该角色？';
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
			table.find('[data-command="access"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var url = './admin-user-role/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						UserRoleAccessModal.show(data);
					}
				});
			});
		}
		
		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './admin-user-role/update-status';
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
							toastr['success']('该角色已启用！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该角色已禁用！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		//UserRoleAccessModal
		
		tableList.find('[data-command="add"]').unbind('click').click(function() {
			AddUserRoleModal.show();
		});
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var AddUserRoleModal = function() {
		var modal = $('#modal-admin-user-role-add');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					name: {
						required: true,
						remote: {
							url: './admin-user-role/check-exist',
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
	                    required: '角色名不能为空！',
	                    remote: '角色已存在！'
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
			var upid = modal.find('select[name="role"]').val();
			var description = modal.find('textarea[name="description"]').val();
			var sort = modal.find('input[name="sort"]').val();
			var params = {name: name, upid: upid, description: description, sort: sort};
			var url = './admin-user-role/add';
			if(action == 'edit') {
				var id = modal.attr('data-id');
				params.id = id;
				url = './admin-user-role/edit';
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
						AdminUserRoleTable2.init();
						if(action == 'add') {
							toastr['success']('角色添加成功！', '操作提示');
						}
						if(action == 'edit') {
							toastr['success']('角色修改成功！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						if(action == 'add') {
							toastr['error']('角色添加失败！' + data.message, '操作提示');
						}
						if(action == 'edit') {
							toastr['error']('角色修改失败！' + data.message, '操作提示');
						}
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
				role.append('<option value="' + val.id + '">' + val.name + '</option>');
			});
		}
		
		var show = function(data) {
			form[0].reset();
			if(data) {
				modal.attr('data-action', 'edit');
				modal.attr('data-id', data.bean.id);
				modal.find('.modal-title').html('编辑角色');
				form.find('input[name="name"]').val(data.bean.name);
				form.find('select[name="role"]').find('option[value="' + data.bean.upid + '"]').attr('selected', true);
				form.find('textarea[name="description"]').val(data.bean.description);
				form.find('input[name="sort"]').val(data.bean.sort);
			} else {
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				modal.find('.modal-title').html('新增角色');
				form.find('select[name="role"]').find('option:eq(0)').attr('selected', true);
			}
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
	
	var UserRoleAccessModal = function() {
		var modal = $('#modal-admin-user-role-access');
		
		var jsTree = modal.find('.jsTree');
		
		modal.find('[data-command="submit"]').unbind('click').click(function() {
			doSubmit();
		});
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var id = modal.attr('data-id');
			var ids = getJsTreeIds();
			var url = './admin-user-role/save-access';
			var params = {id: id, ids: ids};
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
						toastr['success']('修改权限成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('修改权限失败！' + data.message, '操作提示');
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
		
		var getJsTreeIds = function() {
			var list = $.jstree.reference(jsTree).get_checked();
        	var ids = new Array();
        	$.each(list, function(idx, val) {
        		if(val.indexOf('action') != -1) {
					ids.push(val.substring(7));
				}
        	});
        	return ids.toString();
		}
		
		var initJSTree = function(roleId, callback) {
			var url = './admin-user-action/jstree';
			$.ajax({
				type : 'post',
				url : url,
				data : {roleId: roleId},
				dataType : 'json',
				success : function(data) {
					buildJSTree(data, callback);
				}
			});
		}
		
		var buildJSTree = function(data, callback) {
			if($.jstree.reference(jsTree)) {
				$.jstree.reference(jsTree).destroy();
			}
			jsTree.jstree({
				plugins: ['wholerow', 'checkbox'],
				core: {
					themes: {
						responsive: false
					},    
					data: data
				}
			});
			if($.isFunction(callback)) callback();
		}
		
		var checkJSTree = function(data) {
			if(data != '') {
				var ids = new Array();
				$.each(eval(data), function(idx, val) {
					ids.push('action_' + val);
				});
				$.jstree.reference(jsTree).check_node(ids);
			}
			modal.modal('show');
		}
		
		var show = function(data) {
			if(!data) return;
			initJSTree(data.bean.id, function() {
				//$.jstree.reference(jsTree).close_all();
				//$.jstree.reference(jsTree).uncheck_all();
				modal.attr('data-id', data.bean.id);
				setTimeout(function() {
					checkJSTree(data.bean.actions);
				}, 100);
			});
		}
		
		var init = function() {
			initJSTree();
		}
		
		return {
			init: init,
			show: show
		}
		
	}();
	
	return {
		init: function() {
			AdminUserRoleTable2.init();
			//AdminUserRoleTable.init();
			AddUserRoleModal.init();
			//UserRoleAccessModal.init();
		}
	}
}();