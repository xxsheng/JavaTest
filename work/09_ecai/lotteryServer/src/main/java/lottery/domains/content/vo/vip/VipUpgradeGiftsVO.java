package lottery.domains.content.vo.vip;

import lottery.domains.content.entity.VipUpgradeGifts;

public class VipUpgradeGiftsVO {

	private int level;
	private double money;
	private VipUpgradeGifts bean;
	
	public VipUpgradeGiftsVO() {
		
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public VipUpgradeGifts getBean() {
		return bean;
	}

	public void setBean(VipUpgradeGifts bean) {
		this.bean = bean;
	}

}