<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!-- 脚本执行必要参数 -->
<script type="text/javascript">
	var Lottery = ${Lottery}; 
	var UserData = ${UserData}; 
	var PlayRulesGroup = ${PlayRulesGroup};
	var PlayRules = ${PlayRules};
	var Config = ${Config};
</script>
<link rel="stylesheet" href="<%=cdnDomain%>static/plugins/noUiSlider/noUiSlider.css">
<link changeable="true" rel="stylesheet" href="<%=cdnDomain%>static/css/lottery-<%=currentTheme%>.css?v=${cdnVersion}">

<script src="<%=cdnDomain%>static/plugins/lzma/lzma-min.js"></script>
<script src="<%=cdnDomain%>static/plugins/jquery/jquery.SuperSlide.2.1.2.js"></script>

<%--<script src="<%=cdnDomain%>static/plugins/slider/jquery.bxslider.min.js"></script>--%>

<script src="<%=cdnDomain%>static/plugins/noUiSlider/noUiSlider.js"></script>
<%--<script src="<%=cdnDomain%>static/plugin//scrollbar/scrollbar.js"></script>--%>
<script src="<%=cdnDomain%>static/plugins/websocket/websocket.js"></script>
<%--<script src="<%=cdnDomain%>static/js/lottery.order.details.js?v=${cdnVersion}"></script>--%>
<script src="<%=cdnDomain%>static/js/lottery.common.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/lottery.open.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/lottery.order.details.js?v=${cdnVersion}"></script>
<script src="<%=cdnDomain%>static/js/lottery.chase.js?v=${cdnVersion}"></script>
<%--<script src="<%=cdnDomain%>static/js/base.js?v=${cdnVersion}"></script>--%>
