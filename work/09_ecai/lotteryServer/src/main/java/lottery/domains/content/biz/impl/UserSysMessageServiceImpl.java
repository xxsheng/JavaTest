package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.dao.UserSysMessageDao;
import lottery.domains.content.entity.UserSysMessage;
import lottery.domains.content.global.Global;
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
	public boolean addOnlineRecharge(int userId, double amount) {
		int type = Global.USER_SYS_MESSAGE_TYPE_RECHARGE;
		String content = String.format("您已通过在线支付充值%s元。", amount);
		String time = new Moment().toSimpleTime();
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addDividendRequest(int userId, int upId) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String time = new Moment().toSimpleTime();
		String content = String.format("您的上级于%s向您发起了契约分红，请前往<<代理管理->契约分红->契约分红管理>>处理！", time);
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addDividendAgree(String username, int upId) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String time = new Moment().toSimpleTime();
		String content = String.format("您的下级用户%s于%s同意了您发起的契约分红，将于本周期开始结算！", username, time);
		int status = 0;
		UserSysMessage entity = new UserSysMessage(upId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addDividendDeny(String username, int upId) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String time = new Moment().toSimpleTime();
		String content = String.format("您的下级用户%s于%s拒绝了您发起的契约分红，您可以再次进行发起！", username, time);
		int status = 0;
		UserSysMessage entity = new UserSysMessage(upId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addDailySettleRequest(int userId, int upId) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String time = new Moment().toSimpleTime();
		String content = String.format("您的上级于%s向您发起了契约日结，请前往<<代理管理->契约日结->契约日结管理>>处理！", time);
		int status = 0;
		UserSysMessage entity = new UserSysMessage(userId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addDailySettleAgree(String username, int upId) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String time = new Moment().toSimpleTime();
		String content = String.format("您的下级用户%s于%s同意了您发起的契约日结，将于次日凌晨开始结算！", username, time);
		int status = 0;
		UserSysMessage entity = new UserSysMessage(upId, type, content, time, status);
		return uSysMessageDao.add(entity);
	}

	@Override
	public boolean addDailySettleDeny(String username, int upId) {
		int type = Global.USER_SYS_MESSAGE_TYPE_SYSTEM;
		String time = new Moment().toSimpleTime();
		String content = String.format("您的下级用户%s于%s拒绝了您发起的契约日结，您可以再次进行发起！", username, time);
		int status = 0;
		UserSysMessage entity = new UserSysMessage(upId, type, content, time, status);
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
	public boolean updateUnread(int userId, int[] ids) {
		return uSysMessageDao.updateUnread(userId, ids);
	}

	@Override
	public boolean deleteMsg(int userId, int[] ids) {
		return uSysMessageDao.deleteMsg(userId, ids);
	}

	@Override
	public boolean add(UserSysMessage entity) {
		return uSysMessageDao.add(entity);
	}
}