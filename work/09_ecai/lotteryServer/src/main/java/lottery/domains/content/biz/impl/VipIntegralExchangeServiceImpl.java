package lottery.domains.content.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javautils.date.Moment;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.VipIntegralExchangeService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.VipIntegralExchangeDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.VipIntegralExchange;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.config.VipConfig;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VipIntegralExchangeServiceImpl implements VipIntegralExchangeService {
	
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private VipIntegralExchangeDao vIntegralExchangeDao;
	
	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;
	
	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@Override
	public boolean doExchange(WebJSON json, int userId, int integral) {
		User uBean = uDao.getById(userId);
		if(uBean != null) {
			if(integral > 0 && uBean.getIntegral() >= integral) {
				// 检查输入的积分是不是汇率的整数
				VipConfig vipConfig = dataFactory.getVipConfig();
				int exchangeRate = vipConfig.getExchangeRate();
				int maxExchangeMultiple = vipConfig.getMaxExchangeMultiple();
				int maxExchangeTimes = vipConfig.getMaxExchangeTimes();
				if(integral % exchangeRate == 0) {
					int money = integral / exchangeRate;
					if(money > 0 && money <= maxExchangeMultiple) {
						// 验证兑换次数
						String date = new Moment().toSimpleDate();
						int dateCount = vIntegralExchangeDao.getDateCount(userId, date);
						if(dateCount < maxExchangeTimes) {
							// 扣除积分
							boolean iFlag = uDao.updateIntegral(userId, -integral);
							if(iFlag) {
								boolean uFlag = uDao.updateLotteryMoney(userId, money);
								if(uFlag) {
									// 添加帐变记录
									String remarks = "积分换现金";
									int account = Global.BILL_ACCOUNT_LOTTERY;
									uBillService.addIntegralBill(uBean, account, money, remarks);
									// 添加积分兑换记录
									String time = new Moment().toSimpleTime();
									VipIntegralExchange entity = new VipIntegralExchange(userId, integral, money, time);
									vIntegralExchangeDao.add(entity);
								} else {
									uDao.updateIntegral(uBean.getId(), integral);
								}
								return uFlag;
							}
						} else {
							json.set(2, "2-1069");
						}
					} else {
						json.set(2, "2-1068", 1, maxExchangeMultiple);
					}
				} else {
					json.set(2, "2-1067");
				}
			} else {
				json.set(2, "2-1066");
			}
		}
		return false;
	}

}