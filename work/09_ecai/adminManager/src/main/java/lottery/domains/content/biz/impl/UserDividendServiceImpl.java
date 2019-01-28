package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import admin.web.WebJSONObject;
import javautils.StringUtil;
import javautils.date.Moment;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserDividendService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserDividendDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDividend;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.user.UserDividendVO;
import lottery.domains.pool.LotteryDataFactory;
import lottery.web.content.utils.UserCodePointUtil;

@Service
public class UserDividendServiceImpl implements UserDividendService {

	@Autowired
	private UserDividendDao uDividendDao;
	@Autowired
	private LotteryDataFactory dataFactory;
	@Autowired
	private UserDao uDao;
	@Autowired
	private UserService uService;
	@Autowired
	private UserCodePointUtil uCodePointUtil;

	@Override
	public PageList search(List<Integer> userIds, String sTime, String eTime, Double minScale, Double maxScale,
			Integer minValidUser, Integer maxValidUser, Integer status, Integer fixed, int start, int limit) {
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

		if (fixed != null) {
			criterions.add(Restrictions.eq("fixed", fixed));
		}

		// 排序条件
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id"));
		PageList pList = uDividendDao.search(criterions, orders, start, limit);
		List<UserDividendVO> convertList = new ArrayList<>();
		if (pList != null && pList.getList() != null) {
			for (Object tmpBean : pList.getList()) {
				convertList.add(new UserDividendVO((UserDividend) tmpBean, dataFactory));
			}
		}
		pList.setList(convertList);
		return pList;
	}

	@Override
	public UserDividend getByUserId(int userId) {
		return uDividendDao.getByUserId(userId);
	}

	@Override
	public UserDividend getById(int id) {
		return uDividendDao.getById(id);
	}

	// private boolean add(User user, double scale, int minValidUser, int
	// status, int fixed, double minScale,
	// double maxScale, String remarks) {
	// UserDividend bean = uDividendDao.getByUserId(user.getId());
	// if (bean == null) {
	// int userId = user.getId();
	// Moment moment = new Moment();
	// String createTime = moment.toSimpleTime();
	// String createDate = moment.toSimpleDate();
	// String endDate = moment.add(99, "years").toSimpleDate();
	// String agreeTime = status == 2? "" : createTime;
	// UserDividend entity = new UserDividend(userId, scale, minValidUser,
	// createTime, agreeTime, createDate,
	// endDate, 0, 0, status, fixed, minScale, maxScale, remarks);
	// uDividendDao.add(entity);
	// return true;
	// }
	// return false;
	// }

