package lottery.domains.content.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserInfoService;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.dao.UserInfoDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserInfo;

@Service
public class UserInfoServiceImpl implements UserInfoService {
	
	@Autowired
	private UserDao uDao;
	
	@Autowired
	private UserInfoDao uInfoDao;
	
	@Override
	public boolean resetEmail(String username) {
		User uBean = uDao.getByUsername(username);
		if(uBean != null) {
			UserInfo infoBean = uInfoDao.get(uBean.getId());
			if(infoBean != null) {
				infoBean.setEmail(null);
				return uInfoDao.update(infoBean);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean modifyEmail(String username, String email) {
		User uBean = uDao.getByUsername(username);
		if(uBean != null) {
			UserInfo infoBean = uInfoDao.get(uBean.getId());
			if(infoBean == null) {
				infoBean = new UserInfo();
				infoBean.setUserId(uBean.getId());
				infoBean.setEmail(email);
				return uInfoDao.add(infoBean);
			} else {
				infoBean.setEmail(email);
				return uInfoDao.update(infoBean);
			}
		}
		return false;
	}

}