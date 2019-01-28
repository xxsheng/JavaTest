package lottery.domains.pool.play.impl;

import javautils.list.ListUtil;
import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.MultipleResult;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.*;

/**
 * 和值玩法处理类，中奖金额随中奖号码不同而不同，prize有多个的情况下使用这个
 */
public class HZMultiplePrizePlayHandler implements ITicketPlayHandler {
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	private int[] codes;

	public HZMultiplePrizePlayHandler(String playId, int[] offsets, int codeFrom, int codeTo) {
		this.playId = playId;
		this.offsets = offsets;

		int index = 0;
		codes = new int[codeTo-codeFrom+1];
		for (int i = codeFrom; i <=codeTo ; i++) {
			codes[index++] = i;
		}
	}

	public HZMultiplePrizePlayHandler(String playId, int[] offsets, int[] codes) {
		this.playId = playId;
		this.offsets = offsets;
		this.codes = codes;
		Arrays.sort(this.codes);
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
	public WinResult calculateWinNum(int userBetsId, String betCodes, String openNums) {
		WinResult result = new WinResult();
		String[] nums = getBetNums(betCodes); // 购彩号码
		if (nums == null || openNums == null) {
			return result;
		}
		
		int winNum = 0;

		int sum = TicketPlayUtils.getOpenNumSum(openNums, this.offsets); // 指定开奖位置总和

		Map<String, MultipleResult> codeResults = new HashMap<>();

		HashSet<String> betCodeSet = ListUtil.transToHashSet(nums);

		for (String betCode : betCodeSet) {
			int code = Integer.parseInt(betCode); // 所选号码
			if (code == sum) {
				winNum = 1;
				// 所选号码中了和值
				MultipleResult codeResult;
				if (codeResults.containsKey(betCode)) {
					codeResult = codeResults.get(betCode);
				}
				else {
					int codeIndex = Arrays.binarySearch(this.codes, code);
					codeResult = new MultipleResult(betCode, codeIndex);
					codeResults.put(betCode, codeResult);
				}
				codeResult.increseNum();
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		result.setMultipleResults(new ArrayList<MultipleResult>(codeResults.values()));
		return result;
	}
	

	public static void main(String[] args) {
		// 准确性测试
		String betNum = "6,7,8,9,10,11,12,13,14,15,16,17";
		String openCode = "02,03,01,05,09,07,08,10,04,06";

		HZMultiplePrizePlayHandler hd = new HZMultiplePrizePlayHandler("6_pk10_hzqshz", ITicketPlayHandler.OFFSETS_QIANSAN, 6, 27); // 冠亚和值
		WinResult winResult = hd.calculateWinNum(1, betNum, openCode);
		System.out.println(winResult.getWinNum());
		for (MultipleResult multipleResult : winResult.getMultipleResults()) {
			System.out.println(multipleResult.getCode() + "-" + multipleResult.getOddsIndex() + "-" + multipleResult.getNums());
		}

		// // 性能测试
		// String betNum = "3,4,5,6,7,8,9,10,11,12,13";
		// String openCode = "07,06,02,03,09,08,10,01,04,05";
        //
		// HZMultiplePrizePlayHandler hanlder = new HZMultiplePrizePlayHandler("bjpk10_pk10_hzgyhz", ITicketPlayHandler.OFFSETS_QIANER, 3, 19); // 冠亚和值
		// long start = System.currentTimeMillis();
		// for (int i = 0; i < 100000; i++) {
		// 	WinResult winResult = hanlder.calculateWinNum(1, betNum, openCode);
		// }
		// long spent = System.currentTimeMillis() - start;
		// System.out.println("耗时：" + spent);
	}
}
