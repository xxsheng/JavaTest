package lottery.domains.utils.lottery.open;

import javautils.date.DateUtil;
import javautils.date.Moment;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenTime;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 高频彩开奖信息
 * 时时彩、十一选五、快3
 */
@Component
public class HighOpenTimeUtil implements OpenTimeUtil {

	@Autowired
	private LotteryDataFactory df;

	@Override
	public OpenTime getCurrOpenTime(int lotteryId, String currTime) {
		Lottery lottery = df.getLottery(lotteryId);
		if (lottery == null) {
			return null;
		}

		if ("tw5fc".equals(lottery.getShortName())) {
			return getCurrOpenTimeForNext(lotteryId, currTime);
		}

		if(lottery != null) {
			List<LotteryOpenTime> list = df.listLotteryOpenTime(lottery.getShortName());

			if (CollectionUtils.isEmpty(list)) {
				return null;
			}

			String currDate = currTime.substring(0, 10);
			String nextDate = DateUtil.calcNextDay(currDate);
			String lastDate = DateUtil.calcLastDay(currDate);
			for (int i = 0, j = list.size(); i < j; i++) {
				LotteryOpenTime tmpBean = list.get(i);
				String startDate = currDate; // 开始日期
				String stopDate = currDate; // 结束日期
				String openDate = currDate; // 开奖日期
				String expectDate = currDate; // 期号日期
				String startTime = tmpBean.getStartTime(); // 开始时间
				String stopTime = tmpBean.getStopTime(); // 结束时间
				String openTime = tmpBean.getOpenTime(); // 开奖时间
				String expect = tmpBean.getExpect();
				if(i == 0) {
					if(startTime.compareTo(stopTime) > 0) {
						startDate = lastDate;
					}
				} else if(i == j - 1) {
					if(startTime.compareTo(stopTime) > 0) {
						stopDate = nextDate;
					}
					if(startTime.compareTo(openTime) > 0) {
						openDate = nextDate;
					}
					if(currTime.compareTo(stopDate + " " + stopTime) >= 0) {
						tmpBean = list.get(0);
						startDate = nextDate; // 开始日期
						stopDate = nextDate; // 结束日期
						openDate = nextDate; // 开奖日期
						expectDate = nextDate; // 期号日期
						startTime = tmpBean.getStartTime(); // 开始时间
						stopTime = tmpBean.getStopTime(); // 结束时间
						openTime = tmpBean.getOpenTime(); // 开奖时间
						expect = tmpBean.getExpect();
						if(startTime.compareTo(stopTime) > 0) {
							startDate = currDate;
						}
					}
				} else {
					if(startTime.compareTo(stopTime) > 0) {
						stopDate = nextDate;
					}
					if(startTime.compareTo(openTime) > 0) {
						openDate = nextDate;
					}
				}
				if(!tmpBean.getIsTodayExpect()) {
					if(startTime.compareTo(stopTime) > 0) {
						if(currTime.substring(11).compareTo(startTime) >= 0 && currTime.substring(11).compareTo("24:00:00") < 0) {

						} else {
							startDate = lastDate;
							stopDate = currDate;
							expectDate = lastDate;
						}
					} else {
						expectDate = lastDate;
					}
				}
				startTime = startDate + " " + startTime;
				stopTime = stopDate + " " + stopTime;
				openTime = openDate + " " + openTime;
				expect = expectDate.replace("-", "") + "-" + expect;
				if(currTime.compareTo(startTime) >= 0 && currTime.compareTo(stopTime) < 0) {
					OpenTime bean = new OpenTime();
					bean.setExpect(expect);
					bean.setStartTime(startTime);
					bean.setStopTime(stopTime);
					bean.setOpenTime(openTime);
					return bean;
				}
			}
		}
		return null;
	}

