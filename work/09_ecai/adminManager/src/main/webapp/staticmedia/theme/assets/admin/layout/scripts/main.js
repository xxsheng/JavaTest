var MainFrame = function() {
	
	var iframe = $('iframe[name="main-frame"]');
	
	var init = function() {
		iframe.css({height: height()});
	}
	
	var load = function(src) {
		iframe.attr('src', src);
	}
	
	var height = function() {
        var sidebarHeight = Metronic.getViewPort().height - $('.page-header').outerHeight();
        if ($('body').hasClass("page-footer-fixed")) {
            sidebarHeight = sidebarHeight - $('.page-footer').outerHeight();
        }
        return sidebarHeight;
    }
	
	return {
		init: function() {
			init();
			$(window).resize(init);
		},
		load: load
	}
}();

var MainMenu = function() {
	
	var init = function() {
		var hormenu = $('<div class="page-sidebar navbar-collapse collapse">');
		hormenu.append(list(mlist, false));
		$('.page-sidebar-wrapper').html(hormenu);
	};
	
	var list = function(items, sub) {
		var ul = $('<ul>');
		if(sub) {
			ul.addClass('sub-menu');
		} else {
			ul.addClass('page-sidebar-menu page-sidebar-menu-compact').attr('data-slide-speed', 200);
		}
		$.each(items, function(idx, item) {
			var li = $('<li>').append($('<a>'));
			if(item.link) {
				li.find('a').attr('target', 'main-frame');
				li.find('a').attr('href', item.link);
			} else {
				li.find('a').attr('href', 'javascript:;');
			}
			li.find('a').append($('<i>').addClass(item.icon));
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