package lottery.domains.content.dao.impl;

import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.GameDao;
import lottery.domains.content.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Nick on 2016/12/24.
 */
@Repository
public class GameDaoImpl implements GameDao {
    private final String tab = Game.class.getSimpleName();

    @Autowired
    private HibernateSuperDao<Game> superDao;

    @Override
    public List<Game> listAll(){
        String hql = "from " + tab  + " order by typeId asc,sequence desc";
        return superDao.list(hql);
    }

    @Override
    public Game getById(int id) {
        String hql = "from " + tab + " where id = ?0";
        Object[] values = {id};
        return (Game) superDao.unique(hql, values);
    }
}
