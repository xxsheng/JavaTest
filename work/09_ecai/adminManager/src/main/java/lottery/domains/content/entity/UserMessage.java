package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * UserMessage entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_message", catalog = Database.name)
public class UserMessage implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int toUid;
	private int fromUid;
	private int type;
	private String subject;
	private String content;
	private String time;
	private int toStatus;
	private int fromStatus;

	// Constructors

	/** default constructor */
	public UserMessage() {
	}

	/** full constructor */
	public UserMessage(int toUid, int fromUid, int type,
			String subject, String content, String time, int toStatus,
			int fromStatus) {
		this.toUid = toUid;
		this.fromUid = fromUid;
		this.type = type;
		this.subject = subject;
		this.content = content;
		this.time = time;
		this.toStatus = toStatus;
		this.fromStatus = fromStatus;
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

	@Column(name = "to_uid", nullable = false)
	public int getToUid() {
		return this.toUid;
	}

	public void setToUid(int toUid) {
		this.toUid = toUid;
	}

	@Column(name = "from_uid", nullable = false)
	public int getFromUid() {
		return this.fromUid;
	}

	public void setFromUid(int fromUid) {
		this.fromUid = fromUid;
	}

	@Column(name = "type", nullable = false)
	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "subject", nullable = false)
	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "content", nullable = false, length = 16777215)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "time", nullable = false, length = 19)
	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "to_status", nullable = false)
	public int getToStatus() {
		return this.toStatus;
	}

	public void setToStatus(int toStatus) {
		this.toStatus = toStatus;
	}

	@Column(name = "from_status", nullable = false)
	public int getFromStatus() {
		return this.fromStatus;
	}

	public void setFromStatus(int fromStatus) {
		this.fromStatus = fromStatus;
	}

}