package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.LotteryType;
import lottery.domains.pool.LotteryDataFactory;

public class LotteryPlayRulesGroupSimpleVO {
	private String typeName; // 彩票类型名称
	private int typeId; // 彩票类型ID，对应lottery_type表id
	private int groupId; // 玩法组ID
	private String name; // 玩法组名称

	public LotteryPlayRulesGroupSimpleVO(LotteryPlayRulesGroup group, LotteryDataFactory dataFactory) {
		LotteryType lotteryType = dataFactory.getLotteryType(group.getTypeId());
		if (lotteryType != null) this.typeName = lotteryType.getName();

		this.typeId = group.getTypeId();
		this.groupId = group.getId();
		this.name = group.getName();
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
