package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import javautils.ip.IpUtil;
import lottery.domains.content.biz.UserActionLogService;
import lottery.domains.content.dao.UserActionLogDao;
import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.UserActionLog;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserCodeQuota;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class UserActionLogServiceImpl implements UserActionLogService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserActionLogDao uActionLogDao;
	
	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
	
	String ip2Address(String ip) {
		String address = "[未知地址]";
		try {
			String[] infos = IpUtil.find(ip);
			address = Arrays.toString(infos);
		} catch (Exception e) {}
		return address;
	}
	
	boolean add(int userId, String ip, String action) {
		String address = ip2Address(ip);
		String time = new Moment().toSimpleTime();
		UserActionLog entity = new UserActionLog(userId, ip, address, action, time);
		return uActionLogDao.add(entity);
	}

	@Override
	public boolean modLoginPwd(int userId, String ip) {
		String action = "修改登录密码";
		return add(userId, ip, action);
	}
	
	@Override
	public boolean bindWithdrawPwd(int userId, String ip) {
		String action = "设置资金密码";
		return add(userId, ip, action);
	}

	@Override
	public boolean modWithdrawPwd(int userId, String ip) {
		String action = "修改资金密码";
		return add(userId, ip, action);
	}

	@Override
	public boolean bindWithdrawName(int userId, String ip, String withdrawName) {
		String action = "绑定取款人信息：" + withdrawName;
		return add(userId, ip, action);
	}
	
	@Override
	public boolean bindCard(int userId, String ip, int bankId,
			String bankBranch, String cardName, String cardId) {
		PaymentBank paymentBank = dataFactory.getPaymentBank(bankId);
		String bankName = paymentBank.getName();
		String action = String.format("绑定银行卡；开户行：%s；支行：%s；姓名：%s；卡号：%s", bankName, bankBranch, cardName, cardId);
		return add(userId, ip, action);
	}
	
	@Override
	public boolean bindSecurity(int userId, String ip, String question1,
			String question2, String question3) {
		String action = String.format("绑定密保问题；问题1：%s；问题2：%s；问题3：%s", question1, question2, question3);
		return add(userId, ip, action);
	}
	
	@Override
	public boolean withdrawApply(int userId, String ip, UserCard card,
			double amount, double recMoney, double feeMoney) {
		PaymentBank paymentBank = dataFactory.getPaymentBank(card.getBankId());
		String bankName = paymentBank.getName();
		String action = String.format("申请提现；申请金额：%s；到账金额：%s；手续费：%s；开户行：%s；支行：%s；姓名：%s；卡号：%s", amount, recMoney, feeMoney, bankName, card.getBankBranch(), card.getCardName(), card.getCardId());
		return add(userId, ip, action);
	}
	
	@Override
	public boolean bindData(int userId, String ip, String withdrawName, String birthday, int bankId, String bankBranch, String cardId) {
		PaymentBank paymentBank = dataFactory.getPaymentBank(bankId);
		String bankName = paymentBank.getName();
		String action = String.format("绑定资料；真实姓名：%s；生日：%s；开户行：%s；支行：%s；银行卡号：%s；资金密码：******", withdrawName, birthday, bankName, bankBranch, cardId);
		return add(userId, ip, action);
	}
	
	@Override
	public boolean transToUser(int userId, String ip, String targetUser, double amount) {
		String action = String.format("转账到下级用户；下级用户：%s；转账金额：%s", targetUser, amount);
		return add(userId, ip, action);
	}
	
	@Override
	public boolean editUserPoint(int userId, String ip, String targetUser, double oldPoint, double newPoint) {
		String action = String.format("编辑下级返点[配额升点]；下级用户：%s；之前返点：%s；之后返点：%s", targetUser, oldPoint, newPoint);
		return add(userId, ip, action);
	}
	
	@Override
	public boolean editUserPointByAmount(int userId, String ip, String targetUser, double oldPoint, double newPoint, double amount3, double amount7) {
		String action = String.format("编辑下级返点[按量升点]；下级用户：%s；之前返点：%s；之后返点：%s；3天团队消费量：%s；7天团队消费量：%s", targetUser, oldPoint, newPoint, amount3, amount7);
		return add(userId, ip, action);
	}
	
	@Override
	public boolean editUserQuota(int userId, String ip, String targetUser, UserCodeQuota oldQuota, int amount1, int amount2, int amount3) {
		// if(oldQuota == null) {
		// 	oldQuota = new UserCodeQuota();
		// }
		// String oldValue = oldQuota.getCount1() + "," + oldQuota.getCount2() + "," + oldQuota.getCount3();
		// String newValue = (oldQuota.getCount1() + amount1) + "," + (oldQuota.getCount2() + amount2) + "," + (oldQuota.getCount3() + amount3);
		// String action = String.format("编辑下级配额；下级用户：%s；之前配额：[%s]；之后配额：[%s]", targetUser, oldValue, newValue);
		// return add(userId, ip, action);
		return false;
	}
	
	@Override
	public boolean addNewUser(int userId, String ip, String username, int type, double point) {
		String formatType = "未知";
		if(type == Global.USER_TYPE_PROXY) formatType = "代理";
		if(type == Global.USER_TYPE_PLAYER) formatType = "玩家";
		String action = String.format("添加下级用户；用户名：%s；用户类型：%s；返点：%s", username, formatType, point);
		return add(userId, ip, action);
	}

	@Override
	public boolean registUser(int userId, String ip, String username, String upUsername, int type, double point) {
		String formatType = "未知";
		if(type == Global.USER_TYPE_PROXY) formatType = "代理";
		if(type == Global.USER_TYPE_PLAYER) formatType = "玩家";
		String action = String.format("用户注册；用户名：%s；上级用户名：%s；用户类型：%s；返点：%s；", username, upUsername, formatType, point);
		return add(userId, ip, action);
	}

	@Override
	public boolean bindGoogle(int userId, String ip) {
		String action = String.format("绑定谷歌身份验证器");
		return add(userId, ip, action);
	}

	@Override
	public boolean modLoginValidate(int userId, String ip, int loginValidate) {
		String action;
		if (loginValidate == 1) {
			action = "开启异地登录验证";
		}
		else {
			action = "关闭异地登录验证";
		}
		return add(userId, ip, action);
	}

	@Override
	public boolean requestDailySettle(int userId, String ip, String username, String scaleLevel, String lossLevel, String salesLeve,String userLevel) {
		String action = "发起契约日结；用户名：%s；比例：%s；销量：%s；亏损：%s; 有效人数：%s；";
		Object[] values = {username, scaleLevel, lossLevel, salesLeve, userLevel};
		action = String.format(action, values);

		return add(userId, ip, action);
	}

	@Override
	public boolean agreeDailySettle(int userId, String ip) {
		String action = "同意契约日结；";
		return add(userId, ip, action);
	}

	@Override
	public boolean denyDailySettle(int userId, String ip) {
		String action = "拒绝契约日结；";
		return add(userId, ip, action);
	}

	@Override
	public boolean requestDividend(int userId, String ip, String username, String scaleLevel, String lossLevel, String salesLevel, String userLevel) {
		String action = "发起契约分红；用户名：%s；阶梯比例：%s；阶梯销量：%s,阶梯亏损：%s；有效人数：%s；";

//		String scaleStr = new BigDecimal(scale * 100).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
//		scaleStr += "%";

		Object[] values = {username, scaleLevel, salesLevel, lossLevel, userLevel};
		action = String.format(action, values);

		return add(userId, ip, action);
	}

	@Override
	public boolean agreeDividend(int userId, String ip) {
		String action = "同意契约分红；";
		return add(userId, ip, action);
	}

	@Override
	public boolean denyDividend(int userId, String ip) {
		String action = "拒绝契约分红；";
		return add(userId, ip, action);
	}

	@Override
	public boolean collectDividend(int userId, String ip, String startDate, String endDate, double availableAmount) {
		String action = "领取契约分红；周期：%s~%s；金额：%s；";

		String userAmountStr = new BigDecimal(availableAmount).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();

		Object[] values = {startDate, endDate, userAmountStr};
		action = String.format(action, values);

		return add(userId, ip, action);
	}

	@Override
	public boolean issueDividend(int userId, String ip, String startDate, String endDate, double issued) {
		String action = "发放契约分红；周期：%s~%s；金额：%s；";

		String userAmountStr = new BigDecimal(issued).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();

		Object[] values = {startDate, endDate, userAmountStr};
		action = String.format(action, values);

		return add(userId, ip, action);
	}

	@Override
	public boolean collectGameDividend(int userId, String ip, String startDate, String endDate, double userAmount) {
		String action = "领取老虎机真人体育分红；周期：%s~%s；金额：%s；";

		String userAmountStr = new BigDecimal(userAmount).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();

		Object[] values = {startDate, endDate, userAmountStr};
		action = String.format(action, values);

		return add(userId, ip, action);
	}

	@Override
	public boolean userUnbidCard(int userId,  String username,String usercard,String ip, String dateTime, String unbindNum) {
		String action = String.format("用户银行卡解锁；操作用户：%s；银行卡号：%s；第几次：%s；操作时间：%s", username, usercard, unbindNum,dateTime);
		return add(userId, ip, action);
	}

	@Override
	public boolean sign(int userId, int days, String lastSignTime, String ip) {
		String action = String.format("用户签到；连续签到天数：%s；上次签到日期：%s；", days, lastSignTime);
		return add(userId, ip, action);
	}
}