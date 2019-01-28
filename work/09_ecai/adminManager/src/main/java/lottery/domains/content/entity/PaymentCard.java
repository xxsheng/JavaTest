package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * PaymentCard entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "payment_card", catalog = Database.name)
public class PaymentCard implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int bankId;
	private String cardName;
	private String cardId;
	private String branchName;
	private double totalCredits;
	private double usedCredits;
	private double minTotalRecharge;
	private double maxTotalRecharge;
	private String startTime;
	private String endTime;
	private double minUnitRecharge;
	private double maxUnitRecharge;
	private int status;


	public PaymentCard() {
	}

	public PaymentCard(int bankId, String cardName, String cardId, String branchName, double totalCredits, double usedCredits, double minTotalRecharge, double maxTotalRecharge, String startTime, String endTime, double minUnitRecharge, double maxUnitRecharge, int status) {
		this.bankId = bankId;
		this.cardName = cardName;
		this.cardId = cardId;
		this.branchName = branchName;
		this.totalCredits = totalCredits;
		this.usedCredits = usedCredits;
		this.minTotalRecharge = minTotalRecharge;
		this.maxTotalRecharge = maxTotalRecharge;
		this.startTime = startTime;
		this.endTime = endTime;
		this.minUnitRecharge = minUnitRecharge;
		this.maxUnitRecharge = maxUnitRecharge;
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

	@Column(name = "bank_id", nullable = false)
	public int getBankId() {
		return this.bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}

	@Column(name = "card_name", nullable = false)
	public String getCardName() {
		return this.cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	@Column(name = "card_id", nullable = false, length = 512)
	public String getCardId() {
		return this.cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	@Column(name = "total_credits", nullable = false, precision = 12, scale = 3)
	public double getTotalCredits() {
		return this.totalCredits;
	}

	public void setTotalCredits(double totalCredits) {
		this.totalCredits = totalCredits;
	}

	@Column(name = "used_credits", nullable = false, precision = 12, scale = 3)
	public double getUsedCredits() {
		return this.usedCredits;
	}

	public void setUsedCredits(double usedCredits) {
		this.usedCredits = usedCredits;
	}

	@Column(name = "min_total_recharge", nullable = false, precision = 12, scale = 3)
	public double getMinTotalRecharge() {
		return minTotalRecharge;
	}

	public void setMinTotalRecharge(double minTotalRecharge) {
		this.minTotalRecharge = minTotalRecharge;
	}

	@Column(name = "max_total_recharge", nullable = false, precision = 12, scale = 3)
	public double getMaxTotalRecharge() {
		return maxTotalRecharge;
	}

	public void setMaxTotalRecharge(double maxTotalRecharge) {
		this.maxTotalRecharge = maxTotalRecharge;
	}

	@Column(name = "start_time", length = 20)
	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time", length = 20)
	public String getEndTime() {
		return this.endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	@Column(name = "min_unit_recharge", nullable = false, precision = 12, scale = 3)
	public double getMinUnitRecharge() {
		return minUnitRecharge;
	}

	public void setMinUnitRecharge(double minUnitRecharge) {
		this.minUnitRecharge = minUnitRecharge;
	}

	@Column(name = "max_unit_recharge", nullable = false, precision = 12, scale = 3)
	public double getMaxUnitRecharge() {
		return maxUnitRecharge;
	}

	public void setMaxUnitRecharge(double maxUnitRecharge) {
		this.maxUnitRecharge = maxUnitRecharge;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "branch_name")
	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

}