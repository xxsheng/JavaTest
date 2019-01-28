package lottery.domains.pool.play.impl;



import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 *  PK10  直选单式 玩法集合
 */
public class ZHXDSPK10PlayHandler   implements ITicketPlayHandler {
	
	/** 玩法ID */
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	
	public ZHXDSPK10PlayHandler(String playId, int[] offsets) {
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
				winNum ++;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
}
