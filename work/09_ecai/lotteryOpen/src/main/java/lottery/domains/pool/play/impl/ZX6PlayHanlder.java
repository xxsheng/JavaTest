package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 时时彩 四星组选6 玩法处理类 *
 *
 */
public class ZX6PlayHanlder implements ITicketPlayHandler {
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public ZX6PlayHanlder(String playId,int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}

	@Override
	public String[] getBetNums(String betNums) {
//		String[] nums = betNums.trim().split(",");
//		String[] res = new String[nums.length];
//		for (int i = 0; i < nums.length; i++) {
//			res[i] = nums[i].trim();
//		}
		return betNums.trim().split(",");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, offsets);
	}

//	@Override
//	public int calculateWinNum(String index, String openNums) {
//		String nums = getIndexOpenNum(getOpenNums(openNums));
//		if (nums == null || nums.length() != 2) {
//			return 0;
//		}
//		int winNum = 1;
//		for (int i = 0; i < nums.length(); i++) {
//			if (!index.contains(String.valueOf(nums.charAt(i)))) {
//				winNum = 0;
//				break;
//			}
//		}
//		return winNum;
//	}
	
	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String nums = getIndexOpenNum(getOpenNums(openNums));
		if (nums == null || nums.length() != 2) {
			return result;
		}
		int winNum = 1;
		for (int i = 0; i < nums.length(); i++) {
			if (!index.contains(String.valueOf(nums.charAt(i)))) {
				winNum = 0;
				break;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}

	private String getIndexOpenNum(String[] openNums) {
		Map<String, Integer> nums = new HashMap<>();
		for (String num : openNums) {
			if (nums.get(num) == null) {
				nums.put(num, 1);
			} else {
				nums.put(num, nums.get(num) + 1);
			}
		}
		Iterator<String> it = nums.keySet().iterator();
		String c = null;
		StringBuilder resNum = new StringBuilder();
		while (it.hasNext()) {
			c = it.next();
			if (nums.get(c) == 2) {
				resNum.append(c);
			}
		}
		if (resNum.length() != 2) {
			return null;
		}
		return resNum.toString();
	}
}
