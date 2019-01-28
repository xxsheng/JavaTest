package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.HistoryUserWithdraw;
import lottery.domains.content.entity.UserWithdraw;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserWithdrawDao {
	
	boolean update(UserWithdraw entity);

	int getWaitTodo();
	
	UserWithdraw getById(int id);
	
	HistoryUserWithdraw getHistoryById(int id);
	
	UserWithdraw getByBillno(String billno);
	
	List<UserWithdraw> listByOperatorTime(String sDate, String eDate);

	List<UserWithdraw> listByRemitStatus(int[] remitStatuses, boolean third, String sTime, String eTime);
	
	List<UserWithdraw> getLatest(int userId, int status, int count);
	
	PageList find(String sql, int start, int limit);
	
	PageList findHistory(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	double getTotalWithdraw(String sTime, String eTime);

	/**
	 * 总提款订单数/总提款金额
	 * @param sTime 开始时间
	 * @param eTime 结束时间
	 * @return
	 */
	Object[] getTotalWithdrawData(String sTime, String eTime);

	/**
	 * 第一位是总提现(recMoney)，第二位是总手续费(feeMoney)
	 */
	double[] getTotalWithdraw(String billno, Integer userId, String minTime, String maxTime, String minOperatorTime, String maxOperatorTime, Double minMoney, Double maxMoney, String keyword, Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId);
	
	/**
	 * 第一位是总提现(recMoney)，第二位是总手续费(feeMoney)
	 */
	double[] getHistoryTotalWithdraw(String billno, Integer userId, String minTime, String maxTime, String minOperatorTime, String maxOperatorTime, Double minMoney, Double maxMoney, String keyword, Integer status, Integer checkStatus, Integer remitStatus, Integer paymentChannelId);
	
	double getTotalAutoRemit(String sTime, String eTime);
	
	double getTotalFee(String sTime, String eTime);
	
	List<?> getDayWithdraw(String sTime, String eTime);

	/**
	 * 与getDayWithdraw不同的时，这里包含总数，即day,count,sum
	 */
	List<?> getDayWithdraw2(String sTime, String eTime);
	
	boolean lock(String billno, String operatorUser, String operatorTime);
	
	boolean unlock(String billno, String operatorUser);

}