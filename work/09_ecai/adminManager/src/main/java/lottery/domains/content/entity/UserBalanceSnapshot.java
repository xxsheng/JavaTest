package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by Nick on 2017-07-03.
 */
@Entity
@Table(name = "user_balance_snapshot", catalog = Database.name)
public class UserBalanceSnapshot implements java.io.Serializable{
    private int id;
    private double totalMoney;
    private double lotteryMoney;
    private String time;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "total_money", nullable = false, precision = 16, scale = 5)
    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Column(name = "lottery_money", nullable = false, precision = 16, scale = 5)
    public double getLotteryMoney() {
        return lotteryMoney;
    }

    public void setLotteryMoney(double lotteryMoney) {
        this.lotteryMoney = lotteryMoney;
    }

    @Column(name = "time", length = 19)
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
