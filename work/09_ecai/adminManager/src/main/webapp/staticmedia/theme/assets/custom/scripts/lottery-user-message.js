var LotteryUserMessage = function() {
	
	var UserMessageTable = function() {
		var tableList = $('#table-user-message-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var toUser = tableList.find('input[name="toUser"]').val();
			var fromUser = tableList.find('input[name="fromUser"]').val();
			var sTime = tableList.find('input[name="sTime"]').val();
			var eTime = tableList.find('input[name="eTime"]').val();
			var sortColoum = tableList.find('select[name="sortColoum"]').val();
			return {toUser: toUser, fromUser: fromUser, sTime: sTime, eTime: eTime, sortColoum: sortColoum};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-message/list',
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
					if(val.bean.toStatus >= 0) {
						statusAction = '<a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 删除 </a>';
					}
						statusAction += '<a data-command="details" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详细 </a>';
					if(val.bean.type == 1 && val.toUser == '') {
						statusAction += '<a data-command="reply" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-send-o"></i>回复 </a>';
					}

					var toUser = val.bean.type == 1 && val.toUser == ''  ? '系统管理员' : val.toUser;
					var fromUser = val.bean.type == 1 && val.fromUser == ''  ? '系统管理员' : val.fromUser;

					innerHtml +=
					'<tr class="align-center" data-id="' + val.bean.id + '">'+
						'<td>' + val.bean.id + '</td>'+
						'<td>' + toUser + '</td>'+
						'<td>' + fromUser + '</td>'+
						'<td>' + val.bean.subject + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + DataFormat.formatUserMessageStatus(val.bean.toStatus) + '</td>'+
						'<td>' + statusAction + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
				
				table.find('[data-command="delete"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var msg = '确定删除此消息？（无法恢复正常）';
					bootbox.dialog({
						message: msg,
						title: '提示消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									deleteMessage(id);
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
				
				table.find('[data-command="details"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					doLoadDetails(id);
				});
				
				table.find('[data-command="reply"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					doLoadDetails(id, true);
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
		
		var deleteMessage = function(id) {
			var url = './lottery-user-message/delete';
			var params = {id: id};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('已成功删除该消息！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var isLoading = false;
		var doLoadDetails = function(id, isReply) {
			if(isLoading) return;
			var params = {id: id};
			var url = './lottery-user-message/get';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						if(isReply == true){
							doShowReply(data.data);
						}else{
							doShowDetails(data.data);
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var doShowDetails = function(data) {
			var toUser = data.bean.type == 1 && data.toUser == ''  ? '系统管理员' : data.toUser;
			var fromUser = data.bean.type == 1 && data.fromUser == ''  ? '系统管理员' : data.fromUser;

			var modal = $('#modal-lottery-user-message-details');
			modal.find('[data-field="toUser"]').html(toUser);
			modal.find('[data-field="fromUser"]').html(fromUser);
			modal.find('[data-field="time"]').html(data.bean.time);
			modal.find('[data-field="subject"]').html(data.bean.subject);
			modal.find('textarea[name="content"]').val(data.bean.content);
			modal.modal('show');
		}
		
		var doShowReply = function(data) {
			var toUser = data.bean.type == 1 && data.toUser == ''  ? '系统管理员' : data.toUser;
			var fromUser = data.bean.type == 1 && data.fromUser == ''  ? '系统管理员' : data.fromUser;

			var modal = $('#modal-lottery-user-message-reply');
			modal.find('[data-field="toUser"]').html(toUser);
			modal.find('[data-field="fromUser"]').html(fromUser);
			modal.find('[data-field="time"]').html(data.bean.time);
			modal.find('[data-field="subject"]').html("回复>>"+data.bean.subject);
			modal.find('textarea[name="content"]').val('');
			modal.modal('show');
			
			modal.find('[data-command="submit"]').unbind("click").click(function(){
				var form = modal.find('form');
				if(!form.validate().form()){
					return;
				}
				var content = modal.find('textarea[name="content"]').val();
				var params = {id: data.bean.id, content:content};
				var url = './lottery-user-message/reply';
				isLoading = true;
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						isLoading = false;
						if(data.error == 0) {
							toastr['success']('回复成功！', '操作提示');
							modal.modal('hide');
						}
						if(data.error == 1 || data.error == 2) {
							toastr['error']('操作失败！' + data.message, '操作提示');
						}
					}
				});
			});
		}
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		
		var modal = $('#modal-lottery-user-message-reply');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					content: {
						required: true,
					}
				},
				messages: {
					content: {
	                    required: '内容不能为空！',
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
		}
		
		var init = function() {
			pagination.init();
			initForm();
		}
		
		var reload = function() {
			pagination.reload();
		}
		
		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			UserMessageTable.init();
		}
	}
}();