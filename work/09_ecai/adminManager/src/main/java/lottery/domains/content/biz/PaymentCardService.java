package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.PaymentCard;

public interface PaymentCardService {

	List<PaymentCard> listAll();
	
	PaymentCard getById(int id);
	
	boolean add(int bankId, String branchName, String cardName, String cardId, double totalCredits, double minTotalRecharge, double maxTotalRecharge, String startTime, String endTime, double minUnitRecharge, double maxUnitRecharge);
	
	boolean edit(int id, int bankId, String branchName, String cardName, String cardId, double totalCredits, double minTotalRecharge, double maxTotalRecharge, String startTime, String endTime, double minUnitRecharge, double maxUnitRecharge);
	
	boolean updateStatus(int id, int status);
	
	boolean resetCredits(int id);

	boolean addUsedCredits(int cardId, double usedCredits);

	boolean delete(int id);
	
}