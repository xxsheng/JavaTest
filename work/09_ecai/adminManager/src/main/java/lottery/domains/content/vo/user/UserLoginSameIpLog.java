package lottery.domains.content.vo.user;

/**
 * Created by Nick on 2016/12/4.
 */
public class UserLoginSameIpLog {
    private String ip;
    private String address;
    private String users;

    public UserLoginSameIpLog(String ip, String address, String users) {
        this.ip = ip;
        this.address = address;
        this.users = users;
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

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }
}
