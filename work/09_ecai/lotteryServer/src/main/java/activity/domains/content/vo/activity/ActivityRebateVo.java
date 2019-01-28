package activity.domains.content.vo.activity;

import java.util.List;

public class ActivityRebateVo {
	private List<ActivityCostInfoVo> costInfo;
	private int status;//状态（0为正在进行，-1 为已停止）
	
	public List<ActivityCostInfoVo> getCostInfo() {
		return costInfo;
	}
	public void setCostInfo(List<ActivityCostInfoVo> costInfo) {
		this.costInfo = costInfo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
