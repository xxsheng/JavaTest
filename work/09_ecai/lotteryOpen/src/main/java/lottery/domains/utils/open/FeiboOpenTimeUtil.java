// package lottery.domains.utils.open;
//
// import javautils.date.DateUtil;
// import org.springframework.stereotype.Component;
//
// import java.util.ArrayList;
// import java.util.List;
//
// /**
//  * 菲博分分彩
//  * @author root
//  */
// @Component
// public class FeiboOpenTimeUtil implements OpenTimeUtil {
//
// 	private static final int fbffc_delay = 15; // 菲博分分彩提前结束时间
// 	private static final int fb3fc_delay = 15; // 菲博三分彩提前结束时间
// 	private static final int fb5fc_delay = 15; // 菲博五分彩提前结束时间
//
// 	@Override
// 	public OpenTime getCurrOpenTime(int lotteryId, String currTime) {
// 		switch (lotteryId) {
// 		case 110:
// 			try {
// 				OpenTime bean = new OpenTime();
// 				currTime = DateUtil.calcDateByTime(currTime, fbffc_delay);
// 				String date = currTime.substring(0, 10);
// 				int hour = DateUtil.getHours(currTime);
// 				int mins = DateUtil.getMinutes(currTime);
// 				String zeroTime = DateUtil.formatTime(currTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:00");
// 				String startTime = DateUtil.calcDateByTime(zeroTime, -fbffc_delay); // 计算出开始世界
// 				String stopTime = DateUtil.calcDateByTime(startTime, 60);
// 				String openTime = DateUtil.calcDateByTime(zeroTime, 60);
// 				String expectNumber = String.format("%04d", hour * 60 + mins + 1);
// 				String expect = date.replace("-", "") + "-" + expectNumber;
// 				bean.setExpect(expect);
// 				bean.setStartTime(startTime);
// 				bean.setStopTime(stopTime);
// 				bean.setOpenTime(openTime);
// 				return bean;
// 			} catch (Exception e) {}
// 			break;
// 		case 111:
// 			try {
// 				OpenTime bean = new OpenTime();
// 				currTime = DateUtil.calcDateByTime(currTime, fb3fc_delay);
// 				String date = currTime.substring(0, 10);
// 				int hour = DateUtil.getHours(currTime);
// 				int mins = DateUtil.getMinutes(currTime);
// 				String zeroTime = DateUtil.formatTime(currTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd 00:00:00");
// 				int thisExpect = ((int) (hour * 60 + mins)/3) + 1;
// 				String startTime = DateUtil.calcDateByTime(zeroTime, ((thisExpect - 1) * 3 * 60) - fb3fc_delay); // 计算出开始世界
// 				String stopTime = DateUtil.calcDateByTime(startTime, 3 * 60);
// 				String openTime = DateUtil.calcDateByTime(zeroTime, (thisExpect * 3 * 60));
// 				String expectNumber = String.format("%03d", thisExpect);
// 				String expect = date.replace("-", "") + "-" + expectNumber;
// 				bean.setExpect(expect);
// 				bean.setStartTime(startTime);
// 				bean.setStopTime(stopTime);
// 				bean.setOpenTime(openTime);
// 				return bean;
// 			} catch (Exception e) {}
// 			break;
// 		case 112:
// 			try {
// 				OpenTime bean = new OpenTime();
// 				currTime = DateUtil.calcDateByTime(currTime, fb5fc_delay);
// 				String date = currTime.substring(0, 10);
// 				int hour = DateUtil.getHours(currTime);
// 				int mins = DateUtil.getMinutes(currTime);
// 				String zeroTime = DateUtil.formatTime(currTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd 00:00:00");
// 				int thisExpect = ((int) (hour * 60 + mins)/5) + 1;
// 				String startTime = DateUtil.calcDateByTime(zeroTime, ((thisExpect - 1) * 5 * 60) - fb5fc_delay); // 计算出开始世界
// 				String stopTime = DateUtil.calcDateByTime(startTime, 5 * 60);
// 				String openTime = DateUtil.calcDateByTime(zeroTime, (thisExpect * 5 * 60));
// 				String expectNumber = String.format("%03d", thisExpect);
// 				String expect = date.replace("-", "") + "-" + expectNumber;
// 				bean.setExpect(expect);
// 				bean.setStartTime(startTime);
// 				bean.setStopTime(stopTime);
// 				bean.setOpenTime(openTime);
// 				return bean;
// 			} catch (Exception e) {}
// 			break;
// 		default:
// 			break;
// 		}
// 		return null;
// 	}
//
// 	@Override
// 	public OpenTime getLastOpenTime(int lotteryId, String currTime) {
// 		switch (lotteryId) {
// 		case 110:
// 			currTime = DateUtil.calcDateByTime(currTime, -60);
// 			return getCurrOpenTime(lotteryId, currTime);
// 		case 111:
// 			currTime = DateUtil.calcDateByTime(currTime, -60 * 3);
// 			return getCurrOpenTime(lotteryId, currTime);
// 		case 112:
// 			currTime = DateUtil.calcDateByTime(currTime, -60 * 5);
// 			return getCurrOpenTime(lotteryId, currTime);
// 		default:
// 			break;
// 		}
// 		return null;
// 	}
//
// 	@Override
// 	public List<OpenTime> getOpenTimeList(int lotteryId, int count) {
// 		switch (lotteryId) {
// 		case 110:
// 			try {
// 				List<OpenTime> list = new ArrayList<OpenTime>();
// 				String currTime = DateUtil.getCurrentTime();
// 				for (int i = 0; i < count; i++) {
// 					OpenTime tmpBean = getCurrOpenTime(lotteryId, currTime);
// 					if(tmpBean != null) {
// 						list.add(tmpBean);
// 					}
// 					currTime = DateUtil.calcDateByTime(currTime, 60);
// 				}
// 				return list;
// 			} catch (Exception e) {}
// 			break;
// 		case 111:
// 			try {
// 				List<OpenTime> list = new ArrayList<OpenTime>();
// 				String currTime = DateUtil.getCurrentTime();
// 				for (int i = 0; i < count; i++) {
// 					OpenTime tmpBean = getCurrOpenTime(lotteryId, currTime);
// 					if(tmpBean != null) {
// 						list.add(tmpBean);
// 					}
// 					currTime = DateUtil.calcDateByTime(currTime, 60 * 3);
// 				}
// 				return list;
// 			} catch (Exception e) {}
// 			break;
// 		case 112:
// 			try {
// 				List<OpenTime> list = new ArrayList<OpenTime>();
// 				String currTime = DateUtil.getCurrentTime();
// 				for (int i = 0; i < count; i++) {
// 					OpenTime tmpBean = getCurrOpenTime(lotteryId, currTime);
// 					if(tmpBean != null) {
// 						list.add(tmpBean);
// 					}
// 					currTime = DateUtil.calcDateByTime(currTime, 60 * 5);
// 				}
// 				return list;
// 			} catch (Exception e) {}
// 			break;
// 		default:
// 			break;
// 		}
// 		return null;
// 	}
//
// 	@Override
// 	public List<OpenTime> getOpenDateList(int lotteryId, String date) {
// 		switch (lotteryId) {
// 		case 110:
// 			try {
// 				List<OpenTime> list = new ArrayList<OpenTime>();
// 				String currTime = date + " 00:00:00";
// 				for (int i = 0; i < 1440; i++) {
// 					OpenTime tmpBean = getCurrOpenTime(lotteryId, currTime);
// 					if(tmpBean != null) {
// 						list.add(tmpBean);
// 					}
// 					currTime = DateUtil.calcDateByTime(currTime, 60);
// 				}
// 				return list;
// 			} catch (Exception e) {}
// 			break;
// 		case 111:
// 			try {
// 				List<OpenTime> list = new ArrayList<OpenTime>();
// 				String currTime = date + " 00:00:00";
// 				for (int i = 0; i < 480; i++) {
// 					OpenTime tmpBean = getCurrOpenTime(lotteryId, currTime);
// 					if(tmpBean != null) {
// 						list.add(tmpBean);
// 					}
// 					currTime = DateUtil.calcDateByTime(currTime, 60 * 3);
// 				}
// 				return list;
// 			} catch (Exception e) {}
// 			break;
// 		case 112:
// 			try {
// 				List<OpenTime> list = new ArrayList<OpenTime>();
// 				String currTime = date + " 00:00:00";
// 				for (int i = 0; i < 288; i++) {
// 					OpenTime tmpBean = getCurrOpenTime(lotteryId, currTime);
// 					if(tmpBean != null) {
// 						list.add(tmpBean);
// 					}
// 					currTime = DateUtil.calcDateByTime(currTime, 60 * 5);
// 				}
// 				return list;
// 			} catch (Exception e) {}
// 			break;
// 		default:
// 			break;
// 		}
// 		return null;
// 	}
//
// 	@Override
// 	public OpenTime getOpenTime(int lotteryId, String expect) {
// 		switch (lotteryId) {
// 		case 110:
// 			try {
// 				long timeMillis = DateUtil.formatTime(expect.substring(0, 8), "yyyyMMdd");
// 				timeMillis += (Integer.parseInt(expect.substring(9)) - 1) * 60 * 1000;
// 				String currTime = DateUtil.formatTime(timeMillis, "yyyy-MM-dd HH:mm:ss");
// 				return getCurrOpenTime(lotteryId, currTime);
// 			} catch (Exception e) {}
// 			break;
// 		case 111:
// 			try {
// 				long timeMillis = DateUtil.formatTime(expect.substring(0, 8), "yyyyMMdd");
// 				timeMillis += (Integer.parseInt(expect.substring(9)) - 1) * 60 * 3 * 1000;
// 				String currTime = DateUtil.formatTime(timeMillis, "yyyy-MM-dd HH:mm:ss");
// 				return getCurrOpenTime(lotteryId, currTime);
// 			} catch (Exception e) {}
// 			break;
// 		case 112:
// 			try {
// 				long timeMillis = DateUtil.formatTime(expect.substring(0, 8), "yyyyMMdd");
// 				timeMillis += (Integer.parseInt(expect.substring(9)) - 1) * 60 * 5 * 1000;
// 				String currTime = DateUtil.formatTime(timeMillis, "yyyy-MM-dd HH:mm:ss");
// 				return getCurrOpenTime(lotteryId, currTime);
// 			} catch (Exception e) {}
// 			break;
// 		default:
// 			break;
// 		}
// 		return null;
// 	}
// }
