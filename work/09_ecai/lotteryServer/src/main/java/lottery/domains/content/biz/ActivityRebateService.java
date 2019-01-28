package lottery.domains.content.biz;

import java.util.List;

import activity.domains.content.entity.ActivityRebate;

/**
 * 
* <p>Title: ActivityRebateService.java</p>  
* <p>Description:返点活动服务类 </p>  
* @author James  
* @date 2018年2月3日
 */
public interface ActivityRebateService {
	/**
	 * <p>Title: getCommissionServiceList</p>  
	 * <p>Description: 查询所有返点活动</p>
	 * @return
	 */
   List	<ActivityRebate> getRebateActivityList();
}
