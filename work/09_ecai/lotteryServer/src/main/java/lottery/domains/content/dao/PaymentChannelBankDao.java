package lottery.domains.content.dao;

import lottery.domains.content.entity.PaymentChannelBank;

import java.util.List;

public interface PaymentChannelBankDao {
	
	List<PaymentChannelBank> listAll(int status);

}
