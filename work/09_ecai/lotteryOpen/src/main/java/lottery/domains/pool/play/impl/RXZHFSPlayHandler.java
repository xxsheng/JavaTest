package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 * 任选 直选复式
 * 
 */
public class RXZHFSPlayHandler implements ITicketPlayHandler{
	private String playId;
	/** 任选号码数量 */
	private int rxNum;

	public RXZHFSPlayHandler(String playId,int rxNum) {
		this.playId =playId;
		this.rxNum = rxNum;
	}

	@Override
	public String[] getBetNums(String betNums) {
		return betNums.trim().split(",");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, OFFSETS_WUXIN);
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] openNum = getOpenNums(openNums);
		String[] nums = index.trim().split(",");
		int winNum = 0;
		switch (rxNum) {
		case 1:
			break;
		case 2:
			for (int i = 0; i < nums.length; i++) {
				for (int j = i + 1; j < nums.length; j++) {
					if (nums[i].contains(openNum[i]) && nums[j].contains(openNum[j])) {
						winNum++;
					}
				}
			}
			break;
		case 3:
			for (int i = 0; i < nums.length; i++) {
				for (int j = i + 1; j < nums.length; j++) {
					for (int k = j + 1; k < nums.length; k++) {
						if (nums[i].contains(openNum[i]) && nums[j].contains(openNum[j])
								&& nums[k].contains(openNum[k])) {
							winNum++;
						}
					}
				}
			}
			break;
		case 4:
			for (int i = 0; i < nums.length; i++) {
				for (int j = i + 1; j < nums.length; j++) {
					for (int k = j + 1; k < nums.length; k++) {
						for (int l = k + 1; l < nums.length; l++) {
							if (nums[i].contains(openNum[i]) && nums[j].contains(openNum[j])
									&& nums[k].contains(openNum[k]) && nums[l].contains(openNum[l])) {
								winNum++;
							}
						}
					}
				}
			}
			break;
		case 5:
			break;
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
}