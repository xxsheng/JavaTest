package lottery.domains.content.biz.impl;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.KeyRepresentation;
import javautils.StringUtil;
import javautils.date.DateRangeUtil;
import javautils.date.Moment;
import lottery.domains.content.biz.UserDailySettleService;
import lottery.domains.content.biz.UserDividendService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.chart.ChartLineVO;
import lottery.domains.pool.DataFactory;
import lottery.web.content.validate.UserValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
	private static final GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder gacb =
			new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
					.setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
					.setWindowSize(5)
					.setCodeDigits(6)
					.setKeyRepresentation(KeyRepresentation.BASE32);
	private static final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(gacb.build());

	// 已经使用过的验证码60分钟内不允许再次使用
	private static final ConcurrentHashMap<String, String> GOOGLE_CODES = new ConcurrentHashMap<>();
	
	@Autowired
	private UserDao uDao;

	@Autowired
	private UserDailySettleService uDailySettleService;

	@Autowired
	private UserDividendService uDividendService;
	
	@Autowired
	private DataFactory dataFactory;

	@Autowired
	private UserValidate uValidate;

	@Override
	@Transactional(readOnly = true)
	public User getById(int id) {
		return uDao.getById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public User getByUsername(String username) {
		return uDao.getByUsername(username);
	}

	@Override
	public boolean setOnline(String username, String sessionId) {
		return uDao.setOnline(username, sessionId);
	}

	@Override
	public boolean setOffline(String sessionId) {
		return uDao.setOffline(sessionId);
	}

	@Override
	public boolean updateAStatus(int id, int status, String message) {
		return uDao.updateAStatus(id, status, message);
	}

	@Override
	public boolean updateBStatus(int id, int status, String message) {
		return uDao.updateBStatus(id, status, message);
	}

	@Override
	public boolean updateLoginTime(int id, String time) {
		return uDao.updateLoginTime(id, time);
	}

	@Override
	public boolean updateWithdrawName(int id, String withdrawName) {
		return uDao.updateWithdrawName(id, withdrawName);
	}

	@Override
	public boolean updateWithdrawPassword(int id, String md5Pwd) {
		return uDao.updateWithdrawPassword(id, md5Pwd);
	}

	@Override
	public boolean updateLoginPwd(int id, String md5Pwd) {
		return uDao.updateLoginPwd(id, md5Pwd);
	}

	@Override
	public boolean updateBindStatus(int id, int status) {
		return uDao.updateBindStatus(id, status);
	}

	@Override
	public boolean updateNickname(int id, String nickname) {
		return uDao.updateNickname(id, nickname);
	}

	@Override
	public boolean updateIsBindGoogle(int id, int isBindGoogle) {
		return uDao.updateIsBindGoogle(id, isBindGoogle);
	}

	@Override
	public boolean updateSecretKey(int id, String secretKey) {
		return uDao.updateSecretKey(id, secretKey);
	}

	@Override
	public boolean updateLotteryPoint(int id, int code, double lp, double nlp, int ct, int BStatus) {
		// 不允许创建1958等级
		if (code > dataFactory.getLotteryConfig().getNotBetPointAccount()) {
			return false;
		}
		if (code % 2 != 0) {
			return false;
		}

		boolean updated = uDao.updateLotteryPoint(id, code, lp, nlp, ct, BStatus);
		if (updated) {
			uDailySettleService.checkDailySettle(id);
			uDividendService.checkDividend(id);
		}
		return updated;
	}

	// @Override
	// public boolean updateBstatusAndAllowEqualCode(Integer bstatus, int allowEqualCode, int userId) {
	// 	return uDao.updateBstatusAndAllowEqualCode(bstatus, allowEqualCode, userId);
	// }

	@Override
	public Integer addNewUser(String username, String nickname, String password,
			int upid, String upids, int type, int code, double locatePoint,
			double notLocatePoint) {
		// 不允许创建1958等级
//		if (code == 1958) {
//			return null;
//		}
		if (code < 1800 || code > dataFactory.getCodeConfig().getSysCode()) {
			return null;
		}
		if (code % 2 != 0) {
			return null;
		}

		double totalMoney = 0;
//		double lotteryMoney = 0;
		double lotteryMoney = 0;
		double baccaratMoney = 0;
		double freezeMoney = 0;
		double dividendMoney = 0;
		int codeType = 0;
		double extraPoint = 0;
		String registTime = new Moment().toSimpleTime();
		int AStatus = 0;
		int BStatus = 0;
		if (code > dataFactory.getLotteryConfig().getNotBetPointAccount()) {
			// 1956以上禁止投注
			BStatus = -1;
		}
		int allowEqualCode = 1; // 默认允许同级开号
		if(uValidate.allowEqualCode(code)){//1990以上不能同级开号
			allowEqualCode = -1;
		}
		int onlineStatus = 0;
		int allowTransfers = 1; //默认允许上下级转账
		int allowPlatformTransfers = 1;//默认允许平台转账 1允许
		int allowWithdraw = 1;////默认允许平台取款 1允许
		int loginValidate = 0; // 默认关闭异地登录验证
		int bindStatus = 0; // 绑定状态
		int vipLevel = 0;
		double integral = 0;
		User entity = new User(username, password, nickname, totalMoney, lotteryMoney, baccaratMoney, freezeMoney, dividendMoney,
				type, upid, code, locatePoint, notLocatePoint, codeType, extraPoint, registTime, AStatus, BStatus, onlineStatus, 
				allowEqualCode, allowTransfers, loginValidate, bindStatus, vipLevel, integral,allowPlatformTransfers,allowWithdraw);

		// 新注册用户锁定账号N小时不允许取款
		if (dataFactory.getWithdrawConfig().getRegisterHours() > 0) {
			entity.setLockTime(new Moment().add(dataFactory.getWithdrawConfig().getRegisterHours(), "hours").toSimpleTime());
		}

		if(StringUtil.isNotNull(upids)) {
			entity.setUpids(upids);
		}
		boolean flag = uDao.add(entity);

		if (flag) {
			uDailySettleService.checkDailySettle(entity.getId());
			uDividendService.checkDividend(entity.getId());
		}

		return flag ? entity.getId() : null;
	}
	
	@Override
	public Integer addLowerUser(User uBean, String username, String nickname,
			String password, int type, int code, double locatePoint,
			double notLocatePoint) {
		// 不允许创建1958等级
//		if (code == 1958) {
//			return null;
//		}

		if (code < 1800 || code > dataFactory.getCodeConfig().getSysCode()) {
			return null;
		}
		if (code % 2 != 0) {
			return null;
		}

		int upid = uBean.getId();
		String upids = "[" + upid + "]";
		// 生成上下级关系
		if(StringUtil.isNotNull(uBean.getUpids())) {
			upids += "," + uBean.getUpids();
		}
		return addNewUser(username, nickname, password, upid, upids, type, code, locatePoint, notLocatePoint);
	}

	@Override
	@Transactional(readOnly = true)
	public ChartLineVO getOtherCharReport(int[] ids, String sDate, String eDate) {
		String[] xAxis = DateRangeUtil.listDate(sDate, eDate);
		ChartLineVO lineVO = new ChartLineVO();
		lineVO.setxAxis(xAxis);
		// 获取数据
		Number[] yAxis = new Number[xAxis.length];
		for (int i = 0; i < xAxis.length; i++) {
			yAxis[i] = 0;
		}
		lineVO.getyAxis().add(yAxis);
		return lineVO;
	}

	@Override
	public GoogleAuthenticatorKey createCredentialsForUser(int userId) {
		final GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
		uDao.updateSecretKey(userId, key.getKey());
		return key;
	}

	@Override
	public boolean authoriseUser(int userId, int verificationCode) {
		String cacheKey = userId + "_" + verificationCode;
		String usedTime = GOOGLE_CODES.get(cacheKey);
		if (usedTime != null) {
			Moment now = new Moment();
			Moment usedMoment = new Moment().fromTime(usedTime);

			int usedMinutes = now.difference(usedMoment, "minute");
			if (usedMinutes <= 60) {
				// 已经使用过的验证码60分钟内不允许再次使用
				return false;
			}
		}

		String secretKey = uDao.getById(userId).getSecretKey();
		boolean isCodeValid = googleAuthenticator.authorize(secretKey, verificationCode);

		if (isCodeValid) {
			GOOGLE_CODES.put(cacheKey, new Moment().toSimpleTime());
		}

		return isCodeValid;
	}

	@Override
	public boolean updateLoginValidate(int userId, int loginValidate) {
		return uDao.updateLoginValidate(userId, loginValidate);
	}

	@Override
	public boolean updateTotalMoney(int id, double amount) {
		return this.uDao.updateTotalMoney(id,amount);
	}

	// @Schedules(value = {@Scheduled(cron = "0 30 5 * * *"), @Scheduled(cron = "0 00 13 * * *")})
	@Scheduled(cron = "0 30 5 * * *")
	public void clear() {
		GOOGLE_CODES.clear();
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getOfflineDemoList(int onlineStatus) {
		return uDao.getDemoListByonlineStatus(onlineStatus);
	}

	@Override
	public boolean updateDemoUserLotteryMoney(int id, double amount) {
		return uDao.updateDemoUserLotteryMoney(id, amount);
	}
}