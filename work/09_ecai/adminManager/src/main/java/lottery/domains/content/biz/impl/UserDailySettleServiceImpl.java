package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.web.WebJSONObject;
import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserDailySettleService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.dao.UserDailySettleDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDailySettle;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserDailySettleVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;

/**
 * 契约日结Service
 * Created by Nick on 2016/10/31.
 */
@Service
public class UserDailySettleServiceImpl implements UserDailySettleService {
    private static final Logger log = LoggerFactory.getLogger(UserDailySettleServiceImpl.class);
    @Autowired
    private UserDailySettleDao uDailySettleDao;
    @Autowired
    private LotteryDataFactory dataFactory;
    @Autowired
    private UserService uService;
    @Autowired
    private UserDao uDao;
    @Autowired
    private UserCodePointUtil uCodePointUtil;


    @Override
    public PageList search(List<Integer> userIds, String sTime, String eTime, Double minScale, Double maxScale, Integer minValidUser, Integer maxValidUser, Integer status, int start, int limit) {
        start = start < 0 ? 0 : start;
        limit = limit < 0 ? 0 : limit;
        limit = limit > 20 ? 20 : limit;
        // 查询条件
        List<Criterion> criterions = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(userIds)) {
            criterions.add(Restrictions.in("userId", userIds));
        }

        if (StringUtil.isNotNull(sTime)) {
            criterions.add(Restrictions.ge("agreeTime", sTime));
        }
        if (StringUtil.isNotNull(eTime)) {
            criterions.add(Restrictions.le("agreeTime", eTime));
        }

        if (minScale != null) {
            criterions.add(Restrictions.ge("scale", minScale));
        }
        if (maxScale != null) {
            criterions.add(Restrictions.le("scale", maxScale));
        }

        if (minValidUser != null) {
            criterions.add(Restrictions.ge("minValidUser", minValidUser));
        }
        if (maxValidUser != null) {
            criterions.add(Restrictions.le("minValidUser", maxValidUser));
        }

        if (status != null) {
            criterions.add(Restrictions.eq("status", status));
        }

