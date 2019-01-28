package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserRegistLinkDao;
import lottery.domains.content.entity.UserRegistLink;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRegistLinkDaoImpl implements UserRegistLinkDao {

	private final String tab = UserRegistLink.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserRegistLink> superDao;

	@Override
	public boolean add(UserRegistLink entity) {
		return superDao.save(entity);
	}

	@Override
	public UserRegistLink get(String code) {
		String hql = "from " + tab + " where code = ?0";
		Object[] values = { code };
		return (UserRegistLink) superDao.unique(hql, values);
	}

	@Override
	public List<UserRegistLink> getByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.list(hql, values);
	}

	@Override
	public boolean updateAmount(int id, int amount) {
		String hql = "update " + tab
				+ " set amount = amount + ?1 where id = ?0";
		Object[] values = { id, amount };
		return superDao.update(hql, values);
	}

	@Override
	public boolean delete(int id, int userId) {
		String hql = "delete from " + tab + " where id = ?0 and userId = ?1";
		Object[] values = { id, userId };
		return superDao.delete(hql, values);
	}

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders,
						   int start, int limit) {
		return superDao.findPageList(UserRegistLink.class, criterions, orders,
				start, limit);
	}

}