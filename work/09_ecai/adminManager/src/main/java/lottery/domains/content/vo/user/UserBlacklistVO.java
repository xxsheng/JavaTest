package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserBlacklist;

public class UserBlacklistVO {
	private String bankName;
	private UserBlacklist bean;
	
	public UserBlacklistVO(UserBlacklist bean,String bankName){
		this.bankName = bankName;
		this.bean = bean;
	}
	
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public UserBlacklist getBean() {
		return bean;
	}
	public void setBean(UserBlacklist bean) {
		this.bean = bean;
	}
	
	
	
}
