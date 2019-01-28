var LotteryReportUserProfitRanking = function() {
	var ReportTable = function() {
		var todayTableReport = $('#today-table-report');
		var lastWeekTableReport = $('#last-week-table-report');
		var last2weeksTableReport = $('#last-2weeks-table-report');
		var todayTableReportCountDown = $('#countdown', todayTableReport);
		var lotteryDetailsModal = $('#modal-user-report-lottery-details');
		var methodDetailsModal = $('#modal-user-report-method-details');
		var dailyDetailsModal = $('#modal-user-report-daily-details');
		var LOAD_TYPE_TODAY = 1;
		var LOAD_TYPE_LAST_WEEK = 2;
		var LOAD_TYPE_LAST2_WEEKS = 3;

		var getTodayParams = function() {
			var sTime = moment().format('YYYY-MM-DD');
			var eTime = moment().add(1, 'days').format('YYYY-MM-DD');
			return {sTime: sTime, eTime: eTime};
		}
		var getLastWeekParams = function() {
			var sTime = moment().add(-15, 'days').format('YYYY-MM-DD');
			var eTime = moment().format('YYYY-MM-DD');
			return {sTime: sTime, eTime: eTime};
		}
		var getLast2weeksParams = function() {
			var sTime = moment().add(-30, 'days').format('YYYY-MM-DD');
			var eTime = moment().format('YYYY-MM-DD');
			return {sTime: sTime, eTime: eTime};
		}

		var doLoadDataByParams = function(params, tableList, loadType) {
			var url = './lottery-report/user-profit-ranking';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						buildData(data.list, tableList, loadType);
					}
					else {
						toastr['error']('查询失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
				}
			});
		}

		var buildData = function(data, tableList, loadType) {
			var table = tableList.find('table > tbody').empty();
			if(data.length > 0) {
				var innerHtml = '';
				$.each(data, function(idx, val) {
					var formatUser = '<a href="./lottery-user-profile?username='+val.name+'">' + val.name + '</a>';
					var profit = val.profit.toFixed(3);

					var operations;
					if (loadType == LOAD_TYPE_TODAY) {
						operations = '<a data-command="showLotteryDetails" data-val="' + val.name + '" data-sTime="'+val.sTime+'" data-eTime="'+val.eTime+'" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详细 </a>';
					}
					else {
						operations = '<a data-command="showDailyDetails" data-val="' + val.name + '" data-userId="' + val.userId + '" data-sTime="'+val.sTime+'" data-eTime="'+val.eTime+'" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详细 </a>';
					}
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + formatUser + '</td>'+
						'<td>' + profit + '</td>'+
						'<td>' + operations + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);

				table.find('[data-command="showLotteryDetails"]').unbind('click').click(function() {
					var username = $(this).attr('data-val');
					var sTime = $(this).attr('data-sTime');
					var eTime = $(this).attr('data-eTime');
					var loadPrams = {username: username, sTime: sTime, eTime: eTime, self: true};
					doLoadLotteryDetails(loadPrams, function(data, params) {
						doShowLotteryDetails(data, params);
					});
				});
				table.find('[data-command="showDailyDetails"]').unbind('click').click(function() {
					var userId = $(this).attr('data-userId');
					var sTime = $(this).attr('data-sTime');
					var eTime = $(this).attr('data-eTime');
					var loadPrams = {userId: userId, sTime: sTime, eTime: eTime};
					doLoadDailyDetails(loadPrams);
				});
			} else {
				table.html('<tr><td colspan="3">没有记录</td></tr>');
			}
		}

		var doLoadDailyDetails = function(params) {
			var url = './lottery-report/user-profit-ranking';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						doShowDailyDetails(data.list);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var doShowDailyDetails = function(data) {
			var thisTable = dailyDetailsModal.find('table > tbody').empty();
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var formatUser = '<a href="./lottery-user-profile?username='+val.name+'" target="_blank">' + val.name + '</a>';
				var profit = val.profit.toFixed(3);

				var operations;
				operations = '<a data-command="showLotteryDetails" data-val="'+val.name+'" data-sTime="' + val.sTime + '" data-eTime="' + val.eTime + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详细 </a>';
				
				innerHtml +=
					'<tr class="align-center">'+
					'<td>' + formatUser + '</td>'+
					'<td>' + val.sTime + '</td>'+
					'<td>' + profit + '</td>'+
					'<td>' + operations + '</td>'+
					'</tr>';
			});
			thisTable.html(innerHtml);
			thisTable.find('[data-command="showLotteryDetails"]').unbind('click').click(function() {
				var username = $(this).attr('data-val');
				var sTime = $(this).attr('data-sTime');
				var eTime = $(this).attr('data-eTime');
				var loadParams = {username: username, sTime: sTime, eTime: eTime, self: true};
				doLoadLotteryDetails(loadParams, function(data, params) {
					doShowLotteryDetails(data, params);
				});
			});
			dailyDetailsModal.modal('show');
		}

		var doLoadLotteryDetails = function(params, callback) {
			var url = './lottery-report/complex-details';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
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
				doLoadLotteryDetails(params, function(data) {
					doShowMethodDetails(data);
				});
			});
			lotteryDetailsModal.modal('show');
		}

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

		var init = function() {
			var todayParams = getTodayParams();
			doLoadDataByParams(todayParams, todayTableReport, LOAD_TYPE_TODAY);

			// 实时刷新今天的数据
			var countDownSecs = 120;
			var currentCount = countDownSecs;
			setInterval(function(){
				if (currentCount <= 0) {
					todayTableReportCountDown.html("<i class='fa fa-spinner fa-spin'></div>");
					var todayParams = getTodayParams();
					doLoadDataByParams(todayParams, todayTableReport, LOAD_TYPE_TODAY);
					currentCount = countDownSecs;
				}
				else {
					todayTableReportCountDown.html(currentCount + "秒后刷新");
					currentCount--;
				}
			}, 1000);

			var lastWeekParams = getLastWeekParams();
			doLoadDataByParams(lastWeekParams, lastWeekTableReport, LOAD_TYPE_LAST_WEEK);

			var last2weeksParams = getLast2weeksParams();
			doLoadDataByParams(last2weeksParams, last2weeksTableReport, LOAD_TYPE_LAST2_WEEKS);
		}
		
		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			ReportTable.init();
		}
	}
}();