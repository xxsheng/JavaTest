package lottery.domains.content.vo.bill;

import lottery.domains.content.entity.UserGameWaterBill;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;

/**
 * Created by Nick on 2017/02/04
 */
public class UserGameWaterBillVO {
    private String username; // 用户名
    private String fromUsername; // 来自用户
    private UserGameWaterBill bean;

    public UserGameWaterBillVO(UserGameWaterBill bean, DataFactory dataFactory) {
        UserVO userVO = dataFactory.getUser(bean.getUserId());
        if (userVO != null) {
            this.username = userVO.getUsername();
        }

        UserVO fromUserVO = dataFactory.getUser(bean.getFromUser());
        if (fromUserVO != null) {
            this.fromUsername = fromUserVO.getUsername();
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
