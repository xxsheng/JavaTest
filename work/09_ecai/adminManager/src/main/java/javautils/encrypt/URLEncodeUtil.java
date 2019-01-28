package javautils.encrypt;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Nick on 2017-07-01.
 */
public class URLEncodeUtil {
    public static String encode(String str, String charset){
        try {
            return URLEncoder.encode(str, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public static String decode(String str, String charset){
        try {
            return URLDecoder.decode(str, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }
}
