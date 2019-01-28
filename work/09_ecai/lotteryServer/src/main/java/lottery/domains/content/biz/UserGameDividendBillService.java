package lottery.domains.content.biz;

import lottery.domains.content.entity.UserGameDividendBill;
import lottery.web.WebJSON;

/**
 * 老虎机真人体育账单Service
 * Created by Nick on 2016/10/31.
 */
public interface UserGameDividendBillService {
    UserGameDividendBill getById(int id);

    /**
     * 领取契约分红
     */
    boolean collect(WebJSON json, int userId, int id);
}
