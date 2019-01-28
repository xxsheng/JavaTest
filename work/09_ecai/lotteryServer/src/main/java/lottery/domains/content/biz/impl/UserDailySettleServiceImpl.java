package lottery.domains.content.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javautils.date.Moment;
import lottery.domains.content.biz.UserDailySettleService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.dao.UserDailySettleDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDailySettle;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import lottery.web.content.utils.UserCodePointUtil;
import lottery.web.content.validate.UserDailySettleValidate;

/**
 * 契约日结Service
 * Created by Nick on 2016/10/31.
 */
@Service
public class UserDailySettleServiceImpl implements UserDailySettleService {
    @Autowired
    private UserDailySettleDao uDailySettleDao;
    @Autowired
    private UserDao uDao;
    @Autowired
    private UserReadService uReadService;
    @Autowired
    private UserDailySettleValidate settleValidate;
    @Autowired
    private UserSysMessageService userSysMessageService;
    @Autowired
    private UserCodePointUtil uCodePointUtil;
    @Autowired
    private DataFactory dataFactory;

    @Override
    @Transactional(readOnly = true)
    public UserDailySettle getByUserId(int userId) {
        return uDailySettleDao.getByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDailySettle getById(int id) {
        return uDailySettleDao.getById(id);
    }

    @Override
    public boolean request(WebJSON json, int requestUserId, int acceptUserId, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, String userLevel) {
        // 检查发起契约请求是否合法，并返回相应错误码
        boolean valid = settleValidate.isRequestLegal(json, requestUserId, acceptUserId, scaleLevel, lossLevel, salesLevel, minValidUser, userLevel);
        if (!valid) {
            return valid;
        }

        Moment moment = new Moment();
        String nowTime = moment.toSimpleTime();
        String agreeTime = null;
        String startDate = moment.toSimpleDate();
        String endDate = moment.add(99, "years").toSimpleDate();
        double totalAmount = 0;
        int status = Global.DAILY_SETTLE_REQUESTED; // 待同意
        int fixed = Global.DAIYU_FIXED_FIXED; // 固定比例

        // 删除原来的契约数据
        uDailySettleDao.deleteByTeam(acceptUserId);

        // 新增契约数据
        String[] scaleLevels = scaleLevel.split(",");
        UserDailySettle settle = new UserDailySettle(acceptUserId, scaleLevel, lossLevel, salesLevel,
                minValidUser, nowTime, agreeTime, startDate, endDate, totalAmount, status, fixed, 
                Double.valueOf(scaleLevels[0]), Double.valueOf(scaleLevels[scaleLevels.length - 1]), userLevel);
        uDailySettleDao.add(settle);

        // 发通知
        userSysMessageService.addDailySettleRequest(acceptUserId, requestUserId);

        return true;
    }

    @Override
    public boolean agree(WebJSON json, int userId, int id) {
        boolean valid = settleValidate.isAgreeLegal(json, userId, id);
        if (!valid) {
            return valid;
        }

        uDailySettleDao.updateStatus(id, Global.DAILY_SETTLE_VALID, Global.DAILY_SETTLE_REQUESTED);

        // 给上级发通知
        User user = uDao.getById(userId);
        if (user != null) {
            userSysMessageService.addDailySettleAgree(user.getUsername(), user.getUpid());
        }

        return true;
    }

    @Override
    public boolean deny(WebJSON json, int userId, int id) {
        boolean valid = settleValidate.isDenyLegal(json, userId, id);
        if (!valid) {
            return valid;
        }

        uDailySettleDao.updateStatus(id, Global.DAILY_SETTLE_DENY, Global.DAILY_SETTLE_REQUESTED);

        // 发通知
        User user = uDao.getById(userId);
        if (user != null) {
            userSysMessageService.addDailySettleDeny(user.getUsername(), user.getUpid());
        }

        return true;
    }

    private UserDailySettle add(User user, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, int status, int fixed, double minScale, double maxScale, String userLevel) {
        UserDailySettle bean = uDailySettleDao.getByUserId(user.getId());
        if(bean == null) {
            int userId = user.getId();
            Moment moment = new Moment();
            String createTime = moment.toSimpleTime();
            String createDate = moment.toSimpleDate();
            String endDate = moment.add(99, "years").toSimpleDate();
            double totalAmount = 0;
            UserDailySettle entity = new UserDailySettle(userId, scaleLevel, lossLevel, salesLevel, minValidUser, createTime, createTime, createDate, endDate, totalAmount, status, fixed, minScale, maxScale, userLevel);
            uDailySettleDao.add(entity);
            return entity;
        }
        return null;
    }


    /*****************TODO 每个项目都需要根据需求调整**********************/
    @Override
    public void checkDailySettle(int userId) {
        User user = uDao.getById(userId);
        if (user.getId() == Global.USER_TOP_ID) {
            return;
        }

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
        List<User> directLowers = uReadService.getUserDirectLowerFromRead(user.getId());
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
    /*private void adjustZhaoShang(User user) {
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
        int fixed = Global.DAIYU_FIXED_FIXED; // 浮动比例
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
	public double[] getMinMaxScale(User acceptUser) {
		double minScale = 0;
		double maxScale = 0;

		User requestUser = uDao.getById(acceptUser.getUpid());
//		if (uCodePointUtil.isLevel1Proxy(requestUser) || uCodePointUtil.isLevel2Proxy(acceptUser)) {
//			return new double[] { 0, 0 };
//		}

		UserDailySettle upDividend = uDailySettleDao.getByUserId(requestUser.getId());
		if (!uCodePointUtil.isLevel3Proxy(acceptUser) && upDividend == null) {
			return new double[] { 0, 0 };
		}
		
		minScale = dataFactory.getDailySettleConfig().getLevelsScale()[0];
		// 直属以下
		String[] scaleArr = upDividend.getScaleLevel().split(",");
		maxScale = Double.valueOf(scaleArr[scaleArr.length - 1]);
		if (maxScale > dataFactory.getDailySettleConfig().getLevelsScale()[1]) {
			maxScale = dataFactory.getDailySettleConfig().getLevelsScale()[1];
		}

		if (minScale < 0) 
			minScale = 0;
		if (maxScale < 0)
			maxScale = 0;

		return new double[] { minScale, maxScale };
	}

	@Override
	public double[] getMinMaxSales(User acceptUser) {
		double minSales = 0;
		double maxSales = 0;

		User requestUser = uDao.getById(acceptUser.getUpid());
//		if (uCodePointUtil.isLevel1Proxy(requestUser) || uCodePointUtil.isLevel2Proxy(acceptUser)) {
//			return new double[] { 0, 0 };
//		}

		UserDailySettle upDividend = uDailySettleDao.getByUserId(requestUser.getId());
		if (!uCodePointUtil.isLevel3Proxy(acceptUser) && upDividend == null) {
			return new double[] { 0, 0 };
		}

		maxSales = dataFactory.getDailySettleConfig().getLevelsSales()[1];
		// 直属以下
		String[] salesLevel = upDividend.getSalesLevel().split(",");
		minSales = Double.valueOf(salesLevel[0]); 
		for (String l : salesLevel) {
			double ll =Double.valueOf(l); 
			if(ll < minSales){
				minSales = ll;
			}
		}

		if (minSales < 0) 
			minSales = 0;
		if (maxSales < 0)
			maxSales = 0;

		return new double[] { minSales, maxSales };
	}

	@Override
	public double[] getMinMaxLoss(User acceptUser) {
		double minLoss = 0;
		double maxLoss = 0;

		User requestUser = uDao.getById(acceptUser.getUpid());
//		if (uCodePointUtil.isLevel1Proxy(requestUser) || uCodePointUtil.isLevel2Proxy(acceptUser)) {
//			return new double[] { 0, 0 };
//		}

		UserDailySettle upDividend = uDailySettleDao.getByUserId(requestUser.getId());
		if (!uCodePointUtil.isLevel3Proxy(acceptUser) && upDividend == null) {
			return new double[] { 0, 0 };
		}

		maxLoss = dataFactory.getDailySettleConfig().getLevelsLoss()[1];
		// 直属以下
		String[] lossArr = upDividend.getLossLevel().split(",");
		minLoss = Double.valueOf(lossArr[0]); 
		for (String l : lossArr) {
			double ll =Double.valueOf(l); 
			if(ll < minLoss){
				minLoss = ll;
			}
		}

		if (minLoss < 0) 
			minLoss = 0;
		if (maxLoss < 0)
			maxLoss = 0;

		return new double[] { minLoss, maxLoss };
	}
	
	@Override
	public int[] getMinMaxUser(User acceptUser) {
		int minUser = dataFactory.getDailySettleConfig().getMinValidUserl();
		int maxUser = 1000;
		
		User requestUser = uDao.getById(acceptUser.getUpid());
		
		UserDailySettle upDividend = uDailySettleDao.getByUserId(requestUser.getId());
		if (!uCodePointUtil.isLevel3Proxy(acceptUser) && upDividend == null) {
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
