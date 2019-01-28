package lottery.domains.content.dao;

import java.util.List;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserSecurity;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public interface UserSecurityDao {
	
	UserSecurity getById(int id);
	
	List<UserSecurity> getByUserId(int userId);
	
	boolean delete(int userId);
	
	PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit);

	boolean updateValue(int id, String md5Value);
}