package lottery.domains.content.biz;

import java.util.List;
import java.util.Map;

import javautils.jdbc.PageList;

public interface ActivityPacketService {
	
	/**
	 * 查询领取红包记录
	 * @param username
	 * @param date
	 * @param start
	 * @param limit
	 * @return
	 */
	PageList searchBill(String username, String date, int start, int limit);
	
	/**
	 * 查询发送红包记录
	 * @param username
	 * @param date
	 * @param start
	 * @param limit
	 * @return
	 */
	PageList searchPacketInfo(String username, String date, String type, int start, int limit);
	
	/**
	 * 根据总额和数量生成红包
	 * @param count
	 * @param amount
	 * @return
	 */
	public boolean generatePackets(int count, double amount);
	
	/**
	 * 统计 系统红包、用户红包总额
	 * @return
	 */
	List<Map<Integer, Double>> statTotal();
	
}