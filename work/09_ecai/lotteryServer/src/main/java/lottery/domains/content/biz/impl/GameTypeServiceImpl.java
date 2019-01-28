package lottery.domains.content.biz.impl;

import lottery.domains.content.biz.GameTypeService;
import lottery.domains.content.dao.GameTypeDao;
import lottery.domains.content.entity.GameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Nick on 2017-06-02.
 */
@Service
public class GameTypeServiceImpl implements GameTypeService{
    @Autowired
    private GameTypeDao gameTypeDao;

    @Override
    @Transactional(readOnly = true)
    public List<GameType> listAll() {
        return gameTypeDao.listAll();
    }
}
