package lottery.domains.content.dao;

import lottery.domains.content.entity.PaymentChannel;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface PaymentChannelDao {
	
	List<PaymentChannel> listAll();
	
	List<PaymentChannel> listAll(List<Criterion> criterions, List<Order> orders);

	PaymentChannel getById(int id);

	int getOverload();

	boolean add(PaymentChannel entity);
	
	boolean update(PaymentChannel entity);
	
	boolean delete(int id);

	boolean modSequence(int currSequence, int addSequence);

	boolean batchModSequence(int sequence);

	boolean updateSequence(int id, int sequence);
	
	PaymentChannel getBySequence(int sequence);
	
	List<PaymentChannel> getBySequenceUp(int sequence);
	
	List<PaymentChannel> getBySequenceDown(int sequence);
	
	int getMaxSequence();
	
	int getTotal();

	boolean addUsedCredits(int id, double credit);
}