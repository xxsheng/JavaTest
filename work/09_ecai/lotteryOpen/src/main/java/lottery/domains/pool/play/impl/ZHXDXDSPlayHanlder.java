package lottery.domains.pool.play.impl;

import lottery.domains.pool.play.HConstants;
import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;

/**
 *时时彩 直选 大小单双   玩法处理类
 */
public class ZHXDXDSPlayHanlder implements ITicketPlayHandler {
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	
	
	/** SSC 大小单双玩法 (大号)对应位置 */
	String[] OFFSETS_SSC_DA = new String[] { "5", "6", "7", "8", "9" };
	/** SSC 大小单双玩法 (小号)对应位置 */
	String[] OFFSETS_SSC_XIAO = new String[] { "0", "1", "2", "3", "4" };
	/** SSC 大小单双玩法 (单号)对应位置 */
	String[] OFFSETS_SSC_DAN = new String[] { "1", "3", "5", "7", "9" };
	/** SSC 大小单双玩法 (双号)对应位置 */
	String[] OFFSETS_SSC_SHUANG = new String[] { "0", "2", "4", "6", "8" };
	
	public ZHXDXDSPlayHanlder(String playId,int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}
	
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.trim().split(",");
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
		if (nums == null || openNums == null) {
			return result;
		}
		if(openNum == null || openNum.length != offsets.length){
			return result;
		}
		int index0 = 0;
		int index1 = 0;
		//十位/万位
		char [] bet0 = nums[0].toCharArray();
		for (int j = 0; j < bet0.length; j++) {
			String bj0 [] = matchingBetNum(String.valueOf(bet0[j]));
			if(bj0 == null){
				return result;
			}
			if (Arrays.binarySearch(bj0, openNum[0]) >= 0) {
				index0++;
			}
		}
		//个位/千位
		char [] bet1 = nums[1].toCharArray();
		for (int n = 0; n < bet1.length; n++) {
			String bj1 [] = matchingBetNum(String.valueOf(bet1[n]));
			if(bj1 == null){
				return result;
			}
			if (Arrays.binarySearch(bj1, openNum[1]) >= 0) {
				index1++;
			}
		}
		
		int winNum = 0;
		if(index0 >0 && index1 >0){
			winNum = index0 * index1;
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
	
	/**
	 * 匹配SSC大小单双玩法投注号码
	 * @param betNum
	 * @return
	 */
	private String[] matchingBetNum(String betNum){
		switch (betNum) {
			case HConstants.TicketSeriesType.SSC_DA:
				return  OFFSETS_SSC_DA;
			case HConstants.TicketSeriesType.SSC_XIAO:
				return  OFFSETS_SSC_XIAO;
			case HConstants.TicketSeriesType.SSC_DAN:
				return  OFFSETS_SSC_DAN;
			case HConstants.TicketSeriesType.SSC_SHUANG:
				return  OFFSETS_SSC_SHUANG;
			default:
				return null;
		}
	}
}
