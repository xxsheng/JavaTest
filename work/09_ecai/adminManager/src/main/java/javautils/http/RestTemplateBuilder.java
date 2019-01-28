package javautils.http;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * rest template构建器
 * @author Nick
 * @since 2015-10-2
 */
public class RestTemplateBuilder {
	private static final RestTemplateBuilder INSTANCE = new RestTemplateBuilder();
	private static final int DEFAULT_TIMEOUT = 3 * 1000; // 默认3秒超时
	private static final long DEFAULT_KEEPALIVE_DURATION = 60 * 1000; // 默认1分钟超时
	private static final int DEFAULT_RETRYCOUNT = 3; // 默认重试3次
	private static final int DEFAULT_MAX_TOTAL = 100;
	private static final int DEFAULT_MAX_PERROUTE = 50;
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private int timeout;
	private long keepaliveDuration;
	private int retrycount;
	private int maxTotal;
	private int maxPerroute;
	private String sslFile;
	private String sslFilePwd;

	public RestTemplateBuilder() {
		this.timeout = DEFAULT_TIMEOUT;
		this.keepaliveDuration = DEFAULT_KEEPALIVE_DURATION;
		this.retrycount = DEFAULT_RETRYCOUNT;
		this.maxTotal = DEFAULT_MAX_TOTAL;
		this.maxPerroute = DEFAULT_MAX_PERROUTE;
	}

	public RestTemplate build() {
		
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		
		if (this.retrycount > 0) {
			HttpRequestRetryHandler retryHandler = HttpConfigBuilder.getInstance().buildRetryHandler(this.retrycount);
			clientBuilder.setRetryHandler(retryHandler);
		}
		
		if (this.keepaliveDuration > 0) {
			ConnectionKeepAliveStrategy keepAliveStrategy = HttpConfigBuilder.getInstance().buildKeepAliveStrategy(this.keepaliveDuration);
			clientBuilder.setKeepAliveStrategy(keepAliveStrategy);
		}
		
		if (this.timeout > 0) {
			RequestConfig requestConfig = HttpConfigBuilder.getInstance().buildRequestConfig(this.timeout);
			clientBuilder.setDefaultRequestConfig(requestConfig);
		}
		
		if (this.maxTotal > 0 && this.maxPerroute > 0) {

			PoolingHttpClientConnectionManager connMgr;
			if (StringUtils.isNotEmpty(this.sslFile) && StringUtils.isNotEmpty(this.sslFilePwd)) {
				String path = RestTemplateBuilder.class.getResource("/").getPath();
				File file = new File(path + this.sslFile);
				connMgr = HttpConfigBuilder.getInstance().buildP12PoolingHttpClientConnectionManager(maxTotal, this.maxPerroute, file, this.sslFilePwd);
			}
			else {
				connMgr = HttpConfigBuilder.getInstance().buildPoolingHttpClientConnectionManager(maxTotal, this.maxPerroute);
			}
			clientBuilder.setConnectionManager(connMgr);
		}
		
		// if (StringUtils.isNotEmpty(proxyHost) && proxyPort != null
		// 		&& StringUtils.isNotEmpty(proxySchema)) {
		// 	HttpHost proxy = new HttpHost(proxyHost, proxyPort, proxySchema);
		// 	clientBuilder.setProxy(proxy);
		// }

		CloseableHttpClient httpClient = clientBuilder.build();
		
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.setMessageConverters(getMessageConverters());
		return restTemplate;
	}
	
	private List<HttpMessageConverter<?>> getMessageConverters() {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(DEFAULT_CHARSET);
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		
		MediaType jsonMediaType = MediaType.valueOf("application/json;charset=UTF-8");
		MediaType textMediaType = MediaType.valueOf("text/html;charset=UTF-8"); // 避免IE出现下载JSON文件的情况
		List<MediaType> supportedMediaTypes = Arrays.asList(jsonMediaType, textMediaType);
		
		fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
		
		List<HttpMessageConverter<?>> httpMessageConverters  = new ArrayList<>();
		httpMessageConverters.add(stringHttpMessageConverter);
		httpMessageConverters.add(fastJsonHttpMessageConverter);
		return httpMessageConverters;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public long getKeepaliveDuration() {
		return keepaliveDuration;
	}

	public void setKeepaliveDuration(int keepaliveDuration) {
		this.keepaliveDuration = keepaliveDuration;
	}

	public int getRetrycount() {
		return retrycount;
	}

	public void setRetrycount(int retrycount) {
		this.retrycount = retrycount;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxPerroute() {
		return maxPerroute;
	}

	public void setMaxPerroute(int maxPerroute) {
		this.maxPerroute = maxPerroute;
	}

	public String getSslFile() {
		return sslFile;
	}

	public void setSslFile(String sslFile) {
		this.sslFile = sslFile;
	}

	public String getSslFilePwd() {
		return sslFilePwd;
	}

	public void setSslFilePwd(String sslFilePwd) {
		this.sslFilePwd = sslFilePwd;
	}
}