	private OpenTime getCurrOpenTimeForNext(int lotteryId, String currTime) {
		Lottery lottery = df.getLottery(lotteryId);
		if (lottery == null) {
			return null;
		}

		// 找到时间表
		List<LotteryOpenTime> list = df.listLotteryOpenTime(lottery.getShortName());
		String currDate = currTime.substring(0, 10); // 给定的年月日
		String currTimeHMS = currTime.substring(11); // 给定的时分秒
		String nextDate = DateUtil.calcNextDay(currDate);
		String lastDate = DateUtil.calcLastDay(currDate);

		// 先确定是哪一期
		String startTime = null;
		String stopTime = null;
		String openTime = null;
		String expect = null;
		boolean found = false;
		for (LotteryOpenTime lotteryOpenTime : list) {
			startTime = lotteryOpenTime.getStartTime(); // 开始时间
			stopTime = lotteryOpenTime.getStopTime(); // 结束时间
			openTime = lotteryOpenTime.getOpenTime();
			expect = lotteryOpenTime.getExpect();

			// 跨天，开始时间大于停售时间 如23:59:50 -> 00:01:20
			if (lotteryOpenTime.getStartTime().compareTo(lotteryOpenTime.getStopTime()) > 0) {
				// 如果已经是第2天了
				if (currTimeHMS.compareTo(lotteryOpenTime.getStopTime()) < 0) {
					startTime = lastDate + " " + startTime;
					stopTime = currDate + " " + stopTime;
					openTime = currDate + " " + openTime;
				}
				else {
					startTime = currDate + " " + startTime;
					stopTime = nextDate + " " + stopTime;
					openTime = nextDate + " " + openTime;
				}
			}
			else {
				startTime = currDate + " " + startTime;
				stopTime = currDate + " " + stopTime;
				openTime = currDate + " " + openTime;
			}

			// 当前时间是否大于起售时间，小于停售时间，以期来确定是不是这个区间的期数
			if (currTime.compareTo(startTime) >= 0 && currTime.compareTo(stopTime) < 0) {
				found = true; // 找到了
				if (lotteryOpenTime.getIsTodayExpect()) {
					expect = currDate.replace("-", "") + "-" + expect;
				}
				else {
					// 跨天，开始时间大于停售时间 如23:59:50 -> 00:01:20
					if (lotteryOpenTime.getStartTime().compareTo(lotteryOpenTime.getStopTime()) > 0) {
						// 如果已经是第2天了
						if (currTimeHMS.compareTo(lotteryOpenTime.getStopTime()) < 0) {
							expect = currDate.replace("-", "") + "-" + expect;
						}
						else {
							expect = nextDate.replace("-", "") + "-" + expect;
						}
					}
					else {
						expect = nextDate.replace("-", "") + "-" + expect;
					}
				}
				break;
			}
		}

		if (found == false) {
			return null;
		}

		OpenTime bean = new OpenTime();
		bean.setExpect(expect);
		bean.setStartTime(startTime);
		bean.setStopTime(stopTime);
		bean.setOpenTime(openTime);
		return bean;
	}

