package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.GameTypeDao;
import lottery.domains.content.entity.GameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Nick on 2016/12/24.
 */
@Repository
public class GameTypeDaoImpl implements GameTypeDao {
    private final String tab = GameType.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<GameType> superDao;

    @Override
    public List<GameType> listAll(){
        String hql = "from " + tab + " order by platformId asc,sequence desc";
        return superDao.list(hql);
    }
}
