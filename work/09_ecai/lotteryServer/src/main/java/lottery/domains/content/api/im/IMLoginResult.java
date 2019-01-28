package lottery.domains.content.api.im;

/**
 * Created by Nick on 2017-05-24.
 */
public class IMLoginResult extends IMResult {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private String GameUrl;

        public String getGameUrl() {
            return GameUrl;
        }

        public void setGameUrl(String gameUrl) {
            GameUrl = gameUrl;
        }
    }
}
