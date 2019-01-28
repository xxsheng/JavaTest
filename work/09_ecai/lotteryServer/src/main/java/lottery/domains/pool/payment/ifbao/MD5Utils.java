package lottery.domains.pool.payment.ifbao;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {
	public static String generate(String text) {
        return DigestUtils.md5Hex(getContentBytes(text, "UTF-8"));
    }
	
	private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }

        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5 ERROR:" + charset);
        }
    }
}
