package lottery.domains.content.vo.bets;

/**
 * Created by Nick on 2017-05-19.
 */
public class UserBetsCancelAdapter {
    private String chaseBillno;
    private int userId;
    private String winExpect;

    public UserBetsCancelAdapter(String chaseBillno, int userId, String winExpect) {
        this.chaseBillno = chaseBillno;
        this.userId = userId;
        this.winExpect = winExpect;
    }

    public String getChaseBillno() {
        return chaseBillno;
    }

    public void setChaseBillno(String chaseBillno) {
        this.chaseBillno = chaseBillno;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getWinExpect() {
        return winExpect;
    }

    public void setWinExpect(String winExpect) {
        this.winExpect = winExpect;
    }
}
