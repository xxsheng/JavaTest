package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserGameWaterBill;
import lottery.domains.pool.LotteryDataFactory;

/**
 * Created by Nick on 2017/02/04
 */
public class UserGameWaterBillVO {
    private String username; // 用户名
    private String fromUsername; // 来自用户
    private UserGameWaterBill bean;

    public UserGameWaterBillVO(UserGameWaterBill bean, LotteryDataFactory dataFactory) {
        UserVO user = dataFactory.getUser(bean.getUserId());
        if (user == null) {
            this.username = "未知["+bean.getUserId()+"]";
        }
        else {
            this.username = user.getUsername();
        }

        UserVO fromUser = dataFactory.getUser(bean.getFromUser());
        if (fromUser == null) {
            this.fromUsername = "未知["+bean.getFromUser()+"]";
        }
        else {
            this.fromUsername = fromUser.getUsername();
        }
        this.bean = bean;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public UserGameWaterBill getBean() {
        return bean;
    }

    public void setBean(UserGameWaterBill bean) {
        this.bean = bean;
    }
}
