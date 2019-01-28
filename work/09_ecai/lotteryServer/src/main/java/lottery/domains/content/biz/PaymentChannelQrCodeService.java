package lottery.domains.content.biz;

import lottery.domains.content.entity.PaymentChannelQrCode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;


public interface PaymentChannelQrCodeService {
	
	List<PaymentChannelQrCode> listAll();
	
	List<PaymentChannelQrCode> listSweepTansferAll(List<Criterion> criterions, List<Order> orders);
	
	List<PaymentChannelQrCode> getByList(int paymentChannelId);
	
	PaymentChannelQrCode getById(int id);

	boolean add(PaymentChannelQrCode entity);
	
	boolean update(PaymentChannelQrCode entity);
	
	boolean delete(int id);
	
}