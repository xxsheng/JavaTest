package lottery.domains.content.dao;

import java.util.List;

import lottery.domains.content.entity.SysConfig;

public interface SysConfigDao {

	List<SysConfig> listAll();
	
	SysConfig get(String group, String key);
	
	boolean update(SysConfig entity);

	boolean save(SysConfig entity);
}