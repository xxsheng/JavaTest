package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import lottery.domains.content.biz.UserGameReportService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserGameReportDao;
import lottery.domains.content.entity.HistoryUserGameReport;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBaccaratReport;
import lottery.domains.content.entity.UserGameReport;
import lottery.domains.content.entity.UserLotteryReport;
import lottery.domains.content.vo.bill.HistoryUserGameReportVO;
import lottery.domains.content.vo.bill.UserBaccaratReportVO;
import lottery.domains.content.vo.bill.UserGameReportVO;
import lottery.domains.content.vo.bill.UserLotteryReportVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Nick on 2016/12/28.
 */
@Service
public class UserGameReportServiceImpl implements UserGameReportService {
    @Autowired
    private UserGameReportDao uGameReportDao;
    @Autowired
    private UserDao uDao;
    @Autowired
    private LotteryDataFactory lotteryDataFactory;


    @Override
    public boolean update(int userId, int platformId, double billingOrder, double prize, double waterReturn, double proxyReturn, String time) {
        UserGameReport entity = new UserGameReport();
        entity.setBillingOrder(billingOrder);
        entity.setPrize(prize);
        entity.setWaterReturn(waterReturn);
        entity.setProxyReturn(proxyReturn);
        entity.setTime(time);

        UserGameReport userGameReport = uGameReportDao.get(userId, platformId, time);
        if (userGameReport != null) {
            entity.setId(userGameReport.getId());
            return uGameReportDao.update(entity);

        }
        else {
            entity.setUserId(userId);
            entity.setPlatformId(platformId);
            return uGameReportDao.save(entity);
        }
    }

    @Override
    public List<UserGameReportVO> report(String sTime, String eTime) {
        // 列出所有总账号ID
        List<Integer> managerIds = uDao.getUserDirectLowerId(0);

        // 统计每个总账号
        List<UserGameReportVO> userReports = new ArrayList<>(managerIds.size());
        for (Integer managerId : managerIds) {
            UserGameReportVO reportVO = uGameReportDao.sumLowersAndSelf(managerId, sTime, eTime);
            if (reportVO.getTransIn() <= 0 && reportVO.getTransOut() <= 0 && reportVO.getPrize() <= 0
                    && reportVO.getWaterReturn() <= 0 && reportVO.getProxyReturn() <= 0
                    && reportVO.getActivity() <= 0 && reportVO.getBillingOrder() <= 0) {
                // 如果下级用户没有数据，那不计入这条数据
                continue;
            }

            UserVO user = lotteryDataFactory.getUser(managerId);
            if (user != null) {
                reportVO.setName(user.getUsername());
                userReports.add(reportVO);
            }
        }

        // 计算总计
        List<UserGameReportVO> result = new ArrayList<>(userReports.size() + 1);
        UserGameReportVO tBean = new UserGameReportVO("总计");
        for (UserGameReportVO userReport : userReports) {
            tBean.addBean(userReport);
        }
        result.add(tBean);
        result.addAll(userReports);
        return result;


        // // 首先列出来用户下级以及所有下级
        // Map<Integer, User> lowerUsersMap = new HashMap<>();
        // List<User> lowerUserList = uDao.listAll();
        // List<User> directUserList  = uDao.getUserDirectLower(0);
        // // 查询条件
        // List<Criterion> criterions = new ArrayList<>();
        // List<Order> orders = new ArrayList<>();
        // for (User tmpUser : lowerUserList) {
        //     lowerUsersMap.put(tmpUser.getId(), tmpUser);
        // }
        // if(StringUtil.isNotNull(sTime)) {
        //     criterions.add(Restrictions.ge("time", sTime));
        // }
        // if(StringUtil.isNotNull(eTime)) {
        //     criterions.add(Restrictions.lt("time", eTime));
        // }
        // List<UserGameReport> result = uGameReportDao.find(criterions, orders);
        // Map<Integer, UserGameReportVO> resultMap = new HashMap<>();
        // UserGameReportVO tBean = new UserGameReportVO("总计");
        // // 汇总处理
        // for (UserGameReport tmpBean : result) {
        //     User thisUser = lowerUsersMap.get(tmpBean.getUserId());
        //     if(thisUser.getUpid() == 0) { // 直属下级
        //         if(!resultMap.containsKey(thisUser.getId())) {
        //             resultMap.put(thisUser.getId(), new UserGameReportVO(thisUser.getUsername()));
        //         }
        //         resultMap.get(thisUser.getId()).addBean(tmpBean);
        //     } else { // 下级的下级
        //         for (User tmpUser : directUserList) { // 查出具体是哪个下级的会员
        //             if(thisUser.getUpids().indexOf("[" + tmpUser.getId() + "]") != -1) {
        //                 if(!resultMap.containsKey(tmpUser.getId())) {
        //                     resultMap.put(tmpUser.getId(), new UserGameReportVO(tmpUser.getUsername()));
        //                 }
        //                 resultMap.get(tmpUser.getId()).addBean(tmpBean);
        //             }
        //         }
        //     }
        //     tBean.addBean(tmpBean);
        // }
        // List<UserGameReportVO> list = new ArrayList<>();
        // list.add(tBean);
        // Object[] keys = resultMap.keySet().toArray();
        // Arrays.sort(keys);
        // for (Object o : keys) {
        //     list.add(resultMap.get(o));
        // }
        // return list;
    }
    
