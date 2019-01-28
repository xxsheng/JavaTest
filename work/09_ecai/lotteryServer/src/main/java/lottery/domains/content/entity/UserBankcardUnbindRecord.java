package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * UserCard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_bankcard_unbind_record", catalog = Database.name)
public class UserBankcardUnbindRecord implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;//
	private String userIds;	//用户ID
	private String cardId;//银行卡号 索引  唯一约束
	private int unbindNum;//解绑次数
	private String unbindTime;//最后一次解绑时间

	// Constructors

	/** default constructor */
	public UserBankcardUnbindRecord() {
	}

	/** minimal constructor */
	public UserBankcardUnbindRecord(String userIds, String cardId, int unbindNum,
			String unbindTime) {
		this.userIds = userIds;
		this.cardId = cardId;
		this.unbindNum = unbindNum;
		this.unbindTime = unbindTime;
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

	@Column(name = "user_ids", nullable = false)
	public String getUserIds() {
		return this.userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

	@Column(name = "card_id", unique = true, nullable = false, length = 128)
	public String getCardId() {
		return this.cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	@Column(name = "unbind_num", nullable = false)
	public int getUnbindNum() {
		return unbindNum;
	}

	public void setUnbindNum(int unbindNum) {
		this.unbindNum = unbindNum;
	}

	@Column(name = "unbind_time", nullable = false)
	public String getUnbindTime() {
		return unbindTime;
	}

	public void setUnbindTime(String unbindTime) {
		this.unbindTime = unbindTime;
	}
}