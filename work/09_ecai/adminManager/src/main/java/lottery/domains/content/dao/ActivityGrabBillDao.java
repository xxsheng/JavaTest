package lottery.domains.content.dao;


import java.util.List;
import java.util.Map;
import javautils.jdbc.PageList;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import lottery.domains.content.entity.ActivityGrabBill;

public interface ActivityGrabBillDao {

	boolean add(ActivityGrabBill entity);
	
	ActivityGrabBill get(int userId);
	
	public Map<String,Integer> getByPackageGroup();
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	/**
	 * 查询某天发放红包总额，不给时间查询所有红包总额
	 * @param date
	 * @return
	 */
	public Double getOutAmount(String date);
	
	public double getGrabTotal(String sTime, String eTime);
}