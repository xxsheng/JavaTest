package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.domains.content.entity.AdminUser;
import admin.web.WebJSONObject;
import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.PaymentChannelBankService;
import lottery.domains.content.biz.PaymentChannelService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserCardService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.biz.UserWithdrawService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserWithdrawDao;
import lottery.domains.content.dao.UserWithdrawLimitDao;
import lottery.domains.content.dao.UserWithdrawLogDao;
import lottery.domains.content.entity.HistoryUserWithdraw;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.entity.UserWithdrawLog;
import lottery.domains.content.global.Global;
import lottery.domains.content.payment.RX.RXPayment;
import lottery.domains.content.payment.af.AFPayment;
import lottery.domains.content.payment.cf.CFPayment;
import lottery.domains.content.payment.fkt.FKTPayment;
import lottery.domains.content.payment.ht.HTPayment;
import lottery.domains.content.payment.htf.HTFPayment;
import lottery.domains.content.payment.tgf.TGFPayment;
import lottery.domains.content.payment.yr.YRPayment;
import lottery.domains.content.payment.zs.ZSPayment;
import lottery.domains.content.vo.user.HistoryUserWithdrawVO;
import lottery.domains.content.vo.user.UserWithdrawVO;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class UserWithdrawServiceImpl implements UserWithdrawService {
	public static final int[] PROCESSING_STATUSES = new int[] { Global.USER_WITHDRAW_REMITSTATUS_SYNC_STATUS,
			Global.USER_WITHDRAW_REMITSTATUS_BANK_PROCESSING, Global.USER_WITHDRAW_REMITSTATUS_THIRD_UNPROCESS };

	private static final Logger log = LoggerFactory.getLogger(UserWithdrawServiceImpl.class);

	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;

	@Autowired
	private UserWithdrawDao uWithdrawDao;

	@Autowired
	private UserWithdrawLimitDao uWithdrawLimitDao;

	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;

	@Autowired
	private UserSysMessageService uSysMessageService;

	@Autowired
	private ZSPayment zsPayment;

	@Autowired
	private RXPayment rxPayment;

	@Autowired
	private HTPayment htPayment;

	@Autowired
	private CFPayment cfPayment;

	@Autowired
	private FKTPayment fktPayment;

	@Autowired
	private HTFPayment htfPayment;
	
	@Autowired
	private YRPayment yrPayment;
	
	@Autowired
	private AFPayment afPayment;
	
	@Autowired
	private TGFPayment tgfPayment;

	@Autowired
	private PaymentChannelService paymentChannelService;

	@Autowired
	private PaymentChannelBankService paymentChannelBankService;

	@Autowired
	private UserCardService uCardService;

	@Autowired
	private LotteryDataFactory dataFactory;

	@Autowired
	private UserWithdrawLogDao userWithdrawLogDao;
	/**
	 * LotteryDataFactory
	 */
	@Autowired
	private LotteryDataFactory lotteryDataFactory;

	@Override
	public UserWithdrawVO getById(int id) {
		UserWithdraw bean = uWithdrawDao.getById(id);
		if (bean != null) {
			return new UserWithdrawVO(bean, lotteryDataFactory);
		}
		return null;
	}

	@Override
	public HistoryUserWithdrawVO getHistoryById(int id) {
		HistoryUserWithdraw bean = uWithdrawDao.getHistoryById(id);
		if (bean != null) {
			return new HistoryUserWithdrawVO(bean, lotteryDataFactory);
		}
		return null;
	}

	@Override
	public List<UserWithdrawVO> getLatest(int userId, int status, int count) {
		List<UserWithdrawVO> formatList = new ArrayList<>();
		List<UserWithdraw> list = uWithdrawDao.getLatest(userId, status, count);
		for (UserWithdraw tmpBean : list) {
			formatList.add(new UserWithdrawVO(tmpBean, lotteryDataFactory));
		}
		return formatList;
	}

	@Override
	public List<UserWithdraw> listByRemitStatus(int[] remitStatuses, boolean third, String sTime, String eTime) {
		return uWithdrawDao.listByRemitStatus(remitStatuses, third, sTime, eTime);
	}

	@Override
	public PageList search(Integer type,String billno, String username, String minTime, String maxTime, String minOperatorTime,
			String maxOperatorTime, Double minMoney, Double maxMoney, String keyword, Integer status,
			Integer checkStatus, Integer remitStatus, Integer paymentChannelId, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		StringBuilder queryStr = new StringBuilder();
		boolean isSearch = true;
		if (StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				queryStr.append(" and b.user_id  = ").append(user.getId());
			} else {
				isSearch = false;
			}
		}
		if (StringUtil.isNotNull(billno)) {
			queryStr.append(" and b.billno  = ").append("'"+billno+"'" );
		}
		if (StringUtil.isNotNull(minTime)) {
			queryStr.append(" and b.time  > ").append("'"+minTime+"'" );
		}
		if (StringUtil.isNotNull(maxTime)) {
			queryStr.append(" and b.time  < ").append("'"+maxTime+"'" );
		}
		if (StringUtil.isNotNull(minOperatorTime)) {
			queryStr.append(" and b.operator_time  > ").append("'"+minOperatorTime+"'" );
		}
		if (StringUtil.isNotNull(maxOperatorTime)) {
			queryStr.append(" and b.operator_time  < ").append("'"+maxOperatorTime+"'" );
		}
		if (minMoney != null) {
			queryStr.append(" and b.money  >= ").append( minMoney.doubleValue() );
		}
		if (maxMoney != null) {
			queryStr.append(" and b.money  <= ").append( maxMoney.doubleValue() );
		}
		if (StringUtil.isNotNull(keyword)) {
			queryStr.append("and (b.card_name like %"+keyword+"% or b.card_id like  %"+keyword+"% )");
		}
		
		if (status != null) {
			queryStr.append(" and b.status  = ").append(status.intValue() );
		}
		if (checkStatus != null) {
			queryStr.append(" and b.check_status  = ").append(checkStatus.intValue() );
		}
		if (remitStatus != null) {
			queryStr.append(" and b.remit_status  = ").append(remitStatus.intValue() );
		}
		if (paymentChannelId != null) {
			queryStr.append(" and b.payment_channel_id  = ").append(paymentChannelId );
		}
		if(type != null){
			queryStr.append(" and  u.type  = ").append( type );
		}else{
			queryStr.append(" and u.upid  != ").append( 0 );
		}
		queryStr.append("  ORDER BY b.time,b.id DESC ");
		
		if (isSearch) {
			List<UserWithdrawVO> list = new ArrayList<>();
			PageList pList = uWithdrawDao.find(queryStr.toString(), start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new UserWithdrawVO((UserWithdraw) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public PageList searchHistory(String billno, String username, String minTime, String maxTime,
			String minOperatorTime, String maxOperatorTime, Double minMoney, Double maxMoney, String keyword,
			Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		boolean isSearch = true;
		if (StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				criterions.add(Restrictions.eq("userId", user.getId()));
			} else {
				isSearch = false;
			}
		}
		if (StringUtil.isNotNull(billno)) {
			criterions.add(Restrictions.eq("billno", billno));
		}
		if (StringUtil.isNotNull(minTime)) {
			criterions.add(Restrictions.gt("time", minTime));
		}
		if (StringUtil.isNotNull(maxTime)) {
			criterions.add(Restrictions.lt("time", maxTime));
		}
		if (StringUtil.isNotNull(minOperatorTime)) {
			criterions.add(Restrictions.gt("operatorTime", minOperatorTime));
		}
		if (StringUtil.isNotNull(maxOperatorTime)) {
			criterions.add(Restrictions.lt("operatorTime", maxOperatorTime));
		}
		if (minMoney != null) {
			criterions.add(Restrictions.ge("money", minMoney.doubleValue()));
		}
		if (maxMoney != null) {
			criterions.add(Restrictions.le("money", maxMoney.doubleValue()));
		}
		if (StringUtil.isNotNull(keyword)) {
			criterions.add(Restrictions.or(Restrictions.like("cardName", keyword, MatchMode.ANYWHERE),
					Restrictions.like("cardId", keyword, MatchMode.ANYWHERE)));
		}
		if (status != null) {
			criterions.add(Restrictions.eq("status", status.intValue()));
		}
		if (checkStatus != null) {
			criterions.add(Restrictions.eq("checkStatus", checkStatus.intValue()));
		}
		if (remitStatus != null) {
			criterions.add(Restrictions.eq("remitStatus", remitStatus.intValue()));
		}
		if (paymentChannelId != null) {
			criterions.add(Restrictions.eq("paymentChannelId", paymentChannelId));
		}
		orders.add(Order.desc("time"));
		orders.add(Order.desc("id"));
		if (isSearch) {
			List<HistoryUserWithdrawVO> list = new ArrayList<>();
			PageList pList = uWithdrawDao.findHistory(criterions, orders, start, limit);
			for (Object tmpBean : pList.getList()) {
				list.add(new HistoryUserWithdrawVO((HistoryUserWithdraw) tmpBean, lotteryDataFactory));
			}
			pList.setList(list);
			return pList;
		}
		return null;
	}

	@Override
	public boolean manualPay(AdminUser uEntity, WebJSONObject json, int id, String payBillno, String remarks,
			String operatorUser) {
		UserWithdraw entity = uWithdrawDao.getById(id);
		if (entity != null && entity.getStatus() == 0) {
			// 必须已经锁定，并且操作人必须相同
			if (entity.getLockStatus() == 1) {
				if (operatorUser.equals(entity.getOperatorUser())) {
					String operatorTime = new Moment().toSimpleTime();
					String infos = "您的提现已提交至银行处理,请耐心等候！";
					// entity.setStatus(3); // 0：未处理；1：已完成；-1：拒绝支付；
					entity.setInfos(infos);
					entity.setPayBillno(payBillno);
					entity.setOperatorUser(operatorUser);
					entity.setOperatorTime(operatorTime);
					entity.setRemarks("手动出款");
					entity.setRemitStatus(1); // 0：未处理；1：银行处理中；2：打款完成；3：第三方待处理；-1：请求失败；-2：打款失败；-3查询状态中；-4：未知状态；-5：第三方处理失败；-6：银行处理失败；-7：第三方拒绝支付
					entity.setPayType(Global.WITHDRAW_PAY_TYPE_MANUAL);
					entity.setPaymentChannelId(-1);
					boolean result = uWithdrawDao.update(entity);
					if (result) {
						String time = new Moment().toSimpleTime();
						String action = String.format("手动出款；操作人：%s", uEntity.getUsername());
						userWithdrawLogDao.add(new UserWithdrawLog(entity.getBillno(), entity.getUserId(),
								uEntity.getId(), action, time));
					}
					return result;
				} else {
					json.set(2, "2-2021");
				}
			} else {
				json.set(2, "2-2020");
			}
		} else {
			json.set(2, "2-2019");
		}
		return false;
	}

	@Override
	public boolean completeRemit(AdminUser uEntity, WebJSONObject json, int id, String operatorUser) {
		UserWithdraw entity = uWithdrawDao.getById(id);
		if (entity != null && entity.getStatus() == 0) {
			// 必须已经锁定，并且操作人必须相同
			if (entity.getLockStatus() == 1) {
				if (operatorUser.equals(entity.getOperatorUser())) {
					String operatorTime = new Moment().toSimpleTime();
					String infos = "您的提现已处理，请您注意查收！";
					entity.setStatus(1); // 0：未处理；1：已完成；-1：拒绝支付；
					entity.setInfos(infos);
					entity.setOperatorUser(operatorUser);
					entity.setOperatorTime(operatorTime);
					entity.setLockStatus(0); // 0：未锁定；1：已锁定；
					entity.setRemitStatus(2); // 0：未处理；1：银行处理中；2：打款完成；3：第三方待处理；-1：请求失败；-2：打款失败；-3查询状态中；-4：未知状态；-5：第三方处理失败；-6：银行处理失败；-7：第三方拒绝支付
					boolean result = uWithdrawDao.update(entity);
					if (result) {
						// 更新报表
						uBillService.addWithdrawReport(entity);
						uSysMessageService.addConfirmWithdraw(entity.getUserId(), entity.getMoney(),
								entity.getRecMoney());
						String time = new Moment().toSimpleTime();
						String action = String.format("<span style=\"color: #35AA47;\">打款完成</span>；操作人：%s",
								uEntity.getUsername());
						userWithdrawLogDao.add(new UserWithdrawLog(entity.getBillno(), entity.getUserId(),
								uEntity.getId(), action, time));
					}
					return result;
				} else {
					json.set(2, "2-2021");
				}
			} else {
				json.set(2, "2-2019");
			}
		}
		return false;
	}

	@Override
	public boolean apiPay(AdminUser uEntity, WebJSONObject json, int id, PaymentChannel channel) {
		UserWithdraw entity = uWithdrawDao.getById(id);
		if (entity == null || entity.getStatus() != 0) {
			json.set(2, "2-2019");
			return false;
		}

		// 必须已经锁定
		if (entity.getLockStatus() != 1) {
			json.set(2, "2-2020");
			return false;
		}

		// 操作人必须相同
		if (!uEntity.getUsername().equals(entity.getOperatorUser())) {
			json.set(2, "2-2021");
			return false;
		}

		UserCard card = uCardService.getByUserAndCardId(entity.getUserId(), entity.getCardId().trim());
		if (card == null) {
			json.set(2, "2-4011");
			return false;
		}

		PaymentChannelBank bank = paymentChannelBankService.getByChannelAndBankId(channel.getApiPayBankChannelCode(),
				card.getBankId());

		String payBillno = null;
		String operatorTime = new Moment().toSimpleTime();
		// 在这里新加API代付，然后在APIPayOrderSyncJob.getThirdStatus中加入查询即可完成API代付功能
		switch (channel.getChannelCode()) {
		case Global.PAYMENT_CHANNEL_HT:
		case Global.PAYMENT_CHANNEL_HTWECHAT:
		case Global.PAYMENT_CHANNEL_HTQQ:
		case Global.PAYMENT_CHANNEL_HTALIPAY:
		case Global.PAYMENT_CHANNEL_HTJDPAY:
			payBillno = htPayment.daifu(json, entity, card, bank, channel);
			break;
		case Global.PAYMENT_CHANNEL_ZS:
		case Global.PAYMENT_CHANNEL_ZSWECHAT:
		case Global.PAYMENT_CHANNEL_ZSALIPAY:
		case Global.PAYMENT_CHANNEL_ZSQQ:
			payBillno = zsPayment.daifu(json, entity, card, bank, channel);
			break;
		case Global.PAYMENT_CHANNEL_RX:
		case Global.PAYMENT_CHANNEL_RXWECHAT:
		case Global.PAYMENT_CHANNEL_RXQQ:
			payBillno = rxPayment.daifu(json, entity, card, bank, channel);
			break;
		case Global.PAYMENT_CHANNEL_CF:
		case Global.PAYMENT_CHANNEL_CFWECHAT:
		case Global.PAYMENT_CHANNEL_CFALIPAY:
		case Global.PAYMENT_CHANNEL_CFQQ:
		case Global.PAYMENT_CHANNEL_CFJDPAY:
			payBillno = cfPayment.daifu(json, entity, card, bank, channel);
			break;
		case Global.PAYMENT_CHANNEL_FKT:
		case Global.PAYMENT_CHANNEL_FKTWECHAT:
		case Global.PAYMENT_CHANNEL_FKTALIPAY:
		case Global.PAYMENT_CHANNEL_FKTQQ:
		case Global.PAYMENT_CHANNEL_FKTJDPAY:
			payBillno = fktPayment.daifu(json, entity, card, bank, channel);
			break;
		case Global.PAYMENT_CHANNEL_HTF:
		case Global.PAYMENT_CHANNEL_HTFQQ:
		case Global.PAYMENT_CHANNEL_HTFWECHAT:
		case Global.PAYMENT_CHANNEL_HTFALIPAY:
		case Global.PAYMENT_CHANNEL_HTFJDPAY:
			payBillno = htfPayment.daifu(json, entity, card, bank, channel);
			break;

		case Global.PAYMENT_CHANNEL_YR:
		case Global.PAYMENT_CHANNEL_YRQQ:
		case Global.PAYMENT_CHANNEL_YRALIPAY:
		case Global.PAYMENT_CHANNEL_YRWECHAT:
			payBillno = yrPayment.daifu(json, entity, card, bank, channel);
			break;
		case Global.PAYMENT_CHANNEL_AF:
		case Global.PAYMENT_CHANNEL_AFWECHAT:
		case Global.PAYMENT_CHANNEL_AFALIPAY:
		case Global.PAYMENT_CHANNEL_AFQQ:
			payBillno = afPayment.daifu(json, entity, card, bank, channel);
			break;
		case Global.PAYMENT_CHANNEL_TGF:
		case Global.PAYMENT_CHANNEL_TGFQQ:
		case Global.PAYMENT_CHANNEL_TGFQUICK:
		case Global.PAYMENT_CHANNEL_TGFJDPAY:
			payBillno = tgfPayment.daifu(json, entity, card, bank, channel);
			break;
		}

		

		return apiPayResultProcess(uEntity, entity, operatorTime, channel, payBillno, json);
	}

	private boolean apiPayResultProcess(AdminUser uEntity, UserWithdraw entity, String operatorTime,
			PaymentChannel channel, String payBillno, WebJSONObject json) {
		//
		if (json.getError() != null && json.getError() == -1) {
			String infos = "您的提现已提交至银行处理,请耐心等候！";
			entity.setInfos(infos);
			entity.setOperatorUser(uEntity.getUsername());
			entity.setOperatorTime(operatorTime);
			entity.setRemarks("使用" + channel.getName() + "代付");
			entity.setRemitStatus(Global.USER_WITHDRAW_REMITSTATUS_SYNC_STATUS);
			entity.setPayType(Global.WITHDRAW_PAY_TYPE_MANUAL_API);
			entity.setPaymentChannelId(channel.getId());
			boolean result = uWithdrawDao.update(entity);
			if (result) {
				String time = new Moment().toSimpleTime();
				String action = String.format("使用" + channel.getName() + "代付，连接异常，系统开始自动同步状态；操作人：%s",
						uEntity.getUsername());
				userWithdrawLogDao.add(
						new UserWithdrawLog(entity.getBillno(), entity.getUserId(), uEntity.getId(), action, time));
			}
			return result;
		} else {
			if (StringUtils.isNotEmpty(payBillno)) {
				String infos = "您的提现已处理，请您注意查收！";
				entity.setInfos(infos);
				entity.setPayBillno(payBillno);
				entity.setOperatorUser(uEntity.getUsername());
				entity.setOperatorTime(operatorTime);
				entity.setRemarks("使用" + channel.getName() + "代付");
				entity.setRemitStatus(Global.USER_WITHDRAW_REMITSTATUS_THIRD_UNPROCESS);
				entity.setPayType(Global.WITHDRAW_PAY_TYPE_MANUAL_API);
				entity.setPaymentChannelId(channel.getId());
				boolean result = uWithdrawDao.update(entity);
				if (result) {
					String time = new Moment().toSimpleTime();
					String action = String.format("使用" + channel.getName() + "代付，提交成功；操作人：%s", uEntity.getUsername());
					userWithdrawLogDao.add(
							new UserWithdrawLog(entity.getBillno(), entity.getUserId(), uEntity.getId(), action, time));
				}
				return result;
			}
		}

		return false;
	}

	@Override
	public boolean check(AdminUser uEntity, WebJSONObject json, int id, int status) {
		UserWithdraw entity = uWithdrawDao.getById(id);
		if (entity != null && entity.getStatus() == 0) {
			if (entity.getCheckStatus() == 0) {
				entity.setCheckStatus(status); // 0：待审核；1：已通过；2：未通过；
				Boolean boolean1 = uWithdrawDao.update(entity);
				if (boolean1) {
					uWithdrawLimitDao.delByUserId(entity.getUserId());// 提款审核通过时清空消费量
					String time = new Moment().toSimpleTime();
					String action = String.format("<font color=\"#35AA47\">审核通过</font>；操作人：%s", uEntity.getUsername());
					userWithdrawLogDao.add(
							new UserWithdrawLog(entity.getBillno(), entity.getUserId(), uEntity.getId(), action, time));
				}
				return boolean1;
			}
		} else {
			json.set(2, "2-2019");
		}
		return false;
	}

	@Override
	public boolean refuse(AdminUser uEntity, WebJSONObject json, int id, String reason, String remarks,
			String operatorUser) {
		UserWithdraw entity = uWithdrawDao.getById(id);
		if (entity != null && entity.getStatus() == 0) {
			// 必须已经锁定，并且操作人必须相同
			if (entity.getLockStatus() == 1) {
				if (operatorUser.equals(entity.getOperatorUser())) {
					User uBean = uDao.getById(entity.getUserId());
					if (uBean != null) {
						boolean uflag = uDao.updateLotteryMoney(entity.getUserId(), entity.getMoney());
						if (uflag) {
							String operatorTime = new Moment().toSimpleTime();
							String infos = "您的提现已被拒绝，金额已返还！";
							if (StringUtil.isNotNull(reason)) {
								infos += "原因：" + reason;
							}
							if (StringUtil.isNotNull(remarks)) {
								infos += "备注：" + remarks;
							}
							entity.setStatus(-1); // 0：未处理；1：已完成；-1：拒绝支付；
							entity.setInfos(infos);
							entity.setOperatorUser(operatorUser);
							entity.setOperatorTime(operatorTime);
							entity.setRemarks(infos);
							entity.setLockStatus(0); // 0：未锁定；1：已锁定；
							boolean flag = uWithdrawDao.update(entity);
							if (flag) {
								uBillService.addDrawBackBill(entity, uBean, entity.getRemarks());
								uSysMessageService.addRefuseWithdraw(entity.getUserId(), entity.getMoney());
								String time = new Moment().toSimpleTime();
								String action = String.format("<font color=\"#D84A38\">拒绝支付</font>；操作人：%s；%s",
										uEntity.getUsername(), infos);
								userWithdrawLogDao.add(new UserWithdrawLog(entity.getBillno(), entity.getUserId(),
										uEntity.getId(), action, time));
							}
							return flag;
						}
					}
				} else {
					json.set(2, "2-2021");
				}
			} else {
				json.set(2, "2-2020");
			}
		} else {
			json.set(2, "2-2019");
		}
		return false;
	}

	@Override
	public boolean withdrawFailure(AdminUser uEntity, WebJSONObject json, int id, String remarks, String operatorUser) {
		UserWithdraw entity = uWithdrawDao.getById(id);
		if (entity != null && entity.getStatus() == 0) {
			// 必须已经锁定，并且操作人必须相同
			if (entity.getLockStatus() == 1) {
				if (operatorUser.equals(entity.getOperatorUser())) {
					User uBean = uDao.getById(entity.getUserId());
					if (uBean != null) {
						boolean uflag = uDao.updateLotteryMoney(entity.getUserId(), entity.getMoney());
						if (uflag) {
							String operatorTime = new Moment().toSimpleTime();
							String infos = "您的提现失败，金额已返还！";
							if (StringUtil.isNotNull(remarks)) {
								infos += "备注：" + remarks;
							}
							entity.setStatus(1); // 0：未处理；1：已完成；-1：拒绝支付；
							entity.setInfos(infos);
							entity.setOperatorUser(operatorUser);
							entity.setOperatorTime(operatorTime);
							entity.setRemarks(infos);
							entity.setLockStatus(0); // 0：未锁定；1：已锁定；
							entity.setRemitStatus(-2);// 打款状态
							boolean flag = uWithdrawDao.update(entity);
							if (flag) {
								uBillService.addDrawBackBill(entity, uBean, entity.getRemarks());
								uSysMessageService.addRefuse(entity.getUserId(), entity.getMoney());
								String time = new Moment().toSimpleTime();
								String action = String.format("<span style=\"color: #D84A38;\">打款失败</span>；操作人：%s;%s",
										uEntity.getUsername(), infos);
								userWithdrawLogDao.add(new UserWithdrawLog(entity.getBillno(), entity.getUserId(),
										uEntity.getId(), action, time));
							}
							return flag;
						}
					}
				} else {
					json.set(2, "2-2021");
				}
			} else {
				json.set(2, "2-2020");
			}
		} else {
			json.set(2, "2-2019");
		}
		return false;
	}

	@Override
	public boolean reviewedFail(AdminUser uEntity, WebJSONObject json, int id, String remarks, String operatorUser) {
		UserWithdraw entity = uWithdrawDao.getById(id);
		if (entity != null && entity.getStatus() == 0) {
			// 必须已经锁定，并且操作人必须相同
			User uBean = uDao.getById(entity.getUserId());
			if (uBean != null) {
				boolean uflag = uDao.updateLotteryMoney(entity.getUserId(), entity.getMoney());
				if (uflag) {
					String infos = "您的提现审核失败，金额已返还！";
					if (StringUtil.isNotNull(remarks)) {
						infos += "备注：" + remarks;
					}
					entity.setStatus(1); // 0：未处理；1：已完成；-1：拒绝支付；
					entity.setInfos(infos);
					entity.setOperatorUser(operatorUser);
					entity.setRemarks(infos);
					entity.setLockStatus(0); // 0：未锁定；1：已锁定；
					entity.setCheckStatus(-1);
					boolean flag = uWithdrawDao.update(entity);
					if (flag) {
						uBillService.addDrawBackBill(entity, uBean, entity.getRemarks());
						uSysMessageService.addShFail(entity.getUserId(), entity.getMoney());
						String time = new Moment().toSimpleTime();
						String action = String.format("<font color=\"#D84A38\">审核拒绝</font>；操作人：%s；%s",
								uEntity.getUsername(), infos);
						userWithdrawLogDao.add(new UserWithdrawLog(entity.getBillno(), entity.getUserId(),
								uEntity.getId(), action, time));
					}
					return flag;
				}
			}
		} else {
			json.set(2, "2-2019");
		}
		return false;
	}

	@Override
	public boolean lock(AdminUser uEntity, WebJSONObject json, int id, String operatorUser) {
		UserWithdraw entity = uWithdrawDao.getById(id);
		if (entity != null && entity.getStatus() == 0) {
			if (entity.getCheckStatus() != 0) {
				if (entity.getLockStatus() == 0) {
					String operatorTime = new Moment().toSimpleTime();
					Boolean boolean1 = uWithdrawDao.lock(entity.getBillno(), operatorUser, operatorTime);
					if (boolean1) {
						String time = new Moment().toSimpleTime();
						String action = String.format("锁定；操作人：%s", uEntity.getUsername());
						userWithdrawLogDao.add(new UserWithdrawLog(entity.getBillno(), entity.getUserId(),
								uEntity.getId(), action, time));
					}
					return boolean1;
				} else {
					json.set(2, "2-2021");
				}
			} else {
				json.set(2, "2-2023");
			}
		} else {
			json.set(2, "2-2019");
		}
		return false;
	}

	@Override
	public boolean unlock(AdminUser uEntity, WebJSONObject json, int id, String operatorUser) {
		UserWithdraw entity = uWithdrawDao.getById(id);
		if (entity != null && entity.getStatus() == 0) {
			if (entity.getLockStatus() == 1) {
				if (operatorUser != null && operatorUser.equals(entity.getOperatorUser())) {

					Boolean boolean1 = uWithdrawDao.unlock(entity.getBillno(), operatorUser);
					if (boolean1) {
						String time = new Moment().toSimpleTime();
						String action = String.format("解锁；操作人：%s", uEntity.getUsername());
						userWithdrawLogDao.add(new UserWithdrawLog(entity.getBillno(), entity.getUserId(),
								uEntity.getId(), action, time));
					}
					return boolean1;
				} else {
					json.set(2, "2-2021");
				}
			} else {
				json.set(2, "2-2020");
			}
		} else {
			json.set(2, "2-2019");
		}
		return false;
	}

	@Override
	public boolean update(UserWithdraw withdraw) {
		return uWithdrawDao.update(withdraw);
	}

	@Override
	public double[] getTotalWithdraw(String billno, String username, String minTime, String maxTime,
			String minOperatorTime, String maxOperatorTime, Double minMoney, Double maxMoney, String keyword,
			Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId) {
		Integer userId = null;
		if (StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				userId = user.getId();
			}
		}

		return uWithdrawDao.getTotalWithdraw(billno, userId, minTime, maxTime, minOperatorTime, maxOperatorTime,
				minMoney, maxMoney, keyword, status, checkStatus, remitStatus, paymentChannelId);
	}

	@Override
	public double[] getHistoryTotalWithdraw(String billno, String username, String minTime, String maxTime,
			String minOperatorTime, String maxOperatorTime, Double minMoney, Double maxMoney, String keyword,
			Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId) {
		Integer userId = null;
		if (StringUtil.isNotNull(username)) {
			User user = uDao.getByUsername(username);
			if (user != null) {
				userId = user.getId();
			}
		}

		return uWithdrawDao.getHistoryTotalWithdraw(billno, userId, minTime, maxTime, minOperatorTime, maxOperatorTime,
				minMoney, maxMoney, keyword, status, checkStatus, remitStatus, paymentChannelId);
	}
}