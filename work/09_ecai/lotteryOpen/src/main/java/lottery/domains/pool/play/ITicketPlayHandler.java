package lottery.domains.pool.play;


/**
 * 玩法处理接口
 */
public interface ITicketPlayHandler {

	/**
	 * 获取投注号码数组
	 * 
	 * @param betNums
	 * @return
	 */
	String[] getBetNums(String betNums);

	/**
	 * 获取开奖号码中用于开奖计算的号码数组
	 * 
	 * @param openNums
	 * @return
	 */
	String[] getOpenNums(String openNums);

	/**
	 * 根据投注号码和开奖号码计算中奖注数，，若返回0，则未中奖
	 * 
	 * @param index
	 * @param openNums
	 * @return
	 */
	WinResult calculateWinNum(int userBetsId, String index, String openNums);
	
	
	
	String[] OFFSETS = new String[] { "万", "千", "百", "十", "个" };

	/** 五星号码位置 */
	int[] OFFSETS_WUXIN = new int[] { 0, 1, 2, 3, 4 };
	/** 后四号码位置 */
	int[] OFFSETS_HOUSI = new int[] { 1, 2, 3, 4 };
	/** 前三号码位置 */
	int[] OFFSETS_QIANSAN = new int[] { 0, 1, 2 };
	/** 中三号码位置 */
	int[] OFFSETS_ZHONGSAN = new int[] { 1, 2, 3 };
	/** 后三号码位置 */
	int[] OFFSETS_HOUSAN = new int[] { 2, 3, 4 };
	/** 前二号码位置 */
	int[] OFFSETS_QIANER = new int[] { 0, 1 };
	/** 二、三号码位置 */
	int[] OFFSETS_ERSAN = new int[] { 1, 2 };
	/** 后二号码位置 */
	int[] OFFSETS_HOUER = new int[] { 3, 4 };
	
	/** 前一号码位置 */
	int[] OFFSETS_QIANYI = new int[] { 0};
	/** 前四号码位置 */
	int[] OFFSETS_QIANSI = new int[] { 0, 1, 2, 3};
	/** 前五号码位置 */	
	int[] OFFSETS_QIANWU = new int[] { 0, 1, 2, 3, 4};
	/** 后五星号码位置 */
	int[] OFFSETS_HOUWU = new int[] { 5, 6, 7, 8, 9 };
	/** PK10定位 号码位置 */
	int[] OFFSETS_PK10DW = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	
	int[] ERXING_ZHHZ = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
	int[] ERXING_ZXHZ = new int[] { 1, 1, 2, 2, 3, 3, 4, 4, 5, 4, 4, 3, 3, 2, 2, 1, 1 };
	int[] ERXING_MULTI = new int[] { 1, 3, 6, 10 };

	int[] SANXING_ZHHZ = new int[] { 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 63, 69, 73, 75, 75, 73, 69, 63, 55, 45, 36,
			28, 21, 15, 10, 6, 3, 1 };
	int[] SANXING_ZXHZ = new int[] { 1, 2, 2, 4, 5, 6, 8, 10, 11, 13, 14, 14, 15, 15, 14, 14, 13, 11, 10, 8, 6, 5, 4, 2,
			2, 1 };
	int[] SANXING_MULTI = new int[] { 1, 4, 10 };
	int[] SIXING_MULTI = new int[] { 1, 5 };
	
	int [] OFFSETS_QIANERSHI = new int[] { 0, 1, 2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};

	String[] RX_PLAY_IDS = new String[] {"cqssc_wxzhixfs"};
	
	/** 11X5开奖号码中间号码位置*/
	int[] OFFSET_11X5ZHONG = new int[]{2};

	/** 时时彩号码 */
	String[] NUMS_SSC = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	/** 11选5号码 */
	String[] NUMS_115 = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11" };
	/** 福彩3D号码 */
	String[] NUMS_FC3D = NUMS_SSC;
	/** P5P3号码 */
	String[] NUMS_P5P3 = NUMS_SSC;
	/** PK10号码 */
	String[] NUMS_PK10 = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10" };
}
