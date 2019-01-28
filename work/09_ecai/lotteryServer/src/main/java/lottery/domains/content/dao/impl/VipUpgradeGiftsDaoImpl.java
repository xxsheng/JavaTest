package lottery.domains.content.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.VipUpgradeGiftsDao;
import lottery.domains.content.entity.VipUpgradeGifts;

@Repository
public class VipUpgradeGiftsDaoImpl implements VipUpgradeGiftsDao {

	private final String tab = VipUpgradeGifts.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<VipUpgradeGifts> superDao;
	
	@Override
	public VipUpgradeGifts getByUserId(int userId) {
		String hql = "from " + tab + " where userId = ?0 and status = 1 and isReceived = 0";
		Object[] values = {userId};
		List<VipUpgradeGifts> list = superDao.list(hql, values);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public boolean updateReceived(int id, int isReceived) {
		String hql = "update " + tab + " set isReceived = ?1 where id = ?0";
		Object[] values = {id , isReceived};
		return superDao.update(hql, values);
	}

}