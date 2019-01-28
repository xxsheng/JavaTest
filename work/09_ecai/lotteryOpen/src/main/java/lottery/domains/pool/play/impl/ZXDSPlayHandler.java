package lottery.domains.pool.play.impl;

import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时时彩 组选 单式 玩法处理类 *-
 *
 */
public class ZXDSPlayHandler implements ITicketPlayHandler {

	// 组选类型：0混合组选，1组三单式，2组六单式，3组选单式
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	/** 组三 */
	private String playId0;
	/** 组六 */
	private String playId1;
	/** 玩法ID */
	private String playId;
	public ZXDSPlayHandler(String playId,String playId0, String playId1, int[] offsets) {
		this.playId = playId;
		this.playId0 = playId0;
		this.playId1 = playId1;
		this.offsets = offsets;
		schedulerClearCache();
	}

	@Override
	public String[] getBetNums(String betNums) {
		String[] nums = betNums.trim().split(" ");
		TreeSet<String> res = new TreeSet<>();
		for (int i = 0; i < nums.length; i++) {
			if (nums[i] != null && nums[i].trim().length() > 0) {
				res.add(TicketPlayUtils.getSortedNums(nums[i]));
			}
		}
		return res.toArray(new String[] {});
	}

	@Override
	public String[] getOpenNums(String openNums) {
		String[] open = TicketPlayUtils.getOpenNums(openNums, offsets);
		Arrays.sort(open);
		return open;
	}

//	@Override
//	public int calculateWinNum(String index, String openNums) {
//		String[] nums = getBetNums(index);
//		String[] openNum = getOpenNums(openNums);
//		if (nums == null || openNums == null) {
//			return 0;
//		}
//		// 豹子不中奖
//		String[] fixedOpenNum = TicketPlayUtils.getFixedNums(openNum);
//		if (fixedOpenNum.length == 1) {
//			return 0;
//		}
//		StringBuilder sb = new StringBuilder();
//		for (String nm : openNum) {
//			sb.append(nm.trim());
//		}
//		int winNum = 0;
//		Arrays.sort(nums);
//		if (Arrays.binarySearch(nums, sb.toString()) >= 0) {
//			winNum = 1;
//		}
//		return winNum;
//	}

	private ConcurrentHashMap<Integer, String[]> numCache = new ConcurrentHashMap<>();
	public void schedulerClearCache() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				synchronized (numCache) {
					numCache.clear();
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1000, 1000 * 60 * 30); // 每30分钟清理一次缓存
	}
	
	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();

		String[] nums = null;
		boolean hit = false;
		try {
			if (numCache.containsKey(userBetsId)) {
				synchronized (numCache) {
					nums = numCache.get(userBetsId);
					hit = true;
				}
			}
			else {
				nums = getBetNums(index);
			}
		} catch (Exception e) {
			e.printStackTrace();
			numCache.remove(userBetsId);
		}

		if (nums == null) {
			nums = getBetNums(index);
			hit = false;
		}


		// 豹子不中奖
		String[] openNum = getOpenNums(openNums);
		if (nums == null || openNums == null) {
			return result;
		}
		String[] fixedOpenNum = TicketPlayUtils.getFixedNums(openNum);
		if (fixedOpenNum.length == 1) {
			return result;
		}
		StringBuilder sb = new StringBuilder();
		for (String nm : openNum) {
			sb.append(nm.trim());
		}
		int winNum = 0;
		if (!hit) {
			Arrays.sort(nums);
			synchronized (numCache) {
				numCache.put(userBetsId, nums);
			}
		}
		if (Arrays.binarySearch(nums, sb.toString()) >= 0) {
			winNum = 1;
		}
		if (openNum.length == fixedOpenNum.length) {
			result.setPlayId(playId1);
			result.setGroupType(2);
		} else {
			result.setPlayId(playId0);
			result.setGroupType(1);
		}
		result.setWinNum(winNum);
		return result;
	}
	
	
//	public static void main(String[] args) {
//		String betNum = "14 72 16 18";//"0123456789,0123456789,0123456789,0123456789,0123456789";
//		String openCode = "1,2,9,2,6";
//		int [] offsets = new int[]{0,1};
//		
//		ZXDSPlayHandler dwd = new ZXDSPlayHandler("cqssc_exzuxdsq","cqssc_exzuxdsq","cqssc_exzuxdsq",offsets);
//		String [] opens = dwd.getOpenNums(openCode);
//		StringBuffer bf = new StringBuffer();
//		for (int i = 0; i < opens.length; i++) {
//			bf.append(opens[i]);
//		}
//		System.out.println(bf.toString());
//		
//		List<WinResult> result = dwd.calculateWinNum(betNum, openCode);
//		if(null != result){
//			System.out.println("玩法ID："+result.get(0).getPlayId() +"，中奖注数："+result.get(0).getWinNum());
//		}else{
//			System.out.println("==================");
//		}
//	}

	public static void main(String[] args) {
		String betNum = "046 047 049 067 069 079 146 147 149 167 169 179 246 247 249 267 269 279 346 347 349 367 369 379 446 447 449 456 457 459 466 467 468 469 477 478 479 489 499 567 569 579 667 669 677 678 679 689 699 779 789 799";
		ZXDSPlayHandler zxdsPlayHandler = new ZXDSPlayHandler("hg1d5fc_sxhhzxh", "hg1d5fc_sxzuxzsh", "hg1d5fc_sxzuxzlh",
				ITicketPlayHandler.OFFSETS_HOUSAN);
		WinResult winResult = zxdsPlayHandler.calculateWinNum(1, betNum, "0,4,4,4,7");

		System.out.println(winResult.getWinNum());
		System.out.println(winResult.getPlayId());
	}
}
