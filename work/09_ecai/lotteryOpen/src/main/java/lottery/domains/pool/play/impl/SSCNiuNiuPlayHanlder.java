package lottery.domains.pool.play.impl;


import javautils.StringUtil;
import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.MultipleResult;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.*;

/**
 * 时时彩牛牛玩法处理类
 */
public class SSCNiuNiuPlayHanlder implements ITicketPlayHandler {
	private String playId;
	/**开奖号码位置 */
	private int [] offsets;
	private String codeSeparator;

	private static final String[] ODDS_INDEXES = new String[]{"牛大", "牛小", "牛单", "牛双", "无牛", "牛牛", "牛1", "牛2", "牛3", "牛4", "牛5", "牛6", "牛7", "牛8", "牛9", "五条", "炸弹", "葫芦", "顺子", "三条", "两对", "单对", "散号"};
	private static final String NIU_DA = "牛大";
	private static final String NIU_XIAO = "牛小";
	private static final String NIU_DAN = "牛单";
	private static final String NIU_SHUANG = "牛双";
	private static final String WU_NIU = "无牛";
	private static final String NIU_NIU = "牛牛";
	private static final String NIU_1 = "牛1";
	private static final String NIU_2 = "牛2";
	private static final String NIU_3 = "牛3";
	private static final String NIU_4 = "牛4";
	private static final String NIU_5 = "牛5";
	private static final String NIU_6 = "牛6";
	private static final String NIU_7 = "牛7";
	private static final String NIU_8 = "牛8";
	private static final String NIU_9 = "牛9";
	private static final String WU_TIAO = "五条";
	private static final String ZHA_DAN = "炸弹";
	private static final String HU_LU = "葫芦";
	private static final String SHUN_ZI = "顺子";
	private static final String SAN_TIAO = "三条";
	private static final String LIANG_DUI = "两对";
	private static final String DAN_DUI = "单对";
	private static final String SAN_HAO = "散号";

