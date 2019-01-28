package lottery.domains.content.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserHighPrizeDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBetsOriginal;
import lottery.domains.content.entity.UserHighPrize;

/**
 * Created by Nick on 2017/3/13.
 */
@Repository
public class UserHighPrizeDaoImpl implements UserHighPrizeDao {
    private final String tab = UserHighPrize.class.getSimpleName();
    private final String utab = User.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<UserHighPrize> superDao;

    @Override
    public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(UserHighPrize.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public UserHighPrize getById(int id) {
        String hql = "from " + tab + " where id = ?0";
        Object[] values = {id};
        return (UserHighPrize) superDao.unique(hql, values);
    }

    @Override
    public boolean add(UserHighPrize entity) {
        return superDao.save(entity);
    }

    @Override
    public boolean updateStatus(int id, int status) {
        String hql = "update " + tab + " set status = ?1 where id = ?0";
        Object[] values = {id, status};
        return superDao.update(hql, values);
    }

    @Override
    public boolean updateStatusAndConfirmUsername(int id, int status, String confirmUsername) {
        String hql = "update " + tab + " set status = ?1,confirmUsername=?2 where id = ?0";
        Object[] values = {id, status, confirmUsername};
        return superDao.update(hql, values);
    }

    @Override
    public int getUnProcessCount() {
        String hql = "select count(b.id) from " + tab + " b  ," + utab + " u  where u.id = b.userId  and u.upid !=0 and b.status = 0";
        Object result = superDao.unique(hql);
        return result != null ? ((Number) result).intValue() : 0;
    }

	@Override
	public PageList find(String sql, int start, int limit) {
		String hsql = "select b.* from user_high_prize b, user u where b.user_id = u.id  ";
		PageList pageList = superDao.findPageEntityList(hsql+sql, UserHighPrize.class,new HashMap<String, Object>(), start, limit);
		return pageList;
	}
}
