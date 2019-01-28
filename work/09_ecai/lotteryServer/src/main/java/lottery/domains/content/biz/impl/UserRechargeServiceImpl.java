package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import lottery.domains.pool.payment.ty.TyPayment;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javautils.date.Moment;
import javautils.math.MathUtil;
import lottery.domains.content.biz.ActivityFirstRechargeBillService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserRechargeService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.biz.UserWithdrawLimitService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserRechargeDao;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserRecharge;
import lottery.domains.content.global.Global;
import lottery.domains.pool.payment.AbstractPayment;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;
import lottery.domains.pool.payment.af.AFPayment;
import lottery.domains.pool.payment.ay.AYPayment;
import lottery.domains.pool.payment.cf.CFPayment;
import lottery.domains.pool.payment.fkt.FKTPayment;
import lottery.domains.pool.payment.ht.HTPayment;
import lottery.domains.pool.payment.htf.HTFPayment;
import lottery.domains.pool.payment.tgf.TGFPayment;
import lottery.domains.pool.payment.utils.RequestUtils;
import lottery.domains.pool.payment.wf.WFPayment;
import lottery.domains.pool.payment.yr.YRPayment;
import lottery.domains.pool.payment.zs.ZSPayment;
import lottery.web.WUC;
import lottery.web.WebJSON;

@Service
public class UserRechargeServiceImpl implements UserRechargeService {
	private static final Logger log = LoggerFactory.getLogger(UserRechargeServiceImpl.class);
	private static final String BANK_TRANSFER_SECRET_KEY = "a987411dda11G30Oie";

	/**
	 * DAO
	 */
	@Autowired
	private UserRechargeDao uRechargeDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserBillService uBillService;
	
	@Autowired
	private UserWithdrawLimitService uWithdrawLimitService;
	
	@Autowired
	private UserSysMessageService uSysMessageService;

	@Autowired
	private ActivityFirstRechargeBillService firstRechargeBillService;

	@Autowired
	@Qualifier(value = "sessionFactoryAutoLoad")
	private SessionFactory sessionFactoryAutoLoad;

	@Override
	public boolean addRecharge(int userId, double amount, String billno, PaymentChannel channel, Integer requestBankId,String applyIp) {
		double money = amount;
		double beforeMoney = 0;
		double afterMoney = 0;
		double recMoney = money;
		double feeMoney = 0;
		String time = new Moment().toSimpleTime();
		int status = 0; // 还没有支付
		int type = channel.getType();
		int subtype = channel.getSubType();
		int channelId = channel.getId();
		UserRecharge entity = new UserRecharge();
		entity.setBillno(billno);
		entity.setUserId(userId);
		entity.setMoney(money);
		entity.setBeforeMoney(beforeMoney);
		entity.setAfterMoney(afterMoney);
		entity.setRecMoney(recMoney);
		entity.setFeeMoney(feeMoney);
		entity.setTime(time);
		entity.setStatus(status);
		entity.setType(type);
		entity.setSubtype(subtype);
		entity.setChannelId(channelId);
		entity.setRequestBankId(requestBankId);
		entity.setApplyIp(applyIp);
		entity.setInfos("在线充值");
		entity.setRemarks("在线充值");
		return uRechargeDao.add(entity);
	}
	
	@Override
	public Integer addWangYingTransfersRecharge(int userId, double amount, String billno, String name, int cardId, PaymentChannel channel, Integer requestBankId,String postscript,String ip) {
		double money = amount;
		double beforeMoney = 0;
		double afterMoney = 0;
		double recMoney = money;
		double feeMoney = 0;
		String time = new Moment().toSimpleTime();
		int status = 0; // 还没有支付
		int type = channel.getType();
		int subtype = channel.getSubType();
		int channelId = channel.getId();
		UserRecharge entity = new UserRecharge();
		entity.setBillno(billno);
		entity.setUserId(userId);
		entity.setMoney(money);
		entity.setBeforeMoney(beforeMoney);
		entity.setAfterMoney(afterMoney);
		entity.setRecMoney(recMoney);
		entity.setFeeMoney(feeMoney);
		entity.setTime(time);
		entity.setStatus(status);
		entity.setType(type);
		entity.setSubtype(subtype);
		entity.setChannelId(channelId);
		entity.setRequestBankId(requestBankId);
		entity.setPostscript(postscript);
		entity.setApplyIp(ip);
		entity.setInfos("网银转账");
		entity.setRemarks("网银转账");
		entity.setCardId(cardId);
		entity.setRealName(name);

		boolean added  = uRechargeDao.add(entity);

		if (added) {
			uRechargeDao.update(entity);
			// // 把记录加到采集机去
			// added = addToAutoLoad(entity, name);
			// if (!added) {
			// 	return null;
			// }
		}

		return status;
	}

