package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.encrypt.DESUtil;
import lottery.domains.content.biz.PaymentCardService;
import lottery.domains.content.dao.PaymentCardDao;
import lottery.domains.content.dao.read.UserMainReportReadDao;
import lottery.domains.content.entity.PaymentCard;
import lottery.domains.content.vo.pay.PaymentCardVO;
import lottery.domains.pool.DataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {
	private static final String SECRET = "h/:#l^e>c*/thZeaKec)Ail{(My)!p";
	private static final DESUtil DES = DESUtil.getInstance();

	/**
	 * DAO
	 */
	@Autowired
	private PaymentCardDao paymentCardDao;

	@Autowired
	private UserMainReportReadDao uMainReportReadDao;

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@Override
	@Transactional(readOnly = true)
	public PaymentCardVO getRandomAvailableByUserId(int userId, double amount) {
		double totalRechargeMoney = uMainReportReadDao.getTotalRecharge(userId);
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();

		// 会员总共在这张卡的充值
		criterions.add(Restrictions.and(
				Restrictions.le("minTotalRecharge", totalRechargeMoney + amount),
				Restrictions.ge("maxTotalRecharge", totalRechargeMoney + amount)));

		// 会员单次充值
		if (amount > 0.0) {
			criterions.add(Restrictions.and(Restrictions.le("minUnitRecharge", amount),
					Restrictions.ge("maxUnitRecharge", amount)));
		}

		// 状态正常
		criterions.add(Restrictions.eq("status", 0));

		// 按最小使用排序
		orders.add(Order.asc("usedCredits"));

		List<PaymentCard> clist = paymentCardDao.find(criterions, orders);

		if (CollectionUtils.isEmpty(clist)) {
			return null;
		}

		// 挑选卡
		PaymentCardVO cardVO = null;
		for (PaymentCard tmpBean : clist) {
			if (isAvailable(tmpBean, amount)) {
				cardVO = new PaymentCardVO(tmpBean, dataFactory);
				break;
			}
		}

		if (cardVO != null) {
			//decryptCard(cardVO);
		}

		return cardVO;
	}

	@Override
	public List<PaymentCardVO> getAvailableCardsByUserId(int userId, double amount) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();

		// 会员单次充值
		criterions.add(Restrictions.and(
				Restrictions.le("minUnitRecharge", amount),
				Restrictions.ge("maxUnitRecharge", amount)));

		// 状态正常
		criterions.add(Restrictions.eq("status", 0));

		// 按最小使用排序
		orders.add(Order.asc("usedCredits"));

		List<PaymentCard> clist = paymentCardDao.find(criterions, orders);

		if (CollectionUtils.isEmpty(clist)) {
			return null;
		}

		List<PaymentCardVO> availableCards = new ArrayList<>();
		if (!CollectionUtils.isEmpty(clist)) {
			for (PaymentCard paymentCard : clist) {
				PaymentCardVO cardVO = null;
				if (isAvailable(paymentCard, amount)) {
					String decryptBranchName = DES.decryptStr(paymentCard.getBranchName(), SECRET);
					String decryptCardName = DES.decryptStr(paymentCard.getCardName(), SECRET);
					String decryptCardId = DES.decryptStr(paymentCard.getCardId(), SECRET);
					paymentCard.setBranchName(decryptBranchName);
					paymentCard.setCardName(decryptCardName);
					paymentCard.setCardId(decryptCardId);
					cardVO = new PaymentCardVO(paymentCard, dataFactory);
					
				}

				if (cardVO != null) {
					//decryptCard(paymentCard);
					availableCards.add(cardVO);
				}
			}
		}

		return availableCards;
	}

	@Override
	public PaymentCardVO getAvailableByUserId(int userId, String cardId, double amount) {
		List<PaymentCardVO> availableCards = getAvailableCardsByUserId(userId, amount);

		for (PaymentCardVO availableCard : availableCards) {
			if (cardId.equals(availableCard.getCardId())) {
				return availableCard;
			}
		}

		return null;
	}

	private boolean isAvailable(PaymentCard card, double amount) {
		if (card.getStatus() != 0) {
			return false;
		}


		double totalCredits = card.getTotalCredits();
		double usedCredits = card.getUsedCredits();
		if (usedCredits+amount >= totalCredits) {
			return false;
		}



		if (StringUtils.isNotEmpty(card.getStartTime()) && StringUtils.isNotEmpty(card.getEndTime())) {
			String serviceTime = card.getStartTime() + "~" + card.getEndTime();
			if (StringUtil.isServiceTime(new Moment(), serviceTime)) {
				return true;
			}

			return false;
		}
		else {
			return true;
		}
	}

	private void decryptCard(PaymentCardVO cardVO) {
		String decryptBranchName = DES.decryptStr(cardVO.getBranchName(), SECRET);
		String decryptCardName = DES.decryptStr(cardVO.getCardName(), SECRET);
		String decryptCardId = DES.decryptStr(cardVO.getCardId(), SECRET);

		cardVO.setBranchName(decryptBranchName);
		cardVO.setCardName(decryptCardName);
		cardVO.setCardId(decryptCardId);
	}

	private void decryptCard(PaymentCard card) {
		String decryptBranchName = DES.decryptStr(card.getBranchName(), SECRET);
		String decryptCardName = DES.decryptStr(card.getCardName(), SECRET);
		String decryptCardId = DES.decryptStr(card.getCardId(), SECRET);

		card.setBranchName(decryptBranchName);
		card.setCardName(decryptCardName);
		card.setCardId(decryptCardId);
	}

	@Override
	public PaymentCardVO getAvailableById(int cardId, double amount) {
		PaymentCard paymentCard = paymentCardDao.getAvailableById(cardId);
		if (paymentCard == null) {
			return null;
		}

		if (isAvailable(paymentCard, amount)) {
			PaymentCardVO paymentCardVO = new PaymentCardVO(paymentCard, dataFactory);
			decryptCard(paymentCardVO);
			return paymentCardVO;
		}

		return null;
	}
}