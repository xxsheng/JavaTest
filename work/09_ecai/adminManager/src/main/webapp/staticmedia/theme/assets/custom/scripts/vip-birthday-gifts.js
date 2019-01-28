var VipBirthdayGifts = function() {
	
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
	
	var ThisTableList = function() {
		var tableList = $('#vip-birthday-gifts-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var level = tableList.find('select[name="level"]').val();
			var date = tableList.find('input[name="date"]').val();
			var status = tableList.find('select[name="status"]').val();
			return {username: username, level: level, date: date, status: status};
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './vip-birthday-gifts/list',
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
						'<td>' + val.bean.id + '</td>'+
						'<td>' + val.username + '</td>'+
						'<td>' + DataFormat.formatUserVipLevel(val.bean.level) + '</td>'+
						'<td>' + val.bean.money + '</td>'+
						'<td>' + val.bean.birthday + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + DataFormat.formatVipBirthdayGiftsStatus(val.bean.status) + '</td>'+
						'<td>' + DataFormat.formatVipBirthdayGiftsReceivedStatus(val.bean.isReceived) + '</td>'+
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
							className: 'green-meadow',
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
		
		tableList.find('[data-command="calculate"]').unbind('click').click(function() {
			CalculateModal.show();
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
	
	var CalculateModal = function() {
		var modal = $('#modal-calculate');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					date: {
						required: true
					}
				},
				messages: {
					date: {
						required: '日期不能为空！'
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
			var date = modal.find('input[name="date"]').val();
			var params = {date: date};
			var url = './vip-birthday-gifts/calculate';
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
						ThisTableList.reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var show = function() {
			form[0].reset();
			updateDatePicker();
			
			var today = moment().format('YYYY-MM-DD');
			modal.find('input[name="date"]').val(today);
			
			form.find('.help-inline').each(function() {
				if($(this).attr('data-default')) {
					$(this).html($(this).attr('data-default'));
				} else {
					$(this).empty();
				}
			});
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
			ThisTableList.init();
			CalculateModal.init();
			handelDatePicker();
		}
	}
}();