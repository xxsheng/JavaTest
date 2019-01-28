package lottery.domains.content.dao;

import lottery.domains.content.entity.GameType;

import java.util.List;

/**
 * Created by Nick on 2016/12/24.
 */
public interface GameTypeDao {
    List<GameType> listAll();
}
