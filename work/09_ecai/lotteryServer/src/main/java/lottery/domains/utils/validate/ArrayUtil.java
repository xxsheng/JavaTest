package lottery.domains.utils.validate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ArrayUtil {
	
	public static int Combination(int n, int m) {
		if(m < 0 || n < 0) {
			return 0;
		}
		if(m == 0 || n == 0) {
			return 1;
		}
		if(m > n) {
			return 0;
		}
		if(m > n /2.0) {
			m = n - m;
		}
		double result = 0.0;
		for (int i = n; i >= (n - m + 1); i--) {
			result += Math.log(i);
		}
		for (int i = m; i >= 1; i--) {
			result -= Math.log(i);
		}
		result = Math.exp(result);
		return (int) Math.round(result);
	}
	
	public static List<Object[]> CombinationValue(List<Object[]> source, int m, int x) {
		int n = source.size();
		List<Object[]> list = new ArrayList<>();
		int start = 0;
		while (m > 0) {
			if (m == 1) {
				list.add(source.get(start + x));
				break;
			}
			for (int i = 0; i <= n - m; i++) {
				int cnm = (int) Combination(n - 1 - i, m - 1);
				if (x <= cnm - 1) {
					list.add(source.get(start + i));
					start = start + (i + 1);
					n = n - (i + 1);
					m--;
					break;
				} else {
					x = x - cnm;
				}
			}
		}
		return list;
	}
	
	// 数组去重复
	public static Object[] uniquelize(Object[] objArr) {
		Set<Object> set = new LinkedHashSet<Object>();
		for (Object obj : objArr) {
			if(!set.contains(obj)) {
				set.add(obj);
			}
		}
		return set.toArray();
	}
	
	// 合并数组
	public static Object[] concat(Object[] a, Object[] b) {
		Object[] arr = new Object[a.length + b.length];
		for (int i = 0; i < a.length; i++) {
			arr[i] = a[i];
		}
		for (int i = 0; i < b.length; i++) {
			arr[a.length + i] = b[i];
		}
		return arr;
	}
	
	public static boolean inArray(Object o, Object[] arr) {
		for (Object obj : arr) {
			if(obj.equals(o)) return true;
		}
		return false;
	}
	
	// 求两个集合的补集
	public static Object[] complement(Object[] a, Object[] b) {
		return minus(union(a, b), intersect(a, b));
	}
	
	// 求两个集合的交集
	public static Object[] intersect(Object[] a, Object[] b) {
		List<Object> list = new ArrayList<>();
		for (Object o : uniquelize(a)) {
			if(inArray(o, b)) {
				list.add(o);
			}
		}
		return list.toArray();
	}
	
	// 求两个集合的差集
	public static Object[] minus(Object[] a, Object[] b) {
		List<Object> list = new ArrayList<>();
		for (Object o : uniquelize(a)) {
			if(!inArray(o, b)) {
				list.add(o);
			}
		}
		return list.toArray();
	}
	
	// 求两个集合的并集
	public static Object[] union(Object[] a, Object[] b) {
		return uniquelize(concat(a, b));
	}
	
}