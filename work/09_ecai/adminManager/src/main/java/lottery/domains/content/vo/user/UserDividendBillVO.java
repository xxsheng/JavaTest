package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserDividendBill;
import lottery.domains.pool.LotteryDataFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nick on 2016/11/01
 */
public class UserDividendBillVO {
    private String username; // 用户名
    private UserDividendBill bean;
    private List<String> userLevels = new LinkedList<>();

    public UserDividendBillVO(UserDividendBill bean, LotteryDataFactory dataFactory) {
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

    public UserDividendBill getBean() {
        return bean;
    }

    public void setBean(UserDividendBill bean) {
        this.bean = bean;
    }

    public List<String> getUserLevels() {
        return userLevels;
    }

    public void setUserLevels(List<String> userLevels) {
        this.userLevels = userLevels;
    }
}
