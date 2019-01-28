package lottery.domains.content.biz.impl;


import javautils.StringUtil;
import javautils.date.Moment;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.dao.UserSysMessageDao;
import lottery.domains.content.entity.UserSysMessage;
import lottery.domains.content.global.Global;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSysMessageServiceImpl implements UserSysMessageService {

	@Autowired
	private UserSysMessageDao uSysMessageDao;
	
	@Override
	public boolean addTransToUser(int userId, double amount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_RECHARGE;
		String content = String.format("上级代理已为您充值%s元。", amount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addFirstRecharge(int userId, double rechargeAmount, double amount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String content = String.format("您已通过首充活动充值%s元，系统自动赠送%s元。", rechargeAmount, amount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addSysRecharge(int userId, double amount, String remarks) {
		int type = Global.USER_SYS_MESSAGE_TYPE_RECHARGE;
		if(StringUtil.isNotNull(remarks)) {
			remarks = "备注：" + remarks;
		}
		String content = String.format("管理员已为您充值%s元。%s", amount, remarks);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addOnlineRecharge(int userId, double amount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_RECHARGE;
		String content = String.format("您已通过在线支付充值%s元。", amount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addTransfersRecharge(int userId, double amount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_RECHARGE;
		String content = String.format("您已通过网银转账充值%s元。", amount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addConfirmWithdraw(int userId, double amount, double recAmount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_WITHDRAW;
		String content = String.format("您申请提现%s元已支付，实际到账%s元，请注意查收。", amount, recAmount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addRefuseWithdraw(int userId, double amount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_WITHDRAW;
		String content = String.format("您申请提现%s元已被拒绝，金额已返还！", amount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addRefuse(int userId, double amount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_WITHDRAW;
		String content = String.format("您申请提现%s元已失败，金额已返还！", amount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addShFail(int userId, double amount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_WITHDRAW;
		String content = String.format("您申请提现%s元审核未通过，金额已返还！", amount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addActivityBind(int userId, double amount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String content = String.format("您参加的绑定资料体验金%s元，已经派发到您的账户。", amount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addActivityRecharge(int userId, double amount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_RECHARGE;
		String content = String.format("您参加的开业大酬宾奖励%s元，已经派发到您的账户。", amount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addRewardMessage(int userId, String date) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String content = String.format("系统已发放%s佣金。", date);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
	
	@Override
	public boolean addVipLevelUp(int userId, String level) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String content = String.format("尊敬的VIP会员您好，您已成功晋级为%s，从现在开始您可以享受%s待遇。", level, level);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addDividendBill(int userId, String startDate, String endDate) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String content = String.format("您的彩票分红已发放，周期%s~%s，请前往<代理管理->契约分红->契约分红账单>处领取！", startDate, endDate);
		String time = new Moment().toSimpleTime();
		int status = Global.USER_SYS_MESSAGE_STATUS_UNREAD;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addGameDividendBill(int userId, String startDate, String endDate) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String content = String.format("您的老虎机真人体育分红已发放，周期%s~%s，请前往<代理管理->契约分红->老虎机/真人分红>处领取！", startDate, endDate);
		String time = new Moment().toSimpleTime();
		int status = Global.USER_SYS_MESSAGE_STATUS_UNREAD;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addDailySettleBill(int userId, String date) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String content = String.format("您昨日的彩票日结已发放，请前往<代理管理->契约日结->契约日结账单>处查看！");
		String time = new Moment().toSimpleTime();
		int status = Global.USER_SYS_MESSAGE_STATUS_UNREAD;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addGameWaterBill(int userId, String date, String fromUser, String toUser) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String content;
		if (StringUtils.equalsIgnoreCase(fromUser, toUser)) {
			content = String.format("您昨日的老虎机真人体育返水已发放！");
		}
		else {
			content = String.format("您昨日的老虎机真人体育返水已发放,来自用户：%s", fromUser);
		}
		String time = new Moment().toSimpleTime();
		int status = Global.USER_SYS_MESSAGE_STATUS_UNREAD;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}
}