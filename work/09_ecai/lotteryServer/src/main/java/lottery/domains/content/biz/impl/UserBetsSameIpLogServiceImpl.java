package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.UserBetsSameIpLogService;
import lottery.domains.content.dao.UserBetsSameIpLogDao;
import lottery.domains.content.entity.UserBetsSameIpLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Nick on 2017-10-16.
 */
@Service
public class UserBetsSameIpLogServiceImpl implements UserBetsSameIpLogService{
    @Autowired
    private UserBetsSameIpLogDao uBetsSameIpLogDao;

    @Override
    @Transactional(readOnly = true)
    public UserBetsSameIpLog getById(int id) {
        return uBetsSameIpLogDao.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserBetsSameIpLog getByIP(String ip) {
        return uBetsSameIpLogDao.getByIP(ip);
    }

    @Override
    public boolean add(UserBetsSameIpLog log) {
        return uBetsSameIpLogDao.add(log);
    }

    @Override
    public boolean update(UserBetsSameIpLog log) {
        return uBetsSameIpLogDao.update(log);
    }
}
