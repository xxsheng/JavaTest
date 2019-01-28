package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.SysConfigService;
import lottery.domains.content.dao.SysConfigDao;
import lottery.domains.content.entity.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-01.
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {
    @Autowired
    private SysConfigDao sysConfigDao;

    @Override
    @Transactional(readOnly = true)
    public List<SysConfig> listAll() {
        return sysConfigDao.listAll();
    }
}
