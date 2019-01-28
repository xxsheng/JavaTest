package lottery.domains.content.dao.read.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserTransfersReadDao;
import lottery.domains.content.entity.UserTransfers;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserTransfersReadDaoImpl implements UserTransfersReadDao {

	@Autowired
	private HibernateSuperReadDao<UserTransfers> superDao;

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(UserTransfers.class, criterions, orders, start, limit);
	}
	
}