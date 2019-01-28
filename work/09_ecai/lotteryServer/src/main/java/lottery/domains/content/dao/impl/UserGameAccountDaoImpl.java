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
    public UserGameAccount save(UserGameAccount account) {
        superDao.save(account);
        return account;
    }
}
