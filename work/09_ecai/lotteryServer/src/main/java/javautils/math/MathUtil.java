package javautils.math;

import java.math.BigDecimal;
import java.text.DecimalFormat;


public class MathUtil {

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
	 * double 相乘 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static double multiply(double a, double b, int scale) {
		BigDecimal b1 = new BigDecimal(Double.toString(a));
		BigDecimal b2 = new BigDecimal(Double.toString(b));
		return decimalFormat(b1.multiply(b2), scale);
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

	public static double doubleFormat(double d, int point, int model) {
		try {
			BigDecimal bd = new BigDecimal(d);
			return bd.setScale(point, model).doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}

	public static String doubleToString(double d, int point) {
		BigDecimal bd = new BigDecimal(d);
		return bd.setScale(point, BigDecimal.ROUND_HALF_DOWN).toString();
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

	public static boolean isInteger(double value) {
		if (value == 0.0) return true;

		return Math.ceil(value) == Math.floor(value);
	}

	public static String doubleUpTOString(double value) {
		BigDecimal bd = new BigDecimal(value);
		String amountStr = bd.setScale(0, BigDecimal.ROUND_UP).toString();
		return amountStr;
	}
}
