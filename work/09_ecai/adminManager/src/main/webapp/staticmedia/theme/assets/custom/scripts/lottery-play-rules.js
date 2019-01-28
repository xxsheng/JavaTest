var LotteryPlayRules = function() {
	
	var LotteryPlayRulesTable = function() {
		var tableList = $('#table-lottery-play-rules-list');
		
		var isLoading = false;
		var loadData = function() {
			var lotteryId = tableList.find('select[name="lottery"]').val();
			var groupId = tableList.find('select[name="group"]').val();
			if(isLoading) return;
			var url = './lottery-play-rules/list';
			var params = {lotteryId: lotteryId, groupId: groupId};
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
                    if (data.error == 0) {
                        buildData(data.data);
                    }
                    else {
                        bootbox.dialog({
                            message: data.message,
                            title: '提示消息',
                            buttons: {
                                success: {
                                    label: '<i class="fa fa-check"></i> 确定',
                                    className: 'btn-success',
                                    callback: function() {}
                                }
                            }
                        });
                    }
                    isLoading = false;
				}
			});
		}
		
		var buildData = function(data) {
			var table = tableList.find('table > tbody').empty();
			var innerHtml = '';
			$.each(data, function(idx, val) {
                var statusAction;
                if(val.status == 0) {
                    statusAction = '<a data-command="status" data-status="'+val.status+'" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用 </a>';
                }
                else {
                    statusAction = '<a data-command="status" data-status="'+val.status+'" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
                }
				innerHtml +=
				'<tr class="align-center" data-ruleId="' + val.ruleId + '" data-lotteryId="' + val.lotteryId + '" data-typeName="' + val.typeName + '" data-lotteryName="' + val.lotteryName + '" data-groupName="'+ val.groupName +'" data-name="'+ val.name +'">'+
					'<td>' + val.lotteryName + '</td>'+
					'<td>' + val.groupName + '_' + val.name + '</td>'+
					'<td>' + val.minNum + '</td>'+
					'<td>' + val.maxNum + '</td>'+
					'<td>' + val.totalNum + '</td>'+
					'<td>' + DataFormat.formatLotteryPlayRulesFixed(val.fixed) + '</td>'+
					'<td><p style="max-width: 200px;white-space: nowrap;text-overflow: ellipsis;overflow: hidden;" title="'+val.prize+'">' + val.prize + '</p></td>'+
					'<td>' + val.dantiao + '</td>'+
					'<td>' + DataFormat.formatLotteryPlayRulesIsLocate(val.isLocate) + '</td>'+
					'<td>' + DataFormat.formatLotteryPlayRulesStatus(val.status) + '</td>'+
					'<td>' + statusAction + '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a></td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var $tr = $(this).parents('tr');
				var ruleId = $tr.attr('data-ruleId');
				var lotteryId = $tr.attr('data-lotteryId');
				var url = './lottery-play-rules/get';
				var params = {ruleId: ruleId, lotteryId: lotteryId};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						if (data.error == 0) {
							EditLotteryPlayRulesModal.show(data.data);
						}
						else {
                            toastr['error']('操作失败！' + data.message, '操作提示');
						}
					}
				});
			});
            table.find('[data-command="status"]').unbind('click').click(function() {
                var $tr = $(this).parents('tr');
                var ruleId = $tr.attr('data-ruleId');
                var lotteryId = $tr.attr('data-lotteryId');
                var typeName = $tr.attr('data-typeName');
                var lotteryName = $tr.attr('data-lotteryName');
                var groupName = $tr.attr('data-groupName');
                var name = $tr.attr('data-name');
                var status = $(this).attr('data-status');
                var enable;
                var msg;
                if(status == 0) {
                    msg = '确定禁用<span class="fw600 fs14">'+groupName + '_' + name + '</span>？(禁用后该玩法无法投注，已投注的不影响。)';
                    enable = false;
                }
                else {
                    msg = '确定启用<span class="fw600 fs14">'+groupName + '_' + name + '</span>？';
                    enable = true;
                }
                bootbox.dialog({
                    message: msg,
                    title: '提示消息',
                    buttons: {
                        success: {
                            label: '<i class="fa fa-check"></i> 确定',
                            className: 'green-meadow',
                            callback: function() {
                                confirmScope(ruleId, lotteryId, typeName, lotteryName, enable);
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

        var confirmScope = function(ruleId, lotteryId, typeName, lotteryName, enable){
            bootbox.dialog({
                message: '请选择需要操作的范围',
                title: '提示消息',
                buttons: {
                    allLotteries: {
                        label: '所有彩种（' + typeName + '）',
                        className: 'green-meadow',
                        callback: function() {
                            updateStatus(ruleId, null, enable);
                        }
                    },
                    singleLotteries: {
                        label: '当前彩种（' + lotteryName + '）',
                        className: 'green-meadow',
                        callback: function() {
                            updateStatus(ruleId, lotteryId, enable);
                        }
                    },
                    danger: {
                        label: '<i class="fa fa-undo"></i> 取消',
                        className: 'btn-danger',
                        callback: function() {}
                    }
                }
            });
        }

        var isSending = false;
        var updateStatus = function(ruleId, lotteryId, enable) {
            var params = {ruleId: ruleId, lotteryId: lotteryId, enable: enable};
            var url = './lottery-play-rules/update-status';
            isSending = true;
            $.ajax({
                type : 'post',
                url : url,
                data : params,
                dataType : 'json',
                success : function(data) {
                    isSending = false;
                    if(data.error == 0) {
                        loadData();
                        toastr['success']('操作成功！', '操作提示');
                    }
                    if(data.error == 1 || data.error == 2) {
                        toastr['error']('操作失败！' + data.message, '操作提示');
                    }
                }
            });
        }

		// 彩票类型
        var isLotteryTypeLoading = false;
        var loadLotteryType = function() {
            if(isLotteryTypeLoading) return;
            var url = './lottery-type/list';
            isLotteryTypeLoading = true;
            $.ajax({
                type : 'post',
                url : url,
                data : {},
                dataType : 'json',
                success : function(data) {
                    isLotteryTypeLoading = false;
                    buildLotteryType(data);
                }
            });
        }
        var buildLotteryType = function(data) {
            var lotteryType = tableList.find('select[name="lotteryType"]');
            tableList.find('select[name="group"]').empty();
            $.each(data, function(idx, val) {
                lotteryType.append('<option value="' + val.id + '">' + val.name + '</option>');
            });
            lotteryType.unbind('change').change(function() {
                loadLottery();
            });
            loadLottery();
        }

        // 彩票列表
        var isLotteryLoading = false;
        var loadLottery = function() {
            if(isLotteryLoading) return;
            var lotteryType = tableList.find('select[name="lotteryType"]');
            var params = {type: lotteryType.val()};
            var url = './lottery/list';
            isLotteryLoading = true;
            $.ajax({
                type : 'post',
                url : url,
                data : params,
                dataType : 'json',
                success : function(data) {
                    isLotteryLoading = false;
                    buildLottery(data);
                }
            });
        }
        var buildLottery = function(data) {
            var lottery = tableList.find('select[name="lottery"]');
            lottery.empty();
            tableList.find('select[name="group"]').empty();
            $.each(data, function(idx, val) {
                lottery.append('<option value="' + val.bean.id + '">' + val.bean.showName + '</option>');
            });
            lottery.unbind('change').change(function() {
                loadLotteryPlayRulesGroups();
            });
            loadLotteryPlayRulesGroups();
        }

        // 玩法组
        var isLotteryPlayRulesGroupLoading = false;
        var loadLotteryPlayRulesGroups = function() {
            if(isLotteryPlayRulesGroupLoading) return;
            var lotteryType = tableList.find('select[name="lotteryType"]');
            var params = {typeId: lotteryType.val()};
            var url = './lottery-play-rules-group/simple-list';
            isLotteryPlayRulesGroupLoading = true;
            $.ajax({
                type : 'post',
                url : url,
                data : params,
                dataType : 'json',
                success : function(data) {
                    isLotteryPlayRulesGroupLoading = false;
                    buildLotteryPlayRulesGroup(data.data);
                }
            });
        }
        var buildLotteryPlayRulesGroup = function(data) {
            var group = tableList.find('select[name="group"]');
            group.empty();
            group.append('<option value="">全部</option>');
            $.each(data, function(idx, val) {
                if (idx == 0) {
                    group.append('<option value="' + val.groupId + '" selected>' + val.name + '</option>');
                }
                else {
                    group.append('<option value="' + val.groupId + '">' + val.name + '</option>');
                }
            });
            group.unbind('change').change(function() {
                loadData();
            });
            loadData();
            handleSelect();
        }

        loadLotteryType();
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var EditLotteryPlayRulesModal = function() {
		var modal = $('#modal-lottery-play-rules-edit');
		var form = modal.find('form');
		var originalData;
		var initForm = function() {
			form.validate({
				rules: {
                    minNum: {
                        multipledigits: true,
                        required: true
					},
                    maxNum: {
                        multipledigits: true,
                        required: true
					}
				},
				messages: {
                    minNum: {
                        required: '请输入最小注数'
	                },
                    maxNum: {
                        required: '请输入最大注数'
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
			modal.find('[data-command="submitAllLotteries"]').unbind('click').click(function() {
				if(!form.validate().form()) {
				    return;
		    	}

		    	if (!check()) {
				    return;
                }

                doSubmit(true);
			});
			modal.find('[data-command="submitSingleLottery"]').unbind('click').click(function() {
                if(!form.validate().form()) {
                    return;
                }

                if (!check()) {
                    return;
                }

                doSubmit(false);
			});
		}

		var check = function(){
            var minNum = form.find('input[name="minNum"]').val();
            var maxNum = form.find('input[name="maxNum"]').val();

            var originalMinNumLen = originalData.minNum.split(',').length;
            var originalMaxNumLen = originalData.maxNum.split(',').length;
            var minNumLen = minNum.split(',').length;
            var maxNumLen = maxNum.split(',').length;
            if (originalMinNumLen != minNumLen) {
                toastr['error']('最小注数与原数据格式['+originalData.minNum+']不同！', '操作提示');
                return false;
            }
            if (originalMaxNumLen != maxNumLen) {
                toastr['error']('最大注数与原数据格式['+originalData.maxNum+']不同！', '操作提示');
                return false;
            }
            return true;
        }
		
		var isSending = false;
		var doSubmit = function(submitAllLotteries) {
			if(isSending) return;
			var ruleId = form.find('input[name="ruleId"]').val();
			var lotteryId = form.find('input[name="lotteryId"]').val();
			var minNum = form.find('input[name="minNum"]').val();
			var maxNum = form.find('input[name="maxNum"]').val();
			var params;
			if (submitAllLotteries) {
                params = {ruleId: ruleId, lotteryId: null, minNum: minNum, maxNum: maxNum};
			}
			else {
                params = {ruleId: ruleId, lotteryId: lotteryId, minNum: minNum, maxNum: maxNum};
			}
			var url = './lottery-play-rules/edit';
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
						LotteryPlayRulesTable.init();
						toastr['success']('操作成功！', '操作提示');
					}
					else {
                        toastr['error']('操作失败！' + data.message, '操作提示');
                    }
				},
				complete: function() {
					isSending = false;
					modal.modal('hide');
				}
			});
		}
		
		var show = function(data) {
            originalData = null;

			form[0].reset();
			if(data) {
                originalData = data;

				form.find('[data-field="rulesName"]').html(data.typeName + ' > ' + data.lotteryName + ' > ' + data.groupName + '_' + data.name);
				form.find('input[name="minNum"]').val(data.minNum);
				form.find('input[name="maxNum"]').val(data.maxNum);
				form.find('input[name="totalNum"]').val(data.totalNum);
				form.find('input[name="ruleId"]').val(data.ruleId);
				form.find('input[name="lotteryId"]').val(data.lotteryId);

                modal.find('[data-command=submitAllLotteries]').html('修改所有彩种（'+data.typeName+'）');
                modal.find('[data-command=submitSingleLottery]').html('修改当前彩种（'+data.lotteryName+'）');
				// Metronic.updateUniform(form.find('input[name="fixed"]'));
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

	var handleSelect = function() {
	    var $selects = $('.bs-select');
        $selects.selectpicker('destroy');
        $selects.selectpicker({
            iconBase: 'fa',
            tickIcon: 'fa-check'
        });
        $selects.selectpicker('refresh');
    }

	return {
		init: function() {
			EditLotteryPlayRulesModal.init();
		}
	}
}();