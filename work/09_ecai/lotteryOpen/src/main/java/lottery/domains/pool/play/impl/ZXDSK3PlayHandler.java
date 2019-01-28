package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;

/**
 * 快3 X不同号手动选号 玩法处理类
 */
public class ZXDSK3PlayHandler implements ITicketPlayHandler {

	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public ZXDSK3PlayHandler(String playId,int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}
	@Override
	public String[] getBetNums(String betNums) {
		String[] nums = betNums.trim().split(" ");
		String[]  res = new String[nums.length];
		for (int i = 0; i < nums.length; i++) {
			if (nums[i] != null && nums[i].trim().length() > 0) {
				char[] bnums = nums[i].toCharArray();
				Arrays.sort(bnums);
				StringBuilder sb = new StringBuilder();
				for (char bnum : bnums) {
					sb.append(bnum);
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
		if (nums == null || openNum == null) {
			return result;
		}
		StringBuilder sb = new StringBuilder();
		for (String nm : openNum) {
			sb.append(nm.trim());
		}
		int winNum = 0;
		for (int i = 0; i < nums.length; i++) {
			if (sb.toString().trim().contains(nums[i])) {
				//因为投注时没有去重，所有按++ 计算
				winNum ++;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
	
//	public static void main(String[] args) {
//		String betNum = "123 321 426 236 369 618";
//		String openCode = "3,1,2";
//		int offset [] = new int[]{0,1,2};
//		
//		ZXDSK3PlayHandler  hd = new ZXDSK3PlayHandler("单式",offset);
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