        // 排序条件
        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("id"));
        PageList pList = uDailySettleDao.search(criterions, orders, start, limit);
        List<UserDailySettleVO> convertList = new ArrayList<>();
        if(pList != null && pList.getList() != null){
            for (Object tmpBean : pList.getList()) {
                convertList.add(new UserDailySettleVO((UserDailySettle) tmpBean, dataFactory));
            }
        }
        pList.setList(convertList);
        return pList;
    }

    @Override
    public UserDailySettle getByUserId(int userId) {
        return uDailySettleDao.getByUserId(userId);
    }

    @Override
    public UserDailySettle getById(int id) {
        return uDailySettleDao.getById(id);
    }

    private boolean add(User user, String scaleLevel, String salasLevel, String lossLevel, int minValidUser, int status, int fixed, double minScale, double maxScale, String usersLevel) {
        UserDailySettle bean = uDailySettleDao.getByUserId(user.getId());
        if(bean == null) {
            int userId = user.getId();
            Moment moment = new Moment();
            String createTime = moment.toSimpleTime();
            String createDate = moment.toSimpleDate();
            String endDate = moment.add(99, "years").toSimpleDate();
            double totalAmount = 0;
            UserDailySettle entity = new UserDailySettle(userId, scaleLevel,  lossLevel, salasLevel, minValidUser, createTime, createTime, createDate, endDate, totalAmount, status, fixed, minScale, maxScale, usersLevel);
            uDailySettleDao.add(entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByTeam(String username) {
        User uBean = uService.getByUsername(username);
        if(uBean != null) {
            uDailySettleDao.deleteByTeam(uBean.getId());
            return true;
        }
        return false;
    }

    @Override
    public boolean update(WebJSONObject json, int id, String scaleLevel, String salesLevel, String lossLevel, int minValidUser, String usersLevel) {
        // 获取日结数据
        UserDailySettle dailySettle = uDailySettleDao.getById(id);
        if (dailySettle == null) {
            json.set(1, "1-7"); // 数据不存在
            return false;
        }

        if (dailySettle.getScaleLevel().equals(scaleLevel) && dailySettle.getSalesLevel().equals(salesLevel) 
        		&& dailySettle.getLossLevel().equals(lossLevel) && dailySettle.getUserLevel() == usersLevel) {
            json.set(1, "2-29");
            return false;
        }
        

        User user = uDao.getById(dailySettle.getUserId());
        if (!checkCanEdit(json, user)) {
            return false;
        }

        UserDailySettle upDailySettle = uDailySettleDao.getByUserId(user.getUpid());
        if (!uCodePointUtil.isLevel3Proxy(user) && upDailySettle == null) {
            json.setWithParams(2, "2-3008");
            return false;
        }
        
        if (!checkValidLevel(scaleLevel, salesLevel, lossLevel, upDailySettle, usersLevel)) {
        	json.setWithParams(2, "2-3009");
        	return false;
        }

        double[] minMaxScale = getMinMaxScale(user);
        double minScale = minMaxScale[0];
        double maxScale = minMaxScale[1];

        String[] scaleLevels = scaleLevel.split(",");
		if (Double.valueOf(scaleLevels[0]) < minScale	|| Double.valueOf(scaleLevels[scaleLevels.length - 1]) > maxScale) {
			json.setWithParams(2, "2-3006");
			return false;
		}

		double[] minMaxSales = getMinMaxSales(user);
		double minSales = minMaxSales[0];
		double maxSales = minMaxSales[1];
		String[] salesLevels = salesLevel.split(",");
		if (Double.valueOf(salesLevels[0]) < minSales || Double.valueOf(salesLevels[salesLevels.length - 1]) > maxSales) {
			json.setWithParams(2, "2-3006");
			return false;
		}

		double[] minMaxLoss = getMinMaxLoss(user);
		double minLoss = minMaxLoss[0];
		double maxLoss = minMaxLoss[1];
		String[] lossLevels = lossLevel.split(",");
		if (Double.valueOf(lossLevels[0]) < minLoss || Double.valueOf(lossLevels[lossLevels.length - 1]) > maxLoss) {
			json.setWithParams(2, "2-3006");
			return false;
		}
		
		int[] minMaxUser = getMinMaxUsers(user);
		int minUser = minMaxUser[0];
		int maxUser = minMaxUser[1];
		String[] userLevels = usersLevel.split(",");
		if (Integer.valueOf(userLevels[0]) < minUser || Integer.valueOf(userLevels[userLevels.length - 1]) > maxUser) {
			json.setWithParams(2, "2-3006");
			return false;
		}

        return uDailySettleDao.updateSomeFields(id, scaleLevel,lossLevel,salesLevel, minValidUser, usersLevel);
    }

    @Override
    public boolean add(WebJSONObject json, String username, String scaleLevel, String salesLevel, String lossLevel, int minValidUser, int status, String usersLevel) {
        User user = uDao.getByUsername(username);
        if (!checkCanEdit(json, user)) {
            return false;
        }

        // 获取日结数据
        UserDailySettle dailySettle = uDailySettleDao.getByUserId(user.getId());
        if (dailySettle != null) {
            json.set(2, "2-3007");
            return false;
        }

        UserDailySettle upDailySettle = uDailySettleDao.getByUserId(user.getUpid());
        if (!uCodePointUtil.isLevel3Proxy(user) && upDailySettle == null) {
            json.set(2, "2-3008");
            return false;
        } 
        
        if (!checkValidLevel(scaleLevel, salesLevel, lossLevel, upDailySettle, usersLevel)) {
        	json.setWithParams(2, "2-3009");
        	return false;
        }

        double[] minMaxScale = getMinMaxScale(user);
        double minScale = minMaxScale[0];
        double maxScale = minMaxScale[1];

        String[] scaleLevels = scaleLevel.split(",");
		if (Double.valueOf(scaleLevels[0]) < minScale	|| Double.valueOf(scaleLevels[scaleLevels.length - 1]) > maxScale) {
			json.setWithParams(2, "2-3006");
			return false;
		}

		double[] minMaxSales = getMinMaxSales(user);
		double minSales = minMaxSales[0];
		double maxSales = minMaxSales[1];
		String[] salesLevels = salesLevel.split(",");
		if (Double.valueOf(salesLevels[0]) < minSales || Double.valueOf(salesLevels[salesLevels.length - 1]) > maxSales) {
			json.setWithParams(2, "2-3006");
			return false;
		}

		double[] minMaxLoss = getMinMaxLoss(user);
		double minLoss = minMaxLoss[0];
		double maxLoss = minMaxLoss[1];
		String[] lossLevels = lossLevel.split(",");
		if (Double.valueOf(lossLevels[0]) < minLoss || Double.valueOf(lossLevels[lossLevels.length - 1]) > maxLoss) {
			json.setWithParams(2, "2-3006");
			return false;
		}
		
		//验证最大最小人数
		int[] minMaxUser = getMinMaxUsers(user);
		int minUser = minMaxUser[0];
		int maxUser = minMaxUser[1];
		String[] userLevels = usersLevel.split(",");
		if (Integer.valueOf(userLevels[0]) < minUser || Integer.valueOf(userLevels[userLevels.length - 1]) > maxUser) {
			json.setWithParams(2, "2-3006");
			return false;
		}
		
		String[] scaleArrs = scaleLevel.split(",");
		minScale = Double.valueOf(scaleArrs[0]);
		maxScale = Double.valueOf(scaleArrs[scaleArrs.length - 1]);

        return add(user, scaleLevel, salesLevel, lossLevel, minValidUser, status, Global.DAIYU_FIXED_FIXED, minScale, maxScale, usersLevel);
    }

    @Override
    public double[] getMinMaxScale(User acceptUser) {
    	// 本次允许发起的最小比例
    	double minScale = dataFactory.getDailySettleConfig().getLevelsScale()[0];
    	// 本次允许发起的最大比例
    	double maxScale = dataFactory.getDailySettleConfig().getLevelsScale()[1];
    	
        if (uCodePointUtil.isLevel3Proxy(acceptUser)) {
            return new double[]{minScale, maxScale};
        }

        User requestUser = uService.getById(acceptUser.getUpid());
        UserDailySettle upDailySettle = uDailySettleDao.getByUserId(requestUser.getId());
        if (upDailySettle == null) {
            return new double[]{0, 0};
        }
        
        String[] scaleLevel = upDailySettle.getScaleLevel().split(",");
        if (Double.valueOf(scaleLevel[scaleLevel.length - 1]) <= minScale) {
            return new double[]{0, 0};
        }

        maxScale = Double.valueOf(scaleLevel[scaleLevel.length - 1]);
		if (maxScale > dataFactory.getDailySettleConfig().getLevelsScale()[1]) {
			maxScale = dataFactory.getDailySettleConfig().getLevelsScale()[1];
		}

        if (minScale < 0) minScale = 0;
        if (maxScale < 0) maxScale = 0;

        return new double[]{minScale, maxScale};
    }

    @Override
    public boolean checkCanEdit(WebJSONObject json, User user) {
        // 检查层级
        if (uCodePointUtil.isLevel1Proxy(user)) {
            json.set(2, "2-36");
            return false;
        }

        // 检查层级
        if (uCodePointUtil.isLevel2Proxy(user)) {
            json.set(2, "2-39");
            return false;
        }

        boolean checked = checkForRequest(user);
        if (!checked) {
            json.set(2, "2-3012");
            return false;
        }

        return true;
    }

    @Override
    public boolean checkCanDel(WebJSONObject json, User user) {
        if (user.getId() == Global.USER_TOP_ID) {
            json.set(2, "2-33");
            return false;
        }

        // 检查层级
        if (uCodePointUtil.isLevel1Proxy(user)) {
            json.set(2, "2-36");
            return false;
        }
        
        // 检查层级
        if (uCodePointUtil.isLevel2Proxy(user)) {
            json.set(2, "2-39");
            return false;
        }
        return true;
    }

    private boolean checkForRequest(User acceptUser) {
        User requestUser = uService.getById(acceptUser.getUpid()); // 发起人
        if (!allowRequestByUser(requestUser)) {
            return false;
        }

        if (!allowAccept(requestUser, acceptUser)) {
            return false;
        }

        return true;
    }

    /**
     * 发起人的账号是否可以发起
     */
    public boolean allowRequestByUser(User user) {
        if (!dataFactory.getDailySettleConfig().isEnable()) {
            return false;
        }

        if (user.getId() == Global.USER_TOP_ID) {
            return false;
        }

        // 第一等级账户或1800以下都是不可以发起的
        if (uCodePointUtil.isLevel1Proxy(user) || user.getCode() < 1800) {
            return false;
        }
        
        if (user.getCode() < 1800) {
            return false;
        }

//        if (user.getCode() == dataFactory.getCodeConfig().getSysCode()) {
//            // 内部招商不可以发起
//            if (uCodePointUtil.isLevel1Proxy(user)) {
//                return false;
//            }
//        }

        return true;

    }

    /**
     * 是否允许接受契约日结
     * @param requestUser 发起人
     * @param acceptUser 接受人
     */
    public boolean allowAccept(User requestUser, User acceptUser) {
        // 发起人必须是接受人的直属上级
        if (acceptUser.getUpid() != requestUser.getId()) return false;

        // 总账不可以发起或接受
        if (requestUser.getId() == Global.USER_TOP_ID || acceptUser.getId() == Global.USER_TOP_ID) return false;

        // 一级账户 不可以发起或接受
        if (uCodePointUtil.isLevel1Proxy(requestUser) || uCodePointUtil.isLevel1Proxy(acceptUser)) return false;

        // 1800以下不可以发起或接受
        if (requestUser.getCode() < 1800 || acceptUser.getCode() < 1800) return false;

//        if (uCodePointUtil.isLevel2Proxy(requestUser)) {
//            if (uCodePointUtil.isLevel1Proxy(requestUser) || uCodePointUtil.isLevel1Proxy(acceptUser)) {
//                return false;
//            }
//            if (uCodePointUtil.isLevel2Proxy(acceptUser)) {
//                return false;
//            }
//            else if (uCodePointUtil.isLevel2Proxy(requestUser)) {
//                // 招商只允许对直属发起
//                if (uCodePointUtil.isLevel3Proxy(acceptUser)) {
//                    return true;
//                }
//                return false;
//            }
//        }

        if (uCodePointUtil.isLevel2Proxy(requestUser)) {
              // 招商只允许对直属发起
              if (uCodePointUtil.isLevel3Proxy(acceptUser)) {
                  return true;
              }
              return false;
          }
        // 其它账号只能对自己的平级或直属发起，但账号等级差异不允许超过2级
//        int diffCode = requestUser.getCode() - acceptUser.getCode();
//        if (diffCode > 2 || diffCode < 0) return false;

        return true;
    }

    @Override
    public boolean changeZhaoShang(User user, boolean changeToCJZhaoShang) {
    	//TODO 银冠 废弃
//        UserDailySettle uDailySettle = uDailySettleDao.getByUserId(user.getId());
//        if (uDailySettle == null) {
//            return false;
//        }
//
//        if (!uCodePointUtil.isLevel2Proxy(user)) {
//            return false;
//        }
//
//        double scale;
//        int minValidUser = dataFactory.getDailySettleConfig().getZhaoShangMinValidUser();
//        if (changeToCJZhaoShang) {
//            scale = dataFactory.getDailySettleConfig().getCjZhaoShangScale();
//        }
//        else {
//            scale = dataFactory.getDailySettleConfig().getZhaoShangScale();
//        }
//
//        return uDailySettleDao.updateSomeFields(uDailySettle.getId(), scale, minValidUser);
    	return true;
    }

    /*****************每个项目都需要根据需求调整**********************/



    @Override
    public void checkDailySettle(String username) {
        User user = uDao.getByUsername(username);
        if (user.getId() == Global.USER_TOP_ID) {
            return;
        }
        //TODO bojin 关闭
//        if (uCodePointUtil.isLevel1Proxy(user)) {
//            adjustNeibuZhaoShang(user);
//        }
//        else if (uCodePointUtil.isLevel2Proxy(user)) {
//            adjustZhaoShang(user);
//        }
//        else {
            uDailySettleDao.deleteByTeam(user.getId());
//        }
    }

    /**
     * 内部招商。内部招商没有日结，但其下级招商有
     */
    /*private void adjustNeibuZhaoShang(User user) {
        UserDailySettle uDailySettle = uDailySettleDao.getByUserId(user.getId());

        double scale = dataFactory.getDailySettleConfig().getNeibuZhaoShangScale();
        int minValidUser = dataFactory.getDailySettleConfig().getNeibuZhaoShangMinValidUser();
        int initStatus = Global.DAILY_SETTLE_VALID;
        int fixed = Global.DAIYU_FIXED_FIXED; // 浮动比例

        if (uDailySettle == null) {
            // 原为空，新增
            add(user, scale, minValidUser, initStatus, fixed, scale, scale);
        }
        else {
            uDailySettleDao.updateSomeFields(uDailySettle.getId(), scale, minValidUser, initStatus, fixed, scale, scale);
        }

        // 查找其下所有招商号
        List<User> directLowers = uDao.getUserDirectLower(user.getId());
        if (CollectionUtils.isEmpty(directLowers)) {
            return;
        }

        for (User directLower : directLowers) {
            boolean isZhaoShang = uCodePointUtil.isLevel2Proxy(directLower);
            if (isZhaoShang) {
                adjustZhaoShang(directLower);
            }
            else {
                uDailySettleDao.deleteByTeam(directLower.getId());
            }
        }
    }*/

    /**
     * 招商。仅保留招商的契约配置，其余全部删除，因为只有招商的是自动的，其它都是手动的，账户等级发生变化，则需要重置用户手动签的契约
     */
   /* private void adjustZhaoShang(User user) {
        UserDailySettle uDailySettle = uDailySettleDao.getByUserId(user.getId());

        double scale;
        if (user.getIsCjZhaoShang() == Global.DAIYU_IS_CJ_ZHAO_SHANG_YES) {
            scale = dataFactory.getDailySettleConfig().getCjZhaoShangScale();
        }
        else {
            scale = dataFactory.getDailySettleConfig().getZhaoShangScale();
        }

        int minValidUser = dataFactory.getDailySettleConfig().getZhaoShangMinValidUser();
        int initStatus = Global.DAILY_SETTLE_VALID;
        int fixed = Global.DAIYU_FIXED_FIXED; // 固定比例
        if (uDailySettle == null) {
            // 原为空，新增
            add(user, scale, minValidUser, initStatus, fixed, scale, scale);
        }
        else {
            uDailySettleDao.updateSomeFields(uDailySettle.getId(), scale, minValidUser, initStatus, fixed, scale, scale);
        }

        // 删除其下所有契约
        uDailySettleDao.deleteLowers(user.getId());
    }*/
    
    @Override
	public boolean checkValidLevel(String scaleLevel, String salesLevel, String lossLevel, UserDailySettle upDailySettle, String usersLevel) {
		if (!StringUtil.isNotNull(scaleLevel) || !StringUtil.isNotNull(salesLevel) || !StringUtil.isNotNull(lossLevel) || !StringUtil.isNotNull(usersLevel)) {
			return false;
		}
		
		if(null == upDailySettle){
			return checkStartDailyLevel(scaleLevel, salesLevel, lossLevel, usersLevel);
		}
		
		String[] scaleArrs = scaleLevel.split(",");
		String[] upScaleArrs = upDailySettle.getScaleLevel().split(",");

		String[] salesArrs = salesLevel.split(",");
		String[] upSalesArrs = upDailySettle.getSalesLevel().split(",");

		String[] lossArrs = lossLevel.split(",");
		String[] upLossArrs = upDailySettle.getLossLevel().split(",");
		
		String[] userArrs = usersLevel.split(",");
		String[] upUserArrs = upDailySettle.getUserLevel().split(",");

		int maxLength = dataFactory.getDailySettleConfig().getMaxSignLevel();
		if (scaleArrs.length > maxLength || salesArrs.length > maxLength || lossArrs.length > maxLength || userArrs.length > maxLength) {
			return false;
		}

		if (scaleArrs.length != salesArrs.length || scaleArrs.length != lossArrs.length
				|| salesArrs.length != lossArrs.length || userArrs.length != scaleArrs.length) {
			return false;
		}

		double[] scaleConfig = dataFactory.getDailySettleConfig().getLevelsScale();
		for (int i = 0; i < scaleArrs.length; i++) {
			double val = Double.valueOf(scaleArrs[i]);
			// 满足系统配置条件
			if (null != upDailySettle) {
				if (val < scaleConfig[0] || val > Double.valueOf(upScaleArrs[upScaleArrs.length - 1])) {
					return false;
				}
			} else {
				if (val < scaleConfig[0] || val > scaleConfig[1]) {
					return false;
				}
			}

			// 必须是递增条款
			if (i > 0 && (Double.valueOf(scaleArrs[i - 1]) >= val)) {
				return false;
			}

			/*
			 *签署规则：销量、亏损大于等于上级某条款，并且分红比例小于等于上级某条款。
			 */
			double tmSales = Double.valueOf(salesArrs[i]);
			double tmLoss = Double.valueOf(lossArrs[i]);

			// 获取需要匹配的上级条款索引
			int upIndex = -1;
			for (int j = 0; j < upScaleArrs.length; j++) {
				// 找到最配的上级条款
				double tmUpSales = Double.valueOf(upSalesArrs[j]);
				double tmUpLoss = Double.valueOf(upLossArrs[j]);
				if(tmSales >= tmUpSales && tmLoss >= tmUpLoss){
					upIndex = j;
				}
			}
			if (upIndex == -1) {
				return false;
			}

			//比对与上级的比例
			double tmUpScale = Double.valueOf(upScaleArrs[upIndex]);
			if (val > tmUpScale) {
				//再次比对与上级下一个比例
				upIndex++;
				if(upIndex >= upScaleArrs.length){
					return false;
				}else{
					double tmUpSales = Double.valueOf(upSalesArrs[upIndex]);
					double tmUpLoss = Double.valueOf(upLossArrs[upIndex]);
					if(tmSales >= tmUpSales && tmLoss >= tmUpLoss){
						tmUpScale = Double.valueOf(upScaleArrs[upIndex]);
						if (val > tmUpScale) {
							return false;
						}
					}else{
						return false;
					}
				}
			}
		}

		double[] salesConfig = dataFactory.getDailySettleConfig().getLevelsSales();
		for (int i = 0; i < salesArrs.length; i++) {
			double val = Double.valueOf(salesArrs[i]);
			// 满足系统配置条件
			if (null != upDailySettle) {

				double minSales = Double.valueOf(upSalesArrs[0]);
				for (String l : upLossArrs) {
					double ll = Double.valueOf(l);
					if (ll < minSales) {
						minSales = ll;
					}
				}

				if (val < minSales || val > salesConfig[1]) {
					return false;
				}
			} else {
				if (val < salesConfig[0] || val > salesConfig[1]) {
					return false;
				}
			}

			// 必须是递增条款
			// if (i>0 && (Double.valueOf(salesArrs[i-1]) >= val)) {
			// return false;
			// }
		}

		double[] lossConfig = dataFactory.getDailySettleConfig().getLevelsLoss();
		for (int i = 0; i < lossArrs.length; i++) {
			double val = Double.valueOf(lossArrs[i]);
			// 满足系统配置条件
			if (null != upDailySettle) {

				double minLoss = Double.valueOf(upLossArrs[0]);
				for (String l : upLossArrs) {
					double ll = Double.valueOf(l);
					if (ll < minLoss) {
						minLoss = ll;
					}
				}
				if (val < minLoss || val > lossConfig[1]) {
					return false;
				}
			} else {
				if (val < lossConfig[0] || val > lossConfig[1]) {
					return false;
				}
			}

			// 亏损不能大于销量
			if (Double.valueOf(lossArrs[i]) > Double.valueOf(salesArrs[i])) {
				return false;
			}

			// 必须是递增条款
			// if (i>0 && (Double.valueOf(lossArrs[i-1]) >= val)) {
			// return false;
			// }
		}
		
		int[] userConfig = {dataFactory.getDailySettleConfig().getMinValidUserl(), 1000};
		for (int i = 0; i < userArrs.length; i++) {
			int val = Integer.valueOf(userArrs[i]);
			// 满足系统配置条件
			if (null != upDailySettle) {

				int minUser = Integer.valueOf(upUserArrs[0]);
				for (String l : upUserArrs) {
					int ll = Integer.valueOf(l);
					if (ll < minUser) {
						minUser = ll;
					}
				}
				if (val < minUser || val > userConfig[1]) {
					return false;
				}
			} else {
				if (val < userConfig[0] || val > userConfig[1]) {
					return false;
				}
			}

			// 必须是同样人数或者递增人数
			 if (i >0 && (Integer.valueOf(userArrs[i-1]) > val)) {
				 return false;
			 }
		}
		
		return true;
	}
	
	private boolean checkStartDailyLevel(String scaleLevel, String salesLevel, String lossLevel, String userLevel){
		String[] scaleArrs = scaleLevel.split(",");

		String[] salesArrs = salesLevel.split(",");

		String[] lossArrs = lossLevel.split(",");
		
		String[] userArrs = userLevel.split(",");

		int maxLength = dataFactory.getDailySettleConfig().getMaxSignLevel();
		if (scaleArrs.length > maxLength || salesArrs.length > maxLength || lossArrs.length > maxLength || userArrs.length > maxLength) {
			return false;
		}

		if (scaleArrs.length != salesArrs.length || scaleArrs.length != lossArrs.length	|| salesArrs.length != lossArrs.length || scaleArrs.length != userArrs.length) {
			return false;
		}

		double[] scaleConfig = dataFactory.getDailySettleConfig().getLevelsScale();
		for (int i = 0; i < scaleArrs.length; i++) {
			double val = Double.valueOf(scaleArrs[i]);
			// 满足系统配置条件
			if (val < scaleConfig[0] || val > scaleConfig[1]) {
				return false;
			}

			// 必须是递增条款
			if (i > 0 && (Double.valueOf(scaleArrs[i - 1]) >= val)) {
				return false;
			}
		}
		
		int[] userConfig = {dataFactory.getDailySettleConfig().getMinValidUserl(), 1000};
		for (int i = 0; i < userArrs.length; i++) {
			int val = Integer.valueOf(userArrs[i]);
			// 满足系统配置条件
			if (val < userConfig[0] || val > userConfig[1]) {
				return false;
			}

			// 必须是递增条款
			if (i > 0 && (Integer.valueOf(userArrs[i - 1]) > val)) {
				return false;
			}
		}

		double[] salesConfig = dataFactory.getDailySettleConfig().getLevelsSales();
		for (int i = 0; i < salesArrs.length; i++) {
			double val = Double.valueOf(salesArrs[i]);
			// 满足系统配置条件
			if (val < salesConfig[0] || val > salesConfig[1]) {
				return false;
			}
		}
		
		

		double[] lossConfig = dataFactory.getDailySettleConfig().getLevelsLoss();
		for (int i = 0; i < lossArrs.length; i++) {
			double val = Double.valueOf(lossArrs[i]);
			// 满足系统配置条件
			if (val < lossConfig[0] || val > lossConfig[1]) {
				return false;
			}

			// 亏损不能大于销量
			if (Double.valueOf(lossArrs[i]) > Double.valueOf(salesArrs[i])) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public double[] getMinMaxSales(User acceptUser) {
		double minSales = dataFactory.getDailySettleConfig().getLevelsSales()[0];
		double maxSales = dataFactory.getDailySettleConfig().getLevelsSales()[1];

		User requestUser = uService.getById(acceptUser.getUpid());
		if (uCodePointUtil.isLevel3Proxy(acceptUser)) {
			return new double[] { minSales, maxSales };
		}

		UserDailySettle upDividend = uDailySettleDao.getByUserId(requestUser.getId());
		if (upDividend == null) {
			return new double[] { 0, 0 };
		}

		maxSales = dataFactory.getDailySettleConfig().getLevelsSales()[1];
		// 直属以下
		String[] salesLevel = upDividend.getSalesLevel().split(",");
		minSales = Double.valueOf(salesLevel[0]);
		for (String l : salesLevel) {
			double ll = Double.valueOf(l);
			if (ll < minSales) {
				minSales = ll;
			}
		}

		if (minSales < 0)
			minSales = 0;
		if (maxSales < 0)
			maxSales = 0;

		return new double[] { minSales, maxSales };
	}
	
	/**
	 * 获取分红 最大最小 亏损
	 * 
	 * @param acceptUser
	 * @return
	 */
	@Override
	public double[] getMinMaxLoss(User acceptUser) {
		double minLoss = dataFactory.getDailySettleConfig().getLevelsLoss()[0];
		double maxLoss = dataFactory.getDailySettleConfig().getLevelsLoss()[1];

		User requestUser = uService.getById(acceptUser.getUpid());
		if (uCodePointUtil.isLevel3Proxy(acceptUser)) {
			return new double[]{minLoss, maxLoss};
		}

		UserDailySettle upDividend = uDailySettleDao.getByUserId(requestUser.getId());
		if (upDividend == null) {
			return new double[] { 0, 0 };
		}

		maxLoss = dataFactory.getDailySettleConfig().getLevelsLoss()[1];
		// 直属以下
		String[] lossArr = upDividend.getLossLevel().split(",");
		minLoss = Double.valueOf(lossArr[0]);
		for (String l : lossArr) {
			double ll = Double.valueOf(l);
			if (ll < minLoss) {
				minLoss = ll;
			}
		}

		if (minLoss < 0)
			minLoss = 0;
		if (maxLoss < 0)
			maxLoss = 0;

		return new double[] { minLoss, maxLoss };
	}
	
	/**
	 * 获取分红 最大最小 人数
	 * 
	 * @param acceptUser
	 * @return
	 */
	@Override
	public int[] getMinMaxUsers(User acceptUser) {
		int minUser = dataFactory.getDailySettleConfig().getMinValidUserl();
		int maxUser = 1000;
		
		User requestUser = uService.getById(acceptUser.getUpid());
		if (uCodePointUtil.isLevel3Proxy(acceptUser)) {
			return new int[]{minUser, maxUser};
		}

		UserDailySettle upDividend = uDailySettleDao.getByUserId(requestUser.getId());
		if (upDividend == null) {
			return new int[] { 0, 0 };
		}

		// 直属以下
		String[] lossArr = upDividend.getUserLevel().split(",");
		minUser = Integer.valueOf(lossArr[0]);
		for (String l : lossArr) {
			int ll = Integer.valueOf(l);
			if (ll < minUser) {
				minUser = ll;
			}
		}

		if (minUser < 0)
			minUser = 0;

		return new int[] { minUser, maxUser };
	}
    /*****************每个项目都需要根据需求调整**********************/
}
