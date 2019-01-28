package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.SysPlatform;

public interface SysPlatformDao {
	
	List<SysPlatform> listAll();

	boolean updateStatus(int id, int status);
}