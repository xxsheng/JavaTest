package lottery.domains.utils.prize;

import java.math.BigDecimal;

import javautils.math.MathUtil;
import lottery.domains.content.entity.LotteryPlayRules;

public class PrizeUtils {
	
	public static double getModel(String model) {
		if("yuan".equals(model)) return 1;
		if("jiao".equals(model)) return 0.1;
		if("fen".equals(model)) return 0.01;
		if("li".equals(model)) return 0.001;
		if("1yuan".equals(model)) return 0.5;
		if("1jiao".equals(model)) return 0.05;
		if("1fen".equals(model)) return 0.005;
		if("1li".equals(model)) return 0.0005;
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
		if(re.getFixed() == 1) {
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
	
	public static void main(String[] args) {
		double d = (1950 / 54.00) * (2.0 / 2) * 1.0;
		System.out.println( Math.round(d * 1000) * 0.001d);
	}
}