package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.LotteryOpenTime;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

public interface LotteryOpenTimeDao {
	
	List<LotteryOpenTime> listAll();
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
}
