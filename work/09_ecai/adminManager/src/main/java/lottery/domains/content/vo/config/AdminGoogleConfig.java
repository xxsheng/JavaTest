package lottery.domains.content.vo.config;

/**
 * Created by Nick on 2017/08/23.
 */
public class AdminGoogleConfig {
    private boolean loginStatus; // 是否开启后台Google登录验证

    public boolean isLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }
}
