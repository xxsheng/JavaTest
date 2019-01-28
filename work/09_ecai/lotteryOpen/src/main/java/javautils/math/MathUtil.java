package javautils.math;

import java.math.BigDecimal;
import java.text.DecimalFormat;


public class MathUtil {

	public static boolean inRound(double value, double min, double max) {
		return value >= min && value <= max;
	}

	/**
	 * double 相加 +
	 * @param a
	 * @param b
	 * @return
	 */
	public static double add(double a, double b) {
		BigDecimal b1 = new BigDecimal(Double.toString(a));
		BigDecimal b2 = new BigDecimal(Double.toString(b));
		return b1.add(b2).doubleValue();
	}
	
	/**
	 * double 相减  -
	 * @param a
	 * @param b
	 * @return
	 */
	public static double subtract(double a, double b) {
		BigDecimal b1 = new BigDecimal(Double.toString(a));
		BigDecimal b2 = new BigDecimal(Double.toString(b));
		return b1.subtract(b2).doubleValue();
	}
	
	/**
	 * double 相乘 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static double multiply(double a, double b) {
		BigDecimal b1 = new BigDecimal(Double.toString(a));
		BigDecimal b2 = new BigDecimal(Double.toString(b));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * double 相除 /
	 * @param v1
	 * @param v2
	 * @param scale
	 * @return
	 */
	public static double divide(double v1, double v2, int scale) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_DOWN).doubleValue();
	}

	public static void main(String[] args) {
		System.out.println(divide(1.2134,2312.23456,6));
		System.out.println(add(12.9, 0.101));
		
		Double d = new Double(8.747470732E7);
		DecimalFormat format = new DecimalFormat("##.0000");
		System.out.println(format.format(d));
	}

	public static double decimalFormat(BigDecimal bd, int point) {
		return bd.setScale(point, BigDecimal.ROUND_HALF_DOWN).doubleValue();
	}

	public static double doubleFormat(double d, int point) {
		try {
			BigDecimal bd = new BigDecimal(d);
			return bd.setScale(point, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}
	
	public static double StringFormat(String d, int point) {
		try {
			BigDecimal bd = new BigDecimal(d);
			return bd.setScale(point, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.00;
	}

	public static float floatFormat(float f, int point) {
		try {
			BigDecimal bd = new BigDecimal(f);
			return bd.setScale(point, BigDecimal.ROUND_HALF_DOWN).floatValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
}
