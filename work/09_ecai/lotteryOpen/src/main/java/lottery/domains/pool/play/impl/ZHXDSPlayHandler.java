package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 时时彩 直选 单式 玩法处理类   *-
 */
public class ZHXDSPlayHandler implements ITicketPlayHandler {
    private String playId;
    /**
     * 开奖号码中用于开奖计算的号码位置
     */
    private int[] offsets;

    public ZHXDSPlayHandler(String playId, int[] offsets) {
        this.playId = playId;
        this.offsets = offsets;
        schedulerClearCache();
    }

    @Override
    public String[] getBetNums(String betNums) {
        //bill
        return betNums.trim().split(" ");
    }

    @Override
    public String[] getOpenNums(String openNums) {
        return TicketPlayUtils.getOpenNums(openNums, offsets);
    }


//	@Override
//	public int calculateWinNum(String index, String openNums) {
//		String[] nums = getBetNums(index);
//		String[] openNum = getOpenNums(openNums);
//		if (nums == null || openNum == null || openNum.length != offsets.length) {
//			return 0;
//		}
//		StringBuilder openRes = new StringBuilder();
//		for (String num : openNum) {
//			openRes.append(num);
//		}
//		int winNum = 0;
//		Arrays.sort(nums);
//		if (Arrays.binarySearch(nums, openRes.toString()) >= 0) {
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

        // String[] nums = getBetNums(index);
        String[] openNum = getOpenNums(openNums);
        if (nums == null || openNum == null || openNum.length != offsets.length) {
            return result;
        }
        StringBuilder openRes = new StringBuilder();
        for (String num : openNum) {
            openRes.append(num);
        }
        int winNum = 0;

        if (!hit) {
            Arrays.sort(nums);
            synchronized (numCache) {
                numCache.put(userBetsId, nums);
            }
        }

        if (Arrays.binarySearch(nums, openRes.toString()) >= 0) {
            winNum = 1;
        }
        result.setPlayId(playId);
        result.setWinNum(winNum);
        return result;
    }

    public static void main(String[] args) {
        ZHXDSPlayHandler handler = new ZHXDSPlayHandler("1_exzhixdsh",ITicketPlayHandler.OFFSETS_HOUER);
        // WinResult winResult = handler.calculateWinNum(5980325, "73 97 92 87 84 43 22 26 06 86 96 08 09 93 70 94 05 45 36 63 16 03 39 58 64 85 71 31 35 61 11 42 81 02 75 46 40 34 74 20 23 90 15 21 48 07 00 44 41 27", "8,4,4,6,3");
        WinResult winResult = handler.calculateWinNum(5980325, "00 02 03 05 06 07 08 09 11 15 16 20 21 22 23 26 27 31 34 35 36 39 40 41 42 43 44 45 46 48 58 61 63 64 70 71 73 74 75 81 84 85 86 87 90 92 93 94 96 97", "8,4,4,6,3");
        System.out.println(winResult.getWinNum());
    }
}
