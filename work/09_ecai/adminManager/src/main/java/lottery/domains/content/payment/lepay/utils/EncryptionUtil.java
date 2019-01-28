package lottery.domains.content.payment.lepay.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class EncryptionUtil extends DigestUtils {
	private static String encodingCharset = "UTF-8";
	private final static Integer PHONE_ENCRYPT_KEY = 175082; 

	@SuppressWarnings("unused")
	private static String bytesToHex(byte b[]) {
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < b.length; j++) {
			buf.append(hexDigit[b[j] >> 4 & 0xf]);
			buf.append(hexDigit[b[j] & 0xf]);
		}
		return buf.toString();
	}

	public static String decryptBASE64pt(String key) {
		return Base64.encodeBase64URLSafeString(key.getBytes());
	}

	public static byte[] decryptBASE64(String key) throws Exception {
		return Base64.decodeBase64(key.getBytes());
	}

	public static String digest(String aValue) {
		aValue = aValue.trim();
		byte value[];
		try {
			value = aValue.getBytes(encodingCharset);
		} catch (UnsupportedEncodingException e) {
			value = aValue.getBytes();
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return toHex(md.digest(value));
	}

	public static String encrypt(String originalString) {
		return encryptThis(originalString).toLowerCase();
	}

	public static String encryptPassword(String pwd) {
		return StringUtils.lowerCase(encrypt(pwd));
	}

	public static void main(String[] args) {
		try {
			System.out.println(URLEncoder.encode(encryptBASE64("pboleba14".getBytes())));

			System.out.println(decryptBASE64("cGJvbGViYTE0"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static String encryptBASE64(byte key[]) throws Exception {
		return new String(Base64.encodeBase64(key));
	}

	public static String encryptThis(String originalString) {
		return DigestUtils.md5Hex(originalString);
	}

	public static String getHmac(String args[], String key) {
		if (args == null || args.length == 0)
			return null;
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < args.length; i++)
			str.append(args[i]);

		return hmacEncrypt(str.toString(), key);
	}

	public static String hmacEncrypt(String aValue, String aKey) {
		byte k_ipad[] = new byte[64];
		byte k_opad[] = new byte[64];
		byte keyb[];
		byte value[];
		try {
			keyb = aKey.getBytes(encodingCharset);
			value = aValue.getBytes(encodingCharset);
		} catch (UnsupportedEncodingException e) {
			keyb = aKey.getBytes();
			value = aValue.getBytes();
		}
		Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
		Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
		for (int i = 0; i < keyb.length; i++) {
			k_ipad[i] = (byte) (keyb[i] ^ 0x36);
			k_opad[i] = (byte) (keyb[i] ^ 0x5c);
		}

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		md.update(k_ipad);
		md.update(value);
		byte dg[] = md.digest();
		md.reset();
		md.update(k_opad);
		md.update(dg, 0, 16);
		dg = md.digest();
		return toHex(dg);
	}

	public static String md5Encrypt(String originalString) {
		return encryptThis(originalString);
	}

	public static String toHex(byte input[]) {
		if (input == null)
			return null;
		StringBuffer output = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			int current = input[i] & 0xff;
			if (current < 16)
				output.append("0");
			output.append(Integer.toString(current, 16));
		}

		return output.toString();
	}

	public static String generateMD5(String plainText) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(plainText.getBytes(Charset.forName("UTF8")));
			final byte[] resultByte = messageDigest.digest();
			return new String(Hex.encodeHex(resultByte));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public EncryptionUtil() {
	}

}
