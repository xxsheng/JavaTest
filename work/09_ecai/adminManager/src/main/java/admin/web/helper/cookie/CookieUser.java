package admin.web.helper.cookie;

import java.io.Serializable;

public final class CookieUser implements Serializable {

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;

	public CookieUser() {
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
}
