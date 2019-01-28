package lottery.domains.content.biz.read;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserGameDividendBill;
import lottery.web.WebJSON;

import java.util.List;

/**
 * 老虎机真人体育账单Service
 * Created by Nick on 2016/10/31.
 */
public interface UserGameDividendBillReadService {
    PageList searchByZhuGuans(List<Integer> userIds, String sTime, String eTime, int start, int limit);

    PageList searchByLowers(int upUserId, String sTime, String eTime, int start, int limit);
}
