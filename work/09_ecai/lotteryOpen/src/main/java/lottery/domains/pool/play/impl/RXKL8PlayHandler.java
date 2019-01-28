package lottery.domains.pool.play.impl;

import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;

public class RXKL8PlayHandler implements ITicketPlayHandler {
	private String playId;
	/** 不定位码数 */
	private int bdwNum;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	
	public RXKL8PlayHandler(String playId,int bdwNum, int[] offsets) {
		this.playId= playId;
		this.bdwNum = bdwNum;
		this.offsets = offsets;
	}

	@Override
	public String[] getBetNums(String betNums) {
//		String[] nums = betNums.trim().split(",");
		return  betNums.trim().split(",");
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
		int matchNum = bdwMatchNum(nums, openNum);
//		if (bdwNum > 5) {
//			if (matchNum < 5) {
//				return result;
//			}
//			WinResult win = new WinResult();
//			win.setPlayId(playId);
//			win.setWinNum(bdwNum(nums.length-matchNum, bdwNum-20));
//			result.add(win);
//			return result;
//		} else {
//			
//		}
		if (matchNum < bdwNum) {
			return result;
		}
		result.setPlayId(playId);
		result.setWinNum(bdwNum(matchNum, bdwNum));
		return result;
	}
	
	/**
	 * 中奖号码数计算公式
	 * 
	 * @param betNums
	 * @param openNums
	 * @return
	 */
	private static int bdwMatchNum(String[] betNums, String[] openNums) {
		int matchNum = 0;
		Arrays.sort(openNums);
		for (String num : betNums) {
			if (Arrays.binarySearch(openNums, num) >= 0) {
				matchNum++;
			}
		}
		return matchNum;
	}

	/**
	 * 注数计算公式：betNum!/((betNum-bdwNum)!*bdwNum!)
	 * 
	 * @param betNum
	 *            投注号码数
	 * @param bdwNum
	 *            不定位码数
	 * @return
	 */
	private static int bdwNum(int betNum, int bdwNum) {
		int upCount = 1;
		int downCount = 1;
		for (int i = 0; i < bdwNum; i++) {
			upCount = upCount * (betNum - i);
		}
		for (int a = 1; a <= bdwNum; a++) {
			downCount = downCount * a;
		}
		return upCount / downCount;
	}
	
//	public static void main(String[] args) {
//		String betNum = "01,02,03,04,05,06,02,09";
//		
////		String betNum = "01,02,03,04,05,06,07,08,09,10,"
////				+ "11,12,13,14,15,16,17,18,19,20,"
////				+ "21,22,23,24,25,26,27,28,29,30,"
////				+ "31,32,33,34,35,36,37,38,39,40,"
////				+ "41,42,43,44,45,46,47,48,49,50,"
////				+ "51,52,53,54,55,56,57,58,59,60,"
////				+ "61,62,63,64,65,66,67,68,69,70,"
////				+ "71,72,73,74,75,76,77,78,79,80";
//		
//		String openCode = "80,02,03,04,05,06,11,08,34,37,38,47,51,07,60,61,01,66,68,74";
//		int offset[] = new int[] { 0, 1, 2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
//		
//		
//		RXXZXKL8PlayHandler  hd = new RXXZXKL8PlayHandler("单式",7,offset);
//		List<WinResult> result = hd.calculateWinNum(betNum, openCode);
//		
//		String [] openCodeArray = hd.getOpenNums(openCode);
//		StringBuffer sbf = new StringBuffer();
//		for (int i = 0; i < openCodeArray.length; i++) {
//			sbf.append(openCodeArray[i]).append(",");
//		}
//		System.out.println("开奖号码为："+sbf.toString().substring(0,sbf.toString().length()-1));
//		
//		if(result.size()<=0){
//			System.out.println("错误-----");
//		}else{
//			System.out.println("中奖注数："+result.get(0).getWinNum()+
//					",玩法ID为："+result.get(0).getPlayId()+",中奖号码:"+result.get(0).getWinCode());
//		}
//	}
}
