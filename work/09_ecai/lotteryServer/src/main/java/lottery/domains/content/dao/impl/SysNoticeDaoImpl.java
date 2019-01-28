package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.hibernate.HibernateSuperDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.SysNoticeDao;
import lottery.domains.content.entity.SysNotice;

@Repository
public class SysNoticeDaoImpl implements SysNoticeDao {

	private final String tab = SysNotice.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<SysNotice> superDao;
	
	@Override
	public SysNotice getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (SysNotice) superDao.unique(hql, values);
	}
	
	@Override
	public List<SysNotice> get(int count) {
		String hql = "from " + tab + " where status = 0 order by sort desc, time desc";
		return superDao.list(hql, 0, count);
	}

	@Override
	public List<SysNotice> getNoticeTitle(int count) {
		String hql = "select id,title,date from " + tab + " where status = 0 order by sort desc, time desc";
		return superDao.list(hql, 0, count);
	}

	@Override
	public List<SysNotice> getNoticeTitleLastNew(int count) {
		String hql = "from " + tab + " where status = 0 order by sort desc, time desc";
		return superDao.list(hql, 0, count);
	}
	
}