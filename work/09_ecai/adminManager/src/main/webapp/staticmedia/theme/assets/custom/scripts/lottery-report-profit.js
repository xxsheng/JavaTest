var LotteryReportProfit = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}

    var handleSelect = function(selector) {
        var $selects = $(selector);
        $selects.selectpicker('destroy');
        $selects.selectpicker({
            iconBase: 'fa',
            tickIcon: 'fa-check'
        });
        $selects.selectpicker('refresh');
    }
	
	var ReportTable = function() {
		var tableList = $('#table-report');

		var getParams = function() {
			var type = tableList.find('select[name="type"]').val();
			var lottery = tableList.find('select[name="lottery"]').val();
			var ruleId = tableList.find('select[name="rule"]').val();
			var sTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var eTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			eTime = getNetDate(eTime);
			return {type: type, lottery: lottery, ruleId: ruleId, sTime: sTime, eTime: eTime};
		}
		
		var isSearching = false;
		var doSearch = function() {
			if(isSearching) return;
			var params = getParams();
			var url = './lottery-report/profit';
			isSearching = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSearching = false;
					if(data.error == 0) {
						buildData(data.list);
					}
					else {
                        toastr['error']('操作失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
				}
			});
		}
		
		var buildData = function(data) {
			var table = tableList.find('table > tbody').empty();
			if(data.length > 0) {
				var innerHtml = '';
				$.each(data, function(idx, val) {
					var profit = val.returnMoney + val.prizeMoney - val.money;
					innerHtml += 
					'<tr class="align-center">'+
						'<td>' + val.field + '</td>'+
						'<td>' + val.money.toFixed(3) + '</td>'+
						'<td>' + val.returnMoney.toFixed(3) + '</td>'+
						'<td>' + val.prizeMoney.toFixed(3) + '</td>'+
						'<td>' + profit.toFixed(3) + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
			} else {
				table.html('<tr><td colspan="5">没有报表记录</td></tr>');
			}
		}
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			doSearch();
		});

		var loadPlayRules = function(typeId) {
			var url = './lottery-play-rules/simple-list';

			if (!typeId) {
				var select = tableList.find('select[name="rule"]');
				select.empty();
				handleSelect(select);
				return;
			}

			var params = {typeId: typeId};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
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
			var select = tableList.find('select[name="rule"]');
			select.empty();
			select.append('<option value="">全部玩法</option>');
			$.each(data, function(idx, val) {
				select.append('<option value="' + val.ruleId + '">' + val.groupName + '_' + val.name + '</option>');
			});

            handleSelect(select);
		}
		
		var loadLottery = function(type) {
			var url = './lottery/list';

			if (!type) {
                var select = tableList.find('select[name="lottery"]');
                select.empty();
                handleSelect(select);
                return;
			}

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
			var select = tableList.find('select[name="lottery"]');
			select.empty();
			select.append('<option value="">全部彩种</option>');
			$.each(data, function(idx, val) {
				select.append('<option value="' + val.bean.id + '">' + val.bean.showName + '</option>');
			});

            handleSelect(select);
		}
		
		var loadLotteryType = function() {
			var url = './lottery-type/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					buildLotteryType(data);
				}
			});
		}
		
		var buildLotteryType = function(data) {
			var select = tableList.find('select[name="type"]');
			select.empty();
			select.append('<option value="">全部类型</option>');
			$.each(data, function(idx, val) {
				select.append('<option value="' + val.id + '">' + val.name + '</option>');
			});
			select.unbind('change').change(function() {
				var type = select.val();
				loadLottery(type);
				loadPlayRules(type);
			});
            handleSelect(select);
            handleSelect('select[name="lottery"]');
            handleSelect('select[name="rule"]');
		}
		
		loadLotteryType();
		
		var init = function() {
			var today = moment().format('YYYY-MM-DD');
			var tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);
			doSearch();
		}
		
		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			ReportTable.init();
			handelDatePicker();
		}
	}
}();