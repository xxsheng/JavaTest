package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.VipBirthdayGiftsDao;
import lottery.domains.content.entity.VipBirthdayGifts;

@Repository
public class VipBirthdayGiftsDaoImpl implements VipBirthdayGiftsDao {

	private final String tab = VipBirthdayGifts.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<VipBirthdayGifts> superDao;
	
	@Override
	public boolean add(VipBirthdayGifts entity) {
		return superDao.save(entity);
	}

	@Override
	public VipBirthdayGifts getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (VipBirthdayGifts) superDao.unique(hql, values);
	}

	@Override
	public int getWaitTodo() {
		String hql = "select count(id) from " + tab + " where status = 0";
		Object result = superDao.unique(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}
	
	@Override
	public boolean hasRecord(int userId, int year) {
		String hql = "from " + tab + " where userId = ?0 and birthday like ?1";
		Object[] values = {userId, year + "%"};
		List<VipBirthdayGifts> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean update(VipBirthdayGifts entity) {
		return superDao.update(entity);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(VipBirthdayGifts.class, criterions, orders, start, limit);
	}
	
}