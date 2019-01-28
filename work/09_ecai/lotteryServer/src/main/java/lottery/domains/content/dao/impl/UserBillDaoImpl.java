package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBillDao;
import lottery.domains.content.entity.UserBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserBillDaoImpl implements UserBillDao {
	
	private final String tab = UserBill.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<UserBill> superDao;

	@Override
	public boolean add(UserBill entity) {
		return superDao.save(entity);
	}
}