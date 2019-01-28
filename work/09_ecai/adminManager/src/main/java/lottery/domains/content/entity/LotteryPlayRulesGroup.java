package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 玩法组表
 */
@Entity
@Table(name = "lottery_play_rules_group", catalog = Database.name)
public class LotteryPlayRulesGroup implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private int typeId; // 彩票类型ID，对应lottery_type表id
	private String name; // 玩法组名称
	private String code; // 玩法组编码
	private int status; // 状态；0：启用；-1：禁用；优先级低于lottery_play_rules_group_config；
	private int sort; // 排序号，根据type_id进行排序

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	@Column(name = "status")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "sort")
	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
}