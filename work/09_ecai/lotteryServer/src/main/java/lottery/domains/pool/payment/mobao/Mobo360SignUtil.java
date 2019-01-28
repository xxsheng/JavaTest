package lottery.domains.pool.payment.mobao;


import java.security.MessageDigest;
import java.security.Signature;
import java.security.cert.X509Certificate;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Decoder;

public class Mobo360SignUtil {

	private static X509Certificate cert = null;
	private static String signType = Mobo360Config.SIGN_TYPE;
	private static final String CHAR_SET = "UTF-8";
	private static String key = "";

	private Mobo360SignUtil() {

	}

	public static void init(String md5key) throws Exception {
		if ("MD5".equals(signType)) {
			initMD5(md5key);
		}
	}

	/**
	 * MD5签名初始化
	 * 
	 */
	public static void initMD5(String md5key) throws Exception {
		key = md5key;
	}

	/**
	 * 生成签名
	 * 
	 * @param sourceData
	 * @return
	 * @throws Exception
	 */
	public static String signData(String sourceData) throws Exception {
		String signStrintg = "";
		if ("MD5".equals(signType)) {
			signStrintg = signByMD5(sourceData, key);
		}
		signStrintg.replaceAll("\r", "").replaceAll("\n", "");
		return signStrintg;
	}

	/**
	 * 验证签名
	 * 
	 * @param signData
	 *            签名数据
	 * @param srcData
	 *            原数据
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyData(String signData, String srcData)
			throws Exception {
		if ("RSA".equals(signType)) {
			if (null == cert) {
				throw new Exception("签名尚未初始化！");
			}
			if (StringUtils.isBlank(signData)) {
				throw new Exception("系统校验时：签名数据为空！");
			}
			if (StringUtils.isBlank(srcData)) {
				throw new Exception("系统校验时：原数据为空！");
			}
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] b = decoder.decodeBuffer(signData);
			Signature sign = Signature.getInstance("MD5withRSA");
			sign.initVerify(cert);
			sign.update(srcData.getBytes("utf-8"));
			return sign.verify(b);
		} else if ("MD5".equals(signType)) {
			if (signData.equalsIgnoreCase(signByMD5(srcData, key))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static String signByMD5(String sourceData, String key)
			throws Exception {
		String data = sourceData + key;
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] sign = md5.digest(data.getBytes(CHAR_SET));

		return Bytes2HexString(sign).toUpperCase();
	}

	/**
	 * 将byte数组转成十六进制的字符串
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static String Bytes2HexString(byte[] b) {
		StringBuffer ret = new StringBuffer(b.length);
		String hex = "";
		for (int i = 0; i < b.length; i++) {
			hex = Integer.toHexString(b[i] & 0xFF);

			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret.append(hex.toUpperCase());
		}
		return ret.toString();
	}

}