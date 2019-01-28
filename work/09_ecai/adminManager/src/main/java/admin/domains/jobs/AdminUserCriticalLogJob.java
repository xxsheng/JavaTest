package admin.domains.jobs;

import admin.domains.content.dao.AdminUserCriticalLogDao;
import admin.domains.content.entity.AdminUser;
import admin.domains.content.entity.AdminUserAction;
import admin.domains.content.entity.AdminUserCriticalLog;
import admin.domains.pool.AdminDataFactory;
import javautils.date.Moment;
import javautils.http.HttpUtil;
import javautils.ip.IpUtil;
import javautils.math.MathUtil;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.PaymentBank;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDailySettle;
import lottery.domains.content.global.Global;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class AdminUserCriticalLogJob {

	@Autowired
	private AdminUserCriticalLogDao adminUserCriticalLogDao;
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	@Autowired
	private UserDao UserDao;
	@Autowired
	private AdminDataFactory adminDataFactory;
	
	private BlockingQueue<AdminUserCriticalLog> logQueue = new LinkedBlockingDeque<>();

	@Scheduled(cron = "0/5 * * * * *")
	public void run() {
		if(logQueue != null && logQueue.size() > 0) {
			try {
				List<AdminUserCriticalLog> list = new LinkedList<>();
				logQueue.drainTo(list, 1000);
				adminUserCriticalLogDao.save(list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	String ip2Address(String ip) {
		String address = "[未知地址]";
		try {
			String[] infos = IpUtil.find(ip);
			address = Arrays.toString(infos);
		} catch (Exception e) {}
		return address;
	}

	boolean add(AdminUser uEntity, HttpServletRequest request, String action,String actionKey,String username) {
		AdminUserAction adminUserAction = adminDataFactory.getAdminUserAction(actionKey);
		User user=new User();
		if (StringUtils.isNotEmpty(username)) {
			 user=UserDao.getByUsername(username);
		}
		int adminUserId = uEntity.getId();
		String ip = HttpUtil.getClientIpAddr(request);
		String address = ip2Address(ip);
		String time = new Moment().toSimpleTime();
		AdminUserCriticalLog entity = new AdminUserCriticalLog(adminUserId,user.getId(),adminUserAction.getId(), ip, address, action, time);
		String userAgent = request.getHeader("user-agent");
		entity.setUserAgent(userAgent);
		return logQueue.offer(entity);
	}

	/**
	 * 添加用户
	 */
	public boolean logAddUser(AdminUser uEntity, HttpServletRequest request, String username, String relatedUsers, int type, double point,String actionKey) {
		String formatType = "未知";
		String _relatedInfo = "";
		if(type == Global.USER_TYPE_PROXY) formatType = "代理";
		if(type == Global.USER_TYPE_PLAYER) formatType = "玩家";
		if(type == Global.USER_TYPE_RELATED) {
			formatType = "关联账号";
			_relatedInfo = "关联会员：" + relatedUsers + "；";
		}
		String action = String.format("添加会员账号；用户名：%s；用户类型：%s；返点：%s；%s", username, formatType, point, _relatedInfo);
		return add(uEntity, request, action, actionKey,username);
	}
	
	/**
	 * 冻结用户
	 */
	public boolean logLockUser(AdminUser uEntity, HttpServletRequest request, String username, int status, String message,String actionKey) {
		String formatStatus = "未知";
		if(status == -1) {
			formatStatus = "冻结";
		}
		if(status == -2) {
			formatStatus = "永久冻结";
		}
		if(status == -3) {
			formatStatus = "禁用";
		}
		String action = String.format("冻结用户；用户名：%s；状态：%s；说明：%s", username, formatStatus, message);
		return add(uEntity, request, action, actionKey,username);
	}
	
	/**
	 * 解冻用户
	 */
	public boolean logUnLockUser(AdminUser uEntity, HttpServletRequest request, String username,String actionKey) {
		String action = String.format("解冻用户；用户名：%s；", username);
		return add(uEntity, request, action, actionKey, username);
	}
	
	/**
	 * 充值
	 */
	public boolean logRecharge(AdminUser uEntity, HttpServletRequest request, String username, int type, int account, double amount, int limit, String remarks,String actionKey) {
		String formatType = "未知";
		if(type == Global.PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_NOTARRIVAL) {
			formatType = "充值未到账";
		}
		if(type == Global.PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_ACTIVITY) {
			formatType = "活动补贴";
		}
		if(type == Global.PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_ADD) {
			formatType = "修正资金（增加）";
		}
		if(type == Global.PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_MINUS) {
			formatType = "修正资金（减少）";
		}
		String formatAccount = "未知";
		if(account == Global.BILL_ACCOUNT_MAIN) {
			formatAccount = "主账户";
		}
		if(account == Global.BILL_ACCOUNT_LOTTERY) {
			formatAccount = "彩票账户";
		}
		if(account == Global.BILL_ACCOUNT_BACCARAT) {
			formatAccount = "百家乐账户";
		}
		String formatLimit = limit == 1 ? "是" : "否";
		String action = String.format("充值；用户名：%s；充值类型：%s；账户类型：%s；金额：%s；是否需要消费：%s；备注：%s", username, formatType, formatAccount, amount, formatLimit, remarks);
		return add(uEntity, request, action, actionKey,username);
	}


	/**
	 * 管理员操作用户转账
	 */
	public boolean logUserTransfer(AdminUser uEntity, HttpServletRequest request, String aUser, String bUser, double money,String remarks,String actionKey) {
		String moneyStr = MathUtil.doubleToStringDown(money, 1);
		String action = String.format("管理操作用户转账；待转会员：%s；目标会员：%s；金额：%s；备注：%s", aUser, bUser, moneyStr,remarks);
		return add(uEntity, request, action, actionKey,aUser);
	}


	/**
	 * 修改用户登录密码
	 */
	public boolean logModLoginPwd(AdminUser uEntity, HttpServletRequest request, String username,String actionKey) {
		String action = String.format("修改用户登录密码；用户名：%s", username);
		return add(uEntity, request, action, actionKey,username);
	}

	/**
	 * 修改用户资金密码
	 */
	public boolean logModWithdrawPwd(AdminUser uEntity, HttpServletRequest request, String username,String actionKey) {
		String action = String.format("修改用户资金密码；用户名：%s", username);
		return add(uEntity, request, action, actionKey,username);
	}


	/**
	 * 重置密保
	 */
	public boolean logResetSecurity(AdminUser uEntity, HttpServletRequest request, String username,String actionKey) {
		String action = String.format("重置密保；用户名：%s", username);
		return add(uEntity, request, action,actionKey,username);
	}

	/**
	 * 修改返点
	 */
	public boolean logModPoint(AdminUser uEntity, HttpServletRequest request, String username, double point,String actionKey) {
		String action = String.format("修改返点；用户名：%s；返点：%s", username, point);
		return add(uEntity, request, action, actionKey,username);
	}


	/**
	 * 修改用户私返点数
	 */
	public boolean logModExtraPoint(AdminUser uEntity, HttpServletRequest request, String username, double point,String actionKey) {
		String action = String.format("修改用户私返点数；用户名：%s；返点：%s", username, point);
		return add(uEntity, request, action, actionKey,username);
	}


	/**
	 * 修改用户绑定取款人
	 */
	public boolean logModWithdrawName(AdminUser uEntity, HttpServletRequest request, String username, String withdrawName,String actionKey) {
		String action = String.format("修改用户绑定取款人；用户名：%s；绑定取款人：%s", username, withdrawName);
		return add(uEntity, request, action, actionKey,username);
	}


	/**
	 * 解绑谷歌身份验证器
	 */
	public boolean unbindGoogle(AdminUser uEntity, HttpServletRequest request, String username,String actionKey) {
		String action = String.format("解绑谷歌身份验证器；用户名：%s", username);
		return add(uEntity, request, action, actionKey,username);
	}


	/**
	 * 添加用户银行卡
	 */
	public boolean logAddUserCard(AdminUser uEntity, HttpServletRequest request, String username, int bankId, String bankBranch, String cardId,String actionKey) {
		PaymentBank paymentBank = lotteryDataFactory.getPaymentBank(bankId);
		String bankName = paymentBank.getName();
		String action = String.format("添加用户银行卡；用户名：%s；开户行：%s；支行：%s；卡号：%s", username, bankName, bankBranch, cardId);
		return add(uEntity, request, action, actionKey,username);
	}

	/**
	 * 修改用户银行卡资料
	 */
	public boolean logModUserCard(AdminUser uEntity, HttpServletRequest request, String username, int bankId, String bankBranch, String cardId,String actionKey) {
		PaymentBank paymentBank = lotteryDataFactory.getPaymentBank(bankId);
		String bankName = paymentBank.getName();
		String action = String.format("修改用户银行卡资料；用户名：%s；开户行：%s；支行：%s；卡号：%s", username, bankName, bankBranch, cardId);
		return add(uEntity, request, action, actionKey,username);
	}

	/**
	 * 充值漏单补单
	 */
	public boolean logPatchRecharge(AdminUser uEntity, HttpServletRequest request, String billno, String payBillno, String remarks,String actionKey) {
		String action = String.format("充值漏单补单；订单号：%s；支付单号：%s；说明：%s", billno, payBillno, remarks);
		return add(uEntity, request, action, actionKey,null);
	}

	/**
	 * 删除日结数据
	 */
	public boolean logDelDailySettle(AdminUser uEntity, HttpServletRequest request, String username,String actionKey) {
		String action = "删除团队日结；用户名：%s；";

		Object[] values = {username};
		action = String.format(action, values);

		return add(uEntity, request, action, actionKey,username);
	}

	/**
	 * 编辑日结比例
	 */
	public boolean logEditDailySettleScale(AdminUser uEntity, HttpServletRequest request, UserDailySettle uds, double beforeScale, double afterScale,String actionKey) {
		String action = "编辑用户日结比例；ID：%s；编辑前比例：%s；编辑后比例：%s；";

		Object[] values = {uds.getId(), beforeScale, afterScale};
		action = String.format(action, values);

		return add(uEntity, request, action, actionKey,null);
	}

	/**
	 * 删除用户契约分红
	 */
	public boolean logDelDividend(AdminUser uEntity, HttpServletRequest request, String username,String actionKey) {
		String action = "删除契约分红；用户名：%s；";

		Object[] values = {username};
		action = String.format(action, values);

		return add(uEntity, request, action, actionKey,username);
	}

	/**
	 * 线路转移
	 */
	public boolean logChangeLine(AdminUser uEntity, HttpServletRequest request, String aUser, String bUser,String actionKey) {
		String action = String.format("线路转移；转移线路：%s；目标线路：%s", aUser, bUser);
		return add(uEntity, request, action, actionKey,aUser);
	}

	public boolean logChangeZhaoShang(AdminUser uEntity, HttpServletRequest request, String username, int isCJZhaoShang,String actionKey) {
		String action;
		if (isCJZhaoShang == Global.DAIYU_IS_CJ_ZHAO_SHANG_YES) {
			action = String.format("超级招商转为招商；用户名：%s", username);
		}
		else {
			action = String.format("招商转为超级招商；用户名：%s", username);
		}
		return add(uEntity, request, action, actionKey, username);
	}
}