package lottery.domains.content.dao;

import lottery.domains.content.entity.Game;

import java.util.List;

/**
 * Created by Nick on 2016/12/24.
 */
public interface GameDao {
    List<Game> listAll();

    Game getById(int id);
}
