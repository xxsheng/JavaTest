var RechargeWithdrawComplex = function() {
	
	var sDate, eDate;
	var handelDatePicker = function() {
		var drpicker = $('#dashboard-report-range');
		
		var today = moment(),
			yesterday = moment().subtract('days', 1),
			last7Days = moment().subtract('days', 7),
			thisWeekStart = moment().startOf('week').add('days', 1),
			thisWeekEnd = moment().endOf('week').add('days', 1),
			lastWeekStart = moment().subtract('week', 1).startOf('week').add('days', 1),
			lastWeekEnd = moment().subtract('week', 1).endOf('week').add('days', 1),
			thisMonthStart = moment().startOf('month'),
			thisMonthEnd = moment().endOf('month'),
			lastMonthStart = moment().subtract('month', 1).startOf('month'),
			lastMonthEnd = moment().subtract('month', 1).endOf('month');
		drpicker.daterangepicker({
			opens: 'right',
			startDate: today,
			endDate: today,
			minDate: '2012-01-01',
			maxDate: today,
			//dateLimit: {days: 60},
			showDropdowns: true,
			showWeekNumbers: true,
			timePicker: false,
			timePickerIncrement: 1,
			timePicker12Hour: true,
			ranges : {
				'今天': [ today, today ],
				'昨天': [ yesterday, yesterday ],
				'最近7天': [ last7Days, yesterday ],
				//'本周': [ thisWeekStart, thisWeekEnd ],
				'上周': [ lastWeekStart, lastWeekEnd ],
				'本月': [ thisMonthStart, thisMonthEnd ],
				'上个月': [ lastMonthStart, lastMonthEnd ]
			},
			buttonClasses : [ 'btn btn-sm' ],
			applyClass : ' blue',
			cancelClass : 'default',
			format : 'YYYY-MM-DD',
			separator : ' to ',
			locale : {
				applyLabel : '确定',
				cancelLabel : '取消',
				weekLabel : '周',
				fromLabel : '开始日期',
				toLabel : '结束日期',
				customRangeLabel : '自定义日期',
				daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
				monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
				firstDay : 1
			}
		},
		function(start, end) {
			setDate(start, end);
		});
		
		var setDate = function(start, end) {
			sDate = start.format('YYYY-MM-DD');
			eDate = end.format('YYYY-MM-DD');
			drpicker.find('span').html(sDate + ' 至 ' + eDate);
			if (sDate == eDate) {
				drpicker.find('span').html(sDate);
			} else {
				drpicker.find('span').html(sDate + ' 至 ' + eDate);
			}
			RechargeWithdrawComplex.initData();
		}
		
		// setDate(last7Days, yesterday);
		setDate(today, today);
		drpicker.show();
	}

	var initChartConfig = function() {
		require.config({
			paths: {
				echarts: './theme/assets/custom/plugins/echarts/build/dist/',
				macarons: 'theme/assets/custom/plugins/echarts/theme/macarons'
			}
        });
	}

	var initData = function() {
		var url = './recharge-withdraw-complex';
		var params = {sDate: sDate, eDate: eDate};
		$.ajax({
			type : 'post',
			url : url,
			data : params,
			dataType : 'json',
			success : function(result) {
				if (result.error === 0) {
					var charsData = result.data.charts;
					RechargeWithdrawTotalInfo.show(result.data.total);
					ReceiveFeeMoneyCharts.show(charsData[0]);
					RechargeWithdrawDiffCharts.show(charsData[1]);
					RechargeWithdrawCharts.show(charsData[2], charsData[4]);
					RechargeWithdrawCountCharts.show(charsData[5], charsData[6]);
				}
				else if(result.error == 1 || result.error == 2) {
					toastr['error']('操作失败！' + data.message, '操作提示');
				}
			}
		});
	}

	// 统计信息
	var RechargeWithdrawTotalInfo = function() {
		var thisTable = $('#recharge-withdraw-total-info');

		var buildData = function(data) {
			thisTable.find('[data-field="totalReceiveFeeMoney"]').html('¥ ' + data.totalReceiveFeeMoney);
			thisTable.find('[data-field="totalRechargeMoney"]').html('¥ ' + data.totalRechargeMoney);
			thisTable.find('[data-field="totalWithdrawMoney"]').html('¥ ' + data.totalWithdrawMoney);
			thisTable.find('[data-field="totalActualReceiveMoney"]').html('¥ ' + data.totalActualReceiveMoney);
			thisTable.find('[data-field="totalRechargeWithdrawDiff"]').html('¥ ' + data.totalRechargeWithdrawDiff);
			thisTable.find('[data-field="totalRechargeCount"]').html(data.totalRechargeCount);
			thisTable.find('[data-field="totalWithdrawCount"]').html(data.totalWithdrawCount);
		}

		return {
			show: function(data) {
				buildData(data);
			}
		}
	}();

	// 充值第三方手续费
	var ReceiveFeeMoneyCharts = function() {
		var thisTable = $('#chart-receive-fee-money');
		var docMain = thisTable.find('.chart')[0];

		var show = function(data) {
			initChart(data.xAxis, data.yAxis[0]);
		}

		var initChart = function(date, data) {
			require(['echarts', 'echarts/chart/line'], function(echarts) {
				require(['macarons'], function(theme){
		            var thisChart = echarts.init(docMain, theme);
					var option = {
						tooltip: {
							trigger: 'axis'
						},
						toolbox: {
							show : false
						},
						calculable: false,
						grid: { x: 50, y: 10, x2: 50, y2: 30 },
						xAxis: [{
							type : 'category',
							boundaryGap : false,
							data : date
						}],
						yAxis: [{
							type: 'value',
							boundaryGap: [0, 0.1]
						}],
						series: [{
							name: '充值手续费',
							type: 'line',
							data: data,
							markLine: {
								data: [{
									type: 'average',
									name: '平均值'
								}]
							}
						}]
					}
					thisChart.setOption(option);
		        });
			});
		}

		return {
			show: show
		}
	}();

	// 充提差(扣充值手续费)
	var RechargeWithdrawDiffCharts = function() {
		var thisTable = $('#chart-recharge-withdraw-diff');
		var docMain = thisTable.find('.chart')[0];

		var show = function(data) {
			initChart(data.xAxis, data.yAxis[0]);
		}

		var initChart = function(date, data) {
			require(['echarts', 'echarts/chart/line'], function(echarts) {
				require(['macarons'], function(theme){
		            var thisChart = echarts.init(docMain, theme);
					var option = {
						tooltip: {
							trigger: 'axis'
						},
						toolbox: {
							show : false
						},
						calculable: false,
						grid: { x: 50, y: 10, x2: 50, y2: 30 },
						xAxis: [{
							type : 'category',
							boundaryGap : false,
							data : date
						}],
						yAxis: [{
							type: 'value',
							boundaryGap: [0, 0.1]
						}],
						series: [{
							name: '充提差',
							type: 'line',
							data: data,
							markLine: {
								data: [{
									type: 'average',
									name: '平均值'
								}]
							}
						}]
					}
					thisChart.setOption(option);
		        });
			});
		}

		return {
			show: show
		}
	}();

	// 实收实提
	var RechargeWithdrawCharts = function() {
		var thisTable = $('#chart-recharge-withdraw');
		var docMain = thisTable.find('.chart')[0];

		var show = function(recharge, withdraw) {
			initChart(recharge.xAxis, recharge.yAxis[0], withdraw.yAxis[0]);
		}

		var initChart = function(date, recharge, withdraw) {
			require(['echarts', 'echarts/chart/line'], function(echarts) {
				require(['macarons'], function(theme){
		            var thisChart = echarts.init(docMain, theme);
					var option = {
						tooltip: {
							trigger: 'axis'
						},
						legend: {
							data:['实际收款','实际提现']
						},
						toolbox: {
							show : false
						},
						calculable: false,
						grid: { x: 80, y: 40, x2: 80, y2: 30 },
						xAxis: [{
							type : 'category',
							boundaryGap : false,
							data : date
						}],
						yAxis: [{
							type: 'value',
							boundaryGap: [0, 0.1]
						}],
						series: [{
							name: '实际收款',
							type: 'line',
							data: recharge,
							markLine: {
								data: [{
									type: 'average',
									name: '平均值'
								}]
							}
						}, {
							name: '实际提现',
							type: 'line',
							data: withdraw,
							markLine: {
								data: [{
									type: 'average',
									name: '平均值'
								}]
							}
						}]
					}
					thisChart.setOption(option);
		        });
			});
		}

		return {
			show: show
		}
	}();

	// 充提订单量
	var RechargeWithdrawCountCharts = function() {
		var thisTable = $('#chart-recharge-withdraw-count');
		var docMain = thisTable.find('.chart')[0];

		var show = function(recharge, withdraw) {
			initChart(recharge.xAxis, recharge.yAxis[0], withdraw.yAxis[0]);
		}

		var initChart = function(date, recharge, withdraw) {
			require(['echarts', 'echarts/chart/line'], function(echarts) {
				require(['macarons'], function(theme){
		            var thisChart = echarts.init(docMain, theme);
					var option = {
						tooltip: {
							trigger: 'axis'
						},
						legend: {
							data:['充值订单量','提现订单量']
						},
						toolbox: {
							show : false
						},
						calculable: false,
						grid: { x: 80, y: 40, x2: 80, y2: 30 },
						xAxis: [{
							type : 'category',
							boundaryGap : false,
							data : date
						}],
						yAxis: [{
							type: 'value',
							boundaryGap: [0, 0.1]
						}],
						series: [{
							name: '充值订单量',
							type: 'line',
							data: recharge,
							markLine: {
								data: [{
									type: 'average',
									name: '平均值'
								}]
							}
						}, {
							name: '提现订单量',
							type: 'line',
							data: withdraw,
							markLine: {
								data: [{
									type: 'average',
									name: '平均值'
								}]
							}
						}]
					}
					thisChart.setOption(option);
		        });
			});
		}

		return {
			show: show
		}
	}();

	var init = function() {
		initChartConfig();
		handelDatePicker();
		Metronic.addResizeHandler(function() {
			// RechargeWithdrawComplex.initData();
		});
	}

	return {
		init : init,
		initData: initData
	}

}();