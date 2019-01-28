package lottery.domains.content.biz.impl;

import javautils.math.MathUtil;
import lottery.domains.content.biz.UserWithdrawLimitService;
import lottery.domains.content.dao.UserWithdrawLimitDao;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.UserWithdrawLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserWithdrawLimitServiceImpl implements UserWithdrawLimitService {
	@Autowired
	private UserWithdrawLimitDao uWithdrawLimitDao;

	@Override
	public boolean add(int userId, double amount,String time, PaymentChannel channel) {
		if (channel.getConsumptionPercent() > 0) {

			UserWithdrawLimit entity = new UserWithdrawLimit();
			entity.setRechargeMoney(amount);
			entity.setRechargeTime(time);
			entity.setUserId(userId);
			entity.setProportion(channel.getConsumptionPercent());
			entity.setConsumptionRequirements(MathUtil.multiply(amount, channel.getConsumptionPercent()));
			entity.setType(channel.getType());
			entity.setSubType(channel.getSubType());
			return uWithdrawLimitDao.add(entity);
		}
		return true;
	}

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
	@Transactional(readOnly = true)
	public UserWithdrawLimit getByUserId(int userId) {
		return uWithdrawLimitDao.getByUserId(userId);
	}

	@Override
	public List<UserWithdrawLimit> getUserWithdrawLimits(int userId) {
		return uWithdrawLimitDao.getUserWithdrawLimits(userId);
	}
}