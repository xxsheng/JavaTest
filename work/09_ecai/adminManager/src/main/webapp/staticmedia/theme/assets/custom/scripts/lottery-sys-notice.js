var LotterySysNotice = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var updateDatePicker = function() {
		$('.date-picker').datepicker('update');
	}
	
	var LotterySysNoticeTable = function() {
		var tableList = $('#table-lottery-sys-notice-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var status = tableList.find('select[name="status"]').val();
			return {status: status};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './lottery-sys-notice/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var statusAction = '';
					if(val.status == 0) {
						statusAction = '<a data-command="status" data-status="0" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 隐藏</a>';
					}
					if(val.status == -1) {
						statusAction = '<a data-command="status" data-status="-1" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 显示</a>';
					}
					var sortAction = '';
					if(val.sort == 0) {
						sortAction = '<a data-command="sort" data-sort="0" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-arrow-up"></i> 置顶</a>';
					}
					if(val.sort == 1) {
						sortAction = '<a data-command="sort" data-sort="1" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-arrow-down"></i> 降顶</a>';
					}
					innerHtml +=
					'<tr class="align-center" data-id="' + val.id + '">'+
						'<td>' + val.id + '</td>'+
						'<td>' + val.title + '</td>'+
						'<td>' + DataFormat.formatLotterySysNoticeSort(val.sort) + '</td>'+
						'<td>' + DataFormat.formatLotterySysNoticeStatus(val.status) + '</td>'+
						'<td>' + val.date + '</td>'+
						'<td>' + val.time + '</td>'+
						'<td>' + sortAction + statusAction + '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑</a><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-times"></i> 删除</a></td>'+
					'</tr>';
				});
				table.html(innerHtml);
				table.find('[data-command="edit"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var url = './lottery-sys-notice/get';
					var params = {id: id};
					$.ajax({
						type : 'post',
						url : url,
						data : params,
						dataType : 'json',
						success : function(data) {
							AddLotterySysNoticeModal.show(data);
						}
					});
				});
				table.find('[data-command="sort"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var sort = $(this).attr('data-sort');
					var msg = '确定置顶该条数据？';
					if(sort == 1) {
						msg = '确定降顶该条数据？';
					}
					bootbox.dialog({
						message: msg,
						title: '提示消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									if(sort == 0) {
										updateSort(id, 1);
									}
									if(sort == 1) {
										updateSort(id, 0);
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
				table.find('[data-command="status"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var status = $(this).attr('data-status');
					var msg = '确定隐藏该条数据？';
					if(status == -1) {
						msg = '确定显示该条数据？';
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
				table.find('[data-command="delete"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var msg = '确定删除该条数据？';
					bootbox.dialog({
						message: msg,
						title: '提示消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									deleteNotice(id);
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
		
		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './lottery-sys-notice/update-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var updateSort = function(id, sort) {
			var params = {id: id, sort: sort};
			var url = './lottery-sys-notice/update-sort';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var deleteNotice = function(id) {
			var params = {id: id};
			var url = './lottery-sys-notice/delete';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		tableList.find('[data-command="add"]').unbind('click').click(function() {
			AddLotterySysNoticeModal.show();
		});
		
		tableList.find('select[name="status"]').unbind('change').change(function() {
			init();
		});
		
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
	
	var AddLotterySysNoticeModal = function() {
		var modal = $('#modal-lottery-sys-notice-add');
		var form = modal.find('form');
		var editor = UE.getEditor('editor');
		
		var initForm = function() {
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				doSubmit();
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var action = modal.attr('data-action');
			var title = modal.find('input[name="title"]').val();
			var content = editor.getContent();
			var simpleContent = modal.find('input[name="simpleContent"]').val();
			var sort = modal.find('input[name="sort"]').is(':checked') ? 1 : 0;
			var status = modal.find('input[name="status"]').is(':checked') ? 0 : -1;
			var date = modal.find('input[name="date"]').val();
			if(title == '') {
				toastr['error']('公告标题不能为空！', '操作提示');
				return false;
			}
			if(content == '') {
				toastr['error']('公告内容不能为空！', '操作提示');
				return false;
			}
			if(simpleContent == '') {
				toastr['error']('简单内容不能为空！', '操作提示');
				return false;
			}
			var params = {title: title, content: content, simpleContent: simpleContent, sort: sort, status: status, date: date};
			var url = './lottery-sys-notice/add';
			if(action == 'edit') {
				var id = modal.attr('data-id');
				params.id = id;
				url = './lottery-sys-notice/edit';
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
						LotterySysNoticeTable.reload();
						if(action == 'add') {
							toastr['success']('添加成功！', '操作提示');
						}
						if(action == 'edit') {
							toastr['success']('修改成功！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						if(action == 'add') {
							toastr['error']('添加失败！' + data.message, '操作提示');
						}
						if(action == 'edit') {
							toastr['error']('修改失败！' + data.message, '操作提示');
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
		
		var show = function(data) {
			if(data) {
				modal.attr('data-action', 'edit');
				modal.attr('data-id', data.id);
				modal.find('.modal-title').html('编辑公告');
				form.find('input[name="title"]').val(data.title);
				editor.setContent(data.content);
				form.find('input[name="simpleContent"]').val(data.simpleContent);
				var sort = modal.find('input[name="sort"]');
				if(data.sort == 0) {
					if(sort.is(':checked')) {
						sort.trigger('click');
					}
				} else {
					if(!sort.is(':checked')) {
						sort.trigger('click');
					}
				}
				var status = modal.find('input[name="status"]');
				if(data.status == 0) {
					if(!status.is(':checked')) {
						status.trigger('click');
					}
				} else {
					if(status.is(':checked')) {
						status.trigger('click');
					}
				}
				Metronic.initAjax();
				form.find('input[name="date"]').val(data.date);
			} else {
				form[0].reset();
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				modal.find('.modal-title').html('新增公告');
				editor.setContent('');
			}
			updateDatePicker();
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
			LotterySysNoticeTable.init();
			AddLotterySysNoticeModal.init();
			handelDatePicker();
		}
	}
}();