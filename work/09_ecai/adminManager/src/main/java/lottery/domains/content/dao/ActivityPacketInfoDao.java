package lottery.domains.content.dao;

import java.util.List;
import java.util.Map;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.ActivityPacketInfo;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public interface ActivityPacketInfoDao {
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	boolean save(ActivityPacketInfo entity);
	
	List<Map<Integer, Double>> statTotal();
	
}