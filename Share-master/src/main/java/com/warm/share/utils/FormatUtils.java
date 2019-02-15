package com.warm.share.utils;

public class FormatUtils {

    public static boolean phone(String phone) {
        return phone != null && phone.length() == 11;
    }


    public static boolean password(String password) {
        return password != null && password.length() == 32;
    }

}
