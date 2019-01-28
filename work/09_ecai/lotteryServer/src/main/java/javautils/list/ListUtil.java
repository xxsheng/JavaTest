package javautils.list;

import javautils.StringUtil;

import java.util.*;

public class ListUtil {

	/**
	 * 高性能删除全部
	 */
	public static LinkedList removeAll(LinkedList src, LinkedList list){
		if (src == null || src.isEmpty() || list == null || list.isEmpty()) {
			return src;
		}

		HashSet oth = new HashSet(list);
		return removeAll(src, oth);
	}

	/**
	 * 高性能删除全部
	 */
	public static LinkedList removeAll(LinkedList src, HashSet oth){
		if (src == null || src.isEmpty() || oth == null || oth.isEmpty()) {
			return src;
		}

		Iterator iter = src.iterator();//采用Iterator迭代器进行数据的操作
		while(iter.hasNext()){
			if(oth.contains(iter.next())){
				iter.remove();
			}
		}
		return src;
	}

	/**
	 * 高性能删除全部
	 */
	public static Set removeAll(Set src, Set oth){
		if (src == null || src.isEmpty() || oth == null || oth.isEmpty()) {
			return src;
		}

		Iterator iter = src.iterator();//采用Iterator迭代器进行数据的操作
		while(iter.hasNext()){
			if(oth.contains(iter.next())){
				iter.remove();
			}
		}

		return src;
	}

	
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
