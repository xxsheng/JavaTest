package lottery.domains.pool.play.impl;




import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 * 直选组合 玩法处理类
 */
public class ZXZHPlasyHandler implements ITicketPlayHandler {
	private String playId;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;

	public ZXZHPlasyHandler(String playId,int[] offsets) {
		this.playId = playId;
		this.offsets = offsets;
	}

	@Override
	public String[] getBetNums(String betNums) {
		String[] nums = betNums.trim().split(",");
		String[] res = new String[offsets.length];
		int idx = 0;
		for (int i = 0; i < nums.length; i++) {
			if (!"-".equals(nums[i].trim())) {
				res[idx++] = nums[i].replaceAll(" ", "").trim();
			}
		}
		return res;
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, offsets);
	}
	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] openNum = getOpenNums(openNums);
		String[] nums = getBetNums(index);
		if (nums == null || openNum == null ||
				nums.length != offsets.length || openNum.length != offsets.length) {
			return result;
		}
		int winNum = 0;
		for (int i = (nums.length-1); i >= 0 ; i--) {
			String ssnum = nums[i];
			if(ssnum.indexOf(openNum[i]) >=0){
				winNum++;
			}else{
				break;
			}
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
	
	public static void main(String[] args) {
		//9,8,7,6,5
		String betNum = "3,3,3,3,-";
		String openCode = "3,3,2,3,3";
		int offset[] = new int[] {0,1,2,3};

		ZXZHPlasyHandler hd = new ZXZHPlasyHandler("单式", offset);
		WinResult resulthd = hd.calculateWinNum(1, betNum, openCode);

		String[] openCodeArray = hd.getOpenNums(openCode);
		StringBuffer sbf = new StringBuffer();
		for (int i = 0; i < openCodeArray.length; i++) {
			sbf.append(openCodeArray[i]).append(",");
		}
		System.out.println("开奖号码为：" + sbf.toString().substring(0, sbf.toString().length() - 1));
		
		if (resulthd ==null) {
			System.out.println("错误-----");
		} else {
			System.out.println("中奖注数：" + resulthd.getWinNum() + ",玩法ID为：" + resulthd.getPlayId() + ",中奖号码:"
					+ resulthd.getWinCode());
		}
	}
}
