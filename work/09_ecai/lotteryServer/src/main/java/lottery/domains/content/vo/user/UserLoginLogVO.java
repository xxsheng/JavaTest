package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserLoginLog;

/**
 * Created by Nick on 2017-09-09.
 */
public class UserLoginLogVO {
    private int userId;
    private String ip;
    private String address;
    private String time;

    public UserLoginLogVO(UserLoginLog userLoginLog) {
        this.userId = userLoginLog.getUserId();
        this.ip = userLoginLog.getIp();
        this.address = userLoginLog.getAddress();
        this.time = userLoginLog.getTime();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
