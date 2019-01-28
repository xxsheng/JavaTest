var LotteryChase = function() {
	
	var timeList = [];
	var loadExpect = function(count, fn) {
		var url = '/LotteryChaseTime';
		var data = {lotteryId: Lottery.id, count: count};
		$.ajax({
			url: url,
			data: data,
			success: function(response) {
				if(response.data != null && response.data.length > 0){
					timeList = response.data;
					if($.isFunction(fn)) fn();
				} else {
					$.YF.alert_warning('该彩种暂时不支持追号！');
				}
			}
		});
	}
	
	var bUnitMoney = Config.bUnitMoney;
	
	/**
	 * 计算投注列表信息
	 */
	var BListInfo = function() {
		var bList = function() {
			return LotteryMain.bList();
		}
		var money = function(multiple) {
			var amount = 0;
			var list = bList();
			for (var i = 0; i < list.length; i++) {
				var val = list[i];
				amount += multiple * val.num * bUnitMoney * PrizeUtils.model(val.model).money;
			}
			return amount;
		}
		var cList = function($dom) {
			var list = [];
			var thisTable = $dom.find('.tab-list').find('table > tbody');
			thisTable.find('tr').each(function() {
				var thisRow = $(this);
				var checkbox = thisRow.find('.chek');
				var expect = thisRow.find('td:eq(1)').html();
				var multiple = Number(thisRow.find('input[type="text"]').val());
				if(checkbox.hasClass('cheked')) {
					list.push({expect: expect, multiple: multiple});
				}
			});
			return list;
		}
		return {
			bList: bList,
			cList: cList,
			money: money
		}
	}();
	
	/**
	 * 奖金工具
	 */
	var PrizeUtils = function() {
		var model = function(val) {
			switch (val) {
	            case 'yuan':
	                return { val: val, money: 1 }
	            case 'jiao':
	                return { val: val, money: 0.1 }
	            case 'fen':
	                return { val: val, money: 0.01 }
	            case 'li':
	                return { val: val, money: 0.001 }
	            case '1yuan':
	                return { val: val, money: 0.5 }
	            case '1jiao':
	                return { val: val, money: 0.05 }
	            case '1fen':
	                return { val: val, money: 0.005 }
	            case '1li':
	                return { val: val, money: 0.0005 }
	        }
		}
		var money = function(type, m, code) {
			var rule = PlayRules[type];
			var mMoney = model(m).money;
			var prize = [];
			if(rule) {
				var ps = rule.prize.split(',');
				for (var i = 0, j = ps.length; i < j; i++) {
                    if (rule.fixed == false) {
						var pm = (code / Number(ps[i])) * (bUnitMoney / 2) * mMoney;
						prize[i] = pm.toFixed(3);
					}
                    if (rule.fixed == true) {
						var pm = Number(ps[i]) * (bUnitMoney / 2) * mMoney;
						prize[i] = pm.toFixed(3);
					}
				}
			}
			return prize;
		}
		return {
			model: model,
			money: money
		}
	}();
	
	/**
	 * 投注选项
	 */
	var Options = function() {
		var update = function($dom) {
			var chaseText = $dom.find('.chase-text');
			var thisTable = $dom.find('.tab-list').find('table > tbody');
			var totalExpect = 0, totalMoney = 0;
			thisTable.find('tr').each(function() {
				var thisRow = $(this);
				var checkbox = thisRow.find('.chek');
				var thisMoney = Number(thisRow.find('td:eq(3)').html()); 
				if(checkbox.hasClass('cheked')) {
					totalExpect++;
					totalMoney += thisMoney;
				}
			});
			chaseText.find('[data-property="total-expect"]').html(totalExpect);
			chaseText.find('[data-property="total-money"]').html(totalMoney.toFixed(3));
		}
		var isStop = function($dom) {
			var chaseText = $dom.find('.chase-text');
			var isStop = chaseText.find('[data-property="isStop"]');
			return isStop.hasClass('cheked') ? true : false;
		}
		return {update: update, isStop: isStop};
	}();
	
	/**
	 * 计算利润率
	 * count 追号期数
	 * sMultiple 开始倍数
	 * maxMultiple 最大倍投
	 * minProfit 最低利润率（百分比）
	 * money 单倍金额
	 * prize 单倍奖金
	 */
	var calculation = function(count, sMultiple, maxMultiple, minProfit, money, prize) {
		var result = []; // 结果
		var totalMoney = 0;
		var multiple = sMultiple;
		for (var i = 0; i < count; i++) {
			var thisMoney = 0;
			var thisPrize = 0;
			var thisProfit = 0;
			while (true) {
				thisMoney = money * multiple;
				thisPrize = prize * multiple;
				var tempTotal = totalMoney + thisMoney;
				thisProfit = (thisPrize - tempTotal) / tempTotal;
				if(thisProfit >= minProfit) break;
				if(multiple > maxMultiple) return result;
				multiple++;
			}
			totalMoney += thisMoney; // 累计投入
			//alert(multiple + '-' + thisMoney + '-' + totalMoney + '-' + thisPrize);
			result.push({multiple: multiple, thisMoney: thisMoney, thisPrize: thisPrize, thisProfit: thisProfit});
		}
		return result;
	}
	
	var doGenerate = function($dom) {
		var tabs = $dom.find('.chase-tab');
		var chaseText = $dom.find('.chase-text');
		var thisTable = $dom.find('.tab-list').find('table > tbody');
		var total = Number(chaseText.find('input[name="expect"]').val());
		var index = tabs.find('.cur').attr('data-type');
		if(index == 0) {
			// 判断多订单
			var betList = BListInfo.bList();
			if(betList.length > 1) {
				// $.YF.alert_warning('多个订单不支持利润率追号！');
				swal.showValidationError('多个订单不支持利润率追号！')
				return
			}
			var data = betList[0];
			// 计算单倍奖金
			var prize = PrizeUtils.money(data.ruleCode, data.model, data.code);
			if(prize.length > 1) {
				// $.YF.alert_warning('该玩法不支持利润率追号！');
				swal.showValidationError('该玩法不支持利润率追号！')
				return
			}
			// 计算单倍投注金额
			var money = data.num * bUnitMoney * PrizeUtils.model(data.model).money;
			// 获取选项
			var sMultiple = Number(chaseText.find('input[name="s0Multiple"]').val());
			var maxMultiple = Number(chaseText.find('input[name="maxMultiple"]').val());
			var minProfit = Number(chaseText.find('input[name="minProfit"]').val());
			minProfit = minProfit / 100; // 变成百分比
			var result = calculation(total, sMultiple, maxMultiple, minProfit, money, prize);
			thisTable.empty();
			if(result.length > 0) {
				for (var i = 0; i < result.length; i++) {
					if(i > timeList.length - 1) break;
					var val = timeList[i];
					var multiple = result[i].multiple;
					var innerHtml =
						'<tr>'+
						'<td><em class="chek cheked"></em></td>'+
						'<td>' + val.expect + '</td>'+
						'<td><input type="text" class="text" value="' + multiple + '" maxlength="5"> 倍</td>'+
						'<td>' + BListInfo.money(multiple).toFixed(3) + '</td>'+
						'<td>' + val.stopTime + '</td>'+
						'</tr>';
					thisTable.append(innerHtml);
				}
			} else {
				var innerHtml = '<tr><td colspan="5">没有符合要求的方案，请调整参数重新计算！</td></tr>';
				thisTable.append(innerHtml);
			}
			Options.update($dom);
		}
		if(index == 1) {
			var sMultiple = Number(chaseText.find('input[name="s1Multiple"]').val());
			thisTable.empty();
			for (var i = 0; i < total; i++) {
				if(i > timeList.length - 1) break;
				var val = timeList[i];
				var innerHtml =
				'<tr>'+
					'<td><em class="chek cheked"></em></td>'+
					'<td>' + val.expect + '</td>'+
					'<td><input type="text" class="text" value="' + sMultiple + '" maxlength="5"> 倍</td>'+
					'<td>' + BListInfo.money(sMultiple).toFixed(3) + '</td>'+
					'<td>' + val.stopTime + '</td>'+
				'</tr>';
				thisTable.append(innerHtml);
			}
			Options.update($dom);
		}
		if(index == 2) {
			var sMultiple = Number(chaseText.find('input[name="s2Multiple"]').val());
			var operation = chaseText.find('select[name="operation"]').val();
			var expTimes = Number(chaseText.find('input[name="expTimes"]').val());
			var expMultiple = Number(chaseText.find('input[name="expMultiple"]').val());
			thisTable.empty();
			for (var i = 0; i < total; i++) {
				if(i > timeList.length - 1) break;
				var val = timeList[i];
				var multiple = 1;
				if('x' == operation) {
					multiple = i < expTimes ? sMultiple : sMultiple * Math.pow(expMultiple, Math.floor(i / expTimes));
				}
				if('+' == operation) {
					multiple = i < expTimes ? sMultiple : sMultiple + expMultiple * Math.floor(i / expTimes);
				}
				if(multiple > 10000) return;
				var innerHtml =
				'<tr>'+
					'<td><em class="chek cheked"></em></td>'+
					'<td>' + val.expect + '</td>'+
					'<td><input type="text" class="text" value="' + multiple + '" maxlength="5"> 倍</td>'+
					'<td>' + BListInfo.money(multiple).toFixed(3) + '</td>'+
					'<td>' + val.stopTime + '</td>'+
				'</tr>';
				thisTable.append(innerHtml);
			}
			Options.update($dom);
		}
		initTableEvent($dom);
	}
	
	var initNormal = function($dom) {
		var items = []
		var chaseText = $dom.find('.chase-text');
		var thisTable = $dom.find('.tab-list').find('table > tbody');
		var total = Number(chaseText.find('input[name="expect"]').val());
		thisTable.empty();
		for (var i = 0; i < total; i++) {
			if (timeList[i]) {
				var html = '<tr>'+
					'<td><em class="chek"></em></td>'+
					'<td>' + timeList[i].expect + '</td>'+
					'<td><input type="text" class="text" value="0" disabled maxlength="5"> 倍</td>'+
					'<td>0.000</td>'+
					'<td>' + timeList[i].stopTime + '</td>'+
				'</tr>'
				items.push(html)
			}
		}
		thisTable.html(items.join(""))
		initTableEvent($dom);
	}
	
	var initTableEvent = function($dom) {
		var thisTable = $dom.find('.tab-list').find('table > tbody');
		thisTable.find('tr').each(function() {
			var thisRow = $(this);
			var checkbox = thisRow.find('.chek');
			var multiple = thisRow.find('input[type="text"]');
			var update = function() {
				var val = Number(multiple.val());
				if(isNaN(val)) val = 0;
				thisRow.find('td:eq(3)').html(BListInfo.money(val).toFixed(3));
				Options.update($dom);
			}
			checkbox.unbind('click').click(function() {
				if($(this).hasClass('cheked')) {
					$(this).removeClass('cheked');
					multiple.val(0);
					multiple.attr('disabled', 'disabled');
				} else {
					$(this).addClass('cheked');
					multiple.val(1);
					multiple.removeAttr('disabled');
				}
				update();
			});
			multiple.keyup(function() {
				if($(this).val() == '') return;
				var val = Number($(this).val());
				if(/^[0-9]*$/.test(val)) {
					if(val > 10000) $(this).val(10000);
					if(val < 1) $(this).val(1);
				} else {
					$(this).val(1);
				}
				update();
			});
			multiple.keydown(function(e) {
				if($(this).val() == '') return;
				var val = Number($(this).val());
				if(!isNaN(val)) {
					if(e.keyCode == 38) val++;
					if(e.keyCode == 40) val--;
					if(val > 10000) val = 10000;
					if(val < 1) val = 1;
					$(this).val(val);
				}
			});
		});
	}
	
	var bindNumber = function(els, defval) {
		if(els.length == 0) return;
		els.keydown(function(e) {
			if(e.keyCode == 38 || e.keyCode == 40) {
				if($(this).val() == '') return;
				var val = Number($(this).val());
				if(!isNaN(val)) {
					if(e.keyCode == 38) val++;
					if(e.keyCode == 40) val--;
					if(val < 0) val = defval;
					$(this).val(val);
				}
			}
		});
		els.keyup(function() {
			if($(this).val() == '') return;
			var val = Number($(this).val());
			if(/^[0-9]*$/.test(val)) {
				if(val < 0) $(this).val(1);
			} else {
				$(this).val(defval);
			}
		});
		els.blur(function() {
			if($(this).val() == '') {
				$(this).val(defval);
			}
		});
	}
	
	var initDocEvent = function($dom) {
		var tabList = $dom.find('.chase-tab');
		var chaseText = $dom.find('.chase-text');
		tabList.find('.tab').unbind('click').click(function() {
			var index = $(this).attr('data-type');
			if(!$(this).hasClass('cur')) {
				tabList.find('.tab').removeClass('cur');
				$(this).addClass('cur');
				chaseText.find('.option').hide();
				chaseText.find('.option[data-type="' + index + '"]').show();
			}
		});
		chaseText.find('[data-command="generate"]').unbind('click').click(function() {
			doGenerate($dom);
		});
		chaseText.find('.chek').unbind('click').click(function(e) {
			var $target = $(e.target);
			if ($target.hasClass('cheked')) {
				$target.removeClass('cheked');
			}
			else {
				$target.addClass('cheked');
			}
		});

		// 只能输入数字
		var expect = chaseText.find('input[name="expect"]');
		var s0Multiple = chaseText.find('input[name="s0Multiple"]');
		var s1Multiple = chaseText.find('input[name="s1Multiple"]');
		var s2Multiple = chaseText.find('input[name="s2Multiple"]');
		var maxMultiple = chaseText.find('input[name="maxMultiple"]');
		var minProfit = chaseText.find('input[name="minProfit"]');
		var expTimes = chaseText.find('input[name="expTimes"]');
		var expMultiple = chaseText.find('input[name="expMultiple"]');
		bindNumber(expect, 1);
		bindNumber(s0Multiple, 1);
		bindNumber(s1Multiple, 1);
		bindNumber(s2Multiple, 1);
		bindNumber(maxMultiple, 1);
		bindNumber(minProfit, 1);
		bindNumber(expTimes, 1);
		bindNumber(expMultiple, 1);
	}
	
	// 投注
	var isLoading = false;
	function submitHandler($dom, resolve, reject) {
		swal.resetValidationError();
		
		var data
		var blist
		var clist
		var isStop
		var lotteryId
    	var $container
		if(isLoading)
			return
		blist = BListInfo.bList()
		clist = BListInfo.cList($dom)
		if(clist.length < 1 || blist.length < 1) {
			clist.length < 1 && reject('您没有勾选任何追号计划！');
			return 
		}
		isLoading = true

		lotteryId = Lottery.id
		isStop = Options.isStop($dom)
		data = {
			lotteryId: lotteryId, 
			blist: $.toJSON(blist), 
			clist: $.toJSON(clist), 
			isStop: isStop
		}
		$.ajax({
			url: '/UserBetsChase',
			data: data,
			success: function(res) {
				if (res.error == 0) {
					resolve();
				}
				else {
					reject(res.message);
				}
			},
			error: function(xhr, status, e) {
				isLoading = false
			},
			complete : function() {
				isLoading = false
			}
		});
	}
    

	var init = function() {
		if (BListInfo.bList().length < 1) {
			$.YF.alert_warning('投注列表没有可以追号的订单！');
			return
		}
		var expect = $('#bettingTitle [data-property=currentExpect]').html()

		var html = tpl('#lottery_chase_tpl', {expect: expect});
		var $dom;
		swal({
			title: '',
			customClass:'chase',
			width: 720,
			html: html,
			showCloseButton: true,
			showCancelButton: true,
			showConfirmButton: true,
			confirmButtonText: '确认投注',
			cancelButtonText: '关闭',
			buttonsStyling: false,
			confirmButtonClass: 'popup-btn-confirm',
			cancelButtonClass: 'popup-btn-cancel',
			showLoaderOnConfirm: true,
			onBeforeOpen: function (dom) {
				$dom = $(dom);

				loadExpect(10, function() {
					initDocEvent($dom);
					initNormal($dom)

					$(".tab-list", $dom).mCustomScrollbar({
						scrollInertia: 70,
						autoHideScrollbar: true,
						theme:"dark-thin",
						advanced: {
							updateOnContentResize: true
						}
					});
				})
			},
			preConfirm: function(){
				return new Promise(function (resolve, reject) {
					submitHandler($dom, function(){
						resolve();
					}, function(msg){
						reject(msg);
					});
				})
			}
		}).then(function() {
			$.YF.alert_success('投注成功！', function(){
				LotteryMain.clear();
				setTimeout(function(){
					refresh(0);
				}, 2000);
			})
		}, function() {
		})
	}
	return {init: init};
}();