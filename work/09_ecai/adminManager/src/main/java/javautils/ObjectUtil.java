package javautils;

public class ObjectUtil {
	
	public static double toDouble(Object object) {
		if(object != null && object instanceof Number) {
			return ((Number) object).doubleValue();
		}
		return 0;
	}
	
	public static int toInt(Object object) {
		if(object != null && object instanceof Number) {
			return ((Number) object).intValue();
		}
		return 0;
	}
	
	public static long toLong(Object object) {
		if(object != null && object instanceof Number) {
			return ((Number) object).longValue();
		}
		return 0;
	}
	
	public static float toFloat(Object object) {
		if(object != null && object instanceof Number) {
			return ((Number) object).floatValue();
		}
		return 0;
	}
	
	public static short toShort(Object object) {
		if(object != null && object instanceof Number) {
			return ((Number) object).shortValue();
		}
		return 0;
	}
	
}