var Menu = function() {
	
	var init = function() {
		var hormenu = $('<div class="page-sidebar navbar-collapse collapse">');
		hormenu.append(list(mlist, false));
		$('.page-sidebar-wrapper').html(hormenu);
		// 点击加载到iframe的方式
		hormenu.find('a').click(function() {
			var href = $(this).attr('data-href');
			if(href) {
				Iframe.load(href);
				hormenu.find('a').parent('li').removeClass('active');
				$(this).parent('li').addClass('active');
			}
		});
	};
	
	/*var setActive = function(hormenu) {
		var url = window.location.href;
		var link = url.substring(url.lastIndexOf('/') + 1);
		var li = hormenu.find('a[href="' + link + '"]').parent();
		li.addClass('active');
		li.parents('li').addClass('open');
		li.parents('ul').show();
	};*/
	
	var list = function(items, sub) {
		var ul = $('<ul>');
		if(sub) {
			ul.addClass('sub-menu');
		} else {
			ul.addClass('page-sidebar-menu page-sidebar-menu-compact').attr('data-slide-speed', 200);
		}
		$.each(items, function(idx, item) {
			var li = $('<li>').append($('<a href="javascript:;">').attr('data-href', item.link).append($('<i>').addClass(item.icon)));
			if(sub) {
				li.find('a').append($('<span class="title">').html(item.name));
			} else {
				li.find('a').append(item.name);
			}
			if(item.items.length > 0) {
				li.find('a').append('<span class="arrow ">');
				li.append(list(item.items, true));
			}
			ul.append(li);
		});
		return ul;
	};
	
	return {
		init: init
	};
}();