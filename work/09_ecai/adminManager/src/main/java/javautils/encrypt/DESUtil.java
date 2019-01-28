package javautils.encrypt;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;

/**
 * DES加密工具类
 */
public class DESUtil {

	private DESUtil() {
		
	}
	
	public static DESUtil getInstance() {
		return new DESUtil();
	}

	private Key getKey(String strKey) {
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			DESKeySpec keySpec = new DESKeySpec(strKey.getBytes());
			keyFactory.generateSecret(keySpec);
			return keyFactory.generateSecret(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String encryptStr(String strMing, String keyStr) {
		try {
			byte[] byteMing = strMing.getBytes();
			byte[] byteMi = encryptByte(byteMing, getKey(keyStr));
			return new String(Base64.encodeBase64(byteMi));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String decryptStr(String strMi, String keyStr) {
		try {
			byte[] byteMi = Base64.decodeBase64(strMi.getBytes());
			byte[] byteMing = decryptByte(byteMi, getKey(keyStr));
			String strMing = new String(byteMing);
			return strMing;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] encryptByte(byte[] byteS, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(byteS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] decryptByte(byte[] byteD, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(byteD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static void main(String[] args) {
		try {
			String str = "2564456666aasdfasdf";
			String key = "asd$65699))*i-23";


			String encrypt = DESUtil.getInstance().encryptStr(str, key);
			System.out.println(encrypt);
			encrypt = URLEncodeUtil.encode(encrypt, "UTF-8");
			System.out.println(encrypt);

			encrypt = URLEncodeUtil.decode(encrypt, "UTF-8");

			System.out.println(encrypt);

			String decrypt = DESUtil.getInstance().decryptStr(encrypt, key);
			System.out.println(decrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}