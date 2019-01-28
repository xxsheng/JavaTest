package lottery.domains.pool.play.impl;




import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 * 定位胆 玩法处理类
 */
public class DWDPlayHandler implements ITicketPlayHandler {
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public DWDPlayHandler(String playId,int[] offsets) {
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
		String[] openNum = getOpenNums(openNums);
		String[] nums = getBetNums(index);
		if (nums == null || openNum == null || openNum.length != offsets.length) {
			return result;
		}
		int winNum = 0;
		for (int i = 0; i < offsets.length; i++) {
			String ssnum = nums[offsets[i]];
			if(ssnum.indexOf(openNum[i]) >=0){
				winNum++;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
	
//	public static void main(String[] args) {
//		//9,8,7,6,5
//		String betNum = "01 03 05 07 09,"
//				+ "01 03 05 07 09,"
//				+ "01 03 05x 07 09";
//		String openCode = "03,07,10,02,06,04,09,08,01,05";
//		int offset[] = new int[] { 0, 1, 2};
//
//		DWDPlayHandler hd = new DWDPlayHandler("单式", offset);
//		WinResult resulthd = hd.calculateWinNum(betNum, openCode);
//
//		String[] openCodeArray = hd.getOpenNums(openCode);
//		StringBuffer sbf = new StringBuffer();
//		for (int i = 0; i < openCodeArray.length; i++) {
//			sbf.append(openCodeArray[i]).append(",");
//		}
//		System.out.println("开奖号码为：" + sbf.toString().substring(0, sbf.toString().length() - 1));
//		
//		if (resulthd ==null) {
//			System.out.println("错误-----");
//		} else {
//			System.out.println("中奖注数：" + resulthd.getWinNum() + ",玩法ID为：" + resulthd.getPlayId() + ",中奖号码:"
//					+ resulthd.getWinCode());
//		}
//	}

	public static void main(String[] args) {
		// String betNum = "01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10";
		// String openCode = "06,04,09,07,02,01,03,10,05,08";
        //
		// DWDPlayHandler handler = new DWDPlayHandler("bjpk10_qwdingweidan", ITicketPlayHandler.OFFSETS_QIANWU);
		// WinResult winResult = handler.calculateWinNum(betNum, openCode);
		// System.out.println(winResult.getWinNum());

		String betNum = "01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10";
		String openCode = "06,04,09,07,02,01,03,10,05,08";

		DWDPlayHandler handler = new DWDPlayHandler("bjpk10_hwdingweidan", ITicketPlayHandler.OFFSETS_HOUWU);
		WinResult winResult = handler.calculateWinNum(1, betNum, openCode);
		System.out.println(winResult.getWinNum());
	}
}
