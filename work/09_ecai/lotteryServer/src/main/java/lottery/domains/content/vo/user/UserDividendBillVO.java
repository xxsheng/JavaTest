package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserDividendBill;
import lottery.domains.pool.DataFactory;

/**
 * Created by Nick on 2016/11/01
 */
public class UserDividendBillVO {
    private String username; // 用户名
    private UserDividendBill bean;

    public UserDividendBillVO(UserDividendBill bean, DataFactory dataFactory) {
        UserVO userVO = dataFactory.getUser(bean.getUserId());
        if (userVO != null) {
            this.username = userVO.getUsername();
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
}
