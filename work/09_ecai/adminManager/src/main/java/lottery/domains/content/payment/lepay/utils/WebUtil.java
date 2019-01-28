package lottery.domains.content.payment.lepay.utils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

public abstract class WebUtil {
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";

	public static String doPost(String url, Map<String, String> params, int connectTimeout, int readTimeout) throws Exception {
		return doPost(url, params, "UTF-8", connectTimeout, readTimeout);
	}

	public static String doPost(String url, Map<String, String> params, String charset, int connectTimeout, int readTimeout) throws Exception {
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		String query = buildQuery(params, charset);
		byte[] content = new byte[0];
		if (query != null) {
			content = query.getBytes(charset);
		}
		return doPost(url, ctype, content, connectTimeout, readTimeout);
	}

	public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout) throws IOException {
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				conn = getConnection(new URL(url), "POST", ctype);
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (IOException e) {
				throw e;
			}
			try {
				out = conn.getOutputStream();
				out.write(content);
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				throw e;
			}
		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	public static String doGet(String url, String charset) throws IOException {
		HttpURLConnection conn = null;
		String rsp = null;
		try {
			String ctype = "application/x-www-form-urlencoded;charset=" + charset;
			try {
				conn = getConnection(new URL(url), "GET", ctype);
			} catch (IOException e) {
				throw e;
			}
			try {
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				throw e;
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static HttpURLConnection getConnection(URL url, String method, String ctype) throws IOException {
		HttpURLConnection conn = null;
		if ("https".equals(url.getProtocol())) {
			SSLContext ctx = null;
			try {
				ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
			} catch (Exception e) {
				throw new IOException(e);
			}
			HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
			connHttps.setSSLSocketFactory(ctx.getSocketFactory());
			connHttps.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return false;
				}
			});
			conn = connHttps;
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}

		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html,application/json");
		conn.setRequestProperty("User-Agent", "httpclient");
		conn.setRequestProperty("Content-Type", ctype);
		return conn;
	}

	public static String buildQuery(Map<String, String> params, String charset) throws Exception {
		if ((params == null) || (params.isEmpty())) {
			return null;
		}

		StringBuilder query = new StringBuilder();
		boolean hasParam = false;
		for (Entry entry : params.entrySet()) {
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();

			if (StringUtil.areNotEmpty(new String[] { name, value })) {
				if (hasParam)
					query.append("&");
				else {
					hasParam = true;
				}
				query.append(name).append("=").append(value);
			}
		}
		return query.toString();
	}

	public static String buildAlphabeticalSortedQuery(Map<String, String> params) throws Exception {
		if ((params == null) || (params.isEmpty())) {
			return null;
		}

		StringBuilder query = new StringBuilder();
		List<String> keys = new ArrayList(params.keySet());
		Collections.sort(keys);
		boolean hasParam = false;

		for (String name : keys) {
			String value = (String) params.get(name);

			if (StringUtil.areNotEmpty(new String[] { name, value })) {
				if (hasParam)
					query.append("&");
				else {
					hasParam = true;
				}
				query.append(name).append("=").append(value);
			}
		}
		return query.toString();
	}
	
	public static String getURL(Map<String, String> paramsMap) {
		String url = null;
		if ((paramsMap != null) && (paramsMap.size() > 0)) {
			Iterator it = paramsMap.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = paramsMap.get(key);
				if (url == null) {
					url = key + "=" + value;
				} else {
					url = url + "&" + key + "=" + value;
				}
			}
		}
		return url;
	}

	public static String buildRequestStr(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return null;
		}
		StringBuilder query = new StringBuilder();
		List<String> keys = new ArrayList(params.keySet());
		Collections.sort(keys);
		boolean hasParam = false;
		for (String name : keys) {
			String value = params.get(name);
			if (StringUtil.areNotEmpty(new String[] { name, value })) {
				if (hasParam) {
					query.append("&");
				} else {
					hasParam = true;
				}
				query.append(name).append("=").append(value);
			}
		}
		return query.toString();
	}

	protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			return getStreamAsString(conn.getInputStream(), charset);
		}
		String msg = getStreamAsString(es, charset);
		if (StringUtil.isEmpty(msg)) {
			throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
		}
		throw new IOException(msg);
	}

	private static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
			StringWriter writer = new StringWriter();

			char[] chars = new char[256];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null)
				stream.close();
		}
	}

	private static String getResponseCharset(String ctype) {
		String charset = "UTF-8";

		if (!StringUtil.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if ((pair.length != 2) || (StringUtil.isEmpty(pair[1])))
						break;
					charset = pair[1].trim();
					break;
				}

			}

		}

		return charset;
	}

	public static String decode(String value) {
		return decode(value, "UTF-8");
	}

	public static String encode(String value) {
		return encode(value, "UTF-8");
	}

	public static String decode(String value, String charset) {
		String result = null;
		if (!StringUtil.isEmpty(value)) {
			try {
				result = URLDecoder.decode(value, charset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	public static String encode(String value, String charset) {
		String result = null;
		if (!StringUtil.isEmpty(value)) {
			try {
				result = URLEncoder.encode(value, charset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	public static Map<String, String> splitUrlQuery(String query) {
		Map result = new HashMap();

		String[] pairs = query.split("&");
		if ((pairs != null) && (pairs.length > 0)) {
			for (String pair : pairs) {
				String[] param = pair.split("=", 2);
				if ((param != null) && (param.length == 2)) {
					result.put(param[0], param[1]);
				}
			}
		}

		return result;
	}

	private static class DefaultTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	}
}