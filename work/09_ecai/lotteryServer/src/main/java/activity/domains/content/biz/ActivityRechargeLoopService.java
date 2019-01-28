package activity.domains.content.biz;

import activity.domains.content.vo.activity.ActivityRechargeLoopVO;
import lottery.web.WebJSON;

public interface ActivityRechargeLoopService {

	ActivityRechargeLoopVO get(int userId);
	
	boolean receive(WebJSON json, int userId, String ip, int step);
	
}