(function() {
	'use strict';
	//设置全局dom
	var $dom = $('body');

	//全局控制器
	var appController = function($rootScope, $state, $ionicPopup, $location, $window, postDataService) {
		var vm = this;
		//路由跳转
		vm.goto = function(path) {
			$location.path(path);
		}
		vm.routeto = function(path, param) {
			$state.go(path, {
				id: param
			});
		}
		vm.goback = function() {
			$window.history.back();
		}

		//退出登录
		vm.logout = function() {
			postDataService.getPostData(null, '/App/Logout').then(function(data) {
				$state.go('login');
				// $('#refleshCode').attr('src', '/LoginCode?code=' + Math.random());
			});
		}

		//全局检测是否登录
		$rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
			if (toState.name != 'login' && toState.name != 'register') {
				postDataService.fetchPostData(null, '/App/CheckLogin').then(function(data) {
					if (data.data.data.isLogin === false) {
						// var alertPop = $ionicPopup.alert({
						// 	title: '温馨提示',
						// 	template: '您未登录！！'
						// });
						// alertPop.then(function() {
						// 	$state.go('login', {
						// 		login_status: 'alreadyCheck'
						// 	});
						// });
						$state.go('login', {
							login_status: 'alreadyCheck'
						});
					}
				});
			}
		});
	}

	//首页控制器
	var homeController = function($rootScope, $state, $ionicPopup, $location, $window, $ionicSideMenuDelegate, postDataService) {
		var vm = this;

		vm.toggleLeftSideMenu = function() {
			postDataService.getPostData({
				SysMsgId: 0
			}, '/GetGlobal').then(function(data) {
				vm.globalData = data.data.data.uBean;
			});
			$ionicSideMenuDelegate.toggleLeft();
		}
	}
	
	//公告栏
	var tipController = function($scope, $state, $ionicModal, $location, $window, $ionicSideMenuDelegate, postDataService){
		$scope.getSysNoticeList = function(){
			postDataService.fetchPostData({}, '/SysNoticeList').then(function(data) {
	        	$scope.datasetData = data.data.data;
	        	console.log(data);
			});
		}
		setInterval($scope.getSysNoticeList, 120000);
		
		$scope.showContent = function(data){
			$scope.content = data.content;
			$scope.title = data.title;
			$scope.modal.show();
		}
		
		$scope.showList = function(){
			$scope.listModal.show();
		}
		
		$ionicModal.fromTemplateUrl('notice-popover.html', {
			scope: $scope
		}).then(function(modal) {
			$scope.modal = modal;
		});
		
		$ionicModal.fromTemplateUrl('noticeList-popover.html', {
			scope: $scope
		}).then(function(modal) {
			$scope.listModal = modal;
		});
    };
    
    // 走势图
	var lotteryTrendController = function($scope, $timeout, $state, postDataService) {
		$scope.lotteryId = $state.params.id;
		var searchObj = {lotteryId: $scope.lotteryId, command: 'latest-30-desc'};

		$scope.search = function() {
			postDataService.getPostData(searchObj, '/LotteryCodeTrend').then(function(data) {
			});
		}
		
		$scope.refresh = function(){
			$scope.search();
			$timeout(function() {
				$scope.$broadcast('scroll.refreshComplete');
			}, 1000);
		};
	}
	
	//登录控制器
	var loginController = function($scope, $state, $window, postDataService, $ionicPopup, fetchDataService, betUtilsService, passwordService) {
		var vm = this;
		vm.loginObj = {};
		vm.showUsernameClearBtn = false;
		vm.showPasswordClearBtn = false;

		var rememberMe = window.localStorage.getItem('rememberMe');
		var rememberPwd = window.localStorage.getItem('pwd');
		if (rememberMe == 'true' || rememberMe == true) {
			vm.loginObj.username = window.localStorage.getItem('username');
			if (rememberPwd == undefined || rememberPwd == null || rememberPwd == '' || rememberPwd.length != 32) {
				vm.loginObj.password = '';
			}
			else {
				vm.loginObj.password = rememberPwd;
			}
			vm.isRemember = true;
		} else {
			vm.loginObj.username = '';
			vm.loginObj.password = '';
			vm.isRemember = false;
		}

		vm.login = function() {
			if (!vm.loginObj.username) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '用户名不能为空'
				});
				return;
			}

			if (!vm.loginObj.password) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '密码不能为空'
				});
				return;
			}

			if (rememberPwd != undefined && rememberPwd != null && rememberPwd != '') {
				var pwd = vm.loginObj.password;
				if (pwd === rememberPwd) {
					vm.loginObj.password = rememberPwd;
				} else {
					vm.loginObj.password = passwordService.encryptPasswordWithoutToken(vm.loginObj.password);
				}
			} else {
				vm.loginObj.password = passwordService.encryptPasswordWithoutToken(vm.loginObj.password);
			}

			var token = passwordService.getDisposableToken();
			if (!token) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请求超时，请重新操作！'
				})
				return;
			}

			var password = hex_md5(vm.loginObj.password + token).toUpperCase();
			var loginData = {
				username: vm.loginObj.username,
				password: password
			};
			postDataService.submitData(loginData, '/App/Login').then(function(data) {
				if (data.data.error === 0) {
					//用户名写入cookie
					//存入localstorage
					if (vm.isRemember) {
						window.localStorage.setItem('pwd', vm.loginObj.password);
						window.localStorage.setItem('username', vm.loginObj.username);
						window.localStorage.setItem('rememberMe', true);
					} else {
						window.localStorage.removeItem('pwd');
						window.localStorage.removeItem('username');
						window.localStorage.setItem('rememberMe', false);
					}

					$state.go('home');
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
					// vm.refleshimg();
					vm.oneKeyClear = true;
				}

			});
		}
		postDataService.fetchPostData(null, '/App/CheckLogin').then(function(data) {
			if (data.data.data.isLogin === true) {
				$state.go('home');
			}
		});

		vm.isRememberPwd = function() {
			vm.isRemember != vm.isRemember;
		}
	}

	//注册控制器
	var registerController = function($scope, $state, $window, postDataService, $ionicPopup, fetchDataService, betUtilsService, passwordService) {
		var vm = this;
		vm.registerObj = {};
		vm.showUsernameClearBtn = false;
		vm.showPasswordClearBtn = false;
		vm.showRePasswordClearBtn = false;

		if (!$state.params.code) {
			$ionicPopup.alert({
				title: '温馨提示',
				template: '您的注册链接无效！'
			}).then(function(){
				$state.go('login');
			});
			return;
		}

		vm.register = function() {
			if (!vm.registerObj.username) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '用户名不能为空'
				});
				return;
			}
			if (!(/^[a-zA-Z]{1}([a-zA-Z0-9]|[_]){5,11}$/.test(vm.registerObj.username))) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '用户名请输入6-12位，字母开头，不能包含特殊字符。'
				});
				return;
			}

			if (!vm.registerObj.password) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '登录密码不能为空'
				});
				return;
			}
			if (!(/^(?![\d]+$)(?![a-zA-Z]+$)(?![~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]+$)[\da-zA-Z~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]{6,20}$/.test(vm.registerObj.password))) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '登录密码必须为英文、数字、字符任意两种组合，长度6-20位。'
				});
				return;
			}

			if (!vm.registerObj.rePassword) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '重复密码不能为空'
				});
				return;
			}
			if (vm.registerObj.rePassword != vm.registerObj.password) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '两次输入密码不一致，请检查！'
				});
				return;
			}

			if (!(/^[0-9]{4}$/.test(vm.registerObj.checkcode))) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入4位数字验证码。'
				});
				return;
			}

			var password = passwordService.generatePassword(vm.registerObj.password);
			var registerData = {
				username: vm.registerObj.username,
				nickname: vm.registerObj.username,
				password: password,
				code: vm.registerObj.checkcode,
				link: $state.params.code
			}

			postDataService.submitData(registerData, '/UserRegist').then(function(data) {
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '恭喜您注册成功！'
					}).then(function(){
						$state.go('login');
					});
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
					vm.refreshCheckCode();
					vm.oneKeyClear = true;
				}
			});
		}
		vm.refreshCheckCode = function() {
			$('#registercode').attr('src', '/RegistCode?t=' + (+new Date()))
		}
	}

	//彩票控制器
	var betController = function($rootScope, $q, $timeout, $ionicLoading, $scope, $state, $ionicSideMenuDelegate, $ionicScrollDelegate,
								 $ionicModal, $ionicPopover, $ionicPopup, $location,
								 fetchDataService, betUtilsService, shareDataService,
								 lotteryUtilsService, postDataService, webSocketService, lzmaService) {
		$rootScope.lt_time_leave = 0;
		$rootScope.time_leave_flag = true;
		betUtilsService.clearCountdown();
		//路由变化后清除倒计时
		$scope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
			if (toState.name != 'bet' && toState.name != 'cpcart' && toState.name != 'trace') {
				betUtilsService.cleartimer();
			}
		})

		//判断是否加入购物车后继续没有投注继续购彩或是返回
		if (shareDataService.list.length) {
			$('#cart-note-num').removeClass('hidden').html(shareDataService.list.length);
		}
		var vm = this;
		//如果cookie中的彩票id跟路由的参数id不一样 表示切换彩种了 则清除cookie中的methodid 和pname;
		if (betUtilsService.getCookie('cpid') != $state.params.id) {
			betUtilsService.delCookie('method');
			betUtilsService.delCookie('pname');
			//如果切换彩种清空购物车
			shareDataService.list = [];
			$('#cart-note-num').addClass('hidden').val('');
		}
		//将彩种id存入cookie中
		betUtilsService.SetCookie('cpid', $state.params.id);
		vm.id = betUtilsService.getCookie('cpid');
		vm.p_layout = [];
		vm.layout = []; //购彩布局
		vm.gameMethods = []; //游戏种类
		vm.historyNumbers = []; //历史开奖记录
		vm.currentGame = {}; //默认玩法
		//存放布尔类型的对象
		vm.booleanObj = {
			isInput: false, //是否单式
			isTools: true, //是否有大小单双选择按钮
			isSpecial: false //是否特殊号码
		};
		//根据模式和倍数是否存入了cookie 进行相应的处理
		if (betUtilsService.getCookie('mode')) {
			// $('div[data-val="' + betUtilsService.getCookie('mode') + '"]', '#yjfl').addClass('active').siblings('div').removeClass('active');
			$('#model_'+betUtilsService.getCookie('mode')).addClass('active').siblings('div').removeClass('active');
		}
		if (betUtilsService.getCookie('multiple')) {
			$('#multiple').val(betUtilsService.getCookie('multiple'));
		}

		//加载初始化数据
		vm.initbet = function() {
			var questList = [];
			if (vm.id === '117') {
				questList = [postDataService.getPostData({
					lotteryId: vm.id
				}, '/App/Lottery'), postDataService.getPostData({
					lotteryId: vm.id
				}, '/LotteryOpenCode'), postDataService.getPostData({
					SysMsgId: 0
				}, '/GetGlobal')];
			} else {
				questList = [postDataService.getPostData({
					lotteryId: vm.id
				}, '/App/Lottery'), postDataService.getPostData({
					lotteryId: vm.id
				}, '/LotteryOpenTime'), postDataService.getPostData({
					lotteryId: vm.id
				}, '/LotteryOpenCode'), postDataService.getPostData({
					SysMsgId: 0
				}, '/GetGlobal')]
			}
			$q.all(questList).then(function(result) {
				vm.lottery = result[0].data.data.Lottery;
                vm.playRulesGroup = result[0].data.data.PlayRulesGroup;
                vm.playRules = result[0].data.data.PlayRules;
				var method = betUtilsService.getCookie('method') || vm.currentGame.method;
				vm.UserData = result[0].data.data.UserData;
				if (vm.id === '117') {
					vm.historyNumbers = result[1].data;
					$scope.historyNumbers = vm.historyNumbers;
					vm.globalData = result[2].data.data.uBean;
				} else {
					vm.historyNumbers = result[2].data;
					$scope.historyNumbers = vm.historyNumbers;
					vm.globalData = result[3].data.data.uBean;
					vm.server = result[1].data.opentime; //服务器时间
				}
				//设置最高模式
				var maxcode = vm.UserData.code > 1960 ? 1960 : vm.UserData.code,
					uPointTemp = 0,
					uLocPoint = result[0].data.data.UserData.lp,
					uMinCode = Number(vm.UserData.code) - uLocPoint * 20; // TODO 1800
				
				if(vm.UserData.code > 1960){
					uPointTemp = Number((vm.UserData.code - 1960) / 20);
					$('#point').text(uPointTemp.toFixed(2) + "%");
				}
				
				if(vm.id == '201' || vm.id == '202' || vm.id == '203' || vm.id == '204' || vm.id == '205' || vm.id == '206'){
					maxcode = vm.UserData.code > 1960 ? 1960 : vm.UserData.code;
					if(vm.UserData.code > 1960){
						uPointTemp = Number((vm.UserData.code - 1960) / 20);
						$('#point').text(uPointTemp.toFixed(2) + "%");
					}
				}
				
				if(vm.id == '401' || vm.id == '402'){
					maxcode = vm.UserData.code > 1932 ? 1932 : vm.UserData.code;
					if(vm.UserData.code > 1932){
						uPointTemp = Number((vm.UserData.code - 1932) / 20);
						$('#point').text(uPointTemp.toFixed(2) + "%");
					}
				}
				
				if(vm.id == '301' || vm.id == '302' || vm.id == '303' || vm.id == '304' || vm.id == '305' || vm.id == '306' || vm.id == '307' || vm.id == '308' || vm.id == '309'  || vm.id == '310'){
					maxcode = vm.UserData.code > 1920 ? 1920 : vm.UserData.code;
					if(vm.UserData.code > 1932){
						uPointTemp = Number((vm.UserData.code - 1932) / 20);
						$('#point').text(uPointTemp.toFixed(2) + "%");
					}
				}
				
				var prizeArray = vm.playRules[method]['prize'].split(',');
				vm.currentGame.prize = Number(maxcode) / Number(prizeArray[0]) //当前玩法奖金
				$('#prize_group').text(maxcode);
				
				$('#modeajust').attr('min', 1800).attr('max', maxcode).attr('minpoint', uPointTemp).val(maxcode);
				if (vm.id != '117') {
					betUtilsService.countdown(result[1].data.sTime, vm.server.stopTime, vm.server.expect, vm.id);
				} else {
					vm.hideexpect = true;
				}

				// 注册web socket
				webSocketService.register(vm.id, function(socketData){
					// 抓取号码
					if (socketData.type === 1) {
						var expect = vm.id == 117 ? '' : socketData.expect;
						var code = socketData.code;

						var div = $('#collopse div');
						if (!div) {
							return;
						}
						var codeDiv = div.eq(1);
						var openCodes = codeDiv.children();
						var ul = "<ul><li class='ng-binding'>"+expect+"</li><li class='ng-binding'>"+code+"</li></ul>";
						codeDiv.prepend(ul);
						if (openCodes.length >= 5) {
							openCodes.eq(openCodes.length -1).remove();
						}
					}
				});
			});
		}

		//获取布局的数据结构
		vm.getLayout = function() {
			var layouturl = '';
			//如果切换大彩种，则先清除所有cookie
			switch (vm.id) {
				case "101":
				case "103":
				case "104":
				case "105":
				case "115":
				case "117":
				case "118":
				case "119":
				case "120":
				case "121":
				case "122":
				case "123":
				case "124":
				case "125":
				case "126":
				case "127":
				case "128":
				case "129":
					layouturl += '/static/js/layout/layout-ssc.json';
					vm.currentGame.cptype = "ssc";
					break;
				case "201":
				case "202":
				case "203":
				case "204":
				case "205":
				case "206":
					layouturl += '/static/js/layout/layout-11x5.json';
					vm.currentGame.cptype = "11x5";
					break;
				case "401":
				case "402":
				case "403":
					layouturl += '/static/js/layout/layout-3D.json';
					vm.currentGame.cptype = "3D";
					break;
				case "301":
				case "302":
				case "303":
				case "304":
				case "305":
				case "306":
				case "307":
				case "308":
				case "309":
				case "310":
					layouturl += '/static/js/layout/layout-k3.json';
					vm.currentGame.cptype = "k3";
					break;
				case "501":
				case "502":
					layouturl += '/static/js/layout/layout-k8.json';
					vm.currentGame.cptype = "k8";
					break;
				case "601":
				case "801":
				case "901":
					layouturl += '/static/js/layout/layout-pk10.json';
					vm.currentGame.cptype = "pk10";
					break;
				default:
					// statements_def
					break;
			}
			fetchDataService.fetchJson(layouturl).then(function(data) {
				vm.p_layout = data.data.Layout;
				for (var prop in vm.p_layout) {
					var temp = vm.p_layout[prop]['rows'];
					for (var p in temp) {
						vm.gameMethods.push(temp[p]);
					}
				}
				//获取相应玩法的布局的   
				var temp_p_data = $.grep(vm.gameMethods, function(element, index) {
					if (betUtilsService.getCookie('pname')) {
						return element.parentname === betUtilsService.getCookie('pname');
					} else {
						return element.default === '1';
					}
				});
				var shortname = betUtilsService.getCookie('method') || temp_p_data[0]['defaultmethod'];
				$.each(temp_p_data[0]['columns'], function(index, element) {
					if (element['shortname'] === shortname) {
						vm.booleanObj.isInput = element['selectarea']['type'] === 'digital' || element['selectarea']['type'] === 'dxds' ? true : false;
						var temp_data = element['selectarea']['layout'];
						vm.currentGame.method = element['shortname']; //设置玩法名如:wxzhixfs
						vm.currentGame.isrx = element['isrx'];
						vm.booleanObj.isRXinput = element['isrx'] === '1' && element['defaultposition'] != null ? true : false;
						vm.currentGame.cname_cn = temp_p_data[0]['title']; //当前玩法的父类中文名字===>后三
						vm.currentGame.name_cn = element['showname'];
						vm.currentGame.tips_note = element.tips; //玩法说明
						vm.currentGame.bet_note = element.example; //玩法示例
						vm.currentGame.bonus_note = element.help; //中奖说明
						vm.currentGame.compressed = element.compressed; // 是否压缩
						for (var prop in temp_data) {
							temp_data[prop].nums = temp_data[prop].balls;
						}
						//如果不是单式则判断是否是特殊号
						if (vm.booleanObj.isInput) {
							//判断是否特殊号码或和值  没有全大小单双清按钮
							vm.booleanObj.isTools = isNaN(temp_data[0].nums[0]) || element['selectarea']['isButton'] === false ? false : true;
							vm.booleanObj.isSpecial = isNaN(temp_data[0].nums[0]) ? true : false;
						}
						if (element['isrx'] === '1' && element['defaultposition'] != null) {
							vm.rxposition = [];
							vm.rxarray = [];
							vm.rxposition = element['defaultposition'].split('');
							vm.rxarray[0] = {
								id: vm.rxposition[0],
								title: '万'
							}
							vm.rxarray[1] = {
								id: vm.rxposition[1],
								title: '千'
							}
							vm.rxarray[2] = {
								id: vm.rxposition[2],
								title: '百'
							}
							vm.rxarray[3] = {
								id: vm.rxposition[3],
								title: '十'
							}
							vm.rxarray[4] = {
								id: vm.rxposition[4],
								title: '个'
							}
						}
						vm.layout = temp_data;
					}
				});
				vm.initbet();
			});
		}
		vm.getLayout();

		//下拉刷新
		vm.refresh = function() {
			$state.reload();
			$timeout(function() {
				$scope.$broadcast('scroll.refreshComplete');
			}, 1000);
		}

		vm.clearText = function() {
			$('#textarea').val('');
		}

		//玩法说明点击
		$ionicModal.fromTemplateUrl('/template/playintro.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modal = modal;
		});
		vm.openIntro = function() {
			vm.modal.show();
		};
		vm.closeIntro = function() {
			vm.modal.hide();
		};
		//end
		//点击玩法展示和切换
		$ionicPopover.fromTemplateUrl('/template/playlist.html', {
			scope: $scope,
		}).then(function(popover) {
			vm.popover = popover;
		});
		vm.openPopover = function($event) {
			vm.popover.show($event);
		};
		vm.closePopover = function() {
			vm.popover.hide();
		};

		//点击头部查看彩票种类
		$ionicPopover.fromTemplateUrl('/template/cplist.html', {
			scope: $scope,
		}).then(function(popover) {
			vm.cppopover = popover;
		});
		vm.opencpPopover = function($event) {
			vm.cppopover.show($event);
		};
		vm.closecpPopover = function() {
			vm.cppopover.hide();
		};

		$scope.$on('$destroy', function() {
			vm.closeIntro();
			vm.closePopover();
			vm.closecpPopover();
		});

		vm.routetocp = function(path, param) {
			betUtilsService.delCookie('pname');
			betUtilsService.delCookie('method');
			vm.cppopover.hide();
			$state.go(path, {
				id: param
			});
		}

		//绑定range事件
		$dom.on({
			input: function() {
				$('#prize_group').text($(this).val());
				var point = (Number($(this).attr('max')) - Number($(this).val())) * 100 / 2000 + Number($(this).attr('minpoint'));
				$('#point').text(point.toFixed(2) + "%");
				vm.currentGame.prize = $(this).val();
			},
			change: function() {
				$('#prize_group').text($(this).val());
				var point = (Number($(this).attr('max')) - Number($(this).val())) * 100 / 2000 + Number($(this).attr('minpoint'));
				$('#point').text(point.toFixed(2) + "%");
				vm.currentGame.prize = $(this).val();
			}
		}, "#modeajust");

		//绑定开奖记录折叠
		$('#collopseHistory').on('tap', function(e) {
			$(this).toggleClass('on');
			if ($(this).hasClass('on')) {
				$("#collopseIcon").removeClass('ion-arrow-down-b').addClass('ion-arrow-up-b');
			} else {
				$("#collopseIcon").removeClass('ion-arrow-up-b').addClass('ion-arrow-down-b');
			}
			$('#collopse').toggleClass('hidden');
			$ionicScrollDelegate.resize();
		});

		//绑定子玩法切换
		$dom.off('click', '#subplaylist li');
		$dom.on('click', '#subplaylist li', function(e) {
			e.preventDefault();
			//update vm.currentGame
			$('.playitem').removeClass('on');
			$(this).addClass('on');
			var shortname = $(this).data('method');
			var parentName = $(this).data('parent');
			//存入cookie 选择玩法
			betUtilsService.SetCookie('pname', parentName);
			betUtilsService.SetCookie('method', shortname);
			//获取相应玩法的布局
			var temp_p_data = $.grep(vm.gameMethods, function(element, index) {
				return element.parentname === parentName;
			});
			$.each(temp_p_data[0]['columns'], function(index, element) {
				if (element['shortname'] === shortname) {
					vm.booleanObj.isInput = element['selectarea']['type'] === 'digital' || element['selectarea']['type'] === 'dxds' ? true : false;
					var temp_data = element['selectarea']['layout'];
					vm.currentGame.method = element['shortname']; //设置玩法名如:wxzhixfs
					vm.currentGame.isrx = element['isrx'];
					vm.booleanObj.isRXinput = element['isrx'] === '1' && element['defaultposition'] != null ? true : false;
					vm.currentGame.cname_cn = temp_p_data[0]['title']; //当前玩法的类名字
					vm.currentGame.name_cn = element['showname'];
					vm.currentGame.tips_note = element.tips; //玩法说明
					vm.currentGame.bet_note = element.example; //玩法示例
					vm.currentGame.bonus_note = element.help; //中奖说明
					vm.currentGame.compressed = element.compressed; // 是否压缩
					var prizeArray = vm.playRules[vm.currentGame.method]['prize'].split(',');
					vm.currentGame.prize = Number(vm.UserData.code) / Number(prizeArray[0]) //当前玩法奖金
					for (var prop in temp_data) {
						temp_data[prop].nums = temp_data[prop].balls;
					}
					//如果不是单式则判断是否是特殊号
					if (vm.booleanObj.isInput) {
						//判断是否特殊号码或和值  没有全大小单双清按钮
						vm.booleanObj.isTools = isNaN(temp_data[0].nums[0]) || element['selectarea']['isButton'] === false ? false : true;
						vm.booleanObj.isSpecial = isNaN(temp_data[0].nums[0]) ? true : false;
					}
					if (element['isrx'] === '1' && element['defaultposition'] != null) {
						vm.rxposition = [];
						vm.rxarray = [];
						vm.rxposition = element['defaultposition'].split('');
						vm.rxarray[0] = {
							id: vm.rxposition[0],
							title: '万'
						}
						vm.rxarray[1] = {
							id: vm.rxposition[1],
							title: '千'
						}
						vm.rxarray[2] = {
							id: vm.rxposition[2],
							title: '百'
						}
						vm.rxarray[3] = {
							id: vm.rxposition[3],
							title: '十'
						}
						vm.rxarray[4] = {
							id: vm.rxposition[4],
							title: '个'
						}
					}
					vm.layout = temp_data;
				}
			});
			vm.popover.hide();
			betUtilsService.reset();
		});

		//选号选中效果
		$dom.off('tap', '#betArea li.ball');
		$dom.on('tap', '#betArea li.ball', function(e) {
			e.preventDefault();
			var title_en = $(this).data('enname');
			//判断是否胆拖玩法 according to title_en === 'danma'
			var isdanma = $('.lottery-num-ul').eq(0).data('enname'),
				max = $('.lottery-num-ul').eq(0).data('maxpic');
			//表示选择胆码
			if (isdanma) {
				if (title_en) {
					//选号前check是否选择大于了2
					var value = $(this).data('val');
					$.each($('.lottery-num-ul').eq(1).find('li.on'), function(index, el) {
						if ($(el).data('val') === value) {
							$(el).removeClass('on');
						}
					});
					var size = $('.lottery-num-ul').eq(0).find('li.on').size();
					if (size === max) {
						if (!$(this).hasClass('on')) {
							$(this).addClass('on current').siblings('li.current').removeClass('on current');
						}
						if ($(this).hasClass('on') && !$(this).hasClass('current')) {
							$(this).removeClass('on').siblings('li.current').removeClass('current');
						}
					} else {
						$(this).toggleClass('on current').siblings('li.on').removeClass('current');
					}
				} else {
					//如果拖胆选区 ---要去掉胆码里的数据
					var value = $(this).data('val');
					$.each($('.lottery-num-ul').eq(0).find('li.on'), function(index, el) {
						if ($(el).data('val') === value) {
							$(el).removeClass('on current');
						}
					});
					$(this).toggleClass('on');
				}
			} else {
				$(this).toggleClass('on');
			}
			betUtilsService.playOptions().update();
			e.stopPropagation();
		});

		//绑定全大小单双按钮
		$dom.on('tap', '#quickpick li', function(e) {
			e.preventDefault();
			var $target = $(this),
				max,
				$balls = $target.parent().parent().next('ul.lottery-num-ul').find('li.ball');
			max = Math.round($balls.length / 2);

			switch ($target.attr('data-command')) {
				case 'all':
					$balls.addClass('on');
					break
				case 'big':
					$.each($balls, function(i, d) {
						$(d)[i > max - 1 ? 'addClass' : 'removeClass']('on');
					})
					break
				case 'small':
					$.each($balls, function(i, d) {
						$(d)[i < max ? 'addClass' : 'removeClass']('on');
					})
					break
				case 'single':
					$.each($balls, function(i, d) {
						$(d)[parseInt($(d).children().html()) % 2 > 0 ? 'addClass' : 'removeClass']('on');
					})
					break
				case 'double':
					$.each($balls, function(i, d) {
						$(d)[parseInt($(d).children().html()) % 2 < 1 ? 'addClass' : 'removeClass']('on');
					})
					break
				case 'clean':
					$balls.removeClass('on');
					break
			}
			//对胆拖进行处理
			var isdanma = $('.lottery-num-ul').eq(0).data('enname');
			if (isdanma) {
				$.each($('.lottery-num-ul').eq(1).find('li.on'), function(index1, el1) {
					$.each($('.lottery-num-ul').eq(0).find('li.on'), function(index2, el2) {
						if ($(el1).data('val') === $(el2).data('val')) {
							$(el2).removeClass('on current');
						}
					});
				});
			}
			betUtilsService.playOptions().update();
		});

		//绑定倍数加减
		$dom.off('tap', '#minus,#plus');
		$dom.on('tap', '#minus,#plus', function(e) {
			e.preventDefault();
			var val = parseInt($('#multiple').val()),
				$target = $(e.target),
				multiple = $('#multiple');
			if ($target.hasClass('icon-minus')) {
				val > 0 && multiple.val(val - 1);
			} else {
				multiple.val(val + 1);
			}
			multiple.trigger('keyup');
			betUtilsService.SetCookie('multiple', multiple.val()); //倍数存入cookie
			betUtilsService.playOptions().update();
		});

		//倍数输入事件
		$dom.on('keyup', '#multiple', function(e) {
			e.preventDefault();
			var $target = $(e.target),
				val = $target.val();
			if (val === '')
				return
				//add by ian
			if (val.indexOf('0') === 0) {
				$target.val(Number(val) === 0 ? '1' : Number(val));
				return;
			}
			if (/^[0-9]*$/.test(val)) {
				val = Number(val);
				if (val > 10000) {
					//最大1万倍
					$target.val(10000);
				}
				if (val < 1) {
					$target.val(1);
				}
			} else {
				$target.val(1);
			}
			betUtilsService.SetCookie('multiple', $target.val()); //倍数存入cookie
			betUtilsService.playOptions().update();
		});

		//圆角分厘
		$dom.on('tap', '#yjfl div', function(e) {
			var $target = $(this);
			$target.addClass('active').siblings('div').removeClass('active');
			betUtilsService.SetCookie('mode', $target.data('val'));
			betUtilsService.playOptions().update();
		});

		//绑定任选位置选择
		$dom.off('tap', '#selposition li');
		$dom.on('tap', '#selposition li', function(e) {
			e.preventDefault();
			var $target = $(e.target);
			$target.toggleClass('active');
			betUtilsService.playOptions().update();
		});
		//绑定单式输入
		$dom.on('keyup', '#textarea', function(e) {
			var $target = $(e.target);
			var gameid = $('#gameId').val();
			switch (gameid) {
				case "101":
				case "103":
				case "104":
				case "105":
				case "115":
				case "117":
				case "118":
				case "119":
				case "120":
				case "121":
				case "122":
				case "124":
				case "125":
				case "126":
				case "127":
				case "128":
				case "129":
					$target.val($target.val().replace(/\,|\;|\n/g, ' '))
					break;
				case "201":
				case "202":
				case "203":
				case "204":
				case "205":
				case "206":
				case "501":
				case "502":
				case "601":
				case "801":
				case "901":
					$target.val($target.val().replace(/\,|\;|\n/g, ';'))
					break;
					break;
				default:
					$target.val($target.val().replace(/\,|\;|\n/g, ' '))
					break;
			}

			betUtilsService.playOptions().update();
		});

		//一键投注
		vm.quickbet = function() {
			vm.betObj = {};
			var ball = {},
				obj = {},
				method = $('#method').val(),
				cptype = $('#cptype').val();
			var nums = Number($('#nums').text());
			//如果注数为0则返回不继续进行
			if (nums === 0) {
				return;
			}
			//resemble the bet obj
			vm.betObj.blist = [];
			var ball = {};
			ball.lotteryId = vm.id; //彩种ID
			ball.num = nums; //注数
			ball.ruleId = vm.playRules[method].ruleId; //玩法ID
			ball.code = $('#modeajust').val();
			ball.model = $('#yjfl').find('div.active').data('val') //元角分模式
			obj.codes = lotteryUtilsService.inputFormat(method, betUtilsService.getData(), cptype);
			ball.codes = obj.codes; //选号球
			ball.multiple = $('#multiple').val(); //倍数
			if ($('#isrx').val() === '1' && !vm.booleanObj.isInput) {
				var temp = [];
				$('#selposition').find('li').each(function(index, el) {
					if ($(this).find('div').hasClass('active')) {
						temp.push('√');
					} else {
						temp.push('-');
					}
				});
				ball.codes = "[" + temp + "]" + ball.codes;
			}
			vm.betObj.blist.push(ball);
			//提交前弹出确定信息
			var confirmbet = $ionicPopup.confirm({
				title: '确定投注信息',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '<b>确定</b>',
					type: 'button-assertive',
					onTap: function() {
						$ionicLoading.show({template: '正在加载。。。'});

						if (vm.currentGame.compressed === true && nums >= 1000) {
							lzmaService.compress(ball.codes, function(result){
								if (!result) {
									$ionicLoading.hide();
									$ionicPopup.alert({
										title: '错误提示',
										template: '号码添加失败，请重试！'
									});
									return;
								}

								ball.codes = result;

								betUtilsService.reset();
								//submit data to submitURL
								ball.compressed = true; // 是否压缩
								$.ajax({
									url: '/UserBetsGeneral',
									type: 'POST',
									cache: false,
									dataType: 'json',
									contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
									data: {
										blist: JSON.stringify(vm.betObj.blist)
									},
									success: function(data) {
										// console.log(data);
										if (data.error === 0) {
											// vm.clearCart();
											var oktip = $ionicPopup.alert({
												title: '温馨提示',
												template: data.message
											});
											$timeout(function() {
												oktip.close();
												$state.go('bet', {
													id: betUtilsService.getCookie('cpid')
												});
											}, 2000);
											return;
										} else {
											$ionicPopup.alert({
												title: '温馨提示',
												template: data.message
											});
										}
									},
									error: function() {
										$ionicLoading.hide();
										$ionicPopup.alert({
											title: '温馨提示',
											template: '网络连接问题！'
										});
									},
									fail: function() {
										$ionicPopup.alert({
											title: '温馨提示',
											template: '网络连接问题！'
										});
									},
									complete: function() {
										$ionicLoading.hide();
									}
								});
							});
						}
						else {
							betUtilsService.reset();
							ball.compressed = false; // 是否压缩
							//submit data to submitURL
							$.ajax({
								url: '/UserBetsGeneral',
								type: 'POST',
								cache: false,
								dataType: 'json',
								contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
								data: {
									blist: JSON.stringify(vm.betObj.blist)
								},
								success: function(data) {
									$ionicLoading.hide();
									// console.log(data);
									if (data.error === 0) {
										// vm.clearCart();
										var oktip = $ionicPopup.alert({
											title: '温馨提示',
											template: data.message
										});
										$timeout(function() {
											oktip.close();
											$state.go('bet', {
												id: betUtilsService.getCookie('cpid')
											});
										}, 2000);
										return;
									} else {
										$ionicPopup.alert({
											title: '温馨提示',
											template: data.message
										});
									}
								},
								error: function() {
									$ionicLoading.hide();
									$ionicPopup.alert({
										title: '温馨提示',
										template: '网络连接问题！'
									});
								},
								fail: function() {
									$ionicPopup.alert({
										title: '温馨提示',
										template: '网络连接问题！'
									});
								},
								complete: function() {
									$ionicLoading.hide();
								}
							});
						}
					}
				}],
				template: vm.id === '117' ? '<p>总金额:' + $('#total').text() + '元</p>' : '<p>确定加入：' + vm.server.expect + '期</p><p>总金额:' + $('#total').text() + '元</p>'
			});
		}

		// //梭哈投注
		// vm.allbet = function() {
		// 	$ionicPopup.alert({
		// 		title: '温馨提示',
		// 		template: '即将开放，敬请期待！'
		// 	});
		// }

		//梭哈投注
		vm.allbet = function() {
			vm.betObj = {};
			var ball = {},
				balance = 0,
				obj = {},
				method = $('#method').val(),
				cptype = $('#cptype').val();
			var nums = Number($('#nums').text());
			// var total = multiple * num * bUnitMoney * PlayOptions.model().money;
			//如果注数为0则返回不继续进行
			if (nums === 0) {
				return;
			}
        
			//resemble the bet obj
			vm.betObj.blist = [];
			var ball = {};
			ball.lotteryId = vm.id; //彩种ID
			ball.num = nums; //注数
			ball.ruleId = vm.playRules[method].ruleId; //玩法名
			ball.code = $('#modeajust').val();
			ball.model = 'yuan'; // 默认最高元模式
			ball.codes = lotteryUtilsService.inputFormat(method, betUtilsService.getData(), cptype);
			ball.multiple = $('#multiple').val(); //倍数
			vm.betObj.blist.push(ball);
			// 默认最高元模式 元1，角0.1，分0.01，厘0.001
			var total = Number(ball.multiple) * Number(1) * 2 * Number(nums);
			//提交前查看余额
			postDataService.getPostData({
				platformId: 2
			}, '/AccountBalance').then(function(data) {
				if (data.data.error === 0) {
					balance = Number(data.data.data.balance.toFixed(5));
				}
				if (balance < total) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '您的余额不足，不能梭哈！'
					});
					return;
				}
				ball.multiple = Math.floor(balance / total);
				if (ball.multiple > 10000) {
					ball.multiple = 10000;
				}
				total = total * ball.multiple;
				//提交前弹出确定信息
				var confirmbet = $ionicPopup.confirm({
					title: '确定投注信息',
					buttons: [{
						text: '取消',
						type: 'button-dark'
					}, {
						text: '<b>确定</b>',
						type: 'button-assertive',
						onTap: function() {
							$ionicLoading.show({template: '正在加载。。。'});
        
							if (vm.currentGame.compressed === true && nums >= 1000) {
								lzmaService.compress(ball.codes, function(result){
									if (!result) {
										$ionicLoading.hide();
										$ionicPopup.alert({
											title: '错误提示',
											template: '号码添加失败，请重试！'
										});
										return;
									}
        
									ball.codes = result;
        
									betUtilsService.reset();
									//submit data to submitURL
									ball.compressed = true; // 是否压缩
									$.ajax({
										url: '/UserBetsGeneral',
										type: 'POST',
										cache: false,
										dataType: 'json',
										contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
										data: {
											blist: JSON.stringify(vm.betObj.blist)
										},
										success: function(data) {
											// console.log(data);
											if (data.error === 0) {
												// vm.clearCart();
												var oktip = $ionicPopup.alert({
													title: '温馨提示',
													template: data.message
												});
												$timeout(function() {
													oktip.close();
													$state.go('bet', {
														id: betUtilsService.getCookie('cpid')
													});
												}, 2000);
												return;
											} else {
												$ionicPopup.alert({
													title: '温馨提示',
													template: data.message
												});
											}
										},
										error: function() {
											$ionicLoading.hide();
											$ionicPopup.alert({
												title: '温馨提示',
												template: '网络连接问题！'
											});
										},
										fail: function() {
											$ionicPopup.alert({
												title: '温馨提示',
												template: '网络连接问题！'
											});
										},
										complete: function() {
											$ionicLoading.hide();
										}
									});
								});
							}
							else {
								betUtilsService.reset();
								ball.compressed = false; // 是否压缩
								//submit data to submitURL
								$.ajax({
									url: '/UserBetsGeneral',
									type: 'POST',
									cache: false,
									dataType: 'json',
									contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
									data: {
										blist: JSON.stringify(vm.betObj.blist)
									},
									success: function(data) {
										$ionicLoading.hide();
										// console.log(data);
										if (data.error === 0) {
											// vm.clearCart();
											var oktip = $ionicPopup.alert({
												title: '温馨提示',
												template: data.message
											});
											$timeout(function() {
												oktip.close();
												$state.go('bet', {
													id: betUtilsService.getCookie('cpid')
												});
											}, 2000);
											return;
										} else {
											$ionicPopup.alert({
												title: '温馨提示',
												template: data.message
											});
										}
									},
									error: function() {
										$ionicLoading.hide();
										$ionicPopup.alert({
											title: '温馨提示',
											template: '网络连接问题！'
										});
									},
									fail: function() {
										$ionicPopup.alert({
											title: '温馨提示',
											template: '网络连接问题！'
										});
									},
									complete: function() {
										$ionicLoading.hide();
									}
								});
							}
						}
					}],
					template: '<p>确定加入：' + vm.server.expect + '期</p><p>总倍数:' + ball.multiple + '</p><p>总金额:' + total + '元</p>'
				});
			});
        
		}

		//添加购物车
		$('#add_to_cart').on('tap', function(e) {
			var nums = Number($('#nums').text());
			//如果注数为0则返回不继续进行
			if (nums === 0) {
				return;
			}
			//组装购物车数据
			var obj = {},
				finalObj = {},
				method = $('#method').val(),
				cptype = $('#cptype').val();
			obj.position = []; //任选玩法的位置
			obj.name_cn = vm.currentGame.cname_cn + "-" + vm.currentGame.name_cn;
			obj.zhushu = nums;
			obj.ruleId = vm.playRules[method].ruleId;
			obj.code = $('#modeajust').val();
			obj.times = $('#multiple').val();
			obj.moneyunit = $('#yjfl').find('div.active').data('val'); //元角分里
			obj.permoney = Number($('#total').text());
			obj.money = Number($('#total').text());
			obj.codes = lotteryUtilsService.inputFormat(method, betUtilsService.getData(), cptype);
			obj.compressed = vm.currentGame.compressed;
			if ($('#isrx').val() === '1' && !vm.booleanObj.isInput) {
				var temp = [];
				$('#selposition').find('li').each(function(index, el) {
					if ($(this).find('div').hasClass('active')) {
						temp.push('√');
					} else {
						temp.push('-');
					}
				});
				obj.codes = "[" + temp + "]" + obj.codes;
			}
			//处理选号格式
			//判断codes是否传过来是数组---针对任选单式的


			shareDataService.add(obj);
			//组装基本信息总注数 金额 期号 彩种id 投注url...
			//如果是急速秒秒彩 则没有奖期
			if (vm.id != '117') {
				shareDataService.base.issue = vm.server.expect;
			}
			shareDataService.base.cpname = vm.lottery.showName;
			shareDataService.base.lotteryId = vm.id;
			shareDataService.base.submitUrl = '/UserBetsGeneral';
			betUtilsService.reset();
			$('#cart-note-num').removeClass('hidden');
			var totalNums = Number($('#cart-note-num').html());
			$('#cart-note-num').html(1 + totalNums);
		});

		//点击号码篮
		$('#gotoCart').on('tap', function() {
			if ($('#cart-note-num').hasClass('hidden')) {
				return;
			}
			$state.go('cpcart');
		});
		//toggleLeftSideMenu
		vm.toggleLeftSideMenu = function() {
			postDataService.getPostData({
				SysMsgId: 0
			}, '/GetGlobal').then(function(data) {
				vm.globalData = data.data.data.uBean;
			});
			$ionicSideMenuDelegate.toggleLeft();
		}
	}

	//购彩车控制器
	var cartController = function($rootScope, $scope, $ionicLoading, $state, shareDataService, postDataService, $ionicPopup, $timeout, betUtilsService, lzmaService) {
		// console.log('购物车倒计时结束刷新' + $rootScope.lt_time_leave);
		var vm = this;
		vm.currentObj = shareDataService.base;
		vm.cartlist = shareDataService.list;
		vm.betObj = {};
		//if the cart empty then go to ssc view
		if (!vm.cartlist.length) {
			$state.go('bet', {
				id: betUtilsService.getCookie('cpid')
			});
		}
		if ($rootScope.lt_time_leave === 0 && betUtilsService.getCookie('cpid') != '117') {
			$state.go('bet', {
				id: betUtilsService.getCookie('cpid')
			});
		}
		vm.setTotals = function() {
			var totalNums = 0,
				totalMoney = 0;
			for (var prop in vm.cartlist) {
				totalNums += Number(vm.cartlist[prop].zhushu);
				totalMoney += Number(vm.cartlist[prop].money);
			}
			vm.currentObj.totalMoney = totalMoney;
			vm.currentObj.totalNums = totalNums;
		}

		//组装投注数据
		var shouldCompress = false; // 是否应该压缩数据
		vm.resembleData = function() {
			//resemble the bet obj
			vm.betObj.blist = [];
			for (var prop in vm.cartlist) {
				var ball = {};
				ball.lotteryId = shareDataService.base.lotteryId; //彩种ID
				ball.num = vm.cartlist[prop].zhushu; //注数
				ball.ruleId = vm.cartlist[prop].ruleId; //玩法ID
				ball.code = vm.cartlist[prop].code;; //奖金返点
				ball.model = vm.cartlist[prop].moneyunit; //元角分模式
				ball.multiple = vm.cartlist[prop].times; //倍数
				ball.codes = vm.cartlist[prop].codes; //号码
				ball.compressed = vm.cartlist[prop].compressed;
				if (ball.compressed === true && ball.num >= 1000) {
					shouldCompress = true;
				}
				else {
					ball.compressed = false;
				}
				vm.betObj.blist.push(ball);
			}
			//store the betObj to shareDataService;
			shareDataService.betObj = vm.betObj;
		}

		//initial the page
		vm.setTotals();
		vm.resembleData();

		//立即投注
		vm.betsubmit = function() {
			//投注前判断是否以封单
			if (!vm.cartlist.length) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请添加号码！'
				}).then(function() {
					$state.go('bet', {
						id: betUtilsService.getCookie('cpid')
					});

				});
				return;
			}
			//重新组装数据如果修改倍数了
			vm.resembleData();

			//提交前弹出确定信息
			var confirmbet = $ionicPopup.confirm({
				title: '确定投注信息',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '<b>确定</b>',
					type: 'button-assertive',
					onTap: function() {
						//submit data to submitURL     before=> submitData
						$ionicLoading.show({template: '正在加载。。。'});
						if (shouldCompress) {
							var faildCompressed = false;
							var checkCount = 0;
							vm.betObj.blist.forEach(function(data, index, array){
								if (data.compressed === true && data.num >= 1000) {
									lzmaService.compress(data.codes, function(result) {
										if (!result) {
											$ionicLoading.hide();
											$ionicPopup.alert({
												title: '错误提示',
												template: '号码添加失败，请重试！'
											});
											faildCompressed = true;
										}
										else {
											data.codes = result;
											data.compressed = true;
											checkCount++;
										}
									});
								}
								else {
									data.compressed = false; // 是否压缩
									checkCount++;
								}
							});
							// 开始投注
							var checkCompress = setInterval(function(){
								if (faildCompressed) {
									clearInterval(checkCompress);
								}
								else {
									if (checkCount >= vm.betObj.blist.length) {
										clearInterval(checkCompress);
										$timeout(function(){
											$.ajax({
												url: vm.currentObj.submitUrl,
												type: 'POST',
												cache: false,
												dataType: 'json',
												contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
												data: {
													blist: JSON.stringify(vm.betObj.blist)
												},
												success: function(data) {
													if (data.error === 0) {
														vm.clearCart();
														var oktip = $ionicPopup.alert({
															title: '温馨提示',
															template: data.message
														});
														$timeout(function() {
															oktip.close();
															$state.go('bet', {
																id: betUtilsService.getCookie('cpid')
															});
														}, 1000);
														return;
													} else {
														$ionicPopup.alert({
															title: '温馨提示',
															template: data.message
														});
													}
												},
												complete: function() {
													$ionicLoading.hide();
												}
											});
										}, 100);
									}
								}
							}, 100);
						}
						else {
							$.ajax({
								url: vm.currentObj.submitUrl,
								type: 'POST',
								cache: false,
								dataType: 'json',
								contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
								data: {
									blist: JSON.stringify(vm.betObj.blist)
								},
								success: function(data) {
									if (data.error === 0) {
										vm.clearCart();
										var oktip = $ionicPopup.alert({
											title: '温馨提示',
											template: data.message
										});
										$timeout(function() {
											oktip.close();
											$state.go('bet', {
												id: betUtilsService.getCookie('cpid')
											});
										}, 1000);
										return;
									} else {
										$ionicPopup.alert({
											title: '温馨提示',
											template: data.message
										});
									}
								},
								complete: function() {
									$ionicLoading.hide();
								}
							});
						}
					}
				}],
				template: '<p>确定加入：' + shareDataService.base.issue + '期</p><p>总金额:' + shareDataService.base.totalMoney + '元</p>'
			});
		}

		//清空购彩车
		vm.clearCart = function() {
			vm.currentObj.totalNums = 0;
			vm.currentObj.totalMoney = 0;
			vm.cartlist.length = 0;
		}

		//返回
		vm.back = function() {
			$state.go('bet', {
				id: betUtilsService.getCookie('cpid')
			});
		}

		//继续购彩
		vm.continusBuy = function() {
			$state.go('bet', {
				id: betUtilsService.getCookie('cpid')
			});
		}

		//我要追号
		vm.trace = function() {
			$state.go('trace');
		}

		//单注删除
		vm.delSingleCart = function(index) {
			vm.cartlist = shareDataService.remove(index);
			vm.setTotals(); //重新设置金额
		}

		//绑定倍数加减
		$dom.off('tap', '.betcat-plus,.betcat-min');
		$dom.on('tap', '.betcat-plus,.betcat-min', function(e) {
			e.preventDefault();
			var $target = $(e.target),
				val = parseInt($target.siblings('.betcat-mult').val()),
				multiple = $target.siblings('.betcat-mult');
			if ($target.hasClass('icon-minus')) {
				val > 0 && multiple.val(val - 1);
			} else {
				multiple.val(val + 1);
			}
			multiple.trigger('keyup');
		});

		//倍数输入事件
		$dom.off('keyup', '.betcat-mult');
		$dom.on('keyup', '.betcat-mult', function(e) {
			e.preventDefault();
			var $target = $(e.target),
				val = $target.val();
			if (val === '') {
				$target.val(1);
				return
			}
			//add by ian
			if (val.indexOf('0') === 0) {
				$target.val(Number(val) === 0 ? '1' : Number(val));
				return;
			}
			if (/^[0-9]*$/.test(val)) {
				val = Number(val);
				if (val > 10000) {
					//最大1万倍
					$target.val(10000);
				}
				if (val < 1) {
					$target.val(1);
				}
			} else {
				$target.val(1);
			}
			var money = Number($target.siblings('#baseMoney').val()) * Number($target.val()),
				tag = $target.siblings('#index').val();
			$target.siblings('.betcat-money').text(money);
			var item = $.grep(vm.cartlist, function(element, index) {
				return index === Number(tag);
			});
			//cause grep return a array so we use item[0] get the object;
			//You shouldn't ever need to call $apply unless you are interfacing from a non-Angular event. The existence of $apply usually means I am doing something wrong (unless, again, the $apply happens from a non-Angular event).
			//时时同步model
			$scope.$apply(function() {
				item[0].times = $target.val();
				item[0].money = money;
				vm.cartlist.splice(tag, 1, item[0]); //更新数组
				vm.setTotals();
			});
		});
	}

	//我要追号控制器
	var traceController = function($rootScope, $scope, $state, shareDataService, $ionicLoading, betUtilsService, $ionicPopup, $timeout, postDataService) {
		var vm = this;
		vm.isStop = true; //中奖后是否停止
		vm.traceList = [];
		vm.currentObj = shareDataService.base;

		// if (!vm.traceList.length) {
		// 	$state.go('bet', {
		// 		id: betUtilsService.getCookie('cpid')
		// 	});
		// }
		if ($rootScope.lt_time_leave === 0) {
			$state.go('bet', {
				id: betUtilsService.getCookie('cpid')
			});
		}

		//获取所有追号期号
		vm.back = function() {
			$state.go('cpcart');
		}

		//获取追期数
		postDataService.getPostData({
			lotteryId: shareDataService.base.lotteryId,
			count: 100
		}, '/LotteryChaseTime').then(function(data) {
			vm.traceIssues = data.data.data;
			vm.traceMax = vm.traceIssues.length;
		});

		//切换追号类型
		vm.setType = function(value) {
			$('#traceType').find('li[data-type="' + value + '"]').addClass('active').siblings('li').removeClass('active');
			//vm.issame = !vm.issame;
			if (value === 'double') {
				vm.issame = true;
			} else {
				vm.issame = false;
			}
			vm.isCreate = false;
		}

		//中奖后是否停止追号
		vm.isTraceStop = function() {
			return $('#checkstop').find('input').hasClass('ng-not-empty');
		}

		//绑定快速选择
		vm.quickpick = function(num) {
			$('#quickul').find('li div[data-value="' + num + '"]').addClass('active').parent().siblings('li').find('div').removeClass('active');
			$('#issueNums').val(num);
		}

		$dom.on('change', '#issueNums', function() {
			var maxIssue = Number($('#maxissue').text());
			var currentIssue = Number($(this).val());
			if (currentIssue > maxIssue) {
				$(this).val(maxIssue);
			}
		});
		$dom.off('change', '.manulTimes', function() {});
		$dom.on('change', '.manulTimes', function(e) {
			e.preventDefault();
			if ($(this).val() === '0' || $(this).val() === '') {
				// $(this).val('1');
				return;
			}
			var amountSpan = $(this).parent('div').parent('li').next('li').find('span'),
				initValue = Number(amountSpan.attr("initval")) / Number(amountSpan.attr("initm")),
				total = $('#chase-total-money');
			amountSpan.text(initValue * Number($(this).val()));
			var totalval = 0;
			$(this).parent('div').parent('li').parent('ul').parent().find('span.rowmoney').each(function(i,o){
				totalval += Number($(o).html());
			});
			total.text(totalval);
			var td = [];
			$(this).parent('div').parent('li').parent('ul').parent().find('ul').each(function(i,o){
				var lotexp = $(o).find(".lotexp").html();
				var manulTimes = Number($(o).find(".manulTimes").val());
				var rowmoney = Number($(o).find(".rowmoney").html());
				td.push({
					multiple: manulTimes,
					money: rowmoney,
					expect: lotexp
				});
			});
			vm.traceList = td;
		});

		vm.minusTime = function() {
			var currentTime = Number($('#tracetimes').val());
			if (currentTime === 1) {
				return;
			}
			$('#tracetimes').val(currentTime - 1);
		}
		vm.plusTime = function() {
			var currentTime = Number($('#tracetimes').val());
			$('#tracetimes').val(currentTime + 1);
		}

		//生成追号方法
		vm.createTrace = function() {
			var type = $('#traceType').find('li.active').data('type'), //追号类型
				times = $('#tracetimes').val(), //追号起始倍数
				trace_nums = $('#issueNums').val(), //追号期数
				trace_total = vm.traceIssues.length, //全部期数
				td = [], //根据用户填写的条件生成的每期数据
				tm = 0, //生成后的总金额
				m = Number(shareDataService.base.totalMoney), //每期金额的初始值
				msg = ''; //提示信息
			trace_nums = isNaN(trace_nums) ? 0 : Number(trace_nums);
			//期数没有填
			if (trace_nums <= 0) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请填写期数！'
				});
				return false;
			}
			if (Number(trace_nums) > trace_total) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '追号期数不正确，超出了最大可追号期数！'
				});
				return;
			}
			if (type === 'same') {
				for (var i = 0; i < trace_nums; i++) {
					td.push({
						multiple: times,
						money: m * times,
						expect: vm.traceIssues[i]['expect']
					});
					tm += m * times;
				}
			}
			if (type === 'double') {
				var d = parseInt($("#lt_trace_diff").val(), 10); //相隔期数
				var t = parseInt($("#start_times").val(), 10); //起始倍数为1
				d = isNaN(d) ? 0 : d;
				if (d <= 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '请填写相隔期数!'
					});
					return false;
				}
				for (var i = 0; i < trace_nums; i++) {
					if (i != 0 && (i % d) == 0) {
						t *= times;
					}
					td.push({
						multiple: t,
						money: m * t,
						expect: vm.traceIssues[i]['expect']
					});
					tm += m * t;
				}

			}
			if (!td.length) {
				return;
			}
			vm.isCreate = true;
			vm.traceList = td;
			$('#chase-total-nums').text(trace_nums);
			$('#chase-total-money').text(tm);
		}

		//确定追号投注
		vm.confirmbet = function() {
			if (!vm.traceList.length) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请生成追号！'
				});
				return;
			}
			vm.isStop = vm.isTraceStop();
			//组装追号
			// var orders = [];
			// for (var prop in vm.traceList) {
			// 	shareDataService.betObj.orders[vm.traceList[prop]['expect']] = vm.traceList[prop]['times'];
			// }
			//submit data to submitURL
			$ionicLoading.show({
				template: '正在加载。。。'
			});
			$.ajax({
				url: '/UserBetsChase',
				type: 'POST',
				cache: false,
				dataType: 'json',
				contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
				data: {
					lotteryId: shareDataService.base.lotteryId,
					blist: JSON.stringify(shareDataService.betObj.blist),
					clist: JSON.stringify(vm.traceList),
					isStop: vm.isStop
				},
				success: function(data) {
					$ionicLoading.hide();
					if (data.error === 0) {
						//清空数据
						shareDataService.list.length = 0;
						var oktip = $ionicPopup.alert({
							title: '温馨提示',
							template: data.message
						});
						$timeout(function() {
							oktip.close();
							$state.go('bet', {
								id: betUtilsService.getCookie('cpid')
							});
						}, 1000);
						return;
					} else {
						$ionicPopup.alert({
							title: '温馨提示',
							template: data.message
						});
					}
				},
				error: function() {
					$ionicLoading.hide();
				}
			});
		}
	}

	//彩票记录/老虎机/真人记录/追号记录/帐变记录
	var betRecordController = function($scope, $filter, $q, $timeout, $ionicScrollDelegate, $state, $ionicPopup, $ionicModal, $ionicLoading,
									   shareDataService, postDataService, fetchDataService, betUtilsService) {
		var vm = this;
		vm.hasMore = false; // 默认没有更多
		vm.page = vm.page || 1;
		vm.limit = 10;
		var sTime = new Date(),eTime = new Date();
		// 默认值
		vm.searchObj = {
			limit: vm.limit,
			start: (vm.page - 1) * vm.limit,
			expect: '',
			sTime: sTime,
			eTime: eTime,
			lotteryId: '',
			status: '',
			account: '',
			type: '',
			platformId: 11
		}
		vm.getNetTime = function(date){
			var _eTime = new Date(date);
			_eTime.setDate(_eTime.getDate()+1);
			return _eTime;
		}
		vm.betObj = {}
		vm.getSearchParam = function(){
			if (vm.type == 'lottery' || vm.type == 'trace') {
				vm.betObj = {
					limit: vm.limit,
					start: (vm.page - 1) * vm.limit,
					sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
					eTime: $filter('date')(vm.getNetTime(vm.searchObj.eTime), 'yyyy-MM-dd'),
					lotteryId: vm.searchObj.lotteryId,
					expect: vm.searchObj.expect
				}
			}
			else if (vm.type == 'game') {
				vm.betObj = {
					limit: vm.limit,
					start: (vm.page - 1) * vm.limit,
					sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd '),
					eTime: $filter('date')(vm.getNetTime(vm.searchObj.eTime), 'yyyy-MM-dd '),
					platformId: vm.searchObj.platformId
				}
			}
			else if (vm.type == 'changes') {
				vm.betObj = {
					limit: vm.limit,
					start: (vm.page - 1) * vm.limit,
					sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
					eTime: $filter('date')(vm.getNetTime(vm.searchObj.eTime), 'yyyy-MM-dd')
				}
			}
		}
		vm.back = function() {
			$state.go('bet', {
				id: betUtilsService.getCookie('cpid')
			});
		}

		vm.type = $state.params.type;
		var searchURL = vm.type == 'lottery' ? '/UserBetsGeneralSearch'
			: vm.type == 'game' ? '/UserGameBetsSearch'
			: vm.type == 'trace' ? '/UserBetsChaseSearch'
			: vm.type == 'changes' ? '/UserBillSearch' : '';

		vm.init = function() {
			//获取投注记录
			vm.getSearchParam();
			$q.all([postDataService.getPostData(null, '/GetLottery'), postDataService.submitData(vm.betObj, searchURL)]).then(function(result) {
				var cplist = new Array();
				for(var i = 0; i < result[0].data.data.length; i++) {
					if(result[0].data.data[i].status == 0){
						cplist.push(result[0].data.data[i]);
					}
				}
				vm.cplist = cplist;
				vm.records = result[1].data.data;
				vm.hasMore = vm.records.length >= vm.limit;
			});
		}

		vm.init();

		//条件查询
		vm.search = function() {
			vm.page = 1;
			vm.getSearchParam();
			postDataService.submitData(vm.betObj, searchURL).then(function(data) {
				vm.records = data.data.data;
				vm.hasMore = vm.records.length >= vm.limit;
				$ionicScrollDelegate.scrollTop();
			});
		}

		$ionicModal.fromTemplateUrl('/template/betrecordDetail.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modal = modal;
		});
		vm.openModal = function() {
			vm.modal.show();
		};
		vm.closeModal = function() {
			vm.modal.hide();
		};

		// 关闭所有窗口
		$scope.$on("$destroy", function(){
			vm.closeModal();
		});

		//查看详情
		vm.checkDetail = function(id) {
			postDataService.submitData({
				id: id
			}, '/UserBetsDetails').then(function(data) {
				vm.currentRecord = data.data.data;
				vm.openModal();
			});
			// vm.cancelflag = record.bean.status === 0 ? true : false;
			// vm.currentRecord = record;
			// vm.openModal();
		}

		//撤单功能
		vm.cancelOrder = function(bean, type) {
			var obj = {};
			if (vm.type === 'chase') {
				obj = {
					chaseBillno: bean.chaseBillno,
					type: type
				}
			} else {
				obj = {
					id: bean.id,
					type: type
				}
			}
			$ionicPopup.confirm({
				title: '温馨提示',
				template: '确定要撤单吗?',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '确定',
					type: 'button-assertive',
					onTap: function() {
						postDataService.submitData(obj, '/UserBetsCancel').then(function(data) {
							if (data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '撤单成功！'
								});
								vm.closeModal();
								$state.reload();
							} else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}

						});
					}
				}]
			});
		}

		//下拉
		vm.refresh = function() {
			vm.search();
			$timeout(function() {
				$scope.$broadcast('scroll.refreshComplete');
			}, 1000);
		}

		//上拉
		vm.loadmore = function(page) {
			if (!vm.records || vm.records.length < 10) {
				vm.hasMore = false;
				$scope.$broadcast('scroll.infiniteScrollComplete');
				return;
			}
			vm.page = vm.page + 1;
			vm.getSearchParam();
			postDataService.submitData(vm.betObj, searchURL).then(function(data) {
				var datas = data.data.data;
				vm.hasMore = datas.length >= vm.limit ? true : false;
				for (var p in datas) {
					vm.records.push(datas[p]);
				}
				$scope.$broadcast('scroll.infiniteScrollComplete');
			});
		}
	}

	//充提记录控制器
	var chargeRecordController = function($scope, $filter, $state, $ionicPopup, $ionicScrollDelegate, $timeout, postDataService, $ionicModal, fetchDataService, betUtilsService) {
		var vm = this;
		vm.page = vm.page || 1;
		vm.limit = 10;
		vm.showcharge = true;
		vm.showwith = false;
		vm.showtrans = false;
		var sTime = new Date(),
			eTime = new Date();
		sTime.setHours(0, 0, 0);
		eTime.setDate(eTime.getDate());
		eTime.setHours(23, 59, 59);
		vm.searchObj = {
			limit: vm.limit,
			start: (vm.page - 1) * vm.limit,
			sTime: sTime,
			eTime: eTime,
			billno: '',
			type: '',
			status: '',
		}
		vm.back = function() {
			$state.go('bet', {
				id: betUtilsService.getCookie('cpid')
			});
		}
		var type = $state.params.type;
		//充值和提现和账变记录和转账记录切换
		vm.chargetrecord = function(type) {
			vm.page = 1;
			// vm.records = [];
			vm.noMorePage = false;
			$('#betrecord').find('a.' + type).addClass('active').siblings('a').removeClass('active');
			   var _eTime = new Date(vm.searchObj.eTime);
			   _eTime.setDate(_eTime.getDate()+1);
			switch (type) {
				case "charge":
					vm.betObj = {
						limit: 10,
						start: 0,
						billno: vm.searchObj.billno,
						type: vm.searchObj.type,
						account: vm.searchObj.account,
						sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
						eTime: $filter('date')(_eTime, 'yyyy-MM-dd')
					}

					//获取充值记录
					postDataService.submitData(vm.betObj, '/UserRechargeSearch').then(function(data) {
						vm.records = data.data.data.data;
						vm.showcharge = true;
						vm.showwith = false;
						vm.showtrans = false;
					});
					break;
				case "withdraw":
					vm.betObj = {
						limit: 10,
						start: 0,
						billno: vm.searchObj.billno,
						type: vm.searchObj.type,
						account: vm.searchObj.account,
						sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
						eTime: $filter('date')(_eTime, 'yyyy-MM-dd'),
						status: vm.searchObj.status
					}

					//获取提现记录
					postDataService.submitData(vm.betObj, '/UserWithdrawalsSearch').then(function(data) {
						vm.records = data.data.data.data;
						vm.showcharge = false;
						vm.showwith = true;
						vm.showtrans = false;
					});
					break;
				case "changes":
					vm.betObj = {
						limit: 10,
						start: 0,
						account: vm.searchObj.account,
						sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
						eTime: $filter('date')(_eTime, 'yyyy-MM-dd'),
						type: vm.searchObj.type,
					}

					//账变记录
					postDataService.submitData(vm.betObj, '/ProxyBillSearch').then(function(data) {
						vm.records = data.data.data;
						vm.showcharge = false;
						vm.showwith = false;
						vm.showchanges = true;
						vm.showtrans = false;
					});
					break;
				case "trans":
					vm.betObj = {
						limit: 10,
						start: 0,
						billno: vm.searchObj.billno,
						type: vm.searchObj.type,
						account: vm.searchObj.account,
						sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
						eTime: $filter('date')(_eTime, 'yyyy-MM-dd')
					}

					//转账记录
					postDataService.submitData(vm.betObj, '/UserTransfersSearch').then(function(data) {
						vm.records = data.data.data.data;
						vm.showcharge = false;
						vm.showwith = false;
						vm.showtrans = true;
					});
					break;
			}
		}

		vm.chargetrecord(type);

		//条件查询
		vm.searchRecord = function() {
			var type = $state.params.type,
				url = '';
			switch (type) {
				case "charge":
					url = '/UserRechargeSearch';
					break;
				case "withdraw":
					url = '/UserWithdrawalsSearch';
					break;
				case "trans":
					url = '/UserTransfersSearch';
					break;
			}
			//获取投注记录
			vm.betObj = {
				limit: 10,
				start: 0,
				billno: vm.searchObj.billno,
				account: vm.searchObj.account,
				type: vm.searchObj.type,
				sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
				eTime: $filter('date')(vm.searchObj.eTime, 'yyyy-MM-dd'),
				status: vm.searchObj.status
			}
			postDataService.submitData(vm.betObj, url).then(function(data) {
				vm.records = data.data.data;
				$ionicScrollDelegate.resize();
			});
		}

		$ionicModal.fromTemplateUrl('/template/chargeRecordDetail.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modal = modal;
		});
		vm.openModal = function() {
			vm.modal.show();
		};
		vm.closeModal = function() {
			vm.modal.hide();
		};
		// 关闭所有窗口
		$scope.$on("$destroy", function(){
			vm.closeModal();
		});

		//查看详情
		vm.checkDetail = function(item) {
			vm.currentRecord = item;
			vm.openModal();
		}

		//下拉
		vm.reflesh = function() {
			vm.searchRecord();
			$timeout(function() {
				$scope.$broadcast('scroll.refreshComplete');
			}, 1000);
		}

		//上拉
		vm.loadmore = function(page) {
			if (!vm.records || vm.records.length < 10) {
				vm.noMorePage = false;
				$scope.$broadcast('scroll.infiniteScrollComplete');
				return;
			}
			vm.noMorePage = true;
			var type = $('.tab-item.active').data('type');
			vm.page = page + 1;
			switch (type) {
				case "charge":
					//获取投注记录
					postDataService.submitData({
						limit: vm.limit,
						start: (vm.page - 1) * vm.limit,
						billno: vm.searchObj.billno,
						type: vm.searchObj.type,
						sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
						eTime: $filter('date')(vm.searchObj.eTime, 'yyyy-MM-dd')
					}, '/UserRechargeSearch/').then(function(data) {
						var datas = data.data.data.data;
						if (!datas.length) {
							//没有更多内容了
							vm.noMorePage = true;
						} else {
							vm.noMorePage = false;
							for (var p in datas) {
								vm.records.push(datas[p]);
							}
						}
						$scope.$broadcast('scroll.infiniteScrollComplete');
					});
					break;
				case "withdraw":
					//获取投注记录
					postDataService.submitData({
						limit: vm.limit,
						start: (vm.page - 1) * vm.limit,
						billno: vm.searchObj.billno,
						sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
						eTime: $filter('date')(vm.searchObj.eTime, 'yyyy-MM-dd'),
						status: vm.searchObj.status
					}, '/UserWithdrawalsSearch/').then(function(data) {
						var datas = data.data.data.data;
						if (!datas.length) {
							//没有更多内容了
							vm.noMorePage = true;
						} else {
							vm.noMorePage = false;
							for (var p in datas) {
								vm.records.push(datas[p]);
							}
						}
						$scope.$broadcast('scroll.infiniteScrollComplete');
					});
					break;
				case "changes":
					vm.betObj = {
						limit: vm.limit,
						start: (vm.page - 1) * vm.limit,
						billno: vm.searchObj.billno,
						sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
						eTime: $filter('date')(vm.searchObj.eTime, 'yyyy-MM-dd'),
						type: vm.searchObj.type
					}

					//获取投注记录
					postDataService.submitData(vm.betObj, '/mobile-transactions/').then(function(data) {
						var datas = data.data.data.data;
						if (!datas.length) {
							//没有更多内容了
							vm.noMorePage = true;
						} else {
							vm.noMorePage = false;
							for (var p in datas) {
								vm.records.push(datas[p]);
							}
						}
						$scope.$broadcast('scroll.infiniteScrollComplete');
					});
					break;
				case "trans":
					//获取投注记录
					postDataService.submitData({
						limit: vm.limit,
						start: (vm.page - 1) * vm.limit,
						billno: vm.searchObj.billno,
						account: vm.searchObj.account,
						type: vm.searchObj.type,
						sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
						eTime: $filter('date')(vm.searchObj.eTime, 'yyyy-MM-dd')
					}, '/UserTransfersSearch/').then(function(data) {
						var datas = data.data.data.data;
						if (!datas.length) {
							//没有更多内容了
							vm.noMorePage = true;
						} else {
							vm.noMorePage = false;
							for (var p in datas) {
								vm.records.push(datas[p]);
							}
						}
						$scope.$broadcast('scroll.infiniteScrollComplete');
					});
					break;
			}

		}
	}


	// 密码控制器
	var pwdController = function($scope, fetchDataService, $filter, postDataService, $ionicPopup, $state, passwordService) {
		var vm = this;
		vm.modLoginPwdFlag = false;
		vm.bindWithdrawPwdFlag = false;
		vm.modWithdrawPwdFlag = false;

		vm.modLoginPwdObj = {};
		vm.bindWithdrawPwdObj = {};
		vm.modWithdrawPwdObj = {};
		vm.securityObj = {};
		vm.userObj = {};
		var type = $state.params.type;
		switch (type) {
			case "login":
				vm.modLoginPwdFlag = true;
				vm.bindWithdrawPwdFlag = false;
				vm.modWithdrawPwdFlag = false;
				break;
			case "bindWithdrawPwd":
				//获取密保问题
				postDataService.getPostData(null, '/GetUserSecurity').then(function(data) {
					if (data.data.data) {
						vm.question = data.data.data.key;
						vm.bindWithdrawPwdObj.sid = data.data.data.id;
					} else {
						$ionicPopup.alert({
							title: '温馨提示',
							template: '请先绑定安全密保！'
						}).then(function() {
							$state.go('security');
						});
					}
				});
				vm.modLoginPwdFlag = false;
				vm.bindWithdrawPwdFlag = true;
				vm.modWithdrawPwdFlag = false;
				break;
			case "modWithdrawPwd":
				vm.modLoginPwdFlag = false;
				vm.bindWithdrawPwdFlag = false;
				vm.modWithdrawPwdFlag = true;
				break;
		}

		//根据$state.current.name check the view is security or bindUser
		var stateName = $state.current.name;

		// 修改登录密码
		vm.modLoginPwd = function() {
			if (!vm.modLoginPwdObj.oldLoginPassword) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入当前登录密码'
				});
				return;
			}
			var regexp = /^(?![\d]+$)(?![a-zA-Z]+$)(?![~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]+$)[\da-zA-Z~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]{6,20}$/;
			if (!regexp.test(vm.modLoginPwdObj.newLoginPassword)) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '新登录密码必须为英文、数字、特殊字符任意两种组合，长度6-20位'
				});
				return;
			}
			if (vm.modLoginPwdObj.newLoginPassword != vm.modLoginPwdObj.reLoginPassword) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '两次密码不一致！'
				});
				return;
			}

			if (vm.modLoginPwdObj.newLoginPassword == vm.modLoginPwdObj.oldLoginPassword) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '新密码与旧密码不能一致！'
				});
				return;
			}

			var token = passwordService.getDisposableToken();
			if (!token) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请求超时，请重新操作！'
				})
				return;
			}

			var updateJson = {
				oldPassword: passwordService.encryptPasswordWithToken(vm.modLoginPwdObj.oldLoginPassword, token),
				newPassword: passwordService.generatePassword(vm.modLoginPwdObj.newLoginPassword),
			};

			postDataService.submitData(updateJson, '/ModUserLoginPwd').then(function(data) {
				if (data.data.error === 0) {
					document.getElementById("pwd-manage").reset(); //清空form

					$ionicPopup.alert({
						title: '温馨提示',
						template: '登录密码修改成功,请重新登录'
					}).then(function() {
						postDataService.getPostData(null, '/App/Logout').then(function(data) {
							$state.go('login');
						});
					});
				} else {
					if (data.data.error == 2 && data.data.code === '2-1004') {
						$ionicPopup.alert({
							title: '温馨提示',
							template: "当前登录密码输入有误"
						});
					}
					else {
						$ionicPopup.alert({
							title: '温馨提示',
							template: data.data.message
						});
					}
					return;
				}
			});
		}

		// 设置资金密码
		vm.bindWithdrawPwd = function() {
			if (!vm.bindWithdrawPwdObj.answer) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入您的密保答案'
				});
				return;
			}
			if (!vm.bindWithdrawPwdObj.newWithdrawPassword) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入新的资金密码'
				});
				return;
			}
			var regexp = /^(?![\d]+$)(?![a-zA-Z]+$)(?![~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]+$)[\da-zA-Z~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]{6,20}$/;
			if (!regexp.test(vm.bindWithdrawPwdObj.newWithdrawPassword)) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '新资金密码必须为英文、数字、特殊字符任意两种组合，长度6-20位'
				});
				return;
			}
			if (vm.bindWithdrawPwdObj.newWithdrawPassword != vm.bindWithdrawPwdObj.reWithdrawPassword) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '两次密码不一致！'
				});
				return;
			}

			var token = passwordService.getDisposableToken();
			if (!token) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请求超时，请重新操作！'
				})
				return;
			}

			var updateJson = {
				sid: vm.bindWithdrawPwdObj.sid,
				answer: passwordService.encryptPasswordWithToken(vm.bindWithdrawPwdObj.answer, token),
				password: passwordService.generatePassword(vm.bindWithdrawPwdObj.newWithdrawPassword)
			};
			postDataService.submitData(updateJson, '/BindUserWithdrawPwd').then(function(data) {
				if (data.data.error == 0) {
					document.getElementById("pwd-manage").reset(); //清空form
					$ionicPopup.alert({
						title: '温馨提示',
						template: '资金密码设置成功'
					}).then(function() {
						$state.go('member');
					});
				}
				else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}

		// 修改资金密码
		vm.modWithdrawPwd = function() {
			if (!vm.modWithdrawPwdObj.oldModWithdrawPassword) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入当前资金密码'
				});
				return;
			}
			var regexp = /^(?![\d]+$)(?![a-zA-Z]+$)(?![~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]+$)[\da-zA-Z~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]{6,20}$/;
			if (!regexp.test(vm.modWithdrawPwdObj.newModWithdrawPassword)) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '新资金密码必须为英文、数字、特殊字符任意两种组合，长度6-20位'
				});
				return;
			}
			if (vm.modWithdrawPwdObj.newModWithdrawPassword != vm.modWithdrawPwdObj.reModWithdrawPassword) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '两次密码不一致！'
				});
				return;
			}

			if (vm.modWithdrawPwdObj.newModWithdrawPassword == vm.modWithdrawPwdObj.oldModWithdrawPassword) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '新密码与旧密码不能一致！'
				});
				return;
			}

			var token = passwordService.getDisposableToken();
			if (!token) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请求超时，请重新操作！'
				})
				return;
			}

			var updateJson = {
				oldPassword: passwordService.encryptPasswordWithToken(vm.modWithdrawPwdObj.oldModWithdrawPassword, token),
				newPassword: passwordService.generatePassword(vm.modWithdrawPwdObj.newModWithdrawPassword),
			};

			postDataService.submitData(updateJson, '/ModUserWithdrawPwd').then(function(data) {
				if (data.data.error === 0) {
					document.getElementById("pwd-manage").reset(); //清空form

					$ionicPopup.alert({
						title: '温馨提示',
						template: '资金密码修改成功,请重新登录'
					}).then(function() {
						postDataService.getPostData(null, '/App/Logout').then(function(data) {
							$state.go('login');
						});
					});
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
					return;
				}
			});
		}

		//设置安全密保
		vm.setSecurity = function() {
			if (!vm.securityObj.question1) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请设置密保问题1'
				});
				return;
			}
			if (!vm.securityObj.answer1) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请设置密保答案1'
				});
				return;
			}
			if (!vm.securityObj.question2) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请设置密保问题2'
				});
				return;
			}
			if (!vm.securityObj.answer2) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请设置密保答案1'
				});
				return;
			}

			var updateJson = {
				question1: vm.securityObj.question1,
				answer1: passwordService.generatePassword(vm.securityObj.answer1),
				question2: vm.securityObj.question2,
				answer2: passwordService.generatePassword(vm.securityObj.answer2),
				question3: vm.securityObj.question3,
				answer3: passwordService.generatePassword(vm.securityObj.answer3)
			};

			postDataService.submitData(updateJson, '/BindUserSecurity').then(function(data) {
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '安全密保绑定成功！'
					}).then(function() {
						$state.go('member');
					});
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
					return;
				}
				document.getElementById("pwd-manage").reset(); //清空form
			});
		}

		//绑定用户信息
		vm.bindUser = function() {
			if (!vm.userObj.withdrawName) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请填写您的真实姓名！'
				});
				return;
			}
			var postJson = {
				withdrawName: vm.userObj.withdrawName
			}

			postDataService.submitData(postJson, '/ModUserBind').then(function(data) {
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '账户姓名绑定成功！'
					}).then(function(){
						$state.go('member');
					});
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
					return;
				}
				document.getElementById("pwd-manage").reset(); //清空form
			});
		}
	}

	//银行卡绑定
	var bankController = function($scope, fetchDataService, postDataService, $state, $ionicModal, $ionicPopup, passwordService) {
		var vm = this;
		vm.bankobj = {};
		vm.banks = {};
		vm.bankcards = [];
		// get the banks
		postDataService.getPostData(null, '/ListUserCard').then(function(data) {
			if (data.data.error === 0) {
				vm.bankcards = data.data.data;
			}
		});
		//添加银行卡Modal---0
		$ionicModal.fromTemplateUrl('/template/addbank.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modal = modal;
		});
		vm.openModal = function() {
			vm.modal.show();
		}
		vm.closeAddBank = function(type) {
			vm.modal.hide();
		};
		// 关闭所有窗口
		$scope.$on("$destroy", function(){
			vm.closeAddBank();
		});
		vm.addNewBank = function() {
			var url = '/GetBindUserCardNeed';
			// vm.openModal();
			postDataService.getPostData(null, url).then(function(data) {
				if (!data.data.data.bindCardName || data.data.data.bindCardName == null || data.data.data.bindCardName == '') {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '请先绑定账户姓名！'
					});
					return;
				}

				if (data.data.data.count >= 5) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '每人最多绑定5张银行卡！'
					});
					return;
				}

				vm.banklist = data.data.data.bankList;
				vm.bankCardName = data.data.data.bindCardName;
				vm.openModal();
			});
		};

		vm.createNewBank = function() {
			if (!vm.bankObj.bankId) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请选择开户行！'
				});
				return;
			}
			if (!vm.bankObj.bankBranch) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入支行名称！'
				});
				return;
			}
			if (!vm.bankObj.cardId) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入银行卡号！'
				});
				return;
			}
			if (vm.bankObj.cardId != vm.bankObj.confirmBankcard) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '两次卡号不一致！'
				});
				return;
			}
			if (!vm.bankObj.withdrawPwd) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入资金密码！'
				});
				return;
			}

			var token = passwordService.getDisposableToken();
			if (!token) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请求超时，请重新操作！'
				})
				return;
			}

			var postObj = {
				cardId: vm.bankObj.cardId,
				bankId: vm.bankObj.bankId,
				bankBranch: vm.bankObj.bankBranch,
				withdrawPwd: passwordService.encryptPasswordWithToken(vm.bankObj.withdrawPwd, token)
			}

			postDataService.submitData(postObj, '/BindUserCard').then(function(data) {
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '绑定成功！'
					});
					vm.closeAddBank();
					$state.reload();
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}

		//解锁银行卡
		vm.unlockBank = function(id) {
			$ionicPopup.confirm({
				title: '温馨提示',
				template: '确定要解锁该银行卡吗？',
				okText:"确定",
				cancelText:"取消"
			}).then(function(res) {
				if(res){
					postDataService.submitData({
						id: id
					}, '/UserUnBindCard').then(function(data) {
						if (data.data.error === 0) {
							$ionicPopup.alert({
								title: '温馨提示',
								template: '解锁成功！'
							});
							$state.reload();
						} else {
							$ionicPopup.alert({
								title: '温馨提示',
								template: data.data.message
							})
						}
					});
				}
			});
		}

	}

	//充值控制器
	var chargeController = function($scope, shareDataService, postDataService, $state, $ionicPopup) {
		var vm = this;
		vm.wangyinList = [];
		vm.weChatList = [];
		vm.alipayList = [];
		vm.qqList = [];
		vm.jdList = [];
		vm.showChannelList = [];
		vm.selectedChannel = {};
		vm.LoadBankTransfers = function() {
		/*	$ionicLoading.show({
				template: '正在加载。。。'
			});*/
			var pid = vm.selectedChannel.id;
			var amount = 100;
			var data = {
				"pid": pid,
				"amount": amount
			};
			$.ajax({
				url: '/LoadBankTransfers',
				type: 'POST',
				cache: false,
				dataType: 'json',
				contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
				data: data,
				success: function(data) {
					// console.log(data);
					if(data.error === 0) {
						vm.selectedChannel.btdata = data.data;
						$scope.$apply();
					} else {
						$ionicPopup.alert({
							title: '温馨提示',
							template: "无法获取银行卡信息,请尝试其他充值方式"//data.message
						});
					}
				},
				error: function() {
					//$ionicLoading.hide();
					$ionicPopup.alert({
						title: '温馨提示',
						template: '网络连接问题！'
					});
				},
				fail: function() {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '网络连接问题！'
					});
				},
				complete: function() {
				//	$ionicLoading.hide();
				}
			});

		}
		vm.changeTabs = function(type) {
			$('#chargeTabs').find('a.channel-type-' + type).addClass('active').siblings('a').removeClass('active');
			if(type === 'wangYing') {
				vm.showChannelList = vm.wangyinList;
			} else if(type === 'weChat') {
				vm.showChannelList = vm.weChatList;
			} else if(type === 'alipay') {
				vm.showChannelList = vm.alipayList;
			} else if(type === 'qq') {
				vm.showChannelList = vm.qqList;
			} else if(type === 'jd') {
				vm.showChannelList = vm.jdList;
			}
			if(vm.showChannelList.length > 0) {
				vm.selectedChannel = vm.showChannelList[0];
				if((!vm.selectedChannel.banklist.length || vm.selectedChannel.banklist[0].type==1) &&vm.selectedChannel.subType==2){
					vm.LoadBankTransfers();
				    new Clipboard('#charge_CardNameButton', {
				        text: function(trigger) {
				            return vm.selectedChannel.btdata.cardName;
				        }
				    });
				    
				    new Clipboard('#charge_CardIdButton', {
				        text: function(trigger) {
				
				            return vm.selectedChannel.btdata.cardId;
				        }
				    });

					
				}

			}
		}
		vm.changeChannel = function(id) {
			$('#chargeChannels').find('button.channel-id-' + id).addClass('button-positive').siblings('button').removeClass('button-positive');
			var obj = $.map(vm.showChannelList, function(ele) {
				if(ele.id === id) {
					return ele;
				}
			});
			vm.selectedChannel = obj[0];
			if((!vm.selectedChannel.banklist.length || vm.selectedChannel.banklist[0].type==1) &&vm.selectedChannel.subType==2){
					vm.LoadBankTransfers();
				    new Clipboard('#charge_CardNameButton', {
				        text: function(trigger) {
				            return vm.selectedChannel.btdata.cardName;
				        }
				    });
				    
				    new Clipboard('#charge_CardIdButton', {
				        text: function(trigger) {
				            return vm.selectedChannel.btdata.cardId;
				        }
				    });
				}
		}
		vm.changeMoney = function(money) {
			$('#amountBtn').find('button.button-money-' + money).addClass('button-positive').siblings('button').removeClass('button-positive');
		}

		//判断是否在确定支付页面
		if($state.current.name === 'confirmCharge') {
			if(!shareDataService.base.billno) {
				$state.go('charge');
				return;
			}

			vm.confirmObj = shareDataService.base;
			vm.confirmURL = "/RechargeSelfRedirect?pid=" + vm.confirmObj.pid + "&billno=" + vm.confirmObj.billno + "&amount=" + vm.confirmObj.amount + "&bankco=" + vm.confirmObj.bankco + "&Signature=" + vm.confirmObj.Signature + "&requestHost=" + vm.confirmObj.requestHost + "&xapp-target=browser";
			vm.confirmURL = vm.confirmURL.replace(/\+/g, '%2B');
		}

		
		//获取充值方式列表
		vm.listPayment = function(){
			postDataService.getPostData(null, '/App/ListPayment').then(function(data) {
				if(!data.data.hasWithdrawPwd) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '请先设置资金密码再进行充值操作！'
					}).then(function() {
						$state.go('pwd', {
							type: 'bindWithdrawPwd'
						});
					});
					return;
				}

				var list = data.data.channels;
				if(!list || list.length <= 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '目前没有可供您使用的充值方式！'
					}).then(function() {
						$state.go("member");
					});
					return;
				}

				$.each(list, function(index, element) {
					if(element.type == 2 && (element.subType == 1 || element.subType == 2)) {
						element.banklist.push({
							type: element.type,
							bankId: 'WECHAT',
							code: 'WECHAT'
						});
						vm.weChatList.push(element);
					} else if(element.type == 2 && (element.subType == 3 || element.subType == 4)) {
						element.banklist.push({
							type: element.type,
							bankId: 'ALIPAY',
							code: 'ALIPAY'
						});
						vm.alipayList.push(element);
					} else if(element.type == 2 && (element.subType == 5 || element.subType == 6)) {
						element.banklist.push({
							type: element.type,
							bankId: 'QQ',
							code: 'QQ'
						});
						vm.qqList.push(element);
					} else if(element.type == 2 && (element.subType == 7)) {
						element.banklist.push({
							type: element.type,
							bankId: 'JDPAY',
							code: 'JDPAY'
						});
						vm.jdList.push(element);
					} else if(element.type == 1 && (element.subType == 3)) {
						element.banklist.push({
							type: element.type,
							bankId: 'SPEED',
							code: 'SPEED'
						});
						vm.wangyinList.push(element);
					} else {
						vm.wangyinList.push(element);
					}
				});

				if(vm.wangyinList.length > 0) {
					vm.showChannelList = vm.wangyinList;
					vm.activeWangYing = true;
					vm.activeWeChat = false;
					vm.activeAlipay = false;
					vm.activeQQ = false;
					vm.activeJD = false;
				} else if(vm.weChatList.length > 0) {
					vm.showChannelList = vm.weChatList;
					vm.activeWangYing = false;
					vm.activeWeChat = true;
					vm.activeAlipay = false;
					vm.activeQQ = false;
					vm.activeJD = false;
				} else if(vm.alipayList.length > 0) {
					vm.showChannelList = vm.alipayList;
					vm.activeWangYing = false;
					vm.activeWeChat = false;
					vm.activeAlipay = true;
					vm.activeQQ = false;
					vm.activeJD = false;
				} else if(vm.qqList.length > 0) {
					vm.showChannelList = vm.qqList;
					vm.activeWangYing = false;
					vm.activeWeChat = false;
					vm.activeAlipay = false;
					vm.activeQQ = true;
					vm.activeJD = false;
				} else if(vm.jdList.length > 0) {
					vm.showChannelList = vm.jdList;
					vm.activeWangYing = false;
					vm.activeWeChat = false;
					vm.activeAlipay = false;
					vm.activeQQ = false;
					vm.activeJD = true;
				}
				if(vm.showChannelList.length > 0) {
					vm.selectedChannel = vm.showChannelList[0];
					if((!vm.selectedChannel.banklist.length || vm.selectedChannel.banklist[0].type==1) &&vm.selectedChannel.subType==2){
						vm.LoadBankTransfers();
					    new Clipboard('#charge_CardNameButton', {
					        text: function(trigger) {
					
					            return vm.selectedChannel.btdata.cardName;
					        }
					    });
					    
					    new Clipboard('#charge_CardIdButton', {
					        text: function(trigger) {
					            return vm.selectedChannel.btdata.cardId;
					        }
					    });
					}
				}
				
			});
		}

		//提交充值
		vm.submitCharge = function() {

	/*				if (!vm.selectedChannel.bankco) {
						$ionicPopup.alert({
							title: '温馨提示',
							template: '选择充值方式'
						});
						return;
					}*/
			var qrCodeId = null;
			if(vm.selectedChannel.fixedAmountType == 1) {
				var $btn = $('#amountBtn').find('button.button-money.button-positive');
				var money = $btn.attr('data-val');
				qrCodeId = $btn.attr('data-id');
				vm.selectedChannel.amount = parseInt(money);
			} else {
				if(!vm.selectedChannel.amount) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '请输入充值金额'
					});
					return;
				}

				var reg = /^[0-9]+$/;
				if(!reg.test(vm.selectedChannel.amount)) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '充值金额请输入整数'
					});
					return;
				}
				if(Number(vm.selectedChannel.amount) < Number($('#mincharge').text())) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '充值最低为' + $('#mincharge').text()
					});
					return;
				}
				if(Number(vm.selectedChannel.amount) > Number($('#maxcharge').text())) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '充值最高为' + $('#maxcharge').text()
					});
					return;
				}
			}

			var channelCode = $('#channelCode').val();
			var postData = null;
			var url = channelCode == 'bankTransfer' || channelCode == 'alipayTransfer' ? '/BankTransfers' : '/RechargeAdd';
			

			if(channelCode == 'bankTransfer' || channelCode == 'alipayTransfer') {

				var bankId;
				$.each(vm.selectedChannel.banklist, function(index, element) {
					if(element.code == vm.selectedChannel.bankco) {
						bankId = element.bankId;
					}
				});
				postData = {
					amount: vm.selectedChannel.amount,
					pid: $('#bankpid').val(),
					bankId: bankId
				}
			} else {
				postData = {
					bankco: vm.selectedChannel.bankco,
					amount: vm.selectedChannel.amount,
					pid: $('#bankpid').val(),
					qrCodeId: qrCodeId
				}
			}
		
		    if((!vm.selectedChannel.banklist.length || vm.selectedChannel.banklist[0].type==1) && vm.selectedChannel.subType ==2){
		    	var postscript = $("#postscript").val();
		    	var name = $("#btName").val();
		        // 判断是否是空，排除空格
	
		    	 if (/^\s+$/.test(name) || name.length < 1) {
		 			$ionicPopup.alert({
						title: '温馨提示',
						template: '请输入持卡人姓名！'
					});
		                return ;
		            }
		    	  
		        if (!/^[\u0391-\uFFE5]{2,12}(?:·[\u0391-\uFFE5]{2,12})*$/.test(name)) {
		 		$ionicPopup.alert({
					title: '温馨提示',
					template: '持卡人姓名只能输入中文！'
				});
		            return;
		        }
		        var postscript = $("#postscript").val();
		        if (/^\s+$/.test(postscript) || postscript.length < 1) {
		 		$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入转帐时间！！'
				});
		            return;
		        }
		        if (/^(?![\d]+$)(?![a-zA-Z]+$)(?![~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]+$)[\da-zA-Z~!@#$%^&*()_+`\-={}\[\]:;<>?,.\/]{6,20}$/.test(postscript)) {
		       	 $.YF.alert_warning(' 转帐时间不能输入特殊字符！');
		            return;
		        }
					postData = {
					amount: vm.selectedChannel.amount,
					pid: $('#bankpid').val(),
					bankId: vm.selectedChannel.btdata.bankId,
					postscript:$("#postscript").val(),
					name:$("#btName").val()
				}
			}
			postDataService.submitData(postData, url).then(function(data) {
				if(data.data.error === 0) {
					shareDataService.base = data.data.data;
					// shareDataService.base.bankId = $("#bankselect").data("bankId");
					if(channelCode != 'bankTransfer' && channelCode != 'alipayTransfer') {
						$.each(vm.selectedChannel.banklist, function(index, element) {
							if(element.code == vm.selectedChannel.bankco) {
								shareDataService.base.bankId = element.bankId;
							}
						});
					}
					 if((!vm.selectedChannel.banklist.length || vm.selectedChannel.banklist[0].type==1) && vm.selectedChannel.subType ==2){
							$ionicPopup.alert({
								title: '温馨提示',
								template: '您的网银转帐申请已经提交,请您复制好收款银行信息前往支付宝转帐，注意金额保持一致，否则影响上分'
							});
					 }
					 else{
					 	$state.go('confirmCharge');
					 }
					
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
					return;
				}
			});
		}

		// vm.finished = function() {
		// 	vm.chargeTabs(vm.selectedChannel.type);
		// }
	}

	//提现控制器
	var withdrawController = function($scope, shareDataService, postDataService, $state, $ionicPopup, passwordService, $ionicLoading) {
		var vm = this;
		vm.withdrawObj = {
			amount: '',
			cid: '',
			withdrawPwd: ''
		}
		postDataService.getPostData(null, '/LoadUserWithdrawals').then(function(data) {
			vm.bankAccept = data.data.data;
			vm.bankAccept.availableMoney = parseInt(vm.bankAccept.availableMoney);
			if (data.data.data.cList && data.data.data.cList !== null && data.data.data.cList.length > 0) {
				vm.withdrawObj.cid = data.data.data.cList[0].id;
			}

			if (data.data.data.cList.length < 1) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请先绑定银行卡再进行提款操作！'
				}).then(function(){
					$state.go('bankcard');
				});
				return;
			}

			if (!data.data.data.hasWithdrawPwd) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请先设置资金密码再进行提款操作！'
				}).then(function(){
					$state.go('pwd', {
						type: 'bindWithdrawPwd'
					});
				});
				return;
			}
		});

		vm.transferAll = function() {

			$ionicPopup.confirm({
				title: '确认',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '<b>确定</b>',
					type: 'button-assertive',
					onTap: function() {
						$ionicLoading.show({template: '正在加载。。。'});

						postDataService.submitData(null, '/SelfUserTransfersAll').then(function(data) {
							if (data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '您的资金已全部成功转移至主账户！'
								}).then(function () {
									$state.reload();
								});
							} else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}
							$ionicLoading.hide();
						});
					}
				}],
				template: '确认将所有资金转移至主账户？'
			});
		}

		vm.submitWithdraw = function() {
			if (!vm.withdrawObj.amount) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入提款金额'
				});
				return;
			}

			var reg = /^[0-9]+$/ ;
			if (!reg.test(vm.withdrawObj.amount)) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '提款金额请输入整数'
				});
				return;
			}

			var amount = parseInt(vm.withdrawObj.amount);
			if (amount > vm.bankAccept.availableMoney) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '您本次最多可提现' + vm.bankAccept.availableMoney + "元"
				});
				return;
			}
			if (amount < vm.bankAccept.minWithdrawals) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '最低提现额度：' + vm.bankAccept.minWithdrawals + "元"
				});
				return;
			}
			if (amount > vm.bankAccept.maxWithdrawals) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '最高提现额度：' + vm.bankAccept.maxWithdrawals + "元"
				});
				return;
			}

			if (!vm.withdrawObj.cid) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请选择提款银行'
				});
				return;
			}
			if (!vm.withdrawObj.withdrawPwd) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入资金密码'
				});
				return;
			}

			var token = passwordService.getDisposableToken();
			if (!token) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请求超时，请重新操作！'
				})
				return;
			}

			var withdrawPwd = passwordService.encryptPasswordWithToken(vm.withdrawObj.withdrawPwd, token);
			var withdrawData = {
				amount: amount,
				cid: vm.withdrawObj.cid,
				withdrawPwd: withdrawPwd
			}

			postDataService.submitData(withdrawData, '/ApplyUserWithdrawals').then(function(data) {
				// console.log(data.data);
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '提款申请成功！'
					}).then(function() {
						$state.go('home');
					});
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}
	}

	//转账控制器 
	var transController = function($scope, shareDataService, postDataService, $state, $ionicPopup, $ionicLoading) {
		var vm = this;
		vm.transObj = {
			fromAccount: '',
			toAccount: '',
			amount: ''
		}
		postDataService.getPostData(null, '/LoadUserTransfers').then(function(data) {
			var platform = data.data.data.listPlatform;
			vm.transferData = data.data.data;
			vm.platform = $.map(platform, function(element) {
				if (element.status === 0) {
					return element;
				}
			});
		});

		vm.transferAll = function() {

			$ionicPopup.confirm({
				title: '确认',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '<b>确定</b>',
					type: 'button-assertive',
					onTap: function() {
						$ionicLoading.show({template: '正在加载。。。'});

						postDataService.submitData(null, '/SelfUserTransfersAll').then(function(data) {
							if (data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '您的资金已全部成功转移至主账户！'
								}).then(function () {
									$state.reload();
								});
							} else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}
							$ionicLoading.hide();
						});
					}
				}],
				template: '确认将所有资金转移至主账户？'
			});
		}

		vm.submitTrans = function() {
			if (!vm.transObj.fromAccount) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请选择转出账户'
				});
				return;
			}
			if (!vm.transObj.toAccount) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请选择转入账户'
				});
				return;
			}
			if (!vm.transObj.amount) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入转账金额'
				});
				return;
			}
			postDataService.submitData(vm.transObj, '/SelfUserTransfers').then(function(data) {
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '转账成功！'
					}).then(function () {
						$state.reload();
					});
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}
	}

	//个人中心
	var memberController = function($scope, $q, shareDataService, postDataService, $state, $ionicPopup) {
		var vm = this;
		$q.all([postDataService.getPostData(null, '/GetGlobal'), postDataService.getPostData(null, '/GetUserBind')]).then(function(result) {
			vm.booleanObj = result[1].data.data;
			vm.userBean = result[0].data.data.uBean;
			vm.bean = result[0].data.data;

			// if (result[0].data.data.isInitialPassword) {
			// 	$ionicPopup.alert({
			// 		title: '温馨提示',
			// 		template: '系统检测到您的登录密码为系统初始密码，为了保障您的资金安全，请修改密码！'
			// 	}).then(function(){
			// 		$state.go('pwd', {
			// 			type: 'login'
			// 		});
			// 	});
			// 	return;
			// }
		});

		// // 是否显示契约
		// postDataService.getPostData(null, '/SHOW_DAIYU').then(function(data) {
		// 	vm.showContractbill = data.data.showDailySettle;
		// 	vm.showBonusbill = data.data.showDividend;
		// });
	}

	//代理中心 
	var agentController = function($scope, $q, $filter, $ionicModal, $timeout, shareDataService, postDataService, lotteryUtilsService, $state, $ionicPopup, passwordService) {
		var vm = this;
		var type = $state.params.type;
		vm.type = type;
		vm.page = vm.page || 1;
		vm.limit = 10;
		vm.noMorePage = true;
		vm.onlineObj = {
			limit: 10,
			start: 0
		}
		var url = '',
			sTime = new Date(),
			eTime = new Date();
		sTime.setHours(0, 0, 0);
		eTime.setDate(eTime.getDate());
		eTime.setHours(23, 59, 59);
		vm.searchObj = {
			limit: vm.limit,
			start: (vm.page - 1) * vm.limit,
			sTime: sTime,
			eTime: eTime,
			lotteryId: '',
			status: '',
			account: '',
			username: '',
			type: ''
		}
		vm.teamObj = {
			limit: 10,
			start: 0,
			action: 'User',
			username: '',
			minMoney: '',
			maxMoney: ''
		}
		vm.getNetTime = function(date){
			var _eTime = new Date(date);
			_eTime.setDate(_eTime.getDate()+1);
			return _eTime;
		}
		vm.contractObj = {
			limit: vm.limit,
			start: (vm.page - 1) * vm.limit,
			sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
			eTime: $filter('date')(vm.getNetTime(vm.searchObj.eTime), 'yyyy-MM-dd'),
			username: vm.searchObj.username
		}
		vm.accountObj = {};
		vm.linkObj = {};
		vm.showQRCodeObj={};
		vm.userLevels={};
		vm.init = function(type, num) {
			vm.page = 1;
			vm.noMorePage = false;
			var num = 3 | Number(num);
			//代理总览
			if (type === 'viewall') {
				var sDate = new Date();
				var eDate = new Date();
				sDate.setDate(sDate.getDate() - num+1);
				eDate.setDate(eDate.getDate() + 1);
				vm.viewObj = {
					eDate: $filter('date')(eDate, 'yyyy-MM-dd'),
					sDate: $filter('date')(sDate, 'yyyy-MM-dd'),
					type: 'lottery'
				}
				postDataService.getPostData(vm.viewObj, '/LoadProxyIndex').then(function(data) {
					vm.viewData = data.data.data;
				});
			}

			//开户中心
			if (type === 'manul') {
				$('.tab-item.manul').addClass('active').siblings('a').removeClass('active');
				postDataService.getPostData(null, '/ProxyQuotaInfo').then(function(data) {
					vm.manulshow = true;
					vm.linkopen = false;
					vm.webLinkManage = false;
					vm.mobileLinkManage = false;
					vm.uCode = data.data.data.uCode;
					vm.uCode.minCode = 1800;
					vm.uCode.maxCode = vm.uCode.code;
				});
			}

			//团队管理
			if (type === 'teamManage') {
				vm.page = 1;
				vm.noMorePage = false;
				vm.teamObj = {
					limit: 10,
					start: (vm.page - 1) * 10,
					action: 'User',
					username: vm.teamObj.username,
					minMoney: vm.teamObj.minMoney,
					maxMoney: vm.teamObj.maxMoney,
					scope: 3
				}
				postDataService.getPostData(vm.teamObj, '/ProxyUserSearch').then(function(data) {
					vm.userList = data.data.data;
					vm.teamData = data.data;
				});
			}

			//在线会员
			if (type === 'online') {
				vm.page = 1;
				vm.noMorePage = false;
				vm.onlineObj = {
					limit: 10,
					start: (vm.page - 1) * 10
				}
				postDataService.getPostData(vm.onlineObj, '/ProxyOnlineList').then(function(data) {
					vm.contentList = data.data.data;
				});
			}

			//游戏记录
			if (type === 'record') {
				vm.page = 1;
				vm.noMorePage = false;
				vm.showLottery = true;
				vm.showLiveSlot = false;
				vm.showChanges = false;
				vm.recordObj = {
					limit: vm.limit,
					start: (vm.page - 1) * 10,
					sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
					eTime: $filter('date')(vm.getNetTime(vm.searchObj.eTime), 'yyyy-MM-dd'),
					expect: vm.searchObj.expect,
					lotteryId: vm.searchObj.lotteryId,
					username: vm.searchObj.username,
					status: vm.searchObj.status,
					scope: 1
				}

				$('#betrecord').find('a.lottery').addClass('active').siblings('a').removeClass('active');
				$q.all([postDataService.submitData(vm.recordObj, '/ProxyOrderSearch'), postDataService.fetchPostData(null, '/GetLottery')]).then(function(result) {
					vm.userLevels = result[0].data.userLevels;
					vm.records = result[0].data.data;
					vm.lotterys = result[1].data.data;
				});
			}
			// 老虎机真人记录
			if (type === 'liveslot') {
				vm.page = 1;
				vm.noMorePage = false;
				vm.showLottery = false;
				vm.showLiveSlot = true;
				vm.showChanges = false;
				if (!vm.searchObj.platformId) {
					vm.searchObj.platformId = 11;
				}
				
				var _eTime = new Date(vm.searchObj.eTime);
				_eTime.setDate(_eTime.getDate()+1);
				
				vm.recordObj = {
					limit: vm.limit,
					start: (vm.page - 1) * 10,
					sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
					eTime: $filter('date')(_eTime, 'yyyy-MM-dd '),
					username: vm.searchObj.username,
					platformId: vm.searchObj.platformId,
					scope: 1
				}
				$('#betrecord').find('a.liveslot').addClass('active').siblings('a').removeClass('active');
				postDataService.submitData(vm.recordObj, '/ProxyGameOrderSearch').then(function(data) {
					vm.userLevels = data.data.userLevels;
					vm.records = data.data.data;
				});
			}
			//帐变记录
			if (type === 'changes') {
				vm.page = 1;
				vm.noMorePage = false;
				vm.showLottery = false;
				vm.showLiveSlot = false;
				vm.showChanges = true;
				if (!vm.searchObj.account) {
					vm.searchObj.account = 2; // 账号
				}
				if (!vm.searchObj.type) {
					vm.searchObj.type = 6; // 投注
				}
				vm.recordObj = {
					limit: vm.limit,
					start: (vm.page - 1) * 10,
					type: vm.searchObj.type,
					sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
					eTime: $filter('date')(vm.getNetTime(vm.searchObj.eTime), 'yyyy-MM-dd'),
					account: vm.searchObj.account,
					username: vm.searchObj.username,
					scope: 1
				}
				$('#betrecord').find('a.changes').addClass('active').siblings('a').removeClass('active');
				postDataService.submitData(vm.recordObj, '/ProxyBillSearch').then(function(data) {
					vm.userLevels = data.data.userLevels;
					vm.records = data.data.data;
				});
			}
		}

		vm.init(type);

		vm.switchTab = function(type) {
			$('.tab-item.' + type).addClass('active').siblings('a').removeClass('active');
			if (type === 'manul') {
				vm.init('manul');
			}
			if (type === 'link') {
				vm.manulshow = false;
				vm.linkopen = true;
				vm.webLinkManage = false;
				vm.mobileLinkManage = false;
			}
			if (type === 'webLinkManage') {
				vm.manulshow = false;
				vm.linkopen = false;
				vm.webLinkManage = true;
				vm.mobileLinkManage = false;
				var data = {
					start: vm.onlineObj.start,
					limit: vm.onlineObj.limit,
					deviceType: 1
				};
				postDataService.getPostData(data, '/ProxyLinkList').then(function(data) {
					vm.linkList = data.data.data;
					if (!vm.linkList) {
						return;
					}
					for (var i = 0; i < vm.linkList.length; i++) {
						vm.linkList[i].code = data.data.domain + "/register?code=" + vm.linkList[i].code;
					}
				});
			}
			if (type === 'mobileLinkManage') {
				vm.manulshow = false;
				vm.linkopen = false;
				vm.webLinkManage = false;
				vm.mobileLinkManage = true;
				var data = {
					start: vm.onlineObj.start,
					limit: vm.onlineObj.limit,
					deviceType: 2
				};
				postDataService.getPostData(data, '/ProxyLinkList').then(function(data) {
					vm.linkList = data.data.data;
				});
			}
		}

		var clipWebLink;
		$scope.$on('ngRepeatFinished', function(ngRepeatFinishedEvent) {
			if (clipWebLink != null)
				clipWebLink.destroy();
			clipWebLink = new Clipboard('.clipWebLink');
			clipWebLink.off('success').on('success', function(e) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: "已经复制到粘贴板"
				});
                e.clearSelection();
            });
		});

		$ionicModal.fromTemplateUrl('/template/uppoint.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modal = modal;
		});
		vm.closePoint = function() {
			vm.modal.hide();
		}

		$ionicModal.fromTemplateUrl('/template/userselect.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modal_user = modal;
		});
		vm.close = function() {
			vm.modal_user.hide();
		}

		$ionicModal.fromTemplateUrl('/template/transChild.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modal_trans = modal;
		});
		vm.close_trans = function() {
			vm.modal_trans.hide();
		}

		$ionicModal.fromTemplateUrl('/template/agentRecordDetail.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modalRecordDetails = modal;
		});
		vm.openRecordDetails = function() {
			vm.modalRecordDetails.show();
		};
		vm.closeRecordDetails = function() {
			vm.modalRecordDetails.hide();
		};

		$ionicModal.fromTemplateUrl('/template/show_register_qrcode.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modalShowQRCode = modal;
		});
		vm.openQRCodeModal = function() {
			vm.modalShowQRCode.show();
		};
		vm.closeQRCodeModal = function() {
			vm.showQRCodeObj.qrCode = null;
			vm.modalShowQRCode.hide();
		};

		// 关闭所有窗口
		$scope.$on("$destroy", function(){
			vm.closeRecordDetails();
			vm.close_trans();
			vm.close();
			vm.closePoint();
			vm.closeQRCodeModal();
		});

		//查看详情
		vm.checkRecordDetails = function(id) {
			postDataService.submitData({
				id: id
			}, '/ProxyOrderDetails').then(function(data) {
				vm.currentRecord = data.data.data;
				vm.openRecordDetails();
			});
		}

		//下拉
		vm.reflesh = function() {
			var type = $state.params.type;
			vm.init(type);
			$timeout(function() {
				$scope.$broadcast('scroll.refreshComplete');
			}, 1000);
		}

		vm.checkChildForRecords = function(username) {
			var type = $state.params.type;
			vm.searchObj.username = username;
			vm.init(type);
			$timeout(function() {
				$scope.$broadcast('scroll.refreshComplete');
			}, 1000);
		}

		// lottery_agent_record.html上拉
		vm.recordLoadmore = function(page) {
			vm.noMorePage = true;
			var type = $state.params.type;

			if (!vm.records || vm.records.length < 10) {
				$scope.$broadcast('scroll.infiniteScrollComplete');
				return;
			}

			vm.page = vm.page + 1;
			var url = type == 'record' ? "ProxyOrderSearch" :
				type == 'liveslot' ? "ProxyGameOrderSearch" :"ProxyBillSearch";
			vm.recordObj = {
				limit: 10,
				start: (vm.page - 1) * 10,
				sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
				eTime: $filter('date')(vm.getNetTime(vm.searchObj.eTime), 'yyyy-MM-dd'),
				lotteryId: vm.searchObj.lotteryId,
				status: vm.searchObj.status,
				account: vm.searchObj.account,
				expect: vm.searchObj.expect,
				username: vm.searchObj.username,
				type: vm.searchObj.type,
				platformId: vm.searchObj.platformId
			}

			postDataService.getPostData(vm.recordObj, url).then(function(data) {
				$scope.$broadcast('scroll.infiniteScrollComplete');
				var datas = data.data.data;
				if (datas.length > 0) {
					for (var p in datas) {
						vm.records.push(datas[p]);
					}
				}

				if (datas.length>=10) {
					$timeout(function() {
						vm.noMorePage = false;
					}, 1000);
				}
			});
		}

		// teamManage.html上拉
		vm.teamManagerloadmore = function(page) {
			vm.noMorePage = true;
			if (!vm.userList || vm.userList.length < 10) {
				$scope.$broadcast('scroll.infiniteScrollComplete');
				return;
			}
			vm.page = page + 1;
			vm.teamObj = {
				limit: 10,
				start: (vm.page - 1) * 10,
				action: 'User',
				username: vm.teamObj.username,
				minMoney: vm.teamObj.minMoney,
				maxMoney: vm.teamObj.maxMoney,
				scope: vm.teamObj.scope
			}
			postDataService.getPostData(vm.teamObj, '/ProxyUserSearch').then(function(data) {
				$scope.$broadcast('scroll.infiniteScrollComplete');
				var datas = data.data.data;
				if (datas.length > 0) {
					for (var p in datas) {
						vm.userList.push(datas[p]);
					}
				}

				if (datas.length>=10) {
					$timeout(function() {
						vm.noMorePage = false;
					}, 1000);
				}
			});
		}

		// online_member.html上拉
		vm.onlineMemberLoadmore = function(page) {
			vm.noMorePage = true;
			if (!vm.contentList || vm.contentList.length < 10) {
				$scope.$broadcast('scroll.infiniteScrollComplete');
				return;
			}
			vm.page = page + 1;
			vm.onlineObj = {
				limit: 10,
				start: (vm.page - 1) * 10
			}
			postDataService.getPostData(vm.onlineObj, '/ProxyOnlineList').then(function(data) {
				$scope.$broadcast('scroll.infiniteScrollComplete');
				var datas = data.data.data;
				if (datas.length > 0) {
					for (var p in datas) {
						vm.contentList.push(datas[p]);
					}
				}

				if (datas.length>=10) {
					$timeout(function() {
						vm.noMorePage = false;
					}, 1000);
				}
			});
		}

		//上拉
		vm.loadmore = function(page) {
			if (!vm.contentList || vm.contentList.length < 10) {
				vm.noMorePage = false;
				$scope.$broadcast('scroll.infiniteScrollComplete');
				return;
			}
			vm.noMorePage = true;
			var type = $state.params.type;
			vm.page = page + 1;
			switch (type) {
				case "online":
					//获取投注记录
					postDataService.getPostData({
						limit: vm.limit,
						page: vm.page
					}, '/ProxyOnlineList').then(function(data) {
						var datas = data.data.data;
						if (!datas.length) {
							//没有更多内容了
							vm.noMorePage = true;
						} else {
							vm.noMorePage = false;
							for (var p in datas) {
								vm.contentList.push(datas[p]);
							}
						}
						$scope.$broadcast('scroll.infiniteScrollComplete');
					});
					break;
				case 'teamManage':
					postDataService.getPostData(vm.onlineObj, '/ProxyUserSearch').then(function(data) {
						var datas = data.data.data;
						if (!datas.length) {
							//没有更多内容了
							vm.noMorePage = true;
						} else {
							vm.noMorePage = false;
							for (var p in datas) {
								vm.userList.push(datas[p]);
							}
						}
						$scope.$broadcast('scroll.infiniteScrollComplete');
					});
					break;
			}
		}

		//日期筛选
		vm.dateSearch = function(num, id) {
			$('#' + id).addClass('button-positive').parent().siblings('div').find('button').removeClass('button-positive');
			vm.init('viewall', num);
		}

		//普通开户
		vm.createAccount = function() {
			if (!vm.accountObj.username) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入用户名'
				});
				return;
			}
			var reg = /^[0-9]{4}$/ ;
			if (!reg.test(vm.accountObj.code)) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '账号返点请输入4位整数'
				});
				return;
			}
			if (!vm.accountObj.code || Number(vm.accountObj.code) < Number(vm.uCode.minCode) || Number(vm.accountObj.code) > Number(vm.uCode.maxCode)) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '账号返点可分配范围 '+vm.uCode.minCode+' ~ ' + vm.uCode.maxCode
				});
				return;
			}
			if (Number(vm.accountObj.code) % 2 != 0) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '账号返点必须为2可以整除的数,如' + vm.uCode.maxCode
				});
				return;
			}
			var locatePoint = (Number(vm.accountObj.code) - 1800) / 20;
			vm.accountObj.locatePoint = locatePoint;
			postDataService.submitData(vm.accountObj, '/AddProxyUser').then(function(data) {
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '恭喜，开户成功！'
					});
					vm.accountObj.username = '';
					vm.accountObj.code = '';
					vm.accountObj.locatePoint = '';
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}

		//链接开户
		vm.createLink = function() {
			var server = window.location.origin;
			if (!vm.linkObj.amount) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入使用次数'
				});
				return;
			}
			var reg = /^[0-9]{4}$/ ;
			if (!reg.test(vm.linkObj.code)) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '账号返点请输入4位整数'
				});
				return;
			}
			if (!vm.linkObj.code || Number(vm.linkObj.code) < Number(vm.uCode.minCode) || Number(vm.linkObj.code) > Number(vm.uCode.maxCode)) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '账号返点可分配范围 '+vm.uCode.minCode+' ~ ' + vm.uCode.maxCode
				});
				return;
			}
			if (Number(vm.linkObj.code) % 2 != 0) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '账号返点必须为2可以整除的数,如' + vm.uCode.maxCode
				});
				return;
			}
			var locatePoint = (Number(vm.linkObj.code) - 1800) / 20;
			vm.linkObj.locatePoint = locatePoint;

			var regClip;
			postDataService.submitData(vm.linkObj, '/AddProxyLink').then(function(data) {
				if (data.data.error === 0) {
					if (vm.linkObj.deviceType == 1) {
						var link = data.data.domain + "/register?code=" + data.data.linkCode;
						$ionicPopup.alert({
							title: '注册链接添加成功！',
							template: '地址:<a href="' + link + '" target="_blank">'+link+'</a>'+
							'<button class="button button-assertive button-small disable-user-behavior" id="regClip" data-clipboard-text="'+link+'">复制</button>'
						});
						if (regClip != null)
							regClip.destroy();
						regClip = new Clipboard('#regClip');
						regClip.off('success').on('success', function(e) {
			                $("#regClip").html("已复制")
			                e.clearSelection();
			            });
					}
					else if (vm.linkObj.deviceType == 2) {
						$ionicPopup.alert({
							title: '二维码生成成功！',
							template: '<img src="' + data.data.qrCode + '"/><br/><p>使用手机扫描以上二维码即可进行注册</p>'
						});
					}
					vm.linkObj.code = '';
					vm.linkObj.locatePoint = '';
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}

		vm.showQRCode = function(qrCode){
			vm.showQRCodeObj.qrCode = qrCode;
			vm.openQRCodeModal();
		}

		//删除开户链接
		vm.delLink = function(id) {
			$ionicPopup.confirm({
				title: '温馨提示',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '<b>确定</b>',
					type: 'button-assertive',
					onTap: function() {
						postDataService.submitData({
							id: id
						}, '/DelProxyLink').then(function(data) {
							if (data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '删除成功'
								}).then(function() {
									if (vm.webLinkManage == true) {
										vm.switchTab('webLinkManage');
									}
									else if (vm.mobileLinkManage == true) {
										vm.switchTab('mobileLinkManage');
									}
								});
							} else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}
						});
					}
				}],
				template: '确定要删除该链接吗？'
			});
		}

		//团队查询
		vm.searchteam = function() {
			vm.page = 1;
			vm.teamObj = {
				limit: 10,
				start: (vm.page - 1) * 10,
				action: 'Userme',
				username: vm.teamObj.username,
				minMoney: vm.teamObj.minMoney,
				maxMoney: vm.teamObj.maxMoney,
				scope: vm.teamObj.scope
			}
			postDataService.getPostData(vm.teamObj, '/ProxyUserSearch').then(function(data) {
				vm.userList = data.data.data;
				vm.teamData = data.data;
				if (vm.userList.length >= 10) {
					vm.noMorePage = false;
				}
			});
		}

		//配额升点
		vm.configPoint = function(username) {

			postDataService.getPostData({
				username: username
			}, '/LoadProxyEditPoint').then(function(data) {
				if(data.data.data.lBean.locatePoint + 0.1 > data.data.data.uCode.maxLocatePoint) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '无法修改该用户返点信息！'
					});
				}
				else {
					vm.pointObj = data.data.data;
					vm.modal.show();
				}
			});
		}

		//确定配额
		vm.cofirmConfig = function() {
			var code = $('#locatePoint').val();
			if (!code) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入账号返点'
				});
				return;
			}
			if (Number(code) < Number($('#minpoint').text()) || Number(code) > Number($('#maxpoint').text())) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入账号返点在' + $('#minpoint').text() + '~' + $('#maxpoint').text() + '之间！'
				});
				return;
			}
			postDataService.submitData({
				username: $('#username').text(),
				code: $('#locatePoint').val()
			}, '/ProxyEditUserPoint').then(function(data) {
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '恭喜，配额升点成功！'
					});
					vm.closePoint();
					$state.reload();
				}
				else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}

		//下级转账
		vm.childTrans = function(username, nickname) {
			vm.transObj = {};
			vm.transObj.transferType = 1;
			postDataService.getPostData({
				username: username
			}, '/LoadProxyRecharge').then(function(data) {
				vm.transInfo = data.data.data;
				if (!vm.transInfo.allowTransfers) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '不允许下级转账'
					});
					return;
				}
				
				if (!vm.transInfo.hasSecurity) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '请先设置安全密保'
					});
					return;
				}
				if (!vm.transInfo.hasWithdrawPwd) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '请先设置资金密码'
					});
					return;
				}
				vm.transInfo.username = username;
				vm.transInfo.nickname = nickname;
				vm.modal_trans.show();
			});
		}

		//确定下级转账
		vm.confirmTrans = function(sid) {
			if (!vm.transObj.answer) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入密保!'
				});
				return;
			}
			if (!vm.transObj.transferType) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请选择充值方式!'
				});
				return;
			}
			if (!vm.transObj.amount) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入充值金额!'
				});
				return;
			}
			if (!vm.transObj.withdrawPwd) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请输入资金密码!'
				});
				return;
			}

			var token = passwordService.getDisposableToken();
			if (!token) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请求超时，请重新操作！'
				})
				return;
			}

			var answer = passwordService.encryptPasswordWithToken(vm.transObj.answer, token);
			var withdrawPwd = passwordService.encryptPasswordWithToken(vm.transObj.withdrawPwd, token);

			var transData = {
				sid: vm.transInfo.sBean.id,
				username: vm.transInfo.username,
				answer:  answer,
				transferType: vm.transObj.transferType,
				amount: vm.transObj.amount,
				withdrawPwd: withdrawPwd,
			}

			postDataService.submitData(transData, '/ProxyUserRecharge').then(function(data) {
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '下级转账成功!'
					});
					vm.transObj = {};
					vm.transObj.transferType = 1;
					vm.close_trans();
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}

		//查看下级
		vm.checkChild = function(username) {
			vm.page = 1;
			vm.teamObj = {
				limit: 10,
				start: (vm.page - 1) * 10,
				action: 'User',
				username: username,
				minMoney: vm.teamObj.minMoney,
				maxMoney: vm.teamObj.maxMoney,
				scope: vm.teamObj.scope
			}

			postDataService.submitData(vm.teamObj, '/ProxyUserSearch').then(function(data) {
				vm.userList = data.data.data;
				vm.teamData = data.data;
				if (vm.userList.length >= 10) {
					vm.noMorePage = false;
				}
			});
		}

		//绑定上下级查看
		$dom.off('click', 'a.up-search-btn', function() {});
		$dom.on('click', 'a.up-search-btn', function(e) {
			e.preventDefault();
			var username = $(this).text();
			vm.checkChild(username);
		});

		//彩票记录查询
		vm.searchRecord = function() {
			vm.page = 1;
			vm.noMorePage = false;
			var type = $state.params.type,
				url;
			switch (type) {
				case 'record':
					url = '/ProxyOrderSearch';
					break;
				case 'liveslot':
					url = '/ProxyGameOrderSearch';
					break;
				case 'changes':
					url = '/ProxyBillSearch';
					break;
			}
			vm.recordObj = {
				limit: 10,
				start: 0,
				sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
				eTime: $filter('date')(vm.getNetTime(vm.searchObj.eTime), 'yyyy-MM-dd'),
				lotteryId: vm.searchObj.lotteryId,
				status: vm.searchObj.status,
				account: vm.searchObj.account,
				expect: vm.searchObj.expect,
				username: vm.searchObj.username,
				type: vm.searchObj.type,
				platformId: vm.searchObj.platformId,
				scope: vm.searchObj.scope
			}
			postDataService.getPostData(vm.recordObj, url).then(function(data) {
				vm.records = data.data.data;
			});
		}
	}

	//近期开奖
	var recentOpenController = function($scope, postDataService, $ionicPopup) {
		var vm = this;
		postDataService.getPostData(null, '/App/RecentOpenCode').then(function(data) {
			vm.openList = data.data.data;
			for (var p in vm.openList) {
				vm.openList[p].balls = vm.openList[p].code.split(',');
			}
		});
	}
	
	// 历史开奖记录
	var lotteryHistoryController = function($scope, $timeout, $state, postDataService) {
		var vm = this;
		vm.lotteryId = $state.params.id;
		var searchObj = {lotteryId: vm.lotteryId, command: 'latest-30'};

		vm.search = function() {
			postDataService.getPostData(searchObj, '/LotteryCodeTrend').then(function(data) {
				vm.records = data.data.list;
				vm.lottery = data.data.lottery;
			});
		}

		vm.search();
		
		vm.refresh = function(){
			vm.search();
			$timeout(function() {
				$scope.$broadcast('scroll.refreshComplete');
			}, 1000);
		};
	}

	var googleController = function($scope, $state, postDataService, $ionicPopup, passwordService) {
		var vm = this;
		postDataService.getPostData(null, '/BindGoogleGet').then(function(data) {
			vm.googleInfo = data.data.data;
		});

		vm.bindGoogle = function() {
			if (!$('#keyAnswer').val()) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '密保答案不能为空'
				});
				return;
			}
			if (!$('#vCode').val()) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '谷歌口令不能为空'
				});
				return;
			}

			var token = passwordService.getDisposableToken();
			if (!token) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '请求超时，请重新操作！'
				})
				return;
			}

			var answer = passwordService.encryptPasswordWithToken($('#keyAnswer').val(), token);
			postDataService.submitData({
				sid: vm.googleInfo.sid,
				answer: answer,
				vCode: $('#vCode').val()
			}, '/BindGoogle').then(function(data) {
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '谷歌身份绑定成功！'
					}).then(function(){
						$state.go('member');
					});
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}
	}

	//活动
	var promoController = function($scope, postDataService, $ionicPopup, $q, $state, $ionicModal, utilService) {
		var vm = this;
		vm.state = $state.params.id;
		vm.drawObj = {
			drawBtnClass : 'zp2',
			share: 10,
			speed: "12s",
			velocityCurve:"ease",
			weeks:6
		}

		vm.loadWheelData = function(){
			postDataService.getPostData(null, '/LoadActivityWheel').then(function(data) {
				if (data.data.error === 2) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					}).then(function(){
						$state.go('home');
					});
					return;
				}
				else {
					vm.data = data.data.data;
				}
			});
		}

		vm.loadWheelData();

		vm.doWheel = function(){
			vm.openWheelModal();
			// postDataService.submitData(null, '/ActivityWheelDraw').then(function(data) {
			// 	if (data.data.error === 0) {
			// 		var msg;
			// 		if (data.data.amount && data.data.amount > 0) {
			// 			var collectAmount = utilService.setMaxScale(data.data.amount, 3);
			// 			msg = '恭喜您中奖' + collectAmount + '元！';
			// 		}
			// 		else {
			// 			msg = '抽奖失败，请联系客服！';
			// 		}
			// 		$ionicPopup.alert({
			// 			title: '温馨提示',
			// 			template: msg
			// 		}).then(function(){
			// 			vm.loadWheelData();
			// 		});
			// 	} else {
			// 		$ionicPopup.alert({
			// 			title: '温馨提示',
			// 			template: data.data.message
			// 		});
			// 	}
			// });
		}

		//玩法说明点击
		$ionicModal.fromTemplateUrl('/template/wheel.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.wheelModal = modal;
		});
		vm.openWheelModal = function() {
			vm.drawObj.drawBtnClass = 'zp2';
			vm.wheelModal.show();
		};
		vm.closeWheelModal = function() {
			vm.wheelModal.hide();
		};

		// 关闭所有窗口
		$scope.$on("$destroy", function(){
			vm.closeWheelModal();
		});

		vm.startDrawWheel = function() {
			if(vm.drawObj.startTurningOk) return;

			postDataService.getPostData(null, '/ActivityWheelDraw').then(function(data) {
				if (data.data.error != 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					}).then(function(){
						vm.closeWheelModal();
					});
					return;
				}

				var money = parseInt(data.data.amount);
				if (money <= 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '抽奖失败，请联系客服！'
					}).then(function(){
						vm.closeWheelModal();
					});
					return;
				}

				var drawPosition = -1;
				switch (money) {
					case 18: drawPosition = 8; break;
					case 28: drawPosition = 7; break;
					case 88: drawPosition = 2; break;
					case 128: drawPosition = 10; break;
					case 168: drawPosition = 4; break;
					case 288: drawPosition = 9; break;
					case 518: drawPosition = 6; break;
					case 888: drawPosition = 4; break;
					case 1688: drawPosition = 3; break;
					case 2888: drawPosition = 1; break;
				}
				if (drawPosition <= -1) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: "恭喜您中奖" + money + "元！"
					}).then(function(){
						vm.closeWheelModal();
						vm.loadWheelData();
					});
				}
				else {
					vm.turntableDraw(drawPosition, function(){
						$ionicPopup.alert({
							title: '温馨提示',
							template: "恭喜您中奖" + money + "元！"
						}).then(function(){
							vm.closeWheelModal();
							vm.loadWheelData();
						});
					});
				}
			});
		}

		vm.initDrawBtn = function() {
			vm.drawObj.newClass = "rotary"+"new"+parseInt(Math.random()*1000);
			var _jiaodu = parseInt(360/vm.drawObj.share);
			var _yuan = 360*(vm.drawObj.weeks || 4);
			var _str = "";
			var _speed = vm.drawObj.speed || "2s";
			var _velocityCurve = vm.drawObj.velocityCurve || "ease";
			for(var i=1;i<=vm.drawObj.share;i++)
			{
				_str+="."+vm.drawObj.newClass+i+"{";
				_str+="transform:rotate("+((i-1)*_jiaodu+_yuan)+"deg);";
				_str+="-ms-transform:rotate("+((i-1)*_jiaodu+_yuan)+"deg);";
				_str+="-moz-transform:rotate("+((i-1)*_jiaodu+_yuan)+"deg);";
				_str+="-webkit-transform:rotate("+((i-1)*_jiaodu+_yuan)+"deg);";
				_str+="-o-transform:rotate("+((i-1)*_jiaodu+_yuan)+"deg);";
				_str+="transition: transform "+_speed+" "+_velocityCurve+";";
				_str+="-moz-transition: -moz-transform "+_speed+" "+_velocityCurve+";";
				_str+="-webkit-transition: -webkit-transform "+_speed+" "+_velocityCurve+";";
				_str+="-o-transition: -o-transform "+_speed+" "+_velocityCurve+";";
				_str+="}";
				_str+="."+vm.drawObj.newClass+i+"stop{";
				_str+="transform:rotate("+((i-1)*_jiaodu)+"deg);";
				_str+="-ms-transform:rotate("+((i-1)*_jiaodu)+"deg);";
				_str+="-moz-transform:rotate("+((i-1)*_jiaodu)+"deg);";
				_str+="-webkit-transform:rotate("+((i-1)*_jiaodu)+"deg);";
				_str+="-o-transform:rotate("+((i-1)*_jiaodu)+"deg);";
				_str+="}";
			};
			$(document.head).append("<style>"+_str+"</style>");
		}

		vm.initDrawBtn();

		vm.turntableDraw = function(drawPosition, callback) {
			if(vm.drawObj.startTurningOk){return};

			var _speed = vm.drawObj.speed.replace(/s/,"") * 1000;

			vm.drawObj.drawBtnClass = vm.drawObj.drawBtnClass+" "+vm.drawObj.newClass+drawPosition
			vm.drawObj.startTurningOk = true;
			setTimeout(function(){
				if(callback) {
					vm.drawObj.startTurningOk = false;
					callback(drawPosition);
				};
			},_speed+10);
		};
	}

	// 报表中心 彩票报表/游戏报表/主账户报表
	var reportController = function($scope, $timeout, $filter, $state, postDataService, $ionicPopup, $ionicScrollDelegate) {
		var vm = this,sTime = new Date(),eTime = new Date();
		vm.hasMore = false; // 默认没有更多
		vm.page = vm.page || 1;
		vm.type = $state.params.type;
		vm.userLevels = [];
		var searchURL = vm.type == 'lottery' ? '/UserLotteryReport'
							: vm.type == 'game' ? '/UserGameReport'
							: vm.type == 'main' ? '/UserMainReport' : '';
		vm.limit = 10;
		vm.searchObj = {
			limit: 10,
			start: 0,
			sTime: sTime,
			eTime: eTime,
			username: ''
		}
		vm.getSearchParam = function(){
			var _eTime = new Date(vm.searchObj.eTime);
			_eTime.setDate(_eTime.getDate()+1);
			vm.betObj = {
				limit: 10,
				start: (vm.page - 1) * vm.limit,
				sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
				eTime: $filter('date')(_eTime, 'yyyy-MM-dd'),
				username: vm.searchObj.username
			}
		}
		vm.getSearchParam();
		postDataService.getPostData(vm.betObj, searchURL).then(function(data) {
			if (data.data.error === 0) {
				vm.userLevels = data.data.userLevels;
				vm.records = data.data.data;
				vm.hasMore = vm.records.length < data.data.totalCount;
			} else {
				$ionicPopup.alert({
					title: '温馨提示',
					template: data.data.message
				});
			}
		});

		//绑定上下级查看
		vm.checkchild = function(name) {
			vm.searchObj.username = name;
			vm.search();
		}

		vm.checkUpper = function(){
			if (vm.userLevels.length <= 2) {
				vm.searchObj.username = '';
			}
			else {
				vm.searchObj.username = vm.userLevels[vm.userLevels.length-2];
			}
			vm.search();
			return;
		}

		// 下拉刷新
		vm.refresh = function() {
			vm.page = 1;
			vm.search();
			$timeout(function() {
				$scope.$broadcast('scroll.refreshComplete');
			}, 1000);
		}

		vm.search = function() {
			vm.page = 1;
			vm.getSearchParam();
			postDataService.getPostData(vm.betObj, searchURL).then(function(data) {
				if (data.data.error === 0) {
					vm.userLevels = data.data.userLevels;
					vm.records = data.data.data;
					vm.hasMore = vm.records.length < data.data.totalCount;
					$ionicScrollDelegate.scrollTop();
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}

		//上拉
		vm.loadmore = function(page) {
			//首先判断记录数是否超过10条
			if (!vm.records || vm.records.length < 10) {
				vm.hasMore = false;
				$scope.$broadcast('scroll.infiniteScrollComplete');
				return;
			}
			vm.page = vm.page + 1;
			vm.getSearchParam();
			postDataService.submitData(vm.betObj, searchURL).then(function(data) {
				if (data.data.error === 0) {
					var datas = data.data.data;
					for (var p in datas) {
						if (datas[p].name != '总计') {
							vm.records.push(datas[p]);
						}
					}
					vm.hasMore = vm.records.length < data.data.totalCount;
					$scope.$broadcast('scroll.infiniteScrollComplete');
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}
	}

	// 契约日结
	var settleBillController = function($scope, $timeout, $filter, $state, postDataService, $ionicPopup, $ionicScrollDelegate, $ionicModal, $ionicLoading) {
		var vm = this,sTime = new Date(),eTime = new Date();
		vm.type = $state.params.type;
		vm.hasMore = false; // 默认没有更多
		//eTime.setDate(eTime.getDate());
		vm.page = vm.page || 1;
		var searchURL = vm.type == 'contractbill' ? '/DailySettleBillSearch'
			: vm.type == 'contractmanage' ? '/DailySettleSearch'
			: vm.type == 'startcontract' ? '/DailySettleRequestData'
			: vm.type == 'gamewater' ? '/GameWaterBillSearch' : '';
		vm.limit = 10;
		vm.searchObj = {
			limit: 10,
			start: 0,
			sTime: sTime,
			eTime: eTime,
			username: '',
			scope: 1
		}
		vm.getNetTime = function(date){
			var _eTime = new Date(date);
			_eTime.setDate(_eTime.getDate()+1);
			return _eTime;
		}
		vm.startContractObj = {
			"username": '',
			"scale": '',
			"minValidUser": ''
		}
		vm.currentUsername = '';
		vm.getSearchParam = function(){
			if (vm.type == 'contractbill' || vm.type == 'gamewater') {
				vm.contractObj = {
					limit: vm.limit,
					start: (vm.page - 1) * vm.limit,
					sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
					eTime: $filter('date')(vm.getNetTime(vm.searchObj.eTime), 'yyyy-MM-dd'),
					username: vm.searchObj.username,
					scope: vm.searchObj.scope
				}
			}
			else if (vm.type == 'contractmanage') {
				vm.contractObj = {
					limit: vm.limit,
					start: (vm.page - 1) * vm.limit,
					username: vm.searchObj.username,
					scope: vm.searchObj.scope
				}
			}
			else if (vm.type == 'startcontract') {
				vm.contractObj = {}
			}
		}
		vm.getSearchParam();
		postDataService.getPostData(vm.contractObj, searchURL).then(function(data) {
			if (vm.type === 'startcontract') {
				if (data.data.error === 0) {
					vm.initData = data.data;
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						content: data.data.message
					});
				}
			}
			else {
				vm.records = data.data.data;
				if(vm.type == 'contractmanage'){
					  for(var k =0; k < vm.records.length; k++){
							  var item = vm.records[k];
							  var sales = item.salesLevel.split(',');
				              var loss = item.lossLevel.split(',');
				              var scale = item.scaleLevel.split(',');
				              var user = item.userLevel.split(',');
				              var showHtml = new Array(sales.length);
				              for(var j = 0; j < sales.length; j++){
				            	var obj = {};
			              		obj.sales = '条款(' + (j + 1) + ')：销量 >= ' + sales[j] + 'W ；';
			              		obj.loss =   '亏损(' + (j + 1) + ')：亏损 >= '  + loss[j] + 'W ；';
			              		obj.user =   '人数(' + (j + 1) + ')：人数 >= '  + user[j] + '人 ；';
			              		obj.scale =  '比例(' + (j + 1) + ')：'  + '分红比例：' + scale[j] + '%；';
				              	showHtml[j] = obj;
				            }
				              item.showHtml = showHtml;
					  }
				}
				vm.currentUsername = data.data.username;
				vm.userLevels = data.data.userLevels;
				vm.hasMore = vm.records.length < data.data.totalCount;
			}
		});

		// 加载选择用户弹出窗
		if (vm.type === 'startcontract') {
			$ionicModal.fromTemplateUrl('/template/settle_bill_userselect.html', {
				scope: $scope,
				animation: 'slide-in-up'
			}).then(function(modal) {
				vm.modal_user = modal;
			});
			$scope.$on('$destroy', function() {
				vm.modal_user.remove();
			});
		}

		//绑定详情
		vm.checkchild = function(name) {
			vm.searchObj.username = name;
			vm.search();
		}

		// 下拉刷新
		vm.refresh = function() {
			if (vm.type !== 'startcontract') {
				vm.search();
				$timeout(function() {
					$scope.$broadcast('scroll.refreshComplete');
				}, 1000);
			}
		}

		//选择用户
		vm.pickuser = function() {
			vm.modal_user.show();
			postDataService.getPostData(null, "/DailySettleListLower").then(function(data) {
				vm.members = data.data.list;
			});
		}

		// 确认选择用户
		vm.confirm_pick_member = function() {
			if (!vm.userObj) {
				$ionicPopup.alert({
					title: '温馨提示',
					content: '请选择用户'
				});
				return;
			}
			else {
				postDataService.getPostData({
					username: vm.userObj
				}, "/DailySettleRequestData").then(function(data) {
					if (data.data.error === 0) {
						vm.initData = data.data;
						$('#contract_username').val(vm.userObj);
						vm.closeUserModal();
					} else {
						$ionicPopup.alert({
							title: '温馨提示',
							template: data.data.message
						});
					}
				});
			}
		}

		//确定发起契约日结
		vm.confirm_start = function() {
			if (!$('#contract_username').val()) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '用户名不能为空'
				});
				return;
			}
			if (vm.startContractObj.scale == undefined || vm.startContractObj.scale === '') {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '日结比例不能为空'
				});
				return;
			}
			else if (vm.startContractObj.scale < vm.initData.minScale || vm.startContractObj.scale > vm.initData.maxScale) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '日结比例不在范围值内'
				});
				return;
			}

			if (vm.startContractObj.minValidUser === undefined || vm.startContractObj.minValidUser === '') {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '最低有效人数不能为空'
				});
				return;
			}
			else if (vm.startContractObj.minValidUser < vm.initData.minValidUser || vm.startContractObj.minValidUser > vm.initData.maxValidUser) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '最低有效人数不在范围值内'
				});
				return;
			}
			vm.startContractObj.username = $('#contract_username').val();
			postDataService.submitData(vm.startContractObj, '/DailySettleRequest').then(function(data) {
				if(data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '契约日结发起成功，对方同意后即可生效！'
					}).then(function () {
						$state.reload();
					});
				}
				else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}
		
		vm.closeUserModal = function () {
			vm.modal_user.hide();
		}

		vm.agree = function(item){
			if (item.status != 2 || vm.currentUsername !== item.username) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '这不是您的契约日结,请勿操作！'
				});
				return;
			}

			$ionicPopup.confirm({
				title: '确认同意',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '<b>确定</b>',
					type: 'button-assertive',
					onTap: function() {
						$ionicLoading.show({template: '正在加载。。。'});

						postDataService.submitData({id: item.id}, '/DailySettleAgree').then(function(data) {
							if(data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '契约日结同意成功，将于次日凌晨开始结算！'
								}).then(function () {
									$state.reload();
								});
							}
							else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}
							$ionicLoading.hide();
						});
					}
				}],
				template: '确认同意吗？同意后将于次日凌晨开始结算'
			});
		}
		
		vm.deny = function(item){
			if (item.status != 2 || vm.currentUsername !== item.username) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '这不是您的契约日结,请勿操作！'
				});
				return;
			}

			$ionicPopup.confirm({
				title: '确认同意',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '<b>确定</b>',
					type: 'button-assertive',
					onTap: function() {
						$ionicLoading.show({template: '正在加载。。。'});

						postDataService.submitData({id: item.id}, '/DailySettleDeny').then(function(data) {
							if(data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '契约日结拒绝成功！'
								}).then(function () {
									$state.reload();
								});
							}
							else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}
							$ionicLoading.hide();
						});
					}
				}],
				template: '确认拒绝吗？拒绝后可以联系您的上级再次发起'
			});
		}

		// 条件查询
		vm.search = function() {
			if (vm.type !== 'startcontract') {
				vm.page = 1;
				vm.getSearchParam();
				postDataService.submitData(vm.contractObj, searchURL).then(function(data) {
					vm.records = data.data.data;
					vm.currentUsername = data.data.username;
					vm.userLevels = data.data.userLevels;
					vm.hasMore = vm.records.length < data.data.totalCount;
					$ionicScrollDelegate.scrollTop();
				});
			}
		}

		//上拉
		vm.loadmore = function(page) {
			if (vm.type !== 'startcontract') {
				if (!vm.records || vm.records.length < 10) {
					vm.hasMore = false;
					$scope.$broadcast('scroll.infiniteScrollComplete');
					return;
				}
				vm.page = vm.page + 1;
				vm.getSearchParam();
				postDataService.submitData(vm.contractObj, searchURL).then(function(data) {
					var datas = data.data.data;
					for (var p in datas) {
						vm.records.push(datas[p]);
					}
					vm.hasMore = vm.records.length < data.data.totalCount;
					$scope.$broadcast('scroll.infiniteScrollComplete');
				});
			}
		}
	}

	// 契约分红
	var bonusBillController = function($scope, $timeout, $filter, $state, postDataService, $ionicPopup, $ionicScrollDelegate, $ionicModal, $ionicLoading) {
		var vm = this,sTime = new Date(),eTime = new Date();
		vm.type = $state.params.type;
		vm.hasMore = false; // 默认没有更多
		sTime.setDate(sTime.getDate() - 31);
		eTime.setDate(eTime.getDate() + 30);
		vm.page = vm.page || 1;
		var searchURL = vm.type == 'bonusbill' ? '/DividendBillSearch'
			: vm.type == 'bonusmanage' ? '/DividendSearch'
			: vm.type == 'startbonus' ? '/DividendRequestData'
			: vm.type == 'gamebonus' ? '/GameDividendBillSearch' : '';
		vm.limit = 10;
		vm.searchObj = {
			limit: 10,
			start: 0,
			sTime: sTime,
			eTime: eTime,
			username: ''
		}
		vm.startBonusObj = {
			"username": '',
			"scale": '',
			"minValidUser": ''
		}
		vm.currentUsername = '';
		vm.getSearchParam = function(){
			if (vm.type == 'bonusbill' || vm.type == 'gamebonus') {
				vm.bonusObj = {
					limit: vm.limit,
					start: (vm.page - 1) * vm.limit,
					sTime: $filter('date')(vm.searchObj.sTime, 'yyyy-MM-dd'),
					eTime: $filter('date')(vm.searchObj.eTime, 'yyyy-MM-dd'),
					username: vm.searchObj.username
				}
			}
			else if (vm.type == 'bonusmanage') {
				vm.bonusObj = {
					limit: vm.limit,
					start: (vm.page - 1) * vm.limit,
					username: vm.searchObj.username
				}
			}
			else if (vm.type == 'startbonus') {
				vm.bonusObj = {}
			}
		}
		vm.getSearchParam();
		postDataService.getPostData(vm.bonusObj, searchURL).then(function(data) {
			if (vm.type === 'startbonus') {
				if (data.data.error === 0) {
					vm.initData = data.data;
				} else {
					$ionicPopup.alert({
						title: '温馨提示',
						content: data.data.message
					});
				}
			}
			else {
				vm.records = data.data.data;
				if(vm.type == 'bonusmanage'){
					  for(var k =0; k < vm.records.length; k++){
							  var item = vm.records[k];
							  var sales = item.salesLevel.split(',');
				              var loss = item.lossLevel.split(',');
				              var scale = item.scaleLevel.split(',');
				              var user = item.userLevel.split(',');
				              var showHtml = new Array(sales.length);
				              for(var j = 0; j < sales.length; j++){
				            	var obj = {};
				            	obj.sales = '条款(' + (j + 1) + ')：销量 >= ' + sales[j] + 'W ；';
			              		obj.loss =   '亏损(' + (j + 1) + ')：亏损 >= '  + loss[j] + 'W ；';
			              		obj.user =   '人数(' + (j + 1) + ')：人数 >= '  + user[j] + '人；';
			              		obj.scale =  '比例(' + (j + 1) + ')：'  + '分红比例：' + scale[j] + '%；';
				              	showHtml[j] = obj;
				            }
				              item.showHtml = showHtml;
					  }
				}
				vm.currentUsername = data.data.username;
				vm.userLevels = data.data.userLevels;
				vm.hasMore = vm.records.length < data.data.totalCount;
			}
		});

		// 加载选择用户弹出窗
		if (vm.type === 'startbonus') {
			$ionicModal.fromTemplateUrl('/template/bonus_bill_userselect.html', {
				scope: $scope,
				animation: 'slide-in-up'
			}).then(function(modal) {
				vm.modal_user = modal;
			});
			$scope.$on('$destroy', function() {
				vm.modal_user.remove();
			});
		}

		// 加载分红详情弹出窗
		if (vm.type === 'bonusbill') {
			$ionicModal.fromTemplateUrl('/template/bonusDetail.html', {
				scope: $scope,
				animation: 'slide-in-up'
			}).then(function(modal) {
				vm.modal_bonus = modal;
			});
			$scope.$on('$destroy', function() {
				vm.modal_bonus.remove();
			});
		}

		//绑定详情
		vm.checkchild = function(name) {
			vm.searchObj.username = name;
			vm.search();
		}

		// 下拉刷新
		vm.refresh = function() {
			if (vm.type !== 'startbonus') {
				vm.search();
				$timeout(function() {
					$scope.$broadcast('scroll.refreshComplete');
				}, 1000);
			}
		}

		//选择用户
		vm.pickuser = function() {
			vm.modal_user.show();
			postDataService.getPostData(null, "/DividendListLower").then(function(data) {
				vm.members = data.data.list;
			});
		}

		// 确认选择用户
		vm.confirm_pick_member = function() {
			if (!vm.userObj) {
				$ionicPopup.alert({
					title: '温馨提示',
					content: '请选择用户'
				});
				return;
			}
			else {
				postDataService.getPostData({
					username: vm.userObj
				}, "/DividendRequestData").then(function(data) {
					if (data.data.error === 0) {
						vm.initData = data.data;
						$('#contract_username').val(vm.userObj);
						vm.closeUserModal();
					} else {
						$ionicPopup.alert({
							title: '温馨提示',
							template: data.data.message
						});
					}
				});
			}
		}

		//确定发起契约分红
		vm.confirm_dividend = function() {
			if (!$('#contract_username').val()) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '用户名不能为空'
				});
				return;
			}
			if (vm.startBonusObj.scale == undefined || vm.startBonusObj.scale === '') {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '分红比例不能为空'
				});
				return;
			}
			else if (vm.startBonusObj.scale < vm.initData.minScale || vm.startBonusObj.scale > vm.initData.maxScale) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '分红比例不在范围值内'
				});
				return;
			}

			if (vm.startBonusObj.minValidUser === undefined || vm.startBonusObj.minValidUser === '') {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '最低有效人数不能为空'
				});
				return;
			}
			else if (vm.startBonusObj.minValidUser < vm.initData.minValidUser || vm.startBonusObj.minValidUser > vm.initData.maxValidUser) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '最低有效人数不在范围值内'
				});
				return;
			}
			vm.startBonusObj.username = $('#contract_username').val();
			postDataService.submitData(vm.startBonusObj, '/DividendRequest').then(function(data) {
				if(data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示',
						template: '契约分红发起成功，对方同意后即可生效！'
					}).then(function () {
						$state.reload();
					});
				}
				else {
					$ionicPopup.alert({
						title: '温馨提示',
						template: data.data.message
					});
				}
			});
		}
		
		vm.closeUserModal = function () {
			vm.modal_user.hide();
		}

		vm.closeBonusModal = function() {
			vm.modal_bonus.hide();
		}
		
		vm.agree = function(item){
			if (item.status != 2 || vm.currentUsername !== item.username) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '这不是您的契约分红,请勿操作！'
				});
				return;
			}

			$ionicPopup.confirm({
				title: '确认同意',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '<b>确定</b>',
					type: 'button-assertive',
					onTap: function() {
						$ionicLoading.show({template: '正在加载。。。'});

						postDataService.submitData({id: item.id}, '/DividendAgree').then(function(data) {
							if(data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '契约分红同意成功，将于本周期开始结算！'
								}).then(function () {
									$state.reload();
								});
							}
							else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}
							$ionicLoading.hide();
						});
					}
				}],
				template: '确认同意吗？同意后将于本周期开始结算'
			});
		}
		
		vm.deny = function(item){
			if (item.status != 2 || vm.currentUsername !== item.username) {
				$ionicPopup.alert({
					title: '温馨提示',
					template: '这不是您的契约分红,请勿操作！'
				});
				return;
			}

			$ionicPopup.confirm({
				title: '确认同意',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '<b>确定</b>',
					type: 'button-assertive',
					onTap: function() {
						$ionicLoading.show({template: '正在加载。。。'});

						postDataService.submitData({id: item.id}, '/DividendDeny').then(function(data) {
							if(data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '契约分红拒绝成功！'
								}).then(function () {
									$state.reload();
								});
							}
							else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}
							$ionicLoading.hide();
						});
					}
				}],
				template: '确认拒绝吗？拒绝后可以联系您的上级再次发起'
			});
		}

		//查看契约详情
		vm.checkBonusDetail = function(item) {
			vm.currentBonus = item;
			vm.modal_bonus.show();
		}

		//领取分红
		vm.getBonus = function(item) {
			$ionicPopup.confirm({
				title: '领取分红',
				template: "<p>分红金额：" + item.bean.userAmount + "</p><p>累计领取：" + item.bean.totalReceived + "</p><p>目前可领取:" + item.bean.availableAmount + "</p>",
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '领取',
					type: 'button-positive',
					onTap: function() {
						postDataService.submitData({
								id: item.bean.id
							},
							'/DividendCollect').then(function(data) {
							if (data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '成功领取'
								}).then(function() {
									$state.reload();
								});
							} else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}
						});
					}
				}]
			});
		}

		//领取游戏分红
		vm.getGameBonus = function(item) {
			$ionicPopup.confirm({
				title: '领取老虎机真人分红',
				template: "<p>投注："+item.bean.billingOrder+"<p><p>分红比例："+(item.bean.scale*100)+"</p><p>亏损："+item.bean.totalLoss+"</p>金额：" + item.bean.userAmount + "</p>",
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '领取',
					type: 'button-positive',
					onTap: function() {
						postDataService.submitData({
								id: item.bean.id
							},
							'/GameDividendCollect').then(function(data) {
							if (data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '成功领取'
								}).then(function() {
									$state.reload();
								});
							} else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}
						});
					}
				}]
			});
		}

		//发放分红
		vm.sendBonus = function(item) {
			$ionicPopup.confirm({
				title: '发放契约分红',
				template: "<p>分红金额：" + item.bean.userAmount + "</p><p>累计领取：" + item.bean.totalReceived + "</p><p>目前可领取:" + item.bean.availableAmount + "</p>",
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '领取',
					type: 'button-positive',
					onTap: function() {
						postDataService.submitData({
								id: item.bean.id
							},
							'/DividendIssue').then(function(data) {
							if (data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示',
									template: '发放成功'
								}).then(function() {
									$state.reload();
								});
							} else {
								$ionicPopup.alert({
									title: '温馨提示',
									template: data.data.message
								});
							}
						});
					}
				}]
			});
		}

		// 条件查询
		vm.search = function() {
			if (vm.type !== 'startbonus') {
				vm.page = 1;
				vm.getSearchParam();
				postDataService.submitData(vm.bonusObj, searchURL).then(function(data) {
					vm.records = data.data.data;
					vm.currentUsername = data.data.username;
					vm.userLevels = data.data.userLevels;
					vm.hasMore = vm.records.length < data.data.totalCount;
					$ionicScrollDelegate.scrollTop();
				});
			}
		}

		//上拉
		vm.loadmore = function(page) {
			if (vm.type !== 'startbonus') {
				if (!vm.records || vm.records.length < 10) {
					vm.hasMore = false;
					$scope.$broadcast('scroll.infiniteScrollComplete');
					return;
				}
				vm.page = vm.page + 1;
				vm.getSearchParam();
				postDataService.submitData(vm.bonusObj, searchURL).then(function(data) {
					var datas = data.data.data;
					for (var p in datas) {
						vm.records.push(datas[p]);
					}
					vm.hasMore = vm.records.length < data.data.totalCount;
					$scope.$broadcast('scroll.infiniteScrollComplete');
				});
			}
		}
	}

	//消息中心
	var messageController = function($scope, $state, $timeout, $filter, postDataService, $ionicModal, $ionicPopup) {
		var vm = this;
		var type = $state.params.type,
			url;
		vm.showinbox = true;
		vm.page = vm.page || 1;
		vm.limit = 10;
		vm.sendObj = {};
		vm.submitObj = {
			limit: vm.limit,
			start: (vm.page - 1) * vm.limit
		};
		vm.switchTab = function(type) {
			$('#betrecord').find('a.' + type).addClass('active').siblings('a').removeClass('active');
			switch (type) {
				case 'inbox':
					url = '/UserMessageInbox';
					vm.showinbox = true;
					vm.showoutbox = false;
					vm.showsend = false;
					vm.showsysmsg = false;
					break;
				case 'outbox':
					url = '/UserMessageOutbox';
					vm.showinbox = false;
					vm.showoutbox = true;
					vm.showsend = false;
					vm.showsysmsg = false;
					break;
				case 'send':
					vm.showinbox = false;
					vm.showoutbox = false;
					vm.showsend = true;
					vm.showsysmsg = false;
					return;
				case 'sysmsg':
					url = '/UserSysMessage';
					vm.showinbox = false;
					vm.showoutbox = false;
					vm.showsend = false;
					vm.showsysmsg = true;
					break;
			}
			postDataService.getPostData(vm.submitObj, url).then(function(data) {
				vm.messages = data.data.data;
			});
		}
		vm.switchTab(type);

		//下拉刷新
		vm.reflesh = function() {
			var type = $('.tab-item.active').data('type');
			vm.switchTab(type);
			$timeout(function() {
				$scope.$broadcast('scroll.refreshComplete');
			}, 1000);
		}

		//上拉
		vm.loadmore = function(page) {
			//首先判断记录数是否超过10条
			var url = "";
			if (!vm.messages || vm.messages.length < 10) {
				vm.noMorePage = false;
				$scope.$broadcast('scroll.infiniteScrollComplete');
				return;
			}
			var type = $('.tab-item.active').data('type');
			vm.page = vm.page + 1;
			vm.submitObj = {
				limit: vm.limit,
				start: (vm.page - 1) * vm.limit
			};
			switch (type) {
				case 'inbox':
					url = '/UserMessageInbox';
					vm.showinbox = true;
					vm.showoutbox = false;
					vm.showsend = false;
					vm.showsysmsg = false;
					break;
				case 'outbox':
					url = '/UserMessageOutbox';
					vm.showinbox = false;
					vm.showoutbox = true;
					vm.showsend = false;
					vm.showsysmsg = false;
					break;
				case 'sysmsg':
					url = '/UserSysMessage';
					vm.showinbox = false;
					vm.showoutbox = false;
					vm.showsend = false;
					vm.showsysmsg = true;
					break;
			}
			postDataService.submitData(vm.submitObj, url).then(function(data) {
				var datas = data.data.data;
				if (!datas.length) {
					//没有更多内容了
					vm.noMorePage = true;
				} else {
					vm.noMorePage = false;
					for (var p in datas) {
						vm.messages.push(datas[p]);
					}
				}
				$scope.$broadcast('scroll.infiniteScrollComplete');
			});
		}

		//删除消息
		vm.delmsg = function(id) {
			var delurl, tag, url;
			switch (type) {
				case 'inbox':
					tag = 'inbox';
					url = '/DelUserMessage'
					break;
				case 'outbox':
					tag = 'outbox';
					url = '/DelUserMessage'
					break;
				case 'sysmsg':
					url = '/DelUserSysMessage'
					break;
			}
			$ionicPopup.confirm({
				title: '温馨提示',
				template: '确定要删除该消息吗？',
				buttons: [{
					text: '取消',
					type: 'button-dark'
				}, {
					text: '确定',
					type: 'button-assertive',
					onTap: function() {
						postDataService.submitData({
							ids: id,
							type: tag
						}, url).then(function(data) {
							if (data.data.error === 0) {
								$ionicPopup.alert({
									title: '温馨提示 ',
									template: '删除成功！'
								}).then(function() {
									$state.reload();
								});
								return;
							}
							$ionicPopup.alert({
								title: '温馨提示 ',
								template: data.data.message
							});
						});
					}
				}]
			});
		}

		//发送消息
		vm.send = function() {
			if (!vm.sendObj.target) {
				$ionicPopup.alert({
					title: '温馨提示 ',
					template: '请选择收件人'
				});
				return;
			}
			if (vm.sendObj.target === 'lower') {
				if (!vm.sendObj.toUsers) {
					$ionicPopup.alert({
						title: '温馨提示 ',
						template: '请选择收件人！'
					});
					return;
				}
			} else {
				vm.sendObj.toUsers = '';
			}
			if (!vm.sendObj.subject) {
				$ionicPopup.alert({
					title: '温馨提示 ',
					template: '请填写标题！'
				});
				return;
			}
			if (!vm.sendObj.content) {
				$ionicPopup.alert({
					title: '温馨提示 ',
					template: '请填写内容！'
				});
				return;
			}

			postDataService.submitData(vm.sendObj, '/SendUserMessage').then(function(data) {
				if (data.data.error === 0) {
					$ionicPopup.alert({
						title: '温馨提示 ',
						template: '发送成功！'
					});
					vm.sendObj = {};
					return;
				}
				$ionicPopup.alert({
					title: '温馨提示 ',
					template: data.data.message
				});
			});
		}

		//
		$ionicModal.fromTemplateUrl('/template/messageDetail.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modal = modal;
		});
		vm.close_message = function() {
			vm.modal.hide();
		};

		$ionicModal.fromTemplateUrl('/template/messageUserPick.html', {
			scope: $scope,
			animation: 'slide-in-up'
		}).then(function(modal) {
			vm.modal_user = modal;
		});
		vm.close = function() {
			vm.modal_user.hide();
		}

		$scope.$on('$destroy', function() {
			vm.close_message();
			vm.close();
		});

		//查看用户消息详情
		vm.checkDetail = function(item) {
			vm.modal.show();
			vm.messageObj = item;
			if (item.toStatus === 0) {
				postDataService.submitData({
					ids: item.id
				}, '/ReadUserMessage').then(function(data) {
					if (data.data.error === 0) {
						item.toStatus = 1;
					}
				});
			}
		}

		//查看系统消息详情
		vm.checkSysMessageDetail = function(item) {
			vm.modal.show();
			vm.messageObj = item;
			if (item.status === 0) {
				postDataService.submitData({
					ids: item.id
				}, '/ReadUserSysMessage').then(function(data) {
					if (data.data.error === 0) {
						item.status = 1;
					}
				});
			}
		}

		//选择下级
		vm.pickuser = function() {
			vm.modal_user.show();
			postDataService.getPostData(null, '/ListUserDirectLower').then(function(data) {
				vm.members = data.data.list;
			});
		}

		vm.confirm_pick_member = function() {
			vm.close();
			vm.sendObj.toUsers = vm.userObj;
		}
	}

	//电子游戏
	var gamesController = function($scope, $state, $ionicSideMenuDelegate, postDataService, $q) {
		var vm = this;
		$q.all([postDataService.getPostData({
			platformId: 11
		}, '/GameTypeSearch'), postDataService.getPostData({
			name: '',
			typeId: 1,
			platformId: 11
		}, '/GameSearch')]).then(function(result) {
			vm.gameTypes = result[0].data.data;
			vm.games = result[1].data.data;
		});
		vm.toggleLeftSideMenu = function() {
			$ionicSideMenuDelegate.toggleLeft();
		}
		vm.switchGames = function(typeId) {
			$ionicSideMenuDelegate.toggleLeft();
			postDataService.getPostData({
				name: '',
				typeId: typeId,
				platformId: 11
			}, '/GameSearch').then(function(data) {
				vm.games = data.data.data;
			});
		}

	}
	angular.module('app.controllers', ['app.services'])
		.controller('appController', appController) //全局控制器
		.controller('homeController', homeController) //首页控制器
		.controller('loginController', loginController) //登录控制器
		.controller('registerController', registerController) //登录控制器
		.controller('betController', betController) //投注控制器
		.controller('betRecordController', betRecordController) //投注记录控制器
		.controller('cartController', cartController) //购彩车控制器
		.controller('traceController', traceController) //追号控制器
		.controller('bankController', bankController) //银行卡控制器
		.controller('pwdController', pwdController) //资金密码控制器
		.controller('chargeRecordController', chargeRecordController) //充值提现记录
		.controller('chargeController', chargeController) //充值控制器
		.controller('withdrawController', withdrawController) //提款控制器
		.controller('transController', transController) //转账控制器
		.controller('memberController', memberController) // 个人中心控制器
		.controller('agentController', agentController) //代理中心
		.controller('settleBillController', settleBillController) //契约日结
		.controller('bonusBillController', bonusBillController) //契约分红
		.controller('recentOpenController', recentOpenController) //近期开奖
		.controller('lotteryHistoryController', lotteryHistoryController) //历史开奖记录
		.controller('googleController', googleController)
		.controller('promoController', promoController)
		.controller('messageController', messageController)
		.controller('gamesController', gamesController)
		.controller('reportController', reportController)
		.controller('tipController', tipController)
		.controller('lotteryTrendController', lotteryTrendController)
		
})();