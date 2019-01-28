package lottery.domains.content.biz;

import lottery.domains.content.entity.Lottery;

import java.util.List;

/**
 * Created by Nick on 2017-06-01.
 */
public interface LotteryService {
    List<Lottery> listAll();
}
