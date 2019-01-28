package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.HistoryUserRecharge;
import lottery.domains.content.entity.UserRecharge;

public interface UserRechargeDao {
	
	boolean add(UserRecharge entity);
	
	boolean update(UserRecharge entity);

	boolean updateSuccess(int id, double beforeMoney, double afterMoney, double recMoney, String payTime, String payBillno);

	UserRecharge getById(int id);
	
	HistoryUserRecharge getHistoryById(int id);
	
	UserRecharge getByBillno(String billno);
	
	boolean isRecharge(int userId);
	
	List<UserRecharge> getLatest(int userId, int status, int count);

	List<UserRecharge> list(List<Criterion> criterions, List<Order> orders);
	
	PageList  find(String sql,  int start,int  limit );
	
	/**
	 * 查询历史记录
	 * @param criterions
	 * @param orders
	 * @param start
	 * @param limit
	 * @return
	 */
	PageList findHistory(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	List<?> getDayRecharge(String sTime, String eTime);
	
	int getRechargeCount(int status,int type,int subType);
	
	double getTotalFee(String sTime, String eTime);

	double getThirdTotalRecharge(String sTime, String eTime);

	double getTotalRecharge(String sTime, String eTime, int[] type, int[] subtype, Integer payBankId);

	/**
	 * 总充值订单数/总充值金额/总充值第三方手续费
	 * @param sTime 开始时间
	 * @param eTime 结束时间
	 * @param type 类型
	 * @param subtype 子类型
	 * @return
	 */
	Object[] getTotalRechargeData(String sTime, String eTime, Integer type, Integer subtype);

	/**
	 * 按日期分组，获取总充值订单数/总充值金额/总充值第三方手续费
	 * @param sTime
	 * @param eTime
	 * @param type
	 * @param subtype
	 * @return
	 */
	List<?> getDayRecharge2(String sTime, String eTime, Integer type, Integer subtype);

	double getTotalRecharge(String billno, Integer userId, String minTime, String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId);

	double getHistoryTotalRecharge(String billno, Integer userId, String minTime, String maxTime, String minPayTime, String maxPayTime, Double minMoney, Double maxMoney, Integer status, Integer channelId);

}