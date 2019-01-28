package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.PaymentChannelQrCodeDao;
import lottery.domains.content.entity.PaymentChannelQrCode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentChannelDaoQrCodeImpl implements PaymentChannelQrCodeDao {
	
	private final String tab = PaymentChannelQrCode.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<PaymentChannelQrCode> superDao;

	@Override
	public List<PaymentChannelQrCode> listAll() {
		String hql = "from " + tab + " order by sequence";
		return superDao.list(hql);
	}

	@Override
	public List<PaymentChannelQrCode> listAll(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(PaymentChannelQrCode.class, criterions, orders);
	}
	
	@Override
	public List<PaymentChannelQrCode> getByChannelId(int channelId) {
		String hql = "from " + tab + " where channelId = ?0";
		Object[] values = {channelId};
		return superDao.list(hql, values);
	}
	
	@Override
	public PaymentChannelQrCode getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (PaymentChannelQrCode)superDao.unique(hql, values);
	}

	@Override
	public boolean add(PaymentChannelQrCode entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(PaymentChannelQrCode entity) {
		return superDao.update(entity);
	}
	
	@Override
	public boolean delete(int id) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = {id};
		return superDao.delete(hql, values);
	}
}