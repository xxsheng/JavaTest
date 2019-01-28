package lottery.domains.content.api.im;

/**
 * Created by Nick on 2017-05-24.
 */
public class IMValidationResult extends IMResult{
    private Data data;

    public IMValidationResult(String userName, String ip, boolean success, int code, String message) {
        Data data = new Data();
        data.setUserName(userName);
        data.setIP(ip);
        this.data = data;
        super.setSuccess(success);
        super.setCode(code);
        super.setMessage(message);
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private String UserName;
        private String Currency = IMAPI.CURRENCY_CODE;
        private String IP;

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public String getCurrency() {
            return Currency;
        }

        public void setCurrency(String currency) {
            Currency = currency;
        }

        public String getIP() {
            return IP;
        }

        public void setIP(String IP) {
            this.IP = IP;
        }
    }
}
