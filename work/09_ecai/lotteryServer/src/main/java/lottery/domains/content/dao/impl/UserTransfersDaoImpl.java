package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserTransfersDao;
import lottery.domains.content.entity.UserTransfers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserTransfersDaoImpl implements UserTransfersDao {

	@Autowired
	private HibernateSuperDao<UserTransfers> superDao;

	@Override
	public boolean add(UserTransfers entity) {
		return superDao.save(entity);
	}
}