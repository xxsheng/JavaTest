package lottery.domains.content.dao.read.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.hibernate.HibernateSuperReadDao;
import lottery.domains.content.dao.read.UserGameReportReadDao;
import lottery.domains.content.entity.UserGameReport;
import lottery.domains.content.vo.bill.UserGameReportRichVO;
import lottery.domains.content.vo.user.UserGameReportTeamStatisticsVO;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2016/12/28.
 */
@Repository
public class UserGameReportReadDaoImpl implements UserGameReportReadDao {
    private final String tab = UserGameReport.class.getSimpleName();

    @Autowired
    private HibernateSuperReadDao<UserGameReport> superDao;

    @Override
    public List<UserGameReport> find(List<Criterion> criterions, List<Order> orders) {
        return superDao.findByCriteria(UserGameReport.class, criterions, orders);
    }

    @Override
    public List<UserGameReport> getDayListAll(String sDate, String eDate) {
        StringBuffer sql = new StringBuffer("select ugr.time, sum(ugr.trans_in), sum(ugr.trans_out), sum(ugr.water_return), sum(ugr.proxy_return), sum(ugr.activity), sum(ugr.billing_order), sum(ugr.prize) from user_game_report ugr where ugr.time >= :sTime and ugr.time < :eTime and ugr.id > 0 group by ugr.time");

        Map<String, Object> params = new HashMap<>();
        params.put("sTime", sDate);
        params.put("eTime", eDate);

        List<?> results = superDao.listBySql(sql.toString(), params);
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }

        List<UserGameReport> reports = new ArrayList<>(results.size());
        for (Object result : results) {
            Object[] resultArr = (Object[]) result;

            int index = 0;

            String time = resultArr[index] == null ? null : resultArr[index++].toString();
            double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double waterReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double proxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double billingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double prize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();

            UserGameReport report = new UserGameReport();
            report.setTime(time);
            report.setTransIn(transIn);
            report.setTransOut(transOut);
            report.setWaterReturn(waterReturn);
            report.setProxyReturn(proxyReturn);
            report.setActivity(activity);
            report.setBillingOrder(billingOrder);
            report.setPrize(prize);

            reports.add(report);
        }

