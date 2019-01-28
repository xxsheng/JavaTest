package lottery.domains.pool.payment.ty;


import java.security.MessageDigest;

public final class MD5 {
    private static char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    private MD5(){}

    public static String convert(String s){
        return convert(s,hexChars);
    }

    public static String convert(String s,char[] hex){
        try {
            byte[] bytes = s.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            bytes = md.digest();
            int j = bytes.length;
            char[] chars = new char[j * 2];
            int k = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                chars[k++] = hex[b >>> 4 & 0xf];
                chars[k++] = hex[b & 0xf];
            }
            return new String(chars);
        }
        catch (Exception e){
            System.out.println( e.getMessage());
            return null;
        }
    }
    public static void main(String[] args) {
        System.out.println(MD5.convert("1234567890abcdefghijklmnopqrstuvwxyz"));
    }

}