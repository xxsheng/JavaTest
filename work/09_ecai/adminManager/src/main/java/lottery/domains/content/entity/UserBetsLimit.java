package lottery.domains.content.entity;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lottery.domains.content.global.Database;

@Entity
@Table(name = "user_bets_limit", catalog = Database.name,uniqueConstraints = @UniqueConstraint(columnNames = {
		"lottery_id", "user_id" , "max_bet"}))
public class UserBetsLimit {

	private int id;
	private int userId;
	private int lotteryId;
	private double maxBet;
	private double maxPrize;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}
	
	@Column(name = "max_prize")
	public double getMaxPrize() {
		return maxPrize;
	}
	
	public void setMaxPrize(double maxPrize) {
		this.maxPrize = maxPrize;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "user_id", nullable = false)
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	@Column(name = "lottery_id", nullable = false)
	public int getLotteryId() {
		return lotteryId;
	}
	
	public void setLotteryId(int lotteryId) {
		this.lotteryId = lotteryId;
	}
	
	@Column(name = "max_bet", nullable = false)
	public double getMaxBet() {
		return maxBet;
	}
	
	public void setMaxBet(double maxBet) {
		this.maxBet = maxBet;
	}
	
	
}
