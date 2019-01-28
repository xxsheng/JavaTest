package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserBlacklistDao;
import lottery.domains.content.entity.UserBlacklist;

@Repository
public class UserBlacklistDaoImpl implements UserBlacklistDao {
	
	private final String tab = UserBlacklist.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserBlacklist> superDao;
	
	@Override
	public boolean add(UserBlacklist entity) {
		return superDao.save(entity);
	}
	
	@Override
	public List<UserBlacklist> getByUsername(String username) {
		String hql = "from " + tab + " where username = ?0";
		Object[] values = {username};
		return superDao.list(hql, values);
	}
	
	@Override
	public List<UserBlacklist> getByCard(String cardName, String cardId) {
		String hql = "from " + tab + " where cardName = ?0 or cardId = ?1";
		Object[] values = {cardName, cardId};
		return superDao.list(hql, values);
	}
	
	@Override
	public List<UserBlacklist> getByCardName(String cardName) {
		String hql = "from " + tab + " where cardName = ?0";
		Object[] values = {cardName};
		return superDao.list(hql, values);
	}
	
	@Override
	public List<UserBlacklist> list(List<Criterion> criterions,
			List<Order> orders) {
		return superDao.findByCriteria(UserBlacklist.class, criterions, orders);
	}

	@Override
	public int getByIp(String ip) {
		String hql = "select count(id) from " + tab + " where ip = ?0";
		Object[] values = {ip};
		Object result = superDao.unique(hql, values);
		return result == null ? 0 : Integer.parseInt(result.toString());
	}

}