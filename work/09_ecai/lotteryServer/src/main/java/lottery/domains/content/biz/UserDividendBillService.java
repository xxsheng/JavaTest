package lottery.domains.content.biz;

import lottery.domains.content.entity.UserDividendBill;
import lottery.web.WebJSON;

import java.util.List;

/**
 * 契约日结账单Service
 * Created by Nick on 2016/10/31.
 */
public interface UserDividendBillService {
    UserDividendBill getById(int id);

    /**
     * 领取契约分红
     */
    boolean collect(WebJSON json, int userId, int id);

    boolean updateStatus(int id, int status);

    /**
     * 修改状态
     */
    boolean updateStatus(int id, int status, String remarks);

    boolean update(int id, int status, double availableAmount, double totalReceived);

    double getTotalUnIssue(int userId);

    boolean addAvailableMoney(int id, double money);

    boolean addTotalReceived(int id, double money);

    boolean addLowerPaidAmount(int id, double money);
}
