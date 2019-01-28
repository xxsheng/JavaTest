package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBillService;
import lottery.domains.content.biz.UserGameDividendBillService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserGameDividendBillDao;
import lottery.domains.content.entity.UserGameDividendBill;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserGameDividendBillVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 契约分红账单Service
 * Created by Nick on 2016/10/31.
 */
@Service
public class UserGameDividendBillServiceImpl implements UserGameDividendBillService {
    @Autowired
    private UserGameDividendBillDao uGameDividendBillDao;
    @Autowired
    private LotteryDataFactory dataFactory;

    @Autowired
    private UserDao uDao;
    @Autowired
    private UserBillService uBillService;

    @Autowired
    private UserSysMessageService uSysMessageService;


    @Override
    public PageList search(List<Integer> userIds, String sTime, String eTime, Double minUserAmount,
                           Double maxUserAmount, Integer status, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 20 ? 20 : limit;
        // 查询条件
        List<Criterion> criterions = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(userIds)) {
            criterions.add(Restrictions.in("userId", userIds));
        }

        if (StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("indicateStartDate", sTime));
        }
        if (StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.le("indicateEndDate", eTime));
        }

        if (minUserAmount != null) {
            criterions.add(Restrictions.ge("userAmount", minUserAmount));
        }
        if (maxUserAmount != null) {
            criterions.add(Restrictions.le("userAmount", maxUserAmount));
        }

        if (status != null) {
            criterions.add(Restrictions.eq("status", status));
        }

        // 排序条件
        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("id"));
        PageList pList = uGameDividendBillDao.search(criterions, orders, start, limit);
        List<UserGameDividendBillVO> convertList = new ArrayList<>();
        if(pList != null && pList.getList() != null){
            for (Object tmpBean : pList.getList()) {
                convertList.add(new UserGameDividendBillVO((UserGameDividendBill) tmpBean, dataFactory));
            }
        }
        pList.setList(convertList);
        return pList;
    }

    @Override
    public double sumUserAmount(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount, Integer status) {
        return uGameDividendBillDao.sumUserAmount(userIds, sTime, eTime, minUserAmount, maxUserAmount, status);
    }

    @Override
    public List<UserGameDividendBill> findByCriteria(List<Criterion> criterions, List<Order> orders) {
        return uGameDividendBillDao.findByCriteria(criterions, orders);
    }


    @Override
    public UserGameDividendBill getById(int id) {
        return uGameDividendBillDao.getById(id);
    }

    @Override
    public boolean agree(int id, double userAmount ,String remarks) {
        // 查找契约
        UserGameDividendBill dividendBill = getById(id);

        if (dividendBill == null || dividendBill.getUserAmount() <= 0) {
            return false;
        }

        boolean updated = uGameDividendBillDao.update(dividendBill.getId(), Global.GAME_DIVIDEND_BILL_UNCOLLECT, userAmount, remarks);

        // 发放系统消息
        if (updated && userAmount > 0) {
            uSysMessageService.addGameDividendBill(dividendBill.getUserId(), dividendBill.getIndicateStartDate(), dividendBill.getIndicateEndDate());
        }
        return updated;
    }

    @Override
    public boolean deny(int id, double userAmount, String remarks) {
        return uGameDividendBillDao.update(id, Global.DIVIDEND_BILL_DENIED, userAmount, remarks);
    }

    @Override
    public boolean del(int id) {
        return uGameDividendBillDao.del(id);
    }
}
