package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 * 时时彩 任选 组选复式 玩法处理类
 * 
 */
public class SSCRXZXFSPlayHandler implements ITicketPlayHandler{
	private String playId;
	/** 任选号码数量 */
	private int rxNum;

	public SSCRXZXFSPlayHandler(String playId,int rxNum) {
		this.playId = playId;
		this.rxNum = rxNum;
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
		String nums = data[1].trim();
		String[] offsets = data[0].trim().split(",");

		int[] offset = new int[offsets.length];
		for (int i = 0; i < offsets.length; i++) {
			offset[i] = Integer.parseInt(offsets[i]);
		}
		String[] openNum = getOpenNums(openNums);
		int winNum = 0;
		switch (rxNum) {
		case 1: // 一码
			break;
		case 2: // 二码
			for (int i = 0; i < offset.length; i++) {
				for (int j = i + 1; j < offset.length; j++) {
					if (nums.contains(openNum[offset[i]]) && nums.contains(openNum[offset[j]])
							&& !openNum[offset[i]].equals(openNum[offset[j]])) {
						winNum++;
					}
				}
			}
			break;
		case 3: // 三码
			break;
		case 4: // 四码
			break;
		default: // 五码
			break;
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
}
