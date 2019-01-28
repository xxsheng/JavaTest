package admin.tools;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.stereotype.Service;

import admin.tools.entity.ServerConfig;

/**
 * 
 * 重启服务
 * 
 * @author ROOT
 * 
 */
@Service
public class ServerService {
	/**
	 * 配置文件里面的key
	 */
//	private static final String SSH_USER = "user";
//	private static final String SSH_PASSWD = "passwd";
//
//	private static final String CMD_START_GETLOTTERYCODE = "cd /home/sundy/project/GetLotteryCode && sh startup.sh";
//
//	private static String CMD_TOMCAT = "service tomcat ";

	/**
	 * 
	 * @param serverConfig
	 * @param action
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String execute(ServerConfig serverConfig, String action) {
		String result = "";
		String cmd = serverConfig.getCmd();
		String hosts = serverConfig.getHost();
		String user = serverConfig.getUser();
		String passwd = serverConfig.getPassword();
		int port = serverConfig.getPort();

		String[] hostArr = parserHosts(hosts);
		for (String host : hostArr) {
			try {
				result = SSHHelper.execQuick(host, user, passwd, port, cmd
						+ " " + action);
			} catch (Exception e) {
				result = "{\"code\":" + -2 + ",\"message\":\"" + "操作失败,重试！"
						+ "\"}";
			}
		}

		return result;
	}

	/**
	 * 获取对应的server的ip地址
	 * 
	 * @param req
	 * @param key
	 * @return
	 */
	public String[] parserHosts(String hosts) {
		if (null != hosts && !"".equals(hosts)) {
			String[] values = hosts.split(",");
			return values;
		}
		return null;
	}

}
