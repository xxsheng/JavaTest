package lottery.domains.content.vo.activity;

import lottery.domains.content.entity.ActivityRebateWheelBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;

public class ActivityRebateWheelBillVO {
    private String username;
    private ActivityRebateWheelBill bean;

    public ActivityRebateWheelBillVO(ActivityRebateWheelBill bean, LotteryDataFactory lotteryDataFactory) {
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

    public ActivityRebateWheelBill getBean() {
        return bean;
    }

    public void setBean(ActivityRebateWheelBill bean) {
        this.bean = bean;
    }
}
