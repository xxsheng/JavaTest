package lottery.domains.content.dao;

import lottery.domains.content.entity.UserBetsRisk;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserBetsRiskDao {
	List<UserBetsRisk> list(List<Criterion> criterions, List<Order> orders);

	boolean updateStatus(int id, int fromStatus, int toStatus, String openCode, double prizeMoney, String prizeTime, int winNum);
}