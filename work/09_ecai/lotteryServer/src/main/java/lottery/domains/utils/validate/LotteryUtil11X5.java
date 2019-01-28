package lottery.domains.utils.validate;

import javautils.StringUtil;
import javautils.regex.RegexUtil;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.web.content.validate.UserBetsValidate;

import java.util.*;

import org.apache.commons.lang.ArrayUtils;

public class LotteryUtil11X5 {
	
	/**
	 * 输入框单式检测
	 * @param datasel
	 * @param l
	 * @return
	 */
	private static Object[] _inputCheck_Num(List<Object[]> datasel, int l) {
		int nums = datasel.size();
		List<Object[]> list = new ArrayList<Object[]>();
		if(l > 0) {
			String regex = "^([0-9]{2}\\s{1}){" + (l - 1) + "}[0-9]{2}$";
			for (int i = 0; i < datasel.size(); i++) {
				String n = datasel.get(i)[0].toString();
				if(RegexUtil.isMatcher(n, regex) && _numberCheck_Num(n)) {
					list.add(datasel.get(i));
				} else {
					nums = nums - 1;
				}
			}
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = list.size(); i < j; i++) {
			sb.append(list.get(i)[0]);
			if(i != j - 1) {
				sb.append(";");
			}
		}
		return new Object[] {nums, sb.toString()};
	}
	
