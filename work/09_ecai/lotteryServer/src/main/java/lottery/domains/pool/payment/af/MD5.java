package lottery.domains.pool.payment.af;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import sun.misc.BASE64Encoder;

public class MD5 {

	public

	final static String MD5Encoder(String s, String charset) {

		try

		{

			byte[] btInput = s.getBytes(charset);

			MessageDigest mdInst = MessageDigest.getInstance("MD5");

			mdInst.update(btInput);

			byte[] md = mdInst.digest();

			StringBuffer sb = new

			StringBuffer();

			for

			(int

			i = 0; i < md.length; i++) {

				int

				val = ((int) md[i]) & 0xff;

				if

				(val < 16) {

					sb.append("0");

				}

				sb.append(Integer.toHexString(val));

			}

			return

			sb.toString();

		} catch

		(Exception e) {

			return

			null;

		}

	}

	public static String base64Encoder(final String ss, String charset) {
		BASE64Encoder en = new BASE64Encoder(); // base64编码
		String encStr = "";
		if (charset == null || "".equals(charset)) {
			encStr = en.encode(ss.getBytes());
			return encStr;
		}
		try {
			encStr = en.encode(ss.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return encStr;
	}
	public static final String[] hexDigest = {"0" , "1" , "2" , "3" , "4" , "5" , "6" , "7" , "8" , "9" , "a" , "b" , "c" , "d" , "e" , "f"};
	
	public static String byteArrayToHexString(byte[] b) {

		StringBuffer resultSb = new StringBuffer();
		for(int i = 0 ; i<b.length ; i++) {
		resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();

		}
		private static String byteToHexString(byte b) {
		int n = b;
		if(n < 0) {
		n = 256+n;
		}
		int d1 = n/16;
		int d2 = n%16;
		return hexDigest[d1] + hexDigest[d2];
		}
		

	public static String encodeUtf8(String origin) {
		String resultString = null;

		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes("UTF-8")));
		} catch (Exception ex) {
		}

		return resultString;
	}

}
