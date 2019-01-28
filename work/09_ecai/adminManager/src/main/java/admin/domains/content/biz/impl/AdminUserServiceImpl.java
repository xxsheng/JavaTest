package admin.domains.content.biz.impl;

import admin.domains.content.biz.AdminUserRoleService;
import admin.domains.content.biz.AdminUserService;
import admin.domains.content.dao.AdminUserDao;
import admin.domains.content.entity.AdminUser;
import admin.domains.content.entity.AdminUserRole;
import admin.domains.content.vo.AdminUserVO;
import admin.domains.pool.AdminDataFactory;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.KeyRepresentation;
import javautils.StringUtil;
import javautils.date.DateUtil;
import javautils.date.Moment;
import javautils.encrypt.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class AdminUserServiceImpl implements AdminUserService {
	private static final GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder gacb =
			new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
					.setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
					.setWindowSize(5)
					.setCodeDigits(6)
					.setKeyRepresentation(KeyRepresentation.BASE32);
	private static final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(gacb.build());

	// 已经使用过的验证码60分钟内不允许再次使用
	private static final ConcurrentHashMap<String, String> GOOGLE_CODES = new ConcurrentHashMap<>();

	@Autowired
	private AdminUserDao adminUserDao;

	@Autowired
	private AdminUserRoleService adminUserRoleService;

	@Autowired
	private AdminDataFactory adminDataFactory;

	@Override
	public List<AdminUserVO> listAll(int roleId) {
		List<AdminUserRole> rlist = adminUserRoleService.listAll(roleId);
		Set<Integer> rSet = new HashSet<>();
		for (AdminUserRole tmpRole : rlist) {
			if(tmpRole.getUpid() != 0) {
				rSet.add(tmpRole.getId());
			}
		}
		List<AdminUser> ulist = adminUserDao.listAll();
		List<AdminUserVO> list = new ArrayList<>();
		for (AdminUser tmpBean : ulist) {
			if(tmpBean.getId() == 0) continue;
			if(rSet.contains(tmpBean.getRoleId())) {
				list.add(new AdminUserVO(tmpBean, adminDataFactory));
			}
		}
		return list;
	}

	@Override
	public boolean add(String username, String password, int roleId, Integer setWithdrawPwd, String withdrawPwd) {
		String registTime = DateUtil.getCurrentTime();
		password = PasswordUtil.generatePasswordByMD5(password);

		String dbWithdrawPwd;
		if (setWithdrawPwd != null && setWithdrawPwd == 1) {
			dbWithdrawPwd = PasswordUtil.generatePasswordByMD5(withdrawPwd); // 资金密码
		}
		else {
			dbWithdrawPwd = "notset";
		}
		String loginTime = null;
		int status = 0;
		String ips = "";
		int pwd_error = 0;
		AdminUser entity = new AdminUser(username, password, dbWithdrawPwd, roleId, registTime, loginTime, status, ips, pwd_error);
		boolean result = adminUserDao.add(entity);
		if(result) {
			adminDataFactory.initAdminUser();
		}
		return result;
	}

	@Override
	public boolean edit(String username, String password, int roleId, Integer setWithdrawPwd, String withdrawPwd) {
		password = PasswordUtil.generatePasswordByMD5(password);
		AdminUser entity = adminUserDao.getByUsername(username);
		if(entity != null) {
			if(StringUtil.isNotNull(password)) {
				entity.setPassword(password);
			}

			String dbWithdrawPwd;
			if (setWithdrawPwd != null && setWithdrawPwd == 1) {
				dbWithdrawPwd = PasswordUtil.generatePasswordByMD5(withdrawPwd); // 资金密码
				entity.setWithdrawPwd(dbWithdrawPwd);
			}

			entity.setRoleId(roleId);
			return adminUserDao.update(entity);
		}
		return false;
	}

	@Override
	public boolean closeWithdrawPwd(String username) {
		AdminUser entity = adminUserDao.getByUsername(username);
		if (entity == null) {
			return false;
		}

		entity.setWithdrawPwd("notset");
		return adminUserDao.update(entity);
	}

	@Override
	public boolean openWithdrawPwd(String username, String withdrawPwd) {
		AdminUser entity = adminUserDao.getByUsername(username);
		if (entity == null) {
			return false;
		}
		String dbWithdrawPwd = PasswordUtil.generatePasswordByMD5(withdrawPwd); // 资金密码
		entity.setWithdrawPwd(dbWithdrawPwd);
		return adminUserDao.update(entity);
	}

	@Override
	public boolean updateStatus(int id, int status) {
		AdminUser entity = adminUserDao.getById(id);
		if(entity != null) {
			entity.setStatus(status);
			return adminUserDao.update(entity);
		}
		return false;
	}

	@Override
	public boolean updatePwdError(int id, int count) {
		AdminUser entity = adminUserDao.getById(id);
		if(entity != null) {
			entity.setPwd_error(count);
			return adminUserDao.update(entity);
		}
		return false;
	}

	@Override
	public boolean updateIps(int id, String ip) {
		AdminUser entity = adminUserDao.getById(id);
		if(entity != null) {
			String ips = entity.getIps();
			ips = ips+","+ip;
			entity.setIps(ips);
			return adminUserDao.update(entity);
		}
		return false;
	}

	@Override
	public boolean updateLoginTime(int id, String loginTime) {
		AdminUser entity = adminUserDao.getById(id);
		if(entity != null) {
			entity.setLoginTime(loginTime);
			return adminUserDao.update(entity);
		}
		return false;
	}

	@Override
	public boolean modUserLoginPwd(int id, String password) {
		password = PasswordUtil.generatePasswordByMD5(password);
		AdminUser entity = adminUserDao.getById(id);
		if(entity != null) {
			entity.setPassword(password);
			return adminUserDao.update(entity);
		}
		return false;
	}

	@Override
	public boolean modUserWithdrawPwd(int id, String withdrawPwd) {
		withdrawPwd = PasswordUtil.generatePasswordByMD5(withdrawPwd);
		AdminUser entity = adminUserDao.getById(id);
		if(entity != null) {
			entity.setWithdrawPwd(withdrawPwd);
			return adminUserDao.update(entity);
		}
		return false;
	}

	@Override
	public GoogleAuthenticatorKey createCredentialsForUser(String username) {
		final GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
		AdminUser uEntity = adminUserDao.getByUsername(username);
		uEntity.setSecretKey(key.getKey());
		adminUserDao.update(uEntity);

		return key;
	}

	@Override
	public boolean authoriseUser(String username, int verificationCode) {
		String cacheKey = username + "_" + verificationCode;
		String usedTime = GOOGLE_CODES.get(cacheKey);
		if (usedTime != null) {
			Moment now = new Moment();
			Moment usedMoment = new Moment().fromTime(usedTime);

			int usedMinutes = now.difference(usedMoment, "minute");
			if (usedMinutes <= 60) {
				// 已经使用过的验证码60分钟内不允许再次使用
				return false;
			}
		}

		String secretKey = adminUserDao.getByUsername(username).getSecretKey();
		boolean isCodeValid = googleAuthenticator.authorize(secretKey, verificationCode);

		if (isCodeValid) {
			GOOGLE_CODES.put(cacheKey, new Moment().toSimpleTime());
		}

		return isCodeValid;
	}

	@Scheduled(cron = "0 30 5 * * *")
	public void clear() {
		GOOGLE_CODES.clear();
	}
}