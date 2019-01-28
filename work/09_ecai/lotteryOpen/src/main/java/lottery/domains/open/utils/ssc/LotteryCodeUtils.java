package lottery.domains.open.utils.ssc;

import javautils.array.ArrayUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 时时彩复式 Created by Nick on 2016/11/25.
 */
@Component
public class LotteryCodeUtils implements InitializingBean {
	private static LinkedList<String> CODES_SSC = new LinkedList<>();
	private static LinkedList<String> CODES_11X5 = new LinkedList<>();
	private static LinkedList<String> CODES_K3 = new LinkedList<>();
	private static LinkedList<String> CODES_3D = new LinkedList<>();
	private static LinkedList<String> CODES_PK10 = new LinkedList<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		generateSSCCodes(); // 时时彩
		generate11X5Codes(); // 11选5
		generateK3Codes(); // 快3
		generate3DCodes(); // 3D
		generatepk10Codes(); // PK10
	}

	public LinkedList<String> getSSCCodes() {
		LinkedList<String> result = new LinkedList<>();
		result.addAll(CODES_SSC);
		return result;
	}

	/**
	 * 生成时时彩随机开奖号码
	 */
	public String randomSSC() {
		Random random = new Random();
		int index = random.nextInt(CODES_SSC.size());
		return CODES_SSC.get(index);
	}

	public LinkedList<String> get11X5Codes() {
		LinkedList<String> result = new LinkedList<>();
		result.addAll(CODES_11X5);
		return result;
	}

	/**
	 * 生成11和选5随机开奖号码
	 */
	public String random11X5() {
		Random random = new Random();
		int index = random.nextInt(CODES_11X5.size());
		return CODES_11X5.get(index);
	}

	public LinkedList<String> getK3Codes() {
		LinkedList<String> result = new LinkedList<>();
		result.addAll(CODES_K3);
		return result;
	}

	/**
	 * 生成快3随机开奖号码
	 */
	public String randomK3() {
		Random random = new Random();
		int index = random.nextInt(CODES_K3.size());
		return CODES_K3.get(index);
	}

	public LinkedList<String> get3DCodes() {
		LinkedList<String> result = new LinkedList<>();
		result.addAll(CODES_3D);
		return result;
	}

	/**
	 * 生成3D随机开奖号码
	 */
	public String random3D() {
		Random random = new Random();
		int index = random.nextInt(CODES_3D.size());
		return CODES_3D.get(index);
	}

	/**
	 * 生成PK10随机开奖号码
	 */
	public String randompk10() {
		Random random = new Random();
		int index = random.nextInt(CODES_PK10.size());
		return CODES_PK10.get(index);
	}

	public LinkedList<String> getpk10Codes() {
		LinkedList<String> result = new LinkedList<>();
		result.addAll(CODES_PK10);
		return result;
	}

	/**
	 * 生成时时彩10万注开奖号码
	 */
	private void generateSSCCodes() {
		CODES_SSC = new LinkedList<>();
		LinkedHashSet<String> codesSet = new LinkedHashSet<>();

		String[] numCodes = "0,1,2,3,4,5,6,7,8,9".split(",");

		for (int i = 0; i < numCodes.length; i++) {
			for (int j = 0; j < numCodes.length; j++) {
				for (int k = 0; k < numCodes.length; k++) {
					for (int l = 0; l < numCodes.length; l++) {
						for (int m = 0; m < numCodes.length; m++) {
							String num = numCodes[i] + "," + numCodes[j] + "," + numCodes[k] + "," + numCodes[l] + ","
									+ numCodes[m];
							codesSet.add(num);
						}
					}
				}
			}
		}

		CODES_SSC.addAll(codesSet);
		codesSet.clear();
	}

	/**
	 * 生成11选5共55440注开奖号码
	 */
	private void generate11X5Codes() {
		CODES_11X5 = new LinkedList<>();
		LinkedHashSet<String> codesSet = new LinkedHashSet<>();

		String[] numCodes = "01,02,03,04,05,06,07,08,09,10,11".split(",");

		for (int i = 0; i < numCodes.length; i++) {
			for (int j = 0; j < numCodes.length; j++) {
				for (int k = 0; k < numCodes.length; k++) {
					for (int l = 0; l < numCodes.length; l++) {
						for (int m = 0; m < numCodes.length; m++) {

							String num1 = numCodes[i];
							String num2 = numCodes[j];
							String num3 = numCodes[k];
							String num4 = numCodes[l];
							String num5 = numCodes[m];

							String[] nums = new String[] { num1, num2, num3, num4, num5 };

							if (!ArrayUtils.hasSame(nums)) {
								String num = num1 + "," + num2 + "," + num3 + "," + num4 + "," + num5;
								codesSet.add(num);
							}
						}
					}
				}
			}
		}

		CODES_11X5.addAll(codesSet);
		codesSet.clear();
	}

	/**
	 * 生成快3共216注开奖号码
	 */
	private void generateK3Codes() {
		CODES_K3 = new LinkedList<>();
		LinkedHashSet<String> codesSet = new LinkedHashSet<>();

		String[] numCodes = "1,2,3,4,5,6".split(",");

		for (int i = 0; i < numCodes.length; i++) {
			for (int j = 0; j < numCodes.length; j++) {
				for (int k = 0; k < numCodes.length; k++) {
					String num = numCodes[i] + "," + numCodes[j] + "," + numCodes[k];
					codesSet.add(num);
				}
			}
		}

		CODES_K3.addAll(codesSet);
		codesSet.clear();
	}

	/**
	 * 生成3D 1000注开奖号码
	 */
	private void generate3DCodes() {
		CODES_3D = new LinkedList<>();
		LinkedHashSet<String> codesSet = new LinkedHashSet<>();

		String[] numCodes = "0,1,2,3,4,5,6,7,8,9".split(",");

		for (int i = 0; i < numCodes.length; i++) {
			for (int j = 0; j < numCodes.length; j++) {
				for (int k = 0; k < numCodes.length; k++) {
					String num = numCodes[i] + "," + numCodes[j] + "," + numCodes[k];
					codesSet.add(num);
				}
			}
		}

		CODES_3D.addAll(codesSet);
		codesSet.clear();
	}

	/**
	 * 生成PK10 1728注开奖号码
	 */
	private void generatepk10Codes() {
		CODES_PK10 = new LinkedList<>();
		LinkedHashSet<String> codesSet = new LinkedHashSet<>();

		for (int i = 1; i <= 1728; i++) {
			int[] result = new int[10];
			int count = 0;
			while (count < 10) {
				int num = (int) (Math.random() * (10 - 1 + 1)) + 1;
				boolean flag = true;
				for (int j = 0; j < 10; j++) {
					if (num == result[j]) {
						flag = false;
						break;
					}
				}
				if (flag) {
					result[count] = num;
					count++;
				}
			}
			String num = "";
			for (int j : result) {
				if (j < 10) {
					num = num + "0" + j + ",";
				} else {
					num = num + j + ",";
				}
			}
			num = num.substring(0, num.length() - 1);
			codesSet.add(num);
		}

		CODES_PK10.addAll(codesSet);
		codesSet.clear();
	}

	public static void main(String[] args) {
		LotteryCodeUtils utils = new LotteryCodeUtils();
		utils.generateSSCCodes();
		LinkedList<String> sscCodes = utils.getSSCCodes();
		System.out.println("时时彩所有开奖号码共" + sscCodes.size() + "注");
		System.out.println("时时彩所有开奖号码10线程每线程计算" + (sscCodes.size() / 10) + "注");

		utils.generate11X5Codes();
		LinkedList<String> x5Codes = utils.get11X5Codes();
		System.out.println("11选5所有开奖号码共" + x5Codes.size() + "注");
		System.out.println("11选5所有开奖号码10线程每线程使用" + (x5Codes.size() / 10) + "个线程");

		utils.generateK3Codes();
		LinkedList<String> k3Codes = utils.getK3Codes();
		System.out.println("快3所有开奖号码共" + k3Codes.size() + "注");
		System.out.println("快3所有开奖号码10线程每线程使用" + (k3Codes.size() / 10) + "个线程");

		utils.generate3DCodes();
		LinkedList<String> d3Codes = utils.get3DCodes();
		System.out.println("3D所有开奖号码共" + d3Codes.size() + "注");
		System.out.println("3D所有开奖号码10线程每线程使用" + (d3Codes.size() / 10) + "个线程");

		utils.generatepk10Codes();
		LinkedList<String> pk10Codes = utils.getpk10Codes();
		System.out.println("PK10所有开奖号码共" + pk10Codes.size() + "注");
		System.out.println("PK10所有开奖号码10线程每线程使用" + (pk10Codes.size() / 10) + "个线程");
	}

}
