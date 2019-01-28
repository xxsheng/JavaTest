package lottery.domains.content.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserGameReportDao;
import lottery.domains.content.entity.HistoryUserGameReport;
import lottery.domains.content.entity.UserGameReport;
import lottery.domains.content.global.Database;
import lottery.domains.content.vo.bill.HistoryUserGameReportVO;
import lottery.domains.content.vo.bill.UserGameReportVO;

/**
 * Created by Nick on 2016/12/28.
 */
@Repository
public class UserGameReportDaoImpl implements UserGameReportDao {
    private final String tab = UserGameReport.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<UserGameReport> superDao;
    @Autowired
    private HibernateSuperDao<HistoryUserGameReport> historySuperDao;
    
    private final String hsInstance =  Database.name;
    private final String hsbackupInstance=Database.backup;
    @Override
    public boolean save(UserGameReport entity) {
        return superDao.save(entity);
    }

    @Override
    public UserGameReport get(int userId, int platformId, String time) {
        String hql = "from " + tab + " where userId = ?0 and platformId=?1 and time = ?2";
        Object[] values = {userId, platformId, time};
        return (UserGameReport) superDao.unique(hql, values);
    }

    @Override
    public boolean update(UserGameReport entity) {
        String hql = "update " + tab + " set transIn = transIn + ?1, transOut = transOut + ?2, waterReturn = waterReturn + ?3, proxyReturn = proxyReturn + ?4, activity = activity + ?5, billingOrder = billingOrder + ?6, prize = prize + ?7  where id = ?0";
        Object[] values = {entity.getId(), entity.getTransIn(), entity.getTransOut(), entity.getWaterReturn(), entity.getProxyReturn(),  entity.getActivity(), entity.getBillingOrder(), entity.getPrize()};
        return superDao.update(hql, values);
    }

    @Override
    public List<UserGameReport> list(int userId, String sTime, String eTime) {
        String hql = "from " + tab + " where userId = ?0 and time >= ?1 and time < ?2";
        Object[] values = {userId, sTime, eTime};
        return superDao.list(hql, values);
    }

    @Override
    public List<UserGameReport> find(List<Criterion> criterions, List<Order> orders) {
        return superDao.findByCriteria(UserGameReport.class, criterions, orders);
    }
    
    @Override
    public List<HistoryUserGameReport> findHistory(List<Criterion> criterions, List<Order> orders) {
        return historySuperDao.findByCriteria(HistoryUserGameReport.class, criterions, orders);
    }
    @Override
    public List<UserGameReportVO> reportByUser(String sTime, String eTime) {
        String hql = "select userId,sum(transIn),sum(transOut),sum(waterReturn),sum(proxyReturn),sum(activity),sum(billingOrder),sum(prize),time from "+
                tab + " where time >= ?0 and time < ?1 and user_id <> 72 group by userId,time";
        Object[] values = {sTime, eTime};
        List<Object []> result = (List<Object []>) superDao.listObject(hql, values);

        List<UserGameReportVO> reports = new ArrayList<>(result.size());

        for (Object[] objects : result) {
            UserGameReportVO reportVO = new UserGameReportVO();
            reportVO.setUserId(Integer.valueOf(objects[0].toString()));
            reportVO.setTransIn(Double.valueOf(objects[1].toString()));
            reportVO.setTransOut(Double.valueOf(objects[2].toString()));
            reportVO.setWaterReturn(Double.valueOf(objects[3].toString()));
            reportVO.setProxyReturn(Double.valueOf(objects[4].toString()));
            reportVO.setActivity(Double.valueOf(objects[5].toString()));
            reportVO.setBillingOrder(Double.valueOf(objects[6].toString()));
            reportVO.setPrize(Double.valueOf(objects[7].toString()));
            reportVO.setTime(objects[8].toString());
            reports.add(reportVO);
        }
        return reports;
    }

