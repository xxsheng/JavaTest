package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserGameWaterBill;

/**
 * Created by Nick on 2017/2/4.
 */
public interface UserGameWaterBillService {
    PageList search(Integer userId, String sTime, String eTime, Double minUserAmount,
                    Double maxUserAmount, Integer type, Integer status, int start, int limit);

    boolean add(UserGameWaterBill bill);
}
