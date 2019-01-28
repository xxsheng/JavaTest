package lottery.domains.content.biz.impl;

import javautils.encrypt.DESUtil;
import lottery.domains.content.biz.PaymentCardService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.PaymentCardDao;
import lottery.domains.content.entity.PaymentCard;
import lottery.domains.content.global.DbServerSyncEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {
	private static final String SECRET = "h/:#l^e>c*/thZeaKec)Ail{(My)!p";
	private static final DESUtil DES = DESUtil.getInstance();

	@Autowired
	private PaymentCardDao paymentCardDao;

	@Autowired
	private DbServerSyncDao dbServerSyncDao;

	@Override
	public List<PaymentCard> listAll() {
		List<PaymentCard> paymentCards = paymentCardDao.listAll();
		for (PaymentCard paymentCard : paymentCards) {
			String decryptBranchName = DES.decryptStr(paymentCard.getBranchName(), SECRET);
			String decryptCardName = DES.decryptStr(paymentCard.getCardName(), SECRET);
			String decryptCardId = DES.decryptStr(paymentCard.getCardId(), SECRET);

			paymentCard.setBranchName(decryptBranchName);
			paymentCard.setCardName(decryptCardName);
			paymentCard.setCardId(decryptCardId);
		}
		return paymentCards;
	}

	@Override
	public PaymentCard getById(int id) {
		PaymentCard card = paymentCardDao.getById(id);
		String decryptBranchName = DES.decryptStr(card.getBranchName(), SECRET);
		String decryptCardName = DES.decryptStr(card.getCardName(), SECRET);
		String decryptCardId = DES.decryptStr(card.getCardId(), SECRET);

		card.setBranchName(decryptBranchName);
		card.setCardName(decryptCardName);
		card.setCardId(decryptCardId);
		return card;
	}

	@Override
	public boolean add(int bankId, String branchName, String cardName, String cardId,
					   double totalCredits, double minTotalRecharge,
					   double maxTotalRecharge, String startTime,
					   String endTime, double minUnitRecharge, double maxUnitRecharge) {

		String encryptCardName = DES.encryptStr(cardName, SECRET);
		String encryptCardId = DES.encryptStr(cardId, SECRET);
		String encryptBranchName = DES.encryptStr(branchName, SECRET);

		int status = 0;
		double usedCredits = 0;
		PaymentCard entity = new PaymentCard(bankId, encryptCardName, encryptCardId, encryptBranchName, totalCredits, usedCredits, minTotalRecharge, maxTotalRecharge, startTime, endTime, minUnitRecharge, maxUnitRecharge, status);
		entity.setBranchName(encryptBranchName);
		boolean added = paymentCardDao.add(entity);
		if (added) {
			dbServerSyncDao.update(DbServerSyncEnum.PAYMENT_CARD);
		}
		return added;
	}

	@Override
	public boolean edit(int id, int bankId, String branchName, String cardName,
						String cardId, double totalCredits,
						double minTotalRecharge, double maxTotalRecharge,
						String startTime, String endTime, double minUnitRecharge, double maxUnitRecharge) {
		PaymentCard entity = paymentCardDao.getById(id);
		if (entity != null) {

			String encryptCardName = DES.encryptStr(cardName, SECRET);
			String encryptCardId = DES.encryptStr(cardId, SECRET);
			String encryptBranchName = DES.encryptStr(branchName, SECRET);

			entity.setBankId(bankId);
			entity.setCardName(encryptCardName);
			entity.setCardId(encryptCardId);
			entity.setBranchName(encryptBranchName);
			entity.setTotalCredits(totalCredits);
			entity.setMinTotalRecharge(minTotalRecharge);
			entity.setMaxTotalRecharge(maxTotalRecharge);
			entity.setStartTime(startTime);
			entity.setEndTime(endTime);
			entity.setMinUnitRecharge(minUnitRecharge);
			entity.setMaxUnitRecharge(maxUnitRecharge);
			boolean updated = paymentCardDao.update(entity);
			if (updated) {
				dbServerSyncDao.update(DbServerSyncEnum.PAYMENT_CARD);
			}
			return updated;
		}
		return false;
	}

	@Override
	public boolean updateStatus(int id, int status) {
		PaymentCard entity = paymentCardDao.getById(id);
		if (entity != null) {
			entity.setStatus(status);
			boolean updated = paymentCardDao.update(entity);
			if (updated) {
				dbServerSyncDao.update(DbServerSyncEnum.PAYMENT_CARD);
			}
			return updated;
		}
		return false;
	}

	@Override
	public boolean resetCredits(int id) {
		PaymentCard entity = paymentCardDao.getById(id);
		if (entity != null) {
			entity.setUsedCredits(0);
			boolean updated = paymentCardDao.update(entity);
			if (updated) {
				dbServerSyncDao.update(DbServerSyncEnum.PAYMENT_CARD);
			}
			return updated;
		}
		return false;
	}

	@Override
	public boolean addUsedCredits(int cardId, double usedCredits) {
		boolean updated = paymentCardDao.addUsedCredits(cardId, usedCredits);
		if (updated) {
			dbServerSyncDao.update(DbServerSyncEnum.PAYMENT_CARD);
		}
		return updated;
	}

	@Override
	public boolean delete(int id) {
		boolean deleted = paymentCardDao.delete(id);
		if (deleted) {
			dbServerSyncDao.update(DbServerSyncEnum.PAYMENT_CARD);
		}
		return deleted;
	}
}