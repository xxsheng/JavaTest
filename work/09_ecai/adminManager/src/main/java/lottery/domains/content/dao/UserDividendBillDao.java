package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserDividendBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserDividendBillDao {
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

	/**
	 * 查找契约数据统计数据，第一位平台总金额，第二位上级总金额
	 */
	double[] sumUserAmount(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount);

	List<UserDividendBill> findByCriteria(List<Criterion> criterions, List<Order> orders);

	/**
	 * 将未领取的记录改为过期
	 */
	boolean updateAllExpire();

	UserDividendBill getById(int id);

	UserDividendBill getByUserId(int userId, String indicateStartDate, String indicateEndDate);

	boolean add(UserDividendBill dividendBill);

	boolean addAvailableMoney(int id, double money);

	boolean addUserAmount(int id, double money);

	boolean setAvailableMoney(int id, double money);

	boolean addTotalReceived(int id, double money);

	boolean addLowerPaidAmount(int id, double money);


	boolean update(UserDividendBill dividendBill);

	/**
	 * 修改数据及状态等
	 */
	boolean update(int id, int status, String remarks);

	/**
	 * 修改数据及状态等
	 */
	boolean update(int id, int status, double availableAmount, String remarks);

	/**
	 * 删除团队分红数据，物理删除数据
	 */
	boolean del(int id);

	/**
	 * 修改状态
	 */
	boolean updateStatus(int id, int status);

	/**
	 * 修改状态
	 */
	boolean updateStatus(int id, int status, String remarks);
}