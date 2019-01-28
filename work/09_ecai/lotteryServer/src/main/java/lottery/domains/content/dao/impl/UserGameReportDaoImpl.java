package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserGameReportDao;
import lottery.domains.content.entity.UserGameReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2016/12/28.
 */
@Repository
public class UserGameReportDaoImpl implements UserGameReportDao {
    private final String tab = UserGameReport.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<UserGameReport> superDao;

    @Override
    public boolean save(UserGameReport entity) {
        return superDao.save(entity);
    }

    @Override
    public UserGameReport get(int userId, int platformId, String time) {
        String hql = "from " + tab + " where userId = ?0 and platformId=?1 and time = ?2";
        Object[] values = {userId, platformId, time};
        return (UserGameReport) superDao.unique(hql, values);
    }

    @Override
    public boolean update(UserGameReport entity) {
        String hql = "update " + tab + " set transIn = transIn + ?1, transOut = transOut + ?2, waterReturn = waterReturn + ?3, proxyReturn = proxyReturn + ?4, activity = activity + ?5, billingOrder = billingOrder + ?6, prize = prize + ?7  where id = ?0";
        Object[] values = {entity.getId(), entity.getTransIn(), entity.getTransOut(), entity.getWaterReturn(), entity.getProxyReturn(),  entity.getActivity(), entity.getBillingOrder(), entity.getPrize()};
        return superDao.update(hql, values);
    }
}
