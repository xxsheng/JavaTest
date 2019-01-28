package lottery.domains.content.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.SysCodeAmountDao;
import lottery.domains.content.entity.SysCodeAmount;

@Repository
public class SysCodeAmountDaoImpl implements SysCodeAmountDao {

	private final String tab = SysCodeAmount.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<SysCodeAmount> superDao;
	
	@Override
	public List<SysCodeAmount> listAll() {
		String hql = "from " + tab + " order by code asc";
		return superDao.list(hql);
	}

}