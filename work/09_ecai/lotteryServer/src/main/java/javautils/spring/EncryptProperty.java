package javautils.spring;

import com.alibaba.druid.filter.config.ConfigTools;
import javautils.encrypt.DESUtil;

/**
 * Created by Nick on 2016/10/3.
 */
public class EncryptProperty {
    public static void main(String[] args) throws Exception {
        DESUtil instance = DESUtil.getInstance();
        String key = "#$ddw134R$#G#DSFW@#?!@#!@#$CCCREW1";
        // // String key = "$*JUjidjn&0jjfikla1knnkalii097hfk1lf8nGi8el";
        String value = "sunjiang";
        // value = DigestUtils.md5Hex(value).toUpperCase();
        // value = DigestUtils.md5Hex(value).toUpperCase();
        // System.out.println(value);
        // // String value = "asdf";

        String encryptStr = instance.encryptStr(value, key);
        String decryptStr = instance.decryptStr(encryptStr, key);

        System.out.println(encryptStr);
        System.out.println(decryptStr);


         ConfigTools.main(new String[]{value});
    }
}
