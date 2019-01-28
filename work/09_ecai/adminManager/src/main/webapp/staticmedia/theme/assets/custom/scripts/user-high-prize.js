var UserHighPrize = function() {
	
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
	
	var UserHighPrizeTable = function() {
		var tableList = $('#table-game-bets-list');
		var tablePagelist = tableList.find('.page-list');
		var $page = $(".page-content");

		var getSearchParams = function() {
			var type = tableList.find('select[name="type"]').val();
			var username = tableList.find('input[name="username"]').val();
			var nameId = tableList.find('select[name="nameId"]').val();
			var platform = tableList.find('select[name="platform"]').val();
			var status = tableList.find('select[name="status"]').val();
			var refId = tableList.find('input[name="refId"]').val();
			var minMoney = tableList.find('input[name="minMoney"]').val();
			var maxMoney = tableList.find('input[name="maxMoney"]').val();
			var minPrizeMoney = tableList.find('input[name="minPrizeMoney"]').val();
			var maxPrizeMoney = tableList.find('input[name="maxPrizeMoney"]').val();
			var minTimes = tableList.find('input[name="minTimes"]').val();
			var maxTimes = tableList.find('input[name="maxTimes"]').val();
			var minTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var maxTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			maxTime = getNetDate(maxTime);
			return {type:type,username: username, nameId: nameId, platform: platform, status: status, refId: refId, minMoney: minMoney,
				maxMoney: maxMoney, minPrizeMoney: minPrizeMoney, maxPrizeMoney: maxPrizeMoney, minTimes: minTimes,
				maxTimes: maxTimes, minTime: minTime, maxTime: maxTime};
		}

		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './user-high-prize/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
			},
			complete: function() {
			},
			success: function(list, rsp) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var action = '';
					// 状态;0:待确认;1:已锁定;2:已确认
					if(val.bean.status == 0) {
						action += '<a data-command="lock" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-lock"></i> 检查</a>';
						action += '<a data-command="lockAndConfirm" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-lock"></i> 确认</a>';
					}
					else if(val.bean.status == 1 && LoginUser == val.bean.confirmUsername) {
						action += '<a data-command="lock" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-lock"></i> 检查</a>';
						action += '<a data-command="unlock" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-unlock"></i> 取消</a>';
					}

					var platform;
					var refId = '<a data-command="details" href="javascript:;">'+val.bean.refId+'</a></td>';;
					if (val.bean.platform == 2) {
						platform = '彩票';
					}
					else {
						platform = val.platform;
					}

					innerHtml +=
					'<tr class="align-center" data-id="'+val.bean.id+'" data-refId="'+val.bean.refId+'" data-platform="'+val.bean.platform+'">'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
						'<td>' + platform + '</td>'+
						'<td>' + refId + '</td>'+
						'<td>' + val.bean.name + '</td>'+
						'<td>' + val.bean.subName + '</td>'+
						'<td>' + val.bean.money.toFixed(4) + '</td>'+
						'<td>' + val.bean.prizeMoney.toFixed(4) + '</td>'+
						'<td>' + DataFormat.formatUserHighPrizeTimes(val.bean.times.toFixed(2)) + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + DataFormat.formatUserHighPrizeStatus(val.bean.status) + '</td>'+
						'<td>' + val.bean.confirmUsername + '</td>'+
						'<td>' + action + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);

				table.find('[data-command="lock"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var refId = $(this).parents('tr').attr('data-refId');
					var platform = $(this).parents('tr').attr('data-platform');
					lock(id, refId, platform);
				});

				table.find('[data-command="lockAndConfirm"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var refId = $(this).parents('tr').attr('data-refId');
					var platform = $(this).parents('tr').attr('data-platform');
					lockAndConfirm(id, refId, platform);
				});

				table.find('[data-command="unlock"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					unlock(id);
				});

				table.find('[data-command="details"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					var refId = $(this).parents('tr').attr('data-refId');
					var platform = $(this).parents('tr').attr('data-platform');
					doLoadDetails(id, refId, platform, false);
				});
			},
			pageError: function(response) {
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
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});

		var lock = function(id, refId, platform) {
			var params = {id: id};
			var url = './user-high-prize/lock';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						// reload();
						// toastr['success']('操作成功,请尽快检查！', '操作提示');
						doLoadDetails(id, refId, platform, true);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var lockAndConfirm = function(id, refId, platform) {
			var params = {id: id};
			var url = './user-high-prize/lock';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						// reload();
						// toastr['success']('操作成功,请尽快检查！', '操作提示');
						confirm(id, null);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var unlock = function(id) {
			var params = {id: id};
			var url = './user-high-prize/unlock';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var confirm = function(id, hideModal) {
			var params = {id: id};
			var url = './user-high-prize/confirm';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						if (hideModal) {
							hideModal.modal("hide");
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var isLoading = false;
		var doLoadDetails = function(id, refId, platform, showConfirm) {
			if(isLoading) return;
			var params = {id: refId};
			var url = './lottery-user-bets/get';
			if (platform != 2) {
				url = './game-bets/get';
			}
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					if(data.error == 0) {
						if (platform == 2) {
							doShowLotteryDetails(id, data.data, showConfirm);
						}
						else {
							doShowGameDetails(id, data.data, showConfirm);
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		var doShowLotteryDetails = function(id, data, showConfirm) {
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

			var $confirmBtn = modal.find('[data-command="confirm"]').unbind('click');
			if (showConfirm == true) {
				$confirmBtn.click(function() {
					confirm(id, modal);
				});
			}
			else {
				$confirmBtn.hide();
			}
		}

		var doShowGameDetails = function(id, data, showConfirm) {
			var modal = $('#modal-game-bets-details');
			modal.find('[data-field="username"]').html(data.username);
			modal.find('[data-field="platform"]').html(data.platform);
			modal.find('[data-field="betsId"]').html(data.bean.betsId);
			modal.find('[data-field="gameCode"]').html(data.bean.gameCode);
			modal.find('[data-field="gameType"]').html(data.bean.gameType);
			modal.find('[data-field="gameName"]').html(data.bean.gameName);
			var money = data.bean.money + data.bean.progressiveMoney;
			money = money.toFixed(4);
			modal.find('[data-field="money"]').html(money + '元');
			var prizeMoney = data.bean.prizeMoney + data.bean.progressivePrize;
			prizeMoney = prizeMoney.toFixed(4);
			modal.find('[data-field="prizeMoney"]').html(prizeMoney + '元');
			modal.find('[data-field="time"]').html(data.bean.time);
			modal.find('[data-field="prizeTime"]').html(data.bean.prizeTime);
			modal.find('[data-field="status"]').html(DataFormat.formatGameStatus(data.bean.status));
			modal.modal('show');

			var $confirmBtn = modal.find('[data-command="confirm"]').unbind('click');
			if (showConfirm == true) {
				$confirmBtn.click(function() {
					confirm(id, modal);
				});
			}
			else {
				$confirmBtn.hide();
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
			tableList.find('select[name="status"]').val('');
			tableList.find('input[name="minMoney"]').val('');
			tableList.find('input[name="maxMoney"]').val('');
			tableList.find('input[name="refId"]').val('');
		}
		
		isAdvanced();

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
			var nameId = tableList.find('select[name="nameId"]');
			nameId.empty();
			nameId.append('<option value="">全部游戏</option>');
			$.each(data, function(idx, val) {
				nameId.append('<option value="' + val.bean.id + '">' + val.bean.showName + '</option>');
			});
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
			var tableList = $('#table-game-bets-list');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);

			UserHighPrizeTable.init();
			handelDatePicker();
		}
	}
}();