package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserHighPrizeDao;
import lottery.domains.content.entity.UserHighPrize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Nick on 2017/3/13.
 */
@Repository
public class UserHighPrizeDaoImpl implements UserHighPrizeDao {
    private final String tab = UserHighPrize.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<UserHighPrize> superDao;


    @Override
    public boolean add(UserHighPrize entity) {
        return superDao.save(entity);
    }
}
