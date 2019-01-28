package lottery.domains.content.payment.zs.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

/**
 * http操作工具类
 * 
 * @author xu
 * 
 */
public class HttpUtilKeyVal {
	/** 日志 */
	private static Logger log = LoggerFactory.getLogger(HttpUtilKeyVal.class);

	private HttpUtilKeyVal() {
	}

	/** 因http请求容易阻塞，获取请求工具类时使用不同实例操作 */
	public static HttpUtilKeyVal getInstance() {
		return new HttpUtilKeyVal();
	}

	@SuppressWarnings("deprecation")
	public static String doPost(String url, Map<String, String> params) {
		// 构建请求参数
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			boolean isFirst = false;
			for (Entry<String, String> e : params.entrySet()) {
				if (isFirst) {
					sb.append("&");
				}
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				if (!isFirst) {
					isFirst = true;
				}
			}
		}

		// 使用POST方法
		String reciveStr = null;
		PostMethod postMethod = null;
		try {
			HttpClient httpClient = new HttpClient();
			postMethod = new PostMethod(url);
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(80000);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(80000);
			postMethod.setRequestHeader("Connection", "close");
			postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			postMethod.getParams().setContentCharset("utf-8");
			postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			// 填入各个表单域的值
			NameValuePair[] dataList = null;
			if (params != null) {
				dataList = new NameValuePair[params.keySet().size()];
				int i = 0;
				for (Entry<String, String> e : params.entrySet()) {
					dataList[i] = new NameValuePair(e.getKey(), e.getValue());
					i++;
				}
				postMethod.setRequestBody(dataList);
			} else {
				postMethod.setRequestBody("");
			}

			 httpClient.executeMethod(postMethod);

			// 读取返回数据
			InputStream resStream = postMethod.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(resStream));
			String retStr = "";
			String tempbf;
			while ((tempbf = br.readLine()) != null) {
				retStr += tempbf;
			}
			return retStr;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
		return reciveStr;
	}
}
