package lottery.domains.content.vo.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 2016/11/11
 */
public class MailConfig {
    private String username;
    private String personal;
    private String password;
    private String host;
    private int bet; // 投注大于发邮件
    private int open; // 中奖大于发邮件
    private int recharge; // 充值大于发邮件
    private List<String> receiveMails = new ArrayList<>();


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<String> getReceiveMails() {
        return receiveMails;
    }

    public void setReceiveMails(List<String> receiveMails) {
        this.receiveMails = receiveMails;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getRecharge() {
        return recharge;
    }

    public void setRecharge(int recharge) {
        this.recharge = recharge;
    }

    public void addReceiveMail(String mail) {
        this.receiveMails.add(mail);
    }
}
