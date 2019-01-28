package lottery.domains.pool.util;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

/**
 * 玩法工具类：公用方法集合
 * 
 */
public class TicketPlayUtils {
	
	public static String[] getOpenNums(String openNums) {
		String[] nums = null;
		if (openNums.contains(",")) {
			nums = openNums.split(",");
		} else if (openNums.trim().contains(" ")) {
			nums = openNums.split(" ");
		} else {
			nums = new String[openNums.trim().length()];
			for (int i = 0; i < openNums.trim().length(); i++) {
				nums[i] = String.valueOf(openNums.trim().charAt(i));
			}
		}
		if (nums == null) {
			return null;
		}
		return nums;
	}

	/**
	 * 获取指定位置的开奖号码
	 * 
	 * @param openNums
	 *            完整的开奖号码
	 * @param offsets
	 *            位置数组
	 * @return
	 */
	public static String[] getOpenNums(String openNums, int[] offsets) {
		String[] nums = null;
		if (openNums.contains(",")) {
			nums = openNums.split(",");
		} else if (openNums.trim().contains(" ")) {
			nums = openNums.split(" ");
		} else {
			nums = new String[openNums.trim().length()];
			for (int i = 0; i < openNums.trim().length(); i++) {
				nums[i] = String.valueOf(openNums.trim().charAt(i));
			}
		}
		if (nums == null) {
			return null;
		}
		String[] res = new String[offsets.length];
		for (int i = 0; i < offsets.length; i++) {
			res[i] = nums[offsets[i]].trim();
		}
		return res;
	}

	/**
	 * 获取排好序的开奖号
	 * 
	 * @param openNums
	 * @param offsets
	 * @return
	 */
	public static String[] getSortedOpenNums(String openNums, int[] offsets) {
		String[] res = getOpenNums(openNums, offsets);
		Arrays.sort(res);
		return res;
	}
	

	/**
	 * 获取排好序的号码
	 * 
	 * @param nums
	 * @return
	 */
	public static String getSortedNums(String nums) {
		char[] c = nums.toCharArray();
		Arrays.sort(c);
		return new String(c);
	}

	/**
	 * 计算开奖号码和值
	 * 
	 * @param openNums
	 * @param offsets
	 * @return
	 */
	public static int getOpenNumSum(String openNums, int[] offsets) {
		String[] nums = getOpenNums(openNums, offsets);
		int sum = 0;
		for (String num : nums) {
			sum += Integer.parseInt(num);
		}
		return sum;
	}
	
	/**
	 * 计算开奖号码和值
	 * 
	 * @param openNums
	 * @param offsets
	 * @return
	 */
	public static int getOpenNumSumByStr(String openNums, String[] openCodes) {
		int sum = 0;
		for (String num : openCodes) {
			sum += Integer.parseInt(num);
		}
		return sum;
	}

	/**
	 * 号码索引格式化
	 * 
	 * @param betNums
	 * @return
	 */
	public static String toIndexString(String betNums) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < betNums.length(); i++) {
			sb.append(betNums.charAt(i)).append(" ");
		}
		return sb.toString().trim();
	}

	/**
	 * 获取已去重的号码数组，不需要排序
	 * 
	 * @param nums
	 * @return
	 */
	public static String[] getFixedNums(String[] nums) {
		if (nums == null || nums.length <= 0) {
			return null;
		}
		Set<String> res = new HashSet<>();
		for (String num : nums) {
			if (StringUtils.isNotEmpty(num)) {
				res.add(num);
			}
		}
		return res.toArray(new String[] {});
	}

	public static String[] getFixedAndSortedNums(String[] nums) {
		if (nums == null || nums.length <= 0) {
			return null;
		}
		TreeSet<String> res = new TreeSet<>();
		for (String num : nums) {
			if (StringUtils.isNotEmpty(num)) {
				res.add(num);
			}
		}
		return res.toArray(new String[] {});
	}
	
	/**
	 * k3 获得开奖号码中 对子 号码
	 * 
	 * @param nums
	 * @return
	 */
	public static String getOpenDualNum(String [] opencodes) {
		Arrays.sort(opencodes);
		for (int i = 0; i < opencodes.length; i++) {
			int we= 0;
			for (int j = 0; j < opencodes.length; j++) {
				if(opencodes[i].equals(opencodes[j])){
					we ++;
				}
			}
			if (we >= 2) {
				return opencodes[i];
			}
		}
		return null;
	} 
	
	
	public static void main(String[] args) {
		String [] re = new String []{"1","2","3","5","5","6","2"};
		System.out.println(getOpenDualNum(re));
	}
}
