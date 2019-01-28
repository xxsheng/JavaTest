package lottery.domains.content.biz.read.impl;

import lottery.domains.content.biz.read.DbServerSyncReadService;
import lottery.domains.content.dao.read.DbServerSyncReadDao;
import lottery.domains.content.entity.DbServerSync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-01.
 */
@Service
@Transactional(readOnly = true)
public class DbServerSyncReadServiceImpl implements DbServerSyncReadService {
    @Autowired
    private DbServerSyncReadDao dbServerSyncReadDao;

    @Override
    @Transactional(readOnly = true)
    public List<DbServerSync> listAll() {
        return dbServerSyncReadDao.listAll();
    }
}