	private boolean add(User user, String scaleLevel, String salesLevel, String lossLevel, int minValidUser, int status,
			int fixed, double minScale, double maxScale, String remarks, String userLevel) {
		UserDividend bean = uDividendDao.getByUserId(user.getId());
		if (bean == null) {
			int userId = user.getId();
			Moment moment = new Moment();
			String createTime = moment.toSimpleTime();
			String createDate = moment.toSimpleDate();
			String endDate = moment.add(99, "years").toSimpleDate();
			String agreeTime = status == 2 ? "" : createTime;
			UserDividend entity = new UserDividend(userId, scaleLevel, lossLevel, salesLevel, minValidUser, createTime,
					agreeTime, createDate, endDate, 0, 0, status, fixed, minScale, maxScale, remarks, userLevel);
			uDividendDao.add(entity);
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteByTeam(String username) {
		User uBean = uService.getByUsername(username);
		if (uBean != null) {
			uDividendDao.deleteByTeam(uBean.getId());
			return true;
		}
		return false;
	}

	@Override
	public boolean changeZhaoShang(User user, boolean changeToCJZhaoShang) {
		// TODO 废弃招商/超级招商
		// UserDividend uDividend = uDividendDao.getByUserId(user.getId());
		// if (uDividend == null) {
		// return false;
		// }
		//
		// if (!uCodePointUtil.isLevel2Proxy(user)) {
		// return false;
		// }
		//
		// double scale = 0;
		// double minScale;
		// double maxScale;
		// int minValidUser =
		// dataFactory.getDividendConfig().getZhaoShangMinValidUser();
		// if (changeToCJZhaoShang) {
		// minScale = dataFactory.getDividendConfig().getCjZhaoShangMinScale();
		// maxScale = dataFactory.getDividendConfig().getCjZhaoShangMaxScale();
		// } else {
		// minScale = dataFactory.getDividendConfig().getZhaoShangMinScale();
		// maxScale = dataFactory.getDividendConfig().getZhaoShangMaxScale();
		// }
		//
		// return uDividendDao.updateSomeFields(uDividend.getId(), "", "", "",
		// minValidUser, minScale, maxScale);
		return false;
	}

	@Override
	public void checkDividend(String username) {
		// TODO bojin 1990 自动签署
		User user = uDao.getByUsername(username);
		if (user.getId() == Global.USER_TOP_ID) {
			return;
		}

//		if (user.getCode() == dataFactory.getDividendConfig().getStartLevel()) {
//			adjustDividend1990(user);
//		} else if(user.getCode() > dataFactory.getDividendConfig().getStartLevel()) {
//			uDividendDao.deleteByTeam(user.getId());
//		}
		
		uDividendDao.deleteByTeam(user.getId());

		// if (uCodePointUtil.isLevel1Proxy(user)) {
		// adjustNeibuZhaoShang(user);
		// } else if (uCodePointUtil.isLevel2Proxy(user)) {
		// adjustZhaoShang(user);
		// } else {
		// uDividendDao.deleteByTeam(user.getId());
		// }
	}

	private void adjustDividend1990(User user) {
		UserDividend uDividend = uDividendDao.getByUserId(user.getId());
		// 获取配置
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
		// 有则修改
		if (uDividend != null) {
			int id = uDividend.getId();
			uDividendDao.updateSomeFields(id, scaleLevel, lossLevel, salesLevel, minValidUser, fixed, minScale,
					maxScale, status);
		} else {// 没有则新增
			add(user, scaleLevel, salesLevel, lossLevel, minValidUser, status, fixed, minScale, maxScale, remarks, "");
		}
		// 删除旗下所有契约
		uDividendDao.deleteLowers(user.getId());
	}

	/**
	 * 内部招商。内部招商没有分红，但其下级招商有
	 */
	// private void adjustNeibuZhaoShang(User user) {
	// UserDividend uDividend = uDividendDao.getByUserId(user.getId());
	// if (uDividend != null) {
	// uDividendDao.deleteByUser(user.getId());
	// }
	//
	// // 查找其下所有招商号
	// List<User> directLowers = uDao.getUserDirectLower(user.getId());
	// if (CollectionUtils.isEmpty(directLowers)) {
	// return;
	// }
	//
	// for (User directLower : directLowers) {
	// boolean isZhaoShang = uCodePointUtil.isLevel2Proxy(directLower);
	// if (isZhaoShang) {
	// adjustZhaoShang(directLower);
	// } else {
	// uDividendDao.deleteByTeam(directLower.getId());
	// }
	// }
	// }

	/**
	 * 招商。仅保留招商的契约配置，其余全部删除，因为只有招商的是自动的，其它都是手动的，账户等级发生变化，则需要重置用户手动签的契约
	 */
	// private void adjustZhaoShang(User user) {
	// // 获取对应配置
	// UserDividend uDividend = uDividendDao.getByUserId(user.getId());
	//
	//
	// int initStatus = Global.DIVIDEND_VALID;
	// int fixed = Global.DAIYU_FIXED_FLOAT;
	// if (uDividend == null) {
	// // 原为空，新增
	// add(user, scaleLevel, salesLevel, lossLevel, minValidUser, status, fixed,
	// minScale, maxScale, remarks)
	// } else {
	// uDividendDao.updateSomeFields();
	// }
	//
	// // 删除其下所有契约
	// uDividendDao.deleteLowers(user.getId());
	// }

	@Override
	public boolean update(WebJSONObject json, int id, String scaleLevel, String lossLevel, String salesLevel,int minValidUser, String userLevel) {
		// 获取分红数据
		UserDividend dividend = uDividendDao.getById(id);
		if (dividend == null) {
			json.set(1, "1-7"); // 数据不存在
			return false;
		}

		// 已经生效不允许修改
		if (dividend.getStatus() == 1) {
			return false;
		}

		if (dividend.getScaleLevel().equals(scaleLevel) && dividend.getSalesLevel().equals(salesLevel)
				&& dividend.getLossLevel().equals(lossLevel) && dividend.getUserLevel().equals(userLevel)) {
			json.set(1, "2-29");
			return false;
		}

		User user = uDao.getById(dividend.getUserId());
		UserDividend upDividend = uDividendDao.getByUserId(user.getUpid());
		if (!uCodePointUtil.isLevel2Proxy(user) && (upDividend == null || upDividend.getStatus() != 1)) {
			json.set(2, "2-3011");
			return false;
		}

		if (!checkValidLevel(scaleLevel, salesLevel, lossLevel, upDividend, userLevel)) {
			json.setWithParams(2, "2-3009");
			return false;
		}

		if (!checkCanEdit(json, user)) {
			return false;
		}

		double[] minMaxScale = getMinMaxScale(user);
		double minScale = minMaxScale[0];
		double maxScale = minMaxScale[1];
		String[] scaleLevels = scaleLevel.split(",");
		if (Double.valueOf(scaleLevels[0]) < minScale
				|| Double.valueOf(scaleLevels[scaleLevels.length - 1]) > maxScale) {
			json.setWithParams(2, "2-3009");
			return false;
		}

		double[] minMaxSales = getMinMaxSales(user);
		double minSales = minMaxSales[0];
		double maxSales = minMaxSales[1];
		String[] salesLevels = salesLevel.split(",");
		if (Double.valueOf(salesLevels[0]) < minSales
				|| Double.valueOf(salesLevels[salesLevels.length - 1]) > maxSales) {
			json.setWithParams(2, "2-3009");
			return false;
		}

		double[] minMaxLoss = getMinMaxLoss(user);
		double minLoss = minMaxLoss[0];
		double maxLoss = minMaxLoss[1];
		String[] lossLevels = lossLevel.split(",");
		if (Double.valueOf(lossLevels[0]) < minLoss || Double.valueOf(lossLevels[lossLevels.length - 1]) > maxLoss) {
			json.setWithParams(2, "2-3009");
			return false;
		}
		
		int[] minMaxUser = getMinMaxUser(user);
		int minUser = minMaxUser[0];
		int maxUser = minMaxUser[1];
		String[] userLevels = userLevel.split(",");
		if (Integer.valueOf(userLevels[0]) < minUser || Integer.valueOf(userLevels[userLevels.length - 1]) > maxUser) {
			json.setWithParams(2, "2-3006");
			return false;
		}
		
		return uDividendDao.updateSomeFields(id, scaleLevel, lossLevel, salesLevel, minValidUser,
				Double.valueOf(scaleLevels[0]), Double.valueOf(scaleLevels[scaleLevels.length - 1]), userLevel);
	}

	/**
	 * 获取分红 最大 最小 分红比率
	 */
	public double[] getMinMaxScale(User acceptUser) {
		// 本次允许发起的最小比例
    	double minScale = dataFactory.getDividendConfig().getLevelsScale()[0];
    	// 本次允许发起的最大比例
    	double maxScale = dataFactory.getDividendConfig().getLevelsScale()[1];

		User requestUser = uService.getById(acceptUser.getUpid());
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

	/**
	 * 获取分红 最大最小 销量
	 * 
	 * @param acceptUser
	 * @return
	 */
	public double[] getMinMaxSales(User acceptUser) {
		double minSales = dataFactory.getDividendConfig().getLevelsSales()[0];
		double maxSales = dataFactory.getDividendConfig().getLevelsSales()[1];

		User requestUser = uService.getById(acceptUser.getUpid());
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

	/**
	 * 获取分红 最大最小 亏损
	 * 
	 * @param acceptUser
	 * @return
	 */
	public double[] getMinMaxLoss(User acceptUser) {
		double minLoss = dataFactory.getDividendConfig().getLevelsLoss()[0];
		double maxLoss = dataFactory.getDividendConfig().getLevelsLoss()[1];

		User requestUser = uService.getById(acceptUser.getUpid());
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
	public boolean add(WebJSONObject json, String username, String scaleLevel, String lossLevel, String salesLevel,
			int minValidUser, int status, String userLevel) {
		User user = uDao.getByUsername(username);
		if (!checkCanEdit(json, user)) {
			return false;
		}

		UserDividend uDividend = uDividendDao.getByUserId(user.getId());
		if (uDividend != null) {
			json.set(2, "2-3010");
			return false;
		}

		// TODO 1997下级签署 由平台发放
		UserDividend upDividend = uDividendDao.getByUserId(user.getUpid());
		
		if (!uCodePointUtil.isLevel2Proxy(user) && (upDividend == null || upDividend.getStatus() != 1)) {
			json.set(2, "2-3011");
			return false;
		}

		if (!checkValidLevel(scaleLevel, salesLevel, lossLevel, upDividend, userLevel)) {
			json.setWithParams(2, "2-3009");
			return false;
		}

		double[] minMaxScale = getMinMaxScale(user);
		double minScale = minMaxScale[0];
		double maxScale = minMaxScale[1];
		String[] scaleLevels = scaleLevel.split(",");
		if (Double.valueOf(scaleLevels[0]) < minScale
				|| Double.valueOf(scaleLevels[scaleLevels.length - 1]) > maxScale) {
			json.setWithParams(2, "2-3009");
			return false;
		}

		double[] minMaxSales = getMinMaxSales(user);
		double minSales = minMaxSales[0];
		double maxSales = minMaxSales[1];
		String[] salesLevels = salesLevel.split(",");
		if (Double.valueOf(salesLevels[0]) < minSales
				|| Double.valueOf(salesLevels[salesLevels.length - 1]) > maxSales) {
			json.setWithParams(2, "2-3009");
			return false;
		}

		double[] minMaxLoss = getMinMaxLoss(user);
		double minLoss = minMaxLoss[0];
		double maxLoss = minMaxLoss[1];
		String[] lossLevels = lossLevel.split(",");
		if (Double.valueOf(lossLevels[0]) < minLoss || Double.valueOf(lossLevels[lossLevels.length - 1]) > maxLoss) {
			json.setWithParams(2, "2-3009");
			return false;
		}

		//验证最大最小人数
		int[] minMaxUser = getMinMaxUser(user);
		int minUser = minMaxUser[0];
		int maxUser = minMaxUser[1];
		String[] userLevels = userLevel.split(",");
		if (Integer.valueOf(userLevels[0]) < minUser || Integer.valueOf(userLevels[userLevels.length - 1]) > maxUser) {
			json.setWithParams(2, "2-3006");
			return false;
		}
		
		return add(user, scaleLevel, salesLevel, lossLevel, minValidUser, status,
				dataFactory.getDividendConfig().getFixedType(), Double.valueOf(scaleLevels[0]),
				Double.valueOf(scaleLevels[scaleLevels.length - 1]), "后台分红签署", userLevel);
	}

	@Override
	public boolean checkCanEdit(WebJSONObject json, User user) {
		if (user.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-33");
			return false;
		}

		if (uCodePointUtil.isLevel1Proxy(user)) {
			json.set(2, "2-36");
			return false;
		}

//		if (uCodePointUtil.isLevel2Proxy(user)) {
//			json.set(2, "2-39");
//			return false;
//		}

		return true;
	}

	@Override
	public boolean checkCanDel(WebJSONObject json, User user) {
		if (user.getId() == Global.USER_TOP_ID) {
			json.set(2, "2-33");
			return false;
		}

		if (uCodePointUtil.isLevel1Proxy(user)) {
			json.set(2, "2-36");
			return false;
		}

//		if (uCodePointUtil.isLevel2Proxy(user)) {
//			json.set(2, "2-39");
//			return false;
//		}

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
		if (!dataFactory.getDividendConfig().isEnable()) {
			return false;
		}

		if (user.getId() == Global.USER_TOP_ID) {
			return false;
		}

		// 1956以上或1800以下都是不可以发起的
		if (uCodePointUtil.isLevel1Proxy(user) || user.getCode() < 1800) {
			return false;
		}

		// if (user.getCode() == dataFactory.getCodeConfig().getSysCode()) {
		// // 内部招商不可以发起
		// if (uCodePointUtil.isLevel1Proxy(user)) {
		// return false;
		// }
		// }

		return true;
	}

	/**
	 * 是否允许接受契约分红
	 * 
	 * @param requestUser
	 *            发起人
	 * @param acceptUser
	 *            接受人
	 */
	public boolean allowAccept(User requestUser, User acceptUser) {
		if (!dataFactory.getDividendConfig().isEnable())
			return false;

		// 发起人必须是接受人的直属上级
		if (acceptUser.getUpid() != requestUser.getId())
			return false;

		// 总账不可以发起或接受
		if (requestUser.getId() == Global.USER_TOP_ID || acceptUser.getId() == Global.USER_TOP_ID)
			return false;

		// 1997 不可以发起或接受
		if (uCodePointUtil.isLevel1Proxy(requestUser) || uCodePointUtil.isLevel1Proxy(acceptUser))
			return false;

		// 1800以下不可以发起或接受
		if (requestUser.getCode() < 1800 || acceptUser.getCode() < 1800)
			return false;

		if (requestUser.getCode() == dataFactory.getCodeConfig().getSysCode()) {
			if (uCodePointUtil.isLevel1Proxy(requestUser) || uCodePointUtil.isLevel1Proxy(acceptUser)) {
				return false;
			}
			if (uCodePointUtil.isLevel2Proxy(acceptUser)) {
				return false;
			} else if (uCodePointUtil.isLevel2Proxy(requestUser)) {
				// 招商只允许对直属发起
				if (uCodePointUtil.isLevel3Proxy(acceptUser)) {
					return true;
				}
				return false;
			}
		}

		// TODO 其它账号只能对自己的平级或直属发起，但账号等级差异不允许超过2级
//		int diffCode = requestUser.getCode() - acceptUser.getCode();
//		if (diffCode > 2 || diffCode < 0)
//			return false;

		return true;
	}

	@Override
	public boolean checkValidLevel(String scaleLevel, String salesLevel, String lossLevel, UserDividend upDividend, String userLevel) {
		if (!StringUtil.isNotNull(scaleLevel) || !StringUtil.isNotNull(salesLevel) || !StringUtil.isNotNull(lossLevel) || !StringUtil.isNotNull(userLevel)) {
			return false;
		}
		
		if(null == upDividend){
			return checkStartDividendLevel(scaleLevel, salesLevel, lossLevel, userLevel);
		}
		
		String[] scaleArrs = scaleLevel.split(",");
		String[] upScaleArrs = upDividend.getScaleLevel().split(",");

		String[] salesArrs = salesLevel.split(",");
		String[] upSalesArrs = upDividend.getSalesLevel().split(",");

		String[] lossArrs = lossLevel.split(",");
		String[] upLossArrs = upDividend.getLossLevel().split(",");
		
		String[] userArrs = userLevel.split(",");
		String[] upUserArrs = upDividend.getUserLevel().split(",");

		int maxLength = dataFactory.getDividendConfig().getMaxSignLevel();
		if (scaleArrs.length > maxLength || salesArrs.length > maxLength || lossArrs.length > maxLength || userArrs.length > maxLength) {
			return false;
		}

		if (scaleArrs.length != salesArrs.length || scaleArrs.length != lossArrs.length
				|| salesArrs.length != lossArrs.length || userArrs.length != scaleArrs.length) {
			return false;
		}

		double[] scaleConfig = dataFactory.getDividendConfig().getLevelsScale();
		for (int i = 0; i < scaleArrs.length; i++) {
			double val = Double.valueOf(scaleArrs[i]);
			// 满足系统配置条件
			if (null != upDividend) {
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

		double[] salesConfig = dataFactory.getDividendConfig().getLevelsSales();
		for (int i = 0; i < salesArrs.length; i++) {
			double val = Double.valueOf(salesArrs[i]);
			// 满足系统配置条件
			if (null != upDividend) {

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

		double[] lossConfig = dataFactory.getDividendConfig().getLevelsLoss();
		for (int i = 0; i < lossArrs.length; i++) {
			double val = Double.valueOf(lossArrs[i]);
			// 满足系统配置条件
			if (null != upDividend) {

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
		
		int[] userConfig = {dataFactory.getDividendConfig().getMinValidUserl(), 1000};
		for (int i = 0; i < userArrs.length; i++) {
			int val = Integer.valueOf(userArrs[i]);
			// 满足系统配置条件
			if (null != upDividend) {

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
	
	private boolean checkStartDividendLevel(String scaleLevel, String salesLevel, String lossLevel, String userLevel){
		String[] scaleArrs = scaleLevel.split(",");

		String[] salesArrs = salesLevel.split(",");

		String[] lossArrs = lossLevel.split(",");
		
		String[] userArrs = userLevel.split(",");

		int maxLength = dataFactory.getDividendConfig().getMaxSignLevel();
		if (scaleArrs.length > maxLength || salesArrs.length > maxLength || lossArrs.length > maxLength || userArrs.length > maxLength) {
			return false;
		}

		if (scaleArrs.length != salesArrs.length || scaleArrs.length != lossArrs.length	|| salesArrs.length != lossArrs.length || scaleArrs.length != userArrs.length) {
			return false;
		}
		
		int[] userConfig = {dataFactory.getDividendConfig().getMinValidUserl(), 1000};
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

		double[] scaleConfig = dataFactory.getDividendConfig().getLevelsScale();
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

		double[] salesConfig = dataFactory.getDividendConfig().getLevelsSales();
		for (int i = 0; i < salesArrs.length; i++) {
			double val = Double.valueOf(salesArrs[i]);
			// 满足系统配置条件
			if (val < salesConfig[0] || val > salesConfig[1]) {
				return false;
			}
		}

		double[] lossConfig = dataFactory.getDividendConfig().getLevelsLoss();
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
	
	/**
	 * 获取分红 最大最小 人数
	 * 
	 * @param acceptUser
	 * @return
	 */
	@Override
	public int[] getMinMaxUser(User acceptUser) {
		int minUser = dataFactory.getDividendConfig().getMinValidUserl();
		int maxUser = 1000;
		
		User requestUser = uService.getById(acceptUser.getUpid());
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