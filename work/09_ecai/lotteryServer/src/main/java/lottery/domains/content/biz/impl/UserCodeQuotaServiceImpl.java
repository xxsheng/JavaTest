package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.UserCodeQuotaService;
import lottery.domains.content.dao.UserCodeQuotaDao;
import lottery.domains.content.entity.UserCodeQuota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserCodeQuotaServiceImpl implements UserCodeQuotaService {
	@Autowired
	private UserCodeQuotaDao uCodeQuotaDao;

	@Override
	public boolean add(UserCodeQuota entity) {
		return uCodeQuotaDao.add(entity);
	}

	@Override
	public boolean addUpAllocateQuantity(int userId, int code) {
		return uCodeQuotaDao.addUpAllocateQuantity(userId, code);
	}

	@Override
	@Transactional(readOnly = true)
	public UserCodeQuota get(int userId, int code) {
		return uCodeQuotaDao.get(userId, code);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserCodeQuota> list(int userId) {
		return uCodeQuotaDao.list(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserCodeQuota> list(int[] userIds) {
		return uCodeQuotaDao.list(userIds);
	}
}