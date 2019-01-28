package lottery.domains.content.biz;

import admin.web.WebJSONObject;
import javautils.jdbc.PageList;
import lottery.domains.content.entity.UserDividendBill;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * 契约分红账单Service
 * Created by Nick on 2016/10/31.
 */
public interface UserDividendBillService {
    /**
     * 查找契约分页数据
     */
    PageList search(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount,
                    Integer status, Integer issueType, int start, int limit);
    /**
     * 查找平台亏损列表
     */
    PageList searchPlatformLoss(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount, int start, int limit);

    /**
     * 查找契约数据统计数据，第一位平台总金额，第二位上级总金额
     */
    double[] sumUserAmount(List<Integer> userIds, String sTime, String eTime, Double minUserAmount, Double maxUserAmount);

    List<UserDividendBill> findByCriteria(List<Criterion> criterions, List<Order> orders);

    /**
     * 将未领取的记录改为过期
     */
    boolean updateAllExpire();

    /**
     * 获取用户周期内所有下级账单
     */
    List<UserDividendBill> getLowerBills(int userId, String sTime, String eTime);

    /**
     * 获取用户周期内所有直属下级账单
     */
    List<UserDividendBill> getDirectLowerBills(int userId, String sTime, String eTime);

    UserDividendBill getById(int id);

    UserDividendBill getBill(int userId, String sTime, String eTime);

    boolean addAvailableMoney(int id, double money);

    boolean add(UserDividendBill dividendBill);

    /**
     * 处理余额不足
     */
    void issueInsufficient(int id);

    /**
     * 发放分红
     */
    boolean agree(WebJSONObject json, int id, String remarks);

    /**
     * 拒绝分红
     */
    boolean deny(WebJSONObject json, int id, String remarks);

    /**
     * 删除分红数据
     */
    boolean del(WebJSONObject json, int id);

    /**
     * 清空分红账单欠账，该账单，及其所有下注账单改为已发放状态
     */
    boolean reset(WebJSONObject json, int id, String remarks);
    
    /**
     * 获取用户结算周期内领取的分红
     * @param userId
     * @param sTime
     * @param eTime
     * @return
     */
    double queryPeriodCollect(int userId, String sTime, String eTime);
}
