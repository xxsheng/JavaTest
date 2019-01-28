package lottery.web.content.validate;

import javautils.math.MathUtil;
import lottery.domains.content.biz.LotteryOpenCodeService;
import lottery.domains.content.biz.UserBetsLimitService;
import lottery.domains.content.entity.*;
import lottery.domains.content.vo.config.CodeConfig;
import lottery.domains.content.vo.config.LotteryConfig;
import lottery.domains.pool.DataFactory;
import lottery.domains.utils.validate.*;
import lottery.web.WebJSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class UserBetsValidate {
	public static final int MIN_MAX_BELOW_MIN = 1;
	public static final int MIN_MAX_EXCEED_MAX = 2;

	@Autowired
	private DataFactory dataFactory;

	@Autowired
	private UserBetsLimitService userBetsLimitService;

	@Autowired
	private LotteryOpenCodeService lotteryOpenCodeService;

	/**
	 * 验证彩票
	 */
	public boolean isLotteryAllow(WebJSON json, int lotteryId) {
		Lottery lottery = dataFactory.getLottery(lotteryId);
		if(lottery == null) return false;
		if(lottery.getStatus() == -1) {
			json.set(2, "2-1101");
			return false;
		}
		LotteryType lotteryType = dataFactory.getLotteryType(lottery.getType());
		if(lotteryType == null) return false;
		if(lotteryType.getStatus() == -1) {
			json.set(2, "2-1102", lottery.getShowName());
			return false;
		}
		return true;
	}

	/**
	 * 验证玩法、玩法组是否可用
	 */
	public boolean isPlayRulesAllow(WebJSON json, int lotteryId, int ruleId) {
		boolean isLotteryAllow = isLotteryAllow(json, lotteryId);
		if(isLotteryAllow) {
			LotteryPlayRules lotteryPlayRules = dataFactory.getLotteryPlayRules(lotteryId, ruleId);
			if(lotteryPlayRules == null) return false;
			LotteryPlayRulesGroup lotteryPlayRulesGroup = dataFactory.getLotteryPlayRulesGroup(lotteryId, lotteryPlayRules.getGroupId());
			if(lotteryPlayRulesGroup == null) return false;
//			String playRules = lotteryPlayRulesGroup.getName() + "_" + lotteryPlayRules.getName();
			if(lotteryPlayRules.getStatus() == -1) {
				// 玩法禁用
				json.set(2, "2-1106");
				return false;
			}
			if(lotteryPlayRulesGroup.getStatus() == -1) {
				// 玩法组禁用
				json.set(2, "2-1106");
				return false;
			}
//			if (lotteryPlayRules.getMinRecord() != 0 && nums < lotteryPlayRules.getMinRecord()) {
//				json.set(2, "2-1107", lottery.getShowName(), playRules, lotteryPlayRules.getMinRecord());
//				return false;
//			}
//			if (lotteryPlayRules.getMaxRecord() != 0 && nums > lotteryPlayRules.getMaxRecord()) {
//				json.set(2, "2-1108", lottery.getShowName(), playRules, lotteryPlayRules.getMaxRecord());
//				return false;
//			}

			return true;
		}
		return isLotteryAllow;
	}

	/**
	 * 验证用户
	 */
	public boolean isUserAllow(WebJSON json, User uEntity, HttpSession session) {
		if(uEntity.getBStatus() == -1) {
			json.set(2, "2-1104");
			return false;
		}
		if(uEntity.getBStatus() == -2) {
			session.invalidate();
			return false;
		}
		if(uEntity.getBStatus() == -3) {
			json.set(2, "2-1105");
			return false;
		}
		return true;
	}

	/**
	 * 获取号码和注数
	 */
	public Object[] getInputNumber(Lottery lottery, LotteryPlayRules playRules, String codes) {
		switch (lottery.getType()) {
			case 1:
				return LotteryUtilSSC.inputNumber(playRules.getCode(), codes);
			case 2:
				return LotteryUtil11X5.inputNumber(playRules.getCode(), codes);
			case 3:
				return LotteryUtilK3.inputNumber(playRules.getCode(), codes);
			case 4:
				return LotteryUtil3D.inputNumber(playRules.getCode(), codes);
			case 5:
				return LotteryUtilKL8.inputNumber(playRules.getCode(), codes);
			case 6:
				return LotteryUtilPK10.inputNumber(playRules.getCode(), codes);
			case 7:
				return LotteryUtilLHD.inputNumber(playRules.getCode(), codes);
			default:
				return new Object[] { null, 0 };
		}
	}

	/**
	 * 验证号码
	 */
	public boolean validateCode(Lottery lottery, LotteryPlayRules playRules, String codes) {
		if (StringUtils.isEmpty(codes)) {
			return false;
		}

		if (codes.startsWith(" ") || codes.endsWith(" ")) {
			return false;
		}

		switch (lottery.getType()) {
			case 1:
				return LotteryUtilSSC.inputValidate(playRules.getCode(), codes);
			case 2:
				return LotteryUtil11X5.inputValidate(playRules.getCode(), codes);
			case 3:
				return LotteryUtilK3.inputValidate(playRules.getCode(), codes);
			case 4:
				return LotteryUtil3D.inputValidate(playRules.getCode(), codes);
			case 5:
				return LotteryUtilKL8.inputValidate(playRules.getCode(), codes);
			case 6:
				return LotteryUtilPK10.inputValidate(playRules.getCode(), codes);
			case 7:
				return LotteryUtilLHD.inputValidate(playRules.getCode(), codes);
			default:
				return true;
		}
	}

	/**
	 * 验证最大最小投注注数，返回空则通过
	 * 数组第一位：1：小于最小投注；2：超过最大投注
	 * 数组第二位：最大或最小注数
	 * 数组第三位：实际投注数
	 * 数组第四位：注或码
	 */
	public Object[] validateMinMaxNumbers(Lottery lottery, LotteryPlayRules playRules, int num, String codes) {
		if ("0".equals(playRules.getMaxNum()) && "0".equals(playRules.getMinNum())) {
			return null;
		}

		switch (lottery.getType()) {
			case 1:
				return LotteryUtilSSC.validateMinMaxNumbers(playRules, num, codes);
			case 2:
				return LotteryUtil11X5.validateMinMaxNumbers(playRules, num, codes);
			case 3:
				return LotteryUtilK3.validateMinMaxNumbers(playRules, num, codes);
			case 4:
				return LotteryUtil3D.validateMinMaxNumbers(playRules, num, codes);
			case 5:
				return LotteryUtilKL8.validateMinMaxNumbers(playRules, num, codes);
			case 6:
				return LotteryUtilPK10.validateMinMaxNumbers(playRules, num, codes);
			case 7:
				return LotteryUtilLHD.validateMinMaxNumbers(playRules, num, codes);
			default:
				return null;
		}
	}

	public static boolean validateModel(Lottery lottery, String model) {
		if (StringUtils.isEmpty(lottery.getAllowModels())) {
			return false;
		}
		String[] allowModels = lottery.getAllowModels().split(",");
		for (String allowModel : allowModels) {
			if (allowModel.equals(model)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 获取能输入的最大
	 */
	public Object[] getMaxPoint(int lotteryType, String model, User uEntity) {
		CodeConfig config = dataFactory.getCodeConfig();
		LotteryConfig lConfig = dataFactory.getLotteryConfig();
		final int uCode = uEntity.getCode();
		final double uLocPoint = uEntity.getLocatePoint();
		// 允许输入的范围
		int inputMaxCode = uCode;
		int inputMinCode = inputMaxCode - (int) (uLocPoint * 20);
		// 验证模式
		switch (lotteryType) {
			case 1: // 时时彩
			case 7: // 龙虎斗
			case 2: // 十一选五
			case 6: // PK拾
			case 5: // 快乐8
				if("fen".equals(model)) {
					if(lConfig.getFenModelDownCode() > 0) {
						int thisCode = config.getSysCode() - lConfig.getFenModelDownCode();
						if(uCode > thisCode) {
							inputMaxCode = thisCode;
						}
					}
				}
				if("li".equals(model)) {
					if(lConfig.getLiModelDownCode() > 0) {
						int thisCode = config.getSysCode() - lConfig.getLiModelDownCode();
						if(uCode > thisCode) {
							inputMaxCode = thisCode;
						}
					}
				}
				break;
			case 4: // 福彩3D
			case 3: // 快三
				int lotteryDownCode = 40;
				if(3 == lotteryType){
					lotteryDownCode = 60;
				}
//				int lotteryDownCode = 0;
				inputMinCode = inputMinCode - lotteryDownCode;
				if("fen".equals(model) || "li".equals(model)) {
					if("fen".equals(model)) {
						if(lConfig.getFenModelDownCode() > 0) {
							int thisCode = config.getSysCode() - lConfig.getFenModelDownCode();
							if(uCode > thisCode) {
								inputMaxCode = thisCode - lotteryDownCode;
							}
						}
					}
					if("li".equals(model)) {
						if(lConfig.getLiModelDownCode() > 0) {
							int thisCode = config.getSysCode() - lConfig.getLiModelDownCode();
							if(uCode > thisCode) {
								inputMaxCode = thisCode - lotteryDownCode;
							}
						}
					}
				} else {
					inputMaxCode = uCode -lotteryDownCode;
				}
				break;
			default:
				return new Object[] { false };
		}
		double thisLp = uLocPoint - MathUtil.doubleFormat((inputMaxCode - inputMinCode) / 20.0, 1);
		return new Object[] { true, inputMaxCode, thisLp };
	}

	/**
	 * 验证用户返点是否在配置系统的范围内
	 */
	public Object[] getSelectPoint(int lotteryType, String model, int inputCode, User uEntity) {
		CodeConfig config = dataFactory.getCodeConfig(); // 系统返点配置
		LotteryConfig lConfig = dataFactory.getLotteryConfig(); // 彩票配置
		final int uCode = uEntity.getCode(); // 用户返点
		final double uLocPoint = uEntity.getLocatePoint(); // 用户返点数值
		// 允许输入的范围
		int inputMaxCode = uCode; // 最高返点
		int inputMinCode = inputMaxCode - (int) (uLocPoint * 20); // 最低返点一般为1800
		// 验证模式
		switch (lotteryType) {
			case 1: // 时时彩
			case 7: // 龙虎斗
			case 2: // 十一选五
			case 6: // PK拾
			case 5: // 快乐8
				// 分模式
				if("fen".equals(model)) {
					if(lConfig.getFenModelDownCode() > 0) {
						// 当前最高返点=系统配置最高账号级别-彩票分模式降点，如：1960-10
						int thisCode = config.getSysCode() - lConfig.getFenModelDownCode();
						// 如果用户的返点大于系统配置，那么取系统最高返点
						if(uCode > thisCode) {
							inputMaxCode = thisCode;
						}
					}
				}
				// 厘模式
				if("li".equals(model)) {
					if(lConfig.getLiModelDownCode() > 0) {
						int thisCode = config.getSysCode() - lConfig.getLiModelDownCode();
						// 默认将该投注返点设置到  分模式最高返点
						if(uCode > thisCode) {
							inputMaxCode = thisCode;
						}
					}
				}
				break;
			case 4: // 福彩3D
			case 3: // 快三
				int lotteryDownCode = 40; // 默认降点40
//				int lotteryDownCode = 0;
//				inputMinCode = inputMinCode - lotteryDownCode;
				if("fen".equals(model) || "li".equals(model)) {
					// 代码与上面一致，看上面的注释即可
					if("fen".equals(model)) {
						if(lConfig.getFenModelDownCode() > 0) {
							int thisCode = config.getSysCode() - lConfig.getFenModelDownCode();
							if(uCode > thisCode) {
								inputMaxCode = thisCode - lotteryDownCode;
							}
						}
					}
					if("li".equals(model)) {
						if(lConfig.getLiModelDownCode() > 0) {
							int thisCode = config.getSysCode() - lConfig.getLiModelDownCode();
							if(uCode > thisCode) {
								inputMaxCode = thisCode - lotteryDownCode;
							}
						}
					}
				} else {
					inputMaxCode = config.getSysCode() - lotteryDownCode;
				}
				break;
			default:
				return new Object[] { false };
		}
		
		if(uEntity.getId() != 72 && uEntity.getUpid() == 0 && uEntity.getType() == 2){
			inputMaxCode = 1800;
		}
		
		// 判断是否正确的范围
		if(inputCode >= inputMinCode && inputCode <= inputMaxCode) {
			double thisLp = uLocPoint - MathUtil.doubleFormat((inputCode - inputMinCode) / 20.0, 1);
			return new Object[] { true, inputCode, thisLp };
		}
		return new Object[] { false };
	}

	/**
	 * 验证某一期，最大奖金是否超过限额
	 */
	public boolean validateMaxPrizeOneExpect(int userId, int lotteryId, double currentMaxPrize, String expect){
		UserBetsLimit userBetsLimit = userBetsLimitService.get(userId, lotteryId);
		if(userBetsLimit == null){
			return true;
		}else {
			double prizeOneExcept = userBetsLimitService.getMaxPrizeOneExcept(userId, lotteryId, expect, currentMaxPrize);
			//查询彩种所有用户限额配置
			UserBetsLimit allUserMaxPrizeLimit = userBetsLimitService.get(0, lotteryId);
			double allUserPrizeThisExecpt =  userBetsLimitService.getMaxPrizeOneExcept(lotteryId, expect);
			allUserPrizeThisExecpt += currentMaxPrize;

			if(allUserMaxPrizeLimit != null){
				if(prizeOneExcept <= userBetsLimit.getMaxBet() && allUserPrizeThisExecpt <= allUserMaxPrizeLimit.getMaxPrize()){
					return true;
				}
			} else {
				if(prizeOneExcept <= userBetsLimit.getMaxBet()){
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * 验证期号是否已经有开奖号码了
	 */
	public boolean validateExpect(WebJSON json, String lottery, String expect) {
		LotteryOpenCode openCode = lotteryOpenCodeService.getByExcept(lottery, expect);
		if (openCode != null && StringUtils.isNotEmpty(openCode.getCode())) {
			json.set(2, "2-1128", expect);
			return false;
		}
		return true;
	}
}