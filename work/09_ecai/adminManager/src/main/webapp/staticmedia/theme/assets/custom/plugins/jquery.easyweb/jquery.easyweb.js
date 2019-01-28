(function($) {
	$.pagination = function(options) {
		var language = $.extend({}, $.pagination.language, options.language);
		var opts = $.extend({}, $.pagination.defaults, options);
		var render = $(opts.render);
		var page = 1, size = opts.pageSize;
		var load = function() {
			var data = opts.ajaxData;
			if($.isFunction(data)) {
				data = data();
			}
			data = $.extend({}, data, {start: (page - 1) * size, limit: size});
			$.ajax({
				type: opts.ajaxType,
				url: opts.ajaxUrl,
				data: data,
				dataType: 'json',
				beforeSend: opts.beforeSend,
				complete: opts.complete,
				success: function(response) {
					if(response.error == 0) {
						if(response.totalCount && response.totalCount > 0) {
							update(response.totalCount);
							if(response.data && response.data.length > 0) {
								opts.success(response.data, response);

								if ($.showTooltip) {
									$.showTooltip();
								}
							} else {
								if(page > 1) {
									page--;
									load();
								}
							}
						} else {
							opts.emptyData(response);
							render.empty();
						}
					} else {
						opts.pageError(response);
					}
				},
				complete: function(){
				}
			});
		};
		var update = function(totalCount) {
			if(totalCount == 0) return;
			var pageCount = Math.ceil(totalCount/size);
			var pagination = $('<div class="easyweb-pagination">');
			var infos = $('<div class="infos">');
			infos.append(language.infos.replace('${currPage}', '<span class="p">' + page + '</span>').replace('${totalPage}', '<span class="p">' + pageCount + '</span>').replace('${start}', '<span class="s">' + ((page - 1) * size + 1) + '</span>').replace('${end}', '<span class="e">' + (page * size > totalCount ? totalCount : page * size) + '</span>').replace('${total}', '<span class="t">' + totalCount + '</span>'));
			var pages = $('<div class="pages">');
			pages.append($('<a class="top">').html(language.top));
			pages.append($('<a class="prev">').html(language.prev));
			var pageLength = opts.pageLength;
			if(pageCount < pageLength) {
				pageLength = pageCount;
			}
			var startPage = page - (Math.ceil(pageLength/2) - 1);
			var endPage = page + Math.floor(pageLength/2);
			if(startPage < 1) {
				startPage = 1;
				endPage = pageLength;
			}else if(endPage > pageCount) {
				startPage = pageCount - pageLength + 1;
				endPage = pageCount;
			}
			for (var i = startPage; i <= endPage; i++) {
				var thisPage = $('<a class="page">').html(i);
				if(i == page) {
					thisPage.addClass('selected');
				}
				pages.append(thisPage);
			}
			pages.append($('<a class="next">').html(language.next));
			pages.append($('<a class="end">').html(language.end));
			pages.find('.page').click(function() {
				var idx = $(this).html();
				idx = parseInt(idx);
				if(idx != page) {
					page = idx;
					load();
				}
			});
			pages.find('.top').click(function() {
				if(page > 1) {
					page = 1;
					load();
				}
			});
			pages.find('.prev').click(function() {
				if(page > 1) {
					page--;
					load();
				}
			});
			pages.find('.end').click(function() {
				if(page < pageCount) {
					page = pageCount;
					load();
				}
			});
			pages.find('.next').click(function() {
				if(page < pageCount) {
					page++;
					load();
				}
			});
			var go = $('<div class="go">').append($('<input type="text" />').val(page)).append($('<a class="btn-go">').html(language.go));
			go.find('.btn-go').click(function() {
				var idx = go.find('input[type="text"]').val();
				idx = parseInt(idx);
				if(idx > 0 && idx <= pageCount) {
					if(idx != page) {
						page = idx;
						load();
					}
				} else {
					opts.pageError(language.msg);
				}
			});
			if(!opts.hideInfos) {
				pagination.append(infos);
			}
			pagination.append(pages);
			if(!opts.hideGo) {
				pagination.append(go);
			}
			render.html(pagination);
		};
		var init = function() {
			page = 1;
			load();
		};
		return {
			init: init,
			reload: load
		};
	};
	
	$.pagination.language = {
		//infos: '当前第${currPage}/${totalPage}页，显示${start}至${end}条数据，总计${total}条数据。',
		infos: '记录总数：${total}，页数：${currPage}/${totalPage}',
		top: '首页',
		end: '尾页',
		prev: '上一页',
		next: '下一页',
		go: '搜索',
		msg: '请输入正确的页数。'
	};
	
	$.pagination.defaults = {
		render: '.pagination',
		hideInfos: false,
		hideGo: false,
		pageLength: 6,
		pageSize: 10,
		ajaxType: 'post',
		ajaxUrl: '',
		ajaxData: {},
		beforeSend: function() {},
		complete: function() {},
		success: function(list) {},
		pageError: function(response) {
			alert(response.message);
		},
		emptyData: function() {
			
		}
	};
})(jQuery);