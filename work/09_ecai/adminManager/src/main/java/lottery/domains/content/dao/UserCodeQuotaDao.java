package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.UserCodeQuota;

public interface UserCodeQuotaDao {
	
	boolean add(UserCodeQuota entity);
	
	UserCodeQuota get(int userId);
	
	List<UserCodeQuota> list(int[] userIds);
	
	boolean update(UserCodeQuota entity);

	boolean delete(int userId);
	
}