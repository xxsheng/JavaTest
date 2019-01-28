package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.UserDividendService;
import lottery.domains.content.biz.UserSysMessageService;
import lottery.domains.content.biz.read.UserReadService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserDividendDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDividend;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import lottery.web.content.utils.UserCodePointUtil;
import lottery.web.content.validate.UserDividendValidate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserDividendServiceImpl implements UserDividendService {
	@Autowired
	private UserDividendDao uDividendDao;
	@Autowired
	private UserDao uDao;
	@Autowired
	private UserReadService uReadService;
	@Autowired
	private UserDividendValidate dividendValidate;
	@Autowired
	private UserCodePointUtil uCodePointUtil;
	@Autowired
	private UserSysMessageService userSysMessageService;
	@Autowired
	private DataFactory dataFactory;

	@Override
	@Transactional(readOnly = true)
	public UserDividend getByUserId(int userId) {
		return uDividendDao.getByUserId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDividend getById(int id) {
		return uDividendDao.getById(id);
	}

	@Override
	public boolean request(WebJSON json, int requestUserId, int acceptUserId, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, String userLevel) {
		// 检查发起契约请求是否合法，并返回相应错误码
		boolean valid = dividendValidate.isRequestLegal(json, requestUserId, acceptUserId, scaleLevel, lossLevel, salesLevel, minValidUser, userLevel);
		if (!valid) {
			return valid;
		}

		Moment moment = new Moment();
		String nowTime = moment.toSimpleTime();
		String agreeTime = null;
		String startDate = moment.toSimpleDate();
		String endDate = moment.add(99, "years").toSimpleDate();
		double totalAmount = 0;
		double lastAmount = 0;
		int status = Global.DIVIDEND_REQUESTED;
		UserDividend requestUserDividend = uDividendDao.getByUserId(requestUserId);
		int fixed = requestUserDividend == null ?dataFactory.getDividendConfig().getFixedType() : requestUserDividend.getFixed(); // 固定比例
		String remarks = null;

		// 删除原来的契约数据
		uDividendDao.deleteByTeam(acceptUserId);

		// 新增契约数据
		String[] scaleLevels = scaleLevel.split(",");
		UserDividend settle = new UserDividend(acceptUserId, scaleLevel, lossLevel, salesLevel, minValidUser, nowTime, agreeTime,
				startDate, endDate, totalAmount,lastAmount, status, fixed, 
				Double.valueOf(scaleLevels[0]), Double.valueOf(scaleLevels[scaleLevels.length - 1]), remarks, userLevel);
		uDividendDao.add(settle);

		// 发通知
		userSysMessageService.addDividendRequest(acceptUserId, requestUserId);

		return true;
	}

	@Override
	public boolean agree(WebJSON json, int userId, int id) {
		boolean valid = dividendValidate.isAgreeLegal(json, userId, id);
		if (!valid) {
			return valid;
		}

		uDividendDao.updateStatus(id, Global.DIVIDEND_VALID, Global.DIVIDEND_REQUESTED, userId);

		// 发通知
		User user = uDao.getById(userId);
		if (user != null) {
			userSysMessageService.addDividendAgree(user.getUsername(), user.getUpid());
		}

		return true;
	}

	@Override
	public boolean deny(WebJSON json, int userId, int id) {
		boolean valid = dividendValidate.isDenyLegal(json, userId, id);
		if (!valid) {
			return valid;
		}

		uDividendDao.updateStatus(id, Global.DIVIDEND_DENY, Global.DIVIDEND_REQUESTED, userId);

		// 发通知
		User user = uDao.getById(userId);
		if (user != null) {
			userSysMessageService.addDividendDeny(user.getUsername(), user.getUpid());
		}

		return true;
	}

	@Override
	public boolean deleteByTeam(int userId) {
		User uBean = uDao.getById(userId);
		if(uBean != null) {
			uDividendDao.deleteByTeam(uBean.getId());
			return true;
		}
		return false;
	}

	private UserDividend add(User user, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, int status, int fixed, double minScale, double maxScale, String remarks, String userLevel) {
		UserDividend bean = uDividendDao.getByUserId(user.getId());
		if(bean == null) {
			int userId = user.getId();
			Moment moment = new Moment();
			String createTime = moment.toSimpleTime();
			String createDate = moment.toSimpleDate();
			String endDate = moment.add(99, "years").toSimpleDate();
			UserDividend entity = new UserDividend(userId, scaleLevel, lossLevel, salesLevel , minValidUser, createTime, createTime, createDate, endDate, 0, 0, status, fixed, minScale, maxScale, remarks, userLevel);
			uDividendDao.add(entity);
			return entity;
		}
		return null;
	}

	/**
	 * TODO 每个项目都需要根据需求调整
	 */
	@Override
	public void checkDividend(int userId) {
			//TODO  bojin 1990 自动签署
			User user = uDao.getById(userId);
			if (user.getId() == Global.USER_TOP_ID) {
				return;
			}
			
//			if (user.getCode() == dataFactory.getDividendConfig().getStartLevel()) {
//				adjustDividend1990(user);
//			} else if(user.getCode() > dataFactory.getDividendConfig().getStartLevel()) {
//				uDividendDao.deleteByTeam(user.getId());
//			}
			uDividendDao.deleteByTeam(user.getId());
	}
	
	private void adjustDividend1990(User user){
		UserDividend uDividend = uDividendDao.getByUserId(user.getId());
		//获取配置
		int minValidUser = dataFactory.getDividendConfig().getZhaoShangMinValidUser();
		int fixed = dataFactory.getDividendConfig().getFixedType();
		int status = 1;
		String scaleLevel = dataFactory.getDividendConfig().getZhaoShangScaleLevels();
		String[] scaleLevelArr = scaleLevel.split(","); 
		String lossLevel = dataFactory.getDividendConfig().getZhaoShangLossLevels();
		String salesLevel = dataFactory.getDividendConfig().getZhaoShangSalesLevels();
		double minScale = Double.valueOf(scaleLevelArr[0]);
		double maxScale = Double.valueOf(scaleLevelArr[scaleLevelArr.length - 1]);
		String remarks = "自动分红配置";
		String userLevel = "";
		//有则修改
		if(uDividend != null){
			int id = uDividend.getId();
			uDividendDao.updateSomeFields(id, scaleLevel, lossLevel, salesLevel, minValidUser, fixed, minScale, maxScale, status);
		}else{//没有则新增
			add(user, scaleLevel, lossLevel, salesLevel, minValidUser, status, fixed, minScale, maxScale, remarks, userLevel);
		}
		//删除旗下所有契约
		uDividendDao.deleteLowers(user.getId());
	}

	/**
	 * 内部招商。内部招商没有分红，但其下级招商有
	 */
	private void adjustNeibuZhaoShang(User user) {
		UserDividend uDividend = uDividendDao.getByUserId(user.getId());
		if (uDividend != null) {
			uDividendDao.deleteByUser(user.getId());
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
				uDividendDao.deleteByTeam(directLower.getId());
			}
		}
	}


	/**
	 * 招商。仅保留招商的契约配置，其余全部删除，因为只有招商的是自动的，其它都是手动的，账户等级发生变化，则需要重置用户手动签的契约
	 */
	private void adjustZhaoShang(User user) {
		// 对应配置
//		UserDividend uDividend = uDividendDao.getByUserId(user.getId());
//
//		double scale = 0d;
//		double minScale;
//		double maxScale;
//
//		if (user.getIsCjZhaoShang() == Global.DAIYU_IS_CJ_ZHAO_SHANG_YES) {
//			minScale = dataFactory.getDividendConfig().getCjZhaoShangMinScale();
//			maxScale = dataFactory.getDividendConfig().getCjZhaoShangMaxScale();
//		}
//		else {
//			minScale = dataFactory.getDividendConfig().getZhaoShangMinScale();
//			maxScale = dataFactory.getDividendConfig().getZhaoShangMaxScale();
//		}
//
//		int minValidUser = dataFactory.getDividendConfig().getZhaoShangMinValidUser();
//		int initStatus = Global.DIVIDEND_VALID;
//		int fixed = Global.DAIYU_FIXED_FLOAT;
//		if (uDividend == null) {
//			// 原为空，新增
//			add(user, scale, minValidUser, initStatus, fixed, minScale, maxScale, "自动分红配置");
//		}
//		else {
//			uDividendDao.updateSomeFields(uDividend.getId(), scale, minValidUser, fixed, minScale, maxScale, initStatus);
//		}

		// 删除其下所有契约
		uDividendDao.deleteLowers(user.getId());
	}

	@Override
	public double[] getMinMaxScale(User acceptUser) {
		// 本次允许发起的最小比例
    	double minScale = dataFactory.getDividendConfig().getLevelsScale()[0];
    	// 本次允许发起的最大比例
    	double maxScale = dataFactory.getDividendConfig().getLevelsScale()[1];

		User requestUser = uDao.getById(acceptUser.getUpid());
		if (uCodePointUtil.isLevel2Proxy(acceptUser)) {
			return new double[] { minScale, maxScale };
		}

		UserDividend upDividend = uDividendDao.getByUserId(requestUser.getId());
		if (upDividend == null) {
			return new double[] { 0, 0 };
		}

		minScale = dataFactory.getDividendConfig().getLevelsScale()[0];
//		if (uCodePointUtil.isLevel2Proxy(acceptUser)) {
//			if (upDividend == null) {
//				maxScale = dataFactory.getDividendConfig().getLevelsScale()[1];
//			} else {
//				String[] scaleArr = upDividend.getScaleLevel().split(",");
//				maxScale = Double.valueOf(scaleArr[scaleArr.length - 1]);
//			}
//
//		} else {
			// 直属以下
			String[] scaleArr = upDividend.getScaleLevel().split(",");
			maxScale = Double.valueOf(scaleArr[scaleArr.length - 1]);
			if (maxScale > dataFactory.getDividendConfig().getLevelsScale()[1]) {
				maxScale = dataFactory.getDividendConfig().getLevelsScale()[1];
			}
//		}

		if (minScale < 0)
			minScale = 0;
		if (maxScale < 0)
			maxScale = 0;

		return new double[] { minScale, maxScale };
	}

	@Override
	public double[] getMinMaxSales(User acceptUser) {
		double minSales = dataFactory.getDividendConfig().getLevelsSales()[0];
		double maxSales = dataFactory.getDividendConfig().getLevelsSales()[1];

		User requestUser = uDao.getById(acceptUser.getUpid());
		if (uCodePointUtil.isLevel2Proxy(acceptUser)) {
			return new double[] { minSales, maxSales };
		}

		UserDividend upDividend = uDividendDao.getByUserId(requestUser.getId());
		if (upDividend == null) {
			return new double[] { 0, 0 };
		}

		maxSales = dataFactory.getDividendConfig().getLevelsSales()[1];
//		if (uCodePointUtil.isLevel2Proxy(acceptUser)) {
//			if (upDividend == null) {
//				minSales = dataFactory.getDividendConfig().getLevelsSales()[0];
//			} else {
//				String[] salesLevel = upDividend.getSalesLevel().split(",");
//				minSales = Double.valueOf(salesLevel[0]);
//				for (String l : salesLevel) {
//					double ll = Double.valueOf(l);
//					if (ll < minSales) {
//						minSales = ll;
//					}
//				}
//			}
//		} else {
			// 直属以下
			String[] salesLevel = upDividend.getSalesLevel().split(",");
			minSales = Double.valueOf(salesLevel[0]);
			for (String l : salesLevel) {
				double ll = Double.valueOf(l);
				if (ll < minSales) {
					minSales = ll;
				}
			}
//		}

		if (minSales < 0)
			minSales = 0;
		if (maxSales < 0)
			maxSales = 0;

		return new double[] { minSales, maxSales };
	}

	@Override
	public double[] getMinMaxLoss(User acceptUser) {
		double minLoss = dataFactory.getDividendConfig().getLevelsLoss()[0];
		double maxLoss = dataFactory.getDividendConfig().getLevelsLoss()[1];

		User requestUser = uDao.getById(acceptUser.getUpid());
		if (uCodePointUtil.isLevel2Proxy(acceptUser)) {
			return new double[] { minLoss, maxLoss };
		}

		UserDividend upDividend = uDividendDao.getByUserId(requestUser.getId());
		if (upDividend == null) {
			return new double[] { 0, 0 };
		}

		maxLoss = dataFactory.getDividendConfig().getLevelsLoss()[1];
//		if (uCodePointUtil.isLevel2Proxy(acceptUser)) {
//			if (upDividend == null) {
//				minLoss = dataFactory.getDividendConfig().getLevelsLoss()[0];
//			} else {
//				String[] lossLevel = upDividend.getLossLevel().split(",");
//				minLoss = Double.valueOf(lossLevel[0]);
//				for (String l : lossLevel) {
//					double ll = Double.valueOf(l);
//					if (ll < minLoss) {
//						minLoss = ll;
//					}
//				}
//			}
//		} else {
			// 直属以下
			String[] lossArr = upDividend.getLossLevel().split(",");
			minLoss = Double.valueOf(lossArr[0]);
			for (String l : lossArr) {
				double ll = Double.valueOf(l);
				if (ll < minLoss) {
					minLoss = ll;
				}
			}
//		}

		if (minLoss < 0)
			minLoss = 0;
		if (maxLoss < 0)
			maxLoss = 0;

		return new double[] { minLoss, maxLoss };
	}
	
	@Override
	public int[] getMinMaxUser(User acceptUser) {
		int minUser = dataFactory.getDividendConfig().getMinValidUserl();
		int maxUser = 1000;
		
		User requestUser = uDao.getById(acceptUser.getUpid());
		if (uCodePointUtil.isLevel2Proxy(acceptUser)) {
			return new int[]{minUser, maxUser};
		}

		UserDividend upDividend = uDividendDao.getByUserId(requestUser.getId());
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
}