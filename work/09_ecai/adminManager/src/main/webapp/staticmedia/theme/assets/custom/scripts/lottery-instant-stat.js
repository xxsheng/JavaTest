var LotteryInstantStat = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var LotteryInstantStatPanel = function() {
		var thisPanel = $('#lottery-instant-stat');
		
		var getParams = function() {
			var sTime = thisPanel.find('input[name="sDate"]').val() + ' ' + thisPanel.find('input[name="sTime"]').val();
			var eTime = thisPanel.find('input[name="eDate"]').val() + ' ' + thisPanel.find('input[name="eTime"]').val();
			return {sTime: sTime, eTime: eTime};
		}
		
		var isLoading = false;
		var doSearch = function() {
			if(isLoading) return;
			var url = './lottery-instant-stat/list';
			var params = getParams();
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						buildStatData(data);
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
		}
		
		var buildStatData = function(data) {
			var tableStat = thisPanel.find('table[data-table="stat"]');
			tableStat.find('[data-field="icbcMoney"]').html(data.stat.icbcMoney.toFixed(3));
			tableStat.find('[data-field="ccbMoney"]').html(data.stat.ccbMoney.toFixed(3));
			tableStat.find('[data-field="cmbMoney"]').html(data.stat.cmbMoney.toFixed(3));
			tableStat.find('[data-field="cmbcMoney"]').html(data.stat.cmbcMoney.toFixed(3));
			tableStat.find('[data-field="thirdMoney"]').html(data.stat.thirdMoney.toFixed(3));
			tableStat.find('[data-field="notarrivalMoney"]').html(data.stat.notarrivalMoney.toFixed(3));
			
			tableStat.find('[data-field="withdrawalsMoney"]').html(data.stat.withdrawalsMoney.toFixed(3));
			tableStat.find('[data-field="thrildRemitMoney"]').html(data.stat.thrildRemitMoney.toFixed(3));
			tableStat.find('[data-field="bankRemitMoney"]').html((data.stat.withdrawalsMoney - data.stat.thrildRemitMoney).toFixed(3));
			
			tableStat.find('[data-field="transUserIn"]').html(data.stat.transUserIn.toFixed(3));
			tableStat.find('[data-field="transUserOut"]').html(data.stat.transUserOut.toFixed(3));
			
			// Total
			var rechargeTotalMoney = data.stat.icbcMoney + data.stat.ccbMoney + data.stat.cmbMoney + data.stat.cmbcMoney + data.stat.thirdMoney + data.stat.notarrivalMoney;
			tableStat.find('[data-field="rechargeTotalMoney"]').html(rechargeTotalMoney.toFixed(3));
			var rwProfitMoney = rechargeTotalMoney - data.stat.withdrawalsMoney;
			tableStat.find('[data-field="rwProfitMoney"]').html(rwProfitMoney.toFixed(3));
			
			tableStat.find('[data-field="feeFillMoney"]').html(data.stat.feeFillMoney.toFixed(3));
			tableStat.find('[data-field="feeDeductMoney"]').html(data.stat.feeDeductMoney.toFixed(3));
			// Total
			var feeTotalMoney = data.stat.feeFillMoney - data.stat.feeDeductMoney;
			tableStat.find('[data-field="feeTotalMoney"]').html(feeTotalMoney.toFixed(3));
			
			tableStat.find('[data-field="adminAddMoney"]').html(data.stat.adminAddMoney.toFixed(3));
			tableStat.find('[data-field="adminMinusMoney"]').html(data.stat.adminMinusMoney.toFixed(3));
			// Total
			var adminTotalMoney = data.stat.adminAddMoney - data.stat.adminMinusMoney;
			tableStat.find('[data-field="adminTotalMoney"]').html(adminTotalMoney.toFixed(3));
			
			tableStat.find('[data-field="dividendMoney"]').html(data.stat.dividendMoney.toFixed(3));
			// Total
			var dividendTotal = data.stat.dividendMoney;
			tableStat.find('[data-field="dividendTotal"]').html(dividendTotal.toFixed(3));
			
			tableStat.find('[data-field="activityOtherMoney"]').html(data.stat.activityOtherMoney.toFixed(3));
			tableStat.find('[data-field="activityRewardXFMoney"]').html(data.stat.activityRewardXFMoney.toFixed(3));
			tableStat.find('[data-field="activityRewardYKMoney"]').html(data.stat.activityRewardYKMoney.toFixed(3));
			tableStat.find('[data-field="activitySalaryZSMoney"]').html(data.stat.activitySalaryZSMoney.toFixed(3));
			tableStat.find('[data-field="activitySalaryZDMoney"]').html(data.stat.activitySalaryZDMoney.toFixed(3));
			tableStat.find('[data-field="activityBindMoney"]').html(data.stat.activityBindMoney.toFixed(3));
			tableStat.find('[data-field="activityRechargeMoney"]').html(data.stat.activityRechargeMoney.toFixed(3));
			tableStat.find('[data-field="activityRechargeLoopMoney"]').html(data.stat.activityRechargeLoopMoney.toFixed(3));
			tableStat.find('[data-field="activityJiFenMoney"]').html(data.stat.activityJiFenMoney.toFixed(3));
			tableStat.find('[data-field="activityGrabMoney"]').html(data.stat.activityGrabMoney.toFixed(3));
			// Total
			var activityTotalMoney = data.stat.activityOtherMoney + data.stat.activityRewardXFMoney + data.stat.activityRewardYKMoney + data.stat.activitySalaryZSMoney + data.stat.activitySalaryZDMoney + data.stat.activityBindMoney + data.stat.activityRechargeMoney + data.stat.activityRechargeLoopMoney + data.stat.activityGrabMoney;
			tableStat.find('[data-field="activityTotalMoney"]').html(activityTotalMoney.toFixed(3));
			
			// Balance
			tableStat.find('[data-field="totalBalance"]').html(data.stat.totalBalance.toFixed(3));
			tableStat.find('[data-field="lotteryBalance"]').html(data.stat.lotteryBalance.toFixed(3));
			// tableStat.find('[data-field="baccaratBalance"]').html(data.stat.baccaratBalance.toFixed(3));
			
			// allBalance
			//var totalProfitMoney = rwProfitMoney - feeTotalMoney - adminTotalMoney - dividendTotal - activityTotalMoney;
			var allBalance = data.stat.totalBalance + data.stat.lotteryBalance;
			tableStat.find('[data-field="allBalance"]').html(allBalance.toFixed(3));
		}
		
		thisPanel.find('[data-command="search"]').unbind('click').click(function() {
			doSearch();
		});
		
		var init = function() {
			var thisDate = moment(ServerTime).format('YYYY-MM-DD');
			var thisTime = moment(ServerTime).format('HH:mm:ss');
			thisPanel.find('input[name="sDate"]').val(thisDate);
			thisPanel.find('input[name="sTime"]').val('00:00:00');
			thisPanel.find('input[name="eDate"]').val(thisDate);
			thisPanel.find('input[name="eTime"]').val(thisTime);
			doSearch();
		}
		
		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			LotteryInstantStatPanel.init();
			handelDatePicker();
		}
	}
}();