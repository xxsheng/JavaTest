var GameReportComplex = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var ReportTable = function() {
		var tableList = $('#table-report');
		var $userDirection = $('.page-breadcrumb', tableList);
		var $username = $('input[name=username]', tableList);
		var $page = $(".page-content");
		var $loader = $("<div class='loader'></div>");

		var getParams = function() {
			var username = tableList.find('input[name="username"]').val();
			var sTime = tableList.find('[data-field="time"] > input[name="from"]').val();
			var eTime = tableList.find('[data-field="time"] > input[name="to"]').val();
			eTime = getNetDate(eTime);
			return {username: username, sTime: sTime, eTime: eTime};
		}
		
		var isSearching = false;
		var doSearch = function() {
			if(isSearching) return;
			var url = './history-game-report/complex';
			isSearching = true;
			$page.append($loader);
			var params = getParams();
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						buildData(data.list, params.username);
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				},
				complete: function(){
					isSearching = false;
					$loader.remove();
				}
			});
		}
		
		var buildData = function(data, username) {
			var table = tableList.find('table > tbody').empty();
			if(data.length > 0) {
				var innerHtml = '';
				$.each(data, function(idx, val) {
					var formatUser;
					if(idx == 0 || val.name == username || val.hashMore == false) {
						formatUser = val.name;
					} else {
						formatUser = '<a data-command="details" data-val="' + val.name + '" href="javascript:;">' + val.name + '</a>';
					}
					var profit = val.prize + val.waterReturn + val.proxyReturn + val.activity - val.billingOrder;
					innerHtml +=
					'<tr class="align-center">'+
						'<td>' + formatUser + '</td>'+
						'<td>' + val.transIn.toFixed(3) + '</td>'+
						'<td>' + val.transOut.toFixed(3) + '</td>'+
						'<td>' + val.billingOrder.toFixed(3) + '</td>'+
						'<td>' + val.prize.toFixed(3) + '</td>'+
						'<td>' + (val.waterReturn + val.proxyReturn).toFixed(3) + '</td>'+
						'<td>' + val.activity.toFixed(3) + '</td>'+
						'<td>' + profit.toFixed(3) + '</td>'+
					'</tr>';
				});
				table.html(innerHtml);
				table.find('[data-command="details"]').unbind('click').click(function() {
					var username = $(this).attr('data-val');
					if (username == $username.val()) {
						return;
					}
					
					if (username) {
						appendUserDirection(username);
						$username.val(username);
					}
					doSearch();
				});
			} else {
				table.html('<tr><td colspan="8">没有报表记录</td></tr>');
			}
		}

		var appendUserDirection = function (username) {
			var li = $("<li></li>");
			var a = $("<a href='javascript:;'>"+username+"</a>");
			a.bind("click", function () {
				directionClick(a, username);
			});
			var lastChild = $userDirection.find("li:last-child");
			if (lastChild) {
				lastChild.append("<i class=\"fa fa-angle-double-right\"></i>");
			}
			li.append(a);
			$userDirection.append(li);
		}

		var directionClick = function(tag, username) {
			$(tag).next().remove();
			$(tag).parent().nextAll().remove();
			$username.val(username);
			doSearch();
		};
		
		tableList.find('[data-command="search"]').unbind('click').click(function() {
			var username = $username.val();
			if (!username) {
				$userDirection.empty();
			}
			else {
				var lastChild = $userDirection.find("li[data-username="+username+"]");
				if (!lastChild) {
					$userDirection.empty();
				}
				else {
					lastChild.find("i").remove();
					lastChild.nextAll().remove();
				}
			}
			doSearch();
		});

		var init = function() {
			var today = moment().format('YYYY-MM-DD');
			var tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');
			tableList.find('[data-field="time"] > input[name="from"]').val(today);
			tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);
			doSearch();
		}
		
		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			ReportTable.init();
			handelDatePicker();
		}
	}
}();