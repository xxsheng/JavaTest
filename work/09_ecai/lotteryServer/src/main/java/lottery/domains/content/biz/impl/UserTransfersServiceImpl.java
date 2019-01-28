package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.api.ag.AGAPI;
import lottery.domains.content.api.im.IMAPI;
import lottery.domains.content.api.pt.PTAPI;
import lottery.domains.content.api.sb.Win88SBAPI;
import lottery.domains.content.api.sb.Win88SBFundTransferResult;
import lottery.domains.content.biz.*;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserTransfersDao;
import lottery.domains.content.entity.*;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.domains.pool.payment.cfg.MoneyFormat;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserDailySettleValidate;
import lottery.web.content.validate.UserDividendValidate;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Service
public class UserTransfersServiceImpl implements UserTransfersService {
	private static final Logger log = LoggerFactory.getLogger(UserTransfersServiceImpl.class);

	@Value("${ag.actype}")
	private String agActype; // ag是否是试玩账号
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserTransfersDao uTransfersDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;
	
	@Autowired
	private UserSysMessageService uSysMessageService;
	
	@Autowired
	private UserWithdrawLimitService uWithdrawLimitService;
	
	@Autowired
	private UserLotteryReportService uLotteryReportService;

	@Autowired
	private PTAPI ptAPI;

	@Autowired
	private AGAPI agAPI;

	@Autowired
	private IMAPI imApi;

	@Autowired
	private Win88SBAPI sbapi;

	@Autowired
	private UserGameAccountService uGameAccountService;

	@Autowired
	private UserDailySettleValidate uDailySettleValidate;

	@Autowired
	private UserDividendValidate uDividendValidate;

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
	
	/**
	 * 生成流水号
	 */
	private String billno() {
		// return new Moment().format("yyyyMMdd") + OrderUtil.getBillno(8, true);
		// return ObjectId.get().toString();
		return new Moment().format("yyMMddHHmmss") + RandomStringUtils.random(8, true, true);
	}
	
