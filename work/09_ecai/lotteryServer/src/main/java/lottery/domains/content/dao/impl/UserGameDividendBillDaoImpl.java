package lottery.domains.content.dao.impl;

import javautils.date.Moment;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserGameDividendBillDao;
import lottery.domains.content.entity.UserGameDividendBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserGameDividendBillDaoImpl implements UserGameDividendBillDao {
	private final String tab = UserGameDividendBill.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserGameDividendBill> superDao;

	@Override
	public UserGameDividendBill getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (UserGameDividendBill) superDao.unique(hql, values);
	}

	@Override
	public boolean updateStatus(int id, int status, int beforeStatus) {
		String hql = "update " + tab + " set status = ?0, collectTime = ?1 where id = ?2 and status=?3";
		Object[] values = {status, new Moment().toSimpleTime(), id, beforeStatus};
		return superDao.update(hql, values);
	}
}