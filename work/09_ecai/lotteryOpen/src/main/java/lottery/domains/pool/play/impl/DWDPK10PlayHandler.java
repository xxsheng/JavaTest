package lottery.domains.pool.play.impl;




import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 * 定位胆 pk10 玩法处理类
 */
public class DWDPK10PlayHandler implements ITicketPlayHandler {
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public DWDPK10PlayHandler(String playId, int[] offsets) {
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
			String dwNum = nums[i];

			if ("-".equals(dwNum)) {
				continue;
			}

			if(dwNum.indexOf(openNum[i]) >=0){
				winNum++;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}

	public static void main(String[] args) {
		// String betNum = "01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10";
		// String openCode = "06,04,09,07,02,01,03,10,05,08";
        //
		// DWDPlayHandler handler = new DWDPlayHandler("bjpk10_qwdingweidan", ITicketPlayHandler.OFFSETS_QIANWU);
		// WinResult winResult = handler.calculateWinNum(betNum, openCode);
		// System.out.println(winResult.getWinNum());

		String betNum = "01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,01 02 03 04 05 06 07 08 09 10,-";
		String openCode = "06,04,09,07,02,01,03,10,05,08";

		DWDPK10PlayHandler handler = new DWDPK10PlayHandler("bjpk10_hwdingweidan", ITicketPlayHandler.OFFSETS_HOUWU);
		WinResult winResult = handler.calculateWinNum(1, betNum, openCode);
		System.out.println(winResult.getWinNum());
	}
}
