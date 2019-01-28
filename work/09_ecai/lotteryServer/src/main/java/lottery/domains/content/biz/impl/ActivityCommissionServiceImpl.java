package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import activity.domains.content.dao.ActivityRebateDao;
import activity.domains.content.entity.ActivityRebate;
import lottery.domains.content.biz.ActivityCommissionService;
import lottery.domains.content.global.Global;

/**
* <p>Title: ActivityCommissionServiceImpl</p>  
* <p>Description: </p>  
* @author James  
* @date 2018年2月3日
 */
@Service
public class ActivityCommissionServiceImpl  implements ActivityCommissionService {
	@Autowired
	private ActivityRebateDao activityRebateDao;

	@Override
	public List<ActivityRebate> getCommissionServiceList() {
		// 盈亏佣金
		ActivityRebate yingkuiRebate = activityRebateDao.getByStatusAndType(Global.ACTIVITY_REBATE_REWARD_YINGKUI,Global.ACTIVITY_STATUS_OPEN);
		//消费佣金
		ActivityRebate xiaofeiRebate = activityRebateDao.getByStatusAndType(Global.ACTIVITY_REBATE_REWARD_XIAOFEI,Global.ACTIVITY_STATUS_OPEN);
		List<ActivityRebate> resultList = new ArrayList<ActivityRebate>();
		resultList.add(yingkuiRebate);
		resultList.add(xiaofeiRebate);
		return resultList;
	}
	
	
}
