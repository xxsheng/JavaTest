package lottery.domains.pool.play.impl;

import javautils.array.ArrayUtils;
import lottery.domains.pool.play.HConstants;
import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;

/**
 * 大小单双 pk10  玩法处理类
 */
public class DXDSPK10PlayHanlder implements ITicketPlayHandler {
	private String playId;
	private int[] offsets;

	String[] OFFSETS_DA = new String[] {"06", "07", "08", "09", "10"};
	String[] OFFSETS_XIAO = new String[] {"01", "02", "03", "04", "05"};
	String[] OFFSETS_DAN = new String[] { "01", "03", "05", "07", "09" };
	String[] OFFSETS_SHUANG = new String[] {"02", "04", "06", "08", "10"};

	/**
	 * 定位胆大小单双玩法，对offsets进行求和，自动判断是大小还是单双
	 * @param playId 玩法ID
	 * @param offset 定位胆位置
	 */
	public DXDSPK10PlayHanlder(String playId, int offset) {
		this.playId = playId;
		this.offsets = new int[]{offset};
	}
	
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.trim().split(" ");
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
		if (nums == null || openNums == null) {
			return result;
		}

		if (ArrayUtils.hasSame(nums)) {
			return result;
		}

		for (String num : nums) {
			if (num.length() > 1) {
				return result;
			}
		}

		int winNum = 0;
		for (String num : nums) {
			String[] offs = matchingBetNum(num);

			if (Arrays.binarySearch(offs, openNum[0]) >= 0) {
				winNum++;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
	
	private String[] matchingBetNum(String betNum){
		switch (betNum) {
			case HConstants.TicketSeriesType.SSC_DA:
				return OFFSETS_DA;
			case HConstants.TicketSeriesType.SSC_XIAO:
				return OFFSETS_XIAO;
			case HConstants.TicketSeriesType.SSC_DAN:
				return OFFSETS_DAN;
			case HConstants.TicketSeriesType.SSC_SHUANG:
				return OFFSETS_SHUANG;
			default:
				return null;
		}
	}

	public static void main(String[] args) {
		String betNum = "双";
		String openNum = "09,06,05,08,10,02,04,03,01,07";

		DXDSPK10PlayHanlder hanlder = new DXDSPK10PlayHanlder("bjpk10_dw5dxds", 4);
		WinResult winResult = hanlder.calculateWinNum(1, betNum, openNum);
		System.out.println(winResult.getWinNum());
	}
}
