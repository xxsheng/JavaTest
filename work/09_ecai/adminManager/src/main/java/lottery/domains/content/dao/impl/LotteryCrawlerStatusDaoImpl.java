package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.LotteryCrawlerStatusDao;
import lottery.domains.content.entity.LotteryCrawlerStatus;

@Repository
public class LotteryCrawlerStatusDaoImpl implements LotteryCrawlerStatusDao {

	private final String tab = LotteryCrawlerStatus.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<LotteryCrawlerStatus> superDao;
	
	@Override
	public List<LotteryCrawlerStatus> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}
	
	@Override
	public LotteryCrawlerStatus get(String shortName) {
		String hql = "from " + tab + " where shortName = ?0";
		Object[] values = {shortName};
		return (LotteryCrawlerStatus) superDao.unique(hql, values);
	}

	@Override
	public boolean update(LotteryCrawlerStatus entity) {
		return superDao.update(entity);
	}

}