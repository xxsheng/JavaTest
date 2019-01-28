package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserGameDividendBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserGameDividendBillDao {
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

	/**
	 * 统计总金额
	 */
	double sumUserAmount(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount,
						 Integer status);

	List<UserGameDividendBill> findByCriteria(List<Criterion> criterions, List<Order> orders);

	UserGameDividendBill getById(int id);

	boolean add(UserGameDividendBill dividendBill);

	boolean update(int id, int status, double userAmount, String remarks);

	/**
	 * 删除分红数据，物理删除数据
	 */
	boolean del(int id);
}