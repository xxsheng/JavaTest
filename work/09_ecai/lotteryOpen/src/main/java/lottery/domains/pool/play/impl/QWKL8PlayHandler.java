package lottery.domains.pool.play.impl;


import lottery.domains.pool.play.HConstants;
import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;
import org.apache.commons.lang.ArrayUtils;

/**
 *快乐8 趣味玩法处理类
 */
public class QWKL8PlayHandler implements ITicketPlayHandler {
	private String playId;
	public QWKL8PlayHandler(String playId) {
		this.playId = playId;
	}
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.split("\\|");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return openNums.split(",");
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String [] betNum = getBetNums(index);
		String [] openNum = getOpenNums(openNums);
		int sum = TicketPlayUtils.getOpenNumSumByStr(openNums,openNum);
		int winNum = 0;
		switch (playId) {
		case "bjkl8_hezhids"://和值单双
			String reds = processDS(sum);
			if(ArrayUtils.contains(betNum,reds)){
				winNum = 1;
				result.setWinCode(reds);
			}
			break;
		case "bjkl8_hezhidx"://和值大小
			String rexhd = processDHX(sum);
			if(!"".equals(rexhd)){
				if(ArrayUtils.contains(betNum,rexhd)){
					winNum = 1;
					result.setWinCode(rexhd);
				}
			}
			break;
		case "bjkl8_jopan"://奇偶盘
			String rejoh = processJOH(openNum);
			if(ArrayUtils.contains(betNum,rejoh)){
				winNum = 1;
				result.setWinCode(rejoh);
			}
			break;
		case "bjkl8_sxpan"://上下盘
			String resx = processSX(openNum);
			if(ArrayUtils.contains(betNum,resx)){
				winNum = 1;
				result.setWinCode(resx);
			}
			break;
		case "bjkl8_hzdxds":
			String rexhdDX = processDHX(sum);
			String redxdsDS = processDS(sum);
			StringBuffer dxds = new StringBuffer();
			dxds.append(rexhdDX).append(redxdsDS);
			if(ArrayUtils.contains(betNum,dxds.toString())){
				winNum = 1;
				result.setWinCode(dxds.toString());
			}
			break;
		case "bjkl8_hezhiwx":
			String resWuXing = processWUXING(sum);
			if(!"".equals(resWuXing)){
				if(ArrayUtils.contains(betNum,resWuXing)){
					winNum = 1;
					result.setWinCode(resWuXing);
				}
			}
			break;
		default:
			break;
		}
		result.setPlayId(playId);
		result.setWinNum(winNum);
		return result;
	}
	
	/**
	 * 判断和值 单双
	 * @param sum
	 * @return
	 */
	public String processDS(int sum){
		 if(sum % 2 == 0){
			 return HConstants.TicketSeriesType.KL8_HZSHUANG;
		 }else{
			 return HConstants.TicketSeriesType.KL8_HZDAN;
		 }
	}
	
	/**
	 * 判断和值大、和、小，
	 * @param sum
	 * @return
	 */
	public String processDHX(int sum) {
		if (sum >= HConstants.TicketSeriesType.HZX_SRART 
				&& sum <= HConstants.TicketSeriesType.HZX_END) {
			return HConstants.TicketSeriesType.KL8_HZXIAO;
		}
		if (sum == HConstants.TicketSeriesType.HZH) {
			return HConstants.TicketSeriesType.KL8_HZHE;
		}
		if (sum >= HConstants.TicketSeriesType.HZD_SRART 
				&& sum <= HConstants.TicketSeriesType.HZD_END) {
			return HConstants.TicketSeriesType.KL8_HZDA;
		}
		return "";
	}
	
	/**
	 * 判断开奖号码 奇、偶、和 个数
	 */
	public String processJOH(String [] openNum){
		int odd = 0;//奇
		int even = 0;//偶
		for (int i = 0; i < openNum.length; i++) {
			if (Integer.parseInt(openNum[i]) % 2 == 0) {
				even++;
			} else {
				odd++;
			}
		}
		if(odd > even){
			return HConstants.TicketSeriesType.KL8_HZJI;
		}else if(even > odd){
			return HConstants.TicketSeriesType.KL8_HZOU;
		}else{
			return HConstants.TicketSeriesType.KL8_HZHE;
		}
	}
	
	/**
	 * 判断开奖号码上下盘个数
	 */
	public String processSX(String [] openNum){
		int up = 0;//上
		int down = 0;//下
		for (int i = 0; i < openNum.length; i++) {
			if (Integer.parseInt(openNum[i]) >= 40) {
				down ++;
			} else {
				up ++;
			}
		}
		if(up > down){
			return HConstants.TicketSeriesType.KL8_HZSHANG;
		}else if(down > up){
			return HConstants.TicketSeriesType.KL8_HZXIA;
		}else{
			return HConstants.TicketSeriesType.KL8_HZZHONG;
		}
	}
	
	/**
	 * 快乐8 和值五行玩法
	 * @param sum
	 * @return
	 */
	public String processWUXING(int sum){
		if(sum >= HConstants.TicketSeriesType.KL8_WX_JIN_START 
				&& sum <= HConstants.TicketSeriesType.KL8_WX_JIN_END){
			return HConstants.TicketSeriesType.KL8_JIN;
		}
		if(sum >= HConstants.TicketSeriesType.KL8_WX_MU_START 
				&& sum <= HConstants.TicketSeriesType.KL8_WX_MU_END){
			return HConstants.TicketSeriesType.KL8_MU;
		}
		if(sum >= HConstants.TicketSeriesType.KL8_WX_SHUI_START 
				&& sum <= HConstants.TicketSeriesType.KL8_WX_SHUI_END){
			return HConstants.TicketSeriesType.KL8_SHUI;
		}
		if(sum >= HConstants.TicketSeriesType.KL8_WX_HUO_START
				&& sum <= HConstants.TicketSeriesType.KL8_WX_HUO_END){
			return HConstants.TicketSeriesType.KL8_HUO;
		}
		if(sum >= HConstants.TicketSeriesType.KL8_WX_TU_START 
				&& sum <= HConstants.TicketSeriesType.KL8_WX_TU_END){
			return HConstants.TicketSeriesType.KL8_TU;
		}
		return "";
	}

//	public static void main(String[] args) {
//		String betNum = "金|木|水|火|土";
//		String openCode = "06,18,20,21,23,24,30,33,37,38,45,46,48,49,50,54,58,71,76,80";
//		int offset[] = new int[] { 0, 1, 2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
//
//		QWKL8PlayHandler hd = new QWKL8PlayHandler("bjkl8_hezhiwx");
//		String [] open = hd.getBetNums(betNum);
//		System.out.println(open.length);
//		List<WinResult> result = hd.calculateWinNum(betNum, openCode);
//
//		String[] openCodeArray = hd.getOpenNums(openCode);
//		StringBuffer sbf = new StringBuffer();
//		for (int i = 0; i < openCodeArray.length; i++) {
//			sbf.append(openCodeArray[i]).append(",");
//		}
//		System.out.println("开奖号码为：" + sbf.toString().substring(0, sbf.toString().length() - 1));
//
//		if (result.size() <= 0) {
//			System.out.println("错误-----");
//		} else {
//			System.out.println("中奖注数：" + result.get(0).getWinNum() + ",玩法ID为：" + result.get(0).getPlayId() + ",中奖号码:"
//					+ result.get(0).getWinCode());
//		}
//	}
}
