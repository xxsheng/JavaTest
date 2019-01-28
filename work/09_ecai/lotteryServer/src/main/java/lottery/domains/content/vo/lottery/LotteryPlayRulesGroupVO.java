package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.LotteryPlayRulesGroup;

public class LotteryPlayRulesGroupVO {
	private int groupId; // 玩法组ID
	private String name; // 玩法组名称
	private String code; // 玩法组编码

	public LotteryPlayRulesGroupVO(LotteryPlayRulesGroup group) {
		this.groupId = group.getId();
		this.name = group.getName();
		this.code = group.getCode();
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}