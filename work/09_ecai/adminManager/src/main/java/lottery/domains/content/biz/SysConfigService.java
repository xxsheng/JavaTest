package lottery.domains.content.biz;

import java.util.List;

import lottery.domains.content.entity.SysConfig;

public interface SysConfigService {

	List<SysConfig> listAll();

	SysConfig get(String group, String key);

	boolean update(String group, String key, String value);

	boolean initLotteryPrizeSysConfig(String url);

}