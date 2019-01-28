package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.User;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.Collection;
import java.util.List;

public interface UserDao {
	
	/**
	 * 添加用户
	 */
	boolean add(User entity);
	
	/**
	 * 更新用户
	 */
	boolean update(User entity);
	
	/**
	 * 列出所有用户
	 */
	List<User> listAll();
	
	/**
	 * 通过id获取用户
	 */
	User getById(int id);
	
	/**
	 * 通过用户名获取用户
	 */
	User getByUsername(String username);
	
	/**
	 * 更新账户类型
	 */
	boolean updateType(int id, int type);

	/**
	 * 解绑谷歌身份验证器
	 */
	boolean unbindGoogle(int id);

	/**
	 * 清空时间锁
	 */
	boolean resetLockTime(int id);

	/**
	 * 设置取款人
	 */
	boolean updateWithdrawName(int id, String withdrawName);
	
	/**
	 * 设置取现密码
	 */
	boolean updateWithdrawPassword(int id, String md5Pwd);
	
	/**
	 * 更新用户密码
	 */
	boolean updateLoginPwd(int id, String md5Pwd);
	
	/**
	 * 更新用户图案密码
	 */
	boolean updateImgPwd(int id, String md5Pwd);
	
	/**
	 * 获取用户下级代理
	 */
	List<User> getUserLower(int id);
	
	/**
	 * 获取直属下级
	 */
	List<User> getUserDirectLower(int id);

	/**
	 * 根据上级用户获取直属下级ID
	 */
	List<Integer> getUserDirectLowerId(int id);

	/**
	 * 获取用户下级代理
	 */
	List<User> getUserLowerWithoutCode(int id, int code);

	/**
	 * 获取直属下级
	 */
	List<User> getUserDirectLowerWithoutCode(int id, int code);

	/**
	 * 根据上级用户获取直属下级ID
	 */
	List<Integer> getUserDirectLowerIdWithoutCode(int id, int code);

	/**
	 * 更新用户资金
	 */
	boolean updateTotalMoney(int id, double amount);
	
	/**
	 * 更新彩票账户资金
	 */
	boolean updateLotteryMoney(int id, double amount);
	
	/**
	 * 更新彩票账户资金和冻结资金
	 */
	boolean updateLotteryMoney(int id, double lotteryAmount, double freezeAmount);
	
	/**
	 * 更新百家乐资金
	 */
	boolean updateBaccaratMoney(int id, double amount);
	
	/**
	 * 更新用户冻结资金
	 */
	boolean updateFreezeMoney(int id, double amount);
	
	/**
	 * 更新用户分红资金
	 */
	boolean updateDividendMoney(int id, double amount);
	
	/**
	 * 更新用户资金
	 */
	boolean updateMoney(int id, double totalAmount, double lotteryAmount, double baccaratAmount, double freezeAmount, double dividendAmount);
	
	/**
	 * 更新用户返点
	 */
	boolean updateLotteryPoint(int id, int code, double lp, int BStatus, double nlp);
	
	/**
	 * 更新私返点数
	 */
	boolean updateExtraPoint(int id, double point);
	
	/**
	 * 更新代理情况
	 */
	boolean updateProxy(int id, int upid, String upids);
	
	/**
	 * 更新锁定时间
	 */
	boolean updateLockTime(int id, String lockTime);
	
	/**
	 * 更新用户状态
	 */
	boolean updateAStatus(int id, int status, String message);
	
	/**
	 * 更新用户投注权限
	 */
	boolean updateBStatus(int id, int status, String message);
	
	/**
	 * 更改用户同级开号权限
	 */
	boolean updateAllowEqualCode(int id, int status);
	
	/**
	 * 更改用户上下级转账权限
	 */
	boolean updateAllowTransfers(int id, int status);
	
	
	/**
	 * 更改用户平台转账权限
	 */
	boolean updateAllowPlatformTransfers(int id, int status);
	
	/**
	 * 更改用户取款权限
	 */
	boolean updateAllowWithdraw(int id, int status);
	
	/**
	 * 更改VIP等级
	 */
	boolean updateVipLevel(int id, int level);
	
	/**
	 * 列出用户
	 */
	List<User> list(List<Criterion> criterions, List<Order> orders);
	
	/**
	 * 分页查询
	 */
	PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);
	
	/**
	 * 获取新用户
	 */
	int getTotalUserRegist(String sTime, String eTime);
	
	/**
	 * 根据天分组获取用户注册数
	 */
	List<?> getDayUserRegist(String sTime, String eTime);
	
	/**
	 * 获取账户余额
	 */
	Object[] getTotalMoney();
	
	/**
	 * 获取在线人数
	 */
	int getOnlineCount(Integer[] ids);
	
	int getDemoUserCount();
	
	int getFictitiousUserCount();
	
	// /**
	//  * 修改用户的投注状态和平级开号状态
	//  * @param bstatus
	//  * @param allowEqualCode
	//  * @param UserId
	//  * @return
	//  */
	// public boolean updateBstatusAndAllowEqualCode(Integer bstatus, int allowEqualCode,int UserId);

	void updateOnlineStatusNotIn(Collection<String> sessionIds);

	/**
	 * 修改所有人都不在线
	 */
	void updateAllOffline();

	/**
	 * 踢下线
	 */
	void updateOffline(int userId);

	boolean updateRelatedUpper(int userId, int relatedUpid, double relatedPoint);

	boolean updateRelatedLowers(int userId, String relatedLowers);

	boolean updateRelatedUsers(int userId, String relatedUserIds);

	/**
	 * 冻结团队并踢下线
	 */
	boolean lockTeam(int userId, int status, String remark);

	/**
	 * 解冻团队
	 */
	boolean unLockTeam(int userId, int status);
	
	/**
	 * 禁止团队取款
	 */
	boolean prohibitTeamWithdraw(int userId, int status);

	/**
	 * 允许团队取款
	 */
	boolean allowTeamWithdraw(int userId, int status);
	
	/**
	 * 允许团队上下级转账
	 */
	boolean allowTeamTransfers(int userId, int status);
	
	/**
	 * 禁止团队上下级转账
	 */
	boolean prohibitTeamTransfers(int userId, int status);
	
	/**
	 * 允许团队平台转账
	 */
	boolean allowTeamPlatformTransfers(int userId, int status);
	
	/**
	 * 禁止团队平台转账
	 */
	boolean prohibitTeamPlatformTransfers(int userId, int status);

	/**
	 * 设置是否是超级招商字段
	 */
	boolean changeZhaoShang(int userId, int isCJZhaoShang);
	
	boolean delete(int userId);
	
	/**
	 * 根据Type列出所有用户
	 */
	List<User> listAllByType(int type);
}