package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.HistoryUserBets;
import lottery.domains.content.entity.UserBets;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserBetsDao {
	
	boolean updateStatus(int id, int status, String codes, String openCode, double prizeMoney, String prizeTime);
	
	boolean updateLocked(int id, int locked);
	
	UserBets getById(int id);
	/**
	 * 获取历史记录
	 */
	HistoryUserBets getHistoryById(int id);
	
	List<UserBets> getByBillno(String billno, boolean withCodes);
	/**
	 * 获取历史记录
	 */
	List<HistoryUserBets> getHistoryByBillno(String billno, boolean withCodes);
	
	UserBets getBybillno(int userId, String billno);
	
	boolean cancel(int id);
	
	boolean delete(int userId);
	
	boolean isCost(int userId);
	
	List<UserBets> getSuspiciousOrder(int userId, int multiple, boolean withCodes);
	
	List<UserBets> getByFollowBillno(String followBillno, boolean withCodes);
	
	PageList find(String sql , int start, int limit, boolean withCodes);
	
	PageList findHistory(List<Criterion> criterions, List<Order> orders, int start, int limit, boolean withCodes);
	
	List<UserBets> find(List<Criterion> criterions, List<Order> orders, boolean withCodes);
	

	
	/**
	 * 查询总的投注金额（忽略小数点），不包括撤单
	 * @param sTime
	 * @param eTime
	 * @return
	 */
	long getTotalBetsMoney(String sTime, String eTime);

	/**
	 * 查询总的订单数量，不包括撤单
	 * @param sTime
	 * @param eTime
	 * @return
	 */
	int getTotalOrderCount(String sTime, String eTime);
	
	/**
	 * 根据天显示订单数量，不包括撤单
	 * @param sTime
	 * @param eTime
	 * @return
	 */
	List<?> getDayUserBets(int[] lids, String sTime, String eTime);
	
	/**
	 * 根据天显示投注金额，不包括撤单
	 * @param sTime
	 * @param eTime
	 * @return
	 */
	List<?> getDayBetsMoney(int[] lids, String sTime, String eTime);
	
	/**
	 * 根据天显示中奖金额，不包括撤单
	 * @param sTime
	 * @param eTime
	 * @return
	 */
	List<?> getDayPrizeMoney(int[] lids, String sTime, String eTime);
	
	/**
	 * 获取彩票热门程度
	 * @param lids
	 * @param sTime
	 * @param eTime
	 * @return
	 */
	List<?> getLotteryHot(int[] lids, String sTime, String eTime);

	/**
	 * 投注盈亏报表
	 */
	List<?> report(List<Integer> lotteries, Integer ruleId, String sTime, String eTime);
	
	
	int countUserOnline(String time);

	/**
	 * 第一位是总投注(money)，第二位是总奖金(prizeMoney)
	 * @param locked 
	 */
	double[] getTotalMoney(String keyword, Integer userId,  Integer uype, Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
						   String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
						   Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status, Integer locked, String ip);
	
	
	double[] getHistoryTotalMoney(String keyword, Integer userId, Integer uype, Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime,
			   String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple,
			   Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status, Integer locked);
	
	/**
	 * 获取用户时间段内的消费金额
	 */
	double getBillingOrder(int userId, String startTime, String endTime);
	
}