package lottery.domains.content.biz;

import java.util.List;

import activity.domains.content.entity.ActivityRebate;

/**
 * 
* <p>Title: ActivityCommissionService</p>  
* <p>Description:佣金服务类 </p>  
* @author James  
* @date 2018年2月3日
 */
public interface ActivityCommissionService {
	/**
	 * <p>Title: getCommissionServiceList</p>  
	 * <p>Description: 查询消费与亏损佣金</p>
	 * @return
	 */
   List	<ActivityRebate> getCommissionServiceList();
}
