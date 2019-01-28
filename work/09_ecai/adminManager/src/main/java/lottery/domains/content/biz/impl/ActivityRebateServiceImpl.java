package lottery.domains.content.biz.impl;

import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.entity.DbServerSync;
import lottery.domains.content.global.DbServerSyncEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.ActivityRebateService;
import lottery.domains.content.dao.ActivityRebateDao;
import lottery.domains.content.entity.ActivityRebate;

@Service
public class ActivityRebateServiceImpl implements ActivityRebateService {
	
	@Autowired
	private ActivityRebateDao aRebateDao;

	@Autowired
	private DbServerSyncDao dbServerSyncDao;

	@Override
	public boolean updateStatus(int id, int status) {
		ActivityRebate entity = aRebateDao.getById(id);
		if(entity != null) {
			entity.setStatus(status);
			boolean update = aRebateDao.update(entity);
			if (update) {
				dbServerSyncDao.update(DbServerSyncEnum.ACTIVITY);
			}
			return update;
		}
		return false;
	}

	@Override
	public boolean edit(int id, String rules, String startTime, String endTime) {
		ActivityRebate entity = aRebateDao.getById(id);
		if(entity != null) {
			entity.setRules(rules);
			entity.setStartTime(startTime);
			entity.setEndTime(endTime);
			boolean update = aRebateDao.update(entity);
			if (update) {
				dbServerSyncDao.update(DbServerSyncEnum.ACTIVITY);
			}
			return update;
		}
		return false;
	}

	@Override
	public ActivityRebate getByType(int type){
		return aRebateDao.getByType(type);
	}
	
	@Override
	public ActivityRebate getById(int id){
		return aRebateDao.getById(id);
	}
}