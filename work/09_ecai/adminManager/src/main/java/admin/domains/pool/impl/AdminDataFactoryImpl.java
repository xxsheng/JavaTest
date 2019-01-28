package admin.domains.pool.impl;

import admin.domains.content.dao.AdminUserActionDao;
import admin.domains.content.dao.AdminUserDao;
import admin.domains.content.dao.AdminUserMenuDao;
import admin.domains.content.dao.AdminUserRoleDao;
import admin.domains.content.entity.AdminUser;
import admin.domains.content.entity.AdminUserAction;
import admin.domains.content.entity.AdminUserMenu;
import admin.domains.content.entity.AdminUserRole;
import admin.domains.content.vo.AdminUserBaseVO;
import admin.domains.pool.AdminDataFactory;
import admin.web.WSC;
import com.alibaba.fastjson.JSON;
import javautils.StringUtil;
import javautils.ip.IpUtil;
import lottery.domains.content.biz.UserHighPrizeService;
import lottery.web.websocket.WebSocketMsgSender;
import net.sf.json.JSONArray;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@Component
public class AdminDataFactoryImpl implements AdminDataFactory, InitializingBean, ServletContextAware {

	private static final Logger logger = LoggerFactory.getLogger(AdminDataFactoryImpl.class);
	
	@Override
	public void init() {
		logger.info("init AdminDataFactory....start");
		initSysMessage();
		initIpData();
		
		initAdminUserAction();
		initAdminUserMenu();
		initAdminUserRole();
		logger.info("init AdminDataFactory....done");
	}
	
	@Override
	public void setServletContext(ServletContext context) {
		WSC.PROJECT_PATH = context.getRealPath("").replace("\\", "/");
		logger.info("Project Path:" + WSC.PROJECT_PATH);
	}
	
