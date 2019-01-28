package lottery.domains.content.vo.activity;

import lottery.domains.content.entity.ActivityFirstRechargeBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

/**
 * Created by Nick on 2017/3/17.
 */
public class ActivityFirstRechargeBillVO {
    private String username;
    private ActivityFirstRechargeBill bean;

    public ActivityFirstRechargeBillVO(ActivityFirstRechargeBill bean, LotteryDataFactory lotteryDataFactory) {
        this.bean = bean;
        UserVO tmpUser = lotteryDataFactory.getUser(bean.getUserId());
        if(tmpUser != null) {
            this.username = tmpUser.getUsername();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ActivityFirstRechargeBill getBean() {
        return bean;
    }

    public void setBean(ActivityFirstRechargeBill bean) {
        this.bean = bean;
    }
}
