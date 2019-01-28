package lottery.domains.content.payment.RX.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author chenlong
 * @date 2015-4-11
 * @time 上午10:40:38
 * @project epay-usercenter
 * @file HttpClientUtil.java
 */
public class HttpClientUtil {

	/**
	 * Post请求
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String post(String url, Map<String, String> params){
		CloseableHttpClient httpclient = null;
		try {
			//HttpPost连接对象          
			HttpPost httpRequest = new HttpPost(url);
			
			//使用NameValuePair来保存要传递的Post参数         
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();          
	
			//添加要传递的参数
			for(String key : params.keySet()){
				nameValuePairs.add(new BasicNameValuePair(key, params.get(key))); 
			}
	
			//设置字符集             
			HttpEntity httpentity = null;
			httpentity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
			
			//设置请求和传输超时
			RequestConfig config = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000).build();
			httpRequest.setConfig(config);
	
			//请求httpRequest 
			httpRequest.setEntity(httpentity);
			
			//取得默认的HttpClient              
			httpclient = HttpClients.createDefault();
	
			//取得HttpResponse             
			HttpResponse httpResponse;
			httpResponse = httpclient.execute(httpRequest);
	
			//HttpStatus.SC_OK表示连接成功              
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				return EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			} 
		} catch (Exception e) {
			return null;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * Post请求
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String httpsPostJSON(String url, JSONObject json) throws Exception {
		
		//HttpPost连接对象          
		HttpPost httpRequest = new HttpPost(url);
		httpRequest.addHeader("X-tenantId", "single");
		StringEntity se = new StringEntity(json.toString(), "UTF-8");   // 中文乱码在此解决
		se.setContentType("application/json");
		httpRequest.setEntity(se);

		

		//取得默认的HttpClient              
		CloseableHttpClient httpclient = HttpClients.createDefault();        

		//取得HttpResponse             
		HttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(httpRequest);

			//HttpStatus.SC_OK表示连接成功              
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				return EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			} 
		} catch (Exception e) {
			return null;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * Post请求
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String postJSON(String url, JSONObject json) throws Exception {
		
		//HttpPost连接对象          
		HttpPost httpRequest = new HttpPost(url);
		httpRequest.addHeader("X-tenantId", "single");
		StringEntity se = new StringEntity(json.toString(), "UTF-8");   // 中文乱码在此解决
		se.setContentType("application/json");
		httpRequest.setEntity(se);

		

		//取得默认的HttpClient              
		CloseableHttpClient httpclient = HttpClients.createDefault();        

		//取得HttpResponse             
		HttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(httpRequest);

			//HttpStatus.SC_OK表示连接成功              
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				return EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			} 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static String get(String url, Map<String, String> params) throws Exception{

		//使用NameValuePair来保存要传递的Post参数         
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();          

		//添加要传递的参数
		for(String key : params.keySet()){
			nameValuePairs.add(new BasicNameValuePair(key, params.get(key))); 
		}
		
		//对参数编码 
		String param = URLEncodedUtils.format(nameValuePairs, "UTF-8");
		
		//将URL与参数拼接 
		HttpGet httpRequest = new HttpGet(url + "?" + param); 
		
		System.out.println(url + "?" + param);

		//取得默认的HttpClient              
		CloseableHttpClient httpclient = HttpClients.createDefault();        

		//取得HttpResponse             
		HttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(httpRequest);

			//HttpStatus.SC_OK表示连接成功              
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				return EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			} 
		} catch (Exception e) {
			return null;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	/**
	 * Post请求
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String omsPostJSON(String url, JSONObject json) throws Exception {
		
		//HttpPost连接对象          
		HttpPost httpRequest = new HttpPost(url);
		
		StringEntity se = new StringEntity(json.toString(), "UTF-8");   // 中文乱码在此解决
		se.setContentType("application/json");
		httpRequest.setEntity(se);

		httpRequest.setHeader("Content-Type", "application/json");
		httpRequest.setHeader("Accept","application/json");
		httpRequest.setHeader("X-tenantId","single");
		
		//取得默认的HttpClient              
		CloseableHttpClient httpclient = HttpClients.createDefault();        

		//取得HttpResponse             
		HttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(httpRequest);

			//HttpStatus.SC_OK表示连接成功              
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				return EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			} 
		} catch (Exception e) {
			return null;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	/**
	 * Post请求
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String postForm(String url, JSONObject json) throws Exception {
		
		//HttpPost连接对象          
		HttpPost httpRequest = new HttpPost(url);
		
		StringEntity se = new StringEntity(json.toString(), "UTF-8");   // 中文乱码在此解决
		se.setContentType("application/x-www-form-urlencoded");
		httpRequest.setEntity(se);

		
		//取得默认的HttpClient              
		CloseableHttpClient httpclient = HttpClients.createDefault();        

		//取得HttpResponse             
		HttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(httpRequest);

			//HttpStatus.SC_OK表示连接成功              
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				return EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			} 
		} catch (Exception e) {
			return null;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	 public static String sendHttpPostRequest(String reqURL, String data) {
		 	CloseableHttpClient httpclient =  HttpClients.createDefault();
	        String respStr = "";
	        try {
	            HttpPost httppost = new HttpPost(reqURL);
	            StringEntity strEntity = new StringEntity(data, "UTF-8");
	            strEntity.setContentType("application/x-www-form-urlencoded");
	            httppost.setEntity(strEntity);

	            //设置请求和传输超时
				RequestConfig config = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
				httppost.setConfig(config);

	            HttpResponse response = httpclient.execute(httppost);
	            int status = response.getStatusLine().getStatusCode();
	            if (status == HttpStatus.SC_OK) {
					HttpEntity resEntity = response.getEntity();
					if (resEntity != null) {
						respStr = EntityUtils.toString(resEntity);
						EntityUtils.consume(resEntity);
					}
				} 

	        } catch (Exception e) {
	        	e.printStackTrace();
	        	return null;
	        }finally {
	        	try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        return respStr;
	    }
	 
}
