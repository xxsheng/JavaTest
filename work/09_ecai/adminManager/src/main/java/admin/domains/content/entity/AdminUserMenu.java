package admin.domains.content.entity;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Transient;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * AdminUserMenu entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "admin_user_menu", catalog = Database.name)
public class AdminUserMenu implements java.io.Serializable, Cloneable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String icon;
	private String link;
	private int upid;
	private int sort;
	private int status;
	private int baseAction;
	private String allActions;
	private ArrayList<AdminUserMenu> items = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	public AdminUserMenu clone() {
		try {
			AdminUserMenu cloneBean = (AdminUserMenu) super.clone();
			cloneBean.items = (ArrayList<AdminUserMenu>) this.items.clone();
			return cloneBean;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Constructors

	/** default constructor */
	public AdminUserMenu() {
	}

	/** minimal constructor */
	public AdminUserMenu(String name, String icon, String link, int upid,
			int sort, int status) {
		this.name = name;
		this.icon = icon;
		this.link = link;
		this.upid = upid;
		this.sort = sort;
		this.status = status;
	}

	/** full constructor */
	public AdminUserMenu(String name, String icon, String link, int upid,
			int sort, int status, int baseAction, String allActions) {
		this.name = name;
		this.icon = icon;
		this.link = link;
		this.upid = upid;
		this.sort = sort;
		this.status = status;
		this.baseAction = baseAction;
		this.allActions = allActions;
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

	@Column(name = "name", nullable = false, length = 64)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "icon", nullable = false)
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Column(name = "link", nullable = false)
	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Column(name = "upid", nullable = false)
	public int getUpid() {
		return this.upid;
	}

	public void setUpid(int upid) {
		this.upid = upid;
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

	@Column(name = "base_action", nullable = false)
	public int getBaseAction() {
		return this.baseAction;
	}

	public void setBaseAction(int baseAction) {
		this.baseAction = baseAction;
	}

	@Column(name = "all_actions", length = 65535)
	public String getAllActions() {
		return this.allActions;
	}

	public void setAllActions(String allActions) {
		this.allActions = allActions;
	}
	
	@Transient
	public ArrayList<AdminUserMenu> getItems() {
		return items;
	}

	public void setItems(ArrayList<AdminUserMenu> items) {
		this.items = items;
	}

}