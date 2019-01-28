package javautils.list;

import javautils.StringUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ListUtil {

	/**
	 * 高性能删除全部
	 */
	public static HashSet<String> transToHashSet(String[] strs){
		if (strs == null || strs.length <= 0) {
			return new HashSet<>();
		}

		HashSet<String> results = new HashSet<>();
		for (int i = 0; i < strs.length; i++) {
			results.add(strs[i]);
		}
		return results;
	}

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
	 * 高性能取交集
	 */
	public static LinkedList<String> retainAll(LinkedList<String> list1, LinkedList<String> list2){
		if (list1 == null || list1.isEmpty()) {
			return new LinkedList();
		}
		if (list2 == null || list2.isEmpty()) {
			return new LinkedList();
		}

		HashSet<String> retain = new HashSet<>();

		Iterator<String> iter1 = list1.iterator();//采用Iterator迭代器进行数据的操作
		Iterator<String> iter2 = list2.iterator();//采用Iterator迭代器进行数据的操作
		HashSet<String> oth1 = new HashSet(list1);
		HashSet<String> oth2 = new HashSet(list2);

		// list1在list2中有的
		while (iter1.hasNext()) {
			String val = iter1.next();
			if (oth2.contains(val)) {
				retain.add(val);
			}
		}
		// list2在list1中有的
		while (iter2.hasNext()) {
			String val = iter2.next();
			if (oth1.contains(val)) {
				retain.add(val);
			}
		}

		return new LinkedList(retain);
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
