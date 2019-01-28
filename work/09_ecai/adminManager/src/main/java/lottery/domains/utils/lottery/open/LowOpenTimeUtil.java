package lottery.domains.utils.lottery.open;

import java.util.ArrayList;
import java.util.List;

import javautils.date.DateUtil;
import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryOpenTime;
import lottery.domains.pool.LotteryDataFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 低频彩开奖信息
 * 福彩3D、排列三
 */
@Component
public class LowOpenTimeUtil implements OpenTimeUtil {

	@Autowired
	private LotteryDataFactory df;
	
	@Override
	public OpenTime getCurrOpenTime(int lotteryId, String currTime) {
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			List<LotteryOpenTime> list = df.listLotteryOpenTime(lottery.getShortName());
			for (LotteryOpenTime tmpBean : list) {
				String startTime = tmpBean.getStartTime();
				String stopTime = tmpBean.getStopTime();
				if(currTime.compareTo(startTime) >= 0 && currTime.compareTo(stopTime) < 0) {
					OpenTime bean = new OpenTime();
					bean.setExpect(tmpBean.getExpect());
					bean.setStartTime(tmpBean.getStartTime());
					bean.setStopTime(tmpBean.getStopTime());
					bean.setOpenTime(tmpBean.getOpenTime());
					return bean;
				}
			}
		}
		return null;
	}

	@Override
	public OpenTime getLastOpenTime(int lotteryId, String currTime) {
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			List<LotteryOpenTime> list = df.listLotteryOpenTime(lottery.getShortName());
			for (int i = 0, j = list.size(); i < j; i++) {
				LotteryOpenTime tmpBean = list.get(i);
				String startTime = tmpBean.getStartTime();
				String stopTime = tmpBean.getStopTime();
				if(currTime.compareTo(startTime) >= 0 && currTime.compareTo(stopTime) < 0) {
					if(i == 0) {
						return null;
					}
					tmpBean = list.get(i - 1);
					OpenTime bean = new OpenTime();
					bean.setExpect(tmpBean.getExpect());
					bean.setStartTime(tmpBean.getStartTime());
					bean.setStopTime(tmpBean.getStopTime());
					bean.setOpenTime(tmpBean.getOpenTime());
					return bean;
				}
			}
		}
		return null;
	}

	@Override
	public List<OpenTime> getOpenTimeList(int lotteryId, int count) {
		List<OpenTime> list = new ArrayList<>();
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			String currTime = DateUtil.getCurrentTime();
			List<LotteryOpenTime> opList = df.listLotteryOpenTime(lottery.getShortName());
			for (LotteryOpenTime tmpBean : opList) {
				String stopTime = tmpBean.getStopTime();
				if(currTime.compareTo(stopTime) < 0) {
					OpenTime bean = new OpenTime();
					bean.setExpect(tmpBean.getExpect());
					bean.setStartTime(tmpBean.getStartTime());
					bean.setStopTime(tmpBean.getStopTime());
					bean.setOpenTime(tmpBean.getOpenTime());
					list.add(bean);
					if(list.size() == count) break;
				}
			}
		}
		return list;
	}

	@Override
	public List<OpenTime> getOpenDateList(int lotteryId, String date) {
		List<OpenTime> list = new ArrayList<>();
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			List<LotteryOpenTime> opList = df.listLotteryOpenTime(lottery.getShortName());
			for (LotteryOpenTime tmpBean : opList) {
				String openTime = tmpBean.getOpenTime();
				if(openTime.indexOf(date) != -1) {
					OpenTime bean = new OpenTime();
					bean.setExpect(tmpBean.getExpect());
					bean.setStartTime(tmpBean.getStartTime());
					bean.setStopTime(tmpBean.getStopTime());
					bean.setOpenTime(tmpBean.getOpenTime());
					list.add(bean);
				}
			}
		}
		return list;
	}

	@Override
	public OpenTime getOpenTime(int lotteryId, String expect) {
		Lottery lottery = df.getLottery(lotteryId);
		if(lottery != null) {
			List<LotteryOpenTime> opList = df.listLotteryOpenTime(lottery.getShortName());
			for (LotteryOpenTime tmpBean : opList) {
				String thisExpect = tmpBean.getExpect();
				if(thisExpect.equals(expect)) {
					OpenTime bean = new OpenTime();
					bean.setExpect(tmpBean.getExpect());
					bean.setStartTime(tmpBean.getStartTime());
					bean.setStopTime(tmpBean.getStopTime());
					bean.setOpenTime(tmpBean.getOpenTime());
					return bean;
				}
			}
		}
		return null;
	}

}