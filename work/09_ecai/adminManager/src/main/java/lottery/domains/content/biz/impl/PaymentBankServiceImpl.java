package lottery.domains.content.biz.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.PaymentBankService;
import lottery.domains.content.dao.PaymentBankDao;
import lottery.domains.content.entity.PaymentBank;

@Service
public class PaymentBankServiceImpl implements PaymentBankService {
	
	@Autowired
	private PaymentBankDao paymentBankDao;

	@Override
	public List<PaymentBank> listAll() {
		return paymentBankDao.listAll();
	}
	
	@Override
	public PaymentBank getById(int id) {
		return paymentBankDao.getById(id);
	}
	
	@Override
	public boolean update(int id, String name, String url) {
		PaymentBank entity = paymentBankDao.getById(id);
		if(entity != null) {
			entity.setName(name);
			entity.setUrl(url);
			return paymentBankDao.update(entity);
		}
		return false;
	}

}