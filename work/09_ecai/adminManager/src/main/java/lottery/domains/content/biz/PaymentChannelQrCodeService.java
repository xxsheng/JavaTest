package lottery.domains.content.biz;

import lottery.domains.content.entity.PaymentChannelQrCode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;


public interface PaymentChannelQrCodeService {
	
	List<PaymentChannelQrCode> listAll();
	
	List<PaymentChannelQrCode> listAll(List<Criterion> criterions, List<Order> orders);
	
	List<PaymentChannelQrCode> getByChannelId(int channelId);
	
	PaymentChannelQrCode getById(int id);

	boolean add(PaymentChannelQrCode entity);
	
	boolean update(PaymentChannelQrCode entity);
	
	boolean delete(int id);
	
}