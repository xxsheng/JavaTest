package lottery.domains.content.biz.read;

import lottery.domains.content.entity.UserBetsHitRanking;

import java.util.List;

/**
 * Created by Nick on 2017-06-02.
 */
public interface UserBetsHitRankingReadService {
    List<UserBetsHitRanking> listAll(int count);
}
