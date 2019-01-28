package lottery.web.websocket;

import javautils.date.Moment;
import lottery.web.helper.session.SessionUser;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Nick on 2017-04-16.
 */
public class WebSocketInfo implements Serializable{

    private static final long serialVersionUID = -1056797430726188977L;
    private String token;
    private String connectIp;
    private SessionUser user;
    private Date expire;

    public WebSocketInfo(String token, String connectIp, SessionUser user) {
        this.token = token;
        this.connectIp = connectIp;
        this.user = user;
        this.expire = new Moment().add(2, "hours").toDate(); // 过期时间
        // this.expire = new Moment().add(5, "seconds").toDate(); // 过期时间
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getConnectIp() {
        return connectIp;
    }

    public void setConnectIp(String connectIp) {
        this.connectIp = connectIp;
    }

    public SessionUser getUser() {
        return user;
    }

    public void setUser(SessionUser user) {
        this.user = user;
    }

    public Date getExpire() {
        return expire;
    }

    public boolean hasExpired() {
        Moment expireMoment = new Moment().fromDate(this.expire);
        return new Moment().gt(expireMoment);
    }
}
