package lottery.domains.content.biz;

import lottery.domains.content.entity.SysPlatform;

import java.util.List;

public interface SysPlatformService {
	
	List<SysPlatform> listAll();

	boolean updateStatus(int id, int status);
}