package admin.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * 远程执行shell脚本类
 * 
 * @author Hacker
 */
public class RmtShellExecutor {

	/**  */
	private Connection conn;
	/** 远程机器IP */
	private String ip;
	/** 用户名 */
	private String usr;
	/** 密码 */
	private String psword;
	private String charset = Charset.defaultCharset().toString();

	private static final int TIME_OUT = 1000 * 1 * 60;

	/**
	 * 构造函数
	 * 
	 * @param ip
	 * @param usr
	 * @param ps
	 */
	public RmtShellExecutor(String ip, String user, String passwd) {
		this.ip = ip;
		this.usr = user;
		this.psword = passwd;
	}

	/**
	 * 登录
	 * 
	 * @return
	 * @throws IOException
	 */
	private boolean login() throws IOException {
		conn = new Connection(ip);
		conn.connect();
		return conn.authenticateWithPassword(usr, psword);
	}

	/**
	 * 执行脚本
	 * 
	 * @param cmds
	 * @return
	 * @throws Exception
	 */
	public int execNoResultMessage(String cmds) {
		int ret = -1;
		try {
			try {
				if (login()) {
					// Open a new {@link Session} on this connection
					Session session = conn.openSession();
					// Execute a command on the remote machine.
					session.execCommand(cmds);
					// stdOut = new StreamGobbler(session.getStdout());
					// outStr = processStream(stdOut, charset);

					// stdErr = new StreamGobbler(session.getStderr());
					// outErr = processStream(stdErr, charset);
					session.waitForCondition(ChannelCondition.EXIT_STATUS,
							TIME_OUT);
					ret = session.getExitStatus();
				} else {
					System.out.println("登录远程机器失败" + ip);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param cmds
	 * @param retMessage
	 * @return
	 */
	public String execCommand(String cmds, boolean retMessage) {
		String result = "";
		String outStr = "1";
		int ret = -1;
		try {
			try {
				if (login()) {
					// Open a new {@link Session} on this connection
					Session session = conn.openSession();
					if (null != session) {
						// Execute a command on the remote machine.
						session.execCommand(cmds);
						if (retMessage) {
							StreamGobbler stdOut = new StreamGobbler(
									session.getStdout());
							outStr = processStream(stdOut, charset);
						}
						session.waitForCondition(ChannelCondition.EXIT_STATUS,
								TIME_OUT);
						ret = session.getExitStatus();
						if (retMessage) {
							if (0 == ret) {
								if (null != outStr
										&& outStr.contains("stopped")) {
									outStr = "服务器已停止...";
								} else if (null != outStr
										&& outStr.contains("not running")) {
									outStr = "服务器不在运行当中...";
								} else if (null != outStr
										&& outStr.contains("running")) {
									outStr = "服务器正在运行...";
								} else {
									outStr = "服务器未知错误...";
								}
							}
						} else {
							if (0 == ret) {
								outStr = "Lucky！服务器操作成功...";
							} else {
								outStr = "oups！服务器操作失败...";
							}
						}
					}
				} else {
					outStr = "登录远程机器失败...";
				}
			} catch (Exception e) {
				e.printStackTrace();
				outStr = "程序未知异常";
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		result = "{\"code\":" + ret + ",\"message\":\"" + outStr + "\"}";
		System.out.println(result);
		return result;
	}

	/**
	 * @param in
	 * @param charset
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public String processStream(InputStream in, String charset)
			throws Exception {
		byte[] buf = new byte[1024];
		StringBuilder sb = new StringBuilder();
		while (in.read(buf) != -1) {
			sb.append(new String(buf, charset));
		}
		return sb.toString();
	}

	public static void main(String args[]) throws Exception {
		RmtShellExecutor exe = new RmtShellExecutor("104.193.92.177", "root",
				"hellobc@hd2015");
		// 执行myTest.sh 参数为java Know dummy
		// if (0 ==
		// exe.execNoResultMessage("service tomcat LotteryServer stop")) {
		// System.out.println("success");
		// } else {
		// System.out.println("error");
		// }
		exe.execCommand("service tomcat LotteryServer start", false);

	}
}