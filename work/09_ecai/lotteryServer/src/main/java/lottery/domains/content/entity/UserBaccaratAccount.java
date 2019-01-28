package lottery.domains.content.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lottery.domains.content.global.Database;

/**
 * UserBaccaratAccount entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "user_baccarat_account", catalog = Database.name)
public class UserBaccaratAccount implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int userId;
	private double ag;
	private double bbin;
	private double ea;
	private double hg;
	private double og;
	private double bet188;
	private double mg;
	private double pt;

	// Constructors

	/** default constructor */
	public UserBaccaratAccount() {
	}

	/** full constructor */
	public UserBaccaratAccount(double ag, double bbin, double ea, double hg,
			double og, double bet188, double mg, double pt) {
		this.ag = ag;
		this.bbin = bbin;
		this.ea = ea;
		this.hg = hg;
		this.og = og;
		this.bet188 = bet188;
		this.mg = mg;
		this.pt = pt;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "user_id", unique = true, nullable = false)
	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Column(name = "ag", nullable = false, precision = 11, scale = 3)
	public double getAg() {
		return this.ag;
	}

	public void setAg(double ag) {
		this.ag = ag;
	}

	@Column(name = "bbin", nullable = false, precision = 11, scale = 3)
	public double getBbin() {
		return this.bbin;
	}

	public void setBbin(double bbin) {
		this.bbin = bbin;
	}

	@Column(name = "ea", nullable = false, precision = 11, scale = 3)
	public double getEa() {
		return this.ea;
	}

	public void setEa(double ea) {
		this.ea = ea;
	}

	@Column(name = "hg", nullable = false, precision = 11, scale = 3)
	public double getHg() {
		return this.hg;
	}

	public void setHg(double hg) {
		this.hg = hg;
	}

	@Column(name = "og", nullable = false, precision = 11, scale = 3)
	public double getOg() {
		return this.og;
	}

	public void setOg(double og) {
		this.og = og;
	}

	@Column(name = "bet188", nullable = false, precision = 11, scale = 3)
	public double getBet188() {
		return this.bet188;
	}

	public void setBet188(double bet188) {
		this.bet188 = bet188;
	}

	@Column(name = "mg", nullable = false, precision = 11, scale = 3)
	public double getMg() {
		return this.mg;
	}

	public void setMg(double mg) {
		this.mg = mg;
	}

	@Column(name = "pt", nullable = false, precision = 11, scale = 3)
	public double getPt() {
		return this.pt;
	}

	public void setPt(double pt) {
		this.pt = pt;
	}

}