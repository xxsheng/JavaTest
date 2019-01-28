package lottery.domains.content.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserPlanInfoService;
import lottery.domains.content.dao.UserPlanInfoDao;
import lottery.domains.content.entity.UserPlanInfo;

@Service
public class UserPlanInfoServiceImpl implements UserPlanInfoService {
	
	@Autowired
	private UserPlanInfoDao uPlanInfoDao;
	
	@Override
	public UserPlanInfo get(int userId) {
		UserPlanInfo bean = uPlanInfoDao.get(userId);
		if(bean == null) {
			int level = 0;
			int planCount = 0;
			int prizeCount = 0;
			double totalMoney = 0;
			double totalPrize = 0;
			int status = 0;
			bean = new UserPlanInfo(userId, level, planCount, prizeCount, totalMoney, totalPrize, status);
			uPlanInfoDao.add(bean);
		}
		return bean;
	}

}