package lottery.domains.content.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user_bets_hit_ranking", catalog = Database.name)
public class UserBetsHitRanking implements java.io.Serializable, Comparable<UserBetsHitRanking> {
	private static final long serialVersionUID = -2265717841611668255L;

	@JSONField(serialize = false)
	private int id;
	private String name;
	private String username;
	private int prizeMoney;
	private String time;
	private String code; // 游戏编码
	private String type; // 游戏类型
	private int platform; // 平台，2：彩票；4：AG；11：PT

	public UserBetsHitRanking() {
	}

	public UserBetsHitRanking(int id, String name, String username, int prizeMoney, String time, String code, String type, int platform) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.prizeMoney = prizeMoney;
		this.time = time;
		this.code = code;
		this.type = type;
		this.platform = platform;
	}

	public UserBetsHitRanking(String name, String username, int prizeMoney, String time, String code, String type, int platform) {
		this.name = name;
		this.username = username;
		this.prizeMoney = prizeMoney;
		this.time = time;
		this.code = code;
		this.type = type;
		this.platform = platform;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false, length = 256)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "username", nullable = false, length = 256)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "prize_money", nullable = false)
	public int getPrizeMoney() {
		return prizeMoney;
	}

	public void setPrizeMoney(int prizeMoney) {
		this.prizeMoney = prizeMoney;
	}

	@Column(name = "time", nullable = false, length = 20)
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Column(name = "code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "platform", nullable = false)
	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}

	@Override
	public int compareTo(UserBetsHitRanking o) {
		if (this.getPrizeMoney() == o.getPrizeMoney()) {
			return 1; // 排到最后去
		}

		return o.getPrizeMoney() > this.getPrizeMoney() ? 1 : -1;
	}
}