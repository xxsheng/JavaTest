package lottery.domains.content.biz;

import admin.web.WebJSONObject;
import javautils.jdbc.PageList;
import lottery.domains.content.entity.User;
import lottery.domains.content.vo.user.UserProfileVO;

import java.util.List;

public interface UserService {

	/**
	 * 通过id获取用户
	 */
	User getById(int id);

	/**
	 * 通过用户名获取用户
	 */
	User getByUsername(String username);
	
	/**
	 * 账号状态
	 */
	boolean aStatus(String username, int status, String message);
	
	/**
	 * 投注状态
	 */
	boolean bStatus(String username, int status, String message);
	
	/**
	 * 修改登录密码
	 */
	boolean modifyLoginPwd(String username, String password);
	
	/**
	 * 修改取款密码
	 */
	boolean modifyWithdrawPwd(String username, String password);
	
	/**
	 * 修改取款人
	 */
	boolean modifyWithdrawName(String username, String withdrawName);
	
	/**
	 * 重置图案密码
	 */
	boolean resetImagePwd(String username);
	
	/**
	 * 获取个人资料
	 */
	UserProfileVO getUserProfile(String username);
	
	/**
	 * 线路转换
	 */
	boolean changeLine(int type, String aUser, String bUser);
	
	/**
	 * 修改彩票返点
	 */
	boolean modifyLotteryPoint(String username, int code, double locatePoint, double notLocatePoint);
	
	/**
	 * 统一线路降点
	 */
	boolean downLinePoint(String username);
	
	/**
	 * 修改私返点数
	 */
	boolean modifyExtraPoint(String username, double point);
	
	/**
	 * 修改同级开号
	 */
	boolean modifyEqualCode(String username, int status);
	
	/**
	 * 修改上下级转账
	 */
	boolean modifyTransfers(String username, int status);
	
	/**
	 * 修改个人平台转账
	 */
	boolean modifyPlatformTransfers(String username, int status);
	
	/**
	 * 修改个人用户取款
	 */
	boolean modifyWithdraw(String username, int status);
	
	/**
	 * 转换为代理
	 */
	boolean changeProxy(String username);

	/**
	 * 解绑谷歌身份验证器
	 */
	boolean unbindGoogle(String username);

	/**
	 * 清空时间锁
	 */
	boolean resetLockTime(String username);

	/**
	 * 修改配额
	 */
	boolean modifyQuota(String username, int count1, int count2, int count3);
	
	/**
	 * 回收账户
	 */
	User recover(String username);
	
	/**
	 * 搜索用户
	 */
	PageList search(String username, String matchType, String registTime, Double minMoney, Double maxMoney, Double minLotteryMoney, Double maxLotteryMoney,
			Integer minCode, Integer maxCode, String sortColoum, String sortType, Integer aStatus, Integer bStatus, 
			Integer onlineStatus, Integer type,String nickname, int start, int limit);
	
	/**
	 * 在线用户
	 */
	PageList listOnline(String sortColoum, String sortType, int start, int limit);

	/**
	 * 添加新用户
	 */
	boolean addNewUser(WebJSONObject json, String username, String nickname, String password,
					   int upid, String upids, int type, int code, double locatePoint,
					   double notLocatePoint, String relatedUsers);

	/**
	 * 添加下级用户
	 */
	boolean addLowerUser(WebJSONObject json, User uBean, String username, String nickname,
			String password, int type, int code, double locatePoint,
			double notLocatePoint, String relatedUsers);

	/**
	 * 修改VIP 等级
	 * @param username
	 * @param vipLevel
	 * @return
	 */
	boolean modifyUserVipLevel(String username, int vipLevel);

	/**
	 * 踢出用户
	 */
	boolean kickOutUser(int userId, String sessionId);

	/**
	 * 修改关联上级
	 */
	boolean modifyRelatedUpper(WebJSONObject json, String username, String relatedUpUser, double relatedPoint);

	/**
	 * 解除关联上级
	 */
	boolean reliveRelatedUpper(WebJSONObject json, String username);

	/**
	 * 修改关联会员
	 * @param username 关联账号
	 * @param relatedUsers 需要关联哪些会员
	 * @return
	 */
	boolean modifyRelatedUsers(WebJSONObject json, String username, String relatedUsers);

	/**
	 * 冻结团队
	 */
	boolean lockTeam(WebJSONObject json, String username, String remark);

	/**
	 * 解冻团队
	 */
	boolean unLockTeam(WebJSONObject json, String username);
	
	/**
	 * 允许团队取款
	 */
	boolean allowTeamWithdraw(WebJSONObject json, String username);

	/**
	 * 禁止团队取款
	 */
	boolean prohibitTeamWithdraw(WebJSONObject json, String username);
	
	/**
	 * 允许团队上下级转账
	 */
	boolean allowTeamTransfers(WebJSONObject json, String username);

	/**
	 * 禁止团队上下级转账
	 */
	boolean prohibitTeamTransfers(WebJSONObject json, String username);
	
	/**
	 * 允许团队平台转账
	 */
	boolean allowTeamPlatformTransfers(WebJSONObject json, String username);

	/**
	 * 禁止团队平台转账
	 */
	boolean prohibitTeamPlatformTransfers(WebJSONObject json, String username);

	/**
	 * aUser转账至bUser，从aUser的余额(包含主账户和彩票账户)中转给bUser
	 */
	boolean transfer(WebJSONObject json, User aUser, User bUser, double money,String remarks);

	/**
	 * isCJZhaoShang，1：超级招商转招商；0：招商转超级招商
	 */
	boolean changeZhaoShang(WebJSONObject json, String username, int isCJZhaoShang);


	List<String> getUserLevels(User user);

	/**
	 * 查找所有内部招商号
	 */
	List<User> findNeibuZhaoShang();

	/**
	 * 查找所有招商号
	 */
	List<User> findZhaoShang(List<User> neibuZhaoShangs);
	
	List<User> getUserDirectLower(int userId);
}