	/**
	 * 把记录加到采集机去
	 */
	private boolean addToAutoLoad(UserRecharge recharge, String name) {
		Session session = sessionFactoryAutoLoad.openSession();

		try {
			session.beginTransaction();

			String secret = recharge.getTime() + name + recharge.getBillno() + recharge.getPostscript() + BANK_TRANSFER_SECRET_KEY;
			secret = DigestUtils.md5Hex(secret);
			String insertSql = "INSERT INTO `user_bank_transfer` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			SQLQuery insertQuery = session.createSQLQuery(insertSql);
			insertQuery.setParameter(new Integer(0), null); // ID
			insertQuery.setParameter(1, recharge.getBillno()); // 账单
			insertQuery.setParameter(2, ""); // 真实姓名，由采集程序修改
			insertQuery.setParameter(3, recharge.getPostscript()); // 附言
			insertQuery.setParameter(4, 0); // 金额
			insertQuery.setParameter(5, recharge.getTime()); // 提交时间
			insertQuery.setParameter(6, "1"); // 状态，1：未支付，2：成功
			insertQuery.setParameter(7, ""); // 支付流水号
			insertQuery.setParameter(8, ""); // 支付时间
			insertQuery.setParameter(9, secret); // 加密验证码

			int insertCount = insertQuery.executeUpdate();
			if(insertCount > 0) {
				session.getTransaction().commit();
			}
		} catch (Exception e) {
			log.error("数据加到自动上分采集机发生错误", e);
			session.getTransaction().rollback();
			return false;
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}

		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public int checkUnPaidWangYingTransfersRecharge(WebJSON json, int userId, String name) {
		List<Criterion> criterions = new ArrayList<>();
		 criterions.add(Restrictions.eq("userId", userId));
		criterions.add(Restrictions.eq("type", Global.PAYMENT_CHANNEL_TYPE_WANGYING)); // 网银转账
		criterions.add(Restrictions.eq("subtype", Global.PAYMENT_CHANNEL_SUB_TYPE_WANGYING_TRANSFER));
		criterions.add(Restrictions.eq("status", 0)); // 未支付

		String time = new Moment().subtract(48, "hours").toSimpleTime();
		criterions.add(Restrictions.ge("time", time)); // 最多只查询过去48小时

		List<UserRecharge> list = uRechargeDao.list(criterions, null);
		if (CollectionUtils.isNotEmpty(list)) {
			return list.size();
		}
		return 0;
	}

	@Override
	public synchronized boolean doRecharge(PaymentChannel channel, VerifyResult verifyResult) {
		if(verifyResult.getReceiveMoney() <= 0
				|| StringUtils.isEmpty(verifyResult.getSelfBillno())
				|| StringUtils.isEmpty(verifyResult.getChannelBillno())){
			return false;
		}
		UserRecharge userRecharge = uRechargeDao.getByBillno(verifyResult.getSelfBillno());
		if(userRecharge == null){
			return false;
		}
		if(userRecharge.getStatus() == 1){
			return true;
		}
		if (userRecharge.getStatus() != 0) {
			return false;
		}

		// 智付支付宝需要调整金额
		if("aypay".equals(channel.getExt2())){
			
		}else if (userRecharge.getMoney() != verifyResult.getReceiveMoney()) {
			return false;
		}

		int userId = userRecharge.getUserId();
		User uBean = userDao.getById(userId);
		double money = verifyResult.getReceiveMoney();
		double beforeMoney = uBean.getLotteryMoney();
		double afterMoney = beforeMoney + money;
		double recMoney = money;
		Moment moment = new Moment();
		String time = moment.toSimpleTime();
		int status = 1;
		userRecharge.setMoney(money);
		userRecharge.setBeforeMoney(beforeMoney);
		userRecharge.setAfterMoney(afterMoney);
		userRecharge.setRecMoney(recMoney);
		userRecharge.setStatus(status);
		userRecharge.setPayTime(time);
		userRecharge.setPayBillno(verifyResult.getChannelBillno());
		
		double receiveFeeMoney = calculateReceiveFeeMoney(channel, recMoney); // 第三方平台收款手续费
		userRecharge.setReceiveFeeMoney(receiveFeeMoney);

		boolean cFlag = uRechargeDao.update(userRecharge);
		if(cFlag) {
			boolean added = uBillService.addRechargeBill(userRecharge, uBean, receiveFeeMoney);
			if (added) {
				//消费限制
				uWithdrawLimitService.add(uBean.getId(), recMoney, time, channel);
				uSysMessageService.addOnlineRecharge(uBean.getId(), recMoney);
				userDao.updateLotteryMoney(uBean.getId(), recMoney);
				// // 首冲活动
				 if (StringUtils.isNotEmpty(userRecharge.getApplyIp())) {
				 	firstRechargeBillService.tryCollect(userId, money, userRecharge.getApplyIp());
				 }
			}
		}
		return cFlag;
	}

	private double calculateReceiveFeeMoney(PaymentChannel channel, double recMoney) {
		double receiveFeeMoney = 0;
		if (channel != null && channel.getThirdFee() > 0) {
			int thirdFeeFixed = channel.getThirdFeeFixed();
			if (thirdFeeFixed == 1) { // 固定手续费
				receiveFeeMoney = channel.getThirdFeeFixed();
			}
			else if (thirdFeeFixed == 0) { // 不固定手续费
				double feePercent = MathUtil.divide(channel.getThirdFeeFixed(), 100, 6);
				receiveFeeMoney = MathUtil.multiply(feePercent, recMoney, 6);
			}
		}

		return receiveFeeMoney;
	}

	@Override
	public PrepareResult prepare(PaymentChannel channel, String billno, double amount, String bankco, HttpServletRequest request) {
	   String requestHost = RequestUtils.getReferer(request);
//		String notifyUrl = requestHost + WUC.USER_PAYMENT_NOTIFY + "/";
//		String resultUrl = requestHost + WUC.USER_PAYMENT_RESULT + "/";
		String notifyUrl = requestHost + WUC.USER_PAYMENT_NOTIFY + "/" + channel.getId()+"/";
		String resultUrl = requestHost + WUC.USER_PAYMENT_RESULT + "/" + channel.getId()+"/";
		switch(channel.getChannelCode()) {
			case Global.PAYMENT_CHANNEL_HT:
			case Global.PAYMENT_CHANNEL_HTWECHAT:
			case Global.PAYMENT_CHANNEL_HTALIPAY:
			case Global.PAYMENT_CHANNEL_HTQQ:
			case Global.PAYMENT_CHANNEL_HTJDPAY:
				return HTPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_CF:
			case Global.PAYMENT_CHANNEL_CFWECHAT:
			case Global.PAYMENT_CHANNEL_CFALIPAY:
			case Global.PAYMENT_CHANNEL_CFQQ:
			case Global.PAYMENT_CHANNEL_CFJDPAY:
				return CFPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_ZS:
			case Global.PAYMENT_CHANNEL_ZSWECHAT:
			case Global.PAYMENT_CHANNEL_ZSALIPAY:
			case Global.PAYMENT_CHANNEL_ZSQQ:
				return ZSPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_FKT:
				return FKTPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_WF:
				return WFPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_HTF:
			case Global.PAYMENT_CHANNEL_HTFQQ:
			case Global.PAYMENT_CHANNEL_HTFWECHAT:
			case Global.PAYMENT_CHANNEL_HTFALIPAY:
			case Global.PAYMENT_CHANNEL_HTFJDPAY:
				return HTFPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_YR:
			case Global.PAYMENT_CHANNEL_YRQQ:
			case Global.PAYMENT_CHANNEL_YRALIPAY:
			case Global.PAYMENT_CHANNEL_YRWECHAT:
				return YRPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_AF:
			case Global.PAYMENT_CHANNEL_AFQQ:
			case Global.PAYMENT_CHANNEL_AFQUICK:
			case Global.PAYMENT_CHANNEL_AFWECHAT:
				return AFPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);	
			case Global.PAYMENT_CHANNEL_TGF:
			case Global.PAYMENT_CHANNEL_TGFQQ:
			case Global.PAYMENT_CHANNEL_TGFQUICK:
			case Global.PAYMENT_CHANNEL_TGFJDPAY:
				return TGFPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);		
			case Global.PAYMENT_CHANNEL_AYWECHAT:
			case Global.PAYMENT_CHANNEL_AYALIPAY:
				return AYPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
			case Global.PAYMENT_CHANNEL_TYWECHAT:
            case Global.PAYMENT_CHANNEL_TYALIPAY:
                return TyPayment.prepare(billno, amount, bankco, notifyUrl, resultUrl, channel, request);
		}

