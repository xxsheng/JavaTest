package lottery.domains.content.dao.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserBetsSameIpLogDao;
import lottery.domains.content.entity.UserBetsSameIpLog;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBetsSameIpLogDaoImpl implements UserBetsSameIpLogDao {

	private final String tab = UserBetsSameIpLog.class.getSimpleName();

	@Autowired
	private HibernateSuperDao<UserBetsSameIpLog> superDao;

    @Override
    public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        return superDao.findPageList(UserBetsSameIpLog.class, criterions, orders, start, limit);
    }
}