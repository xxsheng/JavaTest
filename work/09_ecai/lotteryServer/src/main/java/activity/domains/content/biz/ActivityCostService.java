package activity.domains.content.biz;


import java.util.Date;

import activity.domains.content.entity.ActivityCostBill;
import activity.domains.content.vo.activity.ActivityCostDrawInfoVo;
import activity.domains.content.vo.activity.ActivityRebateVo;

public interface ActivityCostService {
	//活动信息
	ActivityRebateVo  getCostInfo(int type);
	
	//领取信息
	ActivityCostDrawInfoVo getCostDarwInfo(int userId,String startTime ,String endTime);
	
	//查询用户最近一条领取记录
	ActivityCostBill getLastCostBillInfo(int userId);
	
	//查询当前IP最近一条领取记录
	ActivityCostBill getLastCostBillInfoByIp(String ip);
	
	//得到今天的领取信息
	ActivityCostBill getToDayDrawInfo(int userId,String startTime ,String endTime);
	
	//领取奖励
	boolean darwCostAward(int userId,Double costMoney,Double prizeMoney,Date now,String ip) ;
	
}
