package admin.domains.content.entity;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Transient;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lottery.domains.content.global.Database;

/**
 * AdminUserRole entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "admin_user_role", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class AdminUserRole implements java.io.Serializable, Cloneable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private int upid;
	private String description;
	private int sort;
	private int status;
	private String menus;
	private String actions;
	private ArrayList<AdminUserRole> items = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public AdminUserRole clone() {
		try {
			AdminUserRole cloneBean = (AdminUserRole) super.clone();
			cloneBean.items = (ArrayList<AdminUserRole>) this.items.clone();
			return cloneBean;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Constructors

	/** default constructor */
	public AdminUserRole() {
	}

	/** minimal constructor */
	public AdminUserRole(String name, int upid, int sort, int status) {
		this.name = name;
		this.upid = upid;
		this.sort = sort;
		this.status = status;
	}

	/** full constructor */
	public AdminUserRole(String name, int upid, String description, int sort,
			int status, String menus, String actions) {
		this.name = name;
		this.upid = upid;
		this.description = description;
		this.sort = sort;
		this.status = status;
		this.menus = menus;
		this.actions = actions;
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

	@Column(name = "upid", nullable = false)
	public int getUpid() {
		return upid;
	}

	public void setUpid(int upid) {
		this.upid = upid;
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

	@Column(name = "menus", length = 65535)
	public String getMenus() {
		return this.menus;
	}

	public void setMenus(String menus) {
		this.menus = menus;
	}

	@Column(name = "actions", length = 65535)
	public String getActions() {
		return this.actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	@Transient
	public ArrayList<AdminUserRole> getItems() {
		return items;
	}

	public void setItems(ArrayList<AdminUserRole> items) {
		this.items = items;
	}

}