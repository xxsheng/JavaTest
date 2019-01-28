package lottery.domains.content.biz.read.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.read.GameBetsReadService;
import lottery.domains.content.dao.read.GameBetsReadDao;
import lottery.domains.content.entity.GameBets;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bets.GameBetsVO;
import lottery.domains.pool.DataFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 2016/12/28.
 */
@Service
@Transactional(readOnly = true)
public class GameBetsReadServiceImpl implements GameBetsReadService {
    @Autowired
    private GameBetsReadDao gameBetsReadDao;
    @Autowired
    private DataFactory dataFactory;

    @Override
    @Transactional(readOnly = true)
    public PageList searchAll(Integer platformId, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = gameBetsReadDao.searchAll(platformId, sTime, eTime, start, limit);

        pList = convertPageList(pList);
        return pList;
    }

    @Override
    @Transactional(readOnly = true)
    public PageList searchByUserId(int userId, Integer platformId, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        List<Criterion> criterions = new ArrayList<>();
        criterions.add(Restrictions.eq("userId", userId));
        criterions.add(Restrictions.gt("id", 0));
        if(platformId != null) {
            criterions.add(Restrictions.eq("platformId", platformId.intValue()));
        }
        if(StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("time", sTime));
        }
        if(StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.lt("time", eTime));
        }
        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("time"));
        orders.add(Order.desc("id"));
        PageList pList = gameBetsReadDao.search(criterions, orders, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    @Transactional(readOnly = true)
    public PageList searchByTeam(int userId, Integer platformId, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList;
        if (userId == Global.USER_TOP_ID) {
            pList = gameBetsReadDao.searchAll(platformId, sTime, eTime, start, limit);
        }
        else {
            pList = gameBetsReadDao.searchByTeam(userId, platformId, sTime, eTime, start, limit);
        }

        pList = convertPageList(pList);
        return pList;
    }

    @Override
    @Transactional(readOnly = true)
    public PageList searchByDirectTeam(int userId, Integer platformId, String sTime, String eTime, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = gameBetsReadDao.searchByDirectTeam(userId, platformId, sTime, eTime, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    @Transactional(readOnly = true)
    public PageList searchByTeam(Integer[] userIds, Integer platformId, String sTime, String eTime, int start, int limit) {
        if (userIds == null || userIds.length <= 0) {
            return new PageList();
        }
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 10 ? 10 : limit;

        PageList pList = gameBetsReadDao.searchByTeam(userIds, platformId, sTime, eTime, start, limit);
        pList = convertPageList(pList);
        return pList;
    }

    @Override
    @Transactional(readOnly = true)
    public double getBillingOrder(int userId, String startTime, String endTime) {
        return gameBetsReadDao.getBillingOrder(userId, startTime, endTime);
    }

    private PageList convertPageList(PageList pList) {
        if (pList == null) {
            return new PageList();
        }
        List<GameBetsVO> list = new ArrayList<>();
        for (Object tmpBean : pList.getList()) {
            list.add(new GameBetsVO((GameBets) tmpBean, dataFactory));
        }
        pList.setList(list);
        return pList;
    }
}
