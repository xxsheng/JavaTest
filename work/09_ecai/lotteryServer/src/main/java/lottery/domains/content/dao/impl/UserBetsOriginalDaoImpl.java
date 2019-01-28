package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsOriginalDao;
import lottery.domains.content.entity.UserBetsOriginal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2017/1/15.
 */
@Repository
public class UserBetsOriginalDaoImpl implements UserBetsOriginalDao {
    private final String tab = UserBetsOriginal.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<UserBetsOriginal> superDao;

    @Override
    public void add(UserBetsOriginal original) {
        superDao.save(original);
    }
}
