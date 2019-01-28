package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

/**
 * 11选5 趣味玩法定单双处理类
 */
public class QW11X5DDSPlayHandler implements ITicketPlayHandler {
	private String playId;
	/**中奖号码位置 */
	private int [] offsets;
	
	public QW11X5DDSPlayHandler(String playId,int offsets[]) {
		this.playId = playId;
		this.offsets = offsets;
	}
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.split("\\|");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, offsets);
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String [] openCodes = getOpenNums(openNums);
		String [] betNums = getBetNums(index);
		if (betNums == null || openCodes == null || openCodes.length != offsets.length) {
			return result;
		}
		int dan = 0;
		int shuang = 0;
		for (int i = 0; i < openCodes.length; i++) {
			Integer num = Integer.parseInt(openCodes[i]);
			if(num % 2 == 0){
				shuang++;
			}else{
				dan++;
			}
		}
		int winNum = 0;
		StringBuffer prizeStr = new StringBuffer();
		prizeStr.append(dan).append("单").append(shuang).append("双");
		for (int i = 0; i < betNums.length; i++) {
			if(betNums[i].equals(prizeStr.toString().trim())){
				winNum ++;
			}
		}
		result.setPlayId(playId);
		result.setWinCode(prizeStr.toString().trim());
		result.setWinNum(winNum);
		return result;
	}
	
//	public static void main(String[] args) {
//		String betNum ="04,05,06,09";
//		String openCode = "01,02,04,09,11";
//		int offset [] = new int[]{2};
//		
//		BDWPlayHandler  hd = new BDWPlayHandler("单式",1,offset);
//		List<WinResult> result = hd.calculateWinNum(betNum, openCode);
//		
//		String [] openCodeArray = hd.getOpenNums(openCode);
//		StringBuffer sbf = new StringBuffer();
//		for (int i = 0; i < openCodeArray.length; i++) {
//			sbf.append(openCodeArray[i]).append(",");
//		}
//		System.out.println("开奖号码为："+sbf.toString().substring(0,sbf.toString().length()-1));
//		
//		if(result.size()<=0){
//			System.out.println("错误-----");
//		}else{
//			System.out.println("中奖注数："+result.get(0).getWinNum()+
//					",玩法ID为："+result.get(0).getPlayId()+",中奖号码:"+result.get(0).getWinCode());
//		}
//	}
}
