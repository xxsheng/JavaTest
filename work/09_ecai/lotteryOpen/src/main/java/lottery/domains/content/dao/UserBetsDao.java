package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBets;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserBetsDao {
	boolean add(UserBets userBets);

	UserBets getById(int id);

	UserBets getById(int id, int userId);

	List<UserBets> getByChaseBillno(String chaseBillno, int userId, String winExpect);

	List<UserBets> getByFollowBillno(String followBillno);

	boolean updateToPlan(int id, int type, String planBillno);

	List<UserBets> list(List<Criterion> criterions, List<Order> orders);

	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

	boolean updateStatus(int id, int status, String openCode, double prizeMoney, String prizeTime);

	boolean cancel(int id);

	boolean cancelAndSetOpenCode(int id, String openCode);

	List<?> sumUserProfitGroupByUserId(int lotteryId, String startTime, String endTime, List<Integer> userIds);

	double[] sumProfit(int lotteryId, String startTime, String endTime);

	List<UserBets> getLatest(int userId, int lotteryId, int count);

	List<UserBets> getByExpect(int lotteryId, String expect);

	List<UserBets> getByUserIdAndExpect(int userId, int lotteryId, String expect);
	
	List<UserBets> getNoDemoUserBetsByExpect(String hsql ,Object[] values);
}