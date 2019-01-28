package lottery.domains.pool.play.impl;

import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;

/**
 *K3 同号玩法处理
 */
public class DXTXK3STHPlayHandler implements ITicketPlayHandler {
	private String playId;
	private int [] offsets;
	/** 同号返回个数 如，全同  = 1*/
	private int bthIndexNum;
	public DXTXK3STHPlayHandler(String playId,int bthIndexNum,int [] offsets) {
		this.playId = playId;
		this.offsets = offsets;
		this.bthIndexNum = bthIndexNum;
	}
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.split(",");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		String [] openNum = openNums.split(",");
		return TicketPlayUtils.getFixedNums(openNum);
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] nums = getBetNums(index);
		String[] openNum = getOpenNums(openNums);
		if (nums == null || openNum == null || openNum.length != bthIndexNum) {
			return result;
		}
		int winNum = 0;
		Arrays.sort(openNum);
		StringBuilder sb = new StringBuilder();
		for (String nm : openNum) {
			sb.append(nm.trim());
		}
		
		for (int i = 0; i < nums.length; i++) {
			if(nums[i].contains(sb.toString())){
				winNum ++;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
//
//	public static void main(String[] args) {
//		String betNum = "111,222,333,444,555,666";
//		String openCode = "3,3,3";
//		int offset[] = new int[] { 0, 1, 2 };
//
//		DXTXK3STHPlayHandler hd = new DXTXK3STHPlayHandler("单式", 1,offset);
//		WinResult result = hd.calculateWinNum(betNum, openCode);
//
//		String[] openCodeArray = hd.getOpenNums(openCode);
//		StringBuffer sbf = new StringBuffer();
//		for (int i = 0; i < openCodeArray.length; i++) {
//			sbf.append(openCodeArray[i]).append(",");
//		}
//		System.out.println("开奖号码为：" + sbf.toString().substring(0, sbf.toString().length() - 1));
//
//			System.out.println("中奖注数：" + result.getWinNum() + ",玩法ID为：" + result.getPlayId() + ",中奖号码:"
//					+ result.getWinCode());
//	}
}
