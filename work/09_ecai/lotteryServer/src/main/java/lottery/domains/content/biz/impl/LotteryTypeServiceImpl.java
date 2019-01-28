package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.LotteryTypeService;
import lottery.domains.content.dao.LotteryTypeDao;
import lottery.domains.content.entity.LotteryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-01.
 */
@Service
public class LotteryTypeServiceImpl implements LotteryTypeService {
    @Autowired
    private LotteryTypeDao lotteryTypeDao;

    @Override
    @Transactional(readOnly = true)
    public List<LotteryType> listAll() {
        return lotteryTypeDao.listAll();
    }
}
