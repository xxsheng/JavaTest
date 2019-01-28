package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.VipFreeChips;

public interface VipFreeChipsDao {
	
	boolean add(VipFreeChips entity);
	
	VipFreeChips getById(int id);
	
	List<VipFreeChips> getUntreated();
	
	boolean hasRecord(int userId, String sTime, String eTime);
	
	boolean update(VipFreeChips entity);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

}