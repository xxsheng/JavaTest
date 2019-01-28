package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 *K3 复选 二同号 复式玩法
 */
public class ZXETHFXK3PlayHander implements ITicketPlayHandler {
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	/** 玩法ID */
	private String playId;
	
	public ZXETHFXK3PlayHander(String playId,int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}
	
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.split(",");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, offsets);
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String [] nums = getBetNums(index);
		String[] openNum = getOpenNums(openNums);
		if (nums == null ||openNum == null) {
			return result;
		}
		int winNum = 0;
		String openDual = TicketPlayUtils.getOpenDualNum(openNum);
		if(openDual != null && openDual.length() >0){
			for (int i = 0; i < nums.length; i++) {
				if(openDual.equals(nums[i])){
					winNum = 1;
				}
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
//	
//	public static void main(String[] args) {
//		String betNum = "1,2,3,4,5,6";
//		String openCode = "3,5,1";
//		int offset[] = new int[] { 0, 1, 2 };
//
//		ZXETHFXK3PlayHander hd = new ZXETHFXK3PlayHander("单式", offset);
//		List<WinResult> result = hd.calculateWinNum(betNum, openCode);
//
//		String[] openCodeArray = hd.getOpenNums(openCode);
//		StringBuffer sbf = new StringBuffer();
//		for (int i = 0; i < openCodeArray.length; i++) {
//			sbf.append(openCodeArray[i]).append(",");
//		}
//		System.out.println("开奖号码为：" + sbf.toString().substring(0, sbf.toString().length() - 1));
//
//		if (result.size() <= 0) {
//			System.out.println("错误-----");
//		} else {
//			System.out.println("中奖注数：" + result.get(0).getWinNum() + ",玩法ID为：" + result.get(0).getPlayId() + ",中奖号码:"
//					+ result.get(0).getWinCode());
//		}
//	}
}
