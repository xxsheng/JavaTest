package admin.web.helper.session;

import java.io.Serializable;

public final class SessionUser implements Serializable {

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private int roleId;

	public SessionUser() {

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
}
