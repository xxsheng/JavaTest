package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 彩票玩法表
 */
@Entity
@Table(name = "lottery_play_rules", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {
		"type_id", "code" }))
public class LotteryPlayRules implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id; // ID
	private int groupId; // 玩法组ID
	private int typeId; // 彩票类型ID，对应lottery_type表id
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

	@Column(name = "type_id", nullable = false)
	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	@Column(name = "name", nullable = false, length = 128)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "code", nullable = false, length = 128)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	@Column(name = "total_num", length = 128)
	public String getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(String totalNum) {
		this.totalNum = totalNum;
	}

	@Column(name = "status")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "fixed", nullable = false)
	public int getFixed() {
		return fixed;
	}

	public void setFixed(int fixed) {
		this.fixed = fixed;
	}

	@Column(name = "prize", nullable = false, length = 512)
	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}

	@Column(name = "desc", length = 256)
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Column(name = "dantiao", length = 128)
	public String getDantiao() {
		return dantiao;
	}

	public void setDantiao(String dantiao) {
		this.dantiao = dantiao;
	}

	@Column(name = "is_locate", nullable = false)
	public int getIsLocate() {
		return isLocate;
	}

	public void setIsLocate(int isLocate) {
		this.isLocate = isLocate;
	}
}