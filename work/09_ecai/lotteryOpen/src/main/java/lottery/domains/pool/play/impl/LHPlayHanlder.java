package lottery.domains.pool.play.impl;

import javautils.array.ArrayUtils;
import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;

/**
 * 龙虎  玩法处理类
 */
public class LHPlayHanlder implements ITicketPlayHandler {
	private String playId;
	private int[] offsets;
	private String codeSeparator;

	private static final String HE = "和";
	private static final String LONG = "龙";
	private static final String HU = "虎";

	/**
	 * 龙虎玩法
	 * @param playId 玩法ID
	 * @param offset1 位置1
	 * @param offset2 位置2
	 */
	public LHPlayHanlder(String playId, int offset1, int offset2, String codeSeparator) {
		this.playId = playId;
		this.offsets = new int[]{offset1, offset2};
		this.codeSeparator = codeSeparator;
	}
	
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.trim().split(codeSeparator);
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

		String transferResult = transferOpenNum(openNum);

		if (index.indexOf(transferResult) >= 0) {
			result.setWinNum(1);
		}

		result.setPlayId(playId);
		return result;
	}
	
	private String transferOpenNum(String[] openNum){
		Integer openNum1 = Integer.valueOf(openNum[0]);
		Integer openNum2 = Integer.valueOf(openNum[1]);

		if (openNum1 == openNum2) {
			return HE;
		}
		if (openNum1 > openNum2) {
			return LONG;
		}

		return HU;
	}

	public static void main(String[] args) {
		String betNum = "龙 虎 和";
		String openNum = "07,06,02,03,09,08,10,01,04,07";

		LHPlayHanlder hanlder = new LHPlayHanlder("01vs10", 0, 9, " ");
		WinResult winResult = hanlder.calculateWinNum(1, betNum, openNum);
		System.out.println(winResult.getWinNum());
	}
}
