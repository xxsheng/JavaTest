package lottery.domains.utils.validate;

import javautils.StringUtil;
import javautils.regex.RegexUtil;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.web.content.validate.UserBetsValidate;
import org.apache.commons.lang.math.NumberUtils;

import java.util.*;

public class LotteryUtilPK10 {

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
				if(Integer.parseInt(t[i]) > 10 || Integer.parseInt(t[i]) < 1) {
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
		int nums = 0, tmp_nums = 1;
		switch (type) {
			// 这里验证输入框类型
			case "qianerzxds":
				try {
					nums = (int) _inputCheck_Num(datasel, 2)[0];
				} catch (Exception e) {}
				break;
			case "qiansanzxds":
				try {
					nums = (int) _inputCheck_Num(datasel, 3)[0];
				} catch (Exception e) {}
				break;
			case "qiansizxds":
				try {
					nums = (int) _inputCheck_Num(datasel, 4)[0];
				} catch (Exception e) {}
				break;
			case "qianwuzxds":
				try {
					nums = (int) _inputCheck_Num(datasel, 5)[0];
				} catch (Exception e) {}
				break;
			// 这里验证选号类型
			case "qiansanzxfs":
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
			case "qianerzxfs":
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
			case "qiansizxfs":
				try {
					if (datasel.get(0).length > 0 && datasel.get(1).length > 0 && datasel.get(2).length > 0 && datasel.get(3).length > 0) {
						for (int i = 0; i < datasel.get(0).length; i++) {
							for (int j = 0; j < datasel.get(1).length; j++) {
								for (int k = 0; k < datasel.get(2).length; k++) {
									for (int l = 0; l < datasel.get(3).length; l++) {
										int a = Integer.parseInt(datasel.get(0)[i].toString());
										int b = Integer.parseInt(datasel.get(1)[j].toString());
										int c = Integer.parseInt(datasel.get(2)[k].toString());
										int d = Integer.parseInt(datasel.get(3)[l].toString());
										if (a != b && a != c && a != d
												&& b != c && b != d
												&& c != d) {
											nums++;
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {}
				break;
			case "qianwuzxfs":
				try {
					if (datasel.get(0).length > 0 && datasel.get(1).length > 0 && datasel.get(2).length > 0 && datasel.get(3).length > 0 && datasel.get(4).length > 0) {
						for (int i = 0; i < datasel.get(0).length; i++) {
							for (int j = 0; j < datasel.get(1).length; j++) {
								for (int k = 0; k < datasel.get(2).length; k++) {
									for (int l = 0; l < datasel.get(3).length; l++) {
										for (int m = 0; m < datasel.get(3).length; m++) {
											int a = Integer.parseInt(datasel.get(0)[i].toString());
											int b = Integer.parseInt(datasel.get(1)[j].toString());
											int c = Integer.parseInt(datasel.get(2)[k].toString());
											int d = Integer.parseInt(datasel.get(3)[l].toString());
											int e = Integer.parseInt(datasel.get(4)[m].toString());
											if (a != b && a != c && a != d && a != e
													&& b != c && b != d && b != e
													&& c != d && c != e
													&& d != e) {
												nums++;
											}
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {}
				break;
			case "qwdingweidan":
			case "hwdingweidan":
			case "dw1dxds":
			case "dw2dxds":
			case "dw3dxds":
			case "dw4dxds":
			case "dw5dxds":
			case "pk10_dxdsgyhz":
			case "01vs10":
			case "02vs09":
			case "03vs08":
			case "04vs07":
			case "05vs06":
			case "pk10_hzgyhz":
			case "pk10_hzqshz":
				try {
					int maxplace;
					switch (type) {
						case "qwdingweidan": case "hwdingweidan":
							maxplace = 5;
							break;
						case "dw1dxds":
						case "dw2dxds":
						case "dw3dxds":
						case "dw4dxds":
						case "dw5dxds":
						case "pk10_dxdsgyhz":
						case "01vs10":
						case "02vs09":
						case "03vs08":
						case "04vs07":
						case "05vs06":
						case "pk10_hzgyhz":
						case "pk10_hzqshz":
							maxplace = 1;
							break;
						default:
							maxplace = 1;
							break;
					}
					for (int i = 0; i < maxplace; i++) {
						nums += datasel.get(i).length;
					}
				} catch (Exception e) {}
				break;
			default:
				try {
					int maxplace = 0;
					if("qianyi".equals(type)) {
						maxplace = 1;
					}
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
			// 前一
			case "qianyi":
			// 前二
			case "qianerzxfs":
			case "qianerzxds":
			// 前三
			case "qiansanzxfs":
			case "qiansanzxds":
			// 前四
			case "qiansizxfs":
			case "qiansizxds":
			// 前五
			case "qianwuzxfs":
			case "qianwuzxds":
			// 和值冠亚和值
			case "pk10_hzgyhz":
			// 和值前三和值
			case "pk10_hzqshz":
				int maxNum = Integer.valueOf(playRules.getMaxNum());
				if (num > maxNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, maxNum, num, "注"};
				}
				int minNum = Integer.valueOf(playRules.getMinNum());
				if (num < minNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, minNum, num, "注"};
				}
				return null;
			// 定位胆
			case "qwdingweidan":
			case "hwdingweidan":
				int maxplace = 5;
				int dingweidanMaxNum = Integer.valueOf(playRules.getMaxNum());
				int dingweidanMinNum = Integer.valueOf(playRules.getMinNum());
				for (int i = 0; i < maxplace; i++) {
					if (datasel.get(i).length > dingweidanMaxNum) {
						return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, dingweidanMaxNum, datasel.get(i).length, "码"};
					}
					if (datasel.get(i).length < dingweidanMinNum) {
						return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, dingweidanMinNum, datasel.get(i).length, "码"};
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
			case "qianerzxds":
				return (String) _inputCheck_Num(datasel, 2)[1];
			case "qiansanzxds":
				return (String) _inputCheck_Num(datasel, 3)[1];
			case "qiansizxds":
				return (String) _inputCheck_Num(datasel, 4)[1];
			case "qianwuzxds":
				return (String) _inputCheck_Num(datasel, 5)[1];
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
			case "qianyi":
			case "pk10_hzgyhz":
			case "pk10_hzqshz":
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
			case "qianerzxfs":
			case "qiansanzxfs":
			case "qiansizxfs":
			case "qianwuzxfs":
			case "qwdingweidan":
			case "hwdingweidan":
			case "dw1dxds":
			case "dw2dxds":
			case "dw3dxds":
			case "dw4dxds":
			case "dw5dxds":
			case "pk10_dxdsgyhz":
			case "01vs10":
			case "02vs09":
			case "03vs08":
			case "04vs07":
			case "05vs06":
				return _formatSelect_Num(codestr, 0, 0);
			case "qianerzxds":
			case "qiansanzxds":
			case "qiansizxds":
			case "qianwuzxds":
				return _formatTextarea_Num(codestr);
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
	 * 取值区间：[01-10]
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
			String regex = "^([0-9]{2}\\s{1}){0,9}[0-9]{2}$";
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
	 * 适用类型：单排数字[01-10]
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
	 * 适用类型：多排自定义
	 */
	public static boolean testType04(String dstr, String[] values, int minLength, int maxLength) {
		String[] arr = dstr.split(" ");

		if (hasRepeat(arr)) {
			return false;
		}

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
	 * 取值区间：[01-10]
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
		String regex = "^([0-9]{2}\\s{1}){0,9}[0-9]{2}$";
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

	/**
	 * 适用类型：单排自定义连串数字，不允许重复
	 */
	public static boolean testSingleRowNums(String dstr, String split, int from, int to) {
		String[] arr = dstr.split(split);
		boolean hasRepeat = hasRepeat(arr);
		if (hasRepeat) {
			return false;
		}
		if (arr.length > 0) {
			for (String s : arr) {
				if (!NumberUtils.isDigits(s)) {
					return false;
				}
				int num = Integer.valueOf(s);
				if (num < from || num > to) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	public static boolean inputValidate(String type, String dstr) {
		switch (type) {
			case "qianyi":
				return testType02(dstr, 1, 10);
			case "qianerzxfs":
				return testType01(dstr, 0, 0, 2, 2);
			case "qiansanzxfs":
				return testType01(dstr, 0, 0, 3, 3);
			case "qiansizxfs":
				return testType01(dstr, 0, 0, 4, 4);
			case "qianwuzxfs":
				return testType01(dstr, 0, 0, 5, 5);
			case "qwdingweidan":
			case "hwdingweidan":
				return testType05(dstr, 1, 5, 5);
			case "dw1dxds":
			case "dw2dxds":
			case "dw3dxds":
			case "dw4dxds":
			case "dw5dxds":
			case "pk10_dxdsgyhz":
				try {
					String[] values = {"大", "小", "单", "双"};
					return testType04(dstr, values, 1, 4);
				} catch (Exception e) {}
				break;
			case "01vs10":
			case "02vs09":
			case "03vs08":
			case "04vs07":
			case "05vs06":
				try {
					String[] values = {"龙", "虎"};
					return testType04(dstr, values, 1, 2);
				} catch (Exception e) {}
				break;
			case "pk10_hzgyhz":
				try {
					return testSingleRowNums(dstr, ",", 3, 19);
				} catch (Exception e) {}
				break;
			case "pk10_hzqshz":
				try {
					return testSingleRowNums(dstr, ",", 6, 27);
				} catch (Exception e) {}
				break;
			default:
				System.out.println("不需要验证的方法...." + type);
				return true;
		}
		return false;
	}

	public static void main(String[] args) {
		// long t1 = System.currentTimeMillis();
		// String dstr = "01,01";
		// boolean inputValidate = inputValidate("qianerzxfs", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		// long t1 = System.currentTimeMillis();
		// String dstr = "01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10";
		// boolean inputValidate = inputValidate("qiansizxfs", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		// long t1 = System.currentTimeMillis();
		// String dstr = "01,01,01,01";
		// boolean inputValidate = inputValidate("qiansizxfs", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		// long t1 = System.currentTimeMillis();
		// String dstr = "01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10";
		// boolean inputValidate = inputValidate("qianwuzxfs", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		// long t1 = System.currentTimeMillis();
		// String dstr = "01,01,01,01,01";
		// boolean inputValidate = inputValidate("qianwuzxfs", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		// long t1 = System.currentTimeMillis();
		// String dstr = "01 02 03 04 05";
		// boolean inputValidate = inputValidate("qianwuzxds", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		// long t1 = System.currentTimeMillis();
		// String dstr = "01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10";
		// boolean inputValidate = inputValidate("qwdingweidan", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		// long t1 = System.currentTimeMillis();
		// String dstr = "01,-,01,-,-";
		// boolean inputValidate = inputValidate("qwdingweidan", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		long t1 = System.currentTimeMillis();
		String dstr = "大 单 单";
		boolean inputValidate = inputValidate("dw1dxds", dstr);
		long t2 = System.currentTimeMillis();
		System.out.println(inputValidate + ":" + (t2 - t1) + "ms");
		//
		// long t1 = System.currentTimeMillis();
		// String dstr = "龙 虎";
		// boolean inputValidate = inputValidate("01vs10", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");
	}

}