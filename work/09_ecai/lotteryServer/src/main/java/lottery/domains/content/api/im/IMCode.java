package lottery.domains.content.api.im;

import java.util.HashMap;

/**
 * Created by Nick on 2017-05-24.
 */
public class IMCode {
    public static final int SUCCESS = 0; // 成功，返回遊戲網址 => Data
    public static final int ARG_ERROR = 2; // 參數錯誤，請檢查參數
    public static final int API_ACCOUNT_ERROR = 3; // 介接廠商帳號錯誤
    public static final int ACCOUNT_ERROR = 4; // 遊戲帳號錯誤
    public static final int CODE_ERROR = 5; // 驗證碼錯誤，意思是参数签名错误
    public static final int MAINTENANCE = 9997; // 維護


    private static final HashMap<Integer, String> CODE_MAP = new HashMap<>();
    public static final String DEFAULT_ERROR_CODE = "2-9000";

    private IMCode(){
    }

    static {
        CODE_MAP.put(2, "2-9002"); // IM参数错误，请联系客服！
        CODE_MAP.put(3, "2-9003"); // IM介接廠商帳號錯誤，请联系客服！
        CODE_MAP.put(4, "2-9004"); // 遊戲帳號錯誤
        CODE_MAP.put(5, "2-9005"); // 连接IM时数据签名错误，请联系客服！
        CODE_MAP.put(9997, "2-9006"); // IM正在维护中，请稍候再试！
    }

    public static String transErrorCode(int code) {
        String errorCode = CODE_MAP.get(code);
        if (errorCode == null) {
            return null;
        }

        return errorCode;
    }
}
