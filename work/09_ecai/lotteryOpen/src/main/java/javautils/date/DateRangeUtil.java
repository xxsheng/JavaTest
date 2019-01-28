package javautils.date;

import java.util.ArrayList;
import java.util.List;

/**
 * 依赖于Moment.java
 */
public class DateRangeUtil {
	
	public static void main(String[] args) {
		String[] days = listDate("2014-01-01", "2014-01-15");
		for (String string : days) {
			System.out.println(string);
		}
	}
	
	public static String[] listDate(String sDate, String eDate) {
		Moment sMoment = new Moment().fromDate(sDate);
		Moment eMoment = new Moment().fromDate(eDate);
		List<String> list = new ArrayList<>();
		if(sMoment.le(eMoment)) {
			list.add(sMoment.toSimpleDate());
			int days = eMoment.difference(sMoment, "day");
			for (int i = 0; i < days - 1; i++) {
				list.add(sMoment.add(1, "days").toSimpleDate());
			}
		}
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

}