package lottery.domains.content.biz.read.impl;

import lottery.domains.content.biz.read.UserBetsHitRankingReadService;
import lottery.domains.content.dao.read.UserBetsHitRankingReadDao;
import lottery.domains.content.entity.UserBetsHitRanking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-02.
 */
@Service
@Transactional(readOnly = true)
public class UserBetsHitRankingReadServiceImpl implements UserBetsHitRankingReadService {
    @Autowired
    private UserBetsHitRankingReadDao uBetsHitRankingReadDao;

    @Override
    @Transactional(readOnly = true)
    public List<UserBetsHitRanking> listAll(int count) {
        return uBetsHitRankingReadDao.listAll(count);
    }
}
