package javautils.encrypt;

import javautils.date.Moment;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Created by Nick on 2017-06-28.
 */
public class TokenUtil {
    /**
     * 生成一次性使用的token
     * @return
     */
    public static String generateDisposableToken() {
        String tokenStr = new Moment().format("yyyyMMddHHmmss") + RandomStringUtils.random(8, true, true);
        tokenStr = DigestUtils.md5Hex(tokenStr).toUpperCase();
        return tokenStr;
    }
}
