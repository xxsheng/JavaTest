package lottery.domains.content.biz;

import lottery.domains.content.entity.UserPlanInfo;

public interface UserPlanInfoService {

	UserPlanInfo get(int userId);
	
}