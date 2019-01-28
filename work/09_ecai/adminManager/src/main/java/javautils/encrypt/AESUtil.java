package javautils.encrypt;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESUtil {

    /*
     * 算法/模式/填充 16字节加密后数据长度 不满16字节加密后长度
     * AES/CBC/NoPadding 16 不支持
     * AES/CBC/PKCS5Padding 32 16
     * AES/CBC/ISO10126Padding 32 16
     * AES/CFB/NoPadding 16 原始数据长度
     * AES/CFB/PKCS5Padding 32 16
     * AES/CFB/ISO10126Padding 32 16
     * AES/ECB/NoPadding 16 不支持
     * AES/ECB/PKCS5Padding 32 16
     * AES/ECB/ISO10126Padding 32 16
     * AES/OFB/NoPadding 16 原始数据长度
     * AES/OFB/PKCS5Padding 32 16
     * AES/OFB/ISO10126Padding 32 16
     * AES/PCBC/NoPadding 16 不支持
     * AES/PCBC/PKCS5Padding 32 16
     * AES/PCBC/ISO10126Padding 32 16
     *
     * 注：
     * 1、JCE中AES支持五中模式：CBC，CFB，ECB，OFB，PCBC；支持三种填充：NoPadding，PKCS5Padding，ISO10126Padding。 不带模式和填充来获取AES算法的时候，其默认使用ECB/PKCS5Padding。
     * 2、Java支持的密钥长度：keysize must be equal to 128, 192 or 256
     * 3、Java默认限制使用大于128的密钥加密（解密不受限制），报错信息：java.security.InvalidKeyException: Illegal key size or default parameters
     * 4、下载并安装JCE Policy文件即可突破128密钥长度的限制：覆盖jre\lib\security目录下local_policy.jar、US_export_policy.jar文件即可
     * 5、除ECB外，需提供初始向量（IV），如：Cipher.init(opmode, key, new IvParameterSpec(iv)), 且IV length: must be 16 bytes long
     */

    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 生成随机密钥
     *
     * @return
     * @throws Exception
     */
    public static Key generateKey(int keysize) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(keysize, new SecureRandom());
        Key key = keyGenerator.generateKey();
        return key;
    }

    /**
     * 生成随机密钥
     *
     * @return
     * @throws Exception
     */
    public static Key generateKey() throws Exception {
        return generateKey(128);
    }

    /**
     * 生成固定密钥
     *
     * @param password      作为种子，生成对应的密钥
     * @return
     * @throws Exception
     */
    public static Key generateKey(int keysize, byte[] seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(keysize, new SecureRandom(seed));
        Key key = keyGenerator.generateKey();
        return key;
    }

    /**
     * 生成固定密钥
     *
     * @param password      作为种子，生成对应的密钥
     * @return
     * @throws Exception
     */
    public static Key generateKey(int keysize, String password) throws Exception {
        return generateKey(keysize, password.getBytes());
    }

    /**
     * 生成固定密钥
     *
     * @param password      作为种子，生成对应的密钥
     * @return
     * @throws Exception
     */
    public static Key generateKey(String password) throws Exception {
        return generateKey(128, password);
    }

    /**
     * 执行加密
     *
     * @param content
     * @param key       长度必须为16、24、32位，即128bit、192bit、256bit
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] content, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
        byte[] output = cipher.doFinal(content);
        return output;
    }

    /**
     * 执行加密
     *
     * @param content
     * @param password      作为种子，生成对应的密钥
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] content, String password) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, generateKey(password));
        byte[] output = cipher.doFinal(content);
        return output;
    }

    /**
     * 执行解密
     *
     * @param content
     * @param key       长度必须为16、24、32位，即128bit、192bit、256bit
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] content, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
        byte[] output = cipher.doFinal(content);
        return output;
    }

    /**
     * 执行解密
     *
     * @param content
     * @param password
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] content, String password) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, generateKey(password));
        byte[] output = cipher.doFinal(content);
        return output;
    }

    public static void main(String[] args) throws Exception {

        System.out.println(Arrays.toString(encrypt("当我们把密钥定为大于128时（即192或256）时".getBytes(), "012345")));
        System.out.println(new String(decrypt(encrypt("当我们把密钥定为大于128时（即192或256）时".getBytes(), "012345"), "012345")));

        System.out.println(Arrays.toString(encrypt("当我们把密钥定为大于128时（即192或256）时".getBytes(), "01234567890123450123456789012345".getBytes())));
        System.out.println(new String(decrypt(encrypt("当我们把密钥定为大于128时（即192或256）时".getBytes(), "01234567890123450123456789012345".getBytes()), "01234567890123450123456789012345".getBytes())));

        /*
         * 控制台输出：
         *
         * [-75, 23, -15, -7, -91, 91, -105, 15, 69, -10, -69, 15, 78, 67, -86, -55, 92, 43, -47, -112, -2, 7, -128, -49, -104, -6, 35, -25, -47, -124, -38, -39, 51, -8, -37, 32, 21, -44, -88, -61, -51, -15, -31, 100, 87, -50, -81, -43, -71, 38, -59, -67, -92, 15, 125, 39, -34, 120, -113, -14, -35, -94, -86, 40]
         * 当我们把密钥定为大于128时（即192或256）时
         * [-67, 65, -65, -13, -5, 106, -127, -37, 87, 114, 99, 10, -52, -106, 48, -76, 96, 108, -92, 91, 28, 117, -37, -14, 55, -24, -80, 86, 54, 0, -82, -62, 31, 127, -101, 36, -10, 35, -60, -52, 127, -60, -27, -107, 56, 64, -127, -64, 112, -116, -28, -30, 61, -121, 75, -16, -40, -26, -96, -99, -85, 48, 63, -13]
         * 当我们把密钥定为大于128时（即192或256）时
         */
    }

}