package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 * 快三二同号单选标准选玩法
 */
public class ZXETHDXBZK3PlayHander implements ITicketPlayHandler {
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	/** 玩法ID */
	private String playId;
	
	public ZXETHDXBZK3PlayHander(String playId,int[] offsets) {
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
		if (nums == null ||openNum == null || nums.length != 2) {
			return result;
		}
		int winNum = 0;
		int odd = 0;
		int dual = 0;
		char [] oddBet = nums[1].toCharArray();
		for (int i = 0; i < oddBet.length; i++) {
			if(openNums.contains(String.valueOf(oddBet[i]).trim())){
				odd ++;
			}
		}
		for (int i = 0; i < openNum.length; i++) {
			if(openNum[i].trim().indexOf(nums[0].trim()) > -1){
				dual ++;
			}
		}
		if((dual == 2) && (odd ==1)){
			winNum = 1;
		}
		
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
	
//	public static void main(String[] args) {
//		String betNum = "5,12346";
//		String openCode = "5,1,5";
//		int offset[] = new int[] { 0, 1, 2 };
//
//		ZXETHK3PlayHander hd = new ZXETHK3PlayHander("单式", offset);
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
