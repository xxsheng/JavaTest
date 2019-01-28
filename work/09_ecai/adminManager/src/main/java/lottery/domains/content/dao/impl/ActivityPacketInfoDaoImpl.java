package lottery.domains.content.dao.impl;

import java.util.List;
import java.util.Map;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lottery.domains.content.dao.ActivityPacketInfoDao;
import lottery.domains.content.entity.ActivityPacketInfo;

@Repository
public class ActivityPacketInfoDaoImpl implements ActivityPacketInfoDao {
	
	private final String tab = ActivityPacketInfo.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<ActivityPacketInfo> superDao;

	@Override
	public PageList find(List<Criterion> criterions, List<Order> orders, int start, int limit) {
		return superDao.findPageList(ActivityPacketInfo.class, criterions, orders, start, limit);
	}

	@Override
	public boolean save(ActivityPacketInfo entity) {
		return superDao.save(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<Integer, Double>> statTotal() {
		String hql = "select type , sum(amount) from " + tab + " group by type";
		Object [] values = {};
		return  (List<Map<Integer, Double>>) superDao.listObject(hql, values);
	}

}