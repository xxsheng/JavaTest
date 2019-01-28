package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserGameDividendBill;
import lottery.domains.pool.LotteryDataFactory;

/**
 * Created by Nick on 2016/11/01
 */
public class UserGameDividendBillVO {
    private String username; // 用户名
    private UserGameDividendBill bean;

    public UserGameDividendBillVO(UserGameDividendBill bean, LotteryDataFactory dataFactory) {
        UserVO user = dataFactory.getUser(bean.getUserId());
        if (user == null) {
            this.username = "未知["+bean.getUserId()+"]";
        }
        else {
            this.username = user.getUsername();
        }
        this.bean = bean;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserGameDividendBill getBean() {
        return bean;
    }

    public void setBean(UserGameDividendBill bean) {
        this.bean = bean;
    }
}
