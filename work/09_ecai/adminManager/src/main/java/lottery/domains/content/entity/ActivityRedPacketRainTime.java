package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "activity_red_packet_rain_time", catalog = Database.name, uniqueConstraints = @UniqueConstraint(columnNames = {
		"date", "hour"}))
public class ActivityRedPacketRainTime implements java.io.Serializable {
	private int id;
	private String date;
	private String hour;
	private String startTime;
	private String endTime;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "date", nullable = false, length = 10)
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Column(name = "hour", nullable = false, length = 2)
	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	@Column(name = "start_time", nullable = false, length = 20)
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time", nullable = false, length = 20)
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}