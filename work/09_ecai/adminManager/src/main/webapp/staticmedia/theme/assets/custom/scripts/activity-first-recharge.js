var ActivityFirstRecharge = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	};
	
	var ActivityFirstRechargeConfig = function() {
		var $thisPanel = $('#activity-first-recharge-config');
		var $ul = $thisPanel.find('.rlist > ul');

		var loadData = function() {
			var url = './activity-first-recharge/list';
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
				var minRecharge = val.minRecharge;
				var maxRecharge = val.maxRecharge;
				var amount = val.amount;

				$ul.append('<li>充值<span>' + minRecharge + '</span>-<span>' + maxRecharge + '</span><br/>赠送金额<span>' + amount + '</span><br/>');
			});

			// 绑定事件
			$thisPanel.find('[data-command="edit"]').unbind('click').click(function() {
				EditActivityFirstRechargeConfigModal.show(data);
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
			var url = './activity-first-recharge/update-status';
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
	
	var EditActivityFirstRechargeConfigModal = function() {
		var $modal = $('#activity-first-recharge-config-edit');
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

			rules = $.toJSON(rules);

			var id = $modal.attr('data-id');
			var params = {id: id, rules: rules};
			var url = './activity-first-recharge/edit';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						$modal.modal('hide');
						ActivityFirstRechargeConfig.init();
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
				var $minRecharge = $rule.find("input[name=minRecharge]");
				var minRecharge = $minRecharge.val().trim();
				if (!$.isDigits(minRecharge)) {
					toastr['error']('最小充值必须为数值！', '操作提示');
					$minRecharge.focus();
					return null;
				}
				var _minRecharge = parseInt(minRecharge);
				if (_minRecharge < 1 || _minRecharge > 99999999) {
					toastr['error']('最小充值必须为数值只能为1-99999999！', '操作提示');
					$minRecharge.focus();
					return null;
				}
				minRecharge = _minRecharge;

				var $maxRecharge = $rule.find("input[name=maxRecharge]");
				var maxRecharge = $maxRecharge.val().trim();
				if (maxRecharge != '-1') {
					if (!$.isDigits(maxRecharge)) {
						toastr['error']('最大充值必须为数值！', '操作提示');
						$maxRecharge.focus();
						return null;
					}
				}

				var _maxRecharge = parseInt(maxRecharge);
				if (_maxRecharge < -1 || _maxRecharge > 99999999) {
					toastr['error']('最大充值必须为数值只能为-1至99999999！', '操作提示');
					$maxRecharge.focus();
					return null;
				}
				maxRecharge = _maxRecharge;

				var $amount = $rule.find("input[name=amount]");
				var amount = $amount.val().trim();
				if (amount != '-1') {
					if (!$.isDigits(amount)) {
						toastr['error']('赠送金额必须为数值！', '操作提示');
						$amount.focus();
						return null;
					}
				}

				var _amount = parseInt(amount);
				if (_amount < -1 || _amount > 99999999) {
					toastr['error']('赠送金额必须为数值只能为-1至99999999！', '操作提示');
					$amount.focus();
					return null;
				}
				amount = _amount;
				rules.push({minRecharge: minRecharge, maxRecharge: maxRecharge, amount: amount});
			}

			return rules;
		}

		var show = function(data) {
			$form[0].reset();
			if(data) {
				$modal.attr('data-id', data.id);
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
							'<td width="33%">最小充值</td>'+
							'<td width="33%">最大充值</td>'+
							'<td width="34%"><a data-command="deleteRule" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i> 删除</a></td>'+
						'</tr>'+
						'<tr>'+
							'<td><input type="number" name="minRecharge" class="form-control input-sm" required></td>'+
							'<td><input type="number" name="maxRecharge" class="form-control input-sm" required></td>'+
							'<td></td>'+
						'</tr>' +
						'<tr>'+
							'<td>赠送金额</td>'+
							'<td><input type="number" name="amount" class="form-control input-sm" min="1" max="99999999" required></td>'+
							'<td></td>'+
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
					innerHtml +=
						'<div class="col-md-6" data-type="rule">'+
						'<table class="table table-bordered">'+
							'<tr>'+
								'<td width="33%">最小充值</td>'+
								'<td width="33%">最大充值</td>'+
								'<td width="34%"><a data-command="deleteRule" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-trash-o"></i> 删除</a></td>'+
							'</tr>'+
							'<tr>'+
								'<td><input type="number" name="minRecharge" class="form-control input-sm" min="1" max="99999999" value="'+val.minRecharge+'" required></td>'+
								'<td><input type="number" name="maxRecharge" class="form-control input-sm" min="-1" max="99999999" value="'+val.maxRecharge+'" required></td>'+
								'<td></td>'+
							'</tr>'+
							'<tr>'+
								'<td>赠送金额</td>'+
								'<td><input type="number" name="amount" class="form-control input-sm" min="1" max="99999999" value="'+val.amount+'" required></td>'+
								'<td></td>'+
							'</tr>';
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

	var ActivityFirstRechargeBill = function() {
		var tableList = $('#activity-first-recharge-bill');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var sDate = tableList.find('[data-field="time"] > input[name="from"]').val();
			var eDate = tableList.find('[data-field="time"] > input[name="to"]').val();
			eDate = getNetDate(eDate);
			
			var ip = tableList.find('input[name="ip"]').val();
			return {username: username, sDate: sDate, eDate: eDate, ip: ip};
		};
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './activity-first-recharge/bill',
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
						'<td>' + val.bean.recharge + '</td>'+
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
			var tableList = $('#activity-first-recharge-bill');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);

			ActivityFirstRechargeConfig.init();
			EditActivityFirstRechargeConfigModal.init();
			ActivityFirstRechargeBill.init();
			handelDatePicker();
		}
	};
}();