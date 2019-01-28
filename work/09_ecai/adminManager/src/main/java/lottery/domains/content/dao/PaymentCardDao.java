package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.PaymentCard;

public interface PaymentCardDao {
	
	List<PaymentCard> listAll();
	
	PaymentCard getById(int id);
	
	int getOverload();
	
	boolean add(PaymentCard entity);
	
	boolean update(PaymentCard entity);

	boolean addUsedCredits(int cardId, double usedCredits);
	
	boolean delete(int id);
	
}