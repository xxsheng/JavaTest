package lottery.domains.contentold.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * MusercvuUsers entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "musercvu_users", catalog = "hydb5bgyl", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class MusercvuUsers implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String username;
	private String nickname;
	private String password;
	private String passwords;
	private Integer daili;
	private Integer parentid;
	private String email;
	private String qq;
	private Integer sock;
	private String phonenumber;
	private Double limit;
	private Double resultdian;
	private Double resultdianBdw;
	private Double allmoney;
	private Double icemoney;
	private Double uermoney;
	private Integer userRank;
	private String addtime;
	private String userpass;
	private String userpass1;
	private String bankBank;
	private String bankName;
	private String bankNum;
	private String bankArea;
	private String regfrom;
	private String bankWen;
	private String bankDa;
	private Integer model;
	private Integer playmodel;
	private Timestamp activetime;
	private Timestamp savetime;
	private Integer islock;
	private Integer lock;
	private Double lmmoney;
	private Integer result128;
	private Integer result126;
	private Integer result125;
	private Integer result124;
	private Integer result123;
	private Integer result122;
	private Integer result121;
	private Integer result120;
	private Integer lockS;
	private String ccbName;
	private String ccbNum;
	private Double uermoneyOld;
	private Double uermoneyFh;
	private String rowguid;
	private Timestamp htaddtime;
	private String ct;
	private String address;
	private Integer stateinformation;
	private Integer fh;
	private Double virtual;
	private Timestamp freezeTime;
	private Timestamp zijinPassordModifyTime;
	private Integer upwdIsModifiedAtFirst;
	private Integer zpwdIsModifiedAtFirst;

	// Constructors

	/** default constructor */
	public MusercvuUsers() {
	}

	/** minimal constructor */
	public MusercvuUsers(String username) {
		this.username = username;
	}

	/** full constructor */
	public MusercvuUsers(String username, String nickname, String password,
			String passwords, Integer daili, Integer parentid, String email,
			String qq, Integer sock, String phonenumber, Double limit,
			Double resultdian, Double resultdianBdw, Double allmoney,
			Double icemoney, Double uermoney, Integer userRank,
			String addtime, String userpass, String userpass1,
			String bankBank, String bankName, String bankNum, String bankArea,
			String regfrom, String bankWen, String bankDa, Integer model,
			Integer playmodel, Timestamp activetime, Timestamp savetime,
			Integer islock, Integer lock, Double lmmoney, Integer result128,
			Integer result126, Integer result125, Integer result124,
			Integer result123, Integer result122, Integer result121,
			Integer result120, Integer lockS, String ccbName, String ccbNum,
			Double uermoneyOld, Double uermoneyFh, String rowguid,
			Timestamp htaddtime, String ct, String address,
			Integer stateinformation, Integer fh, Double virtual,
			Timestamp freezeTime, Timestamp zijinPassordModifyTime,
			Integer upwdIsModifiedAtFirst, Integer zpwdIsModifiedAtFirst) {
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.passwords = passwords;
		this.daili = daili;
		this.parentid = parentid;
		this.email = email;
		this.qq = qq;
		this.sock = sock;
		this.phonenumber = phonenumber;
		this.limit = limit;
		this.resultdian = resultdian;
		this.resultdianBdw = resultdianBdw;
		this.allmoney = allmoney;
		this.icemoney = icemoney;
		this.uermoney = uermoney;
		this.userRank = userRank;
		this.addtime = addtime;
		this.userpass = userpass;
		this.userpass1 = userpass1;
		this.bankBank = bankBank;
		this.bankName = bankName;
		this.bankNum = bankNum;
		this.bankArea = bankArea;
		this.regfrom = regfrom;
		this.bankWen = bankWen;
		this.bankDa = bankDa;
		this.model = model;
		this.playmodel = playmodel;
		this.activetime = activetime;
		this.savetime = savetime;
		this.islock = islock;
		this.lock = lock;
		this.lmmoney = lmmoney;
		this.result128 = result128;
		this.result126 = result126;
		this.result125 = result125;
		this.result124 = result124;
		this.result123 = result123;
		this.result122 = result122;
		this.result121 = result121;
		this.result120 = result120;
		this.lockS = lockS;
		this.ccbName = ccbName;
		this.ccbNum = ccbNum;
		this.uermoneyOld = uermoneyOld;
		this.uermoneyFh = uermoneyFh;
		this.rowguid = rowguid;
		this.htaddtime = htaddtime;
		this.ct = ct;
		this.address = address;
		this.stateinformation = stateinformation;
		this.fh = fh;
		this.virtual = virtual;
		this.freezeTime = freezeTime;
		this.zijinPassordModifyTime = zijinPassordModifyTime;
		this.upwdIsModifiedAtFirst = upwdIsModifiedAtFirst;
		this.zpwdIsModifiedAtFirst = zpwdIsModifiedAtFirst;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "username", unique = true, nullable = false, length = 32)
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "nickname", length = 50)
	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Column(name = "password", length = 64)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "passwords", length = 64)
	public String getPasswords() {
		return this.passwords;
	}

	public void setPasswords(String passwords) {
		this.passwords = passwords;
	}

	@Column(name = "daili")
	public Integer getDaili() {
		return this.daili;
	}

	public void setDaili(Integer daili) {
		this.daili = daili;
	}

	@Column(name = "parentid")
	public Integer getParentid() {
		return this.parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	@Column(name = "email", length = 50)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "qq", length = 15)
	public String getQq() {
		return this.qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	@Column(name = "sock")
	public Integer getSock() {
		return this.sock;
	}

	public void setSock(Integer sock) {
		this.sock = sock;
	}

	@Column(name = "phonenumber", length = 20)
	public String getPhonenumber() {
		return this.phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	@Column(name = "limit", scale = 4)
	public Double getLimit() {
		return this.limit;
	}

	public void setLimit(Double limit) {
		this.limit = limit;
	}

	@Column(name = "resultdian", precision = 22, scale = 0)
	public Double getResultdian() {
		return this.resultdian;
	}

	public void setResultdian(Double resultdian) {
		this.resultdian = resultdian;
	}

	@Column(name = "resultdianBDW", precision = 22, scale = 0)
	public Double getResultdianBdw() {
		return this.resultdianBdw;
	}

	public void setResultdianBdw(Double resultdianBdw) {
		this.resultdianBdw = resultdianBdw;
	}

	@Column(name = "allmoney", scale = 4)
	public Double getAllmoney() {
		return this.allmoney;
	}

	public void setAllmoney(Double allmoney) {
		this.allmoney = allmoney;
	}

	@Column(name = "icemoney", scale = 4)
	public Double getIcemoney() {
		return this.icemoney;
	}

	public void setIcemoney(Double icemoney) {
		this.icemoney = icemoney;
	}

	@Column(name = "uermoney", scale = 4)
	public Double getUermoney() {
		return this.uermoney;
	}

	public void setUermoney(Double uermoney) {
		this.uermoney = uermoney;
	}

	@Column(name = "user_rank")
	public Integer getUserRank() {
		return this.userRank;
	}

	public void setUserRank(Integer userRank) {
		this.userRank = userRank;
	}

	@Column(name = "addtime", length = 19)
	public String getAddtime() {
		return this.addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	@Column(name = "userpass", length = 32)
	public String getUserpass() {
		return this.userpass;
	}

	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}

	@Column(name = "userpass1", length = 32)
	public String getUserpass1() {
		return this.userpass1;
	}

	public void setUserpass1(String userpass1) {
		this.userpass1 = userpass1;
	}

	@Column(name = "bank_bank", length = 50)
	public String getBankBank() {
		return this.bankBank;
	}

	public void setBankBank(String bankBank) {
		this.bankBank = bankBank;
	}

	@Column(name = "bank_name", length = 50)
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Column(name = "bank_num", length = 100)
	public String getBankNum() {
		return this.bankNum;
	}

	public void setBankNum(String bankNum) {
		this.bankNum = bankNum;
	}

	@Column(name = "bank_area", length = 50)
	public String getBankArea() {
		return this.bankArea;
	}

	public void setBankArea(String bankArea) {
		this.bankArea = bankArea;
	}

	@Column(name = "regfrom")
	public String getRegfrom() {
		return this.regfrom;
	}

	public void setRegfrom(String regfrom) {
		this.regfrom = regfrom;
	}

	@Column(name = "bank_wen", length = 50)
	public String getBankWen() {
		return this.bankWen;
	}

	public void setBankWen(String bankWen) {
		this.bankWen = bankWen;
	}

	@Column(name = "bank_da", length = 50)
	public String getBankDa() {
		return this.bankDa;
	}

	public void setBankDa(String bankDa) {
		this.bankDa = bankDa;
	}

	@Column(name = "model")
	public Integer getModel() {
		return this.model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}

	@Column(name = "playmodel")
	public Integer getPlaymodel() {
		return this.playmodel;
	}

	public void setPlaymodel(Integer playmodel) {
		this.playmodel = playmodel;
	}

	@Column(name = "activetime", length = 19)
	public Timestamp getActivetime() {
		return this.activetime;
	}

	public void setActivetime(Timestamp activetime) {
		this.activetime = activetime;
	}

	@Column(name = "savetime", length = 19)
	public Timestamp getSavetime() {
		return this.savetime;
	}

	public void setSavetime(Timestamp savetime) {
		this.savetime = savetime;
	}

	@Column(name = "islock")
	public Integer getIslock() {
		return this.islock;
	}

	public void setIslock(Integer islock) {
		this.islock = islock;
	}

	@Column(name = "lock")
	public Integer getLock() {
		return this.lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	@Column(name = "LMmoney", scale = 4)
	public Double getLmmoney() {
		return this.lmmoney;
	}

	public void setLmmoney(Double lmmoney) {
		this.lmmoney = lmmoney;
	}

	@Column(name = "result_128")
	public Integer getResult128() {
		return this.result128;
	}

	public void setResult128(Integer result128) {
		this.result128 = result128;
	}

	@Column(name = "result_126")
	public Integer getResult126() {
		return this.result126;
	}

	public void setResult126(Integer result126) {
		this.result126 = result126;
	}

	@Column(name = "result_125")
	public Integer getResult125() {
		return this.result125;
	}

	public void setResult125(Integer result125) {
		this.result125 = result125;
	}

	@Column(name = "result_124")
	public Integer getResult124() {
		return this.result124;
	}

	public void setResult124(Integer result124) {
		this.result124 = result124;
	}

	@Column(name = "result_123")
	public Integer getResult123() {
		return this.result123;
	}

	public void setResult123(Integer result123) {
		this.result123 = result123;
	}

	@Column(name = "result_122")
	public Integer getResult122() {
		return this.result122;
	}

	public void setResult122(Integer result122) {
		this.result122 = result122;
	}

	@Column(name = "result_121")
	public Integer getResult121() {
		return this.result121;
	}

	public void setResult121(Integer result121) {
		this.result121 = result121;
	}

	@Column(name = "result_120")
	public Integer getResult120() {
		return this.result120;
	}

	public void setResult120(Integer result120) {
		this.result120 = result120;
	}

	@Column(name = "lock_s")
	public Integer getLockS() {
		return this.lockS;
	}

	public void setLockS(Integer lockS) {
		this.lockS = lockS;
	}

	@Column(name = "ccb_name", length = 50)
	public String getCcbName() {
		return this.ccbName;
	}

	public void setCcbName(String ccbName) {
		this.ccbName = ccbName;
	}

	@Column(name = "ccb_num", length = 50)
	public String getCcbNum() {
		return this.ccbNum;
	}

	public void setCcbNum(String ccbNum) {
		this.ccbNum = ccbNum;
	}

	@Column(name = "uermoney_old", scale = 4)
	public Double getUermoneyOld() {
		return this.uermoneyOld;
	}

	public void setUermoneyOld(Double uermoneyOld) {
		this.uermoneyOld = uermoneyOld;
	}

	@Column(name = "uermoney_fh", scale = 4)
	public Double getUermoneyFh() {
		return this.uermoneyFh;
	}

	public void setUermoneyFh(Double uermoneyFh) {
		this.uermoneyFh = uermoneyFh;
	}

	@Column(name = "rowguid", length = 50)
	public String getRowguid() {
		return this.rowguid;
	}

	public void setRowguid(String rowguid) {
		this.rowguid = rowguid;
	}

	@Column(name = "htaddtime", length = 19)
	public Timestamp getHtaddtime() {
		return this.htaddtime;
	}

	public void setHtaddtime(Timestamp htaddtime) {
		this.htaddtime = htaddtime;
	}

	@Column(name = "CT", length = 50)
	public String getCt() {
		return this.ct;
	}

	public void setCt(String ct) {
		this.ct = ct;
	}

	@Column(name = "address", length = 50)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "Stateinformation")
	public Integer getStateinformation() {
		return this.stateinformation;
	}

	public void setStateinformation(Integer stateinformation) {
		this.stateinformation = stateinformation;
	}

	@Column(name = "Fh")
	public Integer getFh() {
		return this.fh;
	}

	public void setFh(Integer fh) {
		this.fh = fh;
	}

	@Column(name = "virtual", scale = 4)
	public Double getVirtual() {
		return this.virtual;
	}

	public void setVirtual(Double virtual) {
		this.virtual = virtual;
	}

	@Column(name = "FreezeTime", length = 19)
	public Timestamp getFreezeTime() {
		return this.freezeTime;
	}

	public void setFreezeTime(Timestamp freezeTime) {
		this.freezeTime = freezeTime;
	}

	@Column(name = "zijinPassordModifyTime", length = 19)
	public Timestamp getZijinPassordModifyTime() {
		return this.zijinPassordModifyTime;
	}

	public void setZijinPassordModifyTime(Timestamp zijinPassordModifyTime) {
		this.zijinPassordModifyTime = zijinPassordModifyTime;
	}

	@Column(name = "upwdIsModifiedAtFirst")
	public Integer getUpwdIsModifiedAtFirst() {
		return this.upwdIsModifiedAtFirst;
	}

	public void setUpwdIsModifiedAtFirst(Integer upwdIsModifiedAtFirst) {
		this.upwdIsModifiedAtFirst = upwdIsModifiedAtFirst;
	}

	@Column(name = "zpwdIsModifiedAtFirst")
	public Integer getZpwdIsModifiedAtFirst() {
		return this.zpwdIsModifiedAtFirst;
	}

	public void setZpwdIsModifiedAtFirst(Integer zpwdIsModifiedAtFirst) {
		this.zpwdIsModifiedAtFirst = zpwdIsModifiedAtFirst;
	}

}