package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.GameBetsService;
import lottery.domains.content.dao.GameBetsDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.GameBets;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.user.GameBetsVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 2017/2/8.
 */
@Service
public class GameBetsServiceImpl implements GameBetsService {
    @Autowired
    private GameBetsDao gameBetsDao;
    @Autowired
    private UserDao uDao;
    @Autowired
    private LotteryDataFactory lotteryDataFactory;

    @Override
    public GameBetsVO getById(int id) {
        GameBets gameBets = gameBetsDao.getById(id);
        if (gameBets != null) {
            return new GameBetsVO(gameBets, lotteryDataFactory);
        }

        return null;
    }

    @Override
    public PageList search(String keyword, String username, Integer platformId, String minTime, String maxTime,
                           Double minMoney, Double maxMoney, Double minPrizeMoney, Double maxPrizeMoney,
                           String gameCode, String gameType, String gameName, int start, int limit) {
        List<Criterion> criterions = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        if(StringUtil.isNotNull(username)) {
            User user = uDao.getByUsername(username);
            if(user != null) {
                criterions.add(Restrictions.eq("userId", user.getId()));
            }
        }
        if(StringUtil.isNotNull(keyword)) {
            Disjunction disjunction = Restrictions.or();
            if(StringUtil.isInteger(keyword)) {
                disjunction.add(Restrictions.eq("id", Integer.parseInt(keyword)));
            }
            disjunction.add(Restrictions.like("betsId", keyword, MatchMode.ANYWHERE));
            criterions.add(disjunction);
        }
        if(platformId != null) {
            criterions.add(Restrictions.eq("platformId", platformId.intValue()));
        }
        if(StringUtil.isNotNull(minTime)) {
            criterions.add(Restrictions.gt("time", minTime));
        }
        if(StringUtil.isNotNull(maxTime)) {
            criterions.add(Restrictions.lt("time", maxTime));
        }
        if(minMoney != null) {
            criterions.add(Restrictions.ge("money", minMoney.doubleValue()));
        }
        if(maxMoney != null) {
            criterions.add(Restrictions.le("money", maxMoney.doubleValue()));
        }
        if(minPrizeMoney != null) {
            criterions.add(Restrictions.ge("prizeMoney", minPrizeMoney.doubleValue()));
        }
        if(maxPrizeMoney != null) {
            criterions.add(Restrictions.le("prizeMoney", maxPrizeMoney.doubleValue()));
        }
        if(StringUtils.isNotEmpty(gameCode)) {
            criterions.add(Restrictions.like("gameCode", gameCode, MatchMode.ANYWHERE));
        }
        if(StringUtils.isNotEmpty(gameType)) {
            criterions.add(Restrictions.like("gameType", gameType, MatchMode.ANYWHERE));
        }
        if(StringUtils.isNotEmpty(gameName)) {
            criterions.add(Restrictions.like("gameName", gameName, MatchMode.ANYWHERE));
        }
        orders.add(Order.desc("id"));
        List<GameBetsVO> list = new ArrayList<>();
        PageList pList = gameBetsDao.find(criterions, orders, start, limit);
        for (Object tmpBean : pList.getList()) {
            GameBetsVO tmpVO = new GameBetsVO((GameBets) tmpBean, lotteryDataFactory);
            list.add(tmpVO);
        }
        pList.setList(list);
        return pList;
    }

    @Override
    public double[] getTotalMoney(String keyword, String username, Integer platformId, String minTime, String maxTime,
                                  Double minMoney, Double maxMoney, Double minPrizeMoney, Double maxPrizeMoney,
                                  String gameCode, String gameType, String gameName) {
        Integer userId = null;
        if (StringUtil.isNotNull(username)) {
            User user = uDao.getByUsername(username);
            if (user != null) {
                userId = user.getId();
            }
        }
        return gameBetsDao.getTotalMoney(keyword, userId, platformId, minTime, maxTime, minMoney, maxMoney, minPrizeMoney, maxPrizeMoney, gameCode, gameType, gameName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public double getBillingOrder(int userId, String startTime, String endTime) {
        return gameBetsDao.getBillingOrder(userId, startTime, endTime);
    }
}
