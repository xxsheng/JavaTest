package lottery.domains.content.dao;

import lottery.domains.content.entity.UserBets;

import java.util.List;

public interface UserBetsDao {
	boolean add(UserBets entity);

	boolean cancel(int id, int userId);

	/**
	 * 根据追单号获取所有投注信息，列表不返回投注号码
	 */
	List<UserBets> getByChaseBillno(String chaseBillno, int userId);
}