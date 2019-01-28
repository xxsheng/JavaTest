package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.UserGameReportService;
import lottery.domains.content.dao.UserGameReportDao;
import lottery.domains.content.entity.UserGameReport;
import lottery.domains.content.global.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Nick on 2016/12/28.
 */
@Service
public class UserGameReportServiceImpl implements UserGameReportService {
    @Autowired
    private UserGameReportDao uGameReportDao;

    @Override
    public boolean update(int userId, int platformId, int type, double amount, String time) {
        UserGameReport entity = new UserGameReport();
        switch (type) {
            case Global.BILL_TYPE_TRANS_IN:
                entity.setTransIn(amount);
                break;
            case Global.BILL_TYPE_TRANS_OUT:
                entity.setTransOut(amount);
                break;
            case Global.BILL_TYPE_PRIZE:
                entity.setPrize(amount);
                break;
            case Global.BILL_TYPE_WATER_RETURN:
                entity.setWaterReturn(amount);
                break;
            case Global.BILL_TYPE_PROXY_RETURN:
                entity.setProxyReturn(amount);
                break;
            case Global.BILL_TYPE_ACTIVITY:
                entity.setActivity(amount);
                break;
            default:
                return false;
        }
        UserGameReport bean = uGameReportDao.get(userId, platformId, time);
        if(bean != null) {
            entity.setId(bean.getId());
            return uGameReportDao.update(entity);
        } else {
            entity.setUserId(userId);
            entity.setPlatformId(platformId);
            entity.setTime(time);
            return uGameReportDao.save(entity);
        }
    }
}
