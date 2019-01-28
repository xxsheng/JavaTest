package lottery.domains.pool.payment.fkt;



import lottery.domains.pool.payment.pay41.utils.AppConstants;
import lottery.domains.pool.payment.pay41.utils.MD5Encoder;
import lottery.domains.pool.payment.pay41.utils.StringUtils;
import lottery.domains.pool.payment.pay41.utils.URLUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by thinkpad on 2015/3/28.
 */
public class KeyValues {

    private List<KeyValue> keyValues = new LinkedList<KeyValue>();

    public List<KeyValue> items()
    {
        return keyValues;
    }

    public void add(KeyValue kv)
    {
        if (kv != null && !StringUtils.isNullOrEmpty(kv.getVal()))
            keyValues.add(kv);
    }

    public String sign(String key, String inputCharset)
    {
        StringBuilder sb = new StringBuilder();
        Collections.sort(keyValues, new Comparator<KeyValue>(){
            public int compare(KeyValue l, KeyValue r) {
                return  l.compare(r);
            }
        });
        for (KeyValue kv : keyValues)
        {
            URLUtils.appendParam(sb, kv.getKey(), kv.getVal());
        }
        URLUtils.appendParam(sb, AppConstants.KEY, key);
        String s = sb.toString();
        s = s.substring(1, s.length());
        System.out.println(s);
      //  return MD5Encoder.encode(s, inputCharset);
        return DigestUtils.md5Hex(s);
    }

    public String getSortUrl(String inputCharset)
    {
        StringBuilder sb = new StringBuilder();
        Collections.sort(keyValues, new Comparator<KeyValue>(){
            public int compare(KeyValue l, KeyValue r) {
                return  l.compare(r);
            }
        });
        for (KeyValue kv : keyValues)
        {
            if(!kv.getKey().equals("notify_url")){
                URLUtils.appendParam(sb, kv.getKey(), kv.getVal());
            }

        }
        String s = sb.toString();
        s = s.substring(1, s.length());
        return s;
    }
}
