package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesConfig;
import lottery.domains.content.entity.LotteryPlayRulesGroup;

public class LotteryPlayRulesVO {
	private int ruleId; // 玩法ID
	private int groupId; // 玩法组ID
	private String name; // 玩法名称
	private String groupName; // 玩法组名称
	private String code; // 玩法编码
	private String minNum; // 最小投注数或单行码数，多个英文逗号分割
	private String maxNum; // 最大投注数或单行码数，多个英文逗号分割
	private boolean fixed; // 是否固定奖金；true：是；false：否；
	private String prize; // 奖金比例（百分比）或固定奖金，多个英文逗号分割
	private String dantiao; // 单挑注数，多个英文逗号分割

	public LotteryPlayRulesVO(LotteryPlayRules rule, LotteryPlayRulesConfig config, LotteryPlayRulesGroup group) {
		this.ruleId = rule.getId();
		this.name = rule.getName();
		this.code = rule.getCode();
		this.fixed = rule.getFixed() == 0 ? false : true;
		this.dantiao = rule.getDantiao();

		if (config != null) {
			this.minNum = config.getMinNum();
			this.maxNum = config.getMaxNum();
			this.prize = config.getPrize();
		}
		else {
			this.minNum = rule.getMinNum();
			this.maxNum = rule.getMaxNum();
			this.prize = rule.getPrize();
		}

		if (group != null) {
			this.groupId = group.getId();
			this.groupName = group.getName();
		}
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

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}

	public String getDantiao() {
		return dantiao;
	}

	public void setDantiao(String dantiao) {
		this.dantiao = dantiao;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}