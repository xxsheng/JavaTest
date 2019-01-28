package lottery.domains.content.vo.user;

import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.entity.UserHighPrize;
import lottery.domains.pool.LotteryDataFactory;

/**
 * Created by Nick on 2017/3/13.
 */
public class UserHighPrizeTimesVO {
    private String username;
    private String platform;
    private UserHighPrize bean;

    public UserHighPrizeTimesVO(UserHighPrize bean, LotteryDataFactory lotteryDataFactory) {
        this.bean = bean;
        UserVO tmpUser = lotteryDataFactory.getUser(bean.getUserId());
        if(tmpUser != null) {
            this.username = tmpUser.getUsername();
        }
        SysPlatform platform = lotteryDataFactory.getSysPlatform(bean.getPlatform());
        if (platform != null) {
            this.platform = platform.getName();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserHighPrize getBean() {
        return bean;
    }

    public void setBean(UserHighPrize bean) {
        this.bean = bean;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
