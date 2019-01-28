var LotteryPlayRulesGroup = function() {
	
	var LotteryPlayRulesGroupTable = function() {
		var tableList = $('#table-lottery-play-rules-group-list');
		
		var isLoading = false;
		var loadData = function() {
			var lotteryId = tableList.find('select[name="lottery"]').val();
			if(isLoading) return;
			var url = './lottery-play-rules-group/list';
			var params = {lotteryId: lotteryId};
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
				'<tr class="align-center" data-groupId="' + val.groupId + '" data-lotteryId="' + val.lotteryId + '" data-typeName="' + val.typeName + '" data-lotteryName="' + val.lotteryName + '">'+
					'<td>' + val.typeName + '</td>'+
					'<td>' + val.lotteryName + '</td>'+
					'<td>' + val.name + '</td>'+
					'<td>' + DataFormat.formatLotteryPlayRulesStatus(val.status) + '</td>'+
					'<td>' + statusAction + '</td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="status"]').unbind('click').click(function() {
				var $tr = $(this).parents('tr');
				var groupId = $tr.attr('data-groupId');
				var lotteryId = $tr.attr('data-lotteryId');
				var typeName = $tr.attr('data-typeName');
				var lotteryName = $tr.attr('data-lotteryName');
				var status = $(this).attr('data-status');
				var enable;
				var msg;
				if(status == 0) {
                    msg = '确定禁用该玩法组？(禁用后该玩法组下所有玩法无法投注，已投注的不影响。)';
                    enable = false;
				}
				else {
					msg = '确定启用该玩法组？';
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
							    confirmScope(groupId, lotteryId, typeName, lotteryName, enable);
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

		var confirmScope = function(groupId, lotteryId, typeName, lotteryName, enable){
            bootbox.dialog({
                message: '请选择需要操作的范围',
                title: '提示消息',
                buttons: {
                    allLotteries: {
                        label: '所有彩种（' + typeName + '）',
                        className: 'green-meadow',
                        callback: function() {
                            updateStatus(groupId, null, enable);
                        }
                    },
                    singleLotteries: {
                        label: '当前彩种（' + lotteryName + '）',
                        className: 'green-meadow',
                        callback: function() {
                            updateStatus(groupId, lotteryId, enable);
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
		var updateStatus = function(groupId, lotteryId, enable) {
			var params = {groupId: groupId, lotteryId: lotteryId, enable: enable};
			var url = './lottery-play-rules-group/update-status';
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
			$.each(data, function(idx, val) {
				lotteryType.append('<option value="' + val.id + '">' + val.name + '</option>');
			});
			lotteryType.unbind('change').change(function() {
                loadLottery();
			});
			loadLottery();
		}

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
            $.each(data, function(idx, val) {
                lottery.append('<option value="' + val.bean.id + '">' + val.bean.showName + '</option>');
            });
            lottery.unbind('change').change(function() {
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
	
	var handleSelect = function() {
		$('.bs-select').selectpicker({
			iconBase: 'fa',
			tickIcon: 'fa-check'
		});
		$('.bs-select').selectpicker('refresh');
	}
	
	return {
		init: function() {
            // LotteryPlayRulesGroupTable.init();
		}
	}
}();