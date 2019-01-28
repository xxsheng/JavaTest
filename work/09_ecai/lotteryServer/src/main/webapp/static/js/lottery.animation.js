$(function(){
	init();
	function init() {
		var name = $("#animation").data("name");
		switch(name) {
			case '重庆时时彩':
				$("#animation_url").html('<center><iframe src="https://kj.kai861.com/view/video/SSC/index.html?10002?1680383.com" width="100%" scrolling="no" height="100%" frameborder="0" style="width: 1128px; height: 752px;"></iframe></center>');
				$("#animation").show();
			  	break;
			case '北京PK拾':
				$("#animation_url").html('<center><iframe src="https://kj.kai861.com/view/video/PK10/video.html?10001?1680383.com" width="100%" scrolling="no" height="100%" frameborder="0" style="width: 1128px; height: 735.652px;"></iframe></center>');
				$("#animation").show();
				break;
			//case '急速赛马':
			//	$("#animation_url").html('<center><iframe src="/static/animation/jssm/index.html" width="100%" scrolling="no" height="100%" frameborder="0" style="width: 1128px; height: 752px;"></iframe></center>');
			//	$("#animation").show();
			//  	break;
		}
	}
	$("#onshow").click(function() {
		var html = $("#onshow")[0].src;
		if(html.indexOf("guan.png") != -1) {
			$("#animation_url").html("");
			$("#animation").hide();
			$("#onshow").attr("src", "/static/images/kai.png");
		}
		if(html.indexOf("kai.png") != -1) {
			init();
			$("#onshow").attr("src", "/static/images/guan.png");
		}
	});
})