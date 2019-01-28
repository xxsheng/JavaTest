package admin.domains.jobs;

import admin.domains.content.dao.AdminUserLogDao;
import admin.domains.content.entity.AdminUser;
import admin.domains.content.entity.AdminUserLog;
import javautils.date.Moment;
import javautils.http.HttpUtil;
import javautils.ip.IpUtil;
import javautils.math.MathUtil;
import lottery.domains.content.entity.*;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class AdminUserLogJob {
	
	@Autowired
	private AdminUserLogDao adminUserLogDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	private BlockingQueue<AdminUserLog> logQueue = new LinkedBlockingDeque<>();

	@Scheduled(cron = "0/5 * * * * *")
	public void run() {
		if(logQueue != null && logQueue.size() > 0) {
			try {
				List<AdminUserLog> list = new LinkedList<>();
				logQueue.drainTo(list, 1000);
				adminUserLogDao.save(list);
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
	
	boolean add(AdminUser uEntity, HttpServletRequest request, String action) {
		int userId = uEntity.getId();
		String ip = HttpUtil.getClientIpAddr(request);
		String address = ip2Address(ip);
		String time = new Moment().toSimpleTime();
		AdminUserLog entity = new AdminUserLog(userId, ip, address, action, time);
		String userAgent = request.getHeader("user-agent");
		entity.setUserAgent(userAgent);
		return logQueue.offer(entity);
	}

	/**
	 * 添加用户
	 */
	public boolean logAddUser(AdminUser uEntity, HttpServletRequest request, String username, String relatedUsers, int type, double point) {
		String formatType = "未知";
		String _relatedInfo = "";
		if(type == Global.USER_TYPE_PROXY) formatType = "代理";
		if(type == Global.USER_TYPE_PLAYER) formatType = "玩家";
		if(type == Global.USER_TYPE_RELATED) {
			formatType = "关联账号";
			_relatedInfo = "关联会员：" + relatedUsers + "；";
		}
		String action = String.format("添加会员账号；用户名：%s；用户类型：%s；返点：%s；%s", username, formatType, point, _relatedInfo);
		return add(uEntity, request, action);
	}
	
	/**
	 * 线路转移
	 */
	public boolean logChangeLine(AdminUser uEntity, HttpServletRequest request, String aUser, String bUser) {
		String action = String.format("线路转移；转移线路：%s；目标线路：%s", aUser, bUser);
		return add(uEntity, request, action);
	}
	
	/**
	 * 充值
	 */
	public boolean logRecharge(AdminUser uEntity, HttpServletRequest request, String username, int type, int account, double amount, int limit, String remarks) {
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
		return add(uEntity, request, action);
	}
	
	/**
	 * 冻结用户
	 */
	public boolean logLockUser(AdminUser uEntity, HttpServletRequest request, String username, int status, String message) {
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
		String action = String.format("冻结用户；用户名：%s；冻结状态：%s；冻结说明：%s", username, formatStatus, message);
		return add(uEntity, request, action);
	}

	/**
	 * 冻结团队
	 */
	public boolean logLockTeam(AdminUser uEntity, HttpServletRequest request, String username, String message) {
		String action = String.format("冻结团队；用户名：%s；冻结说明：%s", username, message);
		return add(uEntity, request, action);
	}
	
	/**
	 * 解冻团队
	 */
	public boolean logUnLockTeam(AdminUser uEntity, HttpServletRequest request, String username, String message) {
		String action = String.format("解冻团队；用户名：%s；解冻说明：%s", username, message);
		return add(uEntity, request, action);
	}
	/**
	 * 禁止团队取款
	 */
	public boolean prohibitTeamWithdraw(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("禁止团队取款；用户名：%s；", username);
		return add(uEntity, request, action);
	}
	/**
	 * 允许团队取款
	 */
	public boolean allowTeamWithdraw(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("开启团队取款；用户名：%s；", username);
		return add(uEntity, request, action);
	}
	/**
	 * 允许团队上下级转账
	 */
	public boolean allowTeamTransfers(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("开启团队上下级转账；用户名：%s；", username);
		return add(uEntity, request, action);
	}
	
	/**
	 * 允许团队转账
	 */
	public boolean allowTeamPlatformTransfers(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("开启团队转账；用户名：%s；", username);
		return add(uEntity, request, action);
	}
	/**
	 * 禁止团队取款
	 */
	public boolean prohibitTeamTransfers(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("禁止团队上下级转账；用户名：%s；", username);
		return add(uEntity, request, action);
	}
	/**
	 * 禁止团队转账
	 */
	public boolean prohibitTeamPlatformTransfers(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("禁止团队转账；用户名：%s；", username);
		return add(uEntity, request, action);
	}
	/**
	 * 管理员操作用户转账
	 */
	public boolean logUserTransfer(AdminUser uEntity, HttpServletRequest request, String aUser, String bUser, double money,String remarks) {
		String moneyStr = MathUtil.doubleToStringDown(money, 1);
		String action = String.format("管理操作用户转账；待转会员：%s；目标会员：%s；金额：%s；备注：%s", aUser, bUser, moneyStr,remarks);
		return add(uEntity, request, action);
	}

	/**
	 * 解冻用户
	 */
	public boolean logUnlockUser(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("解冻用户；用户名：%s", username);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户登录密码
	 */
	public boolean logModLoginPwd(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("修改用户登录密码；用户名：%s", username);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户资金密码
	 */
	public boolean logModWithdrawPwd(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("修改用户资金密码；用户名：%s", username);
		return add(uEntity, request, action);
	}
	
	/**
	 * 重置用户绑定邮箱
	 */
	public boolean logResetEmail(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("重置用户绑定邮箱；用户名：%s", username);
		return add(uEntity, request, action);
	}
	
	/**
	 * 重置密保
	 */
	public boolean logResetSecurity(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("重置密保；用户名：%s", username);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改返点
	 */
	public boolean logModPoint(AdminUser uEntity, HttpServletRequest request, String username, double point) {
		String action = String.format("修改返点；用户名：%s；返点：%s", username, point);
		return add(uEntity, request, action);
	}
	
	/**
	 * 统一降点
	 */
	public boolean logDownPoint(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("统一降点；用户名：%s", username);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户私返点数
	 */
	public boolean logModExtraPoint(AdminUser uEntity, HttpServletRequest request, String username, double point) {
		String action = String.format("修改用户私返点数；用户名：%s；返点：%s", username, point);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户投注权限
	 */
	public boolean logModBStatus(AdminUser uEntity, HttpServletRequest request, String username, int status, String message) {
		String formatStatus = "未知";
		if(status == 0) {
			formatStatus = "正常";
		}
		if(status == -1) {
			formatStatus = "禁止投注";
		}
		if(status == -2) {
			formatStatus = "自动掉线";
		}
		if(status == -3) {
			formatStatus = "投注超时";
		}
		String action = String.format("修改用户投注权限；用户名：%s；投注权限：%s；说明：%s", username, formatStatus, message);
		return add(uEntity, request, action);
	}
	
	/**
	 * 回收账号
	 */
	public boolean logRecoverUser(AdminUser uEntity, HttpServletRequest request, User user) {
		String action = "回收账号；用户名：%s；主账户：%s；彩票账户：%s；百家乐账户：%s；";
		String username = user.getUsername();
		String totalMoney = new BigDecimal(user.getTotalMoney()).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();
		String lotteryMoney = new BigDecimal(user.getLotteryMoney()).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();
		String baccaratMoney = new BigDecimal(user.getBaccaratMoney()).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();

		Object[] values = {username, totalMoney, lotteryMoney, baccaratMoney};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户配额
	 */
	public boolean logRecoverUser(AdminUser uEntity, HttpServletRequest request, String username, int amount1, int amount2, int amount3) {
		String amount = amount1 + "," + amount2 + "," + amount3;
		String action = String.format("修改用户配额；用户名：%s；配额数量：[%s]", username, amount);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户同级开号权限
	 */
	public boolean logModEqualCode(AdminUser uEntity, HttpServletRequest request, String username, int status) {
		String formatStatus = status == 1 ? "允许" : "不允许";
		String action = String.format("修改用户同级开号权限；用户名：%s；状态：%s", username, formatStatus);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户上下级转账权限
	 */
	public boolean logModTransfers(AdminUser uEntity, HttpServletRequest request, String username, int status) {
		String formatStatus = status == 1 ? "允许" : "不允许";
		String action = String.format("修改用户上下级转账权限；用户名：%s；状态：%s", username, formatStatus);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户取款权限
	 */
	public boolean logModWithdraw(AdminUser uEntity, HttpServletRequest request, String username, int status) {
		String formatStatus = status == 1 ? "允许" : "不允许";
		String action = String.format("修改用户取款权限；用户名：%s；状态：%s", username, formatStatus);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户个人转账权限
	 */
	public boolean logModPlatformTransfers(AdminUser uEntity, HttpServletRequest request, String username, int status) {
		String formatStatus = status == 1 ? "允许" : "不允许";
		String action = String.format("修改用户转账权限；用户名：%s；状态：%s", username, formatStatus);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户绑定取款人
	 */
	public boolean logModWithdrawName(AdminUser uEntity, HttpServletRequest request, String username, String withdrawName) {
		String action = String.format("修改用户绑定取款人；用户名：%s；绑定取款人：%s", username, withdrawName);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户绑定邮箱
	 */
	public boolean logModEmail(AdminUser uEntity, HttpServletRequest request, String username, String email) {
		String action = String.format("修改用户绑定邮箱；用户名：%s；绑定邮箱：%s", username, email);
		return add(uEntity, request, action);
	}
	
	/**
	 * 清空用户提款消费量
	 */
	public boolean logResetLimit(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("清空用户提款消费量；用户名：%s", username);
		return add(uEntity, request, action);
	}
	
	public boolean logChangeProxy(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("玩家转代理；用户名：%s", username);
		return add(uEntity, request, action);
	}

	public boolean logChangeZhaoShang(AdminUser uEntity, HttpServletRequest request, String username, int isCJZhaoShang) {
		String action;
		if (isCJZhaoShang == Global.DAIYU_IS_CJ_ZHAO_SHANG_YES) {
			action = String.format("超级招商转为招商；用户名：%s", username);
		}
		else {
			action = String.format("招商转为超级招商；用户名：%s", username);
		}
		return add(uEntity, request, action);
	}

	/**
	 * 解绑谷歌身份验证器
	 */
	public boolean unbindGoogle(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("解绑谷歌身份验证器；用户名：%s", username);
		return add(uEntity, request, action);
	}

	/**
	 * 清空账户时间锁
	 */
	public boolean resetLockTime(AdminUser uEntity, HttpServletRequest request, String username) {
		String action = String.format("清空账户时间锁；用户名：%s", username);
		return add(uEntity, request, action);
	}

	/**
	 * 添加用户银行卡
	 */
	public boolean logAddUserCard(AdminUser uEntity, HttpServletRequest request, String username, int bankId, String bankBranch, String cardId) {
		PaymentBank paymentBank = lotteryDataFactory.getPaymentBank(bankId);
		String bankName = paymentBank.getName();
		String action = String.format("添加用户银行卡；用户名：%s；开户行：%s；支行：%s；卡号：%s", username, bankName, bankBranch, cardId);
		return add(uEntity, request, action);
	}
	
	/**
	 * 修改用户银行卡资料
	 */
	public boolean logModUserCard(AdminUser uEntity, HttpServletRequest request, String username, int bankId, String bankBranch, String cardId) {
		PaymentBank paymentBank = lotteryDataFactory.getPaymentBank(bankId);
		String bankName = paymentBank.getName();
		String action = String.format("修改用户银行卡资料；用户名：%s；开户行：%s；支行：%s；卡号：%s", username, bankName, bankBranch, cardId);
		return add(uEntity, request, action);
	}
	
	/**
	 * 充值漏单补单
	 */
	public boolean logPatchRecharge(AdminUser uEntity, HttpServletRequest request, String billno, String payBillno, String remarks) {
		String action = String.format("充值漏单补单；订单号：%s；支付单号：%s；说明：%s", billno, payBillno, remarks);
		return add(uEntity, request, action);
	}
	
	/**
	 * 充值订单撤销
	 */
	public boolean logCancelRecharge(AdminUser uEntity, HttpServletRequest request, String billno) {
		String action = String.format("充值订单撤销；订单号：%s", billno);
		return add(uEntity, request, action);
	}
	
	/**
	 * 审核用户提现
	 */
	public boolean logCheckWithdraw(AdminUser uEntity, HttpServletRequest request, int id, int status) {
		String formatStatus = "未知";
		if(status == 1) {
			formatStatus = "已通过";
		}
		if(status == -1) {
			formatStatus = "未通过";
		}
		String action = String.format("审核用户提现；订单ID：%s；审核结果：%s", id, formatStatus);
		return add(uEntity, request, action);
	}
	
	/**
	 * 使用手动出款
	 */
	public boolean logManualPay(AdminUser uEntity, HttpServletRequest request, int id, String payBillno, String remarks) {
		String action = String.format("使用手动出款；订单ID：%s；支付单号：%s；备注说明：%s", id, payBillno, remarks);
		return add(uEntity, request, action);
	}

	/**
	 * 使用API代付
	 */
	public boolean logAPIPay(AdminUser uEntity, HttpServletRequest request, int id, PaymentChannel paymentChannel) {
		String action = String.format("使用API代付；订单ID：%s；第三方：%s", id, paymentChannel.getName());
		return add(uEntity, request, action);
	}

	/**
	 * 拒绝支付用户提现
	 */
	public boolean logRefuseWithdraw(AdminUser uEntity, HttpServletRequest request, int id, String reason, String remarks) {
		String action = String.format("拒绝支付用户提现；订单ID：%s；拒绝原因：%s；备注说明：%s", id, reason, remarks);
		return add(uEntity, request, action);
	}
	
	/**
	 * 用户提现审核失败
	 */
	public boolean reviewedFail(AdminUser uEntity, HttpServletRequest request, int id, String remarks) {
		String action = String.format("用户提现审核失败；订单ID：%s；备注说明：%s", id, remarks);
		return add(uEntity, request, action);
	}
	
	/**
	 * 确认用户提现失败
	 */
	public boolean logWithdrawFailure(AdminUser uEntity, HttpServletRequest request, int id, String remarks) {
		String action = String.format("确认用户提现失败；订单ID：%s；备注说明：%s", id, remarks);
		return add(uEntity, request, action);
	}

	/**
	 * 确认用户提现到账
	 */
	public boolean logCompleteRemitWithdraw(AdminUser uEntity, HttpServletRequest request, int id) {
		String action = String.format("确认用户提现到账；订单ID：%s；", id);
		return add(uEntity, request, action);
	}

	/**
	 * 锁定用户提现
	 */
	public boolean logLockWithdraw(AdminUser uEntity, HttpServletRequest request, int id) {
		String action = String.format("锁定用户提现；订单ID：%s；", id);
		return add(uEntity, request, action);
	}

	/**
	 * 解锁用户提现
	 */
	public boolean logUnLockWithdraw(AdminUser uEntity, HttpServletRequest request, int id) {
		String action = String.format("解锁用户提现；订单ID：%s；", id);
		return add(uEntity, request, action);
	}

	/**
	 * 撤销用户投注订单
	 */
	public boolean logCancelOrder(AdminUser uEntity, HttpServletRequest request, int id) {
		String action = String.format("撤销用户投注订单；订单ID：%s", id);
		return add(uEntity, request, action);
	}
	
	/**
	 * 批量撤销订单
	 */
	public boolean logBatchCancelOrder(AdminUser uEntity, HttpServletRequest request, int lotteryId, Integer ruleId, String expect, String match) {
		Lottery lottery = lotteryDataFactory.getLottery(lotteryId);
		if(lottery != null) {
			String formatMethod = "全部玩法";
			if(ruleId != null) {
				LotteryPlayRules rules = lotteryDataFactory.getLotteryPlayRules(ruleId);
				if(rules != null) {
					LotteryPlayRulesGroup group = lotteryDataFactory.getLotteryPlayRulesGroup(rules.getGroupId());
					if (group != null) {
						formatMethod = "[" + group.getName() + "_" + rules.getName() + "]";
					}
				}
			}
			String formatExpect = expect;
			if("eq".equals(match)) {
				formatExpect = "等于" + expect;
			}
			if("le".equals(match)) {
				formatExpect = "小于等于" + expect;
			}
			if("ge".equals(match)) {
				formatExpect = "大于等于" + expect;
			}
			String action = String.format("批量撤销用户订单；彩票类型：%s；玩法规则：%s；投注期号：%s", lottery.getShowName(), formatMethod, formatExpect);
			return add(uEntity, request, action);
		}
		return false;
	}

	/**
	 * 同意发放分红
	 */
	public boolean logAgreeDividend(AdminUser uEntity, HttpServletRequest request, String username, UserDividendBill userDividendBill, String remarks) {
		String action = "同意发放彩票分红；用户名：%s；周期：%s~%s；备注：%s；";
		String startDate = userDividendBill.getIndicateStartDate();
		String endDate = userDividendBill.getIndicateEndDate();

		Object[] values = {username, startDate, endDate, remarks == null ? "" : remarks};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}

	/**
	 * 拒绝发放分红
	 */
	public boolean logDenyDividend(AdminUser uEntity, HttpServletRequest request, String username, UserDividendBill userDividendBill,
								   String remarks) {
		String action = "拒绝发放彩票分红；用户名：%s；周期：%s~%s；拒绝前用户金额：%s；拒绝后用户金额：%s；备注：%s；";
		String startDate = userDividendBill.getIndicateStartDate();
		String endDate = userDividendBill.getIndicateEndDate();

		Object[] values = {username, startDate, endDate, remarks == null ? "" : remarks};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}

	/**
	 * 删除分红数据
	 */
	public boolean logDelDividend(AdminUser uEntity, HttpServletRequest request, String username, UserDividendBill userDividendBill, String remarks) {
		String action = "删除彩票分红数据；用户名：%s；周期：%s~%s；用户金额：%s；备注：%s；";
		String startDate = userDividendBill.getIndicateStartDate();
		String endDate = userDividendBill.getIndicateEndDate();
		String userAmount = new BigDecimal(userDividendBill.getUserAmount()).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();

		Object[] values = {username, startDate, endDate, userAmount, remarks == null ? "" : remarks};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}

	/**
	 * 同意发放老虎机真人体育分红
	 */
	public boolean logAgreeGameDividend(AdminUser uEntity, HttpServletRequest request, String username, UserGameDividendBill userDividendBill,
									double userAmount, String remarks) {
		String action = "同意发放老虎机真人体育分红；用户名：%s；周期：%s~%s；同意前用户金额：%s；同意后用户金额：%s；备注：%s；";
		String startDate = userDividendBill.getIndicateStartDate();
		String endDate = userDividendBill.getIndicateEndDate();
		String beforeUserAmount = new BigDecimal(userDividendBill.getUserAmount()).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();
		String afterUserAmount = new BigDecimal(userAmount).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();

		Object[] values = {username, startDate, endDate, beforeUserAmount, afterUserAmount, remarks == null ? "" : remarks};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}

	/**
	 * 拒绝发放老虎机真人体育分红
	 */
	public boolean logDenyGameDividend(AdminUser uEntity, HttpServletRequest request, String username, UserGameDividendBill userDividendBill,
								   double userAmount, String remarks) {
		String action = "拒绝发放老虎机真人体育分红；用户名：%s；周期：%s~%s；拒绝前用户金额：%s；拒绝后用户金额：%s；备注：%s；";
		String startDate = userDividendBill.getIndicateStartDate();
		String endDate = userDividendBill.getIndicateEndDate();
		String beforeUserAmount = new BigDecimal(userDividendBill.getUserAmount()).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();
		String afterUserAmount = new BigDecimal(userAmount).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();

		Object[] values = {username, startDate, endDate, beforeUserAmount, afterUserAmount, remarks == null ? "" : remarks};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}

	/**
	 * 删除老虎机真人体育分红数据
	 */
	public boolean logDelGameDividend(AdminUser uEntity, HttpServletRequest request, String username, UserGameDividendBill userDividendBill, String remarks) {
		String action = "删除老虎机真人体育分红数据；用户名：%s；周期：%s~%s；用户金额：%s；备注：%s；";
		String startDate = userDividendBill.getIndicateStartDate();
		String endDate = userDividendBill.getIndicateEndDate();
		String userAmount = new BigDecimal(userDividendBill.getUserAmount()).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();

		Object[] values = {username, startDate, endDate, userAmount, remarks == null ? "" : remarks};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}

	/**
	 * 清零分红数据
	 */
	public boolean logResetDividend(AdminUser uEntity, HttpServletRequest request, String username, UserDividendBill userDividendBill, String remarks) {
		String action = "清零分红数据；用户名：%s；周期：%s~%s；清空金额：%s；备注：%s；";
		String startDate = userDividendBill.getIndicateStartDate();
		String endDate = userDividendBill.getIndicateEndDate();
		String availableAmount = new BigDecimal(userDividendBill.getAvailableAmount()).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString();

		Object[] values = {username, startDate, endDate, availableAmount, remarks == null ? "" : remarks};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}

	/**
	 * 添加新游戏
	 */
	public boolean logAddGame(AdminUser uEntity, HttpServletRequest request, String gameName) {
		String action = String.format("添加第三方新游戏；游戏名：%s；", gameName);
		return add(uEntity, request, action);
	}

	/**
	 * 修改游戏
	 */
	public boolean logUpdateGame(AdminUser uEntity, HttpServletRequest request, String gameName) {
		String action = String.format("修改游戏；游戏名：%s；", gameName);
		return add(uEntity, request, action);
	}

	/**
	 * 删除游戏
	 */
	public boolean logDelGame(AdminUser uEntity, HttpServletRequest request, String gameName) {
		String action = String.format("删除第三方游戏；游戏名：%s；", gameName);
		return add(uEntity, request, action);
	}

	/**
	 * 修改游戏状态
	 */
	public boolean logUpdateGameStatus(AdminUser uEntity, HttpServletRequest request, String gameName, int status) {
		String statusCN = "未知";
		if (status == 0) {
			statusCN = "禁用";
		}
		else if (status == 1) {
			statusCN = "启用";
		}
		String action = String.format("修改第三方游戏状态；游戏名：%s；状态：%s；", gameName, statusCN);
		return add(uEntity, request, action);
	}

	/**
	 * 修改游戏显示
	 */
	public boolean logUpdateGameDisplay(AdminUser uEntity, HttpServletRequest request, String gameName, int display) {
		String displayCN = "未知";
		if (display == 0) {
			displayCN = "不显示";
		}
		else if (display == 1) {
			displayCN = "显示";
		}
		String action = String.format("修改第三方游戏显示状态；游戏名：%s；是否显示：%s；", gameName, displayCN);
		return add(uEntity, request, action);
	}

	/**
	 * 修改游戏平台状态
	 */
	public boolean logPlatformModStatus(AdminUser uEntity, HttpServletRequest request, int id, int status) {
		String statusCN;
		if (status == 1) {
			statusCN = "启用";
		}
		else {
			statusCN = "禁用";
		}
		String action = String.format("修改第三方游戏平台状态；平台ID：%s；状态：%s；", id, statusCN);
		return add(uEntity, request, action);
	}

	/**
	 * 锁定大额中奖状态
	 */
	public boolean logLockHighPrize(AdminUser uEntity, HttpServletRequest request, int id) {
		String action = String.format("锁定大额中奖记录；记录ID：%s；", id);
		return add(uEntity, request, action);
	}

	/**
	 * 解锁大额中奖状态
	 */
	public boolean logUnLockHighPrize(AdminUser uEntity, HttpServletRequest request, int id) {
		String action = String.format("解锁大额中奖记录；记录ID：%s；", id);
		return add(uEntity, request, action);
	}

	/**
	 * 确认大额中奖状态
	 */
	public boolean logConfirmHighPrize(AdminUser uEntity, HttpServletRequest request, int id) {
		String action = String.format("确认大额中奖记录；记录ID：%s；", id);
		return add(uEntity, request, action);
	}

	/**
	 * 修改红包雨配置
	 */
	public boolean logEditRedPacketRainConfig(AdminUser uEntity, HttpServletRequest request) {
		String action = String.format("修改红包雨配置；");
		return add(uEntity, request, action);
	}

	/**
	 * 启用&停止红包雨活动
	 */
	public boolean logUpdateStatusRedPacketRain(AdminUser uEntity, HttpServletRequest request, int status) {
		String statusCN;
		if (status == 1) {
			statusCN = "启用";
		}
		else {
			statusCN = "禁用";
		}
		String action = String.format(statusCN + "红包雨活动");
		return add(uEntity, request, action);
	}

	/**
	 * 修改首充活动配置
	 */
	public boolean logEditFirstRechargeConfig(AdminUser uEntity, HttpServletRequest request, String rules) {
		String action = String.format("修改首充活动配置；规则：%s", rules);
		return add(uEntity, request, action);
	}

	/**
	 * 启用&停止首充活动
	 */
	public boolean logUpdateStatusFirstRecharge(AdminUser uEntity, HttpServletRequest request, int status) {
		String statusCN;
		if (status == 1) {
			statusCN = "启用";
		}
		else {
			statusCN = "禁用";
		}
		String action = String.format(statusCN + "首充活动");
		return add(uEntity, request, action);
	}

	/**
	 * 修改关联上级
	 */
	public boolean logModifyRelatedUpper(AdminUser uEntity, HttpServletRequest request, String username, String relatedUpUser, double relatedPoint, String remarks) {
		String action = String.format("修改关联上级；用户名：%s；关联上级：%s；关联返点：%s；备注：%s；", username, relatedUpUser, relatedPoint, remarks);
		return add(uEntity, request, action);
	}

	/**
	 * 解除关联上级
	 */
	public boolean logReliveRelatedUpper(AdminUser uEntity, HttpServletRequest request, String username, String remarks) {
		String action = String.format("解除关联上级；用户名：%s；备注：%s；", username, remarks);
		return add(uEntity, request, action);
	}

	/**
	 * 修改关联会员
	 */
	public boolean logModifyUpdateRelatedUsers(AdminUser uEntity, HttpServletRequest request, String username, String relatedUsers, String remarks) {
		String action = String.format("修改关联会员；用户名：%s；关联会员：%s；备注：%s；", username, relatedUsers, remarks);
		return add(uEntity, request, action);
	}

	/**
	 * 删除银行卡解锁记录
	 */
	public boolean logDelUserCardUnbindRecord(AdminUser uEntity, HttpServletRequest request, String  cardId, String remarks) {
		String action = String.format("删除银行卡解锁记录；银行卡：%s；备注：%s；", cardId, remarks);
		return add(uEntity, request, action);
	}

	/**
	 * 增减彩票期数
	 */
	public boolean logModifyRefExpect(AdminUser uEntity, HttpServletRequest request, String lottery, int times) {
		String action = String.format("增减彩票期号；彩种：%s；增减期数：%s；", lottery, times);
		return add(uEntity, request, action);
	}

	/**
	 * 增加充值通道账号
	 */
	public boolean logAddPaymenChannel(AdminUser uEntity, HttpServletRequest request, String name) {
		String action = String.format("增加充值通道账号；名称：%s；；", name);
		return add(uEntity, request, action);
	}

	/**
	 * 编辑充值通道账号
	 */
	public boolean logEditPaymenChannel(AdminUser uEntity, HttpServletRequest request, String name) {
		String action = String.format("编辑充值通道账号；名称：%s；", name);
		return add(uEntity, request, action);
	}

	/**
	 * 修改充值通道账号状态
	 */
	public boolean logEditPaymenChannelStatus(AdminUser uEntity, HttpServletRequest request, int id, int status) {
		String statusStr = status == 0 ? "启用" : "禁用";
		String action = String.format("修改充值通道账号状态；ID：%s；修改为状态：%s；", id, statusStr);
		return add(uEntity, request, action);
	}

	/**
	 * 删除充值通道账号
	 */
	public boolean logDeletePaymenChannel(AdminUser uEntity, HttpServletRequest request, int id) {
		String action = String.format("删除充值通道账号；ID：%s；", id);
		return add(uEntity, request, action);
	}

	/**
	 * 编辑用户日结配置
	 */
	public boolean logEditDailySettle(AdminUser uEntity, HttpServletRequest request, UserDailySettle uds, String scale, String sales, String loss, String minValidUser) {
		String action = "编辑用户日结配置；ID：%s；用户名：%s；编辑前比例：%s；编辑后比例：%s；编辑前销量：%s；编辑后销量：%s；编辑前亏损：%s；编辑后亏损：%s；编辑前有效人数：%s；编辑后有效人数：%s；";

		UserVO user = lotteryDataFactory.getUser(uds.getUserId());
		String username;
		if (user != null) {
			username = user.getUsername();
		}
		else {
			username = "未知";
		}

		Object[] values = {uds.getId(), username, uds.getScaleLevel(), scale, uds.getSalesLevel(), sales, uds.getLossLevel(), loss, uds.getUserLevel(), minValidUser};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}

	/**
	 * 新增契约日结
	 */
	public boolean logAddDailySettle(AdminUser uEntity, HttpServletRequest request, String username, String scaleLevel, String salesLevel, String lossLevel, String minValidUser, int status) {
		String action = "新增契约日结；用户名：%s；销量：%s；亏损：%s；比例：%s；有效人数：%s；状态：%s；";

		String statusCN;
		if (status == Global.DAILY_SETTLE_VALID) {
			statusCN = "生效";
		}
		else if (status == Global.DAILY_SETTLE_REQUESTED) {
			statusCN = "待同意";
		}
		else {
			statusCN = "未知";
		}

		Object[] values = {username, salesLevel, lossLevel, scaleLevel, minValidUser, statusCN};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}

	/**
	 * 编辑用户分红配置
	 */
	public boolean logEditDividend(AdminUser uEntity, HttpServletRequest request, UserDividend ud, String scaleLevel, String lossLevel,String salesLevel, String userLevel) {
		String action = "编辑用户分红配置；ID：%s；用户名：%s；编辑前比例：%s；编辑后比例：%s；编辑前最低有效人数：%s；编辑后最低有效人数：%s；"
							+ " 编辑前销量：%s; 编辑后销量：%s; 编辑前亏损：%s；编辑后亏损：%s；";

		UserVO user = lotteryDataFactory.getUser(ud.getUserId());
		String username;
		if (user != null) {
			username = user.getUsername();
		}
		else {
			username = "未知";
		}

		Object[] values = {ud.getId(), username, (ud.getScaleLevel()), scaleLevel, ud.getUserLevel(), userLevel,ud.getSalesLevel(),salesLevel,ud.getLossLevel(),lossLevel};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}

	/**
	 * 新增契约分红
	 */
	public boolean logAddDividend(AdminUser uEntity, HttpServletRequest request, String username, String scaleLevel, String lossLevel, String salesLevel, String userLevel, int status) {
		String action = "新增契约分红；用户名：%s；阶梯比例：%s；阶梯销量：%s,阶梯亏损：%s，最低有效人数：%s；状态：%s；";

		String statusCN;
		if (status == Global.DIVIDEND_VALID) {
			statusCN = "生效";
		}
		else if (status == Global.DIVIDEND_REQUESTED) {
			statusCN = "待同意";
		}
		else {
			statusCN = "未知";
		}

		Object[] values = {username, scaleLevel,salesLevel,lossLevel, userLevel, statusCN};
		action = String.format(action, values);

		return add(uEntity, request, action);
	}
}