package admin.domains.content.biz.utils;

import admin.domains.content.entity.AdminUserAction;
import admin.domains.content.entity.AdminUserMenu;
import admin.domains.content.entity.AdminUserRole;
import admin.domains.pool.AdminDataFactory;
import javautils.StringUtil;
import net.sf.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TreeUtil {
	
	public static List<AdminUserMenu> listMenuRoot(List<AdminUserMenu> list) {
		List<AdminUserMenu> allEls = new LinkedList<>();
		for (AdminUserMenu bean : list) {
			allEls.add(bean.clone());
		}
		List<AdminUserMenu> treeEls = new LinkedList<AdminUserMenu>();
		for (AdminUserMenu bean : allEls) {
			if(bean.getUpid() == 0) {
				treeEls.add(bean);
				listMenuChild(allEls, bean);
			}
		}
		return treeEls;
	}
	
	public static void listMenuChild(List<AdminUserMenu> allEls, AdminUserMenu sbean) {
		for (AdminUserMenu bean : allEls) {
			if(bean.getUpid() == sbean.getId()) {
				sbean.getItems().add(bean);
				listMenuChild(allEls, bean);
			}
		}
	}
	
	public static List<AdminUserRole> listRoleRoot(List<AdminUserRole> list) {
		List<AdminUserRole> allEls = new LinkedList<AdminUserRole>();
		for (AdminUserRole bean : list) {
			allEls.add(bean.clone());
		}
		List<AdminUserRole> treeEls = new LinkedList<AdminUserRole>();
		for (AdminUserRole bean : allEls) {
			if(bean.getUpid() == 0) {
				treeEls.add(bean);
				listRoleChild(allEls, bean);
			}
		}
		return treeEls;
	}
	
	public static void listRoleChild(List<AdminUserRole> allEls, AdminUserRole sbean) {
		for (AdminUserRole bean : allEls) {
			if(bean.getUpid() == sbean.getId()) {
				sbean.getItems().add(bean);
				listRoleChild(allEls, bean);
			}
		}
	}

	public static List<JSMenuVO> listJSMenuRoot(List<AdminUserMenu> list) {
		List<JSMenuVO> mList = new LinkedList<>();
		listJSMenuChild(list, mList);
		return mList;
	}
	
	public static void listJSMenuChild(List<AdminUserMenu> list, List<JSMenuVO> mList) {
		for (AdminUserMenu adminUserMenu : list) {
			JSMenuVO jsMenu = new JSMenuVO();
			jsMenu.setName(adminUserMenu.getName());
			jsMenu.setIcon(adminUserMenu.getIcon());
			jsMenu.setLink(adminUserMenu.getLink());
			if(adminUserMenu.getItems().size() > 0) {
				listJSMenuChild(adminUserMenu.getItems(), jsMenu.getItems());
			}
			mList.add(jsMenu);
		}
	}
	
	public static List<JSTreeVO> listJSTreeRoot(List<AdminUserMenu> list, AdminDataFactory df) {
		List<JSTreeVO> treeList = new LinkedList<>();
		listJSTreeChild(list, treeList, df);
		return treeList;
	}
	
	public static void listJSTreeChild(List<AdminUserMenu> list, List<JSTreeVO> treeList, AdminDataFactory df) {
		for (AdminUserMenu adminUserMenu : list) {
			JSTreeVO jsTree = new JSTreeVO();
			jsTree.setId("menu_" + adminUserMenu.getId());
			jsTree.setText(adminUserMenu.getName());
			jsTree.setIcon(adminUserMenu.getIcon());
			if(adminUserMenu.getItems().size() > 0) {
				listJSTreeChild(adminUserMenu.getItems(), jsTree.getChildren(), df);
			}
			listMenuActions(adminUserMenu.getAllActions(), jsTree, df);
			treeList.add(jsTree);
		}
	}
	
	public static void listMenuActions(String allActions, JSTreeVO jsTree, AdminDataFactory df) {
		if(StringUtil.isNotNull(allActions)) {
			JSONArray json = JSONArray.fromObject(allActions);
			for (Object obj : json) {
				AdminUserAction adminUserAction = df.getAdminUserAction((int) obj);
				JSTreeVO acTree = new JSTreeVO();
				acTree.setId("action_" + adminUserAction.getId());
				acTree.setText(adminUserAction.getName());
				acTree.setIcon("fa fa-slack font-green-haze");
				jsTree.getChildren().add(acTree);
			}
		}
	}
	
	public static List<JSTreeVO> listJSTreeRoot2(List<AdminUserMenu> list, Map<Integer, AdminUserAction> aMap) {
		List<JSTreeVO> treeList = new LinkedList<>();
		listJSTreeChild2(list, treeList, aMap);
		return treeList;
	}
	
	public static void listJSTreeChild2(List<AdminUserMenu> list, List<JSTreeVO> treeList, Map<Integer, AdminUserAction> aMap) {
		for (AdminUserMenu adminUserMenu : list) {
			JSTreeVO jsTree = new JSTreeVO();
			jsTree.setId("menu_" + adminUserMenu.getId());
			jsTree.setText(adminUserMenu.getName());
			jsTree.setIcon(adminUserMenu.getIcon());
			if(adminUserMenu.getItems().size() > 0) {
				listJSTreeChild2(adminUserMenu.getItems(), jsTree.getChildren(), aMap);
			}
			listMenuActions2(adminUserMenu.getAllActions(), jsTree, aMap);
			treeList.add(jsTree);
		}
	}
	
	public static void listMenuActions2(String allActions, JSTreeVO jsTree, Map<Integer, AdminUserAction> aMap) {
		if(StringUtil.isNotNull(allActions)) {
			JSONArray json = JSONArray.fromObject(allActions);
			for (Object obj : json) {
				if(aMap.containsKey((int) obj)) {
					AdminUserAction adminUserAction = aMap.get((int) obj);
					JSTreeVO acTree = new JSTreeVO();
					acTree.setId("action_" + adminUserAction.getId());
					acTree.setText(adminUserAction.getName());
					acTree.setIcon("fa fa-slack font-green-haze");
					jsTree.getChildren().add(acTree);
				}
			}
		}
	}
	
}