package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.ActivityRebateWheelBillService;
import lottery.domains.content.dao.ActivityRebateWheelBillDao;
import lottery.domains.content.entity.ActivityRebateWheelBill;
import lottery.domains.content.vo.activity.ActivityRebateWheelBillVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 2017/11/27.
 */
@Service
public class ActivityRebateWheelBillServiceImpl implements ActivityRebateWheelBillService {
    @Autowired
    private ActivityRebateWheelBillDao billDao;

    @Autowired
    private LotteryDataFactory dataFactory;

    @Override
    public PageList find(String username, String minTime, String maxTime, String ip, int start, int limit) {
        List<Criterion> criterions = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        if(StringUtil.isNotNull(username)) {
            UserVO user = dataFactory.getUser(username);
            if(user != null) {
                criterions.add(Restrictions.eq("userId", user.getId()));
            }
        }
        if(StringUtil.isNotNull(minTime)) {
            criterions.add(Restrictions.ge("time", minTime));
        }
        if(StringUtil.isNotNull(maxTime)) {
            criterions.add(Restrictions.lt("time", maxTime));
        }
        if(StringUtil.isNotNull(ip)) {
            criterions.add(Restrictions.eq("ip", ip));
        }
        orders.add(Order.desc("id"));
        List<ActivityRebateWheelBillVO> list = new ArrayList<>();
        PageList pList = billDao.find(criterions, orders, start, limit);
        for (Object tmpBean : pList.getList()) {
            ActivityRebateWheelBillVO tmpVO = new ActivityRebateWheelBillVO((ActivityRebateWheelBill) tmpBean, dataFactory);
            list.add(tmpVO);
        }
        pList.setList(list);
        return pList;
    }

    @Override
    public double sumAmount(String username, String minTime, String maxTime, String ip) {
        Integer userId = null;
        if(StringUtil.isNotNull(username)) {
            UserVO user = dataFactory.getUser(username);
            if(user != null) {
                userId = user.getId();
            }
        }
        return billDao.sumAmount(userId, minTime, maxTime, ip);
    }
}
