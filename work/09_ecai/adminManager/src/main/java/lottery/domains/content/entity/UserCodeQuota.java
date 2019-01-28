package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * UserCodeQuota entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_code_quota", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "code"}))
public class UserCodeQuota implements java.io.Serializable {
	private int id;
	private int userId; // 用户ID
	private int code; // 开号等级
	private int sysAllocateQuantity; // 系统分配数量
	private int upAllocateQuantity; // 上级分配数量

	public UserCodeQuota() {
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

	@Column(name = "user_id", nullable = false)
	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "code", nullable = false)
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Column(name = "sys_allocate_quantity")
	public int getSysAllocateQuantity() {
		return sysAllocateQuantity;
	}

	public void setSysAllocateQuantity(int sysAllocateQuantity) {
		this.sysAllocateQuantity = sysAllocateQuantity;
	}

	@Column(name = "up_allocate_quantity")
	public int getUpAllocateQuantity() {
		return upAllocateQuantity;
	}

	public void setUpAllocateQuantity(int upAllocateQuantity) {
		this.upAllocateQuantity = upAllocateQuantity;
	}
}