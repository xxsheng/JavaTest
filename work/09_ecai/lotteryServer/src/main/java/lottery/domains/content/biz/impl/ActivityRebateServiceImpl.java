package lottery.domains.content.biz.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import activity.domains.content.dao.ActivityRebateDao;
import activity.domains.content.entity.ActivityRebate;
import lottery.domains.content.biz.ActivityRebateService;
import lottery.domains.content.global.Global;

/**
* <p>Title: ActivityRebateService.java</p>  
* <p>Description: </p>  
* @author James  
* @date 2018年2月3日
 */
@Service
public class ActivityRebateServiceImpl  implements  ActivityRebateService{
	@Autowired
	private ActivityRebateDao activityRebateDao;

	@Override
	public List<ActivityRebate> getRebateActivityList() {
		return activityRebateDao.getList(Global.ACTIVITY_STATUS_OPEN);
	}
	
	
}
