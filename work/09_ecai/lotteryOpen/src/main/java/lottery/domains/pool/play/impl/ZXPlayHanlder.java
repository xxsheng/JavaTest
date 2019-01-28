package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 组选xxx 玩法处理类  *-
 *
 */
public class ZXPlayHanlder implements ITicketPlayHandler {
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	/** 多的号码数 */
	private int moreNumCount;
	/** 多的号码重数 */
	private int moreNumTimes;
	/** 少的号码数 */
	private int fewerNumCount;
	/** 少的号码重数 */
	private int fewerNumTimes;

	public ZXPlayHanlder(String playId,int moreNumCount, int moreNumTimes,
			int fewerNumCount, int fewerNumTimes,int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
		this.moreNumCount = moreNumCount;
		this.moreNumTimes = moreNumTimes;
		this.fewerNumCount = fewerNumCount;
		this.fewerNumTimes = fewerNumTimes;
	}

	
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.trim().split(",");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, offsets);
	}

//	@Override
//	public int calculateWinNum(String index, String openNums) {
//		String[] nums = getIndexOpenNum(openNums);
//		if (nums == null || nums.length != 2) {
//			return 0;
//		}
//		int winNum = 1;
//		String[] betNum = getBetNums(index);
//		for (int i = 0; i < nums[0].length(); i++) {
//			if (!betNum[0].contains(String.valueOf(nums[0].charAt(i)))) {
//				winNum = 0;
//				break;
//			}
//		}
//		for (int i = 0; i < nums[1].length(); i++) {
//			
//			if (!betNum[1].contains(String.valueOf(nums[1].charAt(i)))) {
//				winNum = 0;
//				break;
//			}
//		}
//		return winNum;
//	}
	
	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] nums = getIndexOpenNum(openNums);
		if (nums == null || nums.length != 2) {
			return result;
		}
		int winNum = 1;
		String[] betNum = getBetNums(index);
		for (int i = 0; i < nums[0].length(); i++) {
			if (!betNum[0].contains(String.valueOf(nums[0].charAt(i)))) {
				winNum = 0;
				break;
			}
		}
		for (int i = 0; i < nums[1].length(); i++) {
			if (!betNum[1].contains(String.valueOf(nums[1].charAt(i)))) {
				winNum = 0;
				break;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
	
	private String[] getIndexOpenNum(String openNums) {
		String[] openNum = TicketPlayUtils.getOpenNums(openNums, offsets);
		String[] res = new String[2];
		Map<String, Integer> nums = new HashMap<>();
		for (String num : openNum) {
			if (nums.get(num) == null) {
				nums.put(num, 1);
			} else {
				nums.put(num, nums.get(num) + 1);
			}
		}
		Iterator<String> it = nums.keySet().iterator();
		String c = null;
		StringBuilder moreNum = new StringBuilder();
		StringBuilder fewerNum = new StringBuilder();
		int mCount = 0;
		int fCount = 0;
		while (it.hasNext()) {
			c = it.next();
			if (nums.get(c) == moreNumTimes) {
				//bill
				moreNum.append(c);//.append(" "); 
				mCount++;
			} else if (nums.get(c) == fewerNumTimes) {
				//bill
				fewerNum.append(c);//.append(" ");
				fCount++;
			}
		}
		if (mCount != moreNumCount || fCount != fewerNumCount) {
			return null;
		}
		res[0] = moreNum.toString().trim();
		res[1] = fewerNum.toString().trim();
		return res;
	}
}