    @Override
    public List<HistoryUserGameReportVO> historyReport(String sTime, String eTime) {
        // 列出所有总账号ID
        List<Integer> managerIds = uDao.getUserDirectLowerId(0);

        // 统计每个总账号
        List<HistoryUserGameReportVO> userReports = new ArrayList<>(managerIds.size());
        for (Integer managerId : managerIds) {
        	HistoryUserGameReportVO reportVO = uGameReportDao.historySumLowersAndSelf(managerId, sTime, eTime);
            if (reportVO.getTransIn() <= 0 && reportVO.getTransOut() <= 0 && reportVO.getPrize() <= 0
                    && reportVO.getWaterReturn() <= 0 && reportVO.getProxyReturn() <= 0
                    && reportVO.getActivity() <= 0 && reportVO.getBillingOrder() <= 0) {
                // 如果下级用户没有数据，那不计入这条数据
                continue;
            }

            UserVO user = lotteryDataFactory.getUser(managerId);
            if (user != null) {
                reportVO.setName(user.getUsername());
                userReports.add(reportVO);
            }
        }

        // 计算总计
        List<HistoryUserGameReportVO> result = new ArrayList<>(userReports.size() + 1);
        HistoryUserGameReportVO tBean = new HistoryUserGameReportVO("总计");
        for (HistoryUserGameReportVO userReport : userReports) {
            tBean.addBean(userReport);
        }
        result.add(tBean);
        result.addAll(userReports);
        return result;
    }
    @Override
    public List<UserGameReportVO> report(int userId, String sTime, String eTime) {
        User targetUser = uDao.getById(userId);
        if(targetUser != null) {
            // 首先列出来用户下级以及所有下级
            Map<Integer, User> lowerUsersMap = new HashMap<>();
            List<User> lowerUserList = uDao.getUserLower(targetUser.getId());
            List<User> directUserList  = uDao.getUserDirectLower(targetUser.getId());
            // 查询条件
            List<Criterion> criterions = new ArrayList<>();
            List<Order> orders = new ArrayList<>();
            List<Integer> toUids = new ArrayList<>();
            toUids.add(targetUser.getId());
            for (User tmpUser : lowerUserList) {
                toUids.add(tmpUser.getId());
                lowerUsersMap.put(tmpUser.getId(), tmpUser);
            }
            if(StringUtil.isNotNull(sTime)) {
                criterions.add(Restrictions.ge("time", sTime));
            }
            if(StringUtil.isNotNull(eTime)) {
                criterions.add(Restrictions.lt("time", eTime));
            }
            criterions.add(Restrictions.in("userId", toUids));
            List<UserGameReport> result = uGameReportDao.find(criterions, orders);
            Map<Integer, UserGameReportVO> resultMap = new HashMap<>();
            UserGameReportVO tBean = new UserGameReportVO("总计");
            // 汇总处理
            for (UserGameReport tmpBean : result) {
                if(tmpBean.getUserId() == targetUser.getId()) { // 自己
                    if(!resultMap.containsKey(targetUser.getId())) {
                        resultMap.put(targetUser.getId(), new UserGameReportVO(targetUser.getUsername()));
                    }
                    resultMap.get(targetUser.getId()).addBean(tmpBean);
                } else {
                    User thisUser = lowerUsersMap.get(tmpBean.getUserId());
                    if(thisUser.getUpid() == targetUser.getId()) { // 直属下级
                        if(!resultMap.containsKey(thisUser.getId())) {
                            resultMap.put(thisUser.getId(), new UserGameReportVO(thisUser.getUsername()));
                        }
                        resultMap.get(thisUser.getId()).addBean(tmpBean);
                    } else { // 下级的下级
                        for (User tmpUser : directUserList) { // 查出具体是哪个下级的会员
                            if(thisUser.getUpids().indexOf("[" + tmpUser.getId() + "]") != -1) {
                                if(!resultMap.containsKey(tmpUser.getId())) {
                                    resultMap.put(tmpUser.getId(), new UserGameReportVO(tmpUser.getUsername()));
                                }
                                resultMap.get(tmpUser.getId()).addBean(tmpBean);
                            }
                        }
                    }
                }
                tBean.addBean(tmpBean);
            }

            // 看所有用户是否有更多查看
            for (Integer lowerUserId : resultMap.keySet()) {
                UserGameReportVO reportVO = resultMap.get(lowerUserId);

                for (UserGameReport report : result) {
                    User lowerUser = lowerUsersMap.get(report.getUserId());
                    if (lowerUser != null && lowerUser.getUpid() == lowerUserId) {
                        reportVO.setHasMore(true);
                        break;
                    }
                }
            }

            List<UserGameReportVO> list = new ArrayList<>();
            list.add(tBean);
            Object[] keys = resultMap.keySet().toArray();
            Arrays.sort(keys);
            for (Object o : keys) {
                list.add(resultMap.get(o));
            }
            return list;
        }
        return null;
    }
    
