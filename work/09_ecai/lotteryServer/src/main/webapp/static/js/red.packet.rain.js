var RED_PACKET_Pictures = ["/static/images/redpacket/red_packet_sm.jpg"];
// var RED_PACKET_no = 15;
var RED_PACKET_no = 8;
var $body = $('body');

var RED_PACKET_browser_IE_NS =0;
var RED_PACKET_browser_MOZ = (self.innerWidth) ? 1 : 0;
var RED_PACKET_browser_IE7 = (document.documentElement.clientHeight) ? 1 : 0;

var RED_PACKET_Time = null;
var RED_PACKET_dx, RED_PACKET_xp, RED_PACKET_yp;
var RED_PACKET_am, RED_PACKET_stx, RED_PACKET_sty;
var i, RED_PACKET_Browser_Width, RED_PACKET_Browser_Height;

if (RED_PACKET_browser_IE_NS) {
	RED_PACKET_Browser_Width = document.body.clientWidth;
	RED_PACKET_Browser_Height = document.body.clientHeight;
}
else if (RED_PACKET_browser_MOZ) {
	RED_PACKET_Browser_Width = self.innerWidth - 20;
	RED_PACKET_Browser_Height = self.innerHeight;
}
else if (RED_PACKET_browser_IE7)
{
	RED_PACKET_Browser_Width = document.documentElement.clientWidth;
	RED_PACKET_Browser_Height = document.documentElement.clientHeight;
}

RED_PACKET_dx = new Array();
RED_PACKET_xp = new Array();
RED_PACKET_yp = new Array();
RED_PACKET_am = new Array();
RED_PACKET_stx = new Array();
RED_PACKET_sty = new Array();

function INIT_RED_PACKET_RAIN(callback){
	$body.css('overflow-x','hidden');
	for (var i = 0; i < RED_PACKET_no; ++ i) {
		RED_PACKET_dx[i] = 0;
		RED_PACKET_xp[i] = Math.random()*(RED_PACKET_Browser_Width-100);
		RED_PACKET_yp[i] = Math.random()*RED_PACKET_Browser_Height;
		RED_PACKET_am[i] = Math.random()*20;
		RED_PACKET_stx[i] = 0.02 + Math.random()/20;
		RED_PACKET_sty[i] = 1+Math.random();
		var SHOW_Image = RED_PACKET_Pictures[parseInt(Math.random()*1)];

		var image = $('<img id="RED_PACKET_'+i+'" style="z-index: 123'+i+';" class="red_packet_rain" src="'+SHOW_Image+'" data-tap-disabled="true"/>');
		$body.append(image);
	}

	for (var i = 0; i < RED_PACKET_no; ++ i) {
		var red = document.getElementById("RED_PACKET_" + i);
		red.onclick = function () {
			$(red).remove();
			if (callback) {
				callback();
			}
		}
	}
}

function START_RED_PACKET_RAIN() {
	for (var i = 0; i < RED_PACKET_no; ++ i) {
		RED_PACKET_yp[i] += RED_PACKET_sty[i];

		if (RED_PACKET_yp[i] > RED_PACKET_Browser_Height-50) {
			RED_PACKET_xp[i] = Math.random()*(RED_PACKET_Browser_Width-RED_PACKET_am[i]-50);
			RED_PACKET_yp[i] = 0;
			RED_PACKET_stx[i] = 0.02 + Math.random()/20;
			RED_PACKET_sty[i] = 1+Math.random();
		}

		RED_PACKET_dx[i] += RED_PACKET_stx[i];
		var redPacket = $("#RED_PACKET_" + i);
		var top = RED_PACKET_yp[i];
		var left = RED_PACKET_xp[i] + RED_PACKET_am[i]*Math.sin(RED_PACKET_dx[i]);
		redPacket.css({"top": top+"px", "left": left+"px"});
	}
	RED_PACKET_Time = setTimeout("START_RED_PACKET_RAIN()", 5);
}

function STOP_RED_PACKET_RAIN(){
	clearTimeout(RED_PACKET_Time);
	RED_PACKET_Time = null;
	for (var i = 0; i < RED_PACKET_no; ++ i) {
		var redPacket = $("#RED_PACKET_" + i);
		redPacket.remove();
	}
	$body.css('overflow-x','auto');
}

function IS_PLAYING_RED_PACKET_RAIN() {
	return RED_PACKET_Time != null;
}
