package lottery.domains.content.biz.impl;

import javautils.encrypt.PasswordUtil;
import lottery.domains.content.biz.UserSecurityService;
import lottery.domains.content.dao.UserSecurityDao;
import lottery.domains.content.entity.UserSecurity;
import lottery.domains.content.vo.user.UserSecurityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class UserSecurityServiceImpl implements UserSecurityService {
	
	@Autowired
	private UserSecurityDao uSecurityDao;

	@Override
	public List<UserSecurity> listByUserId(int userId) {
		return uSecurityDao.listByUserId(userId);
	}
	
	@Override
	public boolean add(UserSecurity ... list) {
		for (UserSecurity entity : list) {
			uSecurityDao.add(entity);
		}
		return true;
	}
	
	@Override
	public boolean add(int userId, String question, String answer) {
		answer = PasswordUtil.generatePasswordByMD5(answer);
		UserSecurity entity = new UserSecurity(userId, question, answer);
		return uSecurityDao.add(entity);
	}
	
	@Override
	@Transactional(readOnly = true)
	public UserSecurityVO getRandomByUserId(int userId) {
		List<UserSecurity> list = listByUserId(userId);
		if(list.size() > 0) {
			Random random = new Random();
			int index = random.nextInt(list.size());
			return new UserSecurityVO(list.get(index));
		}
		return null;
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean validateSecurity(int id, int userId, String token, String answer) {
		UserSecurity entity = uSecurityDao.getById(id, userId);
		if (entity == null) {
			return false;
		}
		if(PasswordUtil.validatePassword(entity.getValue(), token, answer)) {
			return true;
		}
		return false;
	}

}