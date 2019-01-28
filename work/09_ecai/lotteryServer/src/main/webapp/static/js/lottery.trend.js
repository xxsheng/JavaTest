$(function () {
	var layout
	var moving = null
	var loaded = true
	var $bd = $('body')
	var $table = $('table', $bd)
	var $thead = $('thead', $table)
	var $tbody = $('tbody', $table)
	var $tfoot = $('tfoot', $table)
	var $search = $('div.search', $bd)
	var $loader = $('<div class="loader" />')
	var canvas = document.getElementById('canvas')
	var $searchCondition = $('.search-condition', $search);
	var $commands = $('[data-command="showTrend"]', $searchCondition);
	var $loading = $('.loading', $searchCondition);
	var defaultRuleName = '五星走势图';
	var $defaultRuleName = $('#defaultRuleName', $searchCondition);


	if (Lottery.shortName == 'jsmmc') {
		$thead.html('<tr><th>很抱歉，暂不支持该彩种走势图！</th></tr>')
	}
	else {
		switch (Lottery.type) {
			case 1:
			case 7:
				layout = {
					title: ['万位', '千位', '百位', '十位', '个位'],
					codes: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
					separator: false,
					showDistribution: true
				}
				defaultRuleName = '五星走势图';
				break
			case 2:
				layout = {
					title: ['第一位', '第二位', '第三位', '第四位', '第五位'],
					codes: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11'],
					separator: true,
					showDistribution: true
				}
				defaultRuleName = '五星走势图';
				break
			case 3:
				layout = {
					title: ['第一位', '第二位', '第三位'],
					codes: [1, 2, 3, 4, 5, 6],
					separator: false,
					showDistribution: true
				}
				defaultRuleName = '三星走势图';
				break
			case 4:
				layout = {
					title: ['第一位', '第二位', '第三位'],
					codes: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
					separator: false,
					showDistribution: true
				}
				defaultRuleName = '三星走势图';
				break
			case 6:
				layout = {
					title: ['第一名', '第二名', '第三名', '第四名', '第五名', '第六名', '第七名', '第八名', '第九名', '第十名'],
					codes: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10'],
					separator: true,
					showDistribution: false
				}
				defaultRuleName = '定位走势图';
				break
			default:
				$thead.html('<tr><th>很抱歉，暂不支持该彩种走势图！</th></tr>')
				return
		}
	}

	function renderTable(res) {


		$thead.html(tpl('#thead_tpl', layout))
		if (res) {
			var codeSet = new Array();

			$.each(res.list, function(itemIndex, item){
				var codes = item.code.split(',');
				codeSet.push(codes);
			});

			// 计算遗漏
			var codeLost = calCodeLost(layout, codeSet);

			// 计算遗漏条位置
			var codeLostLine = calCodeLostLine(layout, codeLost);

			// 计算出现次数
			var codeTimes = calCodeTimes(layout, codeSet);

			// 计算最大遗漏
			var maxLost = calMaxLost(layout, codeLost);

			// 计算最大连出
			var maxHit = calMaxHit(layout, codeSet);

			var distribution = null;
			var distributionLost = null;
			if (layout.showDistribution) {
				// 计算号码分布
				distribution = calDistribution(layout, codeSet);
				// 计算号码分布遗漏
				distributionLost = calDistributionLost(layout, codeSet);

				// 计算出现次数
				var distributionCodeTimes = calDistributionCodeTimes(layout, codeSet);

				// 计算最大遗漏
				var distributionMaxLost = calDistributionMaxLost(layout, distributionLost);

				// 计算最大连出
				var distributionMaxHit = calDistributionMaxHit(layout, codeSet);
			}

			$tbody.html(tpl('#tbody_tpl', $.extend({
				codeLost: codeLost,
				distribution: distribution,
				distributionLost: distributionLost,
				codeLostLine: codeLostLine
			}, layout, res)))

			$tfoot.html(tpl('#tfoot_tpl', $.extend({
				codeTimes: codeTimes,
				distributionCodeTimes: distributionCodeTimes,
				maxLost: maxLost,
				distributionMaxLost: distributionMaxLost,
				maxHit: maxHit,
				distributionMaxHit: distributionMaxHit
			}, layout)))
		}
		resize()
	}

	function renderLines(cav) {
		var context = cav.getContext("2d")
		$.each(layout.title, function(i) {
			context.lineWidth = 1.5
			context.strokeStyle = '#f53f00'
			$('tr', $tbody).each(function(n) {
				var $dom = $('cite:eq(' + i + ')', this)
				var offset = $dom.offset()
				var x = offset.left + $dom.width() / 2
				var y = offset.top + $dom.height() / 2 - 116
				context[n === 0 ? 'moveTo' : 'lineTo'](x, y)
			})
			context.stroke()
		})
	}

	function resize() {
		canvas.width = $tbody.width()
		canvas.height = $tbody.height() + 100
		renderLines(canvas)
		// $search.width($tbody.height() + 100)
	}

	function getData(command, callback) {
		if (!loaded)
			return
		loaded = false
		$bd.append($loader)
		$.ajax({
			type: 'post',
			dataType: 'json',
			url: '/LotteryCodeTrend',
			data: {
				lotteryId: Lottery.id,
				command: command
			},
			success: function(res) {
				renderTable(res)
				if (callback) {
					callback();
				}
			},
			complete: function() {
				loaded = true
				$loader.remove()
			}
		})
	}

	function calCodeLost(layout, codeSet) {
		var lostArray = new Array();
		lostArray.length = codeSet.length;

		for(var i=0; i<codeSet.length; i++) {
			var codes = codeSet[i];
			var codeLost = new Array();

			var lastLostArray = i<=0 ? null : lostArray[i-1];

			$.each(layout.title, function(layoutTitleIndex, layoutTitle){

				var titleLost = new Array();
				var titleIndexCode = codes[layoutTitleIndex];
				var lastTitleLost = lastLostArray == null ? null : lastLostArray[layoutTitleIndex];

				$.each(layout.codes, function(layoutCodeIndex, layoutCode){
					if (layoutCode == titleIndexCode) {
						titleLost.push(0);
					}
					else {
						var lastLostTime = lastTitleLost == null ? 0 : lastTitleLost[layoutCodeIndex];
						var currentLostTime = lastLostTime+1;
						titleLost.push(currentLostTime);
					}
				});

				codeLost.push(titleLost);
			});

			lostArray[i] = codeLost;
		}

		return lostArray;
	}

	function calCodeLostLine(layout, codeLost) {
		var lostLineArray = new Array();

		// codeLost是一个三维数组，纵向最后一个为0的以后的纵使向数值都是lostline

		$.each(layout.title, function(layoutTitleIndex, layoutTitle){
			var titleLostLineArray = new Array();

			$.each(layout.codes, function(layoutCodesIndex, layoutCode){

				var codeLostArray = new Array();

				var breakDown = false;
				for(var i=codeLost.length-1; i>=0; i--) {
					var codeLostArr = codeLost[i];
					var lost = codeLostArr[layoutTitleIndex][layoutCodesIndex];
					if (breakDown == false) {
						codeLostArray.push(true);
					}
					else {
						codeLostArray.push(false);
					}
					if (lost == 0) {
						breakDown = true;
					}
				}

				codeLostArray.reverse();

				titleLostLineArray.push(codeLostArray);
			});

			lostLineArray.push(titleLostLineArray);
		});

		return lostLineArray;
	}

	function calDistribution(layout, unSortCodeSet) {
		var codeSet = new Array();

		$.each(unSortCodeSet, function(itemIndex, item){
			item = quickSort(item);
			codeSet.push(item);
		});

		var distributionArray = new Array();

		$.each(codeSet, function(codeArrIndex, codeArr){
			var distribution = new Array();

			$.each(codeArr, function(codeIndex, code){
				var codeDistribution = {};
				codeDistribution.code = code;
				if (isSpecial(code, codeArr)) {
					codeDistribution.special = true;
				}
				else {
					codeDistribution.special = false;
				}

				distribution.push(codeDistribution);
			});

			distributionArray.push(distribution);
		});

		return distributionArray;
	}

	function calDistributionLost(layout, codeSet) {
		var lostArray = new Array();
		lostArray.length = codeSet.length;

		for(var i=0; i<codeSet.length; i++) {
			var codes = codeSet[i];
			var codeLost = new Array();
			var lastLostArray = i<=0 ? null : lostArray[i-1];

			$.each(layout.codes, function(layoutCodeIndex, layoutCode){
				if (inArray(layoutCode, codes)) {
					codeLost.push(0);
				}
				else {
					var lastLostTime = lastLostArray == null ? 0 : lastLostArray[layoutCodeIndex];
					var currentLostTime = lastLostTime+1;
					codeLost.push(currentLostTime);
				}
			});

			lostArray[i] = codeLost;
		}

		return lostArray;
	}

	function calCodeTimes(layout, codeSet) {
		var timesArray = new Array();
		$.each(layout.title, function(layoutTitleIndex, layoutTitle){

			var titleArray = new Array();

			$.each(layout.codes, function(layoutCodeIndex, layoutCode){

				var layoutCodeTimes = 0;

				$.each(codeSet, function(codeArrIndex, codeArr){
					var code = codeArr[layoutTitleIndex];
					if (layoutCode == code) {
						layoutCodeTimes++;
					}
				});

				titleArray.push(layoutCodeTimes);
			});

			timesArray.push(titleArray);
		});

		return timesArray;
	}

	function calDistributionCodeTimes(layout, codeSet) {
		var timesArray = new Array();

		$.each(layout.codes, function(layoutCodeIndex, layoutCode){

			var layoutCodeTimes = 0;

			$.each(codeSet, function(codeArrIndex, codeArr){
				if (inArray(layoutCode, codeArr)) {
					layoutCodeTimes++;
				}
			});

			timesArray.push(layoutCodeTimes);
		});

		return timesArray;
	}

	function calMaxLost(layout, codeLost) {
		var maxLostArray = new Array();

		$.each(layout.title, function(layoutTitleIndex, layoutTitle){
			var titleLostArray = new Array();

			$.each(layout.codes, function(layoutCodesIndex, layoutCode){

				var codeMaxLost = 0;

				$.each(codeLost, function(codeLostArrIndex, codeLostArr){
					var lost = codeLostArr[layoutTitleIndex][layoutCodesIndex];
					if (lost > codeMaxLost) {
						codeMaxLost = lost;
					}
				});

				titleLostArray.push(codeMaxLost);

			});

			maxLostArray.push(titleLostArray);
		});

		return maxLostArray;
	}

	function calDistributionMaxLost(layout, distributionLost) {
		var maxLostArray = new Array();

		$.each(layout.codes, function(layoutCodesIndex, layoutCode){

			var codeMaxLost = 0;

			$.each(distributionLost, function(distributionLostArrIndex, distributionLostArr){
				var lost = distributionLostArr[layoutCodesIndex];
				if (lost > codeMaxLost) {
					codeMaxLost = lost;
				}
			});

			maxLostArray.push(codeMaxLost);

		});

		return maxLostArray;
	}

	function calMaxHit(layout, codeSet) {
		var hitArray = new Array();
		$.each(layout.title, function(layoutTitleIndex, layoutTitle){

			var titleHitArray = new Array();

			$.each(layout.codes, function(layoutCodeIndex, layoutCode){
				var hitIndexArray = new Array();

				$.each(codeSet, function(codeArrIndex, codeArr){
					var code = codeArr[layoutTitleIndex];
					if (layoutCode == code) {
						hitIndexArray.push(codeArrIndex);
					}
				});

				var hitTimes = getSequenceCount(hitIndexArray);

				titleHitArray.push(hitTimes);
			});

			hitArray.push(titleHitArray);
		});

		return hitArray;
	}

	function calDistributionMaxHit(layout, codeSet) {
		var hitArray = new Array();

		$.each(layout.codes, function(layoutCodeIndex, layoutCode){

			var hitIndexArray = new Array();
			$.each(codeSet, function(codeArrIndex, codeArr){
				if (inArray(layoutCode, codeArr)) {
					hitIndexArray.push(codeArrIndex);
				}
			});

			var hitTimes = getSequenceCount(hitIndexArray);

			hitArray.push(hitTimes);
		});

		return hitArray;
	}

	function isSpecial(e, data) {
		switch (Lottery.type) {
			case 1:
			case 7:
				return isSSCSpecial(e, data);
			default:
				return false;
		}
	}

	function isSSCSpecial(e, data) {
		var count = 0;
		for (var i = 0; i < data.length; i++) {
			if (data[i] == e) count++;
		}
		return count > 1;
	}

	var inArray = function(e, data) {
		for (var i = 0; i < data.length; i++) {
			if (data[i] == e) return true;
		}
		return false;
	}

	var quickSort = function(array) {
		var i = 0;
		var j = array.length - 1;
		var Sort = function(i, j){
			// 结束条件
			if(i == j ){ return };
			var key = array[i];
			var stepi = i; // 记录开始位置
			var stepj = j; // 记录结束位置
			while(j > i){
				// j <<-------------- 向前查找
				if(array[j] >= key){
					j--;
				}
				else {
					array[i] = array[j];
					//i++ ------------>>向后查找
					while(j > ++i){
						if(array[i] > key){
							array[j] = array[i];
							break;
						}
					}
				}
			}
			// 如果第一个取出的 key 是最小的数
			if(stepi == i){
				Sort(++i, stepj);
				return;
			}
			// 最后一个空位留给 key
			array[i] = key;
			// 递归
			Sort(stepi, i);
			Sort(j, stepj);
		}

		Sort(i, j);
		return array;
	}

	var getSequenceCount = function(array) {
		if (array == null || array.length <= 0) return 0;
		if (array.length <= 1) return 1;

		array = quickSort(array);

		var maxArrays = new Array();
		var nowArrays = new Array();
		var prev = array[0];
		for(var i=1; i<array.length; i++) {
			var current = array[i];
			if(prev == current-1) {
				nowArrays.push(current);
				if( nowArrays.length >= maxArrays.length ) {
					maxArrays = nowArrays;
				}
			}
			else {
				nowArrays = new Array();
			}
			prev = current;
		}

		return maxArrays.length + 1;
	}

	$commands.on('click', function(e) {
		$commands.hide();
		$loading.show();

		var command = $(this).attr('data-val');
		getData(command, function(){
			setTimeout(function(){
				$commands.show();
				$loading.hide();
			}, 300);
		})
	})

	$search.on('click', '[data-command="collapse"]', function(e) {
		$searchCondition.finish().slideToggle('fast', function(){
			resize();
		});
	})

	$search.on('change', '[data-command="help-line"]', function(e) {
		$tbody.find('td.help-line').toggleClass('border-bottom');
	})

	$search.on('change', '[data-command="lost"]', function(e) {
		$table.toggleClass('table-none-lost');
	})

	$search.on('change', '[data-command="lost-line"]', function(e) {
		$table.toggleClass('table-lost-line');
	})

	$search.on('change', '[data-command="trend"]', function(e) {
		$(canvas).toggleClass('hidden');
	})

	$(window).on('resize', function() {
		if (!moving) {
			moving = setInterval(function() {
				resize()
				clearInterval(moving)
				moving = null
			}, 200)
		}
	})

	$defaultRuleName.html(defaultRuleName);
	getData('latest-30-desc')
})