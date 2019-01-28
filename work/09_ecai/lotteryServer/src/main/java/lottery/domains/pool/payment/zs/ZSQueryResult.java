package lottery.domains.pool.payment.zs;

public class ZSQueryResult {
    private String code; // 响应码 00-成功,其他失败
    private String msg; // 响应消息
    private Data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String merchantCode; // 商户号
        private String outOrderId; // 系统订单号
        private String transTime; // 交易时间
        private String transType; // 交易类型
        private String instructCode; // 通道交易订单号
        private String amount; // 支付金额 单位分
        private String replyCode; // 状态码 00账单支付成功 其它失败
        private String sign; // 状态码 00账单支付成功 其它失败

        public String getMerchantCode() {
            return merchantCode;
        }

        public void setMerchantCode(String merchantCode) {
            this.merchantCode = merchantCode;
        }

        public String getOutOrderId() {
            return outOrderId;
        }

        public void setOutOrderId(String outOrderId) {
            this.outOrderId = outOrderId;
        }

        public String getTransTime() {
            return transTime;
        }

        public void setTransTime(String transTime) {
            this.transTime = transTime;
        }

        public String getTransType() {
            return transType;
        }

        public void setTransType(String transType) {
            this.transType = transType;
        }

        public String getInstructCode() {
            return instructCode;
        }

        public void setInstructCode(String instructCode) {
            this.instructCode = instructCode;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getReplyCode() {
            return replyCode;
        }

        public void setReplyCode(String replyCode) {
            this.replyCode = replyCode;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }


}
