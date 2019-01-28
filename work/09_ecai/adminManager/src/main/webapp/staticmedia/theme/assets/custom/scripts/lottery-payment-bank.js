var LotteryPaymentBank = function() {
	
	var LotteryPaymentBankTable = function() {
		var tableList = $('#table-lottery-payment-bank-list');
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './lottery-payment-bank/list';
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
				innerHtml +=
				'<tr class="align-center" data-id="' + val.id + '">'+
					'<td>' + val.id + '</td>'+
					'<td>' + val.name + '</td>'+
					'<td>' + val.url + '</td>'+
					'<td><a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a></td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var url = './lottery-payment-bank/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						EditPaymentBankModal.show(data);
					}
				});
			});
		}
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var EditPaymentBankModal = function() {
		var modal = $('#modal-payment-bank-edit');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					name: {
						required: true
					}
				},
				messages: {
					name: {
						required: '银行名称不能为空！'
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
			var name = modal.find('input[name="name"]').val();
			var url = modal.find('input[name="url"]').val();
			var params = {id: id, name: name, url: url};
			var url = './lottery-payment-bank/edit';
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
						LotteryPaymentBankTable.init();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
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
			modal.attr('data-id', data.id);
			form.find('input[name="name"]').val(data.name);
			form.find('input[name="url"]').val(data.url);
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
			LotteryPaymentBankTable.init();
			EditPaymentBankModal.init();
		}
	}
}();