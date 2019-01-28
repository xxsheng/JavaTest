package lottery.domains.pool.play;

import java.util.List;

public class WinResult {

	private String playId;
	private int winNum;
	private int groupType= 0;// 默认0   组三:1,组六:2
	/** 中奖号码，默认为0，0则不需要处理，
	 * 如：11X5 趣味猜中位3和9 =?奖金，4和8 =?奖金，5和7=?奖金,6 =?奖金  返回03 or 09 ……
	 *    订单双  5单0双 = ?奖金，4单1双 =?奖金 …… 返回 5单0双，4单1双……
	 **/
	private String winCode = "0#";

	// 奖金下标，如lottery_play_rules.prize=224.00,438.00,840.00,438.00,224.00，下瓢为1，就表示第2位
	private int prizeIndex;

	// 中奖号码，中奖下标，中奖注数，返回多中奖结果
	private List<MultipleResult> multipleResults;
	
	public int getGroupType() {
		return groupType;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}
	
	public String getPlayId() {
		return playId;
	}

	public void setPlayId(String playId) {
		this.playId = playId;
	}

	public int getWinNum() {
		return winNum;
	}

	public void setWinNum(int winNum) {
		this.winNum = winNum;
	}

	public String getWinCode() {
		return winCode;
	}

	public void setWinCode(String winCode) {
		this.winCode = winCode;
	}

	public int getPrizeIndex() {
		return prizeIndex;
	}

	public void setPrizeIndex(int prizeIndex) {
		this.prizeIndex = prizeIndex;
	}

	public List<MultipleResult> getMultipleResults() {
		return multipleResults;
	}

	public void setMultipleResults(List<MultipleResult> multipleResults) {
		this.multipleResults = multipleResults;
	}
}
