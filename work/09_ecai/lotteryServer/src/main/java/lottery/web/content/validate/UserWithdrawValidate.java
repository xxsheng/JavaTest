package lottery.web.content.validate;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.*;
import lottery.domains.content.biz.read.GameBetsReadService;
import lottery.domains.content.biz.read.UserBetsReadService;
import lottery.domains.content.entity.*;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.config.WithdrawConfig;
import lottery.domains.content.vo.user.UserBankcardUnbindVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.content.vo.user.UserWithdrawLimitVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class UserWithdrawValidate {
	private static final Logger log = LoggerFactory.getLogger(UserWithdrawValidate.class);

	/**
	 * DAO
	 */
	@Autowired
	private UserBetsReadService uBetsReadService;

	@Autowired
	private GameBetsReadService gameBetsReadService;
	
	@Autowired
	private UserBlacklistService uBlacklistService;
	
	@Autowired
	private UserWhitelistService uWhitelistService;
	
	@Autowired
	private UserWithdrawLimitService uWithdrawLimitService;
	
	@Autowired
	private UserRechargeService uRechargeService;

	@Autowired
	private UserBankcardUnbindService UnbindService;

	@Autowired
	private DataFactory dataFactory;
	
	public boolean validateToCard(WebJSON json, User uEntity, double amount, int todayCount, double todayMoney) {
		WithdrawConfig config = dataFactory.getWithdrawConfig();
		double minAmount = config.getMinAmount();
		double maxAmount = config.getMaxAmount();
		int maxTimes = config.getMaxTimes();
		// 先验证取现资金
		if(amount < 0) {
			json.set(2, "2-1017");
			return false;
		}
		if(amount < minAmount) {
			json.set(2, "2-1018", minAmount);
			return false;
		}
		if(amount > maxAmount) {
			json.set(2, "2-1019", maxAmount);
			return false;
		}
		if(amount > uEntity.getTotalMoney()) {
			json.set(2, "2-1109");
			return false;
		}
		// 验证一下取现次数
		if(todayCount >= maxTimes) {
			json.set(2, "2-1020", maxTimes);
			return false;
		}
		// 验证取现金额
		double[] vipWithdraw = dataFactory.getVipConfig().getWithdraw();
		int vipLevel = uEntity.getVipLevel();
		double maxDayWithdraw = vipWithdraw[vipLevel];
		if(todayMoney >= maxDayWithdraw) {
			json.set(2, "2-1070");
			return false;
		}
		if(todayMoney + amount > maxDayWithdraw) {
			json.set(2, "2-1071", (maxDayWithdraw - todayMoney));
			return false;
		}
		// 验证消费量
		if(!validateTotalLimit(json, uEntity.getId())) {
			return false;
		}
		return true;
	}
	
	public boolean validateToUser(WebJSON json, User uEntity, double amount) {
		if(amount < 0) {
			json.set(2, "2-1017");
			return false;
		}
		if(amount > uEntity.getTotalMoney()) {
			json.set(2, "2-1109");
			return false;
		}
		return true;
	}

	public boolean validateTotalLimit(WebJSON json, int userId) {
		UserVO user = dataFactory.getUser(userId);
		String username = user == null ? "未知" : user.getUsername();

		List<UserWithdrawLimit> records = uWithdrawLimitService.getUserWithdrawLimits(userId);
		if(CollectionUtils.isEmpty(records)) {
			log.debug("用户{}发起提款，没有找到提款消费要求数据，允许提款", username);
			return true;
		}

		List<UserWithdrawLimit> entities = new ArrayList<>();
		for (UserWithdrawLimit record : records) {
			if (StringUtils.isNotEmpty(record.getRechargeTime()) && record.getRechargeMoney() > 0) {
				entities.add(record);
			}
		}
		if(CollectionUtils.isEmpty(entities)) {
			log.debug("用户{}发起提款，没有找到提款消费要求数据，允许提款", username);
			return true;
		}

		// 所有充值类的数据
		List<UserWithdrawLimit> rechargeEntities = new ArrayList<>();
		for (UserWithdrawLimit entity : entities) {
			if (entity.getConsumptionRequirements() > 0) {
				rechargeEntities.add(entity);
			}
		}

		int listSize = rechargeEntities.size();

		List<UserWithdrawLimitVO> rechargeVOs = new ArrayList<>();
		for(int i = 0; i < listSize; i++){
			UserWithdrawLimit entity = rechargeEntities.get(i);
			double needConsumption = entity.getConsumptionRequirements();

			double remainConsumption = 0; // 剩余消费要求
			double totalBilling = 0; // 消费

			if (needConsumption < 0) {
				remainConsumption = needConsumption;
			}
			else if (needConsumption > 0) {
				String rechargeETime;
				String rechargeSTime = entity.getRechargeTime();
				if (listSize > i+1) {
					rechargeETime = rechargeEntities.get(i+1).getRechargeTime();
				}else{
					rechargeETime = new Moment().toSimpleTime();
				}
				double[] consumptions = getConsumptions(userId, needConsumption, rechargeSTime, rechargeETime);
				remainConsumption = consumptions[0];
				totalBilling = consumptions[1];
			}

			UserWithdrawLimitVO userWithdrawLimitVO = new UserWithdrawLimitVO(entity, totalBilling, remainConsumption);
			rechargeVOs.add(userWithdrawLimitVO);
		}

		// 所有已消费记录都会往较早时间的充值进行消费填补，但不会往后填补。
		if (rechargeVOs.size() > 1) {

			for (int i = 0; i < rechargeVOs.size(); i++) {
				UserWithdrawLimitVO last = rechargeVOs.get(i);
				double lastBilling = last.getTotalBilling();
				if (lastBilling <= 0) {
					continue;
				}

				for (int j = 0; j < i; j++) {
					UserWithdrawLimitVO limitVO = rechargeVOs.get(j);
					double remainConsumption = limitVO.getRemainConsumption();
					if (remainConsumption > 0) {
						double giveBilling = remainConsumption > lastBilling ? lastBilling : remainConsumption;
						remainConsumption = MathUtil.subtract(remainConsumption, giveBilling);
						limitVO.setTotalBilling(MathUtil.add(limitVO.getTotalBilling(), giveBilling));
						limitVO.setRemainConsumption(remainConsumption);
						lastBilling = MathUtil.subtract(lastBilling, giveBilling);
					}
				}

				last.setTotalBilling(lastBilling);
				double lastRemainConsumption = MathUtil.subtract(last.getBean().getConsumptionRequirements(), lastBilling);
				if (lastRemainConsumption < 0) {
					lastRemainConsumption = 0;
				}
				last.setRemainConsumption(lastRemainConsumption);
			}
		}

		// 再把转出类的加进来
		for (UserWithdrawLimit entity : entities) {
			if (entity.getConsumptionRequirements() < 0) {
				UserWithdrawLimitVO userWithdrawLimitVO = new UserWithdrawLimitVO(entity, 0, entity.getConsumptionRequirements());
				rechargeVOs.add(userWithdrawLimitVO);
			}
		}

		double totalRemainConsumption = 0;
		for (UserWithdrawLimitVO userWithdrawLimitVO : rechargeVOs) {
			totalRemainConsumption = MathUtil.add(totalRemainConsumption, userWithdrawLimitVO.getRemainConsumption());
		}

		if (totalRemainConsumption <= 0) {
			log.debug("用户{}发起提款，消费要求已达到，允许提款", username);
			return true;
		}

		BigDecimal bd = new BigDecimal(totalRemainConsumption);
		String amountStr = bd.setScale(0, BigDecimal.ROUND_UP).toString();
		json.set(2, "2-1021", amountStr);
		return false;
	}

	private UserWithdrawLimitVO getLastRecharge(List<UserWithdrawLimitVO> vos) {
		for (int i = vos.size() - 1; i >= 0; i--) {
			if (vos.get(i).getBean().getType() != Global.USER_WITHDRAW_TYPE_USER_OUT) {
				return vos.get(i);
			}
		}

		return null;
	}

	private double[] getConsumptions(int userId, double needConsumption, String sTime, String eTime) {
		double remainConsumption = needConsumption;

		double lotteryBilling = uBetsReadService.getBillingOrder(userId, sTime, eTime);
		if (lotteryBilling < 0) {
			lotteryBilling = 0;
		}

		remainConsumption = MathUtil.subtract(remainConsumption, lotteryBilling);
		if (remainConsumption < 0) {
			remainConsumption = 0;
		}

		double gameBilling = gameBetsReadService.getBillingOrder(userId, sTime, eTime);
		if (gameBilling < 0) {
			gameBilling = 0;
		}

		remainConsumption = MathUtil.subtract(remainConsumption, gameBilling);
		if (remainConsumption < 0) {
			remainConsumption = 0;
		}

		double totalBilling = MathUtil.add(lotteryBilling, gameBilling);

		return new double[]{remainConsumption, totalBilling};
	}

	
	/**
	 * 验证卡片
	 */
	public boolean validateCard(WebJSON json, UserCard uCard) {
		if(uCard == null) {
			json.set(2, "2-1026");
			return false;
		}
		if(uCard.getStatus() == -1) {
			json.set(2, "2-1026");
			return false;
		}
		if(uCard.getStatus() == -2) {
			json.set(2, "2-1027");
			return false;
		}
		return true;
	}

	/**
	 * 验证卡片绑定次数
	 */
	public boolean validateCardUnBind(WebJSON json, UserCard uCard) {
		UserBankcardUnbindVO unbidvo = UnbindService.getUnbindInfoBycardId(uCard.getCardId());
		if (unbidvo == null) return true;
		if (unbidvo.getUnbindNum() <= 0) return true;

		int unbidnumhour = 0;

		if(unbidvo.getUnbindNum() == 1) unbidnumhour = 48; // 第一次48小时
		if(unbidvo.getUnbindNum() >= 2) unbidnumhour = 120; // 第二次120小时

		if(!validateUnbindTime(json, unbidvo.getUnbindTime(), unbidnumhour, unbidvo.getUnbindNum())){
			return false;
		}

		return true;
	}

	/**
	 * 白名单黑名单
	 */
	public boolean testBlackWhitelist(WebJSON json, User uBean, UserCard uCard) {
		// 验证白名单
		List<UserWhitelist> wlist = uWhitelistService.getByUsername(uBean.getUsername());
		if(wlist != null && wlist.size() > 0) {
			return true;
		}
		// 验证黑名单
		List<UserBlacklist> blist = uBlacklistService.getByCard(uCard.getCardName(), uCard.getCardId());
		if(blist != null && blist.size() > 0) {
			json.set(2, "2-1023");
			return false;
		}
		return true;
	}
	
	/**
	 * 验证取款时间
	 */
	public boolean validateTime(WebJSON json) {
		// 先验证能否取款
		WithdrawConfig config = dataFactory.getWithdrawConfig();
		if(config.isEnable() == false) {
			json.setError(2);
			json.setMessage(config.getServiceMsg());
			return false;
		}
		// 再验证是否在服务区内
		String times = config.getServiceTime();
		Moment moment = new Moment();
		boolean isServiceTime = StringUtil.isServiceTime(moment, times);
		if(!isServiceTime) {
			json.set(2, "2-1048", times);
			return false;
		}
		return true;
	}
	
	/**
	 * 验证注册时间
	 */
	public boolean validateRegisterTime(WebJSON json, User uBean) {
		if (dataFactory.getWithdrawConfig().getRegisterHours() <= 0) return true;

		if (StringUtils.isEmpty(uBean.getLockTime())) return true;

		// 账号锁定时间
		Moment userLockTimeMoment = new Moment().fromTime(uBean.getLockTime());

		// 如果当前时间大于账号锁定时间，那么没有锁定
		Moment now = new Moment();
		if(now.gt(userLockTimeMoment)) return true;

		// 实际应锁定时间
		Moment realLockTimeMoment = new Moment().fromTime(uBean.getRegistTime()).add(dataFactory.getWithdrawConfig().getRegisterHours(), "hours");

		// 如果当前时间大于实际应锁定时间，即不是注册锁定
		// 比如实际应锁定到2017-06-19 18:00:00，，当前时间是2017-06-20 18:00:00，那么这就不是注册锁定，而是其它类型的锁定
		if (now.gt(realLockTimeMoment)) return true;

		json.set(2, "2-4019", dataFactory.getWithdrawConfig().getRegisterHours(), realLockTimeMoment.format("yyyy年MM月dd日HH时mm分ss秒"));
		return false;
	}

	/**
	 * 验证时间锁
	 */
	public boolean validateLockTime(WebJSON json, User uBean) {
		if(StringUtil.isNotNull(uBean.getLockTime())) {
			Moment expect = new Moment().fromTime(uBean.getLockTime());
			if(new Moment().lt(expect)) {
				json.set(2, "2-1065", expect.format("yyyy年MM月dd日HH时mm分ss秒"));
				return false;
			}
		}
		return true;
	}

	/**
	 * 验证注册时间
	 * @param json
	 * @param uBean
	 * @return
	 * @throws ParseException 
	 */
	public boolean validateRegistTime(WebJSON json, User uBean,int hour){
		Date now = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		long rank = 0;
		try {
			d = format.parse(uBean.getRegistTime());
			rank =  (now.getTime() - d.getTime()) / 3600000;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(rank < hour) {
			Moment unLockTime = new Moment().fromTime(uBean.getRegistTime()).add(hour, "hours");
			json.set(2, "2-4019", hour, unLockTime.toSimpleTime());
			return false;
		}
		return true;
	}
	
	
	/**
	 * 验证注册时间
	 * @param json
	 * @return
	 * @throws ParseException 
	 */
	public boolean validateUnbindTime(WebJSON json,String unbidtime,int hour,int unbidnum){
		Date now = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = format.parse(unbidtime);
			long rank =  (now.getTime() - d.getTime()) / 3600000;
			if(rank < hour) {
				Moment unLockTime = new Moment().fromTime(unbidtime).add(hour, "hours");
				json.set(2, "2-4025", unbidnum,hour, unLockTime.toSimpleTime());
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			json.set(1, "1-1");
			return false;
		}
		return true;
	}
}