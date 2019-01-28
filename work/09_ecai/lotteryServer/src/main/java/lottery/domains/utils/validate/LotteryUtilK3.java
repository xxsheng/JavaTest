package lottery.domains.utils.validate;

import javautils.StringUtil;
import javautils.regex.RegexUtil;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.web.content.validate.UserBetsValidate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class LotteryUtilK3 {
	private static final String[] LEGAL_CODES = new String[]{"1", "2", "3", "4", "5", "6"};
	
	/**
	 * 输入框单式检测
	 * @param datasel
	 * @param l
	 * @return
	 */
	private static Object[] _inputCheck_Num(List<Object[]> datasel, int l, String fun) {
		int nums = datasel.size();
		List<Object[]> list = new ArrayList<Object[]>();
		if(l > 0) {
			String regex = "^[1-6]{" + l + "}$";
			for (int i = 0; i < datasel.size(); i++) {
				String n = datasel.get(i)[0].toString();
				if("_ethdsCheck".equals(fun)) {
					if(RegexUtil.isMatcher(n, regex) && _ethdsCheck(n, l)) {
						list.add(datasel.get(i));
					} else {
						nums = nums - 1;
					}
				}
				if("_ebthdsCheck".equals(fun)) {
					if(RegexUtil.isMatcher(n, regex) && _ebthdsCheck(n, l)) {
						list.add(datasel.get(i));
					} else {
						nums = nums - 1;
					}
				}
				if("_sbthdsCheck".equals(fun)) {
					if(RegexUtil.isMatcher(n, regex) && _sbthdsCheck(n, l)) {
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
	 * 2排不重复检测
	 */
	private static boolean _uniqueCheck(Object[] a, Object[] b) {
		return ArrayUtil.intersect(a, b).length == 0 ? true : false;
	}
	
	/**
	 * 二同号单式
	 * @param n
	 * @param l
	 * @return
	 */
	private static boolean _ethdsCheck(String n, int l) {
		if(l != 3) return false;
		String first = n.substring(0, 1);
		String second = n.substring(1, 2);
		String third = n.substring(2, 3);
		if (first.equals(second) && second.equals(third)) return false;
		if (first.equals(second) || second.equals(third) || third.equals(first)) return true;
		return false;
	}
	
	/**
     * 二不同号单式
     */
	private static boolean _ebthdsCheck(String n, int l) {
		if (l != 2) return false;
		String first = n.substring(0, 1);
		String second = n.substring(1, 2);
		if (first.equals(second)) return false;
		return true;
	}
    
    /**
     * 三不同号单式
     */
	private static boolean _sbthdsCheck(String n, int l) {
		if (l != 3) return false;
		String first = n.substring(0, 1);
		String second = n.substring(1, 2);
		String third = n.substring(2, 3);
		if (!first.equals(second) && !second.equals(third) && !third.equals(first)) return true;
		return false;
	}
	
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
		int nums = 0;
		switch (type) {
		// 这里验证输入框类型
		case "ebthds":
			try {
				nums = (int) _inputCheck_Num(datasel, 2, "_ebthdsCheck")[0];
			} catch (Exception e) {}
			break;
		case "ethds":
			try {
				nums = (int) _inputCheck_Num(datasel, 3, "_ethdsCheck")[0];
			} catch (Exception e) {}
			break;
		case "sbthds":
			try {
				nums = (int) _inputCheck_Num(datasel, 3, "_sbthdsCheck")[0];
			} catch (Exception e) {}
			break;
		// 这里验证选号类型
		case "ebthdx":
			try {
				if (datasel.get(0).length >= 2) {
					nums += ArrayUtil.Combination(datasel.get(0).length, 2);
				}
			} catch (Exception e) {}
            break; 
		case "ebthdt":
			try {
				int maxplace = 2;
				if(datasel.size() == maxplace) {
					if(_uniqueCheck(datasel.get(0), datasel.get(1))) {
						for (int i = 0; i < maxplace; i++) {
							int s = datasel.get(i).length;
							if (s > 0) {
								if(i > 0) {
									nums = datasel.get(i).length;
								}
							} else {
								nums = 0;
								break;
							}
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "ethdx":
			try {
				int s = datasel.size();
				if(s > 1) {
					int a = datasel.get(0).length;
					int b = datasel.get(1).length;
					if(a > 0 && b > 0) {
						if(_uniqueCheck(datasel.get(0), datasel.get(1))) {
							nums = a * b;
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "ethfx":
			try {
				nums = datasel.get(0).length;
			} catch (Exception e) {}
			break;
		case "sbthdx":
			try {
				if (datasel.get(0).length >= 3) {
					nums += ArrayUtil.Combination(datasel.get(0).length, 3);
	            }
			} catch (Exception e) {}
            break;
		case "sthdx":
		case "hezhi":
		case "hzdxds":
			try {
				nums = datasel.get(0).length;
			} catch (Exception e) {}
            break;
		case "sthtx":
		case "slhtx":
			try {
				nums = datasel.get(0).length > 0 ? 1 : 0;
			} catch (Exception e) {}
            break;
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
		String type = playRules.getCode();
		switch (type) {
			// 二不同号
			case "ebthdx":
			case "ebthds":
			// 三不同号
			case "sbthdx":
			case "sbthds":
			// 和值
			case "hezhi":
			case "hzdxds":
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
	
	/**
	 * 替换号码，主要针对输入框
	 * @param type
	 * @param dstr
	 * @return
	 */
	public static String replaceNumbers(String type, String dstr, List<Object[]> datasel) {
		switch (type) {
		case "ebthds":
			return (String) _inputCheck_Num(datasel, 2, "_ebthdsCheck")[1];
		case "ethds":
			return (String) _inputCheck_Num(datasel, 3, "_ethdsCheck")[1];
		case "sbthds":
			return (String) _inputCheck_Num(datasel, 3, "_sbthdsCheck")[1];
		default:
			return dstr;
		}
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
		case "ebthds":
		case "ethds":
		case "sbthds":
			return _formatTextarea_Num(codestr);
		case "ebthdx":
		case "ethfx":
		case "sbthdx":
		case "sthdx":
		case "sthtx":
		case "slhtx":
		case "hezhi":
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
		case "ebthdt":
		case "ethdx":
			return _formatSelect_Num(codestr, 0, 0);
		case "hzdxds":
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
			break;
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

	public static int getLenthWithoutRepeat(String[] arr) {
		Set<String> set = new HashSet<>();
		for (String s : arr) {
			set.add(s);
		}

		return set.size();
	}
	
	/**
	 * 适用类型: 多排数字
	 * 取值区间：[0-6]
	 * 单排长度：[1-6]
	 * 前后预留位验证：是 (m,n)
	 * 预留标记："-"
	 * 排数行数验证：是(minLength, maxLength)
	 * 多排重复验证：否
	 * 单排重复验证：是
	 */
	public static boolean testType01(String dstr, int m, int n, int minLength, int maxLength) {
		String[] arr = dstr.split(",");
		if(arr.length >= minLength && arr.length <= maxLength) {
			String regex = "^[1-6]{1,6}$";
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
	 * 适用类型：单排数字[0-6]
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
			String regex = "^[1-6]{1}$";
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
	 * 检测单式
	 * @param codes 号码字符串
	 * @param separator 每注号码使用什么分隔
	 * @param length 每注号码的长度
	 * @param legalCodes 合法号码列表
	 * @return
	 */
	public static boolean testNotSameDS(final String codes, final String separator, final int length, final String[] legalCodes) {
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

			String[] splitCodes = StringUtil.splitToString(s);

			boolean singleHasRepeat = hasRepeat(splitCodes);
			if(singleHasRepeat) {
				return false;
			}

			for (Object splitCode : splitCodes) {
				int indexOf = ArrayUtils.indexOf(legalCodes, splitCode);
				if (indexOf == -1) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean testRepeatDS(final String codes, final String separator, final int length,  final int repeatLength, final String[] legalCodes) {
		if (StringUtils.isEmpty(codes) || legalCodes == null || legalCodes.length <= 0 || separator == null || length <= 0 || repeatLength <= 0) {
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

			String[] splitCodes = StringUtil.splitToString(s);

			int codeRepeatLength = getLenthWithoutRepeat(splitCodes);
			if (codeRepeatLength != repeatLength) {
				return false;
			}

			for (Object splitCode : splitCodes) {
				int indexOf = ArrayUtils.indexOf(legalCodes, splitCode);
				if (indexOf == -1) {
					return false;
				}
			}
		}

		return true;
	}


	
	public static boolean inputValidate(String type, String dstr) {
		switch (type) {
		case "ebthdx":
			return testType02(dstr, 2, 6);
		case "ebthds":
			return testNotSameDS(dstr, " ", 2, LEGAL_CODES);
		case "ebthdt":
		case "ethdx":
			try {
				boolean isPass = testType01(dstr, 0, 0, 2, 2);
				if(isPass) {
					String[] arr = dstr.split(",");
					String regex = "^[1-6]{1}$";
					boolean isRow1 = RegexUtil.isMatcher(arr[0], regex);
					if(isRow1) {
						if(arr[1].indexOf(arr[0]) == -1) {
							return true;
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "ethds":
			return testRepeatDS(dstr, " ", 3, 2, LEGAL_CODES);
		case "ethfx":
		case "sthdx":
			return testType02(dstr, 1, 6);
		case "sbthdx":
			return testType02(dstr, 3, 6);
		case "sbthds":
			return testNotSameDS(dstr, " ", 3, LEGAL_CODES);
		case "sthtx":
			return "111,222,333,444,555,666".equals(dstr);
		case "slhtx":
			return "123,234,345,456".equals(dstr);
		case "hezhi":
			try {
				int length = 19;
				String[] values = new String[length];
				for (int i = 3; i < length; i++) {
					values[i] = String.valueOf(i);
				}
				return testType03(dstr, values, 1, 16);
			} catch (Exception e) {}
			break;
		case "hzdxds":
			try {
				String[] values = {"总和大", "总和小", "总和单", "总和双"};
				return testCodes(dstr, ",", values);
			} catch (Exception e) {}
			break;
		default:
			System.out.println("不需要验证的方法...." + type);
			return true;
		}
		return false;
	}
	
	/**
	 * 适用类型：验证用户输入的号码
	 * @param codes 用户输入的号码，应截取去前面的勾号，如112,123,134,124或者1234 1235 1236
     * @param separator 每注号码用什么分割的，如英文逗号或者空格
	 * @param length 每注号码的长度，如3，4，5
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
	
	public static void main(String[] args) {
		String type = "ebthdt";
		String dstr = "1,23456";
		boolean inputValidate = inputValidate(type, dstr);
		System.out.println(inputValidate);
	}
	
}