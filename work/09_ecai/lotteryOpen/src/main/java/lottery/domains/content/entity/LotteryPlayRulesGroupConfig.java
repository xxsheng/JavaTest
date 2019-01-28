package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 玩法组关联彩票配置表
 */
@Entity
@Table(name = "lottery_play_rules_group_config", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {
		"group_id", "lottery_id" }))
public class LotteryPlayRulesGroupConfig implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private int groupId; // 玩法组ID
	private int lotteryId; // 彩票ID
	private int status; // 状态；0：启用；-1：禁用；

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "group_id", nullable = false)
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	@Column(name = "lottery_id", nullable = false)
	public int getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(int lotteryId) {
		this.lotteryId = lotteryId;
	}

	@Column(name = "status")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}