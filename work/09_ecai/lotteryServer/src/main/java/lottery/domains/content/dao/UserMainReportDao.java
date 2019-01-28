package lottery.domains.content.dao;

import lottery.domains.content.entity.UserMainReport;

public interface UserMainReportDao {
	
	boolean add(UserMainReport entity);
	
	UserMainReport get(int userId, String time);
	
	boolean update(UserMainReport entity);
}