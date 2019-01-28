package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.LotteryOpenCode;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public interface LotteryOpenCodeDao {
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	LotteryOpenCode get(String lottery, String expect);
	
	boolean add(LotteryOpenCode entity);
	
	List<LotteryOpenCode> list(String lottery, String[] expects);
	
	boolean update(LotteryOpenCode entity);

	boolean delete(LotteryOpenCode bean);

	int countByInterfaceTime(String lottery, String startTime, String endTime);

	LotteryOpenCode getFirstExpectByInterfaceTime(String lottery, String startTime, String endTime);

	List<LotteryOpenCode> getLatest(String lottery, int count);
	
}