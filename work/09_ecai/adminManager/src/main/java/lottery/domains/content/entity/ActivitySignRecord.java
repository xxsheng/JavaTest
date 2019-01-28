package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * ActivitySignRecord entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "activity_sign_record", catalog = Database.name)
public class ActivitySignRecord implements java.io.Serializable {
	private int id;
	private int userId;
	private int days;
	private String startTime;
	private String lastSignTime;
	private String lastCollectTime;

	// Constructors

	public ActivitySignRecord() {
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

	@Column(name = "user_id", nullable = false, unique = true)
	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "days", nullable = false)
	public int getDays() {
		return this.days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	@Column(name = "start_time", length = 19)
	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Column(name = "last_sign_time", length = 19)
	public String getLastSignTime() {
		return this.lastSignTime;
	}

	public void setLastSignTime(String lastSignTime) {
		this.lastSignTime = lastSignTime;
	}

	@Column(name = "last_collect_time", length = 19)
	public String getLastCollectTime() {
		return lastCollectTime;
	}

	public void setLastCollectTime(String lastCollectTime) {
		this.lastCollectTime = lastCollectTime;
	}
}