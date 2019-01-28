package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserCardDao;
import lottery.domains.content.entity.UserCard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
	public UserCard getById(int id, int userId) {
		String hql = "from " + tab + " where id = ?0 and userId = ?1";
		Object[] values = { id, userId };
		return (UserCard) superDao.unique(hql, values);
	}

	@Override
	public UserCard getByCardId(String cardId) {
		String hql = "from " + tab + " where cardId = ?0";
		Object[] values = { cardId };
		return (UserCard) superDao.unique(hql, values);
	}

	@Override
	public List<UserCard> listByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0";
		Object[] values = { userId };
		return superDao.list(hql, values);
	}

	@Override
	public boolean delete(int id, int userId) {
		String hql = "delete from " + tab + " where id = ?0 and userId = ?1";
		Object[] values = { id, userId };
		return superDao.delete(hql, values);
	}
	
	@Override
	public boolean setDefault(int id, int userId) {
		String resetHql = "update " + tab + " set isDefault = 0 where userId = ?0";
		Object[] resetValues = { userId };
		boolean resetResult = superDao.update(resetHql, resetValues);
		if(resetResult) {
			String updateHql = "update " + tab + " set isDefault = 1 where id = ?0 and userId = ?1";
			Object[] updateValues = { id, userId };
			return superDao.update(updateHql, updateValues);
		}
		return false;
	}

}