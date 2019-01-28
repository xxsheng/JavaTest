package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lottery.domains.content.global.Database;

/**
 * SysPlatform entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_platform", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class SysPlatform implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private int upid;
	private int status;

	// Constructors

	/** default constructor */
	public SysPlatform() {
	}

	/** full constructor */
	public SysPlatform(String name, int upid, int status) {
		this.name = name;
		this.upid = upid;
		this.status = status;
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

	@Column(name = "name", unique = true, nullable = false, length = 128)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "upid", nullable = false)
	public int getUpid() {
		return this.upid;
	}

	public void setUpid(int upid) {
		this.upid = upid;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}