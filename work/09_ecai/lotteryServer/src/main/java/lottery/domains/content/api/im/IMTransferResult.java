package lottery.domains.content.api.im;

/**
 * Created by Nick on 2017-05-24.
 */
public class IMTransferResult extends IMResult {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private String transferSN;

        public String getTransferSN() {
            return transferSN;
        }

        public void setTransferSN(String transferSN) {
            this.transferSN = transferSN;
        }
    }
}
