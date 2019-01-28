package javautils.list;

import java.util.List;

import javautils.StringUtil;

public class ListUtil {
	
	/**
	 * Object数组转int数组
	 * @param o
	 * @return
	 */
	public static int[] transObjectToInt(Object[] o) {
		int[] t = new int[o.length];
		for (int i = 0; i < o.length; i++) {
			if(o[i] instanceof Integer) {
				t[i] = (Integer)o[i];
			}
			if(o[i] instanceof String) {
				String s = (String)o[i];
				if(StringUtil.isIntegerString(s)) {
					t[i] = Integer.parseInt(s);
				}
			}
		}
		return t;
	}
	
	/**
	 * 集合转成String
	 * @param list
	 * @return
	 */
	public static String transListToString(List<?> list) {
		StringBuffer sb = new StringBuffer();
		for (Object obj : list) {
			sb.append(String.valueOf(obj) + ", ");
		}
		if(list.size() > 0) {
			return sb.substring(0, sb.length() - 2);
		}
		return sb.toString();
	}
	
}
