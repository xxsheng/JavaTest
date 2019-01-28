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
	public List<PaymentChannelBank> listAll(int status) {
		String hql = "from " + tab + " where status=?0";
		Object[] values = {status};
		return superDao.list(hql, values);
	}

}
