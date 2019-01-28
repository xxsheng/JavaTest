package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserDailySettleBill;

import java.util.List;

/**
 * Created by Nick on 2017-08-06.
 */
public class UserDailySettleBillAdapter {
    private UserDailySettleBill upperBill; // 上级的账单
    private List<UserDailySettleBillAdapter> lowerBills; // 下级账单

    public UserDailySettleBillAdapter(UserDailySettleBill upperBill, List<UserDailySettleBillAdapter> lowerBills) {
        this.upperBill = upperBill;
        this.lowerBills = lowerBills;
    }

    public UserDailySettleBill getUpperBill() {
        return upperBill;
    }

    public void setUpperBill(UserDailySettleBill upperBill) {
        this.upperBill = upperBill;
    }

    public List<UserDailySettleBillAdapter> getLowerBills() {
        return lowerBills;
    }

    public void setLowerBills(List<UserDailySettleBillAdapter> lowerBills) {
        this.lowerBills = lowerBills;
    }
}
