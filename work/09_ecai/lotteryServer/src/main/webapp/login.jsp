<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String ua = request.getHeader("User-Agent") ;
	if (ua != null) {
		if (ua.indexOf("iPhone") >-1 || ua.indexOf("iPad") >-1 || (ua.indexOf("ndroid") >-1 && ua.indexOf("WebKit") >-1)) {
			response.sendRedirect("/m");
		} else {
			response.sendRedirect("login");
		}
	} else {
		response.sendRedirect("login");
	}
%>