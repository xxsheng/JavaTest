package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javautils.date.Moment;
import javautils.jdbc.PageList;

import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.global.DbServerSyncEnum;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.SysNoticeService;
import lottery.domains.content.dao.SysNoticeDao;
import lottery.domains.content.entity.SysNotice;

@Service
public class SysNoticeServiceImpl implements SysNoticeService {
	
	@Autowired
	private SysNoticeDao sysNoticeDao;

	@Autowired
	private DbServerSyncDao dbServerSyncDao;
	
	@Override
	public PageList search(Integer status, int start, int limit) {
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		if(status != null) {
			criterions.add(Restrictions.eq("status", status.intValue()));
		}
		orders.add(Order.desc("sort"));
		orders.add(Order.desc("time"));
		return sysNoticeDao.find(criterions, orders, start, limit);
	}

	@Override
	public boolean add(String title, String content, String simpleContent, int sort, int status, String date) {
		String time = new Moment().toSimpleTime();
		SysNotice entity = new SysNotice(title, content, simpleContent, sort, status, date, time);
		boolean added = sysNoticeDao.add(entity);
		if (added) {
			dbServerSyncDao.update(DbServerSyncEnum.SYS_NOTICE);
		}
		return added;
	}
	
	@Override
	public boolean edit(int id, String title, String content, String simpleContent, int sort, int status, String date) {
		SysNotice entity = sysNoticeDao.getById(id);
		if(entity != null) {
			entity.setTitle(title);
			entity.setContent(content);
			entity.setSimpleContent(simpleContent);
			entity.setSort(sort);
			entity.setStatus(status);
			entity.setDate(date);
			boolean updated = sysNoticeDao.update(entity);
			if (updated) {
				dbServerSyncDao.update(DbServerSyncEnum.SYS_NOTICE);
			}
			return updated;
		}
		return false;
	}

	@Override
	public boolean updateStatus(int id, int status) {
		SysNotice entity = sysNoticeDao.getById(id);
		if(entity != null) {
			entity.setStatus(status);
			boolean updated = sysNoticeDao.update(entity);
			if (updated) {
				dbServerSyncDao.update(DbServerSyncEnum.SYS_NOTICE);
			}
			return updated;
		}
		return false;
	}
	
	@Override
	public boolean updateSort(int id, int sort) {
		SysNotice entity = sysNoticeDao.getById(id);
		if(entity != null) {
			entity.setSort(sort);
			boolean updated = sysNoticeDao.update(entity);
			if (updated) {
				dbServerSyncDao.update(DbServerSyncEnum.SYS_NOTICE);
			}
			return updated;
		}
		return false;
	}
	
	@Override
	public boolean delete(int id) {
		boolean deleted = sysNoticeDao.delete(id);
		if (deleted) {
			dbServerSyncDao.update(DbServerSyncEnum.SYS_NOTICE);
		}
		return deleted;
	}
	
}