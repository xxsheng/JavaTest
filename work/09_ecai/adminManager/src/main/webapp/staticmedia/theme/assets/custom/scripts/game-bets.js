var GameBets = function() {
	
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
	
	var GameBetsTable = function() {
		var tableList = $('#table-game-bets-list');
		var tablePagelist = tableList.find('.page-list');
		var $page = $(".page-content");
		
		var getSearchParams = function() {
			var keyword = tableList.find('input[name="keyword"]').val();
			var username = tableList.find('input[name="username"]').val();
			var platformId = tableList.find('select[name="platform"]').val();
			var minTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var maxTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			maxTime = getNetDate(maxTime);
			var minBetsMoney = tableList.find('input[name="minBetsMoney"]').val();
			var maxBetsMoney = tableList.find('input[name="maxBetsMoney"]').val();
			var minPrizeMoney = tableList.find('input[name="minPrizeMoney"]').val();
			var maxPrizeMoney = tableList.find('input[name="maxPrizeMoney"]').val();
			var gameCode = tableList.find('input[name="gameCode"]').val();
			var gameType = tableList.find('input[name="gameType"]').val();
			var gameName = tableList.find('input[name="gameName"]').val();
			return {keyword: keyword, username: username, platformId: platformId, minTime: minTime, maxTime: maxTime, minBetsMoney: minBetsMoney,
				maxBetsMoney: maxBetsMoney, minPrizeMoney: minPrizeMoney, maxPrizeMoney: maxPrizeMoney, gameCode: gameCode,
				gameType: gameType, gameName: gameName};
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './game-bets/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
			},
			complete: function() {
			},
			success: function(list, rsp) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {

					var money = val.bean.money + val.bean.progressiveMoney;
					money = money.toFixed(1);
					var prizeMoney = val.bean.prizeMoney + val.bean.progressivePrize;
					prizeMoney = prizeMoney.toFixed(1);

					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + val.bean.id + '</td>'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
						'<td>' + val.platform + '</td>'+
						'<td>' + val.bean.betsId + '</td>'+
						'<td>' + val.bean.gameCode + '</td>'+
						'<td>' + val.bean.gameType + '</td>'+
						'<td>' + val.bean.gameName + '</td>'+
						'<td>' + money + '</td>'+
						'<td>' + prizeMoney + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + val.bean.prizeTime + '</td>'+
						'<td>' + DataFormat.formatGameStatus(val.bean.status) + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
				$("#totalMoney").html(rsp.totalMoney.toFixed(4));
				$("#totalPrizeMoney").html(rsp.totalPrizeMoney.toFixed(4));
			},
			pageError: function(response) {
				$("#totalMoney").html('0.0000');
				$("#totalPrizeMoney").html('0.0000');
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
				$("#totalMoney").html('0.0000');
				$("#totalPrizeMoney").html('0.0000');
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});
		
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
			tableList.find('input[name="gameCode"]').val('');
			tableList.find('input[name="gameType"]').val('');
			tableList.find('input[name="gameName"]').val('');
		}
		
		isAdvanced();
		
		var init = function() {
			pagination.init();
		}
		
		var reload = function() {
			pagination.reload();
		}
		
		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			var today = moment().format('YYYY-MM-DD');
			var tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');
			var tableList = $('#table-game-bets-list');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);

			GameBetsTable.init();
			handelDatePicker();
		}
	}
}();