package admin.domains.content.biz;

import admin.domains.content.vo.AdminUserVO;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.util.List;

public interface AdminUserService {

	List<AdminUserVO> listAll(int roleId);

	/**
	 * 新增用户
	 * @param username 用户名
	 * @param password 密码，密码前台传输时的加密方式：MD5大写(MD5大写(密码明文),调用$.generatePassword(plainStr)即可
	 * @param roleId  权限ID
	 * @param setWithdrawPwd 为1时指定资金密码，否则生成notset字符串
	 * @return
	 */
	boolean add(String username, String password, int roleId, Integer setWithdrawPwd, String withdrawPwd);

	boolean edit(String username, String password, int roleId, Integer setWithdrawPwd, String withdrawPwd);

	/**
	 * 关闭资金密码
	 */
	boolean closeWithdrawPwd(String username);

	/**
	 * 开启资金密码
	 */
	boolean openWithdrawPwd(String username, String withdrawPwd);

	boolean updateStatus(int id, int status);

	boolean updateLoginTime(int id, String loginTime);

	boolean modUserLoginPwd(int id, String password);

	boolean modUserWithdrawPwd(int id, String withdrawPwd);

	boolean updatePwdError(int id, int count);

	boolean updateIps(int uid, String ipAddr);

	GoogleAuthenticatorKey createCredentialsForUser(String username);

	boolean authoriseUser(String username, int verificationCode);

}