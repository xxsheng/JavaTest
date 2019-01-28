package lottery.domains.content.dao.impl;

import java.util.List;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.ActivitySignRecordDao;
import lottery.domains.content.entity.ActivitySignRecord;

@Repository
public class ActivitySignRecordDaoImpl implements ActivitySignRecordDao {
	
	@Autowired
	private HibernateSuperDao<ActivitySignRecord> superDao;

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(ActivitySignRecord.class, criterions, orders, start, limit);
	}
	
}