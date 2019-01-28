package lottery.domains.content.dao;

import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface PaymentChannelDao {
	
	PaymentChannel getById(int id);
	
	PaymentChannel getAvailableById(int id);
	
	List<PaymentChannel> find(List<Criterion> criterions, List<Order> orders);

	List<PaymentChannel> listAll();
}