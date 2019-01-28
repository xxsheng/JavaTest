package lottery.domains.content.biz;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import javautils.jdbc.PageList;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.chart.ChartLineVO;

import java.util.List;

public interface UserService {
	/**
	 * 通过id获取用户,请注意
	 */
	User getById(int id);

	/**
	 * 通过用户名获取用户
	 */
	User getByUsername(String username);

	/**
	 * 设为在线
	 */
	boolean setOnline(String username, String sessionId);

	/**
	 * 设为下线
	 */
	boolean setOffline(String sessionId);

	/**
	 * 更新用户状态
	 */
	boolean updateAStatus(int id, int status, String message);

	/**
	 * 更新用户投注权限
	 */
	boolean updateBStatus(int id, int status, String message);

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
	 * 更新用户绑定状态
	 */
	boolean updateBindStatus(int id, int status);

	/**
	 * 更改用户昵称
	 */
	boolean updateNickname(int id, String nickname);

	/**
	 * 更新google密钥
	 */
	boolean updateSecretKey(int id, String secretKey);

	/**
	 * 更新google绑定状态
	 */
	boolean updateIsBindGoogle(int id, int isBindGoogle);
	
	
	/**
	 * 根据在线状态查询试玩用户
	 */
    List<User> getOfflineDemoList(int onlineStatus);
    
	/**
	 * 更新试玩用户投注资金
	 */
	boolean updateDemoUserLotteryMoney(int id, double amount);

	/**
	 * 更新用户返点
	 */
	boolean updateLotteryPoint(int id, int code, double lp, double nlp, int ct, int BStatus);

	// /**
	//  * 修改投注权限 和 统同级开号权限
	//  */
	// boolean updateBstatusAndAllowEqualCode(Integer bstatus,int allowEqualCode,int userId);
	/**
	 * 添加新用户
	 */
	Integer addNewUser(String username, String nickname, String password,
			int upid, String upids, int type, int code, double locatePoint,
			double notLocatePoint);
	
	/**
	 * 添加下级用户
	 */
	Integer addLowerUser(User uBean, String username, String nickname,
			String password, int type, int code, double locatePoint,
			double notLocatePoint);

	/**
	 * 拼接团队报表  假数据
	 * @param ids
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public ChartLineVO getOtherCharReport(int[] ids, String sDate, String eDate);

	/**
	 * 创建令牌
	 */
	GoogleAuthenticatorKey createCredentialsForUser(int userId);

	/**
	 * 验证用户google验证码
	 */
	boolean authoriseUser(int userId, int verificationCode);

	/**
	 * 修改异地登录验证
	 */
	boolean updateLoginValidate(int userId, int loginValidate);

	/**
	 * 更新用户资金
	 */
	boolean updateTotalMoney(int id, double amount);
}