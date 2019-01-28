package lottery.domains.utils.validate;

import javautils.regex.RegexUtil;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.web.content.validate.UserBetsValidate;

import java.util.*;

public class LotteryUtilKL8 {
	
	/**
	 * 获取投注注数
	 * @param type
	 * @param dstr
	 * @return
	 */
	public static Object[] inputNumber(String type, String dstr) {
		List<Object[]> datasel = inputFormat(type, dstr);
		if(datasel == null || datasel.size() == 0) {
			return new Object[] {null, 0};
		}
		int nums = 0, tmp_nums = 1;
		switch (type) {
		// 这里是验证选择框
		case "rx1":
			try {
				nums = datasel.get(0).length;
			} catch (Exception e) {}
			break;
		case "rx2":
			try {
				int l = datasel.get(0).length;
				if(l >= 2 && l <= 8) {
					nums = ArrayUtil.Combination(l, 2);
				}
			} catch (Exception e) {}
			break;
		case "rx3":
			try {
				int l = datasel.get(0).length;
				if(l >= 3 && l <= 8) {
					nums = ArrayUtil.Combination(l, 3);
				}
			} catch (Exception e) {}
			break;
		case "rx4":
			try {
				int l = datasel.get(0).length;
				if(l >= 4 && l <= 8) {
					nums = ArrayUtil.Combination(l, 4);
				}
			} catch (Exception e) {}
			break;
		case "rx5":
			try {
				int l = datasel.get(0).length;
				if(l >= 5 && l <= 8) {
					nums = ArrayUtil.Combination(l, 5);
				}
			} catch (Exception e) {}
			break;
		case "rx6":
			try {
				int l = datasel.get(0).length;
				if(l >= 6 && l <= 8) {
					nums = ArrayUtil.Combination(l, 6);
				}
			} catch (Exception e) {}
			break;
		case "rx7":
			try {
				int l = datasel.get(0).length;
				if(l >= 7 && l <= 8) {
					nums = ArrayUtil.Combination(l, 7);
				}
			} catch (Exception e) {}
			break;
		default:
			try {
				int maxplace = 1;
				for (int i = 0; i < maxplace; i++) {
					// 有位置上没有选择
					if (datasel.get(i).length == 0) {
						tmp_nums = 0; break;
					}
					tmp_nums *= datasel.get(i).length;
				}
				nums += tmp_nums;
			} catch (Exception e) {}
			break;
		}
		String codes = replaceNumbers(type, dstr, datasel);
		return new Object[] {codes, nums};
	}
	
	/**
	 * 替换号码，主要针对输入框
	 * @param type
	 * @param dstr
	 * @return
	 */
	public static String replaceNumbers(String type, String dstr, List<Object[]> datasel) {
		return dstr;
	}
	
	public static Object[] formatCode(Object[] codes) {
		Set<Object> set = new HashSet<>();
		for (Object o : codes) {
			set.add(o);
		}
		Object[] result = set.toArray();
		Arrays.sort(result);
		return result;
	}
	
	/**
	 * 格式化号码
	 * @param type
	 * @param codestr
	 * @return
	 */
	public static List<Object[]> inputFormat(String type, String codestr) {
		switch (type) {
		case "rx1":
		case "rx2":
		case "rx3":
		case "rx4":
		case "rx5":
		case "rx6":
		case "rx7":
			try {
				List<Object[]> list = new ArrayList<Object[]>();
				String[] arr = codestr.split(",");
				Object[] tmp = new Object[arr.length];
				for (int i = 0; i < arr.length; i++) {
					tmp[i] = arr[i];
				}
				list.add(formatCode(tmp));
				return list;
			} catch (Exception e) {}
			break;
		case "hezhids":
		case "hezhidx":
		case "jopan":
		case "sxpan":
		case "hzdxds":
		case "hezhiwx":
			try {
				List<Object[]> list = new ArrayList<Object[]>();
				String[] arr = codestr.split("\\|");
				Object[] tmp = new Object[arr.length];
				for (int i = 0; i < arr.length; i++) {
					tmp[i] = arr[i];
				}
				list.add(formatCode(tmp));
				return list;
			} catch (Exception e) {}
			break;
		default:
			break;
		}
		return null;
	}

