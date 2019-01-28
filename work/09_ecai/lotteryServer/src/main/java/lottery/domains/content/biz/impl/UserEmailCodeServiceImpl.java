package lottery.domains.content.biz.impl;

import javautils.date.Moment;
import lottery.domains.content.biz.UserEmailCodeService;
import lottery.domains.content.dao.UserEmailCodeDao;
import lottery.domains.content.entity.UserEmailCode;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserEmailCodeServiceImpl implements UserEmailCodeService {
	
	@Autowired
	private UserEmailCodeDao userEmailCodeDao;

	@Override
	public UserEmailCode generate(int type, String username, String email) {
		String code = ObjectId.get().toString(); // OrderUtil.createString(8);
		Moment moment = new Moment();
		String time = moment.toSimpleTime();
		String expirationTime = new String();
		if(type == 1) { // 绑定邮箱
			expirationTime = moment.add(10, "minutes").toSimpleTime();
		}
		if(type == 2) { // 忘记密码
			expirationTime = moment.add(1, "hours").toSimpleTime();
			code = ObjectId.get().toString(); //OrderUtil.createString(24);
		}
		int status = 0;
		UserEmailCode entity = new UserEmailCode(type, code, username, email, time, expirationTime, status);
		boolean result = userEmailCodeDao.save(entity);
		if(result) {
			return entity;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public UserEmailCode get(int type, String username, String code) {
		return userEmailCodeDao.get(type, username, code);
	}
	
	@Override
	public boolean used(int id) {
		int status = 1;
		return userEmailCodeDao.updateStatus(id, status);
	}
	
}