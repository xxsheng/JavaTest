package admin.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * SSH工具类
 * 
 */
public class SSHHelper {

	/**
	 * 远程 执行命令并返回结果调用过程 是同步的（执行完才会返回）
	 * 
	 * @param host
	 *            主机名
	 * @param user
	 *            用户名
	 * @param passwd
	 *            密码
	 * @param port
	 *            端口
	 * @param command
	 *            命令
	 * @return
	 */
	public static String exec(String host, String user, String passwd,
			int port, String command) {
		String result = "";
		int exitStatus = -1;
		Session session = null;
		ChannelExec openChannel = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, port);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(passwd);
			session.connect();
			openChannel = (ChannelExec) session.openChannel("exec");
			openChannel.setCommand(command);

			openChannel.connect();
			InputStream in = openChannel.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String buf = null;
			while (!StringUtils.isEmpty(buf = reader.readLine())) {
				result += new String(buf.getBytes("utf8"), "UTF-8") + "\n";
			}
			exitStatus = openChannel.getExitStatus();
			//
			System.out.println(result);
			if (0 == exitStatus) {
				if (result.contains("running")) {
					result = "运行";
				} else if (result.contains("not running")) {
					result = "停止";
				} else if (result.contains("stopped")) {
					result = "停止";
				} else {
					result = "未知";
				}
			} else {
				result = "获取状态失败";
			}
		} catch (JSchException | IOException e) {
			result += e.getMessage();
		} finally {
			if (openChannel != null && !openChannel.isClosed()) {
				openChannel.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
		result = "{\"code\":" + exitStatus + ",\"message\":\"" + result + "\"}";
		return result;
	}

	public static String execQuick(String host, String user, String passwd,
			int port, String command) {
		String result = "";
		int exitStatus = -1;
		Session session = null;
		ChannelExec openChannel = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, port);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(passwd);
			session.connect();
			openChannel = (ChannelExec) session.openChannel("exec");
			openChannel.setCommand(command);

			openChannel.connect();
			InputStream in = openChannel.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String buf = null;
			if (!StringUtils.isEmpty(buf = reader.readLine())) {
				result += new String(buf.getBytes("utf8"), "UTF-8") + "\n";
			}
			exitStatus = openChannel.getExitStatus();
			//
			if (0 == exitStatus) {
				result = "运行";
			} else {
				result = "请重启";
			}
			System.out.println(exitStatus);
		} catch (JSchException | IOException e) {
			result += e.getMessage();
		} finally {
			if (openChannel != null && !openChannel.isClosed()) {
				openChannel.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
		result = "{\"code\":" + exitStatus + ",\"message\":\"" + result + "\"}";
		return result;
	}

	public static void main(String args[]) {
		String exec = exec("104.193.92.177", "root", "hellobc@hd2015", 22,
				"service tomcat LotteryServer stop");
		System.out.println(exec);
	}
}