    @Override
    public List<HistoryUserGameReportVO> historyReport(int userId, String sTime, String eTime) {
        User targetUser = uDao.getById(userId);
        if(targetUser != null) {
            // 首先列出来用户下级以及所有下级
            Map<Integer, User> lowerUsersMap = new HashMap<>();
            List<User> lowerUserList = uDao.getUserLower(targetUser.getId());
            List<User> directUserList  = uDao.getUserDirectLower(targetUser.getId());
            // 查询条件
            List<Criterion> criterions = new ArrayList<>();
            List<Order> orders = new ArrayList<>();
            List<Integer> toUids = new ArrayList<>();
            toUids.add(targetUser.getId());
            for (User tmpUser : lowerUserList) {
                toUids.add(tmpUser.getId());
                lowerUsersMap.put(tmpUser.getId(), tmpUser);
            }
            if(StringUtil.isNotNull(sTime)) {
                criterions.add(Restrictions.ge("time", sTime));
            }
            if(StringUtil.isNotNull(eTime)) {
                criterions.add(Restrictions.lt("time", eTime));
            }
            criterions.add(Restrictions.in("userId", toUids));
            List<HistoryUserGameReport> result = uGameReportDao.findHistory(criterions, orders);
            Map<Integer, HistoryUserGameReportVO> resultMap = new HashMap<>();
            HistoryUserGameReportVO tBean = new HistoryUserGameReportVO("总计");
            // 汇总处理
            for (HistoryUserGameReport tmpBean : result) {
                if(tmpBean.getUserId() == targetUser.getId()) { // 自己
                    if(!resultMap.containsKey(targetUser.getId())) {
                        resultMap.put(targetUser.getId(), new HistoryUserGameReportVO(targetUser.getUsername()));
                    }
                    resultMap.get(targetUser.getId()).addBean(tmpBean);
                } else {
                    User thisUser = lowerUsersMap.get(tmpBean.getUserId());
                    if(thisUser.getUpid() == targetUser.getId()) { // 直属下级
                        if(!resultMap.containsKey(thisUser.getId())) {
                            resultMap.put(thisUser.getId(), new HistoryUserGameReportVO(thisUser.getUsername()));
                        }
                        resultMap.get(thisUser.getId()).addBean(tmpBean);
                    } else { // 下级的下级
                        for (User tmpUser : directUserList) { // 查出具体是哪个下级的会员
                            if(thisUser.getUpids().indexOf("[" + tmpUser.getId() + "]") != -1) {
                                if(!resultMap.containsKey(tmpUser.getId())) {
                                    resultMap.put(tmpUser.getId(), new HistoryUserGameReportVO(tmpUser.getUsername()));
                                }
                                resultMap.get(tmpUser.getId()).addBean(tmpBean);
                            }
                        }
                    }
                }
                tBean.addBean(tmpBean);
            }

            // 看所有用户是否有更多查看
            for (Integer lowerUserId : resultMap.keySet()) {
            	HistoryUserGameReportVO reportVO = resultMap.get(lowerUserId);

                for (HistoryUserGameReport report : result) {
                    User lowerUser = lowerUsersMap.get(report.getUserId());
                    if (lowerUser != null && lowerUser.getUpid() == lowerUserId) {
                        reportVO.setHasMore(true);
                        break;
                    }
                }
            }

            List<HistoryUserGameReportVO> list = new ArrayList<>();
            list.add(tBean);
            Object[] keys = resultMap.keySet().toArray();
            Arrays.sort(keys);
            for (Object o : keys) {
                list.add(resultMap.get(o));
            }
            return list;
        }
        return null;
    }
    @Override
    public List<UserGameReportVO> reportByUser(String sTime, String eTime) {
        return uGameReportDao.reportByUser(sTime, eTime);
    }
}
