var LotteryUserWithdrawCheck = function() {
	
	var id = Metronic.getURLParameter('id');
	
	var loadData = function() {
		if(!id) return;
		var url = './lottery-user-withdraw/check';
		var params = {id: id};
		$.ajax({
			type : 'post',
			url : url,
			data : params,
			dataType : 'json',
			success : function(data) {
				if(data.error == 0) {
					buildWithdrawDetails(data.wBean);
					buildUserInfo(data.uBean);
					buildBillList(data.uBillList);
					buildUserCardList(data.uCardList);
					buildRechargeList(data.uRechargeList);
					buildWithdrawList(data.uWithdrawList);
					buildOrderList(data.uOrderList);
				}
			}
		});
	}
	
	var buildWithdrawDetails = function(data) {
		var thisTable = $('#table-withdraw-details');
		
		thisTable.find('[data-field]').html('');
		thisTable.find('input[type="text"]').val('');
		thisTable.find('select > option').eq(0).attr('selected', true);
		
		thisTable.find('[data-field="username"]').html(data.username);
		thisTable.find('[data-field="cardName"]').html(data.bean.cardName);
		thisTable.find('[data-field="bankName"]').html(data.bean.bankName);
		thisTable.find('[data-field="cardId"]').html(data.bean.cardId);
		thisTable.find('[data-field="recMoney"]').html(data.bean.recMoney.toFixed(2));
		thisTable.find('[data-field="money"]').html(data.bean.money.toFixed(2));
		thisTable.find('[data-field="feeMoney"]').html(data.bean.feeMoney.toFixed(2));
		thisTable.find('[data-field="time"]').html(data.bean.time);
		thisTable.find('[data-field="status"]').html(DataFormat.formatUserWithdrawStatus(data.bean.status));
		thisTable.find('[data-field="lockStatus"]').html(DataFormat.formatUserWithdrawLockStatus(data.bean.lockStatus));
		thisTable.find('[data-field="checkStatus"]').html(DataFormat.formatUserWithdrawCheckStatus(data.bean.checkStatus));
		thisTable.find('[data-field="remitStatus"]').html(DataFormat.formatUserWithdrawRemitStatus(data.bean.remitStatus));
		
		if(data.bean.lockStatus == 1) {
			thisTable.find('[data-field="lockStatus"]').append('（锁定人：' + '<span style="color:red">'+data.bean.operatorUser +'</span>' + '）');
		}
		
		thisTable.find('[data-field="payBillno"]').html(data.bean.payBillno);
		thisTable.find('[data-field="remarks"]').html(data.bean.remarks);
		
		if(data.bean.status == 0 && data.bean.checkStatus == 0) {
			$('[data-command="check"]').removeClass('hide');
		} else {
			$('[data-command="check"]').addClass('hide');
		}
		
		$('[data-command="check"]').unbind('click').click(function() {
			var thisVal = $(this).attr('data-value');
			$("#id").val(data.bean.id);
			if (thisVal==1) {
				doCheck(data.bean.id, thisVal);
			}else{
				ModifyShFailModal.show();
			}
		});
		
		$('[data-command="cancel"]').unbind('click').click(function() {
			window.location.href = '/lottery-user-withdraw';
		});
		
		thisTable.find('[data-command="bill"]').unbind('click').click(function() {
			var href = '/lottery-user-bill?username=' + data.username;
			$(this).attr('href', href).attr('target', new Date().getTime());
		});
		
		thisTable.find('[data-command="bets"]').unbind('click').click(function() {
			var href = '/lottery-user-bets?username=' + data.username;
			$(this).attr('href', href).attr('target', new Date().getTime());
		});
		
		thisTable.find('[data-command="report"]').unbind('click').click(function() {
			var href = '/lottery-report-complex?username=' + data.username;
			$(this).attr('href', href).attr('target', new Date().getTime());
		});
		
		thisTable.find('[data-command="profile"]').unbind('click').click(function() {
			var href = '/lottery-user-profile?username=' + data.username;
			$(this).attr('href', href).attr('target', new Date().getTime());
		});
		
		thisTable.find('[data-command="recharge"]').unbind('click').click(function() {
			var href = '/lottery-user-recharge?username=' + data.username;
			$(this).attr('href', href).attr('target', new Date().getTime());
		});
	}
	
	// 格式化时间
	var formatTime = function(seconds) {
		var s = 1, m = 60 * s, h = m * 60, d = 24 * h; 
		var ss = 0, mm = 0, hh = 0, dd;
		if(s > 0) {
			dd = Math.floor(seconds / d);
			hh = Math.floor(seconds % d / h);
			mm = Math.floor(seconds % d % h / m);
			ss = Math.floor(seconds % d % h % m / s);
		}
		var p = function(t) {
			return t < 10 ? '0' + t : t;
		}
		return [dd, hh, mm, ss];
	}
	
	// 格式化时间锁
	var formatLockTime = function(time) {
		if(time != '') {
			var surplus = moment(time).valueOf() - moment().valueOf();
			if(surplus > 0) {
				surplus = Math.round(surplus / 1000);
				var fTime = formatTime(surplus);
				var result = '';
				if(surplus > 24 * 60 * 60) {
					result += fTime[0] + '天';
				}
				if(surplus > 60 * 60) {
					result += fTime[1] + '小时';
				}
				if(surplus > 60) {
					result += fTime[2] + '分钟';
				}
				if(fTime[3] > 0) {
					result += fTime[3] + '秒';
				}
				return result;
			}
		}
		return '未锁定';
	}
	
	var buildUserInfo = function(data) {
		var basic = $('#basic_information');
		basic.find('[data-field="id"]').html(data.bean.id);
		basic.find('[data-field="username"]').html(data.bean.username);
		basic.find('[data-field="nickname"]').html(data.bean.nickname);
		basic.find('[data-field="type"]').html(DataFormat.formatUserType(data.bean.type));
		
		basic.find('[data-field="totalMoney"]').html(data.bean.totalMoney.toFixed(3));
		basic.find('[data-field="lotteryMoney"]').html(data.bean.lotteryMoney.toFixed(3));
		basic.find('[data-field="baccaratMoney"]').html(data.bean.baccaratMoney.toFixed(3));
		basic.find('[data-field="freezeMoney"]').html(data.bean.freezeMoney.toFixed(3));
		
		basic.find('[data-field="code"]').html(data.bean.code);
		basic.find('[data-field="locatePoint"]').html(data.bean.locatePoint.toFixed(1) + '%');
		basic.find('[data-field="notLocatePoint"]').html(data.bean.notLocatePoint.toFixed(1) + '%');
		basic.find('[data-field="extraPoint"]').html(data.bean.extraPoint.toFixed(1) + '%');
		
		var formatUpUser = '无';
		if(data.upUser != '') {
			formatUpUser = '<a href="./lottery-user-profile?username=' + data.upUser + '">' + data.upUser + '</a>';
		}
		basic.find('[data-field="lowerUsers"]').html(data.lowerUsers + '人');
		var formatLevelUsers = '无';
		if(data.levelUsers.length > 0) {
			formatLevelUsers = data.bean.username;
			$.each(data.levelUsers, function(idx, val) {
				formatLevelUsers += ' &gt; <a href="./lottery-user-profile?username=' + val + '">' + val + '</a>';
			});
		}
		basic.find('[data-field="levelUsers"]').html(formatLevelUsers);
		
		basic.find('[data-field="registTime"]').html(data.bean.registTime);
		basic.find('[data-field="loginTime"]').html(data.bean.loginTime == '' ? '从未登录过' : data.bean.loginTime);
		basic.find('[data-field="AStatus"]').html(DataFormat.formatUserAStatus(data.bean.AStatus));
		basic.find('[data-field="BStatus"]').html(DataFormat.formatUserBStatus(data.bean.BStatus));
		basic.find('[data-field="message"]').html(data.bean.message);
		
		basic.find('[data-field="onlineStatus"]').html(data.bean.onlineStatus == 1 ? '在线' : '离线');
		basic.find('[data-field="allowEqualCode"]').html(data.bean.allowEqualCode == 1 ? '开启' : '关闭');
		basic.find('[data-field="allowTransfers"]').html(data.bean.allowTransfers == 1 ? '开启' : '关闭');
		
		basic.find('[data-field="lockTime"]').html(formatLockTime(data.bean.lockTime));
	}
	
	var buildBillList = function(data) {
		var thisTable = $('#table-user-bill-list');
		thisTable.find('table > tbody').empty();
		if(data.length > 0) {
			var innerHtml = '';
			$.each(data, function(idx, val) {
				innerHtml +=
				'<tr class="align-center">'+
					'<td>' + val.bean.id + '</td>'+
					'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
					'<td>' + val.account + '</td>'+
					'<td>' + DataFormat.formatUserBillType(val.bean.type) + '</td>'+
					'<td>' + val.bean.beforeMoney.toFixed(4) + '</td>'+
					'<td>' + val.bean.money.toFixed(4) + '</td>'+
					'<td>' + val.bean.afterMoney.toFixed(4) + '</td>'+
					'<td>' + val.bean.time + '</td>'+
					'<td>' + val.bean.remarks + '</td>'+
				'</tr>';
			});
			thisTable.find('table > tbody').html(innerHtml);
		} else {
			var tds = thisTable.find('thead tr th').size();
			thisTable.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
		}
	}
	
	var buildUserCardList = function(data) {
		var thisTable = $('#table-user-card-list');
		thisTable.find('table > tbody').empty();
		if(data.length > 0) {
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var lockTime = val.bean.lockTime != '' ? val.bean.lockTime : '-';
				innerHtml +=
				'<tr class="align-center">'+
					'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
					'<td>' + val.bankName + '</td>'+
					'<td>' + val.bean.cardName + '</td>'+
					'<td>' + val.bean.cardId + '</td>'+
					'<td>' + DataFormat.formatUserCardStatus(val.bean.status) + '</td>'+
					'<td>' + lockTime + '</td>'+
				'</tr>';
			});
			thisTable.find('table > tbody').html(innerHtml);
		} else {
			var tds = thisTable.find('thead tr th').size();
			thisTable.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
		}
	}
	
	var buildRechargeList = function(data) {
		var thisTable = $('#table-user-recharge-list');
		thisTable.find('table > tbody').empty();
		if(data.length > 0) {
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var formatMoney = val.bean.money.toFixed(2);
				if(val.bean.type == 3) {
					formatMoney = '<font color="red">' + formatMoney + '</font>';
				}
				var typeName = DataFormat.formatUserRechargeType(val.bean.type, val.bean.subtype);
				if(val.bean.type !=3){
					if(val.name != ""){
						typeName = val.name;
					}
				}
				innerHtml += 
				'<tr class="align-center">'+
					'<td>' + val.bean.billno + '</td>'+
					'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
					'<td>' + val.bean.beforeMoney.toFixed(2) + '</td>'+
					'<td>' + formatMoney + '</td>'+
					'<td>' + val.bean.afterMoney.toFixed(2) + '</td>'+
					'<td>' + typeName + '</td>'+
					'<td>' + val.bean.time + '</td>'+
					'<td>' + DataFormat.formatUserRechargeStatus(val.bean.status) + '</td>'+
				'</tr>';
			});
			thisTable.find('table > tbody').html(innerHtml);
		} else {
			var tds = thisTable.find('thead tr th').size();
			thisTable.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
		}
	}
	
	var buildWithdrawList = function(data) {
		var thisTable = $('#table-user-withdraw-list');
		thisTable.find('table > tbody').empty();
		if(data.length > 0) {
			var innerHtml = '';
			$.each(data, function(idx, val) {
				innerHtml +=
				'<tr class="align-center" data-name="' + val.username + '" data-id="' + val.bean.id + '">'+
					'<td>' + val.bean.billno + '</td>'+
					'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
					'<td>' + val.bean.beforeMoney.toFixed(2) + '</td>'+
					'<td>' + val.bean.money.toFixed(2) + '</td>'+
					'<td>' + val.bean.afterMoney.toFixed(2) + '</td>'+
					'<td>' + val.bean.cardName + ' (' + val.bean.bankName + ') <br/> ' + val.bean.cardId + '</td>'+
					'<td>' + val.bean.time + '</td>'+
					'<td>' + DataFormat.formatUserWithdrawStatus(val.bean.status) + '</td>'+
					'<td>' + DataFormat.formatUserWithdrawCheckStatus(val.bean.checkStatus) + '</td>'+
				'</tr>';
			});
			thisTable.find('table > tbody').html(innerHtml);
		} else {
			var tds = thisTable.find('thead tr th').size();
			thisTable.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
		}
	}

	var buildOrderList = function(data) {
		var thisTable = $('#table-user-order-list');
		thisTable.find('table > tbody').empty();
		if(data.length > 0) {
			var innerHtml = '';
			$.each(data, function(idx, val) {
				innerHtml +=
				'<tr class="align-center">'+
					'<td>' + val.bean.billno + '</td>'+
					'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
					'<td>' + val.lottery + '</td>'+
					'<td>' + val.bean.expect + '</td>'+
					'<td>' + val.mname + '</td>'+
					'<td>' + DataFormat.formatUserBetsModel(val.bean.model) + '</td>'+
					'<td>' + val.bean.multiple + '</td>'+
					'<td>' + val.bean.money.toFixed(4) + '</td>'+
					'<td>' + val.bean.prizeMoney.toFixed(4) + '</td>'+
					'<td>' + val.bean.time + '</td>'+
					'<td>' + DataFormat.formatUserBetsStatus(val.bean.status) + '</td>'+
				'</tr>';
			});
			thisTable.find('table > tbody').html(innerHtml);
		} else {
			var tds = thisTable.find('thead tr th').size();
			thisTable.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
		}
	}
	
	var isLoading = false;
	var reload = function(id) {
		if(isLoading) return;
		var params = {id: id};
		var url = './lottery-user-withdraw/get';
		isLoading = true;
		$.ajax({
			type : 'post',
			url : url,
			data : params,
			dataType : 'json',
			success : function(data) {
				isLoading = false;
				if(data.error == 0) {
					buildWithdrawDetails(data.data);
				}
				if(data.error == 1 || data.error == 2) {
					toastr['error']('操作失败！' + data.message, '操作提示');
				}
			}
		});
	}
	
	var doCheck = function(id, status) {
		var url = './lottery-user-withdraw/check-result';
		$.ajax({
			type : 'post',
			url : url,
			data : {id: id, status: status},
			dataType : 'json',
			success : function(data) {
				isSending = false;
				if(data.error == 0) {
					reload(id);
					toastr['success']('操作成功！', '操作提示');
				}
				if(data.error == 1 || data.error == 2) {
					toastr['error']('操作失败！' + data.message, '操作提示');
				}
			}
		});
	}
	var ModifyShFailModal = function() {
		var modal = $('#modal-user-sh-Fail');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					message: {
						required: true,
					}
				},
				messages: {
					message: {
						required: '操作备注不能为空！',
					}
	            },
	            invalidHandler: function (event, validator) {},
	            errorPlacement: function (error, element) {
	            	$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
                },
                highlight: function (element) {
                    $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
                },
                unhighlight: function (element) {
                    $(element).closest('.form-group').removeClass('has-error').addClass('has-success');
//                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
					//alert(111);
		    	}
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var message = modal.find('input[name="message"]').val();
			var id=$("#id").val();
			var status = -1;
			var params = {id:id,status:status,remarks: message};
			isSending = true;
			var url = './lottery-user-withdraw/check-result';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						reload(id);
						modal.modal('hide');
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		var show = function() {
			form[0].reset();
			form.find('.help-inline').each(function() {
				if($(this).attr('data-default')) {
					$(this).html($(this).attr('data-default'));
				} else {
					$(this).empty();
				}
			});
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}
		var init = function() {
			initForm();
		}
		return {
			init: init,
			show: show
		}
	}();
	return {
		init: function() {
			loadData();
			ModifyShFailModal.init();
		}
	}
}();