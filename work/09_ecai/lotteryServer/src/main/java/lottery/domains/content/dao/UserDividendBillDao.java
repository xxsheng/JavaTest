package lottery.domains.content.dao;

import lottery.domains.content.entity.UserDividendBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserDividendBillDao {
	List<UserDividendBill> findByCriteria(List<Criterion> criterions, List<Order> orders);

	UserDividendBill getById(int id);

	boolean updateStatus(int id, int status);

	/**
	 * 修改状态
	 */
	boolean updateStatus(int id, int status, String remarks);

	boolean update(int id, int status, double availableAmount, double totalReceived);

	double getTotalUnIssue(int userId);

	boolean addAvailableMoney(int id, double money);

	boolean addTotalReceived(int id, double money);

	boolean addLowerPaidAmount(int id, double money);
}