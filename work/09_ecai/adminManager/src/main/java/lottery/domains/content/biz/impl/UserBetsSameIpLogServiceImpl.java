package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBetsSameIpLogService;
import lottery.domains.content.dao.UserBetsSameIpLogDao;
import lottery.domains.content.entity.UserBetsSameIpLog;
import lottery.domains.content.vo.user.UserBetsSameIpLogVO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 2016/12/24.
 */
@Service
public class UserBetsSameIpLogServiceImpl implements UserBetsSameIpLogService {
    @Autowired
    private UserBetsSameIpLogDao uBetsSameIpLogDao;

    @Override
    public PageList search(String ip, String username, String sortColumn, String sortType, int start, int limit) {
        List<Criterion> criterions = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        if(StringUtil.isNotNull(ip)) {
            criterions.add(Restrictions.eq("ip", ip));
        }
        if(StringUtil.isNotNull(username)) {
            criterions.add(Restrictions.like("users", "[" + username + "]", MatchMode.ANYWHERE));
        }

        if(StringUtil.isNotNull(sortColumn)) {
            if ("usersCount".equals(sortColumn)) {
                orders.add("DESC".equals(sortType) ? Order.desc("usersCount") : Order.asc("usersCount"));
            }
            else if ("lastTime".equals(sortColumn)) {
                orders.add("DESC".equals(sortType) ? Order.desc("lastTime") : Order.asc("lastTime"));
            }
            else if ("times".equals(sortColumn)) {
                orders.add("DESC".equals(sortType) ? Order.desc("times") : Order.asc("times"));
            }
            else if ("amount".equals(sortColumn)) {
                orders.add("DESC".equals(sortType) ? Order.desc("amount") : Order.asc("amount"));
            }
        }
        else {
            orders.add(Order.desc("lastTime"));
            orders.add(Order.desc("id"));
        }


        List<UserBetsSameIpLogVO> list = new ArrayList<>();
        PageList plist = uBetsSameIpLogDao.search(criterions, orders, start, limit);
        for (Object tmpBean : plist.getList()) {
            list.add(new UserBetsSameIpLogVO((UserBetsSameIpLog) tmpBean));
        }
        plist.setList(list);
        return plist;
    }
}
