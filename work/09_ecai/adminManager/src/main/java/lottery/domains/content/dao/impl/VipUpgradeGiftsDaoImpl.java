package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.VipUpgradeGiftsDao;
import lottery.domains.content.entity.VipUpgradeGifts;

@Repository
public class VipUpgradeGiftsDaoImpl implements VipUpgradeGiftsDao {

	private final String tab = VipUpgradeGifts.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<VipUpgradeGifts> superDao;
	
	@Override
	public boolean add(VipUpgradeGifts entity) {
		return superDao.save(entity);
	}
	
	@Override
	public VipUpgradeGifts getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (VipUpgradeGifts) superDao.unique(hql, values);
	}

	@Override
	public int getWaitTodo() {
		String hql = "select count(id) from " + tab + " where status = 0";
		Object result = superDao.unique(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}
	
	@Override
	public boolean hasRecord(int userId, int beforeLevel, int afterLevel) {
		String hql = "from " + tab + " where userId = ?0 and beforeLevel = ?1 and afterLevel = ?2";
		Object[] values = {userId, beforeLevel, afterLevel};
		List<VipUpgradeGifts> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean update(VipUpgradeGifts entity) {
		return superDao.update(entity);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(VipUpgradeGifts.class, criterions, orders, start, limit);
	}

}