	/**
	 * 初始化ip数据库
	 */
	private void initIpData() {
		try {
			File file = ResourceUtils.getFile("classpath:config/ip/17monipdb.dat");
			IpUtil.load(file.getPath());
			logger.info("初始化ip数据库完成！");
		} catch (Exception e) {
			logger.error("初始化ip数据库失败！");
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.init();
	}
	
	/**
	 * 初始化语言文件
	 */
	private Properties sysMessage;
	
	@Override
	public void initSysMessage() {
		try {
			String fileClassPath = "classpath:config/message/language.cn.xml";
			File file = ResourceUtils.getFile(fileClassPath);
			if(file != null) {
				Properties properties = new Properties();
				InputStream inputStream = new FileInputStream(file);
				properties.loadFromXML(inputStream);
				inputStream.close();
				if (sysMessage != null) {
					sysMessage.clear();
				}
				sysMessage = properties;
				logger.info("初始化语言文件完成。");
			} else {
				throw new FileNotFoundException();
			}
		} catch (Exception e) {
			logger.error("加载语言文件失败！", e);
		}
	}
	
	@Override
	public String getSysMessage(String key) {
		return sysMessage.getProperty(key);
	}
	
	/**
	 * 初始化用户基础信息
	 */
	@Autowired
	private AdminUserDao adminUserDao;
	
	private Map<Integer, AdminUserBaseVO> adminUserMap = new LinkedHashMap<>();
	
	@Override
	public void initAdminUser() {
		try {
			List<AdminUser> list = adminUserDao.listAll();
			Map<Integer, AdminUserBaseVO> tmpMap = new LinkedHashMap<>();
			for (AdminUser adminUser : list) {
				tmpMap.put(adminUser.getId(), new AdminUserBaseVO(adminUser.getId(), adminUser.getUsername()));
			}
			if(adminUserMap != null) {
				adminUserMap.clear();
			}
			adminUserMap = tmpMap;
			logger.info("初始化系统用户完成！");
		} catch (Exception e) {
			logger.error("初始化系统用户失败！");
		}
	}
	
	@Override
	public AdminUserBaseVO getAdminUser(int id) {
		if(adminUserMap.containsKey(id)) {
			return adminUserMap.get(id);
		}
		AdminUser adminUser = adminUserDao.getById(id);
		if(adminUser != null) {
			adminUserMap.put(adminUser.getId(), new AdminUserBaseVO(adminUser.getId(), adminUser.getUsername()));
			return adminUserMap.get(id);
		}
		return null;
	}
	
	/**
	 * 初始化管理员行为
	 */
	@Autowired
	private AdminUserActionDao adminUserActionDao;
	
	private Map<Integer, AdminUserAction> adminUserActionMap = new LinkedHashMap<>();
	
	@Override
	public void initAdminUserAction() {
		try {
			List<AdminUserAction> list = adminUserActionDao.listAll();
			Map<Integer, AdminUserAction> tmpMap = new LinkedHashMap<>();
			for (AdminUserAction adminUserAction : list) {
				tmpMap.put(adminUserAction.getId(), adminUserAction);
			}
			if(adminUserActionMap != null) {
				adminUserActionMap.clear();
			}
			adminUserActionMap = tmpMap;
			logger.info("初始化管理员行为分组完成！");
		} catch (Exception e) {
			logger.error("初始化管理员行为分组失败！");
		}
	}
	
	@Override
	public List<AdminUserAction> listAdminUserAction() {
		List<AdminUserAction> list = new ArrayList<>();
		Object[] keys = adminUserActionMap.keySet().toArray();
		for (Object o : keys) {
			list.add(adminUserActionMap.get(o));
		}
		return list;
	}
	
	@Override
	public AdminUserAction getAdminUserAction(int id) {
		if(adminUserActionMap.containsKey(id)) {
			return adminUserActionMap.get(id);
		}
		return null;
	}
	
	@Override
	public AdminUserAction getAdminUserAction(String actionKey) {
		if(StringUtil.isNotNull(actionKey)) {
			Object[] keys = adminUserActionMap.keySet().toArray();
			for (Object o : keys) {
				AdminUserAction adminUserAction = adminUserActionMap.get(o);
				if(actionKey.equals(adminUserAction.getKey())) {
					return adminUserAction;
				}
			}
		}
		return null;
	}
	
	@Override
	public List<AdminUserAction> getAdminUserActionByRoleId(int role) {
		List<AdminUserAction> list = new ArrayList<>();
		if(adminUserRoleMap.containsKey(role)) {
			AdminUserRole adminUserRole = adminUserRoleMap.get(role);
			if(StringUtil.isNotNull(adminUserRole.getActions())) {
				JSONArray menuJson = JSONArray.fromObject(adminUserRole.getActions());
				for (Object obj : menuJson) {
					if(adminUserActionMap.containsKey((int) obj)) {
						list.add(adminUserActionMap.get((int) obj));
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 初始化管理员菜单
	 */
	@Autowired
	private AdminUserMenuDao adminUserMenuDao;
	
	private Map<Integer, AdminUserMenu> adminUserMenuMap = new LinkedHashMap<>();
	
	@Override
	public void initAdminUserMenu() {
		try {
			List<AdminUserMenu> list = adminUserMenuDao.listAll();
			Map<Integer, AdminUserMenu> tmpMap = new LinkedHashMap<>();
			for (AdminUserMenu adminUserMenu : list) {
				tmpMap.put(adminUserMenu.getId(), adminUserMenu);
			}
			if(adminUserMenuMap != null) {
				adminUserMenuMap.clear();
			}
			adminUserMenuMap = tmpMap;
			logger.info("初始化管理员菜单完成！");
		} catch (Exception e) {
			logger.error("初始化管理员菜单失败！");
		}
	}
	
	@Override
	public List<AdminUserMenu> listAdminUserMenu() {
		List<AdminUserMenu> list = new ArrayList<>();
		Object[] keys = adminUserMenuMap.keySet().toArray();
		for (Object o : keys) {
			// 最好是克隆一份出来
			list.add(adminUserMenuMap.get(o).clone());
		}
		return list;
	}
	
	@Override
	public AdminUserMenu getAdminUserMenuByLink(String link) {
		Object[] keys = adminUserMenuMap.keySet().toArray();
		for (Object o : keys) {
			AdminUserMenu tmpMenu = adminUserMenuMap.get(o);
			if(link != null && link.equals(tmpMenu.getLink())) {
				return tmpMenu.clone();
			}
		}
		return null;
	}
	
	public List<AdminUserMenu> getAdminUserMenuByRoleId(int role) {
		List<AdminUserMenu> list = new ArrayList<>();
		if(adminUserRoleMap.containsKey(role)) {
			AdminUserRole adminUserRole = adminUserRoleMap.get(role);
			if(StringUtil.isNotNull(adminUserRole.getMenus())) {
				// JSONArray menuJson = JSONArray.fromObject(adminUserRole.getMenus());
				List<Integer> menuIds = JSON.parseArray(adminUserRole.getMenus(), Integer.class);
				for (Object menuId : menuIds) {
					if(adminUserMenuMap.containsKey(menuId)) {
						// 最好是克隆一份出来
						list.add(adminUserMenuMap.get(menuId).clone());
					}
				}
			}
		}

		Collections.sort(list, new Comparator<AdminUserMenu>() {
			@Override
			public int compare(AdminUserMenu o1, AdminUserMenu o2) {
				if ( o1.getSort() > o2.getSort()) {
					return 1;
				}
				else if ( o1.getSort() < o2.getSort()) {
					return -1;
				}

				return 0;
			}
		});

		return list;
	}
	
	@Override
	public Set<Integer> getAdminUserMenuIdsByAction(int action) {
		Set<Integer> set = new HashSet<>();
		Object[] keys = adminUserMenuMap.keySet().toArray();
		for (Object o : keys) {
			// 最好是克隆一份出来
			AdminUserMenu tmpBean = adminUserMenuMap.get(o).clone();
			// 需要基础查询权限
			if(tmpBean.getBaseAction() != 0) {
				JSONArray jsonArrayActions=JSONArray.fromObject(tmpBean.getAllActions());
				if (jsonArrayActions.contains(action)) {
					set.add(tmpBean.getId());
					// 查找，并向上查找
					int upid = tmpBean.getUpid();
					do {
						if(adminUserMenuMap.containsKey(upid)) {
							// 最好是克隆一份出来
							AdminUserMenu upMenu = adminUserMenuMap.get(upid).clone();
							if(upMenu != null) {
								set.add(upMenu.getId());
								upid = upMenu.getUpid();
							}
						}
					} while (upid != 0);
				}
			}
		}
		return set;
	}
	
	/**
	 * 初始化管理员角色
	 */
	@Autowired
	private AdminUserRoleDao adminUserRoleDao;
	
	private Map<Integer, AdminUserRole> adminUserRoleMap = new LinkedHashMap<>();
	
	@Override
	public void initAdminUserRole() {
		try {
			List<AdminUserRole> list = adminUserRoleDao.listAll();
			Map<Integer, AdminUserRole> tmpMap = new LinkedHashMap<>();
			for (AdminUserRole adminUserRole : list) {
				tmpMap.put(adminUserRole.getId(), adminUserRole);
			}
			if(adminUserRoleMap != null) {
				adminUserRoleMap.clear();
			}
			adminUserRoleMap = tmpMap;
			logger.info("初始化管理员角色完成！");
		} catch (Exception e) {
			logger.error("初始化管理员角色失败！");
		}
	}
	
	@Override
	public AdminUserRole getAdminUserRole(int id) {
		if(adminUserRoleMap.containsKey(id)) {
			return adminUserRoleMap.get(id).clone();
		}
		return null;
	}
	
	@Override
	public List<AdminUserRole> listAdminUserRole() {
		List<AdminUserRole> adminUserRoleList = new ArrayList<>();
		Object[] keys = adminUserRoleMap.keySet().toArray();
		for (Object o : keys) {
			adminUserRoleList.add(adminUserRoleMap.get(o).clone());
		}
		return adminUserRoleList;
	}

	private static volatile boolean isRunningHighPrizeNotice = false; // 标识任务是否正在运行
	private static Object highPrizeNoticeLock = new Object(); // 锁

	@Autowired
	private UserHighPrizeService highPrizeService;

	@Autowired
	private WebSocketMsgSender msgSender;

	@Scheduled(cron = "0/3 * * * * *")
	public void highPrizeNoticesJob() {
		synchronized (highPrizeNoticeLock) {
			if (isRunningHighPrizeNotice == true) {
				// 任务正在运行，本次中断
				return;
			}
			isRunningHighPrizeNotice = true;
		}

		try {
			// 发送大额中奖通知
			highPrizeNotices();
		} finally {
			isRunningHighPrizeNotice = false;
		}
	}

	private void highPrizeNotices() {
		Map<String, String> allHighPrizeNotices = highPrizeService.getAllHighPrizeNotices();
		if (MapUtils.isEmpty(allHighPrizeNotices)) {
			return;
		}
		Set<String> keys = allHighPrizeNotices.keySet();
		for (String key : keys) {
			msgSender.sendHighPrizeNotice(allHighPrizeNotices.get(key));
			highPrizeService.delHighPrizeNotice(key);
		}
	}
}