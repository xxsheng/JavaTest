package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserGameAccountReadDao;
import lottery.domains.content.entity.UserGameAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2016/12/24.
 */
@Repository
public class UserGameAccountReadDaoImpl implements UserGameAccountReadDao {
    private final String tab = UserGameAccount.class.getSimpleName();

    @Autowired
    private HibernateSuperReadDao<UserGameAccount> superDao;

    @Override
    public UserGameAccount get(int userId, int platformId, int model) {
        String hql = "from " + tab + " where userId=?0 and platformId=?1 and model=?2";
        Object[] values = {userId, platformId, model};
        return (UserGameAccount) superDao.unique(hql, values);
    }

    @Override
    public UserGameAccount getByUsername(String username, int platformId, int model) {
        String hql = "from " + tab + " where username=?0 and platformId=?1 and model=?2";
        Object[] values = {username, platformId, model};
        return (UserGameAccount) superDao.unique(hql, values);
    }

    @Override
    public List<?> getDayRegistAll(String sTime, String eTime) {
        String sql = "select substring(time, 1, 10), count(id) from user_game_account where time >= :sTime and time < :eTime group by substring(time, 1, 10)";

        Map<String, Object> params = new HashMap<>();
        params.put("sTime", sTime);
        params.put("eTime", eTime);

        return superDao.listBySql(sql, params);
    }

    @Override
    public List<?> getDayRegistByTeam(int userId, String sTime, String eTime) {
        return getDayRegistByTeam(new int[]{userId}, sTime, eTime);
    }

    @Override
    public List<?> getDayRegistByTeam(int[] userIds, String sTime, String eTime) {
        StringBuffer sql = new StringBuffer("select substring(uga.time, 1, 10), count(uga.id) from user_game_account uga,user u where uga.user_id = u.id and (u.id in(:userIds)");

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < userIds.length; i++) {
            sql.append(" or u.upids like :upid").append(i);
            params.put("upid" + i, "%[" + userIds[i] + "]%");
        }

        sql.append(")and uga.time >= :sTime and uga.time < :eTime group by substring(uga.time, 1, 10)");

        params.put("sTime", sTime);
        params.put("eTime", eTime);
        params.put("userIds", ArrayUtils.transInIds(userIds));

        return superDao.listBySql(sql.toString(), params);
    }
}
