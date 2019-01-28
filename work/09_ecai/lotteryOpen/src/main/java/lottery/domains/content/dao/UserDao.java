package lottery.domains.content.dao;

import lottery.domains.content.entity.User;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserDao {
    /**
     * 列出用户
     */
    List<User> list(List<Criterion> criterions, List<Order> orders);

	/**
	 * 列出所有用户
	 */
	List<User> listAll();
    

	/**
	 * 更新彩票账户资金
	 */
	boolean updateLotteryMoney(int id, double lotteryAmount, double freezeAmount);

	/**
	 * 更新彩票账户资金
	 */
	boolean updateLotteryMoney(int id, double lotteryAmount);

	/**
	 * 更新彩票账户资金
	 */
	boolean updateFreezeMoney(int id, double freezeAmount);

	/**
	 * 通过id获取用户
	 */
	User getById(int id);

	/**
	 * 清理用户负数金额
	 */
	int clearUserNegateFreezeMoney();

	/**
	 * 获取用户下级代理
	 */
	List<User> getUserLower(int id);

	/**
	 * 获取直属下级
	 */
	List<User> getUserDirectLower(int id);
}