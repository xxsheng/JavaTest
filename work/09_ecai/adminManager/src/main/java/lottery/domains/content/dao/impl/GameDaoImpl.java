package lottery.domains.content.dao.impl;

import javautils.jdbc.PageList;
import javautils.jdbc.hibernate.HibernateSuperDao;
import lottery.domains.content.dao.GameDao;
import lottery.domains.content.entity.Game;
import lottery.domains.content.entity.User;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
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
    public PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit) {
        String propertyName = "id";
        return superDao.findPageList(Game.class, propertyName, criterions, orders, start, limit);
    }

    @Override
    public boolean add(Game game) {
        return superDao.save(game);
    }

    @Override
    public Game getById(int id) {
        String hql = "from " + tab + " where id = ?0";
        Object[] values = {id};
        return (Game) superDao.unique(hql, values);
    }

    @Override
    public Game getByGameName(String gameName) {
        String hql = "from " + tab + " where gameName = ?0";
        Object[] values = {gameName};
        return (Game) superDao.unique(hql, values);
    }

    @Override
    public Game getByGameCode(String gameCode) {
        String hql = "from " + tab + " where gameCode = ?0";
        Object[] values = {gameCode};
        return (Game) superDao.unique(hql, values);
    }

    @Override
    public boolean deleteById(int id) {
        String hql = "delete from " + tab + " where id = ?0";
        Object[] values = {id};
        return superDao.delete(hql, values);
    }

    @Override
    public boolean update(Game game) {
        return superDao.update(game);
    }

    @Override
    public boolean updateSequence(int id, int sequence) {
        String hql = "update " + tab + " set sequence = ?0 where id = ?1";
        Object[] values = {sequence, id};
        return superDao.update(hql, values);
    }

    @Override
    public boolean updateDisplay(int id, int display) {
        String hql = "update " + tab + " set display = ?0 where id = ?1";
        Object[] values = {display, id};
        return superDao.update(hql, values);
    }
}
