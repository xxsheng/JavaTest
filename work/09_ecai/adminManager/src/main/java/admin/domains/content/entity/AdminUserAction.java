package admin.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lottery.domains.content.global.Database;

/**
 * AdminUserAction entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "admin_user_action", catalog = Database.name, uniqueConstraints = {
		@UniqueConstraint(columnNames = "key"),
		@UniqueConstraint(columnNames = "name") })
public class AdminUserAction implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String key;
	private String description;
	private int sort;
	private int status;

	// Constructors

	/** default constructor */
	public AdminUserAction() {
	}

	/** minimal constructor */
	public AdminUserAction(String name, String key, int sort, int status) {
		this.name = name;
		this.key = key;
		this.sort = sort;
		this.status = status;
	}

	/** full constructor */
	public AdminUserAction(String name, String key, String description,
			int sort, int status) {
		this.name = name;
		this.key = key;
		this.description = description;
		this.sort = sort;
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

	@Column(name = "name", unique = true, nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "key", unique = true, nullable = false)
	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "sort", nullable = false)
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

}