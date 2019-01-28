package lottery.domains.pool.play.impl;



import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;


/**
 * 直选 和值 玩法处理类 *
 */
public class ZHHZPlayHandler implements ITicketPlayHandler {
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public ZHHZPlayHandler(String playId,int[] offsets) {
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

//	@Override
//	public int calculateWinNum(String index, String openNums) {
//		String[] nums = getBetNums(index);
//		if (nums == null || openNums == null) {
//			return 0;
//		}
//		int winNum = 0;
//		int sum = TicketPlayUtils.getOpenNumSum(openNums, offsets);
//		for (int i = 0; i < nums.length; i++) {
//			if (Integer.parseInt(nums[i]) == sum) {
//				// if (offsets.length == 3) {
//				// return SANXING_BETS[sum];
//				// } else if (offsets.length == 2) {
//				// return ERXING_BETS[sum];
//				// }
//				winNum = 1;
//				break;
//			}
//		}
//		return winNum;
//	}
	
	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] nums = getBetNums(index);
		if (nums == null || openNums == null) {
			return result;
		}
		int winNum = 0;
		int sum = TicketPlayUtils.getOpenNumSum(openNums, offsets);
		for (int i = 0; i < nums.length; i++) {
			if (Integer.parseInt(nums[i]) == sum) {
				//K3 和值需要根据中奖号码来判断奖金
				result.setWinCode(nums[i]);
				winNum = 1;
				break;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
	

//	public static void main(String[] args) {
//		String betNum = "3,4,5,6,7,8,9,10,11,12";
//		String openCode = "0,6,6";
//		int offset[] = new int[] { 0, 1, 2 };
//
//		ZHHZPlayHandler hd = new ZHHZPlayHandler("单式", offset);
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
