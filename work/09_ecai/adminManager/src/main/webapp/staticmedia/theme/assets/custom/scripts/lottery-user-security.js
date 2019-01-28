var LotteryUserSecurity = function() {
	
	var UserSecurityTable = function() {
		var tableList = $('#table-user-security-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var key = tableList.find('input[name="key"]').val();
			return {username: username, key: key};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-security/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					innerHtml +=
					'<tr class="align-center" data-id="' + val.bean.id + '">'+
						'<td>' + val.username + '</td>'+
						'<td>' + val.bean.key + '</td>'+
						'<td>' + val.bean.value + '</td>'+
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
		
		tableList.find('[data-command="reset"]').unbind('click').click(function() {
			ResetLotteryUserSecurityModal.show();
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
	
	var ResetLotteryUserSecurityModal = function() {
		var modal = $('#modal-lottery-user-security-reset');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					username: {
						required: true
					}
				},
				messages: {
					username: {
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
			if(isSending) return;
			var username = form.find('input[name="username"]').val();
			var params = {username: username};
			var url = './lottery-user-security/reset';
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
						UserSecurityTable.reload();
						toastr['success']('密保重置成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('密保重置失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var show = function() {
			form[0].reset();
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
			UserSecurityTable.init();
			ResetLotteryUserSecurityModal.init();
		}
	}
}();