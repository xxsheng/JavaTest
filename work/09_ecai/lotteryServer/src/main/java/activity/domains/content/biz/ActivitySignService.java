package activity.domains.content.biz;

import activity.domains.content.vo.activity.ActivitySignVO;
import lottery.web.WebJSON;

public interface ActivitySignService {

	/**
	 * 获取当前连续签到情况
	 */
	ActivitySignVO getSignData(int userId);

	/**
	 * 签到，等于0：签到成功; 小于0：签到失败; 大于0：签到成功并返回成功领取的金额
	 */
	double sign(WebJSON json, int userId);
}