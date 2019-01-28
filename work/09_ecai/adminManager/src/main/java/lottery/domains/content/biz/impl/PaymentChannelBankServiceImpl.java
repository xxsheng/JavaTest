package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.PaymentChannelBankService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.PaymentChannelBankDao;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.content.vo.payment.PaymentChannelBankVO;
import lottery.domains.pool.LotteryDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentChannelBankServiceImpl implements PaymentChannelBankService {
	
	@Autowired
	private PaymentChannelBankDao paymentChannelBankDao;

	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private DbServerSyncDao dbServerSyncDao;

	@Override
	public List<PaymentChannelBankVO> list(String type) {
		List<PaymentChannelBank> blist = paymentChannelBankDao.list(type);
		List<PaymentChannelBankVO> list = new ArrayList<>();
		for (PaymentChannelBank tmpBean : blist) {
			list.add(new PaymentChannelBankVO(tmpBean, lotteryDataFactory));
		}
		return list;
	}

	@Override
	public boolean updateStatus(int id, int status) {
		PaymentChannelBank entity = paymentChannelBankDao.getById(id);
		if(entity != null) {
			entity.setStatus(status);
			boolean result = paymentChannelBankDao.update(entity);
			if(result) {
				dbServerSyncDao.update(DbServerSyncEnum.PAYMENT_CHANNEL_BANK);
			}
			return result;
		}
		return false;
	}

	@Override
	public PaymentChannelBank getByChannelAndBankId(String channelCode, int bankId){
		return paymentChannelBankDao.getByChannelAndBankId(channelCode, bankId);
	}
}