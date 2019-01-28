package lottery.domains.utils.user;

import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCodePointUtil {

    /**
     * DAO
     */
    @Autowired
    private UserDao uDao;

    @Autowired
    private DataFactory dataFactory;

    /**
     * 等级1代理：系统最高等级 & 上级是总账(72) （等级1用户一般是指主管或内部招商   主管|内部招商 -> 招商 -> 直属）
     */
    public boolean isLevel1Proxy(User uBean) {
        if (uBean.getType() == Global.USER_TYPE_PROXY
                && uBean.getCode() == dataFactory.getCodeConfig().getSysCode()
                && uBean.getUpid() == Global.USER_TOP_ID) {
            return true;
        }
        return false;
    }

    /**
     * 等级2代理：系统最高等级 & 上级是等级1用户 （等级2一般是指招商或超级招商   主管|内部招商 -> 招商 -> 直属）
     */
    public boolean isLevel2Proxy(User uBean) {
        if (uBean.getType() == Global.USER_TYPE_PROXY
                && uBean.getCode() == dataFactory.getCodeConfig().getSysCode()
                && uBean.getUpid() != 0
                && uBean.getUpid() != Global.USER_TOP_ID) {
            User upBean = uDao.getById(uBean.getUpid());
            if (isLevel1Proxy(upBean)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 等级3代理：系统最高等级 & 上级是等级2用户 （等级3用户一般是指直属   主管|内部招商 -> 招商 -> 直属）
     */
    public boolean isLevel3Proxy(User uBean) {
        if (uBean.getType() == Global.USER_TYPE_PROXY
                && uBean.getCode() == dataFactory.getCodeConfig().getSysCode()
                && uBean.getUpid() != 0
                && uBean.getUpid() != Global.USER_TOP_ID) {
            User upBean = uDao.getById(uBean.getUpid());
            if (isLevel2Proxy(upBean)) {
                return true;
            }
        }
        return false;
    }
}