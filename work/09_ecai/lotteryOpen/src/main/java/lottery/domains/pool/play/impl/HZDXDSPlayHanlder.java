package lottery.domains.pool.play.impl;

import lottery.domains.pool.play.HConstants;
import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.MultipleResult;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 和值大小单双  玩法处理类
 */
public class HZDXDSPlayHanlder implements ITicketPlayHandler {
	private String playId;
	private int[] offsets;

	String split = " ";
	int[] offsets_da;
	int[] offsets_xiao;

	/**
	 * 和值大小单双玩法，对offsets进行求和，判断是大小还是单双,双小单双数值由调用方提供
	 * 注(单双不用管，类里面会算)
	 * @param playId 玩法ID
	 * @param offsets_da 大数值集合
	 * @param offsets_xiao 小数值集合
	 * @param offsets 定位胆位置
	 */
	public HZDXDSPlayHanlder(String playId, int[] offsets_da, int[] offsets_xiao, int[] offsets) {
		this.playId = playId;
		this.offsets_da = offsets_da;
		this.offsets_xiao = offsets_xiao;
		this.offsets = offsets;
	}

	/**
	 * 和值大小单双玩法，对offsets进行求和，判断是大小还是单双,双小单双数值由调用方提供
	 * 注(单双不用管，类里面会算)
	 * @param playId 玩法ID
	 * @param offsets_da 大数值集合
	 * @param offsets_xiao 小数值集合
	 * @param offsets 定位胆位置
	 * @param split 投注号码是以什么字符分隔的
	 */
	public HZDXDSPlayHanlder(String playId, int[] offsets_da, int[] offsets_xiao, int[] offsets, String split) {
		this.playId = playId;
		this.offsets_da = offsets_da;
		this.offsets_xiao = offsets_xiao;
		this.offsets = offsets;
		this.split = split;
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
	public WinResult calculateWinNum(int userBetsId, String codes, String openNums) {
		WinResult result = new WinResult();
		String[] nums = getBetNums(codes);
		if (nums == null || openNums == null) {
			return result;
		}
		int sum = TicketPlayUtils.getOpenNumSum(openNums, offsets);

		MultipleResult da = new MultipleResult(HConstants.TicketSeriesType.SSC_DA, 0);
		MultipleResult xiao = new MultipleResult(HConstants.TicketSeriesType.SSC_XIAO, 1);
		MultipleResult dan = new MultipleResult(HConstants.TicketSeriesType.SSC_DAN, 2);
		MultipleResult shuang = new MultipleResult(HConstants.TicketSeriesType.SSC_SHUANG, 3);

		for (String num : nums) {
			if (HConstants.TicketSeriesType.SSC_DA.equals(num)) {
				if (Arrays.binarySearch(offsets_da, sum) >= 0) {
					da.increseNum();
				}
			}
			else if (HConstants.TicketSeriesType.SSC_XIAO.equals(num)){
				if (Arrays.binarySearch(offsets_xiao, sum) >= 0) {
					xiao.increseNum();
				}
			}
			else if (HConstants.TicketSeriesType.SSC_DAN.equals(num)) {
				if (sum % 2 == 1) {
					dan.increseNum();
				}
			}
			else if (HConstants.TicketSeriesType.SSC_SHUANG.equals(num)) {
				if (sum % 2 == 0) {
					shuang.increseNum();
				};
			}
		}

		List<MultipleResult> results = new ArrayList<>();
		if (da.getNums() > 0) results.add(da);
		if (xiao.getNums() > 0) results.add(xiao);
		if (dan.getNums() > 0) results.add(dan);
		if (shuang.getNums() > 0) results.add(shuang);

		result.setPlayId(playId);
		result.setMultipleResults(results);

		return result;
	}

	public static void main(String[] args) {
		// 准确性测试
		String betNum = "大";
		String openNum = "10,08,03,01,02,06,07,05,04,09";

		int[] offsets_da = new int[] {12, 13, 14, 15, 16, 17, 18, 19};;
		int[] offsets_xiao = new int[] {3, 4, 5, 6, 7, 8, 9, 10, 11};

		HZDXDSPlayHanlder hanlder = new HZDXDSPlayHanlder("bjpk10_pk10_dxdsgyhz", offsets_da, offsets_xiao, ITicketPlayHandler.OFFSETS_QIANER);
		WinResult winResult = hanlder.calculateWinNum(1, betNum, openNum);
		for (MultipleResult multipleResult : winResult.getMultipleResults()) {
			System.out.println(multipleResult.getCode() + "-" + multipleResult.getOddsIndex() + "-" + multipleResult.getNums());
		}

		// // 性能测试
		// String betNum = "大 小 单 双";
		// String openNum = "07,06,02,03,09,08,10,01,04,05";
        //
		// int[] offsets_da = new int[] {12, 13, 14, 15, 16, 17, 18, 19};;
		// int[] offsets_xiao = new int[] {3, 4, 5, 6, 7, 8, 9, 10, 11};
		// int[] offsets = new int[]{0, 1};
        //
		// HZDXDSPlayHanlder hanlder = new HZDXDSPlayHanlder("bjpk10_pk10_dxdsgyhz", offsets_da, offsets_xiao, offsets);
        //
		// long start = System.currentTimeMillis();
		// for (int i = 0; i < 100000; i++) {
		// 	WinResult winResult = hanlder.calculateWinNum(1, betNum, openNum);
		// }
		// long spent = System.currentTimeMillis() - start;
		// System.out.println("耗时：" + spent);
	}
}
