package admin.tools.entity;

/**
 * 服务器配置
 * 
 * @author jay
 *
 */
public class ServerConfig {

	private String host;// 服务器ip地址
	private int port;// ssh 端口
	private String user;// 登陆用户名
	private String password;// 登陆密码
	private String cmd; // 操作内容

	public ServerConfig(String host, int port, String user, String password,
			String cmd) {
		super();
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.cmd = cmd;
	}

	public ServerConfig() {
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

}