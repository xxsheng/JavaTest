package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import lottery.domains.content.vo.user.UserCardVO;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserCardDao;
import lottery.domains.content.entity.UserCard;

@Repository
public class UserCardDaoImpl implements UserCardDao {
	
	private final String tab = UserCard.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserCard> superDao;
	
	@Override
	public boolean add(UserCard entity) {
		return superDao.save(entity);
	}
	
	@Override
	public UserCard getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserCard) superDao.unique(hql, values);
	}
	
	@Override
	public List<UserCard> getByUserId(int id) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = {id};
		return superDao.list(hql, values);
	}
	
	@Override
	public UserCard getByCardId(String cardId) {
		String hql = "from " + tab + " where cardId = ?0";
		Object[] values = { cardId };
		return (UserCard) superDao.unique(hql, values);
	}

	@Override
	public UserCard getByUserAndCardId(int userId, String cardId) {
		String hql = "from " + tab + " where userId = ?0 and cardId = ?1";
		Object[] values = { userId, cardId };
		List<UserCard> list = superDao.list(hql, values);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}

	@Override
	public boolean update(UserCard entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean updateCardName(int userId, String cardName) {
		String hql = "update " + tab + " set cardName = ?1 where userId = ?0";
		Object[] values = {userId, cardName};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean delete(int userId) {
		String hql = "delete from " + tab + " where userId = ?0";
		Object[] values = {userId};
		return superDao.delete(hql, values);
	}
	
	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(UserCard.class, criterions, orders, start, limit);
	}
	
}