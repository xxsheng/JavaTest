package lottery.domains.content.api.im;

/**
 * Created by Nick on 2017-05-24.
 */
public class IMBalanceResult extends IMResult {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private Double Balance;

        public Double getBalance() {
            return Balance;
        }

        public void setBalance(Double balance) {
            Balance = balance;
        }
    }
}
