package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.SysNoticeService;
import lottery.domains.content.dao.SysNoticeDao;
import lottery.domains.content.entity.SysNotice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-02.
 */
@Service
public class SysNoticeServiceImpl implements SysNoticeService{
    @Autowired
    private SysNoticeDao sysNoticeDao;

    @Override
    @Transactional(readOnly = true)
    public SysNotice getById(int id) {
        return sysNoticeDao.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysNotice> get(int count) {
        return sysNoticeDao.get(count);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysNotice> getNoticeTitle(int count) {
        return sysNoticeDao.getNoticeTitle(count);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SysNotice> getNoticeTitleLastNew(int count) {
        return sysNoticeDao.getNoticeTitleLastNew(count);
    }
}