	@Override
	public OpenTime getLastOpenTime(int lotteryId, String currTime) {
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			// 先得到当前销售期
			OpenTime currOpenTime = getCurrOpenTime(lotteryId, currTime);

			if (currOpenTime == null) {
				return null;
			}

			String tmpExpect = currOpenTime.getExpect();
			String tmpDate = tmpExpect.substring(0, 8);
			String currDate = DateUtil.formatTime(tmpDate, "yyyyMMdd", "yyyy-MM-dd");
			String currExpect = tmpExpect.substring(9);
			String lastDate = currDate;
			int lastExpect = Integer.parseInt(currExpect);
			int times = lottery.getTimes();
			// 如果是第1期，那么上一期的就是昨天最后一期
			if(lastExpect == 1) {
				lastDate = DateUtil.calcLastDay(currDate);
				lastExpect = times;
			} else {
				lastExpect--;
			}

			int formatCount = 3;
			if (lottery.getTimes() >= 1000) {
				formatCount = 4;
			}
			String expect = lastDate.replaceAll("-", "") + "-" + String.format("%0"+formatCount+"d", lastExpect);

			if ("tw5fc".equals(lottery.getShortName())) {
				return getOpenTimeForNext(lotteryId, expect);
			}
			return getOpenTime(lotteryId, expect);
		}
		return null;
	}

	@Override
	public List<OpenTime> getOpenTimeList(int lotteryId, int count) {
		List<OpenTime> list = new ArrayList<>();
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			String currTime = DateUtil.getCurrentTime();
			// 先得到当前销售期
			OpenTime currOpenTime = getCurrOpenTime(lotteryId, currTime);
			String tmpExpect = currOpenTime.getExpect();
			String tmpDate = tmpExpect.substring(0, 8);
			String currDate = DateUtil.formatTime(tmpDate, "yyyyMMdd", "yyyy-MM-dd");
			int currExpect = Integer.parseInt(tmpExpect.substring(9));
			for (int i = 0; i < count; i++) {

				int formatCount = 3;
				if (lottery.getTimes() >= 1000) {
					formatCount = 4;
				}

				String expect = currDate.replaceAll("-", "") + "-" + String.format("%0"+formatCount+"d", currExpect);

				OpenTime tmpBean;
				if ("tw5fc".equals(lottery.getShortName())) {
					tmpBean = getOpenTimeForNext(lotteryId, expect);
				}
				else {
					tmpBean = getOpenTime(lotteryId, expect);
				}
				if(tmpBean != null) {
					list.add(tmpBean);
				}
				// 计算下一期
				String nextDate = currDate;
				int nextExpect = currExpect;
				int times = lottery.getTimes();
				// 如果已经是最后一期了，那么就是第一期
				if(nextExpect == times) {
					nextDate = DateUtil.calcNextDay(currDate);
					nextExpect = 1;
				} else {
					nextExpect++;
				}
				currDate = nextDate;
				currExpect = nextExpect;
			}
		}
		return list;
	}

	@Override
	public List<OpenTime> getOpenDateList(int lotteryId, String date) {
		List<OpenTime> list = new ArrayList<>();
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			int times = lottery.getTimes();
			for (int i = 0; i < times; i++) {

				int formatCount = 3;
				if (lottery.getTimes() >= 1000) {
					formatCount = 4;
				}

				String expect = date.replaceAll("-", "") + "-" + String.format("%0"+formatCount+"d", i + 1);
				OpenTime tmpBean = getOpenTime(lotteryId, expect);
				if(tmpBean != null) {
					list.add(tmpBean);
				}
			}
		}
		return list;
	}

	@Override
	public OpenTime getOpenTime(int lotteryId, String expect) {
		Lottery lottery = df.getLottery(lotteryId);
		if (lottery == null) {
			return null;
		}

		if ("tw5fc".equals(lottery.getShortName())) {
			return getOpenTimeForNext(lotteryId, expect);
		}

		if(lottery != null) {
			List<LotteryOpenTime> list = df.listLotteryOpenTime(lottery.getShortName());
			String date = expect.substring(0, 8);
			String currDate = DateUtil.formatTime(date, "yyyyMMdd", "yyyy-MM-dd");
			String nextDate = DateUtil.calcNextDay(currDate);
			String lastDate = DateUtil.calcLastDay(currDate);
			String currExpect = expect.substring(9);

			for (int i = 0, j = list.size(); i < j; i++) {
				LotteryOpenTime tmpBean = list.get(i);
				if(tmpBean.getExpect().equals(currExpect)) {
					// 把时间设定为当时的开始时间，那么就能得到当时的开奖时间信息
					String startDate = currDate;
					String startTime = tmpBean.getStartTime();
					String stopTime = tmpBean.getStopTime();
					if(i == 0) {
						// 如果开始时间小于结束时间，那么就是前一天
						if(startTime.compareTo(stopTime) > 0) {
							startDate = lastDate;
						}
					}
					if(!tmpBean.getIsTodayExpect()) {
						// 新疆时时彩
						if ("xjssc".equals(lottery.getShortName())) {
							if(startTime.compareTo(stopTime) > 0) {
								String currTime = new Moment().format("HH:mm:ss");
								if(currTime.compareTo(startTime) >= 0 && currTime.compareTo("24:00:00") < 0) {
								} else {
									startDate = currDate;
								}
							} else {
								startDate = nextDate;
							}
						}
						else {
							startDate = nextDate;
						}
					}
					startTime = startDate + " " + startTime;
					return getCurrOpenTime(lotteryId, startTime);
				}
			}
		}
		return null;
	}

	private OpenTime getOpenTimeForNext(int lotteryId, String expect) {
		Lottery lottery = df.getLottery(lotteryId);
		if (lottery == null) {
			return null;
		}

		List<LotteryOpenTime> list = df.listLotteryOpenTime(lottery.getShortName());
		String date = expect.substring(0, 8);
		String currDate = DateUtil.formatTime(date, "yyyyMMdd", "yyyy-MM-dd");
		String lastDate = DateUtil.calcLastDay(currDate);
		String currExpect = expect.substring(9);

		for (LotteryOpenTime lotteryOpenTime : list) {
			if(!lotteryOpenTime.getExpect().equals(currExpect)) {
				continue;
			}

			String startTime;
			if (lotteryOpenTime.getIsTodayExpect()) {
				startTime = currDate + " " + lotteryOpenTime.getStartTime();
			}
			else {
				startTime = lastDate + " " + lotteryOpenTime.getStartTime();
			}
			return getCurrOpenTimeForNext(lotteryId, startTime);
		}
		return null;
	}
}