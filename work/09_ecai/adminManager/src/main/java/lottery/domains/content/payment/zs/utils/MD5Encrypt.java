/**
 * MD5加密算法
 */
package lottery.domains.content.payment.zs.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>Title: MD5加密算法</p>
 * <p>Description: 商户不需要进行修改</p>
 * <p>西安银博科技发展公司 2009. All rights reserved.</p>
 */
public class MD5Encrypt {

	private static final String ENCODE = "UTF-8";

	/**
	 * 功能：MD5加密
	 * @param strSrc 加密的源字符串
	 * @return 加密串 长度32位(hex串)
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */
	public static String getMessageDigest(String strSrc) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = null;
		String strDes = null;
		final String ALGO_MD5 = "MD5";

		byte[] bt = strSrc.getBytes(ENCODE);
		md = MessageDigest.getInstance(ALGO_MD5);
		md.update(bt);
		strDes = StringUtil.byte2hex(md.digest());
		return strDes;
	}

	/**
	 * 将字节数组转为HEX字符串(16进制串)
	 * @param bts 要转换的字节数组
	 * @return 转换后的HEX串
	 */
	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

}
