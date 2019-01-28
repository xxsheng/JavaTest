package lottery.domains.content.biz.impl;

import javautils.encrypt.PaymentChannelEncrypt;
import lottery.domains.content.biz.PaymentChannelQrCodeService;
import lottery.domains.content.dao.PaymentChannelQrCodeDao;
import lottery.domains.content.entity.PaymentChannelQrCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentChannelQrCodeServiceImpl implements PaymentChannelQrCodeService {
	
	@Autowired
	private PaymentChannelQrCodeDao paymentChannelQrCodeDao;

	@Override
	public List<PaymentChannelQrCode> listAll() {
		List<PaymentChannelQrCode> paymentChannelQrCodes = paymentChannelQrCodeDao.listAll();
		if (CollectionUtils.isNotEmpty(paymentChannelQrCodes)) {
			for (PaymentChannelQrCode paymentChannelQrCode : paymentChannelQrCodes) {
				decryptSensitiveProperties(paymentChannelQrCode);
			}
		}
		return paymentChannelQrCodes;
	}

	@Override
	public List<PaymentChannelQrCode> listAll(List<Criterion> criterions, List<Order> orders) {
		List<PaymentChannelQrCode> paymentChannelQrCodes = paymentChannelQrCodeDao.listAll(criterions, orders);
		if (CollectionUtils.isNotEmpty(paymentChannelQrCodes)) {
			for (PaymentChannelQrCode paymentChannelQrCode : paymentChannelQrCodes) {
				decryptSensitiveProperties(paymentChannelQrCode);
			}
		}
		return paymentChannelQrCodes;
	}

	@Override
	public List<PaymentChannelQrCode> getByChannelId(int channelId) {
		List<PaymentChannelQrCode> paymentChannelQrCodes = paymentChannelQrCodeDao.getByChannelId(channelId);
		if (CollectionUtils.isNotEmpty(paymentChannelQrCodes)) {
			for (PaymentChannelQrCode paymentChannelQrCode : paymentChannelQrCodes) {
				decryptSensitiveProperties(paymentChannelQrCode);
			}
		}
		return paymentChannelQrCodes;
	}
	
	@Override
	public PaymentChannelQrCode getById(int id) {
		PaymentChannelQrCode paymentChannelQrCode = paymentChannelQrCodeDao.getById(id);
		if (paymentChannelQrCode != null) {
			decryptSensitiveProperties(paymentChannelQrCode);
		}
		return paymentChannelQrCode;
	}

	@Override
	public boolean add(PaymentChannelQrCode entity) {
		return paymentChannelQrCodeDao.add(entity);
	}

	@Override
	public boolean update(PaymentChannelQrCode entity) {
		return paymentChannelQrCodeDao.update(entity);
	}

	@Override
	public boolean delete(int id) {
		return paymentChannelQrCodeDao.delete(id);
	}

	private void decryptSensitiveProperties(PaymentChannelQrCode qrCode) {
		if (StringUtils.isNotEmpty(qrCode.getQrUrlCode())) {
			String qrUrlCode = PaymentChannelEncrypt.decrypt(qrCode.getQrUrlCode()); // 二维码解密
			qrCode.setQrUrlCode(qrUrlCode);
		}
	}

}
