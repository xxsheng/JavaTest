package lottery.domains.utils.prize;

import javautils.math.MathUtil;
import lottery.domains.content.entity.LotteryPlayRules;

import java.math.BigDecimal;

public class PrizeUtils {
	
	public static double getModel(String model) {
		if("yuan".equals(model)) return 1; // 2元
		if("1yuan".equals(model)) return 0.5; // 1元
		if("jiao".equals(model)) return 0.1; // 2角
		if("1jiao".equals(model)) return 0.05; // 1角
		if("fen".equals(model)) return 0.01; // 2分
		if("1fen".equals(model)) return 0.005; // 1分
		if("li".equals(model)) return 0.001; // 2厘
		if("1li".equals(model)) return 0.0005; // 1 厘
		return 0;
	}
	
	public static double getMoney(LotteryPlayRules rule, String model, double bUnitMoney, int code) {
		double mMoney = getModel(model);
		double maxPrize = 0;
		String[] ps = rule.getPrize().split(",");
		for (int i = 0, j = ps.length; i < j; i++) {
			if(rule.getFixed() == 0) {
				double pm = (code / Double.parseDouble(ps[i])) * (bUnitMoney / 2) * mMoney;
				if(pm > maxPrize) {
					BigDecimal bd = new BigDecimal(pm);
					maxPrize = MathUtil.decimalFormat(bd, 10);
				}
			}
			if(rule.getFixed() == 1) {
				double pm = Double.parseDouble(ps[i]) * (bUnitMoney / 2) * mMoney;
				if(pm > maxPrize) {
					BigDecimal bd = new BigDecimal(pm);
					maxPrize = MathUtil.decimalFormat(bd, 10);
				}
			}
		}
		return maxPrize;
	}
	
	/**
	 * 普通玩法奖金计算
	 * @param rule
	 * @param model
	 * @param bUnitMoney
	 * @param code
	 * @param multiple
	 * @param winCode
	 * @param WinNum
	 * @return
	 */
	public static double getBetWinMoney(LotteryPlayRules  re,  String model,
			double bUnitMoney,int code){
		double mMoney = getModel(model);
		double maxPrize = 0;
		if(re.getFixed() == 0) {
			double pm = (code / Double.parseDouble(re.getPrize())) * (bUnitMoney / 2) * mMoney;
			if(pm > maxPrize) {
				BigDecimal bd = new BigDecimal(pm);
				maxPrize = MathUtil.decimalFormat(bd, 10);
			}
		}
		else if(re.getFixed() == 1) {
			double pm =  Double.parseDouble(re.getPrize()) * (bUnitMoney / 2) * mMoney;
			if(pm > maxPrize) {
				BigDecimal bd = new BigDecimal(pm);
				maxPrize = MathUtil.decimalFormat(bd, 10);
			}
		}
		return maxPrize;
	}
	
	
	/**
	 * 根据中奖号码计算中奖奖金
	 * @param rule  奖金配置信息
	 * @param model 模式(圆、角、分)
	 * @param bUnitMoney 单位
	 * @param code 返点号(1950......)
	 * @param multiple 倍数
	 * @param winCode 中奖号码 
	 * @param WinNum 中奖注数
	 * @return
	 */
	public static double getWinMoneyByCode(int fixed, String model, 
			double bUnitMoney, int code,double prizeres) {
		double mMoney = getModel(model);
		double maxPrize = 0;
		if(fixed == 0) {
			double pm = (code / prizeres) * (bUnitMoney / 2) * mMoney;
			if(pm > maxPrize) {
				BigDecimal bd = new BigDecimal(pm);
				maxPrize = MathUtil.decimalFormat(bd, 10);
			}
		}
		if(fixed == 1) {
			double pm = prizeres * (bUnitMoney / 2) * mMoney;
			if(pm > maxPrize) {
				BigDecimal bd = new BigDecimal(pm);
				maxPrize = MathUtil.decimalFormat(bd, 10);
			}
		}
		return maxPrize;
	}

	/**
	 * 根据赔率计算单注单倍多少钱
	 */
	public static double getPrize(int fixed, String model, double bUnitMoney, int code, double odds) {
		double mMoney = getModel(model);
		double maxPrize = 0;
		if (fixed == 0) {
			double pm = (code / odds) * (bUnitMoney / 2) * mMoney;
			BigDecimal bd = new BigDecimal(pm);
			maxPrize = MathUtil.decimalFormat(bd, 10);
		}
		else if (fixed == 1) {
			double pm = odds * (bUnitMoney / 2) * mMoney;
			BigDecimal bd = new BigDecimal(pm);
			maxPrize = MathUtil.decimalFormat(bd, 10);
		}

		return maxPrize;
	}

	public static void main(String[] args) {
		// double d = (1950 / 54.00) * (2.0 / 2) * 1.0;
		// System.out.println( Math.round(d * 1000) * 0.001d);

		System.out.println(getPrize(0, "yuan", 2, 1946, 444.4953));
	}
}