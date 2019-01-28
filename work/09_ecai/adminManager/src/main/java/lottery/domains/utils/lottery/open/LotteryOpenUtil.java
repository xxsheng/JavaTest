package lottery.domains.utils.lottery.open;

import java.util.ArrayList;
import java.util.List;

import javautils.date.DateUtil;

import javautils.date.Moment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenTime;
import lottery.domains.pool.LotteryDataFactory;

@Component
public class LotteryOpenUtil {
	
	@Autowired
	private LotteryDataFactory df;
	
	@Autowired
	@Qualifier("highOpenTimeUtil")
	private OpenTimeUtil highOpenTimeUtil;
	
	@Autowired
	@Qualifier("lowOpenTimeUtil")
	private OpenTimeUtil lowOpenTimeUtil;

	/**
	 * 根据彩票期数获取开奖时间，以及结束时间
	 * @param lotteryId
	 * @param expect
	 * @return
	 */
	public OpenTime getOpentime(int lotteryId, String expect) {
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			if(lottery.getExpectTrans() == 1) {
				expect = trans(lotteryId, Integer.parseInt(expect));
				OpenTime bean = highOpenTimeUtil.getOpenTime(lotteryId, expect);
				return trans(lotteryId, bean);
			}
			switch (lottery.getType()) {
			case 1:
			case 2:
			case 3:
			case 7:
				if("bjk3".equals(lottery.getShortName())){
					expect = trans(lotteryId, Integer.parseInt(expect));
					OpenTime bean = highOpenTimeUtil.getOpenTime(lotteryId, expect);
					return trans(lotteryId, bean);
				}
				return highOpenTimeUtil.getOpenTime(lotteryId, expect);
			case 4:
				return lowOpenTimeUtil.getOpenTime(lotteryId, expect);
			case 5:
			case 6:
				if("bjpk10".equals(lottery.getShortName())) {
					expect = trans(lotteryId, Integer.parseInt(expect));
					OpenTime bean = highOpenTimeUtil.getOpenTime(lotteryId, expect);
					return trans(lotteryId, bean);
				}
				return highOpenTimeUtil.getOpenTime(lotteryId, expect);
			default:
				break;
			}
		}
		return null;
	}
	
	/**
	 * 获取追号的时间列表
	 * @param lotteryId
	 * @return
	 */
	public List<OpenTime> getOpenTimeList(int lotteryId, int count) {
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			if(lottery.getExpectTrans() == 1) {
				List<OpenTime> list = highOpenTimeUtil.getOpenTimeList(lotteryId, count);
				return trans(lotteryId, list);
			}
			switch (lottery.getType()) {
			case 1:
			case 2:
			case 3:
			case 7:
				if("bjk3".equals(lottery.getShortName())){
					List<OpenTime> list = highOpenTimeUtil.getOpenTimeList(lotteryId, count);
					return trans(lotteryId, list);
				}
				return highOpenTimeUtil.getOpenTimeList(lotteryId, count);
			case 4:
				return lowOpenTimeUtil.getOpenTimeList(lotteryId, count);
			case 5:
			case 6:
				if ("bjpk10".equals(lottery.getShortName())) {
					List<OpenTime> list = highOpenTimeUtil.getOpenTimeList(lotteryId, count);
					return trans(lotteryId, list);
				}
				return highOpenTimeUtil.getOpenTimeList(lotteryId, count);
			default:
				break;
			}
		}
		return null;
	}
	
	/**
	 * 获取日期期数表
	 * @param lotteryId
	 * @param count
	 * @return
	 */
	public List<OpenTime> getOpenDateList(int lotteryId, String date) {
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			if(lottery.getExpectTrans() == 1) {
				List<OpenTime> list = highOpenTimeUtil.getOpenDateList(lotteryId, date);
				return trans(lotteryId, list);
			}
			switch (lottery.getType()) {
			case 1:
			case 2:
			case 3:
			case 7:
				if("bjk3".equals(lottery.getShortName())){
					List<OpenTime> list = highOpenTimeUtil.getOpenDateList(lotteryId, date);
					return trans(lotteryId, list);
				}
				return highOpenTimeUtil.getOpenDateList(lotteryId, date);
			case 4:
				return lowOpenTimeUtil.getOpenDateList(lotteryId, date);
			case 5:
			case 6:
				if("bjpk10".equals(lottery.getShortName())) {
					List<OpenTime> list = highOpenTimeUtil.getOpenDateList(lotteryId, date);
					return trans(lotteryId, list);
				}
				return highOpenTimeUtil.getOpenDateList(lotteryId, date);
			default:
				break;
			}
		}
		return null;
	}
	
	/**
	 * 获取当前销售期的时间
	 * @param lotteryId
	 * @return
	 */
	public OpenTime getCurrOpenTime(int lotteryId) {
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			String currTime = DateUtil.getCurrentTime();
			if(lottery.getExpectTrans() == 1) {
				OpenTime bean = highOpenTimeUtil.getCurrOpenTime(lotteryId, currTime);
				return trans(lotteryId, bean);
			}
			switch (lottery.getType()) {
			case 1:
			case 2:
			case 3:
			case 7:
				if("bjk3".equals(lottery.getShortName())){
					OpenTime bean = highOpenTimeUtil.getCurrOpenTime(lotteryId, currTime);
					return trans(lotteryId, bean);
				}
				return highOpenTimeUtil.getCurrOpenTime(lotteryId, currTime);
			case 4:
				return lowOpenTimeUtil.getCurrOpenTime(lotteryId, currTime);
			case 5:
			case 6:
				if("bjpk10".equals(lottery.getShortName())) {
					OpenTime bean = highOpenTimeUtil.getCurrOpenTime(lotteryId, currTime);
					return trans(lotteryId, bean);
				}
				return highOpenTimeUtil.getCurrOpenTime(lotteryId, currTime);
			default:
				break;
			}
		}
		return null;
	}
	
	/**
	 * 获取当前开奖期的时间
	 * @param crawler
	 * @return
	 */
	public OpenTime getLastOpenTime(int lotteryId) {
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			String currTime = DateUtil.getCurrentTime();
			if(lottery.getExpectTrans() == 1) {
				OpenTime bean = highOpenTimeUtil.getLastOpenTime(lotteryId, currTime);
				return trans(lotteryId, bean);
			}
			switch (lottery.getType()) {
			case 1:
			case 2:
			case 3:
			case 7:
				if("bjk3".equals(lottery.getShortName())){
					OpenTime bean = highOpenTimeUtil.getLastOpenTime(lotteryId, currTime);
					return trans(lotteryId, bean);
				}
				return highOpenTimeUtil.getLastOpenTime(lotteryId, currTime);
			case 4:
				return lowOpenTimeUtil.getLastOpenTime(lotteryId, currTime);
			case 5:
			case 6:	
				if("bjpk10".equals(lottery.getShortName())) {
					OpenTime bean = highOpenTimeUtil.getLastOpenTime(lotteryId, currTime);
					return trans(lotteryId, bean);
				}
				return highOpenTimeUtil.getLastOpenTime(lotteryId, currTime);
			default:
				break;
			}
		}
		return null;
	}
	
	/**
	 * 转换北京快乐8、北京PK拾期号成数字累计期号
	 * @param lotteryId
	 * @param bean
	 * @return
	 */
	public OpenTime trans(int lotteryId, OpenTime bean) {
		Lottery lottery = df.getLottery(lotteryId);
		String refLotteryName = lottery.getShortName() + "_ref"; // 获取坐标名称
		List<LotteryOpenTime> opList = df.listLotteryOpenTime(refLotteryName);
		LotteryOpenTime lotteryOpenTime = opList.get(0);
		String refDate = lotteryOpenTime.getOpenTime();
		int refExpect = Integer.parseInt(lotteryOpenTime.getExpect());
		int times = lottery.getTimes();
		return OpenTimeTransUtil.trans(bean, refDate, refExpect, times);
	}
	
	/**
	 * 转换北京快乐8、北京PK拾数字累计期号成日期期号
	 * @param lotteryId
	 * @param expect
	 * @return
	 */
	public String trans(int lotteryId, int expect){
		Lottery lottery = df.getLottery(lotteryId);
		String refLotteryName = lottery.getShortName() + "_ref"; // 获取坐标名称
		List<LotteryOpenTime> opList = df.listLotteryOpenTime(refLotteryName);
		LotteryOpenTime lotteryOpenTime = opList.get(0);
		String refDate = lotteryOpenTime.getOpenTime();
		int refExpect = Integer.parseInt(lotteryOpenTime.getExpect());
		int times = lottery.getTimes();
		return OpenTimeTransUtil.trans(expect, refDate, refExpect, times);
	}
	
	/**
	 * 批量转换
	 * @param lotteryId
	 * @param list
	 * @return
	 */
	public List<OpenTime> trans(int lotteryId, List<OpenTime> list) {
		List<OpenTime> nList = new ArrayList<>();
		for (OpenTime bean : list) {
			nList.add(trans(lotteryId, bean));
		}
		return nList;
	}

	public String subtractExpect(String lotteryShortName, String expect) {
		Lottery lottery = df.getLottery(lotteryShortName);
		if (lottery == null) return null;

		String subExpect;
		if (expect.indexOf("-") <= -1) {
			Integer integerExpect = Integer.valueOf(expect);
			integerExpect -= 1;

			if (integerExpect.toString().length() >= expect.length()) {
				subExpect = integerExpect.toString();
			}
			else {
				subExpect = String.format("%0"+expect.length()+"d", integerExpect);
			}
		}
		else {
			String[] split = expect.split("-");
			int formatCount = split[1].length();
			String date = split[0];
			String currExpect = split[1];

			if (currExpect.equals("001") || currExpect.equals("0001")) {
				date = new Moment().fromDate(date).subtract(1, "days").format("yyyyMMdd");
				subExpect = String.format("%0"+formatCount+"d", lottery.getTimes());
			}
			else {
				Integer integer = Integer.valueOf(currExpect);
				integer -= 1;
				if (integer.toString().length() >= formatCount) {
					subExpect = integer.toString();
				}
				else {
					subExpect = String.format("%0"+formatCount+"d", integer);
				}

			}
			subExpect = date + "-" + subExpect;
		}

		return subExpect;
	}
}