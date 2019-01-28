package javautils.ftp;

/**
 * Created by Nick on 2017/1/1.
 */
public class FtpConfig {
    //服务器地址名称
    private String server;
    //端口号
    private int port;
    //用户名称
    private String username;
    //密码
    private String password;
    //工作目录
    private String location;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
