package javautils.array;

import java.util.*;

public class ArrayUtils {
	public static boolean hasSame(String... vals) {
		Set<String> set = new HashSet<>();
		set.addAll(Arrays.asList(vals));

		return vals.length != set.size();
	}

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
	
	/**
	 * 针对数据库in
	 * @param ids
	 * @return
	 */
	public static String transInIds(int[] ids) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = ids.length; i < j; i++) {
			sb.append(ids[i]);
			if(i < j - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public static String transInIds(Integer[] ids) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = ids.length; i < j; i++) {
			sb.append(ids[i].intValue());
			if(i < j - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 针对数据库in
	 * @param ids
	 * @return
	 */
	public static String transInIds(List<Integer> ids) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = ids.size(); i < j; i++) {
			sb.append(ids.get(i).intValue());
			if(i < j - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 存进数据库
	 * @param ids
	 * @return
	 */
	public static String transInsertIds(int[] ids) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = ids.length; i < j; i++) {
			sb.append("[" + ids[i] + "]");
			if(i < j - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取id的时候，解析成id数组
	 * @param ids
	 * @return
	 */
	public static int[] transGetIds(String ids) {
		String[] tmp = ids.replaceAll("\\[|\\]", "").split(",");
		int[] arr = new int[tmp.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Integer.parseInt(tmp[i]);
		}
		return arr;
	}
	
	/**
	 * 转换成字符串
	 * @param list
	 * @return
	 */
	public static String toString(List<Integer> list) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = list.size(); i < j; i++) {
			sb.append(list.get(i));
			if(i < j - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public static String toString(Object[] objs) {
		StringBuffer sb = new StringBuffer();
		for (Object obj : objs) {
			sb.append(obj.toString());
		}
		return sb.toString();
	}

	/**
	 * 快速排序<br>
	 * 算法的思想在于分而治之:先找一个元素(一般来说都是数组头元素),把比它大的都放到右边,把比它小的都放到左边;<br>
	 * 然后再按照这样的思想去处理两个子数组; 下面说的子数组头元素通指用来划分数组的元素;<br>
	 * <br>
	 * 下面程序关键点就在于!forward, low0++, high0--这些运算; 这三个运算使得a[low0],a[high0]里面总有一个指向子数组头元素; <br>
	 * 可以用极端的情况来方便理解这三个值的运作: <br>
	 * 假如我的数列为0123456789, 初始时forward=false,0作为子数组划分依据,很显然第一轮的时候不会发生任何交换,low0一直指向0,<br>
	 * high0逐渐下降直到它指向0为止; 同理可思考9876543210这个例子;<br>
	 * <br>
	 * 时间复杂度: 平均:O(nlogn)，最好:O(nlogn);最坏:O(n^2);
	 * 空间复杂度: O(logn);要为递归栈提供空间
	 * @param a 待排序数组<br>
	 * @param low 子数组开始的下标;<br>
	 * @param high 子数组结束的下标;<br>
	 */
	public static void quickSortDouble(Double[] a, int low, int high){
		if(low>=high){
			return;
		}
		int low0 = low;
		int high0 = high;
		boolean forward = false;
		while(low0!=high0){
			if(a[low0]>a[high0]){
				double tmp = a[low0];
				a[low0] = a[high0];
				a[high0] = tmp;
				forward = !forward;
			}
			if(forward){
				low0++;
			}
			else{
				high0--;
			}
		}
		low0--;
		high0++;
		quickSortDouble(a, low, low0);
		quickSortDouble(a, high0, high);
	}

	/**
	 * 快速排序的简单调用形式<br>
	 * 方便测试和调用<br>
	 * @param a
	 */
	public static void quickSortDouble(Double[] a){
		quickSortDouble(a, 0, a.length-1);
	}


	/**
	 * 快速排序<br>
	 * 算法的思想在于分而治之:先找一个元素(一般来说都是数组头元素),把比它大的都放到右边,把比它小的都放到左边;<br>
	 * 然后再按照这样的思想去处理两个子数组; 下面说的子数组头元素通指用来划分数组的元素;<br>
	 * <br>
	 * 下面程序关键点就在于!forward, low0++, high0--这些运算; 这三个运算使得a[low0],a[high0]里面总有一个指向子数组头元素; <br>
	 * 可以用极端的情况来方便理解这三个值的运作: <br>
	 * 假如我的数列为0123456789, 初始时forward=false,0作为子数组划分依据,很显然第一轮的时候不会发生任何交换,low0一直指向0,<br>
	 * high0逐渐下降直到它指向0为止; 同理可思考9876543210这个例子;<br>
	 * <br>
	 * 时间复杂度: 平均:O(nlogn)，最好:O(nlogn);最坏:O(n^2);
	 * 空间复杂度: O(logn);要为递归栈提供空间
	 * @param a 待排序数组<br>
	 * @param low 子数组开始的下标;<br>
	 * @param high 子数组结束的下标;<br>
	 */
	public static void quickSortString(String[] a, int low, int high){
		if(low>=high){
			return;
		}
		int low0 = low;
		int high0 = high;
		boolean forward = false;
		while(low0!=high0){
			if(a[low0].compareTo(a[high0]) > 0){
				String tmp = a[low0];
				a[low0] = a[high0];
				a[high0] = tmp;
				forward = !forward;
			}
			if(forward){
				low0++;
			}
			else{
				high0--;
			}
		}
		low0--;
		high0++;
		quickSortString(a, low, low0);
		quickSortString(a, high0, high);
	}

	/**
	 * 快速排序的简单调用形式<br>
	 * 方便测试和调用<br>
	 * @param a
	 */
	public static void quickSortString(String[] a){
		quickSortString(a, 0, a.length-1);
	}

	public static void main(String[] args) {
		// ConcurrentHashMap<String, Double> prizeCodes = new ConcurrentHashMap<>();
		// prizeCodes.put("1000000", 2000000d);
		// prizeCodes.put("0", 2d);
		// for (int i = 1; i <= 100000; i++) {
		// 	// prizeCodes.put(i + "", 0.01954);
		// 	prizeCodes.put(i + "", Integer.valueOf(RandomUtils.nextInt(200000)+5).doubleValue());
		// }
        //
		// long start = System.currentTimeMillis();
        //
        //
		// // Double[] prizes = prizeCodes.values().toArray(new Double[]{});
		// HashSet<Double> values = new HashSet<>(prizeCodes.values());
		// Double[] prizes = values.toArray(new Double[]{});
        //
		// ArrayUtils.quickSortDouble(prizes);
		// // Arrays.sort(prizes);
		// double minPrize = prizes[0];
		// double maxPrize = prizes[prizes.length-1];
        //
		// System.out.println("最小" + minPrize);
		// System.out.println("最大" + maxPrize);
        //
		// long spend = System.currentTimeMillis() - start;
		// System.out.println("耗时" + spend);

//		System.out.println(Combination(5, 11));

//		Double[] ds = {200d, 100d, 200d, 300d, 400d, 500d, 600d, 700d ,800d, 200d, 20d, 20d, 30d};
//
//		quickSortDouble(ds);
//
//		System.out.println(ds[0]);
	}
}