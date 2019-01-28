package lottery.domains.pool.play;

import lottery.domains.pool.play.impl.*;

import java.util.HashMap;
import java.util.Map;


/**
 * 所有玩法集合
 * 在这里新增玩法、修改玩法等
 * 必须保证playHandlers的key是唯一的
 */
public class TicketPlayHandlerContext {

	private static Map<String, ITicketPlayHandler> playHandlers = new HashMap<>();

	static {
		sscHandlers(); // 时时彩 type：1
		x5Handlers(); // 11选5 type：2
		k3Handlers(); // 快3 type：3
		d3Handlers(); // 3D type：4
		kl8Handlers(); // 快乐8 type：5
		pk10Handlers(); // Pk10 type：6
		lhdHandlers(); // 龙虎斗 type：7
	}

	/**
	 * 获取玩法对应的计算类
	 * @param typeId 彩票类型ID
	 * @param ruleCode 玩法编码
	 */
	public static ITicketPlayHandler getHandler(int typeId, String ruleCode) {
		String type = typeId + "_" + ruleCode;
		return getHandler(type);
	}

	/**
	 * 获取玩法对应的计算类，type应该传入(类型ID_玩法code)，如(1_wxzhixfs则为时时彩五星直选复式)
	 */
	private static ITicketPlayHandler getHandler(String type) {
		return playHandlers.get(type);
	}

