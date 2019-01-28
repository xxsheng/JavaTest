var LotteryUserBetsLimit = function() {
	
	var LotteryUserBetsLimitTable = function() {
		var tableList = $('#table-lottery-gobal-bets-limit-list');
		var tablePagelist = tableList.find('.page-list');
		
		var userTableList = $('#table-lottery-user-bets-limit-list');
		var userTablePagelist = userTableList.find('.page-list');
		
		var loadData = function(queryGobalSetting) {
			var queryUsername = $('#queryUsername').val();
			
			var pagination = $.pagination({
				render: queryGobalSetting == true ? tablePagelist : userTablePagelist ,
				pageSize: 10,
				ajaxType: 'post',
				ajaxUrl: './lottery-user-bets-limit/list',
				ajaxData: { username:queryUsername, queryGobalSetting:queryGobalSetting },
				beforeSend: function() {
					
				},
				complete: function() {
					
				},
				success: function(list) {
					buildData(list, queryGobalSetting);
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
					if(!queryGobalSetting){
						userTableList.find('table > tbody').empty();
						userTablePagelist.html('没有相关数据');
					}else{
						tableList.find('table > tbody').empty();
						tablePagelist.html('没有相关数据');
					}
				}
			});
			
			pagination.init();
		}
		
		var buildData = function(data, queryGobalSetting) {
			var table = null;
			if(!queryGobalSetting){
				table = userTableList.find('table > tbody').empty();
			}else{
				table = tableList.find('table > tbody').empty();
			}
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var userQueryTd = '';
				var golbalTd = '';
				if(!queryGobalSetting){
					userQueryTd += '<td>' + val.userName + '</td>';
				}else{
					golbalTd += '<td>' + val.bean.maxPrize + '</td>';
				}
				
				innerHtml +=
				'<tr class="align-center" data-id="' + val.bean.id + '">'+ userQueryTd +
					'<td>' + val.lotteryName + '</td>'+ golbalTd +
					'<td>' + val.bean.maxBet + '</td>'+ 
					'<td>' + '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 删除 </a></td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var url = './lottery-user-bets-limit/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						if(queryGobalSetting){
							AddLotteryGobalBetsLimitModal.show(data);
						}else{
							AddLotteryUserBetsLimitModal.show(data);
						}
					}
				});
			});
			
			table.find('[data-command="delete"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var msg = '确定删除该配置？';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								deleteCard(id, queryGobalSetting);
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
		
		var deleteCard = function(id, queryGobalSetting) {
			var params = {id: id};
			var url = './lottery-user-bets-limit/delete';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						if(queryGobalSetting){
							loadData(true);
						}else{
							loadData(false);
						}
						
						toastr['success']('删除成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		tableList.find('[data-command="add"]').unbind('click').click(function() {
			AddLotteryGobalBetsLimitModal.show();
		});
		
		userTableList.find('[data-command="add"]').unbind('click').click(function() {
			AddLotteryUserBetsLimitModal.show();
		});
		
		userTableList.find('[data-command="search"]').unbind('click').click(function() {
			loadData(false);
		});
		
		var init = function() {
			loadData(true);
			loadData(false);
		}
		
		return {
			init: init,
			loadData : loadData
		}
	}();
	
	var AddLotteryGobalBetsLimitModal = function() {
		var modal = $('#modal-lottery-gobal-bets-limit-add');
		
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					maxBet: {
						required: true,
						number: true
					},
					maxPrize: {
						required: true,
						number: true
					}
				},
				messages: {
					maxBet: {
	                    required: '每期最大限额不能为空！',
	                    number:'请输入正确的金额'
	                },
	        		maxPrize: {
	                    required: '每期最大限额不能为空！',
	                    number:'请输入正确的金额'
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
			var lotteryId = modal.find('select[name="lotteryId"]').val();
			var maxBet = modal.find('input[name="maxBet"]').val();
			var maxPrize = modal.find('input[name="maxPrize"]').val();
			var username = '系统全局配置';
			var id = modal.attr('data-id');
			
			var params = { id:id, lotteryId:lotteryId, maxBet:maxBet, username:username, source:'gobal', maxPrize:maxPrize };
			var url = './lottery-user-bets-limit/add-update';
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
						LotteryUserBetsLimitTable.loadData(true);
						if(id != ''){
							toastr['success']('修改成功！', '操作提示');
						}else{
							toastr['success']('添加成功！', '操作提示');
						}
						
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('账号添加失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var show = function(data) {
			form[0].reset();
			if(data) {
				modal.attr('data-action', 'edit');
				modal.attr('data-id', data.data.bean.id);
				modal.find('.modal-title').html('编辑投注限额');
				form.find('select[name="lotteryId"]').find('option[value="' + data.data.bean.lotteryId + '"]').attr('selected', true);
				form.find('input[name="maxBet"]').val(data.data.bean.maxBet);
				form.find('input[name="maxPrize"]').val(data.data.bean.maxPrize);
				Metronic.initAjax();
			} else {
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				modal.find('.modal-title').html('新增投注限额');
				form.find('select[name="lotteryId"]').find('option:eq(0)').attr('selected', true);
				Metronic.initAjax();
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
	
	var AddLotteryUserBetsLimitModal = function() {
		var modal = $('#modal-lottery-user-bets-limit-add');
		
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					maxBet: {
						required: true,
						number: true
					},
					userName:{
						required: true,
					}
				},
				messages: {
					maxBet: {
	                    required: '最大限额不能为空！',
	                    number:'请输入正确的金额'
	                },
	                userName:{
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
			var lotteryId = modal.find('select[name="lotteryId"]').val();
			var maxBet = modal.find('input[name="maxBet"]').val();
			var username = modal.find('input[name="userName"]').val();
			var id = modal.attr('data-id');
			var params = { id:id, lotteryId:lotteryId, maxBet:maxBet, username:username, source:'user', maxPrize:0};
			var url = './lottery-user-bets-limit/add-update';
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
						LotteryUserBetsLimitTable.loadData(false);
						if(id != ''){
							toastr['success']('修改成功！', '操作提示');
						}else{
							toastr['success']('添加成功！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('账号添加失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var show = function(data) {
			form[0].reset();
			if(data) {
				modal.attr('data-action', 'edit');
				modal.attr('data-id', data.data.bean.id);
				modal.find('.modal-title').html('编辑投注限额');
				form.find('select[name="lotteryId"]').find('option[value="' + data.data.bean.lotteryId + '"]').attr('selected', true);
				form.find('input[name="maxBet"]').val(data.data.bean.maxBet);
				form.find('input[name="userName"]').val(data.data.userName);
				form.find('input[name="userName"]').attr('readonly', true);
				Metronic.initAjax();
			} else {
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				modal.find('.modal-title').html('新增投注限额');
				form.find('select[name="lotteryId"]').find('option:eq(0)').attr('selected', true);
				form.find('input[name="userName"]').attr('readonly', false);
				Metronic.initAjax();
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
			LotteryUserBetsLimitTable.init();
			AddLotteryUserBetsLimitModal.init();
			AddLotteryGobalBetsLimitModal.init();
		}
	}
}();