package lottery.domains.content.api.ag;

import java.util.HashMap;

/**
 * Created by Nick on 2016/12/26.
 */
public final class AGCode {
    private static final HashMap<String, String> CODE_MAP = new HashMap<>();
    public static final String DEFAULT_ERROR_CODE = "2-8000";

    private AGCode(){
    }

    static {
        CODE_MAP.put("key_error", "2-8000"); // 连接AG时发生未知错误，请联系客服！
        CODE_MAP.put("network_error", "2-8001"); // 连接AG时发生网络错误，请联系客服！
        CODE_MAP.put("account_add_fail", "2-8002"); // 创建AG账号失败,可能是密码不正确或账号已存在！
        CODE_MAP.put("error", "2-8006"); // AG返回的错误
        CODE_MAP.put("account_not_exist", "2-8003"); // 您的用户在PT方被冻结，请联系客服！
        CODE_MAP.put("duplicate_transfer", "2-8004"); // 重复转账，请稍候再试！
        CODE_MAP.put("not_enough_credit", "2-8005"); // 余额不足，无法转账！
    }

    public static String transErrorCode(String code) {
        String errorCode = CODE_MAP.get(code);
        if (errorCode == null) {
            return null;
        }

        return errorCode;
    }
}
