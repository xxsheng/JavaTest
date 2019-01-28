angular.module('app', ['ionic', 'ui.router', 'app.controllers', 'app.services', ])
	.run(function($rootScope, $ionicPlatform, $http) {
		$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
		$ionicPlatform.ready(function() {
			// Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
			// for form inputs)
			if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
				cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
				cordova.plugins.Keyboard.disableScroll(true);
			}
			if (window.StatusBar) {
				// org.apache.cordova.statusbar required
				StatusBar.styleDefault();
			}
		});

		$rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
			// 清除
			if (toState.name != 'bet') {
				WS.disconnect();
				// STOP_RED_PACKET_RAIN();
				// $("#redPacketLayerCaptured").remove();
				// $("#redPacketLayerOpened").remove();
			}
		})
	})
	.config(function($stateProvider, $urlRouterProvider) {
		$stateProvider
			.state('home', {
				url: '/home',
				templateUrl: '../../template/home.html'
			})
			.state('login', {
				url: '/login',
				templateUrl: '../../template/login.html'
			})
			.state('register', {
				url: '/register/:code',
				templateUrl: '../../template/register.html'
			})
			//投注页面
			.state('bet', {
				url: '/bet/:id',
				templateUrl: '../../template/bet.html'
			})
			//投注记录页面
			.state('betrecord', {
				url: '/betrecord/:type',
				templateUrl: '../../template/betrecord.html'
			})
			//购彩车页面
			.state('cpcart', {
				url: '/cpcart',
				templateUrl: '../../template/cpcart.html'
			})
			//追号页面
			.state('trace', {
				url: '/trace',
				templateUrl: '../../template/trace.html'
			})
			//充提记录页面
			.state('chargeRecord', {
				url: '/chargeRecord/:type',
				templateUrl: '../../template/chargeRecord.html'
			})
			//充值页面
			.state('charge', {
				url: '/charge',
				templateUrl: '../../template/charge.html'
			})
			//充值确认页面
			.state('confirmCharge', {
				url: '/confirmCharge',
				templateUrl: '../../template/confirmCharge.html'
			})
			//个人中心页面
			.state('member', {
				url: '/member',
				templateUrl: '../../template/member.html'
			})
			//购彩大厅
			.state('lobby', {
				url: '/lobby',
				templateUrl: '../../template/lobby.html'
			})
			//近期开奖
			.state('recentopen', {
				url: '/recentopen',
				templateUrl: '../../template/recentopen.html'
			})
			//彩票历史开奖
			.state('lotteryhistory', {
				url: '/lotteryhistory/:id',
				templateUrl: '../../template/lotteryhistory.html'
			})
			//银行卡管理
			.state('bankcard', {
				url: '/bankcard',
				templateUrl: '../../template/bankcard.html'
			})
			//密码管理
			.state('pwd', {
				url: '/pwd/:type',
				templateUrl: '../../template/pwd.html'
			})
			//设置密保
			.state('security', {
				url: '/security',
				templateUrl: '../../template/security.html'
			})
			//设置资金密码
			.state('setmoneypwd', {
				url: '/setmoneypwd',
				templateUrl: '../../template/setmoneypwd.html'
			})
			//绑定用户资料
			.state('binduser', {
				url: '/binduser',
				templateUrl: '../../template/binduser.html'
			})
			//绑定google
			.state('googleBind', {
				url: '/googleBind',
				templateUrl: '../../template/googleBind.html'
			})
			//系统公告
			.state('sysmsg', {
				url: '/sysmsg',
				templateUrl: '../../template/sysmsg.html'
			})
			//团队开户
			.state('createAccount', {
				url: '/createAccount/:type',
				templateUrl: '../../template/createAccount.html'
			})
			//团队管理
			.state('team_manage', {
				url: '/team_manage/:type',
				templateUrl: '../../template/teamManage.html'
			})
			//代理总览
			.state('overview', {
				url: '/overview/:type',
				templateUrl: '../../template/overview.html'
			})
			//在线会员
			.state('online_member', {
				url: '/online_member/:type',
				templateUrl: '../../template/online_member.html'
			})
			//彩票记录
			.state('lottery_record', {
				url: '/lottery_record/:type',
				templateUrl: '../../template/lottery_agent_record.html'
			})
			//契约日结
			.state('settle_bill', {
				url: '/settle_bill/:type',
				templateUrl: '../../template/settleBill.html'
			})
			//契约分红
			.state('bonus_bill', {
				url: '/bonus_bill/:type',
				templateUrl: '../../template/bonusbill.html'
			})
			//提款申请
			.state('withdraw', {
				url: '/withdraw',
				templateUrl: '../../template/withdraw.html'
			})
			//转账
			.state('transfer', {
				url: '/transfer',
				templateUrl: '../../template/transfer.html'
			})
			.state('transfer_account', {
				url: '/transfer_account',
				templateUrl: '../../template/transfer_account.html'
			})
			//活动
			.state('promotion', {
				url: '/promotion/:id',
				templateUrl: '../../template/promotion.html'
			})
			// 报表中心
			.state('report', {
				url: '/report/:type',
				templateUrl: '../../template/report.html'
			})
			//消息中心
			.state('message', {
				url: '/message/:type',
				templateUrl: '../../template/message.html'
			})
			//电子游戏
			.state('games', {
				url: '/games',
				templateUrl: '../../template/games.html'
			})
			//走势图
			.state('trend', {
				url: '/trend',
				templateUrl: '../../template/lotteryTrend.html'
			})

		$urlRouterProvider.otherwise('/login');
	})
	.filter('numberFilter', function() {
		return function(data) {
			return data.split('').join(',');
		}
	})
	.filter('valueFiler', function() {
		return function(data) {
			if (data.length > 2) {
				return data.split('')[0];
			} else {
				return data;
			}

		}
	})
	.filter('prizeFilter', function() {
		return function(data) {
			if (data === null) {
				return 0;
			} else {
				return data;
			}
		}
	})
	.filter('stopFileter', function() {
		return function(data) {
			if (data === 1) {
				return "是"
			} else {
				return "否"
			}
		}
	})
	.filter('typeFilter', function() {
		return function(data) {
			if (data === 1 || data === 3) {
				return "代理";
			}
			if (data === 2) {
				return "玩家"
			}
		}
	})
	.filter('onlineFileter', function() {
		return function(data) {
			if (data === 1) {
				return "在线";
			}
			if (data === 0) {
				return "离线"
			}
		}
	})
	.filter('statusFilter', function() {
		return function(data) {
			if (data === -1) {
				return "已撤单"
			}
			if (data === 0) {
				return '未开奖'
			}
			if (data === 1) {
				return "未中奖"
			}
			if (data === 2) {
				return "已中奖"
			}
		}
	})
	.filter('gameStatusFilter', function() {
		return function(status) {
			if(status == -1) {
				return '未知';
			}
			if(status == 1) {
				return '完成';
			}
			if(status == 2) {
				return '等待中';
			}
			if(status == 3) {
				return '进行中';
			}
			if(status == 4) {
				return '赢';
			}
			if(status == 5) {
				return '输';
			}
			if(status == 6) {
				return '平局';
			}
			if(status == 7) {
				return '拒绝';
			}
			if(status == 8) {
				return '退钱';
			}
			if(status == 9) {
				return '取消';
			}
			if(status == 10) {
				return '上半场赢';
			}
			if(status == 11) {
				return '上半场输';
			}
			if(status == 12) {
				return '和';
			}
			return '';
		}
	})
	.filter('systemFilter', function() {
		return function(data) {
			if (data === 0) {
				return "系统消息"
			}
			if (data === 2) {
				return '到账通知'
			}
			if (data === 3) {
				return "提现通知"
			}
		}
	})
	.filter('readFilter', function() {
		return function(data) {
			if (data === 0) {
				return "未读"
			}
			if (data === 1) {
				return '已读'
			}
			if (data === -1) {
				return "已删除"
			}
		}
	})
	.filter('modelFilter', function() {
		return function(data) {
			if (data === 'yuan') {
				return "2元";
			}
			if (data === 'jiao') {
				return "2角"
			}
			if (data === 'fen') {
				return "2分"
			}
			if (data === 'li') {
				return "2厘"
			}
			if (data === '1yuan') {
				return "1元";
			}
			if (data === '1jiao') {
				return "1角"
			}
			if (data === '1fen') {
				return "1分"
			}
			if (data === '1li') {
				return "1厘"
			}
		}
	})
	.filter('lockFilter', function() {
		return function(data) {
			if (data === 0) {
				return "使用中";
			}
			if (data === 1) {
				return "锁定"
			}

		}
	})
	.filter('bankFilter', function() {
		return function(num) {
			return num && (num
				.toString().indexOf('.') != -1 ? num.toString().replace(/(\d)(?=(\d{4})+\.)/g, function($0, $1) {
					return $1 + " ";
				}) : num.toString().replace(/(\d)(?=(\d{4})+\b)/g, function($0, $1) {
					return $1 + " ";
				}));
		}
	})
	.filter('fileterTransType', function() {
		return function(data) {
			if (data === 0) {
				return "平台转账";
			}
			if (data === 1) {
				return "上下级转账(存)";
			}
			if (data === 2) {
				return "上下级转账(取)";
			}

		}
	})
	.filter('contractFilter', function() {
		return function(data) {
			if (data === 1) {
				return "已发放";
			}
			if (data === 2) {
				return "核对中";
			}
			if (data === 3) {
				return "待领取";
			}
			if (data === 4) {
				return "已拒绝";
			}
			if (data === 5) {
				return "未达标";
			}
			if (data === 6) {
				return "余额不足";
			}
			if (data === 7) {
				return '部分发放';
			}
			if (data === 8) {
				return '已过期';
			}
		}
	})
	.filter('dailySettleBillStatusFilter', function() {
		return function(data) {
			if (data === 1) {
				return "已发放";
			}
			if (data === 2) {
				return "部分发放";
			}
			if (data === 3) {
				return "余额不足";
			}
			if (data === 4) {
				return "未达标";
			}
			if (data === 5) {
				return "已拒绝";
			}
		}
	})
	.filter('waterStatusFilter', function() {
		return function(data) {
			if (data === 1) {
				return "已发放";
			}
			if (data === 2) {
				return "已拒绝";
			}
		}
	})
	.filter('changesTypeFilter', function() {
		return function(data) {
			if (data === 1) {
				return "充值";
			}
			if (data === 2) {
				return "取款";
			}
			if (data === 3) {
				return "转入";
			}
			if (data === 4) {
				return "转出";
			}
			if (data === 5) {
				return "优惠活动";
			}
			if (data === 6) {
				return "投注";
			}
			if (data === 7) {
				return '派奖';
			}
			if (data === 8) {
				return "投注返点";
			}
			if (data === 9) {
				return "代理返点";
			}
			if (data === 10) {
				return "撤销订单";
			}
			if (data === 11) {
				return "会员返水";
			}
			if (data === 13) {
				return "管理员增";
			}
			if (data === 14) {
				return '管理员减';
			}
			if (data === 15) {
				return "上下级转账";
			}
			if (data === 16) {
				return "取款退回";
			}
			if (data === 17) {
				return "积分兑换";
			}
			if (data === 18) {
				return "支付佣金";
			}
			if (data === 19) {
				return "获得佣金";
			}
			if (data === 20) {
				return "退还佣金";
			}
			if (data === 21) {
				return '红包';
			}
			if (data === 22) {
				return '日结';
			}

		}
	})
	.filter('bonusContractFilter', function() {
		return function(data) {
			if (data === 1) {
				return "生效";
			}
			if (data === 2) {
				return "待同意";
			}
			if (data === 3) {
				return "已过期";
			}
			if (data === 4) {
				return "无效";
			}
			if (data === 5) {
				return "已拒绝";
			}
		}
	})
	.filter('fileterWithdrawType', function() {
		return function(data) {
			if (data === 0) {
				return "待处理";
			}
			if (data === 1) {
				return "已完成";
			}
			if (data === 2) {
				return "处理中";
			}
			if (data === 3) {
				return "银行处理中";
			}
			if (data === 4) {
				return "提现失败";
			}
			if (data === -1) {
				return "拒绝支付";
			}

		}
	})
	.filter('fileterPaymentChannelType', function() {
		return function(type) {
			// 1：网银充值
			if(type == 1) {
				return '网银充值';
			}
			// 2：手机充值
			if(type == 2) {
				return '手机充值';
			}
			// 3：系统充值
			if(type == 3) {
				return '系统充值';
			}
			// 4：上下级转账
			if(type == 4) {
				return '上下级转账';
			}
			return '未知';
		}
	})
	.filter('filterURL', function() {
		return function(data) {
			if (data) {
				return data.replace(/\+/g, '%2B');
			}
			else {
				return data;
			}
		}
	})
	.filter('filterBankName', function() {
		return function(data) {
			if (data === 1) {
				return "中国工商银行";
			}
			if (data === 2) {
				return "中国建设银行";
			}
			if (data === 3) {
				return "中国农业银行";
			}
			if (data === 4) {
				return "招商银行";
			}
			if (data === 5) {
				return "中国银行";
			}
			if (data === 6) {
				return "交通银行";
			}
			if (data === 7) {
				return "浦发银行";
			}
			if (data === 8) {
				return "兴业银行";
			}
			if (data === 9) {
				return "中信银行";
			}
			if (data === 9) {
				return "中信银行";
			}
			if (data === 10) {
				return "宁波银行";
			}
			if (data === 11) {
				return "上海银行";
			}
			if (data === 12) {
				return "杭州银行";
			}
			if (data === 13) {
				return "渤海银行";
			}
			if (data === 14) {
				return "浙商银行";
			}
			if (data === 15) {
				return "广发银行";
			}
			if (data === 16) {
				return "中国邮政储蓄银行";
			}
			if (data === 17) {
				return "深圳发展银行";
			}
			if (data === 18) {
				return "中国民生银行";
			}
			if (data === 19) {
				return "中国光大银行";
			}
			if (data === 20) {
				return "华夏银行";
			}
			if (data === 21) {
				return "北京银行";
			}
			if (data === 22) {
				return "南京银行";
			}
			if (data === 23) {
				return "平安银行";
			}
			if (data === 24) {
				return "北京农村商业银行";
			}
			if (data === 'WECHAT') {
				return "微信";
			}
			if (data === 'ALIPAY') {
				return "支付宝";
			}
			if (data === 'SPEED') {
				return "快捷支付";
			}
			if (data === 'QQ') {
				return "QQ钱包";
			}
			if (data === 'JDPAY') {
				return "京东钱包";
			}
		}
	})
	.filter('amountColorFilter', ['$sce', function ($sce) {
		return function (text) {
			var amount = parseFloat(text.replace(/,/g, ''));
			amount = amount.toFixed(2);
			var html = amount;
			if (amount > 0) {
				html = '<span class="amount_positive">' + amount + '</span>'
			}
			else if (amount < 0){
				html = '<span class="amount_negative">' + amount + '</span>'
			}
			return $sce.trustAsHtml(html);
		};
	}])
	.directive('commonfooter', function($sce) {
		return {
			restrict: 'E',
			replace: true,
			transclude: true,
			templateUrl: $sce.trustAsResourceUrl('../../template/footer.html')
		}
	})
	.directive('onFinishRender', function($timeout) {
		return {
			restrict: 'A',
			link: function(scope, element, attr) {
				if (scope.$last === true) {
					$timeout(function() {
						scope.$emit(attr.onFinishRender);
					});
				}
			}
		}
	})
	.directive("slideFollow",function($window, $timeout){
        return {
        	restrict: 'AE',
            replace : true,
            scope : {
                id : "@",
                datasetData : "="
            },
            template : "<li ng-repeat='data in datasetData'><a href='javascript:;' ng-click='showContent(data)'>{{data.title}}</a></li>",
            link: function(scope, element, attr) {
                var itsWatch = scope.$watch("its", function(newvalue, oldvalue) {
                        itsWatch();
                        var i = 1;    //element是ul
                        
                        setInterval(function() {
                        	var length = element[0].parentElement.children.length;
                            if(i == length) {
                                i = 0;//初始位置
                                element[0].parentElement.style.top  = "0px";
                            }
                            var topscorll = -(i * 30);

                            feeltoTop(topscorll)
                            i++;
                        }, 3000)
                        //向上滚动
                        function feeltoTop(topscorll){  //console.log(topscorll):topscorll是top值
                            var buchang = -5;
                            var feelTimer = setInterval(function(){
                            	element[0].parentElement.style.top = parseInt(element[0].parentElement.style.top) + buchang + "px";
                                if(parseInt(element[0].parentElement.style.top) <= topscorll){
                                	element[0].parentElement.style.top = topscorll + "px";
                                    window.clearInterval(feelTimer);
                                }
                            },100);
                        }
                })
            }
        }
    })