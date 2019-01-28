package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.PaymentChannelBankDao;
import lottery.domains.content.entity.PaymentChannelBank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentChannelBankDaoImpl implements PaymentChannelBankDao {
	
	private final String tab = PaymentChannelBank.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<PaymentChannelBank> superDao;

	@Override
	public List<PaymentChannelBank> list(String channelCode) {
		String hql = "from " + tab + " where channelCode = ?0";
		Object[] values = {channelCode};
		return superDao.list(hql, values);
	}

	@Override
	public PaymentChannelBank getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (PaymentChannelBank) superDao.unique(hql, values);
	}

	@Override
	public boolean update(PaymentChannelBank entity) {
		return superDao.update(entity);
	}

	@Override
	public PaymentChannelBank getByChannelAndBankId(String channelCode, int bankId) {
		String hql = "from " + tab + " where channelCode = ?0 and bankId=?1";
		Object[] values = {channelCode, bankId};
		return (PaymentChannelBank) superDao.unique(hql, values);
	}
}