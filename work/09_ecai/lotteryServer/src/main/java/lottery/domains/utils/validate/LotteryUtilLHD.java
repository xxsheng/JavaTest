package lottery.domains.utils.validate;

import javautils.StringUtil;
import javautils.regex.RegexUtil;
import lottery.domains.content.entity.LotteryPlayRules;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class LotteryUtilLHD {
	private static final String[] LEGAL_CODES = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

	/**
	 * 输入框单式检测
	 * @param datasel
	 * @param l
	 * @param hzc
	 * @return
	 */
	private static Object[] _inputCheck_Num(List<Object[]> datasel, int l, boolean hzc) {
		int nums = datasel.size();
		List<Object[]> list = new ArrayList<Object[]>();
		if(l > 0) {
			String regex = "^[0-9]{" + l + "}$";
			for (int i = 0; i < datasel.size(); i++) {
				String n = datasel.get(i)[0].toString();
				if(hzc) {
					if(RegexUtil.isMatcher(n, regex) && _inputHZCheck_Num(n, l)) {
						list.add(datasel.get(i));
					} else {
						nums = nums - 1;
					}
				} else {
					if(RegexUtil.isMatcher(n, regex)) {
						list.add(datasel.get(i));
					} else {
						nums = nums - 1;
					}
				}
			}
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = list.size(); i < j; i++) {
			sb.append(list.get(i)[0]);
			if(i != j - 1) {
				sb.append(" ");
			}
		}
		return new Object[] {nums, sb.toString()};
	}

	/**
	 * 和值混合组选检测
	 * @param n
	 * @param l
	 * @return
	 */
	private static boolean _inputHZCheck_Num(String n, int l) {
		if(l == 2) {
			String[] arr = { "00", "11", "22", "33", "44", "55", "66", "77", "88", "99" };
			return ArrayUtil.inArray(n, arr) ? false : true;
		} else {
			String[] arr = { "000", "111", "222", "333", "444", "555", "666", "777", "888", "999" };
			return ArrayUtil.inArray(n, arr) ? false : true;
		}
	}

	/**
	 * 获取投注注数
	 */
	public static Object[] inputNumber(String type, String dstr) {
		List<Object[]> datasel = inputFormat(type, dstr);
		if(datasel == null || datasel.size() == 0) {
			return new Object[] {null, 0};
		}
		int nums = 0, tmp_nums = 1;
		switch (type) {
			case "lhd_longhuhewq":
			case "lhd_longhuhewb":
			case "lhd_longhuhews":
			case "lhd_longhuhewg":
			case "lhd_longhuheqb":
			case "lhd_longhuheqs":
			case "lhd_longhuheqg":
			case "lhd_longhuhebs":
			case "lhd_longhuhebg":
			case "lhd_longhuhesg":
				int maxplace = 1;
				// 号码不符合规则
				if(datasel.size() == maxplace) {
					for (int i = 0; i < maxplace; i++) {
						// 有位置上没有选择
						if (datasel.get(i).length == 0) {
							tmp_nums = 0; break;
						}
						tmp_nums *= datasel.get(i).length;
					}
					nums += tmp_nums;
				}
			default:
				break;
		}
		String codes = replaceNumbers(type, dstr, datasel);
		return new Object[] {codes, nums};
	}

	/**
	 * 验证最大最小投注注数，返回空则通过
	 * 数组第一位：1：小于最小投注；2：超过最大投注
	 * 数组第二位：最大或最小注数
	 * 数组第三位：实际投注数
	 * 数组第四位：注或码
	 */
	public static Object[] validateMinMaxNumbers(LotteryPlayRules playRules, int num, String codes) {
		return null;
	}

	/**
	 * 替换号码，主要针对输入框
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

	public static List<Object[]> _formatSelect_Num(String s, int m, int n) {
		List<Object[]> list = new ArrayList<Object[]>();
		String[] arr = s.split(",");
		for (int i = m, j = arr.length; i < j - n; i++) {
			list.add(formatCode(StringUtil.split(arr[i].replace("-", ""))));
		}
		return list;
	}

	public static List<Object[]> _formatTextarea_Num(String codestr) {
		List<Object[]> list = new ArrayList<Object[]>();
		codestr = codestr.replaceAll(",|;", " ");
		String[] arr = codestr.split(" ");
		for (String s : arr) {
			list.add(new Object[] {s});
		}
		return list;
	}

	/**
	 * 格式化号码
	 * @param type
	 * @param codestr
	 * @return
	 */
	public static List<Object[]> inputFormat(String type, String codestr) {
		switch (type) {
			case "lhd_longhuhewq":
			case "lhd_longhuhewb":
			case "lhd_longhuhews":
			case "lhd_longhuhewg":
			case "lhd_longhuheqb":
			case "lhd_longhuheqs":
			case "lhd_longhuheqg":
			case "lhd_longhuhebs":
			case "lhd_longhuhebg":
			case "lhd_longhuhesg":
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
			default:
				return _formatSelect_Num(codestr, 0, 0);
		}
		return null;
	}

	/******************************** 检测号码 ********************************/

	/**
	 * 判断重复:0123456789这种
	 * @param s
	 * @return
	 */
	public static boolean hasRepeat(String s) {
		Set<String> set = new HashSet<>();
		for (int i = 0; i < s.length(); i++) {
			String w = s.substring(i, i + 1);
			if(set.contains(w)) {
				return true;
			}
			set.add(w);
		}
		return false;
	}

	/**
	 * 判断重复:0,1,2,3,4,5,6,7,8,9
	 * @param s
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
	 * 适用类型: 多排数字
	 * 取值区间：[0-9]
	 * 单排长度：[1-10]
	 * 前后预留位验证：是 (m,n)
	 * 预留标记："-"
	 * 排数行数验证：是(minLength, maxLength)
	 * 多排重复验证：否
	 * 单排重复验证：是
	 */
	public static boolean testType01(String dstr, int m, int n, int minLength, int maxLength) {
		String[] arr = dstr.split(",");
		if(arr.length >= minLength && arr.length <= maxLength) {
			String regex = "^[0-9]{1,10}$";
			for (int i = 0; i < arr.length; i++) {
				String s = arr[i];
				if(m != 0 && i < m) {
					if("-".equals(s) == false) {
						return false;
					}
					continue;
				}
				if(n != 0 && i >= arr.length - n) {
					if("-".equals(s) == false) {
						return false;
					}
					continue;
				}
				boolean hasRepeat = hasRepeat(s);
				if(hasRepeat) {
					return false;
				}
				boolean matcher = RegexUtil.isMatcher(s, regex);
				if(!matcher) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 适用类型：单排数字[0-9]
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
			String regex = "^[0-9]{1}$";
			for (String s : arr) {
				boolean matcher = RegexUtil.isMatcher(s, regex);
				if(!matcher) {
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
	public static boolean testType03(String dstr, String[] values, int minLength, int maxLength) {
		String[] arr = dstr.split(",");
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

	/**
	 * 适用类型：多排自定义
	 */
	public static boolean testType04(String dstr, String[] values, int minLength, int maxLength) {
		String[] arr = dstr.split(",");
		if(arr.length >= minLength && arr.length <= maxLength) {
			HashSet<String> set = new HashSet<>();
			for (String s : values) {
				set.add(s);
			}
			for (String s : arr) {
				boolean hasRepeat = hasRepeat(s);
				if(hasRepeat) {
					return false;
				}
				for (int i = 0; i < s.length(); i++) {
					String w = s.substring(i, i + 1);
					if(!set.contains(w)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 适用类型: 定位胆、任选
	 * 取值区间：[0-9]
	 * 单排长度：[1-10]
	 * 预留标记："-"
	 * 排数行数验证：是(minLength, maxLength)
	 * 多排重复验证：否
	 * 单排重复验证：是
	 */
	public static boolean testType05(String dstr, int minLength, int maxLength, int rowLength) {
		String[] arr = dstr.split(",");
		if(arr.length != rowLength) {
			return false;
		}
		int length = 0;
		String regex = "^[0-9]{1,10}$";
		for (String s : arr) {
			if("-".equals(s)) {
				continue;
			}
			length++;
			boolean hasRepeat = hasRepeat(s);
			if(hasRepeat) {
				return false;
			}
			boolean matcher = RegexUtil.isMatcher(s, regex);
			if(!matcher) {
				return false;
			}
		}
		if(length >= minLength && length <= maxLength) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 适用类型：任选位置
	 */
	public static boolean testType06(String dstr, int minLength, int maxLength, int rowLength) {
		String[] arr = dstr.split(",");
		if(arr.length != rowLength) {
			return false;
		}
		int length = 0;
		for (String s : arr) {
			if("-".equals(s)) {
				continue;
			}
			length++;
			if(!"√".equals(s)) {
				return false;
			}
		}
		if(length >= minLength && length <= maxLength) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 适用类型：验证用户输入的号码
	 * @param codes 用户输入的号码，应截取去前面的勾号，如112,123,134,124或者1234 1235 1236
	 * @param separator 每注号码用什么分割的，如英文逗号或者空格
	 * @param length 每注号码的长度，如3，4，5
	 * @param legalCodes 合法号码，合法的号码列表，如时时彩：{0,1,2,3,4,5,6,7,8,9}
	 * @return
	 */
	public static boolean testCodes(final String codes, final String separator, final int length, final String[] legalCodes) {
		if (StringUtils.isEmpty(codes) || legalCodes == null || legalCodes.length <= 0 || separator == null || length <= 0) {
			return false;
		}

		String[] arr = codes.split(separator);
		if (arr == null || arr.length <= 0) {
			return false;
		}

		boolean hasRepeat = hasRepeat(arr);
		if(hasRepeat) {
			return false;
		}

		for (String s : arr) {
			if (s.length() != length) {
				return false;
			}

			Object[] splitCodes = StringUtil.split(s);
			for (Object splitCode : splitCodes) {
				int indexOf = ArrayUtils.indexOf(legalCodes, splitCode.toString());
				if (indexOf == -1) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 适用类型：验证用户输入的号码
	 * @param codes 用户输入的号码
	 * @param codesLength 号码的长度，如1。则只能有1注号码
	 * @param legalCodes 合法号码，合法的号码列表，如{龙,虎,和}
	 * @return
	 */
	public static boolean testCodes(final String codes, int codesLength, final String[] legalCodes) {
		if (StringUtils.isEmpty(codes) || legalCodes == null || legalCodes.length <= 0) {
			return false;
		}

		if (codes.length() != codesLength) {
			return false;
		}

		int indexOf = ArrayUtils.indexOf(legalCodes, codes);
		if (indexOf == -1) {
			return false;
		}

		return true;
	}

	/**
	 * 适用类型：验证用户输入的号码
	 * @param codes 用户输入的号码，应截取去前面的勾号，如112,123,134,124或者1234 1235 1236
	 * @param separator 每注号码用什么分割的，如英文逗号或者空格
	 * @param legalCodes 合法号码，合法的号码列表，如时时彩：{0,1,2,3,4,5,6,7,8,9}
	 * @return
	 */
	public static boolean testCodes(final String codes, final String separator, final String[] legalCodes) {
		if (StringUtils.isEmpty(codes) || legalCodes == null || legalCodes.length <= 0 || separator == null) {
			return false;
		}

		String[] arr = codes.split(separator);
		if (arr == null || arr.length <= 0) {
			return false;
		}

		boolean hasRepeat = hasRepeat(arr);
		if(hasRepeat) {
			return false;
		}

		for (String code : arr) {
			int indexOf = ArrayUtils.indexOf(legalCodes, code);
			if (indexOf == -1) {
				return false;
			}
		}

		return true;
	}

	public static boolean inputValidate(String type, String dstr) {
		switch (type) {
			// 龙虎和
			case "lhd_longhuhewq":
			case "lhd_longhuhewb":
			case "lhd_longhuhews":
			case "lhd_longhuhewg":
			case "lhd_longhuheqb":
			case "lhd_longhuheqs":
			case "lhd_longhuheqg":
			case "lhd_longhuhebs":
			case "lhd_longhuhebg":
			case "lhd_longhuhesg":
				try {
					String[] values = {"龙", "虎", "和"};
					return testCodes(dstr, 1, values);
				} catch (Exception e) {}
				break;
			default:
				System.out.println("不需要验证的方法...." + type);
				return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String type = "lhd_longhuhewq";
		String dstr = "龙,和";
		boolean validate = inputValidate(type, dstr);
		System.out.println(validate);
	}

}