package lottery.domains.content.payment.lepay.utils;

public abstract class StringUtil {
	public static boolean isEmpty(String value) {
		int strLen;
		if ((value == null) || ((strLen = value.length()) == 0)) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(value.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumeric(Object obj) {
		if (obj == null) {
			return false;
		}
		char[] chars = obj.toString().toCharArray();
		int length = chars.length;
		if (length < 1) {
			return false;
		}
		int i = 0;
		if ((length > 1) && (chars[0] == '-'))
			;
		for (i = 1; i < length; i++) {
			if (!Character.isDigit(chars[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean areNotEmpty(String[] values) {
		boolean result = true;
		if ((values == null) || (values.length == 0))
			result = false;
		else {
			for (String value : values) {
				result &= !isEmpty(value);
			}
		}
		return result;
	}

	public static String unicodeToChinese(String unicode) {
		StringBuilder out = new StringBuilder();
		if (!isEmpty(unicode)) {
			for (int i = 0; i < unicode.length(); i++) {
				out.append(unicode.charAt(i));
			}
		}
		return out.toString();
	}

	public static String stripNonValidXMLCharacters(String input) {
		if ((input == null) || ("".equals(input)))
			return "";
		StringBuilder out = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {
			char current = input.charAt(i);
			if ((current == '\t') || (current == '\n') || (current == '\r') || ((current >= ' ') && (current <= 55295)) || ((current >= 57344) && (current <= 65533)) || ((current >= 65536) && (current <= 1114111))) {
				out.append(current);
			}
		}
		return out.toString();
	}

	public static boolean contains(String src, String dest) {
		if ((isEmpty(src)) || (isEmpty(dest))) {
			return false;
		}

		if (src.indexOf(dest) == -1) {
			return false;
		}

		return true;
	}
}