package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.SysPlatformService;
import lottery.domains.content.dao.SysPlatformDao;
import lottery.domains.content.entity.SysPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-01.
 */
@Service
public class SysPlatformServiceImpl implements SysPlatformService {
    @Autowired
    private SysPlatformDao sysPlatformDao;

    @Override
    @Transactional(readOnly = true)
    public List<SysPlatform> listAll() {
        return sysPlatformDao.listAll();
    }
}
