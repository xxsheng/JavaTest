package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.SysPlatformService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.SysPlatformDao;
import lottery.domains.content.entity.SysPlatform;
import lottery.domains.content.global.DbServerSyncEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Nick on 2017-08-31.
 */
@Service
public class SysPlatformServiceImpl implements SysPlatformService {
    @Autowired
    private SysPlatformDao sysPlatformDao;
    @Autowired
    private DbServerSyncDao dbServerSyncDao;

    @Override
    public List<SysPlatform> listAll() {
        return sysPlatformDao.listAll();
    }

    @Override
    public boolean updateStatus(int id, int status) {
        boolean updated = sysPlatformDao.updateStatus(id, status);
        if (updated) {
            dbServerSyncDao.update(DbServerSyncEnum.SYS_PLATFORM);
        }
        return updated;
    }
}
