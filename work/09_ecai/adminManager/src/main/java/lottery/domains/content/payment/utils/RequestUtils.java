package lottery.domains.content.payment.utils;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

public class RequestUtils {
	
	public static String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		try {
			byte[] b = new byte[4096];
			int n;
			while ((n = in.read(b)) != -1) {
				out.append(new String(b, 0, n));
			}

			return out.toString();
		} catch (IOException ex) {
			throw ex;
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
}
