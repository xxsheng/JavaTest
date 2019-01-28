package javautils.array;

import javautils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
	
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

	public static List<Integer> transStrToInteger(List<String> ids) {
		if (ids == null || ids.size() <= 0) {
			return new ArrayList<>();
		}
		List<Integer> arr = new ArrayList<>(ids.size());
		for (int i = 0; i < ids.size(); i++) {
			arr.add(Integer.valueOf(ids.get(i)));
		}
		return arr;
	}

	/**
	 * 删除某个id
	 * @param ids
	 * @return
	 */
	public static String deleteInsertIds(String ids, int id, boolean isAll) {
		if(StringUtil.isNotNull(ids)) {
			if(ids.indexOf("[" + id + "]") != -1) {
				String[] tmp = ids.replaceAll("\\[|\\]", "").split(",");
				List<Integer> list = new ArrayList<>();
				for (int i = 0; i < tmp.length; i++) {
					if(id != Integer.parseInt(tmp[i])) {
						list.add(Integer.parseInt(tmp[i]));
					} else {
						if(isAll) {
							break;
						}
					}
				}
				int[] arr = new int[list.size()];
				for (int i = 0; i < list.size(); i++) {
					arr[i] = list.get(i);
				}
				return transInsertIds(arr);
			}
		}
		return ids;
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
	 * 获取id的时候，解析成id数组
	 * @param ids
	 * @return
	 */
	public static Integer[] transGetIdsToInteger(String ids) {
		String[] tmp = ids.replaceAll("\\[|\\]", "").split(",");
		Integer[] arr = new Integer[tmp.length];
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

	/**
	 * 转换成字符串
	 * @return
	 */
	public static String toStringFromSet(java.util.Set<String> set) {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		for (String str : set) {
			sb.append(str);
			if(count < set.size() - 1) {
				sb.append(",");
			}
			count++;
		}
		return sb.toString();
	}

	public static int[] listToInt(List<Integer> list) {
		int[] arr = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = list.get(i);
		}

		return arr;
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
	public static void quickSortDouble(double[] a, int low, int high){
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
	public static void quickSortDouble(double[] a){
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

	public static void main(String[] args) {
		String str = deleteInsertIds("[418],[243],[199],[72]", 243, true);
		System.out.println(str);
	}
}