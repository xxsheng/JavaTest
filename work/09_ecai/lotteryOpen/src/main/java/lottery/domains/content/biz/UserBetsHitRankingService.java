package lottery.domains.content.biz;

import lottery.domains.content.entity.UserBets;

/**
 * Created by Nick on 2017/2/27.
 */
public interface UserBetsHitRankingService {
    /**
     * 每天前10名
     */
    void addIfNecessary(UserBets userBets);
}
