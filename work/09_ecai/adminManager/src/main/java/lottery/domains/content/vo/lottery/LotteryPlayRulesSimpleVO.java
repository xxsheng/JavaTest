package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.LotteryType;
import lottery.domains.pool.LotteryDataFactory;

public class LotteryPlayRulesSimpleVO {
	private String typeName; // 彩票类型名称
	private String groupName; // 玩法组名称
	private int typeId; // 彩票类型ID
	private int groupId; // 玩法组ID
	private int ruleId; // 玩法ID
	private String name; // 玩法名称
	private String code; // 玩法编码

	public LotteryPlayRulesSimpleVO(LotteryPlayRules rule, LotteryDataFactory dataFactory) {
		LotteryType lotteryType = dataFactory.getLotteryType(rule.getTypeId());
		if (lotteryType != null) this.typeName = lotteryType.getName();

		LotteryPlayRulesGroup lotteryPlayRulesGroup = dataFactory.getLotteryPlayRulesGroup(rule.getGroupId());
		if (lotteryPlayRulesGroup != null) this.groupName = lotteryPlayRulesGroup.getName();

		this.typeId = rule.getTypeId();
		this.groupId = rule.getGroupId();
		this.ruleId = rule.getId();
		this.name = rule.getName();
		this.code = rule.getCode();
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
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
