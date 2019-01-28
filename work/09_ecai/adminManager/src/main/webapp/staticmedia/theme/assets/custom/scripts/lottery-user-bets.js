var my_lzma = new LZMA("theme/assets/global/plugins/lzma/lzma_worker-min.js");

var LotteryUserBets = function() {
	
	$('#lotterychange').click(function(){
		var modal = $('#modal-lottery-user-bets-change');
		var id = modal.find('[data-field="id"]').html();
		var locked = modal.find('[data-field="locked"]').html();
		var openCode = modal.find('[data-field="openCode"]').html();
		var prizeTime = modal.find('[data-field="prizeTime"]').html();
		var codes = modal.find('textarea[name="codes"]').val();
		$.post("lottery-user-bets/change",{id:id,act:'change',locked:locked,codes:codes},function(result){
			if(result.error == 0) {
				alert(result.message);
			} else {
				alert(result.message);
			}
			window.location.reload();
		});
	}); 
	
	$('#lotterylocked').click(function(){
		var modal = $('#modal-lottery-user-bets-change');
		var id = modal.find('[data-field="id"]').html();
		var locked = modal.find('[data-field="locked"]').html();
		var lock = 0;
		if(locked == 0) {
			lock = 1;
		}
		$.post("lottery-user-bets/change",{id:id,act:'locked',locked:lock},function(result){
			if(result.error == 0) {
				alert(result.message);
			} else {
				alert("锁定失败");
			}
			window.location.reload();
		});
	}); 
	
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
	

	var UserBetsTable = function() {
		var tableList = $('#table-user-bets-list');
		var tablePagelist = tableList.find('.page-list');

		var getSearchParams = function() {
			var keyword = tableList.find('input[name="keyword"]').val();
			var username = tableList.find('input[name="username"]').val();
			var type = tableList.find('select[name="type"]').val();
			var utype = tableList.find('select[name="utype"]').val();
			var lotteryId = tableList.find('select[name="lottery"]').val();
			var ruleId = tableList.find('select[name="rule"]').val();
			var expect = tableList.find('input[name="expect"]').val();
			var minTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var maxTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			maxTime = getNetDate(maxTime);
			var minPrizeTime = tableList.find('[data-field="prizeTime"] > input[name="from"]').val();
			var maxPrizeTime = tableList.find('[data-field="prizeTime"] > input[name="to"]').val();
			maxPrizeTime = getNetDate(maxPrizeTime);
			var minBetsMoney = tableList.find('input[name="minBetsMoney"]').val();
			var maxBetsMoney = tableList.find('input[name="maxBetsMoney"]').val();
			var minMultiple = tableList.find('input[name="minMultiple"]').val();
			var maxMultiple = tableList.find('input[name="maxMultiple"]').val();
			var minPrizeMoney = tableList.find('input[name="minPrizeMoney"]').val();
			var maxPrizeMoney = tableList.find('input[name="maxPrizeMoney"]').val();
			var maxPrizeMoney = tableList.find('input[name="maxPrizeMoney"]').val();
			var status = tableList.find('select[name="status"]').val();
			var ip = tableList.find('input[name="ip"]').val();
			return {utype:utype,keyword: keyword, username: username, type: type, lotteryId: lotteryId, expect: expect, ruleId: ruleId,
				minTime: minTime, maxTime: maxTime, minPrizeTime: minPrizeTime, maxPrizeTime: maxPrizeTime,
				minBetsMoney: minBetsMoney, maxBetsMoney: maxBetsMoney, minMultiple: minMultiple, maxMultiple: maxMultiple,
				minPrizeMoney: minPrizeMoney, maxPrizeMoney: maxPrizeMoney, status: status, ip: ip};
		}

		var targetUser = Metronic.getURLParameter('username');
		if(targetUser) {
			tableList.find('input[name="username"]').val(targetUser);
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-bets/list',
			ajaxData: getSearchParams,
			beforeSend: function() {

			},
			complete: function() {

			},
			success: function(list, rsp) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var statusAction = '<a href="javascript:;" class="btn default btn-xs black disabled">无操作 </a>';
					var lockedAction = '<a href="javascript:;" class="btn default btn-xs black disabled">无操作 </a>';
					var msg = '<font color="green">[正常]</font>';
					if(val.bean.status == 0) {
						statusAction = '<a data-command="cancel" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 撤单 </a>';
						lockedAction = '<a data-command="change" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 改单 </a>';
					}
					if(val.bean.locked == 1) {
						msg = '<font color="red">[锁定]</font>';
					}
					innerHtml +=
						'<tr class="align-center" data-id="' + val.bean.id + '">'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
						'<td>' + val.lottery + '</td>'+
						'<td>' + val.bean.expect + '</td>'+
						'<td>' + val.mname + '</td>'+
						'<td>' + DataFormat.formatUserBetsModel(val.bean.model) + '</td>'+
						'<td>' + val.bean.multiple + '</td>'+
						'<td>' + val.bean.money.toFixed(4) + '</td>'+
						'<td>' + val.bean.prizeMoney.toFixed(4) + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + val.bean.prizeTime + '</td>'+
						'<td>' + DataFormat.formatUserBetsStatus(val.bean.status) + msg + '</td>'+
						'<td>' + statusAction + '<a data-command="details" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详细 </a>' + lockedAction + '</td>'+
						'</tr>';
				});
				table.html(innerHtml);
				$("#totalMoney").html(rsp.totalMoney.toFixed(4));
				$("#canceltotalMoney").html(rsp.canceltotalMoney.toFixed(4));
				$("#totalPrizeMoney").html(rsp.totalPrizeMoney.toFixed(4));
				table.find('[data-command="cancel"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var msg = '确定撤销订单？';
					bootbox.dialog({
						message: msg,
						title: '提示消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									cancelBets(id);
								}
							},
							danger: {
								label: '<i class="fa fa-undo"></i> 取消',
								className: 'btn-danger',
								callback: function() {}
							}
						}
					});
				});

				table.find('[data-command="details"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					doLoadDetails(id);
				});
				
				table.find('[data-command="change"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					doLoadChange(id);
				});
			},
			pageError: function(response) {
				$("#totalMoney").html('0.0000');
				$("#canceltotalMoney").html('0.0000');
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
				$("#canceltotalMoney").html('0.0000');
				$("#totalPrizeMoney").html('0.0000');
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});

		var cancelBets = function(id) {
			var url = './lottery-user-bets/cancel';
			var params = {id: id};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('已成功撤销该订单！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var isLoading = false;
		var doLoadDetails = function(id) {
			if(isLoading) return;
			var params = {id: id};
			var url = './lottery-user-bets/get';
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
		
		var doLoadChange = function(id) {
			if(isLoading) return;
			var params = {id: id};
			var url = './lottery-user-bets/get';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						doShowChange(data.data);
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
			modal.find('[data-field="prizeTime"]').html(data.bean.prizeTime);
			modal.find('[data-field="ip"]').html(data.bean.ip);
			modal.modal('show');
			if (data.bean.compressed === 1) {
				var textarea = modal.find('textarea[name="codes"]');
				textarea.val('');
				textarea.hide();
				var spinner = $("<i class='fa fa-spinner fa-spin mt10 ml10'></i>");
				textarea.before(spinner);

				setTimeout(function(){
					var byteArr = LZMAUtil.fromHex(data.bean.codes);
					my_lzma.decompress(byteArr, function(result, error){
						var codes;
						if (error) {
							codes = '格式化号码错误';
						}
						else {
							codes = result;
						}
						spinner.remove();
						textarea.val(codes).show();
					});
				}, 200);
			}
			else {
				modal.find('textarea[name="codes"]').val(data.bean.codes).show();
			}
		}
		
		var doShowChange = function(data) {
			var modal = $('#modal-lottery-user-bets-change');
			modal.find('[data-field="id"]').html(data.bean.id);
			modal.find('[data-field="locked"]').html(data.bean.locked);
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
			modal.find('[data-field="prizeTime"]').html(data.bean.prizeTime);
			modal.find('[data-field="ip"]').html(data.bean.ip);
			modal.modal('show');
			if (data.bean.compressed === 1) {
				var textarea = modal.find('textarea[name="codes"]');
				textarea.val('');
				textarea.hide();
				var spinner = $("<i class='fa fa-spinner fa-spin mt10 ml10'></i>");
				textarea.before(spinner);

				setTimeout(function(){
					var byteArr = LZMAUtil.fromHex(data.bean.codes);
					my_lzma.decompress(byteArr, function(result, error){
						var codes;
						if (error) {
							codes = '格式化号码错误';
						}
						else {
							codes = result;
						}
						spinner.remove();
						textarea.val(codes).show();
					});
				}, 200);
			}
			else {
				modal.find('textarea[name="codes"]').val(data.bean.codes).show();
			}
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
			var prizeDateTime = tableList.find('[data-field="prizeTime"]');
			prizeDateTime.find('input[name="from"]').val('');
			prizeDateTime.find('input[name="to"]').val('');
			tableList.find('input[name="minBetsMoney"]').val('');
			tableList.find('input[name="maxBetsMoney"]').val('');
			tableList.find('input[name="minMultiple"]').val('');
			tableList.find('input[name="maxMultiple"]').val('');
			tableList.find('input[name="minPrizeMoney"]').val('');
			tableList.find('input[name="maxPrizeMoney"]').val('');
			tableList.find('input[name="ip"]').val('');
		}

		isAdvanced();

		var isPlayRulesLoading = false;
		var loadPlayRules = function(lotteryType) {
			if(isPlayRulesLoading) return;

            if (!lotteryType) {
                tableList.find('select[name="rule"]').empty();
                handleSelect();
                return;
            }

			var url = './lottery-play-rules/simple-list';
			var params = {typeId: lotteryType};
			isPlayRulesLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isPlayRulesLoading = false;
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
			var rule = tableList.find('select[name="rule"]');
            rule.empty();
            rule.append('<option value="">全部玩法</option>');
			$.each(data, function(idx, val) {
				rule.append('<option value="' + val.ruleId + '">' + val.groupName + '_' + val.name + '</option>');
			});
			handleSelect();
		}

		var isLotteryLoading = false;
		var loadLottery = function() {
			if(isLotteryLoading) return;
			var url = './lottery/list';
			isLotteryLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					isLotteryLoading = false;
					buildLottery(data);
				}
			});
		}

		var buildLottery = function(data) {
			var lottery = tableList.find('select[name="lottery"]');
			lottery.empty();
			lottery.append('<option value="">全部类型</option>');
			$.each(data, function(idx, val) {
				lottery.append('<option data-type="' + val.bean.type + '" value="' + val.bean.id + '">' + val.bean.showName + '</option>');
			});
			lottery.unbind('change').change(function() {
				var lotteryType = lottery.find('option:selected').attr('data-type');
				loadPlayRules(lotteryType);
			});
			handleSelect();
		}

		loadLottery();

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
			var tableList = $('#table-user-bets-list');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);

			UserBetsTable.init();
			handelDatePicker();
		}
	}
}();