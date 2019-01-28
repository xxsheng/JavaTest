package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.Arrays;
import java.util.TreeSet;

/**
 * 时时彩 任选 组选 组三 玩法处理类
 *
 */
public class SSCRXZXZ3PlayHanlder implements ITicketPlayHandler{
	private String playId;
	public SSCRXZXZ3PlayHanlder(String playId){
		this.playId = playId;
	}

	@Override
	public String[] getBetNums(String bet) {
		String [] res = new String [2];
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
		if(placeVal.equals("")  || betValue.equals("")){
			return null;
		}
		res[0] = placeVal;
		res[1] = betValue;
		return res;
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, OFFSETS_WUXIN);
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] data = getBetNums(index);
		if (data == null || data.length != 2) {
			return result;
		}
		int winNum = 0;
		String[] offsets = data[0].trim().split(",");
		String[] betNum = data[1].trim().split(",");
		Arrays.sort(betNum);
		String[] nums = getOpenNums(openNums);

		for (int i = 0; i < offsets.length; i++) {
			for (int j = i + 1; j < offsets.length; j++) {
				for (int k = j + 1; k < offsets.length; k++) {
					TreeSet<String> set = new TreeSet<>();
					set.add(nums[Integer.parseInt(offsets[i])]);
					set.add(nums[Integer.parseInt(offsets[j])]);
					set.add(nums[Integer.parseInt(offsets[k])]);
					if (set.size() == 2) {
						if (Arrays.binarySearch(betNum, set.pollFirst()) >= 0
								&& Arrays.binarySearch(betNum, set.pollFirst()) >= 0) {
							winNum++;
						}
					}
				}
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
	
	
//	public static void main(String[] args) {
//		String betNum = "[√,√,√,√,√]0,1,2,3,4,5,6,7,8,9";// "0123456789,0123456789,0123456789,0123456789,0123456789";
//		String openCode = "4,6,4,6,0";//67038
//		int[] offsets = new int[] { 0,1,2};
//
//		SSCRXZXZ6PlayHanlder dwd = new SSCRXZXZ6PlayHanlder("cqssc_rx2fs");
//		String[] opens = dwd.getOpenNums(openCode);
//		StringBuffer bf = new StringBuffer();
//		for (int i = 0; i < opens.length; i++) {
//			bf.append(opens[i]);
//		}
//		System.out.println(bf.toString());
//
//		List<WinResult> result = dwd.calculateWinNum(betNum, openCode);
//		if (null != result) {
//			System.out.println("玩法ID：" + result.get(0).getPlayId() + "，中奖注数：" + result.get(0).getWinNum());
//		} else {
//			System.out.println("==================");
//		}
//	}
}
