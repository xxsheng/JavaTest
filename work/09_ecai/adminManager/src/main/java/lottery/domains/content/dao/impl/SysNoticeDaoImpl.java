package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
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
		Object[] values = { id };
		return (SysNotice) superDao.unique(hql, values);
	}

	@Override
	public boolean add(SysNotice entity) {
		return superDao.save(entity);
	}

	@Override
	public boolean update(SysNotice entity) {
		return superDao.update(entity);
	}

	@Override
	public boolean delete(int id) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = { id };
		return superDao.delete(hql, values);
	}

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(SysNotice.class, criterions, orders, start, limit);
	}

}