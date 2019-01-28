var ActivityRebateWheel = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	};
	
	var ActivityRebateWheelConfig = function() {
		var $thisPanel = $('#activity-rebate-wheel-config');
		var $ul = $thisPanel.find('.rlist > ul');

		var loadData = function() {
			var url = './activity-rebate-wheel/list';
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
			var rulesObj = $.parseJSON(data.rules);
			$thisPanel.attr('data-id', data.id);
			$ul.empty();

			$.each(rulesObj.rules, function(idx, rule) {
				var minCost = rule.minCost;
				var maxCost = rule.maxCost;
				if (maxCost <= -1) {
					maxCost = '无限';
				}
				$ul.append('<li>消费<span>' + minCost + '</span>-<span>' + maxCost + '</span><br/>随机金额<span>' + rule.amounts +'</span><br/>');
			});

			// 绑定事件
			$thisPanel.find('[data-command="edit"]').unbind('click').click(function() {
				EditActivityRebateWheelConfigModal.show(data);
			});
			if(data.status == 0) {
				$thisPanel.find('[data-command="status"]').html('<i class="fa fa-ban"></i> 暂停</a>');
				$thisPanel.find('[data-command="status"]').attr('data-status', data.status);
			}
			else if(data.status == -1) {
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
									updateStatus(id, -1);
								}
								if(status == -1) {
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
			var url = './activity-rebate-wheel/update-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						init();
						if(status == 0) {
							toastr['success']('已启用该活动！', '操作提示');
						}
						if(status == -1) {
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
	
	var EditActivityRebateWheelConfigModal = function() {
		var $modal = $('#activity-rebate-wheel-config-edit');
		var $form = $modal.find('form');
		var $rules = $form.find('[data-type="rules"]');

		var initForm = function() {
			$modal.find('[data-command="submit"]').unbind('click').click(function() {
				doSubmit();
			});
		};

		var doSubmit = function() {
			var rules = checkGetRules();
			if (rules == null) {
				return;
			}

			var rule = {rules: rules};
			rule = $.toJSON(rule);
			var params = {rule: rule};
			var url = './activity-rebate-wheel/edit';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						$modal.modal('hide');
						ActivityRebateWheelConfig.init();
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

				var $amounts = $rule.find("input[name=amounts]");
				var amountsVal = $amounts.val().trim();
				if ($.isEmpty(amountsVal)) {
					toastr['error']('请输入随机金额', '操作提示');
					$amounts.focus();
					return null;
				}
				var amountsValArr = amountsVal.split(',');
				if (amountsValArr.length <= 0) {
					toastr['error']('随机金额输入不正确，请使用英文逗号分割', '操作提示');
					$amounts.focus();
					return null;
				}

				var amountsArr = new Array();
				for(var j=0; j<amountsValArr.length; j++) {
					var amount = amountsValArr[j];
					if (!$.isDigits(amount)) {
						toastr['error']('随机金额['+amount+']输入不正确，请输入数值', '操作提示');
						$amounts.focus();
						return null;
					}

					amount = parseInt(amount);
					if (amount < 0 || amount > 99999999) {
						toastr['error']('随机金额['+amount+']数值错误，数值只能为1至99999999！', '操作提示');
						$amounts.focus();
						return null;
					}

					amountsArr.push(amount);
				}

				if (amountsArr.length <= 0) {
					toastr['error']('随机金额输入不正确，请检查', '操作提示');
					$amounts.focus();
					return null;
				}

				rules.push({minCost: minCost, maxCost: maxCost, amounts: amountsArr});
			}

			return rules;
		}

		var show = function(data) {
			$form[0].reset();
			if(data) {
				$modal.attr('data-id', data.id);
				var rulesObj = $.parseJSON(data.rules);
				resetTableData(rulesObj.rules);
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
						'<table class="table table-bordered  rules-table">'+
						'<tr>'+
						'<td width="45%">最小消费</td>'+
						'<td width="45%">最大消费</td>'+
						'<td width="10%"><a data-command="deleteRule" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i></a></td>'+
						'</tr>'+
						'<tr>'+
						'<td><input type="number" name="minCost" class="form-control input-sm" required autocomplete="off"></td>'+
						'<td><input type="number" name="maxCost" class="form-control input-sm" required autocomplete="off"></td>'+
						'<td></td>'+
						'</tr>' +
						'<tr>'+
						'<td>随机金额</td>'+
						'<td colspan="2"><input type="text" name="amounts" class="form-control input-sm" required autocomplete="off"></td>'+
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
							'<table class="table table-bordered rules-table">'+
								'<tr>'+
									'<td width="45%">最小消费</td>'+
									'<td width="45%">最大消费</td>'+
									'<td width="10%"><a data-command="deleteRule" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i></a></td>'+
								'</tr>'+
								'<tr>'+
									'<td><input type="number" name="minCost" class="form-control input-sm" min="1" max="99999999" value="'+val.minCost+'" required autocomplete="off"></td>'+
									'<td><input type="number" name="maxCost" class="form-control input-sm" min="-1" max="99999999" value="'+val.maxCost+'" required autocomplete="off"></td>'+
									'<td></td>'+
								'</tr>' +
								'<tr>'+
									'<td>随机金额</td>'+
									'<td colspan="2"><input type="text" name="amounts" class="form-control input-sm" value="'+val.amounts+'" required autocomplete="off"></td>'+
								'</tr>' +
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

	var ActivityRebateWheelBill = function() {
		var tableList = $('#activity-rebate-wheel-bill');
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
			ajaxUrl: './activity-rebate-wheel/bill',
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
			var tableList = $('#activity-rebate-wheel-bill');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);

			ActivityRebateWheelConfig.init();
			EditActivityRebateWheelConfigModal.init();
			ActivityRebateWheelBill.init();
			handelDatePicker();
		}
	};
}();