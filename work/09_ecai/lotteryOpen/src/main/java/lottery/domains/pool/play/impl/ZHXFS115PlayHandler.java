package lottery.domains.pool.play.impl;



import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 * 11选5 直选 复式 玩法处理类
 * 
 */
public class ZHXFS115PlayHandler implements ITicketPlayHandler {
	/** 玩法ID */
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public ZHXFS115PlayHandler(String playId, int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}
	
	@Override
	public String[] getBetNums(String betNums) {
		String[] nums = betNums.trim().split(",");
		String[] res = new String[offsets.length];
		int idx = 0;
		for (int i = 0; i < nums.length; i++) {
			if (!"-".equals(nums[i].trim())) {
				res[idx++] = nums[i].trim();
			}
		}
		return res;
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, offsets);
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] openNum = getOpenNums(openNums);
		String[] nums = getBetNums(index);
		if (nums == null || openNum == null || openNum.length != offsets.length) {
			return result;
		}
		int winNum = 1;
		for (int i = 0; i < offsets.length; i++) {
			if (nums[i].indexOf(openNum[offsets[i]]) < 0) {
				winNum = 0;
				break;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
}
