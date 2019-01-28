// package lottery.domains.pool.payment.rx.util;
//
//
// import java.net.URLDecoder;
// import java.net.URLEncoder;
// import java.security.interfaces.RSAPrivateKey;
// import java.security.interfaces.RSAPublicKey;
// import java.util.Map;
//
// import org.apache.http.util.EntityUtils;
//
// import lottery.domains.pool.payment.cfg.PayConfig;
//
// public class MainTest {
// 	public static void main(String[] args) throws Exception {
// //		Map<String, Object> map=RSAEncrypt.initKey();
// //		// 得到私钥
// //        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("RSAPrivateKey");
// //        // 得到公钥
// //        RSAPublicKey publicKey = (RSAPublicKey) map.get("RSAPublicKey");
// //        // 得到公钥字符串  CoderUtil.decryptBASE64
// //        String publicKeyString = CoderUtil.encryptBASE64(publicKey.getEncoded());
// //        // 得到私钥字符串
// //        String privateKeyString = CoderUtil.encryptBASE64(privateKey.getEncoded());
// //        System.out.println("publicKeyString:"+publicKeyString);
// //        System.out.println("privateKeyString:"+privateKeyString);
//
// //        System.out.println("--------------公钥加密私钥解密过程-------------------");
//         String plainText="i钥加密私钥解密";
//         //公钥加密过程
// //        byte[] cipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(PayConfig.RX.PUBLIC_KEY),plainText.getBytes());
// //        String cipher=Base64Utils.encode(cipherData);
//         String cipher="eyJhbW91bnQiOiI1MDAwLjAwIiwicmVzcENvZGUiOiIwMDAwIiwicmVzcEluZm8iOiJzdWNjZXNzIiwib3JkZXJJZCI6IjE3MTAxMjEzNDIzNHZSRHlKbkdvIiwidHlwZSI6ImtvemwiLCJ1c2VyaWQiOiJERTEzMTMzNzUxNDA3In0=";
//         //私钥解密过程
// //        byte[] res=RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY), cipher.getBytes());
//         byte[] res=RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY), Base64.decode(cipher));
// //          byte[] res=RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY), Base64Utils.decode(cipher));
//         String restr=new String(res);
// //        System.out.println("原文："+plainText);
// //        System.out.println("加密："+cipher);
//         System.out.println("解密："+restr);
// //        System.out.println();
//
// //        System.out.println("--------------私钥加密公钥解密过程-------------------");
// //        plainText="IpsRarHey29CVh+VHsuc6bfT3N1HuW28I0tADBvBAQLSqguaeJ9XUVlGszIQDfC9rSz/QnrSDa+dXvces0lzGGFiLP98m6/QKvpwaPxbusM5CWIhpnvVsWyPh0CgvuzwZ8WLriTARfUDKlj3/EAGWHcMbhiZISdRHAG6avB1sIY/eNtgB65AHmofyYEKriswXuF0gpn14A47nEAHS+Q53nhakCzdGt9YbRJs2xfL/GNs1sU9mPfB2FyeQ86G7Vj2zT/J85ziUFciYncxnEEO7jzUXV1EsvUk0hzQjoxSXRzvqY9IDlMGJftuxziX7NvyzmqjdpTOWwigfP/yrZKpSAVxUSeJk5+l2l4BH7IEtCKLG6gTtGhIy1C4QQk0c7Cc2S61qy0SnLjdnZqQbsNz5HntpCZLerdnSto4DnH67YSk5A2CaO082911wn33e7QpAZS6dEXCQVIzVX3QODYrDSVMZfUFstRUEKC4bEc8b81Y6cS/k1051250zz50xDlqkErycO2A5gRbVdH/7PcS757eUMrjBjd6eF2rOfsguU+AHBKVPaC/MGv5HvDrMBY8kfkiB3W2BDCfCsIyNQhwPb7nA4L2qAM9xmM43XcgvaX+ZJcBOhJ9ESp79gbgg71J9iPrxxZPzVkj525D7G5l3B3MrShSoYvFgQi7CHALKok=";
//         //私钥加密过程
// //        cipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY),plainText.getBytes());
// //        cipher=Base64Utils.encode(cipherData);
//         //公钥解密过程
// //        byte[] res=RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr(PayConfig.RX.PUBLIC_KEY), Base64.decode(cipher));
// //        byte[] res=RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(PayConfig.RX.PRIVATE_KEY), Base64Utils.decode(cipher));
// //        cipher=new String(res);
// //        System.out.println("原文："+plainText);
// //        System.out.println("加密："+cipher);
// //        System.out.println("解密："+cipher);
// //        System.out.println();
//
// //        System.out.println("---------------私钥签名过程------------------");
// //        String content="ihep_这是用于签名的原始数据";
// //        String signstr=RSASignature.sign(plainText,PayConfig.RX.PRIVATE_KEY);
// //        System.out.println("签名原串："+plainText);
// //        System.out.println("签名串："+signstr);
// //        System.out.println();
//
// //        System.out.println("---------------公钥校验签名------------------");
// //        System.out.println("签名原串："+plainText);
// //        System.out.println("签名串："+cipher);
//
// //        System.out.println("验签结果："+RSAEncrypt.publicsign(plainText, Base64Utils.decode(cipher), RSAEncrypt.loadPublicKeyByStr(PayConfig.RX.PUBLIC_KEY)));
// //        System.out.println("验签结果："+RSASignature.doCheck(plainText, cipher, PayConfig.RX.PUBLIC_KEY));
// //        System.out.println();
//
// //        String url="http%3A%2F%2Fportal.hs.com%2FRechargenotify%2F113";
// //        String str = EntityUtils.toString(url, "UTF-8");
// //           System.out.println(URLDecoder.decode(url,"UTF-8"));
//     }
// }
