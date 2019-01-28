package lottery.domains.content.dao;

import lottery.domains.content.entity.PaymentChannelBank;

import java.util.List;

public interface PaymentChannelBankDao {
	
	List<PaymentChannelBank> list(String channelCode);
	
	PaymentChannelBank getById(int id);

	boolean update(PaymentChannelBank entity);

	PaymentChannelBank getByChannelAndBankId(String channelCode, int bankId);
}