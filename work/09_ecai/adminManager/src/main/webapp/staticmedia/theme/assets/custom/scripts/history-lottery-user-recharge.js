var LotteryUserRecharge = function() {
	var tableList = $('#table-user-recharge-list');
	
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
	
	var isLoading = false;
	var loadData = function() {
		if(isLoading) return;
		var url = './lottery-payment-channel/simple-list';
		isLoading = true;
		$.ajax({
			type : 'post',
			url : url,
			dataType : 'json',
			success : function(data) {
				isLoading = false;
				buildData(data);
			}
		});
	}
	
	var buildData = function(data) {
		tableList.find('select[name="subtype"]').find("option").remove();
		var lotteryPayType = tableList.find('select[name="subtype"]');
		lotteryPayType.append('<option value="">查看全部</option>');
		$.each(data.data, function(idx, val) {
			lotteryPayType.append('<option value="' + val.id + '">' + val.name + '</option>');
		});
		lotteryPayType.append('<option value="-1">系统充值(充值未到账)</option>');
	}
	
	var UserRechargeTable = function() {
		var tablePagelist = tableList.find('.page-list');
		
		var targetUser = Metronic.getURLParameter('username');
		if(targetUser) {
			tableList.find('input[name="username"]').val(targetUser);
		}
		
		var getSearchParams = function() {
			var billno = tableList.find('input[name="billno"]').val();
			var username = tableList.find('input[name="username"]').val();
			var minTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var maxTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			maxTime = getNetDate(maxTime);
			var minPayTime = tableList.find('[data-field="payTime"] > input[name="from"]').val();
			var maxPayTime = tableList.find('[data-field="payTime"] > input[name="to"]').val();
			maxPayTime = getNetDate(maxPayTime);
			var minMoney = tableList.find('input[name="minMoney"]').val();
			var maxMoney = tableList.find('input[name="maxMoney"]').val();
			var status = tableList.find('select[name="status"]').val();
			var type = tableList.find('select[name="subtype"]').attr("data-type");
			var subtype = tableList.find('select[name="subtype"]').val();
			return {billno: billno, username: username, minTime: minTime, maxTime: maxTime, minPayTime: minPayTime, maxPayTime: maxPayTime, minMoney: minMoney, maxMoney: maxMoney, status: status, type: type, subtype: subtype};
		}
		
		var download = function (){
			var p = getSearchParams();
			var url ='/lottery-user-recharge/download-bill?billno='+p.billno+"&username="+p.username+"&minTime="+p.minTime+"&maxTime="+p.maxTime+"&minMoney="+p.minMoney+"&maxMoney="+p.maxMoney+"&status="+p.status;
			window.open(url);
		}
		
		var pagination = $.pagination({
			render: tablePagelist,
			pageSize: 20,
			ajaxType: 'post',
			ajaxUrl: './history-lottery-user-recharge/list',
			ajaxData: getSearchParams,
			beforeSend: function() {
				
			},
			complete: function() {
				
			},
			success: function(list, rsp) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					var statusAction = '';

					if(val.bean.status == 0) {
						statusAction = '<a data-command="cancel" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 撤单 </a><a data-command="patch" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 补单 </a>';
					}
				
					var info = val.bean.infos;
					var budan=info.indexOf("充值漏单补单");
					var syscz=info.indexOf("系统补充值未到账");
					
					var bgColor = 'while';
					if(budan ==0){
						bgColor = '#6fa8dc';
					}else if(syscz == 0){
						bgColor = '#ea9999';
					}
					var typeName = DataFormat.formatUserRechargeType(val.bean.type, val.bean.subtype);
					if(val.bean.type !=3){
						if(val.name != ""){
							typeName = val.name;
						}
					}
					innerHtml += 
					'<tr class="align-center" style="background-color:'+bgColor+'" data-billno="'  + val.bean.billno + '" data-id="' + val.bean.id + '">'+
						'<td>' + val.bean.id + '</td>'+
						'<td><a href="./lottery-user-profile?username=' + val.username + '">' + val.username + '</a></td>'+
						'<td>' + val.bean.money.toFixed(1) + '</td>'+
						'<td>' + typeName + '</td>'+
						'<td>' + val.bean.time + '</td>'+
						'<td>' + val.bean.payTime + '</td>'+
						'<td>' + DataFormat.formatUserRechargeStatus(val.bean.status) + '</td>'+
						'<td>' + val.bean.billno + '</td>'+
						'<td>' + val.bean.remarks + '</td>'+
						// '<td>' + val.bean.realName + '</td>'+
						// '<td>' + (val.receiveCard === null ? "" : val.receiveCard.bean.cardId) + '</td>'+
						// '<td>' + val.bean.postscript + '</td>'+
						'<td><a data-command="details" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 详细 </a></td>'+
					'</tr>';
					
				});
				table.html(innerHtml);
				$("#totalRecharge").html(rsp.totalRecharge.toFixed(1));
				table.find('[data-command="details"]').unbind('click').click(function() {
					var id = $(this).parents('tr').attr('data-id');
					doLoadDetails(id);
				});
				table.find('[data-command="cancel"]').unbind('click').click(function() {
					var billno = $(this).parents('tr').attr('data-billno');
					var msg = '确定撤销该充值订单？撤消后可能会造成充值不到账！';
					bootbox.dialog({
						message: msg,
						title: '提示消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									doCancelOrder(billno);
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
				table.find('[data-command="patch"]').unbind('click').click(function() {
					var billno = $(this).parents('tr').attr('data-billno');
					PatchOrderModal.show(billno);
				});
			},
			pageError: function(response) {
				$("#totalRecharge").html("0.0");
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
				$("#totalRecharge").html("0.0");
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});
		
		var doCancelOrder = function(billno) {
			var params = {billno: billno};
			var url = './lottery-user-recharge/cancel';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						reload();
						toastr['success']('该订单已成功撤销！', '操作提示');
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
			var url = './history-lottery-user-recharge/get';
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
			var modal = $('#modal-user-recharge-details');
			var typeName = DataFormat.formatUserRechargeType(data.bean.type, data.bean.subtype);
			if(data.bean.type !=3){
				if(data.name != ""){
					typeName = data.name;
				}
			}
			modal.find('[data-field="id"]').html(data.bean.id);
			modal.find('[data-field="billno"]').html(data.bean.billno);
			modal.find('[data-field="username"]').html(data.username);
			modal.find('[data-field="money"]').html(data.bean.money.toFixed(1));
			modal.find('[data-field="time"]').html(data.bean.time);
			modal.find('[data-field="status"]').html(DataFormat.formatUserRechargeStatus(data.bean.status));
			modal.find('[data-field="type"]').html(typeName);
			modal.find('[data-field="payBillno"]').html(data.bean.payBillno);
			modal.find('[data-field="payTime"]').html(data.bean.payTime);
			modal.find('[data-field="infos"]').html(data.bean.infos);
			modal.find('[data-field="remarks"]').html(data.bean.remarks);
			modal.find('[data-field="realName"]').html(data.bean.realName);
			modal.find('[data-field="cardId"]').html(data.receiveCard == null ? '' : data.receiveCard.bean.cardId);
			modal.find('[data-field="postscript"]').html(data.bean.postscript);
			modal.modal('show');
		}
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			init();
		});
		tableList.find('[data-command="download"]').unbind('click').click(function() {
			download();
		});

		var init = function() {
			pagination.init();
		}
		
		var reload = function() {
			pagination.reload();
		}
		
		return {
			init: init,
			reload: reload
		}
	}();
	
	var PatchOrderModal = function() {
		var modal = $('#modal-patch-order');
		var form = modal.find('form');
		var initForm = function() {
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				doSubmit();
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var billno = modal.find('input[name="billno"]').val();
			var paybillno = modal.find('input[name="paybillno"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();
			var token = $.getDisposableToken();
			withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, token);
			var params = {billno: billno, paybillno: paybillno, remarks: remarks, withdrawPwd: withdrawPwd};
			var url = './lottery-user-recharge/patch';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						modal.modal('hide');
						UserRechargeTable.reload();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				},
				complete: function() {
					isSending = false;
					modal.modal('hide');
				},
				error: function() {
					isSending = false;
					modal.modal('hide');
					toastr['error']('服务异常！请刷新后再试！', '操作提示');
				}
			});
		}
		
		var show = function(data) {
			if(!data) return;
			form[0].reset();
			form.find('.help-inline').each(function() {
				if($(this).attr('data-default')) {
					$(this).html($(this).attr('data-default'));
				} else {
					$(this).empty();
				}
			});
			form.find('input[name="billno"]').val(data);
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
			var today = moment().format('YYYY-MM-DD');
			var tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');
			tableList.find('[data-field="payTime"] > input[name="from"]').val(today);
			tableList.find('[data-field="payTime"] > input[name="to"]').val(tomorrow);
			loadData();
			UserRechargeTable.init();
			PatchOrderModal.init();
			handleSelect();
			handelDatePicker();
		}
	}
}();