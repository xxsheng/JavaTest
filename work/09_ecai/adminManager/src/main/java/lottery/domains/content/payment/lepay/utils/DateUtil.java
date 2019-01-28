package lottery.domains.content.payment.lepay.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
	private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal();

	private static final Object object = new Object();

	private static SimpleDateFormat getDateFormat(String pattern) throws RuntimeException {
		SimpleDateFormat dateFormat = (SimpleDateFormat) threadLocal.get();
		if (dateFormat == null) {
			synchronized (object) {
				if (dateFormat == null) {
					dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setLenient(false);
					threadLocal.set(dateFormat);
				}
			}
		}
		dateFormat.applyPattern(pattern);
		return dateFormat;
	}

	private static int getInteger(Date date, int dateType) {
		int num = 0;
		Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
			num = calendar.get(dateType);
		}
		return num;
	}

	private static String addInteger(String date, int dateType, int amount) {
		String dateString = null;
		DateStyle dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			myDate = addInteger(myDate, dateType, amount);
			dateString = DateToString(myDate, dateStyle);
		}
		return dateString;
	}

	private static Date addInteger(Date date, int dateType, int amount) {
		Date myDate = null;
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(dateType, amount);
			myDate = calendar.getTime();
		}
		return myDate;
	}

	private static Date getAccurateDate(List<Long> timestamps) {
		Date date = null;
		long timestamp = 0L;
		Map map = new HashMap();
		List absoluteValues = new ArrayList();

		if ((timestamps != null) && (timestamps.size() > 0)) {
			if (timestamps.size() > 1) {
				for (int i = 0; i < timestamps.size(); i++) {
					for (int j = i + 1; j < timestamps.size(); j++) {
						long absoluteValue = Math.abs(((Long) timestamps.get(i)).longValue() - ((Long) timestamps.get(j)).longValue());
						absoluteValues.add(Long.valueOf(absoluteValue));

						long[] timestampTmp = { ((Long) timestamps.get(i)).longValue(), ((Long) timestamps.get(j)).longValue() };
						map.put(Long.valueOf(absoluteValue), timestampTmp);
					}

				}

				long minAbsoluteValue = -1L;
				if (!absoluteValues.isEmpty()) {
					minAbsoluteValue = ((Long) absoluteValues.get(0)).longValue();
					for (int i = 1; i < absoluteValues.size(); i++) {
						if (minAbsoluteValue > ((Long) absoluteValues.get(i)).longValue()) {
							minAbsoluteValue = ((Long) absoluteValues.get(i)).longValue();
						}
					}
				}

				if (minAbsoluteValue != -1L) {
					long[] timestampsLastTmp = (long[]) map.get(Long.valueOf(minAbsoluteValue));

					long dateOne = timestampsLastTmp[0];
					long dateTwo = timestampsLastTmp[1];
					if (absoluteValues.size() > 1)
						timestamp = Math.abs(dateOne) > Math.abs(dateTwo) ? dateOne : dateTwo;
				}
			} else {
				timestamp = ((Long) timestamps.get(0)).longValue();
			}
		}

		if (timestamp != 0L) {
			date = new Date(timestamp);
		}
		return date;
	}

	public static boolean isDate(String date) {
		boolean isDate = false;
		if ((date != null) && (getDateStyle(date) != null)) {
			isDate = true;
		}

		return isDate;
	}

	public static DateStyle getDateStyle(String date) {
		DateStyle dateStyle = null;
		Map map = new HashMap();
		List timestamps = new ArrayList();
		for (DateStyle style : DateStyle.values())
			if (!style.isShowOnly()) {
				Date dateTmp = null;
				if (date != null)
					try {
						ParsePosition pos = new ParsePosition(0);
						dateTmp = getDateFormat(style.getValue()).parse(date, pos);
						if (pos.getIndex() != date.length())
							dateTmp = null;
					} catch (Exception localException) {
					}
				if (dateTmp != null) {
					timestamps.add(Long.valueOf(dateTmp.getTime()));
					map.put(Long.valueOf(dateTmp.getTime()), style);
				}
			}
		Date accurateDate = getAccurateDate(timestamps);
		if (accurateDate != null) {
			dateStyle = (DateStyle) map.get(Long.valueOf(accurateDate.getTime()));
		}
		return dateStyle;
	}

	public static Date StringToDate(String date) {
		DateStyle dateStyle = getDateStyle(date);
		return StringToDate(date, dateStyle);
	}

	public static Date StringToDate(String date, String pattern) {
		Date myDate = null;
		if (date != null)
			try {
				myDate = getDateFormat(pattern).parse(date);
			} catch (Exception localException) {
			}
		return myDate;
	}

	public static Date StringToDate(String date, DateStyle dateStyle) {
		Date myDate = null;
		if (dateStyle != null) {
			myDate = StringToDate(date, dateStyle.getValue());
		}
		return myDate;
	}

	public static String DateToString(Date date, String pattern) {
		String dateString = null;
		if (date != null)
			try {
				dateString = getDateFormat(pattern).format(date);
			} catch (Exception localException) {
			}
		return dateString;
	}

	public static String DateToString(Date date, DateStyle dateStyle) {
		String dateString = null;
		if (dateStyle != null) {
			dateString = DateToString(date, dateStyle.getValue());
		}
		return dateString;
	}

	public static String StringToString(String date, String newPattern) {
		DateStyle oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, newPattern);
	}

	public static String StringToString(String date, DateStyle newDateStyle) {
		DateStyle oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, newDateStyle);
	}

	public static String StringToString(String date, String olddPattern, String newPattern) {
		return DateToString(StringToDate(date, olddPattern), newPattern);
	}

	public static String StringToString(String date, DateStyle olddDteStyle, String newParttern) {
		String dateString = null;
		if (olddDteStyle != null) {
			dateString = StringToString(date, olddDteStyle.getValue(), newParttern);
		}

		return dateString;
	}

	public static String StringToString(String date, String olddPattern, DateStyle newDateStyle) {
		String dateString = null;
		if (newDateStyle != null) {
			dateString = StringToString(date, olddPattern, newDateStyle.getValue());
		}
		return dateString;
	}

	public static String StringToString(String date, DateStyle olddDteStyle, DateStyle newDateStyle) {
		String dateString = null;
		if ((olddDteStyle != null) && (newDateStyle != null)) {
			dateString = StringToString(date, olddDteStyle.getValue(), newDateStyle.getValue());
		}
		return dateString;
	}

	public static String addYear(String date, int yearAmount) {
		return addInteger(date, 1, yearAmount);
	}

	public static Date addYear(Date date, int yearAmount) {
		return addInteger(date, 1, yearAmount);
	}

	public static String addMonth(String date, int monthAmount) {
		return addInteger(date, 2, monthAmount);
	}

	public static Date addMonth(Date date, int monthAmount) {
		return addInteger(date, 2, monthAmount);
	}

	public static String addDay(String date, int dayAmount) {
		return addInteger(date, 5, dayAmount);
	}

	public static Date addDay(Date date, int dayAmount) {
		return addInteger(date, 5, dayAmount);
	}

	public static String addHour(String date, int hourAmount) {
		return addInteger(date, 11, hourAmount);
	}

	public static Date addHour(Date date, int hourAmount) {
		return addInteger(date, 11, hourAmount);
	}

	public static String addMinute(String date, int minuteAmount) {
		return addInteger(date, 12, minuteAmount);
	}

	public static Date addMinute(Date date, int minuteAmount) {
		return addInteger(date, 12, minuteAmount);
	}

	public static String addSecond(String date, int secondAmount) {
		return addInteger(date, 13, secondAmount);
	}

	public static Date addSecond(Date date, int secondAmount) {
		return addInteger(date, 13, secondAmount);
	}

	public static int getYear(String date) {
		return getYear(StringToDate(date));
	}

	public static int getYear(Date date) {
		return getInteger(date, 1);
	}

	public static int getMonth(String date) {
		return getMonth(StringToDate(date));
	}

	public static int getMonth(Date date) {
		return getInteger(date, 2) + 1;
	}

	public static int getDay(String date) {
		return getDay(StringToDate(date));
	}

	public static int getDay(Date date) {
		return getInteger(date, 5);
	}

	public static int getHour(String date) {
		return getHour(StringToDate(date));
	}

	public static int getHour(Date date) {
		return getInteger(date, 11);
	}

	public static int getMinute(String date) {
		return getMinute(StringToDate(date));
	}

	public static int getMinute(Date date) {
		return getInteger(date, 12);
	}

	public static int getSecond(String date) {
		return getSecond(StringToDate(date));
	}

	public static int getSecond(Date date) {
		return getInteger(date, 13);
	}

	public static String getDate(String date) {
		return StringToString(date, DateStyle.YYYY_MM_DD);
	}

	public static String getDate(Date date) {
		return DateToString(date, DateStyle.YYYY_MM_DD);
	}

	public static String getTime(String date) {
		return StringToString(date, DateStyle.HH_MM_SS);
	}

	public static String getTime(Date date) {
		return DateToString(date, DateStyle.HH_MM_SS);
	}

	public static String getDateTime(String date) {
		return StringToString(date, DateStyle.YYYY_MM_DD_HH_MM_SS);
	}

	public static String getDateTime(Date date) {
		return DateToString(date, DateStyle.YYYY_MM_DD_HH_MM_SS);
	}

	public static Week getWeek(String date) {
		Week week = null;
		DateStyle dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			week = getWeek(myDate);
		}
		return week;
	}

	public static Week getWeek(Date date) {
		Week week = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekNumber = calendar.get(7) - 1;
		switch (weekNumber) {
		case 0:
			week = Week.SUNDAY;
			break;
		case 1:
			week = Week.MONDAY;
			break;
		case 2:
			week = Week.TUESDAY;
			break;
		case 3:
			week = Week.WEDNESDAY;
			break;
		case 4:
			week = Week.THURSDAY;
			break;
		case 5:
			week = Week.FRIDAY;
			break;
		case 6:
			week = Week.SATURDAY;
		}

		return week;
	}

	public static int getIntervalDays(String date, String otherDate) {
		return getIntervalDays(StringToDate(date), StringToDate(otherDate));
	}

	public static int getIntervalDays(Date date, Date otherDate) {
		int num = -1;
		Date dateTmp = StringToDate(getDate(date), DateStyle.YYYY_MM_DD);

		Date otherDateTmp = StringToDate(getDate(otherDate), DateStyle.YYYY_MM_DD);

		if ((dateTmp != null) && (otherDateTmp != null)) {
			long time = Math.abs(dateTmp.getTime() - otherDateTmp.getTime());
			num = (int) (time / 86400000L);
		}
		return num;
	}

	public static String getAge(Date date, Date otherDate) {
		int dis = getIntervalDays(new Date(), otherDate);
		int year = dis / 365;
		int month = dis % 365 / 30;
		int day = dis % 365 % 31;
		String age = (year > 0 ? year + "��" : "") + (month > 0 ? month + "����" : "") + day + "��";

		return age;
	}

	public static int getStartTimeOfOneDay(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		return (int) (cal.getTimeInMillis() / 1000L);
	}

	public static String getCurrentDate(String dateFormat) {
		return new SimpleDateFormat(dateFormat).format(new Date());
	}

	public static String getMsgId() {
		int ran = getRandom(10);
		String msgId = getCurrentDate("yyyyMMddHHmmss") + "-" + ran;
		return msgId;
	}

	public static int getRandom(int length) {
		int num = 1;
		double random = Math.random();
		if (random < 0.1D) {
			random += 0.1D;
		}

		for (int i = 0; i < length; i++) {
			num *= 10;
		}

		return (int) (random * num);
	}
}