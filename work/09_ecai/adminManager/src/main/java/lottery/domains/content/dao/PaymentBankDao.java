package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.PaymentBank;

public interface PaymentBankDao {

	List<PaymentBank> listAll();
	
	PaymentBank getById(int id);
	
	boolean update(PaymentBank entity);
	
}