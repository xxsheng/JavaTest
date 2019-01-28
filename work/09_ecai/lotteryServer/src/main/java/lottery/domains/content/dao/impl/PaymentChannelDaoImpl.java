package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.PaymentChannelDao;
import lottery.domains.content.entity.PaymentChannel;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentChannelDaoImpl implements PaymentChannelDao {
	
	private final String tab = PaymentChannel.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<PaymentChannel> superDao;

	@Override
	public PaymentChannel getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (PaymentChannel) superDao.unique(hql, values);
	}
	
	@Override
	public PaymentChannel getAvailableById(int id) {
		String hql = "from " + tab + " where id = ?0 and status = 0";
		Object[] values = {id};
		return (PaymentChannel) superDao.unique(hql, values);
	}
	
	@Override
	public List<PaymentChannel> find(List<Criterion> criterions,
			List<Order> orders) {
		return superDao.findByCriteria(PaymentChannel.class, criterions, orders);
	}

	@Override
	public List<PaymentChannel> listAll() {
		String hql = "from " + tab;
		Object[] values = {};
		return superDao.list(hql, values);
	}
}