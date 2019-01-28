var LotteryUserBill = function() {
	
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
	
	var UserBillTable = function() {
		var tableList = $('#table-user-bill-list');
		var tablePagelist = tableList.find('.page-list');
		
		var getSearchParams = function() {
			var keyword = tableList.find('input[name="keyword"]').val();
			var username = tableList.find('input[name="username"]').val();
			var type = tableList.find('select[name="type"]').val();
			var minTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var maxTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			maxTime = getNetDate(maxTime);
			var minMoney = tableList.find('input[name="minMoney"]').val();
			var maxMoney = tableList.find('input[name="maxMoney"]').val();
			var status = tableList.find('select[name="status"]').val();
			return {keyword: keyword, username: username, type: type, minTime: minTime, maxTime: maxTime, minMoney: minMoney, maxMoney: maxMoney, status: status};
		}
		
		var targetUser = Metronic.getURLParameter('username');
		if(targetUser) {
			tableList.find('input[name="username"]').val(targetUser);
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-bill/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var formatId = val.bean.id;
					if(val.bean.account == 2 && $.inArray(val.bean.type, [6,7,8,9,10]) != -1){
						formatId = '<a href="javascript:;" data-id="' + val.bean.refId + '" data-command="details">' + val.bean.id + '</a>';
					}
					innerHtml +=
					'<tr class="align-center" data-id="' + val.bean.id + '">'+
						'<td>' + formatId + '</td>'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
						'<td>' + val.account + '</td>'+
						'<td>' + DataFormat.formatUserBillType(val.bean.type) + '</td>'+
						'<td>' + val.bean.beforeMoney.toFixed(4) + '</td>'+
						'<td>' + val.bean.money.toFixed(4) + '</td>'+
						'<td>' + val.bean.afterMoney.toFixed(4) + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + val.bean.remarks + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
				table.find('[data-command="details"]').unbind('click').click(function() {
					var id = $(this).attr('data-id');
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
		
		var isLoading = false;
		var doLoadDetails = function(id) {
			if(isLoading) return;
			var params = {id: id};
			var url = './lottery-user-bill/details';
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
			var dateTime = tableList.find('[data-field="time"]');
			dateTime.find('input[name="from"]').val('').change();
			dateTime.find('input[name="to"]').val('').change();
			tableList.find('input[name="minMoney"]').val('');
			tableList.find('input[name="maxMoney"]').val('');
			tableList.find('select[name="status"] > option').eq(0).attr('selected', true);
			handleSelect();
		}
		
		isAdvanced();
		
		var init = function() {
			pagination.init();
		}
		
		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			var today = moment().format('YYYY-MM-DD');
			var tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');
			var tableList = $('#table-user-bill-list');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);
			
			UserBillTable.init();
			handleSelect();
			handelDatePicker();
		}
	}
}();