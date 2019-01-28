var LotteryUserBetsBatch = function() {
	
	var handleSelect = function() {
		$('.bs-select').selectpicker({
			iconBase: 'fa',
			tickIcon: 'fa-check'
		});
		$('.bs-select').selectpicker('refresh');
	}
	
	var UserBetsBatchTable = function() {
		var table = $('#table-user-bets-batch');
		var console = table.find('.console');
		
		var isPlayRulesLoading = false;
		var loadPlayRules = function(lotteryType) {
			if(isPlayRulesLoading) return;

			if (!lotteryType) {
                table.find('select[name="rule"]').empty();
                handleSelect();
                return;
			}

			var url = './lottery-play-rules/simple-list';
			var params = {typeId: lotteryType};
			isPlayRulesLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isPlayRulesLoading = false;
                    if (data.error == 0) {
                        buildPlayRules(data.data);
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
				}
			});
		}
		
		var buildPlayRules = function(data) {
			var rule = table.find('select[name="rule"]');
			rule.empty();
			rule.append('<option value="">全部玩法</option>');
			$.each(data, function(idx, val) {
				rule.append('<option value="' + val.ruleId + '">' + val.groupName + '_' + val.name + '</option>');
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
			var lottery = table.find('select[name="lottery"]');
			lottery.empty();
			lottery.append('<option value="">全部彩种</option>');
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
		
		var isLoading = false;
		var stepQuery = function(lotteryId, ruleId, expect, match) {
			if(isLoading) return;
			console.append('<div class="line">正在查询...</div>');
			var url = './lottery-user-bets/batch';
			var params = {step: 'query', lotteryId: lotteryId, ruleId: ruleId, expect: expect, match: match};
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						console.append('<div class="line">已查询到可撤单数据<i class="danger"> ' + data.data + ' </i>条。</div>');
						if(data.data == 0) {
							console.append('<div class="line">没有可撤单数据，操作结束。</div>');
						} else {
							console.append('<div class="line action">是否继续撤单？<a data-command="yes" href="javascript:;" class="action">是</a>/<a data-command="no" href="javascript:;" class="action">否</a></div>');
							console.find('a[data-command="yes"]').unbind('click').click(function() {
								stepExecute(lotteryId, ruleId, expect, match);
								console.find('a[data-command="yes"]').unbind('click');
								console.find('a[data-command="no"]').unbind('click');
							});
							console.find('a[data-command="no"]').unbind('click').click(function() {
								console.append('<div class="line"><i class="danger">操作已取消...</i></div>');
								console.find('a[data-command="yes"]').unbind('click');
								console.find('a[data-command="no"]').unbind('click');
							});
						}
					}
					else {
						console.append('<div class="line"><i class="danger">操作失败！' + data.message + '</i></div>');
					}
				}
			});
		}
		
		var stepExecute = function(lotteryId, ruleId, expect, match) {
			console.append('<div class="line">正在执行中...(请不要关闭浏览器或切换页面。)</div>');
			var url = './lottery-user-bets/batch';
			var params = {step: 'execute', lotteryId: lotteryId, ruleId: ruleId, expect: expect, match: match};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						console.append('<div class="line"><i class="success">操作完成！</i></div>');
					}
					else {
						console.append('<div class="line"><i class="danger">操作失败！' + data.message + '</i></div>');
					}
				}
			});
		}
		
		table.find('a[data-command="cancel"]').unbind('click').click(function() {
			var lotteryId = table.find('select[name="lottery"]').val();
			var ruleId = table.find('select[name="rule"]').val();
			var match = table.find('select[name="match"]').val();
			var expect = table.find('input[name="expect"]').val();
			if(lotteryId) {
				console.show();
				console.empty();
				stepQuery(lotteryId, ruleId, expect, match);
			} else {
				console.show();
				console.empty();
				console.append('<div class="line">请选择要撤单的彩种！</div>');
			}
		});
		
		return {
			init: function() {
				console.hide();
				console.empty();
			}
		}
	}();
	
	return {
		init: function() {
			UserBetsBatchTable.init();
		}
	}
}();