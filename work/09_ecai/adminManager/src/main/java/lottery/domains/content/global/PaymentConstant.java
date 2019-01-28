package lottery.domains.content.global;

/**
 * Created by Nick on 2017-11-10.
 */
public final class PaymentConstant {
    public static String formatPaymentChannelType(int type, int subType) {
        // 1：网银充值
        if(type == 1) {
            if (subType == 1) return "网银在线";
            if (subType == 2) return "网银转账";
            if (subType == 3) return "快捷支付";
            if (subType == 4) return "银联扫码";
            if (subType == 5) return "网银扫码转账";
            return "网银充值";
        }
        // 2：手机充值
        if(type == 2) {
            if (subType == 1) return "微信在线";
            if (subType == 2) return "微信扫码转账";
            if (subType == 3) return "支付宝在线";
            if (subType == 4) return "支付宝扫码转账";
            if (subType == 5) return "QQ在线";
            if (subType == 6) return "QQ扫码转账";
            if (subType == 7) return "京东钱包";
            return "手机充值";
        }
        // 3：系统充值
        if(type == 3) {
            if (subType == 1) return "充值未到账";
            if (subType == 2) return "活动补贴";
            if (subType == 3) return "管理员增";
            if (subType == 4) return "管理员减";
            return "系统充值";
        }
        // 4：上下级转账
        if(type == 4) {
            if (subType == 1) return "上下级转账(转入)";
            if (subType == 2) return "上下级转账(转出)";
            return "上下级转账";
        }

        return "未知";
    }
}
