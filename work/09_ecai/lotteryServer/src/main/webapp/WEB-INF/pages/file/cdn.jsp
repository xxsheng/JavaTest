<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
    String currentTheme = "white";

    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("YF_THEME")) {
                currentTheme = cookie.getValue();
                break;
            }
        }
    }

    if (!currentTheme.equals("black") && !currentTheme.equals("blue") && !currentTheme.equals("white")) {
        currentTheme = "black";
    }

     String cdnDomain = "/";
%>
<script>
var cdnDomain = '/';
if (cdnDomain == '/' || cdnDomain == '//') {
    cdnDomain = window.location.protocol + '//' + window.location.host;
    cdnDomain += '/';
}
    var cdnVersion = '${cdnVersion}';
    var currentTheme = "<%=currentTheme%>";
</script>