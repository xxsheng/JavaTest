var GameList = function() {
	var GameTable = function() {
		var tableList = $('#table-game-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var gameName = tableList.find('input[name="gameName"]').val();
			var gameCode = tableList.find('input[name="gameCode"]').val();
			var platformId = tableList.find('select[name="platformId"]').val();
			var typeId = tableList.find('select[name="typeId"]').val();
			var display = tableList.find('select[name="display"]').val();
			var flashSupport = tableList.find('select[name="flashSupport"]').val();
			var h5Support = tableList.find('select[name="h5Support"]').val();
			return {gameName: gameName, gameCode: gameCode, platformId: platformId, typeId: typeId, display: display, flashSupport : flashSupport, h5Support : h5Support};
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './game/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var operate = '';
					if(val.bean.display == 0) { // 状态为不显示
						operate = '<a data-command="display" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 显示</a>';
					}
					else {
						operate = '<a data-command="unDisplay" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 隐藏</a>';
					}

					operate += '<a data-command="mod" data-id="' + val.bean.id + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑</a>';
					operate += '<a data-command="del" data-id="' + val.bean.id + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-remove"></i> 删除</a>';

					innerHtml +=
					'<tr class="align-center" data-id="' + val.bean.id + '">'+
						'<td>' + val.bean.id + '</td>'+
						'<td>' + val.bean.gameName + '</td>'+
						'<td>' + val.bean.gameCode + '</td>'+
						'<td>' + val.platformName + '</td>'+
						'<td>' + val.typeName + '</td>'+
						'<td>' + val.bean.imgUrl + '</td>'+
						'<td>' + val.bean.sequence + '</td>'+
						'<td>' + DataFormat.formatGameDisplay(val.bean.display) + '</td>'+
						'<td>' + DataFormat.formatGameFlashSupport(val.bean.flashSupport) + '</td>'+
						'<td>' + DataFormat.formatGameH5Support(val.bean.h5Support) + '</td>'+
						'<td>' + operate + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
				
				table.find('[data-command="display"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					confirmDisplay(id, 1);
				});
				table.find('[data-command="unDisplay"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					confirmDisplay(id, 0);
				});
				table.find('[data-command="mod"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					GameModModal.show(id);
				});
				table.find('[data-command="del"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					confirmDel(id);
				});
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
		
		var confirmDisplay = function(id, display) {
			var msg;
			if (display == 1) {
				msg = "确定将该游戏在前端显示(客户端操作需联系接口人)？";
			}
			else {
				msg = "确定将该游戏在前端隐藏(客户端操作需联系接口人)？";
			}
			
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doDisplay(id, display);
						}
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
					}
				}
			});
		};

		var doDisplay = function(id, display) {
			var params = {id : id, display : display};
			var url = './game/mod-display';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
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

		var confirmDel = function(id) {
			var msg = "确定将该游戏删除(客户端操作需联系接口人)？";

			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doDel(id);
						}
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
					}
				}
			});
		};

		var doDel = function(id) {
			var params = {id : id};
			var url = './game/del';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
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

		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});

		tableList.find('[data-command="add"]').unbind('click').click(function() {
			GameAddModal.show();
		});
		
		tableList.find('input[name="advanced"]').unbind('change').change(function() {
			isAdvanced($(this));
		});
		
		var isAdvanced = function(advanced) {
			if(!advanced) {
				advanced = tableList.find('input[name="advanced"]');
			}
			if(advanced.is(':checked')) {
				tableList.find('[data-hide="advanced"]').show();
			} else {
				clearAdvanced();
				tableList.find('[data-hide="advanced"]').hide();
			}
		}
		
		var clearAdvanced = function() {
			tableList.find('input[name="display"]').val('');
			tableList.find('input[name="flashSupport"]').val('');
			tableList.find('input[name="h5Support"]').val('');
		}
		
		isAdvanced();

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

	/**
	 * 新增游戏弹出窗口
	 */
	var GameAddModal = function() {
		var modal = $('#modal-game-add');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					gameName: {
						required: true
					},
					gameCode: {
						required: true
					},
					typeId: {
						required: true
					},
					imgUrl: {
						required: true
					},
					sequence: {
						required: true,
						number: true,
						min: 1,
						max: 9999
					}
				},
				messages: {
					gameName: {
						required: '游戏名不能为空！'
					},
					gameCode: {
						required: '游戏编码不能为空！'
					},
					typeId: {
						required: '游戏类型不能为空！'
					},
					imgUrl: {
						required: '图片不能为空！'
					},
					sequence: {
						required: '排序号不能为空！',
						number: '排序号必须为数字！',
						min: '最小为1！',
						min: '最大为9999！'
					},
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

		var show = function() {
			form.find('input[name="gameName"]').val('');
			form.find('input[name="gameCode"]').val('');
			form.find('select[name="typeId"]').val('1');
			form.find('input[name="imgUrl"]').val('');
			form.find('input[name="sequence"]').val('');
			form.find('input[name="display"]').parent().removeClass('checked');
			form.find('input[name="display"][value="1"]').parent().addClass('checked');
			form.find('input[name="flashSupport"]').parent().removeClass('checked');
			form.find('input[name="flashSupport"][value="1"]').parent().addClass('checked');
			form.find('input[name="h5Support"]').parent().removeClass('checked');
			form.find('input[name="h5Support"][value="0"]').parent().addClass('checked');
			form.find('input[name="progressiveSupport"]').parent().removeClass('checked');
			form.find('input[name="progressiveSupport"][value="0"]').parent().addClass('checked');
			form.find('input[name="progressiveCode"]').val('');
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}

		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var gameName = modal.find('input[name="gameName"]').val();
			var gameCode = modal.find('input[name="gameCode"]').val();
			var platformId = modal.find('input[name="platformId"]').val();
			var typeId = modal.find('select[name="typeId"]').val();
			var imgUrl = modal.find('input[name="imgUrl"]').val();
			var sequence = modal.find('input[name="sequence"]').val();
			var display = modal.find('input[name="display"]:checked').val();
			var flashSupport = modal.find('input[name="flashSupport"]:checked').val();
			var h5Support = modal.find('input[name="h5Support"]:checked').val();
			var progressiveSupport = modal.find('input[name="progressiveSupport"]:checked').val();
			var progressiveCode = modal.find('input[name="progressiveCode"]').val();
			var params = {gameName: gameName, gameCode: gameCode, platformId: platformId, typeId: typeId, imgUrl: imgUrl, sequence: sequence, display: display,
				flashSupport: flashSupport, h5Support: h5Support, progressiveSupport: progressiveSupport, progressiveCode: progressiveCode};
			var url = './game/add';
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
						GameTable.reload();
						toastr['success']('添加游戏成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('添加游戏失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
					isSending = false;
				}
			});
		}

		var init = function() {
			initForm();
		}

		return {
			init: init,
			show: show
		}
	}();

	/**
	 * 修改游戏弹出窗口
	 */
	var GameModModal = function() {
		var modal = $('#modal-game-mod');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					gameName: {
						required: true
					},
					gameCode: {
						required: true
					},
					typeId: {
						required: true
					},
					imgUrl: {
						required: true
					},
					sequence: {
						required: true,
						number: true,
						min: 1,
						max: 9999
					}
				},
				messages: {
					gameName: {
						required: '游戏名不能为空！'
					},
					gameCode: {
						required: '游戏编码不能为空！'
					},
					typeId: {
						required: '游戏类型不能为空！'
					},
					imgUrl: {
						required: '图片不能为空！'
					},
					sequence: {
						required: '排序号不能为空！',
						number: '排序号必须为数字！',
						min: '最小为1！',
						min: '最大为9999！'
					},
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

		var show = function(id) {
			var params = {id : id};
			$.ajax({
				type : 'post',
				url : './game/get',
				data : params,
				dataType : 'json',
				success : function(data) {
					var bean = data.data;
					isSending = false;
					if(data.error == 0) {
						form.find('input[name="id"]').val(id);
						form.find('input[name="gameName"]').val(bean.gameName);
						form.find('input[name="gameCode"]').val(bean.gameCode);
						form.find('select[name="typeId"]').val(bean.typeId);
						form.find('input[name="imgUrl"]').val(bean.imgUrl);
						form.find('input[name="sequence"]').val(bean.sequence);
						form.find('input[name="display"]').parent().removeClass('checked');
						form.find('input[name="display"][value="'+bean.display+'"]').parent().addClass('checked');
						form.find('input[name="display"][value="'+bean.display+'"]').attr('checked');
						form.find('input[name="flashSupport"]').parent().removeClass('checked');
						form.find('input[name="flashSupport"][value="'+bean.flashSupport+'"]').parent().addClass('checked');
						form.find('input[name="flashSupport"][value="'+bean.flashSupport+'"]').attr('checked');
						form.find('input[name="h5Support"]').parent().removeClass('checked');
						form.find('input[name="h5Support"][value="'+bean.h5Support+'"]').parent().addClass('checked');
						form.find('input[name="h5Support"][value="'+bean.h5Support+'"]').attr('checked');
						form.find('input[name="progressiveSupport"]').parent().removeClass('checked');
						form.find('input[name="progressiveSupport"][value="'+bean.progressiveSupport+'"]').parent().addClass('checked');
						form.find('input[name="progressiveSupport"][value="'+bean.progressiveSupport+'"]').attr('checked');
						form.find('input[name="progressiveCode"]').val(bean.progressiveCode);
						form.find('.has-error').removeClass('has-error');
						form.find('.has-success').removeClass('has-success');
						modal.modal('show');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('获取游戏详情失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var id = modal.find('input[name="id"]').val();
			var gameName = modal.find('input[name="gameName"]').val();
			var gameCode = modal.find('input[name="gameCode"]').val();
			var platformId = modal.find('input[name="platformId"]').val();
			var typeId = modal.find('select[name="typeId"]').val();
			var imgUrl = modal.find('input[name="imgUrl"]').val();
			var sequence = modal.find('input[name="sequence"]').val();
			var display = modal.find('input[name="display"]:checked').val();
			var flashSupport = modal.find('input[name="flashSupport"]:checked').val();
			var h5Support = modal.find('input[name="h5Support"]:checked').val();
			var progressiveSupport = modal.find('input[name="progressiveSupport"]:checked').val();
			var progressiveCode = modal.find('input[name="progressiveCode"]').val();
			var params = {id: id, gameName: gameName, gameCode: gameCode, platformId: platformId, typeId: typeId, imgUrl: imgUrl, sequence: sequence, display: display,
				flashSupport: flashSupport, h5Support: h5Support, progressiveSupport: progressiveSupport, progressiveCode: progressiveCode};
			var url = './game/mod';
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
						GameTable.reload();
						toastr['success']('修改游戏成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('修改游戏失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
					isSending = false;
				}
			});
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
			GameTable.init();
			GameAddModal.init();
			GameModModal.init();
		}
	}
}();