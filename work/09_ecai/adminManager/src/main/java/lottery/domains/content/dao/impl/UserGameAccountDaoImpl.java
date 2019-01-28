package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserGameAccountDao;
import lottery.domains.content.entity.UserGameAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2016/12/24.
 */
@Repository
public class UserGameAccountDaoImpl implements UserGameAccountDao {
    private final String tab = UserGameAccount.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<UserGameAccount> superDao;

    @Override
    public UserGameAccount get(String platformName, int platformId) {
        String hql = "from " + tab + " where username=?0 and platformId=?1 and model = 1";
        Object[] values = {platformName, platformId};
        return (UserGameAccount) superDao.unique(hql, values);
    }

    @Override
    public UserGameAccount get(int userId, int platformId) {
        String hql = "from " + tab + " where userId=?0 and platformId=?1 and model = 1";
        Object[] values = {userId, platformId};
        return (UserGameAccount) superDao.unique(hql, values);
    }

    @Override
    public UserGameAccount save(UserGameAccount account) {
        superDao.save(account);
        return account;
    }
}
