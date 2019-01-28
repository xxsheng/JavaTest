package admin.domains.content.biz;

import java.util.List;

import admin.domains.content.vo.AdminUserActionVO;
import javautils.jdbc.PageList;

public interface AdminUserCriticalLogService {

	//查询操作关键日志
	PageList search(Integer actionId,String username, String ip, String keyword, String sDate, String eDate, int start, int limit);
	//查询修改姓名银行卡操作日志
	PageList search(String username, int start, int limit);
	
	//查询操作关键日志
	List<AdminUserActionVO> findAction();
	
}