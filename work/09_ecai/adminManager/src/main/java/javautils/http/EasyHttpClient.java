package javautils.http;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.SocketTimeoutException;

public class EasyHttpClient {

	private static final Logger logger = LoggerFactory.getLogger(EasyHttpClient.class);
	
	private HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
	private final int TIMEOUT = 10 * 1000;
	private final int SO_TIMEOUT = 10 * 1000;
	private int REPEAT_TIMES = 3;
	
	public EasyHttpClient() {
		setTimeOut(TIMEOUT, SO_TIMEOUT);
	}
	
	/**
	 * 设置超时时间
	 * @param TIMEOUT
	 * @param SO_TIMEOUT
	 */
	public void setTimeOut(int TIMEOUT, int SO_TIMEOUT) {
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(SO_TIMEOUT);
	}
	
	/**
	 * 设置请求重复次数
	 * @param times
	 */
	public void setRepeatTimes(int times) {
		this.REPEAT_TIMES = times;
	}
	
	/**
	 * GET请求
	 * @param url
	 * @return
	 */
	public String get(String url) {
		GetMethod get = new GetMethod(url);
		for (int i = 0; i < REPEAT_TIMES; i++) {
			logger.debug("正在发送请求，当前第" + (i + 1) + "次...");
			try {
				int state = httpClient.executeMethod(get);
				logger.debug("请求状态" + state);
				if(state == HttpStatus.SC_OK) {
					String data = fromInputStream(get.getResponseBodyAsStream());
					logger.debug("成功获取到数据，长度为" + data.length());
					get.releaseConnection();
					return data;
				}
			} catch(ConnectTimeoutException e) {
				logger.error("请求超时...Connect Timeout");
			} catch(SocketTimeoutException e) {
				logger.error("请求超时...Socket Timeout");
			} catch (Exception e) {
				logger.error("请求出错...", e);
			} finally {
				get.releaseConnection();
			}
		}
		return null;
	}
	
	/**
	 * POST请求
	 * @param url
	 * @param params
	 * @return
	 */
	public String post(String url, NameValuePair[] params) {
		PostMethod post = new PostMethod(url);
		if(params != null) {
			post.setRequestBody(params);
		}
		for (int i = 0; i < REPEAT_TIMES; i++) {
			logger.debug("正在发送请求，当前第" + (i + 1) + "次...");
			try {
				int state = httpClient.executeMethod(post);
				logger.debug("请求状态" + state);
				if(state == HttpStatus.SC_OK) {
					String data = fromInputStream(post.getResponseBodyAsStream());
					logger.debug("成功获取到数据，长度为" + data.length());
					post.releaseConnection();
					return data;
				}
			} catch(ConnectTimeoutException e) {
				logger.error("请求超时...Connect Timeout");
			} catch(SocketTimeoutException e) {
				logger.error("请求超时...Socket Timeout");
			} catch (Exception e) {
				logger.error("请求出错...", e);
			} finally {
				post.releaseConnection();
			}
		}
		return null;
	}
	
	/**
	 * InputStream转String
	 * @param inputStream
	 * @return
	 */
	public static String fromInputStream(InputStream inputStream) {
		try {
			StringBuffer sb = new StringBuffer();
			byte[] bytes = new byte[1024];
			int len;
			while ((len = inputStream.read(bytes)) != -1) {
				sb.append(new String(bytes, 0, len));
			}
			inputStream.close();
			return sb.toString();
		} catch (Exception e) {}
		return null;
	}
	
}