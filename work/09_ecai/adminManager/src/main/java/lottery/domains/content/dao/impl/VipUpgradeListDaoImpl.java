package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.VipUpgradeListDao;
import lottery.domains.content.entity.VipUpgradeList;

@Repository
public class VipUpgradeListDaoImpl implements VipUpgradeListDao {
	
	private final String tab = VipUpgradeList.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<VipUpgradeList> superDao;

	@Override
	public boolean add(VipUpgradeList entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean hasRecord(int userId, String month) {
		String hql = "from " + tab + " where userId = ?0 and month = ?1";
		Object[] values = {userId, month};
		List<VipUpgradeList> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(VipUpgradeList.class, criterions, orders, start, limit);
	}

}