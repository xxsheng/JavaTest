package lottery.domains.content.dao;

import lottery.domains.content.entity.ActivityRebateWheelIp;

/**
 * Created by Nick on 2017/11/27.
 */
public interface ActivityRebateWheelIpDao {
    boolean add(ActivityRebateWheelIp entity);

    ActivityRebateWheelIp getByIp(String ip);
}
