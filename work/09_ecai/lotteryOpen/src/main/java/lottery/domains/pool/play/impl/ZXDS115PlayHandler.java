package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;

/**
 * 11选5 组选 单式 玩法处理类
 */
public class ZXDS115PlayHandler implements ITicketPlayHandler {

	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public ZXDS115PlayHandler(String playId,int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}
	@Override
	public String[] getBetNums(String betNums) {
		String[] nums = betNums.trim().split(";");
		String[]  res = new String[nums.length];
		for (int i = 0; i < nums.length; i++) {
			if (nums[i] != null && nums[i].trim().length() > 0) {
				String[] bnums = nums[i].split(" ");
				Arrays.sort(bnums);
				StringBuilder sb = new StringBuilder();
				for (String bnum : bnums) {
					sb.append(bnum).append(" ");
				}
				res[i] = sb.toString().trim();
			}
		}
		return res;
	}

	@Override
	public String[] getOpenNums(String openNums) {
		String[] open = TicketPlayUtils.getOpenNums(openNums, offsets);
		Arrays.sort(open);
		return open;
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] nums = getBetNums(index);
		String[] openNum = getOpenNums(openNums);
		if (nums == null || openNums == null || openNum == null) {
			return result;
		}
		StringBuilder sb = new StringBuilder();
		for (String nm : openNum) {
			sb.append(nm.trim()).append(" ");
		}
		int winNum = 0;
		for (int i = 0; i < nums.length; i++) {
			if (nums[i].equals(sb.toString().trim())) {
				//因为投注时没有去重，所有按++ 计算
				winNum ++;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
}
