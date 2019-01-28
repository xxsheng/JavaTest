package lottery.domains.content.dao;

import lottery.domains.content.entity.User;

import java.util.List;

public interface UserDao {
	
	/**
	 * 添加用户
	 */
	boolean add(User entity);

	/**
	 * 通过id获取用户
	 */
	User getById(int id);
	
	/**
	 * 通过用户名获取用户
	 */
	User getByUsername(String username);

	/**
	 * 更新用户登录时间
	 */
	boolean updateLoginTime(int id, String time);

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
	 * 更新试玩用户投注资金
	 */
	boolean updateDemoUserLotteryMoney(int id, double amount);
	
	/**
	 * 更新用户图案密码
	 */
	boolean updateImgPwd(int id, String md5Pwd);

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
	boolean updateLotteryPoint(int id, int code, double lp, double nlp, int ct, int BStatus);
	
	/**
	 * 更新用户状态
	 */
	boolean updateAStatus(int id, int status, String message);
	
	/**
	 * 更新用户投注权限
	 */
	boolean updateBStatus(int id, int status, String message);
	
	/**
	 * 更新用户绑定状态
	 */
	boolean updateBindStatus(int id, int status);
	
	/**
	 * 更改用户昵称
	 */
	boolean updateNickname(int id, String nickname);
	
	/**
	 * 设为在线
	 */
	boolean setOnline(String username, String sessionId);
	
	/**
	 * 获取不在线的试玩用户
	 * <p>Title: getOfflineDemoList</p>  
	 * <p>Description: </p>  
	 * @return
	 */
	
   List<User> getDemoListByonlineStatus(int onlineStatus);
	
	/**
	 * 设为下线
	 */
	boolean setOffline(String sessionId);
	
	/**
	 * 更新积分
	 */
	boolean updateIntegral(int id, double amount);

	/**
	 * 更新冻结金额
	 */
	boolean updateFreezeMoney(List<Object[]> list);
	
	// /**
	//  * 修改投注权限 和 统同级开号权限
	//  */
	// boolean updateBstatusAndAllowEqualCode(Integer bstatus,int allowEqualCode,int userId);

	/**
	 * 更新google密钥
	 */
	boolean updateSecretKey(int id, String secretKey);

	/**
	 * 更新google绑定状态
	 */
	boolean updateIsBindGoogle(int id, int isBindGoogle);

	/**
	 * 修改异地登录验证
	 */
	boolean updateLoginValidate(int uerId, int loginValidate);
}