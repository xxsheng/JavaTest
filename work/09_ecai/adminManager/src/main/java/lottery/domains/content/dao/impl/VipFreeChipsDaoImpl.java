package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.VipFreeChipsDao;
import lottery.domains.content.entity.VipFreeChips;

@Repository
public class VipFreeChipsDaoImpl implements VipFreeChipsDao {

	private final String tab = VipFreeChips.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<VipFreeChips> superDao;
	
	@Override
	public boolean add(VipFreeChips entity) {
		return superDao.save(entity);
	}
	
	@Override
	public VipFreeChips getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (VipFreeChips) superDao.unique(hql, values);
	}
	
	@Override
	public List<VipFreeChips> getUntreated() {
		String hql = "from " + tab + " where status = 0";
		return superDao.list(hql);
	}

	@Override
	public boolean hasRecord(int userId, String sTime, String eTime) {
		String hql = "from " + tab + " where userId = ?0 and startTime = ?1 and endTime = ?2";
		Object[] values = {userId, sTime, eTime};
		List<VipFreeChips> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean update(VipFreeChips entity) {
		return superDao.update(entity);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(VipFreeChips.class, criterions, orders, start, limit);
	}

}