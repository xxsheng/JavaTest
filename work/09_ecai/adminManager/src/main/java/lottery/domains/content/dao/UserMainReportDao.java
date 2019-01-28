package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.bill.UserMainReportVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import lottery.domains.content.entity.UserMainReport;

public interface UserMainReportDao {
	
	boolean add(UserMainReport entity);
	
	UserMainReport get(int userId, String time);
	
	List<UserMainReport> list(int userId, String sTime, String eTime);
	
	boolean update(UserMainReport entity);
	
	List<UserMainReport> find(List<Criterion> criterions, List<Order> orders);

	/**
	 * 统计自己及下级
	 */
	UserMainReportVO sumLowersAndSelf(int userId, String sTime, String eTime);


}