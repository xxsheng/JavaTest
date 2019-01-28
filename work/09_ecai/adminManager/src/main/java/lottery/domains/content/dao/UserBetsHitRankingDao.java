package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserBetsHitRanking;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface UserBetsHitRankingDao {

	UserBetsHitRanking getById(int id);
	
	boolean add(UserBetsHitRanking entity);
	
	boolean update(UserBetsHitRanking entity);
	
	boolean delete(int id);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
}