package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.PaymentCard;

public interface PaymentCardDao {
	
	PaymentCard getById(int id);
	
	PaymentCard getAvailableById(int id);

	List<PaymentCard> find(List<Criterion> criterions, List<Order> orders);
	
	List<PaymentCard> findListAvailable();
	
}