package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.PaymentCardDao;
import lottery.domains.content.entity.PaymentCard;

@Repository
public class PaymentCardDaoImpl implements PaymentCardDao {
	
	private final String tab = PaymentCard.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<PaymentCard> superDao;

	@Override
	public PaymentCard getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (PaymentCard) superDao.unique(hql, values);
	}
	
	@Override
	public PaymentCard getAvailableById(int id) {
		String hql = "from " + tab + " where id = ?0 and status = 0";
		Object[] values = {id};
		return (PaymentCard) superDao.unique(hql, values);
	}
	
	@Override
	public List<PaymentCard> find(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(PaymentCard.class, criterions, orders);
	}

	@Override
	public List<PaymentCard> findListAvailable() {
		String hql = "from " + tab + " where status = ?0";
		return superDao.list(hql);
	}

}