	/**
	 * 时时彩所有玩法集合
	 */
	private static Map<String, ITicketPlayHandler> sscHandlers() {
		Map<String, ITicketPlayHandler> map = new HashMap<>();
		// 五星  Start 
		playHandlers.put("1_wxzhixfs",
				new ZHXFSPlayHandler("1_wxzhixfs",ITicketPlayHandler.OFFSETS_WUXIN)); // 五星直选复式*
		playHandlers.put("1_wxzhixds",
				new ZHXDSPlayHandler("1_wxzhixds",ITicketPlayHandler.OFFSETS_WUXIN)); // 五星直选单式*
		playHandlers.put("1_wxzux120",
				new BDWPlayHandler("1_wxzux120",5,ITicketPlayHandler.OFFSETS_WUXIN));// 五星组选120*
		playHandlers.put("1_wxzux60",
				new ZXPlayHanlder("1_wxzux60",1, 2, 3, 1, ITicketPlayHandler.OFFSETS_WUXIN));// 五星组选60 *
		playHandlers.put("1_wxzux30",
				new ZXPlayHanlder("1_wxzux30",2, 2, 1, 1, ITicketPlayHandler.OFFSETS_WUXIN));// 五星组选30 *
		playHandlers.put("1_wxzux20",
				new ZXPlayHanlder("1_wxzux20",1, 3, 2, 1, ITicketPlayHandler.OFFSETS_WUXIN));// 五星组选20 *
		playHandlers.put("1_wxzux10",
				new ZXPlayHanlder("1_wxzux10",1, 3, 1, 2, ITicketPlayHandler.OFFSETS_WUXIN));// 五星组选10 *
		playHandlers.put("1_wxzux5",
				new ZXPlayHanlder("1_wxzux5",1, 4, 1, 1, ITicketPlayHandler.OFFSETS_WUXIN));// 五星组选5 *
		playHandlers.put("1_wxzhixzh",
				new ZXZHPlasyHandler("1_wxzhixzh",ITicketPlayHandler.OFFSETS_WUXIN));//五星组合 *
		playHandlers.put("1_wxdxds",
				new ZHDXDSPlayHanlder("1_wxdxds",ITicketPlayHandler.OFFSETS_WUXIN, 23, 22, 2)); // 五星总和大小单双
		// 五星 END  
		
		// 四星 Start
		playHandlers.put("1_sixzhixfsh",
				new ZHXFSPlayHandler("1_sixzhixfsh",ITicketPlayHandler.OFFSETS_HOUSI)); // 四星直选复式(后) *
		playHandlers.put("1_sixzhixdsh",
				new ZHXDSPlayHandler("1_sixzhixdsh",ITicketPlayHandler.OFFSETS_HOUSI)); // 四星直选单式(后) *
		playHandlers.put("1_sixzux24h",
				new BDWPlayHandler("1_sixzux24h",4, ITicketPlayHandler.OFFSETS_HOUSI)); // 四星组选24(后) *
		playHandlers.put("1_sixzux12h",
				new ZXPlayHanlder("1_sixzux12h",1, 2, 2, 1, ITicketPlayHandler.OFFSETS_HOUSI)); // 四星组选12(后) *
		playHandlers.put("1_sixzux6h",
				new ZX6PlayHanlder("1_sixzux6h",ITicketPlayHandler.OFFSETS_HOUSI)); // 四星组选6(后) *
		playHandlers.put("1_sixzux4h",
				new ZXPlayHanlder("1_sixzux4h",1, 3, 1, 1, ITicketPlayHandler.OFFSETS_HOUSI)); // 四星组选4(后) *
		playHandlers.put("1_sixzhixzhh",
				new ZXZHPlasyHandler("1_sixzhixzhh",ITicketPlayHandler.OFFSETS_HOUSI));//四星组合(后) *
		
		playHandlers.put("1_sixzhixfsq",
				new ZHXFSPlayHandler("1_sixzhixfsq",ITicketPlayHandler.OFFSETS_QIANSI)); // 四星直选复式(前) *
		playHandlers.put("1_sixzhixdsq",
				new ZHXDSPlayHandler("1_sixzhixdsq",ITicketPlayHandler.OFFSETS_QIANSI)); // 四星直选单式(前) *
		playHandlers.put("1_sixzux24q",
				new BDWPlayHandler("1_sixzux24q",4, ITicketPlayHandler.OFFSETS_QIANSI)); // 四星组选24(前) *
		playHandlers.put("1_sixzux12q",
				new ZXPlayHanlder("1_sixzux12q",1, 2, 2, 1, ITicketPlayHandler.OFFSETS_QIANSI)); // 四星组选12(前) *
		playHandlers.put("1_sixzux6q",
				new ZX6PlayHanlder("1_sixzux6q",ITicketPlayHandler.OFFSETS_QIANSI)); // 四星组选6(前) *
		playHandlers.put("1_sixzux4q",
				new ZXPlayHanlder("1_sixzux4q",1, 3, 1, 1, ITicketPlayHandler.OFFSETS_QIANSI)); // 四星组选4(前) *
		playHandlers.put("1_sixzhixzhq",
				new ZXZHPlasyHandler("1_sixzhixzhq",ITicketPlayHandler.OFFSETS_QIANSI));//四星组合(前) *
		//四星END
		
		//三星Start
		playHandlers.put("1_sxzhixfsq",
				new ZHXFSPlayHandler("1_sxzhixfsq",ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三直选复式 *
		playHandlers.put("1_sxzhixdsq",
				new ZHXDSPlayHandler("1_sxzhixdsq",ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三直选单式 *
		playHandlers.put("1_sxzhixhzq",
				new ZHHZPlayHandler("1_sxzhixhzq",ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三直选和值 *
		playHandlers.put("1_sxzuxzsq",
				new ZXZ3PlayHanlder("1_sxzuxzsq",ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三组三 *
		playHandlers.put("1_sxzuxzlq",
				new BDWPlayHandler("1_sxzuxzlq",3, ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三组六 *
		playHandlers.put("1_sxhhzxq",
				new ZXDSPlayHandler("1_sxhhzxq","1_sxzuxzsq","1_sxzuxzlq",
						ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三混合组选 *
		playHandlers.put("1_sxzuxhzq",
				new ZXHZPlayHanlder("1_sxzuxzsq","1_sxzuxzlq",ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三组选和值
		
		playHandlers.put("1_sxzhixfsz",
				new ZHXFSPlayHandler("1_sxzhixfsz",ITicketPlayHandler.OFFSETS_ZHONGSAN)); // 中三直选复式 *
		playHandlers.put("1_sxzhixdsz",
				new ZHXDSPlayHandler("1_sxzhixdsz",ITicketPlayHandler.OFFSETS_ZHONGSAN)); // 中三直选单式 *
		playHandlers.put("1_sxzhixhzz",
				new ZHHZPlayHandler("1_sxzhixhzz",ITicketPlayHandler.OFFSETS_ZHONGSAN)); // 中三直选和值 *
		playHandlers.put("1_sxzuxzsz",
				new ZXZ3PlayHanlder("1_sxzuxzsz",ITicketPlayHandler.OFFSETS_ZHONGSAN)); // 中三组三 *
		playHandlers.put("1_sxzuxzlz",
				new BDWPlayHandler("1_sxzuxzlz",3, ITicketPlayHandler.OFFSETS_ZHONGSAN)); // 中三组六 *
		playHandlers.put("1_sxhhzxz",
				new ZXDSPlayHandler("1_sxhhzxz","1_sxzuxzsz","1_sxzuxzlz",
						ITicketPlayHandler.OFFSETS_ZHONGSAN)); // 中三混合组选 *
		playHandlers.put("1_sxzuxhzz",
				new ZXHZPlayHanlder("1_sxzuxzsz","1_sxzuxzlz",ITicketPlayHandler.OFFSETS_ZHONGSAN)); // 中三组选和值

		playHandlers.put("1_sxzhixfsh",
				new ZHXFSPlayHandler("1_sxzhixfsh",ITicketPlayHandler.OFFSETS_HOUSAN)); // 后三直选复式 *
		playHandlers.put("1_sxzhixdsh",
				new ZHXDSPlayHandler("1_sxzhixdsh",ITicketPlayHandler.OFFSETS_HOUSAN)); // 后三直选单式 *
		playHandlers.put("1_sxzhixhzh",
				new ZHHZPlayHandler("1_sxzhixhzh",ITicketPlayHandler.OFFSETS_HOUSAN)); // 后三直选和值 *
		playHandlers.put("1_sxzuxzsh",
				new ZXZ3PlayHanlder("1_sxzuxzsh",ITicketPlayHandler.OFFSETS_HOUSAN)); // 后三组三 *
		playHandlers.put("1_sxzuxzlh",
				new BDWPlayHandler("1_sxzuxzlh",3, ITicketPlayHandler.OFFSETS_HOUSAN)); // 后三组六 *
		playHandlers.put("1_sxhhzxh",
				new ZXDSPlayHandler("1_sxhhzxh","1_sxzuxzsh","1_sxzuxzlh",
						ITicketPlayHandler.OFFSETS_HOUSAN)); // 后三混合组选 *
		playHandlers.put("1_sxzuxhzh",
				new ZXHZPlayHanlder("1_sxzuxzsh","1_sxzuxzlh",ITicketPlayHandler.OFFSETS_HOUSAN)); // 中三组选和值
		//三星END
		
		//二星Start
		playHandlers.put("1_exzhixfsq",
				new ZHXFSPlayHandler("1_exzhixfsq",ITicketPlayHandler.OFFSETS_QIANER)); // 前二直选复式 *
		playHandlers.put("1_exzhixfsqkill",
				new ZHXFSKillPlayHandler("1_exzhixfsqkill",ITicketPlayHandler.OFFSETS_QIANER)); // 前二直选复式 杀对子*
		playHandlers.put("1_exzhixdsq",
				new ZHXDSPlayHandler("1_exzhixdsq",ITicketPlayHandler.OFFSETS_QIANER)); // 前二直选单式 *
		playHandlers.put("1_exzhixhzq",
				new ZHHZPlayHandler("1_exzhixhzq",ITicketPlayHandler.OFFSETS_QIANER)); // 前二直选和值 *
		playHandlers.put("1_dxdsq",
				new ZHXDXDSPlayHanlder("1_dxdsq",ITicketPlayHandler.OFFSETS_QIANER)); // 前二 大小单双 *
		playHandlers.put("1_exzuxfsq",
				new BDWPlayHandler("1_exzuxfsq",2, ITicketPlayHandler.OFFSETS_QIANER)); // 前二组选复式 *
		playHandlers.put("1_exzuxdsq",
				new ZXDSPlayHandler("1_exzuxdsq","1_exzuxdsq","1_exzuxdsq",
						ITicketPlayHandler.OFFSETS_QIANER)); // 前二组选单式 *
		playHandlers.put("1_exzuxhzq",
				new ZXHZPlayHanlder("1_exzuxhzq","1_exzuxhzq",ITicketPlayHandler.OFFSETS_QIANER)); // 前二组选和值
		
		playHandlers.put("1_exzhixfsh",
				new ZHXFSPlayHandler("1_exzhixfsh",ITicketPlayHandler.OFFSETS_HOUER)); // 后二直选复式 *
		playHandlers.put("1_exzhixdsh",
				new ZHXDSPlayHandler("1_exzhixdsh",ITicketPlayHandler.OFFSETS_HOUER)); // 后二直选单式 *
		playHandlers.put("1_exzhixfshkill",
				new ZHXFSKillPlayHandler("1_exzhixfshkill",ITicketPlayHandler.OFFSETS_HOUER)); // 后二直选复式 杀对子*
		playHandlers.put("1_exzhixhzh",
				new ZHHZPlayHandler("1_exzhixhzh",ITicketPlayHandler.OFFSETS_HOUER)); // 后二直选和值 *
		playHandlers.put("1_dxdsh",
				new ZHXDXDSPlayHanlder("1_dxdsh",ITicketPlayHandler.OFFSETS_HOUER)); // 后二 大小单双 *
		playHandlers.put("1_exzuxfsh",
				new BDWPlayHandler("1_exzuxfsh",2, ITicketPlayHandler.OFFSETS_HOUER)); // 后二组选复式 *
		playHandlers.put("1_exzuxdsh",
				new ZXDSPlayHandler("1_exzuxdsh","1_exzuxdsh","1_exzuxdsh",
						ITicketPlayHandler.OFFSETS_HOUER)); // 后二组选单式 *
		playHandlers.put("1_exzuxhzh",
				new ZXHZPlayHanlder("1_exzuxhzh","1_exzuxhzh",ITicketPlayHandler.OFFSETS_HOUER)); // 前二组选和值
		//二星END
		
		//定位胆 Start
		playHandlers.put("1_dw",
				new DWDPlayHandler("1_dw",ITicketPlayHandler.OFFSETS_WUXIN)); //定位胆 *
		//定位胆 END
		
		//不定位 Start
		playHandlers.put("1_bdw1mq",
				new BDWPlayHandler("1_bdw1mq",1, ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三一码不定位 *
		playHandlers.put("1_bdw1mz",
				new BDWPlayHandler("1_bdw1mz",1, ITicketPlayHandler.OFFSETS_ZHONGSAN)); // 中三一码不定位 *
		playHandlers.put("1_bdw1mh",
				new BDWPlayHandler("1_bdw1mh",1, ITicketPlayHandler.OFFSETS_HOUSAN)); // 后三一码不定位 *
		
		playHandlers.put("1_bdw2mq",
				new BDWPlayHandler("1_bdw2mq",2, ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三二码不定位 *
		playHandlers.put("1_bdw2mz",
				new BDWPlayHandler("1_bdw2mz",2, ITicketPlayHandler.OFFSETS_ZHONGSAN)); // 中三二码不定位 *
		playHandlers.put("1_bdw2mh",
				new BDWPlayHandler("1_bdw2mh",2, ITicketPlayHandler.OFFSETS_HOUSAN)); // 后三二码不定位 *

		playHandlers.put("1_bdwsix1mq",
				new BDWPlayHandler("1_bdwsix1mq",1, ITicketPlayHandler.OFFSETS_QIANSI)); // 前四一码不定位 *
		playHandlers.put("1_bdwsix2mq",
				new BDWPlayHandler("1_bdwsix2mq",2, ITicketPlayHandler.OFFSETS_QIANSI)); // 前四二码不定位 *

		playHandlers.put("1_bdwsix1mh",
				new BDWPlayHandler("1_bdwsix1mh",1, ITicketPlayHandler.OFFSETS_HOUSI)); // 后四一码不定位 *
		playHandlers.put("1_bdwsix2mh",
				new BDWPlayHandler("1_bdwsix2mh",2, ITicketPlayHandler.OFFSETS_HOUSI)); // 后四二码不定位 *

		playHandlers.put("1_bdwwx2m",
				new BDWPlayHandler("1_bdwwx2m",2, ITicketPlayHandler.OFFSETS_WUXIN)); // 五星二码不定位 *
		playHandlers.put("1_bdwwx3m",
				new BDWPlayHandler("1_bdwwx3m",3, ITicketPlayHandler.OFFSETS_WUXIN)); // 五星三码不定位 *

		//不定位 END
		
		//任选Start
		playHandlers.put("1_rx2fs", new RXZHFSPlayHandler("1_rx2fs",2)); // 任二直选复式 *
		playHandlers.put("1_rx3fs", new RXZHFSPlayHandler("1_rx3fs",3)); // 任三直选复式 *
		playHandlers.put("1_rx4fs", new RXZHFSPlayHandler("1_rx4fs",4)); // 任四直选复式 *
		
		playHandlers.put("1_rx2ds", new SSCRXZHDSPlayHandler("1_rx2ds",2)); // 任二直选单式 *
		playHandlers.put("1_rx3ds", new SSCRXZHDSPlayHandler("1_rx3ds",3)); // 任三直选单式 *
		playHandlers.put("1_rx4ds", new SSCRXZHDSPlayHandler("1_rx4ds",4)); // 任四直选单式 *

		playHandlers.put("1_rx2zx", new SSCRXZXFSPlayHandler("1_rx2zx",2)); // 任二组选复式 *
		playHandlers.put("1_rx3z6", new SSCRXZXZ6PlayHanlder("1_rx3z6")); // 任三组六复式 *
		playHandlers.put("1_rx3z3", new SSCRXZXZ3PlayHanlder("1_rx3z3")); // 任三组三复式 *
		//任选 END
		
		//趣味Start
		playHandlers.put("1_qwyffs", new SSCQWPlayHandler("1_qwyffs",
				ITicketPlayHandler.OFFSETS_WUXIN,1)); // 一帆风顺 *
		playHandlers.put("1_qwhscs", new SSCQWPlayHandler("1_qwhscs",
				ITicketPlayHandler.OFFSETS_WUXIN,2)); // 好事成双 *
		playHandlers.put("1_qwsxbx", new SSCQWPlayHandler("1_qwsxbx",
				ITicketPlayHandler.OFFSETS_WUXIN,3)); // 三星报喜 *
		playHandlers.put("1_qwsjfc", new SSCQWPlayHandler("1_qwsjfc",
				ITicketPlayHandler.OFFSETS_WUXIN,4)); // 四季发财 *
		//趣味END

		// 龙虎
		playHandlers.put("1_longhuhewq",
				new SSCLHPlayHanlder("1_longhuhewq", 0, 1)); // 龙虎万千
		playHandlers.put("1_longhuhewb",
				new SSCLHPlayHanlder("1_longhuhewb", 0, 2)); // 龙虎万百
		playHandlers.put("1_longhuhews",
				new SSCLHPlayHanlder("1_longhuhews", 0, 3)); // 龙虎万十
		playHandlers.put("1_longhuhewg",
				new SSCLHPlayHanlder("1_longhuhewg", 0, 4)); // 龙虎万个
		playHandlers.put("1_longhuheqb",
				new SSCLHPlayHanlder("1_longhuheqb", 1, 2)); // 龙虎千百
		playHandlers.put("1_longhuheqs",
				new SSCLHPlayHanlder("1_longhuheqs", 1, 3)); // 龙虎千十
		playHandlers.put("1_longhuheqg",
				new SSCLHPlayHanlder("1_longhuheqg", 1, 4)); // 龙虎千个
		playHandlers.put("1_longhuhebs",
				new SSCLHPlayHanlder("1_longhuhebs", 2, 3)); // 龙虎百十
		playHandlers.put("1_longhuhebg",
				new SSCLHPlayHanlder("1_longhuhebg", 2, 4)); // 龙虎百个
		playHandlers.put("1_longhuhesg",
				new SSCLHPlayHanlder("1_longhuhesg", 3, 4)); // 龙虎十个
		// 龙虎

		// 牛牛
		playHandlers.put("1_sscniuniu",
				new SSCNiuNiuPlayHanlder("1_sscniuniu", ITicketPlayHandler.OFFSETS_WUXIN, ",")); // 牛牛
		// 牛牛
		
		return map;
	}
	
	/**
	 * 11选5所有玩法集合
	 */
	private static Map<String, ITicketPlayHandler> x5Handlers() {
		Map<String, ITicketPlayHandler> map = new HashMap<>();
		//三星Start
		playHandlers.put("2_sanmzhixfsq",
				new ZHXFS115PlayHandler("2_sanmzhixfsq", ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三直选复式 *
		playHandlers.put("2_sanmzhixdsq",
				new ZHXDS115PlayHandler("2_sanmzhixdsq", ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三直选单式 *
		playHandlers.put("2_sanmzuxfsq",
				new BDWPlayHandler("2_sanmzuxfsq", 3, ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三组选复式 *
		playHandlers.put("2_sanmzuxdsq",
				new ZXDS115PlayHandler("2_sanmzuxdsq",ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三组选单式 *
		//三星END
		
		//二星Start
		playHandlers.put("2_ermzhixfsq",
				new ZHXFS115PlayHandler("2_ermzhixfsq", ITicketPlayHandler.OFFSETS_QIANER)); // 前二直选复式 *
		playHandlers.put("2_ermzhixdsq",
				new ZHXDS115PlayHandler("2_ermzhixdsq", ITicketPlayHandler.OFFSETS_QIANER)); // 前二直选单式 *
		playHandlers.put("2_ermzuxfsq",
				new BDWPlayHandler("2_ermzuxfsq", 2, ITicketPlayHandler.OFFSETS_QIANER)); // 前二组选复式 *
		playHandlers.put("2_ermzuxdsq",
				new ZXDS115PlayHandler("2_ermzuxdsq",ITicketPlayHandler.OFFSETS_QIANER)); // 前二组选单式 *
		//二星END
		
		//不定位Start
		playHandlers.put("2_bdw",
				new BDWPlayHandler("2_bdw", 1, ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三一码不定位 *
		//不定位END
		
		//定位胆Start
		playHandlers.put("2_dwd",
				new DWDPlayHandler("2_dwd", ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三定位胆 *
		//定位胆END
		
		//趣味Start
		playHandlers.put("2_dds",
				new QW11X5DDSPlayHandler("2_dds", ITicketPlayHandler.OFFSETS_WUXIN)); // 定单双 *
		playHandlers.put("2_czw",
				new SYX5CZWPlayHandler("2_czw", 1,ITicketPlayHandler.OFFSET_11X5ZHONG));//猜中位 *
		//趣味 END
		
		//任选Start
		playHandlers.put("2_rx1fs",
				new BDWPlayHandler("2_rx1fs", 1, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选一中一复式
		playHandlers.put("2_rx2fs",
				new BDWPlayHandler("2_rx2fs", 2, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选二中二复式
		playHandlers.put("2_rx3fs",
				new BDWPlayHandler("2_rx3fs", 3, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选三中三复式
		playHandlers.put("2_rx4fs",
				new BDWPlayHandler("2_rx4fs", 4, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选四中四复式
		playHandlers.put("2_rx5fs",
				new BDWPlayHandler("2_rx5fs", 5, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选五中五复式
		playHandlers.put("2_rx6fs",
				new BDWPlayHandler("2_rx6fs", 6, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选六中五复式
		playHandlers.put("2_rx7fs",
				new BDWPlayHandler("2_rx7fs", 7, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选七中五复式
		playHandlers.put("2_rx8fs",
				new BDWPlayHandler("2_rx8fs", 8, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选八中五复式

		playHandlers.put("2_rx1ds",
				new RXDS115PlayHandler("2_rx1ds", 1, ITicketPlayHandler.OFFSETS_WUXIN));// 任选一中一单式
		playHandlers.put("2_rx2ds",
				new RXDS115PlayHandler("2_rx2ds", 2, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选二中二单式
		playHandlers.put("2_rx3ds",
				new RXDS115PlayHandler("2_rx3ds", 3, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选三中三单式
		playHandlers.put("2_rx4ds",
				new RXDS115PlayHandler("2_rx4ds", 4, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选四中四单式
		playHandlers.put("2_rx5ds",
				new RXDS115PlayHandler("2_rx5ds", 5, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选五中五单式
		playHandlers.put("2_rx6ds",
				new RXDS115PlayHandler("2_rx6ds", 6, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选六中五单式
		playHandlers.put("2_rx7ds",
				new RXDS115PlayHandler("2_rx7ds", 7, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选七中五单式
		playHandlers.put("2_rx8ds",
				new RXDS115PlayHandler("2_rx8ds", 8, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选八中五单式
		//任选END
		
		//任选拖胆START
		playHandlers.put("2_rxtd2",
				new RXTD115PlayHandler("2_rxtd2", 2, 2, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选二中二拖胆
		playHandlers.put("2_rxtd3",
				new RXTD115PlayHandler("2_rxtd3", 3, 3, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选三中三拖胆
		playHandlers.put("2_rxtd4",
				new RXTD115PlayHandler("2_rxtd4", 4, 4, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选四中四拖胆
		playHandlers.put("2_rxtd5",
				new RXTD115PlayHandler("2_rxtd5", 5, 5, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选五中五拖胆
		playHandlers.put("2_rxtd6",
				new RXTD115PlayHandler("2_rxtd6", 5, 6, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选六中五拖胆
		playHandlers.put("2_rxtd7",
				new RXTD115PlayHandler("2_rxtd7", 5, 7, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选七中五拖胆
		playHandlers.put("2_rxtd8",
				new RXTD115PlayHandler("2_rxtd8", 5, 8, ITicketPlayHandler.OFFSETS_WUXIN)); // 任选八中五拖胆
		//任选拖胆END
		return map;
	}

	/**
	 * 快3所有玩法集合
	 */
	private static Map<String, ITicketPlayHandler> k3Handlers(){
		Map<String, ITicketPlayHandler> map = new HashMap<>();
		playHandlers.put("3_ebthdx",
				new BDWPlayHandler("3_ebthdx",2, ITicketPlayHandler.OFFSETS_QIANSAN));// 二不同号，标准选号
		playHandlers.put("3_ebthds",
				new ZXDSK3PlayHandler("3_ebthds",ITicketPlayHandler.OFFSETS_QIANSAN));// 二不同号，手动选号
		playHandlers.put("3_ebthdt",
				new ZXDTPlayHandler("3_ebthdt",ITicketPlayHandler.OFFSETS_QIANSAN));// 二不同号，胆拖

		playHandlers.put("3_ethdx",
				new ZXETHDXBZK3PlayHander("3_ethdx",ITicketPlayHandler.OFFSETS_QIANSAN));// 二同号标准选号
		playHandlers.put("3_ethds",
				new ZXDSK3PlayHandler("3_ethds",ITicketPlayHandler.OFFSETS_QIANSAN));// 二同号手动选号
		playHandlers.put("3_ethfx",
				new ZXETHFXK3PlayHander("3_ethfx",ITicketPlayHandler.OFFSETS_QIANSAN));// 二同号复选

		playHandlers.put("3_sbthdx",
				new BDWPlayHandler("3_sbthdx",3, ITicketPlayHandler.OFFSETS_QIANSAN));// 三不同号，标准选号
		playHandlers.put("3_sbthds",
				new ZXDSK3PlayHandler("3_sbthds",ITicketPlayHandler.OFFSETS_QIANSAN));//三不同号 手动选号

		playHandlers.put("3_sthdx",
				new DXTXK3STHPlayHandler("3_sthdx",1,ITicketPlayHandler.OFFSETS_QIANSAN));//三同号单选
		playHandlers.put("3_sthtx",
				new DXTXK3STHPlayHandler("3_sthtx",1,ITicketPlayHandler.OFFSETS_QIANSAN));//三同号通选
		playHandlers.put("3_slhtx",
				new TXK3SLHPlayHandler("3_slhtx",ITicketPlayHandler.OFFSETS_QIANSAN));//三连号通选
		playHandlers.put("3_hezhi",
				new ZHHZPlayHandler("3_hezhi",ITicketPlayHandler.OFFSETS_QIANSAN));//和值
		playHandlers.put("3_hzdxds",
				new ZHDXDSPlayHanlder("3_hzdxds",ITicketPlayHandler.OFFSETS_QIANSAN, 11, 10, 2));//和值大小单双
		return map;
	}

	/**
	 * 福彩3D所有玩法集合
	 * 福彩3D：
	 *  1、 前三混合组选 需要根据玩法ID（playId）判断奖金
	 *  2、定位胆需要根据中奖注数计算奖金
	 *  3、不定位需要根据中奖注数计算奖金
	 * @return
	 */
	private static Map<String, ITicketPlayHandler> d3Handlers() {
		Map<String, ITicketPlayHandler> map = new HashMap<>();
		//前三                                              
		playHandlers.put("4_sanxzhixfs",
				new ZHXFSPlayHandler("4_sanxzhixfs", ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三直选复式
		playHandlers.put("4_sanxzhixds",
				new ZHXDSPlayHandler("4_sanxzhixds", ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三直选单式
		playHandlers.put("4_sanxzhixhz",
				new ZHHZPlayHandler("4_sanxzhixhz", ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三直选和值

		playHandlers.put("4_sanxzs",
				new ZXZ3PlayHanlder("4_sanxzs", ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三组三
		playHandlers.put("4_sanxzl",
				new BDWPlayHandler("4_sanxzl", 3, ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三组六
		playHandlers.put("4_sanxhhzx",
				new ZXDSPlayHandler("4_sanxhhzx","4_sanxzs", "4_sanxzl",
				ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三混合组选
		
		//二星
		playHandlers.put("4_exzhixfsh",
				new ZHXFSPlayHandler("4_exzhixfsh", ITicketPlayHandler.OFFSETS_ERSAN)); // 后二直选复式
		playHandlers.put("4_exzhixdsh",
				new ZHXDSPlayHandler("4_exzhixdsh", ITicketPlayHandler.OFFSETS_ERSAN)); // 后二直选单式
		playHandlers.put("4_exzhixhzh",
				 new ZHHZPlayHandler("4_exzhixhzh",ITicketPlayHandler.OFFSETS_ERSAN)); // 后二直选和值
		playHandlers.put("4_exzuxfsh",
				new BDWPlayHandler("4_exzuxfsh", 2, ITicketPlayHandler.OFFSETS_ERSAN)); // 后二组选复式
		playHandlers.put("4_exzuxdsh", new ZXDSPlayHandler("4_exzuxdsh","4_exzuxdsh", "4_exzuxdsh",
				ITicketPlayHandler.OFFSETS_ERSAN)); // 后二组选单式
		
		playHandlers.put("4_exzhixfsq",
				new ZHXFSPlayHandler("4_exzhixfsq", ITicketPlayHandler.OFFSETS_QIANER)); // 前二直选复式
		playHandlers.put("4_exzhixdsq",
				new ZHXDSPlayHandler("4_exzhixdsq", ITicketPlayHandler.OFFSETS_QIANER)); // 前二直选单式
		playHandlers.put("4_exzhixhzq",
				 new ZHHZPlayHandler("4_exzhixhzq",ITicketPlayHandler.OFFSETS_QIANER)); // 前二直选和值
		playHandlers.put("4_exzuxfsq",
				new BDWPlayHandler("4_exzuxfsq", 2, ITicketPlayHandler.OFFSETS_QIANER)); // 前二组选复式
		playHandlers.put("4_exzuxdsq", new ZXDSPlayHandler("4_exzuxdsq", "4_exzuxdsq", "4_exzuxdsq",
				ITicketPlayHandler.OFFSETS_QIANER)); // 前二组选单式
		
		//定位胆
		playHandlers.put("4_dwd",
				new DWDPlayHandler("4_dwd", ITicketPlayHandler.OFFSETS_QIANSAN)); // 一星定位胆复式

		//一码不定位
		playHandlers.put("4_yimabdw",
				new BDWPlayHandler("4_yimabdw", 1, ITicketPlayHandler.OFFSETS_QIANSAN)); // 前三一码不定位
		
		return map;
	}

	/**
	 * 快乐8所有玩法集合
	 */
	private static Map<String, ITicketPlayHandler> kl8Handlers(){
		Map<String, ITicketPlayHandler> map = new HashMap<>();
		playHandlers.put("5_hezhids",
				new QWKL8PlayHandler("5_hezhids"));//和值单双
		playHandlers.put("5_hezhidx",
				new QWKL8PlayHandler("5_hezhidx"));//和值大小
		playHandlers.put("5_jopan",
				new QWKL8PlayHandler("5_jopan"));//奇偶盘
		playHandlers.put("5_sxpan",
				new QWKL8PlayHandler("5_sxpan"));//上下盘
		playHandlers.put("5_hzdxds",
				new QWKL8PlayHandler("5_hzdxds"));//大小单双

		playHandlers.put("5_rx1",
				new RXKL8PlayHandler("5_rx1",1,ITicketPlayHandler.OFFSETS_QIANERSHI));//任1
		playHandlers.put("5_rx2",
				new RXKL8PlayHandler("5_rx2",2,ITicketPlayHandler.OFFSETS_QIANERSHI));//任2
		playHandlers.put("5_rx3",
				new RXKL8PlayHandler("5_rx3",3,ITicketPlayHandler.OFFSETS_QIANERSHI));//任3
		playHandlers.put("5_rx4",
				new RXKL8PlayHandler("5_rx4",4,ITicketPlayHandler.OFFSETS_QIANERSHI));//任4
		playHandlers.put("5_rx5",
				new RXKL8PlayHandler("5_rx5",5,ITicketPlayHandler.OFFSETS_QIANERSHI));//任5
		playHandlers.put("5_rx6",
				new RXKL8PlayHandler("5_rx6",6,ITicketPlayHandler.OFFSETS_QIANERSHI));//任6
		playHandlers.put("5_rx7",
				new RXKL8PlayHandler("5_rx7",7,ITicketPlayHandler.OFFSETS_QIANERSHI));//任7

		playHandlers.put("5_hezhiwx",
				new QWKL8PlayHandler("5_hezhiwx"));//五行

		return map;
	}


	/**
	 * PK10所有玩法集合
	 */
	private static Map<String, ITicketPlayHandler> pk10Handlers() {
		Map<String, ITicketPlayHandler> map = new HashMap<>();
		playHandlers.put("6_qianyi",
				new BDWPlayHandler("6_qianyi", 1,ITicketPlayHandler.OFFSETS_QIANYI));// 前一

		playHandlers.put("6_qianerzxfs",
				new ZHIXFSERMAYSPlayHandler("6_qianerzxfs", ITicketPlayHandler.OFFSETS_QIANER));// 前二 复式
		playHandlers.put("6_qianerzxds",
				new ZHXDSPK10PlayHandler("6_qianerzxds", ITicketPlayHandler.OFFSETS_QIANER));// 前二单式

		playHandlers.put("6_qiansanzxfs",
				new ZHIXFSERMAYSPlayHandler("6_qiansanzxfs", ITicketPlayHandler.OFFSETS_QIANSAN));// 前三复式
		playHandlers.put("6_qiansanzxds",
				new ZHXDSPK10PlayHandler("6_qiansanzxds", ITicketPlayHandler.OFFSETS_QIANSAN));// 前三单式

		playHandlers.put("6_qiansizxfs",
				new ZHIXFSERMAYSPlayHandler("6_qiansizxfs", ITicketPlayHandler.OFFSETS_QIANSI));// 前四复式
		playHandlers.put("6_qiansizxds",
				new ZHXDSPK10PlayHandler("6_qiansizxds", ITicketPlayHandler.OFFSETS_QIANSI));// 前四单式

		playHandlers.put("6_qianwuzxfs",
				new ZHIXFSERMAYSPlayHandler("6_qianwuzxfs", ITicketPlayHandler.OFFSETS_QIANWU));// 前五复式
		playHandlers.put("6_qianwuzxds",
				new ZHXDSPK10PlayHandler("6_qianwuzxds", ITicketPlayHandler.OFFSETS_QIANWU));// 前五单式


		playHandlers.put("6_qwdingweidan",
				new DWDPK10PlayHandler("6_qwdingweidan", ITicketPlayHandler.OFFSETS_QIANWU));// 前五定位胆

		playHandlers.put("6_hwdingweidan",
				new DWDPK10PlayHandler("6_hwdingweidan", ITicketPlayHandler.OFFSETS_HOUWU));// 后五定位胆

		playHandlers.put("6_dw1dxds",
				new DXDSPK10PlayHanlder("6_dw1dxds", 0)); // 大小单双第一位
		playHandlers.put("6_dw2dxds",
				new DXDSPK10PlayHanlder("6_dw2dxds", 1)); // 大小单双第二位
		playHandlers.put("6_dw3dxds",
				new DXDSPK10PlayHanlder("6_dw3dxds", 2)); // 大小单双第三位
		playHandlers.put("6_dw4dxds",
				new DXDSPK10PlayHanlder("6_dw4dxds", 3)); // 大小单双第四位
		playHandlers.put("6_dw5dxds",
				new DXDSPK10PlayHanlder("6_dw5dxds", 4)); // 大小单双第五位

		playHandlers.put("6_pk10_dxdsgyhz",
				new HZDXDSPlayHanlder("6_pk10_dxdsgyhz", new int[] {12, 13, 14, 15, 16, 17, 18, 19}, new int[] {3, 4, 5, 6, 7, 8, 9, 10, 11}, ITicketPlayHandler.OFFSETS_QIANER)); // 大小单双冠亚和值

		playHandlers.put("6_pk10_hzgyhz",
				new HZMultiplePrizePlayHandler("6_pk10_hzgyhz", ITicketPlayHandler.OFFSETS_QIANER, 3, 19)); // 冠亚和值
		playHandlers.put("6_pk10_hzqshz",
				new HZMultiplePrizePlayHandler("6_pk10_hzqshz", ITicketPlayHandler.OFFSETS_QIANSAN, 6, 27)); // 前三和值

		playHandlers.put("6_01vs10",
				new LHPlayHanlder("6_01vs10", 0, 9, " ")); // 龙虎01VS10
		playHandlers.put("6_02vs09",
				new LHPlayHanlder("6_02vs09", 1, 8, " ")); // 龙虎02VS09
		playHandlers.put("6_03vs08",
				new LHPlayHanlder("6_03vs08", 2, 7, " ")); // 龙虎03VS08
		playHandlers.put("6_04vs07",
				new LHPlayHanlder("6_04vs07", 3, 6, " ")); // 龙虎04VS07
		playHandlers.put("6_05vs06",
				new LHPlayHanlder("6_05vs06", 4, 5, " ")); // 龙虎05VS06
		return map;

	}

	/**
	 * 龙虎斗所有玩法集合
	 */
	private static Map<String, ITicketPlayHandler> lhdHandlers() {
		Map<String, ITicketPlayHandler> map = new HashMap<>();

		// 龙虎
		playHandlers.put("7_lhd_longhuhewq",
				new SSCLHPlayHanlder("7_lhd_longhuhewq", 0, 1)); // 龙虎万千
		playHandlers.put("7_lhd_longhuhewb",
				new SSCLHPlayHanlder("7_lhd_longhuhewb", 0, 2)); // 龙虎万百
		playHandlers.put("7_lhd_longhuhews",
				new SSCLHPlayHanlder("7_lhd_longhuhews", 0, 3)); // 龙虎万十
		playHandlers.put("7_lhd_longhuhewg",
				new SSCLHPlayHanlder("7_lhd_longhuhewg", 0, 4)); // 龙虎万个
		playHandlers.put("7_lhd_longhuheqb",
				new SSCLHPlayHanlder("7_lhd_longhuheqb", 1, 2)); // 龙虎千百
		playHandlers.put("7_lhd_longhuheqs",
				new SSCLHPlayHanlder("7_lhd_longhuheqs", 1, 3)); // 龙虎千十
		playHandlers.put("7_lhd_longhuheqg",
				new SSCLHPlayHanlder("7_lhd_longhuheqg", 1, 4)); // 龙虎千个
		playHandlers.put("7_lhd_longhuhebs",
				new SSCLHPlayHanlder("7_lhd_longhuhebs", 2, 3)); // 龙虎百十
		playHandlers.put("7_lhd_longhuhebg",
				new SSCLHPlayHanlder("7_lhd_longhuhebg", 2, 4)); // 龙虎百个
		playHandlers.put("7_lhd_longhuhesg",
				new SSCLHPlayHanlder("7_lhd_longhuhesg", 3, 4)); // 龙虎十个
		// 龙虎

		return map;
	}
}
