package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;

/**
 * 11选5 直选 单式 玩法处理类
 *
 */
public class ZHXDS115PlayHandler implements ITicketPlayHandler {

	/** 玩法ID */
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public ZHXDS115PlayHandler(String playId, int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}

	@Override
	public String[] getBetNums(String betNums) {
		return betNums.trim().split(";");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, offsets);
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] nums = getBetNums(index);
		String[] openNum = getOpenNums(openNums);
		if (nums == null || openNum == null || openNum.length != offsets.length) {
			return result;
		}
		StringBuilder openRes = new StringBuilder();
		for (String num : openNum) {
			openRes.append(num).append(" ");
		}
		int winNum = 0;
		Arrays.sort(nums);
		if (Arrays.binarySearch(nums, openRes.toString().trim()) >= 0) {
			winNum = 1;
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
}
