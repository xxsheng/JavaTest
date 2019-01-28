package lottery.domains.content.dao;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.Game;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * Created by Nick on 2016/12/24.
 */
public interface GameDao {
    PageList search(List<Criterion> criterions, List<Order> orders, int start, int limit);

    boolean add(Game game);

    Game getById(int id);

    Game getByGameName(String gameName);

    Game getByGameCode(String gameCode);

    boolean deleteById(int id);

    boolean update(Game game);

    boolean updateSequence(int id, int sequence);

    boolean updateDisplay(int id, int display);
}
