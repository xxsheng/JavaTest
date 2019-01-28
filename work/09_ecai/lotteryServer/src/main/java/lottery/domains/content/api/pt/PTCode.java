package lottery.domains.content.api.pt;

import java.util.HashMap;

/**
 * Created by Nick on 2016/12/26.
 */
public final class PTCode {
    private static final HashMap<String, String> CODE_MAP = new HashMap<>();
    public static final String DEFAULT_ERROR_CODE = "2-7004";

    private PTCode(){
    }

    static {
        CODE_MAP.put("19", "2-7000"); // 创建PT用户失败，用户名已存在，请联系客服！
        CODE_MAP.put("41", "2-7001"); // 您的用户在PT方不存在，请联系客服！
        CODE_MAP.put("109", "2-7001"); // 您的用户在PT方不存在，请联系客服！
        CODE_MAP.put("44", "2-7002"); // 您的用户在PT方被冻结，请联系客服！
        CODE_MAP.put("49", "2-7003"); // 您当前不允许使用PT，请联系客服！
        CODE_MAP.put("71", "2-7004"); // 连接PT时发生未知错误，请联系客服！
        CODE_MAP.put("97", "2-7009"); // 平台PT余额不足，请联系客服！
        CODE_MAP.put("98", "2-7005"); // 您的PT余额不足！
        CODE_MAP.put("302", "2-7006"); // 重复转账单，请重试！
    }

    public static String transErrorCode(String code) {
        String errorCode = CODE_MAP.get(code);
        if (errorCode == null) {
            return DEFAULT_ERROR_CODE;
        }

        return errorCode;
    }
}
