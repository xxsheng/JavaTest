package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import javautils.lzma.LZMAJsUtil;
import javautils.redis.JedisTemplate;
import lottery.domains.content.biz.UserBetsService;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.dao.UserBetsDao;
import lottery.domains.content.dao.UserBetsOriginalDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.read.UserBetsReadDao;
import lottery.domains.content.entity.*;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.config.LotteryConfig;
import lottery.domains.jobs.MailJob;
import lottery.domains.jobs.UserBetsSameIpLogJob;
import lottery.domains.pool.DataFactory;
import lottery.domains.utils.open.LotteryOpenUtil;
import lottery.domains.utils.open.OpenTime;
import lottery.domains.utils.prize.PrizeUtils;
import lottery.web.WSC;
import lottery.web.WebJSON;
import lottery.web.content.validate.UserBetsValidate;
import lottery.web.content.validate.UserDailySettleValidate;
import lottery.web.content.validate.UserDividendValidate;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserBetsServiceImpl implements UserBetsService {
	private static final Logger log = LoggerFactory.getLogger(UserBetsServiceImpl.class);
	/**
	 * DAO
	 */
	@Autowired
	private UserDao uDao;

	@Autowired
	private UserBetsDao uBetsDao;

	@Autowired
	private UserBetsReadDao uBetsReadDao;

	@Autowired
	private UserBetsOriginalDao uBetsOriginalDao;

	@Autowired
	private UserDividendValidate uDividendValidate;

	@Autowired
	private UserDailySettleValidate uDailySettleValidate;

	/**
	 * SERVICE
	 */
	@Autowired
	private UserBillService uBillService;

	/**
	 * UTILS
	 */
	@Autowired
	private LotteryOpenUtil lOpenUtil;

	/**
	 * VALIDATE
	 */
	@Autowired
	private UserBetsValidate uBetsValidate;

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;

	@Autowired
	private MailJob mailJob;

	@Autowired
	private JedisTemplate jedisTemplate;

	@Autowired
	private UserBetsSameIpLogJob uBetsSameIpLogJob;

	/**
	 * 生成投注单号
	 */
	private String billno() {
		// 总共20位，时间到秒12位 + 8位随机字符
		return new Moment().format("yyMMddHHmmss") + RandomStringUtils.random(8, true, true);
	}

	/**
	 * 生成追号单号
	 */
	private String chaseBillno() {
		// return OrderUtil.createString(32);
		return ObjectId.get().toString();
	}

	/**
	 * 下单操作
	 */
	private boolean doOrder(UserBets bBean) {
		User uBean = uDao.getById(bBean.getUserId()); // 由于service设置事物获取最新金额应该从session中获取
														// by anson 2015/12/02
		double bMoney = bBean.getMoney();
		if (uBean.getLotteryMoney() >= bMoney) {
			boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), -bMoney, bMoney);
			if (uFlag) {
				boolean bFlag = uBetsDao.add(bBean);
				if (bFlag) {
					uBillService.addSpendBill(bBean, uBean);
					try {
						UserBetsOriginal original = new UserBetsOriginal(bBean);
						uBetsOriginalDao.add(original); // 增加原始注单记录
					} catch (Exception e) {
						log.error("增加原始注单时发生异常", e);
					}

					try {
						if (bBean.getMoney() >= dataFactory.getMailConfig().getBet()) {
							mailJob.sendBet(uBean.getUsername(), bBean);
						}
					} catch (Exception e) {
						log.error("投注发生异常", e);
					}

					// 缓存注单ID
					cacheOrderId(bBean);

					// 添加同IP投注日志
					uBetsSameIpLogJob.add(bBean);
				} else {
					// 防止漏单
					uDao.updateLotteryMoney(uBean.getId(), bMoney, -bMoney);
				}
				return bFlag;
			}
		}
		return false;
	}

	private void cacheOrderId(UserBets bBean) {
		// 缓存未结算注单ID
		final String cacheKey = String.format(WSC.USER_BETS_UNOPEN_RECENT_KEY, bBean.getUserId());
		final String userBetsId = bBean.getId() + "";

		try {
			jedisTemplate.execute(new JedisTemplate.PipelineActionNoResult() {
				@Override
				public void action(Pipeline pipeline) {
					pipeline.lpush(cacheKey, userBetsId);
					pipeline.ltrim(cacheKey, 0, 50); // 最多设置50条
					pipeline.expire(cacheKey, 60 * 60 * 12); // 12小时过期
					pipeline.sync();
				}
			});
		} catch (JedisException e) {
			log.error("执行Redis缓存注单ID时出错", e);
		}
	}

	private void cacheOrderCompleteId(UserBets bBean) {
		final String unOpenCacheKey = String.format(WSC.USER_BETS_UNOPEN_RECENT_KEY, bBean.getUserId());
		final String openedCacheKey = String.format(WSC.USER_BETS_OPENED_RECENT_KEY, bBean.getUserId());
		final String userBetsId = bBean.getId() + "";

		try {
			jedisTemplate.execute(new JedisTemplate.PipelineActionNoResult() {
				@Override
				public void action(Pipeline pipeline) {
					pipeline.lrem(unOpenCacheKey, 1, userBetsId); // 移除未结算订单ID
					pipeline.lpush(openedCacheKey, userBetsId); // 设置已结算订单ID
					pipeline.ltrim(openedCacheKey, 0, 5); // 最多设置5条
					pipeline.expire(openedCacheKey, 60 * 60 * 12); // 12小时过期
					pipeline.sync();
				}
			});
		} catch (JedisException e) {
			log.error("执行Redis缓存注单ID时出错", e);
		}
	}

	/**
	 * 取消订单
	 */
	private boolean doCancelOrder(UserBets bBean, User uBean) {
		Moment thisTime = new Moment();
		Moment stopTime = new Moment().fromTime(bBean.getStopTime());
		if (bBean.getType() != Global.USER_BETS_TYPE_PLAN) {
			if (bBean.getStatus() == 0 && thisTime.lt(stopTime)) {
				boolean cFlag = uBetsDao.cancel(bBean.getId(), uBean.getId());
				if (cFlag) {
					if (uBean != null) {
						boolean uFlag = uDao.updateLotteryMoney(uBean.getId(), bBean.getMoney(), -bBean.getMoney());
						if (uFlag) {
							cacheOrderCompleteId(bBean);
							uBillService.addCancelOrderBill(bBean, uBean);
						}
					}
				}
				return cFlag;
			}
		}
		return false;
	}

	@Override
	public boolean reBet(WebJSON json, User uEntity, int id, int userId, String ip) {
		LotteryConfig config = dataFactory.getLotteryConfig();
		if (uEntity.getCode() > config.getNotBetPointAccount() || uEntity.getCode() < 1800) {
			// 1956以上不允许投注
			json.set(2, "2-1141",config.getNotBetPointAccount());
			return false;
		}
		
		if (uEntity.getType() == Global.USER_TYPE_RELATED) {
			// 关联账号不允许投注
			json.set(2, "2-1104");
			return false;
		}

		UserBets userBets = uBetsReadDao.getByIdWithCodes(id, userId);
		if (userBets == null) {
			json.set(2, "2-1028"); // 订单不存在
			return false;
		}
		int code = userBets.getCode();
		if (code > config.getNotBetPoint() || code < 1800) {
			json.set(2, "2-1140",config.getNotBetPoint());
			return false;
		}
		
//		if(code < 1940 && uEntity.getId() != 72 && uEntity.getUpid() != 0){
//			//1940 以下不允许投注
//			json.set(2, "2-1142", 1940);
//			return false;
//		}

		// 验证是否有未发放的分红金额
		if (!uDividendValidate.validateUnIssue(json, uEntity.getId())) {
			json.set(2, "2-1138");
			return false;
		}

		// 验证是否有未发放的日结金额
		if (!uDailySettleValidate.validateUnIssue(json, uEntity.getId())) {
			json.set(2, "2-1139");
			return false;
		}

		// 彩票
		Lottery lottery = dataFactory.getLottery(userBets.getLotteryId());
		if (lottery == null) {
			json.set(2, "2-1101");
			return false;
		}

		// 玩法
		LotteryPlayRules playRules = dataFactory.getLotteryPlayRules(lottery.getId(), userBets.getRuleId());
		if (playRules == null) {
			json.set(2, "2-6");
			return false;
		}
		// 玩法组
		LotteryPlayRulesGroup playRulesGroup = dataFactory.getLotteryPlayRulesGroup(lottery.getId(),
				playRules.getGroupId());
		if (playRulesGroup == null) {
			json.set(2, "2-6");
			return false;
		}
		// 验证模式
		if (!uBetsValidate.validateModel(lottery, userBets.getModel())) {
			String name = playRulesGroup.getName() + "_" + playRules.getName();
			json.set(2, "2-1131", name);
			return false;
		}

		// 验证号码
		boolean isPassValidate = uBetsValidate.validateCode(lottery, playRules, userBets.getCodes());
		if (!isPassValidate) {
			json.set(2, "2-1112");
			return false;
		}

		// 获取号码和注数
		Object[] inputNumber = uBetsValidate.getInputNumber(lottery, playRules, userBets.getCodes());
		String codes = (String) inputNumber[0];
		int nums = (int) inputNumber[1]; // 注数
		if (nums == 0) {
			json.set(2, "2-1112");
			return false;
		}

		// 验证最大注数
		Object[] minMaxNumValidate = uBetsValidate.validateMinMaxNumbers(lottery, playRules, nums, codes);
		if (minMaxNumValidate != null) {
			int errorType = (Integer) minMaxNumValidate[0];
			String name = playRulesGroup.getName() + "_" + playRules.getName();
			String chooseNum = minMaxNumValidate[1].toString();
			String exceedNum = minMaxNumValidate[2].toString();
			String validateType = minMaxNumValidate[3].toString();
			if (errorType == UserBetsValidate.MIN_MAX_EXCEED_MAX) {
				json.set(2, "2-1129", name, "超过最大", chooseNum, validateType, exceedNum, validateType);
				return false;
			}
			if (errorType == UserBetsValidate.MIN_MAX_BELOW_MIN) {
				json.set(2, "2-1129", name, "低于最小", chooseNum, validateType, exceedNum, validateType);
				return false;
			}
		}

		// 验证返点
		Object[] selectPoint = uBetsValidate.getSelectPoint(lottery.getType(), userBets.getModel(), userBets.getCode(),
				uEntity);
		if (!(boolean) selectPoint[0]) {
			json.set(2, "2-6");
			return false;
		}
		code = (int) selectPoint[1];
		double point = (double) selectPoint[2];
		// 验证玩法
		if (!uBetsValidate.isPlayRulesAllow(json, userBets.getLotteryId(), playRules.getId()))
			return false;
		// 验证倍数
		if (!(userBets.getMultiple() > 0 && userBets.getMultiple() <= 10000)) {
			json.set(2, "2-6");
			return false;
		}

		int bUnitMoney = config.getbUnitMoney();
		double mMoney = PrizeUtils.getModel(userBets.getModel());
		if (mMoney <= 0) {
			json.set(2, "2-6");
			return false;
		}
		double thisMoney = nums * userBets.getMultiple() * mMoney * bUnitMoney;
		if (thisMoney <= 0) {
			json.set(2, "2-6");
			return false;
		}
		/*if ("li".equals(userBets.getModel()) || "1li".equals(userBets.getModel())) {
			if (thisMoney < 1) {
				json.set(2, "2-1132", "厘", "1"); // 厘模式必须至少投注1元
				return false;
			}
		}

		if (thisMoney < 0.2) {
			json.set(2, "2-1137", "0.2"); // 单注最低0.2元
			return false;
		}*/

		if (uEntity.getLotteryMoney() < thisMoney) {
			json.set(2, "2-1109");
			return false;
		}

		int type = Global.USER_BETS_TYPE_GENERAL;
		int status = Global.USER_BETS_STATUS_NOT_OPEN;
		String billno = billno();
		UserBets reUserBets;
		if ("jsmmc".equals(lottery.getShortName())) {
			Moment moment = new Moment();
			String time = moment.toSimpleTime();
			String expect = moment.format("yyyyMMddHHmmss");
			reUserBets = new UserBets(billno, uEntity.getId(), type, userBets.getLotteryId(), expect,
					userBets.getRuleId(), codes, nums, userBets.getModel(), userBets.getMultiple(), code, point,
					thisMoney, time, time, time, status, userBets.getCompressed(), ip);
		} else {
			OpenTime oTime = lOpenUtil.getCurrOpenTime(userBets.getLotteryId());
			String except = oTime.getExpect();
			// 验证是否已经有抓取号码了
			if (!uBetsValidate.validateExpect(json, lottery.getShortName(), except)) {
				json.set(2, "2-1128", except);
				return false;
			}
			String thisTime = new Moment().toSimpleTime();
			reUserBets = new UserBets(billno, uEntity.getId(), type, userBets.getLotteryId(), oTime.getExpect(),
					userBets.getRuleId(), codes, nums, userBets.getModel(), userBets.getMultiple(), code, point,
					thisMoney, thisTime, oTime.getStopTime(), oTime.getOpenTime(), status, userBets.getCompressed(),
					ip);
		}

		doOrder(reUserBets);
		return true;
	}

	@Override
	public boolean general(WebJSON json, User uEntity, JSONArray bjson, String ip) {
		LotteryConfig config = dataFactory.getLotteryConfig();
		if (uEntity.getCode() > config.getNotBetPointAccount() || uEntity.getCode() < 1800) {
			// 1956以上不允许投注
			json.set(2, "2-1141",config.getNotBetPointAccount());
			return false;
		}
		
		// 验证是否有未发放的分红金额
		if (!uDividendValidate.validateUnIssue(json, uEntity.getId())) {
			json.set(2, "2-1138");
			return false;
		}

		// 验证是否有未发放的日结金额
		if (!uDailySettleValidate.validateUnIssue(json, uEntity.getId())) {
			json.set(2, "2-1139");
			return false;
		}

		if (uEntity.getType() == Global.USER_TYPE_RELATED) {
			// 关联账号不允许投注
			json.set(2, "2-1104");
			return false;
		}
		if (bjson == null) {
			json.set(2, "2-6");
			return false;
		}
		if (bjson.size() > 20) {
			json.set(2, "2-1133", 20);
			return false;
		}

		List<UserBets> list = new ArrayList<>();
		double totalMoney = 0;
		int lotteryId = 0;
		double maxPrize = 0;
		String except = null;
		for (Object b : bjson) {
			JSONObject o = JSONObject.fromObject(b);
			lotteryId = o.getInt("lotteryId");
			String codes = o.getString("codes");
			int compressed = 0; // 是否已经压缩，1:true；0:false
			String model = o.getString("model");
			int multiple = o.getInt("multiple");
			int ruleId = o.getInt("ruleId");
			int code = o.getInt("code");
//			if(code < 1940 && uEntity.getId() != 72 && uEntity.getUpid() != 0){
//				//1940 以下不允许投注
//				json.set(2, "2-1142", 1940);
//				return false;
//			}
			if (o.containsKey("compressed") && o.getBoolean("compressed")) {
				codes = LZMAJsUtil.decompress(codes); // 解压缩
			}

			if (code > dataFactory.getLotteryConfig().getNotBetPoint() || code < 1800) {
				json.set(2, "2-6");
				return false;
			}

			// 彩票
			Lottery lottery = dataFactory.getLottery(lotteryId);
			if (lottery == null)
				continue;
			// 玩法
			LotteryPlayRules playRules = dataFactory.getLotteryPlayRules(lottery.getId(), ruleId);
			if (playRules == null) {
				json.set(2, "2-6");
				return false;
			}
			// 玩法组
			LotteryPlayRulesGroup playRulesGroup = dataFactory.getLotteryPlayRulesGroup(lottery.getId(),
					playRules.getGroupId());
			if (playRulesGroup == null) {
				json.set(2, "2-6");
				return false;
			}

			if (playRules.getFixed() == 1) {
				code = uEntity.getCode(); // 固定奖金玩法不允许有投注返点
			}
			// 验证模式
			if (!uBetsValidate.validateModel(lottery, model)) {
				String name = playRulesGroup.getName() + "_" + playRules.getName();
				json.set(2, "2-1131", name);
				return false;
			}
			// 验证号码
			boolean isPassValidate = uBetsValidate.validateCode(lottery, playRules, codes);
			if (!isPassValidate) {
				json.set(2, "2-1112");
				return false;
			}
			// 获取号码和注数
			Object[] inputNumber = uBetsValidate.getInputNumber(lottery, playRules, codes);
			codes = (String) inputNumber[0];
			int nums = (int) inputNumber[1]; // 注数
			if (nums <= 0) {
				json.set(2, "2-1112");
				return false;
			}
			// 验证最大注数
			Object[] minMaxNumValidate = uBetsValidate.validateMinMaxNumbers(lottery, playRules, nums, codes);
			if (minMaxNumValidate != null) {
				int errorType = (Integer) minMaxNumValidate[0];
				String name = playRulesGroup.getName() + "_" + playRules.getName();
				String chooseNum = minMaxNumValidate[1].toString();
				String exceedNum = minMaxNumValidate[2].toString();
				String validateType = minMaxNumValidate[3].toString();
				if (errorType == UserBetsValidate.MIN_MAX_EXCEED_MAX) {
					json.set(2, "2-1129", name, "超过最大", chooseNum, validateType, exceedNum, validateType);
					return false;
				}
				if (errorType == UserBetsValidate.MIN_MAX_BELOW_MIN) {
					json.set(2, "2-1129", name, "低于最小", chooseNum, validateType, exceedNum, validateType);
					return false;
				}
			}
			// 验证返点
			Object[] selectPoint = uBetsValidate.getSelectPoint(lottery.getType(), model, code, uEntity);
			if (!(boolean) selectPoint[0])
				continue; // 返点验证失败
			code = (int) selectPoint[1];
			if (code > config.getNotBetPoint() || code < 1800) {
				continue; // 返点验证失败
			}
			double point = (double) selectPoint[2];
			// 验证玩法
			if (!uBetsValidate.isPlayRulesAllow(json, lotteryId, playRules.getId()))
				return false;
			// 验证倍数
			if (!(multiple > 0 && multiple <= 10000))
				continue;
			int bUnitMoney = config.getbUnitMoney();
			double mMoney = PrizeUtils.getModel(model);
			if (mMoney == 0)
				continue; // 如果非法修改，则跳过这条记录
			double thisMoney = nums * multiple * mMoney * bUnitMoney;
			if (thisMoney <= 0)
				continue; // 如果投注金额小于0，则跳过这条记录
			
			/*if ("li".equals(model) || "1li".equals(model)) {
				if (thisMoney < 1) {
					json.set(2, "2-1132", "厘", "1"); // 厘模式必须至少投注1元
					return false;
				}
			}
			if (thisMoney < 0.2) {
				json.set(2, "2-1137", "0.2"); // 单注最低0.2元
				return false;
			}*/
			
			int type = Global.USER_BETS_TYPE_GENERAL;
			int status = Global.USER_BETS_STATUS_NOT_OPEN;
			String billno = billno();
			if (lotteryId == 117) {
				Moment moment = new Moment();
				String time = moment.toSimpleTime();
				String expect = moment.format("yyyyMMddHHmmss");
				list.add(new UserBets(billno, uEntity.getId(), type, lotteryId, expect, ruleId, codes, nums, model,
						multiple, code, point, thisMoney, time, time, time, status, compressed, ip));
			} else {
				OpenTime oTime = lOpenUtil.getCurrOpenTime(lotteryId);
				except = oTime.getExpect();
				// 验证是否已经有抓取号码了
				if (!uBetsValidate.validateExpect(json, lottery.getShortName(), except))
					return false;
				String thisTime = new Moment().toSimpleTime();
				list.add(new UserBets(billno, uEntity.getId(), type, lotteryId, oTime.getExpect(), ruleId, codes, nums,
						model, multiple, code, point, thisMoney, thisTime, oTime.getStopTime(), oTime.getOpenTime(),
						status, compressed, ip));
			}
			totalMoney += thisMoney;
			// 计算奖金总额
			double maxUnitPrize = PrizeUtils.getMoney(playRules, model, bUnitMoney, code);
			maxPrize += (maxUnitPrize * multiple);
		}
		if (list.size() > 0) {

			if (uEntity.getLotteryMoney() >= totalMoney) {
				for (UserBets tmpBean : list) {
					doOrder(tmpBean);
				}
			} else {
				json.set(2, "2-1109");
				return false;
			}

			// 增加奖金限额验证
			// boolean outOfMaxPrize =
			// uBetsValidate.validateMaxPrizeOneExpect(uEntity.getId(),
			// lotteryId, maxPrize, except);
			// if(!outOfMaxPrize){
			// json.set(2, "2-1126");
			// return false;
			// }
			// if(uEntity.getLotteryMoney() >= totalMoney) {
			// for (UserBets tmpBean : list) {
			// doOrder(tmpBean);
			// }
			// betsLimitService.setMaxPrizeOneExcept(uEntity.getId(), lotteryId,
			// except, maxPrize, false);
			// } else {
			// json.set(2, "2-1109");
			// return false;
			// }
		} else {
			json.set(2, "2-1110");
			return false;
		}
		return true;
	}

	@Override
	public boolean chase(WebJSON json, User uEntity, int lotteryId, JSONArray bjson, JSONArray cjson, String isStop,
			String ip) {
		LotteryConfig config = dataFactory.getLotteryConfig();
		if (uEntity.getCode() > config.getNotBetPointAccount() || uEntity.getCode() < 1800) {
			// 1956以上不允许投注
			json.set(2, "2-1141",config.getNotBetPointAccount());
			return false;
		}
		
		if (uEntity.getType() == Global.USER_TYPE_RELATED) {
			// 关联账号不允许投注
			json.set(2, "2-1104");
			return false;
		}
		if (cjson == null) {
			json.set(2, "2-6");
			return false;
		}
		if (cjson.size() > 100) {
			json.set(2, "2-1134", 100);
			return false;
		}

		// 验证是否有未发放的分红金额
		if (!uDividendValidate.validateUnIssue(json, uEntity.getId())) {
			json.set(2, "2-1138");
			return false;
		}

		// 验证是否有未发放的日结金额
		if (!uDailySettleValidate.validateUnIssue(json, uEntity.getId())) {
			json.set(2, "2-1139");
			return false;
		}

		List<UserBets> list = new ArrayList<>();
		// 彩票
		Lottery lottery = dataFactory.getLottery(lotteryId);
		for (Object b : bjson) {
			JSONObject o = JSONObject.fromObject(b);
			String codes = o.getString("codes");
			int compressed = 0;
			String model = o.getString("model");
			int ruleId = o.getInt("ruleId");
			int code = o.getInt("code");
//			if(code < 1940 && uEntity.getId() != 72 && uEntity.getUpid() != 0){
//				//1940 以下不允许投注
//				json.set(2, "2-1142", 1940);
//				return false;
//			}
			if (o.containsKey("compressed") && o.getBoolean("compressed")) {
				codes = LZMAJsUtil.decompress(codes); // 解压缩
			}
			if (lottery == null)
				continue;

			if (code > config.getNotBetPoint() || code < 1800) {
				json.set(2, "2-6");
				return false;
			}

			// 玩法
			// 玩法
			LotteryPlayRules playRules = dataFactory.getLotteryPlayRules(lottery.getId(), ruleId);
			if (playRules == null) {
				json.set(2, "2-6");
				return false;
			}
			// 玩法组
			LotteryPlayRulesGroup playRulesGroup = dataFactory.getLotteryPlayRulesGroup(lottery.getId(),
					playRules.getGroupId());
			if (playRulesGroup == null) {
				json.set(2, "2-6");
				return false;
			}
			if (playRules.getFixed() == 1) {
				code = uEntity.getCode(); // 固定奖金玩法不允许有投注返点
			}
			// 验证模式
			if (!uBetsValidate.validateModel(lottery, model)) {
				String name = playRulesGroup.getName() + "_" + playRules.getName();
				json.set(2, "2-1131", name);
				return false;
			}
			// 验证号码
			boolean isPassValidate = uBetsValidate.validateCode(lottery, playRules, codes);
			if (!isPassValidate) {
				json.set(2, "2-1112");
				return false;
			}
			// 获取号码和注数
			Object[] inputNumber = uBetsValidate.getInputNumber(lottery, playRules, codes);
			codes = (String) inputNumber[0];
			int nums = (int) inputNumber[1];
			if (nums == 0)
				continue; // 如果这个注数为0，则跳过这条记录
			// 验证返点
			Object[] selectPoint = uBetsValidate.getSelectPoint(lottery.getType(), model, code, uEntity);
			if (!(boolean) selectPoint[0])
				continue; // 返点验证失败
			code = (int) selectPoint[1];
			if (code > config.getNotBetPoint() || code < 1800) {
				continue; // 返点验证失败
			}
			double point = (double) selectPoint[2];
			// 验证玩法
			if (!uBetsValidate.isPlayRulesAllow(json, lotteryId, ruleId))
				return false;
			int bUnitMoney = config.getbUnitMoney();
			double mMoney = PrizeUtils.getModel(model);
			if (mMoney == 0)
				continue; // 如果非法修改，则跳过这条记录
			double thisMoney = nums * mMoney * bUnitMoney; // 1倍投注金额
			if (thisMoney <= 0)
				continue; // 如果投注金额小于0，则跳过这条记录
			int type = Global.USER_BETS_TYPE_CHASE;
			int status = Global.USER_BETS_STATUS_NOT_OPEN;
			String thisTime = new Moment().toSimpleTime();
			// 追号
			String chaseBillno = chaseBillno();
			int chaseStop = "true".equals(isStop) ? 1 : 0;
			UserBets tmpBean = new UserBets(null, uEntity.getId(), type, lotteryId, null, ruleId, codes, nums, model, 0,
					code, point, thisMoney, thisTime, null, null, status, compressed, ip);
			tmpBean.setChaseBillno(chaseBillno);
			tmpBean.setChaseStop(chaseStop);
			list.add(tmpBean);
		}
		if (list.size() > 0) {
			List<UserBets> blist = new ArrayList<>();
			double totalMoney = 0;
			for (Object c : cjson) {
				JSONObject o = JSONObject.fromObject(c);
				String expect = o.getString("expect");
				// 验证是否已经有抓取号码了
				if (!uBetsValidate.validateExpect(json, lottery.getShortName(), expect))
					return false;
				int multiple = o.getInt("multiple");
				// 验证倍数
				if (!(multiple > 0 && multiple <= 10000))
					continue;
				OpenTime oTime = lOpenUtil.getOpentime(lotteryId, expect);
				Moment stopTime = new Moment().fromTime(oTime.getStopTime());
				Moment openTime = new Moment().fromTime(oTime.getOpenTime());
				Moment thisTime = new Moment();
				if (thisTime.ge(stopTime))
					continue; // 如果当前时间大于结束时间，则已过销售期
				for (UserBets bBean : list) {
					UserBets tmpBean = bBean.clone();
					String billno = billno();
					double thisMoney = tmpBean.getMoney() * multiple;
					/*if ("li".equals(tmpBean.getModel()) || "1li".equals(tmpBean.getModel())) {
						if (thisMoney < 1) {
							json.set(2, "2-1132", "厘", "1"); // 厘模式必须至少投注1元
							return false;
						}
					}
					if (thisMoney < 0.2) {
						json.set(2, "2-1137", "0.2"); // 单注最低0.2元
						return false;
					}*/
					
					tmpBean.setBillno(billno);
					tmpBean.setExpect(expect);
					tmpBean.setMultiple(multiple);
					tmpBean.setMoney(thisMoney);
					tmpBean.setStopTime(stopTime.toSimpleTime());
					tmpBean.setOpenTime(openTime.toSimpleTime());
					blist.add(tmpBean);
					// 计算最大奖金
					// LotteryPlayRules lotteryPlayRules =
					// dataFactory.getLotteryPlayRules(lottery.getType(),
					// bBean.getMethod());
					// double maxUnitPrize =
					// PrizeUtils.getMoney(lotteryPlayRules, bBean.getModel(),
					// config.getbUnitMoney(), bBean.getCode());
					// boolean outOfMaxPrize =
					// uBetsValidate.validateMaxPrizeOneExpect(uEntity.getId(),
					// lotteryId, maxUnitPrize * multiple, expect);
					// if(!outOfMaxPrize){
					// json.set(2, "2-1127", expect);
					// return false;
					// }
					totalMoney += thisMoney;
				}
			}
			if (uEntity.getLotteryMoney() >= totalMoney) {
				for (UserBets tmpBean : blist) {
					doOrder(tmpBean);
					// //设置奖金金额到redis里面
					// addPrize2Cache(uEntity, lotteryId, config, tmpBean);
				}
			} else {
				json.set(2, "2-1109");
				return false;
			}
		} else {
			json.set(2, "2-1110");
			return false;
		}
		return true;
	}

	// private void addPrize2Cache(User uEntity, int lotteryId, LotteryConfig
	// config, UserBets tmpBean) {
	// Lottery lottery = dataFactory.getLottery(lotteryId);
	// LotteryPlayRules lotteryPlayRules =
	// dataFactory.getLotteryPlayRules(lottery.getType(), tmpBean.getMethod());
	// double maxUnitPrize = PrizeUtils.getMoney(lotteryPlayRules,
	// tmpBean.getModel(), config.getbUnitMoney(), tmpBean.getCode());
	// betsLimitService.setMaxPrizeOneExcept(uEntity.getId(), lotteryId,
	// tmpBean.getExpect(), maxUnitPrize * tmpBean.getMultiple(), true);
	// }

	@Override
	public boolean cancelGeneral(int id, int userId, User sessionUser) {
		UserBets bean = uBetsReadDao.getByIdWithoutCodes(id, userId);
		if (bean != null) {
			boolean result = doCancelOrder(bean, sessionUser);
			// if(result){
			// betsLimitService.deleteLimitAfterCancelOder(bean);
			// }
			return result;
		}
		return false;
	}

	// 批量撤销追号订单由于事物一致，需要用session中的user 否则账单记录金额有问题
	@Override
	public boolean cancelChase(String chaseBillno, int userId, User sessionUser) {
		List<UserBets> list = uBetsDao.getByChaseBillno(chaseBillno, userId);
		if (null == list || list.isEmpty()) {
			return false;
		}
		for (UserBets bBean : list) {
			if (bBean.getStatus() == 0) {
				doCancelOrder(bBean, sessionUser);
			}
		}
		return true;
	}
}