	/**
	 * 和值混合组选检测
	 * @param n
	 * @return
	 */
	private static boolean _numberCheck_Num(String n) {
		String[] t = n.split(" ");
		for (int i = 0; i < t.length; i++) {
			if(!StringUtil.isInteger(t[i])) {
				return false;
			} else {
				if(Integer.parseInt(t[i]) > 11 || Integer.parseInt(t[i]) < 1) {
					return false;
				}
			}
			for (int j = i + 1; j < t.length; j++) {
				if(!StringUtil.isInteger(t[j])) {
					return false;
				} else {
					if(Integer.parseInt(t[i]) == Integer.parseInt(t[j])) {
						return false;
					}
				}
			}
		}
		return true;
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
		case "sanmzhixdsq":
		case "sanmzuxdsq":
			try {
				nums = (int) _inputCheck_Num(datasel, 3)[0];
			} catch (Exception e) {}
			break;
		case "ermzhixdsq":
		case "ermzuxdsq":
			try {
				nums = (int) _inputCheck_Num(datasel, 2)[0];
			} catch (Exception e) {}
			break;
		case "rx1ds":
			try {
				nums = (int) _inputCheck_Num(datasel, 1)[0];
			} catch (Exception e) {}
			break;
		case "rx2ds":
			try {
				nums = (int) _inputCheck_Num(datasel, 2)[0];
			} catch (Exception e) {}
			break;
		case "rx3ds":
			try {
				nums = (int) _inputCheck_Num(datasel, 3)[0];
			} catch (Exception e) {}
			break;
		case "rx4ds":
			try {
				nums = (int) _inputCheck_Num(datasel, 4)[0];
			} catch (Exception e) {}
			break;
		case "rx5ds":
			try {
				nums = (int) _inputCheck_Num(datasel, 5)[0];
			} catch (Exception e) {}
			break;
		case "rx6ds":
			try {
				nums = (int) _inputCheck_Num(datasel, 6)[0];
			} catch (Exception e) {}
			break;
		case "rx7ds":
			try {
				nums = (int) _inputCheck_Num(datasel, 7)[0];
			} catch (Exception e) {}
			break;
		case "rx8ds":
			try {
				nums = (int) _inputCheck_Num(datasel, 8)[0];
			} catch (Exception e) {}
			break;
		// 这里验证选号类型
		case "sanmzhixfsq":
			try {
				if (datasel.get(0).length > 0 && datasel.get(1).length > 0 && datasel.get(2).length > 0) {
					for (int i = 0; i < datasel.get(0).length; i++) {
						for (int j = 0; j < datasel.get(1).length; j++) {
							for (int k = 0; k < datasel.get(2).length; k++) {
								int a = Integer.parseInt(datasel.get(0)[i].toString());
								int b = Integer.parseInt(datasel.get(1)[j].toString());
								int c = Integer.parseInt(datasel.get(2)[k].toString());
								if (a != b && a != c && b != c) {
									nums++;
								}
							}
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "sanmzuxfsq":
			try {
				int maxplace = 1;
				for (int i = 0; i < maxplace; i++) {
					int s = datasel.get(i).length;
					if (s > 2) {
						nums += s * (s - 1) * (s - 2) / 6;
					}
				}
			} catch (Exception e) {}
			break;
		case "ermzhixfsq":
			try {
				if (datasel.get(0).length > 0 && datasel.get(1).length > 0) {
					for (int i = 0; i < datasel.get(0).length; i++) {
						for (int j = 0; j < datasel.get(1).length; j++) {
							int a = Integer.parseInt(datasel.get(0)[i].toString());
							int b = Integer.parseInt(datasel.get(1)[j].toString());
							if (a != b) {
								nums++;
							}
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "ermzuxfsq":
			try {
				int maxplace = 1;
				for (int i = 0; i < maxplace; i++) {
					int s = datasel.get(i).length;
					if (s > 1) {
						nums += s * (s - 1) / 2;
					}
				}
			} catch (Exception e) {}
			break;
		case "bdw":
		case "dwd":
		case "dds":
		case "czw":
		case "rx1fs": // 任选1中1
			try {
				int maxplace = 0;
				if("bdw".equals(type) || "dds".equals(type) || "czw".equals(type) || "rx1fs".equals(type)) {
					maxplace = 1;
				}
				if("dwd".equals(type)) {
					maxplace = 3;
				}
				for (int i = 0; i < maxplace; i++) {
					nums += datasel.get(i).length;
				}
			} catch (Exception e) {}
			
			break;
		case "rx2fs": // 任选2中2
			try {
				int maxplace = 1;
				for (int i = 0; i < maxplace; i++) {
					int s = datasel.get(i).length;
					if (s > 1) {
						nums += s * (s - 1) / 2;
					}
				}
			} catch (Exception e) {}
			break;
		case "rx3fs": // 任选3中3
			try {
				int maxplace = 1;
				for (int i = 0; i < maxplace; i++) {
					int s = datasel.get(i).length;
					if (s > 2) {
						nums += s * (s - 1) * (s - 2) / 6;
					}
				}
			} catch (Exception e) {}
			break;
		case "rx4fs": // 任选4中4
			try {
				int maxplace = 1;
				for (int i = 0; i < maxplace; i++) {
					int s = datasel.get(i).length;
					if (s > 3) {
						nums += s * (s - 1) * (s - 2) * (s - 3) / 24;
					}
				}
			} catch (Exception e) {}
			break;
		case "rx5fs": // 任选5中5
			try {
				int maxplace = 1;
				for (int i = 0; i < maxplace; i++) {
					int s = datasel.get(i).length;
					if (s > 4) {
						nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) / 120;
					}
				}
			} catch (Exception e) {}
			break;
		case "rx6fs": // 任选6中6
			try {
				int maxplace = 1;
				for (int i = 0; i < maxplace; i++) {
					int s = datasel.get(i).length;
					if (s > 5) {
						nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) * (s - 5) / 720;
					}
				}
			} catch (Exception e) {}
			break;
		case "rx7fs": // 任选7中7
			try {
				int maxplace = 1;
				for (int i = 0; i < maxplace; i++) {
					int s = datasel.get(i).length;
					if (s > 6) {
						nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) * (s - 5) * (s - 6) / 5040;
					}
				}
			} catch (Exception e) {}
			break;
		case "rx8fs": // 任选8中8
			try {
				int maxplace = 1;
				for (int i = 0; i < maxplace; i++) {
					int s = datasel.get(i).length;
					if (s > 7) {
						nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) * (s - 5) * (s - 6) * (s - 7) / 40320;
					}
				}
			} catch (Exception e) {}
			break;
		case "rxtd2":
		case "rxtd3":
		case "rxtd4":
		case "rxtd5":
		case "rxtd6":
		case "rxtd7":
		case "rxtd8":
			try {
				if(datasel.size() != 2){
					return new Object[] {null, 0};
				}
				Object[] betArr = ArrayUtil.uniquelize(ArrayUtils.addAll(datasel.get(0), datasel.get(1)));
				int gcode = Integer.valueOf(type.substring(4));
				if (betArr.length >= gcode) {
                	nums = ArrayUtil.Combination(datasel.get(1).length, gcode - datasel.get(0).length);
                } else {
                	nums = 0;
                }
			} catch (Exception e) {	}
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
		List<Object[]> datasel = inputFormat(type, codes);
		if(datasel == null || datasel.size() == 0) {
			return null;
		}
		switch (type) {
			// 三码
			case "sanmzhixfsq":
			case "sanmzhixdsq":
			case "sanmzuxfsq":
			case "sanmzuxdsq":
			// 二码
			case "ermzhixfsq":
			case "ermzhixdsq":
			case "ermzuxfsq":
			case "ermzuxdsq":
			// 任选一中一
			case "rx1fs":
			case "rx1ds":
			// 任选二中二
			case "rx2fs":
			case "rx2ds":
			case "rxtd2":	
			// 任选三中三
			case "rx3fs":
			case "rx3ds":
			case "rxtd3":
			// 任选四中四
			case "rx4fs":
			case "rx4ds":
			case "rxtd4":
			// 任选五中五
			case "rx5fs":
			case "rx5ds":
			case "rxtd5":
			// 任选六中五
			case "rx6fs":
			case "rx6ds":
			case "rxtd6":
			// 任选七中五
			case "rx7fs":
			case "rx7ds":
			case "rxtd7":
			// 任选八中五
			case "rx8fs":
			case "rx8ds":
			case "rxtd8":
				int sanmMin = Integer.valueOf(playRules.getMinNum());
				int sanmMax = Integer.valueOf(playRules.getMaxNum());
				if (num > sanmMax) {
					return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, sanmMax, num, "注"};
				}
				if (num < sanmMin) {
					return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, sanmMin, num, "注"};
				}
				return null;
			// 不定胆
			case "bdw":
				int bdwMin = Integer.valueOf(playRules.getMinNum());
				int bdwMax = Integer.valueOf(playRules.getMaxNum());
				if (num > bdwMax) {
					return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, bdwMax, num, "码"};
				}
				if (num < bdwMin) {
					return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, bdwMin, num, "码"};
				}
				return null;
			// 定位胆
			case "dwd":
				int maxplace = 3;
				int dwdMin = Integer.valueOf(playRules.getMinNum());
				int dwdMax = Integer.valueOf(playRules.getMaxNum());
				for (int i = 0; i < maxplace; i++) {
					if (datasel.get(i).length > dwdMax) {
						return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, dwdMax, datasel.get(i).length, "码"};
					}
					if (datasel.get(i).length < dwdMin) {
						return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, dwdMin, datasel.get(i).length, "码"};
					}
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
		case "sanmzhixdsq":
		case "sanmzuxdsq":
			return (String) _inputCheck_Num(datasel, 3)[1];
		case "ermzhixdsq":
		case "ermzuxdsq":
			return (String) _inputCheck_Num(datasel, 2)[1];
		case "rx1ds":
			return (String) _inputCheck_Num(datasel, 1)[1];
		case "rx2ds":
			return (String) _inputCheck_Num(datasel, 2)[1];
		case "rx3ds":
			return (String) _inputCheck_Num(datasel, 3)[1];
		case "rx4ds":
			return (String) _inputCheck_Num(datasel, 4)[1];
		case "rx5ds":
			return (String) _inputCheck_Num(datasel, 5)[1];
		case "rx6ds":
			return (String) _inputCheck_Num(datasel, 6)[1];
		case "rx7ds":
			return (String) _inputCheck_Num(datasel, 7)[1];
		case "rx8ds":
			return (String) _inputCheck_Num(datasel, 8)[1];
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
			String tmp = arr[i].replace("-", "");
			if(StringUtil.isNotNull(tmp)) {
				list.add(formatCode(arr[i].replace("-", "").split(" ")));
			} else {
				list.add(new Object[]{});
			}
		}
		return list;
	}
	
	public static List<Object[]> _formatTextarea_Num(String codestr) {
		List<Object[]> list = new ArrayList<Object[]>();
		codestr = codestr.replaceAll(",|;", ";");
		String[] arr = codestr.split(";");
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
		case "sanmzhixfsq":
			return _formatSelect_Num(codestr, 0, 2);
		case "dwd":
			return _formatSelect_Num(codestr, 0, 0);
		case "ermzhixfsq":
			return _formatSelect_Num(codestr, 0, 3);
		case "rxtd1":
		case "rxtd2":
		case "rxtd3":
		case "rxtd4":
		case "rxtd5":
		case "rxtd6":
		case "rxtd7":
		case "rxtd8":
			return _formatSelect_Num(codestr, 0, 0);
		case "sanmzuxfsq":
		case "ermzuxfsq":
		case "bdw":
		case "rx1fs":
		case "rx2fs":
		case "rx3fs":
		case "rx4fs":
		case "rx5fs":
		case "rx6fs":
		case "rx7fs":
		case "rx8fs":
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
		case "sanmzhixdsq":
		case "sanmzuxdsq":
		case "ermzhixdsq":
		case "ermzuxdsq":
		case "rx1ds":
		case "rx2ds":
		case "rx3ds":
		case "rx4ds":
		case "rx5ds":
		case "rx6ds":
		case "rx7ds":
		case "rx8ds":
			return _formatTextarea_Num(codestr);
		case "dds":
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
		case "czw":
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
	 * 判断重复:01 02 03 04 05 06 07 08 09 10 11这种
	 * @param s
	 * @return
	 */
	public static boolean hasRepeat(String s) {
		String[] arr = s.split(" ");
		return hasRepeat(arr);
	}
	
	/**
	 * 判断重复:01,02,03,04,05,06,07,08,09,10,11
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
	 * 取值区间：[01-11]
	 * 单排长度：[1-11]
	 * 前后预留位验证：是 (m,n)
	 * 预留标记："-"
	 * 排数行数验证：是(minLength, maxLength)
	 * 多排重复验证：否
	 * 单排重复验证：是
	 */
	public static boolean testType01(String dstr, int m, int n, int minLength, int maxLength) {
		String[] arr = dstr.split(",");
		if(arr.length >= minLength && arr.length <= maxLength) {
			String regex = "^([0-9]{2}\\s{1}){0,10}[0-9]{2}$";
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
				if(!_numberCheck_Num(s)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 适用类型：单排数字[01-11]
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
				if(!_numberCheck_Num(s)) {
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
	
	/**
	 * 适用类型: 定位胆、任选
	 * 取值区间：[01-11]
	 * 单排长度：[1-11]
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
		String regex = "^([0-9]{2}\\s{1}){0,10}[0-9]{2}$";
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
			if(!_numberCheck_Num(s)) {
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
		case "sanmzhixfsq":
			return testType01(dstr, 0, 2, 5, 5);
		case "sanmzuxfsq":
			return testType02(dstr, 3, 11);
		case "ermzhixfsq":
			return testType01(dstr, 0, 3, 5, 5);
		case "ermzuxfsq":
			return testType02(dstr, 2, 11);
		case "bdw":
			return testType02(dstr, 1, 11);
		case "dwd":
			return testType05(dstr, 1, 3, 5);
		case "dds":
			try {
				String[] arr = dstr.split("\\|");
				String[] values = {"5单0双", "4单1双", "3单2双", "2单3双", "1单4双", "0单5双"};
				return testType03(arr, values, 1, 6);
			} catch (Exception e) {}
			break;
		case "czw":
			try {
				String[] arr = dstr.split(",");
				String[] values = {"03", "04", "05", "06", "07", "08", "09"};
				return testType03(arr, values, 1, 7);
			} catch (Exception e) {}
			break;
		case "rx1fs":
			return testType02(dstr, 1, 11);
		case "rx2fs":
			return testType02(dstr, 2, 11);
		case "rx3fs":
			return testType02(dstr, 3, 11);
		case "rx4fs":
			return testType02(dstr, 4, 11);
		case "rx5fs":
			return testType02(dstr, 5, 11);
		case "rx6fs":
			return testType02(dstr, 6, 11);
		case "rx7fs":
			return testType02(dstr, 7, 11);
		case "rx8fs":
			return testType02(dstr, 8, 11);
		case "rxtd2":
		case "rxtd3":
		case "rxtd4":
		case "rxtd5":
		case "rxtd6":
		case "rxtd7":
		case "rxtd8":
			int gcode = Integer.valueOf(type.substring(4));
			dstr = dstr.replaceAll(" ", ",");
			return testType02(dstr, gcode, 11);
		default:
			System.out.println("不需要验证的方法...." + type);
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		String dstr = "03,04,05,06,07,08,09";
		boolean inputValidate = inputValidate("czw", dstr);
		long t2 = System.currentTimeMillis();
		System.out.println(inputValidate + ":" + (t2 - t1) + "ms");
		
		System.out.println("rxtd5".substring(4));
		
		int nums = 0;
		String type = "rxtd3";
//		Object[] sel1 = new Object[]{"01","02"};
//		Object[] sel2 = new Object[]{"04","03","05","06","07"};
//		List<Object[]> datasel = new ArrayList<>();
//		datasel.add(sel1);
//		datasel.add(sel2);
		dstr = "01 02,01 04 05 06 07";
		List<Object[]> datasel = inputFormat(type, dstr);
		Object[] betArr = ArrayUtil.uniquelize(ArrayUtils.addAll(datasel.get(0), datasel.get(1)));
		for (Object object : betArr) {
			System.out.println(object.toString());
		}
		int gcode = Integer.valueOf(type.substring(4));
		if (betArr.length >= gcode) {
        	nums = ArrayUtil.Combination(datasel.get(1).length, gcode - datasel.get(0).length);
        } else {
        	nums = 0;
        }
		System.out.println(nums);
		dstr = dstr.replaceAll(" ", ",");
		System.out.println(testType02(dstr, gcode, 11));
	}
	
}