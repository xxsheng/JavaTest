package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.GameBetsService;
import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.biz.UserWithdrawLimitService;
import lottery.domains.content.dao.UserWithdrawLimitDao;
import lottery.domains.content.entity.UserWithdrawLimit;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.content.vo.user.UserWithdrawLimitVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserWithdrawLimitServiceImpl implements UserWithdrawLimitService {
	
	@Autowired
	private UserWithdrawLimitDao uWithdrawLimitDao;
	@Autowired
	private LotteryDataFactory dataFactory;
	
	@Autowired
	private GameBetsService gameBetsService;
	@Autowired
	private UserBetsService uBetsService;
	@Override
	public boolean add(int userId, double amount, String time, int type, int subType, double percent) {
		if (percent > 0) {
			UserWithdrawLimit entity = new UserWithdrawLimit();
			entity.setRechargeMoney(amount);
			entity.setRechargeTime(time);
			entity.setUserId(userId);
			entity.setProportion(percent);
			entity.setConsumptionRequirements(MathUtil.multiply(amount, percent));
			entity.setType(type);
			entity.setSubType(subType);
			return uWithdrawLimitDao.add(entity);
		}

		return true;
	}

	@Override
	public boolean delByUserId(int userId) {
		return uWithdrawLimitDao.delByUserId(userId);
	}

	@Override
	public UserWithdrawLimit getByUserId(int userId) {
		return uWithdrawLimitDao.getByUserId(userId);
	}

	@Override
	public Map<String, Object> getUserWithdrawLimits(int userId, String time) {
		UserVO user = dataFactory.getUser(userId);
		String username = user == null ? "未知" : user.getUsername();

		List<UserWithdrawLimit> records = uWithdrawLimitDao.getUserWithdrawLimits(userId, time);

		List<UserWithdrawLimit> entities = new ArrayList<>();
		for (UserWithdrawLimit record : records) {
			if (StringUtils.isNotEmpty(record.getRechargeTime()) && record.getRechargeMoney() > 0) {
				entities.add(record);
			}
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

			UserWithdrawLimitVO userWithdrawLimitVO = new UserWithdrawLimitVO(entity, username, totalBilling, remainConsumption);
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
				UserWithdrawLimitVO userWithdrawLimitVO = new UserWithdrawLimitVO(entity, username, 0, entity.getConsumptionRequirements());
				rechargeVOs.add(userWithdrawLimitVO);
			}
		}

		// 按照时间排序
		Collections.sort(rechargeVOs, new Comparator<UserWithdrawLimitVO>() {
			@Override
			public int compare(UserWithdrawLimitVO o1, UserWithdrawLimitVO o2) {
				return o1.getBean().getRechargeTime().compareTo(o2.getBean().getRechargeTime());
			}
		});

		double totalRemainConsumption = 0;
		for (UserWithdrawLimitVO userWithdrawLimitVO : rechargeVOs) {
			totalRemainConsumption = MathUtil.add(totalRemainConsumption, userWithdrawLimitVO.getRemainConsumption());
		}

		if (totalRemainConsumption < 0) {
			totalRemainConsumption = 0;
		}

		Map<String, Object> map =new HashMap<>();
		map.put("list", rechargeVOs);
		map.put("totalRemainConsumption", totalRemainConsumption);
		return map;
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

		double lotteryBilling = uBetsService.getBillingOrder(userId, sTime, eTime);
		if (lotteryBilling < 0) {
			lotteryBilling = 0;
		}

		remainConsumption = MathUtil.subtract(remainConsumption, lotteryBilling);
		if (remainConsumption < 0) {
			remainConsumption = 0;
		}

		double gameBilling = gameBetsService.getBillingOrder(userId, sTime, eTime);
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
}