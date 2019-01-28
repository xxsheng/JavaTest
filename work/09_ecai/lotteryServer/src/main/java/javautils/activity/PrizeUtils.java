package javautils.activity;

import java.util.ArrayList;
import java.util.List;

import activity.domains.content.vo.activity.ActivityCostInfoVo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PrizeUtils {
	/**
	 * 根据消费额拿的活动配置对应的消费奖励
	 * @param res
	 * @param costMoney
	 * @return
	 */
	public static double costConfigPrize(String res,double costMoney){
		double darwMoney = 0;
		JSONArray jsonArray = JSONArray.fromObject(res);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject obj = JSONObject.fromObject(String.valueOf(jsonArray.get(i)));
			double cost = Double.parseDouble(String.valueOf(obj.get("cost")));
			double prizeMoney = Double.parseDouble(String.valueOf(obj.get("prizeMoney")));
			if(costMoney >= cost && prizeMoney > darwMoney){
				darwMoney = prizeMoney;
			}
		}
		return darwMoney;
	}
	
	public static List<ActivityCostInfoVo> costConfigInfo(String res){
		JSONArray jsonArray = JSONArray.fromObject(res);
		List<ActivityCostInfoVo> listvo = new ArrayList<ActivityCostInfoVo>();
		for (int i = 0; i < jsonArray.size(); i++) {
			ActivityCostInfoVo cost = new ActivityCostInfoVo();
			JSONObject obj = JSONObject.fromObject(String.valueOf(jsonArray.get(i)));
			cost.setCost(Double.parseDouble(String.valueOf(obj.get("cost"))));
			cost.setPrizeMoney(Double.parseDouble(String.valueOf(obj.get("prizeMoney"))));
			listvo.add(cost);
		}
		return listvo;
	}
}
