package lottery.domains.pool.play.impl;

import java.util.Arrays;
import java.util.Collections;

import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

public class ZXHZPlayHanlder implements ITicketPlayHandler {

	private int[] offsets;
	/** 组三 */
	private String playId0;
	/** 组六 */
	private String playId1;
	
	public ZXHZPlayHanlder(String playId0, String playId1, int[] offsets) {
		this.playId0 = playId0;
		this.playId1 = playId1;
		this.offsets = offsets;
	}
	
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.trim().split(",");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		String[] open = TicketPlayUtils.getOpenNums(openNums, offsets);
		Arrays.sort(open);
		return open;
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] openNum = getOpenNums(openNums);
		if(playId0.equals(playId1) && (playId0.equalsIgnoreCase("1_exzuxhzh") || playId0.equals("1_exzuxhzq"))){
			result.setPlayId(playId0);
			int winNum = ssczuxhz2x(index, openNum);
			result.setWinNum(winNum);
		}else{
			int winNum = ssczuxhz3x(index, openNum);
			String[] fixedOpenNum = TicketPlayUtils.getFixedNums(openNum);
			if (openNum.length == fixedOpenNum.length) {
				result.setPlayId(playId1);
				result.setGroupType(2);
			} else {
				result.setPlayId(playId0);
				result.setGroupType(1);
			}
			
			if(1 == winNum){
				result.setWinNum(1);
			}else if(2 == winNum){
				result.setWinNum(1);
			}
		}
		return result;
	}
	
	private Integer ssczuxhz2x(String betCode, String[] codes) {
		Integer result = 0;
		String sum = String.valueOf(sum(codes, true));
		String[] betCodes = betCode.split(",");
		
		//判断是否有重复号码
		int dbc = Collections.frequency(Arrays.asList(codes), codes[0]);
		if (dbc == 2) {
			return result;
		}
		
		for (String bc : betCodes) {
			if (Integer.valueOf(bc) == Integer.valueOf(sum)) {
				result = 1;
				break;
			}
		}
		return result;
	}
	
	private Integer ssczuxhz3x(String betCode, String[] codes) {
		Integer result = 0;
		String sum = String.valueOf(sum(codes, true));
		String[] betCodes = betCode.split(",");
		//判断是否有重复号码
		int dbc = Collections.frequency(Arrays.asList(codes), codes[0]);
		if (dbc == 3) {
			return result;
		}
		for (String bc : betCodes) {
			if (Integer.valueOf(bc) == Integer.valueOf(sum)) {
				boolean isone = false;
				for (String c : codes) {
					dbc = Collections.frequency(Arrays.asList(codes), c);
					if(dbc > 1){
						isone = true;
						break;
					}
				}
				
				//验证中组三还是组六
				if(isone){
					result = 1;
					break;
				}else{
					result = 2;
					break;
				}
			}
		}
		return result;
	}
	
	private int sum(String[] codes, boolean leopard) {
		// 验证豹子
		if (leopard && Collections.frequency(Arrays.asList(codes), codes[0]) == codes.length) {
			return -1;
		}
		int result = 0;
		for (int i = 0; i < codes.length; i++) {
			result += Integer.parseInt(codes[i]);
		}
		return result;
	}

	public static void main(String[] args) {
		ZXHZPlayHanlder test = new ZXHZPlayHanlder("1_sxzuxzsq","1_sxzuxzlq",ITicketPlayHandler.OFFSETS_QIANSAN);
		WinResult r1= test.calculateWinNum(11, "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26", "9,8,1");
		WinResult r2 = test.calculateWinNum(11, "2,3", "1,1,0");
		System.out.println(r1.getPlayId() + "     " + r1.getWinNum());
		System.out.println(r2.getPlayId() + "     " + r2.getWinNum());
		
		ZXHZPlayHanlder test2 = new ZXHZPlayHanlder("1_exzuxhzq","1_exzuxhzq",ITicketPlayHandler.OFFSETS_QIANSAN);
		WinResult r3= test2.calculateWinNum(11, "2,3", "1,2");
		System.out.println(r3.getPlayId() + "     " + r3.getWinNum());
	}
}
