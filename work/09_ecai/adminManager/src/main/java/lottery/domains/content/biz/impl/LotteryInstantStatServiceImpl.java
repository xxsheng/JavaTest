package lottery.domains.content.biz.impl;

import javautils.ObjectUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.LotteryInstantStatService;
import lottery.domains.content.dao.ActivityBindBillDao;
import lottery.domains.content.dao.ActivityGrabBillDao;
import lottery.domains.content.dao.ActivityRechargeLoopBillDao;
import lottery.domains.content.dao.ActivityRewardBillDao;
import lottery.domains.content.dao.ActivitySalaryBillDao;
import lottery.domains.content.dao.ActivitySignBillDao;
import lottery.domains.content.dao.UserBillDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserRechargeDao;
import lottery.domains.content.dao.UserTransfersDao;
import lottery.domains.content.dao.UserWithdrawDao;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.InstantStatVO;

@Service
public class LotteryInstantStatServiceImpl implements LotteryInstantStatService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserBillDao uBillDao;
	
	@Autowired
	private UserRechargeDao uRechargeDao;
	
	@Autowired
	private UserWithdrawDao uWithdrawDao;
	
	@Autowired
	private UserTransfersDao uTransfersDao;
	
	@Autowired
	private ActivityBindBillDao aBindBillDao;
	
	@Autowired
	private ActivityRewardBillDao aRewardBillDao;
	
	@Autowired
	private ActivitySalaryBillDao aSalaryBillDao;
	
	@Autowired
	private ActivityRechargeLoopBillDao aRechargeLoopBillDao;
	
	@Autowired
	private ActivityGrabBillDao aGrabBillDao;
	
	@Override
	public InstantStatVO getInstantStat(String sTime, String eTime) {
		InstantStatVO bean = new InstantStatVO();
		// ICBC
		{
			int[] type = { Global.PAYMENT_CHANNEL_TYPE_WANGYING };
			int[] subtype = { Global.PAYMENT_CHANNEL_SUB_TYPE_WANGYING_TRANSFER };
			int payBankId = 1;
			double money = uRechargeDao.getTotalRecharge(sTime, eTime, type, subtype, payBankId);
			bean.setIcbcMoney(money);
		}
		// CCB
		{
			int[] type = { Global.PAYMENT_CHANNEL_TYPE_WANGYING };
			int[] subtype = { Global.PAYMENT_CHANNEL_SUB_TYPE_WANGYING_TRANSFER };
			int payBankId = 2;
			double money = uRechargeDao.getTotalRecharge(sTime, eTime, type, subtype, payBankId);
			bean.setCcbMoney(money);
		}
		// CMB
		{
			int[] type = { Global.PAYMENT_CHANNEL_TYPE_WANGYING };
			int[] subtype = { Global.PAYMENT_CHANNEL_SUB_TYPE_WANGYING_TRANSFER };
			int payBankId = 4;
			double money = uRechargeDao.getTotalRecharge(sTime, eTime, type, subtype, payBankId);
			bean.setCmbMoney(money);
		}
		// CMBC
		{
			int[] type = { Global.PAYMENT_CHANNEL_TYPE_WANGYING };
			int[] subtype = { Global.PAYMENT_CHANNEL_SUB_TYPE_WANGYING_TRANSFER };
			int payBankId = 18;
			double money = uRechargeDao.getTotalRecharge(sTime, eTime, type, subtype, payBankId);
			bean.setCmbcMoney(money);
		}
		// THRID
		{
			double money = uRechargeDao.getThirdTotalRecharge(sTime, eTime);
			bean.setThirdMoney(money);
		}
		// 充值未到账
		{
			int[] thisType = { Global.PAYMENT_CHANNEL_TYPE_SYSTEM };
			int[] subtype = { Global.PAYMENT_CHANNEL_SUB_TYPE_SYSTEM_NOTARRIVAL };
			double money = uRechargeDao.getTotalRecharge(sTime, eTime, thisType, subtype, null);
			bean.setNotarrivalMoney(money);
		}
		// 取款
		{
			double money = uWithdrawDao.getTotalWithdraw(sTime, eTime);
			bean.setWithdrawalsMoney(money);
		}
		// 自动打款
		{
			double money = uWithdrawDao.getTotalAutoRemit(sTime, eTime);
			bean.setThrildRemitMoney(money);
		}
		// 上下级转账（存）
		{
			int type = Global.USER_TRANSFERS_TYPE_USER_IN;
			double money = uTransfersDao.getTotalTransfers(sTime, eTime, type);
			bean.setTransUserIn(money);
		}
		// 上下级转账（取）
		{
			int type = Global.USER_TRANSFERS_TYPE_USER_OUT;
			double money = uTransfersDao.getTotalTransfers(sTime, eTime, type);
			bean.setTransUserOut(money);
		}
		// 手续费取=用户取款扣的手续费
		{
			double money = uWithdrawDao.getTotalFee(sTime, eTime);
			bean.setFeeDeductMoney(money);
		}
		// 手续费存=用户充值的手续费(实际第三方)
		{
			double feeFillMoney = uRechargeDao.getTotalFee(sTime, eTime);
			bean.setFeeFillMoney(feeFillMoney);
		}
		// 修改资金（增加）
		{
			int type = Global.BILL_TYPE_ADMIN_ADD;
			int[] refType = { };
			double money = uBillDao.getTotalMoney(sTime, eTime, type, refType);
			bean.setAdminAddMoney(money);
		}
		// 修改资金（减少）
		{
			int type = Global.BILL_TYPE_ADMIN_MINUS;
			int[] refType = { };
			double money = uBillDao.getTotalMoney(sTime, eTime, type, refType);
			bean.setAdminMinusMoney(money);
		}
		// 分红资金
		{
			int type = Global.BILL_TYPE_DIVIDEND;
			int[] refType = { };
			double money = uBillDao.getTotalMoney(sTime, eTime, type, refType);
			bean.setDividendMoney(money);
		}
		// 消费佣金
		{
			int type = 1;
			double money = aRewardBillDao.total(sTime, eTime, type);
			bean.setActivityRewardXFMoney(money);
		}
		// 盈亏佣金
		{
			int type = 2;
			double money = aRewardBillDao.total(sTime, eTime, type);
			bean.setActivityRewardYKMoney(money);
		}
		// 直属日工资
		{
			int type = 1;
			double money = aSalaryBillDao.total(sTime, eTime, type);
			bean.setActivitySalaryZSMoney(money);
		}
		// 总代日工资
		{
			int type = 2;
			double money = aSalaryBillDao.total(sTime, eTime, type);
			bean.setActivitySalaryZDMoney(money);
		}
		// 绑定资料体验金
		{
			double money = aBindBillDao.total(sTime, eTime);
			bean.setActivityBindMoney(money);
		}
		
		// VIP  2,
		{
			int type = Global.BILL_TYPE_ACTIVITY;
			int[] refType = {2};
			double money = uBillDao.getTotalMoney(sTime, eTime, type, refType);
			bean.setActivityRechargeMoney(money);
		}
		
		// 连环嗨不停
		{
			double money = aRechargeLoopBillDao.total(sTime, eTime);
			bean.setActivityRechargeLoopMoney(money);
		}
		//积分兑换
		{
			int type = Global.BILL_TYPE_INTEGRAL;
			int[] refType = { };
			double money = uBillDao.getTotalMoney(sTime, eTime, type, refType);
			bean.setActivityJiFenMoney(money);
		}
		{
			double total = mActivitySignBillDao.total(sTime, eTime);
			bean.setActivitySignMoney(total);
		}
		// 活动补贴
		{
			int type = Global.BILL_TYPE_ACTIVITY;
			int[] refType = { 0 };
			double money = uBillDao.getTotalMoney(sTime, eTime, type, refType);
			bean.setActivityOtherMoney(money);
		}
		// 平台用户余额
		{
			Object[] banlance = uDao.getTotalMoney();
			double totalBalance = ObjectUtil.toDouble(banlance[0]);
			bean.setTotalBalance(totalBalance);
			double lotteryBalance = ObjectUtil.toDouble(banlance[1]);
			bean.setLotteryBalance(lotteryBalance);
			double baccaratBalance = ObjectUtil.toDouble(banlance[2]);
			bean.setBaccaratBalance(baccaratBalance);
		}
		// 拆红包总额
		{
			double money = aGrabBillDao.getGrabTotal(sTime, eTime);
			bean.setActivityGrabMoney(money);
		}
		return bean;
	}
	@Autowired
	private ActivitySignBillDao mActivitySignBillDao;
}