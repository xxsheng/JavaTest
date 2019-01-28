package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityFirstRechargeBillService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserLotteryReportService;
import lottery.domains.content.biz.UserRechargeService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.biz.UserWithdrawLimitService;
import lottery.domains.content.dao.PaymentChannelDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserRechargeDao;
import lottery.domains.content.entity.HistoryUserRecharge;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserRecharge;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.payment.PaymentChannelVO;
import lottery.domains.content.vo.user.HistoryUserRechargeVO;
import lottery.domains.content.vo.user.UserRechargeVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class UserRechargeServiceImpl implements UserRechargeService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserRechargeDao uRechargeDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;
	
	@Autowired
	private UserSysMessageService uSysMessageService;
	
	@Autowired
	private UserWithdrawLimitService uWithdrawLimitService;

	@Autowired
	private UserLotteryReportService uLotteryReportService;
	@Autowired
	private  ActivityFirstRechargeBillService activityFirstRechargeBillService;
	
	/**
	 * LotteryDataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Autowired
	private PaymentChannelDao paymentChannelDao;
	
	@Autowired 
	private UserDao userDao;
	/**
	 * 生成流水号
	 */
	private String billno() {
		return new Moment().format("yyMMddHHmmss") + RandomStringUtils.random(8, true, true);
	}
	
	@Override
	public UserRechargeVO getById(int id) {
		UserRecharge bean = uRechargeDao.getById(id);
		if(bean != null) {
			return new UserRechargeVO(bean, lotteryDataFactory);
		}
		return null;
	}
	
	@Override
	public HistoryUserRechargeVO getHistoryById(int id) {
		HistoryUserRecharge bean = uRechargeDao.getHistoryById(id);
		if(bean != null) {
			return new HistoryUserRechargeVO(bean, lotteryDataFactory);
		}
		return null;
	}
	
	@Override
	public List<UserRechargeVO> getLatest(int userId, int status, int count) {
		List<UserRechargeVO> formatList = new ArrayList<>();
		List<UserRecharge> list = uRechargeDao.getLatest(userId, status, count);
		for (UserRecharge tmpBean : list) {
			formatList.add(new UserRechargeVO(tmpBean, lotteryDataFactory));
		}
		return formatList;
	}

	@Override
	public List<UserRecharge> listByPayTimeAndStatus(String sDate, String eDate, int status) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("time"));
		orders.add(Order.desc("id"));

		criterions.add(Restrictions.eq("status", status));
		criterions.add(Restrictions.ge("payTime", sDate));
		criterions.add(Restrictions.lt("payTime", eDate));

		List<UserRecharge> rechargeList = uRechargeDao.list(criterions, orders);
		return rechargeList;
	}

	@Override
	public PageList search(Integer type,String billno, String username, String minTime,
			String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId,
			int start, int limit) {
	
		StringBuilder queryStr = new StringBuilder();
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if(user != null) {
				queryStr.append(" and b.user_id  = ").append(user.getId());
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(billno)) {
			queryStr.append(" and b.billno  = ").append("'"+billno+"'" );
		}
		if(StringUtil.isNotNull(minTime)) {
			queryStr.append(" and b.time  > ").append("'"+minTime+"'" );
		
		}
		if(StringUtil.isNotNull(maxTime)) {
			queryStr.append(" and b.time  < ").append("'"+maxTime+"'" );
		}
		if(StringUtil.isNotNull(minPayTime)) {
			queryStr.append(" and b.pay_time  > ").append("'"+minPayTime+"'" );
		}
		if(StringUtil.isNotNull(maxPayTime)) {
			queryStr.append(" and b.pay_time  < ").append("'"+maxPayTime+"'" );
		}
		if(minMoney != null) {
			queryStr.append(" and b.money  >= ").append( minMoney.doubleValue() );
		}
		if(maxMoney != null) {
			queryStr.append(" and b.money  <= ").append( maxMoney.doubleValue() );
		}
		if(status != null) {
			queryStr.append(" and b.status  = ").append( status.intValue() );
		}
		if(channelId != null) {
			queryStr.append(" and b.channel_id  = ").append( channelId );
		}
		if(type != null){
			queryStr.append(" and  u.type  = ").append( type );
		}else{
			queryStr.append(" and u.upid  != ").append( 0 );
		}
		queryStr.append("  ORDER BY b.time,b.id DESC ");
		if(isSearch) {
			List<UserRechargeVO> list = new ArrayList<>();
			PageList pList = uRechargeDao.find(queryStr.toString(), start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new UserRechargeVO((UserRecharge) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	/**
	 * 查询历史充值记录
	 */
	@Override
	public PageList searchHistory(String billno, String username, String minTime,
			String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId,
			int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("time"));
		orders.add(Order.desc("id"));
		boolean isSearch = true;
		if(StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if(user != null) {
				criterions.add(Restrictions.eq("userId", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if(StringUtil.isNotNull(billno)) {
			criterions.add(Restrictions.eq("billno", billno));
		}
		if(StringUtil.isNotNull(minTime)) {
			criterions.add(Restrictions.gt("time", minTime));
		}
		if(StringUtil.isNotNull(maxTime)) {
			criterions.add(Restrictions.lt("time", maxTime));
		}
		if(StringUtil.isNotNull(minPayTime)) {
			criterions.add(Restrictions.gt("payTime", minPayTime));
		}
		if(StringUtil.isNotNull(maxPayTime)) {
			criterions.add(Restrictions.lt("payTime", maxPayTime));
		}
		if(minMoney != null) {
			criterions.add(Restrictions.ge("money", minMoney.doubleValue()));
		}
		if(maxMoney != null) {
			criterions.add(Restrictions.le("money", maxMoney.doubleValue()));
		}
		if(status != null) {
			criterions.add(Restrictions.eq("status", status.intValue()));
		}
		if(channelId != null) {
			criterions.add(Restrictions.eq("channelId", channelId));
		}
		if(isSearch) {
			List<HistoryUserRechargeVO> list = new ArrayList<>();
			PageList pList = uRechargeDao.findHistory(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new HistoryUserRechargeVO((HistoryUserRecharge) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}
	
	@Override
	public boolean addSystemRecharge(String username, int subtype, int account, double amount, int isLimit, String remarks) {
		User uBean = uDao.getByUsername(username);
		if(uBean != null) {
			// 充值未到账
			if(subtype == Global.PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_NOTARRIVAL) {
				if(amount > 0) {
					boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), amount);
					if(uFlag) {
						String billno = billno();
						double money = amount;
						//2016-09-20 现在改为直接充值到彩票账户
						//double beforeMoney = uBean.getTotalMoney();
						double beforeMoney = uBean.getLotteryMoney();
						double afterMoney = beforeMoney + money;
						double recMoney = money;
						double feeMoney = 0;
						Moment moment = new Moment();
						String time = moment.toSimpleTime();
						int status = 1;
						int type = Global.PAYMENT_CHANNEL_TYPE_SYSTEM;
						String infos = "系统补充值未到账。";
						if(StringUtil.isNotNull(remarks)) {
							infos += "备注：" + remarks;
						}
						UserRecharge cBean = new UserRecharge(billno, uBean.getId(), money, beforeMoney, afterMoney, recMoney, feeMoney, time, status, type, subtype);
						cBean.setChannelId(-1);
						cBean.setPayTime(time);
						cBean.setInfos(infos);
						cBean.setRemarks(remarks);
						boolean cFlag = uRechargeDao.add(cBean);
						if(cFlag) {
							uBillService.addRechargeBill(cBean, uBean, infos);
							//取现限制
							double percent = lotteryDataFactory.getWithdrawConfig().getSystemConsumptionPercent();
							uWithdrawLimitService.add(uBean.getId(), amount,time, type, subtype, percent);
							uSysMessageService.addSysRecharge(uBean.getId(), amount, remarks);
						}
						return cFlag;
					}
				}
			}
			// 活动补贴
			if(subtype == Global.PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_ACTIVITY) {
				if(amount > 0) {
					boolean uFlag = false;
					if(account == Global.BILL_ACCOUNT_LOTTERY) {
						uFlag = uDao.updateLotteryMoney(uBean.getId(), amount);
					}
					if(uFlag) {
						String infos = "系统活动补贴。";
						if(StringUtil.isNotNull(remarks)) {
							infos += "备注：" + remarks;
						}
						int refType = 0; // 系统活动补贴
						uBillService.addActivityBill(uBean, account, amount, refType, infos);
						if(isLimit == 1) {
							String time = new Moment().toSimpleTime();

							int type = Global.PAYMENT_CHANNEL_TYPE_SYSTEM;
							double percent = lotteryDataFactory.getWithdrawConfig().getSystemConsumptionPercent();
							uWithdrawLimitService.add(uBean.getId(), amount,time, type, subtype, percent);
						}
					}
					return uFlag;
				}
			}
			// 增加资金
			if(subtype == Global.PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_ADD) {
				if(amount > 0) {
					boolean uFlag = false;
					if(account == Global.BILL_ACCOUNT_MAIN) {
						uFlag = uDao.updateTotalMoney(uBean.getId(), amount);
					}
					if(account == Global.BILL_ACCOUNT_LOTTERY) {
						uFlag = uDao.updateLotteryMoney(uBean.getId(), amount);
					}
					if(account == Global.BILL_ACCOUNT_BACCARAT) {
						uFlag = uDao.updateBaccaratMoney(uBean.getId(), amount);
					}
					if(uFlag) {
						String infos = "管理员增加资金。";
						if(StringUtil.isNotNull(remarks)) {
							infos += "备注：" + remarks;
						}
						uBillService.addAdminAddBill(uBean, account, amount, infos);
					}
					return uFlag;
				}
			}
			// 减少资金
			if(subtype == Global.PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_MINUS) {
				if(amount > 0) {
					boolean uFlag = false;
					if(account == Global.BILL_ACCOUNT_MAIN) {
						uFlag = uDao.updateTotalMoney(uBean.getId(), -amount);
					}
					if(account == Global.BILL_ACCOUNT_LOTTERY) {
						uFlag = uDao.updateLotteryMoney(uBean.getId(), -amount);
					}
					if(account == Global.BILL_ACCOUNT_BACCARAT) {
						uFlag = uDao.updateBaccaratMoney(uBean.getId(), -amount);
					}
					if(uFlag) {
						String infos = "管理员减少资金。";
						if(StringUtil.isNotNull(remarks)) {
							infos += "备注：" + remarks;
						}
						uBillService.addAdminMinusBill(uBean, account, amount, infos);
					}
					return uFlag;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean patchOrder(String billno, String payBillno, String remarks) {
		UserRecharge cBean = uRechargeDao.getByBillno(billno);
		// 该订单必须为未支付
		if(cBean != null && cBean.getStatus() == 0) {
			User uBean = uDao.getById(cBean.getUserId());
			if(uBean != null) {
				Moment moment = new Moment();
				String payTime = moment.toSimpleTime();

				double money = cBean.getRecMoney();
				String infos = "充值漏单补单，" + money;

				PaymentChannelVO channel = lotteryDataFactory.getPaymentChannelVO(cBean.getChannelId());

				if (channel == null) {
					return false;
				}

				if (channel.getAddMoneyType() == Global.ADD_MONEY_TYPE_MANUAL) {
					infos = "在线充值";
				}

				cBean.setBeforeMoney(uBean.getLotteryMoney());
				cBean.setAfterMoney(uBean.getLotteryMoney() + money);
				cBean.setStatus(1);
				cBean.setPayBillno(payBillno);
				cBean.setPayTime(payTime);
				cBean.setInfos(infos);
				cBean.setRemarks(remarks);
				boolean result = uRechargeDao.update(cBean);
				if(result) {
					boolean flag = uDao.updateLotteryMoney(uBean.getId(), money);
					if(flag) {
						String _remarks = "在线充值";
						uBillService.addRechargeBill(cBean, uBean, _remarks);
						uWithdrawLimitService.add(uBean.getId(), money, payTime, channel.getType(), channel.getSubType(), channel.getConsumptionPercent());
						uSysMessageService.addOnlineRecharge(uBean.getId(), money);
						if(uBean.getUpid() != 0){
							activityFirstRechargeBillService.tryCollect(cBean.getUserId(), money, cBean.getApplyIp());
						}
				
						if (channel.getAddMoneyType() == Global.ADD_MONEY_TYPE_MANUAL) {
							// 手动上分账号都需要增加额度
							paymentChannelDao.addUsedCredits(channel.getId(), money);
						}
					}
				}
				return result;
			}
		}
		return false;
	}
	
	@Override
	public boolean cancelOrder(String billno) {
		UserRecharge bean = uRechargeDao.getByBillno(billno);
		if(bean != null && bean.getStatus() == 0) {
			bean.setStatus(-1);
			return uRechargeDao.update(bean);
		}
		return false;
	}

	@Override
	public double getTotalRecharge(Integer type,String billno, String username, String minTime, String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId) {
		Integer userId = null;
		if (StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				userId = user.getId();
			}
		}

		return uRechargeDao.getTotalRecharge(billno, userId, minTime, maxTime, minPayTime, maxPayTime, minMoney, maxMoney, status, channelId);
	}
	
	@Override
	public double getHistoryTotalRecharge(String billno, String username, String minTime, String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId) {
		Integer userId = null;
		if (StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				userId = user.getId();
			}
		}

		return uRechargeDao.getHistoryTotalRecharge(billno, userId, minTime, maxTime, minPayTime, maxPayTime, minMoney, maxMoney, status, channelId);
	}
}