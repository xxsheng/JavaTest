package javautils.encrypt;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;

public class PasswordUtil {
	private static HashSet<String> SIMPLE_DBPASSWORS = new HashSet<>();

	private final static String[] SIMPLE_DIGITS = { "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	static {
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin1"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin12"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin123"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin1234"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin12345"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin123456"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin1234567"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin12345678"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin123456789"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin1234567890"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("admin0123456789"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("12"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("123"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("1234"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("12345"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("123456"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("1234567"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("12345678"));
		SIMPLE_DBPASSWORS.add(generatePasswordByPlainString("123456789"));

		for (int i = 0; i < 10; i++) {
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(i+"", 6)));
		}

		// 简单密码
		for (String simpleDigit : SIMPLE_DIGITS) {
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 6)));

			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(simpleDigit + "12345"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(simpleDigit + "123456"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(simpleDigit + "1234567"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(simpleDigit + "12345678"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(simpleDigit + "123456789"));

			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 2) + "1234"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 2)  + "12345"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 2)  + "123456"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 2)  + "1234567"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 2)  + "12345678"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 2)  + "123456789"));

			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 3)  + "123"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 3)  + "1234"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 3)  + "12345"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 3)  + "123456"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 3)  + "1234567"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 3)  + "12345678"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 3)  + "123456789"));

			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 4) + "12"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 4) + "123"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 4) + "1234"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 4) + "12345"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 4) + "123456"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 4) + "1234567"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 4) + "12345678"));
			SIMPLE_DBPASSWORS.add(generatePasswordByPlainString(StringUtils.repeat(simpleDigit, 4) + "123456789"));
		}
	}

	public static boolean isSimplePasswordForGenerate(String password) {
		String dbPassword = generatePasswordByMD5(password);
		return isSimplePassword(dbPassword);
	}

	public static boolean isSimplePassword(String dbPassword) {
		return SIMPLE_DBPASSWORS.contains(dbPassword);
	}

	/**
	 * 把给定的MD5字符串加密成密码
	 * @param md5String 密码前台传输时的加密方式：MD5大写(MD5大写(密码明文),调用$.generatePassword(plainStr)即可
	 * @return
	 */
	public static String generatePasswordByMD5(String md5String) {
		return MD5Util.getMD5Code(md5String).toUpperCase();
	}
	/**
	 * 把明文字符串加密成数据库密码，直接存入数据库即可
	 * @param plainString 明文字符串
	 * @return
	 */
	public static String generatePasswordByPlainString(String plainString) {
		String password = MD5Util.getMD5Code(plainString).toUpperCase(); // 第一次加密，并转换成大写

		password = MD5Util.getMD5Code(password).toUpperCase(); // 第二次加密，并转换成大写

		password = MD5Util.getMD5Code(password).toUpperCase(); // 第三次加密，并转换成大写

		return password;
	}


	/**
	 * 验证输入的密码是否正确
	 * @param dbPassword
	 *            数据库密码
	 * @param token
	 * 			加密token
	 * @param md5String
	 *            密码前台传输时的加密方式：MD5大写(MD5大写(MD5大写(MD5大写(密码明文))) + token),调用$.encryptPasswordWithToken(plainStr, token)
	 * @return 验证结果，TRUE:正确 FALSE:错误
	 */
	public static boolean validatePassword(String dbPassword, String token, String md5String) {
		String dbPasswordWithToken = MD5Util.getMD5Code(dbPassword + token).toUpperCase();

		return md5String.equals(dbPasswordWithToken);
	}

	/**
	 * 验证是否是一样的密码
	 * @param dbPassword 数据库密码
	 * @param md5String 密码前台传输时的加密方式：MD5大写(MD5大写(密码明文),调用$.encryptPasswordWithToken(plainStr, token)
	 * @return
	 */
	public static boolean validateSamePassword(String dbPassword, String md5String) {
		String inputPassword = generatePasswordByMD5(md5String);

		return dbPassword.equals(inputPassword);
	}

	//生成密码
	public static void main(String[] args) {
		String password = "123456";
		password = generatePasswordByPlainString(password);
		System.out.println(password);
//		System.out.println(MD5Util.getMD5Code(password + "30561FB071BDF92EC47C7D74262325A5").toUpperCase());
//
//		password = "a1234567";
//		password = generatePasswordByPlainString(password);
//		System.out.println(password);
	}
}