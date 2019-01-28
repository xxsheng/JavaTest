package javautils.http;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import javautils.StringUtil;
import lottery.web.WebJSON;

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

	public static String getRealIp(WebJSON json, HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
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
		/*
		if (!isLegalIPV4(ip)) {
			// 不是合法IPV4地址
			if (json != null) {
				json.set(2, "2-1092", ip);
			}
			return null;
		}
		*/
		return ip;
	}

	public static boolean isLegalIPV4(String ipv4) {
		if(ipv4 == null || ipv4.length()==0){
			return false; //字符串为空或者空串
		}

		String[] parts=ipv4.split("\\.");//因为java doc里已经说明, split的参数是reg, 即正则表达式, 如果用"|"分割, 则需使用"\\|"
		if(parts.length != 4){
			return false;//分割开的数组根本就不是4个数字
		}
		for(int i=0; i<parts.length; i++){
			try{
				int n = Integer.parseInt(parts[i]);
				if(n<0 || n>255){
					return false;//数字不在正确范围内
				}
			}catch (NumberFormatException e) {
				return false;//转换数字不正确
			}
		}
		return true;
	}

	public static String getHost(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getHeader("Host").trim();
	}
	
	public static String escapeInput(String s) {
		return HtmlUtils.htmlEscape(s);
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
			if (StringUtils.isEmpty(value)) {
				return null;
			}
			return Integer.parseInt(value);
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
	
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

	public static Boolean getBooleanParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			if (StringUtils.isEmpty(value)) {
				return null;
			}
			return Boolean.parseBoolean(value);
		} catch (Exception e) {}
		return null;
	}
	
	public static Double getDoubleParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			if (StringUtils.isEmpty(value)) {
				return null;
			}
			return Double.parseDouble(value);
		} catch (Exception e) {}
		return null;
	}
	
	public static Float getFloatParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			if (StringUtils.isEmpty(value)) {
				return null;
			}
			return Float.parseFloat(value);
		} catch (Exception e) {}
		return null;
	}
	
	public static Long getLongParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		try {
			if (StringUtils.isEmpty(value)) {
				return null;
			}
			return Long.parseLong(value);
		} catch (Exception e) {}
		return null;
	}
	
	public static String formatSessionId(final HttpSession session) {
		String id = session.getId();
		if(id.indexOf("!") != -1) {
			id = id.substring(0, id.indexOf("!"));
		}
		return id;
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
		Enumeration<?> paramNames = request.getParameterNames();
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
	
	public static void printHeaderFields(HttpURLConnection conn) {
		Map<String, List<String>> headerFields = conn.getHeaderFields();
		for (Object key : headerFields.keySet().toArray()) {
			System.out.println(key + ":" + headerFields.get(key).toString());
			for (String value : headerFields.get(key)) {
				System.out.println("==============:" + value);
			}
		}
	}

	/**
	 * 返回http(s)://www.xxx.com
	 */
	public static String getReferer(HttpServletRequest request) {
//		return request.getScheme() + "://" + getServerName(request);
		return getSchema(request) + "://" + getServerName(request);
	}

	/**
	 * 获取访问协议，nginx中需要配置  proxy_set_header   X-Forwarded-Proto $scheme;
	 */
	public static String getSchema(HttpServletRequest request) {
		String schema = request.getHeader("X-Forwarded-Proto");
		if (!"http".equalsIgnoreCase(schema) && !"https".equalsIgnoreCase(schema)) {
			schema = request.getScheme();
		}
		return schema;
	}
	/**
	 * 返回www.xxx.com
	 */
	public static String getServerName(HttpServletRequest request) {
		return request.getServerName();
	}

	public static String getUserAgent(HttpServletRequest request) {
		return getHeader(request, "User-Agent");
	}

	/**
	 * 返回经过过滤的http头
	 */
	public static String getHeader(HttpServletRequest request, String header) {
		String headerValue = request.getHeader(header);
		headerValue = HttpUtil.escapeInput(headerValue);
		return headerValue;
	}
}