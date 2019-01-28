package lottery.domains.pool.payment.cfg;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyFormat {
	
	/**
	 * 通汇支付金额格式
	 * @param money
	 */
	public static String FormatPay41(String money){
		return moneyToYuanForPositive(money);
	}
	
	/**
	 * 智付金额格式
	 * @param money
	 * @return
	 */
	public static String fonmatDinpay(String money){
		return moneyToYuanForPositive(money);
	}
	
	/**
	 * 摩宝金额格式
	 * @param money
	 * @return
	 */
	public static String fonmatMobao(String money){
		return moneyToYuanForPositive(money);
	}
	
	/**
	 * 环商通支付金额格式
	 * @param money
	 */
	public static String FormatHuanst(String money){
		return moneyToYuanForPositive(money);
	}
	
	/**
	 * 雅虎支付金额格式
	 * @param money
	 * @return
	 */
	public static String FormatIfbao(String money){
		return moneyToYuanForPositive(money);
	}

	/**
	 * 将金额转行为“元”格式
	 * @param money
	 * @return
	 */
	public static String moneyToYuanForPositive(String money) {
		if (money == null) {
			return "0.00";
		}
		if(money.contains(".")){
			String fist = money.substring(0,money.indexOf("."));
			String cosp = money.substring(money.indexOf(".")+1,money.length());
			if(cosp.length() >=2){
				StringBuffer bf = new StringBuffer(fist);
				String mt = cosp.substring(0, 2);
				return bf.append(".").append(mt).toString();
			}else{
				StringBuffer bf = new StringBuffer(fist);
				if(cosp.length() == 1){
					bf.append(".").append(cosp).append("0");
				}
				if(cosp.length() == 0){
					bf.append(".").append(cosp).append("00");
				}
				return bf.toString();
			}
		}else{
			StringBuffer bf = new StringBuffer(money);
			bf.append(".00");
			return bf.toString();
		}
	}


	/**
	 * 将金额转换成分格式
	 * @param yuan
	 * @return
	 */
	public static long yuanToFenMoney(String yuan) {
		if (yuan == null || yuan.length() <= 0) {
			return 0;
		}
		try {
			int pIdx = yuan.indexOf(".");
			int len = yuan.length();
			String fixed = yuan.replaceAll("\\.", "");
			if (pIdx < 0 || pIdx == len - 1) {
				return Long.valueOf(fixed + "00");
			} else if (pIdx == len - 2) {
				return Long.valueOf(fixed + "0");
			}else {
				return Long.valueOf(fixed.substring(0, pIdx + 2));
			}
		} catch (Exception e) {
			return 0;
		}
	}

	
	public static String pasMoney(Double money){
		DecimalFormat df = new DecimalFormat("#########0.00");
		return df.format(money);
	}

	/**
	 * 转换成double类型的整数
	 * @param value
	 * @return
	 */
	public static double toDigitDouble(double value) {
		BigDecimal bigDecimal = BigDecimal.valueOf(value);
		return Double.valueOf(bigDecimal.intValue());
	}
	
	public static void main(String[] args) {
		// System.out.println(Double.parseDouble("112")/100);
		// System.out.println(Double.valueOf("1.0").intValue());
//		System.out.println(pasMoney(1.0));
//
//		double money1 = 100.00;
//		double money2 = 99.49;
//		String s1 = MoneyFormat.fonmatDinpay(String.valueOf(money1));
//		String s2 = MoneyFormat.fonmatDinpay(String.valueOf(money2));
//
//		System.out.println(s1.equals(s2));
//		System.out.println(money2+0.51 >= money1);

		double money1 = 100.00;

		System.out.println(BigDecimal.valueOf(money1).toString());
		System.out.println(yuanToFenMoney(money1+""));
	}
}
