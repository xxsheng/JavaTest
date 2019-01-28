package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.*;
import lottery.domains.pool.LotteryDataFactory;

public class LotteryPlayRulesVO {
	private String typeName; // 彩票类型名称
	private String groupName; // 玩法组名称
	private String lotteryName; // 彩票名称
	private int typeId; // 彩票类型ID
	private int groupId; // 玩法组ID
	private int lotteryId; // 彩票ID
	private int ruleId; // 玩法ID
	private String name; // 玩法名称
	private String code; // 玩法编码
	private String minNum; // 最小投注多少注,多个用英文逗号分割
	private String maxNum; // 最大投注多少注,多个用英文逗号分割
	private String totalNum; // 最大投注多少注,多个用英文逗号分割
	private int status; // 状态；0：启用；-1：禁用；优先级低于lottery_play_rules_config
	private int fixed; // 是否固定奖金；0：否；1：是；
	private String prize; // 奖金比例（百分比）或固定奖金，多个英文逗号分割
	private String desc; // 描述
	private String dantiao; // 单挑注数，多个英文逗号分割
	private int isLocate; // 是否是定位玩法；0：否；1：是；

	public LotteryPlayRulesVO(LotteryPlayRules rule, LotteryPlayRulesConfig config, LotteryDataFactory dataFactory) {
		LotteryType lotteryType = dataFactory.getLotteryType(rule.getTypeId());
		if (lotteryType != null) this.typeName = lotteryType.getName();

		LotteryPlayRulesGroup lotteryPlayRulesGroup = dataFactory.getLotteryPlayRulesGroup(rule.getGroupId());
		if (lotteryPlayRulesGroup != null) this.groupName = lotteryPlayRulesGroup.getName();

		this.typeId = rule.getTypeId();
		this.groupId = rule.getGroupId();
		this.ruleId = rule.getId();
		this.name = rule.getName();
		this.code = rule.getCode();
		this.totalNum = rule.getTotalNum();
		this.fixed = rule.getFixed();
		this.desc = rule.getDesc();
		this.dantiao = rule.getDantiao();
		this.isLocate = rule.getIsLocate();

		if (config != null) {
			// LotteryPlayRulesConfig优先级高于LotteryPlayRules
			this.minNum = config.getMinNum();
			this.maxNum = config.getMaxNum();
			this.status = config.getStatus();
			this.prize = config.getPrize();

			Lottery lottery = dataFactory.getLottery(config.getLotteryId());
			if (lottery != null) {
				this.lotteryId = lottery.getId();
				this.lotteryName = lottery.getShowName();
			}
		}
		else {
			this.minNum = rule.getMinNum();
			this.maxNum = rule.getMaxNum();
			this.status = rule.getStatus();
			this.prize = rule.getPrize();
		}
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

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(int lotteryId) {
		this.lotteryId = lotteryId;
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

	public String getMinNum() {
		return minNum;
	}

	public void setMinNum(String minNum) {
		this.minNum = minNum;
	}

	public String getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(String maxNum) {
		this.maxNum = maxNum;
	}

	public String getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(String totalNum) {
		this.totalNum = totalNum;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getFixed() {
		return fixed;
	}

	public void setFixed(int fixed) {
		this.fixed = fixed;
	}

	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDantiao() {
		return dantiao;
	}

	public void setDantiao(String dantiao) {
		this.dantiao = dantiao;
	}

	public int getIsLocate() {
		return isLocate;
	}

	public void setIsLocate(int isLocate) {
		this.isLocate = isLocate;
	}
}
