package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 *时时彩 趣味玩法
 */
public class SSCQWPlayHandler implements ITicketPlayHandler {
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	/** 趣味玩法 中奖个数 */
	private int rxNum;

	public SSCQWPlayHandler(String playId,int[] offsets,int rxNum) {
		this.playId = playId;
		this.offsets = offsets;
		this.rxNum =rxNum;
	}
	@Override
	public String[] getBetNums(String betNums) {
		String bets[] = betNums.trim().split(",");
		return TicketPlayUtils.getFixedNums(bets);
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
		int winNum = 0;
		for (int i = 0; i < nums.length; i++) {
			int count = 0;
			for (int j = 0; j < openNum.length; j++) {
				if(nums[i].equals(openNum[j])){
					count ++;
				}
			}
			if(count >= rxNum){
				winNum ++;
				count = 0;
			}
		}
		
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
//	
//	
//	public static void main(String[] args) {
//		String [] open = new String []{"1","2","3","4","2"};
//		String [] ditOpen = TicketPlayUtils.getFixedNums(open);
//
//		
//		SSCQWPlayHandler bdw = new SSCQWPlayHandler("12321",ITicketPlayHandler.OFFSETS_WUXIN,2);
//		WinResult win = bdw.calculateWinNum("0,1,2,3,4,5,6,7,8,9", "2,1,2,4,3");
//		System.out.println(win.getWinNum());
//	}
}