	@Override
	public boolean transfersToSelf(WebJSON json, int userId, int toAccount, int fromAccount, double amount) {
		User uBean = uDao.getById(userId);
		//验证是否可以转账
		if (uBean.getAllowPlatformTransfers() !=1) {
			json.set(2, "2-1090");
			return false;
		}
		if (toAccount == fromAccount) {
			json.set(2, "2-7011");
			return false;
		}
		SysPlatform toPlatform = dataFactory.getSysPlatform(toAccount);
		if (toPlatform == null || toPlatform.getStatus() != 0) {
			json.set(2, "2-3002");
			return false;
		}
		SysPlatform fromPlatform = dataFactory.getSysPlatform(fromAccount);
		if(fromPlatform == null || fromPlatform.getStatus() != 0) {
			json.set(2, "2-3001");
			return false;
		}
		if (amount <= 0) {
			json.set(2, "2-6");
			return false;
		}
//		if (amount > 50000) {
//			json.set(2, "2-7013", 50000);
//			return false;
//		}

		double transOutBeforeMoney = 0;
		double transOutAfterMoney = 0;
		double transInBeforeMoney = 0;
		double transInAfterMoney = 0;
		String billno = billno();
		// 主账户 -> 彩票账户
		if(fromAccount == Global.BILL_ACCOUNT_MAIN && toAccount == Global.BILL_ACCOUNT_LOTTERY) {
			if (amount > uBean.getTotalMoney()) {
				json.set(2, "2-1109");
				return false;
			}

			// 验证是否有未发放的分红金额
			if (!uDividendValidate.validateUnIssue(json, userId)) {
				return false;
			}

			// 验证是否有未发放的日结金额
			if (!uDailySettleValidate.validateUnIssue(json, userId)) {
				return false;
			}

			transOutBeforeMoney = uBean.getTotalMoney();
			transInBeforeMoney = uBean.getLotteryMoney();
			boolean result = uDao.updateMoney(userId, -amount, amount, 0, 0, 0);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = transOutBeforeMoney - amount;
			transInAfterMoney = transInBeforeMoney + amount;
		}
		// 主账户 -> PT
		else if(fromAccount == Global.BILL_ACCOUNT_MAIN && toAccount == Global.BILL_ACCOUNT_PT) {
			if (amount > uBean.getTotalMoney()) {
				json.set(2, "2-1109");
				return false;
			}

			// 验证是否有未发放的分红金额
			if (!uDividendValidate.validateUnIssue(json, userId)) {
				return false;
			}
			// 验证是否有未发放的日结金额
			if (!uDailySettleValidate.validateUnIssue(json, userId)) {
				return false;
			}

			// 先减去主账户金额
			transOutBeforeMoney = uBean.getTotalMoney();
			boolean result = uDao.updateTotalMoney(userId, -amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = transOutBeforeMoney - amount;

			// 再给PT加钱
			UserGameAccount ugc;
			try {
				ugc = uGameAccountService.createIfNoAccount(json, userId, uBean.getUsername(), toAccount, 1);
			} catch (Exception e) {
				log.error("主账户转PT失败，创建PT账号失败", e);
				ugc = null;
			}
			if (ugc == null) {
				// 创建账号失败，把钱加回来
				log.error("主账户转PT失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
				uDao.updateTotalMoney(userId, amount);
				json.set(2, "2-7000");
				return false;
			}

			Double balance;
			try {
				balance = ptAPI.playerDeposit(json, ugc.getUsername(), amount, billno);
			} catch (Exception e) {
				log.error("主账户转PT失败，调PT转账接口时失败", e);
				balance = null;
			}
			if (balance == null) {
				// 转账失败，把钱加回来
				log.error("主账户转PT失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
				uDao.updateTotalMoney(userId, amount);
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			transInBeforeMoney = balance - amount;
			transInAfterMoney = balance;
		}
		// 主账户 -> AG
		else if(fromAccount == Global.BILL_ACCOUNT_MAIN && toAccount == Global.BILL_ACCOUNT_AG) {
			if (!"1".equals(agActype)) {
				// 试玩账号不允许转账
				json.set(2, "2-3002");
				return false;
			}

			if (!MathUtil.isInteger(amount) || amount < 1) {
				// AG平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}

			if (amount > uBean.getTotalMoney()) {
				json.set(2, "2-1109");
				return false;
			}

			// 验证是否有未发放的分红金额
			if (!uDividendValidate.validateUnIssue(json, userId)) {
				return false;
			}
			// 验证是否有未发放的日结金额
			if (!uDailySettleValidate.validateUnIssue(json, userId)) {
				return false;
			}

			// 先减去主账户金额
			transOutBeforeMoney = uBean.getTotalMoney();
			boolean result = uDao.updateTotalMoney(userId, -amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = transOutBeforeMoney - amount;

			boolean flag = agAPI.transferCreditNew(json, uBean.getUsername(), Double.valueOf(amount).intValue(), true);
			if (!flag) {
				// 转账失败，把钱加回来
				log.error("主账户转AG失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
				uDao.updateTotalMoney(userId, amount);
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}
			// 再给AG加钱
//			UserGameAccount ugc;
//			try {
//				ugc = uGameAccountService.createIfNoAccount(json, userId, uBean.getUsername(), toAccount, 1);
//			} catch (Exception e) {
//				log.error("主账户转AG失败，创建AG账号失败", e);
//				ugc = null;
//			}
//			if (ugc == null) {
//				// 创建账号失败，把钱加回来
//				log.error("主账户转AG失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
//				uDao.updateTotalMoney(userId, amount);
//				json.set(2, "2-8002");
//				return false;
//			}
//			String password = uGameAccountService.decryptPwd(ugc.getPassword());
//			try {
//				billno = agAPI.transferIn(json, ugc.getUsername(), password, Double.valueOf(amount).intValue());
//			} catch (Exception e) {
//				log.error("主账户转AG失败，调AG转账接口时失败", e);
//				billno = null;
//			}
//			if (billno == null) {
//				// 转账失败，把钱加回来
//				log.error("主账户转AG失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
//				uDao.updateTotalMoney(userId, amount);
//				if (json.getError() == 0) {
//					json.set(1, "1-1");
//				}
//				return false;
//			}

			Double balance = agAPI.getBalanceNew(json, uBean.getUsername());
			if (balance == null) {
				transInBeforeMoney = 0;
				transInAfterMoney = amount;
			}
			else {
				transInBeforeMoney = balance - amount;
				transInAfterMoney = balance;
			}
		}
		// 主账户 -> IM
		else if(fromAccount == Global.BILL_ACCOUNT_MAIN && toAccount == Global.BILL_ACCOUNT_IM) {
			if (!MathUtil.isInteger(amount) || amount < 1) {
				// IM平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}

			if (amount > uBean.getTotalMoney()) {
				json.set(2, "2-1109");
				return false;
			}

			UserGameAccount ugc = dataFactory.getGameAccount(userId, toAccount, 1);
			if (ugc == null) {
				json.set(2, "2-9007");
				return false;
			}

			// 验证是否有未发放的分红金额
			if (!uDividendValidate.validateUnIssue(json, userId)) {
				return false;
			}
			// 验证是否有未发放的日结金额
			if (!uDailySettleValidate.validateUnIssue(json, userId)) {
				return false;
			}

			// 先减去主账户金额
			transOutBeforeMoney = uBean.getTotalMoney();
			boolean result = uDao.updateTotalMoney(userId, -amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = transOutBeforeMoney - amount;

			String password = uGameAccountService.decryptPwd(ugc.getPassword());
			try {
				billno = imApi.deposit(json, ugc.getUsername(), password, MoneyFormat.pasMoney(amount));
			} catch (Exception e) {
				log.error("主账户转IM失败，调IM转账接口时失败", e);
				billno = null;
			}
			if (billno == null) {
				// 转账失败，把钱加回来
				log.error("主账户转IM失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
				uDao.updateTotalMoney(userId, amount);
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			Double balance = imApi.balance(json, ugc.getUsername());
			if (balance == null) {
				transInBeforeMoney = 0;
				transInAfterMoney = amount;
			}
			else {
				transInBeforeMoney = balance - amount;
				transInAfterMoney = balance;
			}
		}
		// 主账户 -> SB
		else if(fromAccount == Global.BILL_ACCOUNT_MAIN && toAccount == Global.BILL_ACCOUNT_SB) {
			if (!MathUtil.isInteger(amount) || amount < 1) {
				// 只能输入整数
				json.set(2, "2-8009");
				return false;
			}

			if (amount > uBean.getTotalMoney()) {
				json.set(2, "2-1109");
				return false;
			}

			UserGameAccount ugc = uGameAccountService.createIfNoAccount(json, userId, uBean.getUsername(), toAccount, 1);
			if (ugc == null) {
				return false;
			}

			// 验证是否有未发放的分红金额
			if (!uDividendValidate.validateUnIssue(json, userId)) {
				return false;
			}
			// 验证是否有未发放的日结金额
			if (!uDailySettleValidate.validateUnIssue(json, userId)) {
				return false;
			}

			// 先减去主账户金额
			transOutBeforeMoney = uBean.getTotalMoney();
			boolean result = uDao.updateTotalMoney(userId, -amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = transOutBeforeMoney - amount;

			try {
				Win88SBFundTransferResult win88SBFundTransferResult = sbapi.deposit(json, ugc.getUsername(), BigDecimal.valueOf(amount).intValue());
				if (win88SBFundTransferResult != null) {
					billno = win88SBFundTransferResult.getOpTransId();
					transInBeforeMoney = win88SBFundTransferResult.getData().getBeforeAmount();
					transInAfterMoney = win88SBFundTransferResult.getData().getAfterAmount();
				}
				else {
					billno = null;
				}
			} catch (Exception e) {
				log.error("主账户转SB失败，调SB转账接口时失败", e);
				billno = null;
			}
			if (StringUtils.isEmpty(billno)) {
				// 转账失败，把钱加回来
				log.error("主账户转SB失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
				uDao.updateTotalMoney(userId, amount);
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}
		}
		// 彩票账户 -> 主账户
		else if(fromAccount == 2 && toAccount == 1) {
			if (amount > uBean.getLotteryMoney()) {
				json.set(2, "2-1109");
				return false;
			}

			transOutBeforeMoney = uBean.getLotteryMoney();
			transInBeforeMoney = uBean.getTotalMoney();
			boolean result = uDao.updateMoney(userId, amount, -amount, 0, 0, 0);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = transOutBeforeMoney - amount;
			transInAfterMoney = transInBeforeMoney + amount;
		}
		// 彩票账户 -> PT
		else if(fromAccount == 2 && toAccount == 11) {
			if (amount > uBean.getLotteryMoney()) {
				json.set(2, "2-1109");
				return false;
			}

			// 验证是否有未发放的分红金额
			if (!uDividendValidate.validateUnIssue(json, userId)) {
				return false;
			}
			// 验证是否有未发放的日结金额
			if (!uDailySettleValidate.validateUnIssue(json, userId)) {
				return false;
			}

			// 减去彩票账户金额
			transOutBeforeMoney = uBean.getLotteryMoney();
			boolean result = uDao.updateLotteryMoney(userId, -amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = transOutBeforeMoney - amount;

			// 再给PT加钱
			UserGameAccount ugc;
			try {
				ugc = uGameAccountService.createIfNoAccount(json, userId, uBean.getUsername(), toAccount, 1);
			} catch (Exception e) {
				log.error("彩票账户转PT失败，创建PT账号失败", e);
				ugc = null;
			}
			if (ugc == null) {
				// 创建账号失败，把钱加回来
				log.error("彩票账户转PT失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
				uDao.updateLotteryMoney(userId, amount);
				json.set(2, "2-7000");
				return false;
			}
			Double balance;
			try {
				balance = ptAPI.playerDeposit(json, ugc.getUsername(), amount, billno);
			} catch (Exception e) {
				log.error("彩票账户转PT失败，调PT转账接口时失败", e);
				balance = null;
			}
			if (balance == null) {
				// 转账失败，把钱加回来
				log.error("彩票账户转PT失败，把用户的钱加回来，转账单号：" + billno + ",用户名：" + uBean.getUsername() + ",金额：" + amount);
				uDao.updateLotteryMoney(userId, amount);
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			transInBeforeMoney = balance - amount;
			transInAfterMoney = balance;
		}
		// 彩票账户 -> AG
		else if(fromAccount == 2 && toAccount == 4) {
			if (!"1".equals(agActype)) {
				// 试玩账号不允许转账
				json.set(2, "2-3002");
				return false;
			}

			if (!MathUtil.isInteger(amount) || amount < 1) {
				// AG平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}

			if (amount > uBean.getLotteryMoney()) {
				json.set(2, "2-1109");
				return false;
			}

			// 验证是否有未发放的分红金额
			if (!uDividendValidate.validateUnIssue(json, userId)) {
				return false;
			}
			// 验证是否有未发放的日结金额
			if (!uDailySettleValidate.validateUnIssue(json, userId)) {
				return false;
			}

			// 减去彩票账户金额
			transOutBeforeMoney = uBean.getLotteryMoney();
			boolean result = uDao.updateLotteryMoney(userId, -amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = transOutBeforeMoney - amount;


			boolean flag = agAPI.transferCreditNew(json, uBean.getUsername(), Double.valueOf(amount).intValue(), true);
			if (!flag) {
				// 转账失败，把钱加回来
				log.error("主账户转AG失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
				uDao.updateTotalMoney(userId, amount);
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			// 再给AG加钱
//			UserGameAccount ugc;
//			try {
//				ugc = uGameAccountService.createIfNoAccount(json, userId, uBean.getUsername(), toAccount, 1);
//			} catch (Exception e) {
//				log.error("彩票账户转AG失败，创建AG账号失败", e);
//				ugc = null;
//			}
//			if (ugc == null) {
//				// 创建账号失败，把钱加回来
//				log.error("彩票账户转AG失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
//				uDao.updateLotteryMoney(userId, amount);
//				json.set(2, "2-8002");
//				return false;
//			}
//			String password = uGameAccountService.decryptPwd(ugc.getPassword());
//			try {
//				billno = agAPI.transferIn(json, ugc.getUsername(), password, Double.valueOf(amount).intValue());
//			} catch (Exception e) {
//				log.error("彩票账户转AG失败，调AG转账接口时失败", e);
//				billno = null;
//			}
//			if (billno == null) {
//				// 转账失败，把钱加回来
//				log.error("彩票账户转AG失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
//				uDao.updateLotteryMoney(userId, amount);
//				if (json.getError() == 0) {
//					json.set(1, "1-1");
//				}
//				return false;
//			}

			Double balance = agAPI.getBalanceNew(json, uBean.getUsername());
			if (balance == null) {
				transInBeforeMoney = 0;
				transInAfterMoney = amount;
			}
			else {
				transInBeforeMoney = balance - amount;
				transInAfterMoney = balance;
			}
		}
		// 彩票账户 -> IM
		else if(fromAccount == Global.BILL_ACCOUNT_LOTTERY && toAccount == Global.BILL_ACCOUNT_IM) {
			if (!MathUtil.isInteger(amount) || amount < 1) {
				// 平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}

			if (amount > uBean.getLotteryMoney()) {
				json.set(2, "2-1109");
				return false;
			}

			UserGameAccount ugc = dataFactory.getGameAccount(userId, toAccount, 1);
			if (ugc == null) {
				json.set(2, "2-9007");
				return false;
			}

			// 验证是否有未发放的分红金额
			if (!uDividendValidate.validateUnIssue(json, userId)) {
				return false;
			}
			// 验证是否有未发放的日结金额
			if (!uDailySettleValidate.validateUnIssue(json, userId)) {
				return false;
			}

			// 减去彩票账户金额
			transOutBeforeMoney = uBean.getLotteryMoney();
			boolean result = uDao.updateLotteryMoney(userId, -amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = transOutBeforeMoney - amount;

			String password = uGameAccountService.decryptPwd(ugc.getPassword());
			try {
				billno = imApi.deposit(json, ugc.getUsername(), password, MoneyFormat.pasMoney(amount));
			} catch (Exception e) {
				log.error("彩票账户转IM失败，调IM转账接口时失败", e);
				billno = null;
			}
			if (billno == null) {
				// 转账失败，把钱加回来
				log.error("彩票账户转IM失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
				uDao.updateLotteryMoney(userId, amount);
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			Double balance = imApi.balance(json, ugc.getUsername());
			if (balance == null) {
				transInBeforeMoney = 0;
				transInAfterMoney = amount;
			}
			else {
				transInBeforeMoney = balance - amount;
				transInAfterMoney = balance;
			}
		}
		// 彩票账户 -> SB
		else if(fromAccount == Global.BILL_ACCOUNT_LOTTERY && toAccount == Global.BILL_ACCOUNT_SB) {
			if (!MathUtil.isInteger(amount) || amount < 1) {
				// 平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}

			if (amount > uBean.getLotteryMoney()) {
				json.set(2, "2-1109");
				return false;
			}

			UserGameAccount ugc = uGameAccountService.createIfNoAccount(json, userId, uBean.getUsername(), toAccount, 1);
			if (ugc == null) {
				return false;
			}

			// 验证是否有未发放的分红金额
			if (!uDividendValidate.validateUnIssue(json, userId)) {
				return false;
			}
			// 验证是否有未发放的日结金额
			if (!uDailySettleValidate.validateUnIssue(json, userId)) {
				return false;
			}

			// 减去彩票账户金额，先减钱防止中途消费
			transOutBeforeMoney = uBean.getLotteryMoney();
			boolean result = uDao.updateLotteryMoney(userId, -amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = transOutBeforeMoney - amount;

			try {
				Win88SBFundTransferResult win88SBFundTransferResult = sbapi.deposit(json, ugc.getUsername(), BigDecimal.valueOf(amount).intValue());
				if (win88SBFundTransferResult != null) {
					billno = win88SBFundTransferResult.getOpTransId();
					transInBeforeMoney = win88SBFundTransferResult.getData().getBeforeAmount();
					transInAfterMoney = win88SBFundTransferResult.getData().getAfterAmount();
				}
				else {
					billno = null;
				}
			} catch (Exception e) {
				log.error("彩票账户转SB失败，调SB转账接口时失败", e);
				billno = null;
			}
			if (StringUtils.isEmpty(billno)) {
				// 转账失败，把钱加回来
				log.error("彩票账户转SB失败，把用户的钱加回来,用户名：" + uBean.getUsername() + ",金额：" + amount);
				uDao.updateLotteryMoney(userId, amount);
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}
		}
		// PT -> 主账户
		else if(fromAccount == 11 && toAccount == 1) {
			// 先给PT减钱
			UserGameAccount ugc = dataFactory.getGameAccount(userId, fromAccount, 1);
			if (ugc == null) {
				// 没有账号，返回余额不足
				json.set(2, "2-7005");
				return false;
			}

			Double balance = ptAPI.playerBalance(json, ugc.getUsername());
			if (balance == null) {
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			if (balance < amount) {
				// 没有账号，返回余额不足
				json.set(2, "2-7005");
				return false;
			}

			try {
				balance = ptAPI.playerWithdraw(json, ugc.getUsername(), amount, billno);
			} catch (Exception e) {
				log.error("PT转主账户失败,用户名：" + uBean.getUsername() + ",金额：" + amount, e);
				balance = null;
			}
			if (balance == null) {
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			// 再查一次，防止请求远程API过程中用户进行消费
			uBean = uDao.getById(userId);

			// 主账户加钱
			transOutBeforeMoney = balance + amount;
			transInBeforeMoney = uBean.getTotalMoney();
			boolean result = uDao.updateTotalMoney(userId, amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = balance;
			transInAfterMoney = transInBeforeMoney + amount;
		}
		// PT -> 彩票账户
		else if(fromAccount == 11 && toAccount == 2) {
			// 先给PT减钱
			UserGameAccount ugc = dataFactory.getGameAccount(userId, fromAccount, 1);
			if (ugc == null) {
				// 没有账号，返回余额不足
				json.set(2, "2-7005");
				return false;
			}

			Double balance = ptAPI.playerBalance(json, ugc.getUsername());
			if (balance == null) {
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}


			if (balance < amount) {
				// 没有账号，返回余额不足
				json.set(2, "2-7005");
				return false;
			}

			try {
				balance = ptAPI.playerWithdraw(json, ugc.getUsername(), amount, billno);
			} catch (Exception e) {
				log.error("PT转彩票账户失败,用户名：" + uBean.getUsername() + ",金额：" + amount, e);
				balance = null;
			}
			if (balance == null) {
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			// 再查一次，防止请求远程API过程中用户进行消费
			uBean = uDao.getById(userId);

			// 彩票账户加钱
			transOutBeforeMoney = balance + amount;
			transInBeforeMoney = uBean.getLotteryMoney();
			boolean result = uDao.updateLotteryMoney(userId, amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transOutAfterMoney = balance;
			transInAfterMoney = transInBeforeMoney + amount;
		}
		// AG -> 主账
		else if(fromAccount == 4 && toAccount == 1) {
			if (!"1".equals(agActype)) {
				// 试玩账号不允许转账
				json.set(2, "2-3001");
				return false;
			}

			if (!MathUtil.isInteger(amount) || amount < 1) {
				// AG平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}



			Double agGalance = agAPI.getBalanceNew(json, uBean.getUsername());
			if (agGalance == null || amount > agGalance) {
				// 余额不足
				json.set(2, "2-8005");
				return false;
			}
			// 先给AG减钱
			boolean flag = agAPI.transferCreditNew(json, uBean.getUsername(), Double.valueOf(amount).intValue(), false);
			if(!flag){
				log.error("AG转主账户失败,用户名：" + uBean.getUsername() + ",金额：" + amount);
				json.set(1, "1-1");
				return false;
			}


//			UserGameAccount agAccount = dataFactory.getGameAccount(userId, fromAccount, 1);
//			if (agAccount == null) {
//				// 没有账号，返回余额不足
//				json.set(2, "2-8005");
//				return false;
//			}
//			String password = uGameAccountService.decryptPwd(agAccount.getPassword());

//			Double agGalance = agAPI.getBalance(json, agAccount.getUsername(), password);
//			if (agGalance == null || amount > agGalance) {
//				// 余额不足
//				json.set(2, "2-8005");
//				return false;
//			}

//			try {
//				billno = agAPI.transferOut(json, agAccount.getUsername(), password, Double.valueOf(amount).intValue());
//			} catch (Exception e) {
//				log.error("AG转主账户失败,用户名：" + uBean.getUsername() + ",金额：" + amount, e);
//				billno = null;
//			}
//			if (billno == null) {
//				if (json.getError() == 0) {
//					json.set(1, "1-1");
//				}
//				return false;
//			}

			if (agGalance == null) {
				transOutBeforeMoney = 0;
				transOutAfterMoney = 0;
			}
			else {
				transOutBeforeMoney = agGalance;
				transOutAfterMoney = agGalance - amount;
			}

			// 再查一次，防止请求远程API过程中用户进行消费
			uBean = uDao.getById(userId);

			// 主账户加钱
			transInBeforeMoney = uBean.getTotalMoney();
			boolean result = uDao.updateTotalMoney(userId, amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transInAfterMoney = transInBeforeMoney + amount;
		}
		// AG -> 彩账
		else if(fromAccount == 4 && toAccount == 2) {
			if (!"1".equals(agActype)) {
				// 试玩账号不允许转账
				json.set(2, "2-3001");
				return false;
			}

			if (!MathUtil.isInteger(amount) || amount < 1) {
				// AG平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}



			// 先给AG减钱
			Double agGalance = agAPI.getBalanceNew(json, uBean.getUsername());
			if (agGalance == null || amount > agGalance) {
				// 余额不足
				json.set(2, "2-8005");
				return false;
			}
			boolean flag = agAPI.transferCreditNew(json, uBean.getUsername(), Double.valueOf(amount).intValue(), false);
			if(!flag){
				log.error("AG转主账户失败,用户名：" + uBean.getUsername() + ",金额：" + amount);
				json.set(1, "1-1");
				return false;
			}
//			// 先给AG减钱
//			UserGameAccount agAccount = dataFactory.getGameAccount(userId, fromAccount, 1);
//			if (agAccount == null) {
//				// 没有账号，返回余额不足
//				json.set(2, "2-8005");
//				return false;
//			}
//			String password = uGameAccountService.decryptPwd(agAccount.getPassword());
//
//			Double agGalance = agAPI.getBalance(json, agAccount.getUsername(), password);
//			if (agGalance == null || amount > agGalance) {
//				// 余额不足
//				json.set(2, "2-8005");
//				return false;
//			}
//
//			try {
//				billno = agAPI.transferOut(json, agAccount.getUsername(), password, Double.valueOf(amount).intValue());
//			} catch (Exception e) {
//				log.error("AG转彩票账户失败,用户名：" + uBean.getUsername() + ",金额：" + amount, e);
//				billno = null;
//			}
//			if (billno == null) {
//				if (json.getError() == 0) {
//					json.set(1, "1-1");
//				}
//				return false;
//			}

			if (agGalance == null) {
				transOutBeforeMoney = 0;
				transOutAfterMoney = 0;
			}
			else {
				transOutBeforeMoney = agGalance;
				transOutAfterMoney = agGalance - amount;
			}

			// 再查一次，防止请求远程API过程中用户进行消费
			uBean = uDao.getById(userId);

			// 彩票账户加钱
			transInBeforeMoney = uBean.getLotteryMoney();
			boolean result = uDao.updateLotteryMoney(userId, amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transInAfterMoney = transInBeforeMoney + amount;
		}
		// IM -> 主账
		else if(fromAccount == Global.BILL_ACCOUNT_IM && toAccount == Global.BILL_ACCOUNT_MAIN) {
			if (!MathUtil.isInteger(amount) || amount < 1) {
				// 平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}

			UserGameAccount ugc = dataFactory.getGameAccount(userId, toAccount, 1);
			if (ugc == null) {
				json.set(2, "2-9007");
				return false;
			}
			String password = uGameAccountService.decryptPwd(ugc.getPassword());

			Double balance = imApi.balance(json, ugc.getUsername());
			if (balance == null || amount > balance) {
				// 余额不足
				json.set(2, "2-8005");
				return false;
			}

			try {
				billno = imApi.withdraw(json, ugc.getUsername(), password, MoneyFormat.pasMoney(amount));
			} catch (Exception e) {
				log.error("IM转主账户失败,用户名：" + uBean.getUsername() + ",金额：" + amount, e);
				billno = null;
			}
			if (billno == null) {
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			if (balance == null) {
				transOutBeforeMoney = 0;
				transOutAfterMoney = 0;
			}
			else {
				transOutBeforeMoney = balance;
				transOutAfterMoney = balance - amount;
			}

			// 主账户加钱
			transInBeforeMoney = uBean.getTotalMoney();
			boolean result = uDao.updateTotalMoney(userId, amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transInAfterMoney = transInBeforeMoney + amount;
		}
		// IM -> 彩账
		else if(fromAccount == Global.BILL_ACCOUNT_IM && toAccount == Global.BILL_ACCOUNT_LOTTERY) {
			if (!MathUtil.isInteger(amount) || amount < 1) {
				// AG平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}

			UserGameAccount ugc = dataFactory.getGameAccount(userId, toAccount, 1);
			if (ugc == null) {
				json.set(2, "2-9007");
				return false;
			}
			String password = uGameAccountService.decryptPwd(ugc.getPassword());

			Double balance = imApi.balance(json, ugc.getUsername());
			if (balance == null || amount > balance) {
				// 余额不足
				json.set(2, "2-8005");
				return false;
			}

			try {
				billno = imApi.withdraw(json, ugc.getUsername(), password, MoneyFormat.pasMoney(amount));
			} catch (Exception e) {
				log.error("IM转彩票账户失败,用户名：" + uBean.getUsername() + ",金额：" + amount, e);
				billno = null;
			}
			if (billno == null) {
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			if (balance == null) {
				transOutBeforeMoney = 0;
				transOutAfterMoney = 0;
			}
			else {
				transOutBeforeMoney = balance;
				transOutAfterMoney = balance - amount;
			}

			// 彩票账户加钱
			transInBeforeMoney = uBean.getLotteryMoney();
			boolean result = uDao.updateLotteryMoney(userId, amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transInAfterMoney = transInBeforeMoney + amount;
		}
		// SB -> 主账
		else if(fromAccount == Global.BILL_ACCOUNT_SB && toAccount == Global.BILL_ACCOUNT_MAIN) {
			if (!MathUtil.isInteger(amount) || amount < 1) {
				// 平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}

			UserGameAccount ugc = dataFactory.getGameAccount(userId, fromAccount, 1);
			if (ugc == null) {
				json.set(2, "2-8005");
				return false;
			}

			Double sbBalance = sbapi.checkUserBalance(json, ugc.getUsername());
			if (sbBalance == null || amount > sbBalance) {
				// 余额不足
				json.set(2, "2-8005");
				return false;
			}

			try {
				Win88SBFundTransferResult win88SBFundTransferResult = sbapi.withdraw(json, ugc.getUsername(), BigDecimal.valueOf(amount).intValue());
				if (win88SBFundTransferResult != null) {
					billno = win88SBFundTransferResult.getOpTransId();
					transOutBeforeMoney = win88SBFundTransferResult.getData().getBeforeAmount();
					transOutAfterMoney = win88SBFundTransferResult.getData().getAfterAmount();
				}
				else {
					billno = null;
				}
			} catch (Exception e) {
				log.error("SB转主账户失败,用户名：" + uBean.getUsername() + ",金额：" + amount, e);
				billno = null;
			}
			if (StringUtils.isEmpty(billno)) {
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			// 主账户加钱
			transInBeforeMoney = uBean.getTotalMoney();
			boolean result = uDao.updateTotalMoney(userId, amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transInAfterMoney = transInBeforeMoney + amount;
		}
		// SB -> 彩账
		else if(fromAccount == Global.BILL_ACCOUNT_SB && toAccount == Global.BILL_ACCOUNT_LOTTERY) {
			if (!MathUtil.isInteger(amount) || amount < 1) {
				// AG平台只能输入整数
				json.set(2, "2-8009");
				return false;
			}

			UserGameAccount ugc = dataFactory.getGameAccount(userId, fromAccount, 1);
			if (ugc == null) {
				json.set(2, "2-8005");
				return false;
			}

			Double sbBalance = sbapi.checkUserBalance(json, ugc.getUsername());
			if (sbBalance == null || amount > sbBalance) {
				// 余额不足
				json.set(2, "2-8005");
				return false;
			}

			try {
				Win88SBFundTransferResult win88SBFundTransferResult = sbapi.withdraw(json, ugc.getUsername(), BigDecimal.valueOf(amount).intValue());
				if (win88SBFundTransferResult != null) {
					billno = win88SBFundTransferResult.getOpTransId();
					transOutBeforeMoney = win88SBFundTransferResult.getData().getBeforeAmount();
					transOutAfterMoney = win88SBFundTransferResult.getData().getAfterAmount();
				}
				else {
					billno = null;
				}
			} catch (Exception e) {
				log.error("SB转彩票账户失败,用户名：" + uBean.getUsername() + ",金额：" + amount, e);
				billno = null;
			}
			if (StringUtils.isEmpty(billno)) {
				if (json.getError() == 0) {
					json.set(1, "1-1");
				}
				return false;
			}

			// 彩票账户加钱
			transInBeforeMoney = uBean.getLotteryMoney();
			boolean result = uDao.updateLotteryMoney(userId, amount);
			if (!result) {
				json.set(1, "1-1");
				return false;
			}
			transInAfterMoney = transInBeforeMoney + amount;
		}
		else {
			json.set(2, "2-7007");
			return false;
		}

		// 转出账单
		int toUid = uBean.getId();
		int fromUid = toUid;
		String time = new Moment().toSimpleTime();
		int status = 1; // 正常状态
		int type = Global.USER_TRANSFERS_TYPE_DEFAULT;
		String infos = String.format("%s转入%s，%s元", fromPlatform.getName(), toPlatform.getName(), amount);
		UserTransfers entity = new UserTransfers(billno, toUid, fromUid, toAccount, fromAccount, amount, transOutBeforeMoney, transOutAfterMoney, time, status, type, infos);
		uTransfersDao.add(entity);
		uBillService.addTransOutBill(entity, uBean, fromAccount, infos, transOutBeforeMoney, transOutAfterMoney);
		uBillService.addTransInBill(entity, uBean, toAccount, infos, transInBeforeMoney, transInAfterMoney);
		return true;

	}

//	private boolean transferAG(WebJSON json, int userId, int toAccount, int fromAccount, double amount) {
//		if (fromAccount != Global.BILL_ACCOUNT_AG && toAccount != Global.BILL_ACCOUNT_AG) {
//			json.set(2, "2-7007");
//			return false;
//		}
//
//		// 主账转AG
//		if(fromAccount == Global.BILL_ACCOUNT_MAIN && toAccount == Global.BILL_ACCOUNT_AG) {
//
//		}
//		// 彩账转AG
//		else if(fromAccount == Global.BILL_ACCOUNT_LOTTERY && toAccount == Global.BILL_ACCOUNT_AG) {
//
//		}
//		// AG转主账
//		else if(fromAccount == Global.BILL_ACCOUNT_AG && toAccount == Global.BILL_ACCOUNT_MAIN) {
//
//		}
//		// AG转彩账
//		else if(fromAccount == Global.BILL_ACCOUNT_AG && toAccount == Global.BILL_ACCOUNT_LOTTERY) {
//
//		}
//		else {
//			json.set(2, "2-7007");
//			return false;
//		}
//	}

	@Override
	public boolean transfersToUser(int toUser, int fromUser, double amount,int transferType) {
		User toBean = uDao.getById(toUser);
		User fromBean = uDao.getById(fromUser);
		if(toBean != null && fromBean != null) {
			DecimalFormat fmat =  new DecimalFormat("##.000");
			String infosMoney = fmat.format(amount);

			if(amount > 0 && fromBean.getTotalMoney() >= amount) {
				boolean uFlag = uDao.updateTotalMoney(fromUser, -amount);
				if (!uFlag) {
					return false;
				}

				if(uFlag) {
					String time = new Moment().toSimpleTime();
					String billno = billno();
					int toAccount = 1;
					int fromAccount = 1;
					int toUid = toUser;
					int fromUid = fromUser;
					double money = amount;
					double beforeMoney = fromBean.getTotalMoney();
					double afterMoney = beforeMoney - money;
					int status = 1; // 正常状态
					int type = Global.USER_TRANSFERS_TYPE_USER_OUT;
					//充值
					if(transferType == Global.USER_TRANSFERS_TYPE){
						//如果是转账操作，减去相应的消费限制
						uWithdrawLimitService.add(fromUser, -amount, time, Global.PAYMENT_CHANNEL_TYPE_TRANSFER, Global.PAYMENT_CHANNEL_SUB_TYPE_TRANSFER_OUT, dataFactory.getWithdrawConfig().getTransferConsumptionPercent());

						String infos = "转出到下级账户:" + toBean.getUsername() + "，金额" + infosMoney + "元。";
						UserTransfers entity = new UserTransfers(billno, toUid, fromUid, toAccount,
								fromAccount, money, beforeMoney, afterMoney, time, status, type, infos);
						uTransfersDao.add(entity);

						// 生成流水
						uBillService.addUserTransOutBill(fromBean, Global.BILL_ACCOUNT_MAIN, amount, infos);
					}
					//活动补贴
					if(transferType == Global.BILL_TYPE_ACTIVITY){
						String infos = "活动转账:" + toBean.getUsername() + "，金额" + infosMoney + "元。";
						int refType = Global.BILL_TYPE_ACTIVITY;

						// 生成流水
						uBillService.addUserTransOutBill(fromBean, Global.BILL_ACCOUNT_MAIN, amount, infos);

						//从彩票账户报表减去相应的活动资金
						String date = new Moment().toSimpleDate();
						uLotteryReportService.update(fromBean.getId(), refType, -money, date);
					}
					//分红
					if(transferType == Global.BILL_TYPE_DIVIDEND){
						String infos = "分红转账:" + toBean.getUsername() + "，金额" + infosMoney + "元。";
						UserTransfers entity = new UserTransfers(billno, toUid, fromUid, toAccount, 
								fromAccount, money, beforeMoney, afterMoney, time, status, type, infos);
						uTransfersDao.add(entity);

						// 生成流水
						uBillService.addUserTransOutBill(fromBean, Global.BILL_ACCOUNT_MAIN, amount, infos);
					}
				}

				// 下级加钱
				boolean toResult = uDao.updateLotteryMoney(toUser, amount);
				if(toResult) {
					String time = new Moment().toSimpleTime();
					//转账类型  1  上下级转账
					if(transferType == Global.USER_TRANSFERS_TYPE){
						//消费限制
						uWithdrawLimitService.add(toUser, amount, time, Global.PAYMENT_CHANNEL_TYPE_TRANSFER, Global.PAYMENT_CHANNEL_SUB_TYPE_TRANSFER_IN, dataFactory.getWithdrawConfig().getTransferConsumptionPercent());

						String billno = billno();
						int toAccount = Global.BILL_ACCOUNT_LOTTERY;//改为彩票账户
						int fromAccount = Global.BILL_ACCOUNT_LOTTERY;
						int toUid = toUser;
						int fromUid = fromUser;
						double money = amount;
						//double beforeMoney = toBean.getTotalMoney();
						double beforeMoney = toBean.getLotteryMoney();//改为彩票账户
						double afterMoney = beforeMoney + money;
						int status = 1; // 正常状态
						int type = Global.USER_TRANSFERS_TYPE_USER_IN;
						String infos = "上级代理:"+fromBean.getUsername()+"转账，转入金额:" + infosMoney + "元。";
						UserTransfers entity = new UserTransfers(billno, toUid, fromUid, toAccount,
								fromAccount, money, beforeMoney, afterMoney, time, status, type, infos);
						uTransfersDao.add(entity);
						// 转入用户账单
						uBillService.addUserTransInBill(toBean, Global.BILL_ACCOUNT_LOTTERY, amount, infos);
						// 到账通知
						uSysMessageService.addTransToUser(toUser, amount);
					}
					//5 优惠活动
					if(transferType == Global.BILL_TYPE_ACTIVITY){
						if(amount > 0) {
							String infos = "上级代理:"+fromBean.getUsername()+"活动转账，转入金额:" + infosMoney + "元。";
							int refType = Global.BILL_TYPE_ACTIVITY; // 系统活动补贴    现在直接转到彩票账户
							uBillService.addActivityBill(toBean, Global.BILL_ACCOUNT_LOTTERY, amount, refType, infos);
							// 到账通知
							uSysMessageService.addTransToUser(toBean.getId(), amount);
						}
					}
					// 12 代理分红
					if(transferType == Global.BILL_TYPE_DIVIDEND){
						String infos = "上级代理:"+fromBean.getUsername()+"分红转账，转入金额:" + infosMoney + "元。";
						String billno = billno();
						int userId = toBean.getId();
						int type = Global.BILL_TYPE_DIVIDEND;// 分红    现在直接转到彩票账户
						double money = amount;
						double beforeMoney = 0;
						beforeMoney = toBean.getLotteryMoney();
						double afterMoney = beforeMoney + money;
						int refType = type;
						String refId = null;
						Moment thisTime = new Moment();
						UserBill tmpBill = new UserBill(billno, userId, Global.BILL_ACCOUNT_LOTTERY,
								type, money, beforeMoney, afterMoney, refType, refId, thisTime.toSimpleTime(), infos);
						uBillService.add(tmpBill);
						// 到账通知
						uSysMessageService.addTransToUser(userId, amount);
					}
				}
				return toResult;
			}
		}
		return false;
	}

	@Override
	public boolean transferAll(WebJSON json, int userId) {
		User uBean = uDao.getById(userId);
		if (uBean == null) {
			json.set(2, "2-6");
			return false;
		}
		try {
			// 彩票账户 -> 主账户
			if (uBean.getLotteryMoney() > 0) {
                transfersToSelf(json, userId, Global.BILL_ACCOUNT_MAIN, Global.BILL_ACCOUNT_LOTTERY, uBean.getLotteryMoney());
            }

            // AG -> 主账户
            SysPlatform agPlatform = dataFactory.getSysPlatform(Global.BILL_ACCOUNT_AG);
            if (agPlatform != null && agPlatform.getStatus() == 0) {
                UserGameAccount agAccount = dataFactory.getGameAccount(userId, Global.BILL_ACCOUNT_AG, 1);
                if (agAccount != null) {
                    String password = uGameAccountService.decryptPwd(agAccount.getPassword());

                    Double agGalance = agAPI.getBalance(json, agAccount.getUsername(), password);
                    if (agGalance != null && agGalance > 1) {
                        double agTransferAmount = MoneyFormat.toDigitDouble(agGalance);
                        transfersToSelf(json, userId, Global.BILL_ACCOUNT_MAIN, Global.BILL_ACCOUNT_AG, agTransferAmount);
                    }
                }
            }

            // PT -> 主账户
            SysPlatform ptPlatform = dataFactory.getSysPlatform(Global.BILL_ACCOUNT_PT);
            if (ptPlatform != null && ptPlatform.getStatus() == 0) {
                UserGameAccount ptAccount = dataFactory.getGameAccount(userId, Global.BILL_ACCOUNT_PT, 1);
                if (ptAccount != null) {

                    Double ptBalance = ptAPI.playerBalance(json, ptAccount.getUsername());
                    if (ptBalance != null && ptBalance > 1) {
                        double ptTransferAmount = MoneyFormat.toDigitDouble(ptBalance);
                        transfersToSelf(json, userId, Global.BILL_ACCOUNT_MAIN, Global.BILL_ACCOUNT_PT, ptTransferAmount);
                    }
                }
            }

			return true;
		} catch (Exception e) {
			log.error("资金归集出错", e);
			json.set(1, "1-1");
		}

		return false;
	}
}
