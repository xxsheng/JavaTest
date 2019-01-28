var LotteryCrawlerStatus = function() {
	
	var LotteryCrawlerStatusTable = function() {
		var tableList = $('#table-lottery-crawler-status-list');
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './lottery-crawler-status/list';
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
				innerHtml +=
				'<tr class="align-center" data-name="' + val.bean.shortName + '">'+
					'<td>' + val.bean.showName + '</td>'+
					'<td>' + val.bean.shortName + '</td>'+
					'<td>' + val.bean.times + '</td>'+
					'<td>' + val.bean.lastExpect + '</td>'+
					'<td>' + val.openTime.expect + '</td>'+
					'<td>' + val.bean.lastUpdate + '</td>'+
					'<td><a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a></td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var lottery = $(this).parents('tr').attr('data-name');
				var url = './lottery-crawler-status/get';
				var params = {lottery: lottery};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						EditLotteryCrawlerStatusModal.show(data);
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
	
	var EditLotteryCrawlerStatusModal = function() {
		var modal = $('#modal-lottery-crawler-status-edit');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					lastExpect: {
						required: true
					}
				},
				messages: {
					lastExpect: {
	                    required: '期数不能为空！'
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
			var lottery = modal.attr('data-name');
			var lastExpect = modal.find('input[name="lastExpect"]').val();
			var lastUpdate = modal.find('input[name="lastUpdate"]').val();
			var params = {lottery: lottery, lastExpect: lastExpect, lastUpdate: lastUpdate};
			var url = './lottery-crawler-status/edit';
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
						LotteryCrawlerStatusTable.init();
						toastr['success']('修改Server配置成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('修改Server配置失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var show = function(data) {
			form[0].reset();
			if(data) {
				modal.attr('data-name', data.shortName);
				form.find('input[name="lottery"]').val(data.showName);
				form.find('input[name="times"]').val(data.times);
				form.find('input[name="lastExpect"]').val(data.lastExpect);
				form.find('input[name="lastUpdate"]').val(data.lastUpdate);
				
				form.find('.help-inline').empty();
				form.find('.has-error').removeClass('has-error');
				form.find('.has-success').removeClass('has-success');
				modal.modal('show');
			}
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
			LotteryCrawlerStatusTable.init();
			EditLotteryCrawlerStatusModal.init();
		}
	}
}();