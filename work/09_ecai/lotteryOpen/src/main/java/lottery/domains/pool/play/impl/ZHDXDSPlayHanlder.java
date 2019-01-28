package lottery.domains.pool.play.impl;

import lottery.domains.pool.play.HConstants;
import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * 总和大小单双
 */
public class ZHDXDSPlayHanlder implements ITicketPlayHandler {
	private String playId;
	private int[] offsets;
	private int daGreaterEqualsThan;
	private int xiaoLessEqualsThan;
	private int maxHits;

	/**
	 * 注意该玩法会限制同样的投注号码只能中一次
	 * @param playId 玩法ID
	 * @param offsets 开奖号码位置
	 * @param daGreaterEqualsThan 开奖号码位置的总和大于等于指定数即大
	 * @param xiaoLessEqualsThan 开奖号码位置的总和小于等于指定数即小
	 * @param maxHits 最大中奖注数，预计算好，避免不必要的性能浪费，小于等于0即无限制
	 */
	public ZHDXDSPlayHanlder(String playId, int[] offsets, int daGreaterEqualsThan, int xiaoLessEqualsThan, int maxHits) {
		this.playId = playId;
		this.offsets = offsets;
		this.daGreaterEqualsThan = daGreaterEqualsThan;
		this.xiaoLessEqualsThan = xiaoLessEqualsThan;
		this.maxHits = maxHits;
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
	public WinResult calculateWinNum(int userBetsId, String betCodes, String openCodes) {
		WinResult result = new WinResult();
		String[] betCodesArr = getBetNums(betCodes);
		String[] openCodesArr = getOpenNums(openCodes);
		if (betCodesArr == null || openCodesArr == null || openCodesArr.length != offsets.length) {
			return result;
		}

		// 求开奖号码位置的总和
		int openCodesSum = 0;
		for (String openCode : openCodesArr) {
			openCodesSum += Integer.valueOf(openCode);
		}

		HashSet<String> hits = new HashSet<>();
		for (String betCode : betCodesArr) {
			if (maxHits > 0 && hits.size() >= maxHits) {
				break;
			}
			switch (betCode) {
				case HConstants.TicketSeriesType.SSC_ZH_DA:
					// 总和大于等于指定数即大
					if (openCodesSum >= daGreaterEqualsThan) {
						hits.add(betCode);
					}
					break;
				case HConstants.TicketSeriesType.SSC_ZH_XIAO:
					// 总和小于等于指定数即小
					if (openCodesSum <= xiaoLessEqualsThan) {
						hits.add(betCode);
					}
					break;
				case HConstants.TicketSeriesType.SSC_ZH_DAN:
					// 总和除2余1即单
					if (openCodesSum % 2 == 1) {
						hits.add(betCode);
					}
					break;
				case HConstants.TicketSeriesType.SSC_ZH_SHUANG:
					// 总和除2余0即双
					if (openCodesSum % 2 == 0) {
						hits.add(betCode);
					}
					break;
				default:
					break;
			}
		}

		result.setPlayId(playId);
		result.setWinNum(hits.size());
		return result;
	}

	public static void main(String[] args) {
		String betCodes = "总和大,总和小,总和单,总和双";
		String openCodes = "8,8,8,3,4";

		ZHDXDSPlayHanlder hanlder = new ZHDXDSPlayHanlder("wxdxds", ITicketPlayHandler.OFFSETS_WUXIN, 23, 22, 2);
		WinResult winResult = hanlder.calculateWinNum(1, betCodes, openCodes);
		System.out.println(winResult + "---");
		System.out.println(winResult.getWinNum());
	}
}
