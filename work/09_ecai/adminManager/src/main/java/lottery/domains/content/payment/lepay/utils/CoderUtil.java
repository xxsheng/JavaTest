package lottery.domains.content.payment.lepay.utils;

import org.apache.commons.codec.binary.Base64;

public class CoderUtil {

	// ����
	public static byte[] decryptBASE64(String key) {
		byte[] b = key.getBytes();
		Base64 base64 = new Base64();
		return base64.decode(b);

	}

	// ����
	public static String encryptBASE64(byte[] key) throws Exception {
		Base64 base64 = new Base64();
		byte[] b = base64.encode(key);
		String s = new String(b);
		return s;
	}
}