    @Override
    public UserGameReportVO sumLowersAndSelf(int userId, String sTime, String eTime) {
        String sql = "select sum(ugr.trans_in) transIn,sum(ugr.trans_out) transOut,sum(ugr.prize) prize,sum(ugr.water_return) waterReturn,sum(ugr.proxy_return) proxyReturn,sum(ugr.activity) activity,sum(ugr.billing_order) billingOrder from user_game_report ugr left join user u on ugr.user_id = u.id where ugr.time >= :sTime and ugr.time < :eTime and (u.upids like :upid or ugr.user_id = :userId)";
        Map<String, Object> params = new HashMap<>();
        params.put("sTime", sTime);
        params.put("eTime", eTime);
        params.put("upid", "%[" + userId + "]%");
        params.put("userId", userId);

        Object result = superDao.uniqueSqlWithParams(sql, params);
        if (result == null) {
            return null;
        }
        Object[] results = (Object[]) result;
        double transIn = results[0] == null ? 0 : ((BigDecimal) results[0]).doubleValue();
        double transOut = results[1] == null ? 0 : ((BigDecimal) results[1]).doubleValue();
        double prize = results[2] == null ? 0 : ((BigDecimal) results[2]).doubleValue();
        double waterReturn = results[3] == null ? 0 : ((BigDecimal) results[3]).doubleValue();
        double proxyReturn = results[4] == null ? 0 : ((BigDecimal) results[4]).doubleValue();
        double activity = results[5] == null ? 0 : ((BigDecimal) results[5]).doubleValue();
        double billingOrder = results[6] == null ? 0 : ((BigDecimal) results[6]).doubleValue();

        UserGameReportVO report = new UserGameReportVO();
        report.setTransIn(transIn);
        report.setTransOut(transOut);
        report.setPrize(prize);
        report.setWaterReturn(waterReturn);
        report.setProxyReturn(proxyReturn);
        report.setActivity(activity);
        report.setBillingOrder(billingOrder);
        return report;
    }
    @Override
    public HistoryUserGameReportVO historySumLowersAndSelf(int userId, String sTime, String eTime) {
    	String sql = "select sum(ugr.trans_in) transIn,sum(ugr.trans_out) transOut,sum(ugr.prize) prize,sum(ugr.water_return) waterReturn,sum(ugr.proxy_return) proxyReturn,sum(ugr.activity) activity,sum(ugr.billing_order) billingOrder from "+hsbackupInstance+".user_game_report ugr left join "+hsInstance+".user u on ugr.user_id = u.id where ugr.time >= :sTime and ugr.time < :eTime and (u.upids like :upid or ugr.user_id = :userId)";
        Map<String, Object> params = new HashMap<>();
        params.put("sTime", sTime);
        params.put("eTime", eTime);
        params.put("upid", "%[" + userId + "]%");
        params.put("userId", userId);

        Object result = historySuperDao.uniqueSqlWithParams(sql, params);
        if (result == null) {
            return null;
        }
        Object[] results = (Object[]) result;
        double transIn = results[0] == null ? 0 : ((BigDecimal) results[0]).doubleValue();
        double transOut = results[1] == null ? 0 : ((BigDecimal) results[1]).doubleValue();
        double prize = results[2] == null ? 0 : ((BigDecimal) results[2]).doubleValue();
        double waterReturn = results[3] == null ? 0 : ((BigDecimal) results[3]).doubleValue();
        double proxyReturn = results[4] == null ? 0 : ((BigDecimal) results[4]).doubleValue();
        double activity = results[5] == null ? 0 : ((BigDecimal) results[5]).doubleValue();
        double billingOrder = results[6] == null ? 0 : ((BigDecimal) results[6]).doubleValue();

        HistoryUserGameReportVO report = new HistoryUserGameReportVO();
        report.setTransIn(transIn);
        report.setTransOut(transOut);
        report.setPrize(prize);
        report.setWaterReturn(waterReturn);
        report.setProxyReturn(proxyReturn);
        report.setActivity(activity);
        report.setBillingOrder(billingOrder);
        return report;
    }

    @Override
    public UserGameReportVO sum(int userId, String sTime, String eTime) {
        String sql = "select sum(ugr.trans_in) transIn,sum(ugr.trans_out) transOut,sum(ugr.prize) prize,sum(ugr.spend_return) spendReturn,sum(ugr.proxy_return) proxyReturn,sum(ugr.activity) activity,sum(ugr.billing_order) billingOrder from user_game_report ugr left join user u on ugr.user_id = u.id where ugr.time >= :sTime and ugr.time < :eTime and ugr.user_id = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("sTime", sTime);
        params.put("eTime", eTime);
        params.put("userId", userId);
        Object result = superDao.uniqueSqlWithParams(sql, params);
        if (result == null) {
            return null;
        }
        Object[] results = (Object[]) result;
        double transIn = results[0] == null ? 0 : ((BigDecimal) results[0]).doubleValue();
        double transOut = results[1] == null ? 0 : ((BigDecimal) results[1]).doubleValue();
        double prize = results[2] == null ? 0 : ((BigDecimal) results[2]).doubleValue();
        double waterReturn = results[3] == null ? 0 : ((BigDecimal) results[3]).doubleValue();
        double proxyReturn = results[4] == null ? 0 : ((BigDecimal) results[4]).doubleValue();
        double activity = results[5] == null ? 0 : ((BigDecimal) results[5]).doubleValue();
        double billingOrder = results[6] == null ? 0 : ((BigDecimal) results[6]).doubleValue();

        UserGameReportVO report = new UserGameReportVO();
        report.setTransIn(transIn);
        report.setTransOut(transOut);
        report.setPrize(prize);
        report.setWaterReturn(waterReturn);
        report.setProxyReturn(proxyReturn);
        report.setActivity(activity);
        report.setBillingOrder(billingOrder);
        return report;
    }
}
