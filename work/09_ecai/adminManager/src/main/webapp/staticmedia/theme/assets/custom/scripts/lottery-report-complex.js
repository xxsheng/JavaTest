var LotteryReportComplex = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var ReportTable = function() {
		var tableList = $('#table-report');
		var $userLevels = $('.page-breadcrumb', tableList);
		var $username = $('input[name=username]', tableList);
		var $page = $(".page-content");
		var $loader = $("<div class='loader'></div>");

		var targetUser = Metronic.getURLParameter('username');
		if(targetUser) {
			tableList.find('input[name="username"]').val(targetUser);
		}
		
		var getParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var sTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var eTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			eTime = getNetDate(eTime);
			return {username: username, sTime: sTime, eTime: eTime};
		}
		
		var isSearching = false;
		var doSearch = function() {
			if(isSearching) return;
			var url = './lottery-report/complex';
			isSearching = true;
			$page.append($loader);
			var params = getParams();
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSearching = false;
					if(data.error == 0) {
						showUserLevels(data.userLevels);
						buildData(data.list, params.username);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
					$loader.remove();
				}
			});
		}
		
		var buildData = function(data, username) {
			var table = tableList.find('table > tbody').empty();
			if(data && data.length > 0) {
				var innerHtml = '';
				$.each(data, function(idx, val) {
					var formatUser;
					if(idx == 0 || val.name == username || val.hashMore == false) {
						formatUser = val.name;
					} else {
						formatUser = '<a data-command="details" data-val="' + val.name + '" href="javascript:;">' + val.name + '</a>';
					}

					var operation = '';
					if (idx != 0) {
						operation = '<a data-command="more" data-val="' + val.name + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详细 </a>';
					}

					var profit = val.prize + val.spendReturn + val.proxyReturn + val.activity + val.packet + val.rechargeFee - val.billingOrder;
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + formatUser + '</td>'+
//						'<td>' + val.transIn.toFixed(3) + '</td>'+
//						'<td>' + val.transOut.toFixed(3) + '</td>'+
						'<td>' + val.cashIn.toFixed(3) + '</td>'+
						'<td>' + val.cashOut.toFixed(3) + '</td>'+
						'<td>' + val.billingOrder.toFixed(3) + '</td>'+
						'<td>' + val.prize.toFixed(3) + '</td>'+
						'<td>' + (val.spendReturn + val.proxyReturn).toFixed(3) + '</td>'+
						'<td>' + val.activity.toFixed(3) + '</td>'+
						'<td>' + val.rechargeFee.toFixed(3) + '</td>'+
						'<td>' + val.dividend.toFixed(3) + '</td>'+
						'<td>' + profit.toFixed(3) + '</td>'+
						'<td>' + operation + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
				table.find('[data-command="details"]').unbind('click').click(function() {
					var username = $(this).attr('data-val');
					if (username == $username.val()) {
						return;
					}
					if (username) {
						$username.val(username);
					}
					doSearch();
				});
				table.find('[data-command="more"]').unbind('click').click(function() {
					var params = getParams();
					params.username = $(this).attr('data-val');
					doLoadDetails(params, function(data, params) {
						doShowLotteryDetails(data, params);
					});
				});
			} else {
				table.html('<tr><td colspan="10">没有报表记录</td></tr>');
			}
		}
		
		var isLoading = false;
		var doLoadDetails = function(params, callback) {
			if(isLoading) return;
			var url = './lottery-report/complex-details';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						if($.isFunction(callback)) {
							callback(data.list, params);
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var lotteryDetailsModal = $('#modal-user-report-lottery-details');
		var doShowLotteryDetails = function(data, params) {
			var thisTable = lotteryDetailsModal.find('table > tbody').empty();
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var profit = val.prize + val.spendReturn + val.proxyReturn - val.billingOrder;
				innerHtml += 
				'<tr class="align-center">'+
					'<td>' + val.name + '</td>'+
					'<td>' + val.billingOrder.toFixed(3) + '</td>'+
					'<td>' + val.prize.toFixed(3) + '</td>'+
					'<td>' + (val.spendReturn + val.proxyReturn).toFixed(3) + '</td>'+
					'<td>' + profit.toFixed(3) + '</td>'+
					'<td><a data-command="more" data-val="' + val.key + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详细 </a></td>'+
				'</tr>';
			});
			thisTable.html(innerHtml);
			thisTable.find('[data-command="more"]').unbind('click').click(function() {
				params.lotteryId = $(this).attr('data-val');
				doLoadDetails(params, function(data) {
					doShowMethodDetails(data);
				});
			});
			lotteryDetailsModal.modal('show');
		}
		
		var methodDetailsModal = $('#modal-user-report-method-details');
		var doShowMethodDetails = function(data) {
			var thisTable = methodDetailsModal.find('table > tbody').empty();
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var profit = val.prize + val.spendReturn + val.proxyReturn - val.billingOrder;
				innerHtml += 
				'<tr class="align-center">'+
					'<td>' + val.name + '</td>'+
					'<td>' + val.billingOrder.toFixed(3) + '</td>'+
					'<td>' + val.prize.toFixed(3) + '</td>'+
					'<td>' + (val.spendReturn + val.proxyReturn).toFixed(3) + '</td>'+
					'<td>' + profit.toFixed(3) + '</td>'+
				'</tr>';
			});
			thisTable.html(innerHtml);
			methodDetailsModal.modal('show');
		}

		var showUserLevels = function (userLevels) {
			$userLevels.empty();
			
			$.each(userLevels, function(index, username){
				$userLevels.append('<li data-username="'+username+'"><a href="javascript:;" data-command="showLowers" data-username="'+username+'">'+username+'</a></li>');
				
				if (index < userLevels.length - 1) {
					$userLevels.append('<i class=\"fa fa-angle-double-right prl5\"></i>');
				}
			});
		}

		tableList.on('click', '[data-command=showLowers]', function(){
			var thisUsername = $(this).attr('data-username');
			$username.val(thisUsername);
			doSearch();
		});
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			doSearch();
		});

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