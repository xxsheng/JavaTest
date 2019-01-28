package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.PaymentBank;

public interface PaymentBankService {
	
	List<PaymentBank> listAll();
	
	PaymentBank getById(int id);

	boolean update(int id, String name, String url);
	
}