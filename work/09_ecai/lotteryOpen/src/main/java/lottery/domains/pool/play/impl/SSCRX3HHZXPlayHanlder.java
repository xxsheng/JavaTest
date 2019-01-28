package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时时彩 任三 混合组选 玩法处理类
 *
 */
public class SSCRX3HHZXPlayHanlder{
	static {
		schedulerClearCache();
	}

	private static final String PLAYID_ZUX3 = "sxzuxzsh"; // 只是表达组三的赔率ID
	private static final String PLAYID_ZUX6 = "sxzuxzlh"; // 只是表达组六的赔率ID
	public SSCRX3HHZXPlayHanlder(){
	}

	public Object[] getBetNums(String bet) {
		Object [] res = new Object [2];
		String []xz = bet.substring(1,10).split(",");
		StringBuffer placeIndex = new StringBuffer();
		for (int i = 0; i < xz.length; i++) {
			if(xz[i].equals("√")){
				placeIndex.append(String.valueOf(i)).append(",");
			}
		}
		String placeVal = placeIndex.toString().trim();
		if(!placeVal.equals("")){
			placeVal = placeVal.substring(0,placeVal.length()-1);
		}
		
		String betValue = bet.substring(11,bet.length()).trim();

		TreeSet<String> filter = new TreeSet<>();
		String[] split = betValue.split(" ");
		for (int i = 0; i < split.length; i++) {
			if (split[i] != null && split[i].trim().length() > 0) {
				filter.add(TicketPlayUtils.getSortedNums(split[i]));
			}
		}
		String[] nums = filter.toArray(new String[] {});

		res[0] = placeVal.trim().split(",");
		res[1] = nums;
		return res;
	}

	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, ITicketPlayHandler.OFFSETS_WUXIN);
	}

	private static ConcurrentHashMap<Integer, Object[]> numCache = new ConcurrentHashMap<>();
	public static void schedulerClearCache() {
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

	public List<WinResult> calculateWinNum(int userBetsId, String index, String openNums) {
		Map<String, WinResult> results = new HashMap<>();

		Object[] data = null;
		boolean hit = false;
		try {
			if (numCache.containsKey(userBetsId)) {
				synchronized (numCache) {
					data = numCache.get(userBetsId);
					hit = true;
				}
			}
			else {
				data = getBetNums(index);
			}
		} catch (Exception e) {
			e.printStackTrace();
			numCache.remove(userBetsId);
		}

		if (data == null) {
			data = getBetNums(index);
			hit = false;
		}

		if (data.length != 2) {
			return null;
		}

		String[] offsets = ((String[])data[0]);
		String[] betNums = ((String[])data[1]);
		if (!hit) {
			Arrays.sort(betNums);
			data[1] = betNums;
			synchronized (numCache) {
				numCache.put(userBetsId, data);
			}
		}

		for (int i = 0; i < offsets.length - 1; i++) {
			for (int j = i + 1; j < offsets.length; j++) {
				for (int k = j + 1; k < offsets.length; k++) {
					String[] offsetOpenNums = TicketPlayUtils.getOpenNums(openNums, new int[]{Integer.parseInt(offsets[i]), Integer.parseInt(offsets[j]), Integer.parseInt(offsets[k])});

					// 开奖号码是豹子不开
					if (offsetOpenNums[0].equals(offsetOpenNums[1]) && offsetOpenNums[1].equals(offsetOpenNums[2])) {
						continue;
					}

					Arrays.sort(offsetOpenNums);
					String num = new StringBuilder().append(offsetOpenNums[0]).append(offsetOpenNums[1]).append(offsetOpenNums[2]).toString();
					if (Arrays.binarySearch(betNums, num) >= 0) {


						String[] fixedOpenNum = TicketPlayUtils.getFixedNums(offsetOpenNums);
						// System.out.println("中奖号码:" + offsetOpenNums[0] + offsetOpenNums[1]  + offsetOpenNums[2] + "(" + offsets[i] + offsets[j] + offsets[k] + ")");

						String playId = fixedOpenNum.length == 2 ? PLAYID_ZUX3 : PLAYID_ZUX6;
						int groupType = fixedOpenNum.length == 2 ? 1 : 2;
						WinResult result = results.get(playId);
						if (result == null) {
							result = new WinResult();
							result.setPlayId(playId);
							result.setGroupType(groupType);
						}

						result.setWinNum(result.getWinNum()+1);
						results.put(playId, result);
					}
				}
			}
		}

		return new ArrayList<>(results.values());
	}

	/**
	 * 根据购买号码判断是组3还是组6
	 */
	private String getPlayId(char[] chars) {
		if (chars[0] == chars[1] || chars[0] == chars[2] || chars[1] == chars[2]) {
			return PLAYID_ZUX3;
		}

		return PLAYID_ZUX6;
	}

	public static void main(String[] args) {
		// String betNum = "[√,√,√,√,√]001 002 004 006 007 008 009 011 012 014 016 017 018 019 022 024 026 027 028 029 044 046 047 048 049 066 067 068 069 077 078 079 088 089 099 112 114 116 117 118 119 122 124 126 127 128 129 144 146 147 148 149 166 167 168 169 177 178 179 188 189 199 224 226 227 228 229 244 246 247 248 249 266 267 268 269 277 278 279 288 289 299 446 447 448 449 466 467 468 469 477 478 479 488 489 499 667 668 669 677 678 679 688 689 699 778 779 788 789 799 889 899";// "0123456789,0123456789,0123456789,0123456789,0123456789";
		// String openCode = "1,5,5,7,0";//67038
		// String openCode = "9,9,1,2,8";
		// String openCode = "7,5,4,6,7";
		String betNum = "[√,√,√,√,√]027 028 029 127 128 129 227 228 229 237 238 239 247 248 249 257 258 259 267 268 269 277 278 279 288 289 299";
		String openCode = "8,2,1,2,9";

		long start = System.currentTimeMillis();
		SSCRX3HHZXPlayHanlder hanlder = new SSCRX3HHZXPlayHanlder();

		System.out.println("开奖号码" + openCode);
		// for (int i = 0; i < 1000000; i++) {
			List<WinResult> results = hanlder.calculateWinNum(1, betNum, openCode);
		// }

		long spend = System.currentTimeMillis() - start;

		if (results != null) {
			for (WinResult result : results) {
				System.out.println("玩法ID：" + result.getPlayId() + "，中奖注数：" + result.getWinNum());
			}
		}
		System.out.println("耗时" + spend);

		// String code = "001 002 004 006 007 008 009 011 012 014 016 017 018 019 022 024 026 027 028 029 044 046 047 048 049 066 067 068 069 077 078 079 088 089 099 112 114 116 117 118 119 122 124 126 127 128 129 144 146 147 148 149 166 167 168 169 177 178 179 188 189 199 224 226 227 228 229 244 246 247 248 249 266 267 268 269 277 278 279 288 289 299 446 447 448 449 466 467 468 469 477 478 479 488 489 499 667 668 669 677 678 679 688 689 699 778 779 788 789 799 889 899";
		// String[] split = code.split(" ");
        //
		// System.out.println(split.length);
        //
		// for (String s : split) {
		// 	System.out.println(Arrays.binarySearch(split, s));
		// }


	}
}
