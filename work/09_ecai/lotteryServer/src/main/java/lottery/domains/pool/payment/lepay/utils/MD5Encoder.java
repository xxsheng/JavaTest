package lottery.domains.pool.payment.lepay.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

/**
 * Created by thinkpad on 2015/3/28.
 */
public class MD5Encoder {

	public static String md5Encode(String content) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] bytes = content.getBytes("utf-8");
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(bytes);
		return Hex.encodeHexString(messageDigest.digest());
	}

}
