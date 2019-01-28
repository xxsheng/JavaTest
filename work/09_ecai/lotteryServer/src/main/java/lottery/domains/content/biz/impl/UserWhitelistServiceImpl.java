package lottery.domains.content.biz.impl;

import lottery.domains.content.dao.UserWhitelistDao;
import lottery.domains.content.entity.UserWhitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lottery.domains.content.biz.UserWhitelistService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserWhitelistServiceImpl implements UserWhitelistService {
    @Autowired
    private UserWhitelistDao uWhitelistDao;

    @Override
    @Transactional(readOnly = true)
    public List<UserWhitelist> getByUsername(String username) {
        return uWhitelistDao.getByUsername(username);
    }
}