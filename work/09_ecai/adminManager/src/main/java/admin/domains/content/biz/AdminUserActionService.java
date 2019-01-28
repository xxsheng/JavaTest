package admin.domains.content.biz;

import java.util.List;

import admin.domains.content.vo.AdminUserActionVO;

public interface AdminUserActionService {

	List<AdminUserActionVO> listAll();
	
	boolean updateStatus(int id, int status);
	
}