	/**
	 * 验证最大最小投注注数，返回空则通过
	 * 数组第一位：1：小于最小投注；2：超过最大投注
	 * 数组第二位：最大或最小注数
	 * 数组第三位：实际投注数
	 * 数组第四位：注或码
	 */
	public static Object[] validateMinMaxNumbers(LotteryPlayRules playRules, int num, String codes) {
		String type = playRules.getCode();
		switch (type) {
			// 任选1
			case "rx1":
				int minNum = Integer.valueOf(playRules.getMinNum());
				if (num < minNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, minNum, num, "注"};
				}

				int maxNum = Integer.valueOf(playRules.getMaxNum());
				if (num > maxNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, maxNum, num, "注"};
				}
				return null;
			default :
				return null;
		}
	}
	
	/******************************** 检测号码 ********************************/
	
	/**
	 * 判断重复:0,1,2,3,4,5,6,7,8,9
	 * @return
	 */
	public static boolean hasRepeat(String[] arr) {
		Set<String> set = new HashSet<>();
		for (String s : arr) {
			if(set.contains(s)) {
				return true;
			}
			set.add(s);
		}
		return false;
	}
	
	/**
	 * 适用类型：单排数字[01-80]
	 * 单排长度验证：是(minLength, maxLength)
	 * 单排重复验证：是
	 */
	public static boolean testType02(String dstr, int minLength, int maxLength) {
		String[] arr = dstr.split(",");
		if(arr.length >= minLength && arr.length <= maxLength) {
			boolean hasRepeat = hasRepeat(arr);
			if(hasRepeat) {
				return false;
			}
			String regex = "^[0-9]{2}$";
			for (String s : arr) {
				boolean matcher = RegexUtil.isMatcher(s, regex);
				if(!matcher) {
					return false;
				}
				if(Integer.parseInt(s) > 80 || Integer.parseInt(s) < 1) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 适用类型：单排自定义
	 */
	public static boolean testType03(String[] arr, String[] values, int minLength, int maxLength) {
		if(arr.length >= minLength && arr.length <= maxLength) {
			HashSet<String> set = new HashSet<>();
			for (String s : values) {
				set.add(s);
			}
			boolean hasRepeat = hasRepeat(arr);
			if(hasRepeat) {
				return false;
			}
			for (String s : arr) {
				if(!set.contains(s)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static boolean inputValidate(String type, String dstr) {
		switch (type) {
		case "hezhids":
			try {
				String[] arr = dstr.split("\\|");
				String[] values = {"单", "双"};
				return testType03(arr, values, 1, 2);
			} catch (Exception e) {}
			break;
		case "hezhidx":
			try {
				String[] arr = dstr.split("\\|");
				String[] values = {"大", "和", "小"};
				return testType03(arr, values, 1, 3);
			} catch (Exception e) {}
			break;
		case "jopan":
			try {
				String[] arr = dstr.split("\\|");
				String[] values = {"奇", "和", "偶"};
				return testType03(arr, values, 1, 3);
			} catch (Exception e) {}
			break;
		case "sxpan":
			try {
				String[] arr = dstr.split("\\|");
				String[] values = {"上", "中", "下"};
				return testType03(arr, values, 1, 3);
			} catch (Exception e) {}
			break;
		case "hzdxds":
			try {
				String[] arr = dstr.split("\\|");
				String[] values = {"大单", "大双", "小单", "小双"};
				return testType03(arr, values, 1, 4);
			} catch (Exception e) {}
			break;
		case "rx1":
			return testType02(dstr, 1, 80);
		case "rx2":
			return testType02(dstr, 2, 8);
		case "rx3":
			return testType02(dstr, 3, 8);
		case "rx4":
			return testType02(dstr, 4, 8);
		case "rx5":
			return testType02(dstr, 5, 8);
		case "rx6":
			return testType02(dstr, 6, 8);
		case "rx7":
			return testType02(dstr, 7, 8);
		case "hezhiwx":
			try {
				String[] arr = dstr.split("\\|");
				String[] values = {"金", "木", "水", "火", "土"};
				return testType03(arr, values, 1, 5);
			} catch (Exception e) {}
			break;
		default:
			System.out.println("不需要验证的方法...." + type);
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		String type = "rx2";
		String dstr = "01,02,03,04,05,06,07,08,09";
		boolean inputValidate = inputValidate(type, dstr);
		System.out.println(inputValidate);
	}
	
}