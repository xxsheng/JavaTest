package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;

/**
 *K3 连号玩法处理
 */
public class TXK3SLHPlayHandler implements ITicketPlayHandler {
	private String playId;
	private int [] offsets;
	public TXK3SLHPlayHandler(String playId,int [] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.split(",");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return openNums.split(",");
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] nums = getBetNums(index);
		String[] openNum = getOpenNums(openNums);
		if (nums == null || openNum == null) {
			return result;
		}
		int winNum = 0;
		StringBuilder sb = new StringBuilder();
		for (String nm : openNum) {
			sb.append(nm.trim());
		}
		
		for (int i = 0; i < nums.length; i++) {
			if(nums[i].equals(sb.toString())){
				winNum ++;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
//
//	public static void main(String[] args) {
//		String betNum = "123,234,345,456";
//		String openCode = "1,2,4";
//		int offset[] = new int[] { 0, 1, 2 };
//
//		TXK3SLHPlayHandler hd = new TXK3SLHPlayHandler("单式",offset);
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