        return reports;
    }

    @Override
    public List<UserGameReport> getDayListByTeam(int userId, String sDate, String eDate) {
        return getDayListByTeam(new int[]{userId}, sDate, eDate);
    }

    @Override
    public List<UserGameReport> getDayListByTeam(int[] userIds, String sDate, String eDate) {
        StringBuffer sql = new StringBuffer("select ugr.time, sum(ugr.trans_in), sum(ugr.trans_out), sum(ugr.water_return), sum(ugr.proxy_return), sum(ugr.activity), sum(ugr.billing_order), sum(ugr.prize) from user_game_report ugr,user u where ugr.user_id = u.id and (u.id in(:userIds)");

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < userIds.length; i++) {
            sql.append(" or u.upids like :upid").append(i);
            params.put("upid" + i, "%[" + userIds[i] + "]%");
        }

        sql.append(") and ugr.time >= :sTime and ugr.time < :eTime and ugr.id > 0 group by ugr.time");

        params.put("userIds", ArrayUtils.transInIds(userIds));
        params.put("sTime", sDate);
        params.put("eTime", eDate);

        List<?> results = superDao.listBySql(sql.toString(), params);
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }

        List<UserGameReport> reports = new ArrayList<>(results.size());
        for (Object result : results) {
            Object[] resultArr = (Object[]) result;

            int index = 0;

            String time = resultArr[index] == null ? null : resultArr[index++].toString();
            double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double waterReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double proxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double billingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double prize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();

            UserGameReport report = new UserGameReport();
            report.setTime(time);
            report.setTransIn(transIn);
            report.setTransOut(transOut);
            report.setWaterReturn(waterReturn);
            report.setProxyReturn(proxyReturn);
            report.setActivity(activity);
            report.setBillingOrder(billingOrder);
            report.setPrize(prize);

            reports.add(report);
        }

        return reports;
    }

    // @Override
    // public List<UserGameReport> getDayList(int[] ids, String sDate, String eDate) {
    //     if(ids.length > 0) {
    //         String hql = "from " + tab + " where userId in (" + ArrayUtils.transInIds(ids) +  ") and time >= ?0 and time < ?1";
    //         Object[] values = {sDate, eDate};
    //         return superDao.list(hql, values);
    //     }
    //     return null;
    // }

    @Override
    public List<UserGameReportRichVO> searchAll(String sTime, String eTime) {
        String sql = "select ugr.user_id, sum(ugr.trans_in), sum(ugr.trans_out), sum(ugr.water_return), sum(ugr.proxy_return), sum(ugr.activity), sum(ugr.billing_order), sum(ugr.prize),u.username,u.upid,u.upids from user_game_report ugr,user u where ugr.user_id = u.id and ugr.time >= :sTime and ugr.time < :eTime and ugr.id > 0 group by ugr.user_id,u.username,u.upid,u.upids";

        Map<String, Object> params = new HashMap<>();
        params.put("sTime", sTime);
        params.put("eTime", eTime);

        List<?> results = superDao.listBySql(sql, params);
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }

        List<UserGameReportRichVO> reports = new ArrayList<>(results.size());
        for (Object result : results) {
            Object[] resultArr = (Object[]) result;

            int index = 0;

            int _userId = resultArr[index] == null ? 0 : ((Integer) resultArr[index++]);
            double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double waterReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double proxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double billingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double prize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            String username = resultArr[index] == null ? null : resultArr[index++].toString();
            int upid = resultArr[index] == null ? null : ((Integer) resultArr[index++]);
            String upids = resultArr[index] == null ? null : resultArr[index++].toString();

            UserGameReportRichVO report = new UserGameReportRichVO();
            report.setUserId(_userId);
            report.setTransIn(transIn);
            report.setTransOut(transOut);
            report.setWaterReturn(waterReturn);
            report.setProxyReturn(proxyReturn);
            report.setActivity(activity);
            report.setBillingOrder(billingOrder);
            report.setPrize(prize);
            report.setUsername(username);
            report.setUpid(upid);
            report.setUpids(upids);

            reports.add(report);
        }

        return reports;
    }

    @Override
    public List<UserGameReportRichVO> searchByTeam(int userId, String sTime, String eTime) {
        String sql = "select ugr.user_id, sum(ugr.trans_in), sum(ugr.trans_out), sum(ugr.water_return), sum(ugr.proxy_return), sum(ugr.activity), sum(ugr.billing_order), sum(ugr.prize),u.username,u.upid,u.upids from user_game_report ugr,user u where ugr.user_id = u.id and (u.id = :userId or u.upids like :upid) and ugr.time >= :sTime and ugr.time < :eTime and ugr.id > 0 group by ugr.user_id,u.username,u.upid,u.upids";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("sTime", sTime);
        params.put("eTime", eTime);
        params.put("upid", "%[" + userId + "]%");

        List<?> results = superDao.listBySql(sql, params);
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }

        List<UserGameReportRichVO> reports = new ArrayList<>(results.size());
        for (Object result : results) {
            Object[] resultArr = (Object[]) result;

            int index = 0;

            int _userId = resultArr[index] == null ? 0 : ((Integer) resultArr[index++]);
            double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double waterReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double proxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double billingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double prize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            String username = resultArr[index] == null ? null : resultArr[index++].toString();
            int upid = resultArr[index] == null ? null : ((Integer) resultArr[index++]);
            String upids = resultArr[index] == null ? null : resultArr[index++].toString();

            UserGameReportRichVO report = new UserGameReportRichVO();
            report.setUserId(_userId);
            report.setTransIn(transIn);
            report.setTransOut(transOut);
            report.setWaterReturn(waterReturn);
            report.setProxyReturn(proxyReturn);
            report.setActivity(activity);
            report.setBillingOrder(billingOrder);
            report.setPrize(prize);
            report.setUsername(username);
            report.setUpid(upid);
            report.setUpids(upids);

            reports.add(report);
        }

        return reports;
    }

    @Override
    public List<UserGameReportRichVO> searchByTeam(int[] userIds, String sTime, String eTime) {
        StringBuffer sql = new StringBuffer("select ugr.user_id, sum(ugr.trans_in), sum(ugr.trans_out), sum(ugr.water_return), sum(ugr.proxy_return), sum(ugr.activity), sum(ugr.billing_order), sum(ugr.prize),u.username,u.upid,u.upids from user_game_report ugr,user u where ugr.user_id = u.id and (u.id in(:userIds)");

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < userIds.length; i++) {
            sql.append(" or u.upids like :upid").append(i);
            params.put("upid" + i, "%[" + userIds[i] + "]%");
        }

        sql.append(") and ugr.time >= :sTime and ugr.time < :eTime and ugr.id > 0 group by ugr.user_id,u.username,u.upid,u.upids");

        params.put("userIds", ArrayUtils.transInIds(userIds));
        params.put("sTime", sTime);
        params.put("eTime", eTime);

        List<?> results = superDao.listBySql(sql.toString(), params);
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }

        List<UserGameReportRichVO> reports = new ArrayList<>(results.size());
        for (Object result : results) {
            Object[] resultArr = (Object[]) result;

            int index = 0;

            int _userId = resultArr[index] == null ? 0 : ((Integer) resultArr[index++]);
            double transIn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double transOut = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double waterReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double proxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double activity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double billingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            double prize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
            String username = resultArr[index] == null ? null : resultArr[index++].toString();
            int upid = resultArr[index] == null ? null : ((Integer) resultArr[index++]);
            String upids = resultArr[index] == null ? null : resultArr[index++].toString();

            UserGameReportRichVO report = new UserGameReportRichVO();
            report.setUserId(_userId);
            report.setTransIn(transIn);
            report.setTransOut(transOut);
            report.setWaterReturn(waterReturn);
            report.setProxyReturn(proxyReturn);
            report.setActivity(activity);
            report.setBillingOrder(billingOrder);
            report.setPrize(prize);
            report.setUsername(username);
            report.setUpid(upid);
            report.setUpids(upids);

            reports.add(report);
        }

        return reports;
    }

    @Override
    public UserGameReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate) {
        StringBuffer sql = new StringBuffer("select sum(ugr.water_return), sum(ugr.proxy_return), sum(ugr.activity), sum(ugr.billing_order), sum(ugr.prize) from user_game_report ugr,user u where ugr.user_id = u.id and (u.id = :userId or u.upids like :upid)");

        sql.append(" and ugr.time >= :sTime and ugr.time < :eTime and ugr.id > 0");

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("upid", "%[" + userId + "]%");
        params.put("sTime", sDate);
        params.put("eTime", eDate);

        Object unique = superDao.uniqueSqlWithParams(sql.toString(), params);
        if (unique == null) {
            return new UserGameReportTeamStatisticsVO();
        }

        Object[] resultArr = (Object[]) unique;
        int index = 0;
        double totalGameWaterReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
        double totalGameProxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
        double totalGameActivity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
        double totalGameBillingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
        double totalGamePrize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;

        double totalGameProfit = totalGamePrize + totalGameWaterReturn + totalGameProxyReturn + totalGameActivity - totalGameBillingOrder;

        UserGameReportTeamStatisticsVO uTeamStatisticsVO = new UserGameReportTeamStatisticsVO();
        uTeamStatisticsVO.setTotalGameWaterReturn(totalGameWaterReturn);
        uTeamStatisticsVO.setTotalGameProxyReturn(totalGameProxyReturn);
        uTeamStatisticsVO.setTotalGameActivity(totalGameActivity);
        uTeamStatisticsVO.setTotalGameBillingOrder(totalGameBillingOrder);
        uTeamStatisticsVO.setTotalGamePrize(totalGamePrize);
        uTeamStatisticsVO.setTotalGameProfit(totalGameProfit);
        return uTeamStatisticsVO;
    }

    @Override
    public UserGameReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate) {
        StringBuffer sql = new StringBuffer("select sum(ugr.water_return), sum(ugr.proxy_return), sum(ugr.activity), sum(ugr.billing_order), sum(ugr.prize) from user_game_report ugr,user u where ugr.user_id = u.id and (u.id in ("+ArrayUtils.transInIds(userIds)+") or ");

        Map<String, Object> params = new HashMap<>();
        params.put("sTime", sDate);
        params.put("eTime", eDate);

        for (int i = 0; i < userIds.length; i++) {
            sql.append("u.upids like :upid").append(i);
            params.put("upid" + i, "%["+userIds[i]+"]%");

            if (i < userIds.length - 1) {
                sql.append(" or ");
            }
        }

        sql.append(" ) and ugr.time >= :sTime and ugr.time < :eTime and ugr.id > 0");

        Object unique = superDao.uniqueSqlWithParams(sql.toString(), params);
        if (unique == null) {
            return new UserGameReportTeamStatisticsVO();
        }

        Object[] resultArr = (Object[]) unique;
        int index = 0;
        double totalGameWaterReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
        double totalGameProxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
        double totalGameActivity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
        double totalGameBillingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;
        double totalGamePrize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index]).doubleValue(); index++;

        double totalGameProfit = totalGamePrize + totalGameWaterReturn + totalGameProxyReturn + totalGameActivity - totalGameBillingOrder;

        UserGameReportTeamStatisticsVO uTeamStatisticsVO = new UserGameReportTeamStatisticsVO();
        uTeamStatisticsVO.setTotalGameWaterReturn(totalGameWaterReturn);
        uTeamStatisticsVO.setTotalGameProxyReturn(totalGameProxyReturn);
        uTeamStatisticsVO.setTotalGameActivity(totalGameActivity);
        uTeamStatisticsVO.setTotalGameBillingOrder(totalGameBillingOrder);
        uTeamStatisticsVO.setTotalGamePrize(totalGamePrize);
        uTeamStatisticsVO.setTotalGameProfit(totalGameProfit);
        return uTeamStatisticsVO;
    }

    @Override
    public UserGameReportTeamStatisticsVO statisticsAll(String sDate, String eDate) {
        StringBuffer sql = new StringBuffer("select sum(ugr.water_return), sum(ugr.proxy_return), sum(ugr.activity), sum(ugr.billing_order), sum(ugr.prize) from user_game_report ugr,user u where ugr.user_id = u.id and ugr.time >= :sTime and ugr.time < :eTime and ugr.id > 0 and u.upid != 0");

        Map<String, Object> params = new HashMap<>();
        params.put("sTime", sDate);
        params.put("eTime", eDate);

        Object unique = superDao.uniqueSqlWithParams(sql.toString(), params);
        if (unique == null) {
            return new UserGameReportTeamStatisticsVO();
        }

        Object[] resultArr = (Object[]) unique;
        int index = 0;
        double totalGameWaterReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
        double totalGameProxyReturn = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
        double totalGameActivity = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
        double totalGameBillingOrder = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();
        double totalGamePrize = resultArr[index] == null ? 0 : ((BigDecimal) resultArr[index++]).doubleValue();

        double totalGameProfit = totalGamePrize + totalGameWaterReturn + totalGameProxyReturn + totalGameActivity - totalGameBillingOrder;

        UserGameReportTeamStatisticsVO uTeamStatisticsVO = new UserGameReportTeamStatisticsVO();
        uTeamStatisticsVO.setTotalGameWaterReturn(totalGameWaterReturn);
        uTeamStatisticsVO.setTotalGameProxyReturn(totalGameProxyReturn);
        uTeamStatisticsVO.setTotalGameActivity(totalGameActivity);
        uTeamStatisticsVO.setTotalGameBillingOrder(totalGameBillingOrder);
        uTeamStatisticsVO.setTotalGamePrize(totalGamePrize);
        uTeamStatisticsVO.setTotalGameProfit(totalGameProfit);
        return uTeamStatisticsVO;
    }
}
