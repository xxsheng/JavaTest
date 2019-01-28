package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserWithdrawService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserWithdrawDao;
import lottery.domains.content.dao.UserWithdrawLimitDao;
import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.global.Global;
import lottery.domains.jobs.MailJob;
import lottery.domains.pool.DataFactory;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserWithdrawServiceImpl implements UserWithdrawService {
	private static final Logger log = LoggerFactory.getLogger(UserWithdrawServiceImpl.class);
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserWithdrawDao uWithdrawDao;
	
	@Autowired
	private UserWithdrawLimitDao uWithdrawLimitDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;

	@Autowired
	private MailJob mailJob;
	
	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
	
	/**
	 * 生成流水号
	 */
	private String billno() {
		// return new Moment().format("yyMMddss") + OrderUtil.getBillno(12, true);
		return new Moment().format("yyMMddHHmmss") + RandomStringUtils.random(8, true, true);
		// return ObjectId.get().toString();
	}
	
	@Override
	public synchronized boolean apply(int userId, UserCard uCard, double amount, double recMoney, double feeMoney) {
		if (amount <= 0) return false;

		PaymentBank bank = dataFactory.getPaymentBank(uCard.getBankId());
		if (bank == null) return false;

		User uBean = uDao.getById(userId);
		if (uBean == null) return false;

		if(amount > uBean.getTotalMoney()) return false;

		boolean uFlag = uDao.updateTotalMoney(userId, -amount);
		if(uFlag) {
			// 重置提款消费累计
			uWithdrawLimitDao.resetLimit(userId);

			String billno = billno();
			String time = new Moment().toSimpleTime();
			double beforeMoney = uBean.getTotalMoney();
			double afterMoney = beforeMoney - amount;
			int status = 0;
			int lockStatus = 0;
			int checkStatus = 0;
			String infos = "取现：" + amount;
			UserWithdraw entity = new UserWithdraw(billno, userId, amount, beforeMoney, afterMoney, recMoney, feeMoney, time, status, lockStatus, checkStatus);
			entity.setInfos(infos);
			// 银行信息
			entity.setBankName(bank.getName());
			entity.setBankBranch(uCard.getBankBranch());
			entity.setCardName(uCard.getCardName());
			entity.setCardId(uCard.getCardId());
			boolean flag = uWithdrawDao.add(entity);
			if(flag) {
				// 生成流水
				uBillService.addWithdrawBill(entity, Global.BILL_ACCOUNT_MAIN, amount, uBean, "主账户取现：" + amount);
			}
			return flag;
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public int getDateCashCount(int userId, String date) {
		return uWithdrawDao.getDateCashCount(userId, date);
	}

	@Override
	@Transactional(readOnly = true)
	public double getDateCashMoney(int userId, String date) {
		return uWithdrawDao.getDateCashMoney(userId, date);
	}
}