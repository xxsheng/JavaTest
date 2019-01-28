package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserSecurity;

public class UserSecurityVO {

	private int id;
	private String key;
	private String value;

	public UserSecurityVO(UserSecurity bean) {
		this.id = bean.getId();
		this.key = bean.getKey();
		this.value = "***";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}