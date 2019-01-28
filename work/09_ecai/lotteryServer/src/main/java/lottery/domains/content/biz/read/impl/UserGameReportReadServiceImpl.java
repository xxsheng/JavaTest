package lottery.domains.content.biz.read.impl;

import javautils.array.ArrayUtils;
import lottery.domains.content.biz.read.UserGameReportReadService;
import lottery.domains.content.dao.read.UserGameReportReadDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserGameReport;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.bill.UserGameReportRichVO;
import lottery.domains.content.vo.bill.UserGameReportVO;
import lottery.domains.content.vo.user.UserGameReportTeamStatisticsVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.DataFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Nick on 2016/12/28.
 */
@Service
@Transactional(readOnly = true)
public class UserGameReportReadServiceImpl implements UserGameReportReadService {
    @Autowired
    private UserGameReportReadDao uGameReportReadDao;

    @Autowired
    private DataFactory dataFactory;

    @Override
    @Transactional(readOnly = true)
    public List<UserGameReportVO> reportByUser(User user, String sTime, String eTime) {
        List<UserGameReportRichVO> results;
        if (user.getType() == Global.USER_TYPE_RELATED) {
            int[] relatedUserIds = ArrayUtils.transGetIds(user.getRelatedUsers());
            results = uGameReportReadDao.searchByTeam(relatedUserIds, sTime, eTime);
        }
        else if (user.getId() == Global.USER_TOP_ID) {
            results = uGameReportReadDao.searchAll(sTime, eTime);
        }
        else {
            results = uGameReportReadDao.searchByTeam(user.getId(), sTime, eTime);
        }
        Map<Integer, UserGameReportVO> resultMap = new HashMap<>();
        UserGameReportVO tBean = new UserGameReportVO("总计");

        for (UserGameReportRichVO tmpBean : results) {
            if(tmpBean.getUserId() == user.getId()) { // 自己
                if(!resultMap.containsKey(user.getId())) {
                    resultMap.put(user.getId(), new UserGameReportVO(user.getUsername()));
                }
                resultMap.get(user.getId()).addBean(tmpBean);
            } else {
                // 直属下级或关联直属下级
                if(tmpBean.getUpid() == user.getId() ||
                        (user.getType() == Global.USER_TYPE_RELATED &&
                                user.getRelatedUsers().indexOf("[" + tmpBean.getUserId()+ "]") > -1)) {
                    if(!resultMap.containsKey(tmpBean.getUserId())) {
                        resultMap.put(tmpBean.getUserId(), new UserGameReportVO(tmpBean.getUsername()).addBean(tmpBean));
                    }
                    else {
                        resultMap.get(tmpBean.getUserId()).addBean(tmpBean);
                    }
                }
                else {
                    // 下级的下级。或关联账号的下级

                    String upids;
                    if (user.getType() == Global.USER_TYPE_RELATED) {
                        // 关联账号的下级,关联账号只能关联到主管账号，这里拿出主管ID就可以了
                        upids = StringUtils.substringBeforeLast(tmpBean.getUpids(), "["+Global.USER_TOP_ID+"]");
                    }
                    else {
                        // 直属下级的下级
                        upids = StringUtils.substringBeforeLast(tmpBean.getUpids(), "["+user.getId()+"]");
                    }

                    if (StringUtils.isNotEmpty(upids)) {
                        if (upids.endsWith(",")) {
                            upids = upids.substring(0, upids.length() - 1);
                        }

                        int[] upidsArr = ArrayUtils.transGetIds(upids);
                        int directId = upidsArr[upidsArr.length - 1];

                        if(!resultMap.containsKey(directId)) {
                            UserVO directUser = dataFactory.getUser(directId);
                            if (directUser != null) {
                                UserGameReportVO reportVO = new UserGameReportVO(directUser.getUsername()).addBean(tmpBean);
                                reportVO.setHasMore(true);
                                resultMap.put(directUser.getId(), reportVO);
                            }
                        }
                        else {
                            resultMap.get(directId).addBean(tmpBean).setHasMore(true);
                        }
                    }
                }
            }

            // 汇总总计
            tBean.addBean(tmpBean);
        }

        List<UserGameReportVO> list = new ArrayList<>();
        list.add(tBean);
        Object[] keys = resultMap.keySet().toArray();
        Arrays.sort(keys);
        for (Object o : keys) {
            UserGameReportVO reportVO = resultMap.get(o);

            if (reportVO.getTransIn() > 0 || reportVO.getTransOut() > 0 || reportVO.getPrize() > 0 || reportVO.getWaterReturn() > 0 || reportVO.getProxyReturn() > 0
                    || reportVO.getActivity() > 0 || reportVO.getBillingOrder() > 0) {
                list.add(resultMap.get(o));
            }
        }
        return list;

        // List<UserGameReportRichVO> results;
        // if (user.getId() == Global.USER_TOP_ID) {
        //     results = uGameReportReadDao.searchAll(sTime, eTime);
        // }
        // else {
        //     results = uGameReportReadDao.searchByTeam(user.getId(), sTime, eTime);
        // }
        // Map<Integer, UserGameReportVO> resultMap = new HashMap<>();
        // UserGameReportVO tBean = new UserGameReportVO("总计");
        //
        // for (UserGameReportRichVO tmpBean : results) {
        //     if(tmpBean.getUserId() == user.getId()) { // 自己
        //         if(!resultMap.containsKey(user.getId())) {
        //             resultMap.put(user.getId(), new UserGameReportVO(user.getUsername()));
        //         }
        //         resultMap.get(user.getId()).addBean(tmpBean);
        //     } else {
        //         // 直属下级或关联直属下级
        //         if(tmpBean.getUpid() == user.getId()) {
        //             if(!resultMap.containsKey(tmpBean.getUserId())) {
        //                 resultMap.put(tmpBean.getUserId(), new UserGameReportVO(tmpBean.getUsername()).addBean(tmpBean));
        //             }
        //             else {
        //                 resultMap.get(tmpBean.getUserId()).addBean(tmpBean);
        //             }
        //         }
        //         else {
        //             // 下级的下级
        //             String upids = StringUtils.substringBeforeLast(tmpBean.getUpids(), "["+user.getId()+"]");
        //
        //             if (StringUtils.isNotEmpty(upids)) {
        //                 if (upids.endsWith(",")) {
        //                     upids = upids.substring(0, upids.length() - 1);
        //                 }
        //
        //                 int[] upidsArr = ArrayUtils.transGetIds(upids);
        //                 int directId = upidsArr[upidsArr.length - 1];
        //
        //                 if(!resultMap.containsKey(directId)) {
        //                     UserVO directUser = dataFactory.getUser(directId);
        //                     if (directUser != null) {
        //                         UserGameReportVO reportVO = new UserGameReportVO(directUser.getUsername()).addBean(tmpBean);
        //                         reportVO.setHasMore(true);
        //                         resultMap.put(directUser.getId(), reportVO);
        //                     }
        //                 }
        //                 else {
        //                     resultMap.get(directId).addBean(tmpBean).setHasMore(true);
        //                 }
        //             }
        //         }
        //     }
        //
        //     // 汇总总计
        //     tBean.addBean(tmpBean);
        // }
        //
        // List<UserGameReportVO> list = new ArrayList<>();
        // list.add(tBean);
        // Object[] keys = resultMap.keySet().toArray();
        // Arrays.sort(keys);
        // for (Object o : keys) {
        //     UserGameReportVO reportVO = resultMap.get(o);
        //
        //     if (reportVO.getTransIn() > 0 || reportVO.getTransOut() > 0 || reportVO.getPrize() > 0 || reportVO.getWaterReturn() > 0 || reportVO.getProxyReturn() > 0
        //             || reportVO.getActivity() > 0 || reportVO.getBillingOrder() > 0) {
        //         list.add(resultMap.get(o));
        //     }
        // }
        // return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGameReportVO> reportByTime(User user, String sDate, String eDate) {
        List<UserGameReport> results;
        if (user.getId() == Global.USER_TOP_ID) {
            results = uGameReportReadDao.getDayListAll(sDate, eDate);
        }
        else {
            results = uGameReportReadDao.getDayListByTeam(user.getId(), sDate, eDate);
        }

        if (results == null) {
            return new ArrayList<>();
        }

        List<UserGameReportVO> resultList = new ArrayList<>();
        Map<String, UserGameReportVO> resultMap = new HashMap<>();
        for (UserGameReport tmpBean : results) {
            if(!resultMap.containsKey(tmpBean.getTime())) {
                resultMap.put(tmpBean.getTime(), new UserGameReportVO(tmpBean.getTime()));
            }
            resultMap.get(tmpBean.getTime()).addBean(tmpBean);
        }
        for (Object o : resultMap.keySet().toArray()) {
            resultList.add(resultMap.get(o));
        }
        return resultList;
    }

    @Override
    @Transactional(readOnly = true)
    public UserGameReportTeamStatisticsVO statisticsByTeam(int userId, String sDate, String eDate) {
        return uGameReportReadDao.statisticsByTeam(userId, sDate, eDate);
    }

    @Override
    @Transactional(readOnly = true)
    public UserGameReportTeamStatisticsVO statisticsByTeam(int[] userIds, String sDate, String eDate) {
        return uGameReportReadDao.statisticsByTeam(userIds, sDate, eDate);
    }

    @Override
    @Transactional(readOnly = true)
    public UserGameReportTeamStatisticsVO statisticsAll(String sDate, String eDate) {
        return uGameReportReadDao.statisticsAll(sDate, eDate);
    }
}
