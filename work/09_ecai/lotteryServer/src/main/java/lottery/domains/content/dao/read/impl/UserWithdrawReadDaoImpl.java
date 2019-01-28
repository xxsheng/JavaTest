package lottery.domains.content.dao.read.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserWithdrawReadDao;
import lottery.domains.content.entity.UserWithdraw;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserWithdrawReadDaoImpl implements UserWithdrawReadDao {
	
	private final String tab = UserWithdraw.class.getSimpleName();
	
	@Autowired
	private HibernateSuperReadDao<UserWithdraw> superDao;

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserWithdraw.class, propertyName, criterions, orders, start, limit);
	}
	
}