package lottery.domains.content.biz;

import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserGameDividendBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * 真人老虎机体育分红账单Service
 * Created by Nick on 2017/01/31.
 */
public interface UserGameDividendBillService {
    /**
     * 查找契约分页数据
     */
    PageList search(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount,
                    Integer status, int start, int limit);
    /**
     * 统计总金额
     */
    double sumUserAmount(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount,
                    Integer status);

    List<UserGameDividendBill> findByCriteria(List<Criterion> criterions, List<Order> orders);


    UserGameDividendBill getById(int id);

    /**
     * 发放分红
     */
    boolean agree(int id, double userAmount, String remarks);

    /**
     * 拒绝分红
     */
    boolean deny(int id, double userAmount, String remarks);

    /**
     * 删除分红数据
     */
    boolean del(int id);
}
