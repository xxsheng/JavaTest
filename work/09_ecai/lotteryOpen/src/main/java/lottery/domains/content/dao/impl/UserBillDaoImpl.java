package lottery.domains.content.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.jdbc.PageList;

import javautils.jdbc.hibernate.HibernateSuperDao;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.UserBillDao;
import lottery.domains.content.entity.UserBill;

@Repository
public class UserBillDaoImpl implements UserBillDao {
	
	private final String tab = UserBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserBill> superDao;

	@Override
	public boolean add(UserBill entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean addBills(List<UserBill> userBills) {
		String sql = "insert into `user_bill`(`billno`, `user_id`, `account`, `type`, `money`, `before_money`, " +
				"`after_money`, `ref_type`, `ref_id`, `time`, `remarks`) " +
				"values(?,?,?,?,?,?,?,?,?,?,?)";
		List<Object[]> params = new ArrayList<Object[]>();
		for (UserBill tmp : userBills) {
			Object[] param = {tmp.getBillno(), tmp.getUserId(), tmp.getAccount(), tmp.getType(), tmp.getMoney(), tmp.getBeforeMoney(),
					tmp.getAfterMoney(), tmp.getRefType(), tmp.getRefId(), tmp.getTime(), tmp.getRemarks()};
			params.add(param);
		}
		return superDao.doWork(sql, params);
	}

	@Override
	public UserBill getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserBill) superDao.unique(hql, values);
	}
	
	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(UserBill.class, propertyName, criterions, orders, start, limit);
	}

	@Override
	public int clearUserBillNegateBeforeMoney() {
		String hql = "update " + tab + " set beforeMoney = 0 where beforeMoney < 0";
		return superDao.updateByHql(hql);
	}

	@Override
	public int clearUserBillNegateAfterMoney() {
		String hql = "update " + tab + " set afterMoney = 0 where afterMoney < 0";
		return superDao.updateByHql(hql);
	}
}