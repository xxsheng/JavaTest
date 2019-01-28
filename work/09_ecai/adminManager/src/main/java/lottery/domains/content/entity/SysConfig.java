package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * SysConfig entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_config", catalog = Database.name)
public class SysConfig implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String group;
	private String key;
	private String value;
	private String description;

	// Constructors

	/** default constructor */
	public SysConfig() {
	}

	/** minimal constructor */
	public SysConfig(String value) {
		this.value = value;
	}

	/** full constructor */
	public SysConfig(String value, String description) {
		this.value = value;
		this.description = description;
	}

	// Property accessors
	@Id
	@Column(name = "`group`", nullable = false, length = 64)
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	@Id
	@Column(name = "`key`", nullable = false, length = 64)
	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Column(name = "`value`", nullable = false)
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}