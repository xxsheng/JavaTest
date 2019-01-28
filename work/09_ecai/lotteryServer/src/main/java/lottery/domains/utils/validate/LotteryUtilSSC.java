package lottery.domains.utils.validate;

import javautils.StringUtil;
import javautils.regex.RegexUtil;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.web.content.validate.UserBetsValidate;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class LotteryUtilSSC {
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
		// 这里是验证输入框
		case "wxzhixds":
			try {
				nums = (int) _inputCheck_Num(datasel, 5, false)[0];
			} catch (Exception e) {}
			break;
		case "sixzhixdsh":
		case "sixzhixdsq":
			try {
				nums = (int) _inputCheck_Num(datasel, 4, false)[0];
			} catch (Exception e) {}
			break;
		case "sxzhixdsh":
		case "sxzhixdsz":
		case "sxzhixdsq":
			try {
				nums = (int) _inputCheck_Num(datasel, 3, false)[0];
			} catch (Exception e) {}
			break;
		case "sxhhzxh":
		case "sxhhzxz":
		case "sxhhzxq":
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
		case "wxzux120":
			try {
				int s = datasel.get(0).length;
				if(s > 4) {
					nums += ArrayUtil.Combination(s, 5);
				}
			} catch (Exception e) {}
			break;
		case "wxzux60":
		case "wxzux30":
		case "wxzux20":
		case "wxzux10":
		case "wxzux5":
			try {
				int[] minchosen = new int[2];
				if("wxzux60".equals(type)) {
					minchosen[0] = 1;
					minchosen[1] = 3;
				}
				if("wxzux30".equals(type)) {
					minchosen[0] = 2;
					minchosen[1] = 1;
				}
				if("wxzux20".equals(type)) {
					minchosen[0] = 1;
					minchosen[1] = 2;
				}
				if("wxzux10".equals(type) || "wxzux5".equals(type)) {
					minchosen[0] = 1;
					minchosen[1] = 1;
				}
				if(datasel.get(0).length >= minchosen[0] && datasel.get(1).length >= minchosen[1]) {
					int h = ArrayUtil.intersect(datasel.get(0), datasel.get(1)).length;
					tmp_nums = ArrayUtil.Combination(datasel.get(0).length, minchosen[0]) * ArrayUtil.Combination(datasel.get(1).length, minchosen[1]);
					if (h > 0) {
						if ("wxzux60".equals(type)) {
							tmp_nums -= ArrayUtil.Combination(h, 1) * ArrayUtil.Combination(datasel.get(1).length - 1, 2);
						}
						if ("wxzux30".equals(type)) {
							tmp_nums -= ArrayUtil.Combination(h, 2) * ArrayUtil.Combination(2, 1);
							if (datasel.get(0).length - h > 0) {
								tmp_nums -= ArrayUtil.Combination(h, 1) * ArrayUtil.Combination(datasel.get(0).length - h, 1);
							}
						}
						if ("wxzux20".equals(type)) {
							tmp_nums -= ArrayUtil.Combination(h, 1) * ArrayUtil.Combination(datasel.get(1).length - 1, 1);
						}
						if ("wxzux10".equals(type) || "wxzux5".equals(type)) {
							tmp_nums -= ArrayUtil.Combination(h, 1);
						}
					}
					nums += tmp_nums;
				}
			} catch (Exception e) {}
			break;
		case "sixzux24h":
		case "sixzux24q":
			try {
				int s = datasel.get(0).length;
				if (s > 3) {
					nums += ArrayUtil.Combination(s, 4);
				}
			} catch (Exception e) {}
			break;
		case "sixzux6h":
		case "sixzux6q":
			try {
				int[] minchosen = new int[2];
				minchosen[0] = 2;
				if (datasel.get(0).length >= minchosen[0]) {
					nums += ArrayUtil.Combination(datasel.get(0).length, minchosen[0]);
				}
			} catch (Exception e) {}
			break;
		case "sixzux12h":
		case "sixzux12q":
		case "sixzux4h":
		case "sixzux4q":
			try {
				int[] minchosen = new int[2];
				if("sixzux12h".equals(type) || "sixzux12q".equals(type)) {
					minchosen[0] = 1;
					minchosen[1] = 2;
				}
				if("sixzux4h".equals(type) || "sixzux4q".equals(type)) {
					minchosen[0] = 1;
					minchosen[1] = 1;
				}
				if (datasel.get(0).length >= minchosen[0] && datasel.get(1).length >= minchosen[1]) {
					int h = ArrayUtil.intersect(datasel.get(0), datasel.get(1)).length;
					tmp_nums = ArrayUtil.Combination(datasel.get(0).length, minchosen[0]) * ArrayUtil.Combination(datasel.get(1).length, minchosen[1]);
					if (h > 0) {
						if ("sixzux12h".equals(type) || "sixzux12q".equals(type)) {
							tmp_nums -= ArrayUtil.Combination(h, 1) * ArrayUtil.Combination(datasel.get(1).length - 1, 1);
						}
						if ("sixzux4h".equals(type) || "sixzux4q".equals(type)) {
							tmp_nums -= ArrayUtil.Combination(h, 1);
						}
					}
					nums += tmp_nums;
				}
			} catch (Exception e) {}
			break;
		case "sxzuxzsh":
		case "sxzuxzsz":
		case "sxzuxzsq":
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
		case "sxzuxzlh":
		case "sxzuxzlz":
		case "sxzuxzlq":
		case "bdwwx3m":
			try {
				int maxplace = 1;
				if(datasel.size() == maxplace) {
					for (int i = 0; i < maxplace; i++) {
						int s = datasel.get(i).length;
						// 组六或不定胆五星三码必须选三位或者以上
						if (s > 2) {
							nums += s * (s - 1) * (s - 2) / 6;
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "wxzhixzh":
		case "sixzhixzhh":
		case "sixzhixzhq":
			try {
				int maxplace = 0;
				if("wxzhixzh".equals(type)) {
					maxplace = 5;
				}
				if("sixzhixzhh".equals(type) || "sixzhixzhq".equals(type)) {
					maxplace = 4;
				}
				if(datasel.size() == maxplace) {
					for (int i = 0; i < maxplace; i++) {
						// 有位置上没有选择
						if (datasel.get(i).length == 0) {
							tmp_nums = 0; break;
						}
						tmp_nums *= datasel.get(i).length;
					}
					nums += tmp_nums * maxplace;
				}
			} catch (Exception e) {}
			break;
		case "sxzhixhzh":
		case "sxzhixhzz":
		case "sxzhixhzq":
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
		case "sxzuxhzh":
        case "sxzuxhzz":
        case "sxzuxhzq":
        case "exzuxhzh":
        case "exzuxhzq":
        	try {
				JSONObject cc = JSONObject.fromObject("{ 1: 1, 2: 2, 3: 2, 4: 4, 5: 5, 6: 6, 7: 8, 8: 10, 9: 11, 10: 13, 11: 14, 12: 14, 13: 15, 14: 15, 15: 14, 16: 14, 17: 13, 18: 11, 19: 10, 20: 8, 21: 6, 22: 5, 23: 4, 24: 2, 25: 2, 26: 1}");
				if("exzuxhzh".equals(type) || "exzuxhzq".equals(type)) {
					cc = JSONObject.fromObject("{ 1: 1, 2: 1, 3: 2, 4: 2, 5: 3, 6: 3, 7: 4, 8: 4, 9: 5, 10: 4, 11: 4, 12: 3, 13: 3, 14: 2, 15: 2, 16: 1, 17: 1}");
				}
				for (int i = 0; i < datasel.get(0).length; i++) {
					Object val = cc.get(datasel.get(0)[i]);
					if(val != null) {
						nums += (int)val;
					}
				}
			} catch (Exception e) {}
        	break;
		case "rx2fs":
		case "rx3fs":
		case "rx4fs":
			try {
				int minplace = 0;
				if("rx2fs".equals(type)) {
					minplace = 2;
				}
				if("rx3fs".equals(type)) {
					minplace = 3;
				}
				if("rx4fs".equals(type)) {
					minplace = 4;
				}
				if(datasel.size() >= minplace) {
					int l = (int) ArrayUtil.Combination(datasel.size(), minplace);
					for (int i = 0; i < l; i++) {
						tmp_nums = 1;
						List<Object[]> data = ArrayUtil.CombinationValue(datasel, minplace, i);
						for (int j = 0; j < data.size(); j++) {
							tmp_nums *= data.get(j).length;
						}
						nums += tmp_nums;
					}
				}
			} catch (Exception e) {}
			break;
		case "rx2ds":
		case "rx3ds":
		case "rx4ds":
			try {
				if(datasel.size() > 1) {
					int place = 0;
					for (int i = 0; i < datasel.get(0).length; i++) {
						if("√".equals(datasel.get(0)[i])) place++;
					}
					List<Object[]> newsel = new ArrayList<Object[]>();
					for (int i = 1; i < datasel.size(); i++) {
						newsel.add(datasel.get(i));
					}
					int m = 0;
					if("rx2ds".equals(type)) {
						m = 2;
					}
					if("rx3ds".equals(type)) {
						m = 3;
					}
					if("rx4ds".equals(type)) {
						m = 4;
					}
					// 任选2必须大于选了2位以上才能组成组合
					if(place >= m) {
						int h = ArrayUtil.Combination(place, m);
						if(h > 0) {// 组合数必须大于0
							nums += (int)_inputCheck_Num(newsel, m, false)[0];
							nums *=  h;
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "rx3hhzx":
			try {
				if(datasel.size() > 1) {
					int place = 0;
					for (int i = 0; i < datasel.get(0).length; i++) {
						if("√".equals(datasel.get(0)[i])) place++;
					}
					List<Object[]> newsel = new ArrayList<>();
					for (int i = 1; i < datasel.size(); i++) {
						newsel.add(datasel.get(i));
					}
					int m = 3; // 必须选择3个以上位置才可以
					if(place >= m) {
						int h = ArrayUtil.Combination(place, m);
						if(h > 0) {// 组合数必须大于0
							nums += (int)_inputCheck_Num(newsel, 3, true)[0];
							nums *=  h;
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "rx3z3":
			try {
				int maxplace = 1;
				if(datasel.size() > 1) {
					int place = 0;
					for (int i = 0; i < datasel.get(0).length; i++) {
						if("√".equals(datasel.get(0)[i])) place++;
					}
					Object[] newsel = datasel.get(1);
					int m = 3;
					// 任选3必须大于选了3位以上才能组成组合
					if(place >= m) {
						int h = ArrayUtil.Combination(place, m);
						if(h > 0) {// 组合数必须大于0
							for (int i = 0; i < maxplace; i++) {
								int s = newsel.length;
								// 组三必须选两位或者以上
								if (s > 1) {
									nums += s * (s - 1);
								}
							}
							nums *= h;
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "rx3z6":
			try {
				int maxplace = 1;
				if(datasel.size() > 1) {
					int place = 0;
					for (int i = 0; i < datasel.get(0).length; i++) {
						if("√".equals(datasel.get(0)[i])) place++;
					}
					Object[] newsel = datasel.get(1);
					int m = 3;
					// 任选3必须大于选了3位以上才能组成组合
					if(place >= m) {
						int h = ArrayUtil.Combination(place, m);
						if(h > 0) {// 组合数必须大于0
							for (int i = 0; i < maxplace; i++) {
								int s = newsel.length;
								// 组六必须选三位或者以上
								if (s > 2) {
									nums += s * (s - 1) * (s - 2) / 6;
								}
							}
							nums *= h;
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "rx2zx":
			try {
				int maxplace = 1;
				if(datasel.size() > 1) {
					int place = 0;
					for (int i = 0; i < datasel.get(0).length; i++) {
						if("√".equals(datasel.get(0)[i])) place++;
					}
					Object[] newsel = datasel.get(1);
					int m = 2;
					// 任选2必须大于选了2位以上才能组成组合
					if(place >= m) {
						int h = ArrayUtil.Combination(place, m);
						if(h > 0) {// 组合数必须大于0
							for (int i = 0; i < maxplace; i++) {
								int s = newsel.length;
								// 二码不定位必须选两位或者以上
								if (s > 1) {
									nums += s * (s - 1) / 2;
								}
							}
							nums *= h;
						}
					}
				}
			} catch (Exception e) {}
			break;
		case "dw": //定位胆所有在一起特殊处理
			try {
				int maxplace = 5;
				if(datasel.size() == maxplace) {
					for (int i = 0; i < maxplace; i++) {
						nums += datasel.get(i).length;
					}
				}
			} catch (Exception e) {}
			break;
		case "bdw2mh":
		case "bdw2mz":
		case "bdw2mq":
		case "bdwsix2mq":
		case "bdwsix2mh":
		case "bdwwx2m":
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
				case "wxzhixfs":
					maxplace = 5;
					break;
				case "sixzhixfsh":
				case "sixzhixfsq":
					maxplace = 4;
					break;
				case "sxzhixfsh":
				case "sxzhixfsz":
				case "sxzhixfsq":
					maxplace = 3;
					break;
				case "exzhixfsh":
				case "exzhixfsq":
				case "dxdsh":
				case "dxdsq":
					maxplace = 2;
					break;
				case "bdw1mh":
				case "bdw1mz":
				case "bdw1mq":
				case "bdwsix1mq":
				case "bdwsix1mh":
				case "qwyffs":
				case "qwhscs":
				case "qwsxbx":
				case "qwsjfc":
				case "longhuhewq":
				case "longhuhewb":
				case "longhuhews":
				case "longhuhewg":
				case "longhuheqb":
				case "longhuheqs":
				case "longhuheqg":
				case "longhuhebs":
				case "longhuhebg":
				case "longhuhesg":
				case "wxdxds":
				case "sscniuniu":
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
			// 五星
			case "wxzhixfs":
			case "wxzhixds":
			case "wxzux120":
			case "wxzux60":
			case "wxzux30":
			case "wxzux20":
			case "wxzux10":
			case "wxzux5":
			case "wxzhixzh":
			// 四星
			case "sixzhixfsh":
			case "sixzhixdsh":
			case "sixzux24h":
			case "sixzux12h":
			case "sixzux6h":
			case "sixzux4h":
			case "sixzhixfsq":
			case "sixzhixdsq":
			case "sixzux24q":
			case "sixzux12q":
			case "sixzux6q":
			case "sixzux4q":
			// 四星组合
			case "sixzhixzhh":
			case "sixzhixzhq":
			// 三星
			case "sxzhixfsh":
			case "sxzhixdsh":
			case "sxzhixhzh":
			case "sxhhzxh":
			case "sxzhixfsz":
			case "sxzhixdsz":
			case "sxzhixhzz":
			case "sxhhzxz":
			case "sxzhixfsq":
			case "sxzhixdsq":
			case "sxzhixhzq":
			case "sxhhzxq":
			case "sxzuxhzh":
            case "sxzuxhzz":
            case "sxzuxhzq":
			// 二星
			case "exzhixfsh":
			case "exzhixdsh":
			case "exzhixhzh":
			case "dxdsh":
			case "exzuxfsh":
			case "exzuxdsh":
			case "exzhixfsq":
			case "exzhixdsq":
			case "exzhixhzq":
            case "exzuxhzh":
            case "exzuxhzq":
			case "dxdsq":
			case "exzuxfsq":
			case "exzuxdsq":
			// 一帆风顺
			case "qwyffs":
				int wxMinNum = Integer.valueOf(playRules.getMinNum());
				if (num < wxMinNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, wxMinNum, num, "注"};
				}

				int wxMaxNum = Integer.valueOf(playRules.getMaxNum());
				if (num > wxMaxNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, wxMaxNum, num, "注"};
				}
				return null;
			// 定位胆
			case "dw":
				int dwMinNum = Integer.valueOf(playRules.getMinNum());
				int dwMaxNum = Integer.valueOf(playRules.getMaxNum());
				int maxplace = 5;
				for (int i = 0; i < maxplace; i++) {
					if (datasel.get(i).length > dwMaxNum) {
						return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, dwMaxNum, datasel.get(i).length, "码"};
					}
					if (datasel.get(i).length < dwMinNum) {
						return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, dwMinNum, datasel.get(i).length, "码"};
					}
				}
				return null;
			// 不定胆
			case "bdw1mh":
			case "bdw1mz":
			case "bdw1mq":
			case "bdw2mh":
			case "bdw2mz":
			case "bdw2mq":
			case "bdwsix1mq":
			case "bdwsix2mq":
			case "bdwsix1mh":
			case "bdwsix2mh":
			case "bdwwx2m":
			case "bdwwx3m":
				int bdwMinNum = Integer.valueOf(playRules.getMinNum());
				int bdwMaxNum = Integer.valueOf(playRules.getMaxNum());
				if (datasel.get(0).length > bdwMaxNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, bdwMaxNum, datasel.get(0).length, "码"};
				}
				if (datasel.get(0).length < bdwMinNum) {
					return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, bdwMinNum, datasel.get(0).length, "码"};
				}
				return null;
			// 任选
			case "rx2fs":
			case "rx3fs":
			case "rx4fs":
			case "rx2ds":
			case "rx3ds":
			case "rx4ds":
				int m = 0; // 组合基数
				int max = Integer.valueOf(playRules.getMaxNum()); // 每组最大注数
				int min = Integer.valueOf(playRules.getMinNum()); // 每组最小注数
				if (type.equals("rx2fs") || type.equals("rx2ds")) {
					m = 2;
				}
				else if (type.equals("rx3fs") || type.equals("rx3ds")) {
					m = 3;
				}
				else if (type.equals("rx4fs") || type.equals("rx4ds")) {
					m = 4;
				}

				// 选择位置
				int place = 0;
				if (type.equals("rx2fs") || type.equals("rx3fs") || type.equals("rx4fs")) {
					for (int i = 0; i < 5; i++) {
						if (datasel.get(i).length > 0) place++;
					}
				}
				else {
					for (int i = 0; i < datasel.get(0).length; i++) {
						if (datasel.get(0)[i] == "√") place++;
					}
				}
				if (place >= m) {
					int h = ArrayUtil.Combination(place, m);
					if (h > 0) {
						int maxNums = h * max;
						int minNums = h * min;
						if (num > maxNums) {
							return new Object[]{UserBetsValidate.MIN_MAX_EXCEED_MAX, maxNums, num, "注"};
						}
						if (num < minNums) {
							return new Object[]{UserBetsValidate.MIN_MAX_BELOW_MIN, minNums, num, "注"};
						}
					}
				}
				return null;
			default :
				return null;
		}
	}

	/**
	 * 替换号码，主要针对输入框
	 */
	public static String replaceNumbers(String type, String dstr, List<Object[]> datasel) {
		switch (type) {
		case "wxzhixds":
			return (String) _inputCheck_Num(datasel, 5, false)[1];
		case "sixzhixdsh":
		case "sixzhixdsq":
			return (String) _inputCheck_Num(datasel, 4, false)[1];
		case "sxzhixdsh":
		case "sxzhixdsz":
		case "sxzhixdsq":
			return (String) _inputCheck_Num(datasel, 3, false)[1];
		case "sxhhzxh":
		case "sxhhzxz":
		case "sxhhzxq":
			return (String) _inputCheck_Num(datasel, 3, true)[1];
		case "exzhixdsh":
		case "exzhixdsq":
			return (String) _inputCheck_Num(datasel, 2, false)[1];
		case "exzuxdsh":
		case "exzuxdsq":
			return (String) _inputCheck_Num(datasel, 2, true)[1];
		case "rx2ds":
		case "rx3ds":
		case "rx4ds":
			int m = 0;
			if("rx2ds".equals(type)) {
				m = 2;
			}
			if("rx3ds".equals(type)) {
				m = 3;
			}
			if("rx4ds".equals(type)) {
				m = 4;
			}
			Object[] space = datasel.get(0);
			List<Object[]> newsel = new ArrayList<Object[]>();
			for (int i = 1; i < datasel.size(); i++) {
				newsel.add(datasel.get(i));
			}
			return "[" + StringUtil.transArrayToString(space) + "]" + (String) _inputCheck_Num(datasel, m, false)[1];
		case "rx3hhzx":
			m = 3;
			space = datasel.get(0);
			newsel = new ArrayList<>();
			for (int i = 1; i < datasel.size(); i++) {
				newsel.add(datasel.get(i));
			}
			return "[" + StringUtil.transArrayToString(space) + "]" + (String) _inputCheck_Num(newsel, m, true)[1];
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
		case "wxzhixds":
		case "sixzhixdsh":
		case "sixzhixdsq":
		case "sxzhixdsh":
		case "sxzhixdsz":
		case "sxzhixdsq":
		case "sxhhzxh":
		case "sxhhzxz":
		case "sxhhzxq":
		case "exzhixdsh":
		case "exzhixdsq":
		case "exzuxdsh":
		case "exzuxdsq":
			return _formatTextarea_Num(codestr);
		case "rx2ds":
		case "rx3ds":
		case "rx4ds":
		case "rx3hhzx":
			try {
				List<Object[]> list = new ArrayList<Object[]>();
				String space = StringUtil.substring(codestr, "[", "]", false);
				list.add(space.split(","));
				String codes = codestr.substring(11);
				list.addAll(_formatTextarea_Num(codes));
				return list;
			} catch (Exception e) {}
			break;
		case "rx3z3":
		case "rx3z6":
		case "rx2zx":
			try {
				List<Object[]> list = new ArrayList<Object[]>();
				String space = StringUtil.substring(codestr, "[", "]", false);
				list.add(space.split(","));
				String codes = codestr.substring(11);
				
				String[] arr = codes.split(",");
				Object[] tmp = new Object[arr.length];
				for (int i = 0; i < arr.length; i++) {
					tmp[i] = arr[i];
				}
				
				list.add(formatCode(tmp));
				return list;
			} catch (Exception e) {}
			break;
		case "wxzux120":
		case "sixzux24h":
		case "sixzux24q":
		case "sixzux6h":
		case "sixzux6q":
		case "sxzuxzsh":
		case "sxzuxzsz":
		case "sxzuxzsq":
		case "sxzuxzlh":
		case "sxzuxzlz":
		case "sxzuxzlq":
		case "exzuxfsh":
		case "exzuxfsq":
		case "bdw1mh":
		case "bdw1mz":
		case "bdw1mq":
		case "bdw2mh":
		case "bdw2mz":
		case "bdw2mq":
		case "bdwsix1mq":
		case "bdwsix2mq":
		case "bdwsix1mh":
		case "bdwsix2mh":
		case "bdwwx2m":
		case "bdwwx3m":
		case "qwyffs":
		case "qwhscs":
		case "qwsxbx":
		case "qwsjfc":
		case "sxzhixhzh":
		case "sxzhixhzz":
		case "sxzhixhzq":
		case "exzhixhzh":
		case "exzhixhzq":
		case "sxzuxhzh":
        case "sxzuxhzz":
        case "sxzuxhzq":
        case "exzuxhzh":
        case "exzuxhzq":
		case "longhuhewq":
		case "longhuhewb":
		case "longhuhews":
		case "longhuhewg":
		case "longhuheqb":
		case "longhuheqs":
		case "longhuheqg":
		case "longhuhebs":
		case "longhuhebg":
		case "longhuhesg":
		case "wxdxds":
		case "sscniuniu":
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
		case "sixzhixfsh":
		case "sixzhixzhh":
			return _formatSelect_Num(codestr, 1, 0);
		case "sixzhixfsq":
		case "sixzhixzhq":
			return _formatSelect_Num(codestr, 0, 1);
		case "sxzhixfsh":
			return _formatSelect_Num(codestr, 2, 0);
		case "sxzhixfsz":
			return _formatSelect_Num(codestr, 1, 1);
		case "sxzhixfsq":
			return _formatSelect_Num(codestr, 0, 2);
		case "exzhixfsh":
			return _formatSelect_Num(codestr, 3, 0);
		case "exzhixfsq":
			return _formatSelect_Num(codestr, 0, 3);
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

	public static boolean inputValidate(String type, String dstr) {
		switch (type) {
		case "wxzhixfs":
		case "wxzhixzh":
			return testType01(dstr, 0, 0, 5, 5);
		// 五星单式
		case "wxzhixds":
			return testCodes(dstr, " ", 5, LEGAL_CODES);
		case "wxzux120":
			return testType02(dstr, 5, 10);
		case "wxzux60":
		case "wxzux30":
		case "wxzux20":
		case "wxzux10":
		case "wxzux5":
			return testType01(dstr, 0, 0, 2, 2);
		case "sixzhixfsh":
		case "sixzhixzhh":
			return testType01(dstr, 1, 0, 5, 5);
		// 四星单式
		case "sixzhixdsh":
		case "sixzhixdsq":
			return testCodes(dstr, " ", 4, LEGAL_CODES);
		case "sixzhixfsq":
		case "sixzhixzhq":
			return testType01(dstr, 0, 1, 5, 5);
		case "sixzux24h":
		case "sixzux24q":
			return testType02(dstr, 4, 10);
		case "sixzux6h":
		case "sixzux6q":
			return testType02(dstr, 2, 10);
		case "sixzux12h":
		case "sixzux12q":
		case "sixzux4h":
		case "sixzux4q":
			return testType01(dstr, 0, 0, 2, 2);
		case "sxzhixfsh":
			return testType01(dstr, 2, 0, 5, 5);
		case "sxzhixfsz":
			return testType01(dstr, 1, 1, 5, 5);
		case "sxzhixfsq":
			return testType01(dstr, 0, 2, 5, 5);
		case "sxzuxzsh":
		case "sxzuxzsz":
		case "sxzuxzsq":
			return testType02(dstr, 2, 10);
		case "sxzuxzlh":
		case "sxzuxzlz":
		case "sxzuxzlq":
			return testType02(dstr, 3, 10);
		case "sxzhixhzh":
		case "sxzhixhzz":
		case "sxzhixhzq":
			try {
				int length = 28;
				String[] values = new String[length];
				for (int i = 0; i < length; i++) {
					values[i] = String.valueOf(i);
				}
				return testType03(dstr, values, 1, length);
			} catch (Exception e) {}
			break;
		case "sxzuxhzh":
		case "sxzuxhzz":
		case "sxzuxhzq":
			try {
				int length = 26;
				String[] values = new String[length];
				for (int i = 0; i < length; i++) {
					values[i] = String.valueOf(i + 1);
				}
				return testType03(dstr, values, 1, length);
			} catch (Exception e) {}
			break;
		// 三星单式
		case "sxzhixdsh":
		case "sxzhixdsz":
		case "sxzhixdsq":
		// 三星混合组选
		case "sxhhzxh":
		case "sxhhzxz":
		case "sxhhzxq":
			return testCodes(dstr, " ", 3, LEGAL_CODES);
		case "exzhixfsh":
			return testType01(dstr, 3, 0, 5, 5);
		case "exzhixfsq":
			return testType01(dstr, 0, 3, 5, 5);
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
		case "exzuxhzq":
		case "exzuxhzh":
			try {
				int length = 17;
				String[] values = new String[length];
				for (int i = 0; i < length; i++) {
					values[i] = String.valueOf(i + 1);
				}
				return testType03(dstr, values, 1, length);
			} catch (Exception e) {}
			break;
		// 二星直选单式&二星组选单式
		case "exzhixdsh":
		case "exzhixdsq":
		case "exzuxdsh":
		case "exzuxdsq":
			return testCodes(dstr, " ", 2, LEGAL_CODES);
		case "dxdsh":
		case "dxdsq":
			try {
				String[] values = {"大", "小", "单", "双"};
				return testType04(dstr, values, 2, 2);
			} catch (Exception e) {}
			break;
		case "bdw1mh":
		case "bdw1mz":
		case "bdw1mq":
		case "bdwsix1mq":
		case "bdwsix1mh":
			return testType02(dstr, 1, 10);
		case "bdw2mh":
		case "bdw2mz":
		case "bdw2mq":
		case "bdwsix2mq":
		case "bdwsix2mh":
		case "bdwwx2m":
			return testType02(dstr, 2, 10);
		case "bdwwx3m":
			return testType02(dstr, 3, 10);
		case "qwyffs":
		case "qwhscs":
		case "qwsxbx":
		case "qwsjfc":
			return testType02(dstr, 1, 10);
		case "dw":
			return testType05(dstr, 1, 5, 5);
		case "rx2fs":
			return testType05(dstr, 2, 5, 5);
		case "rx3fs":
			return testType05(dstr, 3, 5, 5);
		case "rx4fs":
			return testType05(dstr, 4, 5, 5);
		case "rx3z3":
			try {
				String space = StringUtil.substring(dstr, "[", "]", false);
				String codes = dstr.substring(11);
				boolean testSpace = testType06(space, 3, 5, 5);
				if(!testSpace) {
					return false;
				}
				boolean testCodes = testType02(codes, 2, 10);
				if(!testCodes) {
					return false;
				}
				return true;
			} catch (Exception e) {}
			break;
		case "rx3z6":
			try {
				String space = StringUtil.substring(dstr, "[", "]", false);
				String codes = dstr.substring(11);
				boolean testSpace = testType06(space, 3, 5, 5);
				if(!testSpace) {
					return false;
				}
				boolean testCodes = testType02(codes, 3, 10);
				if(!testCodes) {
					return false;
				}
				return true;
			} catch (Exception e) {}
			break;
		case "rx2zx":
			try {
				String space = StringUtil.substring(dstr, "[", "]", false);
				String codes = dstr.substring(11);
				boolean testSpace = testType06(space, 2, 5, 5);
				if(!testSpace) {
					return false;
				}
				boolean testCodes = testType02(codes, 2, 10);
				if(!testCodes) {
					return false;
				}
				return true;
			} catch (Exception e) {}
			break;
		case "rx3hhzx":
			try {
				String space = StringUtil.substring(dstr, "[", "]", false);
				String codes = dstr.substring(11);
				boolean testSpace = testType06(space, 3, 5, 5);
				if(!testSpace) {
					return false;
				}
				boolean testCodes = testCodes(codes, ",", 3, LEGAL_CODES);
				if(!testCodes) {
					return false;
				}
				return true;
			} catch (Exception e) {}
			break;
		// 龙虎和
		case "longhuhewq":
		case "longhuhewb":
		case "longhuhews":
		case "longhuhewg":
		case "longhuheqb":
		case "longhuheqs":
		case "longhuheqg":
		case "longhuhebs":
		case "longhuhebg":
		case "longhuhesg":
			try {
				String[] values = {"龙", "虎", "和"};
				return testCodes(dstr, ",", 1, values);
			} catch (Exception e) {}
			break;
		case "wxdxds":
			try {
				String[] values = {"总和大", "总和小", "总和单", "总和双"};
				return testCodes(dstr, ",", values);
			} catch (Exception e) {}
			break;
		case "sscniuniu":
			try {
				String[] values = {"牛大","牛小", "牛单", "牛双", "无牛", "牛牛", "牛1", "牛2", "牛3", "牛4", "牛5", "牛6", "牛7", "牛8", "牛9", "五条", "炸弹", "葫芦", "顺子", "三条", "两对", "单对", "散号"};
				return testCodes(dstr, ",", values);
			} catch (Exception e) {}
			break;
		default:
			System.out.println("不需要验证的方法...." + type);
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		 String type = "wxzhixfs";
		 String dstr = "0123456789,10123456789,0123456789,0123456789,0123456789";
		 Object[] objects = inputNumber(type, dstr);
		 System.out.println(objects[0]);
		 System.out.println(objects[1]);

		// String type = "rx2ds";
		// String dstr = "[-,√,√,-,-]01 00 02 03 04 05 06 07 08 09 10 13 16 19 20 23 26 29 30 31 32 33 34 35 36 37 38 39 40 43 46 49 50 53 56 59 60 61 62 63 64 65 66 67 68 69 70 73 76 79 80 83 86 89 90 91 92 93 94 95 96 97 98 99";
		// Object[] objects = inputNumber(type, dstr);
		// System.out.println(objects[0] + "," + objects[1]);


		// long t1 = System.currentTimeMillis();
		// String dstr = "大 小 单 双";
		// boolean inputValidate = inputValidate("dxdsq", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		// long t1 = System.currentTimeMillis();
		// String dstr = "龙,虎,和,和";
		// boolean inputValidate = inputValidate("longhuhesg", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		// long t1 = System.currentTimeMillis();
		// String dstr = "总和大,总和小,总和小";
		// boolean inputValidate = inputValidate("wxdxds", dstr);
		// long t2 = System.currentTimeMillis();
		// System.out.println(inputValidate + ":" + (t2 - t1) + "ms");
//
//		long t1 = System.currentTimeMillis();
//		String dstr = "牛大,牛小,牛单,牛单";
//		boolean inputValidate = inputValidate("sscniuniu", dstr);
//		long t2 = System.currentTimeMillis();
//		System.out.println(inputValidate + ":" + (t2 - t1) + "ms");

		//
        //
		// case "sscniuniu":
		// try {
		// 	String[] values = {"牛大","牛小", "牛单", "牛双", "无牛", "牛牛", "牛1", "牛2", "牛3", "牛4", "牛5", "牛6", "牛7", "牛8", "牛9", "五条", "炸弹", "葫芦", "顺子", "三条", "两对", "单对", "散号"};
		// 	return testCodes(dstr, ",", values);
		// } catch (Exception e) {}

	}
	
}