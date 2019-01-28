package lottery.domains.content.dao;

import lottery.domains.content.entity.UserCodeQuota;

import java.util.List;

public interface UserCodeQuotaDao {
	boolean add(UserCodeQuota entity);
	
	UserCodeQuota get(int userId, int code);

	List<UserCodeQuota> list(int userId);

	List<UserCodeQuota> list(int[] userIds);

	boolean addUpAllocateQuantity(int userId, int code);


}