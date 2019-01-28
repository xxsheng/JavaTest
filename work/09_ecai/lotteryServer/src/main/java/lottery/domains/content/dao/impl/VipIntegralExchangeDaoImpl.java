package lottery.domains.content.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.VipIntegralExchangeDao;
import lottery.domains.content.entity.VipIntegralExchange;

@Repository
public class VipIntegralExchangeDaoImpl implements VipIntegralExchangeDao {
	
	private final String tab = VipIntegralExchange.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<VipIntegralExchange> superDao;

	@Override
	public boolean add(VipIntegralExchange entity) {
		return superDao.save(entity);
	}

	@Override
	public int getDateCount(int userId, String date) {
		String hql = "select count(id) from " + tab + " where userId = ?0 and time like ?1";
		Object[] values = {userId, "%" + date + "%"};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).intValue() : 0;
	}

}