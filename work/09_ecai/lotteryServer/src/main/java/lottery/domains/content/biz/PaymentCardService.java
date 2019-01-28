package lottery.domains.content.biz;

import lottery.domains.content.vo.pay.PaymentCardVO;

import java.util.List;

public interface PaymentCardService {

	PaymentCardVO getRandomAvailableByUserId(int userId, double amount);

	List<PaymentCardVO> getAvailableCardsByUserId(int userId, double amount);

	PaymentCardVO getAvailableByUserId(int userId, String cardId, double amount);

	PaymentCardVO getAvailableById(int cardId, double amount);

}