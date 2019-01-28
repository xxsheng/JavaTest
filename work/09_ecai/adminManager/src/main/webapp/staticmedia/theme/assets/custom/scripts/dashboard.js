var Dashboard = function() {
	
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
			Dashboard.load(sDate, eDate);
		}
		
		setDate(today, today);
		drpicker.show();
	}
	
	var LotteryTotalInfo = function() {
		var thisTable = $('#lottery-total-info');
		
		var loadData = function() {
			var url = './dashboard/total-info';
			var params = {sDate: sDate, eDate: eDate};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					buildData(data);
				}
			});
		}
		
		var buildData = function(data) {
			thisTable.find('[data-field="totalUserRegist"]').html(data.totalUserRegist);
			thisTable.find('[data-field="totalBetsMoney"]').html('¥ ' + data.totalBetsMoney);
			thisTable.find('[data-field="totalOrderCount"]').html(data.totalOrderCount);
			thisTable.find('[data-field="totalProfitMoney"]').html('¥' + data.totalProfitMoney);
		}
		
		return {
			init: function() {
				loadData();
			}
		}
	}();
	
	var initChartConfig = function() {
		require.config({
			paths: {
				echarts: './theme/assets/custom/plugins/echarts/build/dist/',
				macarons: 'theme/assets/custom/plugins/echarts/theme/macarons'
			}
        });
	}
	
	var UserRegistCharts = function() {
		var thisTable = $('#chart-user-regist');
		var docMain = thisTable.find('.chart')[0];
		
		var loadData = function() {
			var url = './dashboard/chart-user-regist';
			var params = {sDate: sDate, eDate: eDate};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					initChart(data.xAxis, data.yAxis[0]);
				}
			});
			/*var date = ['11月1日','11月2日','11月3日','11月4日','11月5日','11月6日','11月7日'];
			var data = [0, 0, 1, 1, 2, 3, 4, 10];
			initChart(date, data);*/
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
							name: '注册',
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
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var UserLoginCharts = function() {
		var thisTable = $('#chart-user-login');
		var docMain = thisTable.find('.chart')[0];
		
		var loadData = function() {
			var url = './dashboard/chart-user-login';
			var params = {sDate: sDate, eDate: eDate};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					initChart(data.xAxis, data.yAxis[0]);
				}
			});
			/*var date = ['11月1日','11月2日','11月3日','11月4日','11月5日','11月6日','11月7日'];
			var data = [300, 866, 788, 999, 562, 236, 785];
			initChart(date, data);*/
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
							name: '登录',
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
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var UserComplexCharts = function() {
		var thisTable = $('#chart-user-complex');
		var docMain = thisTable.find('.chart')[0];
		var lotteryType = thisTable.find('select[name="lotteryType"]');
		var lottery = thisTable.find('select[name="lottery"]');
		
		var loadData = function() {
			var url = './dashboard/chart-user-complex';
			var type = lotteryType.val();
			var id = lottery.val();
			var params = {type: type, id: id, sDate: sDate, eDate: eDate};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					initChart(data.xAxis, data.yAxis[0], data.yAxis[1]);
				}
			});
			/*var date = ['11月1日','11月2日','11月3日','11月4日','11月5日','11月6日','11月7日'];
			var cost = [152362, 256200, 365852, 120156, 652215, 562365, 985621];
			var prize = [352360, 456215, 265156, 562336, 352662, 145256, 563255];
			initChart(date, cost, prize);*/
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
					        data:['消费', '中奖']
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
							name: '消费',
							type: 'line',
							data: recharge,
							markLine: {
								data: [{
									type: 'average', 
									name: '平均值'
								}]
							}
						}, {
							name: '中奖',
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
		
		lotteryType.change(function() {
			init();
			var type = lotteryType.val();
			Lottery.init(type, lottery);
		});
		
		lottery.change(function() {
			init();
		});
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var LotteryHotCharts = function() {
		var thisTable = $('#chart-lottery-hot');
		var docMain = thisTable.find('.chart')[0];
		var lotteryType = thisTable.find('select[name="lotteryType"]');
		
		var loadData = function() {
			var url = './dashboard/chart-lottery-hot';
			var type = lotteryType.val();
			var params = {type: type, sDate: sDate, eDate: eDate};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					initChart(data.legend, data.series);
				}
			});
			/*var types = ['重庆时时彩', '江西时时彩', '新疆时时彩', '天津时时彩', '江苏快3'];
			var data = [{value:1548, name:'重庆时时彩'}, {value:310, name:'江西时时彩'}, {value:234, name:'新疆时时彩'}, {value:135, name:'天津时时彩'},  {value:335, name:'江苏快3'}]
			initChart(types, data);*/
		}
		
		var initChart = function(types, data) {
			require(['echarts', 'echarts/chart/pie'], function(echarts) {
				require(['macarons'], function(theme){
		            var thisChart = echarts.init(docMain, theme);
					var option = {
						tooltip: {
							trigger: 'item',
					        formatter: '{a} <br/>{b} : {c} ({d}%)'
						},
						legend: {
							orient: 'vertical',
					        x: 'left',
					        data: types
					    },
						toolbox: {
							show: false
						},
						calculable: false,
						series: [{
				            name:'游戏热度',
				            type:'pie',
				            radius : '66%',
				            center: ['60%', '50%'],
				            data: data
				        }]
					}
					thisChart.setOption(option);
		        });
			});
		}
		
		lotteryType.change(function() {
			init();
		});
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var UserBetsCharts = function() {
		var thisTable = $('#chart-user-bets');
		var docMain = thisTable.find('.chart')[0];
		var lotteryType = thisTable.find('select[name="lotteryType"]');
		var lottery = thisTable.find('select[name="lottery"]');
		
		var loadData = function() {
			var url = './dashboard/chart-user-bets';
			var type = lotteryType.val();
			var id = lottery.val();
			var params = {type: type, id: id, sDate: sDate, eDate: eDate};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					initChart(data.xAxis, data.yAxis[0]);
				}
			});
			//var date = ['11月1日','11月2日','11月3日','11月4日','11月5日','11月6日','11月7日'];
			//var data = [3005, 2562, 5626, 4562, 8965, 2655, 5562];
			//initChart(date, data);
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
							name: '订单数',
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
		
		lotteryType.change(function() {
			init();
			var type = lotteryType.val();
			Lottery.init(type, lottery);
		});
		
		lottery.change(function() {
			init();
		});
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var LotteryType = function() {
		
		var loadData = function(render) {
			var url = './lottery-type/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					buildData(data, render);
				}
			});
		}
		
		var buildData = function(data, render) {
			if(!render) {
				render = $('select[name="lotteryType"]');
			}
			render.empty();
			render.append('<option value="">全部类型</option>');
			$.each(data, function(idx, val) {
				render.append('<option value="' + val.id + '">' + val.name + '</option>');
			});
		}
		
		var init = function(render) {
			loadData(render);
		}
		
		return {
			init: init
		}
	}();
	
	var Lottery = function() {
		
		var loadData = function(type, render) {
			var url = './lottery/list';
			var params = {type: type};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					buildData(data, render);
				}
			});
		}
		
		var buildData = function(data, render) {
			if(!render) {
				render = $('select[name="lottery"]');
			}
			render.empty();
			render.append('<option value="">全部彩票</option>');
			$.each(data, function(idx, val) {
				render.append('<option value="' + val.bean.id + '">' + val.bean.showName + '</option>');
			});
		}
		
		var init = function(type, render) {
			loadData(type, render);
		}
		
		return {
			init: init
		}
	}();
	
	var init = function() {
		initChartConfig();
		handelDatePicker();
		LotteryType.init();
		Lottery.init();
		Metronic.addResizeHandler(function() {
			Dashboard.load();
		});
	}

	return {
		init : init,
		load : function() {
			LotteryTotalInfo.init();
			UserRegistCharts.init();
			UserLoginCharts.init();
			UserComplexCharts.init();
			LotteryHotCharts.init();
			UserBetsCharts.init();
		}
	}

}();