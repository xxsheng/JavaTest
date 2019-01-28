package lottery.domains.content.payment.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

/**
 * url工具类
 * 
 * @author Mick
 * @since 2015-9-3
 */
public class UrlParamUtils {
	private static final String EMPTY = "";
	private static final String EQUALS = "=";
	private static final String DEFAULT_SEPARATOR = "&";
	
	public static String toUrlParam(Map<String, String> params) {
		return toUrlParam(params, DEFAULT_SEPARATOR, true);
	}
	
	public static String toUrlParam(Map<String, String> params, String separator) {
		return toUrlParam(params, separator, true);
	}
	
	public static String toUrlParam(Map<String, String> params, String separator, boolean ignoreEmpty) {
		if (params == null || params.isEmpty()) {
			return EMPTY;
		}
		
		StringBuffer url = new StringBuffer();
		
		Iterator<Entry<String, String>> it = params.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, String> entry = it.next();
			String value = entry.getValue();
			boolean valueIsEmpty = isEmpty(value);
			if (ignoreEmpty && valueIsEmpty) { // 主动忽略空值并且值为空
				continue;
			}
			
			url.append(entry.getKey()).append(EQUALS) .append(value);
			if (it.hasNext()) {
				url.append(separator);
			}
		}
		
		return url.toString();
	}
	

	
	 public String buildSignStr(Map<String, Object> params) {
	        StringBuilder sb = new StringBuilder();
	        // 将参数以参数名的字典升序排序
	        Map<String, Object> sortParams = new TreeMap<String, Object>(params);
	        // 遍历排序的字典,并拼接"key=value"格式
	        for (Map.Entry<String, Object> entry : sortParams.entrySet()) {
	            if (sb.length()!=0) {
	                sb.append(DEFAULT_SEPARATOR);
	            }
	            sb.append(entry.getKey()).append("=").append(entry.getValue());
	        }
	        return sb.toString();
	    }


	public static String toUrlParamWithoutEmpty(Map<String, String> params, String separator,boolean bool) {
		if (params == null || params.isEmpty()) {
			return EMPTY;
		}
		 Map<String, String> sortParams = null;
		StringBuffer url = new StringBuffer();
		if(bool){
			 sortParams = new TreeMap<String, String>(params);
		}else{
			sortParams = params;
		}
	
		Iterator<Entry<String, String>> it = sortParams.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, String> entry = it.next();
			String value = entry.getValue();
			if (StringUtils.isEmpty(value)) { // 主动忽略空值并且值为空
				continue;
			}

			url.append(entry.getKey()).append(EQUALS) .append(value);
			if (it.hasNext()) {
				url.append(separator);
			}
		}

		String urlStr = url.toString();
		if (urlStr.endsWith(separator)) {
			urlStr = urlStr.substring(0, urlStr.length() - separator.length());
		}

		return urlStr;
	}

	public static Map<String, String> fromUrlParam(String url) {
		Map<String, String> paramsMap = new HashMap<>();

		String[] params = url.split(DEFAULT_SEPARATOR);
		for (String param : params) {
			String[] values = param.split(EQUALS);

			if (values == null) {
				continue;
			}

			String key = values.length > 0 ? values[0] : null;
			String value = values.length > 1 ? values[1] : null;
			if (key == null) {
				continue;
			}

			paramsMap.put(key, value);
		}

		return paramsMap;
	}

	private static boolean isEmpty(String value) {
		return value == null || EMPTY.equals(value);
	}
}
