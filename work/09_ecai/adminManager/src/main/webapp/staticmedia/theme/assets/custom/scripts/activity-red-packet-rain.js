var ActivityRedPacketRain = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	};
	
	var ActivityRedPacketRainConfig = function() {
		var $thisPanel = $('#activity-red-packet-rain-config');
		var $ul = $thisPanel.find('.rlist > ul');

		var loadData = function() {
			var url = './activity-red-packet-rain/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						buildData(data.data);
					}
					if(data.error == 1 || data.error == 2) {
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
		};
		
		var buildData = function(data) {
			$thisPanel.attr('data-id', data.id);
			$ul.empty();

			$.each(eval(data.rules), function(idx, val) {
				var minCost = val.minCost;
				var maxCost = val.maxCost;
				var probabilities = val.probabilities;

				var minAmount;
				var maxAmount;
				$.each(probabilities, function(proIndex, probability){
					var _minAmount = null;
					var _maxAmount = null;
					if ((probability.amount+"").indexOf("-") > -1) {
						var split = probability.amount.split("-");
						_minAmount = parseInt(split[0]);
						_maxAmount = parseInt(split[1]);
					}
					else {
						_minAmount = parseInt(probability.amount);
						_maxAmount = parseInt(probability.amount);
					}

					if (minAmount == null || _minAmount < minAmount) {
						minAmount = _minAmount;
					}
					if (maxAmount == null || _maxAmount > maxAmount) {
						maxAmount = _maxAmount;
					}
				});
				$ul.append('<li>消费<span>' + minCost + '</span>-<span>' + maxCost + '</span><br/>随机金额<span>' + minAmount + '</span>-<span>'+maxAmount+'</span><br/>');
			});

			// 绑定事件
			$thisPanel.find('[data-command="edit"]').unbind('click').click(function() {
				EditActivityRedPacketRainConfigModal.show(data);
			});
			if(data.status == 1) {
				$thisPanel.find('[data-command="status"]').html('<i class="fa fa-ban"></i> 暂停</a>');
				$thisPanel.find('[data-command="status"]').attr('data-status', data.status);
			}
			if(data.status == 0) {
				$thisPanel.find('[data-command="status"]').html('<i class="fa fa-check"></i> 启用</a>');
				$thisPanel.find('[data-command="status"]').attr('data-status', data.status);
			}
			$thisPanel.find('[data-command="status"]').unbind('click').click(function() {
				var id = $thisPanel.attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定暂停该活动？';
				if(status == 0) {
					msg = '确定启用该活动？';
				}
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								if(status == 0) {
									updateStatus(id, 1);
								}
								if(status == 1) {
									updateStatus(id, 0);
								}
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
		};
		
		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './activity-red-packet-rain/update-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						init();
						if(status == 1) {
							toastr['success']('已启用该活动！', '操作提示');
						}
						if(status == 0) {
							toastr['success']('已暂停该活动！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		};
		
		var init = function() {
			loadData();
		};
		
		return {
			init: init
		};
	}();
	
	var EditActivityRedPacketRainConfigModal = function() {
		var $modal = $('#activity-red-packet-rain-config-edit');
		var $form = $modal.find('form');
		var $rules = $form.find('[data-type="rules"]');

		var initForm = function() {
			$modal.find('[data-command="submit"]').unbind('click').click(function() {
				doSubmit();
			});
		};
		
		var getRules = function() {
			// var rules = [];
			// tableRules.find('tbody > tr').each(function() {
			// 	var total = $(this).find('input').eq(0).val();
			// 	var cost = $(this).find('input').eq(1).val();
			// 	if(total != '' && cost != '') {
			// 		total = parseFloat(total);
			// 		cost = parseFloat(cost);
			// 		rules.push({"common":{total: total, cost: cost}});
			// 	}
			// });
			//
			// var packages = [];
			// tableRulesPackage.find('tbody > tr').each(function() {
			// 	var amount = $(this).find('input').eq(0).val();
			// 	var count = $(this).find('input').eq(1).val();
			// 	if(amount != '' && count != '') {
			// 		packages.push({amount: amount, count: count});
			// 	}
			// });
			//
			// rules.push({"packages":packages});
			//
			// return $.toJSON(rules);
		};
		
		var doSubmit = function() {
			var hours = checkGetHours();
			if (hours == null) {
				return;
			}
			var durationMinutes = checkGetDurationMinutes();
			if (durationMinutes == null) {
				return;
			}
			var rules = checkGetRules();
			if (rules == null) {
				return;
			}

			rules = $.toJSON(rules);

			var id = $modal.attr('data-id');
			var params = {id: id, hours: hours, durationMinutes: durationMinutes, rules: rules};
			var url = './activity-red-packet-rain/edit';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						$modal.modal('hide');
						ActivityRedPacketRainConfig.init();
						toastr['success']('活动修改成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('活动修改失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
				}
			});
		};

		var checkGetHours = function(){
			var $hours = $form.find("input[name=hours]");
			var hours = $hours.val().trim();
			if ($.isEmptyObject((hours))) {
				toastr['error']('时间段不能为空！', '操作提示');
				$hours.focus();
				return null;
			}
			var hoursArr = hours.split(",");
			for(var i in hoursArr) {
				var hour = hoursArr[i];
				if (!$.isDigits(hour) || hour.length != 2) {
					toastr['error']('时间段必须全部为数字,英文逗号分割,不足10补0！', '操作提示');
					$hours.focus();
					return null;
				}
				var _hour = parseInt(hour);
				if (_hour < 0 || _hour > 23) {
					toastr['error']('时间段必须为24小时制！', '操作提示');
					$hours.focus();
					return null;
				}
			}

			var uniqueHoursArr = $.uniquelize(hoursArr);
			if (uniqueHoursArr.length != hoursArr.length) {
				toastr['error']('时间段所有数值必须唯一！', '操作提示');
				$hours.focus();
				return null;
			}

			return hours;
		}

		var checkGetDurationMinutes = function(){
			var $durationMinutes = $form.find("input[name=durationMinutes]");
			var durationMinutes = $durationMinutes.val().trim();
			if ($.isEmptyObject((durationMinutes))) {
				toastr['error']('持续分钟不能为空！', '操作提示');
				$durationMinutes.focus();
				return null;
			}
			if (!$.isDigits(durationMinutes)) {
				toastr['error']('持续分钟必须为数字！', '操作提示');
				$durationMinutes.focus();
				return null;
			}
			var _durationMinutes = parseInt(durationMinutes);
			if (_durationMinutes < 5 || _durationMinutes > 50) {
				toastr['error']('持续时间只能为5-50分钟！', '操作提示');
				$durationMinutes.focus();
				return null;
			}

			return durationMinutes;
		}

		var checkGetRules = function(){
			var $rules = $form.find('[data-type="rule"]');
			if (!$rules || $rules.length <= 0) {
				toastr['error']('必须添加至少一个规则！', '操作提示');
				return null;
			}

			// 先把所有值取出来,做基础验证
			var rules = new Array();
			for(var i=0; i<$rules.length; i++) {
				var $rule = $($rules[i]);
				if (i >= $rules.length) {
					break;
				}
				var $minCost = $rule.find("input[name=minCost]");
				var minCost = $minCost.val().trim();
				if (!$.isDigits(minCost)) {
					toastr['error']('最小消费必须为数值！', '操作提示');
					$minCost.focus();
					return null;
				}
				var _minCost = parseInt(minCost);
				if (_minCost < 1 || _minCost > 99999999) {
					toastr['error']('最小消费必须为数值只能为1-99999999！', '操作提示');
					$minCost.focus();
					return null;
				}
				minCost = _minCost;

				var $maxCost = $rule.find("input[name=maxCost]");
				var maxCost = $maxCost.val().trim();
				if (maxCost != '-1') {
					if (!$.isDigits(maxCost)) {
						toastr['error']('最大消费必须为数值！', '操作提示');
						$maxCost.focus();
						return null;
					}
				}

				var _maxCost = parseInt(maxCost);
				if (_maxCost < -1 || _maxCost > 99999999) {
					toastr['error']('最大消费必须为数值只能为-1至99999999！', '操作提示');
					$maxCost.focus();
					return null;
				}
				maxCost = _maxCost;

				var $probabilities = $rule.find('[data-type="probabilities"]');
				if (!$probabilities || $probabilities.length <= 0) {
					toastr['error']('必须为第'+(i+1)+'个规则添加至少一个比例！', '操作提示');
					return null;
				}

				var probabilities = new Array();
				var totalProbability = 0.0;
				for(var j=0; j<$probabilities.length; j++) {
					var $probabilityTr = $($probabilities[j]);
					var $amount = $probabilityTr.find("input[name=amount]");
					var amount = $amount.val().trim();
					if (amount.indexOf("-") > -1) {
						var amounts = amount.split("-");
						for(var k in amounts) {
							if (!$.isDigits(amounts[k])) {
								toastr['error']('第'+(i+1)+'个规则第'+(j+1)+'个金额必须为数值！', '操作提示');
								$amount.focus();
								return null;
							}
						}
					}
					else {
						if (!$.isDigits(amount)) {
							toastr['error']('第'+(i+1)+'个规则第'+(j+1)+'个金额必须为数值！', '操作提示');
							$amount.focus();
							return null;
						}
					}

					var $probability = $probabilityTr.find("input[name=probability]");
					var probability = $probability.val().trim();
					if (!$.isNumeric(probability) || parseFloat(probability) <= 0 || parseFloat(probability) > 1) {
						toastr['error']('第'+(i+1)+'个规则第'+(j+1)+'个比例必须为大于0小于1正确数值！', '操作提示');
						$probability.focus();
						return null;
					}
					probability = parseFloat(probability);
					totalProbability = $.floatAdd(totalProbability, probability);

					probabilities.push({amount: amount, probability: probability});
				}

				if (totalProbability != 1) {
					toastr['error']('第'+(i+1)+'个规则总比例不等于1,请检查！', '操作提示');
					return null;
				}

				rules.push({minCost: minCost, maxCost: maxCost, probabilities: probabilities});
			}

			// 做重复性验证

			return rules;
		}

		var show = function(data) {
			$form[0].reset();
			if(data) {
				$modal.attr('data-id', data.id);
				$form.find("input[name=hours]").val(data.hours);
				$form.find("input[name=durationMinutes]").val(data.durationMinutes);
				var rules = eval(data.rules);
				resetTableData(rules);
			}
			$form.find('.help-inline').empty();
			$form.find('.has-error').removeClass('has-error');
			$form.find('.has-success').removeClass('has-success');
			$modal.modal('show');
		};
		
		var initTableRules = function() {
			$rules.find('[data-command="deleteRule"]').unbind("click").click(function() {
				$(this).parents('[data-type="rule"]').remove();
			});

			$form.find('[data-command="addRule"]').unbind("click").click(function() {
				var length = $rules.children('[data-type="rule"]').length;
				var innerHtml = '';
				if (length > 0 && length % 2 == 0) {
					innerHtml += "<div class='clearfix'></div>";
				}

				if (length >= 10) {
					toastr['error']('最多添加10条规则！', '操作失败');
				}
				else {
					innerHtml += '<div class="col-md-6" data-type="rule">'+
						'<table class="table table-bordered">'+
						'<tr>'+
						'<td width="33%">最小消费</td>'+
						'<td width="33%">最大消费</td>'+
						'<td width="34%"><a data-command="deleteRule" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i> 删除</a></td>'+
						'</tr>'+
						'<tr>'+
						'<td><input type="number" name="minCost" class="form-control input-sm" required></td>'+
						'<td><input type="number" name="maxCost" class="form-control input-sm" required></td>'+
						'<td></td>'+
						'</tr>' +
						'<tr>'+
						'<td>金额</td>'+
						'<td>比例(%)</td>'+
						'<td><a data-command="addProbability" href="javascript:;" class="btn default btn-xs green"><i class="fa fa-plus"></i> 添加</a></td>'+
						'</tr>'+
						'</table>' +
						'</div>';
					$rules.append(innerHtml);

					// 自动滚动到最底部
					var $rulesDom = $rules[0];
					$rulesDom.scrollTop = $rulesDom.scrollHeight;

					initTableRules();
				}
			});

			$rules.find('[data-command="deleteProbability"]').unbind("click").click(function() {
				$(this).parents('tr').remove();
			});

			$rules.find('[data-command="addProbability"]').unbind("click").click(function() {
				var $tr = $('<tr data-type="probabilities"></tr>');
				$tr.append('<td><input type="text" name="amount" class="form-control input-sm" required></td>');
				$tr.append('<td><input type="number" name="probability" min="0.01" max="1" step="0.01" class="form-control input-sm" required></td>');

				var $td = $('<td></td>');
				var $a = $('<a data-command="deleteProbability" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i> 删除</a>');
				$td.append($a);
				$tr.append($td);
				$a.click(function(){
					$(this).parents('tr').remove();
				});

				$(this).parents('table').append($tr);
			});
		};
		
		var resetTableData = function(rules) {
			$rules.empty();
			if(rules) {
				$.each(rules, function(idx, val) {
					var innerHtml = '';
						if (idx > 0 && idx % 2 == 0) {
							innerHtml += "<div class='clearfix'></div>";
						}
						innerHtml += '<div class="col-md-6" data-type="rule">'+
							'<table class="table table-bordered">'+
								'<tr>'+
									'<td width="33%">最小消费</td>'+
									'<td width="33%">最大消费</td>'+
									'<td width="34%"><a data-command="deleteRule" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i> 删除</a></td>'+
								'</tr>'+
								'<tr>'+
									'<td><input type="number" name="minCost" class="form-control input-sm" min="1" max="99999999" value="'+val.minCost+'" required></td>'+
									'<td><input type="number" name="maxCost" class="form-control input-sm" min="-1" max="99999999" value="'+val.maxCost+'" required></td>'+
									'<td></td>'+
								'</tr>' +
								'<tr>'+
									'<td>金额</td>'+
									'<td>比例(%)</td>'+
									'<td><a data-command="addProbability" href="javascript:;" class="btn default btn-xs green"><i class="fa fa-plus"></i> 添加</a></td>'+
								'</tr>';
					$.each(val.probabilities, function(proIndex, probability){
						innerHtml +=
							'<tr data-type="probabilities">'+
								'<td><input type="text" name="amount" class="form-control input-sm" value="'+probability.amount+'" required></td>'+
								'<td><input type="number" name="probability" min="0.01" max="1" step="0.01" class="form-control input-sm" value="'+probability.probability+'" required></td>'+
								'<td><a data-command="deleteProbability" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i> 删除</a></td>';
							'</tr>';
					});
					innerHtml +=
							'</table>' +
						'</div>';
					$rules.append(innerHtml);
				});
			}
			initTableRules();
		};
		
		var init = function() {
			initForm();
		};
		
		return {
			init: init,
			show: show
		};
		
	}();

	var ActivityRedPacketRainBill = function() {
		var tableList = $('#activity-red-packet-rain-bill');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var minTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var maxTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			maxTime = getNetDate(maxTime);
			var ip = tableList.find('input[name="ip"]').val();
			return {username: username, minTime: minTime, maxTime: maxTime, ip: ip};
		};
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './activity-red-packet-rain/bill',
			ajaxData: getSearchParams,
			beforeSend: function() {
			},
			complete: function() {
			},
			success: function(list, rsp) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + val.bean.id + '</td>'+
						'<td>' + val.username + '</td>'+
						'<td>' + val.bean.cost.toFixed(3) + '</td>'+
						'<td>' + val.bean.amount + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + val.bean.ip + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
				$("#totalAmount", tableList).html(rsp.totalAmount);
			},
			pageError: function(response) {
				$("#totalAmount", tableList).html("0");
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
				$("#totalAmount", tableList).html("0");
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		
		var init = function() {
			pagination.init();
		};
		
		var reload = function() {
			pagination.reload();
		};
		
		return {
			init: init,
			reload: reload
		};
		
	}();
	
	
	return {
		init: function() {
			var today = moment().format('YYYY-MM-DD');
			var tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');
			var tableList = $('#activity-red-packet-rain-bill');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);

			ActivityRedPacketRainConfig.init();
			EditActivityRedPacketRainConfigModal.init();
			ActivityRedPacketRainBill.init();
			handelDatePicker();
		}
	};
}();