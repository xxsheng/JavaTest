package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;


/**
 * 不定位 玩法处理类 *
 * 
 */
public class BDWPlayHandler implements ITicketPlayHandler {
	private String playId;
	/** 不定位码数 */
	private int bdwNum;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	
	public BDWPlayHandler(String playId,int bdwNum, int[] offsets) {
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
		if (bdwNum > 5) {
			if (matchNum < 5) {
				return result;
			}
			result.setPlayId(playId);
			int wnum = bdwNum(nums.length-matchNum, bdwNum-5);
			result.setWinNum(wnum);
			if(wnum > 0){
				//用于11X5的猜中位奖金计算
				result.setWinCode(openNum[0]);
			}
			return result;
		} else {
			if (matchNum < bdwNum) {
				return result;
			}
			result.setPlayId(playId);
			int wnum = bdwNum(matchNum, bdwNum);
			result.setWinNum(wnum);
			if(wnum > 0){
				//用于11X5的猜中位奖金计算
				result.setWinCode(openNum[0]);
			}
			return result;
		}
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

	public static void main(String[] args) {
		// BDWPlayHandler cqssc_bdwwx2m = new BDWPlayHandler("1_bdwwx2m", 2, ITicketPlayHandler.OFFSETS_WUXIN);
		// WinResult winResult = cqssc_bdwwx2m.calculateWinNum(1, "0,1,2,3,4,5,6,7", "6,7,6,0,5"); // 67 60 65 70 75 05
		// System.out.println(winResult.getWinNum());

		BDWPlayHandler cqssc_bdwwx3m = new BDWPlayHandler("1_bdwwx3m", 3, ITicketPlayHandler.OFFSETS_WUXIN);
		WinResult winResult = cqssc_bdwwx3m.calculateWinNum(1, "0,1,2,3,4,5,6,7", "7,3,2,0,1"); // 732 731 721 321
		System.out.println(winResult.getWinNum());

		// BDWPlayHandler cqssc_bdwsix2mq = new BDWPlayHandler("1_bdwsix2mq", 2, ITicketPlayHandler.OFFSETS_QIANSI);
		// WinResult winResult = cqssc_bdwsix2mq.calculateWinNum(1, "0,1,2,3,4,5,6,7", "6,7,6,0,5"); // 67 60 70
		// System.out.println(winResult.getWinNum());
	}
}
