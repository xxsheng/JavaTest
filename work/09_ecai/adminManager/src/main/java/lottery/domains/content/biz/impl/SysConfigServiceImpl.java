package lottery.domains.content.biz.impl;

import java.util.List;

import javautils.http.EasyHttpClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.SysConfigService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.SysConfigDao;
import lottery.domains.content.entity.SysConfig;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.pool.LotteryDataFactory;

@Service
public class SysConfigServiceImpl implements SysConfigService {

	@Autowired
	private SysConfigDao sysConfigDao;

	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	@Autowired
	private DbServerSyncDao dbServerSyncDao;
	
	@Bean
	EasyHttpClient easyHttpClient() {
		EasyHttpClient httpClient = new EasyHttpClient();
		httpClient.setRepeatTimes(1);
		httpClient.setTimeOut(1000, 1000);
		return httpClient;
	}

	@Override
	public List<SysConfig> listAll() {
		return sysConfigDao.listAll();
	}

	@Override
	public SysConfig get(String group, String key) {
		return sysConfigDao.get(group, key);
	}

	@Override
	public boolean update(String group, String key, String value) {
		SysConfig entity = sysConfigDao.get(group, key);
		if (entity != null) {
			entity.setValue(value);
			boolean flag = sysConfigDao.update(entity);
			if (flag) {
				// 更新系统缓存
				lotteryDataFactory.initSysConfig();
				dbServerSyncDao.update(DbServerSyncEnum.SYS_CONFIG);
			}
			return flag;
		}
		return false;
	}

	@Override
	public boolean initLotteryPrizeSysConfig(String url) {
		url = url + "/lottery/prize/init-sysconfig";// 如果改变对应的LotteryPrize也要变
		String result = easyHttpClient().get(url);
		if ("success".equals(result)) {
			return true;
		}
		return false;
	}

}