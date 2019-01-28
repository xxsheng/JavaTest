package lottery.web.content;

import java.util.Properties;

import javautils.http.EasyHttpClient;
import javautils.http.HttpUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import admin.domains.content.entity.AdminUser;
import admin.domains.jobs.AdminUserActionLogJob;
import admin.tools.ServerService;
import admin.tools.StringUtils;
import admin.tools.entity.ServerConfig;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;

@Controller
public class SysControlController extends AbstractActionController {

	@Autowired
	private AdminUserActionLogJob adminUserActionLogJob;

	@Autowired
	private ServerService mServerService;

	@RequestMapping(value = WUC.LOTTERY_SYS_CONTROL_DO, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_SYS_CONTROL_DO(HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		String actionKey = WUC.LOTTERY_SYS_CONTROL_DO;
		long t1 = System.currentTimeMillis();
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			// 上面是验证权限，没有权限无法访问
			String server = request.getParameter("server");
			String action = request.getParameter("action");// start restart
			ServerConfig config = getConfig(request, server);
			String result = mServerService.execute(config, action);
			HttpUtil.write(response, result, HttpUtil.json);
		} else {
			json.set(2, "2-6");
		}
		long t2 = System.currentTimeMillis();
		if (uEntity != null) {
			adminUserActionLogJob.add(request, actionKey, uEntity, json, t2
					- t1);
		}
		HttpUtil.write(response, json.toString(), HttpUtil.json);
	}

	@RequestMapping(value = WUC.LOTTERY_SYS_CONTROL_STATUS, method = { RequestMethod.POST })
	@ResponseBody
	public void LOTTERY_SYS_CONTROL_STATUS(HttpSession session,
			HttpServletRequest request, HttpServletResponse response) {
		String server = request.getParameter("server");
		String host = request.getParameter("host");
		EasyHttpClient client = new EasyHttpClient();
		String string = client.get(host
				+ "/lottery-sys-control?action=status&server=" + server);
		HttpUtil.write(response, string, HttpUtil.json);
	}

	/**
	 * 获取本地配置文件
	 * 
	 * @param req
	 * @param key
	 * @return
	 */
	public ServerConfig getConfig(HttpServletRequest req, String key) {
		try {
			Properties p = new Properties();
			ServletContext ctx = req.getSession().getServletContext();
			// 通过环境变量获得配置文件流
			p.load(ctx
					.getResourceAsStream("WEB-INF/properties/config.properties"));
			String values = p.getProperty(key);
			String[] split = values.split(";");
			int port = StringUtils.toInt(split[1], 22);
			ServerConfig config = new ServerConfig(split[0], port, split[2],
					split[3], split[4]);
			return config;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}