package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserBill;

public interface UserBillDao {
	
	boolean add(UserBill entity);

	/**
	 * 批量新增注单
	 */
	boolean addBills(List<UserBill> userBills);

	UserBill getById(int id);
	
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

	/**
	 * 清理负数之前之前金额
	 */
	int clearUserBillNegateBeforeMoney();

	/**
	 * 清理负数之前之后金额
	 */
	int clearUserBillNegateAfterMoney();
}