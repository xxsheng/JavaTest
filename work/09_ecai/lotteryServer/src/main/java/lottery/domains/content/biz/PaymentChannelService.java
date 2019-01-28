package lottery.domains.content.biz;

import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.vo.pay.PaymentChannelVO;
import lottery.web.WebJSON;
import lottery.web.helper.session.SessionUser;

import java.util.List;

public interface PaymentChannelService {
	
	List<PaymentChannelVO> getAvailableByUserId(SessionUser user);

	List<PaymentChannel> listAll();

	boolean isAvailable(WebJSON json, SessionUser user, double rechargeAmount, PaymentChannel channel);
}