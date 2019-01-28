package lottery.domains.content.biz;

import lottery.domains.content.entity.GameType;

import java.util.List;

/**
 * Created by Nick on 2017-06-02.
 */
public interface GameTypeService {
    List<GameType> listAll();
}
