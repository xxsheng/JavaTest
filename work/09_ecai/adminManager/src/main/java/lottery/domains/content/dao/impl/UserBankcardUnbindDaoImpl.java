package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBankcardUnbindDao;
import lottery.domains.content.entity.UserBankcardUnbindRecord;

@Repository
public class UserBankcardUnbindDaoImpl implements UserBankcardUnbindDao{
	
	public static final String tab = UserBankcardUnbindRecord.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserBankcardUnbindRecord> superDao;
	@Override
	public boolean add(UserBankcardUnbindRecord entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(UserBankcardUnbindRecord entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean delByCardId(String cardId) {
		String hql = "delete from " + tab + " where cardId = ?0";
		Object[] values = {cardId};
		return superDao.delete(hql, values);
	}

	@Override
	public boolean updateByParam(String userIds,String cardId, int unbindNum, String unbindTime) {
		String hql = "update " + tab + " set userIds =?0, unbindNum = ?1,unbindTime = ?2 where cardId = ?3";
		Object[] values = {userIds,unbindNum, unbindTime,cardId};
		return superDao.update(hql, values);
	}

	@Override
	public UserBankcardUnbindRecord getUnbindInfoById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserBankcardUnbindRecord) superDao.unique(hql, values);
	}

	@Override
	public UserBankcardUnbindRecord getUnbindInfoBycardId(String cardId) {
		String hql = "from " + tab + " where cardId = ?0";
		Object[] values = {cardId};
		return (UserBankcardUnbindRecord) superDao.unique(hql, values);
	}

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(UserBankcardUnbindRecord.class, criterions, orders, start, limit);
	}

	@Override
	public List<UserBankcardUnbindRecord> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}
}
