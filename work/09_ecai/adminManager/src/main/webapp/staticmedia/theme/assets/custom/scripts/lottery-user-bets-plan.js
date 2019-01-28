var LotteryUserBetsPlan = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
		
	var handleSelect = function() {
		$('.bs-select').selectpicker({
			iconBase: 'fa',
			tickIcon: 'fa-check'
		});
		$('.bs-select').selectpicker('refresh');
	}
	
	var UserBetsPlanTable = function() {
		var tableList = $('#table-user-bets-plan-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var keyword = tableList.find('input[name="keyword"]').val();
			var username = tableList.find('input[name="username"]').val();
			var lotteryId = tableList.find('select[name="lottery"]').val();
			var method = tableList.find('select[name="rules"]').val();
			var expect = tableList.find('input[name="expect"]').val();
			var minTime = tableList.find('input[name="minTime"]').val();
			var maxTime = tableList.find('input[name="maxTime"]').val();
			var minBetsMoney = tableList.find('input[name="minBetsMoney"]').val();
			var maxBetsMoney = tableList.find('input[name="maxBetsMoney"]').val();
			var minMultiple = tableList.find('input[name="minMultiple"]').val();
			var maxMultiple = tableList.find('input[name="maxMultiple"]').val();
			var status = tableList.find('select[name="status"]').val();
			return {keyword: keyword, username: username, lotteryId: lotteryId, expect: expect, method: method, minTime: minTime, maxTime: maxTime, minBetsMoney: minBetsMoney, maxBetsMoney: maxBetsMoney, minMultiple: minMultiple, maxMultiple: maxMultiple, status: status};
		}
		
		var targetUser = Metronic.getURLParameter('username');
		if(targetUser) {
			tableList.find('input[name="username"]').val(targetUser);
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-bets-plan/list',
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
						'<td>' + val.bean.billno + '</td>'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
						'<td>' + val.lottery + '</td>'+
						'<td>' + val.bean.expect + '</td>'+
						'<td>' + val.mname + '</td>'+
						'<td>' + val.bean.money.toFixed(4) + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + val.bean.followCount + '</td>'+
						'<td>' + val.bean.rewardMoney.toFixed(4) + '</td>'+
						'<td>' + DataFormat.formatUserBetsPlanStatus(val.bean.status) + '</td>'+
						'<td><a data-command="details" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详细 </a></td>'+
					'</tr>';
				});
				table.html(innerHtml);
				
				table.find('[data-command="cancel"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var msg = '确定撤销订单？';
					bootbox.dialog({
						message: msg,
						title: '提示消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									cancelBets(id);
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
		
		var cancelBets = function(id) {
			var url = './lottery-user-bets/cancel';
			var params = {id: id};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('已成功撤销该订单！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var isLoading = false;
		var doLoadDetails = function(id) {
			if(isLoading) return;
			var params = {id: id};
			var url = './lottery-user-bets/get';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						doShowDetails(data.data);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var doShowDetails = function(data) {
			var modal = $('#modal-lottery-user-bets-details');
			modal.find('[data-field="username"]').html(data.username);
			modal.find('[data-field="billno"]').html(data.bean.billno);
			modal.find('[data-field="status"]').html(DataFormat.formatUserBetsStatus(data.bean.status));
			modal.find('[data-field="lottery"]').html(data.lottery);
			modal.find('[data-field="expect"]').html(data.bean.expect);
			modal.find('[data-field="mname"]').html(data.mname);
			modal.find('[data-field="nums"]').html(data.bean.nums);
			modal.find('[data-field="model"]').html(DataFormat.formatUserBetsModel(data.bean.model));
			modal.find('[data-field="multiple"]').html(data.bean.multiple);
			modal.find('[data-field="prizeInfo"]').html(data.bean.code + '+返点' + data.bean.point.toFixed(1) + '%');
			modal.find('[data-field="chaseStop"]').html(data.bean.chaseStop == 1 ? '是' : '否');
			modal.find('[data-field="time"]').html(data.bean.time);
			modal.find('[data-field="money"]').html(data.bean.money.toFixed(4) + '元');
			modal.find('[data-field="prizeMoney"]').html(data.bean.prizeMoney.toFixed(4) + '元');
			modal.find('[data-field="stopTime"]').html(data.bean.stopTime);
			modal.find('[data-field="openCode"]').html(data.bean.openCode);
			modal.find('textarea[name="codes"]').val(data.bean.codes);
			modal.modal('show');
		}
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
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
			tableList.find('input[name="expect"]').val('');
			tableList.find('input[name="minTime"]').val('');
			tableList.find('input[name="maxTime"]').val('');
			tableList.find('input[name="minBetsMoney"]').val('');
			tableList.find('input[name="maxBetsMoney"]').val('');
			tableList.find('input[name="minMultiple"]').val('');
			tableList.find('input[name="maxMultiple"]').val('');
			tableList.find('input[name="minPrizeMoney"]').val('');
			tableList.find('input[name="maxPrizeMoney"]').val('');
		}
		
		isAdvanced();
		
		var isPlayRulesLoading = false;
		var loadPlayRules = function(lotteryType) {
			if(isPlayRulesLoading) return;
			var url = './lottery-play-rules/list';
			var params = {type: lotteryType};
			isPlayRulesLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isPlayRulesLoading = false;
					buildPlayRules(data);
				}
			});
		}
		
		var buildPlayRules = function(data) {
			var rules = tableList.find('select[name="rules"]');
			rules.empty();
			rules.append('<option value="">全部类型</option>');
			$.each(data, function(idx, val) {
				rules.append('<option value="' + val.bean.methodName + '">' + val.bean.group + '_' + val.bean.name + '</option>');
			});
			handleSelect();
		}
		
		var isLotteryLoading = false;
		var loadLottery = function() {
			if(isLotteryLoading) return;
			var url = './lottery/list';
			isLotteryLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : {},
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
			lottery.append('<option value="">全部类型</option>');
			$.each(data, function(idx, val) {
				lottery.append('<option data-type="' + val.bean.type + '" value="' + val.bean.id + '">' + val.bean.showName + '</option>');
			});
			lottery.unbind('change').change(function() {
				var lotteryType = lottery.find('option:selected').attr('data-type');
				loadPlayRules(lotteryType);
			});
			handleSelect();
		}
		
		loadLottery();
		
		var init = function() {
			pagination.init();
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
			UserBetsPlanTable.init();
			handelDatePicker();
		}
	}
}();