package lottery.domains.pool.play.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javautils.StringUtil;
import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

public class RXTD115PlayHandler implements ITicketPlayHandler {
	private String playId;
	/** 中奖个数 */
	private int winCount;
	/**有效投注号码个数**/
	private int betCount;
	/** 开奖号码中用于开奖计算的号码位置 */
	private int[] offsets;
	
	public RXTD115PlayHandler(String playId,int winCount, int betCount, int[] offsets) {
		this.playId= playId;
		this.winCount = winCount;
		this.offsets = offsets;
		this.betCount = betCount;
	}
	@Override
	public String[] getBetNums(String betNums) {
		return betNums.trim().split(",");
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, offsets);
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String index, String openNums) {
		WinResult result = new WinResult();
		String[] nums = getBetNums(index);
		String[] openNum = getOpenNums(openNums);
		if (nums == null || openNum == null || openNum.length != offsets.length) {
			return result;
		}
		result.setPlayId(playId);
		List<Integer> wins = zuxuantd(index.trim(), openNums, winCount, betCount);
		result.setWinNum(wins.size());
		return result;
	}
	
	public static void main(String[] args) {
		RXTD115PlayHandler test = new RXTD115PlayHandler("2_rxtd3", 3, 3,  ITicketPlayHandler.OFFSETS_WUXIN);
		long s = System.currentTimeMillis();
//		for (int i = 0; i < 10000; i++) {
			WinResult result = test.calculateWinNum(11, "02,07 08 09 10 11", "11,02,08,07,05");
			System.out.println(result.getWinNum());
//		}
		long e = System.currentTimeMillis();
		System.out.println(e-s);
	}
	
	private List<Integer> zuxuantd(String betCode, String openCode, int count, int length) {
		List<Integer> result = new ArrayList<Integer>();
		String[] betCodes = betCode.split(",");
		String[] dCodes = betCodes[0].split(" ");
		String[] tCodes = betCodes[1].split(" ");

		int check = 0;

		//验证胆码，胆码必须中
		for (int i = 0; i < dCodes.length; i++) {
			if (StringUtil.isNotNull(dCodes[i].trim()) && openCode.contains(dCodes[i].trim())) {
				check++;
			}
		}
		if(check != dCodes.length && dCodes.length < 6){
			return result;
		}else if(dCodes.length > 5 && check < 5){//任7 任8 
			return result;
		}
		
		int codeLength = length - dCodes.length;
		Set<String[]> set = doSet11x5(tCodes, codeLength);

		// 组合投注号码
		List<String> clist = new ArrayList<String>();
		for (String[] str : set) {
			String code = "";
			for (int i = 0; i < str.length; i++) {
				code+= str[i]+" ";
			}
			code += betCodes[0];
			clist.add(code);
		}
		
		check = 0;
		for (String str : clist) {
			String[] c = str.split(" ");
			for (int i = 0; i < c.length; i++) {
				if (StringUtil.isNotNull(c[i].trim()) && openCode.contains(c[i].trim())) {
					check++;
				}
			}
			if (check >= count) {
				result.add(1);
			}
			check = 0;
		}
		return result;
	}
	
	private Set<String[]> doSet11x5(String[] a, int m) {
		Set<String[]> result = new HashSet<String[]>();
		int n = a.length;
		if (m >= n) {
			result.add(a);
			return result;
		}

		int[] bs = new int[n];
		for (int i = 0; i < n; i++) {
			bs[i] = 0;
		}
		// 初始化
		for (int i = 0; i < m; i++) {
			bs[i] = 1;
		}
		boolean flag = true;
		boolean tempFlag = false;
		int pos = 0;
		int sum = 0;
		// 首先找到第一个10组合，然后变成01，同时将左边所有的1移动到数组的最左边
		do {
			sum = 0;
			pos = 0;
			tempFlag = true;
			result.add(print(bs, a, m));

			for (int i = 0; i < n - 1; i++) {
				if (bs[i] == 1 && bs[i + 1] == 0) {
					bs[i] = 0;
					bs[i + 1] = 1;
					pos = i;
					break;
				}
			}
			// 将左边的1全部移动到数组的最左边

			for (int i = 0; i < pos; i++) {
				if (bs[i] == 1) {
					sum++;
				}
			}
			for (int i = 0; i < pos; i++) {
				if (i < sum) {
					bs[i] = 1;
				} else {
					bs[i] = 0;
				}
			}

			// 检查是否所有的1都移动到了最右边
			for (int i = n - m; i < n; i++) {
				if (bs[i] == 0) {
					tempFlag = false;
					break;
				}
			}
			if (tempFlag == false) {
				flag = true;
			} else {
				flag = false;
			}

		} while (flag);
		result.add(print(bs, a, m));
		return result;
	}
	
	private String[] print(int[] bs, String[] a, int m) {
		String[] result = new String[m];
		int pos = 0;
		for (int i = 0; i < bs.length; i++) {
			if (bs[i] == 1) {
				result[pos] = a[i];
				pos++;
			}
		}
		return result;
	}

}
