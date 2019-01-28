package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 彩票玩法彩票配置表
 */
@Entity
@Table(name = "lottery_play_rules_config", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {
		"rule_id", "lottery_id" }))
public class LotteryPlayRulesConfig implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id; // ID
	private int ruleId; // 玩法ID
	private int lotteryId; // 彩票ID
	private String minNum; // 最小投注多少注,多个用英文逗号分割
	private String maxNum; // 最大投注多少注,多个用英文逗号分割
	private int status; // 状态；0：启用；-1：禁用；
	private String prize; // 奖金比例（百分比）或固定奖金，多个英文逗号分割

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "rule_id", nullable = false)
	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	@Column(name = "lottery_id", nullable = false)
	public int getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(int lotteryId) {
		this.lotteryId = lotteryId;
	}

	@Column(name = "min_num", length = 128)
	public String getMinNum() {
		return minNum;
	}

	public void setMinNum(String minNum) {
		this.minNum = minNum;
	}

	@Column(name = "max_num", length = 128)
	public String getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(String maxNum) {
		this.maxNum = maxNum;
	}

	@Column(name = "status")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "prize", nullable = false, length = 512)
	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}
}