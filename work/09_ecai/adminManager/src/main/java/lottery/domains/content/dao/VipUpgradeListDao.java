package lottery.domains.content.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.VipUpgradeList;

public interface VipUpgradeListDao {
	
	boolean add(VipUpgradeList entity);
	
	boolean hasRecord(int userId, String month);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

}