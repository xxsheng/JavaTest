package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * 11选5 组选 单式 玩法处理类
 */
public class RXDS115PlayHandler implements ITicketPlayHandler {

	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	/** 单式每注号码数 */
	private int dsNum;

	public RXDS115PlayHandler(String playId, int dsNum, int[] offsets) {
		this.playId = playId;
		this.dsNum = dsNum;
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
		int winNum = 0;
		if (dsNum > 5) {
			for (int i = 0; i < nums.length; i++) {
				Collection<?> inter = CollectionUtils.intersection(Arrays.asList(nums[i].split(" ")),
						Arrays.asList(openNum)); // 求交集
				if (inter.size() == 5) {
					winNum++;
				}
			}
		} else {
			for (int i = 0; i < nums.length; i++) {
				Collection<?> inter = CollectionUtils.intersection(Arrays.asList(nums[i].split(" ")),
						Arrays.asList(openNum));
				if (inter.size() == dsNum) {
					winNum++;
				}
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
}
