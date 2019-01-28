var LotteryUserProfile = function() {
	
	var username = Metronic.getURLParameter('username');

	var UserProfileTable = function() {
		var loadData = function() {
			if(!username) return;
			var url = './lottery-user-profile/get';
			var params = {username: username};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						buildUserInfo(data.UserProfile, data.uInfo, data.CardList, data.SecurityList, data.PTAccount, data.AGAccount);
						// buildPlanInfo(data.UserPlanInfo);
						buildCardInfo(data.CardList);
						// buildUserQuota(data.UserProfile.bean, data.QuotaList);
					}
				}
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
		
		var basic = $('#basic_information');
		var buildUserInfo = function(data, uInfo, clist, slist, ptAccount, agAccount) {
			basic.find('[data-field="id"]').html(data.bean.id);
			basic.find('[data-field="username"]').html(data.bean.username);
			basic.find('[data-field="nickname"]').html(data.bean.nickname);
			var type = DataFormat.formatUserType(data.bean.type);
			if(type == '玩家' && data.bean.nickname == '试玩用户'){
				basic.find('[data-field="type"]').html("试玩用户");
			}else{
				basic.find('[data-field="type"]').html(DataFormat.formatUserType(data.bean.type));
			}
			
			if(data.bean.type == 2) {
				basic.find('[data-command="change-proxy"]').removeClass("disabled");
			}

			basic.find('[data-field="totalMoney"]').html(data.bean.totalMoney.toFixed(3));
			basic.find('[data-field="lotteryMoney"]').html(data.bean.lotteryMoney.toFixed(3));
			// basic.find('[data-field="baccaratMoney"]').html(data.bean.baccaratMoney.toFixed(3));
			basic.find('[data-field="freezeMoney"]').html(data.bean.freezeMoney.toFixed(3));

			basic.find('[data-field="ptUsername"]').html(ptAccount ? ptAccount.username : "从未游戏过");
			basic.find('[data-field="agUsername"]').html(agAccount ? agAccount.username : "从未游戏过");

			basic.find('[data-field="code"]').html(data.bean.code);
			basic.find('[data-field="locatePoint"]').html(data.bean.locatePoint.toFixed(2) + '%');
			basic.find('[data-field="notLocatePoint"]').html(data.bean.notLocatePoint.toFixed(2) + '%');
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
		
			basic.find('[data-field="withdrawName"]').html(data.bean.withdrawName == '' ? '未绑定' : data.bean.withdrawName);
			basic.find('[data-field="withdrawPassword"]').html(data.bean.withdrawPassword == '' ? '未设定' : '已设定');
			basic.find('[data-field="bindCard"]').html(clist.length + '张');
			basic.find('[data-field="bindSecurity"]').html(slist.length > 0 ? '已设定' : '未设定');
			
			basic.find('[data-field="bindEmail"]').html(uInfo ? uInfo.email : '未绑定');
			
			if(slist.length > 0) {
				basic.find('[data-field="securityQ1"]').html(slist[0].bean.key);
				basic.find('[data-field="securityQ2"]').html(slist[1].bean.key);
				basic.find('[data-field="securityQ3"]').html(slist[2].bean.key);
			}
			
			basic.find('[data-field="registTime"]').html(data.bean.registTime);
			basic.find('[data-field="loginTime"]').html(data.bean.loginTime == '' ? '从未登录过' : data.bean.loginTime);
			basic.find('[data-field="AStatus"]').html(DataFormat.formatUserAStatus(data.bean.AStatus));
			basic.find('[data-field="BStatus"]').html(DataFormat.formatUserBStatus(data.bean.BStatus));
			basic.find('[data-field="vipLevel"]').html(DataFormat.formatUserVipLevel(data.bean.vipLevel));
			basic.find('[data-field="integral"]').html(data.bean.integral.toFixed(3));
			basic.find('[data-field="isBindGoogle"]').html(data.bean.isBindGoogle == 1 ? "已绑定" : "未绑定");
			if (data.bean.isBindGoogle == 1) {
				basic.find('[data-command="unbind-google"]').removeClass("disabled");
			}
			var formatRelatedUpper = '';
			if (data.relatedUpUser) {
				formatRelatedUpper += ' <a href="./lottery-user-profile?username=' + data.relatedUpUser + '">' + data.relatedUpUser + '</a>';
				formatRelatedUpper += '&nbsp;+返点' + (data.bean.relatedPoint * 100) + '%';
			}
			else {
				formatRelatedUpper = '无';
			}
			basic.find('[data-field="relatedUpper"]').html(formatRelatedUpper);

			var formatRelatedUsers = '';
			if (data.bean.type == 3) {
				basic.find('[data-command="modify-related-users"]').removeClass("disabled").removeAttr("disabled");
				$.each(data.relatedUsers, function(idx, val) {
					formatRelatedUsers += ' <a href="./lottery-user-profile?username=' + val + '">' + val + '</a>';
				});
			}
			else {
				formatRelatedUsers = '无';
			}
			basic.find('[data-field="relatedUsers"]').html(formatRelatedUsers);

			if(data.zhaoShang) {
				if (data.bean.isCjZhaoShang == 0) {
					basic.find('[data-command="change-zhaoshang"]').removeClass("disabled").html('<i class="fa fa-edit"></i> 转为超级招商');
				}
				else {
					basic.find('[data-command="change-zhaoshang"]').removeClass("disabled").html('<i class="fa fa-edit"></i> 转为招商');
				}
			}
			
			basic.find('[data-field="message"]').html(data.bean.message);
			basic.find('[data-field="onlineStatus"]').html(data.bean.onlineStatus == 1 ? '在线' : '离线');
			basic.find('[data-field="allowEqualCode"]').html(data.bean.allowEqualCode == 1 ? '开启' : '关闭');
			basic.find('[data-field="allowTransfers"]').html(data.bean.allowTransfers == 1 ? '开启' : '关闭');
			//取款
			basic.find('[data-field="allowWithdraw"]').html(data.bean.allowWithdraw == 1 ? '开启' : '关闭');
			//转账
			basic.find('[data-field="allowPlatformTransfers"]').html(data.bean.allowPlatformTransfers == 1 ? '开启' : '关闭');
			basic.find('[data-field="lockTime"]').html(formatLockTime(data.bean.lockTime));
			if (data.bean.lockTime) {
				basic.find('[data-command="reset-lock-time"]').removeClass("disabled");
			}
			
			if(data.bean.AStatus < 0) {
				basic.find('[data-command="AStatus"]').attr('data-status', data.bean.AStatus).html('<i class="fa fa-check"></i> 恢复正常');
			} else {
				basic.find('[data-command="AStatus"]').attr('data-status', data.bean.AStatus).html('<i class="fa fa-ban"></i> 冻结账号');
			}
			basic.find('[data-command="BStatus"]').attr('data-astatus', data.bean.AStatus).attr('data-bstatus', data.bean.BStatus);
			basic.find('[data-command="recover"]').attr('data-status', data.bean.AStatus);
			
			if(data.bean.allowEqualCode < 0) {
				basic.find('[data-command="allow-equal-code"]').attr('data-value', data.bean.allowEqualCode).html('<i class="fa fa-check"></i> 开启同级开号');
			} else {
				basic.find('[data-command="allow-equal-code"]').attr('data-value', data.bean.allowEqualCode).html('<i class="fa fa-ban"></i> 关闭同级开号');
			}
			
			if(data.bean.allowTransfers < 0) {
				basic.find('[data-command="allow-transfers"]').attr('data-value', data.bean.allowTransfers).html('<i class="fa fa-check"></i> 开启上下级转账');
			} else {
				basic.find('[data-command="allow-transfers"]').attr('data-value', data.bean.allowTransfers).html('<i class="fa fa-ban"></i> 关闭上下级转账');
			}
			//取款
			if(data.bean.allowWithdraw < 0) {
				basic.find('[data-command="allow-withdraw"]').attr('data-value', data.bean.allowWithdraw).html('<i class="fa fa-check"></i> 开启取款');
			} else {
				basic.find('[data-command="allow-withdraw"]').attr('data-value', data.bean.allowWithdraw).html('<i class="fa fa-ban"></i> 关闭取款');
			}
			//转账
			if(data.bean.allowPlatformTransfers < 0) {
				basic.find('[data-command="allow-platform-transfers"]').attr('data-value', data.bean.allowPlatformTransfers).html('<i class="fa fa-check"></i> 开启转账');
			} else {
				basic.find('[data-command="allow-platform-transfers"]').attr('data-value', data.bean.allowPlatformTransfers).html('<i class="fa fa-ban"></i> 关闭转账');
			}

			// 总账号不允许操作
			if (data.bean.id != 72) {
				basic.find('[data-command="lock-team"]').removeAttr("disabled");
				basic.find('[data-command="un-lock-team"]').removeAttr("disabled");
			}

			basic.on('click', 'a[data-method=checkPTBalance]', function(){
				var $td = $("td[data-field=ptMoney]");
				$td.html("<span class='loadImg'/>");
				loadGameBalance(11, data.bean.id, $td);
			});
			basic.on('click', 'a[data-method=checkAGBalance]', function(){
				var $td = $("td[data-field=agMoney]");
				$td.html("<span class='loadImg'/>");
				loadGameBalance(4, data.bean.id, $td);
			});

			basic.find('[data-command="modify-related-upper"]').unbind('click').click(function() {
				RelatedUpperModifyModal.show(username, data.relatedUpUser, data.bean.relatedPoint);
			});

			basic.find('[data-command="modify-related-users"]').unbind('click').click(function() {
				RelatedUserModifyModal.show(username, data.relatedUsers);
			});

			basic.find('[data-command="change-zhaoshang"]').unbind('click').click(function() {
				confirmChangeZhaoShang(data.bean.isCjZhaoShang);
			});
		}
		
		var loadGameBalance = function(platformId, userId, $td) {
			$.ajax({
				type : 'post',
				url : './game/balance',
				data : {userId: userId, platformId: platformId},
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						$td.html(data.data.balance.toFixed(3));
					}
				}
			});
		}
		
		var buildPlanInfo = function(data) {
			if(null == data) return;
			var planInfo = $('#plan-info');
			var table = planInfo.find('table > tbody').empty();
			var innerHtml = 
			'<tr class="align-center">'+
				'<td>' + DataFormat.formatUserPlanLevel(data.level) + '</td>'+
				'<td>' + data.planCount + '</td>'+
				'<td>' + data.prizeCount + '</td>'+
				'<td>' + data.totalMoney.toFixed(3) + '</td>'+
				'<td>' + data.totalPrize.toFixed(3) + '</td>'+
			'</tr>';
			table.html(innerHtml);
		}
		
		var buildCardInfo = function(data) {
			if(null == data) return;
			var card = $('#card_information');
			var table = card.find('table > tbody').empty();
			var innerHtml = '';
			$.each(data, function(idx, val) {
				var statusAction = '';
				if(val.bean.status == 0) {
					statusAction = '<a data-command="status" data-status="-1" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-times"></i> 无效</a><a data-command="status" data-status="-2" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-lock"></i> 锁定</a>';
				}
				if(val.bean.status == -1) {
					statusAction = '<a data-command="status" data-status="0" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 资料通过</a>';
				}
				if(val.bean.status == -2) {
					statusAction = '<a data-command="status" data-status="0" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-unlock"></i> 解锁</a>';
				}
				var bankBranch = val.bean.bankBranch != '' ? val.bean.bankBranch : '-';
				var lockTime = val.bean.lockTime != '' ? val.bean.lockTime : '-';
				innerHtml +=
				'<tr class="align-center" data-id="' + val.bean.id + '">'+
					'<td>' + (idx + 1) + '</td>'+
					'<td>' + val.username + '</td>'+
					'<td>' + val.bankName + '</td>'+
					'<td>' + bankBranch + '</td>'+
					'<td>' + val.bean.cardName + '</td>'+
					'<td>' + val.bean.cardId + '</td>'+
					'<td>' + DataFormat.formatUserCardStatus(val.bean.status) + '</td>'+
					'<td>' + lockTime + '</td>'+
					'<td>' + statusAction + '<a data-command="edit" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-edit"></i> 编辑 </a></td>'+
				'</tr>';
			});
			table.html(innerHtml);
			table.find('[data-command="edit"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				doLoadCard(id, 'edit');
			});
			
			table.find('[data-command="check"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				doLoadCard(id, 'check');
			});
			
			card.find('[data-command="add"]').unbind('click').click(function() {
				EditLotteryUserCardModal.show('add', username);
			});
			
			var doLoadCard = function(id, action) {
				var url = './lottery-user-card/get';
				var params = {id: id};
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						if(data.error == 0) {
							EditLotteryUserCardModal.show(action, data.data);
						}
						if(data.error == 1 || data.error == 2) {
							toastr['error']('操作失败！' + data.message, '操作提示');
						}
					}
				});
			}
			
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定要修改卡片状态？';
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								updateStatus(id, status);
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
		}
		
		var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './lottery-user-card/lock-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						loadData();
						toastr['success']('操作成功，卡片状态已改变！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var confirmResetSecurity  = function() {
			var msg = '确定重置密保问题？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							resetSecurity();
						}
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
					}
				}
			});
		}
		
		var resetSecurity = function() {
			var params = {username: username};
			var url = './lottery-user-security/reset';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						init();
						toastr['success']('密保重置成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('密保重置失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var confirmChangeZhaoShang  = function(isCJZhaoShang) {
			var msg;
			if (isCJZhaoShang == 1) {
				msg = '确定要将用户转为招商等级？';
			}
			else {
				msg = '确定要将用户转为超级招商等级？';
			}

			msg += '（系统将自动替换相应待遇）';
			
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							changeZhaoShang(isCJZhaoShang);
						}
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
					}
				}
			});
		}
		
		var changeZhaoShang = function(isCJZhaoShang) {
			var params = {username: username, isCJZhaoShang: isCJZhaoShang};
			var url = './lottery-user/change-zhaoshang';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						init();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var quota = $('#user_quota');
		var buildUserQuota = function(uBean, uQuota) {
			var thisTable = quota.find('table');
			quota.find('tbody').empty();
			
			if(uBean.type == 2) {
				thisTable.find('tbody').append('<tr><td colspan="20">该用户为玩家，无法分配配额。</td></tr>');
				return false;
			}
			
			$.each(uQuota, function(i, val) {
				var maxLocatePoint = uBean.locatePoint;
				if(uBean.allowEqualCode != 1) {
					maxLocatePoint = maxLocatePoint - 0.1;
				}
				if(maxLocatePoint >= val.minPoint) {
					var maxPoint = val.maxPoint > maxLocatePoint ? maxLocatePoint : val.maxPoint;
					var innerHtml = 
					'<tr class="align-center">'+
						'<td>' + val.minPoint.toFixed(1) + ' ~ ' + maxPoint.toFixed(1) + '</td>'+
						'<td><input name="count' + val.countIndex + '" type="text" class="form-control input-xsmall input-inline input-sm align-center" value="' + val.totalCount + '"></td>'+
						'<td>' + (val.totalCount - val.surplusCount) + '</td>'+
						'<td>' + val. surplusCount + '</td>'+
					'</tr>';
					thisTable.find('tbody').append(innerHtml);
				}
			});
		}
		
		quota.find('[data-command="save"]').unbind('click').click(function() {
			var count1 = quota.find('input[name="count1"]').val();
			count1 = count1 ? count1 : 0;
			var count2 = quota.find('input[name="count2"]').val();
			count2 = count2 ? count2 : 0;
			var count3 = quota.find('input[name="count3"]').val();
			count3 = count3 ? count3 : 0;
			modifyUserQuota(count1, count2, count3);
		});
		
		var modifyUserQuota = function(count1, count2, count3) {
			var params = {username: username, count1: count1, count2: count2, count3: count3};
			var url = './lottery-user/modify-quota';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						init();
						toastr['success']('配额修改成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('配额修改失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		// 下面开始按钮操作
		basic.find('[data-command="login-password"]').unbind('click').click(function() {
			ModifyPasswordModal.show('login-password', '修改登录密码');
		});
		
		basic.find('[data-command="withdraw-password"]').unbind('click').click(function() {
			ModifyPasswordModal.show('withdraw-password', '修改资金密码');
		});
		
		basic.find('[data-command="withdraw-name"]').unbind('click').click(function() {
			ModifyWithdrawNameModal.show();
		});
		
		basic.find('[data-command="reset-email"]').unbind('click').click(function() {
			var msg = '确认重置邮箱？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							resetEmail();
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

		var resetEmail = function() {
			var params = {username: username};
			var url = './lottery-user/reset-email';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						init();
						toastr['success']('邮箱重置成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('邮箱重置失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		basic.find('[data-command="modify-email"]').unbind('click').click(function() {
			ModifyEmailModal.show();
		});
		
		basic.find('[data-command="modify-xiaofei"]').unbind('click').click(function() {
			var msg = '清空会员提款消费量？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							resetXiaofei();
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
		
		var  resetXiaofei = function() {
			var params = {username: username};
			var url = './lottery-user/reset-user-xiaofei';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserWithdrawLimitTable.init();
						toastr['success']('重置成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('重置失败！' + data.message, '操作提示');
					}
				}
			});
		};
		
		basic.find('[data-command="image-password"]').unbind('click').click(function() {
			var msg = '确认重置图案密码？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							resetImagePwd();
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
		
		var resetImagePwd = function() {
			var params = {username: username};
			var url = './lottery-user/reset-image-pwd';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						init();
						toastr['success']('图案密码重置成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('图案密码重置失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		basic.find('[data-command="modify-point"]').unbind('click').click(function() {
			var params = {username: username};
			var url = './lottery-user/get-point-info';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					ModifyPointModal.show(data);
				}
			});
		});
		
		basic.find('[data-command="down-point"]').unbind('click').click(function() {
			var msg = '统一降点后，该代理以及其所有下级将统一降低返点0.1，号级别统一降低1个级别。该功能系统将自动删除用户团队所有契约配置(不可恢复),请谨慎操作！';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							downPoint();
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
		
		var downPoint = function() {
			var params = {username: username};
			var url = './lottery-user/down-point';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						init();
						toastr['success']('统一降点成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('统一降点失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		basic.find('[data-command="extra-point"]').unbind('click').click(function() {
			var params = {username: username};
			var url = './lottery-user/get-point-info';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					EditExtraPointModal.show(data.uBean);
				}
			});
		});
		
		basic.find('[data-command="AStatus"]').unbind('click').click(function() {
			var status = $(this).attr('data-status');
			if(status < 0) {
				if(status == -2) {
					toastr['error']('该账户已经被永久冻结，无法恢复正常！', '操作提示');
				} else {
					confirmUnlock(username);
				}
			} else {
				LockLotteryUserModal.show(username);
			}
		});
		
		var confirmUnlock = function(username) {
			var msg = '确定恢复该用户至正常状态？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							unlockUser(username);
						}
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
					}
				}
			});
		}
		
		var unlockUser = function(username) {
			var params = {username: username};
			var url = './lottery-user/unlock';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('该账号已经恢复正常！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		basic.find('[data-command="BStatus"]').unbind('click').click(function() {
			var astatus = $(this).attr('data-astatus');
			var bstatus = $(this).attr('data-bstatus');
			if(astatus < 0) {
				toastr['error']('该账号不是正常状态，请先恢复至正常！', '操作提示');
				return;
			}
			SetUserBetsStatusModal.show(bstatus);
		});
		
		basic.find('[data-command="reset-security"]').unbind('click').click(function() {
			confirmResetSecurity();
		});
		
		basic.find('[data-command="recharge"]').unbind('click').click(function() {
			RechargeModal.show();
		});
		
		basic.find('[data-command="recover"]').unbind('click').click(function() {
			var msg = '确定要回收该用户？（该操作无法恢复）';
			var status = $(this).attr('data-status');
			if(status < 0) {
				toastr['error']('该账号不是正常状态，请先恢复至正常！', '操作提示');
				return;
			}
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							recoverUser(username);
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
		
		var recoverUser = function() {
			var params = {username: username};
			var url = './lottery-user/recover';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('该账号已被成功被系统回收！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		basic.find('[data-command="allow-equal-code"]').unbind('click').click(function() {
			var value = $(this).attr('data-value');
			var msg = '确定要开启同级开号权限？';
			if(value > 0) {
				msg = '确定要关闭同级开号权限？'
			}
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							if(value > 0) {
								updateAllowEqualCode(-1);
							} else {
								updateAllowEqualCode(1);
							}
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
		
		var updateAllowEqualCode = function(status) {
			var params = {username: username, status: status};
			var url = './lottery-user/modify-equal-code';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		basic.find('[data-command="allow-transfers"]').unbind('click').click(function() {
			var value = $(this).attr('data-value');
			var msg = '确定要开启上下级转账权限？';
			if(value > 0) {
				msg = '确定要关闭上下级转账权限？'
			}
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							if(value > 0) {
								updateAllowTransfers(-1);
							} else {
								updateAllowTransfers(1);
							}
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
		
		basic.find('[data-command="allow-withdraw"]').unbind('click').click(function() {
			var value = $(this).attr('data-value');
			var msg = '确定要开启取款权限？';
			if(value > 0) {
				msg = '确定要关闭取款权限？'
			}
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							if(value > 0) {
								updateAllowWithdraw(-1);
							} else {
								updateAllowWithdraw(1);
							}
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
		
		basic.find('[data-command="allow-platform-transfers"]').unbind('click').click(function() {
			var value = $(this).attr('data-value');
			var msg = '确定要开启转账权限？';
			if(value > 0) {
				msg = '确定要关闭转账权限？'
			}
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							if(value > 0) {
								updateAllowPlatformTransfers(-1);
							} else {
								updateAllowPlatformTransfers(1);
							}
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
		
		
		basic.find('[data-command="allow-team-transfers"]').unbind('click').click(function() {
			var msg = '确定要开启团队所有成员上下级转账权限？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							allowTeamTransfers();
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
		
		basic.find('[data-command="prohibit-team-transfers"]').unbind('click').click(function() {
			var msg = '确定要关闭团队所有成员上下级转账权限？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							prohibitTeamTransfers();
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
		
		basic.find('[data-command="allow-team-platform-transfers"]').unbind('click').click(function() {
			var msg = '确定要开启团队所有成员转账权限？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							allowTeamPlatformTransfers();
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
		
		basic.find('[data-command="prohibit-team-platform-transfers"]').unbind('click').click(function() {
			var msg = '确定要关闭团队所有成员转账权限？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							prohibitTeamPlatformTransfers();
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
		
		basic.find('[data-command="allow-team-withdraw"]').unbind('click').click(function() {
			var msg = '确定要开启团队所有成员取款权限？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							allowTeamWithdraw();
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
		
		basic.find('[data-command="prohibit-team-withdraw"]').unbind('click').click(function() {
			var msg = '确定要关闭团队所有成员取款权限？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							prohibitTeamWithdraw();
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
		
		var updateAllowTransfers = function(status) {
			var params = {username: username, status: status};
			var url = './lottery-user/modify-transfers';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		//开启团队上下级转账
		var allowTeamTransfers = function() {
			var params = {username: username, status: status};
			var url = './lottery-user/allow-team-transfers';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('该账号团队上下级转账权限已开启！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		//关闭团队上下级转账
		var prohibitTeamTransfers = function(status) {
			var params = {username: username, status: status};
			var url = './lottery-user/prohibit-team-transfers';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		//开启团队上下级转账
		var allowTeamTransfers = function() {
			var params = {username: username, status: status};
			var url = './lottery-user/allow-team-transfers';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('该团队所有成员上下级转账权限已开启！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		//关闭团队上下级转账
		var prohibitTeamTransfers = function(status) {
			var params = {username: username, status: status};
			var url = './lottery-user/prohibit-team-transfers';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('该团队所有成员上下级转账权限已关闭！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		//开启团队转账
		var allowTeamPlatformTransfers = function() {
			var params = {username: username, status: status};
			var url = './lottery-user/allow-team-platform-transfers';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('该团队所有成员转账权限已开启！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		//关闭团队转账
		var prohibitTeamPlatformTransfers = function(status) {
			var params = {username: username, status: status};
			var url = './lottery-user/prohibit-team-platform-transfers';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('该团队所有成员转账权限已关闭！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var updateAllowWithdraw = function(status) {
			var params = {username: username, status: status};
			var url = './lottery-user/modify-withdraw';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var updateAllowPlatformTransfers = function(status) {
			var params = {username: username, status: status};
			var url = './lottery-user/modify-platform-transfers';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		//开启团队取款
		var allowTeamWithdraw = function(status) {
			var params = {username: username, status: status};
			var url = './lottery-user/allow-team-withdraw';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('该团队所有成员取款权限已开启！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		//禁止团队取款
		var prohibitTeamWithdraw = function() {
			var params = {username: username};
			var url = './lottery-user/prohibit-team-withdraw';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('该团队所有成员取款权限已关闭！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		basic.find('[data-command="change-proxy"]').unbind('click').click(function() {
			var msg = '确定要把改用户转换为代理？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doChangeProxy();
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

		var doChangeProxy = function() {
			var params = {username: username};
			var url = './lottery-user/change-proxy';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('该账户已成功转为代理用户！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		basic.find('[data-command="unbind-google"]').unbind('click').click(function() {
			var msg = '确定要为用户解绑谷歌身份验证器？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doUnbindGoogle();
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

		var doUnbindGoogle = function() {
			var params = {username: username};
			var url = './lottery-user/unbind-google';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						basic.find('[data-command="unbind-google"]').addClass("disabled");
						toastr['success']('已成功为用户解绑谷歌身份验证器！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		basic.find('[data-command="reset-lock-time"]').unbind('click').click(function() {
			var msg = '确定要为用户清空账户时间锁？';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doResetLockTime();
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

		var doResetLockTime = function() {
			var params = {username: username};
			var url = './lottery-user/reset-lock-time';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						UserProfileTable.init();
						basic.find('[data-command="reset-lock-time"]').addClass("disabled");
						toastr['success']('已成功为用户清空账户时间锁！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		// *******************冻结及解冻团队****************************
		basic.find('[data-command="lock-team"]').unbind('click').click(function() {
			bootbox.prompt({
				size: "small",
				inputType: 'text',
				title: "请输入备注",
				callback: function(remark){
					if (!remark) {
						return;
					}

					var msg = '<strong>确定要冻结该团队？(会员自己也会被冻结，该操作不会影响分红及日结)</strong>';
					bootbox.dialog({
						message: msg,
						title: '确认消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									doLockTeam(remark);
								}
							},
							danger: {
								label: '<i class="fa fa-undo"></i> 取消',
								className: 'btn-danger',
								callback: function() {}
							}
						}
					});
				}
			})
		});

		var doLockTeam = function(remark) {
			var params = {username: username, remark: remark};
			var url = './lottery-user/lock-team';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('已成功将该团队所有成员冻结！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}

		basic.find('[data-command="un-lock-team"]').unbind('click').click(function() {
			bootbox.prompt({
				size: "small",
				inputType: 'text',
				title: "请输入备注",
				callback: function(remark){
					if (!remark) {
						return;
					}

					var msg = '<strong>确定要解冻该团队？</strong>';
					bootbox.dialog({
						message: msg,
						title: '确认消息',
						buttons: {
							success: {
								label: '<i class="fa fa-check"></i> 确定',
								className: 'green-meadow',
								callback: function() {
									doUnLockTeam(remark);
								}
							},
							danger: {
								label: '<i class="fa fa-undo"></i> 取消',
								className: 'btn-danger',
								callback: function() {}
							}
						}
					});
				}
			})
		});

		var doUnLockTeam = function(remark) {
			var params = {username: username, remark: remark};
			var url = './lottery-user/un-lock-team';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						UserProfileTable.init();
						toastr['success']('已成功将该团队所有成员解冻！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		// *******************冻结及解冻团队****************************

		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	var UserLoginInfo = function() {
		var tableList = $('#login_logs');
		var tablePagelist = tableList.find('.page-list');
		var pagination = $.pagination({
			render: tablePagelist,
			pageLength: 3,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './lottery-user-login-log/list',
			ajaxData: {username: username},
			beforeSend: function() {},
			complete: function() {},
			success: function(list) {
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + val.time + '</td>'+
						'<td>' + val.ip + '</td>'+
						'<td>' + val.address + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
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
		
		
		var init = function() {
			pagination.init();
		}
		
		return {
			init: init
		}
		
	}();
	var UserUpdateLogs = function() {
		var $tab = $('#critical-log-tab');
		var tableList = $('#user_update_logs');
		var tablePagelist = tableList.find('.page-list');
		var pagination = $.pagination({
			render: tablePagelist,
			pageLength: 3,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './admin-user-critical-log/list',
			ajaxData: {username: username},
			beforeSend: function() {},
			complete: function() {},
			success: function(list) {
				$tab.show();
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					innerHtml +=
						'<tr class="align-center">'+
						'<td>' + val.bean.action + '</td>'+
						'<td>' + val.adminUsername + '</td>'+
						'<td>' + val.bean.ip + '</td>'+
						'<td>' + val.bean.address + '</td>'+
						'<td>' + val.bean.time + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
			},
			pageError: function(response) {
				$tab.hide();
			},
			emptyData: function() {
				$tab.show();
				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});
		
		
		var init = function() {
			pagination.init();
		}
		
		return {
			init: init
		}
		
	}();
	var EditLotteryUserCardModal = function() {
		var modal = $('#modal-lottery-user-card-edit');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					username: {
						required: true
					},
					cardName: {
						required: true
					},
					cardId: {
						required: true
					}
				},
				messages: {
					username: {
						required: '用户名不能为空！'
					},
					cardName: {
	                    required: '姓名不能为空！'
	                },
	                cardId: {
	                    required: '卡号不能为空！'
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
                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var action = modal.attr('data-action');
			var params = {};
			var url = '';
			if(action == 'edit') {
				url = './lottery-user-card/edit';
				var id = modal.attr('data-id');
				var bankId = form.find('select[name="bank"]').val();
				var bankBranch = form.find('input[name="bankBranch"]').val();
				var cardName = form.find('input[name="cardName"]').val();
				var cardId = form.find('input[name="cardId"]').val();
				params = {id: id, bankId: bankId, bankBranch: bankBranch, cardName: cardName, cardId: cardId};
			}
			if(action == 'add') {
				url = './lottery-user-card/add';
				var username = form.find('input[name="username"]').val();
				var bankId = form.find('select[name="bank"]').val();
				var bankBranch = form.find('input[name="bankBranch"]').val();
				var cardName = form.find('input[name="cardName"]').val();
				var cardId = form.find('input[name="cardId"]').val();
				var status = form.find('input[name="status"]:checked').val();
				params = {username: username, bankId: bankId, bankBranch: bankBranch, cardName: cardName, cardId: cardId, status: status};
			}
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
						UserProfileTable.init();
						UserUpdateLogs.init();
						if(action == 'edit') {
							toastr['success']('银行卡修改成功！', '操作提示');
						}
						if(action == 'add') {
							toastr['success']('银行卡添加完成！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						if(action == 'edit') {
							toastr['error']('银行卡修改失败！' + data.message, '操作提示');
						}
						if(action == 'add') {
							toastr['error']('银行卡添加失败！' + data.message, '操作提示');
						}
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
		
		var loadBank = function() {
			var url = './lottery-payment-bank/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					buildBank(data);
				}
			});
		}
		
		var buildBank = function(data) {
			var bank = form.find('select[name="bank"]');
			bank.empty();
			$.each(data, function(idx, val) {
				bank.append('<option value="' + val.id + '">' + val.name + '</option>');
			});
		}
		
		var show = function(action, data) {
			form[0].reset();
			if('edit' == action) {
				modal.attr('data-action', 'edit');
				modal.attr('data-id', data.bean.id);
				modal.find('.modal-title').html('编辑银行卡');
				form.find('input[name="username"]').val(data.username);
				form.find('select[name="bank"]').removeAttr('disabled').find('option[value="' + data.bean.bankId + '"]').attr('selected', true);
				form.find('input[name="bankBranch"]').val(data.bean.bankBranch).removeAttr('disabled');
				form.find('input[name="cardName"]').val(data.bean.cardName).removeAttr('disabled');
				form.find('input[name="cardId"]').val(data.bean.cardId).removeAttr('disabled');
				form.find('input[name="status"][value="' + data.bean.status + '"]').attr('checked', true);
				Metronic.initAjax();
			}
			if('add' == action) {
				modal.attr('data-action', 'add');
				modal.removeAttr('data-id');
				modal.find('.modal-title').html('添加银行卡');
				if(data) {
					form.find('input[name="username"]').attr('disabled', true).val(data);
				} else {
					form.find('input[name="username"]').removeAttr('disabled');
				}
				form.find('select[name="bank"]').removeAttr('disabled').find('option').eq(0).attr('selected', true);
				form.find('input[name="bankBranch"]').removeAttr('disabled');
				form.find('input[name="cardName"]').removeAttr('disabled');
				form.find('input[name="cardId"]').removeAttr('disabled');
				form.find('input[name="status"]').eq(0).attr('checked', true);
				Metronic.initAjax();
			}
			form.find('.help-inline').empty();
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}
		
		var init = function() {
			initForm();
			loadBank();
		}
		
		return {
			init: init,
			show: show
		}
		
	}();
	
	var ModifyPasswordModal = function() {
		var modal = $('#modal-password-modify');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					password1: {
						required: true,
						minlength: 6
					},
					password2: {
						required: true,
						minlength: 6,
						equalTo: 'input[name="password1"]'
					}
				},
				messages: {
					password1: {
						required: '用户密码不能为空！',
						minlength: '至少输入{0}个字符',
					},
					password2: {
						required: '重复密码不能为空！',
						equalTo: '两次密码不一致！',
						minlength: '至少输入{0}个字符',
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
                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var action = modal.attr('data-action');
			var username = form.find('input[name="username"]').val();
			var password = form.find('input[name="password1"]').val();
			password = $.generatePassword(password);
			var params = {username: username, password: password};
			var url = '';
			if(action == 'login-password') {
				url = './lottery-user/modify-login-pwd';
			}
			if(action == 'withdraw-password') {
				url = './lottery-user/modify-withdraw-pwd';
			}
			if(url == '') return;
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
						UserProfileTable.init();
						toastr['success']('修改密码成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('修改密码失败！' + data.message, '操作提示');
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
		
		var show = function(action, title) {
			form[0].reset();
			if(action) {
				modal.attr('data-action', action);
				modal.find('.modal-title').html(title);
				form.find('input[name="username"]').val(username);
				
				form.find('.help-inline').empty();
				form.find('.has-error').removeClass('has-error');
				form.find('.has-success').removeClass('has-success');
				modal.modal('show');
			}
		}
		
		var init = function() {
			initForm();
		}
		
		return {
			init: init,
			show: show
		}
		
	}();
	
	var ModifyPointModal = function() {
		var modal = $('#modal-point-modify');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					locatePoint: {
						required: true,
						number: true,
						min: 0
					}
				},
				messages: {
					locatePoint: {
						required: '返点不能为空！',
						number: '返点必须为数字！',
						min: '返点不能小于0！'
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
                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			var msg = '该功能系统将自动删除用户团队所有契约配置(不可恢复),请谨慎操作！';
			bootbox.dialog({
				message: msg,
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {

							if(isSending) return;
							var username = form.find('input[name="username"]').val();
							var locatePoint = form.find('input[name="locatePoint"]').val();
							var params = {username: username, locatePoint: locatePoint};
							var url = './lottery-user/modify-point';
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
										UserProfileTable.init();
										toastr['success']('修改返点成功！', '操作提示');
									}
									if(data.error == 1 || data.error == 2) {
										toastr['error']('修改返点失败！' + data.message, '操作提示');
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
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
					}
				}
			});
		}
		
		var show = function(data) {
			if(data) {
				form[0].reset();
				form.find('input[name="username"]').val(data.uBean.username);
				form.find('input[name="locatePoint"]').val(data.uBean.locatePoint.toFixed(1));
				form.find('[data-field="minPoint"]').html(data.rBean.minPoint.toFixed(1));
				form.find('[data-field="maxPoint"]').html(data.rBean.maxPoint.toFixed(1));
				form.find('.help-inline').empty();
				form.find('.has-error').removeClass('has-error');
				form.find('.has-success').removeClass('has-success');
				modal.modal('show');
			}
		}
		
		var init = function() {
			initForm();
		}
		
		return {
			init: init,
			show: show
		}
		
	}();
	
	var RechargeModal = function() {
		var modal = $('#modal-recharge');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					type: {
						required: true
					},
					amount: {
						required: true,
						number: true,
						min: 0.01
					},
					withdrawPwd: {
						required: true
					},
					remarks: {
						required: true
					}
				},
				messages: {
					type: {
						required: '请选择类型！'
					},
	                amount: {
	                	required: '金额不能为空！',
	                	number: '请填写正确金额！',
	                	min: '最低操作金额0.01元。'
	                },
					withdrawPwd: {
						required: '资金密码不能为空！'
					},
					remarks: {
						required: '说明不能为空！'
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
                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('select[name="type"]').change(function() {
				if($(this).val() == 1) {
					modal.find('[data-hidden="account"]').hide();
					modal.find('[data-hidden="limit"]').hide();
				}
				if($(this).val() == 2) {
					modal.find('[data-hidden="account"]').show();
					modal.find('[data-hidden="limit"]').show();
					modal.find('select[name="account"]').find('option[value="1"]').hide();
					modal.find('select[name="account"]').find('option[value="2"]').attr('selected', 'selected');
				} else {
					modal.find('select[name="account"]').find('option[value="1"]').show();
					modal.find('select[name="account"]').find('option[value="1"]').attr('selected', 'selected');
				}
				if($(this).val() == 3 || $(this).val() == 4) {
					modal.find('[data-hidden="account"]').show();
					modal.find('[data-hidden="limit"]').hide();
				}
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var show = function() {
			form.find('input[name="username"]').val(username);
			form.find('input[name="amount"]').val('');
			form.find('input[name="withdrawPwd"]').val('');
			form.find('input[name="remarks"]').val('');
			modal.find('select[name="type"]').trigger('change');
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var username = modal.find('input[name="username"]').val();
			var type = modal.find('select[name="type"]').val();
			var account = modal.find('select[name="account"]').val();
			var amount = modal.find('input[name="amount"]').val();
			var withdrawPwd = modal.find('input[name="withdrawPwd"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var limit = modal.find('input[name="limit"]').is(':checked') ? 1 : 0;
			isSending = true;

			$.ajax({
				type : 'post',
				url : './DisposableToken',
				data : {},
				dataType : 'json',
				success : function(tokenData) {
					isSending = false;
					if(tokenData.error == 0) {
						withdrawPwd = $.encryptPasswordWithToken(withdrawPwd, tokenData.token);
						var params = {username: username, type: type, account: account, amount: amount, withdrawPwd: withdrawPwd, limit: limit, remarks: remarks};
						var url = './lottery-user-recharge/add';
						$.ajax({
							type : 'post',
							url : url,
							data : params,
							dataType : 'json',
							success : function(data) {
								isSending = false;
								if(data.error == 0) {
									modal.modal('hide');
									UserProfileTable.init();
									toastr['success']('用户充值成功！', '操作提示');
								}
								if(data.error == 1 || data.error == 2) {
									toastr['error']('用户充值失败！' + data.message, '操作提示');
								}
							}
						});
					}
					else {
						isSending = false;
						toastr['error']('请求超时，请重试！', '操作提示');
					}
				},
				error: function(){
					isSending = false;
					toastr['error']('请求失败，请重试！', '操作提示');
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
		
		var init = function() {
			initForm();
		}
		
		return {
			init: init,
			show: show
		}
	}();
	
	var LockLotteryUserModal = function() {
		var modal = $('#modal-lottery-user-lock');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					message: {
						required: true
					}
				},
				messages: {
					message: {
						required: '请填写冻结原因！！'
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
                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var username = modal.find('input[name="username"]').val();
			var status = modal.find('input[name="status"]:checked').val();
			var message = modal.find('input[name="message"]').val();
			var params = {username: username, status: status, message: message};
			var url = './lottery-user/lock';
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
						UserProfileTable.init();
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
			form.find('input[name="username"]').val(data);
			form.find('input[name="status"]').eq(1).trigger('click');
			Metronic.initAjax();
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
	
	var SetUserBetsStatusModal = function() {
		var modal = $('#modal-user-bets-status-set');
		var form = modal.find('form');
		var initForm = function() {
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				doSubmit();
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var status = modal.find('input[name="status"]:checked').val();
			var message = modal.find('input[name="message"]').val();
			var params = {username: username, status: status, message: message};
			var url = './lottery-user/bets-status';
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
						UserProfileTable.init();
						toastr['success']('修改投注权限成功！', '操作提示');
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
			form.find('input[name="username"]').val(username);
			form.find('input[name="status"][value="' + data + '"]').trigger('click');
			Metronic.initAjax();
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
	
	var EditExtraPointModal = function() {
		var modal = $('#modal-extra-point-edit');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					point: {
						required: true,
						number: true,
						min: 0
					}
				},
				messages: {
					point: {
						required: '返点不能为空！',
						number: '返点必须为数字！',
						min: '返点不能小于0！'
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
                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var point = modal.find('input[name="point"]').val();
			var params = {username: username, point: point};
			var url = './lottery-user/modify-extra-point';
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
						UserProfileTable.init();
						toastr['success']('修改私返点数成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('修改私返点数失败！' + data.message, '操作提示');
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
			form.find('input[name="point"]').val(data.extraPoint.toFixed(1));
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
	
	var ModifyWithdrawNameModal = function() {
		var modal = $('#modal-withdraw-name-modify');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					withdrawName: {
						required: true
					}
				},
				messages: {
					withdrawName: {
						required: '取款人不能为空！'
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
                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var withdrawName = modal.find('input[name="withdrawName"]').val();
			var params = {username: username, withdrawName: withdrawName};
			var url = './lottery-user/modify-withdraw-name';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						UserProfileTable.init();
						modal.modal('hide');
						UserUpdateLogs.init();
						toastr['success']('用户取款人修改成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('用户取款人修改失败！' + data.message, '操作提示');
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
	
	var ModifyEmailModal = function() {
		var modal = $('#modal-email-modify');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					email: {
						required: true,
						email: true
					}
				},
				messages: {
					email: {
						required: '邮箱地址不能为空！',
						email: '请输入正确的邮箱地址！'
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
                    $(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
                }
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
		    	}
			});
		}
		
		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var email = modal.find('input[name="email"]').val();
			var params = {username: username, email: email};
			var url = './lottery-user/modify-email';
			isSending = true;
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						UserProfileTable.init();
						modal.modal('hide');
						toastr['success']('用户邮箱修改成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('用户邮箱修改失败！' + data.message, '操作提示');
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

	// 修改关联上级弹窗
	var RelatedUpperModifyModal = function() {
		var modal = $('#modal-related-upper-modify');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					relatedUpUser: {
						required: true,
						minlength: 6,
						maxlength: 12
					},
					relatedPoint: {
						required: true,
						number: true,
						min: 0,
						max: 1
					},
					remarks: {
						required: true,
						minlength: 1,
						maxlength: 100
					}
				},
				messages: {
					relatedUpUser: {
						required: '请输入关联上级！',
						minlength: '至少输入{0}个字符！',
						maxlength: '最多输入{0}个字符！'
					},
					relatedPoint: {
						required: '请输入关联返点！',
						number: '请输入数值！',
						min: '最小{0}！',
						max: '最大{0}！'
					},
					remarks: {
						required: '请输入操作说明！',
						minlength: '至少输入{0}个字符！',
						maxlength: '最多输入{0}个字符！'
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
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
				}
			});
			modal.find('[data-command="relate"]').unbind('click').click(function() {
				var relatedUpUser = form.find('input[name="relatedUpUser"]').val();
				var relatedPoint = form.find('input[name="relatedPoint"]').val();
				if (!relatedUpUser) {
					toastr['error']('请输入关联上级！', '操作提示');
					return;
				}
				if (!relatedPoint) {
					toastr['error']('请输入关联返点！', '操作提示');
					return;
				}

				if(form.validate().form()) {
					doRelateConfirm();
				}
			});
			modal.find('[data-command="relive"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doReliveConfirm();
				}
			});
		}

		var show = function(show, relatedUpUser, relatedPoint) {
			form.find('input[name="username"]').val(username);
			form.find('input[name="relatedUpUser"]').val(relatedUpUser);
			form.find('input[name="relatedPoint"]').val(relatedPoint);
			form.find('input[name="remarks"]').val('');
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}

		var doRelateConfirm = function() {
			bootbox.dialog({
				message: '是否确认操作？',
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doRelate();
						}
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
					}
				}
			});
		}

		var doReliveConfirm = function() {
			bootbox.dialog({
				message: '是否确认操作？',
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doRelive();
						}
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
					}
				}
			});
		}

		var isSending = false;
		var doRelate = function() {
			if(isSending) return;
			var username = modal.find('input[name="username"]').val();
			var relatedUpUser = modal.find('input[name="relatedUpUser"]').val().trim();
			var relatedPoint = modal.find('input[name="relatedPoint"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var params = {username: username, relatedUpUser: relatedUpUser, relatedPoint: relatedPoint, remarks: remarks};
			var url = './lottery-user/modify-related-upper';
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
						UserProfileTable.init();
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

		var doRelive = function() {
			if(isSending) return;
			var username = modal.find('input[name="username"]').val();
			var remarks = modal.find('input[name="remarks"]').val();
			var params = {username: username, remarks: remarks};
			var url = './lottery-user/relive-related-upper';
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
						UserProfileTable.init();
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

		var init = function() {
			initForm();
		}

		return {
			init: init,
			show: show
		}
	}();

	
	// 修改关联会员弹窗
	var RelatedUserModifyModal = function() {

		var modal = $('#modal-related-users-modify');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					remarks: {
						required: true,
						minlength: 1,
						maxlength: 100
					}
				},
				messages: {
					remarks: {
						required: '请输入操作说明！',
						minlength: '至少输入{0}个字符！',
						maxlength: '最多输入{0}个字符！'
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
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
				}
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doConfirm();
				}
			});
		}

		var show = function(username, relatedUsers) {
			form.find('input[name="username"]').val(username);
			form.find('input[name="relatedUsers"]').val(relatedUsers);
			form.find('input[name="remarks"]').val('');
			form.find('.has-error').removeClass('has-error');
			form.find('.has-success').removeClass('has-success');
			modal.modal('show');
		}

		var doConfirm = function() {
			bootbox.dialog({
				message: '是否确认操作？',
				title: '提示消息',
				buttons: {
					success: {
						label: '<i class="fa fa-check"></i> 确定',
						className: 'green-meadow',
						callback: function() {
							doSubmit();
						}
					},
					danger: {
						label: '<i class="fa fa-undo"></i> 取消',
						className: 'btn-danger',
						callback: function() {}
					}
				}
			});
		}

		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var username = modal.find('input[name="username"]').val();
			var _relatedUsers = modal.find('input[name="relatedUsers"]').val().trim();
			var remarks = modal.find('input[name="remarks"]').val();
			var params = {username: username, relatedUsers: _relatedUsers, remarks: remarks};
			var url = './lottery-user/modify-related-users';
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
						UserProfileTable.init();
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

		var init = function() {
			initForm();
		}

		return {
			init: init,
			show: show
		}
	}();

	var UserWithdrawLimitTable = function(){
		var $tab = $('#withdraw-limit-log-tab');
		var tableList = $('#withdraw-limit-table');
		var tablePagelist = tableList.find('.page-list');
		var pagination = $.pagination({
			render: tablePagelist,
			pageLength: 3,
			pageSize: 10,
			ajaxType: 'post',
			ajaxUrl: './lottery-user/withdraw-limit-list',
			ajaxData: {username: username},
			beforeSend: function() {},
			complete: function() {},
			success: function(list, data) {
				$tab.show();
				var table = tableList.find('table > tbody').empty();
				var innerHtml = '';
				$.each(list, function(idx, val) {
					innerHtml +=
						'<tr class="align-center">'+
						'<td>' + val.bean.rechargeTime + '</td>'+
						'<td>' + val.bean.rechargeMoney.toFixed(2) + '</td>'+
						'<td>' + (val.bean.proportion.toFixed(2) * 100) + '%</td>'+
						'<td>' + (val.totalBilling.toFixed(2) + '/' + val.bean.consumptionRequirements.toFixed(2)) + '</td>';
						if(val.remainConsumption>0){
							innerHtml += '<td><span class="color-red">' + val.remainConsumption.toFixed(2) + '</span></td>';
						}
						else if(val.remainConsumption<0){
							innerHtml += '<td><span class="color-blue">' + val.remainConsumption.toFixed(2) + '</span></td>';
						}
						else{
							innerHtml += '<td><span class="color-green">' + val.remainConsumption.toFixed(2) + '</span></td>';
						}
						innerHtml += '<td>' + DataFormat.formatPaymentChannelType(val.bean.type, val.bean.subType) + '</td>'+
						'</tr>';
				});


				var withdrawLimitHtml;
				if (data.totalRemainConsumption > 0) {
					withdrawLimitHtml = '还需消费：<span class="color-red">'+data.totalRemainConsumption.toFixed(2)+'</span>';
				}
				else {
					withdrawLimitHtml = '还需消费：<span class="color-green">'+data.totalRemainConsumption.toFixed(2)+'</span>';
				}
				table.html(innerHtml);
				tableList.find('#withdraw-limit').html(withdrawLimitHtml);
			},
			pageError: function(response) {
				$tab.hide();
			},
			emptyData: function() {
				$tab.show();

				tableList.find('#withdraw-limit').html('还需消费：<span class="color-green">0.00</span>');

				var tds = tableList.find('thead tr th').size();
				tableList.find('table > tbody').html('<tr><td colspan="'+tds+'">没有相关数据</td></tr>');
			}
		});


		var init = function() {
			pagination.init();
		}

		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			UserProfileTable.init();
			EditLotteryUserCardModal.init();
			ModifyPasswordModal.init();
			ModifyPointModal.init();
			RechargeModal.init();
			UserLoginInfo.init();
			UserUpdateLogs.init();
			LockLotteryUserModal.init();
			EditExtraPointModal.init();
			SetUserBetsStatusModal.init();
			ModifyWithdrawNameModal.init();
			ModifyEmailModal.init();
			RelatedUpperModifyModal.init();
			RelatedUserModifyModal.init();
			UserWithdrawLimitTable.init();
		}
	}
}();