package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityRewardService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.dao.ActivityRebateDao;
import lottery.domains.content.dao.ActivityRewardBillDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserLotteryReportDao;
import lottery.domains.content.entity.ActivityRebate;
import lottery.domains.content.entity.ActivityRewardBill;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.entity.activity.RebateRulesReward;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.activity.ActivityRewardBillVO;
import lottery.domains.pool.LotteryDataFactory;
import net.sf.json.JSONArray;

@Service
public class ActivityRewardServiceImpl implements ActivityRewardService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private ActivityRebateDao aRebateDao;

	@Autowired
	private ActivityRewardBillDao aRewardBillDao;
	
	@Autowired
	private UserLotteryReportDao uLotteryReportDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;
	
	@Autowired
	private UserSysMessageService uSysMessageService;
	
	/**
	 * LotteryDataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Override
	public PageList search(String username, String date, Integer type, Integer status, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if(user != null) {
				criterions.add(Restrictions.eq("toUser", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(date)) {
			criterions.add(Restrictions.eq("date", date));
		}
		if(type != null) {
			criterions.add(Restrictions.eq("type", type.intValue()));
		}
 		if(status != null) {
			criterions.add(Restrictions.eq("status", status.intValue()));
		}
 		orders.add(Order.desc("id"));
		if(isSearch) {
			List<ActivityRewardBillVO> list = new ArrayList<>();
			PageList pList = aRewardBillDao.find(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new ActivityRewardBillVO((ActivityRewardBill) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	
	@Override
	public boolean add(int toUser, int fromUser, int type, double totalMoney, double money, String date) {
		String time = new Moment().toSimpleTime();
		int status = 0;
		ActivityRewardBill entity = new ActivityRewardBill(toUser, fromUser, type, totalMoney, money, date, time, status);
		return aRewardBillDao.add(entity);
	}
	
	@Override
	public List<ActivityRewardBillVO> getLatest(int toUser, int status, int count) {
		List<ActivityRewardBillVO> formatList = new ArrayList<>();
		List<ActivityRewardBill> list = aRewardBillDao.getLatest(toUser, status, count);
		for (ActivityRewardBill tmpBean : list) {
			formatList.add(new ActivityRewardBillVO(tmpBean, lotteryDataFactory));
		}
		return formatList;
	}

	@Override
	public boolean calculate(int type, String date) {
		ActivityRebate activity = aRebateDao.getByType(type);
		if(activity.getStatus() == 0) {
			@SuppressWarnings("unchecked")
			List<RebateRulesReward> rewards = (List<RebateRulesReward>) JSONArray.toCollection(JSONArray.fromObject(activity.getRules()), RebateRulesReward.class);
			String sTime = new Moment().fromDate(date).toSimpleDate();
			String eTime = new Moment().fromDate(date).add(1, "days").toSimpleDate();
			List<UserLotteryReport> list = doReport(sTime, eTime);
			for (UserLotteryReport tmpBean : list) {
				if (tmpBean.getUserId() == 72) {
					continue;
				}

				if(type == Global.ACTIVITY_REBATE_REWARD_XIAOFEI) {
					double totalMoney = tmpBean.getBillingOrder();
					if(totalMoney > 0) {
						RebateRulesReward rBean = doMatch(rewards, totalMoney);
						int bType = 1;
						addHandelBill(tmpBean.getUserId(), rBean, bType, totalMoney, date);
					}
				}
				if(type == Global.ACTIVITY_REBATE_REWARD_YINGKUI) {
					double totalMoney = tmpBean.getBillingOrder() - tmpBean.getPrize() - tmpBean.getSpendReturn();
					if(totalMoney > 0) {
						RebateRulesReward rBean = doMatch(rewards, totalMoney);
						int bType = 2;
						addHandelBill(tmpBean.getUserId(), rBean, bType, totalMoney, date);
					}
				}
			}
			return true;
		}
		return false;
	}
	
	List<UserLotteryReport> doReport(String sTime, String eTime) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		if(StringUtil.isNotNull(sTime)) {
			criterions.add(Restrictions.ge("time", sTime));
		}
		if(StringUtil.isNotNull(eTime)) {
			criterions.add(Restrictions.lt("time", eTime));
		}
		return uLotteryReportDao.find(criterions, orders);
	}
	
	RebateRulesReward doMatch(List<RebateRulesReward> rewards, double totalMoney) {
		RebateRulesReward reward = new RebateRulesReward();
		for (RebateRulesReward tmpBean : rewards) {
			double money = tmpBean.getMoney();
			if(totalMoney >= money && money > reward.getMoney()) {
				reward = tmpBean;
			}
		}
		return reward;
	}
	
	boolean addHandelBill(int fromUser, RebateRulesReward rBean, int type, double totalMoney, String date) {
		if(rBean.getMoney() > 0) {
			User thisUser = uDao.getById(fromUser);
			if(thisUser == null) return false;
			
			if(rBean.getRewardUp1() == 0) return false;
			if(thisUser.getUpid() == 0 || thisUser.getUpid() == 72) return false;
			User up1 = uDao.getById(thisUser.getUpid());
			if(up1 == null) return false;
			try {
				int toUesr = up1.getId();
				double money = rBean.getRewardUp1();
				boolean hasRecord = aRewardBillDao.hasRecord(toUesr, fromUser, type, date);
				if(!hasRecord) {
					add(toUesr, fromUser, type, totalMoney, money, date);
				}
			} catch (Exception e) {}
			if(rBean.getRewardUp2() == 0) return false;
			if(up1.getUpid() == 0) return false;
			User up2 = uDao.getById(up1.getUpid());
			if(up2 == null) return false;
			try {
				int toUesr = up2.getId();
				double money = rBean.getRewardUp2();
				boolean hasRecord = aRewardBillDao.hasRecord(toUesr, fromUser, type, date);
				if(!hasRecord) {
					add(toUesr, fromUser, type, totalMoney, money, date);
				}
			} catch (Exception e) {}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean agreeAll(String date) {
		List<ActivityRewardBill> list = aRewardBillDao.getUntreated(date);
		Set<Integer> uSet = new HashSet<>();
		for (ActivityRewardBill tmpBean : list) {
			try {
				if(tmpBean.getStatus() == 0) {
					String thisTime = new Moment().toSimpleTime();
					tmpBean.setStatus(1);
					tmpBean.setTime(thisTime);
					boolean aFlag = aRewardBillDao.update(tmpBean);
					if(aFlag) {
						User uBean = uDao.getById(tmpBean.getToUser());
						boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), tmpBean.getMoney());
						if(uFlag) {
							uSet.add(uBean.getId());
							String remarks = "彩票账户，";
							if(tmpBean.getType() == 1) {
								remarks += "发放消费佣金。";
							}
							if(tmpBean.getType() == 2) {
								remarks += "发放盈亏佣金。";
							}
							int refType = 1; // 其他活动类型
							uBillService.addActivityBill(uBean, Global.BILL_ACCOUNT_LOTTERY, tmpBean.getMoney(), refType, remarks);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String formatDate = new Moment().fromDate(date).format("yyyy年MM月dd日");
		for (Integer id : uSet) {
			uSysMessageService.addRewardMessage(id, formatDate);
		}
		return true;
	}

}