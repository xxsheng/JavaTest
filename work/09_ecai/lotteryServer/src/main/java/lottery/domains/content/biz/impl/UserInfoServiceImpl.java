package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.UserInfoService;
import lottery.domains.content.dao.UserInfoDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserInfo;
import lottery.domains.content.vo.user.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	private UserInfoDao uInfoDao;
	
	@Override
	@Transactional(readOnly = true)
	public UserInfoVO get(User uBean) {
		UserInfo iBean = uInfoDao.get(uBean.getId());
		return new UserInfoVO(uBean, iBean);
	}

	@Override
	@Transactional(readOnly = true)
	public UserInfo get(int userId) {
		return uInfoDao.get(userId);
	}

	@Override
	public boolean add(UserInfo entity) {
		return uInfoDao.add(entity);
	}

	@Override
	public boolean update(UserInfo entity) {
		return uInfoDao.update(entity);
	}
}