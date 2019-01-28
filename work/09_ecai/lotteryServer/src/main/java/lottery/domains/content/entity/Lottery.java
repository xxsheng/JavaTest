package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Lottery entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "lottery", catalog = Database.name)
public class Lottery implements java.io.Serializable {

	// Fields

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String showName;
	private String shortName;
	private int type;
	private int times;
	private int sort;
	private int status;
	private int self; // 是否是自主彩,1:是，其它：不是
	private int expectTrans; // 是否需要转换彩种期号,0：不需要；1：需要
	private String allowModels; // 允许使用的模式
	private int display; // 是否显示；1：显示；其它：不显示

	// Constructors

	/** default constructor */
	public Lottery() {
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "show_name", nullable = false, length = 32)
	public String getShowName() {
		return this.showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	@Column(name = "short_name", length = 32, nullable = false)
	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Column(name = "type", nullable = false)
	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "times", nullable = false)
	public int getTimes() {
		return this.times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	@Column(name = "`sort`", nullable = false)
	public int getSort() {
		return this.sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "self", nullable = false)
	public int getSelf() {
		return self;
	}

	public void setSelf(int self) {
		this.self = self;
	}

	@Column(name = "expect_trans", nullable = false)
	public int getExpectTrans() {
		return expectTrans;
	}

	public void setExpectTrans(int expectTrans) {
		this.expectTrans = expectTrans;
	}

	@Column(name = "allow_models", nullable = false)
	public String getAllowModels() {
		return allowModels;
	}

	public void setAllowModels(String allowModels) {
		this.allowModels = allowModels;
	}

	@Column(name = "display", nullable = false)
	public int getDisplay() {
		return display;
	}

	public void setDisplay(int display) {
		this.display = display;
	}
}