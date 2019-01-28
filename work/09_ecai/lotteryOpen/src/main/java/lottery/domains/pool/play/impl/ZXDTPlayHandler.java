package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;

/**
 * 直选 胆拖 玩法处理类  只适用K3
 */
public class ZXDTPlayHandler implements ITicketPlayHandler {

	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public ZXDTPlayHandler(String playId,int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}

	@Override
	public String[] getBetNums(String betNums) {
		String[] nums = betNums.trim().split(",");
		String[] res = new String[nums.length];
		for (int i = 0; i < nums.length; i++) {
			if (nums[i] != null && nums[i].trim().length() > 0) {
				char[] bnums = nums[i].toCharArray();
				Arrays.sort(bnums);
				StringBuilder sb = new StringBuilder();
				for (char bnum : bnums) {
					sb.append(bnum).append(" ");
				}
				res[i] = sb.toString().trim();
			}
		}
		return res;
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getSortedOpenNums(openNums, offsets);
	}


	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] openNum = getOpenNums(openNums);
		String[] nums = getBetNums(index);
		if (nums == null || nums.length != 2 || openNum == null || openNum.length != offsets.length) {
			return result;
		}
		String[] dNums = nums[0].trim().split(" ");
		String[] tNums = nums[1].trim().split(" ");
		int dhit = 0;
		for (String dnum : dNums) {
			if (Arrays.binarySearch(openNum, dnum) >= 0) {
				dhit++;
			}
		}
		int thit = 0;
		for (String tnum : tNums) {
			if (Arrays.binarySearch(openNum, tnum) >= 0) {
				thit++;
			}
		}	
		result.setPlayId(playId);
		result.setWinNum( dhit * thit);
		return result;
	}
}
