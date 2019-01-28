var UserBetsHitRanking = function() {

	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true,
			initialDate: new Date()
        });
	}

	var updateDatePicker = function() {
		$('.date-picker').datepicker('update');
	}

	var UserBetsHitRankingTable = function() {
		var tableList = $('#table-user-bets-hit-ranking-list');
		var tablePagelist = tableList.find('.page-list');

		var getSearchParams = function() {
			return {};
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './user-bets-hit-ranking/list',
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
					'<tr class="align-center" data-id="' + val.id + '">'+
						'<td>' + val.id + '</td>'+
						'<td>' + val.name + '</td>'+
						'<td>' + val.username + '</td>'+
						'<td>' + val.prizeMoney + '</td>'+
						'<td>' + val.time + '</td>'+
						'<td>' + '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑</a><a data-command="delete" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-times"></i> 删除</a></td>'+
					'</tr>';
				});
				table.html(innerHtml);
				table.find('[data-command="edit"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var url = './user-bets-hit-ranking/get';
					var params = {id: id};
					$.ajax({
						type : 'post',
						url : url,
						data : params,
						dataType : 'json',
						success : function(data) {
							AddUserBetsHitRankingModal.show(data);
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
									deleteUserBetsHitRanking(id);
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

		var deleting = false;
		var deleteUserBetsHitRanking = function(id) {
			if (deleting == true) {
				return;
			}
			var params = {id: id};
			var url = './user-bets-hit-ranking/del';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					deleting = false;
					if(data.error == 0) {
						reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
					deleting = false;
				},
				error: function() {
					deleting = false;
					toastr['error']('服务异常！请刷新后重试！' + data.message, '操作提示');
				}
			});
		}

		tableList.find('[data-command="add"]').unbind('click').click(function() {
			AddUserBetsHitRankingModal.show();
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

	var AddUserBetsHitRankingModal = function() {
		var modal = $('#modal-user-bets-hit-ranking-add');
		var form = modal.find('form');

		var initForm = function() {
			form.validate({
				rules: {
					username: {
						required: true
					},
					prizeMoney: {
						required: true,
						number: true,
						min: 1,
						max: 99999999
					},
					time: {
						required: true
					}
				},
				messages: {
					username: {
						required: '请输入用户名！'
					},
					prizeMoney: {
						required: '请输入奖金！',
						number: '请填写正确金额！',
						min: '最低奖金1元。'
					},
					time: {
						required: '请选择时间！'
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
			var id = modal.attr('data-id');
			var $lottery = modal.find('select[name="lottery"]').find(':selected');
			var name = $lottery.attr('data-name');;
			var username = modal.find('input[name="username"]').val();
			var prizeMoney = modal.find('input[name="prizeMoney"]').val();
			var time = modal.find('input[name="time"]').val();
			var code = $lottery.val();
			var type = $lottery.attr('data-type');
			if(name == '') {
				toastr['error']('名称不能为空！', '操作提示');
				return false;
			}
			if(username == '') {
				toastr['error']('用户名不能为空！', '操作提示');
				return false;
			}
			if(prizeMoney == '') {
				toastr['error']('奖金不能为空！', '操作提示');
				return false;
			}
			if(time == '') {
				toastr['error']('时间不能为空！', '操作提示');
				return false;
			}
			var params = {name: name, username: username, prizeMoney: prizeMoney, time: time, code: code, type: type, platform: 2};
			var url = './user-bets-hit-ranking/add';
			if(action == 'edit') {
				var id = modal.attr('data-id');
				params.id = id;
				url = './user-bets-hit-ranking/edit';
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
						UserBetsHitRankingTable.reload();
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
				modal.find('.modal-title').html('编辑中奖排行榜');
				// form.find('input[name="name"]').val(data.name);
				form.find('input[name="username"]').val(data.username);
				form.find('input[name="prizeMoney"]').val(data.prizeMoney);
				form.find('input[name="time"]').val(data.time);
				// form.find('input[name="code"]').val(data.code);
				// form.find('input[name="type"]').val(data.type);
				form.find('select[name="lottery"]').val(data.code);
				Metronic.initAjax();
			} else {
				form[0].reset();
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				var today = moment().format('YYYY-MM-DD');
				modal.find('input[name="time"]').val(today);
				modal.find('.modal-title').html('新增中奖排行榜');
			}
			updateDatePicker();
			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}

		var loadLottery = function(type) {
			var url = './lottery/list';
			var params = {type: type};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					buildLottery(data);
				}
			});
		}

		var buildLottery = function(data) {
			var select = modal.find('select[name="lottery"]');
			select.empty();
			$.each(data, function(idx, val) {
				select.append('<option value="' + val.bean.shortName + '" data-name="'+val.bean.showName+'" data-type="'+val.bean.type+'">' + val.bean.showName + '</option>');
			});
		}

		var init = function() {
			loadLottery();
			initForm();
		}

		return {
			init: init,
			show: show
		}

	}();

	return {
		init: function() {
			UserBetsHitRankingTable.init();
			AddUserBetsHitRankingModal.init();
			handelDatePicker();
		}
	}
}();