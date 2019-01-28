package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.GameBets;
import lottery.domains.content.entity.UserHighPrize;

import java.util.Map;

/**
 * Created by Nick on 2017/2/27.
 */
public interface UserHighPrizeService {
    PageList search(Integer type,String username, Integer platform,
                    String nameId, String subName, String refId,
                    Double minMoney, Double maxMoney,
                    Double minPrizeMoney, Double maxPrizeMoney,
                    Double minTimes, Double maxTimes,
                    String minTime, String maxTime,
                    Integer status, String confirmUsername,
                    int start, int limit);

    UserHighPrize getById(int id);

    boolean lock(int id, String username);

    boolean unlock(int id, String username);

    boolean confirm(int id, String username);

    void addIfNecessary(GameBets gameBets);

    int getUnProcessCount();

    /**
     * 获取所有大额中奖通知，key:域；value:消息
     */
    Map<String, String> getAllHighPrizeNotices();

    /**
     * 删除一个大额中奖通知
     */
    void delHighPrizeNotice(String field);
}
