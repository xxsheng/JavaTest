package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.vo.user.GameBetsVO;

/**
 * Created by Nick on 2017/2/8.
 */
public interface GameBetsService {

    GameBetsVO getById(int id);

    PageList search(String keyword, String username, Integer platformId, String minTime,
                    String maxTime, Double minMoney, Double maxMoney, Double minPrizeMoney, Double maxPrizeMoney,
                    String gameCode, String gameType, String gameName, int start, int limit);

    /**
     * 第一位是总投注(money)，第二位是总奖金(prizeMoney)
     */
    double[] getTotalMoney(String keyword, String username, Integer platformId, String minTime,
                           String maxTime, Double minMoney, Double maxMoney, Double minPrizeMoney, Double maxPrizeMoney,
                           String gameCode, String gameType, String gameName);
    
    double getBillingOrder(int userId, String startTime, String endTime);
}
