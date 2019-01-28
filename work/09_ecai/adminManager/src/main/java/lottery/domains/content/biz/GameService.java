package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.Game;

/**
 * Created by Nick on 2016/12/24.
 */
public interface GameService {
    PageList search(String gameName, String gameCode, Integer typeId, Integer platformId,
                    Integer display, Integer flashSupport, Integer h5Support,
                    int start, int limit);

    boolean add(String gameName, String gameCode, Integer typeId, Integer platformId, String imgUrl, int sequence, int display, Integer flashSupport, Integer h5Support, Integer progressiveSupport, String progressiveCode);

    Game getById(int id);

    Game getByGameName(String gameName);

    Game getByGameCode(String gameCode);

    boolean deleteById(int id);

    boolean update(int id, String gameName, String gameCode, Integer typeId, Integer platformId, String imgUrl, Integer sequence, Integer display,
                   Integer flashSupport, Integer h5Support, Integer progressiveSupport, String progressiveCode);

    boolean updateSequence(int id, int sequence);

    boolean updateDisplay(int id, int display);
}
