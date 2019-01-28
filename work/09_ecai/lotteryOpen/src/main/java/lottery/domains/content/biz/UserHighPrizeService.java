package lottery.domains.content.biz;

import lottery.domains.content.entity.UserBets;

/**
 * Created by Nick on 2017/2/27.
 */
public interface UserHighPrizeService {
    void addIfNecessary(UserBets userBets);
}
