package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 三星组选 组三 玩法处理类 *
 */
public class ZXZ3PlayHanlder implements ITicketPlayHandler {
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public ZXZ3PlayHanlder(String playId,int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}

	@Override
	public String[] getBetNums(String betNums) {
		String[] nums = betNums.trim().split(",");
		return TicketPlayUtils.getFixedNums(nums);
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, offsets);
	}

//	@Override
//	public int calculateWinNum(String index, String openNums) {
//		String[] betNum = getBetNums(index);
//		String[] nums = getIndexOpenNum(openNums);
//		if (nums == null || nums.length != 2) {
//			return 0;
//		}
//		int winNum = 1;
//		Arrays.sort(betNum);
//		for (String num : nums) {
//			if (Arrays.binarySearch(betNum, num) < 0) {
//				winNum = 0;
//				break;
//			}
//		}
//		return winNum;
//	}
	
	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] betNum = getBetNums(index);
		String[] nums = getIndexOpenNum(openNums);
		if (nums == null || nums.length != 2) {
			return result;
		}
		int winNum = 1;
		Arrays.sort(betNum);
		for (String num : nums) {
			if (Arrays.binarySearch(betNum, num) < 0) {
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
		Map<String, Integer> nums = new HashMap<>();
		for (String num : openNum) {
			if (nums.get(num) == null) {
				nums.put(num, 1);
			} else {
				nums.put(num, nums.get(num) + 1);
			}
		}
		if (nums.size() != 2) {
			return null;
		}
		String[] res = new String[2];
		Iterator<String> it = nums.keySet().iterator();
		int idx = 0;
		while (it.hasNext()) {
			res[idx++] = it.next();
		}
		return res;
	}
}
