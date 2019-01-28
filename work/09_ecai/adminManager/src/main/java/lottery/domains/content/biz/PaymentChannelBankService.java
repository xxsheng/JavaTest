package lottery.domains.content.biz;

import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.vo.payment.PaymentChannelBankVO;

import java.util.List;

public interface PaymentChannelBankService {
	
	List<PaymentChannelBankVO> list(String channelCode);
	
	boolean updateStatus(int id, int status);

	PaymentChannelBank getByChannelAndBankId(String channelCode, int bankId);
}