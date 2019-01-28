package javautils;

import javautils.date.Moment;
import javautils.math.MathUtil;
import javautils.regex.RegexUtil;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class StringUtil {

	/**
	 * 判断字符串是否为空
	 * @param s
	 * @return
	 */
	public static boolean isNotNull(String s) {
		if(s == null) return false;
		if(s.trim().length() == 0) return false;
		return true;
	}
	
	/**
	 * 判断字符串能否转成int
	 * @param s
	 * @return
	 */
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断字符串能否转成double
	 * @param s
	 * @return
	 */
	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断字符串能否转成float
	 * @param s
	 * @return
	 */
	public static boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断字符串能否转成boolean
	 * @param s
	 * @return
	 */
	public static boolean isBoolean(String s) {
		try {
			Boolean.parseBoolean(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断能否转成short
	 * @param s
	 * @return
	 */
	public static boolean isShort(String s) {
		try {
			Short.parseShort(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断能否转成long
	 * @param s
	 * @return
	 */
	public static boolean isLong(String s) {
		try {
			Long.parseLong(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 自动转类型
	 * @param s
	 * @param clazz
	 * @return
	 */
	public static Object transObject(String s, Class<?> clazz) {
		if(clazz != null) {
			if(clazz == Integer.class) {
				if(isInteger(s)) {
					return Integer.parseInt(s);
				}
				return 0;
			}
			if(clazz == Double.class) {
				if(isDouble(s)) {
					return Double.parseDouble(s);
				}
				return 0;
			}
			if(clazz == Float.class) {
				if(isFloat(s)) {
					return Float.parseFloat(s);
				}
				return 0;
			}
			if(clazz == Boolean.class) {
				if(isBoolean(s)) {
					return Boolean.parseBoolean(s);
				}
				return true;
			}
			if(clazz == Short.class) {
				if(isShort(s)) {
					return Short.parseShort(s);
				}
				return 0;
			}
			if(clazz == Long.class) {
				if(isLong(s)) {
					return Long.parseLong(s);
				}
				return 0;
			}
		}
		return s;
	}
	
	public static String transArrayToString(Object[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = array.length; i < j; i++) {
			sb.append(array[i].toString());
			if(i < j - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public static String transArrayToString(Object[] array, String p) {
		String tempStr = new String();
		for (Object value : array) {
			tempStr += p + value.toString() + p + ",";
		}
		tempStr = tempStr.substring(0, tempStr.length() - 2);
		return tempStr;
	}

	public static int[] transStringToIntArray(String string, String regex) {
		if (isNotNull(string)) {
			String[] sArray = string.split(regex);
			int[] intArray = new int[sArray.length];
			for (int i = 0; i < sArray.length; i++) {
				if(isIntegerString(sArray[i])) {
					intArray[i] = Integer.parseInt(sArray[i]);
				}
			}
			return intArray;
		}
		return null;
	}
	
	/**
	 * 转换文件大小
	 * @param bytes
	 * @return
	 */
	public static String transDataLong(long b) {
		StringBuffer sb = new StringBuffer();
		long KB = 1024;
		long MB = KB * 1024;
		long GB = MB * 1024;
		long TB = GB * 1024;
		if(b >= TB) {
			sb.append(MathUtil.doubleFormat((double)b/TB, 2) + "TB");
		} else if(b >= GB) {
			sb.append(MathUtil.doubleFormat((double)b/GB, 2) + "GB");
		} else if(b >= MB) {
			sb.append(MathUtil.doubleFormat((double)b/MB, 2) + "MB");
		} else if(b >= KB) {
			sb.append(MathUtil.doubleFormat((double)b/KB, 2) + "KB");
		} else {
			sb.append(b + "B");
		}
		return sb.toString();
	}
	
	
	/**
	 * 判断是否是int类型字符串
	 * @param str
	 * @return
	 */
	public static boolean isIntegerString(String str) {
		boolean flag = false;
		if(RegexUtil.isMatcher(str, RegexUtil.integer_regex)) {
			flag = true;
		}
		return flag;
	}
	
	public static boolean isDoubleString(String str) {
		boolean flag = false;
		if(RegexUtil.isMatcher(str, RegexUtil.rational_numbers_regex)) {
			flag = true;
		}
		return flag;
	}
	
	public static boolean isFloatString(String str) {
		boolean flag = false;
		if(RegexUtil.isMatcher(str, RegexUtil.rational_numbers_regex)) {
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 判断字符串是否是日期yyyy-MM-dd格式
	 * @param s
	 * @return
	 */
	public static boolean isDateString(String str) {
		try {
			if(str.length() != 10) {
				return false;
			}
			new SimpleDateFormat("yyyy-MM-dd").parse(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static String markWithSymbol(Object obj, String symbol) {
		return symbol + obj.toString() + symbol;
	}
	
	//123456789
	public static Object[] split(String s) {
		char[] carr = s.toCharArray();
		Object[] arr = new Object[carr.length];
		for (int i = 0; i < carr.length; i++) {
			arr[i] = carr[i];
		}
		return arr;
	}
	//123456789
	public static String[] splitToStr(String s) {
		char[] carr = s.toCharArray();
		String[] arr = new String[carr.length];
		for (int i = 0; i < carr.length; i++) {
			arr[i] = carr[i] + "";
		}
		return arr;
	}
	//123456789
	public static String[] splitDistinct(String s) {
		String[] strs = splitToStr(s);

		HashSet<String> distinct = new HashSet<>();
		for (String str : strs) {
			distinct.add(str);
		}

		return distinct.toArray(new String[]{});
	}

	public static String substring(String s, String start, String end, boolean isInSub) {
		int idx = s.indexOf(start);
		int edx = s.indexOf(end);
		idx = idx == -1 ? 0 : idx + (isInSub ? 0 : start.length());
		edx = edx == -1 ? s.length() : edx + (isInSub ? end.length() : 0);
		return s.substring(idx, edx);
	}
	
	public static String doubleFormat(double d) {
		DecimalFormat decimalformat = new  DecimalFormat("#0.00"); 
		return decimalformat.format(d);
	}
	
	public static String fromInputStream(InputStream inputStream) {
		try {
			StringBuffer sb = new StringBuffer();
			byte[] bytes = new byte[1024];
			int len;
			while ((len = inputStream.read(bytes)) != -1) {
				sb.append(new String(bytes, 0, len));
			}
			inputStream.close();
			return sb.toString();
		} catch (Exception e) {}
		return null;
	}
	
	/**
	 * 验证是否是服务时间
	 * @param moment
	 * @param times 格式为12:00~2:00
	 * @return
	 */
	public static boolean isServiceTime(Moment moment, String times) {
    	int thisMins = moment.hour() * 60 + moment.minute();
		String[] timeArr = times.split("~");
		int startMins = Integer.parseInt(timeArr[0].split(":")[0]) * 60 + Integer.parseInt(timeArr[0].split(":")[1]);
		int endMins = Integer.parseInt(timeArr[1].split(":")[0]) * 60 + Integer.parseInt(timeArr[1].split(":")[1]);
		if(startMins < endMins) {
			// 不在服务内的时间是 0 - start 和 end - 24
			if((thisMins > 0 && thisMins < startMins) || (thisMins > endMins && thisMins < 24 * 60)) {
				return false;
			}
		} else {
			// 不在服务内的时间是 end - start
			if(thisMins > endMins && thisMins < startMins) {
				return false;
			}
		}
		return true;
    }
	
	//姓名只能包含中文
	public static boolean isNameChn(String name){
		boolean result = true;
		int num = 0;
        for(int i=0;i<name.length();i++){
            if(!Pattern.matches(RegexUtil.name_input_chn, name.substring(i, i + 1))){
                num++;
            }
        }
        if(num > 0){
        	result = false;;
        }
        return result;
	}

	public static String join(String str, String separator) {
		if (isNotNull(str)) {
			return StringUtils.join(split(str), separator);
		}
		return null;
	}

	/**
	 * 按位置进行替换，如String str = replaceAt("[√,√,√,-,-]", '√', new char[]{'1', '2'}, new int[]{1, 3});
	 * @param str 要替换的字符串
	 * @param search 查找字符
	 * @param withs 依次替换成指定字符
	 * @param ats 匹配第n次出现的时候按指定字符进行替换
	 * @return
	 */
	public static String replaceAt(String str, char search, char[] withs, int[] ats) {
		if (isNotNull(str)) {
			char[] chars = str.toCharArray();
			int appearN = 0;
			int withN = 0;
			int atN = 0;

			for (int i = 0; i < chars.length; i++) {

				if (withN >= withs.length || atN >= ats.length) {
					break;
				}

				char aChar = chars[i];
				if (aChar == search) {
					// 第n次出现
					appearN++;

					if (appearN == ats[atN]) {
						chars[i] = withs[withN++];
						atN++;
					}
				}
			}

			return new String(chars);
		}
		return str;
	}

	/**
	 * 统计每个字符串出现的次数，并按照从大到小进行排序
	 */
	public static Map<String, Integer> countChars(String str) {
		char[] chars = str.toCharArray();
		Map<String, Integer> map = new HashMap<>();

		for (char aChar : chars) {
			String _char = aChar + "";
			Integer count = map.get(_char);
			if (count == null) {
				count = 0;
			}

			count++;
			map.put(_char, count);
		}

		Map<String, Integer> sortedMap = new LinkedHashMap<>();
		if (map != null && !map.isEmpty()) {
			List<Map.Entry<String, Integer>> entryList = new ArrayList<>(map.entrySet());
			Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
			Map.Entry<String, Integer> tmpEntry;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}
	/**
	 * 统计每个字符串出现的次数，并按照从大到小进行排序
	 */
	public static Map<String, Integer> countChars(String[] str) {
		Map<String, Integer> map = new HashMap<>();

		for (String aChar : str) {
			String _char = aChar;
			Integer count = map.get(_char);
			if (count == null) {
				count = 0;
			}

			count++;
			map.put(_char, count);
		}

		return sortByValueMap(map);
	}

	private static Map<String, Integer> sortByValueMap(Map<String, Integer> map) {
		Map<String, Integer> sortedMap = new LinkedHashMap<>();
		if (map != null && !map.isEmpty()) {
			List<Map.Entry<String, Integer>> entryList = new ArrayList<>(map.entrySet());
			Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
			Map.Entry<String, Integer> tmpEntry;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}

	public static void main(String[] args) {
		// String str = replaceAt("√,√,√,√,√", '√', new char[]{'1', '2', '3'}, new int[]{1, 2, 5});
		// System.out.println(str);

		// Map<String, Integer> stringIntegerMap = countChars("002003006007009022023026027029033036103703906606706907707910992232262272292332362372392662672692772792993363373393663673693773793996676696776796997797991");
		// for (String s : stringIntegerMap.keySet()) {
		// 	System.out.println(s + " = " + stringIntegerMap.get(s));
		// }

		Map<String, Integer> stringIntegerMap = countChars(new String[]{"2", "1", "2", "1", "1"});
		Collection<Integer> values = stringIntegerMap.values();
		Integer[] integers = values.toArray(new Integer[]{});
		System.out.println(integers[0]);
		System.out.println(integers[1]);

		// Map<String, Integer> stringIntegerMap = countChars("0112223333555555");
		// for (Map.Entry<String, Integer> stringIntegerEntry : stringIntegerMap.entrySet()) {
		// 	System.out.println(stringIntegerEntry.getKey() + "=" + stringIntegerEntry.getValue());
		// }
	}
}