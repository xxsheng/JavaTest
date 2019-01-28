package javautils.http;

import javautils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Http工具类
 */
public class HttpUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	public static final String json = "text/json";
	public static final String html = "text/html";
	public static final String xml = "text/xml";
	
	public static void write(HttpServletResponse response, String s) {
		logger.debug(s);
		if (StringUtil.isNotNull(s)) {
			try {
				response.setCharacterEncoding("utf-8");
				PrintWriter writer = response.getWriter();
				writer.write(s);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void write(HttpServletResponse response, String s,
			String ContentType) {
		logger.debug(s);
		if (StringUtil.isNotNull(s)) {
			try {
				response.setContentType(ContentType);
				response.setCharacterEncoding("utf-8");
				PrintWriter writer = response.getWriter();
				writer.write(s);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeJSONP(HttpServletRequest request,
			HttpServletResponse response, String s, String ContentType) {
		String callback = request.getParameter("callback");
		logger.debug(s);
		if (StringUtil.isNotNull(s)) {
			try {
				String callbackStr = callback + "(" + s + ")"; // 跨域请求用
				response.setContentType(ContentType);
				response.setCharacterEncoding("utf-8");
				PrintWriter writer = response.getWriter();
				writer.write(callbackStr);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void writeJSONP(HttpServletRequest request,
			HttpServletResponse response, String s) {
		String callback = request.getParameter("callback");
		logger.debug(s);
		if (StringUtil.isNotNull(s)) {
			try {
				String callbackStr = callback + "(" + s + ")"; // 跨域请求用
				response.setCharacterEncoding("utf-8");
				PrintWriter writer = response.getWriter();
				writer.write(callbackStr);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getWebDomainPath(HttpServletRequest request) {
		String name = request.getServerName();
		if("localhost".equals(name)) {
			name = "127.0.0.1";
		}
		int port = request.getServerPort();
		if(port == 80) {
			return "http://" + name;
		} else {
			return "http://" + name + ":" + port;
		}
	}

	public static String getWebPath(HttpServletRequest request) {
		String path = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + request.getServletPath();
		return path;
	}
	
	public static String getRequestPath(HttpServletRequest request) {
        String path = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + request.getServletPath();
		String queryStr = request.getQueryString();
		if(StringUtil.isNotNull(queryStr)) {
			path += "?" + queryStr;
		}
        return path;
	}
	
	public static Map<String, String> getRequestMap(String queryStr) {
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtil.isNotNull(queryStr)) {
			String[] strs = queryStr.split("&");
			for (String str : strs) {
				String[] keyValue = str.split("=");
				map.put(keyValue[0], keyValue[1]);
			}
		}
		return map;
	}
	
	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if(ip.split(",").length > 0) {
			ip = ip.split(",")[0];
		}
		return ip;
	}
	
	public static Short getShortParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			return Short.parseShort(value);
		} catch (Exception e) {}
		return null;
	}
	
	public static Integer getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			if (StringUtil.isNotNull(value)) {
				return Integer.parseInt(value);
			}
			return null;
		} catch (Exception e) {}
		return null;
	}

	public static String getStringParameterTrim(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			if (value != null) return value.trim();
			return value;
		} catch (Exception e) {}
		return null;
	}

	public static Boolean getBooleanParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			return Boolean.parseBoolean(value);
		} catch (Exception e) {}
		return null;
	}
	
	public static Double getDoubleParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			if (StringUtil.isNotNull(value)) {
				return Double.parseDouble(value);
			}
			return null;
		} catch (Exception e) {}
		return null;
	}
	
	public static Float getFloatParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {}
		return null;
	}
	
	public static Long getLongParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			return Long.parseLong(value);
		} catch (Exception e) {}
		return null;
	}
	
	public static String encodeURL(String destURL) {
		try {
			destURL = URLEncoder.encode(destURL, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return destURL;
	}

	public static String decodeURL(String destURL) {
		try {
			destURL = URLDecoder.decode(destURL, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return destURL;
	}
	
	public static String getRequestURL(HttpServletRequest request) {
		boolean flag = true;
		String paramName, paramValue;
		StringBuffer requestURL = request.getRequestURL();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			if (flag) {
				flag = false;
				requestURL.append("?");
			} else {
				requestURL.append("&");
			}
			paramName = (String) paramNames.nextElement();
			try {
				paramValue = URLEncoder.encode(request.getParameter(paramName), "utf-8");
				requestURL.append(paramName).append("=").append(paramValue);
			} catch (UnsupportedEncodingException e) {
			}
		}
		return requestURL.toString();
	}
	
	public static void printRquestParams(HttpServletRequest request) {
		Map<String, String[]> paramsMap = request.getParameterMap();
		for (Object key : paramsMap.keySet().toArray()) {
			System.out.println("key:" + key);
			System.out.print("value:");
			for (String value : paramsMap.get(key)) {
				System.out.print(value);
			}
			System.out.println();
		}
	}
	
	public static void printHeaderFields(HttpURLConnection conn) {
		Map<String, List<String>> headerFields = conn.getHeaderFields();
		for (Object key : headerFields.keySet().toArray()) {
			System.out.println(key + ":" + headerFields.get(key).toString());
			for (String value : headerFields.get(key)) {
				System.out.println("==============:" + value);
			}
		}
	}
	
}