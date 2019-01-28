package lottery.domains.content.biz;

import lottery.domains.content.entity.UserCodeQuota;

import java.util.List;

public interface UserCodeQuotaService {
	boolean add(UserCodeQuota entity);

	boolean addUpAllocateQuantity(int userId, int code);

	UserCodeQuota get(int userId, int code);

	List<UserCodeQuota> list(int userId);

	List<UserCodeQuota> list(int[] userIds);
}