	public SSCNiuNiuPlayHanlder(String playId, int offsets[], String codeSeparator) {
		this.playId = playId;
        if (offsets.length != 5) {
            throw new RuntimeException("牛牛玩法必须只有5位开奖号码");
        }
        this.offsets = offsets;
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

	private Integer[] countNums(String[] openNumsArr) {
		Map<String, Integer> numsRepatMap = StringUtil.countChars(openNumsArr);

		Collection<Integer> values = numsRepatMap.values();
		Integer[] counts = values.toArray(new Integer[]{});
		return counts;
	}

	private int[] sortNums(String[] openNumsArr) {
		int[] openNumInt = new int[openNumsArr.length];
		for (int i = 0; i < openNumsArr.length; i++) {
			openNumInt[i] = Integer.valueOf(openNumsArr[i]);
		}

		Arrays.sort(openNumInt);
		return openNumInt;
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String betCodes, String openNums) {
		WinResult result = new WinResult();
		String[] betNums = getBetNums(betCodes); // 购彩号码
		String[] openNumsArr = getOpenNums(openNums); // 购彩号码
		if (betNums == null || betNums == null) {
			return result;
		}
		if (openNumsArr == null || openNumsArr == null) {
			return result;
		}

		Integer[] numCounts = countNums(openNumsArr);
		int[] sortedNums = sortNums(openNumsArr);

		// 牛几 -1：无牛，其它：返回几就是牛几
		int niuNum = calculateNiuNum(sortedNums);
		// 是否是五条
		boolean isWuTiao = isWuTiao(sortedNums);
		// 是否是炸弹
		boolean isSiTiao = isSiTiao(numCounts);
		// 是否是葫芦
		boolean isHulu = isHulu(numCounts);
		// 是否是顺子
		boolean isShunZi = isShunZi(sortedNums);
		// 是否是三条
		boolean isSanTiao = isSanTiao(numCounts);
		// 是否是两对
		boolean isLiangDui = isLiangDui(numCounts);
		// 是否是单对
		boolean isDanDui = isDanDui(numCounts);
		// 是否是散号
		boolean isSanHao = isSanHao(sortedNums);


		Map<String, MultipleResult> codeResults = new HashMap<>();
		int winNum = 0;
		for (String betNum : betNums) {

			boolean hit = false; // 是否中奖
			int prizeIndex = -1;

			switch (betNum) {
				case NIU_DA : hit = niuNum >= 6 || niuNum == 0; prizeIndex = 0; break; // 牛6、牛7、牛8、牛9、牛牛
				case NIU_XIAO : hit = niuNum >= 1 && niuNum <= 5; prizeIndex = 1; break; // 牛1、牛2、牛3、牛4、牛5
				case NIU_DAN : hit = niuNum > 0 && niuNum % 2 == 1; prizeIndex = 2; break; // 牛1、牛3、牛5、牛7、牛9
				case NIU_SHUANG : hit = niuNum > -1 && niuNum % 2 == 0; prizeIndex = 3; break; // 牛2、牛4、牛6、牛8、牛牛
				case WU_NIU : hit = niuNum == -1; prizeIndex = 4; break; // 无牛
				case NIU_NIU : hit = niuNum == 0; prizeIndex = 5; break; // 牛牛
				case NIU_1 : hit = niuNum == 1; prizeIndex = 6; break; // 牛1
				case NIU_2 : hit = niuNum == 2; prizeIndex = 7; break; // 牛2
				case NIU_3 : hit = niuNum == 3; prizeIndex = 8; break; // 牛3
				case NIU_4 : hit = niuNum == 4; prizeIndex = 9; break; // 牛4
				case NIU_5 : hit = niuNum == 5; prizeIndex = 10; break; // 牛5
				case NIU_6 : hit = niuNum == 6; prizeIndex = 11; break; // 牛6
				case NIU_7 : hit = niuNum == 7; prizeIndex = 12; break; // 牛7
				case NIU_8 : hit = niuNum == 8; prizeIndex = 13; break; // 牛8
				case NIU_9 : hit = niuNum == 9; prizeIndex = 14; break; // 牛9
				case WU_TIAO : hit = isWuTiao; prizeIndex = 15; break; // 五条
				case ZHA_DAN : hit = isSiTiao; prizeIndex = 16; break; // 炸弹
				case HU_LU : hit = isHulu; prizeIndex = 17; break; // 葫芦
				case SHUN_ZI : hit = isShunZi; prizeIndex = 18; break; // 顺子
				case SAN_TIAO : hit = isSanTiao; prizeIndex = 19; break; // 三条
				case LIANG_DUI : hit = isLiangDui; prizeIndex = 20; break; // 两对
				case DAN_DUI : hit = isDanDui; prizeIndex = 21; break; // 单对
				case SAN_HAO : hit = isSanHao; prizeIndex = 22; break; // 散号
				default: hit = false; break;
			}

			if (hit) {
				winNum = 1;
				MultipleResult codeResult;
				if (codeResults.containsKey(betNum)) {
					codeResult = codeResults.get(betNum);
				}
				else {
					codeResult = new MultipleResult(betNum, prizeIndex);
					codeResults.put(betNum, codeResult);
				}
				codeResult.increseNum();
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		result.setMultipleResults(new ArrayList<>(codeResults.values()));
		return result;
	}



	/**
	 * -1：无牛；其它：返回几就是牛几
	 * @return
	 */
	private int calculateNiuNum(int[] nums) {

        int niuNum = -1; // 默认无牛
        for (int i = 0; i < nums.length; i++) {
            for (int j = i+1; j < nums.length; j++) {
                for (int k = j+1; k < nums.length; k++) {

                    int num1 = nums[i];
                    int num2 = nums[j];
                    int num3 = nums[k];

                    int niuSum = num1 + num2 + num3;

                    if (niuSum % 10 == 0) {
						// 有牛
						int num4Index = -1;
						int num5Index = -1;

						if (i != 0 && j != 0 && k != 0) num4Index = 0;
						if (i != 1 && j != 1 && k != 1) num4Index = 1;
						if (i != 2 && j != 2 && k != 2) num4Index = 2;
						if (i != 3 && j != 3 && k != 3) num4Index = 3;
						if (i != 4 && j != 4 && k != 4) num4Index = 4;

						if (i != 0 && j != 0 && k != 0 && num4Index != 0) num5Index = 0;
						if (i != 1 && j != 1 && k != 1 && num4Index != 1) num5Index = 1;
						if (i != 2 && j != 2 && k != 2 && num4Index != 2) num5Index = 2;
						if (i != 3 && j != 3 && k != 3 && num4Index != 3) num5Index = 3;
						if (i != 4 && j != 4 && k != 4 && num4Index != 4) num5Index = 4;

						int num4 = nums[num4Index];
						int num5 = nums[num5Index];
						int remainSum = num4 + num5;
						niuNum = remainSum % 10;

						// 结束所有循环
						j = nums.length;
						i = nums.length;
						break;
                    }
                }
            }
        }

        return niuNum;
    }

	/**
	 * 是否是五条
	 */
	private boolean isWuTiao(int[] nums) {
		int num1 = nums[0];
		int num2 = nums[1];
		int num3 = nums[2];
		int num4 = nums[3];
		int num5 = nums[4];

		return num1 == num2
				&& num2 == num3
				&& num3 == num4
				&& num4 == num5;
	}

	/**
	 * 是否是四条
	 */
	private boolean isSiTiao(Integer[] numCounts) {
		if (numCounts == null || numCounts.length != 2) {
			return false;
		}

		int repeatNum1 = numCounts[0];
		int repeatNum2 = numCounts[1];

		if (repeatNum1 == 4 && repeatNum2 == 1) {
			return true;
		}

		return false;
	}

	/**
	 * 是否是葫芦
	 */
	private boolean isHulu(Integer[] numCounts) {
		if (numCounts == null || numCounts.length != 2) {
			return false;
		}

		int repeatNum1 = numCounts[0];
		int repeatNum2 = numCounts[1];

		if (repeatNum1 == 3 && repeatNum2 == 2) {
			return true;
		}

		return false;
	}


	/**
	 * 是否是顺子
	 */
	private boolean isShunZi(int[] nums) {
		// 01234/ 12345/ 23456/ 34567/ 45678/ 56789/ 06789/ 01789/ 01289/01239；

		int num1 = nums[0];
		int num2 = nums[1];
		int num3 = nums[2];
		int num4 = nums[3];
		int num5 = nums[4];

		if (num1 == 0 && num2 == 1 && num3 == 2 && num4 == 3 && num5 == 4) {
			return true;
		}
		if (num1 == 1 && num2 == 2 && num3 == 3 && num4 == 4 && num5 == 5) {
			return true;
		}
		if (num1 == 2 && num2 == 3 && num3 == 4 && num4 == 5 && num5 == 6) {
			return true;
		}
		if (num1 == 3 && num2 == 4 && num3 == 5 && num4 == 6 && num5 == 7) {
			return true;
		}
		if (num1 == 4 && num2 == 5 && num3 == 6 && num4 == 7 && num5 == 8) {
			return true;
		}
		if (num1 == 5 && num2 == 6 && num3 == 7 && num4 == 8 && num5 == 9) {
			return true;
		}
		if (num1 == 0 && num2 == 6 && num3 == 7 && num4 == 8 && num5 == 9) {
			return true;
		}
		if (num1 == 0 && num2 == 1 && num3 == 7 && num4 == 8 && num5 == 9) {
			return true;
		}
		if (num1 == 0 && num2 == 1 && num3 == 2 && num4 == 8 && num5 == 9) {
			return true;
		}
		if (num1 == 0 && num2 == 1 && num3 == 2 && num4 == 3 && num5 == 9) {
			return true;
		}

		return false;
	}

	/**
	 * 是否是三条
	 */
	private boolean isSanTiao(Integer[] numCounts) {
		if (numCounts == null || numCounts.length != 3) {
			return false;
		}

		int repeatNum1 = numCounts[0];
		int repeatNum2 = numCounts[1];
		int repeatNum3 = numCounts[2];

		if (repeatNum1 == 3 && repeatNum2 == 1 && repeatNum3 == 1) {
			return true;
		}

		return false;
	}

	/**
	 * 是否是两对，如果是葫芦，也不能算作两对
	 */
	private boolean isLiangDui(Integer[] numCounts) {
		if (numCounts == null || numCounts.length != 3) {
			return false;
		}

		int repeatNum1 = numCounts[0];
		int repeatNum2 = numCounts[1];
		int repeatNum3 = numCounts[2];

		if (repeatNum1 == 2 && repeatNum2 == 2 && repeatNum3 == 1) {
			return true;
		}

		return false;
	}

	/**
	 * 是否是单对
	 */
	private boolean isDanDui(Integer[] numCounts) {
		if (numCounts == null || numCounts.length != 4) {
			return false;
		}

		int repeatNum1 = numCounts[0];
		int repeatNum2 = numCounts[1];
		int repeatNum3 = numCounts[2];
		int repeatNum4 = numCounts[3];

		if (repeatNum1 == 2 && repeatNum2 == 1 && repeatNum3 == 1 && repeatNum4 == 1) {
			return true;
		}

		return false;
	}

	/**
	 * 是否是散号，除顺子以外的无对号，则是散号
	 */
	private boolean isSanHao(int[] nums) {
		HashSet<Integer> repeatNums = new HashSet<>();
		for (int num : nums) {
			if (repeatNums.contains(num)) {
				return false;
			}

			repeatNums.add(num);
		}

		if (repeatNums.size() == 5) {
			if (isShunZi(nums)) {
				return false;
			}

			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		String codes = "无牛";
		String openNum = "3,7,2,3,1";

		SSCNiuNiuPlayHanlder sscNiuNiuPlayHanlder = new SSCNiuNiuPlayHanlder("sscniuniu", ITicketPlayHandler.OFFSETS_WUXIN, ",");
		WinResult winResult = sscNiuNiuPlayHanlder.calculateWinNum(1, codes, openNum);
		System.out.println(winResult.getWinNum());
		for (MultipleResult multipleResult : winResult.getMultipleResults()) {
			System.out.println(multipleResult.getCode() + "-" + multipleResult.getOddsIndex() + "-" + multipleResult.getNums());
		}
	}
}