		return AbstractPayment.retPrepareFailed("暂不支持该充值通道！");
	}
	
	@Override
	public VerifyResult verify(PaymentChannel channel, Map<String, String> resMap) {
		try {
			switch(channel.getChannelCode()) {
                case Global.PAYMENT_CHANNEL_HT:
                case Global.PAYMENT_CHANNEL_HTWECHAT:
                case Global.PAYMENT_CHANNEL_HTALIPAY:
                case Global.PAYMENT_CHANNEL_HTQQ:
                case Global.PAYMENT_CHANNEL_HTJDPAY:
                    return HTPayment.verify(channel, resMap);
                case Global.PAYMENT_CHANNEL_CF:
                case Global.PAYMENT_CHANNEL_CFWECHAT:
                case Global.PAYMENT_CHANNEL_CFALIPAY:
                case Global.PAYMENT_CHANNEL_CFQQ:
                case Global.PAYMENT_CHANNEL_CFJDPAY:
                    return CFPayment.verify(channel, resMap);
                case Global.PAYMENT_CHANNEL_ZS:
                case Global.PAYMENT_CHANNEL_ZSWECHAT:
                case Global.PAYMENT_CHANNEL_ZSALIPAY:
                case Global.PAYMENT_CHANNEL_ZSQQ:
                    return ZSPayment.verify(channel, resMap);
                case Global.PAYMENT_CHANNEL_FKT:
                    return FKTPayment.verify(channel, resMap);
                case Global.PAYMENT_CHANNEL_WF:
                    return WFPayment.verify(channel, resMap);
                case Global.PAYMENT_CHANNEL_HTF:
                case Global.PAYMENT_CHANNEL_HTFQQ:
                case Global.PAYMENT_CHANNEL_HTFWECHAT:
                case Global.PAYMENT_CHANNEL_HTFALIPAY:
                case Global.PAYMENT_CHANNEL_HTFJDPAY:
                    return HTFPayment.verify(channel, resMap);
                case Global.PAYMENT_CHANNEL_YR:;
                case Global.PAYMENT_CHANNEL_YRQQ:;
                case Global.PAYMENT_CHANNEL_YRWECHAT:
                case Global.PAYMENT_CHANNEL_YRALIPAY:
                    return YRPayment.verify(channel, resMap);
                case Global.PAYMENT_CHANNEL_AF:;
                case Global.PAYMENT_CHANNEL_AFQQ:;
                case Global.PAYMENT_CHANNEL_AFWECHAT:
                case Global.PAYMENT_CHANNEL_AFQUICK:
                    return AFPayment.verify(channel, resMap);
    			case Global.PAYMENT_CHANNEL_TGF:
    			case Global.PAYMENT_CHANNEL_TGFQQ:
    			case Global.PAYMENT_CHANNEL_TGFQUICK:
    			case Global.PAYMENT_CHANNEL_TGFJDPAY:
    				return TGFPayment.verify(channel, resMap);
    			case Global.PAYMENT_CHANNEL_AYWECHAT:
    			case Global.PAYMENT_CHANNEL_AYALIPAY: 
    				  return AYPayment.verify(channel, resMap);
    			case Global.PAYMENT_CHANNEL_TYWECHAT:
    			case Global.PAYMENT_CHANNEL_TYALIPAY:
    				  return TyPayment.verify(channel, resMap);

            }
		} catch (Exception e) {

			log.error("回调处理异常", e);
			return AbstractPayment.retVerifyFailed("处理异常！");
		}

		return AbstractPayment.retVerifyFailed("暂不支持该充值通道！");
	}

	@Override
	@Transactional(readOnly = true)
	public UserRecharge getUserRechargeOrderInfo(String billno) {
		return  uRechargeDao.getByBillno(billno);
	}

	@Override
	@Transactional(readOnly = true)
	public UserRecharge getByBillno(String billno) {
		return uRechargeDao.getByBillno(billno);
	}
}