package lottery.domains.content.vo.config;

import lottery.domains.pool.DataFactory;

public class SysLotteryConfigVO {

	private int sysCode;
	private double sysLp;
	private double sysNlp;

	private int bUnitMoney;
	private int fenModelDownCode;
	private int liModelDownCode;
	
	public SysLotteryConfigVO() {
		
	}
	
	public SysLotteryConfigVO(DataFactory dataFactory) {
		CodeConfig cConfig = dataFactory.getCodeConfig();
		this.sysCode = cConfig.getSysCode();
		this.sysLp = cConfig.getSysLp();
		this.sysNlp = cConfig.getSysNlp();
		LotteryConfig lConfig = dataFactory.getLotteryConfig();
		this.bUnitMoney = lConfig.getbUnitMoney();
		this.fenModelDownCode = lConfig.getFenModelDownCode();
		this.liModelDownCode = lConfig.getLiModelDownCode();
	}

	public int getSysCode() {
		return sysCode;
	}

	public void setSysCode(int sysCode) {
		this.sysCode = sysCode;
	}

	public double getSysLp() {
		return sysLp;
	}

	public void setSysLp(double sysLp) {
		this.sysLp = sysLp;
	}

	public double getSysNlp() {
		return sysNlp;
	}

	public void setSysNlp(double sysNlp) {
		this.sysNlp = sysNlp;
	}

	public int getbUnitMoney() {
		return bUnitMoney;
	}

	public void setbUnitMoney(int bUnitMoney) {
		this.bUnitMoney = bUnitMoney;
	}

	public int getFenModelDownCode() {
		return fenModelDownCode;
	}

	public void setFenModelDownCode(int fenModelDownCode) {
		this.fenModelDownCode = fenModelDownCode;
	}

	public int getLiModelDownCode() {
		return liModelDownCode;
	}

	public void setLiModelDownCode(int liModelDownCode) {
		this.liModelDownCode = liModelDownCode;
	}

}