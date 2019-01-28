var my_lzma = new LZMA("theme/assets/global/plugins/lzma/lzma_worker-min.js");

var LotteryUserBetsOriginal = function() {
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

	var UserBetsOrignalTable = function() {
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
			var minBetsMoney = tableList.find('input[name="minBetsMoney"]').val();
			var maxBetsMoney = tableList.find('input[name="maxBetsMoney"]').val();
			var minMultiple = tableList.find('input[name="minMultiple"]').val();
			var maxMultiple = tableList.find('input[name="maxMultiple"]').val();
			var minPrizeMoney = tableList.find('input[name="minPrizeMoney"]').val();
			var maxPrizeMoney = tableList.find('input[name="maxPrizeMoney"]').val();
			var status = tableList.find('select[name="status"]').val();
			return {keyword: keyword, username: username, utype: utype,type: type, lotteryId: lotteryId, expect: expect, ruleId: ruleId,
				minTime: minTime, maxTime: maxTime,
				minBetsMoney: minBetsMoney, maxBetsMoney: maxBetsMoney, minMultiple: minMultiple, maxMultiple: maxMultiple,
				minPrizeMoney: minPrizeMoney, maxPrizeMoney: maxPrizeMoney, status: status};
		}

		var targetUser = Metronic.getURLParameter('username');
		if(targetUser) {
			tableList.find('input[name="username"]').val(targetUser);
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-bets/original-list',
			ajaxData: getSearchParams,
			beforeSend: function() {

			},
			complete: function() {

			},
			success: function(list, rsp) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var trClass = '';
					if (val.manipulate == 1) {
						trClass = 'danger';
					}
					innerHtml +=
						'<tr class="align-center '+trClass+'" data-id="' + val.bean.id + '">'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
						'<td>' + val.lottery + '</td>'+
						'<td>' + val.bean.expect + '</td>'+
						'<td>' + val.mname + '</td>'+
						'<td>' + DataFormat.formatUserBetsModel(val.bean.model) + '</td>'+
						'<td>' + val.bean.multiple + '</td>'+
						'<td>' + val.bean.money.toFixed(4) + '</td>'+
						'<td>' + val.bean.prizeMoney.toFixed(4) + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + val.bean.identification + '</td>'+
						'<td>' + val.currIdentification + '</td>'+
						'<td>' + (val.manipulate == 1 ? '是' : '否') + '</td>'+
						'<td>' + '<a data-command="details" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详细 </a></td>'+
						'</tr>';
				});
				table.html(innerHtml);
				$("#totalMoney").html(rsp.totalMoney.toFixed(4));
				$("#totalPrizeMoney").html(rsp.totalPrizeMoney.toFixed(4));

				table.find('[data-command="details"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					doLoadDetails(id);
				});
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

		var isLoading = false;
		var doLoadDetails = function(id) {
			if(isLoading) return;
			var params = {id: id};
			var url = './lottery-user-bets/original-get';
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
			modal.find('[data-field="prizeTime"]').html(data.bean.prizeTime);
			modal.find('[data-field="identification"]').html(data.bean.identification);
			modal.find('[data-field="currIdentification"]').html(data.currIdentification);
			modal.find('[data-field="manipulate"]').html(data.manipulate == 1 ? '是' : '否');
			modal.modal('show');

			if (data.bean.compressed === 1) {
				var textarea = modal.find('textarea[name="codes"]');
				textarea.hide();
				var spinner = $("<i class='fa fa-spinner mt10 ml10'></i>");
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

			UserBetsOrignalTable.init();
			handelDatePicker();
		}
	}
}();