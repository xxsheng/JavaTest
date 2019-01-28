package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.LotteryPlayRulesGroupConfig;
import lottery.domains.content.entity.LotteryType;
import lottery.domains.pool.LotteryDataFactory;

public class LotteryPlayRulesGroupVO {
	private String typeName; // 彩票类型名称
	private String lotteryName; // 彩票名称
	private int typeId; // 彩票类型ID，对应lottery_type表id
	private int lotteryId; // 彩票ID
	private int groupId; // 玩法组ID
	private String name; // 玩法组名称
	private String code; // 玩法组编码
	private int status; // 状态；0：启用；-1：禁用；优先级低于lottery_play_rules_group_config；

	public LotteryPlayRulesGroupVO(LotteryPlayRulesGroup group, LotteryPlayRulesGroupConfig config, LotteryDataFactory dataFactory) {
		LotteryType lotteryType = dataFactory.getLotteryType(group.getTypeId());
		if (lotteryType != null) this.typeName = lotteryType.getName();

		this.typeId = group.getTypeId();
		this.groupId = group.getId();
		this.name = group.getName();
		this.code = group.getCode();

		if (config != null) {
			// LotteryPlayRulesGroupConfig优先级高于LotteryPlayRulesGroup
			this.status = config.getStatus();
			this.lotteryId = config.getLotteryId();

			Lottery lottery = dataFactory.getLottery(config.getLotteryId());
			if (lottery != null) {
				this.lotteryName = lottery.getShowName();
			}
		}
		else {
			this.status = group.getStatus();
		}
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getLotteryName() {
		return lotteryName;
	}

	public void setLotteryName(String lotteryName) {
		this.lotteryName = lotteryName;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(int lotteryId) {
		this.lotteryId = lotteryId;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
