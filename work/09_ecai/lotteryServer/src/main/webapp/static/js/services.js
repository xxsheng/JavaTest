(function() {
	/*----------  create by ian 2016-11-14  ----------*/
	'use strict';
	/*----------  describe: POST方法提交的service  ----------*/

	var postDataService = function($http, $q, $ionicLoading) {
		return {
			submitBet: function(data, url) {
				var deferred = $q.defer();
				$ionicLoading.show({
					template: '正在加载。。。'
				});
				$http.post(url, data, {
					headers: {
						"X-Requested-With": "XMLHttpRequest",
						'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
					}
				}).then(function(data) {
					$ionicLoading.hide();
					deferred.resolve(data);
				}, function(error) {
					$ionicLoading.hide();
					console.log(error.status);
					if (error.status === -1) {
						alert('网络连接问题');
						return;
					}
					alert(error.statusText);
				});
				return deferred.promise;
			},
			submitData: function(data, url) {
				var deferred = $q.defer();
				$ionicLoading.show({
					template: '正在加载。。。'
				});
				$http.post(url, $.param(data), {
					headers: {
						"X-Requested-With": "XMLHttpRequest",
						'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
					}
				}).then(function(data) {
					$ionicLoading.hide();
					deferred.resolve(data);
				}, function(error) {
					$ionicLoading.hide();
					if (error.status === -1) {
						alert('网络连接问题');
						return;
					}
					alert(error.statusText);
				});
				return deferred.promise;
			},
			getPostData: function(data, url) {
				var deferred = $q.defer();
				$ionicLoading.show({
					template: '正在加载。。。'
				});
				$http.post(url, $.param(data), {
					headers: {
						"X-Requested-With": "XMLHttpRequest",
						'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
					}
				}).then(function(data) {
					$ionicLoading.hide();
					deferred.resolve(data);
				}, function(error) {
					$ionicLoading.hide();
					if (error.status === -1) {
						alert('网络连接问题');
						return;
					}
					alert(error.statusText);
				});
				return deferred.promise;
			},
			fetchPostData: function(data, url) {
				var deferred = $q.defer();
				$http.post(url, $.param(data), {
					headers: {
						"X-Requested-With": "XMLHttpRequest",
						'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
					}
				}).then(function(data) {
					deferred.resolve(data);
				}, function(error) {
					if (error.status === -1) {
						alert('网络连接问题');
						return;
					}
					alert(error.statusText);
				});
				return deferred.promise;
			},
			fetchCodes: function(data, url) {
				var deferred = $q.defer();
				$http.post(url, $.param(data), {
					headers: {
						"X-Requested-With": "XMLHttpRequest",
						'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
					}
				}).then(function(data) {
					deferred.resolve(data);
				}, function(error) {
					deferred.reject();
					// alert(error.statusText);
				});
				return deferred.promise;
			}
		}
	}

	/*----------  describe: get方法的service  ----------*/
	var fetchDataService = function($http, $q, $ionicLoading, $state, $ionicPopup) {
		return {
			fetchData: function(url) {
				var deferred = $q.defer();
				$ionicLoading.show({
					template: '正在加载。。'
				});
				$http.get(url, {
					headers: {
						"X-Requested-With": "XMLHttpRequest"
					}
				}).then(function(data) {
					$ionicLoading.hide();
					//判断是否登录
					if (data.data.errno === 1004) {
						$ionicPopup.alert({
							title: '温馨提示',
							template: '登录过期,请重新登录！'
						}).then(function() {
							$state.go('login');
						});

					}
					deferred.resolve(data);
				}, function(error) {
					$ionicLoading.hide();
					if (error.status === -1) {
						alert('网络连接问题');
						return;
					}
					alert(error.statusText);
					return;
				});
				return deferred.promise;
			},
			fetchJson: function(url) {
				var deferred = $q.defer();
				$http.get(url).then(function(data) {
					deferred.resolve(data);
				}, function(error) {
					if (error.status === -1) {
						alert('网络连接问题');
						return;
					}
					alert(error.statusText);
					return;
				});
				return deferred.promise;
			}
		}
	}

	/*--------------------------投注的注数算法--------------------------------*/
	var lotteryUtilsService = function() {
		// 组合数
		var ComNum = function(n, m) {
			m = parseInt(m);
			n = parseInt(n);
			if (m < 0 || n < 0) {
				return false;
			}
			if (m == 0 || n == 0) {
				return 1;
			}
			if (m > n) {
				return 0;
			}
			if (m > n / 2.0) {
				m = n - m;
			}
			var result = 0.0;
			for (var i = n; i >= (n - m + 1); i--) {
				result += Math.log(i);
			}
			for (var i = m; i >= 1; i--) {
				result -= Math.log(i);
			}
			result = Math.exp(result);
			return Math.round(result);
		}

		// 组合值
		var ComVal = function(source, m, x) {
			var n = source.length;
			var list = [];
			var start = 0;
			while (m > 0) {
				if (m == 1) {
					list.push(source[start + x]);
					break;
				}
				for (var i = 0; i <= n - m; i++) {
					var cnm = ComNum(n - 1 - i, m - 1);
					if (x <= cnm - 1) {
						list.push(source[start + i]);
						start = start + (i + 1);
						n = n - (i + 1);
						m--;
						break;
					} else {
						x = x - cnm;
					}
				}
			}
			return list;
		}

		// 判断是否存在
		var inArray = function(e, data) {
			for (var i = 0; i < data.length; i++) {
				if (data[i] == e) return true;
			}
			return false;
		}

		// 数组去重复
		var uniquelize = function(data) {
			var array = new Array();
			for (var i = 0; i < data.length; i++) {
				if (!inArray(data[i], array)) {
					array.push(data[i]);
				}
			}
			return array;
		}

		//求两个集合的并集
		var union = function(a, b) {
			return uniquelize(a.concat(b));
		}

		//求两个集合的差集
		var minus = function(a, b) {
			var array = new Array();
			var ua = uniquelize(a);
			for (var i = 0; i < ua.length; i++) {
				if (!inArray(ua[i], b)) {
					array.push(ua[i]);
				}
			}
			return array;
		}

		//求两个集合的交集
		var intersect = function(a, b) {
			var array = new Array();
			var ua = uniquelize(a);
			for (var i = 0; i < ua.length; i++) {
				if (inArray(ua[i], b)) {
					array.push(ua[i]);
				}
			}
			return array;
		}

		//求两个集合的补集
		var complement = function(a, b) {
			return minus(union(a, b), intersect(a, b));
		}

		// 去除重复，最快速方法，会排序
		var unique = function(data) {
			data.sort();
			var re = [data[0]];
			for (var i = 1; i < data.length; i++) {
				if (data[i] !== re[re.length - 1]) {
					re.push(data[i]);
				}
			}
			return re;
		}

		//组三单式
		var v = function(N, I) {
			if (I != 3) {
				return false
			}
			var M = "";
			var K = "";
			var J = "";
			var L = 0;
			for (L = 0; L < I; L++) {
				if (L == 0) {
					M = N.substr(L, 1)
				}
				if (L == 1) {
					K = N.substr(L, 1)
				}
				if (L == 2) {
					J = N.substr(L, 1)
				}
			}
			if (M == K && K == J) {
				return false
			}
			if (M == K || K == J || J == M) {
				return true
			}
			return false
		};
		//组六单式
		var w = function(N, I) {
			if (I != 3) {
				return false
			}
			var M = "";
			var K = "";
			var J = "";
			var L = 0;
			for (L = 0; L < I; L++) {
				if (L == 0) {
					M = N.substr(L, 1)
				}
				if (L == 1) {
					K = N.substr(L, 1)
				}
				if (L == 2) {
					J = N.substr(L, 1)
				}
			}
			if (M == K || K == J || J == M) {
				return false
			} else {
				return true
			}
			return false
		};
		// 根据下标删除
		var remove = function(data, idx) {
			if (data.length > idx) {
				data.splice(idx, 1);
			}
			return data;
		}

		/**
		 * 输入框号码检测------11x5
		 */
		var _numberCheck_Num = function(n) {
			var t = n.split(' ');
			var l = t.length;
			for (var i = 0; i < l; i++) {
				if (Number(t[i]) > 11 || Number(t[i]) < 1) {
					return false;
				}
				for (var j = i + 1; j < l; j++) {
					if (Number(t[i]) == Number(t[j])) {
						return false;
					}
				}
			}
			return true;
		}

		/**
		 * 输入框类型检测
		 */
		var _inputCheck_Num_11x5 = function(datasel, l, fun) {
			fun = $.isFunction(fun) ? fun : function(n, l) {
				return true;
			}
			var newsel = []; // 新的号码
			datasel = unique(datasel); // 去除重复
			var regex = new RegExp('^([0-9]{2}\\s{1}){' + (l - 1) + '}[0-9]{2}$');
			$.each(datasel, function(i, n) {
				if (regex.test(n) && fun(n, l)) {
					newsel.push(n);
				}
			});
			return newsel;
		}

		/**
		 * 输入框类型检测ssc
		 */
		var _inputCheck_Num = function(datasel, l, fun, sort, position) {
			fun = $.isFunction(fun) ? fun : function(n, l) {
				return true;
			}
			position = position || false;
			var newsel = []; // 新的号码
			var dump = []; //被过滤的号码
			if (sort) { // 如果需要号码排序
				var sortsel = [];
				if (position) {
					datasel = datasel.slice(1, datasel.length);
				}
				$.each(datasel, function(i, n) {
					sortsel.push(n.split('').sort().toString().replace(/\,/g, ''));
				});
				datasel = sortsel;
			}
			datasel = unique(datasel); // 去除重复
			var regex = new RegExp('^[0-9]{' + l + '}$');
			$.each(datasel, function(i, n) {
				if (regex.test(n) && fun(n, l)) {
					newsel.push(n);
				} else {
					dump.push(n);
				}
			});
			return newsel;
		}

		/**
		 * 输入框类型检测---k3
		 */
		var _inputCheck_Num_k3 = function(datasel, l, fun) {
			fun = $.isFunction(fun) ? fun : function(n, l) {
				return true;
			}
			var newsel = []; // 新的号码
			datasel = unique(datasel); // 去除重复
			var regex = new RegExp('^[1-6]{' + l + '}$');
			$.each(datasel, function(i, n) {
				if (regex.test(n) && fun(n, l)) {
					newsel.push(n);
				}
			});
			return newsel;
		}

		/**
		 * 2排不重复检测
		 */
		var _uniqueCheck = function(a, b) {
			return intersect(a, b).length == 0 ? true : false;
		}

		/**
		 * 二同号单式
		 */
		var _ethdsCheck = function(n, l) {
			if (l != 3) return false;
			var first = n.substring(0, 1);
			var second = n.substring(1, 2);
			var third = n.substring(2, 3);
			if (first == second && second == third) return false;
			if (first == second || second == third || third == first) return true;
			return false;
		}

		/**
		 * 二不同号单式
		 */
		var _ebthdsCheck = function(n, l) {
			if (l != 2) return false;
			var first = n.substring(0, 1);
			var second = n.substring(1, 2);
			if (first == second) return false;
			return true;
		}

		/**
		 * 三不同号单式
		 */
		var _sbthdsCheck = function(n, l) {
			if (l != 3) return false;
			var first = n.substring(0, 1);
			var second = n.substring(1, 2);
			var third = n.substring(2, 3);
			if (first != second && second != third && third != first) return true;
			return false;
		}


		/**
		 * 和值检测
		 */
		var _HHZXCheck_Num = function(n, l) {
			var a = new Array();
			if (l == 2) { //两位
				a = ['00', '11', '22', '33', '44', '55', '66', '77', '88', '99'];
			} else { //三位[默认]
				a = ['000', '111', '222', '333', '444', '555', '666', '777', '888', '999'];
			}
			return $.inArray(n, a) == -1 ? true : false;
		}

		/**
		 * 2排不重复检测
		 */
		var _uniqueCheck = function(a, b) {
			return intersect(a, b).length == 0 ? true : false;
		}

		/**
		 * 二同号单式
		 */
		var _ethdsCheck = function(n, l) {
			if (l != 3) return false;
			var first = n.substring(0, 1);
			var second = n.substring(1, 2);
			var third = n.substring(2, 3);
			if (first == second && second == third) return false;
			if (first == second || second == third || third == first) return true;
			return false;
		}

		/**
		 * 二不同号单式
		 */
		var _ebthdsCheck = function(n, l) {
			if (l != 2) return false;
			var first = n.substring(0, 1);
			var second = n.substring(1, 2);
			if (first == second) return false;
			return true;
		}

		/**
		 * 三不同号单式
		 */
		var _sbthdsCheck = function(n, l) {
			if (l != 3) return false;
			var first = n.substring(0, 1);
			var second = n.substring(1, 2);
			var third = n.substring(2, 3);
			if (first != second && second != third && third != first) return true;
			return false;
		}

		/**
		 * 多少注计算
		 */
		var _inputNumbers = function(type, datasel, cptype) {
			var nums = 0,
				tmp_nums = 1;
			if (cptype === 'ssc') {
				switch (type) {
					case 'rx3z3':
						var maxplace = 1;
						if (datasel.length > 1) {
							var place = 0;
							for (var i = 0; i < datasel[0].length; i++) {
								if (datasel[0][i] == '√') place++;
							}
							var newsel = datasel[1];
							var m = 3;
							// 任选3必须大于选了3位以上才能组成组合
							if (place >= m) {
								var h = ComNum(place, m);
								if (h > 0) { // 组合数必须大于0
									for (var i = 0; i < maxplace; i++) {
										var s = newsel.length;
										// 组三必须选两位或者以上
										if (s > 1) {
											nums += s * (s - 1);
										}
									}
									nums *= h;
								}
							}
						}
						break;
					case 'rx3z6':
						var maxplace = 1;
						if (datasel.length > 1) {
							var place = 0;
							for (var i = 0; i < datasel[0].length; i++) {
								if (datasel[0][i] == '√') place++;
							}
							var newsel = datasel[1];
							var m = 3;
							// 任选3必须大于选了3位以上才能组成组合
							if (place >= m) {
								var h = ComNum(place, m);
								if (h > 0) { // 组合数必须大于0
									for (var i = 0; i < maxplace; i++) {
										var s = newsel.length;
										// 组六必须选三位或者以上
										if (s > 2) {
											nums += s * (s - 1) * (s - 2) / 6;
										}
									}
									nums *= h;
								}
							}
						}
						break;
					case 'rx2zx':
						var maxplace = 1;
						if (datasel.length > 1) {
							var place = 0;
							for (var i = 0; i < datasel[0].length; i++) {
								if (datasel[0][i] == '√') place++;
							}
							var newsel = datasel[1];
							var m = 2;
							// 任选2必须大于选了2位以上才能组成组合
							if (place >= m) {
								var h = ComNum(place, m);
								if (h > 0) { // 组合数必须大于0
									for (var i = 0; i < maxplace; i++) {
										var s = newsel.length;
										// 二码不定位必须选两位或者以上
										if (s > 1) {
											nums += s * (s - 1) / 2;
										}
									}
									nums *= h;
								}
							}
						}
						break;
					case 'rx2ds':
					case 'rx3ds':
					case 'rx4ds':
						if (datasel.length > 1) {
							var place = 0;
							for (var i = 0; i < datasel[0].length; i++) {
								if (datasel[0][i] == '√') place++;
							}
							var newsel = [];
							for (var i = 1; i < datasel.length; i++) {
								newsel.push(datasel[i]);
							}
							var m = 0;
							if (type == 'rx2ds') {
								m = 2;
							}
							if (type == 'rx3ds') {
								m = 3;
							}
							if (type == 'rx4ds') {
								m = 4;
							}
							// 任选2必须大于选了2位以上才能组成组合
							if (place >= m) {
								var h = ComNum(place, m);
								if (h > 0) { // 组合数必须大于0
									nums += _inputCheck_Num(newsel, m).length;
									nums *= h;
								}
							}
						}
						break;
					case 'rx3hhzx':
						if (datasel.length > 1) {
							var place = 0;
							for (var i = 0; i < datasel[0].length; i++) {
								if (datasel[0][i] == '√') place++;
							}
							var newsel = [];
							for (var i = 1; i < datasel.length; i++) {
								newsel.push(datasel[i]);
							}
							var m = 3; // 必须选择3个以上位置才可以
							if (place >= m) {
								var h = ComNum(place, m);
								if (h > 0) { // 组合数必须大于0
									nums += _inputCheck_Num(newsel, 3, _HHZXCheck_Num, true).length;
									nums *= h;
								}
							}
						}
						break;
					case 'wxzhixds':
						nums = _inputCheck_Num(datasel, 5).length;
						break;
					case 'sixzhixdsh':
					case 'sixzhixdsq':
						nums = _inputCheck_Num(datasel, 4).length;
						break;
					case 'sxzhixdsh':
					case 'sxzhixdsz':
					case 'sxzhixdsq':
						nums = _inputCheck_Num(datasel, 3).length;
						break;
					case 'sxhhzxh':
					case 'sxhhzxz':
					case 'sxhhzxq':
						nums = _inputCheck_Num(datasel, 3, _HHZXCheck_Num, true).length;
						break;
					case 'exzhixdsh':
					case 'exzhixdsq':
						nums = _inputCheck_Num(datasel, 2).length;
						break;
					case 'exzuxdsh':
					case 'exzuxdsq':
						nums = _inputCheck_Num(datasel, 2, _HHZXCheck_Num, true).length;
						break;
					case 'wxzux120':
						var s = datasel[0].length;
						if (s > 4) {
							nums += ComNum(s, 5);
						}
						break;
					case 'wxzux60':
					case 'wxzux30':
					case 'wxzux20':
					case 'wxzux10':
					case 'wxzux5':
						var minchosen = new Array();
						if (type == 'wxzux60') {
							minchosen = [1, 3];
						}
						if (type == 'wxzux30') {
							minchosen = [2, 1];
						}
						if (type == 'wxzux20') {
							minchosen = [1, 2];
						}
						if (type == 'wxzux10' || type == 'wxzux5') {
							minchosen = [1, 1];
						}
						if (datasel[0].length >= minchosen[0] && datasel[1].length >= minchosen[1]) {
							var h = intersect(datasel[0], datasel[1]).length;
							tmp_nums = ComNum(datasel[0].length, minchosen[0]) * ComNum(datasel[1].length, minchosen[1]);
							if (h > 0) {
								if (type == 'wxzux60') {
									tmp_nums -= ComNum(h, 1) * ComNum(datasel[1].length - 1, 2);
								}
								if (type == 'wxzux30') {
									tmp_nums -= ComNum(h, 2) * ComNum(2, 1);
									if (datasel[0].length - h > 0) {
										tmp_nums -= ComNum(h, 1) * ComNum(datasel[0].length - h, 1);
									}
								}
								if (type == 'wxzux20') {
									tmp_nums -= ComNum(h, 1) * ComNum(datasel[1].length - 1, 1);
								}
								if (type == 'wxzux10' || type == 'wxzux5') {
									tmp_nums -= ComNum(h, 1);
								}
							}
							nums += tmp_nums;
						}
						break;
					case 'sixzux24h':
					case 'sixzux24q':
						var s = datasel[0].length;
						if (s > 3) {
							nums += ComNum(s, 4);
						}
						break;
					case 'sixzux6h':
					case 'sixzux6q':
						var minchosen = [2];
						if (datasel[0].length >= minchosen[0]) {
							nums += ComNum(datasel[0].length, minchosen[0]);
						}
						break;
					case 'sixzux12h':
					case 'sixzux12q':
					case 'sixzux4h':
					case 'sixzux4q':
						var minchosen = new Array();
						if (type == 'sixzux12h' || type == 'sixzux12q') {
							minchosen = [1, 2];
						}
						if (type == 'sixzux4h' || type == 'sixzux4q') {
							minchosen = [1, 1];
						}
						if (datasel[0].length >= minchosen[0] && datasel[1].length >= minchosen[1]) {
							var h = intersect(datasel[0], datasel[1]).length;
							tmp_nums = ComNum(datasel[0].length, minchosen[0]) * ComNum(datasel[1].length, minchosen[1]);
							if (h > 0) {
								if (type == 'sixzux12h' || type == 'sixzux12q') {
									tmp_nums -= ComNum(h, 1) * ComNum(datasel[1].length - 1, 1);
								}
								if (type == 'sixzux4h' || type == 'sixzux4q') {
									tmp_nums -= ComNum(h, 1);
								}
							}
							nums += tmp_nums;
						}
						break;
					case 'sxzuxzsh':
					case 'sxzuxzsz':
					case 'sxzuxzsq':
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							// 组三必须选两位或者以上
							if (s > 1) {
								nums += s * (s - 1);
							}
						}
						break;
					case 'sxzuxzlh':
					case 'sxzuxzlz':
					case 'sxzuxzlq':
					case 'bdwwx3m':
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							// 组六或不定胆五星三码必须选三位或者以上
							if (s > 2) {
								nums += s * (s - 1) * (s - 2) / 6;
							}
						}
						break;
					case 'wxzhixzh':
					case 'sixzhixzhh':
					case 'sixzhixzhq':
						var maxplace = 0;
						if ('wxzhixzh' == type) {
							maxplace = 5;
						}
						if ('sixzhixzhh' == type || 'sixzhixzhq' == type) {
							maxplace = 4;
						}
						for (var i = 0; i < maxplace; i++) {
							// 有位置上没有选择
							if (datasel[i].length == 0) {
								tmp_nums = 0;
								break;
							}
							tmp_nums *= datasel[i].length;
						}
						nums += tmp_nums * maxplace;
						break;
					case 'sxzhixhzh':
					case 'sxzhixhzz':
					case 'sxzhixhzq':
					case 'exzhixhzh':
					case 'exzhixhzq':
						var cc = {
							0: 1,
							1: 3,
							2: 6,
							3: 10,
							4: 15,
							5: 21,
							6: 28,
							7: 36,
							8: 45,
							9: 55,
							10: 63,
							11: 69,
							12: 73,
							13: 75,
							14: 75,
							15: 73,
							16: 69,
							17: 63,
							18: 55,
							19: 45,
							20: 36,
							21: 28,
							22: 21,
							23: 15,
							24: 10,
							25: 6,
							26: 3,
							27: 1
						};
						if (type == 'exzhixhzh' || type == 'exzhixhzq') {
							cc = {
								0: 1,
								1: 2,
								2: 3,
								3: 4,
								4: 5,
								5: 6,
								6: 7,
								7: 8,
								8: 9,
								9: 10,
								10: 9,
								11: 8,
								12: 7,
								13: 6,
								14: 5,
								15: 4,
								16: 3,
								17: 2,
								18: 1
							};
						}
						for (var i = 0; i < datasel[0].length; i++) {
							nums += cc[parseInt(datasel[0][i], 10)];
						}
						break;
					case 'sxzuxhzh':
	                case 'sxzuxhzz':
	                case 'sxzuxhzq':
	                case 'exzuxhzh':
	                case 'exzuxhzq':
	                	var cc = { 1: 1, 2: 2, 3: 2, 4: 4, 5: 5, 6: 6, 7: 8, 8: 10, 9: 11, 10: 13, 11: 14, 12: 14, 13: 15, 14: 15, 15: 14, 16: 14, 17: 13, 18: 11, 19: 10, 20: 8, 21: 6, 22: 5, 23: 4, 24: 2, 25: 2, 26: 1};
	                    if (type == 'exzuxhzq' || type == 'exzuxhzh') {
	                        cc = { 1: 1, 2: 1, 3: 2, 4: 2, 5: 3, 6: 3, 7: 4, 8: 4, 9: 5, 10: 4, 11: 4, 12: 3, 13: 3, 14: 2, 15: 2, 16: 1, 17: 1};
	                    }
						for (var i = 0; i < datasel[0].length; i++) {
							nums += cc[parseInt(datasel[0][i], 10)];
						}
						break;
					case 'rx2fs':
					case 'rx3fs':
					case 'rx4fs':
						var minplace = 0;
						if (type == 'rx2fs') {
							minplace = 2;
						}
						if (type == 'rx3fs') {
							minplace = 3;
						}
						if (type == 'rx4fs') {
							minplace = 4;
						}
						var newsel = [];
						for (var i = 0; i < datasel.length; i++) {
							if (datasel[i].length != 0) {
								newsel.push(datasel[i]);
							}
						}
						// 最少位数
						if (newsel.length >= minplace) {
							var l = ComNum(newsel.length, minplace);
							for (var i = 0; i < l; i++) {
								tmp_nums = 1;
								var data = ComVal(newsel, minplace, i);
								for (var j = 0; j < data.length; j++) {
									tmp_nums *= data[j].length;
								}
								nums += tmp_nums;
							}
						}
						break;
					case 'dw': //定位胆所有在一起特殊处理
						var maxplace = 5;
						for (var i = 0; i < maxplace; i++) {
							nums += datasel[i].length;
						}
						break;
					case 'bdw2mh':
					case 'bdw2mz':
					case 'bdw2mq':
					case 'bdwsix2mq':
					case 'bdwsix2mh':
					case 'bdwwx2m':
					case 'exzuxfsh':
					case 'exzuxfsq':
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							// 二码不定位必须选两位或者以上
							if (s > 1) {
								nums += s * (s - 1) / 2;
							}
						}
						break;
					default:
						var maxplace = 0;
						switch (type) {
							case 'wxzhixfs':
								maxplace = 5;
								break;
							case 'sixzhixfsh':
							case 'sixzhixfsq':
								maxplace = 4;
								break;
							case 'sxzhixfsh':
							case 'sxzhixfsz':
							case 'sxzhixfsq':
								maxplace = 3;
								break;
							case 'exzhixfsh':
							case 'exzhixfsq':
							case 'dxdsh':
							case 'dxdsq':
								maxplace = 2;
								break;
							case 'bdw1mh':
							case 'bdw1mz':
							case 'bdw1mq':
							case 'bdwsix1mq':
							case 'bdwsix1mh':
							case 'qwyffs':
							case 'qwhscs':
							case 'qwsxbx':
							case 'qwsjfc':
							case 'longhuhewq':
							case 'longhuhewb':
							case 'longhuhews':
							case 'longhuhewg':
							case 'longhuheqb':
							case 'longhuheqs':
							case 'longhuheqg':
							case 'longhuhebs':
							case 'longhuhebg':
							case 'longhuhesg':
							case 'wxdxds':
							case 'sscniuniu':
								maxplace = 1;
								break;
						}
						if (datasel.length == maxplace) {
							for (var i = 0; i < maxplace; i++) {
								// 有位置上没有选择
								if (datasel[i].length == 0) {
									tmp_nums = 0;
									break;
								}
								tmp_nums *= datasel[i].length;
							}
							nums += tmp_nums;
						}
				}
			}
			if (cptype === '11x5') {
				switch (type) {
					// 这里验证输入框类型
					case 'sanmzhixdsq':
					case 'sanmzuxdsq':
						return _inputCheck_Num_11x5(datasel, 3, _numberCheck_Num).length;
					case 'ermzhixdsq':
					case 'ermzuxdsq':
						return _inputCheck_Num_11x5(datasel, 2, _numberCheck_Num).length;
					case 'rx1ds':
						return _inputCheck_Num_11x5(datasel, 1, _numberCheck_Num).length;
					case 'rx2ds':
						return _inputCheck_Num_11x5(datasel, 2, _numberCheck_Num).length;
					case 'rx3ds':
						return _inputCheck_Num_11x5(datasel, 3, _numberCheck_Num).length;
					case 'rx4ds':
						return _inputCheck_Num_11x5(datasel, 4, _numberCheck_Num).length;
					case 'rx5ds':
						return _inputCheck_Num_11x5(datasel, 5, _numberCheck_Num).length;
					case 'rx6ds':
						return _inputCheck_Num_11x5(datasel, 6, _numberCheck_Num).length;
					case 'rx7ds':
						return _inputCheck_Num_11x5(datasel, 7, _numberCheck_Num).length;
					case 'rx8ds':
						return _inputCheck_Num_11x5(datasel, 8, _numberCheck_Num).length;
						// 这里验证选号类型
					case 'sanmzhixfsq':
						if (datasel[0].length > 0 && datasel[1].length > 0 && datasel[2].length > 0) {
							for (var i = 0; i < datasel[0].length; i++) {
								for (var j = 0; j < datasel[1].length; j++) {
									for (var k = 0; k < datasel[2].length; k++) {
										if (datasel[0][i] != datasel[1][j] && datasel[0][i] != datasel[2][k] && datasel[1][j] != datasel[2][k]) {
											nums++;
										}
									}
								}
							}
						}
						break;
					case 'sanmzuxfsq':
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							if (s > 2) {
								nums += s * (s - 1) * (s - 2) / 6;
							}
						}
						break;
					case 'ermzhixfsq':
						if (datasel[0].length > 0 && datasel[1].length > 0) {
							for (var i = 0; i < datasel[0].length; i++) {
								for (var j = 0; j < datasel[1].length; j++) {
									if (datasel[0][i] != datasel[1][j]) {
										nums++;
									}
								}
							}
						}
						break;
					case 'ermzuxfsq':
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							if (s > 1) {
								nums += s * (s - 1) / 2;
							}
						}
						break;
					case 'bdw':
					case 'dwd':
					case 'dds':
					case 'czw':
					case 'rx1fs': // 任选1中1
						var maxplace = 0;
						if ('bdw' == type || 'dds' == type || 'czw' == type || 'rx1fs' == type) {
							maxplace = 1;
						}
						if ('dwd' == type) {
							maxplace = 3;
						}
						for (var i = 0; i < maxplace; i++) {
							nums += datasel[i].length;
						}
						break;
					case 'rx2fs': // 任选2中2
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							if (s > 1) {
								nums += s * (s - 1) / 2;
							}
						}
						break;
					case 'rx3fs': // 任选3中3
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							if (s > 2) {
								nums += s * (s - 1) * (s - 2) / 6;
							}
						}
						break;
					case 'rx4fs': // 任选4中4
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							if (s > 3) {
								nums += s * (s - 1) * (s - 2) * (s - 3) / 24;
							}
						}
						break;
					case 'rx5fs': // 任选5中5
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							if (s > 4) {
								nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) / 120;
							}
						}
						break;
					case 'rx6fs': // 任选6中6
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							if (s > 5) {
								nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) * (s - 5) / 720;
							}
						}
						break;
					case 'rx7fs': // 任选7中7
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							if (s > 6) {
								nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) * (s - 5) * (s - 6) / 5040;
							}
						}
						break;
					case 'rx8fs': // 任选8中8
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							if (s > 7) {
								nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) * (s - 5) * (s - 6) * (s - 7) / 40320;
							}
						}
						break;
					case 'rxtd2': //任选拖胆
	                case 'rxtd3':
	                case 'rxtd4':
	                case 'rxtd5':
	                case 'rxtd6':
	                case 'rxtd7':
	                case 'rxtd8':
	                	 var maxplace = 1;
	                	 var isNul = false;
	                    for (i = 0; i <= maxplace; i++) {
	                    	if (datasel[i].length == 0) { //有位置上没有选择
	                    		isNul = true;
	                            break;
	                        }
	                    }
	                    if(isNul){
	                    	break;
	                    }
	                    var betArr = uniquelize(datasel[0].concat(datasel[1]));
	                    var gcode = Number(type.substring(4));
	                    if (betArr.length >= gcode) {
	                    	nums = ComNum(datasel[1].length, gcode - datasel[0].length);
	                    } else {
	                    	nums = 0;
	                    }
	                    break;
					default:
						break;
				}
			}
			if (cptype === 'k3') {
				switch (type) {
					case 'ebthds':
						return _inputCheck_Num_k3(datasel, 2, _ebthdsCheck).length;
					case 'ethds':
						return _inputCheck_Num_k3(datasel, 3, _ethdsCheck).length;
					case 'sbthds':
						return _inputCheck_Num_k3(datasel, 3, _sbthdsCheck).length;
						// 选号
					case 'ebthdx': // 二不同号，标准选号
						if (datasel[0].length >= 2) {
							nums += ComNum(datasel[0].length, 2);
						}
						break;
					case 'ebthdt':
						var maxplace = 2;
						if (datasel.length == maxplace) {
							if (_uniqueCheck(datasel[0], datasel[1])) {
								for (var i = 0; i < maxplace; i++) {
									var s = datasel[i].length;
									if (s > 0) {
										if (i > 0) {
											nums = datasel[i].length;
										}
									} else {
										nums = 0;
										break;
									}
								}
							}
						}
						break;
					case 'ethdx':
						var s = datasel.length;
						if (s > 1) {
							var a = datasel[0].length;
							var b = datasel[1].length;
							if (a > 0 && b > 0) {
								if (_uniqueCheck(datasel[0], datasel[1])) {
									nums = a * b;
								}
							}
						}
						break;
					case 'ethfx':
						nums = datasel[0].length;
						break;
					case 'sbthdx': // 三不同号单选
						if (datasel[0].length >= 3) {
							nums += ComNum(datasel[0].length, 3);
						}
						break;
					case 'sthdx': // 三同号单选
					case 'hezhi': // 和值
					case 'hzdxds':
						nums = datasel[0].length;
						break;
					case 'sthtx': // 三同号通选
					case 'slhtx': // 三连号通选
						nums = datasel[0].length > 0 ? 1 : 0;
						break;
					default:
						break;
				}
			}
			if (cptype === '3D') {
				switch (type) {
					case 'sanxzhixds':
						nums = _inputCheck_Num(datasel, 3).length;
						break;
					case 'sanxhhzx':
						nums = _inputCheck_Num(datasel, 3, _HHZXCheck_Num, true).length;
						break;
					case 'exzhixdsh':
					case 'exzhixdsq':
						nums = _inputCheck_Num(datasel, 2).length;
						break;
					case 'exzuxdsh':
					case 'exzuxdsq':
						nums = _inputCheck_Num(datasel, 2, _HHZXCheck_Num, true).length;
						break;
					case 'sanxzs':
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							// 组三必须选两位或者以上
							if (s > 1) {
								nums += s * (s - 1);
							}
						}
						break;
					case 'sanxzl':
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							// 组六必须选三位或者以上
							if (s > 2) {
								nums += s * (s - 1) * (s - 2) / 6;
							}
						}
						break;
					case 'sanxzhixhz':
					case 'exzhixhzh':
					case 'exzhixhzq':
						var cc = {
							0: 1,
							1: 3,
							2: 6,
							3: 10,
							4: 15,
							5: 21,
							6: 28,
							7: 36,
							8: 45,
							9: 55,
							10: 63,
							11: 69,
							12: 73,
							13: 75,
							14: 75,
							15: 73,
							16: 69,
							17: 63,
							18: 55,
							19: 45,
							20: 36,
							21: 28,
							22: 21,
							23: 15,
							24: 10,
							25: 6,
							26: 3,
							27: 1
						};
						if (type == 'exzhixhzh' || type == 'exzhixhzq') {
							cc = {
								0: 1,
								1: 2,
								2: 3,
								3: 4,
								4: 5,
								5: 6,
								6: 7,
								7: 8,
								8: 9,
								9: 10,
								10: 9,
								11: 8,
								12: 7,
								13: 6,
								14: 5,
								15: 4,
								16: 3,
								17: 2,
								18: 1
							};
						}
						for (var i = 0; i < datasel[0].length; i++) {
							nums += cc[parseInt(datasel[0][i], 10)];
						}
						break;
					case 'dwd': //定位胆所有在一起特殊处理
						var maxplace = 3;
						for (var i = 0; i < maxplace; i++) {
							nums += datasel[i].length;
						}
						break;
					case 'exzuxfsh':
					case 'exzuxfsq':
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							var s = datasel[i].length;
							// 二码不定位必须选两位或者以上
							if (s > 1) {
								nums += s * (s - 1) / 2;
							}
						}
						break;
					default:
						var maxplace = 0;
						switch (type) {
							case "sanxzhixfs":
								maxplace = 3;
								break;
							case "exzhixfsh":
							case "exzhixfsq":
								maxplace = 2;
								break;
							case "yimabdw":
								maxplace = 1;
								break;
						}
						for (var i = 0; i < maxplace; i++) {
							// 有位置上没有选择
							if (datasel[i].length == 0) {
								tmp_nums = 0;
								break;
							}
							tmp_nums *= datasel[i].length;
						}
						nums += tmp_nums;
				}
			}
			if (cptype === 'pk10') {
				switch (type) {
					case 'qianerzxds':
						return _inputCheck_Num_11x5(datasel, 2, _numberCheck_Num).length;
					case 'qiansanzxds':
						return _inputCheck_Num_11x5(datasel, 3, _numberCheck_Num).length;
					case 'qiansizxds':
						return _inputCheck_Num_11x5(datasel, 4, _numberCheck_Num).length;
					case 'qianwuzxds':
						return _inputCheck_Num_11x5(datasel, 5, _numberCheck_Num).length;
						// 选号
					case 'qiansanzxfs':
						if (datasel[0].length > 0 && datasel[1].length > 0 && datasel[2].length > 0) {
							for (var i = 0; i < datasel[0].length; i++) {
								for (var j = 0; j < datasel[1].length; j++) {
									for (var k = 0; k < datasel[2].length; k++) {
										if (datasel[0][i] != datasel[1][j] && datasel[0][i] != datasel[2][k] && datasel[1][j] != datasel[2][k]) {
											nums++;
										}
									}
								}
							}
						}
						break;
					case 'qianerzxfs':
						if (datasel[0].length > 0 && datasel[1].length > 0) {
							for (var i = 0; i < datasel[0].length; i++) {
								for (var j = 0; j < datasel[1].length; j++) {
									if (datasel[0][i] != datasel[1][j]) {
										nums++;
									}
								}
							}
						}
						break;
					case 'qiansizxfs':
						if (datasel[0].length > 0 && datasel[1].length > 0 && datasel[2].length > 0 && datasel[3].length > 0) {
							for (var i = 0; i < datasel[0].length; i++) {
								for (var j = 0; j < datasel[1].length; j++) {
									for (var k = 0; k < datasel[2].length; k++) {
										for (var l = 0; l < datasel[3].length; l++) {
											var a = datasel[0][i];
											var b = datasel[1][j];
											var c = datasel[2][k];
											var d = datasel[3][l];
											if (a != b && a != c && a != d && b != c && b != d && c != d) {
												nums++;
											}
										}
									}
								}
							}
						}
						break;
					case 'qianwuzxfs':
						if (datasel[0].length > 0 && datasel[1].length > 0 && datasel[2].length > 0 && datasel[3].length > 0 && datasel[4].length > 0) {
							for (var i = 0; i < datasel[0].length; i++) {
								for (var j = 0; j < datasel[1].length; j++) {
									for (var k = 0; k < datasel[2].length; k++) {
										for (var l = 0; l < datasel[3].length; l++) {
											for (var m = 0; m < datasel[4].length; m++) {
												var a = datasel[0][i];
												var b = datasel[1][j];
												var c = datasel[2][k];
												var d = datasel[3][l];
												var e = datasel[4][m];
												if (a != b && a != c && a != d && a != e && b != c && b != d && b != e && c != d && c != e && d != e) {
													nums++;
												}
											}
										}
									}
								}
							}
						}
						break;
					case 'qwdingweidan':
					case 'hwdingweidan':
					case 'dw1dxds':
					case 'dw2dxds':
					case 'dw3dxds':
					case 'dw4dxds':
					case 'dw5dxds':
					case 'pk10_dxdsgyhz':
					case '01vs10':
					case '02vs09':
					case '03vs08':
					case '04vs07':
					case '05vs06':
					case 'pk10_hzgyhz':
					case 'pk10_hzqshz':
						var maxplace = 1;
						switch (type) {
							case 'qwdingweidan':
							case 'hwdingweidan':
								maxplace = 5;
								break;
							case 'dw1dxds':
							case 'dw2dxds':
							case 'dw3dxds':
							case 'dw4dxds':
							case 'dw5dxds':
							case 'pk10_dxdsgyhz':
							case '01vs10':
							case '02vs09':
							case '03vs08':
							case '04vs07':
							case '05vs06':
							case 'pk10_hzgyhz':
							case 'pk10_hzqshz':
								maxplace = 1;
								break;
							default:
								maxplace = 1;
								break;
						}
						for (var i = 0; i < maxplace; i++) {
							nums += datasel[i].length;
						}
						break;
					default:
						var maxplace = 0;
						if ('qianyi' == type) maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							// 有位置上没有选择
							if (datasel[i].length == 0) {
								tmp_nums = 0;
								break;
							}
							tmp_nums *= datasel[i].length;
						}
						nums += tmp_nums;
				}
			}
			if (cptype === 'k8') {
				switch (type) {
					case 'rx1':
						nums = datasel[0].length + (datasel[1]).length;
						break;
					case 'rx2':
						var l = datasel[0].length + (datasel[1]).length;
						if (l >= 2 && l <= 8) {
							nums = ComNum(l, 2);
						}
						break;
					case 'rx3':
						var l = datasel[0].length + (datasel[1]).length;
						if (l >= 3 && l <= 8) {
							nums = ComNum(l, 3);
						}
						break;
					case 'rx4':
						var l = datasel[0].length + (datasel[1]).length;
						if (l >= 4 && l <= 8) {
							nums = ComNum(l, 4);
						}
						break;
					case 'rx5':
						var l = datasel[0].length + (datasel[1]).length;
						if (l >= 5 && l <= 8) {
							nums = ComNum(l, 5);
						}
						break;
					case 'rx6':
						var l = datasel[0].length + (datasel[1]).length;
						if (l >= 6 && l <= 8) {
							nums = ComNum(l, 6);
						}
						break;
					case 'rx7':
						var l = datasel[0].length + (datasel[1]).length;
						if (l >= 7 && l <= 8) {
							nums = ComNum(l, 7);
						}
						break;
					default:
						var maxplace = 1;
						for (var i = 0; i < maxplace; i++) {
							// 有位置上没有选择
							if (datasel[i].length == 0) {
								tmp_nums = 0;
								break;
							}
							tmp_nums *= datasel[i].length;
						}
						nums += tmp_nums;
				}
			}
			return nums;
		}

		//11x5
		var _formatSelect_Num_11x5 = function(datasel, m, n) {
			var newsel = new Array();
			if (!m) m = 0;
			if (!n) n = 0;
			for (var i = 0; i < m; i++) {
				newsel.push('-');
			}
			for (var i = 0; i < datasel.length; i++) {
				var f = datasel[i].toString().replace(/\,|√|-/g, ' ');
				if (f == '') {
					newsel.push('-');
				} else {
					newsel.push(f);
				}
			}
			for (var i = 0; i < n; i++) {
				newsel.push('-');
			}
			return newsel.toString();
		}

		//ssc
		var _formatSelect_Num = function(datasel, m, n) {
			var newsel = new Array();
			if (!m) m = 0;
			if (!n) n = 0;
			for (var i = 0; i < m; i++) {
				newsel.push('-');
			}
			for (var i = 0; i < datasel.length; i++) {
				var f = datasel[i].toString().replace(/\,|√|-/g, '');
				if (f == '') {
					newsel.push('-');
				} else {
					newsel.push(f);
				}
			}
			for (var i = 0; i < n; i++) {
				newsel.push('-');
			}
			return newsel.toString();
		}

		//单式计算
		var _formatTextarea_Num = function(type, datasel, cptype) {
			if (cptype === 'ssc') {
				switch (type) {
					case 'wxzhixds':
						datasel = _inputCheck_Num(datasel, 5);
						break;
					case 'sixzhixdsh':
					case 'sixzhixdsq':
						datasel = _inputCheck_Num(datasel, 4);
						break;
					case 'sxzhixdsh':
					case 'sxzhixdsz':
					case 'sxzhixdsq':
						datasel = _inputCheck_Num(datasel, 3);
						break;
					case 'sxhhzxh':
					case 'sxhhzxz':
					case 'sxhhzxq':
						datasel = _inputCheck_Num(datasel, 3, _HHZXCheck_Num, true);
						break;
					case 'exzhixdsh':
					case 'exzhixdsq':
						datasel = _inputCheck_Num(datasel, 2);
						break;
					case 'exzuxdsh':
					case 'exzuxdsq':
						datasel = _inputCheck_Num(datasel, 2, _HHZXCheck_Num, true);
						break;
					case 'rx2ds':
					case 'rx3ds':
					case 'rx4ds':
						if (datasel.length > 1) {
							var place = 0;
							for (var i = 0; i < datasel[0].length; i++) {
								if (datasel[0][i] == '√') place++;
							}
							var newsel = [];
							for (var i = 1; i < datasel.length; i++) {
								newsel.push(datasel[i]);
							}
							var m = 0;
							if (type == 'rx2ds') {
								m = 2;
							}
							if (type == 'rx3ds') {

								m = 3;
							}
							if (type == 'rx4ds') {
								m = 4;
							}
							// 任选2必须大于选了2位以上才能组成组合
							if (place >= m) {
								var h = ComNum(place, m);
								if (h > 0) { // 组合数必须大于0
									// return '[' + datasel[0] + ']' + _inputCheck_Num(newsel, m);
									return _inputCheck_Num(newsel, m);
								}
							}
						}
						break;
					case 'rx3hhzx':
						if (datasel.length > 1) {
							var place = 0;
							for (var i = 0; i < datasel[0].length; i++) {
								if (datasel[0][i] == '√') place++;
							}
							var newsel = [];
							for (var i = 1; i < datasel.length; i++) {
								newsel.push(datasel[i]);
							}
							var m = 3;
							if (place >= m) {
								var h = ComNum(place, m);
								if (h > 0) { // 组合数必须大于0
									return _inputCheck_Num(newsel, 3, _HHZXCheck_Num, true);
								}
							}
						}
						break;
				}
				return datasel.toString().replace(/\,/g, ' ');
			}
			if (cptype === 'k3') {
				/*************k3*******************/
				switch (type) {
					case 'ebthds':
						datasel = _inputCheck_Num_k3(datasel, 2, _ebthdsCheck);
						break;
					case 'ethds':
						datasel = _inputCheck_Num_k3(datasel, 3, _ethdsCheck);
						break;
					case 'sbthds':
						datasel = _inputCheck_Num_k3(datasel, 3, _sbthdsCheck);
						break;
					default:
						break;
				}
				return datasel.toString().replace(/\,/g, ' ');
			}
			if (cptype === '11x5') {
				switch (type) {
					//11x5单式
					case 'sanmzhixdsq':
					case 'sanmzuxdsq':
						datasel = _inputCheck_Num_11x5(datasel, 3, _numberCheck_Num);
						break;
					case 'ermzhixdsq':
					case 'ermzuxdsq':
						datasel = _inputCheck_Num_11x5(datasel, 2, _numberCheck_Num);
						break;
					case 'rx1ds':
						datasel = _inputCheck_Num_11x5(datasel, 1, _numberCheck_Num);
						break;
					case 'rx2ds':
						datasel = _inputCheck_Num_11x5(datasel, 2, _numberCheck_Num);
						break;
					case 'rx3ds':
						datasel = _inputCheck_Num_11x5(datasel, 3, _numberCheck_Num);
						break;
					case 'rx4ds':
						datasel = _inputCheck_Num_11x5(datasel, 4, _numberCheck_Num);
						break;
					case 'rx5ds':
						datasel = _inputCheck_Num_11x5(datasel, 5, _numberCheck_Num);
						break;
					case 'rx6ds':
						datasel = _inputCheck_Num_11x5(datasel, 6, _numberCheck_Num);
						break;
					case 'rx7ds':
						datasel = _inputCheck_Num_11x5(datasel, 7, _numberCheck_Num);
						break;
					case 'rx8ds':
						datasel = _inputCheck_Num_11x5(datasel, 8, _numberCheck_Num);
						break;

				}
				return datasel.toString().replace(/\,/g, ';');
			}
			if (cptype === '3D') {
				switch (type) {
					case 'sanxzhixds':
						datasel = _inputCheck_Num(datasel, 3);
						break;
					case 'sanxhhzx':
						datasel = _inputCheck_Num(datasel, 3, _HHZXCheck_Num, true);
						break;
					case 'exzhixdsh':
					case 'exzhixdsq':
						datasel = _inputCheck_Num(datasel, 2);
						break;
					case 'exzuxdsh':
					case 'exzuxdsq':
						datasel = _inputCheck_Num(datasel, 2, _HHZXCheck_Num, true);
						break;
					default:
						break;
				}
				return datasel.toString().replace(/\,/g, ' ');
			}
			if (cptype === 'pk10') {
				switch (type) {
					case 'qianerzxds':
						datasel = _inputCheck_Num_11x5(datasel, 2, _numberCheck_Num);
						break;
					case 'qiansanzxds':
						datasel = _inputCheck_Num_11x5(datasel, 3, _numberCheck_Num);
						break;
					case 'qiansizxds':
						datasel = _inputCheck_Num_11x5(datasel, 4, _numberCheck_Num);
						break;
					case 'qianwuzxds':
						datasel = _inputCheck_Num_11x5(datasel, 5, _numberCheck_Num);
						break;
					default:
						break;
				}
				return datasel.toString().replace(/\,/g, ';');
			}
		}

		var _inputFormat = function(type, datasel, cptype) {
			//时时彩的计算
			if (cptype === 'ssc') {
				switch (type) {
					case 'wxzhixds':
					case 'sixzhixdsh':
					case 'sixzhixdsq':
					case 'sxzhixdsh':
					case 'sxzhixdsz':
					case 'sxzhixdsq':
					case 'sxhhzxh':
					case 'sxhhzxz':
					case 'sxhhzxq':
					case 'exzhixdsh':
					case 'exzhixdsq':
					case 'exzuxdsh':
					case 'exzuxdsq':
					case 'rx2ds':
					case 'rx3ds':
					case 'rx4ds':
					case 'rx3hhzx':
						return _formatTextarea_Num(type, datasel, cptype);
					case 'rx3z3':
					case 'rx3z6':
					case 'rx2zx':
						var space = datasel[0];
						return '[' + space + ']' + remove(datasel, 0).toString();
					case 'wxzux120':
					case 'sixzux24h':
					case 'sixzux24q':
					case 'sixzux6h':
					case 'sixzux6q':
					case 'sxzuxzsh':
					case 'sxzuxzsz':
					case 'sxzuxzsq':
					case 'sxzuxzlh':
					case 'sxzuxzlz':
					case 'sxzuxzlq':
					case 'exzuxfsh':
					case 'exzuxfsq':
					case 'bdw1mh':
					case 'bdw1mz':
					case 'bdw1mq':
					case 'bdw2mh':
					case 'bdw2mz':
					case 'bdw2mq':
					case "bdwsix1mq":
					case "bdwsix2mq":
					case "bdwsix1mh":
					case "bdwsix2mh":
					case "bdwwx2m":
					case "bdwwx3m":
					case 'qwyffs':
					case 'qwhscs':
					case 'qwsxbx':
					case 'qwsjfc':
					case 'longhuhewq':
					case 'longhuhewb':
					case 'longhuhews':
					case 'longhuhewg':
					case 'longhuheqb':
					case 'longhuheqs':
					case 'longhuheqg':
					case 'longhuhebs':
					case 'longhuhebg':
					case 'longhuhesg':
					case 'sxzhixhzh':
					case 'sxzhixhzz':
					case 'sxzhixhzq':
					case 'exzhixhzh':
					case 'exzhixhzq':
					case 'sxzuxhzh':
	                case 'sxzuxhzz':
	                case 'sxzuxhzq':
	                case 'exzuxhzh':
	                case 'exzuxhzq':
					case 'wxdxds':
					case 'sscniuniu':
						return datasel.toString();
					case 'sixzhixfsh':
					case 'sixzhixzhh':
						return _formatSelect_Num(datasel, 1);
					case 'sixzhixfsq':
					case 'sixzhixzhq':
						return _formatSelect_Num(datasel, 0, 1);
					case 'sxzhixfsh':
						return _formatSelect_Num(datasel, 2);
					case 'sxzhixfsz':
						return _formatSelect_Num(datasel, 1, 1);
					case 'sxzhixfsq':
						return _formatSelect_Num(datasel, 0, 2);
					case 'exzhixfsh':
						return _formatSelect_Num(datasel, 3);
					case 'exzhixfsq':
						return _formatSelect_Num(datasel, 0, 3);
					default:
						return _formatSelect_Num(datasel);
				}
			}
			if (cptype === 'k3') {
				switch (type) {
					case 'ebthds':
					case 'ethds':
					case 'sbthds':
						return _formatTextarea_Num(type, datasel, cptype);
					case 'ebthdx':
					case 'ethfx':
					case 'sbthdx':
					case 'sthdx':
					case 'sthtx':
					case 'slhtx':
					case 'hezhi':
					case 'hzdxds':
						return datasel[0].toString();
					case 'ebthdt':
					case 'ethdx':
						return _formatSelect_Num(datasel);
					default:
						break;
				}
			}
			if (cptype === '3D') {
				switch (type) {
					case 'sanxzhixds':
					case 'sanxhhzx':
					case 'exzhixdsh':
					case 'exzhixdsq':
					case 'exzuxdsh':
					case 'exzuxdsq':
						return _formatTextarea_Num(type, datasel, cptype);
					case 'sanxzs':
					case 'sanxzl':
					case 'exzuxfsh':
					case 'exzuxfsq':
					case 'yimabdw':
					case 'sanxzhixhz':
					case 'exzhixhzh':
					case 'exzhixhzq':
						return datasel.toString();
					case 'sanxzhixfs':
						return _formatSelect_Num(datasel);
					case 'exzhixfsh':
						return _formatSelect_Num(datasel, 1);
					case 'exzhixfsq':
						return _formatSelect_Num(datasel, 0, 1);
					default:
						return _formatSelect_Num(datasel);
				}
			}
			if (cptype === 'pk10') {
				switch (type) {
					case 'qianyi':
						return datasel[0].toString();
					case 'qianerzxfs':
					case 'qiansanzxfs':
					case 'qiansizxfs':
					case 'qianwuzxfs':
					case 'qwdingweidan':
					case 'hwdingweidan':
					case 'dw1dxds':
					case 'dw2dxds':
					case 'dw3dxds':
					case 'dw4dxds':
					case 'dw5dxds':
					case 'pk10_dxdsgyhz':
					case '01vs10':
					case '02vs09':
					case '03vs08':
					case '04vs07':
					case '05vs06':
						return _formatSelect_Num_11x5(datasel);
					case 'pk10_hzgyhz':
					case 'pk10_hzqshz':
						return datasel.toString();
					case 'qianerzxds':
					case 'qiansanzxds':
					case 'qiansizxds':
					case 'qianwuzxds':
						return _formatTextarea_Num(type, datasel, cptype);
					default:
						break;
				}
			}
			if (cptype === '11x5') {
				switch (type) {
					case 'sanmzhixfsq':
					case 'dwd':
						return _formatSelect_Num_11x5(datasel, 0, 2);
					case 'ermzhixfsq':
						return _formatSelect_Num_11x5(datasel, 0, 3);
					case 'sanmzuxfsq':
					case 'ermzuxfsq':
					case 'bdw':
					case 'rx1fs':
					case 'rx2fs':
					case 'rx3fs':
					case 'rx4fs':
					case 'rx5fs':
					case 'rx6fs':
					case 'rx7fs':
					case 'rx8fs':
						return datasel[0].toString();
					case 'rxtd1':
	                case 'rxtd2':
	                case 'rxtd3':
	                case 'rxtd4':
	                case 'rxtd5':
	                case 'rxtd6':
	                case 'rxtd7':
	                case 'rxtd8':
	                	return _formatSelect_Num_11x5(datasel);
					case 'sanmzhixdsq':
					case 'sanmzuxdsq':
					case 'ermzhixdsq':
					case 'ermzuxdsq':
					case 'rx1ds':
					case 'rx2ds':
					case 'rx3ds':
					case 'rx4ds':
					case 'rx5ds':
					case 'rx6ds':
					case 'rx7ds':
					case 'rx8ds':
						return _formatTextarea_Num(type, datasel, cptype);
					case 'dds':
						return datasel[0].toString().replace(/\,/g, '|');
					case 'czw':
						return datasel[0].toString();
					default:
						break;
				}
			}
			if (cptype === 'k8') {
				switch (type) {
					case 'rx1':
					case 'rx2':
					case 'rx3':
					case 'rx4':
					case 'rx5':
					case 'rx6':
					case 'rx7':
						return datasel[0].concat(datasel[1]).toString();
					case 'hezhids':
					case 'hezhidx':
					case 'jopan':
					case 'sxpan':
					case 'hzdxds':
					case 'hezhiwx':
						return datasel[0].toString().replace(/\,/g, '|');
					default:
						break;
				}
			}
		}

		//暂无作用
		var _typeFormat = function(code) {
			var a = [code[0], code[1], code[2]];
			var b = [code[2], code[3], code[4]];
			var _a = a.uniquelize();
			var _b = b.uniquelize();
			var arr = [];
			if (_a.length == 1) arr[0] = '豹子';
			if (_a.length == 2) arr[0] = '组三';
			if (_a.length == 3) arr[0] = '组六';
			if (_b.length == 1) arr[1] = '豹子';
			if (_b.length == 2) arr[1] = '组三';
			if (_b.length == 3) arr[1] = '组六';
			return arr;
		}

		var _toHex = function(byte_arr) {
			var hex_str = '', i, len, tmp_hex;
			len = byte_arr.length;
			for (i = 0; i < len; ++i) {
				if (byte_arr[i] < 0) {
					byte_arr[i] = byte_arr[i] + 256;
				}
				tmp_hex = byte_arr[i].toString(16);
				if (tmp_hex.length === 1) {
					tmp_hex = '0' + tmp_hex;
				}
				hex_str += tmp_hex;
			}
			return hex_str.trim();
		}

		var _dateDiff = function(strFrom, strTo){
			if (strFrom == null || strTo == null) return 0;
			var dateFrom = new Date(strFrom);
			var dateTo = new Date(strTo);
			var diff = dateTo.valueOf() - dateFrom.valueOf();
			var diff_day = parseInt(diff/(1000*60*60*24));
			return diff_day;
		}
		return {
			inputNumbers: _inputNumbers,
			inputFormat: _inputFormat,
			toHex: _toHex,
			dateDiff: _dateDiff
		}
	}

	//公共service
	var betUtilsService = function($rootScope, postDataService, $state, lotteryUtilsService, $ionicPopup, $timeout) {
		//cookie 操作
		var SetCookie = function(name, value, expire) {
			var exp = new Date();
			exp.setTime(exp.getTime() + expire);
			document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
		}

		//获取cookie
		var getCookie = function(name) {
			var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
			if (arr != null) return unescape(arr[2]);
			return null;
		}

		//删除cookie
		var delCookie = function(name) {
			var exp = new Date();
			exp.setTime(exp.getTime() - 1);
			var cval = getCookie(name);
			if (cval != null) document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
		}

		// 得到用户选择的位置，格式化后的数据
		var getSelectPlace = function(playArea) {
			var places = [],
				sp = $('.selposition', playArea);
			if (sp && sp.length > 0) {
				$.each(sp, function() {
					var place = [];
					var as = $(this).find('li');
					$.each(as, function() {
						if ($(this).find('div').hasClass('active')) {
							place.push('√');
						} else {
							place.push('-');
						}
					});
					places.push(place);

				});
			}
			return places;
		}

		// 得到用户选择的号码，格式化后的数据
		var getSelectBall = function(playArea) {
			var datasel = [],
				sb = $('.ssc-digital', playArea).find('ul.lottery-num-ul');
			if (sb && sb.length > 0) {
				$.each(sb, function() {
					var ball = [];
					var as = $(this).find('li.on');
					$.each(as, function() {
						var val = $(this).attr('data-val');
						ball.push(val);
					});
					datasel.push(ball);
				});
			}
			return datasel;
		}

		// 得到用户输入的号码，格式化后的数据
		var getTextareaBall = function(playArea) {
			var datasel = [];
			var $textarea = $('#textarea', playArea);
			if ($textarea.length > 0) {
				//根据gameid
				var gameid = $('#gameId').val(),
					as, format;
				switch (gameid) {
					case "101":
					case "104":
					case "103":
					case "105":
					case "118":
					case "115":
					case "117":
					case "120":
					case "119":
					case "121":
					case "122":
					case "123":
					case "124":
					case "125":
					case "126":
					case "127":
					case "401":
					case "402":
						format = $textarea.val().replace(/\,|\;|\n|\t/g, ' ');
						as = format.split(' ');
						break;
					case "201":
					case "202":
					case "203":
					case "204":
					case "205":
					case "206":
						format = $textarea.val().replace(/\,|\;|\n|\t/g, ';');
						as = format.split(';');
						break;
					default:
						format = $textarea.val().replace(/\,|\;|\n|\t/g, ';');
						as = format.split(';');
						break;
				}
				$.each(as, function(idx, val) {
					datasel.push(val);
				});
			}
			return datasel;
		}

		var getData = function() {
			var datasel = [],
				$betarea = $('#betArea'),
				places = getSelectPlace($betarea), //任选 --位置
				balls = getSelectBall($betarea),
				textarea = getTextareaBall($betarea);
			//fballs = getFieldsetsBall($betarea);
			return datasel.concat(places).concat(balls).concat(textarea);
		}

		var playOptions = function() {
			var els = function() {
				return $('#betContainer');
			}

			var multiple = function() {
				return Number(els().find('input[name="multiple"]').val());
			}

			var model = function() {
				var data, val = $('.yuan-jiao-fen', els()).find('div.active').data('val');
				switch (val) {
					case 'yuan':
						data = {
							val: 1,
							money: 1
						}
						break
					case 'jiao':
						data = {
							val: 2,
							money: 0.1
						}
						break
					case 'fen':
						data = {
							val: 3,
							money: 0.01
						}
						break
					case 'li':
						data = {
							val: 4,
							money: 0.001
						}
						break
					case '1yuan':
						data = {
							val: 5,
							money: 0.5
						}
						break
					case '1jiao':
						data = {
							val: 6,
							money: 0.05
						}
						break
					case '1fen':
						data = {
							val: 7,
							money: 0.005
						}
						break
					case '1li':
						data = {
							val: 8,
							money: 0.0005
						}
						break
				}
				return data;
			}

			/**************p_kinds是否任选 methodid玩法id*********************/
			var update = function() {
				var bUnitMoney = 2,
					method = $('#method').val(),
					cptype = $('#cptype').val(),
					num = lotteryUtilsService.inputNumbers(method, getData(), cptype),
					total = multiple() * num * bUnitMoney * model().money,
					beishu = multiple();
				els().find('[data-field="num"]').html(num);
				els().find('[data-field="multiple"]').html(beishu);
				els().find('[data-field="total"]').html(total.toFixed(4));
			}

			return {
				els: els,
				multiple: multiple,
				model: model,
				update: update
			};
		}

		//玩法切换后重置功能
		var reset = function() {
			//选号球样式清空
			$('li.ball', '#betArea').removeClass('on');
			$('#quickpick li', '#betArea').removeClass('on');
			$('#nums').text(0); //注数清零
			$('#total').text('0.00'); //金额清零
			$('#cart-note-num').addClass('hidden');
		}
        //
		// //抓奖倒计时
		// var fetchno; //每5秒抓一次
		// var fetchLottery = function(issue, lotteryId) {
		// 	var isopen = false,
		// 		curtime, fetchCodeTimeout;
		// 	var _getCode = function(issue, lotteryId) {
		// 		postDataService.fetchCodes({
		// 			lotteryId: lotteryId
		// 		}, '/LotteryOpenCode').then(function(data) {
		// 			//计算请求成功后所需的时间差 Date.now()-curtime;
		// 			var difftime = Date.now() - curtime;
		// 			var first_issue = data.data[0];
		// 			//如果已经开奖，就返回不往下执行
		// 			if (isopen) {
		// 				clearTimeout(fetchno);
		// 				return;
		// 			}
		// 			//如果返回的开奖信息中第一条跟传入的期号匹配则表示已开奖 则清空抓将倒计时
		// 			if (first_issue['expect'] === issue) {
		// 				isopen = true;
		// 				clearTimeout(fetchno);
		// 				//开奖了提示开奖号码和期号
		// 				var closeissue = $ionicPopup.alert({
		// 					title: '温馨提示',
		// 					template: '开奖号码：' + first_issue['code'] + '<br>' + '开奖期号:' + first_issue['expect']
		// 				});
		// 				$timeout(function() {
		// 					closeissue.close(); //close the popup after 3 seconds for some reason
		// 				}, 2000);
        //
		// 				//去获取盈亏接口param  lotteryid and expect
		// 				postDataService.fetchPostData({
		// 					lotteryId: lotteryId,
		// 					expect: issue
		// 				}, '/UserBetsProfit').then(function(data) {
		// 					var totalPrize = data.data.data.totalPrize;
		// 					// console.log('本期盈亏：', data.data.data.totalPrize);
		// 					if (totalPrize > 0) {
		// 						var winalert = $ionicPopup.alert({
		// 							title: '恭喜您中奖啦！',
		// 							template: '奖金：' + totalPrize + '<br>' + '开奖期号:' + data.data.data.expect
		// 						});
		// 						$timeout(function() {
		// 							winalert.close(); //close the popup after 3 seconds for some reason
		// 						}, 2000);
		// 					}
		// 				});
		// 				return;
		// 			}
        //
		// 			//如果没有开出奖 则比较请求返回时间差与5秒（默认5秒抓将一次）比较
		// 			//如果小于5秒则settimeout 5秒 如果大于5秒则立即进行下一次请求
		// 			if (difftime < 5000) {
		// 				curtime = Date.now();
		// 				// console.log('五秒后执行');
		// 				fetchno = setTimeout(function() {
		// 					curtime = Date.now();
		// 					_getCode(issue, lotteryId);
		// 				}, 5000);
		// 			} else {
		// 				curtime = Date.now();
		// 				_getCode(issue, lotteryId);
		// 			}
		// 		}, function(error) {
		// 			var difftime = Date.now() - curtime;
		// 			if (difftime < 5000) {
		// 				curtime = Date.now();
		// 				fetchno = setTimeout(function() {
		// 					curtime = Date.now();
		// 					_getCode(issue, lotteryId);
		// 				}, 5000);
		// 			} else {
		// 				curtime = Date.now();
		// 				_getCode(issue, lotteryId);
		// 			}
		// 		});
		// 	}
		// 	fetchno = setTimeout(function() {
		// 		curtime = Date.now();
		// 		_getCode(issue, lotteryId);
		// 	}, 5000);
		// }

		//倒计时功能
		var timerno;
		var countdown = function(start, end, issue, lotteryId) {
			$rootScope.time_leave_flag = false;
			$rootScope.lt_time_leave = 0;
			$rootScope.timer = "00:00:00";
			if (start == "" || end == "") {
				$rootScope.lt_time_leave = 0;
				return;
			}

			function fftime(n) {
				return Number(n) < 10 ? "" + 0 + Number(n) : Number(n);
			}

			function format(dateStr) {
				return new Date(dateStr.replace(/[\-\u4e00-\u9fa5]/g, "/"))
			}

			function diff(t) {
				return t > 0 ? {
					day: Math.floor(t / 86400),
					hour: Math.floor(t % 86400 / 3600),
					minute: Math.floor(t % 3600 / 60),
					second: Math.floor(t % 60)
				} : {
					day: 0,
					hour: 0,
					minute: 0,
					second: 0
				}
			}

			$rootScope.lt_time_leave = (format(end).getTime() - format(start).getTime()) / 1000;
			timerno = window.setInterval(function() {
				if ($rootScope.time_leave_flag) {
					//如果是从其他view返回的投注页时 倒计时还在进行中 防止生成多个倒计时
					clearInterval(timerno);
					// clearInterval(fetchno);
					return;
				}
				//如果倒计时结束清除倒计时
				if ($rootScope.lt_time_leave <= 0) {
					clearInterval(timerno);
					//倒计时结束了开始后台抓奖
					// fetchLottery(issue, lotteryId);
					var closeissue = $ionicPopup.alert({
						title: '温馨提示',
						template: '本期已结束,进入下一期'
					});
					$timeout(function() {
						closeissue.close(); //close the popup after 3 seconds for some reason
					}, 2000);
					if ($state.current.name === 'bet') {
						$state.reload();
					} else {
						$state.go('bet', {
							id: lotteryId
						});
					}

					//倒计时结束后去拉取下一期的服务器时间
				}
				$rootScope.$apply(function() {
					var oDate = diff($rootScope.lt_time_leave--);
					$rootScope.timer = fftime(oDate.hour) + ":" + fftime(oDate.minute) + ":" + fftime(oDate.second);
				});

			}, 1000);
		}

		//清空倒计时--view改变时用
		var cleartimer = function() {
			clearInterval(timerno);
			// clearTimeout(fetchno);
		}
		var clearCountdown = function() {
			clearInterval(timerno);
		}

		return {
			playOptions: playOptions,
			SetCookie: SetCookie,
			getCookie: getCookie,
			delCookie: delCookie,
			reset: reset,
			getData: getData,
			countdown: countdown,
			// fetchLottery: fetchLottery,
			cleartimer: cleartimer,
			clearCountdown: clearCountdown
		}
	}

	//不同controller之前共享数据
	var shareDataService = function() {
		var messages = {};
		messages.list = []; //存放购彩内容
		messages.traceList = []; //存放追号期数
		messages.betObj = {}; //存放投注的list
		messages.base = {};
		messages.add = function(message) {
			//暂用push 有待优化
			messages.list.push(message);
		};
		messages.remove = function(index) {
			messages.list.splice(index - 1, 1);
			return messages.list;
		}
		messages.removeAll = function() {
			messages.list.length = 0;
		}
		messages.removeIssue = function(index) {
			messages.traceList.splice(index, 1);
			return messages.traceList;
		}
		return messages;

	}

	var webSocketService = function($ionicPopup, $timeout, $filter, postDataService, betUtilsService) {
		// 注册
		var register = function(lotteryId, callback) {
			var params = "type=1&lotteryId=" + lotteryId;
			WS.connect(params, function(data){
				var socketData = $.parseJSON(data);
				switch (socketData.type) {
					case 1:
						notifyOpenCode(socketData); break;
					case 2:
						notifyUserBets(socketData); break;
					// case 3:
					// 	notifyRainStart(socketData); break;
					// case 4:
					// 	notifyRainEnd(socketData); break;
					default:
						break;
				}
				
				if (callback) {
					callback(socketData);
				}
			});
		}

		// 显示开奖号码
		var notifyOpenCode = function(socketData) {
			var title = '';
			var lotteryId = betUtilsService.getCookie('cpid');
			if (lotteryId == 117 || lotteryId == '117' || lotteryId === 117 || lotteryId === '117') {
				title = '您的开奖号码为';
			}
			else {
				title += "第" + socketData.expect + "期开奖号码";
			}
			var opencodeAlert = $ionicPopup.alert({
				title: title,
				template: socketData.code
			});
			$timeout(function() {
				opencodeAlert.close();
			}, 3000);
		}

		// 注单提醒
		var notifyUserBets = function(socketData) {
			// 注单通知
			var text = "彩种：" + socketData.lottery + "</br>";
			var lotteryId = betUtilsService.getCookie('cpid');
			if (lotteryId === 117) {
				text += "期号：" + socketData.expect + "</br>";
			}
			text += "玩法：" + socketData.mname + "</br>";
			text += "投注金额：" + socketData.money.toFixed(3) + "</br>";

			if (socketData.status == 2) {
				text += "中奖金额：" + socketData.prizeMoney.toFixed(3) + "</br>";
			}
			text += "状态：" + $filter('statusFilter')(socketData.status);
			var userBetsAlert = $ionicPopup.alert({
				title: "注单开奖提醒",
				template: text
			});
			$timeout(function() {
				userBetsAlert.close();
			}, 5000);
		}

		// 红包雨开始
		var notifyRainStart = function(socketData) {
			// 红包雨活动开始通知
			var startData = socketData.startTime +"_"+ socketData.endTime;
			var item = "RED_PACKET_RAIN_" + startData;
			var noticed = window.localStorage.getItem(item);
			if (noticed == null || noticed == undefined || noticed == '') {
				$timeout(function() {
					playRedPacketRain();
				}, 1000);
				window.localStorage.setItem(item, true);
			}
			else {
				playRedPacketRain();
			}

			// 删除旧的localstorage,避免过多
			var storage = window.localStorage;
			for(var i=0; i<storage.length; i++) {
				var old = storage.key(i);
				if (old.indexOf("RED_PACKET_RAIN_") > -1 && old != item) {
					window.localStorage.removeItem(old);
				}
			}
		}

		// 红包雨结束
		var notifyRainEnd = function(socketData) {
			stopRedPacketRain();
			var title;
			var text;
			if (socketData.todayHasMore == true) {
				title = "本轮红包雨结束";
				text = "本轮红包雨活动结束,不要走开,还有下一轮哦";
			}
			else {
				title = "今天红包雨结束";
				text = "今天的红包雨活动已经结束,请明天继续参与";
			}
			var rainEndAlert = $ionicPopup.alert({
				title: title,
				template: text
			});
			$timeout(function() {
				rainEndAlert.close();
			}, 3000);
		}

		var playRedPacketRain = function() {
			var playing = IS_PLAYING_RED_PACKET_RAIN();
			if (!playing && $("#redPacketLayerCaptured").length == 0 && $("#redPacketLayerOpened").length == 0) {
				var left = (RED_PACKET_Browser_Width - 320) / 2;
				var top = (RED_PACKET_Browser_Height - 250) / 2;
				INIT_RED_PACKET_RAIN(function(){
					stopRedPacketRain();

					var openHtml = '<div class="red_packet_layer" id="redPacketLayerCaptured" style="left: '+left+'px;top: '+top+'px;" data-tap-disabled="true">' +
						'<img src="/static/images/redpacket/red_packet_captured_bg.png" class="red_packet_layer_bg" data-tap-disabled="true"/>' +
						'<p class="red_packet_layer_word" id="redPacketLayerWord" data-tap-disabled="true">点击拆开红包</p>' +
						'<img src="/static/images/redpacket/red_packet_open_btn.png" class="red_packet_layer_open_btn" id="redPacketLayerOpenBtn" data-tap-disabled="true"/>' +
						'<i class="icon ion-close-circled" id="redPacketCloseBtn" data-tap-disabled="true"></i>'+
						'</div>';
					$('body').append(openHtml);
					var redPacketLayerOpenBtn = document.getElementById("redPacketLayerOpenBtn");
					var $redPacketLayerOpenBtn = $(redPacketLayerOpenBtn);
					var redPacketLayerWord = $("#redPacketLayerWord");

					var redPacketCloseBtn = document.getElementById("redPacketCloseBtn");
					redPacketCloseBtn.onclick = function(){
						stopRedPacketRain();
					};

					redPacketLayerOpenBtn.onclick = function(){
						if($redPacketLayerOpenBtn.hasClass("spin")){
							return;
						}

						redPacketLayerWord.html("正在拆开红包...");
						$redPacketLayerOpenBtn.addClass("spin");

						$timeout(function() {
							postDataService.fetchPostData(null, "/activityRedPacketCollect").then(function(data){
								stopRedPacketRain();
								if (data.data.error === 0) {
									var prizeHtml = '<div class="red_packet_layer" id="redPacketLayerOpened" style="left: '+left+'px;top: '+top+'px;" data-tap-disabled="true">' +
										'<img src="/static/images/redpacket/red_packet_opened_bg.png" class="red_packet_layer_bg" data-tap-disabled="true"/>' +
										'<p class="red_packet_layer_word" id="redPacketLayerWord" data-tap-disabled="true">恭喜您获得￥<span class="red_packet_layer_amount" data-tap-disabled="true">'+data.data.amount+'</span>元红包</p>' +
										'<i class="icon ion-close-circled" id="redPacketCloseBtn" data-tap-disabled="true"></i>'+
										'</div>';
									$('body').append(prizeHtml);
								}
								else {
									var errorHtml = '<div class="red_packet_layer" id="redPacketLayerOpened" style="left: '+left+'px;top: '+top+'px;" data-tap-disabled="true">' +
										'<img src="/static/images/redpacket/red_packet_opened_bg.png" class="red_packet_layer_bg" data-tap-disabled="true"/>' +
										'<p class="red_packet_layer_word" id="redPacketLayerWord" data-tap-disabled="true">'+data.data.message+'</p>' +
										'<i class="icon ion-close-circled" id="redPacketCloseBtn" data-tap-disabled="true"></i>'+
										'</div>';
									$('body').append(errorHtml);
								}

								var redPacketCloseBtn = document.getElementById("redPacketCloseBtn");
								redPacketCloseBtn.onclick = function(){
									stopRedPacketRain();
								};
							});
						}, 2000);
					};
				});
				START_RED_PACKET_RAIN();
			}
		}

		var stopRedPacketRain = function() {
			STOP_RED_PACKET_RAIN();
			$("#redPacketLayerCaptured").remove();
			$("#redPacketLayerOpened").remove();
		}

		return {
			register: register
		}
	}

	var lzmaService = function(lotteryUtilsService) {
		var my_lzma = new LZMA("/static/js/lzma/lzma_worker-min.js");

		var _compress = function(data, callback) {
			my_lzma.compress(data, 1, function(result, error){
				if (error) {
					callback(data);
					return;
				}
				else {
					var compressedData = lotteryUtilsService.toHex(result);
					callback(compressedData);
				}
			});
		}

		return {
			compress: _compress
		}
	}

	var passwordService = function() {
		var _getDisposableToken = function() {
			var token = null;
			$.ajax({
				url : '/DisposableToken',
				type: 'POST',
				dataType: 'json',
				contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
				async: false,
				success : function(tokenRes) {
					if (tokenRes.error === 0) {
						token = tokenRes.token;
					}
				},
				complete: function() {
				},
				error: function(){
				}
			});
			return token;
		}
		
		var _encryptPasswordWithToken = function(plainStr, token) {
			var password = hex_md5(plainStr).toUpperCase();
			password = hex_md5(password).toUpperCase();
			password = hex_md5(password).toUpperCase();
			password = hex_md5(password + token).toUpperCase();
			return password;
		}
		
		var _encryptPasswordWithoutToken = function(plainStr) {
			var password = hex_md5(plainStr).toUpperCase();
			password = hex_md5(password).toUpperCase();
			password = hex_md5(password).toUpperCase();
			return password;
		}

		var _generatePassword = function(plainStr) {
			var password = hex_md5(plainStr).toUpperCase();
			password = hex_md5(password).toUpperCase();
			return password;
		}

		return {
			getDisposableToken: _getDisposableToken,
			encryptPasswordWithToken: _encryptPasswordWithToken,
			encryptPasswordWithoutToken: _encryptPasswordWithoutToken,
			generatePassword: _generatePassword
		}
	}

	var utilService = function() {
		var setMaxScale = function(value, maxScale) {
			if (value == 0) {
				return 0;
			}
			var valStr = value.toString();
			var nums = valStr.split(".");
			if(nums.length == 1){
				return value;
			}
			if (nums.length>1) {
				if(nums[1].length > maxScale){
					var end = nums[0].length + 1 + maxScale;
					return parseFloat(valStr.substring(0, end));
				}
				return value;
			}
			return value;
		}

		return {
			setMaxScale: setMaxScale
		}
	}

	angular.module('app.services', [])
		.service('postDataService', postDataService)
		.service('lotteryUtilsService', lotteryUtilsService)
		.service('fetchDataService', fetchDataService)
		.service('betUtilsService', betUtilsService)
		.service('shareDataService', shareDataService)
		.service('webSocketService', webSocketService)
		.service('lzmaService', lzmaService)
		.service('passwordService', passwordService)
		.service('utilService', utilService)
})();