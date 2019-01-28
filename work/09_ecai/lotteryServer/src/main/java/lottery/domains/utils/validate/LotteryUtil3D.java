package lottery.domains.utils.validate;

import javautils.StringUtil;
import javautils.regex.RegexUtil;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.web.content.validate.UserBetsValidate;
import net.sf.json.JSONObject;

import java.util.*;

public class LotteryUtil3D {
	
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
		// 这里是验证输入框
		case "sanxzhixds":
			try {
				nums = (int) _inputCheck_Num(datasel, 3, false)[0];
			} catch (Exception e) {}
			break;
		case "sanxhhzx":
			try {
				nums = (int) _inputCheck_Num(datasel, 3, true)[0];
			} catch (Exception e) {}
			break;
		case "exzhixdsh":
		case "exzhixdsq":
			try {
				nums = (int) _inputCheck_Num(datasel, 2, false)[0];
			} catch (Exception e) {}
			break;
		case "exzuxdsh":
		case "exzuxdsq":
			try {
				nums = (int) _inputCheck_Num(datasel, 2, true)[0];
			} catch (Exception e) {}
			break;
		// 这里是验证选择框
		case "sanxzs":
			try {
				int maxplace = 1;
				if(datasel.size() == maxplace) {
					for (int i = 0; i < maxplace; i++) {
						int s = datasel.get(i).length;
						// 组三必须选两位或者以上
						if (s > 1) {
							nums += s * (s - 1);
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "sanxzl":
			try {
				int maxplace = 1;
				if(datasel.size() == maxplace) {
					for (int i = 0; i < maxplace; i++) {
						int s = datasel.get(i).length;
						// 组六必须选三位或者以上
						if (s > 2) {
							nums += s * (s - 1) * (s - 2) / 6;
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "sanxzhixhz":
		case "exzhixhzh":
		case "exzhixhzq":
			try {
				JSONObject cc = JSONObject.fromObject("{0 : 1,1 : 3,2 : 6,3 : 10,4 : 15,5 : 21,6 : 28,7 : 36,8 : 45,9 : 55,10 : 63,11 : 69,12 : 73,13 : 75,14 : 75,15 : 73,16 : 69,17 : 63,18 : 55,19 : 45,20 : 36,21 : 28,22 : 21,23 : 15,24 : 10,25 : 6,26 : 3,27 : 1}");
				if("exzhixhzh".equals(type) || "exzhixhzq".equals(type)) {
					cc = JSONObject.fromObject("{0 : 1,1 : 2,2 : 3,3 : 4,4 : 5,5 : 6,6 : 7,7 : 8,8 : 9,9 : 10,10 : 9,11 : 8,12 : 7,13 : 6,14 : 5,15 : 4,16 : 3,17 : 2,18 : 1}");
				}
				for (int i = 0; i < datasel.get(0).length; i++) {
					Object val = cc.get(datasel.get(0)[i]);
					if(val != null) {
						nums += (int)val;
					}
				}
			} catch (Exception e) {}
		    break;
		case "dwd": //定位胆所有在一起特殊处理
			try {
				int maxplace = 3;
				if(datasel.size() == maxplace) {
					for (int i = 0; i < maxplace; i++) {
						nums += datasel.get(i).length;
					}
				}
			} catch (Exception e) {}
			break;
		case "exzuxfsh":
		case "exzuxfsq":
			try {
				int maxplace = 1;
				if(datasel.size() == maxplace) {
					for (int i = 0; i < maxplace; i++) {
						int s = datasel.get(i).length;
						// 二码不定位必须选两位或者以上
						if (s > 1) {
							nums += s * (s - 1) / 2;
						}
					}
				}
			} catch (Exception e) {}
			break;
		default:
			try {
				int maxplace = 0;
				switch (type) {
				case "sanxzhixfs":
					maxplace = 3;
					break;
				case "exzhixfsh":
				case "exzhixfsq":
					maxplace = 2;
					break;
				case "yimabdw":
					maxplace = 1;
					break;
				}
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
			} catch (Exception e) {}
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
		List<Object[]> datasel = inputFormat(type, codes);
		if(datasel == null || datasel.size() == 0) {
			return null;
		}
		switch (type) {
			// 三码
			case "sanxzhixfs":
			case "sanxzhixds":
			case "sanxzhixhz":
			// 二码
			case "exzhixfsh":
			case "exzhixdsh":
			case "exzhixhzh":
			case "exzhixfsq":
			case "exzhixdsq":
			case "exzhixhzq":
				int exzhixMaxNum = Integer.valueOf(playRules.getMaxNum());
				if (num > exzhixMaxNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, exzhixMaxNum, num, "注"};
				}
				int exzhixMinNum = Integer.valueOf(playRules.getMinNum());
				if (num < exzhixMinNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, exzhixMinNum, num, "注"};
				}
				return null;
			// 定位胆
			case "dwd":
				int maxplace = 3;
				int dwdMaxNum = Integer.valueOf(playRules.getMaxNum());
				int dwdMinNum = Integer.valueOf(playRules.getMinNum());
				for (int i = 0; i < maxplace; i++) {
					if (datasel.get(i).length > dwdMaxNum) {
						return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, dwdMaxNum, datasel.get(i).length, "码"};
					}
					if (datasel.get(i).length < dwdMinNum) {
						return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, dwdMinNum, datasel.get(i).length, "码"};
					}
				}
				return null;
			// 不定胆
			case "yimabdw":
				int yimabdwMaxNum = Integer.valueOf(playRules.getMaxNum());
				if (num > yimabdwMaxNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, yimabdwMaxNum, num, "码"};
				}
				int yimabdwMinNum = Integer.valueOf(playRules.getMinNum());
				if (num < yimabdwMinNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, yimabdwMinNum, num, "码"};
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
		case "sanxzhixds":
			return (String) _inputCheck_Num(datasel, 3, false)[1];
		case "sanxhhzx":
			return (String) _inputCheck_Num(datasel, 3, true)[1];
		case "exzhixdsh":
		case "exzhixdsq":
			return (String) _inputCheck_Num(datasel, 2, false)[1];
		case "exzuxdsh":
		case "exzuxdsq":
			return (String) _inputCheck_Num(datasel, 2, true)[1];
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
		case "sanxzhixds":
		case "sanxhhzx":
		case "exzhixdsh":
		case "exzhixdsq":
		case "exzuxdsh":
		case "exzuxdsq":
			return _formatTextarea_Num(codestr);
		case "sanxzs":
		case "sanxzl":
		case "exzuxfsh":
		case "exzuxfsq":
		case "yimabdw":
		case "sanxzhixhz":
		case "exzhixhzh":
		case "exzhixhzq":
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
		case "sanxzhixfs":
			return _formatSelect_Num(codestr, 0, 0);
		case "exzhixfsh":
			return _formatSelect_Num(codestr, 1, 0);
		case "exzhixfsq":
			return _formatSelect_Num(codestr, 0, 1);
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
	
	public static boolean inputValidate(String type, String dstr) {
		switch (type) {
		case "sanxzhixfs":
			return testType01(dstr, 0, 0, 3, 3);
		case "sanxzhixhz":
			try {
				int length = 28;
				String[] values = new String[length];
				for (int i = 0; i < length; i++) {
					values[i] = String.valueOf(i);
				}
				return testType03(dstr, values, 1, length);
			} catch (Exception e) {}
			break;
		case "sanxzs":
			return testType02(dstr, 2, 10);
		case "sanxzl":
			return testType02(dstr, 3, 10);
		case "exzhixfsh":
			return testType01(dstr, 1, 0, 3, 3);
		case "exzhixfsq":
			return testType01(dstr, 0, 1, 3, 3);
		case "exzuxfsh":
		case "exzuxfsq":
			return testType02(dstr, 2, 10);
		case "exzhixhzh":
		case "exzhixhzq":
			try {
				int length = 19;
				String[] values = new String[length];
				for (int i = 0; i < length; i++) {
					values[i] = String.valueOf(i);
				}
				return testType03(dstr, values, 1, length);
			} catch (Exception e) {}
			break;
		case "dwd":
			return testType05(dstr, 1, 3, 3);
		case "yimabdw":
			return testType02(dstr, 1, 10);
		default:
			System.out.println("不需要验证的方法...." + type);
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		String type = "sanxzs";
		String dstr = "3,9";
		boolean inputValidate = inputValidate(type, dstr);
		System.out.println(inputValidate);
	}
	
}