package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.PaymentBankDao;
import lottery.domains.content.entity.PaymentBank;

@Repository
public class PaymentBankDaoImpl implements PaymentBankDao {
	
	private final String tab = PaymentBank.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<PaymentBank> superDao;

	@Override
	public List<PaymentBank> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}

	@Override
	public PaymentBank getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (PaymentBank) superDao.unique(hql, values);
	}

	@Override
	public boolean update(PaymentBank entity) {
		return superDao.update(entity);
	}
	
}