package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.LotteryOpenTime;

public interface LotteryOpenTimeDao {
	
	List<LotteryOpenTime> listAll();
	
	List<LotteryOpenTime> list(String lottery);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	LotteryOpenTime getById(int id);

	LotteryOpenTime getByLottery(String lottery);

	boolean update(LotteryOpenTime entity);
	
	boolean save(LotteryOpenTime entity);
	
}
