package lottery.domains.content.dao.impl;

import javautils.array.ArrayUtils;
import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
	
	public static final String tab = User.class.getSimpleName();
	
	@Autowired
	private HibernateSuperDao<User> superDao;
	
	@Override
	public boolean add(User entity) {
		return superDao.save(entity);
	}
	
	@Override
	public boolean update(User entity) {
		return superDao.update(entity);
	}

	@Override
	public List<User> listAll() {
		String hql = "from " + tab;
		return superDao.list(hql);
	}
	
	@Override
	public User getById(int id) {
		String hql = "from " + tab + " where id = ?0";
		Object[] values = {id};
		return (User) superDao.unique(hql, values);
	}

	@Override
	public User getByUsername(String username) {
		String hql = "from " + tab + " where username = ?0";
		Object[] values = {username};
		return (User) superDao.unique(hql, values);
	}
	
	@Override
	public boolean updateType(int id, int type) {
		String hql = "update " + tab + " set type = ?1 where id = ?0";
		Object[] values = {id, type};
		return superDao.update(hql, values);
	}

	@Override
	public boolean unbindGoogle(int id) {
		String hql = "update " + tab + " set secretKey = null,isBindGoogle = 0 where id = ?0";
		Object[] values = {id};
		return superDao.update(hql, values);
	}

	@Override
	public boolean resetLockTime(int id) {
		String hql = "update " + tab + " set lockTime = null where id = ?0";
		Object[] values = {id};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateWithdrawName(int id, String withdrawName) {
		String hql = "update " + tab + " set withdrawName = ?1 where id = ?0";
		Object[] values = {id, withdrawName};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateWithdrawPassword(int id, String md5Pwd) {
		String hql = "update " + tab + " set withdrawPassword = ?1 where id = ?0";
		Object[] values = {id, md5Pwd};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateLoginPwd(int id, String md5Pwd) {
		String hql = "update " + tab + " set password = ?1 where id = ?0";
		Object[] values = {id, md5Pwd};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateImgPwd(int id, String md5Pwd) {
		String hql = "update " + tab + " set imgPassword = ?1 where id = ?0";
		Object[] values = {id, md5Pwd};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateTotalMoney(int id, double amount) {
		String hql = "update " + tab + " set totalMoney = totalMoney + ?1 where id = ?0";
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateLotteryMoney(int id, double amount) {
		String hql = "update " + tab + " set lotteryMoney = lotteryMoney + ?1 where id = ?0";
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateLotteryMoney(int id, double lotteryAmount, double freezeAmount) {
		String hql = "update " + tab + " set lotteryMoney = lotteryMoney + ?1, freezeMoney = freezeMoney + ?2 where id = ?0";
		if(lotteryAmount < 0) {
			hql += " and lotteryMoney >= " + (-lotteryAmount);
		}
		if(freezeAmount < 0) {
			hql += " and freezeMoney >= " + (-freezeAmount);
		}
		Object[] values = {id, lotteryAmount, freezeAmount};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateBaccaratMoney(int id, double amount) {
		String hql = "update " + tab + " set baccaratMoney = baccaratMoney + ?1 where id = ?0";
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateFreezeMoney(int id, double amount) {
		String hql = "update " + tab + " set freezeMoney = freezeMoney + ?1 where id = ?0";
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateDividendMoney(int id, double amount) {
		String hql = "update " + tab + " set dividendMoney = dividendMoney + ?1 where id = ?0";
		Object[] values = {id, amount};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateMoney(int id, double totalAmount, double lotteryAmount,
			double baccaratAmount, double freezeAmount, double dividendAmount) {
		String hql = "update " + tab + " set totalMoney = totalMoney + ?1, lotteryMoney = lotteryMoney + ?2, baccaratMoney = baccaratMoney + ?3, freezeMoney = freezeMoney + ?4, dividendMoney = dividendMoney + ?5 where id = ?0";
		Object[] values = {id, totalAmount, lotteryAmount, baccaratAmount, freezeAmount, dividendAmount};
		return superDao.update(hql, values);
	}
	
	@Override
	public List<User> getUserLower(int id) {
		String hql = "from " + tab + " where upids like ?0";
		Object[] values = {"%[" + id + "]%"};
		return superDao.list(hql, values);
	}

	@Override
	public List<User> getUserDirectLower(int id) {
		String hql = "from " + tab + " where upid = ?0";
		Object[] values = {id};
		return superDao.list(hql, values);
	}

	@Override
	public List<Integer> getUserDirectLowerId(int id) {
		String hql = "select id from " + tab + " where upid = ?0 and type = ?1 ";
		Object[] values = {id,1};
		List<?> objects = superDao.listObject(hql, values);
		List<Integer> userIds = new ArrayList<>();
		for (Object object : objects) {
			userIds.add(Integer.valueOf(object.toString()));
		}
		return userIds;
	}

	@Override
	public List<User> getUserLowerWithoutCode(int id, int code) {
		String hql = "from " + tab + " where upids like ?0 and code <> ?1";
		Object[] values = {"%[" + id + "]%", code};
		return superDao.list(hql, values);
	}

	@Override
	public List<User> getUserDirectLowerWithoutCode(int id, int code) {
		String hql = "from " + tab + " where upid = ?0 and code <> ?1";
		Object[] values = {id, code};
		return superDao.list(hql, values);
	}

	@Override
	public List<Integer> getUserDirectLowerIdWithoutCode(int id, int code) {
		String hql = "select id from " + tab + " where upid = ?0 and code <> ?1";
		Object[] values = {id, code};
		List<?> objects = superDao.listObject(hql, values);
		List<Integer> userIds = new ArrayList<>();
		for (Object object : objects) {
			userIds.add(Integer.valueOf(object.toString()));
		}
		return userIds;
	}

	@Override
	public boolean updateLotteryPoint(int id, int code, double lp, int BStatus, double nlp) {
		String hql = "update " + tab + " set code = ?1, locatePoint = ?2, notLocatePoint = ?3,BStatus =?4 where id = ?0";
		Object[] values = {id, code, lp, nlp, BStatus};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateProxy(int id, int upid, String upids) {
		String hql = "update " + tab + " set upid = ?1, upids = ?2 where id = ?0";
		Object[] values = {id, upid, upids};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateLockTime(int id, String lockTime) {
		String hql = "update " + tab + " set lockTime = ?1 where id = ?0";
		Object[] values = {id, lockTime};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateExtraPoint(int id, double point) {
		String hql = "update " + tab + " set extraPoint = ?1 where id = ?0";
		Object[] values = {id, point};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateAStatus(int id, int status, String message) {
		String hql = "update " + tab + " set AStatus = ?1, message = ?2 where id = ?0";
		Object[] values = {id, status, message};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateBStatus(int id, int status, String message) {
		String hql = "update " + tab + " set BStatus = ?1, message = ?2 where id = ?0";
		Object[] values = {id, status, message};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateAllowEqualCode(int id, int status) {
		String hql = "update " + tab + " set allowEqualCode = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateAllowTransfers(int id, int status) {
		String hql = "update " + tab + " set allowTransfers = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateAllowPlatformTransfers(int id, int status) {
		String hql = "update " + tab + " set allowPlatformTransfers = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateAllowWithdraw(int id, int status) {
		String hql = "update " + tab + " set allowWithdraw = ?1 where id = ?0";
		Object[] values = {id, status};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean updateVipLevel(int id, int level) {
		String hql = "update " + tab + " set vipLevel = ?1 where id = ?0";
		Object[] values = {id, level};
		return superDao.update(hql, values);
	}
	
	@Override
	public List<User> list(List<Criterion> criterions, List<Order> orders) {
		return superDao.findByCriteria(User.class, criterions, orders);
	}

	@Override
	public PageList search(List<Criterion> criterions, List<Order> orders,
			int start, int limit) {
		String propertyName = "id";
		return superDao.findPageList(User.class, propertyName, criterions, orders, start, limit);
	}
	
	@Override
	public int getTotalUserRegist(String sTime, String eTime) {
		String hql = "select count(id) from " + tab + " where upid != ?2 and registTime >= ?0 and registTime < ?1";
		Object[] values = {sTime, eTime,0};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).intValue() : 0;
	}
	
	@Override
	public List<?> getDayUserRegist(String sTime, String eTime) {
		String hql = "select substring(registTime, 1, 10), count(id) from " + tab + " where registTime >= ?0 and registTime < ?1  and upid != ?2  group by substring(registTime, 1, 10)";
		Object[] values = {sTime, eTime,0};
		return superDao.listObject(hql, values);
	}
	
	@Override
	public Object[] getTotalMoney() {
		String hql = "select sum(totalMoney), sum(lotteryMoney), sum(baccaratMoney) from " + tab + " where upid !=?0";
		Object[] values = {0};
		return (Object[]) superDao.unique(hql,values);
	}
	
	@Override
	public int getOnlineCount(Integer[] ids) {
		String hql = "select count(id) from " + tab + " where onlineStatus = 1  and upid !=0";
		if(ids != null && ids.length > 0) {
			hql += " and id in (" + ArrayUtils.transInIds(ids) + ")";
		}
		Object result = superDao.unique(hql);
		return result != null ? ((Number) result).intValue() : 0;
	}
	// @Override
	// public boolean updateBstatusAndAllowEqualCode(Integer bstatus, int allowEqualCode,int UserId) {
	// 	String hql = "update " + tab + " set BStatus = ?1, allowEqualCode = ?2 where id = ?0";
	// 	Object[] values = {UserId, bstatus, allowEqualCode};
	// 	return superDao.update(hql, values);
	// }

	@Override
	public void updateOnlineStatusNotIn(Collection<String> sessionIds) {
		if (sessionIds == null || sessionIds.isEmpty()) {
			return;
		}
		String hql = "update " + tab + " set sessionId = null, onlineStatus = 0 where sessionId not in ("+ArrayUtils.toStringWithQuote(sessionIds)+") or sessionId is null";
		Object[] values = {};
		superDao.update(hql, values);
	}

	@Override
	public void updateAllOffline() {
		String hql = "update " + tab + " set sessionId = null, onlineStatus = 0";
		Object[] values = {};
		superDao.update(hql, values);
	}

	@Override
	public void updateOffline(int userId) {
		String hql = "update " + tab + " set sessionId = null, onlineStatus = 0 where id = ?0";
		Object[] values = {userId};
		superDao.update(hql, values);
	}

	@Override
	public boolean updateRelatedUpper(int userId, int relatedUpid, double relatedPoint) {
		String hql = "update " + tab + " set relatedUpid = ?0,relatedPoint=?1 where id = ?2";
		Object[] values = {relatedUpid, relatedPoint, userId};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateRelatedLowers(int userId, String relatedLowers) {
		String hql = "update " + tab + " set relatedLowers = ?0 where id = ?1";
		Object[] values = {relatedLowers, userId};
		return superDao.update(hql, values);
	}

	@Override
	public boolean updateRelatedUsers(int userId, String relatedUserIds) {
		String hql = "update " + tab + " set relatedUsers = ?1 where type = 3 and id = ?0";
		Object[] values = {userId, relatedUserIds};
		return superDao.update(hql, values);
	}


	@Override
	public boolean lockTeam(int userId, int status, String remark) {
		String hql = "update " + tab + " set AStatus = ?0, message = ?1, sessionId = null, onlineStatus = 0 where upids like ?2 or id = ?3";
		Object[] values = {status, remark, "%[" + userId + "]%", userId};
		return superDao.update(hql, values);
	}

	@Override
	public boolean unLockTeam(int userId, int status) {
		String hql = "update " + tab + " set AStatus = ?0, message = null where upids like ?1 or id = ?2";
		Object[] values = {status, "%[" + userId + "]%", userId};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean prohibitTeamWithdraw(int userId, int status) {
		String hql = "update " + tab + " set allowWithdraw = ?0 where upids like ?1 or id = ?2";
		Object[] values = {status, "%[" + userId + "]%", userId};
		return superDao.update(hql, values);
	}

	@Override
	public boolean allowTeamWithdraw(int userId, int status) {
		String hql = "update " + tab + " set allowWithdraw = ?0 where upids like ?1 or id = ?2";
		Object[] values = {status, "%[" + userId + "]%", userId};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean allowTeamTransfers(int userId, int status) {
		String hql = "update " + tab + " set allowTransfers = ?0 where upids like ?1 or id = ?2";
		Object[] values = {status, "%[" + userId + "]%", userId};
		return superDao.update(hql, values);
	}

	
	@Override
	public boolean prohibitTeamTransfers(int userId, int status) {
		String hql = "update " + tab + " set allowTransfers = ?0 where upids like ?1 or id = ?2";
		Object[] values = {status, "%[" + userId + "]%", userId};
		return superDao.update(hql, values);
	}
	
	@Override
	public boolean allowTeamPlatformTransfers(int userId, int status) {
		String hql = "update " + tab + " set allowPlatformTransfers = ?0 where upids like ?1 or id = ?2";
		Object[] values = {status, "%[" + userId + "]%", userId};
		return superDao.update(hql, values);
	}

	
	@Override
	public boolean prohibitTeamPlatformTransfers(int userId, int status) {
		String hql = "update " + tab + " set allowPlatformTransfers = ?0 where upids like ?1 or id = ?2";
		Object[] values = {status, "%[" + userId + "]%", userId};
		return superDao.update(hql, values);
	}

	@Override
	public boolean changeZhaoShang(int userId, int isCJZhaoShang) {
		String hql = "update " + tab + " set isCjZhaoShang = ?0 where id = ?1";
		Object[] values = {isCJZhaoShang, userId};
		return superDao.update(hql, values);
	}

	@Override
	public boolean delete(int userId) {
		String hql = "delete from " + tab + " where id = ?0";
		Object[] values = {userId};
		return superDao.delete(hql, values);
	}

	@Override
	public int getDemoUserCount() {
		String hql = "select count(id) from " + tab + " where 1=1 and (nickname = ?0 and upid =0) and type = ?1";
		Object[] values = {"试玩用户", 2};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public int getFictitiousUserCount() {
		String hql = "select count(id) from " + tab + " where type = ?0";
		Object[] values = {4};
		Object result = superDao.unique(hql, values);
		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public List<User> listAllByType(int type) {
		String hql = "from " + tab + " where type = ?0";
		Object[] values = {type};
		return superDao.list(hql, values);
	}
}