package lottery.web.content.utils;

import javautils.array.ArrayUtils;
import javautils.math.MathUtil;
import lottery.domains.content.dao.UserCodeQuotaDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.config.CodeConfig;
import lottery.domains.content.vo.user.UserCodeQuotaVO;
import lottery.domains.content.vo.user.UserCodeRangeVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class UserCodePointUtil {

    /**
     * DAO
     */
    @Autowired
    private UserDao uDao;

    @Autowired
    private UserCodeQuotaDao uCodeQuotaDao;

    /**
     * DataFactory
     */
    @Autowired
    private LotteryDataFactory dataFactory;

    /**
     * 获取用户账号级别
     */
    public int getUserCode(double locatePoint) {
        CodeConfig config = dataFactory.getCodeConfig();
        return config.getSysCode() - (int) (MathUtil.subtract(config.getSysLp(), locatePoint) * 20);
    }

    /**
     * 获取定位返点
     */
    public double getLocatePoint(int code) {
        CodeConfig config = dataFactory.getCodeConfig();
        return config.getSysLp() - (config.getSysCode() - code) / 20.0;
    }

    /**
     * 获取不定位返点
     */
    public double getNotLocatePoint(int code) {
        CodeConfig config = dataFactory.getCodeConfig();
        double result = config.getSysNlp() - (config.getSysCode() - code) / 20.0;
        return result < 0 ? 0 : result;
    }

    /**
     * 获取不定位返点
     */
    public double getNotLocatePoint(double locatePoint) {
        return getNotLocatePoint(getUserCode(locatePoint));
    }

    /**
     * 获取用户总配额
     */
    public List<UserCodeQuotaVO> listTotalQuota(User uBean) {
        return new ArrayList<>();
        // List<SysCodeRangeVO> sysCodeRange = dataFactory.getCodeConfig().getSysCodeRange();
        // List<UserCodeQuotaVO> sysQuotaList = new LinkedList<>();
        // UserCodeQuota uQuota = uCodeQuotaDao.get(uBean.getId()); // 用户已分配的
        //
        // for (int i = 0; i < sysCodeRange.size(); i++) {
        //     SysCodeRangeVO range = sysCodeRange.get(i);
        //
        //     if (range.getPoint() == uBean.getLocatePoint()) {
        //         UserCodeQuotaVO quota = new UserCodeQuotaVO();
        //         quota.setMaxPoint(range.getMaxPoint());
        //         quota.setMinPoint(range.getMinPoint());
        //         int countIndex = i+1;
        //         quota.setCountIndex(countIndex);
        //
        //         // 还没有分配，取系统默认
        //         if (uQuota == null) {
        //             quota.setTotalCount(range.getDefaultQuantity());
        //             quota.setSurplusCount(range.getDefaultQuantity());
        //         }
        //         // 已经分配
        //         else {
        //             int totalCount =
        //                     i == 0 ? uQuota.getCount1()
        //                     : i == 1 ? uQuota.getCount2()
        //                     : i == 2 ? uQuota.getCount3()
        //                     : 0;
        //             quota.setTotalCount(totalCount);
        //             quota.setSurplusCount(totalCount);
        //         }
        //         sysQuotaList.add(quota);
        //     }
        // }
        // return sysQuotaList;
    }

    /**
     * 获取用户剩余配额
     */
    public List<UserCodeQuotaVO> listSurplusQuota(User uBean) {
        return new ArrayList<>();
        // List<UserCodeQuotaVO> totalQuotaList = listTotalQuota(uBean);
        // if (CollectionUtils.isEmpty(totalQuotaList)) {
        //     return new ArrayList<>();
        // }
        //
        //
        // List<User> lowerList = uDao.getUserDirectLower(uBean.getId());
        // // 排除开户的
        // for (User tmpUser : lowerList) {
        //     if(tmpUser.getCodeType() == 0) {
        //         double locatePoint = tmpUser.getLocatePoint();
        //         for (UserCodeQuotaVO tmpQuota : totalQuotaList) {
        //             if(locatePoint >= tmpQuota.getMinPoint() && locatePoint <= tmpQuota.getMaxPoint()) {
        //                 tmpQuota.addSurplusCount(-1);
        //             }
        //         }
        //     }
        // }
        // // // 排除已分配的
        // // int[] userIds = new int[lowerList.size()];
        // // for (int i = 0; i < lowerList.size(); i++) {
        // // 	userIds[i] = lowerList.get(i).getId();
        // // }
        // // List<UserCodeQuota> lowerQuotaList = uCodeQuotaDao.list(userIds);
        // // for (UserCodeQuota tmpQuota : lowerQuotaList) {
        // //
        // // 	for (UserCodeQuotaVO userCodeQuotaVO : totalQuotaList) {
        // //
        // // 	}
        // //
        // // 	// totalQuotaList.get(0).addSurplusCount(-tmpQuota.getCount1());
        // // 	// totalQuotaList.get(1).addSurplusCount(-tmpQuota.getCount2());
        // // 	// totalQuotaList.get(2).addSurplusCount(-tmpQuota.getCount3());
        // // }
        // return totalQuotaList;
    }

    /**
     * 获取可分配配额区间
     */
    public UserCodeRangeVO getUserCodeRange(User uBean, List<UserCodeQuotaVO> surplusList) {
        // // 是否同级开户
        // double maxLocatePoint = uBean.getLocatePoint();
        // /// 如果不允许同级开号
        // if (uBean.getAllowEqualCode() != 1) {
        //     maxLocatePoint = MathUtil.subtract(maxLocatePoint, 0.1);
        // }
        // for (UserCodeQuotaVO tmpQuota : surplusList) {
        //     if (maxLocatePoint >= tmpQuota.getMinPoint() && maxLocatePoint <= tmpQuota.getMaxPoint()) {
        //         // 满足有配额的时候退出
        //         if (tmpQuota.getSurplusCount() > 0) break;
        //         maxLocatePoint = MathUtil.subtract(tmpQuota.getMinPoint(), 0.1);
        //     }
        // }
        // int code = getUserCode(maxLocatePoint);
        // double maxNotLocatePoint = getNotLocatePoint(maxLocatePoint);
        // return new UserCodeRangeVO(code, maxLocatePoint, maxNotLocatePoint);
        return null;
    }

    public List<String> getUserLevels(String username) {
        if (StringUtils.isEmpty(username)) {
            return new ArrayList<>();
        }

        List<String> userLevels = new LinkedList<>();

        User user = uDao.getByUsername(username);
        if (user == null) {
            return userLevels;
        }



        if (StringUtils.isEmpty(user.getUpids())) {
            return userLevels;
        }



        int[] upids = ArrayUtils.transGetIds(user.getUpids());
        for (int i = upids.length - 1; i >= 0; i--) {
            UserVO upUser = dataFactory.getUser(upids[i]);
            if (upUser != null) {
                userLevels.add(upUser.getUsername());
            }
        }

        userLevels.add(username);

        return userLevels;
    }


    /**
     * 等级1代理：系统最高等级 & 上级是总账(72) （等级1用户一般是指主管或内部招商   主管|内部招商 -> 招商 -> 直属）
     */
    public boolean isLevel1Proxy(User uBean) {
        if (uBean.getType() == Global.USER_TYPE_PROXY
//                && uBean.getCode() == dataFactory.getCodeConfig().getSysCode()
                && uBean.getUpid() == Global.USER_TOP_ID) {
            return true;
        }
        return false;
    }

    /**
     * 等级2代理：系统最高等级 & 上级是等级1用户 （等级2一般是指招商或超级招商   主管|内部招商 -> 招商 -> 直属）
     */
    public boolean isLevel2Proxy(User uBean) {
    	if (uBean.getType() == Global.USER_TYPE_PROXY
                && uBean.getUpid() != 0
                && uBean.getUpid() != Global.USER_TOP_ID) {
            User upBean = uDao.getById(uBean.getUpid());
            if (isLevel1Proxy(upBean)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 等级2代理（招商）：系统最高等级 & 上级是等级1用户
     */
    public boolean isLevel2ZhaoShangProxy(User uBean) {
        if (uBean.getType() == Global.USER_TYPE_PROXY
                && uBean.getCode() == dataFactory.getCodeConfig().getSysCode()
                && uBean.getUpid() != 0
                && uBean.getUpid() != Global.USER_TOP_ID
                && uBean.getIsCjZhaoShang() == Global.DAIYU_IS_CJ_ZHAO_SHANG_NO) {
            User upBean = uDao.getById(uBean.getUpid());
            if (isLevel1Proxy(upBean)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 等级2代理（超级招商）：系统最高等级 & 上级是等级1用户
     */
    public boolean isLevel2CJZhaoShangProxy(User uBean) {
        if (uBean.getType() == Global.USER_TYPE_PROXY
                && uBean.getCode() == dataFactory.getCodeConfig().getSysCode()
                && uBean.getUpid() != 0
                && uBean.getUpid() != Global.USER_TOP_ID
                && uBean.getIsCjZhaoShang() == Global.DAIYU_IS_CJ_ZHAO_SHANG_YES) {
            User upBean = uDao.getById(uBean.getUpid());
            if (isLevel1Proxy(upBean)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 等级3代理：系统最高等级 & 上级是等级2用户 （等级3用户一般是指直属   主管|内部招商 -> 招商 -> 直属）
     */
    public boolean isLevel3Proxy(User uBean) {
    	if (uBean.getType() == Global.USER_TYPE_PROXY
                && uBean.getUpid() != 0
                && uBean.getUpid() != Global.USER_TOP_ID) {
            User upBean = uDao.getById(uBean.getUpid());
            if (isLevel2Proxy(upBean)) {
                return true;
            }
        }
        return false;
    }
}