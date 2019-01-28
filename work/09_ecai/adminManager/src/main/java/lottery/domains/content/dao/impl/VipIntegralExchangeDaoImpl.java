package lottery.domains.content.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.VipIntegralExchangeDao;
import lottery.domains.content.entity.VipIntegralExchange;

@Repository
public class VipIntegralExchangeDaoImpl implements VipIntegralExchangeDao {
	
	@Autowired
	private HibernateSuperDao<VipIntegralExchange> superDao;
	
	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(VipIntegralExchange.class, criterions, orders, start, limit);
	}
	
}