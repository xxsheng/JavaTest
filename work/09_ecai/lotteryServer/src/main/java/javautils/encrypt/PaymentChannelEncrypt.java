package javautils.encrypt;

public class PaymentChannelEncrypt {
    public static void main(String[] args) {
        String encrypt = encrypt("http:127.0.0.1:12");
        String decrypt = decrypt(encrypt);
        System.out.println(encrypt);
        System.out.println(decrypt("cQ154u+Dtqqw6mkvSE/aO6qC+TykR5LR"));
    }

    private static final DESUtil DES = DESUtil.getInstance();
    private static final String DES_KEY = "#f$ddw4aFF2Wfgaewdff#GR0(DSFW@#?!@23#!@#a";

    public static String encrypt(String str) {
        try {
            return DES.encryptStr(str, DES_KEY); 
        } catch (Exception e) {
            System.out.println("加密充值通道时出错");
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String str) {
        try {
            return DES.decryptStr(str, DES_KEY);
        } catch (Exception e) {
            System.out.println("解密充值通道时出错");
            e.printStackTrace();
            return null;
        }
    }
}