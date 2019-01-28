package lottery.domains.content.api.sb;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by Nick on 2017-05-28.
 */
public class Win88SBCheckUserBalanceResult extends Win88SBResult {
    private List<Data> Data;

    public List<Win88SBCheckUserBalanceResult.Data> getData() {
        return Data;
    }

    public void setData(List<Win88SBCheckUserBalanceResult.Data> data) {
        Data = data;
    }

    public class Data {
        @JSONField(name = "playerName")
        private String playerName;
        @JSONField(name = "balance")
        private Double balance;
        @JSONField(name = "outstanding")
        private Double outstanding;
        @JSONField(name = "currency")
        private Integer currency;

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public Double getBalance() {
            return balance;
        }

        public void setBalance(Double balance) {
            this.balance = balance;
        }

        public Double getOutstanding() {
            return outstanding;
        }

        public void setOutstanding(Double outstanding) {
            this.outstanding = outstanding;
        }

        public Integer getCurrency() {
            return currency;
        }

        public void setCurrency(Integer currency) {
            this.currency = currency;
        }
    }
}
