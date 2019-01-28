package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.LotteryService;
import lottery.domains.content.dao.LotteryDao;
import lottery.domains.content.entity.Lottery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-01.
 */
@Service
public class LotteryServiceImpl implements LotteryService {
    @Autowired
    private LotteryDao lotteryDao;

    @Override
    @Transactional(readOnly = true)
    public List<Lottery> listAll() {
        return lotteryDao.listAll();
    }
}
