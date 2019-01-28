package activity.domains.content.biz.impl;

import activity.domains.content.biz.ActivitySignService;
import activity.domains.content.dao.ActivitySignBillDao;
import activity.domains.content.dao.ActivitySignRecordDao;
import activity.domains.content.entity.ActivitySignBill;
import activity.domains.content.entity.ActivitySignRecord;
import activity.domains.content.vo.activity.ActivitySignVO;
import javautils.date.DateUtil;
import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.read.UserLotteryReportReadService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class ActivitySignServiceImpl implements ActivitySignService {
	@Autowired
	private UserDao uDao;

	@Autowired
	private ActivitySignRecordDao aSignRecordDao;

	@Autowired
	private ActivitySignBillDao aSignBillDao;

	@Autowired
	private UserBillService uBillService;

	@Autowired
	private UserLotteryReportReadService uLotteryReportReadService;

	@Autowired
	private DataFactory dataFactory;

	/**
	 * 获取签到情况
	 */
	@Override
	public ActivitySignVO getSignData(int userId) {
		// 活动未开启
		if (dataFactory.getActivitySignConfig().getStatus() != 0) {
			return null;
		}

		ActivitySignRecord record = aSignRecordDao.getByUserId(userId);

		// 还没有签到过
		if (record == null) {
			return new ActivitySignVO(0, false);
		}

		// 上次签到是否已经中断
		boolean isInterrupted = isInterrupted(record);
		if (isInterrupted) {
			return new ActivitySignVO(0, false);
		}

		boolean isTodaySigned = isTodaySigned(record);

		if (record.getDays() >= dataFactory.getActivitySignConfig().getDay() && isTodaySigned) {
			return new ActivitySignVO(dataFactory.getActivitySignConfig().getDay(), isTodaySigned);
		}

		int days = record.getDays() >= dataFactory.getActivitySignConfig().getDay() ? 0 : record.getDays();

		return new ActivitySignVO(days, isTodaySigned);
	}

	@Override
	public double sign(WebJSON json, int userId) {
		// 活动未开启
		if (dataFactory.getActivitySignConfig().getStatus() != 0) {
			json.set(2, "2-4001");
			return -1;
		}

		ActivitySignRecord record = aSignRecordDao.getByUserId(userId);

		if (record != null) {
			// 今天已经签到过了
			boolean isTodaySigned = isTodaySigned(record);
			if (isTodaySigned) {
				json.set(2, "2-4029");
				return -1;
			}
		}

		// 是否达到最低消费要求
		double todayCost = getTodayCost(userId);
		if (todayCost < dataFactory.getActivitySignConfig().getMinCost()) {
			int minCostInt = new BigDecimal(dataFactory.getActivitySignConfig().getMinCost()).intValue();
			json.set(2, "2-4009", minCostInt);
			return -1;
		}

		// 还没有签到过
		if (record == null) {
			firstSign(userId);
			return 0;
		}

		// 签到已经中断,重新开始签到
		boolean isInterrupted = isInterrupted(record);
		if (isInterrupted) {
			String time = new Moment().toSimpleTime();
			record.setStartTime(time);
			record.setLastSignTime(time);
			record.setDays(1);
			aSignRecordDao.update(record);
			return 0;
		}

		// 签到已经满了7天了，重新签
		if (record.getDays() >= dataFactory.getActivitySignConfig().getDay()) {
			String time = new Moment().toSimpleTime();
			record.setStartTime(time);
			record.setLastSignTime(time);
			record.setDays(1);
			aSignRecordDao.update(record);
			return 0;
		}

		record.setLastSignTime(new Moment().toSimpleTime());
		record.setDays(record.getDays()+1);
		aSignRecordDao.update(record);

		// 如果签到前是第7次签到，那么应该加相应的奖励
		if (record.getDays() == (dataFactory.getActivitySignConfig().getDay())) {
			double amount = collect(record);
			if (amount == 0) {
				return 0;
			}
			else {
				return amount;
			}
		}
		return 0;
	}

	private double collect(ActivitySignRecord record) {
		String startDate = new Moment().subtract(dataFactory.getActivitySignConfig().getDay()-1, "days").toSimpleDate();
		String endDate = new Moment().add(1, "days").toSimpleDate();
		double cost = getCost(record.getUserId(), startDate, endDate);
		if (cost > dataFactory.getActivitySignConfig().getMinCost()) {
			double amount = MathUtil.multiply(cost, dataFactory.getActivitySignConfig().getRewardPercent());
			if (amount > dataFactory.getActivitySignConfig().getMax()) {
				amount = dataFactory.getActivitySignConfig().getMax();
			}

			User user = new User();
			user.setId(record.getUserId());
			// 新增账单
			boolean addedBill = uBillService.addActivityBill(user, Global.BILL_ACCOUNT_LOTTERY, amount, Global.BILL_TYPE_ACTIVITY, "签到活动");
			if (addedBill) {

				// 新增签到账单
				ActivitySignBill bill = new ActivitySignBill();
				bill.setDays(record.getDays());
				bill.setStartTime(record.getStartTime());
				bill.setEndTime(new Moment().toSimpleTime());
				bill.setMoney(amount);
				bill.setScale(dataFactory.getActivitySignConfig().getRewardPercent());
				bill.setTime(new Moment().toSimpleTime());
				bill.setTotalCost(cost);
				bill.setUserId(record.getUserId());
				aSignBillDao.add(bill);

				// 加钱
				uDao.updateLotteryMoney(record.getUserId(), amount);

				aSignRecordDao.updateLastCollectTime(record.getUserId(), new Moment().toSimpleTime());
			}

			return amount;
		}

		return 0;
	}

	private int firstSign(int userId) {
		int days = 1;
		ActivitySignRecord record = new ActivitySignRecord();
		record.setUserId(userId);
		record.setDays(days);
		String time = new Moment().toSimpleTime();
		record.setLastSignTime(time);
		record.setStartTime(time);
		aSignRecordDao.add(record);
		return days;
	}

	/**
	 * 签到过程是否中断
	 */
	private boolean isInterrupted(ActivitySignRecord record) {
		// 上次签到是否已经中断，上次签到时间距离现在超过2天，即已经中断
		Date lastSignTime = new Moment().fromTime(record.getLastSignTime()).toDate();
		Date now = new Date();
		int days = DateUtil.calcDays(now, lastSignTime);
		if (days >= 2) {
			return true;
		}
		return false;
	}

	/**
	 * 今天是否已经签到
	 */
	private boolean isTodaySigned(ActivitySignRecord record) {
		// 今天是否已经签到
		if (record == null) return false;

		String lastSignDate = new Moment().fromTime(record.getLastSignTime()).toSimpleDate();
		String today = new Moment().toSimpleDate();
		if (today.equals(lastSignDate)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取今天的消费总计
	 */
	private double getTodayCost(int userId) {
		String sDate = new Moment().toSimpleDate();
		String eDate = new Moment().add(1, "days").toSimpleDate();

		return getCost(userId, sDate, eDate);
	}

	/**
	 * 获取签到期间的消费总计
	 */
	private double getCost(int userId, String sDate, String eDate) {
		List<?> userCosts = uLotteryReportReadService.listAmountGroupByUserIds(new Integer[]{userId}, sDate, eDate);
		if (CollectionUtils.isNotEmpty(userCosts)) {
			Object[] userCostArr = (Object[]) userCosts.get(0);
			double cost = (Double) userCostArr[1];
			return cost;
		}

		return 0;
	}
}