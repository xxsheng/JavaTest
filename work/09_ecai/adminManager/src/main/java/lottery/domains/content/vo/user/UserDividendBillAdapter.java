package lottery.domains.content.vo.user;

import lottery.domains.content.entity.UserDividendBill;

import java.util.List;

/**
 * Created by Nick on 2017-08-06.
 */
public class UserDividendBillAdapter {
    private UserDividendBill upperBill; // 上级的账单
    private List<UserDividendBillAdapter> lowerBills; // 下级账单

    public UserDividendBillAdapter(UserDividendBill upperBill, List<UserDividendBillAdapter> lowerBills) {
        this.upperBill = upperBill;
        this.lowerBills = lowerBills;
    }

    public UserDividendBill getUpperBill() {
        return upperBill;
    }

    public void setUpperBill(UserDividendBill upperBill) {
        this.upperBill = upperBill;
    }

    public List<UserDividendBillAdapter> getLowerBills() {
        return lowerBills;
    }

    public void setLowerBills(List<UserDividendBillAdapter> lowerBills) {
        this.lowerBills = lowerBills;
    }
}
